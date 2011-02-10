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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.sikuli.script.TransparentWindow;


public class NavigationDialog extends SikuliGuideDialog implements ActionListener{

   String message;
   
   int response;
   JLabel messageLabel;

   String command = null;
   
   
   public String getActionCommand(){
      return command;
   }
   
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

   
   Button next;
   Button prev;
   Button close;
   Box button_row;
   
   public NavigationDialog(Object owner_, String message_, int style){
      super(owner_);
      
      // these are meant to prevent the message box from stealing
      // focus when it's clicked, but they don't seem to work
//      setFocusableWindowState(false);
//      setFocusable(false);
      

      
      this.owner = owner_;
      this.message = message_;

      setMinimumSize(new Dimension(200,50));

      Container panel = this.getContentPane();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

      String html = "<html><div style='color:white;font-size:15px;padding:3px;'>" + message + "</div></html>";
      messageLabel = new JLabel(html);
      
      Dimension size = messageLabel.getPreferredSize();
      if (size.width > 300){
         // hack to limit the width of the text to 300px
         html = "<html><div style='color:white;font-size:15px;padding:3px;width:300px;'>" + message + "</div></html>";
         messageLabel = new JLabel(html);
      }    

      Box row1 = new Box(BoxLayout.X_AXIS);
      row1.add(messageLabel);

      
      next = new Button("Next");
      next.setActionCommand(NEXT);
      prev = new Button("Previous");
      prev.setActionCommand(PREVIOUS);
      close = new Button("Close");
      close.setActionCommand(CLOSE);
      
      
      button_row = new Box(BoxLayout.X_AXIS); 
      setButtons(button_row, style);
            
      panel.add(row1);
      panel.add(button_row);
      

      next.addActionListener(this);
      prev.addActionListener(this);
      close.addActionListener(this);
   }
   
   public void setStyle(int style){
      button_row.removeAll();
      setButtons(button_row, style);
      pack();
      setLocationRelativeTo((Component) owner);
   }
   
   private void setButtons(Container c, int style){
      if (style == SikuliGuide.FIRST){
         c.add(next);
         c.add(close);
      } else if (style == SikuliGuide.MIDDLE){
         c.add(prev);
         c.add(next);
         c.add(close);    
      }else if (style == SikuliGuide.LAST){
         c.add(prev);
         c.add(close);         
      }else if (style == SikuliGuide.SIMPLE){
         c.add(next);
      }
   }
   
   
   @Override
   public void actionPerformed(ActionEvent e) {
      
      command = e.getActionCommand();
      dismiss();
      
   }
   
}


