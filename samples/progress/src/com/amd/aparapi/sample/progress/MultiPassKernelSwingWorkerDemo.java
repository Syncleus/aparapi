package com.amd.aparapi.sample.progress;

import com.amd.aparapi.*;
import com.amd.aparapi.internal.kernel.*;
import com.amd.aparapi.util.swing.MultiPassKernelSwingWorker;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Demonstrates progress tracking and cancellation for multi-pass kernels, via {@link MultiPassKernelSwingWorker}.
 */
public class MultiPassKernelSwingWorkerDemo {

   private static final int PASS_COUNT = 200;
   private static JButton startButton;
   private static JButton cancelButton;
   private static JProgressBar progress;
   private static JLabel status = new JLabel("Press Start", JLabel.CENTER);
   private static LongRunningKernel kernel;
   private static MultiPassKernelSwingWorker worker;

   private static final boolean TEST_JTP = false;

   public static void main(String[] ignored) throws Exception {
      if (TEST_JTP) {
         KernelManager.setKernelManager(KernelManagers.JTP_ONLY);
      }
      kernel = new LongRunningKernel();

      UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
      JPanel rootPanel = new JPanel();
      rootPanel.setLayout(new BorderLayout());
      JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
      startButton = new JButton("Start");
      cancelButton = new JButton("Cancel");
      startButton.setEnabled(true);
      startButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            start();
         }
      });
      cancelButton.setEnabled(false);
      cancelButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            cancel();
         }
      });
      buttons.add(startButton);
      buttons.add(cancelButton);
      rootPanel.add(buttons, BorderLayout.SOUTH);

      progress = new JProgressBar(new DefaultBoundedRangeModel(0, 0, 0, PASS_COUNT));

      rootPanel.add(status, BorderLayout.CENTER);
      rootPanel.add(progress, BorderLayout.NORTH);

      JFrame frame = new JFrame("MultiPassKernelSwingWorker Demo");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(rootPanel);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }

   private static MultiPassKernelSwingWorker createWorker() {
      return new MultiPassKernelSwingWorker(kernel) {
         @Override
         protected void executeKernel(Kernel kernel) {
            int range;
            if (TEST_JTP) {
               range = LongRunningKernel.RANGE / 1000;
            } else {
               range = LongRunningKernel.RANGE;
            }
            kernel.execute(range, PASS_COUNT);
         }

         @Override
         protected void updatePassId(int passId) {
            updateProgress(passId);
         }

         @Override
         protected void done() {
            updateProgress(KernelRunner.PASS_ID_COMPLETED_EXECUTION);
            startButton.setEnabled(true);
            cancelButton.setEnabled(false);
         }
      };
   }

   private static void start() {
      if (!SwingUtilities.isEventDispatchThread()) {
         throw new IllegalStateException();
      }

      startButton.setEnabled(false);
      cancelButton.setEnabled(true);
      worker = createWorker();
      worker.execute();
      System.out.println("Started execution of MultiPassKernelSwingWorker");
   }

   private static void updateProgress(int passId) {
      int progressValue;
      if (passId >= 0) {
         progressValue = passId;
         status.setText("passId = " + passId);
      } else if (passId == KernelRunner.PASS_ID_PREPARING_EXECUTION) {
         progressValue = 0;
         status.setText("Preparing");
      } else if (passId == KernelRunner.PASS_ID_COMPLETED_EXECUTION) {
         progressValue = PASS_COUNT;
         status.setText("Complete");
      } else {
         progressValue = 0;
         status.setText("Illegal status " + passId);
      }
      progress.getModel().setValue(progressValue);
   }

   private static void cancel() {
      worker.cancelExecution();
   }
}

