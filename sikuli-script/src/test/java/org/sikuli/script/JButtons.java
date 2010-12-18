package org.sikuli.script;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/** Simple example illustrating the use of JButton, especially
 *  the new constructors that permit you to add an image.
 *  1998-99 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 */

public class JButtons extends JFrame {
   int[] count = {0,0,0};

   public static void main(String[] args) {
      new JButtons();
   }

   public int[] getCount(){
      return count;
   }

   public JButtons() {
    super("Using JButton");
    Container content = getContentPane();
    content.setBackground(Color.white);
    content.setLayout(new FlowLayout());
    JButton button1 = new JButton("Java");
    button1.addActionListener(new ActionListener(){
       public void actionPerformed(ActionEvent ae2){
          count[0]++;
       }
    });
    content.add(button1);

    ImageIcon cup = new ImageIcon("test-res/cup.gif");
    JButton button2 = new JButton(cup);
    button2.addActionListener(new ActionListener(){
       public void actionPerformed(ActionEvent ae2){
          count[1]++;
       }
    });
    content.add(button2);

    JButton button3 = new JButton("Java", cup);
    button3.addActionListener(new ActionListener(){
       public void actionPerformed(ActionEvent ae2){
          count[2]++;
          System.out.println("click on button3 " + count[2]);
       }
    });

    content.add(button3);
    pack();
    setVisible(true);
  }
}
