/* From http://java.sun.com/docs/books/tutorial/index.html */
/*
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */
/*
 * KeyEventDemo.java is a 1.4 example that requires no other files.
 */
package org.sikuli.script;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class KeyEventDemo extends JPanel implements KeyListener, ActionListener {
  JTextArea displayArea;

  JTextField typingArea;

  static final String newline = "\n";

  public KeyEventDemo() {
    super(new BorderLayout());

    JButton button = new JButton("Clear");
    button.addActionListener(this);

    typingArea = new JTextField(20);
    typingArea.addKeyListener(this);

    //Uncomment this if you wish to turn off focus
    //traversal. The focus subsystem consumes
    //focus traversal keys, such as Tab and Shift Tab.
    //If you uncomment the following line of code, this
    //disables focus traversal and the Tab events will
    //become available to the key event listener.
    //typingArea.setFocusTraversalKeysEnabled(false);

    displayArea = new JTextArea();
    displayArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(displayArea);
    scrollPane.setPreferredSize(new Dimension(375, 125));

    add(typingArea, BorderLayout.PAGE_START);
    add(scrollPane, BorderLayout.CENTER);
    add(button, BorderLayout.PAGE_END);
  }

  /** Handle the key typed event from the text field. */
  public void keyTyped(KeyEvent e) {
    displayInfo(e, "KEY TYPED: ");
  }

  /** Handle the key pressed event from the text field. */
  public void keyPressed(KeyEvent e) {
    displayInfo(e, "KEY PRESSED: ");
  }

  /** Handle the key released event from the text field. */
  public void keyReleased(KeyEvent e) {
    displayInfo(e, "KEY RELEASED: ");
  }

  /** Handle the button click. */
  public void actionPerformed(ActionEvent e) {
    //Clear the text components.
    displayArea.setText("");
    typingArea.setText("");

    //Return the focus to the typing area.
    typingArea.requestFocusInWindow();
  }

  /*
   * We have to jump through some hoops to avoid trying to print non-printing
   * characters such as Shift. (Not only do they not print, but if you put
   * them in a String, the characters afterward won't show up in the text
   * area.)
   */
  protected void displayInfo(KeyEvent e, String s) {
    String keyString, modString, tmpString, actionString, locationString;

    //You should only rely on the key char if the event
    //is a key typed event.
    int id = e.getID();
    if (id == KeyEvent.KEY_TYPED) {
      char c = e.getKeyChar();
      keyString = "key character = '" + c + "'";
    } else {
      int keyCode = e.getKeyCode();
      keyString = "key code = " + keyCode + " ("
          + KeyEvent.getKeyText(keyCode) + ")";
    }

    int modifiers = e.getModifiersEx();
    modString = "modifiers = " + modifiers;
    tmpString = KeyEvent.getModifiersExText(modifiers);
    if (tmpString.length() > 0) {
      modString += " (" + tmpString + ")";
    } else {
      modString += " (no modifiers)";
    }

    actionString = "action key? ";
    if (e.isActionKey()) {
      actionString += "YES";
    } else {
      actionString += "NO";
    }

    locationString = "key location: ";
    int location = e.getKeyLocation();
    if (location == KeyEvent.KEY_LOCATION_STANDARD) {
      locationString += "standard";
    } else if (location == KeyEvent.KEY_LOCATION_LEFT) {
      locationString += "left";
    } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
      locationString += "right";
    } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
      locationString += "numpad";
    } else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
      locationString += "unknown";
    }

    displayArea.append(s + newline + "    " + keyString + newline + "    "
        + modString + newline + "    " + actionString + newline
        + "    " + locationString + newline);
    displayArea.setCaretPosition(displayArea.getDocument().getLength());
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be
   * invoked from the event-dispatching thread.
   */
  public static JFrame createAndShowGUI() {
    //Make sure we have nice window decorations.
    JFrame.setDefaultLookAndFeelDecorated(true);

    //Create and set up the window.
    JFrame frame = new JFrame("KeyEventDemo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Create and set up the content pane.
    JComponent newContentPane = new KeyEventDemo();
    newContentPane.setOpaque(true); //content panes must be opaque
    frame.setContentPane(newContentPane);

    //Display the window.
    frame.pack();
    frame.setVisible(true);
    return frame;
  }

  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }
}
