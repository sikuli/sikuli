/**
 * 
 */
package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.sikuli.script.Debug;

public class SklTextPropertyEditor extends JComponent implements KeyListener {
   
   BufferedImage image;
   float scale;
   int w,h;
   
   private SklTextModel _textModel;
   JTextField _textField;
   
   public SklTextPropertyEditor()  {
       super();
       
       _textField = new JTextField(20);
       
       _textField.setSize(_textField.getPreferredSize());

       setSize(_textField.getPreferredSize());       
       //setActualSize(_textField.getPreferredSize());
       
       add(_textField);
       
       // allow the text editor to move as the user moves the edited target
       //getTargetComponent().addFollower(this);
       
       _textField.addKeyListener(this);
       setFocusable(true);
       setOpaque(true);
       
       
       addFocusListener(new FocusListener(){

         @Override
         public void focusGained(FocusEvent arg0) {
         }

         @Override
         public void focusLost(FocusEvent arg0) {
            _textModel.setText(_textField.getText());
            setVisible(false);            
         }
          
       });
       
    //   setBounds(new Rectangle(0,0,100,100));
   }

   @Override
   public void keyPressed(KeyEvent k) {
      
      if (k.getKeyCode() == KeyEvent.VK_ENTER){ 
         
         Debug.info("[TextPropertyEditor] User pressed ENTER");
         
         _textModel.setText(_textField.getText());
         
//         if (getTargetComponent() instanceof SikuliGuideText)
//            ((SikuliGuideText) getTargetComponent()).setText(textField.getText());
//         else if (getTargetComponent() instanceof SikuliGuideFlag)
//            ((SikuliGuideFlag) getTargetComponent()).setText(textField.getText());
         
         setVisible(false);     
                  
      }else if (k.getKeyCode() == KeyEvent.VK_ESCAPE){
         
         Debug.info("[TextPropertyEditor] User pressed ESCAPE");
         
         setVisible(false);
         
      }
   }
   

   
   @Override
   public void requestFocus(){
      _textField.requestFocus();
   }

   @Override
   public void keyReleased(KeyEvent arg0) {
   }

   @Override
   public void keyTyped(KeyEvent arg0) {
   }

   public void setTextModel(SklTextModel textModel) {
      _textModel = textModel;      
      _textField.setText(_textModel.getText());

      _textField.setSize(_textField.getPreferredSize());
      setSize(_textField.getPreferredSize());
      
//      Debug.info("" +this);
//      if (targetComponent instanceof SikuliGuideText){
//         setLocationRelativeToComponent(targetComponent, 
//               Layout.TOP);
//         
//         String text = ((SikuliGuideText) targetComponent).getText();
//         textField.setText(text);
//      }
//      else if (targetComponent instanceof SikuliGuideFlag){
//         setLocationRelativeToComponent(targetComponent, 
//               Layout.TOP);
//         
//         String text = ((SikuliGuideFlag) targetComponent).getText();
//         textField.setText(text);         
//      }
//
      

   }

   public SklTextModel getTextModel() {
      return _textModel;
   }
      
   
}