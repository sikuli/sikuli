/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
#include "org_sikuli_script_Win32Util.h"
#include "windows.h"
#include <jawt.h>
#include <jawt_md.h>
#include <cstring>
#include <iostream>
#include <assert.h>

using namespace std;

const int BUF_SIZE = 128;

static bool strstr_i(char const *haystack, const char *pneedle){
   int count = 0;
   char buf[BUF_SIZE];
   register char const *p;
   char *needle, *q, *foundto;
   if (!*pneedle) return true;
   if (!haystack) return false;

   needle = buf;
   p = pneedle; q = needle;
   
   while ((*q++ = tolower(*p++)) && count<BUF_SIZE)
      count++;
   p = haystack - 1; foundto = needle;
   while (*++p) {
      if (tolower(*p) == *foundto) {
         if (!*++foundto) {
            return true;
         }
      } else foundto = needle;
   }
   return false;
}


static const char *gAppName = NULL;
static DWORD gPid;

static BOOL CALLBACK killWindowByPid(HWND handle, long lParam){
   DWORD pid;
   GetWindowThreadProcessId(handle, &pid);
   if(pid == gPid){
      HANDLE proc = OpenProcess(SYNCHRONIZE|PROCESS_TERMINATE, FALSE, pid);
      TerminateProcess(proc, 0);
      CloseHandle(proc);
      return FALSE;
   }
   return TRUE;
}

// pre-req: gAppName is set
static BOOL CALLBACK killWindowByAppName(HWND handle, long lParam){
   char buf[BUF_SIZE];
   GetWindowText(handle, buf, BUF_SIZE);
   if( strstr_i(buf, gAppName) != NULL ){
      DWORD pid;
      GetWindowThreadProcessId(handle, &pid);
      HANDLE proc = OpenProcess(SYNCHRONIZE|PROCESS_TERMINATE, FALSE, pid);
      TerminateProcess(proc, 0);
      CloseHandle(proc);
      return FALSE;
   }
   return TRUE;
}

static int gWinNum, gWinCount;

// pre-req: gAppName, gWinNum, gWinCount = 0 
// output: gPid of the found window
static BOOL CALLBACK focusWinByAppName(HWND handle, long lParam){
   char filename[BUF_SIZE], title[BUF_SIZE] ;
   GetWindowModuleFileName(handle, filename, BUF_SIZE);
   GetWindowText(handle, title, BUF_SIZE);
   if( strstr_i(filename, gAppName) != NULL || strstr_i(title, gAppName) != NULL){
      if(gWinCount == gWinNum){
         SetForegroundWindow(handle);
         GetWindowThreadProcessId(handle, &gPid);
         return FALSE;
      }
      gWinCount++;
   }
   return TRUE;
}

static BOOL CALLBACK focusWinByPid(HWND handle, long lParam){
   DWORD pid;
   GetWindowThreadProcessId(handle, &pid);
   if( pid == gPid){
      if(gWinCount == gWinNum){
         SetForegroundWindow(handle);
         return FALSE;
      }
      gWinCount++;
   }
   return TRUE;
}



static HWND gFoundHandle;
static BOOL CALLBACK findWindowHandleByAppName(HWND handle, long lParam){
   char buf[BUF_SIZE];
   GetWindowText(handle, buf, BUF_SIZE);
   //fprintf(stderr,"win: %s\n", buf);
   if( strstr_i(buf, gAppName) != NULL ){
      if(gWinCount == gWinNum){
         gFoundHandle = handle;
         return FALSE;
      }
      gWinCount++;
   }
   return TRUE;
}

static BOOL CALLBACK findWindowHandleByPid(HWND handle, long lParam){
   DWORD pid;
   GetWindowThreadProcessId(handle, &pid);
   if( pid == gPid ){
      if(gWinCount == gWinNum){
         gFoundHandle = handle;
         return FALSE;
      }
      gWinCount++;
   }
   return TRUE;
}

JNIEXPORT jint JNICALL Java_org_sikuli_script_Win32Util_switchApp__Ljava_lang_String_2I (JNIEnv *env, jobject jobj, jstring jAppName, jint jWinNum){

   gAppName = env->GetStringUTFChars(jAppName, NULL);
   gWinNum = jWinNum;
   gWinCount = 0;
   BOOL result = EnumWindows((WNDENUMPROC)focusWinByAppName, 0);
   env->ReleaseStringUTFChars(jAppName, gAppName);
   if(result != 0){ // switch failed. open it
      return Java_org_sikuli_script_Win32Util_openApp(env, jobj, jAppName);
   }
   return gPid;
}

JNIEXPORT jint JNICALL Java_org_sikuli_script_Win32Util_switchApp__II(JNIEnv *env, jobject jobj, jint jPid, jint jWinNum){
   gPid = jPid;
   gWinNum = jWinNum;
   gWinCount = 0;
   BOOL result = EnumWindows((WNDENUMPROC)focusWinByPid, 0);
   if(result != 0)
      return 0;
   return jPid;
}

//returns PID, or 0 if failed.
JNIEXPORT jint JNICALL Java_org_sikuli_script_Win32Util_openApp(JNIEnv *env, jobject jobj, jstring jAppName){
   char *appName = (char *)env->GetStringUTFChars(jAppName, NULL);
   int n = strlen(appName);
   char *buf = new char[n+3];
   STARTUPINFO si;
   PROCESS_INFORMATION pi;
   ZeroMemory(&si, sizeof(si));
   ZeroMemory(&pi, sizeof(pi));
   si.cb = sizeof(si);
   int result = CreateProcess( NULL,
     appName, NULL, NULL, FALSE, 0, 
     NULL, NULL, &si, &pi);

   env->ReleaseStringUTFChars(jAppName, appName);

   if(!result) // CreateProcess failed
      return 0;
   return pi.dwProcessId;
}

JNIEXPORT jint JNICALL Java_org_sikuli_script_Win32Util_closeApp__I
  (JNIEnv *env, jobject jobj, jint jPid){
  
   gPid = jPid;
   BOOL result = EnumWindows((WNDENUMPROC)killWindowByPid, 0);
   if(result!=0)
      return -1;
   return 0;
}

JNIEXPORT jint JNICALL Java_org_sikuli_script_Win32Util_closeApp__Ljava_lang_String_2(JNIEnv *env, jobject jobj, jstring jAppName){
   gAppName = env->GetStringUTFChars(jAppName, NULL);
   BOOL result = EnumWindows((WNDENUMPROC)killWindowByAppName, 0);
   env->ReleaseStringUTFChars(jAppName, gAppName);
   if(result!=0)
      return -1;
   return 0;
}


HWND getHwndFromComponent(jobject parent, JNIEnv *env) {
   JAWT awt;
   JAWT_DrawingSurface* ds;
   JAWT_DrawingSurfaceInfo* dsi;
   JAWT_Win32DrawingSurfaceInfo* dsi_win;
   jboolean result;
   jint lock;
   HWND hwnd;
   
   // Get the AWT
   awt.version = JAWT_VERSION_1_4;
   if( (result = JAWT_GetAWT(env, &awt)) == JNI_FALSE){
      fprintf(stderr, "AWT not found\n");
   }

   // Get the drawing surface
   ds = awt.GetDrawingSurface(env, parent);
   if(ds == NULL)
      fprintf(stderr, "no drawing surface\n");

   // Lock the drawing surface
   lock = ds->Lock(ds);
   if((lock & JAWT_LOCK_ERROR) != 0) {
      fprintf(stderr, "error locking surface\n");
      awt.FreeDrawingSurface(ds);
      return NULL;
   }

   // Get the drawing surface info
   dsi = ds->GetDrawingSurfaceInfo(ds);
   //NSLog(@"drawing info %x", dsi);

   // Get the platform-specific drawing info
   dsi_win = (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;

   hwnd = dsi_win->hwnd;

   // Free the drawing surface info
   ds->FreeDrawingSurfaceInfo(dsi);
   // Unlock the drawing surface
   ds->Unlock(ds);

   // Free the drawing surface
   awt.FreeDrawingSurface(ds);

   return hwnd;
}

void setLayeredAndTransparent( HWND windowHandle )
{
HWND hwnd = ( HWND )windowHandle;
LONG_PTR winLong = GetWindowLongPtr( hwnd, GWL_EXSTYLE );
// Set layered and transparent
winLong = winLong | WS_EX_LAYERED | WS_EX_TRANSPARENT;

LONG_PTR previousValue = SetWindowLongPtr( hwnd, GWL_EXSTYLE, winLong );

if ( previousValue == 0 ){
   DWORD ErrorCode = GetLastError();
   char ErrorBuff[200];

   FormatMessage(FORMAT_MESSAGE_FROM_SYSTEM, NULL,
     ErrorCode, 0, ErrorBuff, 200, NULL);
   printf("\t !!!! In setLayeredAndTransparentAndTakeOffTaskbar: \n\t SetWindowLong failed. Returned value == %d \n", previousValue );
   printf("\t GetLastErrpr == %s \n", ErrorBuff);
   fflush( stdout );
}
}

void takeWindowOffTaskbar( HWND windowHandle ){
   LONG_PTR winLong = GetWindowLongPtr( (HWND) windowHandle, GWL_EXSTYLE );
   winLong = winLong & (~WS_EX_APPWINDOW);
   winLong = winLong | WS_EX_TOOLWINDOW;
   LONG_PTR previousValue = SetWindowLongPtr( (HWND) windowHandle, GWL_EXSTYLE, winLong );
}

void setTopMost( HWND windowHandle ){
   RECT rect;
   GetWindowRect( (HWND) windowHandle, &rect );
   SetWindowPos(
     (HWND) windowHandle,
     HWND_TOPMOST,
     rect.left,
     rect.top,
     rect.right - rect.left,
     rect.bottom - rect.top,
     SWP_NOACTIVATE | SWP_SHOWWINDOW | SWP_NOMOVE | SWP_NOSIZE );
}


void makeClickThrough( HWND windowHandle ){
   setTopMost( windowHandle );
   HRESULT hr = SetLayeredWindowAttributes(
     (HWND) windowHandle,
     RGB(0,0,0),
     // RGB(255,255,255),
     100, // full transparency
     //255, // no transparency
//     LWA_ALPHA );
     LWA_COLORKEY );
}

JNIEXPORT void JNICALL Java_org_sikuli_script_Win32Util_bringWindowToFront
  (JNIEnv *env, jclass jobj, jobject jwin, jboolean jIgnoreMouse){
   
   HWND hwnd = getHwndFromComponent(jwin, env);
   setLayeredAndTransparent(hwnd);
   //takeWindowOffTaskbar(hwnd);
   if(jIgnoreMouse)
      makeClickThrough(hwnd);

}


JNIEXPORT jlong JNICALL Java_org_sikuli_script_Win32Util_getHwnd__II
  (JNIEnv *env, jclass jobj, jint jPid, jint jWinNum){
   gPid = jPid;
   gWinNum = jWinNum;
   gWinCount = 0;
   BOOL result = EnumWindows((WNDENUMPROC)findWindowHandleByPid, 0);
   if( result != 0){ // failed
      return 0;
   }
   return (jlong)gFoundHandle;
  
}


JNIEXPORT jlong JNICALL Java_org_sikuli_script_Win32Util_getHwnd__Ljava_lang_String_2I
  (JNIEnv *env, jclass jobj, jstring jAppName, jint jWinNum){
   gAppName = env->GetStringUTFChars(jAppName, NULL);
   gWinNum = jWinNum;
   gWinCount = 0;
   BOOL result = EnumWindows((WNDENUMPROC)findWindowHandleByAppName, 0);
   env->ReleaseStringUTFChars(jAppName, gAppName);
   if( result != 0){ // failed
      return 0;
   }
   //fprintf(stderr, "getHwnd: %d\n", (long)gFoundHandle);
   return (jlong)gFoundHandle;
  }


#define CLASS_RECTANGLE "java/awt/Rectangle"

jobject convertRectToJRectangle(JNIEnv *env, const RECT& r){
   jclass rectangleClass = env->FindClass(CLASS_RECTANGLE);
   assert(rectangleClass != NULL);
   jmethodID constructor = env->GetMethodID(rectangleClass, "<init>", "(IIII)V");
   jobject rectangle = env->NewObject(rectangleClass, constructor, (int)r.left, (int)r.top,
                                                                   (int)r.right-r.left, 
                                                                   (int)r.bottom-r.top);
   env->DeleteLocalRef(rectangleClass);
   return rectangle;
}

/*
 * Class:     org_sikuli_script_Win32Util
 * Method:    getRegion
 * Signature: (JI)Ljava/awt/Rectangle;
 */
JNIEXPORT jobject JNICALL Java_org_sikuli_script_Win32Util_getRegion
  (JNIEnv *env, jclass jobj, jlong jHwnd, jint jWinNum){
     RECT rect;
     HWND hwnd = (HWND)jHwnd;
     if(GetWindowRect(hwnd, &rect)){
        //fprintf(stderr, "rect: %d %d %d %d\n", rect.left, rect.top, rect.right, rect.bottom);
        return convertRectToJRectangle(env, rect);
     }
     return NULL;
  }

/*
 * Class:     org_sikuli_script_Win32Util
 * Method:    getFocusedRegion
 * Signature: ()Ljava/awt/Rectangle;
 */
JNIEXPORT jobject JNICALL Java_org_sikuli_script_Win32Util_getFocusedRegion
  (JNIEnv *env, jclass jobj){
     RECT rect;
     HWND hwnd = GetForegroundWindow();
     if(GetWindowRect(hwnd, &rect)){
        return convertRectToJRectangle(env, rect);
     }
     return NULL;
  }

