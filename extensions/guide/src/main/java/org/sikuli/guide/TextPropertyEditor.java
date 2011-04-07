/**
 * 
 */
package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JTextField;

import org.sikuli.script.Debug;

public class TextPropertyEditor extends SikuliGuideComponent implements KeyListener {
   
   BufferedImage image;
   float scale;
   int w,h;
   
   private SikuliGuideComponent targetComponent;
   JTextField textField;
   
   
   
   public TextPropertyEditor()  {
       super();
       
       textField = new JTextField(20);
       
       textField.setSize(textField.getPreferredSize());
       setSize(textField.getPreferredSize());       
       add(textField);
       
       // allow the text editor to move as the user moves the edited target
       //getTargetComponent().addFollower(this);
       
       textField.addKeyListener(this);
       setFocusable(true);
       setOpaque(true);
    //   setBounds(new Rectangle(0,0,100,100));
   }

   @Override
   public void keyPressed(KeyEvent k) {
      
      if (k.getKeyCode() == KeyEvent.VK_ENTER){ 
         
         Debug.info("[TextPropertyEditor] User pressed ENTER");
         
         ((SikuliGuideText) getTargetComponent()).setText(textField.getText());
         setVisible(false);     
                  
      }else if (k.getKeyCode() == KeyEvent.VK_ESCAPE){
         
         Debug.info("[TextPropertyEditor] User pressed ESCAPE");
         
         setVisible(false);
         
      }
   }
   

   
   @Override
   public void requestFocus(){
      textField.requestFocus();
   }

   @Override
   public void keyReleased(KeyEvent arg0) {
   }

   @Override
   public void keyTyped(KeyEvent arg0) {
   }

   public void setTargetComponent(SikuliGuideComponent targetComponent) {
      this.targetComponent = targetComponent;
      
      Debug.info("" +this);
      if (targetComponent instanceof SikuliGuideText){
         setLocationRelativeToComponent(targetComponent, 
               SikuliGuideComponent.TOP);
         
         String text = ((SikuliGuideText) targetComponent).getText();
         textField.setText(text);
      }

   }

   public SikuliGuideComponent getTargetComponent() {
      return targetComponent;
   }
      
   
}