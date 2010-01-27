package edu.mit.csail.uid;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.prefs.*;
import org.jdesktop.layout.*;

/*
 * Created by JFormDesigner on Mon Nov 16 10:13:52 EST 2009
 */



/**
 * @author Sean Chang
 */
public class PreferencesWin extends JFrame {
   UserPreferences pref = UserPreferences.getInstance();
   int _cap_hkey, _cap_mod;
   int _old_cap_hkey, _old_cap_mod;

	public PreferencesWin() {
		initComponents();
      loadPrefs();
	}

   private void loadPrefs(){
      SikuliIDE ide = SikuliIDE.getInstance();
      double delay = pref.getCaptureDelay();
      _spnDelay.setValue(delay);
      _old_cap_hkey = _cap_hkey = pref.getCaptureHotkey();
      _old_cap_mod = _cap_mod = pref.getCaptureHotkeyModifiers();
      setTxtHotkey(_cap_hkey, _cap_mod);
   }

   private void savePrefs(){
      SikuliIDE ide = SikuliIDE.getInstance();
      pref.setCaptureDelay((Double)_spnDelay.getValue());
      pref.setCaptureHotkey(_cap_hkey);
      pref.setCaptureHotkeyModifiers(_cap_mod);
      if(_old_cap_hkey != _cap_hkey || _old_cap_mod != _cap_mod){
         //FIXME: remove the old hotkey
         ide.installCaptureHotkey(_cap_hkey, _cap_mod);
      }
   }

   private void setTxtHotkey(int code, int mod){
      _cap_hkey = code;
      _cap_mod = mod;
      _txtHotkey.setText( Utils.convertKeyToText(code, mod) );
   }

   private void btnOkActionPerformed(ActionEvent e) {
      savePrefs();
      this.dispose();
   }

   private void btnCancelActionPerformed(ActionEvent e) {
      this.dispose();
   }

   private void txtHotkeyFocusGained(FocusEvent e) {
      _txtHotkey.setEditable(true);
   }

   private void txtHotkeyKeyPressed(KeyEvent e) {
      int code = e.getKeyCode();
      int mod = e.getModifiers();
      Debug.log(7, "" + code + " " + mod);
      setTxtHotkey(code, mod);
      _txtHotkey.setEditable(false);
   }

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
      // Generated using JFormDesigner Evaluation license - Sean Chang
      _tabPane = new JTabbedPane();
      JPanel paneCapture = new JPanel();
      _txtHotkey = new JTextField();
      JLabel lblHotkey = new JLabel();
      JLabel lblDelay = new JLabel();
      _spnDelay = new JSpinner();
      JLabel lblDelaySecs = new JLabel();
      JPanel paneOkCancel = new JPanel();
      JPanel hSpacer1 = new JPanel(null);
      _btnOk = new JButton();
      _btnCancel = new JButton();

      //======== this ========
      Container contentPane = getContentPane();
      contentPane.setLayout(new BorderLayout());

      //======== _tabPane ========
      {

         //======== paneCapture ========
         {

            // JFormDesigner evaluation mark
            /*
            paneCapture.setBorder(new javax.swing.border.CompoundBorder(
               new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                  "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                  javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                  java.awt.Color.red), paneCapture.getBorder())); paneCapture.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});
                  */


            //---- _txtHotkey ----
            _txtHotkey.setHorizontalAlignment(SwingConstants.RIGHT);
            _txtHotkey.addFocusListener(new FocusAdapter() {
               @Override
               public void focusGained(FocusEvent e) {
                  txtHotkeyFocusGained(e);
               }
            });
            _txtHotkey.addKeyListener(new KeyAdapter() {
               @Override
               public void keyPressed(KeyEvent e) {
                  txtHotkeyKeyPressed(e);
               }
            });

            //---- lblHotkey ----
            lblHotkey.setText("Quick Capture hotkey:");

            //---- lblDelay ----
            lblDelay.setText("Capture delay:");

            //---- _spnDelay ----
            _spnDelay.setModel(new SpinnerNumberModel(1.0, 0.0, null, 0.1));

            //---- lblDelaySecs ----
            lblDelaySecs.setText("seconds");

            GroupLayout paneCaptureLayout = new GroupLayout(paneCapture);
            paneCapture.setLayout(paneCaptureLayout);
            paneCaptureLayout.setHorizontalGroup(
               paneCaptureLayout.createParallelGroup()
                  .add(paneCaptureLayout.createSequentialGroup()
                     .add(26, 26, 26)
                     .add(paneCaptureLayout.createParallelGroup()
                        .add(lblHotkey)
                        .add(lblDelay))
                     .addPreferredGap(LayoutStyle.RELATED)
                     .add(paneCaptureLayout.createParallelGroup(GroupLayout.LEADING, false)
                        .add(paneCaptureLayout.createSequentialGroup()
                           .add(_spnDelay, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                           .addPreferredGap(LayoutStyle.RELATED)
                           .add(lblDelaySecs))
                        .add(_txtHotkey))
                     .add(50, 50, 50))
            );
            paneCaptureLayout.setVerticalGroup(
               paneCaptureLayout.createParallelGroup()
                  .add(paneCaptureLayout.createSequentialGroup()
                     .add(34, 34, 34)
                     .add(paneCaptureLayout.createParallelGroup(GroupLayout.BASELINE)
                        .add(lblHotkey, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                        .add(_txtHotkey, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                     .addPreferredGap(LayoutStyle.RELATED)
                     .add(paneCaptureLayout.createParallelGroup()
                        .add(lblDelaySecs, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                        .add(_spnDelay)
                        .add(lblDelay, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                     .add(150, 150, 150))
            );
         }
         _tabPane.addTab("Screen Capturing", paneCapture);

      }
      contentPane.add(_tabPane, BorderLayout.CENTER);

      //======== paneOkCancel ========
      {
         paneOkCancel.setLayout(new BoxLayout(paneOkCancel, BoxLayout.X_AXIS));
         paneOkCancel.add(hSpacer1);

         //---- _btnOk ----
         _btnOk.setText("OK");
         _btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               btnOkActionPerformed(e);
            }
         });
         paneOkCancel.add(_btnOk);

         //---- _btnCancel ----
         _btnCancel.setText("Cancel");
         _btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               btnCancelActionPerformed(e);
            }
         });
         paneOkCancel.add(_btnCancel);
      }
      contentPane.add(paneOkCancel, BorderLayout.SOUTH);
      pack();
      setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
   // Generated using JFormDesigner Evaluation license - Sean Chang
   private JTabbedPane _tabPane;
   private JTextField _txtHotkey;
   private JSpinner _spnDelay;
   private JButton _btnOk;
   private JButton _btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
