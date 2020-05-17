package kerneltest;


/*
 * @(#)Graph.java	1.9 99/08/04
 *
 * Copyright (c) 1997, 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR OREF PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS OREF RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;


class Node{
   double x;
   double y;
   double z;

   double dx;
   double dy;
   double dz;

   boolean fixed;
   String lbl;
}


class Edge{
   int from;
   int to;
   double len;
}


class GraphPanel extends Panel implements Runnable, MouseListener, MouseMotionListener{
   Graph graph;
   int nnodes;
   Node nodes[] = new Node[1000];

   int nedges;
   Edge edges[] = new Edge[2000];

   Thread relaxer;
   boolean stress;
   boolean random;

   GraphPanel(Graph graph){
      this.graph = graph;
      addMouseListener(this);
   }

   int findNode(String lbl){
      for(int i = 0; i < nnodes; i++){
         if(nodes[i].lbl.equals(lbl)){
            return i;
         }
      }
      return addNode(lbl);
   }

   int addNode(String lbl){
      Node n = new Node();
      n.x = 10 + 380 * Math.random();
      n.y = 10 + 380 * Math.random();
      n.z = 10 + 380 * Math.random();
      n.lbl = lbl;
      nodes[nnodes] = n;
      return nnodes++;
   }

   void addEdge(String from, String to, int len){
      Edge e = new Edge();
      e.from = findNode(from);
      e.to = findNode(to);
      e.len = len;
      edges[nedges++] = e;
   }

   public void run(){
      Thread me = Thread.currentThread();
      while(relaxer == me){
         relax();
         if(random && (Math.random() < 0.03)){
            Node n = nodes[(int) (Math.random() * nnodes)];
            if(!n.fixed){
               n.x += 100 * Math.random() - 50;
               n.y += 100 * Math.random() - 50;
               n.z += 100 * Math.random() - 50;
            }
            // graph.play(graph.getCodeBase(), "audio/drip.au");
         }
         try{
            Thread.sleep(100);
         }catch(InterruptedException e){
            break;
         }
      }
   }

   synchronized void relax(){
      for(int i = 0; i < nedges; i++){
         Edge e = edges[i];
         double vx = nodes[e.to].x - nodes[e.from].x;
         double vy = nodes[e.to].y - nodes[e.from].y;
         double vz = nodes[e.to].z - nodes[e.from].z;
         double len = Math.sqrt(vx * vx + vy * vy + vz * vz);
         len = (len == 0) ? .0001 : len;
         double f = (edges[i].len - len) / (len * 3);
         double dx = f * vx;
         double dy = f * vy;
         double dz = f * vz;

         nodes[e.to].dx += dx;
         nodes[e.to].dy += dy;
         nodes[e.to].dz += dz;
         nodes[e.from].dx += -dx;
         nodes[e.from].dy += -dy;
         nodes[e.from].dz += -dz;
      }


      long start = System.currentTimeMillis();

      for(int i = 0; i < nnodes; i++){
         Node n1 = nodes[i];
         double dx = 0;
         double dy = 0;
         double dz = 0;

         for(int j = 0; j < nnodes; j++){
            if(i == j){
               continue;
            }
            Node n2 = nodes[j];
            double vx = n1.x - n2.x;
            double vy = n1.y - n2.y;
            double vz = n1.z - n2.z;
            double len = vx * vx + vy * vy + vz * vz;
            if(len == 0){
               dx += Math.random();
               dy += Math.random();
               dz += Math.random();
            }else if(len < 100 * 100 * 100){
               dx += vx / len;
               dy += vy / len;
               dz += vz / len;
            }
         }
         double dlen = dx * dx + dy * dy + dz * dz;
         if(dlen > 0){
            dlen = Math.sqrt(dlen) / 2;
            n1.dx += dx / dlen;
            n1.dy += dy / dlen;
            n1.dz += dz / dlen;
         }
      }

      long end = System.currentTimeMillis();
      System.out.println("end-start=" + (end - start));


      Dimension d = getSize();
      for(int i = 0; i < nnodes; i++){
         Node n = nodes[i];
         if(!n.fixed){
            n.x += Math.max(-5, Math.min(5, n.dx));

            n.y += Math.max(-5, Math.min(5, n.dy));
            n.z += Math.max(-5, Math.min(5, n.dz));
         }
         if(n.x < 0){
            n.x = 0;
         }else if(n.x > d.width){
            n.x = d.width;
         }
         if(n.y < 0){
            n.y = 0;
         }else if(n.y > d.height){
            n.y = d.height;
         }
         if(n.z < 0){
            n.z = 0;
         }else if(n.z > d.width){
            n.z = d.width;
         }
         n.dx /= 2;
         n.dy /= 2;
         n.dz /= 2;
      }
      repaint();
   }

   Node pick;
   boolean pickfixed;
   Image offscreen;
   Dimension offscreensize;
   Graphics2D offgraphics;

   final Color fixedColor = Color.red;
   final Color selectColor = Color.pink;
   final Color edgeColor = Color.black;
   final Color nodeColor = new Color(250, 220, 100);
   final Color stressColor = Color.darkGray;
   final Color arcColor1 = Color.black;
   final Color arcColor2 = Color.pink;
   final Color arcColor3 = Color.red;

   public void paintNode(Graphics2D g, Node n, FontMetrics fm){

      int x = (int) n.x;
      int y = (int) n.y;
      g.setColor((n == pick) ? selectColor : (n.fixed ? fixedColor : nodeColor));
      int w = fm.stringWidth(n.lbl) + 10;
      int h = fm.getHeight() + 4;
      g.fillRect(x - w / 2, y - h / 2, w, h);
      g.setColor(Color.black);
      g.drawRect(x - w / 2, y - h / 2, w - 1, h - 1);
      g.drawString(n.lbl, x - (w - 10) / 2, (y - (h - 4) / 2) + fm.getAscent());
   }

   public synchronized void update(Graphics g){
      Dimension d = getSize();
      if((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)){
         offscreen = createImage(d.width, d.height);
         offscreensize = d;
         if(offgraphics != null){
            offgraphics.dispose();
         }
         offgraphics = (Graphics2D) offscreen.getGraphics();
         offgraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         offgraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         offgraphics.setFont(getFont());
      }

      offgraphics.setColor(getBackground());
      offgraphics.fillRect(0, 0, d.width, d.height);
      for(int i = 0; i < nedges; i++){
         Edge e = edges[i];
         int x1 = (int) nodes[e.from].x;
         int y1 = (int) nodes[e.from].y;
         int x2 = (int) nodes[e.to].x;
         int y2 = (int) nodes[e.to].y;

         int len = (int) Math.abs(Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) - e.len);
         offgraphics.setColor((len < 10) ? arcColor1 : (len < 20 ? arcColor2 : arcColor3));
         offgraphics.drawLine(x1, y1, x2, y2);
         if(stress){
            String lbl = String.valueOf(len);
            offgraphics.setColor(stressColor);
            offgraphics.drawString(lbl, x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2);
            offgraphics.setColor(edgeColor);
         }
      }

      FontMetrics fm = offgraphics.getFontMetrics();
      for(int i = 0; i < nnodes; i++){
         paintNode(offgraphics, nodes[i], fm);
      }
      g.drawImage(offscreen, 0, 0, null);
   }

   //1.1 event handling
   public void mouseClicked(MouseEvent e){
   }

   public void mousePressed(MouseEvent e){
      addMouseMotionListener(this);
      double nearest = Double.MAX_VALUE;
      int x = e.getX();
      int y = e.getY();
      for(int i = 0; i < nnodes; i++){
         Node n = nodes[i];
         double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
         if(dist < nearest){
            pick = n;
            nearest = dist;
         }
      }
      pickfixed = pick.fixed;
      pick.fixed = true;
      pick.x = x;
      pick.y = y;
      repaint();
      e.consume();
   }

   public void mouseReleased(MouseEvent e){
      removeMouseMotionListener(this);
      if(pick != null){
         pick.x = e.getX();
         pick.y = e.getY();
         pick.fixed = pickfixed;
         pick = null;
      }
      repaint();
      e.consume();
   }

   public void mouseEntered(MouseEvent e){
   }

   public void mouseExited(MouseEvent e){
   }

   public void mouseDragged(MouseEvent e){
      pick.x = e.getX();
      pick.y = e.getY();
      repaint();
      e.consume();
   }

   public void mouseMoved(MouseEvent e){
   }

   public void start(){
      relaxer = new Thread(this);
      relaxer.start();
   }

   public void stop(){
      relaxer = null;
   }

}


public class Graph extends JFrame implements ActionListener, ItemListener{

   GraphPanel panel;
   Panel controlPanel;

   Button scramble = new Button("Scramble");
   Button shake = new Button("Shake");
   Checkbox stress = new Checkbox("Stress");
   Checkbox random = new Checkbox("Random");

   public Graph(){
      setLayout(new BorderLayout());
      panel = new GraphPanel(this);
      add("Center", panel);
      controlPanel = new Panel();
      add("South", controlPanel);

      controlPanel.add(scramble);
      scramble.addActionListener(this);
      controlPanel.add(shake);
      shake.addActionListener(this);
      controlPanel.add(stress);
      stress.addItemListener(this);
      controlPanel.add(random);
      random.addItemListener(this);

      for(int i = 0; i < 300; i++){
         String from = "" + ((int) (Math.random() * 100));
         String to = "" + ((int) (Math.random() * 100));
         if(!from.equals(to)){
            panel.addEdge(from, to, ((int) (Math.random() * 200) + 50));
         }
      }

   }


   public void actionPerformed(ActionEvent e){
      Object src = e.getSource();

      if(src == scramble){
         // play(getCodeBase(), "audio/computer.au");
         Dimension d = getSize();
         for(int i = 0; i < panel.nnodes; i++){
            Node n = panel.nodes[i];
            if(!n.fixed){
               n.x = 10 + (d.width - 20) * Math.random();
               n.y = 10 + (d.height - 20) * Math.random();
               n.z = 10 + (d.height - 20) * Math.random();
            }
         }
         return;
      }

      if(src == shake){

         Dimension d = getSize();
         for(int i = 0; i < panel.nnodes; i++){
            Node n = panel.nodes[i];
            if(!n.fixed){
               n.x += 80 * Math.random() - 40;
               n.y += 80 * Math.random() - 40;
               n.z += 80 * Math.random() - 40;
            }
         }
      }
   }

   void start(){
      panel.start();
   }


   public void itemStateChanged(ItemEvent e){
      Object src = e.getSource();
      boolean on = e.getStateChange() == ItemEvent.SELECTED;
      if(src == stress){
         panel.stress = on;
      }else if(src == random){
         panel.random = on;
      }
   }


   static public void main(String[] args){
      Graph g = new Graph();
      g.setVisible(true);
      g.pack();
      g.start();
      g.setDefaultCloseOperation(EXIT_ON_CLOSE);
   }

}
