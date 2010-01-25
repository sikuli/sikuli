package edu.mit.csail.uid;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class KeyCodeConverter {
   private Map<Character, Integer> _codeMap;

   public int getKeyCode(char u_ch){
      String keyname = "X";
      if( (u_ch >= 'A' && u_ch <= 'Z') || (u_ch >= '0' && u_ch <= '9') )
         keyname = new Character(u_ch).toString();
      else{
         if( u_ch == '.' )
            keyname = "PERIOD";
      }
      return AWTKeyStroke.getAWTKeyStroke("pressed " + keyname).getKeyCode();
   }
         
   public KeyCodeConverter(){

System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ACCEPT, 0).getKeyChar() + " VK_ACCEPT");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ADD, 0).getKeyChar() + " VK_ADD");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ALT, 0).getKeyChar() + " VK_ALT");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_AMPERSAND, 0).getKeyChar() + " VK_AMPERSAND");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ASTERISK, 0).getKeyChar() + " VK_ASTERISK");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_AT, 0).getKeyChar() + " VK_AT");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_BACK_QUOTE, 0).getKeyChar() + " VK_BACK_QUOTE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_BACK_SLASH, 0).getKeyChar() + " VK_BACK_SLASH");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_BACK_SPACE, 0).getKeyChar() + " VK_BACK_SPACE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_BRACELEFT, 0).getKeyChar() + " VK_BRACELEFT");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_BRACERIGHT, 0).getKeyChar() + " VK_BRACERIGHT");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_CANCEL, 0).getKeyChar() + " VK_CANCEL");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_CAPS_LOCK, 0).getKeyChar() + " VK_CAPS_LOCK");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_CIRCUMFLEX, 0).getKeyChar() + " VK_CIRCUMFLEX");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_CLEAR, 0).getKeyChar() + " VK_CLEAR");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_CLOSE_BRACKET, 0).getKeyChar() + " VK_CLOSE_BRACKET");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_CODE_INPUT, 0).getKeyChar() + " VK_CODE_INPUT");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_COLON, 0).getKeyChar() + " VK_COLON");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_COMMA, 0).getKeyChar() + " VK_COMMA");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_COMPOSE, 0).getKeyChar() + " VK_COMPOSE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_CONTROL, 0).getKeyChar() + " VK_CONTROL");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_CONVERT, 0).getKeyChar() + " VK_CONVERT");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_COPY, 0).getKeyChar() + " VK_COPY");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_CUT, 0).getKeyChar() + " VK_CUT");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_ABOVEDOT, 0).getKeyChar() + " VK_DEAD_ABOVEDOT");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_ABOVERING, 0).getKeyChar() + " VK_DEAD_ABOVERING");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_ACUTE, 0).getKeyChar() + " VK_DEAD_ACUTE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_BREVE, 0).getKeyChar() + " VK_DEAD_BREVE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_CARON, 0).getKeyChar() + " VK_DEAD_CARON");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_CEDILLA, 0).getKeyChar() + " VK_DEAD_CEDILLA");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_CIRCUMFLEX, 0).getKeyChar() + " VK_DEAD_CIRCUMFLEX");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_DIAERESIS, 0).getKeyChar() + " VK_DEAD_DIAERESIS");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_DOUBLEACUTE, 0).getKeyChar() + " VK_DEAD_DOUBLEACUTE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_GRAVE, 0).getKeyChar() + " VK_DEAD_GRAVE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_IOTA, 0).getKeyChar() + " VK_DEAD_IOTA");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_MACRON, 0).getKeyChar() + " VK_DEAD_MACRON");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_OGONEK, 0).getKeyChar() + " VK_DEAD_OGONEK");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_SEMIVOICED_SOUND, 0).getKeyChar() + " VK_DEAD_SEMIVOICED_SOUND");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_TILDE, 0).getKeyChar() + " VK_DEAD_TILDE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DEAD_VOICED_SOUND, 0).getKeyChar() + " VK_DEAD_VOICED_SOUND");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DECIMAL, 0).getKeyChar() + " VK_DECIMAL");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DELETE, 0).getKeyChar() + " VK_DELETE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DIVIDE, 0).getKeyChar() + " VK_DIVIDE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DOLLAR, 0).getKeyChar() + " VK_DOLLAR");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DOWN, 0).getKeyChar() + " VK_DOWN");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_END, 0).getKeyChar() + " VK_END");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0).getKeyChar() + " VK_ENTER");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_EQUALS, 0).getKeyChar() + " VK_EQUALS");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ESCAPE, 0).getKeyChar() + " VK_ESCAPE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_EURO_SIGN, 0).getKeyChar() + " VK_EURO_SIGN");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_EXCLAMATION_MARK, 0).getKeyChar() + " VK_EXCLAMATION_MARK");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_FINAL, 0).getKeyChar() + " VK_FINAL");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_FIND, 0).getKeyChar() + " VK_FIND");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_FULL_WIDTH, 0).getKeyChar() + " VK_FULL_WIDTH");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_GREATER, 0).getKeyChar() + " VK_GREATER");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_HALF_WIDTH, 0).getKeyChar() + " VK_HALF_WIDTH");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_HELP, 0).getKeyChar() + " VK_HELP");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_HIRAGANA, 0).getKeyChar() + " VK_HIRAGANA");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_HOME, 0).getKeyChar() + " VK_HOME");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_INPUT_METHOD_ON_OFF, 0).getKeyChar() + " VK_INPUT_METHOD_ON_OFF");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_INSERT, 0).getKeyChar() + " VK_INSERT");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_INVERTED_EXCLAMATION_MARK, 0).getKeyChar() + " VK_INVERTED_EXCLAMATION_MARK");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_JAPANESE_HIRAGANA, 0).getKeyChar() + " VK_JAPANESE_HIRAGANA");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_JAPANESE_KATAKANA, 0).getKeyChar() + " VK_JAPANESE_KATAKANA");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_JAPANESE_ROMAN, 0).getKeyChar() + " VK_JAPANESE_ROMAN");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_OPEN_BRACKET, 0).getKeyChar() + " VK_OPEN_BRACKET");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_PAGE_DOWN, 0).getKeyChar() + " VK_PAGE_DOWN");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_PAGE_UP, 0).getKeyChar() + " VK_PAGE_UP");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_PASTE, 0).getKeyChar() + " VK_PASTE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_PAUSE, 0).getKeyChar() + " VK_PAUSE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_PERIOD, 0).getKeyChar() + " VK_PERIOD");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_PLUS, 0).getKeyChar() + " VK_PLUS");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_PRINTSCREEN, 0).getKeyChar() + " VK_PRINTSCREEN");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_PROPS, 0).getKeyChar() + " VK_PROPS");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_QUOTE, 0).getKeyChar() + " VK_QUOTE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_QUOTEDBL, 0).getKeyChar() + " VK_QUOTEDBL");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ROMAN_CHARACTERS, 0).getKeyChar() + " VK_ROMAN_CHARACTERS");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_S, 0).getKeyChar() + " VK_S");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_SCROLL_LOCK, 0).getKeyChar() + " VK_SCROLL_LOCK");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_SEMICOLON, 0).getKeyChar() + " VK_SEMICOLON");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_SEPARATER, 0).getKeyChar() + " VK_SEPARATER");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_SHIFT, 0).getKeyChar() + " VK_SHIFT");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_SLASH, 0).getKeyChar() + " VK_SLASH");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_SPACE, 0).getKeyChar() + " VK_SPACE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_STOP, 0).getKeyChar() + " VK_STOP");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_SUBTRACT, 0).getKeyChar() + " VK_SUBTRACT");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0).getKeyChar() + " VK_TAB");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_UNDEFINED, 0).getKeyChar() + " VK_UNDEFINED");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_UNDERSCORE, 0).getKeyChar() + " VK_UNDERSCORE");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_UNDO, 0).getKeyChar() + " VK_UNDO");
System.out.println( AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_UP, 0).getKeyChar() + " VK_UP");
   }
}
