
package org.sikuli.script;

import java.awt.Rectangle;
import java.io.IOException;
import com.wapmx.nativeutils.jniloader.NativeLoader;

public class VNCNative {
   static { 
      try {
         NativeLoader.loadLibrary("VNCNative");
      } catch(IOException e){
         e.printStackTrace();
      }
   }

   public static native long rfbGetClient(int 	bitsPerSample, int 	samplesPerPixel, int 	bytesPerPixel);
   public static native void rfbInitClient(long client,String[] args,String password);
   public static native int WaitForMessage(long client,int usecs);
   public static native boolean HandleRFBServerMessage(long client);
   public static native void CopyScreenToData(long client, int[] data,int x,int y,int width,int height);
   public static native int GetWidth(long client);
   public static native int GetHeight(long client);
   public static native boolean SendPointerEvent(long client, int x, int y, int buttonMask);
   public static native boolean SendKeyEvent(long client, int key, boolean down);
}

