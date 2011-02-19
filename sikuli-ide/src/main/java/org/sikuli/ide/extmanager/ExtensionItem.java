/**
 * 
 */
package org.sikuli.ide.extmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MouseInputListener;

import org.sikuli.ide.I18N;
import org.sikuli.script.Debug;
import org.sikuli.script.ExtensionManager;

class ExtensionItem extends JPanel implements ActionListener {
   
   static public boolean isInstalled(String name){
      ExtensionManager extMgr = ExtensionManager.getInstance();
      if(extMgr.isInstalled(name))
         return true;
      return false;
   }
   
   
   JButton _installCtrl;
   JButton _infoCtrl;
   
   String _name;
   String _infourl;
   String _jarurl;
   String _version;
   String _description;
   boolean _installed;
   
   final int NOT_INSTALLED = 0;
   final int INSTALLED = 1;
   final int OUT_OF_DATE = 2;
   int _status = NOT_INSTALLED;
   
   JPanel _controls;
   JPanel _content;
   JLabel _htmlLabel;
   public ExtensionItem(String name, String version, String description, 
         String imgurl, String infourl, String jarurl){
      this._name = name;
      this._version = version;
      this._infourl = infourl;
      this._infourl = infourl;
      this._jarurl = jarurl;
      this._description = description;
      this._status = getStatus();

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      
      Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
      setBorder(loweredetched);

            
      _content = new JPanel();
      
      
      Image image = null;
      try {
          // Read from a URL
          URL url = new URL(imgurl);
          image = ImageIO.read(url);
      } catch (IOException e) {
      }

      JLabel iconLabel = new JLabel();
      iconLabel.setIcon(new ImageIcon(image));
      iconLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

      _content.setLayout(new BorderLayout(5, 5));
      _content.add(iconLabel, BorderLayout.LINE_START);
      
      
      
      _htmlLabel = new JLabel(renderHTML());
      _content.add(_htmlLabel);
      
      add(_content);
      
      JButton btn = new JButton(I18N._I("extBtnInstall"));
      btn.addActionListener(this);
      btn.setActionCommand("Install"); 
      _installCtrl = btn;
      _installCtrl.setFocusable(false);
      
      btn = new JButton(I18N._I("extBtnInfo"));
      btn.addActionListener(this);
      btn.setActionCommand("Info");
      _infoCtrl = btn;
      _infoCtrl.setFocusable(false);

      
      //setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
      _controls = new JPanel();
      _controls.setLayout(new BorderLayout(5, 5));

      _controls.add(_infoCtrl,BorderLayout.LINE_START);
      _controls.add(_installCtrl,BorderLayout.LINE_END);
      
      
      add(_controls);
      
      _controls.setVisible(false);
      updateControls();
      
      
      addMouseListener(new MouseAdapter(){
         
         public void mousePressed(MouseEvent e){
            
            ExtensionManagerFrame.getInstance().select((ExtensionItem)e.getSource());
         }
      });
   }
   
   
   public void setSelected(boolean selected){
      _controls.setVisible(selected);
      /*
      
      
      Color darkRed = new Color(0.5f,0.0f,0.0f);
      
      Color bg,fg;
      if (selected){
         bg = darkRed;//Color.red;
         fg = Color.white;
      }else{
         bg = null;
         fg = Color.black;
      }
      _controls.setBackground(bg);
      _content.setBackground(bg);
      
      for (Component comp : _content.getComponents()){
         comp.setForeground(fg);
      }
      */
      
   }
   
   
   public String renderHTML(){      
      String installed_version =  ExtensionManager.getInstance().getVersion(_name);
      if (installed_version == null)
         installed_version = "Not installed";
      return "<html><div style='width:300px'><b>" + _name + "</b> " + "(" + installed_version + ")" + "<br>"
         + _description + "</div></html>";
   }
   
   
   public int getStatus(){
      
      ExtensionManager extMgr = ExtensionManager.getInstance();
      
      if (!extMgr.isInstalled(_name)){
         return NOT_INSTALLED;          
      }else if (extMgr.isOutOfDate(_name,_version)){
         return OUT_OF_DATE;        
      }else {
         return INSTALLED;
      }
      
   }
   
   public void updateControls(){
      
      int status = getStatus();
      
      if (status == INSTALLED){
         _installCtrl.setEnabled(false);
         _installCtrl.setText(I18N._I("extMsgInstalled"));
      }else if (status == NOT_INSTALLED){
         _installCtrl.setEnabled(true);
         _installCtrl.setText(I18N._I("extBtnInstallVer",_version));
      }else if (status == OUT_OF_DATE){
         _installCtrl.setEnabled(true);
         _installCtrl.setText(I18N._I("extBtnUpdateVer",_version));
      }
      
      _htmlLabel.setText(renderHTML());
   }
   

   static void openURL(String url){
      try{
         URL u = new URL(url);
         java.awt.Desktop.getDesktop().browse(u.toURI());
      }
      catch(Exception ex){
         ex.printStackTrace();
      }
   }
   
   
   @Override
   public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      if (cmd.equals("Install")){
         Debug.log("Installing " + _name + " at " + _jarurl);
         
         ExtensionManager extMgr = ExtensionManager.getInstance();

         // try to install the extension
         if (extMgr.install(_name, _jarurl,_version)){
            
            // if successful, change the item's status to installed
            //_installed = true;
            updateControls();
         }
         
      }else if (cmd.equals("Info")){
         
         Debug.log("Openning URL: " + _infourl);   
         openURL(_infourl);
      }
      
   }
}
