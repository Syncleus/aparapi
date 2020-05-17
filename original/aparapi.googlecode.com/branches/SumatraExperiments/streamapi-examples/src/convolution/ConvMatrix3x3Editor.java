
package convolution;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConvMatrix3x3Editor{
   Component component;

   float[] default3x3;

   float[] none3x3 = new float[] {
         0,
         0,
         0,
         0,
         1,
         0,
         0,
         0,
         0
   };

   float[] blur3x3 = new float[] {
         .1f,
         .1f,
         .1f,
         .1f,
         .1f,
         .1f,
         .1f,
         .1f,
         .1f
   };

   JSpinner[] spinners = new JSpinner[9];

   protected void updated(float[] _convMatrix3x3) {

   };

   void set(float[] _to, float[] _from) {
      for (int i = 0; i < 9; i++) {
         _to[i] = _from[i];
         spinners[i].setValue((Double) (double) _to[i]);

      }
      updated(_to);
   }

   ConvMatrix3x3Editor(final float[] _convMatrix3x3) {
      default3x3 = Arrays.copyOf(_convMatrix3x3, _convMatrix3x3.length);
      JPanel leftPanel = new JPanel();
      JPanel controlPanel = new JPanel();
      BoxLayout layout = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
      controlPanel.setLayout(layout);
      component = leftPanel;
      JPanel grid3x3Panel = new JPanel();
      controlPanel.add(grid3x3Panel);
      grid3x3Panel.setLayout(new GridLayout(3, 3));
      for (int i = 0; i < 9; i++) {
         final int index = i;
         SpinnerModel model = new SpinnerNumberModel(_convMatrix3x3[index], -50f, 50f, 1f);
         JSpinner spinner = new JSpinner(model);
         spinners[i] = spinner;
         spinner.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent ce) {
               JSpinner source = (JSpinner) ce.getSource();
               double value = ((Double) source.getValue());
               _convMatrix3x3[index] = (float) value;
               updated(_convMatrix3x3);
            }
         });
         grid3x3Panel.add(spinner);
      }
      String[] options = new String[] {
            "DEFAULT",
            "NONE",
            "BLUR"
      };
      JComboBox combo = new JComboBox(options);
      combo.addActionListener(new ActionListener(){

         @Override public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            String value = (String) cb.getSelectedItem();
            if (value.equals("DEFAULT")) {
               set(_convMatrix3x3, default3x3);
            } else if (value.equals("NONE")) {
               set(_convMatrix3x3, none3x3);
            } else if (value.equals("BLUR")) {
               set(_convMatrix3x3, blur3x3);
            }
         }

      });
      controlPanel.add(combo);

      leftPanel.add(controlPanel, BorderLayout.NORTH);
   }
}
