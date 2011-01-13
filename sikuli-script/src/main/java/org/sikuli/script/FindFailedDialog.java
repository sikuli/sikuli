/**
 * 
 */
package org.sikuli.script;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;



//TODO:
//- (done) close --> abort
//- (done) keep the dialog box always on top
//- (done) display target image
//- display oversized target images at a proper scale
//- beautify the layout
//- (done) disable resizing
//- ensure the dialog disappears before find is reattempted so that it won't find the
//target image in the dialog box. 

class FindFailedDialog extends JDialog implements ActionListener {

   /**
    * 
    */

   JButton retryButton;
   JButton skipButton;
   JButton abortButton;

   FindFailedResponse _response;

   public <PSC> FindFailedDialog(PSC  target){
      setModal(true);
      //super(new Frame(),true);

      JPanel panel = new JPanel();
      panel.setLayout(new BorderLayout());

      Component targetComp = createTargetComponent(target);

      panel.add(targetComp,BorderLayout.NORTH);

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

      add(panel);
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);


      addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent e){
            _response = FindFailedResponse.ABORT;
            //dispose();
         }
      });

      //pack(); // don't pack, doing so messes up AlwaysOnTop
      //setLocationRelativeTo(null);

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

   public FindFailedResponse getResponse(){
      return _response;
   }


   <PSC> Component createTargetComponent(PSC target){

      if( target instanceof Pattern ){
         Pattern p = (Pattern) target;
         JLabel c = new JLabel("Sikuli can not find pattern :" + p);
         return c;
      }
      else if( target instanceof String){

         String s = (String) target;
         try{
            String filename = (new ImageLocator()).locate((String)target);

            Image image = null;
            try {
               image = ImageIO.read(new File(filename));
            } catch (IOException e) {
            }

            JPanel p = new JPanel();
            p.setLayout(new BorderLayout());
            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(new ImageIcon(image));

            JLabel c = new JLabel("Sikuli is unable to find the target image.");

            p.add(c,BorderLayout.PAGE_START);
            p.add(new JLabel((String)target));
            p.add(iconLabel,BorderLayout.PAGE_END);
            return p;

         }
         catch(IOException e){
            JLabel c = new JLabel("Sikuli can not find text :" + s);
            return c;

         }
      }

      return null;
   }

   @Override
   public void setVisible(boolean flag){

      if (flag){
         // These can not be called in the constructor.
         // Doing so somehow made it impossible to keep
         // the dialog always on top.
         toFront();
         setAlwaysOnTop(true);
         pack();
         setResizable(false);
         setLocationRelativeTo(this);
      }

      super.setVisible(flag);
   }

   public static void main(String[] args) {

      //    JFrame f = new JFrame();
      //FindFailedDialog fd = new FindFailedDialog("Test");
      FindFailedDialog fd = new FindFailedDialog("Test");
      fd.setVisible(true);
      //f.setVisible(true);
      //fd.setAlwaysOnTop(true);
      //fd.waitResponse();

      //    synchronized(f){
      //       try {
      //          f.wait();
      //       } catch (InterruptedException e) {
      //          e.printStackTrace();
      //       }
      //    }


      Debug.log("" + fd.getResponse());
   }
}
