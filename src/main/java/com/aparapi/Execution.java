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
 * Handle returned by {@link Kernel#executeAsync(Range)} and related methods.
 */
public final class Execution {
   private final Kernel kernel;
   private final Thread thread;
   private volatile Throwable failure;

   Execution(Kernel _kernel, Runnable _task) {
      kernel = _kernel;
      thread = new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               _task.run();
            } catch (Throwable t) {
               failure = t;
            }
         }
      }, "Aparapi async execution: " + _kernel.getClass().getName());
      thread.start();
   }

   /**
    * Wait until the asynchronous execution has completed.
    */
   public void waitUntilFinished() {
      boolean interrupted = false;
      while (thread.isAlive()) {
         try {
            thread.join();
         } catch (InterruptedException e) {
            interrupted = true;
         }
      }
      if (interrupted) {
         Thread.currentThread().interrupt();
      }
      rethrowFailure();
   }

   /**
    * @return true when the asynchronous execution thread has completed.
    */
   public boolean isFinished() {
      return !thread.isAlive();
   }

   /**
    * @return The kernel instance used for this execution.
    */
   public Kernel getKernel() {
      return kernel;
   }

   /**
    * @return The failure thrown by asynchronous execution, or null when execution completed successfully or is still running.
    */
   public Throwable getFailure() {
      return failure;
   }

   private void rethrowFailure() {
      if (failure instanceof RuntimeException) {
         throw (RuntimeException) failure;
      }
      if (failure instanceof Error) {
         throw (Error) failure;
      }
      if (failure != null) {
         throw new RuntimeException(failure);
      }
   }
}
