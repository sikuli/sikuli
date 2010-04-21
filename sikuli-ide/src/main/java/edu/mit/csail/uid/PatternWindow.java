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

   private JTabbedPane tabPane;
   private JPanel paneTarget, panePreview;
   private JLabel btnSimilar;
   private JSlider sldSimilar;
   private JSpinner txtNumMatches;

   private JPanel glass;

   public PatternWindow(ImageButton imgBtn, boolean exact, 
                        float similarity, int numMatches){
      super("Pattern Settings");
      _imgBtn = imgBtn;
      //setBackground(new java.awt.Color(255,255,255,128)); 
      Point pos = imgBtn.getLocationOnScreen();
      Debug.log( "pattern window: " + pos );
      setLocation(pos.x, pos.y);

      Container c = getContentPane();
      tabPane = new JTabbedPane();
      paneTarget = createTargetPanel();
      panePreview = createPrewviewPanel();
      tabPane.addTab("Matching Preview", panePreview);
      tabPane.addTab("Target Offset", paneTarget);
      c.add(tabPane);

      pack();

      init(exact, similarity, numMatches);

      setVisible(true);
   }

   private JPanel createTargetPanel(){
      return new TargetOffsetPanel(_imgBtn.getImageFilename());
   }

   private JPanel createPrewviewPanel(){
      JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

      createScreenshots(p);
      createButtons(p);
      p.add(Box.createVerticalStrut(5));
      p.doLayout();
      return p;
   }

   private void init(boolean exact, float similarity, int numMatches){
      sldSimilar.setValue((int)(similarity * 100));
      txtNumMatches.setValue(numMatches);
      txtNumMatches.addChangeListener(_screenshot);
      try{
         _screenshot.setParameters( _imgBtn.getImageFilename(),
                                   exact, similarity, numMatches);
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }

   private void createScreenshots(Container c){
      _screenshot = new ScreenshotPane();
      _screenshot.addObserver(this);
      c.add(Box.createVerticalStrut(10));
      Box lrMargins = Box.createHorizontalBox();
      lrMargins.add(Box.createHorizontalStrut(10));
      lrMargins.add(_screenshot);
      lrMargins.add(Box.createHorizontalStrut(10));
      c.add(lrMargins);
      c.add(Box.createVerticalStrut(10));
   }

   private JSlider createSlider(){
      sldSimilar = new JSlider(0, 100, 70);

      sldSimilar.setMajorTickSpacing(10);
      sldSimilar.setPaintTicks(true);

      Hashtable labelTable = new Hashtable();
      labelTable.put( new Integer( 0 ), new JLabel("0.0") );
      labelTable.put( new Integer( 50 ), new JLabel("0.5") );
      labelTable.put( new Integer( 100 ), new JLabel("1.0") );
      sldSimilar.setLabelTable( labelTable );
      sldSimilar.setPaintLabels(true);

      sldSimilar.addChangeListener(_screenshot);

      return sldSimilar;

   }
   private void createButtons(Container parent){
      JPanel pane = new JPanel(new GridBagLayout());
      btnSimilar = new JLabel("Similarity:");

      sldSimilar = createSlider();
      JLabel lblNumMatches = new JLabel("Number of matches:");
      SpinnerNumberModel model = new SpinnerNumberModel(50, 0, ScreenshotPane.MAX_NUM_MATCHING, 1); 
      txtNumMatches = new JSpinner(model);
      lblNumMatches.setLabelFor(txtNumMatches);
      JButton btnOK = new JButton("OK");
      btnOK.addActionListener(new ActionOK(this));
      JButton btnCancel = new JButton("Cancel");
      btnCancel.addActionListener(new ActionCancel(this));

      ImageIcon loadingIcon = new ImageIcon(
            SikuliIDE.class.getResource("/icons/loading.gif"));
      JLabel lblLoading = new JLabel(loadingIcon);

      glass = (JPanel)getGlassPane();
      glass.setLayout(new BorderLayout());
      glass.add(lblLoading, BorderLayout.CENTER);
      glass.setVisible(true);


      GridBagConstraints c = new GridBagConstraints();

      c.fill = 1;
      c.gridy = 0;
      pane.add( btnSimilar, c );
      pane.add( sldSimilar, c );

      c.fill = 0;
      c.gridy = 1;
      pane.add( lblNumMatches, c );
      c.insets = new Insets(0, 0, 0, 100);
      pane.add( txtNumMatches, c );

      c.gridy = 3;
      c.gridx = 1;
      c.insets = new Insets(0,0,0,0);
      c.anchor = GridBagConstraints.LAST_LINE_END;
      pane.add(btnOK, c);
      c.gridx = 2;
      pane.add(btnCancel, c);

      parent.add(pane);

   }

   private JButton createButton(String label, ActionListener listener){
      JButton btn = new JButton(label);
      btn.setAlignmentX(Component.CENTER_ALIGNMENT);
      btn.addActionListener(listener);
      return btn;
   }

   public void update(Subject s){
      glass.setVisible(false);
   }

   class ActionOK implements ActionListener {
      private Window _parent;
      public ActionOK(Window parent){
         _parent = parent;
      }

      public void actionPerformed(ActionEvent e) {
         float similarity = (float)sldSimilar.getValue()/100;
         boolean exact = (similarity == 1.0f);
         int numMatches = (Integer)txtNumMatches.getValue();
         _imgBtn.setParameters(exact, similarity, numMatches);
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


class TargetOffsetPanel extends JPanel {
   final static int MAX_H = 300;
   BufferedImage _img;
   int _w, _h;

   public TargetOffsetPanel(String patFilename){
      try {
         _img = ImageIO.read(new File(patFilename));
         _w = _img.getWidth();
         _h = _img.getHeight();
         if(_h>MAX_H){
            _w = (int)(_w * (float)MAX_H/_h);
            _h = MAX_H;
         }
      } catch (IOException e) {
         Debug.error("Can't load " + patFilename);
      }
      setPreferredSize(new Dimension(_w, _h));
   }

   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D)g;
      if( _img != null ){
         g2d.drawImage(_img, 0, 0, _w, _h, null);
      }
   }
}

/*
class TargetOffsetPanel extends ScreenshotPane {
   double DEFAULT_MARGIN = 0.3;
}
*/

class ScreenshotPane extends JPanel implements ChangeListener, ComponentListener, Subject{
   final static int DEFAULT_H = 300;
   static int MAX_NUM_MATCHING = 100;

   Region _match_region;
   ScreenImage _simg;
   BufferedImage _screen = null;
   int _width, _height;
   double _scale, _ratio;

   boolean _runFind = false; 

   float _similarity;
   int _numMatches;
   Set<Match> _fullMatches = null;
   Vector<Match> _showMatches = null;
   Observer _observer = null;

   public ScreenshotPane(){
      _match_region = new UnionScreen();
      int w = _match_region.w, h = _match_region.h;
      _ratio = (double)w/h;
      _height = DEFAULT_H;
      _scale = (double)_height/h;
      _width = (int)(w * _scale);
      setPreferredSize(new Dimension(_width, _height));
      addComponentListener(this);
      takeScreenshot();
   }

   public void componentHidden(ComponentEvent e) { } 
   public void componentMoved(ComponentEvent e) { }
   public void componentShown(ComponentEvent e) { }

   public void componentResized(ComponentEvent e) {
      _width = getWidth();
      _height = (int)((double)_width/_ratio);
      _scale = (double)_height/_match_region.h;
      setPreferredSize(new Dimension(_width, _height));
   }

   public void setParameters(boolean exact, float similarity, int numMatches){
      if(!exact)
         _similarity = similarity;
      else
         _similarity = 1.0f;
      _numMatches = numMatches;
      filterMatches(_similarity, _numMatches);
      repaint();
   }

   public void setSimilarity(float similarity){
      _similarity = similarity;
      filterMatches(_similarity, _numMatches);
      repaint();
   }

   public void setNumMatches(int numMatches){
      _numMatches = numMatches;
      filterMatches(_similarity, _numMatches);
      repaint();
   }

   public void addObserver( Observer ob ){
      _observer = ob;
   }

   public void notifyObserver(){
      if(_observer != null)
         _observer.update(this);
   }

   public void setParameters(final String patFilename,
                             final boolean exact, final float similarity, 
                             final int numMatches)
                                             throws IOException, AWTException{
      if( !_runFind ){
         _runFind = true;
         Thread thread = new Thread(new Runnable(){
            public void run(){
               try{
                  Finder f = new Finder(_simg, _match_region);
                  f.find(new Pattern(patFilename).similar(0f));
                  _fullMatches = new TreeSet<Match>(new Comparator(){
                     public int compare(Object o1, Object o2){
                        return -1 * ((Comparable)o1).compareTo(o2);
                     }
                     public boolean equals(Object o){
                        return false;
                     }
                  });
                  int count=0;
                  while(f.hasNext()){
                     if(++count > MAX_NUM_MATCHING)
                        break;
                     Match m = f.next();
                     synchronized(_fullMatches){
                        _fullMatches.add(m);
                     }
                     setParameters(exact, similarity, numMatches);
                     notifyObserver();
                  }
               }
               catch(Exception e){
                  e.printStackTrace();
               }
            }
         });
         thread.start();
      }
      else
         setParameters(exact, similarity, numMatches);
   }

   void filterMatches(float similarity, int numMatches){
      int count = 0;
      if(_fullMatches != null && numMatches>=0){
         Debug.log(7, "filterMatches(%.2f,%d): %d", 
                   similarity, numMatches, count);
         if(_showMatches == null)
            _showMatches = new Vector<Match>();
         synchronized(_showMatches){
            _showMatches.clear();
            if(numMatches == 0) return;
            synchronized(_fullMatches){
               for(Match m : _fullMatches){
                  if( m.score >= similarity ){
                     _showMatches.add(m);
                     if( ++count >= numMatches )
                        break;
                  }
               }
            }
         }
      }
      return;
   }

   void takeScreenshot(){
      SikuliIDE ide = SikuliIDE.getInstance();
      ide.setVisible(false);
      try{
         Thread.sleep(500);
      }
      catch(Exception e){}
      _simg = _match_region.getScreen().capture();
      _screen = _simg.getImage();
      ide.setVisible(true);
   }

   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D)g;
      if( _screen != null ){
         g2d.drawImage(_screen, 0, 0, _width, _height, null);
         if( _showMatches != null )
            paintMatches(g2d);
         else
            paintOverlay(g2d);
      }
   }

   void paintOverlay(Graphics2D g2d){
      g2d.setColor(new Color(0,0,0,150));
      g2d.fillRect(0, 0, _width, _height);
   }

   void paintMatches(Graphics2D g2d){
      synchronized(_showMatches){
         for(Match m : _showMatches){
            int x = (int)(m.x*_scale);
            int y = (int)(m.y*_scale);
            int w = (int)(m.w*_scale);
            int h = (int)(m.h*_scale);
            // map hue to 0.5~1.0
            Color c = new Color(
                  Color.HSBtoRGB( 0.5f+(float)m.score/2, 1.0f, 1.0f));
            // map alpha to 20~150
            Color cMask = new Color(
                  c.getRed(), c.getGreen(), c.getBlue(), 20+(int)(m.score*130));
            g2d.setColor(cMask);
            g2d.fillRect(x, y, w, h);
            g2d.drawRect(x, y, w-1, h-1);
         }
      }
   
   }

   public void stateChanged(javax.swing.event.ChangeEvent e) {
      Object src = e.getSource();
      if( src instanceof JSlider){
         JSlider source = (JSlider)e.getSource();
         int val = (int)source.getValue();
         setSimilarity((float)val/100);
      }
      else if( src instanceof JSpinner){
         JSpinner source = (JSpinner)e.getSource();
         int val = (Integer)source.getValue();
         setNumMatches(val);
      }
   }

}
