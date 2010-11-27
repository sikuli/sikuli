package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;


public class PatternWindow extends JFrame implements Observer {

   private ImageButton _imgBtn;
   private ScreenshotPane _screenshot;
   private TargetOffsetPane _tarOffsetPane;
   private NamingPane _namingPane;

   private JTabbedPane tabPane;
   private JPanel paneTarget, panePreview;

   private JPanel glass;
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
      //tabPane.setPreferredSize(new Dimension(500,300));
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
            _simg, _imgBtn.getImageFilename(), _imgBtn.getTargetOffset());
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
         _screenshot.setParameters( _imgBtn.getImageFilename(),
                                   exact, similarity, numMatches);
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }

   private void createScreenshots(Container c){
      _screenshot = new ScreenshotPane(_simg);
      _screenshot.addObserver(this);
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

      ImageIcon loadingIcon = new ImageIcon(
            SikuliIDE.class.getResource("/icons/loading.gif"));
      JLabel lblLoading = new JLabel(loadingIcon);

      glass = (JPanel)getGlassPane();
      glass.setLayout(new BorderLayout());
      glass.add(lblLoading, BorderLayout.CENTER);
      glass.setVisible(true);

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


   public void update(Subject s){
      glass.setVisible(false);
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
         _imgBtn.setParameters(
               _screenshot.isExact(), _screenshot.getSimilarity(),
               _screenshot.getNumMatches());
         _imgBtn.setTargetOffset( _tarOffsetPane.getTargetOffset() );
         if(_namingPane.isDirty()){
            String filename = _namingPane.getAbsolutePath();
            String oldFilename = _imgBtn.getFilename();
            Debug.log("rename " + oldFilename + " " + filename);
            Utils.rename(oldFilename, filename);
            _imgBtn.setFilename(filename);
         }
         Debug.info("update :" + _imgBtn.toString());
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
