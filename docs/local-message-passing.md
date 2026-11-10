# Integer messages within a work-group

`localSend` and `localReceive` provide a mailbox protocol for JTP and OpenCL
kernels. Each mailbox is an `@Local int[]` field (a field whose name ends in
`Kernel.LOCAL_SUFFIX` also works). Its contents are shared within one work-group;
other work-groups have their own scratch memory.

For each round:

1. Send messages with `localSend(mailbox, slot, value)`. Every slot that will be
   read must have exactly one writer. Not every work-item needs to send.
2. Every work-item calls `localReceive(mailbox, slot)` in the same control-flow
   order. A receiver may choose any written slot. Several receivers can read
   the same slot for a broadcast.
3. The mailbox can immediately be reused for the next round. The receive call
   contains a barrier before reading and another after reading.

The slot is an ordinary array index, not an automatic local ID. Keep all slots
within the array bounds. For multidimensional groups, calculate a linear rank
when one slot per work-item is needed. All participants must use the same
mailbox; collective receives must not appear in divergent conditionals or loops.
These helpers do not implement cross-group messaging, tags, queues, or an MPI
runtime. Sequential execution cannot implement multi-work-item rendezvous.

## Ring exchange and host/device data

This example sends each input to the next local work-item. The last work-item
in each group sends to local work-item zero. The two receive barriers make it
safe to repeat the send/receive pair in a loop.

```java
class Ring extends Kernel {
    @Local final int[] mailbox = new int[4];
    final int[] input = new int[16];
    final int[] output = new int[16];

    @Override public void run() {
        int rank = getLocalId();
        localSend(mailbox, (rank + 1) % getLocalSize(), input[getGlobalId()]);
        output[getGlobalId()] = localReceive(mailbox, rank);
    }
}

Ring kernel = new Ring();
try {
    for (int i = 0; i < kernel.input.length; i++) {
        kernel.input[i] = i;
    }
    kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP); // or an OpenCL device
    kernel.setExplicit(true);
    kernel.put(kernel.input);
    kernel.execute(Range.create(16, 4));
    kernel.get(kernel.output);
    // output: [3, 0, 1, 2, 7, 4, 5, 6, 11, 8, 9, 10, 15, 12, 13, 14]
} finally {
    kernel.dispose();
}
```

Import `com.aparapi.Kernel` and `com.aparapi.Range` for this example. For OpenCL,
select a suitable device/execution mode instead of JTP. Ordinary input/output
arrays carry messages across the host/device boundary using Aparapi's existing
`put` and `get` operations (or automatic transfers). Do not use `put`/`get` on
the local mailbox: it is uninitialized device scratch space, not a host buffer.
There is no concurrent host/device communication during an executing kernel.
