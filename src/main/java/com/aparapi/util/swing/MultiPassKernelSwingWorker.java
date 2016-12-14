/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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
package com.aparapi.util.swing;

import com.aparapi.Kernel;
import com.aparapi.internal.kernel.KernelRunner;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Implementation of SwingWorker to assist in progress tracking and cancellation of multi-pass {@link Kernel}s.
 */
public abstract class MultiPassKernelSwingWorker extends SwingWorker<Void, Void>{

   public static final int DEFAULT_POLL_INTERVAL = 50;

   private Kernel kernel;
   private Timer timer;

   protected MultiPassKernelSwingWorker(Kernel kernel) {
      this.kernel = kernel;
   }

   /** Utility method which just invokes {@link Kernel#cancelMultiPass()} on the executing kernel. */
   public void cancelExecution() {
      kernel.cancelMultiPass();
   }

   /** This method must invoke one of the {@code kernel}'s execute() methods. */
   protected abstract void executeKernel(Kernel kernel);

   /** This method, which is always invoked on the swing event dispatch thread, should be used to update any components (such as a {@link javax.swing.JProgressBar}) so
    * as to reflect the progress of the multi-pass Kernel being executed.
    *
    * @param passId The passId for the Kernel's current pass, or one of the constant fields returnable by {@link KernelRunner#getCurrentPass()}.
    */
   protected abstract void updatePassId(int passId);

   /** Executes the {@link #kernel} via {@link #executeKernel(Kernel)}, whilst also managing progress updates for the kernel's passId. */
   @Override
   protected final Void doInBackground() throws Exception {
      try {
         setUpExecution();
         executeKernel(kernel);
         return null;
      }
      finally {
         cleanUpExecution();
      }
   }

   private void setUpExecution() {
      ActionListener listener = new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            updatePassId();
         }
      };
      timer = new Timer(getPollIntervalMillis(), listener);
      timer.setCoalesce(false);
      timer.start();
   }

   private void cleanUpExecution() {
      timer.stop();
      timer = null;
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            updatePassId(KernelRunner.PASS_ID_COMPLETED_EXECUTION);
         }
      });
   }

   private void updatePassId() {
      int progress = kernel.getCurrentPass();
      updatePassId(progress);
   }

   /** The interval at which the Kernel's current passId is polled. Unless overridden, returns {@link #DEFAULT_POLL_INTERVAL}. */
   protected int getPollIntervalMillis() {
      return DEFAULT_POLL_INTERVAL;
   }
}
