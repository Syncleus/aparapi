/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aparapi;

/**
 * Handle returned by asynchronous kernel execution.
 */
public final class Execution {
   private final Kernel kernel;
   private final Thread thread;
   private volatile Throwable failure;
   private volatile boolean finished;

   Execution(final Kernel _kernel, final Runnable _execution) {
      if (_kernel == null) {
         throw new NullPointerException("_kernel");
      }
      if (_execution == null) {
         throw new NullPointerException("_execution");
      }
      kernel = _kernel;
      thread = new Thread(new Runnable() {
         @Override public void run() {
            try {
               _execution.run();
            } catch (Throwable t) {
               failure = t;
            } finally {
               finished = true;
            }
         }
      }, "Aparapi Kernel Async Execution");
      thread.start();
   }

   /**
    * Wait until the asynchronous execution has finished.
    *
    * @return the executed kernel
    */
   public Kernel waitUntilFinished() {
      boolean interrupted = false;
      while (true) {
         try {
            thread.join();
            break;
         } catch (InterruptedException e) {
            interrupted = true;
         }
      }
      if (interrupted) {
         Thread.currentThread().interrupt();
      }
      if (failure instanceof RuntimeException) {
         throw (RuntimeException) failure;
      }
      if (failure instanceof Error) {
         throw (Error) failure;
      }
      if (failure != null) {
         throw new RuntimeException(failure);
      }
      return kernel;
   }

   public boolean isFinished() {
      return finished;
   }

   public Kernel getKernel() {
      return kernel;
   }
}
