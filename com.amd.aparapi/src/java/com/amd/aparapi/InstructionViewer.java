package com.amd.aparapi;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.amd.aparapi.InstructionSet.CompositeInstruction;
import com.amd.aparapi.InstructionViewer.Form.Check;
import com.amd.aparapi.InstructionViewer.Form.Template;
import com.amd.aparapi.InstructionViewer.Form.Toggle;

public class InstructionViewer implements Config.InstructionListener{

   public static abstract class Form<T extends Form.Template> {
      public @interface OneOf {
         String label();

         String[] options();
      }

      public interface Template{
      }

      @Retention(RetentionPolicy.RUNTIME) public @interface List {
         Class<?> value();

      }

      @Retention(RetentionPolicy.RUNTIME) public @interface Toggle {
         String label();

         String on();

         String off();
      }

      @Retention(RetentionPolicy.RUNTIME) public @interface Check {
         String label();
      }

      public final static int INSET = 5;

      private T template;

      JPanel panel;

      private SpringLayout layout = new SpringLayout();

      void setBoolean(Field _field, boolean _value) {
         try {
            _field.setBoolean(template, _value);
         } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      boolean getBoolean(Field _field) {
         try {
            return (_field.getBoolean(template));
         } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         return (false);
      }

      Object get(Field _field) {
         try {
            return (_field.get(template));
         } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         return (null);
      }

      public Form(T _template) {
         template = _template;
         panel = new JPanel(layout);
         JComponent last = panel;
         Map<Field, JLabel> fieldToLabelMap = new LinkedHashMap<Field, JLabel>();
         Field fieldWithWidestLabel = null;
         int fieldWithWidestLabelWidth = 0;

         // we need to know the widest Label so create the labels in one pass
         for (Field field : template.getClass().getFields()) {
            String labelString = null;

            Check checkAnnotation = field.getAnnotation(Check.class);
            if (checkAnnotation != null) {
               labelString = checkAnnotation.label();
            } else {
               Toggle toggleAnnotation = field.getAnnotation(Toggle.class);
               if (toggleAnnotation != null) {
                  labelString = toggleAnnotation.label();
               }
            }
            if (labelString != null) {
               JLabel label = new JLabel(labelString);
               panel.add(label);

               fieldToLabelMap.put(field, label);
               if (labelString.length() > fieldWithWidestLabelWidth) {
                  fieldWithWidestLabel = field;
                  fieldWithWidestLabelWidth = labelString.length();
               }
            }
         }

         for (Field field : fieldToLabelMap.keySet()) {
            layout.putConstraint(SpringLayout.NORTH, fieldToLabelMap.get(field), INSET, (last == panel) ? SpringLayout.NORTH
                  : SpringLayout.SOUTH, last);
            layout.putConstraint(SpringLayout.WEST, fieldToLabelMap.get(field), INSET, SpringLayout.WEST, panel);
            JComponent newComponent = null;

            if (field.getType().isAssignableFrom(Boolean.TYPE)) {
               final Field booleanField = field;

               Toggle toggleAnnotation = field.getAnnotation(Toggle.class);
               if (toggleAnnotation != null) {
                  final String toggleButtonOnLabel = toggleAnnotation.on();
                  final String toggleButtonOffLabel = toggleAnnotation.off();
                  String toggleButtonLabel = getBoolean(field) ? toggleButtonOnLabel : toggleButtonOffLabel;
                  JToggleButton toggleButton = new JToggleButton(toggleButtonLabel, getBoolean(field));
                  toggleButton.addActionListener(new ActionListener(){
                     @Override public void actionPerformed(ActionEvent _actionEvent) {
                        JToggleButton toggleButton = ((JToggleButton) _actionEvent.getSource());
                        //  System.out.println("toggle toggle "+toggleButton);
                        if (toggleButton.getText().equals(toggleButtonOnLabel)) {
                           toggleButton.setText(toggleButtonOffLabel);
                           setBoolean(booleanField, false);

                        } else {
                           toggleButton.setText(toggleButtonOnLabel);
                           setBoolean(booleanField, true);

                        }
                        sync();

                     }
                  });
                  newComponent = toggleButton;
               }
               Check checkAnnotation = field.getAnnotation(Check.class);
               if (checkAnnotation != null) {
                  JCheckBox checkBox = new JCheckBox();
                  checkBox.setSelected(getBoolean(field));

                  checkBox.addChangeListener(new ChangeListener(){

                     @Override public void stateChanged(ChangeEvent _changeEvent) {

                        JCheckBox checkBox = ((JCheckBox) _changeEvent.getSource());
                        //  System.out.println("check toggle "+checkBox);
                        setBoolean(booleanField, checkBox.isSelected());
                        sync();

                     }
                  });
                  newComponent = checkBox;
               }
            }
            if (newComponent != null) {
               panel.add(newComponent);
               layout.putConstraint(SpringLayout.NORTH, newComponent, INSET, (last == panel) ? SpringLayout.NORTH
                     : SpringLayout.SOUTH, last);
               layout.putConstraint(SpringLayout.WEST, newComponent, INSET, SpringLayout.EAST,
                     fieldToLabelMap.get(fieldWithWidestLabel));
               layout.putConstraint(SpringLayout.EAST, newComponent, INSET, SpringLayout.EAST, panel);
            }
            last = newComponent;
         }

         layout.layoutContainer(panel);

      }

      public abstract void sync();

      public Component getPanel() {
         return (panel);
      }
   }

   public static final int VMARGIN = 2;

   public static final int HMARGIN = 2;

   public static final int HGAPROOT = 100;

   public static final int HGAP = 40;

   public static final int VGAP = 20;

   public static final int ARROWGAP = 5;

   public static final int EDGEGAP = 20;

   public static final int CURVEBOW = 20;

   public static class Options implements Template{

      @Toggle(label = "Fold", on = "On", off = "Off") public boolean fold = true;

      @Check(label = "Fan Edges") public boolean edgeFan = true;

      @Check(label = "Curves") public boolean edgeCurve = false;

      @Check(label = "PC") public boolean showPc = true;

      @Check(label = "Bytecode Labels") public boolean verboseBytecodeLabels = false;

      @Check(label = "Collapse All") public boolean collapseAll = false;

      /* @Check(label = "Show expressions")*/public boolean showExpressions = false;

   }

   private static class XY{
      public XY(double _x, double _y) {
         x = _x;
         y = _y;
      }

      double x, y;
   }

   private static class View{
      AffineTransform offGraphicsTransform = new AffineTransform();

      private double scale = 1;

      private double x;

      private double y;

      public double translatex(int _screenx) {
         return ((_screenx - offGraphicsTransform.getTranslateX()) / offGraphicsTransform.getScaleX());

      }

      public double screenx() {
         return (offGraphicsTransform.getScaleX() * x + offGraphicsTransform.getTranslateX());
      }

      public double translatey(int _screeny) {
         return ((_screeny - offGraphicsTransform.getTranslateY()) / offGraphicsTransform.getScaleY());
      }

      public double screeny() {
         return (offGraphicsTransform.getScaleY() * y + offGraphicsTransform.getTranslateY());
      }
   }

   private JPanel container;

   private BufferedImage offscreen;

   private Dimension offscreensize;

   private Graphics2D offgraphics;

   public void dirty() {
      dirty = true;

      container.repaint();
   }

   private boolean dirty = false;

   private View view = new View();

   private XY dragStart = null;

   public synchronized void draw(Graphics _g) {

      Dimension containerSize = container.getSize();
      if (dirty || (offscreen == null) || (containerSize.width != offscreensize.width)
            || (containerSize.height != offscreensize.height)) {
         offscreensize = new Dimension(containerSize.width, containerSize.height);
         offscreen = (BufferedImage) container.createImage(offscreensize.width, offscreensize.height);

         if (offgraphics != null) {
            offgraphics.dispose();
         }
         offgraphics = offscreen.createGraphics();

         offgraphics.setFont(container.getFont());
         offgraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         offgraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         offgraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         // offgraphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
         offgraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
         AffineTransform offGraphicsTransform = new AffineTransform();
         offgraphics.setTransform(offGraphicsTransform);
         offgraphics.setColor(container.getBackground());
         offgraphics.fillRect(0, 0, (offscreensize.width), (offscreensize.height));
         offGraphicsTransform.setToTranslation(view.screenx(), view.screeny());
         offGraphicsTransform.scale(view.scale, view.scale);
         offgraphics.setTransform(offGraphicsTransform);
         view.offGraphicsTransform = offGraphicsTransform;
         dirty = false;

      } else {
         offgraphics.setColor(container.getBackground());
         offgraphics.fillRect(0, 0, (offscreensize.width), (offscreensize.height));
      }
      render(offgraphics);
      _g.drawImage(offscreen, 0, 0, null);

   }

   public Component getContainer() {
      return (container);
   }

   public void text(Graphics2D _g, String _text, double _x, double _y) {
      FontMetrics fm = _g.getFontMetrics();
      _g.drawString(_text, (int) _x, (int) (_y - fm.getAscent() + fm.getHeight()));

   }

   public void text(Graphics2D _g, Color _color, String _text, double _x, double _y) {
      Color color = _g.getColor();
      _g.setColor(_color);
      text(_g, _text, _x, _y);
      _g.setColor(color);

   }

   public void line(Graphics2D _g, Stroke _stroke, double _x1, double _y1, double _x2, double _y2) {
      Stroke stroke = _g.getStroke();
      _g.setStroke(_stroke);
      line(_g, _x1, _y1, _x2, _y2);
      _g.setStroke(stroke);
   }

   public void stroke(Graphics2D _g, Stroke _stroke, Shape _rect) {
      Stroke stroke = _g.getStroke();
      _g.setStroke(_stroke);
      draw(_g, _rect);
      _g.setStroke(stroke);
   }

   public void fill(Graphics2D _g, Color _color, Shape _rect) {
      Color color = _g.getColor();
      _g.setColor(_color);
      fill(_g, _rect);
      _g.setColor(color);
   }

   public void fillStroke(Graphics2D _g, Color _fillColor, Color _strokeColor, Stroke _stroke, Shape _rect) {
      Color color = _g.getColor();
      _g.setColor(_fillColor);
      fill(_g, _rect);
      _g.setColor(_strokeColor);
      stroke(_g, _stroke, _rect);
      _g.setColor(color);
   }

   public void line(Graphics2D _g, double _x1, double _y1, double _x2, double _y2) {
      _g.drawLine((int) _x1, (int) _y1, (int) _x2, (int) _y2);
   }

   public void draw(Graphics2D _g, Shape _rectangle) {
      _g.draw(_rectangle);
   }

   public void fill(Graphics2D _g, Shape _rectangle) {
      _g.fill(_rectangle);
   }

   public Options config = new Options();

   final private Color unselectedColor = Color.WHITE;

   final private Color selectedColor = Color.gray.brighter();

   private Stroke thickStroke = new BasicStroke((float) 2.0);

   private Stroke thinStroke = new BasicStroke((float) 1.0);

   private Stroke outlineStroke = new BasicStroke((float) 0.5);

   public Polygon arrowHeadOut = new Polygon();
   {
      arrowHeadOut.addPoint(8, -4);
      arrowHeadOut.addPoint(0, 0);
      arrowHeadOut.addPoint(8, 4);
      arrowHeadOut.addPoint(8, -4);
   }

   Polygon arrowHeadIn = new Polygon();
   {
      arrowHeadIn.addPoint(0, -4);
      arrowHeadIn.addPoint(8, 0);
      arrowHeadIn.addPoint(0, 4);
      arrowHeadIn.addPoint(0, -4);
   }

   public class InstructionView{

      private Instruction instruction;

      private Shape shape;

      public Instruction branchTarget;

      public Instruction collapsedBranchTarget;

      public String label;

      public boolean dim;

      public InstructionView(Instruction _instruction) {
         instruction = _instruction;
      }

   }

   private Map<Instruction, InstructionView> locationToInstructionViewMap = new HashMap<Instruction, InstructionView>();

   InstructionView getInstructionView(Instruction _instruction) {

      InstructionView instructionView = locationToInstructionViewMap.get(_instruction);
      if (instructionView == null) {
         locationToInstructionViewMap.put(_instruction, instructionView = new InstructionView(_instruction));

      }
      return (instructionView);
   }

   double foldPlace(Graphics2D _g, InstructionView _instructionView, double _x, double _y, boolean _dim) {
      _instructionView.dim = _dim;
      FontMetrics fm = _g.getFontMetrics();

      _instructionView.label = InstructionHelper.getLabel(_instructionView.instruction, config.showPc, config.showExpressions,
            config.verboseBytecodeLabels);

      int w = fm.stringWidth(_instructionView.label) + HMARGIN;
      int h = fm.getHeight() + VMARGIN;

      double y = _y;
      double x = _x + w + (_instructionView.instruction.getRootExpr() == _instructionView.instruction ? HGAP : HGAP);

      if (!config.collapseAll && !config.showExpressions) {

         for (Instruction e = _instructionView.instruction.getFirstChild(); e != null; e = e.getNextExpr()) {

            y = foldPlace(_g, getInstructionView(e), x, y, _dim);
            if (e != _instructionView.instruction.getLastChild()) {
               y += VGAP;
            }
         }

      }
      double top = (y + _y) / 2 - (h / 2);
      _instructionView.shape = new Rectangle((int) _x, (int) top, w, h);
      return (Math.max(_y, y));

   }

   void foldRender(Graphics2D _g, InstructionView _instructionView) {
      Instruction instruction = _instructionView.instruction;
      if (!config.collapseAll && !config.showExpressions) {
         for (Instruction e = instruction.getFirstChild(); e != null; e = e.getNextExpr()) {

            foldRender(_g, getInstructionView(e));

         }
      }
      if (_instructionView.dim) {
         _g.setColor(unselectedColor);
      } else {
         _g.setColor(selectedColor);
      }
      _g.fill(_instructionView.shape);
      _g.setColor(Color.black);
      _g.setStroke(outlineStroke);
      _g.draw(_instructionView.shape);
      text(_g, _instructionView.label, _instructionView.shape.getBounds().getCenterX()
            - _instructionView.shape.getBounds().getWidth() / 2, _instructionView.shape.getBounds().getCenterY());

      if (!config.collapseAll && !config.showExpressions) {

         if (config.edgeFan) {

            for (Instruction e = instruction.getFirstChild(); e != null; e = e.getNextExpr()) {
               InstructionView iv = getInstructionView(e);
               double x1 = _instructionView.shape.getBounds().getMaxX() + ARROWGAP;

               double y1 = _instructionView.shape.getBounds().getCenterY();
               double x2 = iv.shape.getBounds().getMinX() - 5;
               double y2 = iv.shape.getBounds().getCenterY();

               if (config.edgeCurve) {
                  _g.draw(new CubicCurve2D.Double(x1, y1, x1 + CURVEBOW, y1, x2 - CURVEBOW, y2, x2, y2));
               } else {
                  double dx = (x1 - x2);
                  double dy = (y1 - y2);

                  AffineTransform transform = _g.getTransform();
                  double hypot = Math.hypot(dy, dx);
                  double angle = Math.atan2(dy, dx);
                  _g.translate(x2, y2);
                  _g.rotate(angle);
                  line(_g, thickStroke, 0, 0, hypot, 0);
                  _g.fillPolygon(arrowHeadOut);
                  _g.setTransform(transform);
               }
            }

         } else {

            _g.setStroke(thickStroke);
            if (instruction.getFirstChild() != null && instruction.getFirstChild() != instruction.getLastChild()) { // >1 children
               InstructionView iv0 = getInstructionView(instruction.getFirstChild());
               InstructionView ivn = getInstructionView(instruction.getLastChild());

               double midx = (_instructionView.shape.getBounds().getMaxX() + iv0.shape.getBounds().getMinX()) / 2;
               line(_g, midx, iv0.shape.getBounds().getCenterY(), midx, ivn.shape.getBounds().getCenterY());
               line(_g, _instructionView.shape.getBounds().getMaxX() + ARROWGAP, _instructionView.shape.getBounds().getCenterY(),
                     midx, _instructionView.shape.getBounds().getCenterY());

               for (Instruction e = instruction.getFirstChild(); e != null; e = e.getNextExpr()) {
                  InstructionView iv = getInstructionView(e);
                  line(_g, midx, iv.shape.getBounds().getCenterY(), iv.shape.getBounds().getMinX() - ARROWGAP, iv.shape.getBounds()
                        .getCenterY());
               }
            } else if (instruction.getFirstChild() != null) { // 1 child
               InstructionView iv = getInstructionView(instruction.getFirstChild());
               line(_g, _instructionView.shape.getBounds().getMaxX() + ARROWGAP, _instructionView.shape.getBounds().getCenterY(),
                     iv.shape.getBounds().getMinX() - ARROWGAP, iv.shape.getBounds().getCenterY());
            }
         }
      }

   }

   double flatPlace(Graphics2D _g, InstructionView _instructionView, double _x, double _y) {
      FontMetrics fm = _g.getFontMetrics();
      Instruction instruction = _instructionView.instruction;
      _instructionView.label = InstructionHelper.getLabel(instruction, config.showPc, config.showExpressions,
            config.verboseBytecodeLabels);

      int h = fm.getHeight() + 2;
      double top = _y / 2 - (h / 2);
      _instructionView.shape = new Rectangle((int) _x, (int) top, fm.stringWidth(_instructionView.label) + 2, h);
      return (_y + h);
   }

   void flatRender(Graphics2D _g, InstructionView _instructionView) {
      _g.setColor(unselectedColor);
      _g.fill(_instructionView.shape);
      _g.setColor(Color.black);
      stroke(_g, outlineStroke, _instructionView.shape);
      text(_g, _instructionView.label, _instructionView.shape.getBounds().getCenterX()
            - _instructionView.shape.getBounds().getWidth() / 2, _instructionView.shape.getBounds().getCenterY());
   }

   ClassModel classModel = null;

   public InstructionViewer(Color _background, String _name) {

      try {
         classModel = new ClassModel(Class.forName(_name));
      } catch (ClassParseException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      container = new JPanel(){
         /**
          * 
          */
         private static final long serialVersionUID = 1L;

         @Override public void paintComponent(Graphics g) {
            draw(g);
         }
      };
      container.setBackground(_background);

      MouseAdapter mouseAdaptor = new MouseAdapter(){
         @Override public void mouseEntered(MouseEvent e) {
            container.requestFocusInWindow();
         }

         @Override public void mouseDragged(MouseEvent e) {
            // System.out.println("dragged");
            if (dragStart != null) {
               view.x = view.translatex(e.getX()) - dragStart.x;
               view.y = view.translatey(e.getY()) - dragStart.y;
               dirty();
            }

         }

         @Override public void mousePressed(MouseEvent e) {

            if (e.getButton() == 1) {
               dragStart = new XY(view.translatex(e.getX()), view.translatey(e.getY()));
               dirty();

            } else if (e.getButton() == 3) {

               if (select(view.translatex(e.getX()), view.translatey(e.getY()))) {
                  dirty();
               }
            }

         }

         @Override public void mouseReleased(MouseEvent e) {
            dragStart = null;
            // container.repaint();
         }

         @Override public void mouseWheelMoved(MouseWheelEvent e) {
            view.scale += e.getWheelRotation() / 10.0;
            dirty();
         }

      };

      KeyAdapter keyAdaptor = new KeyAdapter(){
         @Override public void keyTyped(KeyEvent arg0) {
            if (arg0.getKeyChar() == '-' || arg0.getKeyChar() == '+') {
               if (arg0.getKeyChar() == '-') {
                  view.scale -= .1;
               } else {
                  view.scale += .1;
               }
               dirty();
            }

         }

      };
      container.addMouseMotionListener(mouseAdaptor);
      container.addMouseListener(mouseAdaptor);
      container.addMouseWheelListener(mouseAdaptor);
      container.addKeyListener(keyAdaptor);
      container.repaint();

   }

   public InstructionViewer() {

      JFrame frame = new JFrame();

      Color background = Color.WHITE;
      JPanel panel = new JPanel(new BorderLayout());
      JMenuBar menuBar = new JMenuBar();

      JMenu fileMenu = new JMenu("File");
      fileMenu.setMnemonic(KeyEvent.VK_F);
      ActionListener closeActionListener = new ActionListener(){
         @Override public void actionPerformed(ActionEvent arg0) {
            System.exit(1);
         }

      };
      ActionListener nextActionListener = new ActionListener(){
         @Override public void actionPerformed(ActionEvent arg0) {
            doorbell.ring();

         }

      };
      JMenuItem closeMenuItem = new JMenuItem("Close");
      closeMenuItem.setMnemonic(KeyEvent.VK_C);
      closeMenuItem.addActionListener(closeActionListener);
      fileMenu.add(closeMenuItem);
      menuBar.add(fileMenu);
      menuBar.setEnabled(true);
      frame.setJMenuBar(menuBar);

      // http://java.sun.com/docs/books/tutorial/uiswing/components/toolbar.html
      JToolBar toolBar = new JToolBar();
      JButton closeButton = new JButton("Close");
      closeButton.addActionListener(closeActionListener);
      toolBar.add(closeButton);

      JButton nextButton = new JButton("Next");
      nextButton.addActionListener(nextActionListener);
      toolBar.add(nextButton);

      panel.add(BorderLayout.PAGE_START, toolBar);

      container = new JPanel(){
         /**
          * 
          */
         private static final long serialVersionUID = 1L;

         @Override public void paintComponent(Graphics g) {
            draw(g);
         }
      };
      container.setBackground(Color.WHITE);

      MouseAdapter mouseAdaptor = new MouseAdapter(){
         @Override public void mouseEntered(MouseEvent e) {
            container.requestFocusInWindow();
         }

         @Override public void mouseDragged(MouseEvent e) {
            // System.out.println("dragged");
            if (dragStart != null) {
               view.x = view.translatex(e.getX()) - dragStart.x;
               view.y = view.translatey(e.getY()) - dragStart.y;
               dirty();
            }

         }

         @Override public void mousePressed(MouseEvent e) {

            if (e.getButton() == 1) {
               dragStart = new XY(view.translatex(e.getX()), view.translatey(e.getY()));
               dirty();

            } else if (e.getButton() == 3) {

               if (select(view.translatex(e.getX()), view.translatey(e.getY()))) {
                  dirty();
               }
            }

         }

         @Override public void mouseReleased(MouseEvent e) {
            dragStart = null;
            // container.repaint();
         }

         @Override public void mouseWheelMoved(MouseWheelEvent e) {
            view.scale += e.getWheelRotation() / 10.0;
            dirty();
         }

      };

      KeyAdapter keyAdaptor = new KeyAdapter(){
         @Override public void keyTyped(KeyEvent arg0) {
            if (arg0.getKeyChar() == '-' || arg0.getKeyChar() == '+') {
               if (arg0.getKeyChar() == '-') {
                  view.scale -= .1;
               } else {
                  view.scale += .1;
               }
               dirty();
            }

         }

      };
      container.addMouseMotionListener(mouseAdaptor);
      container.addMouseListener(mouseAdaptor);
      container.addMouseWheelListener(mouseAdaptor);
      container.addKeyListener(keyAdaptor);
      container.repaint();

      panel.add(BorderLayout.CENTER, container);

      JPanel controls = new JPanel(new BorderLayout());

      Form<Options> form = new Form<Options>(config){
         @Override public void sync() {
            dirty();
         }
      };
      controls.add(form.getPanel());

      controls.setPreferredSize(new Dimension(200, 500));
      panel.add(BorderLayout.EAST, controls);
      frame.setBackground(background);
      frame.getContentPane().add(panel);
      frame.setPreferredSize(new Dimension(1024, 1000));
      frame.pack();
      frame.setVisible(true);

   }

   public boolean select(double _x, double _y) {
      for (Instruction l = first; l != null; l = l.getNextPC()) {
         InstructionView iv = getInstructionView(l);
         if (iv.shape != null && iv.shape.contains(_x, _y)) {

            return (true);
         }
      }
      return (false);
   }

   public void render(Graphics2D _g) {
      if (first != null) {

         if (config.fold) {
            double y = 100;
            Instruction firstRoot = first.getRootExpr();
            List<InstructionView> instructionViews = new ArrayList<InstructionView>();

            Instruction lastInstruction = null;
            for (Instruction instruction = firstRoot; instruction != null; instruction = instruction.getNextExpr()) {
               InstructionView iv = getInstructionView(instruction);
               iv.dim = false;
               y = foldPlace(_g, iv, 100, y, false) + VGAP;
               instructionViews.add(iv);
               lastInstruction = instruction;
            }
            lastInstruction.getRootExpr();
            while (lastInstruction instanceof CompositeInstruction) {
               lastInstruction = lastInstruction.getLastChild();
            }
            for (Instruction instruction = lastInstruction.getNextPC(); instruction != null; instruction = instruction.getNextPC()) {

               InstructionView iv = getInstructionView(instruction);
               iv.dim = true;
               y = foldPlace(_g, iv, 100, y, true) + VGAP;
               instructionViews.add(iv);

            }

            _g.setColor(Color.black);

            for (InstructionView instructionView : instructionViews) {
               if (instructionView.instruction.isBranch()) {
                  Instruction rootFromInstruction = instructionView.instruction;
                  Instruction rootToInstruction = instructionView.instruction.asBranch().getTarget();
                  InstructionView fromIv = getInstructionView(rootFromInstruction);
                  InstructionView toIv = getInstructionView(rootToInstruction);
                  edge(_g, Color.BLACK, fromIv, toIv, null, null);
               }
            }

            InstructionView last = null;

            for (InstructionView instructionView : instructionViews) {

               foldRender(_g, instructionView);
               if (last != null) {
                  line(_g, thickStroke, 120, last.shape.getBounds().getMaxY(), 120, instructionView.shape.getBounds().getMinY());
               }
               foldRender(_g, instructionView);
               last = instructionView;
            }

         } else {
            double y = 100;
            for (Instruction l = first; l != null; l = l.getNextPC()) {

               y = flatPlace(_g, getInstructionView(l), 100, y) + VGAP;

            }

            _g.setColor(Color.black);
            for (Instruction l = first; l != null; l = l.getNextPC()) {
               if (l.isBranch()) {
                  Instruction rootFromInstruction = l;
                  Instruction rootToInstruction = l.asBranch().getTarget();
                  InstructionView fromIv = getInstructionView(rootFromInstruction);
                  InstructionView toIv = getInstructionView(rootToInstruction);

                  edge(_g, Color.BLACK, fromIv, toIv, null, null);
               }

            }

            InstructionView last = null;
            for (Instruction l = first; l != null; l = l.getNextPC()) {
               InstructionView iv = getInstructionView(l);

               if (last != null) {
                  line(_g, thickStroke, 120, last.shape.getBounds().getMaxY(), 120, iv.shape.getBounds().getMinY());
               }
               flatRender(_g, iv);
               last = iv;
            }
         }
      }

   }

   public void edge(Graphics2D _g, Color _color, InstructionView _branch, InstructionView _target, String _endLabel,
         String _startLabel) {

      int delta = _target.instruction.getThisPC() - _branch.instruction.getThisPC();
      int adjust = 7 + Math.abs(delta);
      double y1 = (int) _branch.shape.getBounds().getMaxY();
      if (_target.shape != null) {
         _g.setStroke(thinStroke);
         Color old = _g.getColor();
         _g.setColor(_color);
         double y2 = (int) _target.shape.getBounds().getMinY();
         if (delta > 0) {

            double x1 = (int) _branch.shape.getBounds().getMinX() - EDGEGAP;
            double x2 = (int) _target.shape.getBounds().getMinX() - EDGEGAP;

            _g.draw(new CubicCurve2D.Double(x1, y1, x1 - adjust, y1, x1 - adjust, y2, x2, y2));

            AffineTransform transform = _g.getTransform();
            _g.translate(x2 - 5, y2);
            _g.fillPolygon(arrowHeadIn);
            _g.setTransform(transform);

         } else {

            double x1 = (int) _branch.shape.getBounds().getMaxX() + EDGEGAP;
            double x2 = (int) _target.shape.getBounds().getMaxX() + EDGEGAP;

            _g.draw(new CubicCurve2D.Double(x1, y1, Math.max(x1, x2) + adjust, y1, Math.max(x1, x2) + adjust, y2, x2, y2));
            AffineTransform transform = _g.getTransform();

            _g.translate(x2 - 5, y2);
            _g.fillPolygon(arrowHeadOut);
            _g.setTransform(transform);

         }
         _g.setColor(old);
      }
   }

   volatile Instruction first = null;

   volatile Instruction current = null;

   @Override public void showAndTell(String message, Instruction head, Instruction _instruction) {

      if (first == null) {
         first = head;
      }
      current = _instruction;
      this.dirty();
      doorbell.snooze();

   }

   public static class DoorBell{
      volatile boolean notified = false;

      public synchronized void snooze() {
         while (!notified) {
            try {
               this.wait();
            } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
         notified = false;
      }

      public synchronized void ring() {
         notified = true;
         this.notify();
      }

   }

   public static DoorBell doorbell = new DoorBell();

   public static void main(String[] _args) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
         UnsupportedLookAndFeelException, AparapiException {

      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      Color background = Color.WHITE;
      JPanel panel = new JPanel(new BorderLayout());
      JMenuBar menuBar = new JMenuBar();

      JMenu fileMenu = new JMenu("File");
      fileMenu.setMnemonic(KeyEvent.VK_F);
      ActionListener closeActionListener = new ActionListener(){
         @Override public void actionPerformed(ActionEvent arg0) {
            System.exit(1);
         }

      };
      ActionListener nextActionListener = new ActionListener(){
         @Override public void actionPerformed(ActionEvent arg0) {
            doorbell.ring();

         }

      };
      JMenuItem closeMenuItem = new JMenuItem("Close");
      closeMenuItem.setMnemonic(KeyEvent.VK_C);
      closeMenuItem.addActionListener(closeActionListener);
      fileMenu.add(closeMenuItem);
      menuBar.add(fileMenu);
      menuBar.setEnabled(true);
      frame.setJMenuBar(menuBar);

      final InstructionViewer instructionViewer = new InstructionViewer(background, _args[0]);

      Config.instructionListener = instructionViewer;
      // http://java.sun.com/docs/books/tutorial/uiswing/components/toolbar.html
      JToolBar toolBar = new JToolBar();
      JButton closeButton = new JButton("Close");
      closeButton.addActionListener(closeActionListener);
      toolBar.add(closeButton);

      JButton nextButton = new JButton("Next");
      nextButton.addActionListener(nextActionListener);
      toolBar.add(nextButton);

      panel.add(BorderLayout.PAGE_START, toolBar);

      panel.add(BorderLayout.CENTER, instructionViewer.getContainer());

      JPanel controls = new JPanel(new BorderLayout());

      Form<Options> form = new Form<Options>(instructionViewer.config){
         @Override public void sync() {
            instructionViewer.dirty();
         }
      };
      controls.add(form.getPanel());

      controls.setPreferredSize(new Dimension(200, 500));
      panel.add(BorderLayout.EAST, controls);
      frame.setBackground(background);
      frame.getContentPane().add(panel);
      frame.setPreferredSize(new Dimension(800, 1000));
      frame.pack();
      frame.setVisible(true);

      (new Thread(new Runnable(){

         @Override public void run() {

            Entrypoint entrypoint;
            try {
               entrypoint = instructionViewer.classModel.getEntrypoint();
               MethodModel method = entrypoint.getMethodModel();
            } catch (AparapiException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }

         }

      })).start();

   }

}
