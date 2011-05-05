/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

import java.io.File;
import java.io.FilenameFilter;
import java.awt.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;

public class FileChooser {
   Frame _parent;

   public FileChooser(Frame parent){
      _parent = parent;
   }

   protected File open(String msg, int mode, GeneralFileFilter[] filters,int selectionMode){
      if( Utils.isMacOSX() ){
         FileDialog fd = new FileDialog(_parent, msg, mode);
         for(GeneralFileFilter filter: filters)
            fd.setFilenameFilter(filter);
         fd.setVisible(true);
         if(fd.getFile() == null)
            return null;
         return new File(fd.getDirectory(), fd.getFile());
      }	
      return openWithSwingDialog(msg, mode, filters, selectionMode);
   }

   protected File openWithSwingDialog(String msg, int mode, GeneralFileFilter[] filters,int selectionMode){
      JFileChooser fchooser = new JFileChooser();
      if(mode==FileDialog.SAVE) {
         fchooser.setDialogType(JFileChooser.SAVE_DIALOG);
      }
      UserPreferences pref = UserPreferences.getInstance();
      String last_dir = pref.get("LAST_OPEN_DIR", "");
      if(!last_dir.equals(""))
         fchooser.setCurrentDirectory(new File(last_dir));
      fchooser.setAcceptAllFileFilterUsed(false);
      for(GeneralFileFilter filter: filters)
         fchooser.setFileFilter(filter);
      fchooser.setFileSelectionMode(selectionMode);
      fchooser.setSelectedFile(null);
      if(fchooser.showDialog(_parent, null) != JFileChooser.APPROVE_OPTION)
         return null;
      File ret = fchooser.getSelectedFile();
      String dir = ret.getParent();
      UserPreferences.getInstance().put("LAST_OPEN_DIR",dir);
      return ret;
   }

   public File loadImage(){
      return open("Open a Image File", FileDialog.LOAD,
         new GeneralFileFilter[]{
             new GeneralFileFilter("png","PNG Image Files (*.png)")
      },JFileChooser.FILES_ONLY);
   }

   public File load(){
      return open("Open a Sikuli Source File", FileDialog.LOAD,
         new GeneralFileFilter[]{
             new GeneralFileFilter("sikuli","Sikuli source files (*.sikuli)")
      },JFileChooser.DIRECTORIES_ONLY);
   }

   public File save(){
      return open("Save a Sikuli Source File", FileDialog.SAVE,
         new GeneralFileFilter[]{
             new GeneralFileFilter("sikuli","Sikuli source files (*.sikuli)")
      },JFileChooser.DIRECTORIES_ONLY);
   }

   public File export(){
      return open("Export a Sikuli Executable File", FileDialog.SAVE,
         new GeneralFileFilter[]{
             new GeneralFileFilter("skl","Sikuli executable files (*.skl)")
      },JFileChooser.FILES_ONLY);
   }

}

class GeneralFileFilter extends FileFilter implements FilenameFilter{
   private String _ext, _desc;
   public GeneralFileFilter(String ext, String desc){
      _ext = ext;
      _desc = desc;
   }

   public boolean accept(File dir, String fname)
   {
      int i = fname.lastIndexOf('.');
      if (i > 0 && i < fname.length()-1){
         String ext = fname.substring(i+1).toLowerCase();
         if(ext.equals(_ext) )
            return true;
      }
      return false;
   }

   public boolean accept(File f)
   {
      if (f.isDirectory()) return true;

      String s = f.getName();
      int i = s.lastIndexOf('.');
      if (i > 0 && i < s.length()-1){
         String ext = s.substring(i+1).toLowerCase();
         if(ext.equals(_ext) )
            return true;
      }
      return false;
   }
   public String getDescription(){
      return _desc;
   }
}
