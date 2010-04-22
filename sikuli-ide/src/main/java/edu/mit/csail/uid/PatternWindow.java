package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;
import javax.swing.plaf.basic.BasicSliderUI;

class SimilaritySlider extends JSlider implements MouseMotionListener, MouseListener {
   final JPopupMenu pop = new JPopupMenu();
   JMenuItem item = new JMenuItem();

   public SimilaritySlider(int min, int max, int val){
      super(min,max,val);
      addMouseMotionListener( this );
      addMouseListener( this );
      pop.add( item );

      pop.setDoubleBuffered( true );

   }

   protected void paintComponent(Graphics g){
      int w = getWidth();
      final int margin = 13;
      final int y1 = 20, y2 = 30;
      for(int i=margin;i<w-margin;i++){
         float score = (float)i/(w-margin*2);
         g.setColor(getScoreColor(score));
         g.drawLine(i, y1, i, y2);
      }
      super.paintComponent(g);
   }

   static Color getScoreColor(double score){
      // map hue to 0.5~1.0
      Color c = new Color(
            Color.HSBtoRGB( 0.5f+(float)score/2, 1.0f, 1.0f));
      // map alpha to 20~150
      Color cMask = new Color(
            c.getRed(), c.getGreen(), c.getBlue(), 20+(int)(score*130));
      return cMask;
   }

   public void showToolTip ( MouseEvent me )
   {      
      String txt = String.format("%.2f", (float)getValue()/100);
      item.setText(txt);

      //limit the tooltip location relative to the slider
      Rectangle b = me.getComponent().getBounds();
      int x = me.getX();      
      x = (x > (b.x) ?  (b.x) : 
            (x < (b.x -b.width) ? (b.x -b.width) : x));

      pop.show( me.getComponent(), x - 5, -30 );

      item.setArmed( false );
      item.setSelected(false);
   }

   public void mouseDragged ( MouseEvent me )
   {
      showToolTip( me );
   }

   public void mouseMoved ( MouseEvent me ) { }

   public void mousePressed ( MouseEvent me ) {
      showToolTip( me );
   }

   public void mouseClicked ( MouseEvent me ) { }

   public void mouseReleased ( MouseEvent me ) {
      pop.setVisible( false );
   }

   public void mouseEntered ( MouseEvent me ) { }

   public void mouseExited ( MouseEvent me ) { }
}

public class PatternWindow extends JFrame implements Observer {

   private ImageButton _imgBtn;
   private ScreenshotPane _screenshot;

   private JTabbedPane tabPane;
   private JPanel paneTarget, panePreview;

   private JPanel glass;
   private ScreenImage _simg;

   public PatternWindow(ImageButton imgBtn, boolean exact, 
                        float similarity, int numMatches){
      super("Pattern Settings");
      _imgBtn = imgBtn;
      //setBackground(new java.awt.Color(255,255,255,128)); 
      Point pos = imgBtn.getLocationOnScreen();
      Debug.log( "pattern window: " + pos );
      setLocation(pos.x, pos.y);

      takeScreenshot();
      Container c = getContentPane();
      c.setLayout(new BorderLayout());

      tabPane = new JTabbedPane();
      //tabPane.setPreferredSize(new Dimension(500,300));
      paneTarget = createTargetPanel();
      panePreview = createPrewviewPanel();
      tabPane.addTab("Matching Preview", panePreview);
      tabPane.addTab("Target Offset", paneTarget);
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

      TargetOffsetPanel tarP = 
         new TargetOffsetPanel(_simg, _imgBtn.getImageFilename());
      //p.addObserver(this);
      createMarginBox(p, tarP);
      p.add(Box.createVerticalStrut(5));
      //p.add(tarP.createControls());
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

   class ActionOK implements ActionListener {
      private Window _parent;
      public ActionOK(Window parent){
         _parent = parent;
      }

      public void actionPerformed(ActionEvent e) {
         /*
         float similarity = (float)sldSimilar.getValue()/100;
         boolean exact = (similarity == 1.0f);
         int numMatches = (Integer)txtNumMatches.getValue();
         */
         _imgBtn.setParameters(
               _screenshot.isExact(), _screenshot.getSimilarity(),
               _screenshot.getNumMatches());
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


class TargetOffsetPanel extends JPanel implements MouseListener{
   final static int DEFAULT_H = 300;
   final static float DEFAULT_PATTERN_RATIO=0.4f;
   ScreenImage _simg;
   BufferedImage _img;
   int _w, _h;
   Match _match;

   int _viewX, _viewY, _viewW, _viewH;
   float _zoomRatio, _ratio;
   Location _tar = new Location(0,0);

   public TargetOffsetPanel(ScreenImage simg, String patFilename){
      _simg = simg;
      Finder f = new Finder(_simg, new Region(simg.getROI()));
      f.find(patFilename);
      if(f.hasNext()){
         _match = f.next();
         setTarget(0,0);
      }
      else{
         try {
            _img = ImageIO.read(new File(patFilename));
         } catch (IOException e) {
            Debug.error("Can't load " + patFilename);
         }
      }
      _ratio = DEFAULT_PATTERN_RATIO;
      Rectangle r = _simg.getROI();
      int w = DEFAULT_H/r.height*r.width;
      setPreferredSize(new Dimension(w, DEFAULT_H));
      addMouseListener(this);
   }

   private void zoomToMatch(){
      Rectangle scr = _simg.getROI();
      _viewW = (int)(_match.w/_ratio);
      _zoomRatio = getWidth()/(float)_viewW;
      _viewH = (int)(getHeight()/_zoomRatio);
      _viewX = _match.x + _match.w/2 - _viewW/2;
      _viewY = _match.y + _match.h/2 - _viewH/2;
   }

   public void setTarget(int dx, int dy){
      Debug.log("new target: " + dx + "," + dy);
      Location center = _match.getCenter();
      _tar.x = center.x + dx;
      _tar.y = center.y + dy;
      repaint();
   }

   public void mousePressed ( MouseEvent me ) {
      Location tar = convertViewToScreen(me.getPoint());
      Debug.log(4, "click: " + me.getPoint() + " -> " + tar);
      Location center = _match.getCenter();
      setTarget(tar.x-center.x, tar.y-center.y);
   }

   public void mouseClicked ( MouseEvent me ) { }

   public void mouseReleased ( MouseEvent me ) {
   }

   public void mouseEntered ( MouseEvent me ) { }

   public void mouseExited ( MouseEvent me ) { }


   public void paint(Graphics g){
      Graphics2D g2d = (Graphics2D)g;
      if( getWidth() > 0 && getHeight() > 0){
         zoomToMatch();
         BufferedImage clip = 
            _simg.getImage().getSubimage(_viewX, _viewY, _viewW, _viewH);
         g2d.drawImage(clip, 0, 0, getWidth(), getHeight(), null);
         paintMatch(g2d);
         paintTarget(g2d);
      }
   }

   Location convertViewToScreen(Point p){
      Location ret = new Location(0,0);
      ret.x = (int)(p.x/_zoomRatio+_viewX);
      ret.y = (int)(p.y/_zoomRatio+_viewY);
      return ret;
   }

   Point convertScreenToView(Location loc){
      Point ret = new Point();
      ret.x = (int)((loc.x - _viewX) * _zoomRatio);
      ret.y = (int)((loc.y - _viewY) * _zoomRatio);
      return ret;
   }

   void paintTarget(Graphics2D g2d){
      final int CROSS_LEN=20/2;
      Point l = convertScreenToView(_tar);
      g2d.setColor(Color.BLACK);
      g2d.drawLine(l.x-CROSS_LEN, l.y+1, l.x+CROSS_LEN, l.y+1);
      g2d.drawLine(l.x+1, l.y-CROSS_LEN, l.x+1, l.y+CROSS_LEN);
      g2d.setColor(Color.WHITE);
      g2d.drawLine(l.x-CROSS_LEN, l.y, l.x+CROSS_LEN, l.y);
      g2d.drawLine(l.x, l.y-CROSS_LEN, l.x, l.y+CROSS_LEN);
   }

   void paintMatch(Graphics2D g2d){
      int w = (int)(getWidth() * _ratio), 
          h = (int)((float)w/_img.getWidth()*_img.getHeight());
      int x = getWidth()/2- w/2, y = getHeight()/2-h/2;
      Color c = SimilaritySlider.getScoreColor(_match.score);
      g2d.setColor(c);
      g2d.fillRect(x, y, w, h);
      g2d.drawRect(x, y, w-1, h-1);
   }
}


class ScreenshotPane extends JPanel implements ChangeListener, ComponentListener, Subject{
   final static int DEFAULT_H = 300;
   static int MAX_NUM_MATCHING = 100;

   Region _match_region;
   int _width, _height;
   double _scale, _ratio;

   boolean _runFind = false; 

   float _similarity;
   int _numMatches;
   Set<Match> _fullMatches = null;
   Vector<Match> _showMatches = null;
   Observer _observer = null;

   protected ScreenImage _simg;
   protected BufferedImage _screen = null;

   private JLabel btnSimilar;
   private JSlider sldSimilar;
   private JSpinner txtNumMatches;

   public ScreenshotPane(ScreenImage simg){
      _match_region = new UnionScreen();
      int w = _match_region.w, h = _match_region.h;
      _ratio = (double)w/h;
      _height = DEFAULT_H;
      _scale = (double)_height/h;
      _width = (int)(w * _scale);
      setPreferredSize(new Dimension(_width, _height));
      addComponentListener(this);
      _simg = simg;
      _screen = simg.getImage();
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

   private JSlider createSlider(){
      //sldSimilar = new JSlider(0, 100, 70);
      sldSimilar = new SimilaritySlider(0, 100, 70);

      sldSimilar.setMajorTickSpacing(10);
      sldSimilar.setPaintTicks(true);

      Hashtable labelTable = new Hashtable();
      labelTable.put( new Integer( 0 ), new JLabel("0.0") );
      labelTable.put( new Integer( 50 ), new JLabel("0.5") );
      labelTable.put( new Integer( 100 ), new JLabel("1.0") );
      sldSimilar.setLabelTable( labelTable );
      sldSimilar.setPaintLabels(true);

      sldSimilar.addChangeListener(this);

      return sldSimilar;

   }

   public JComponent createControls(){
      JPanel pane = new JPanel(new GridBagLayout());
      btnSimilar = new JLabel("Similarity:");

      sldSimilar = createSlider();
      JLabel lblNumMatches = new JLabel("Number of matches:");
      SpinnerNumberModel model = new SpinnerNumberModel(50, 0, ScreenshotPane.MAX_NUM_MATCHING, 1); 
      txtNumMatches = new JSpinner(model);
      lblNumMatches.setLabelFor(txtNumMatches);

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

      txtNumMatches.addChangeListener(this);

      return pane;
   }

   public boolean isExact(){  return _similarity==1.0f; }
   public float getSimilarity(){ return _similarity;  }
   public int getNumMatches(){ return _numMatches; }

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
            Color c = SimilaritySlider.getScoreColor(m.score);
            g2d.setColor(c);
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


