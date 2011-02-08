package org.sikuli.ide;

public interface CodeFormatter {

   public void setTabWidth(int tabwidth);

   public int getTabWidth();

   /**
    * 
    * @param linenum 0-based
    * @return
    */
   public int shouldChangeNextLineIndentation(int linenum);
}
