package org.sikuli.guide;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;

import org.sikuli.script.Env;



interface ScreenRegionSelectorListener{   
   void selectionChanged(Rectangle region); 
}

public class ScreenRegionSelector extends JFrame{

   
   private List<ScreenRegionSelectorListener> _listeners = new ArrayList<ScreenRegionSelectorListener>();
   
   void addListener(ScreenRegionSelectorListener listener){
      _listeners.add(listener);
   }
   
   void removeListener(ScreenRegionSelectorListener listener){
      _listeners.remove(listener);
   }
      
   private void fireSelectionChanged(Rectangle selection){
      for (ScreenRegionSelectorListener listener : _listeners){
         listener.selectionChanged(selection);
      }
   }

   
   
   DashesWindowView _dashesWindow;
   RightResizeMarker _rightResizeMarker;
   LeftResizeMarker _leftResizeMarker;

   Rectangle _window;
   Robot _robot;
   
   ScreenRegionSelector(){      
      this(new Rectangle(100,100,500,500));
   }
   
   ScreenRegionSelector(Rectangle initWindow){
      setAlwaysOnTop(true);
      setResizable(false);
      setUndecorated(true);
      ((JComponent) getContentPane()).setBorder(BorderFactory.createLineBorder(Color.black));
      
      
      add(new ControlPanel());
      
      try {
         _robot = new Robot();
      } catch (AWTException e1) {
         e1.printStackTrace();
      }
      
      _window = initWindow;      
      _rightResizeMarker = new RightResizeMarker();
      _leftResizeMarker = new LeftResizeMarker();
      
      _dashesWindow = new DashesWindowView();
      _dashesWindow.setAlwaysOnTop(true);
      ComponentDragMover cm = new ComponentDragMover(this);
      cm.registerComponent(this);      
      cm.addMoveListener(new DraggedMoveListener(){

         @Override
         public void componentMoved(Component source, Point origin,
               Point destination) {
            //Debug.info("Frame dragged");
            _window.x = getLocation().x + 25;
            _window.y = getLocation().y + getSize().height;
            updateLocation();
         }
         
      });
      
      
      addComponentListener(new ComponentAdapter(){

         @Override
         public void componentShown(ComponentEvent e){
            _dashesWindow.setVisible(true);
            _rightResizeMarker.setVisible(true);
            _leftResizeMarker.setVisible(true);
         }

         @Override
         public void componentHidden(ComponentEvent e){
            _dashesWindow.setVisible(false);
            _rightResizeMarker.setVisible(false);
            _leftResizeMarker.setVisible(false);
         }
         
      });
      
      updateLocation();
   }
   
   
   void setWindow(Rectangle window){
      _window = window;
      updateLocation();      
   }
   
   
   int controlPanelHeight = 25;
   int markerSize = 25;
   void updateLocation(){     
      setBounds(_window.x - markerSize, _window.y - controlPanelHeight, _window.width + 2*markerSize, controlPanelHeight);
      
      _dashesWindow.setBounds(_window);      
      _rightResizeMarker.setLocation(_window.x + _window.width, _window.y + _window.height);
      _leftResizeMarker.setLocation(_window.x - markerSize, _window.y + _window.height);
      
      fireSelectionChanged(_window.getBounds());
   }
      
   
   abstract class ResizeMarker extends JFrame {
      ResizeMarker(){         
         setUndecorated(true);
         setResizable(false);
         setSize(25,25);
         setAlwaysOnTop(true);
         
         ((JComponent) getContentPane()).setBorder(BorderFactory.createLineBorder(Color.black));
         
         ComponentDragMover cm = new ComponentDragMover(this);
         cm.registerComponent(this);         
         cm.addMoveListener(new DraggedMoveListener(){
            @Override
            public void componentMoved(Component source, Point origin,
                  Point destination) {
               movedByUser();               
            }
         });
      }
      
      protected abstract void movedByUser();
   }
   
   class RightResizeMarker extends ResizeMarker{
      
      @Override
      protected void movedByUser() {
         //Debug.info("Right resizer moved");
         _window.width = _rightResizeMarker.getLocation().x - _window.x;
         _window.height = _rightResizeMarker.getLocation().y - _window.y;
         updateLocation();         
      }
   }
   
   class LeftResizeMarker extends ResizeMarker{
      
      @Override
      protected void movedByUser() {
         //Debug.info("Left resizer moved");
         _window.x = _leftResizeMarker.getLocation().x + 25;
         _window.width = _rightResizeMarker.getLocation().x - _leftResizeMarker.getLocation().x - 25;
         _window.height = _leftResizeMarker.getLocation().y - _window.y;
         updateLocation();
      }
   }
   
   
   
   class DashesWindowView extends JWindow{
      
      DashesWindowView() {
         setBackground(null);
         getContentPane().setBackground(null);
         Env.getOSUtil().setWindowOpaque(this, false);
         setAlwaysOnTop(true);         
      }
      
      int phase = 0;
      public void paint(Graphics g){
         Graphics2D g2d = (Graphics2D) g;
         super.paint(g);
         
         phase += 1;
         if (phase > 12){
            phase = 0;
         }
         
         float [] phases = {6.0F, 6.0F};
         Stroke dashes = new BasicStroke(2f, BasicStroke.CAP_BUTT, 
               BasicStroke.JOIN_MITER, 
               10.0F, phases, phase);

         Stroke solid = new BasicStroke(2f); 
         
         Dimension d = getSize();         
         g2d.setStroke(solid);
         g2d.setColor(Color.white);         
         g2d.drawRect(1,1,d.width-3,d.height-3);

         g2d.setStroke(dashes);
         g2d.setColor(Color.black);
         g2d.drawRect(1,1,d.width-3,d.height-3);
         
         Timer timer = new Timer(100, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
               repaint();
            }
            
         });
         timer.setRepeats(false);
         timer.start();

      }
   }
   
   class ControlPanel extends JPanel{
      
      ControlPanel(){
      }
   }
   
   
   

   public static void main(String[] args){
      ScreenRegionSelector ew = new ScreenRegionSelector();    
      ew.setVisible(true);
   }

}
