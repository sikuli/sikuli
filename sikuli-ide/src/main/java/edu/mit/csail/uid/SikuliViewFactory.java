package edu.mit.csail.uid;

import java.awt.*;
import java.util.*;
import javax.swing.text.*;
import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.*;
import javax.swing.SizeRequirements;

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
   final String tabStr = "    "; // default tab size: 4


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

    private static String[] keywordsSikuli = {
       "find", "wait", "findAll", "waitVanish",
       "click", "doubleClick", "rightClick", "hover", 
       "type", "paste",
       "dragDrop", "drag", "dropAt",
       "mouseMove", "mouseDown", "mouseUp",
       "keyDown", "keyUp",
       "onAppear", "onVanish", "onChange", "observe", "stopObserver",
       "popup", "capture", "input",
       "switchApp", "openApp", "closeApp",
       "assertExist", "assertNotExist",
    };

    static {
       Debug.log(4, "init patternColors");
       fontParenthesis = new Font("Osaka-Mono", Font.PLAIN, 30);
        // NOTE: the order is important!
        patternColors = new HashMap<Pattern, Color>();
        patternColors.put(Pattern.compile("(#:.*$)"), new Color(220,220,220));
        patternColors.put(Pattern.compile("(#.*$)"), new Color(200,0,200));
        patternColors.put(Pattern.compile("(\"[^\"]*\"?)"), new Color(128,0,0));
        patternColors.put(Pattern.compile("\\b([0-9]+)\\b"), new Color(128,64,0));
        for(int i=0;i<keywords.length;i++)
           patternColors.put(Pattern.compile("\\b("+keywords[i]+")\\b"), 
                             Color.blue);

        for(int i=0;i<keywordsSikuli.length;i++){
           patternColors.put(Pattern.compile("\\b("+keywordsSikuli[i]+")\\b"), 
                             new Color(63,127,127));
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

   private int tabbedWidth(){
      if(_fMetrics==null)
         return -1;
      String str = getText(getStartOffset(), getEndOffset()).toString();
      int tab = countTab(str);
      int tabWidth = _fMetrics.stringWidth(tabStr.substring(1));
      return _fMetrics.stringWidth(str) + tabWidth*tab;
   }

   public float getMinimumSpan(int axis) {
      float f = super.getMinimumSpan(axis);
      if(axis == View.X_AXIS && _fMetrics!=null){
         f = tabbedWidth();
      }
      return f;
   }

   public float getMaximumSpan(int axis) {
      float f = super.getMaximumSpan(axis);
      if(axis == View.X_AXIS && _fMetrics!=null ){
         f = tabbedWidth();
      }
      return f;
   }

   public float getPreferredSpan(int axis) {
      float f = super.getPreferredSpan(axis);
      if(axis == View.X_AXIS && _fMetrics!=null){
         f = tabbedWidth();
      }
      return f;
   }


   public int viewToModel(float fx, float fy, Shape a, Position.Bias[] bias) {
      bias[0] = Position.Bias.Forward;

      Debug.log(3, "viewToModel: " + fx + " " + fy);
      String str = getText(getStartOffset(), getEndOffset()).toString();

      int left = getStartOffset(), right = getEndOffset();
      int pos = 0;
      while(left<right){
         Debug.log(3, "viewToModel: " + left + " " + right + " " + pos);
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
         Debug.log(3, "viewToModel: try " + pos);
         Shape s1 = modelToView(pos, a, bias[0]);
         Shape s2 = modelToView(pos+1, a, bias[0]);
         if( Math.abs(s1.getBounds().x-fx) < Math.abs(s2.getBounds().x-fx) )
            return pos;
         else
            return pos+1;
      }
      catch(BadLocationException ble){}
      return pos;

      /*
      int pos = super.viewToModel(fx, fy, a, bias);
      Debug.log(3, "viewToModel: super.pos " + pos); 
      String strHead = getText(getStartOffset(), pos).toString();
      int tabHead = countTab(strHead);
      if(tabHead>0){
         // super:  TEXT 
         //   new:[\t][\t]TEXT
         Debug.log(3, "viewToModel: tab count " + tabHead);
         int tabWidth = _fMetrics.stringWidth(tabStr.substring(1));
         pos = viewToModel(fx-tabWidth*tabHead, fy, a, bias);
      }
      return pos;
      */

      /*
      Rectangle alloc = a.getBounds();
      Document doc = getDocument();
      int x = (int) fx;
      int y = (int) fy;

      // If they're asking about a view position above the area covered by      
      // this view, then the position is assumed to be the starting position
      // of this view.
      if (y < alloc.y) {
         return getStartOffset();
      }
      // If they're asking about a position below this view, the position
      // is assumed to be the ending position of this view.
      else if (y > alloc.y + alloc.height) {
         return getEndOffset() - 1;
      }
      else {

         // If the point is to the left of the line...
         if (x < alloc.x)
            return getStartOffset();
         // If the point is to the right of the line...
         else if (x > alloc.x + alloc.width)
            return getEndOffset() - 1;
         else {
            // Determine the offset into the text
         }

      }
      */

   }


   public Shape modelToView(int pos, Shape a, Position.Bias b)
                                             throws BadLocationException {
      
      if(_fMetrics==null)  
         return super.modelToView(pos, a, b);
      String strHead = getText(getStartOffset(), pos).toString();
      String strTail = getText(pos, getEndOffset()).toString();
      //System.out.println("modelToView: [" + strHead + "] [" + strTail+"]");
      int tabHead = countTab(strHead), tabTail = countTab(strTail);
      //System.out.println("modelToView: " + tabHead + " " + tabTail);
      Shape s = super.modelToView(pos, a, b);
      Rectangle ret = s.getBounds();
      int tabWidth = _fMetrics.stringWidth(tabStr.substring(1));
      ret.x += tabHead*tabWidth;
      ret.width += tabTail*tabWidth;
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
      drawString(g2d, tabStr, x, y);
      return x + _fMetrics.stringWidth(tabStr);
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
         x += _fMetrics.stringWidth(str);
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


