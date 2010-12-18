package org.sikuli.ide;

import java.awt.*;
import javax.swing.*;

class StatusBar extends JPanel {

   private JLabel _lblMsg;
   private JLabel _lblCaretPos;

   public StatusBar() {
      setLayout(new BorderLayout());
      setPreferredSize(new Dimension(10, 20));

      JPanel rightPanel = new JPanel(new BorderLayout());
      rightPanel.setOpaque(false);
      _lblMsg = new JLabel();
      _lblMsg.setPreferredSize(new Dimension(400, 20));
      _lblCaretPos = new JLabel();
      _lblCaretPos.setPreferredSize(new Dimension(200, 20));
      setCaretPosition(1, 1);

      add(_lblMsg, BorderLayout.WEST);
      add(_lblCaretPos, BorderLayout.LINE_END);
      add(rightPanel, BorderLayout.EAST);
   }

   public void setCaretPosition(int row, int col){
      _lblCaretPos.setText(
            I18N._I("statusLineColumn", row, col) );
   }

   protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      int y = 0;
      g.setColor(new Color(156, 154, 140));
      g.drawLine(0, y, getWidth(), y);
      y++;
      g.setColor(new Color(196, 194, 183));
      g.drawLine(0, y, getWidth(), y);
   }
}
