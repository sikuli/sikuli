/**
 * 
 */
package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
   
   SikuliGuideComponent targetComponent;
   JTextField textField;
   
   public TextPropertyEditor(SikuliGuideComponent comp)  {
       super();
       this.targetComponent = comp;
       
       
       textField = new JTextField(20);
       
       if (comp instanceof SikuliGuideText){          
          String text = ((SikuliGuideText) targetComponent).getText();
          textField.setText(text);
       }
       
       textField.setSize(textField.getPreferredSize());
       setSize(textField.getPreferredSize());       
       add(textField);
       
       // allow the text editor to move as the user moves the edited target
       targetComponent.addFollower(this);
       
       textField.addKeyListener(this);
   }

   @Override
   public void keyPressed(KeyEvent k) {
      Debug.info("Entered");
      if (k.getKeyCode() == KeyEvent.VK_ENTER){ 
         Debug.info("Entered");
         ((SikuliGuideText) targetComponent).setText(textField.getText());
         setVisible(false);     
         
         // make this editor no longer following the edited target
         targetComponent.removeFollower(this);
      }      
      // TODO ESC to cancel
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
      
   
}