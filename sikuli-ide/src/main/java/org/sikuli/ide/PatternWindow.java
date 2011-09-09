/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;

import org.sikuli.script.Location;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.Debug;
import org.sikuli.script.Region;
import org.sikuli.script.UnionScreen;


public class PatternWindow extends JFrame {

   private ImageButton _imgBtn;
   private ScreenshotPane _screenshot;
   private TargetOffsetPane _tarOffsetPane;
   private NamingPane _namingPane;

   private JTabbedPane tabPane;
   private JPanel paneTarget, panePreview;

   private ScreenImage _simg;

   static String _I(String key, Object... args){ 
      return I18N._I(key, args);
   }

   public PatternWindow(ImageButton imgBtn, boolean exact, 
                        float similarity, int numMatches){
      super(_I("winPatternSettings"));
      _imgBtn = imgBtn;
      //setBackground(new java.awt.Color(255,255,255,128)); 
      Point pos = imgBtn.getLocationOnScreen();
      Debug.log(4, "pattern window: " + pos );
      setLocation(pos.x, pos.y);

      takeScreenshot();
      Container c = getContentPane();
      c.setLayout(new BorderLayout());

      tabPane = new JTabbedPane();
      tabPane.setPreferredSize(new Dimension(790,700));
      paneTarget = createTargetPanel();
      panePreview = createPrewviewPanel();
      _namingPane = new NamingPane(_imgBtn);
      tabPane.addTab(_I("tabNaming"), _namingPane);
      tabPane.addTab(_I("tabMatchingPreview"), panePreview);
      tabPane.addTab(_I("tabTargetOffset"), paneTarget);
      c.add(tabPane, BorderLayout.CENTER);
      c.add(createButtons(), BorderLayout.SOUTH);

      c.doLayout();
      pack();

      init(exact, similarity, numMatches);

      setVisible(true);
   }

   void takeScreenshot(){
      SikuliIDE ide = SikuliIDE.getInstance();
      ide.setVisible(false);
      try{
         Thread.sleep(500);
      }
      catch(Exception e){}
      Region match_region = new UnionScreen();
      _simg = match_region.getScreen().capture();
      ide.setVisible(true);
   }

   private JPanel createTargetPanel(){
      JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

      _tarOffsetPane = new TargetOffsetPane(
            _simg, _imgBtn.getFilename(), _imgBtn.getTargetOffset());
      //p.addObserver(this);
      createMarginBox(p, _tarOffsetPane);
      p.add(Box.createVerticalStrut(5));
      p.add(_tarOffsetPane.createControls());
      p.doLayout();
      return p;
   }

   private JPanel createPrewviewPanel(){
      JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

      createScreenshots(p);
      p.add(Box.createVerticalStrut(5));
      p.add(_screenshot.createControls());
      p.doLayout();
      return p;
   }

   private void init(boolean exact, float similarity, int numMatches){
      try{
         _screenshot.setParameters( _imgBtn.getFilename(),
                                   exact, similarity, numMatches);
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }

   private void createScreenshots(Container c){
      _screenshot = new ScreenshotPane(_simg);
      //_screenshot.addObserver(this);
      createMarginBox(c, _screenshot);
   }


   private void createMarginBox(Container c, Component comp){
      c.add(Box.createVerticalStrut(10));
      Box lrMargins = Box.createHorizontalBox();
      lrMargins.add(Box.createHorizontalStrut(10));
      lrMargins.add(comp);
      lrMargins.add(Box.createHorizontalStrut(10));
      c.add(lrMargins);
      c.add(Box.createVerticalStrut(10));
   }


   private JComponent createButtons(){
      JPanel pane = new JPanel(new GridBagLayout());

      JButton btnOK = new JButton(_I("ok"));
      btnOK.addActionListener(new ActionOK(this));
      JButton btnCancel = new JButton(_I("cancel"));
      btnCancel.addActionListener(new ActionCancel(this));

      GridBagConstraints c = new GridBagConstraints();

      c.gridy = 3;
      c.gridx = 1;
      c.insets = new Insets(5,0,10,0);
      c.anchor = GridBagConstraints.LAST_LINE_END;
      pane.add(btnOK, c);
      c.gridx = 2;
      pane.add(btnCancel, c);

      return pane;
   }


   public void setTargetOffset(Location offset){
      if(offset != null)
         _tarOffsetPane.setTarget(offset.x, offset.y);
   }

   class ActionOK implements ActionListener {
      private Window _parent;
      public ActionOK(Window parent){
         _parent = parent;
      }

      public void actionPerformed(ActionEvent e) {
         if(_namingPane.isDirty()){
            String filename = _namingPane.getAbsolutePath();
            String oldFilename = _imgBtn.getFilename();
            if(Utils.exists(filename)){
               String name = Utils.getName(filename);
               int ret = JOptionPane.showConfirmDialog(
                     _parent,
                     I18N._I("msgFileExists", name),
                     I18N._I("dlgFileExists"),
                     JOptionPane.WARNING_MESSAGE,
                     JOptionPane.YES_NO_OPTION);
               if(ret != JOptionPane.YES_OPTION) 
                  return;
            }
            try{
               Utils.xcopy(oldFilename, filename);
               _imgBtn.setFilename(filename);
            }
            catch(IOException ioe){
               Debug.error("renaming failed: " + oldFilename + " " + filename);
               Debug.error(ioe.getMessage());
            }
         }
         _imgBtn.setParameters(
               _screenshot.isExact(), _screenshot.getSimilarity(),
               _screenshot.getNumMatches());
         _imgBtn.setTargetOffset( _tarOffsetPane.getTargetOffset() );
         Debug.log("update: " + _imgBtn.toString());
         _parent.dispose();
      }
   }

   class ActionCancel implements ActionListener {
      private Window _parent;
      public ActionCancel(Window parent){
         _parent = parent;
      }
      public void actionPerformed(ActionEvent e) {
         _parent.dispose();
      }
   }

}
