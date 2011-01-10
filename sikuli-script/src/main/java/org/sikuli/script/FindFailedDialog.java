package org.sikuli.script;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.sikuli.script.Region.FindFailedResponse;

public class FindFailedDialog extends JDialog implements ActionListener {
   
   private JOptionPane optionPane;

   JButton retryButton;
   JButton skipButton;
   JButton abortButton;
   
   FindFailedResponse _response;
   
   public <PSC>  FindFailedDialog(Frame frame, PSC target){
      super(frame, true);
      
      
      String msgString = "Sikuli is unable to find " + target;   
      JLabel msg = new JLabel(msgString);

      JPanel panel = new JPanel();
      panel.setLayout(new BorderLayout());
      
      panel.add(msg,BorderLayout.NORTH);
      
      JPanel buttons = new JPanel();
      
      retryButton = new JButton("Retry");
      retryButton.addActionListener(this);
      
      skipButton = new JButton("Skip");
      skipButton.addActionListener(this);
      
      abortButton = new JButton("Abort");
      abortButton.addActionListener(this);
      
      buttons.add(retryButton);
      buttons.add(skipButton);
      buttons.add(abortButton);
      
      panel.add(buttons,BorderLayout.SOUTH);
      
      getContentPane().add(panel);
      pack();
      setLocationRelativeTo(null);
   }
   
   public FindFailedResponse getResponse(){
      return _response;
   }
   
   @Override
   public void actionPerformed(ActionEvent e) {
      if (retryButton == e.getSource()){
         _response = FindFailedResponse.RETRY;
      }else if (abortButton == e.getSource()){
         _response = FindFailedResponse.ABORT;
      }else if (skipButton == e.getSource()){
         _response = FindFailedResponse.SKIP;
      }
      dispose();  
   }
   
   
   public static void main(String[] args) {

      FindFailedDialog fd = new FindFailedDialog(null, "Test");
      fd.setVisible(true);
      
      Debug.log("" + fd.getResponse());
   //    Screen screen = new Screen();
   //
}
}
