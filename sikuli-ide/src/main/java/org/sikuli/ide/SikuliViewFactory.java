/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

import java.awt.*;
import java.util.*;
import javax.swing.text.*;
import javax.swing.*;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.*;
import javax.swing.SizeRequirements;

import org.sikuli.script.Debug;

public class SikuliViewFactory implements ViewFactory {

    /**
     * @see javax.swing.text.ViewFactory#create(javax.swing.text.Element)
     */
   public View create(Element elem) {
      String kind = elem.getName();
      Debug.log(8, "create: " + kind );

      if (kind != null)
      {
         if (kind.equals(AbstractDocument.ContentElementName))
         {
            return new HighlightLabelView(elem);
            //return new LabelView(elem);
         }
         else if (kind.equals(AbstractDocument.ParagraphElementName))
         {
            //return new MyParagraphView(elem);
            //return new ParagraphView(elem);
            return new LineBoxView(elem, View.X_AXIS);
         }
         else if (kind.equals(AbstractDocument.SectionElementName))
         {
            //return new CenteredBoxView(elem, View.Y_AXIS);
            return new SectionBoxView(elem, View.Y_AXIS);
         }
         else if (kind.equals(StyleConstants.ComponentElementName))
         {
            return new ComponentView(elem);
         }
         else if (kind.equals(StyleConstants.IconElementName))
         {
            return new IconView(elem);
         }
      }

      // default to text display
      return new LabelView(elem);

      //return new SikuliView(element);
   }

}

class MyParagraphView extends ParagraphView
{


    protected View createRow(){
       Element elem = getElement();
       Debug.log(1, "createRow: " + elem);
       View row = new LineBoxView(elem, View.X_AXIS);
       return row;
    }
    public MyParagraphView(Element elem){
        super(elem);
    }
}



class LineBoxView extends BoxView 
{
   public LineBoxView(Element elem, int axis)
   {
      super(elem,axis);
   }

   // for Utilities.getRowStart
   public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
      Rectangle r = a.getBounds();
      View v = getViewAtPosition(pos, r);
      if ((v != null) && (!v.getElement().isLeaf())) {
         // Don't adjust the height if the view represents a branch.
         return super.modelToView(pos, a, b);
      }
      r = a.getBounds();
      int height = r.height;
      int y = r.y;
      Shape loc = super.modelToView(pos, a, b);
      r = loc.getBounds();
      r.height = height;
      r.y = y;
      return r;
   }

   protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans)
   {
      super.layoutMinorAxis(targetSpan,axis,offsets,spans);

      int maxH = 0;
      int offset = 0;

      for (int i = 0; i < spans.length; i++)
         if( spans[i] > maxH )
            maxH = spans[i];
      for (int i = 0; i < offsets.length; i++)
         offsets[i] = (maxH - spans[i])/2;
   }

}

class SectionBoxView extends BoxView
{
  public SectionBoxView(Element elem, int axis)
  {
     super(elem,axis);
  }

  protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans)
  {
     super.layoutMajorAxis(targetSpan,axis,offsets,spans);
     int count = getViewCount();
     if( count == 0 )
        return;
     int offset = 0;
     offsets[0] = 0;
     spans[0] = (int)getView(0).getMinimumSpan(View.Y_AXIS);
     for(int i=1;i<count;i++){
        View view = getView(i);
        spans[i] = (int)view.getMinimumSpan(View.Y_AXIS);
        offset += spans[i-1];
        offsets[i] = offset;
     }


  }

  protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans)
  {
     super.layoutMinorAxis(targetSpan,axis,offsets,spans);
     int count = getViewCount();
     for(int i=0;i<count;i++){
        offsets[i] = 0;
     }

     
  }
}

class HighlightLabelView extends LabelView {

   static FontMetrics _fMetrics = null;
   static String tabStr = nSpaces(UserPreferences.getInstance().getTabWidth());
   static {
      UserPreferences.getInstance().addPreferenceChangeListener(
            new PreferenceChangeListener(){
               @Override
               public void preferenceChange(PreferenceChangeEvent event){
                  //FIXME: need to reposition images 
                  if( event.getKey().equals("TAB_WIDTH") ){
                     tabStr = nSpaces(Integer.parseInt(event.getNewValue()));
                  }
               }
            }
      );
   }

   private static String nSpaces(int n){
      char[] s = new char[n];
      Arrays.fill(s, ' ');
      return new String(s);
   }

   private static Map<Pattern, Color> patternColors;
   private static Font fontParenthesis;

   private static String[] keywords = {
      "and",       "del",       "for",       "is",        "raise",    
      "assert",    "elif",      "from",      "lambda",    "return",   
      "break",     "else",      "global",    "not",       "try",      
      "class",     "except",    "if",        "or",        "while",    
      "continue",  "exec",      "import",    "pass",      "yield",    
      "def",       "finally",   "in",        "print",     "with"
   };

   private static String[] keywordsSikuliClass = {
      "Region", "Screen", "Match", "Pattern",
      "Location", "VDict", "Env", "Key", "Button", "Finder",
      "App", "KeyModifier", "Vision"
   };

   private static String[] keywordsSikuli = {
      "find", "wait", "findAll", "waitVanish", "exists",
      "click", "doubleClick", "rightClick", "hover", "wheel",
      "type", "paste",
      "dragDrop", "drag", "dropAt",
      "mouseMove", "mouseDown", "mouseUp",
      "keyDown", "keyUp",
      "onAppear", "onVanish", "onChange", "observe", "stopObserver",
      "popup", "capture", "input", "sleep", "run",
      "switchApp", "openApp", "closeApp",
      "assertExist", "assertNotExist",
      "selectRegion",
      "getOS", "getMouseLocation", "exit",
      //Region
      "right", "left", "above", "below", "nearby", "inside",
      "getScreen", "getCenter", 
      "setX", "setY", "setW", "setH", "setRect", "setROI",
      "getX", "getY", "getW", "getH", "getRect", "getROI",
      "highlight",
      "getNumberScreens", "getBounds",
      //Pattern
      "similar", "targetOffset", "getLastMatch", "getLastMatches",
      "getTargetOffset", "getFilename",
      //global
      "setAutoWaitTimeout", "setBundlePath", "setShowActions",
      "setThrowException",
      "hasNext", "next", "destroy", "exact", "offset",
      "getOSVersion", "getScore", "getTarget",
      "getBundlePath", "getAutoWaitTimeout", "getThrowException",
      "getClipboard",
      "addImagePath", "removeImagePath", "getImagePath",
      //App class
      "open", "close", "focus", "window", "focusedWindow",

   };

   private static String[] constantsSikuli = {
      "FOREVER",
      "KEY_SHIFT", "KEY_CTRL", "KEY_META", "KEY_ALT", "KEY_CMD", "KEY_WIN",
      "ENTER", "BACKSPACE", "TAB", "ESC", "UP", "RIGHT", "DOWN", "LEFT",
      "PAGE_UP", "PAGE_DOWN", "DELETE", "END", "HOME", "INSERT", "F1",
      "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12",
      "F13", "F14", "F15", "SHIFT", "CTRL", "ALT", "META", "CMD", "WIN",
      "SCREEN", "MIDDLE",
      "WHEEL_UP", "WHEEL_DOWN",
      "PRINTSCREEN", "SCROLL_LOCK", "PAUSE", "CAPS_LOCK", "NUM0",
      "NUM1", "NUM2", "NUM3", "NUM4", "NUM5", "NUM6", "NUM7", "NUM8", "NUM9",
      "SEPARATOR", "NUM_LOCK", "ADD", "MINUS", "MULTIPLY", "DIVIDE"
   };

   static {
      Debug.log(4, "init patternColors");
      fontParenthesis = new Font("Osaka-Mono", Font.PLAIN, 30);
      // NOTE: the order is important!
      patternColors = new HashMap<Pattern, Color>();
      patternColors.put(Pattern.compile("(#:.*$)"), new Color(220,220,220));
      patternColors.put(Pattern.compile("(#.*$)"), new Color(138,140,193));
      patternColors.put(Pattern.compile("(\"[^\"]*\"?)"), new Color(128,0,0));
      patternColors.put(Pattern.compile("(\'[^\']*\'?)"), new Color(128,0,0));
      patternColors.put(Pattern.compile("\\b([0-9]+)\\b"), new Color(128,64,0));
      for(int i=0;i<keywords.length;i++)
         patternColors.put(Pattern.compile("\\b("+keywords[i]+")\\b"), 
               Color.blue);

      for(int i=0;i<keywordsSikuli.length;i++){
         patternColors.put(Pattern.compile("\\b("+keywordsSikuli[i]+")\\b"), 
               new Color(63,127,127));
      }

      for(int i=0;i<keywordsSikuliClass.length;i++){
         patternColors.put(Pattern.compile(
                  "\\b("+keywordsSikuliClass[i]+")\\b"), 
               new Color(215,41,56));
      }

      for(int i=0;i<constantsSikuli.length;i++){
         patternColors.put(Pattern.compile(
                  "\\b("+constantsSikuli[i]+")\\b"), 
               new Color(128,64,0));
      }

      //patternColors.put(Pattern.compile("(\t)"), Color.white);

      /*
         patternColors.put(Pattern.compile("(\\()$"), Color.black);
         patternColors.put(Pattern.compile("^(\\))"), Color.black);
         */


   }


   public HighlightLabelView(Element elm){
      super(elm);
   }

   private int countTab(String str){
      int pos = -1;
      int count = 0;
      while((pos=str.indexOf('\t', pos+1))!=-1){
         count++;
      }
      return count;
   }


   private int getTabWidth(){
      return stringWidth(tabStr);
   }


   private int stringWidth(String str){
      if(_fMetrics == null)
         _fMetrics = getGraphics().getFontMetrics();
      return _fMetrics.stringWidth(str);
   }


   private float getRealTabWidth(){
      final int tabCharWidth;
      if(Utils.isMacOSX())
         tabCharWidth = stringWidth("\t");
      else
         tabCharWidth = stringWidth(" ");
      return getTabWidth() - tabCharWidth /* + 1f */; //still buggy
   }

   private float tabbedWidth(){
      String str = getText(getStartOffset(), getEndOffset()).toString();
      int tab = countTab(str);
      if(Utils.isMacOSX())
         return stringWidth(str) + getRealTabWidth()*tab;
      else
         return stringWidth(str) + getTabWidth()*tab;
   }

   public float getMinimumSpan(int axis) {
      float f = super.getMinimumSpan(axis);
      if(axis == View.X_AXIS){
         f = tabbedWidth();
      }
      return f;
   }

   public float getMaximumSpan(int axis) {
      float f = super.getMaximumSpan(axis);
      if(axis == View.X_AXIS){
         f = tabbedWidth();
      }
      return f;
   }

   public float getPreferredSpan(int axis) {
      float f = super.getPreferredSpan(axis);
      if(axis == View.X_AXIS){
         f = tabbedWidth();
      }
      return f;
   }



   public int viewToModel(float fx, float fy, Shape a, Position.Bias[] bias) {
      bias[0] = Position.Bias.Forward;

      Debug.log(9, "viewToModel: " + fx + " " + fy);
      String str = getText(getStartOffset(), getEndOffset()).toString();

      int left = getStartOffset(), right = getEndOffset();
      int pos = 0;
      while(left<right){
         Debug.log(9, "viewToModel: " + left + " " + right + " " + pos);
         pos = (left+right)/2;
         try{
            Shape s = modelToView(pos, a, bias[0]);
            float sx = s.getBounds().x;
            if( sx > fx )
               right = pos;
            else if( sx < fx )
               left = pos+1;
            else
               break;
         }
         catch(BadLocationException ble){
            break;
         }
      }
      pos = left-1>=getStartOffset()? left-1 : getStartOffset();
      try{
         Debug.log(9, "viewToModel: try " + pos);
         Shape s1 = modelToView(pos, a, bias[0]);
         Shape s2 = modelToView(pos+1, a, bias[0]);
         if( Math.abs(s1.getBounds().x-fx) <= Math.abs(s2.getBounds().x-fx) )
            return pos;
         else
            return pos+1;
      }
      catch(BadLocationException ble){}
      return pos;

   }


   public Shape modelToView(int pos, Shape a, Position.Bias b)
                                             throws BadLocationException {
      
      int start = getStartOffset(), end = getEndOffset();
      Debug.log(9,"[modelToView] start: " + start + 
                  " end: " + end + " pos:" + pos);
      String strHead = getText(start, pos).toString();
      String strTail = getText(pos, end).toString();
      Debug.log(9, "[modelToView] [" + strHead + "]-pos-[" + strTail+"]");
      int tabHead = countTab(strHead), tabTail = countTab(strTail);
      Debug.log(9, "[modelToView] " + tabHead + " " + tabTail);
      Shape s = super.modelToView(pos, a, b);
      Rectangle ret = s.getBounds();
      Debug.log(9, "[modelToView] super.bounds: " + ret);
      if(pos!=end)
         ret.x += tabHead * getRealTabWidth();
         //ret.width += tabTail*tabWidth;
      Debug.log(9, "[modelToView] new bounds: " + ret);
      return ret;
   }

   public void paint(Graphics g, Shape shape){
      Graphics2D g2d = (Graphics2D)g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

      //super.paint(g, shape); // for drawing selection

      String text = getText(getStartOffset(), getEndOffset()).toString();
      //System.out.println("draw " + text);

      SortedMap<Integer, Integer> posMap = new TreeMap<Integer, Integer>();
      SortedMap<Integer, Color> colorMap = new TreeMap<Integer, Color>();
      buildColorMaps(text, posMap, colorMap);

      if(_fMetrics == null)
         _fMetrics = g2d.getFontMetrics();
      Rectangle alloc = (shape instanceof Rectangle)? 
                           (Rectangle)shape : shape.getBounds();

      int sx = alloc.x;
      int sy = alloc.y + alloc.height - _fMetrics.getDescent();
      int i = 0;

      for (Map.Entry<Integer, Integer> entry : posMap.entrySet()) {
         int start = entry.getKey();
         int end = entry.getValue();

         if (i <= start) {
            g2d.setColor(Color.black);
            String str = text.substring(i, start);
            sx = drawString(g2d, str, sx, sy);
         }
         else
            break;

         g2d.setColor(colorMap.get(start));
         i = end;
         String str = text.substring(start, i);
         /*
         if( str.equals("(") || str.equals(")") )
            sx = drawParenthesis(g2d, str, sx, sy);
         else
         */
            sx = drawString(g2d, str, sx, sy);
      }

      // Paint possible remaining text black
      if (i < text.length()) {
         g2d.setColor(Color.black);
         String str = text.substring(i, text.length());
         sx = drawString(g2d, str, sx, sy);
      }

   }

   int drawTab(Graphics2D g2d, int x, int y){
      return drawString(g2d, tabStr, x, y);
   }

   int drawString(Graphics2D g2d, String str, int x, int y){
      if(str.length()==0)
         return x;
      int tabPos = str.indexOf('\t');
      if( tabPos != -1){
         x = drawString(g2d, str.substring(0, tabPos), x, y);
         x = drawTab(g2d, x, y);
         x = drawString(g2d, str.substring(tabPos+1), x, y);
      }
      else{
         g2d.drawString(str, x, y);
         x += stringWidth(str);
      }
      return x;
   }

   int drawParenthesis(Graphics2D g2d, String str, int x, int y){
      Font origFont = g2d.getFont();
      g2d.setFont(fontParenthesis);
      g2d.drawString(str, x, y);
      x += g2d.getFontMetrics().stringWidth(str);
      g2d.setFont(origFont);
      return x;
   }


   void buildColorMaps(String text, Map<Integer, Integer> posMap, 
                                    Map<Integer,Color> colorMap){

      // Match all regexes on this snippet, store positions
         for (Map.Entry<Pattern, Color> entry : patternColors.entrySet()) {

            Matcher matcher = entry.getKey().matcher(text);

            while(matcher.find()) {
               posMap.put(matcher.start(1), matcher.end());
               colorMap.put(matcher.start(1), entry.getValue());
            }
         }
   }

}


