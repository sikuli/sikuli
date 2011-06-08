package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.sikuli.script.Debug;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class DefaultSklTextModel 
   extends DefaultSklObjectModel implements SklTextModel{
   
   @Element
   private String text;
   
   private int maximumWidth = Integer.MAX_VALUE;
   private int fontSize = 12;

   public DefaultSklTextModel(String text) {
      super();
      setText(text);
      setBackground(Color.yellow);
   }

   public DefaultSklTextModel() {
      super();
      setBackground(Color.yellow);
   }

   
   @Override   
   public void setText(String text) {
      this.pcs.firePropertyChange(PROPERTY_TEXT, this.text, this.text = text);
   }

   @Override
   public String getText() {
      return text;
   }
   
   public void setMaximumWidth(int maximumWidth) {
      this.maximumWidth = maximumWidth;
   }

   public int getMaximumWidth() {
      return maximumWidth;
   }

   public void setFontSize(int fontSize) {
      this.fontSize = fontSize;
   }

   public int getFontSize() {
      return fontSize;
   }

}

class SklTextView extends SklView {
   JPanel panel;
   JLabel label;
   public SklTextView(SklModel model){
      super(model);   
   }
   
   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      super.propertyChange(evt);
      update();
   }
   
   @Override
   protected void update(){
      if (label == null){         
         panel = new JPanel();
         label = new JLabel();         
         panel.setLayout(null);
         panel.add(label);
         add(panel);
      }

      SklTextModel textModel = (SklTextModel) _model;
      label.setText(textModel.getText());
      label.setSize(label.getPreferredSize());
      label.setLocation(textModel.padding,textModel.padding);      
      panel.setOpaque(true);
      panel.setBackground(textModel.getBackground());

      setLayout(null);
      
      Dimension size = label.getPreferredSize();
      size.width = size.width + 2*textModel.padding;
      size.height = size.height + 2*textModel.padding;
      panel.setSize(size);
      setActualSize(size);
      
      _model.setWidth(size.width);
      _model.setHeight(size.height);
      super.update();      
   }
   
}
