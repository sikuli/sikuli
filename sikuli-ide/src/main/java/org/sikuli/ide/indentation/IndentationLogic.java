package org.sikuli.ide.indentation;

/**
 * An object that provides hints for automatic indentation of lines that are
 * being entered. When the user has entered a new line of code and hits the
 * enter key, the object provides hints how to change the indentation of the
 * last line entered and the next line. The typical usage is:
 * <ol>
 * <li>Call {@link #reset()} to reset the object's state.
 * <li>Feed the code to the object by calling {@link #addText(String)} up to and
 * including the last entered by the user (including the end-of-line sequence).
 * <li>Call {@link #shouldChangeLastLineIndentation()} and
 * {@link #shouldChangeNextLineIndentation()} to get hints about the indentation
 * of the last line entered and the next line.
 * </ol>
 * Note: the proposed indentation change for the next line may depend on the
 * current indentation of the last line entered. If you change the indentation
 * of the last line entered, you may also have to adjust the indentation of the
 * next line by the same amount.
 */
public interface IndentationLogic {

   /**
    * Sets the number of whitespace columns that equals a single tab used by
    * this object to calculate the indentation of lines.
    * 
    * @param tabWidth
    *           the number of whitespace columns that equals a single tab
    */
   public void setTabWidth(int tabWidth);

   /**
    * Returns the number of whitespace columns that equals a single tab used by
    * this object to calculate the indentation of lines.
    * 
    * @return the number of whitespace columns that equals a single tab
    */
   public int getTabWidth();

   /**
    * Resets the state of this object. The new state will be as if no text had
    * been fed to this object.
    */
   public void reset();

   /**
    * Feeds a chunk of text (i.e. code) to this object. The text can be a single
    * line or multiple lines or even incomplete lines. You can feed an entire
    * document at once, or line by line. Any new text will be (virtually)
    * appended to text added earlier.
    * 
    * @param text
    *           a chunk of code
    */
   public void addText(String text);

   /**
    * Returns the line number of the last line fed to this object.
    * 
    * @return the line number of the last line (0-based)
    */
   public int getLastLineNumber();

   /**
    * Returns a hint about how the indentation of the last line fed to this
    * object should be changed. A negative value means decrease indentation
    * while a positive value means increase indentation by the returned value.
    * 
    * @return the number of columns by which the indentation should be changed
    */
   public int shouldChangeLastLineIndentation();

   /**
    * Returns a hint about how the indentation of the next line (the line
    * following the last line fed to this object) should be changed. A negative
    * value means decrease indentation while a positive value means increase
    * indentation by the returned value.
    * 
    * @return the number of columns by which the indentation should be changed
    */
   public int shouldChangeNextLineIndentation();
}
