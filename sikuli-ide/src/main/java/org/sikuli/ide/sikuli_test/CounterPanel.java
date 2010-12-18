package org.sikuli.ide.sikuli_test;

import org.sikuli.ide.SikuliIDE;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * A panel with test run counters
 */
public class CounterPanel extends JPanel {
   private JLabel fNumberOfErrors;
   private JLabel fNumberOfFailures;
   private JLabel fNumberOfRuns;
   private Icon fFailureIcon= SikuliIDE.getIconResource("/icons/failure.gif");
   private Icon fErrorIcon= SikuliIDE.getIconResource("/icons/error.gif");

   private int fTotal;

   public CounterPanel() {
      super(new GridBagLayout());
      fNumberOfErrors= createOutputField(2);
      fNumberOfFailures= createOutputField(2);
      fNumberOfRuns= createOutputField(3);

      addToGrid(new JLabel("Runs:", SwingConstants.CENTER),
            0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0));
      addToGrid(fNumberOfRuns,
            1, 0, 1, 1, 0.33, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
            new Insets(0, 8, 0, 0));

      addToGrid(new JLabel("Errors:", fErrorIcon, SwingConstants.LEFT),
            2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(0, 8, 0, 0));
      addToGrid(fNumberOfErrors,
            3, 0, 1, 1, 0.33, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 8, 0, 0));

      addToGrid(new JLabel("Failures:", fFailureIcon, SwingConstants.LEFT),
            4, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.LINE_END, GridBagConstraints.NONE,
            new Insets(0, 8, 0, 0));
      addToGrid(fNumberOfFailures,
            5, 0, 1, 1, 0.33, 0.0,
            GridBagConstraints.LINE_END, GridBagConstraints.HORIZONTAL,
            new Insets(0, 8, 0, 0));
      reset();
   }

   private JLabel createOutputField(int width) {
      JLabel field= new JLabel("0");
      // force a fixed layout to avoid accidental hiding on relayout
      //field.setMinimumSize(field.getPreferredSize());
      //field.setMaximumSize(field.getPreferredSize());
      field.setHorizontalAlignment(SwingConstants.LEFT);
      //field.setFont(StatusLine.BOLD_FONT);
      //field.setEditable(false);
      field.setBorder(BorderFactory.createEmptyBorder());
      return field;
   }

   public void addToGrid(Component comp,
         int gridx, int gridy, int gridwidth, int gridheight,
         double weightx, double weighty,
         int anchor, int fill,
         Insets insets) {

      GridBagConstraints constraints= new GridBagConstraints();
      constraints.gridx= gridx;
      constraints.gridy= gridy;
      constraints.gridwidth= gridwidth;
      constraints.gridheight= gridheight;
      constraints.weightx= weightx;
      constraints.weighty= weighty;
      constraints.anchor= anchor;
      constraints.fill= fill;
      constraints.insets= insets;
      add(comp, constraints);
   }

   public void reset() {
      setLabelValue(fNumberOfErrors, 0);
      setLabelValue(fNumberOfFailures, 0);
      fTotal= 0;
      setRunValue(0);
   }

   public void setTotal(int value) {
      fTotal= value;
   }

   public void setRunValue(int value) {
      fNumberOfRuns.setText(Integer.toString(value) + "/" + fTotal);
   }

   public void setErrorValue(int value) {
      setLabelValue(fNumberOfErrors, value);
   }

   public void setFailureValue(int value) {
      setLabelValue(fNumberOfFailures, value);
   }

   private void setLabelValue(JLabel label, int value) {
      label.setText(Integer.toString(value));
   }
}
