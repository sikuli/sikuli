package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

public class SklStepForegroundView extends JPanel implements PropertyChangeListener, StepDataEventListener  {

   SklStepModel _stepModel;
   SklStepForegroundView(SklStepModel stepModel){
      setLayout(null);
      setOpaque(true);         
      setModel(stepModel);  
   }

   JPanel panel;      
   void setModel(SklStepModel model){
      removeAll();

      if (_stepModel != null){
         _stepModel.removeDataEventListener(this);       
      }                     
      _stepModel = model;
      _stepModel.addDataEventListener(this);       


      panel = new JPanel();
      panel.setLayout(null);

      SklImageView imageView = (SklImageView) SklViewFactory.createView(_stepModel.getReferenceImageModel());
      panel.add(imageView,0);

      for (SklModel m : _stepModel.getModels()){
         SklView view = SklViewFactory.createView(m);
         panel.add(view,0);
      }      
      panel.setBounds(imageView.getBounds());
      int width = _stepModel.getReferenceImageModel().getImage().getWidth();
      int height = _stepModel.getReferenceImageModel().getImage().getHeight();
      panel.setSize(width,height);
      
      setLayout(null);
      add(panel);
      
      Rectangle r = getRegionToPaint();
      panel.setLocation(-r.x,-r.y);
      
      

   }

   Rectangle getForegroundBounds(SklStepModel stepModel){

      Rectangle bounds = null;
      for (SklModel model : stepModel.getModels()){

         Rectangle modelBounds = new Rectangle();
         modelBounds.x = model.getX();
         modelBounds.y = model.getY();
         modelBounds.width = model.getWidth();
         modelBounds.height = model.getHeight();

         if (bounds == null){
            bounds = modelBounds;             
         }else{
            bounds.add(modelBounds);
         }
      }

      return bounds;     
   }


   int marginSize = 40;

   Rectangle getRegionToPaint(){
      Rectangle regionToPaint;

      // if there are foreground objects
      if (_stepModel.getModels().size() > 0){

         Rectangle fgbounds = getForegroundBounds(_stepModel);

         Dimension fgSize = fgbounds.getSize();            
         Dimension windowSize = getSize();

         regionToPaint = new Rectangle();

         // center the fg region within the displayable window
         if (fgSize.width < windowSize.width){
            regionToPaint.x = fgbounds.x + fgbounds.width/2 - windowSize.width/2;
            regionToPaint.width = windowSize.width;
         } else {
            fgbounds.grow(10,10);
            regionToPaint.x = fgbounds.x;
            regionToPaint.width = fgbounds.width;

         }

         if (fgSize.height < windowSize.height){
            regionToPaint.y = fgbounds.y + fgbounds.height/2 - windowSize.height/2;
            regionToPaint.height = windowSize.height;
         } else {
            fgbounds.grow(10,10);
            regionToPaint.y = fgbounds.y;
            regionToPaint.height = fgbounds.height;
         }


      } else{

         regionToPaint = _stepModel.getReferenceImageModel().getBounds();             
      }
      
      return regionToPaint;
   }

   @Override
   public void paint(Graphics g){
      super.paint(g);
      // draw black boundary
      Graphics2D g2d = (Graphics2D) g;
      Rectangle r = getBounds();
      g2d.setStroke(new BasicStroke(1f));
       g2d.setColor(Color.black);
       g2d.drawRect(0,0,r.width - 1,r.height - 1);
   }
   @Override
   public void paintChildren(Graphics g){
      
      Rectangle r = getRegionToPaint();
      panel.setLocation(-r.x,-r.y);
      
      Graphics2D g2d = (Graphics2D) g;

      Dimension size = getSize();
      Rectangle region = getRegionToPaint();
     
      if (size.width < region.width || size.height < region.height){

         float scalex = 1f* size.width / region.width;
         float scaley = 1f* size.height / region.height;
         float minscale = Math.min(scalex,scaley);

         int height = (int) (region.height * minscale);
         int width = (int) (region.width * minscale);

         int x = size.width/2 - width/2;
         int y = size.height/2 - height/2; 
         g2d.translate(x,y);
         g2d.scale(minscale,minscale);

      }
      
      super.paintChildren(g);



//      if (_stepModel.isSelected()){
//         Rectangle r = getBounds();
//         g2d.setStroke(new BasicStroke(3f));
//         g2d.setColor(Color.red);
//         g2d.drawRect(1,1,r.width - 3,r.height - 3);
//      }

   }

   @Override
   public void propertyChange(PropertyChangeEvent e) {
      if (e.getSource() == _stepModel){

         Rectangle bounds = null;
         if (_stepModel.getModels().size() == 0){

            bounds = _stepModel.getReferenceImageModel().getBounds();

         }else{

            bounds = getForegroundBounds(_stepModel);

         }

         setPreferredSize(bounds.getSize());
      }
   }

   @Override
   public void contentsChanged(Object source) {
      if (source == _stepModel){
         setModel((SklStepModel) source);

//         if (getTopLevelAncestor() != null)
//            getTopLevelAncestor().repaint();
//         // TODO: figure out a way to do local update instaed of updating the entire list view
         //Rectangle r = getBounds();
         //SklStepListView.this.repaint(r.x,r.y,r.width,r.height);
         //SklDocumentListView.this.repaint();
         //validate();
         //repaintImmediately();
      }
   }

}


