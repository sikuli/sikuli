package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;


class SimilaritySlider extends JSlider implements MouseMotionListener, MouseListener {
   final JPopupMenu pop = new JPopupMenu();
   JMenuItem item = new JMenuItem();

   public SimilaritySlider(int min, int max, int val){
      super(min,max,val);
      addMouseMotionListener( this );
      addMouseListener( this );
      pop.add( item );

      pop.setDoubleBuffered( true );

   }

   protected void paintComponent(Graphics g){
      int w = getWidth();
      final int margin = 13;
      final int y1 = 20, y2 = 30;
      for(int i=margin;i<w-margin;i++){
         float score = (float)i/(w-margin*2);
         g.setColor(getScoreColor(score));
         g.drawLine(i, y1, i, y2);
      }
      super.paintComponent(g);
   }

   static Color getScoreColor(double score){
      // map hue to 0.5~1.0
      Color c = new Color(
            Color.HSBtoRGB( 0.5f+(float)score/2, 1.0f, 1.0f));
      // map alpha to 20~150
      Color cMask = new Color(
            c.getRed(), c.getGreen(), c.getBlue(), 20+(int)(score*130));
      return cMask;
   }

   public void showToolTip ( MouseEvent me )
   {      
      String txt = String.format("%.2f", (float)getValue()/100);
      item.setText(txt);

      //limit the tooltip location relative to the slider
      Rectangle b = me.getComponent().getBounds();
      int x = me.getX();      
      x = (x > (b.x) ?  (b.x) : 
            (x < (b.x -b.width) ? (b.x -b.width) : x));

      pop.show( me.getComponent(), x - 5, -30 );

      item.setArmed( false );
      item.setSelected(false);
   }

   public void mouseDragged ( MouseEvent me )
   {
      showToolTip( me );
   }

   public void mouseMoved ( MouseEvent me ) { }

   public void mousePressed ( MouseEvent me ) {
      showToolTip( me );
   }

   public void mouseClicked ( MouseEvent me ) { }

   public void mouseReleased ( MouseEvent me ) {
      pop.setVisible( false );
   }

   public void mouseEntered ( MouseEvent me ) { }

   public void mouseExited ( MouseEvent me ) { }
}
