package org.sikuli.ide;

/**
 * 
 */
public interface Indentation {

   public void setTabWidth(int tabWidth);

   public int getTabWidth();

   public void reset();

   public void addText(String text);

   public int getLastLineNumber();

   public int shouldChangeLastLineIndentation();

   public int shouldChangeNextLineIndentation();
}
