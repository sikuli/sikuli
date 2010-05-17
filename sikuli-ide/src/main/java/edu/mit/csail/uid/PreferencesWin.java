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
      super( SikuliIDE._I("winPreferences") );
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
      _chkAutoUpdate.setSelected(pref.getCheckUpdate());
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
      pref.setCheckUpdate(_chkAutoUpdate.isSelected());
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
      JPanel paneGeneral = new JPanel();
      _chkAutoUpdate = new JCheckBox();
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
            lblHotkey.setText(SikuliIDE._I("prefCaptureHotkey"));

            //---- lblDelay ----
            lblDelay.setText(SikuliIDE._I("prefCaptureDelay"));

            //---- _spnDelay ----
            _spnDelay.setModel(new SpinnerNumberModel(1.0, 0.0, null, 0.1));

            //---- lblDelaySecs ----
            lblDelaySecs.setText(SikuliIDE._I("prefSeconds"));

            org.jdesktop.layout.GroupLayout paneCaptureLayout = 
               new org.jdesktop.layout.GroupLayout(paneCapture);
            paneCapture.setLayout(paneCaptureLayout);
            paneCaptureLayout.setHorizontalGroup(
               paneCaptureLayout.createParallelGroup()
                  .add(paneCaptureLayout.createSequentialGroup()
                     .add(26, 26, 26)
                     .add(paneCaptureLayout.createParallelGroup()
                        .add(lblHotkey)
                        .add(lblDelay))
                     .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                     .add(paneCaptureLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(paneCaptureLayout.createSequentialGroup()
                           .add(_spnDelay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                           .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                           .add(lblDelaySecs))
                        .add(_txtHotkey))
                     .add(50, 50, 50))
            );
            paneCaptureLayout.setVerticalGroup(
               paneCaptureLayout.createParallelGroup()
                  .add(paneCaptureLayout.createSequentialGroup()
                     .add(34, 34, 34)
                     .add(paneCaptureLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(lblHotkey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(_txtHotkey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                     .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                     .add(paneCaptureLayout.createParallelGroup()
                        .add(lblDelaySecs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                        .add(_spnDelay)
                        .add(lblDelay, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                     .add(150, 150, 150))
            );
         }
         _tabPane.addTab( SikuliIDE._I("prefTabScreenCapturing"), paneCapture);

         //======== paneGeneral ========
         {
            //---- _chkAutoUpdate ----
            _chkAutoUpdate.setText(SikuliIDE._I("prefGeneralAutoCheck"));

            org.jdesktop.layout.GroupLayout paneGeneralLayout = new org.jdesktop.layout.GroupLayout(paneGeneral);
            paneGeneral.setLayout(paneGeneralLayout);
            paneGeneralLayout.setHorizontalGroup(
               paneGeneralLayout.createParallelGroup()
                  .add(org.jdesktop.layout.GroupLayout.TRAILING, paneGeneralLayout.createSequentialGroup()
                     .add(85, 85, 85)
                     .add(_chkAutoUpdate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                     .add(82, 82, 82))
            );
            paneGeneralLayout.setVerticalGroup(
               paneGeneralLayout.createParallelGroup()
                  .add(paneGeneralLayout.createSequentialGroup()
                     .add(32, 32, 32)
                     .add(_chkAutoUpdate)
                     .addContainerGap(191, Short.MAX_VALUE))
            );
         }
         _tabPane.addTab( SikuliIDE._I("prefTabGeneralSettings"), paneGeneral);

      }
      contentPane.add(_tabPane, BorderLayout.CENTER);

      //======== paneOkCancel ========
      {
         paneOkCancel.setLayout(new BoxLayout(paneOkCancel, BoxLayout.X_AXIS));
         paneOkCancel.add(hSpacer1);

         //---- _btnOk ----
         _btnOk.setText(SikuliIDE._I("ok"));
         _btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               btnOkActionPerformed(e);
            }
         });
         paneOkCancel.add(_btnOk);

         //---- _btnCancel ----
         _btnCancel.setText(SikuliIDE._I("cancel"));
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
   private JCheckBox _chkAutoUpdate;
   private JButton _btnOk;
   private JButton _btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
