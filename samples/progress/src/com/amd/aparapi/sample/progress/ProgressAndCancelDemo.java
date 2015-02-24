package com.amd.aparapi.sample.progress;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.internal.kernel.KernelRunner;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Demonstrates progress tracking and cancellation for multi-pass kernels.
 */
public class ProgressAndCancelDemo {

   private static final int PASS_COUNT = 200;
   private static final int POLL_SLEEP = 50;
   private static JButton startButton;
   private static JButton cancelButton;
   private static JProgressBar progress;
   private static JLabel status = new JLabel("Press Start", JLabel.CENTER);

   private static LongRunningKernel kernel;
   private static Timer timer;

   private static final boolean TEST_JTP = false;

   public static void main(String[] ignored) throws Exception {

      System.setProperty("com.amd.aparapi.enableShowGeneratedOpenCL", "true");
      System.setProperty("com.amd.aparapi.enableVerboseJNI", "true");
      System.setProperty("com.amd.aparapi.dumpFlags", "true");
      System.setProperty("com.amd.aparapi.enableVerboseJNIOpenCLResourceTracking", "true");
      System.setProperty("com.amd.aparapi.enableExecutionModeReporting", "true");

      kernel = new LongRunningKernel();
      if (TEST_JTP) {
         kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
      }
      Thread asynchReader = new Thread() {
         @Override
         public void run() {
            while (true) {
               try {
                  int cancelState = kernel.getCancelState();
                  int passId = kernel.getCurrentPass();
                  System.out.println("cancel = " + cancelState + ", passId = " + passId);
                  Thread.sleep(50);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }
      };
      //asynchReader.start();
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

      JFrame frame = new JFrame("Progress and Cancel Demo");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(rootPanel);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }

   private static void start() {
      if (!SwingUtilities.isEventDispatchThread()) {
         throw new IllegalStateException();
      }
      Thread executionThread = new Thread() {
         @Override
         public void run() {
            executeKernel();
         }
      };
      executionThread.start();
      updateProgress();
      timer = new Timer(POLL_SLEEP, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            updateProgress();
         }
      });
      timer.setCoalesce(false);
      timer.setRepeats(true);
      timer.start();
      System.out.println("Started on EDT");
   }

   private static void updateProgress() {
      int passId = kernel.getCurrentPass();
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
      kernel.cancelMultiPass();
   }

   private static void executeKernel() {
      System.out.println("Starting execution");
      startButton.setEnabled(false);
      cancelButton.setEnabled(true);
      try {
         int range;
         if (TEST_JTP) {
            range = LongRunningKernel.RANGE / 1000;
         } else {
            range = LongRunningKernel.RANGE;
         }
         kernel.execute(range, PASS_COUNT);
      } catch (Throwable t) {
         t.printStackTrace();
      } finally {
         System.out.println("Finished execution");
         System.out.println("kernel.data[0] = " + kernel.data[0]);
         if (timer != null) {
            timer.stop();
            timer = null;
         }
         startButton.setEnabled(true);
         cancelButton.setEnabled(false);
         updateProgress();
      }
   }

}
