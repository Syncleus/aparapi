package com.amd.aparapi.examples.nbody;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.media.opengl.GL2;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;


public class Test{


   class Controls{
      private int frames;
      private long last = System.currentTimeMillis();
      private boolean running = false;
      private JPanel controlPanel = new JPanel(new FlowLayout());
      private JTextField framesPerSecondTextField = new JTextField("0", 5);
      private JTextField thetaField = new JTextField("0", 5);
      private JTextField phiField = new JTextField("0", 5);
      private JTextField camField = new JTextField("0,0,0", 20);
      private JPanel matrix = new JPanel(new GridLayout(4, 4));
      private JLabel[] grid = new JLabel[16];

      Controls(){
         for(int i = 0; i < 16; i++){
            grid[i] = new JLabel(String.format("%5.2f", (float) i));
         }
         matrix.add(grid[0]);
         matrix.add(grid[4]);
         matrix.add(grid[8]);
         matrix.add(grid[12]);
         matrix.add(grid[1]);
         matrix.add(grid[5]);
         matrix.add(grid[9]);
         matrix.add(grid[13]);
         matrix.add(grid[2]);
         matrix.add(grid[6]);
         matrix.add(grid[10]);
         matrix.add(grid[14]);
         matrix.add(grid[3]);
         matrix.add(grid[7]);
         matrix.add(grid[11]);
         matrix.add(grid[15]);
         controlPanel.add(matrix);
         controlPanel.add(new JLabel("    theta:"));
         controlPanel.add(thetaField);
         controlPanel.add(new JLabel("    phi:"));
         controlPanel.add(phiField);
         controlPanel.add(new JLabel("    cam:"));
         controlPanel.add(camField);
      }

      JPanel getContainer(){
         return (controlPanel);
      }

      void incFrame(){
         long now = System.currentTimeMillis();
         long time = now - last;
         frames++;
         if(time > 1000){ // We update the frames/sec every second
            if(running){
               final float framesPerSecond = (frames * 1000.0f) / time;
               framesPerSecondTextField.setText(String.format("%5.2f", framesPerSecond));
            }
            frames = 0;
            last = now;
         }
      }

      void setMatrix(float[] matrix){
         for(int i = 0; i < 16; i++){
            grid[i].setText(String.format("%5.2f", matrix[i]));
         }
      }

      void setTheta(float _theta){
         thetaField.setText(String.format("%5.2f", _theta));
      }

      void setPhi(float _phi){
         phiField.setText(String.format("%5.2f", _phi));
      }

      boolean isRunning(){
         return (running);
      }

      public void setCam(float camX, float camY, float camZ){
         camField.setText(String.format("%5.2f, %5.2f, %5.2f", camX, camY, camZ));
      }
   }


   public void main(){
      JFrame frame = new JFrame("NBody");
      JPanel panel = new JPanel(new BorderLayout());
      Controls controls = new Controls();
      panel.add(controls.getContainer(), BorderLayout.NORTH);

      Camera camera = new Camera();
      Universe universe = new Universe(camera, 512, 512){
         Texture texture;

         public void render(GL2 gl){
            texture.enable(gl);
            texture.bind(gl);

            float[] modelview = new float[16];
            gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
            float camX = -(modelview[0] * modelview[12] + modelview[1] * modelview[13] + modelview[2] * modelview[14]);
            float camY = -(modelview[4] * modelview[12] + modelview[5] * modelview[13] + modelview[6] * modelview[14]);
            float camZ = -(modelview[8] * modelview[12] + modelview[9] * modelview[13] + modelview[10] * modelview[14]);


            controls.setMatrix(modelview);
            controls.setTheta(camera.getTheta());
            controls.setPhi(camera.getPhi());
            controls.setCam(camX, camY, camZ);

            gl.glColor3f(1f, 1f, 1f);
            gl.glBegin(GL2.GL_QUADS); {
               for(int x = 0; x < 3; x++){
                  for(int y = 0; y < 3; y++){
                     for(int z = 0; z < 3; z++){

                        float xcenter = (x - 1) * 20;
                        float ycenter = (y - 1) * 20;
                        float zcenter = (z - 1) * 20;


                        gl.glTexCoord2f(0, 0);
                        gl.glVertex3f(xcenter - 10, ycenter - 10, zcenter);
                        gl.glTexCoord2f(0, 1);
                        gl.glVertex3f(xcenter - 10, ycenter + 10, zcenter);
                        gl.glTexCoord2f(1, 1);
                        gl.glVertex3f(xcenter + 10, ycenter + 10, zcenter);
                        gl.glTexCoord2f(1, 0);
                        gl.glVertex3f(xcenter + 10, ycenter - 10, zcenter);

                        gl.glTexCoord2f(0, 0);
                        gl.glVertex3f(xcenter, ycenter - 10, zcenter - 10);
                        gl.glTexCoord2f(0, 1);
                        gl.glVertex3f(xcenter, ycenter + 10, zcenter - 10);
                        gl.glTexCoord2f(1, 1);
                        gl.glVertex3f(xcenter, ycenter + 10, zcenter + 10);
                        gl.glTexCoord2f(1, 0);
                        gl.glVertex3f(xcenter, ycenter - 10, zcenter + 10);

                        gl.glTexCoord2f(0, 0);
                        gl.glVertex3f(xcenter - 10, ycenter, zcenter - 10);
                        gl.glTexCoord2f(0, 1);
                        gl.glVertex3f(xcenter - 10, ycenter, zcenter + 10);
                        gl.glTexCoord2f(1, 1);
                        gl.glVertex3f(xcenter + 10, ycenter, zcenter + 10);
                        gl.glTexCoord2f(1, 0);
                        gl.glVertex3f(xcenter + 10, ycenter, zcenter - 10);
                     }
                  }
               }
            } gl.glEnd();
         }

         public void setup(GL2 gl){
            texture = getTexture("particle", "jpg");
         }
      };


      panel.add(universe.getCanvas(), BorderLayout.CENTER);
      frame.getContentPane().add(panel, BorderLayout.CENTER);
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
      (new FPSAnimator(universe.getCanvas(), 100)).start();

   }

   public static void main(String[] args){
      Test test = new Test();
      test.main();
   }

}
