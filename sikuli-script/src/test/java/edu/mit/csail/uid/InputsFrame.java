package edu.mit.csail.uid;

import java.awt.*;
import javax.swing.*;

/** Panel to hold input textfields. **/
public class InputsFrame extends JFrame
{

   public static void main(String[] args) {
      new InputsFrame();
   }

   JTextField fTextfieldTop;
   JTextField fTextfieldBot;

   InputsFrame(){
      this("Input x ","1.5", "Input y ","");
      pack();
      setVisible(true);
   }

   public String getText(int i){
      if(i==0) return fTextfieldTop.getText();
      return fTextfieldBot.getText();
   }

   /** Constructor builds panel with labels and text fields. **/
   InputsFrame (String label_str_top, String init_top,
                String label_str_bot, String init_bot) {

     Container c = getContentPane();

     // Set the layout with 2 rows by 2 columns
     c.setLayout (new GridLayout (2,2));

     // Create two text fields with the initial values
     fTextfieldTop = new JTextField (init_top);
     fTextfieldBot = new JTextField (init_bot);

     // Create the first label and right justify the text
     JLabel label_top =
       new JLabel (label_str_top, SwingConstants.RIGHT);

     // Insert the label and textfield into the top grid row
     c.add (label_top);
     c.add (fTextfieldTop);

     // Create the second label and right justify the text
     JLabel label_bot =
       new JLabel (label_str_bot, SwingConstants.RIGHT);

     // Insert the second label and textfield into the bottom grid row
     c.add (label_bot);
     c.add (fTextfieldBot);
   } // ctor

} 
