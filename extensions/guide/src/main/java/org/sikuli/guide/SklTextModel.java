package org.sikuli.guide;

interface SklTextModel extends SklModel {
   
   public void setText(String text);
   public String getText();
   
   
   public final int padding = 2;    
   static public final String PROPERTY_TEXT = "text";
}