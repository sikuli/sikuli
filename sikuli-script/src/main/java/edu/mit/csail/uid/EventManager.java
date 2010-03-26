package edu.mit.csail.uid;

import java.awt.*;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import com.wapmx.nativeutils.jniloader.NativeLoader;

public class EventManager {
   final static int APPEAR = 0;
   final static int VANISH = 1;
   final static int CHANGE = 2;
   private static EventManager _instance = null;
   private long _c_instance = 0;

   static {
      try{
         NativeLoader.loadLibrary("ScreenMatchProxy");
         System.out.println("ScreenMatchProxy loaded.");
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   public static EventManager getInstance(){
      if(_instance==null)
         _instance = new EventManager();
      return _instance;
   }

   protected EventManager(){
   }

   public <PSC> void addAppearObserver(PSC ptn, Region region, 
                                       SikuliEventObserver ob){
   
   }

   public <PSC> void addVanishObserver(PSC ptn, Region region, 
                                       SikuliEventObserver ob){
   
   }

   public <PSC> void addChangeObserver(PSC ptn, Region region, 
                                       SikuliEventObserver ob){
   
   }

   private native long createEventManager(); 
   private native void addObserver(long sem_instance, 
                                   int evt_type, String target_image_filename,
                                   int handler_id, int x, int y, int w, int h); 

   private native void _update(long sem_instance, 
                               byte[] screenImage, int w, int h);

}


