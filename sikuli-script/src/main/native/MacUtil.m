#import "edu_mit_csail_uid_MacUtil.h"
#import <Cocoa/Cocoa.h>
#import <JavaVM/jawt_md.h>

NSWindow * GetWindowFromComponent(jobject parent, JNIEnv *env) {
   JAWT awt;
   JAWT_DrawingSurface* ds;
   JAWT_DrawingSurfaceInfo* dsi;
   JAWT_MacOSXDrawingSurfaceInfo* dsi_mac;
   jboolean result;
   jint lock;

   // Get the AWT
   awt.version = JAWT_VERSION_1_4;
   if( (result = JAWT_GetAWT(env, &awt)) == JNI_FALSE){
      NSLog(@"AWT not found");   
   }

   // Get the drawing surface
   ds = awt.GetDrawingSurface(env, parent);
   if(ds == NULL)
      NSLog(@"no drawing surface");

   // Lock the drawing surface
   lock = ds->Lock(ds);
   if((lock & JAWT_LOCK_ERROR) != 0) {
      NSLog(@"error locking surface");
      awt.FreeDrawingSurface(ds);
      return NULL;
   }

   // Get the drawing surface info
   dsi = ds->GetDrawingSurfaceInfo(ds);
   //NSLog(@"drawing info %x", dsi);

   // Get the platform-specific drawing info
   dsi_mac = (JAWT_MacOSXDrawingSurfaceInfo*)dsi->platformInfo;
   //NSLog(@"mac drawing info %x", dsi_mac);

   // Get the NSView corresponding to the component that was passed
   NSView *view = dsi_mac->cocoaViewRef;

   // Free the drawing surface info
   ds->FreeDrawingSurfaceInfo(dsi);
   // Unlock the drawing surface
   ds->Unlock(ds);

   // Free the drawing surface
   awt.FreeDrawingSurface(ds);

   // Get the view's parent window; this is what we need to show a sheet
   return [view window];
}

JNIEXPORT void JNICALL Java_edu_mit_csail_uid_MacUtil_bringWindowToFront
  (JNIEnv *env, jobject jobj, jobject jwin){
  
   NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];

   NSWindow *win = GetWindowFromComponent(jwin, env);
   NSLog(@"bringWindowToFront: %@", win); 
   [win setIgnoresMouseEvents:YES];
   /*
   [win setBackgroundColor:[NSColor blackColor]];
   [win setOpaque:NO];
   [win setAlphaValue:0.60];
   */
   [win setLevel:NSScreenSaverWindowLevel];
   [win orderFront:nil];

   [pool release];
}
