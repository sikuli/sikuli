/**
 * 
 */
package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import org.sikuli.guide.util.ComponentMover;
import org.sikuli.script.Debug;
import org.sikuli.script.Region;

public class OverviewWindow extends JWindow {

   ArrayList<StepThumbnail> stepThumbnails = new ArrayList<StepThumbnail>();

   Color selectionColor = new Color(240,255,255);
   
   JPanel panel;
   public OverviewWindow(Frame owner){         
      super(owner);

      panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      panel.setSize(240,600);    
      //panel.setBackground(new Color(255,245,238,150));
      //panel.setBackground(new Color(190,190,190));
      // http://www.tayloredmktg.com/rgb/
      panel.setBackground(new Color(119,136,153));
      add(panel);

      setSize(panel.getSize());

      ComponentMover cm = new ComponentMover();
      cm.registerComponent(this);
   }



   class MyImage extends JComponent {
      BufferedImage image;
      public void paintComponent(Graphics g){
         super.paintComponent(g);

         Graphics2D g2d = (Graphics2D) g;        
         g2d.drawImage(image,0,0,null,null);
         g2d.setColor(Color.black);
         g2d.drawRect(0,0,image.getWidth()-1,image.getHeight()-1);
      }
   }
   
   class StepThumbnail extends SikuliGuideComponent implements MouseListener{

      Step step;
      boolean selected;
      
      MyImage myImage = new MyImage();
      public StepThumbnail(Step step) {
         super();
         this.step = step;
         addMouseListener(this);      

         myImage.image = step.getThumbnailImage();

         JLabel label = new JLabel(""+(step.getIndex()+1));

         label.setSize(15,30);
         label.setLocation(5,0);
         
         myImage.setLocation(17,10);
         myImage.setSize(myImage.image.getWidth(),myImage.image.getHeight());
          
         add(label);
         add(myImage);
         
         Component c = Box.createRigidArea(new Dimension(0,10));
         c.setLocation(0,myImage.image.getHeight());

         add(c);
         
         setSize(label.getWidth() + myImage.getWidth(), myImage.getHeight()+20);
         //setMaximumSize(new Dimension(500,150));
         //setPreferredSize(new Dimension(300,300));
      }

      void refreshImage(){
         myImage.image = step.getThumbnailImage();
         repaint();
      }

      public void paintComponent(Graphics g){
         super.paintComponent(g);

         Graphics2D g2d = (Graphics2D) g;
         
         if (selected){
            g2d.setStroke(new BasicStroke(3f));
            g2d.setColor(selectionColor);
            g2d.fillRect(0,0,getWidth()-1,getHeight()-1);
            
          //  g2d.drawImage(image,15,0,null,null);

         }else{
            
         //   g2d.drawImage(image,15,0,null,null);
            g2d.setColor(Color.black);
           // g2d.drawRect(15,0,image.getWidth()-1,image.getHeight()-1);

         }
         
         


      }

      void setSelected(boolean selected){
         this.selected = selected;
         repaint();
      }

      @Override
      public void mouseClicked(MouseEvent e) {
         Debug.info("Thumbnail is clicked");
         editor.selectStep(step);
         selectStep(step);
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
      }

      @Override
      public void mouseReleased(MouseEvent e) {
      }
   }

   private EditorWindow editor;
   public void addStep(Step step){            
      StepThumbnail stepThumbnail = new StepThumbnail(step);

      stepThumbnails.add(stepThumbnail);


      panel.add(Box.createRigidArea(new Dimension(0,10)));
      panel.add(stepThumbnail);
      //panel.setSize(panel.getPreferredSize());
      repaint();
   }

   StepThumbnail getStepThumbnail(Step step){
      // TODO use hashmap to lookup 
      for (StepThumbnail stepThumbnail : stepThumbnails){
         if (stepThumbnail.step == step){
            return stepThumbnail;
         }
      }
      return null;
   }


   public void stepContentChanged(Step step){      
      StepThumbnail st = getStepThumbnail(step);
      if (st != null)
         st.refreshImage();   
   }

   Step selectedStep = null;
   public void selectStep(Step step){
      StepThumbnail st = getStepThumbnail(selectedStep);
      if (st != null)
         st.setSelected(false);   

      this.selectedStep = step;

      st = getStepThumbnail(selectedStep);
      if (st != null)
         st.setSelected(true);

      repaint();
   }

   public void setEditor(EditorWindow editor) {
      this.editor = editor;
   }
   public EditorWindow getEditor() {
      return editor;
   }


}