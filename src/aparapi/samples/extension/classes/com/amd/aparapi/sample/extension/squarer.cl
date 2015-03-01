__kernel void square( __global float *in, __global float *out){
   const size_t id = get_global_id(0);
   out[id] = in[id]*in[id];
}

