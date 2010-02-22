package edu.mit.csail.uid;

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

   protected File open(String msg, int mode, GeneralFileFilter[] filters){
      FileDialog fd = new FileDialog(_parent, msg, mode);
      for(GeneralFileFilter filter: filters)
         fd.setFilenameFilter(filter);
      fd.setVisible(true);
      if(fd.getFile() == null)
         return null;
      return new File(fd.getDirectory(), fd.getFile());
   }

   protected File openWithSwingDialog(String msg, int mode, GeneralFileFilter[] filters){
      JFileChooser fcLoad = new JFileChooser();
      //fcLoad.setCurrentDirectory(new File(System.getProperty("user.dir")));
      fcLoad.setAcceptAllFileFilterUsed(false);
      for(GeneralFileFilter filter: filters)
         fcLoad.setFileFilter(filter);
      fcLoad.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
      fcLoad.setSelectedFile(null);
      if(fcLoad.showDialog(_parent, null) != JFileChooser.APPROVE_OPTION)
         return null;
      return fcLoad.getSelectedFile();
   }

   public File loadImage(){
      return open("Open a Image File", FileDialog.LOAD,
         new GeneralFileFilter[]{
             new GeneralFileFilter("png","PNG Image Files (*.png)")
      });
   }

   public File load(){
      return open("Open a Sikuli Source File", FileDialog.LOAD,
         new GeneralFileFilter[]{
             new GeneralFileFilter("sikuli","Sikuli source files (*.sikuli)")
      });
   }

   public File save(){
      return open("Save a Sikuli Source File", FileDialog.SAVE,
         new GeneralFileFilter[]{
             new GeneralFileFilter("sikuli","Sikuli source files (*.sikuli)")
      });
   }

   public File export(){
      return open("Export a Sikuli Executable File", FileDialog.SAVE,
         new GeneralFileFilter[]{
             new GeneralFileFilter("skl","Sikuli executable files (*.skl)")
      });
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
