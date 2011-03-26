package org.sikuli.guide;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.sikuli.script.TransparentWindow;


public class SingleButtonMessageBox extends BaseDialog {

   String message;

   JLabel messageLabel;

   class Button extends JButton{

      public Button(String text){
         super(text);
         Font f = new Font("sansserif", Font.BOLD, 16);
//         setBackground(new Color(0.2F,0.2f,0.2f));
//         setForeground(Color.white);
         setFont(f);
         setFocusable(false);
      }
   }
   

   public SingleButtonMessageBox(Object owner_, String button_text_, String message_){
      super(owner_);
      
      // these are meant to prevent the message box from stealing
      // focus when it's clicked, but they don't seem to work
//      setFocusableWindowState(false);
//      setFocusable(false);
      

      setMinimumSize(new Dimension(200,50));

      Container panel = this.getContentPane();

      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

      String html = "<html><div style='color:white;font-size:15px;padding:5px;width:300px;'>" + message + "</div></html>";
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
            dismiss();
         }

      });
   }
   

   
}




