package org.sikuli.script;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GridLayoutDemo extends JFrame {
   static final String gapList[] = {"0", "10", "15", "20"};
   final static int maxGap = 20;
   JComboBox horGapComboBox;
   JComboBox verGapComboBox;
   JButton applyButton = new JButton("Apply gaps");
   GridLayout experimentLayout = new GridLayout(0,2);

   JButton[] buttons = new JButton[10];
   public GridLayoutDemo(String name) {
      super(name);
      setResizable(false);
   }

   public void initGaps() {
      horGapComboBox = new JComboBox(gapList);
      verGapComboBox = new JComboBox(gapList);
   }

   public String getText(int i){
      return buttons[i].getText();
   }

   public void addComponentsToPane(final Container pane) {
      initGaps();
      final JPanel compsToExperiment = new JPanel();
      compsToExperiment.setLayout(experimentLayout);
      JPanel controls = new JPanel();
      controls.setLayout(new GridLayout(3,3));

      for(int i=1;i<=9;i++){
         buttons[i] = new JButton("Button " + i);
         buttons[i].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               ((JButton)ae.getSource()).setText("clicked");
            }
         });
         controls.add(buttons[i]);
      }
      /*
      //Set up components preferred size
      JButton b = new JButton("Just fake button");
      Dimension buttonSize = b.getPreferredSize();
      compsToExperiment.setPreferredSize(new Dimension((int)(buttonSize.getWidth() * 2.5)+maxGap,
      (int)(buttonSize.getHeight() * 3.5)+maxGap * 2));

      //Add buttons to experiment with Grid Layout
      compsToExperiment.add(new JButton("Button 1"));
      compsToExperiment.add(new JButton("Button 2"));
      compsToExperiment.add(new JButton("Button 3"));
      compsToExperiment.add(new JButton("Long-Named Button 4"));
      compsToExperiment.add(new JButton("5"));

      //Add controls to set up horizontal and vertical gaps
      controls.add(new Label("Horizontal gap:"));
      controls.add(new Label("Vertical gap:"));
      controls.add(new Label(" "));
      controls.add(horGapComboBox);
      controls.add(verGapComboBox);
      controls.add(applyButton);

      //Process the Apply gaps button press
      applyButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
      //Get the horizontal gap value
      String horGap = (String)horGapComboBox.getSelectedItem();
      //Get the vertical gap value
      String verGap = (String)verGapComboBox.getSelectedItem();
      //Set up the horizontal gap value
      experimentLayout.setHgap(Integer.parseInt(horGap));
      //Set up the vertical gap value
      experimentLayout.setVgap(Integer.parseInt(verGap));
      //Set up the layout of the buttons
      experimentLayout.layoutContainer(compsToExperiment);
      }
      });
      pane.add(compsToExperiment, BorderLayout.NORTH);
      pane.add(new JSeparator(), BorderLayout.CENTER);
      */
      pane.add(controls, BorderLayout.SOUTH);
   }

   /**
    * Create the GUI and show it.  For thread safety,
    * this method is invoked from the
    * event dispatch thread.
    */
   public static GridLayoutDemo createAndShowGUI() {
      //Create and set up the window.
      GridLayoutDemo frame = new GridLayoutDemo("GridLayoutDemo");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //Set up the content pane.
      frame.addComponentsToPane(frame.getContentPane());
      //Display the window.
      frame.pack();
      frame.setVisible(true);
      return frame;
   }

   public static void main(String[] args) {
      //Schedule a job for the event dispatch thread:
      //creating and showing this application's GUI.
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGUI();
         }
      });
   }
}
