package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;


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


