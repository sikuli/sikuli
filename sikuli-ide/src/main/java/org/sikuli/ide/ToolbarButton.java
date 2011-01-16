package org.sikuli.ide;

import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import com.explodingpixels.macwidgets.plaf.UnifiedToolbarButtonUI;

public class ToolbarButton extends JButton {

   public ToolbarButton(){
      setBorderPainted(false);
      putClientProperty("JButton.buttonType", "textured");
      //setIconTextGap(8);
      /*
      setVerticalTextPosition(SwingConstants.BOTTOM);
      setHorizontalTextPosition(SwingConstants.CENTER);
      Font f = new Font(null, Font.PLAIN, 10);
      setFont(f);
      */
      setUI(new UnifiedToolbarButtonUI());
      setBorder(BorderFactory.createEmptyBorder(3,10,3,10));
   }
}
