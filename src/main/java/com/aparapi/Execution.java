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
 * Handle returned by asynchronous kernel executions.
 */
public final class Execution {
   private final Thread thread;
   private volatile Throwable failure;

   private Execution(final Runnable task, String threadName) {
      thread = new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               task.run();
            } catch (Throwable t) {
               failure = t;
            }
         }
      }, threadName);
   }

   static Execution start(final Runnable task, String threadName) {
      Execution execution = new Execution(task, threadName);
      execution.thread.start();
      return execution;
   }

   /**
    * Wait until the asynchronous kernel execution has completed.
    *
    * @return this execution handle
    */
   public Execution waitUntilFinished() {
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
      throwFailureIfAny();
      return this;
   }

   /**
    * @return true if the asynchronous execution has completed
    */
   public boolean isFinished() {
      return !thread.isAlive();
   }

   /**
    * @return true if the asynchronous execution completed with an exception or error
    */
   public boolean hasFailed() {
      return failure != null;
   }

   private void throwFailureIfAny() {
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
