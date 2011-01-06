package org.sikuli.guide;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.sikuli.script.TransparentWindow;


class SingleButtonMessageBox extends TransparentWindow {

   String message;
   Object owner;

   JLabel messageLabel;

   class Button extends JButton{

      public Button(String text){
         super(text);
         Font f = new Font("sansserif", Font.BOLD, 16);
         setFont(f);
      }
   }

   public SingleButtonMessageBox(Object owner_, String button_text_, String message_){

      this.owner = owner_;
      this.message = message_;

      setMinimumSize(new Dimension(200,50));

      Container panel = this.getContentPane();
      panel.setBackground(null);
      Color transparentColor = new Color(0F,0F,0F,0.2F);
      setBackground(transparentColor);  

      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

      String html = "<html><div style='color:white;font-size:15px;padding:3px;'>" + message + "</div></html>";
      messageLabel = new JLabel(html);

      Box row1 = new Box(BoxLayout.X_AXIS);
      row1.add(messageLabel);

      JButton next = new Button(button_text_);
      Box row2 = new Box(BoxLayout.X_AXIS);
      row2.add(next);

      panel.add(row1);
      panel.add(row2);

      next.addActionListener(new ActionListener(){

         @Override
         public void actionPerformed(ActionEvent e) {
            setVisible(false);
            dispose();
            synchronized(owner){
               owner.notify();
            }
         }

      });
   }
}