#include "edu_mit_csail_uid_Win32Util.h"
#include "windows.h"
#include <cstring>

using namespace std;


static bool strstr_i(char const *haystack, const char *pneedle){
   int count = 0;
   char buf[512];
   register char const *p;
   char *needle, *q, *foundto;
   if (!*pneedle) return true;
   if (!haystack) return false;

   needle = buf;
   p = pneedle; q = needle;
   
   while ((*q++ = tolower(*p++)) && count<512)
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


const char *gAppName;
static BOOL killWindow(HWND handle, long lParam){
   char buf[255];
   GetWindowText(handle, buf, 254);
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

static BOOL findWindow(HWND handle, long lParam){
   char buf[255];
   GetWindowText(handle, buf, 254);
   if( strstr_i(buf, gAppName) != NULL ){
      SetForegroundWindow(handle);
      return FALSE;
   }
   return TRUE;
}

JNIEXPORT jint JNICALL Java_edu_mit_csail_uid_Win32Util_switchApp(JNIEnv *env, jobject jobj, jstring jAppName){

   gAppName = env->GetStringUTFChars(jAppName, NULL);
   BOOL result = EnumWindows((WNDENUMPROC)findWindow, 0);
   env->ReleaseStringUTFChars(jAppName, gAppName);
   if( result != 0){ // switch failed. open it
		return Java_edu_mit_csail_uid_Win32Util_openApp(env, jobj, jAppName);
   }
   return result;
}

JNIEXPORT jint JNICALL Java_edu_mit_csail_uid_Win32Util_openApp(JNIEnv *env, jobject jobj, jstring jAppName){
   const char *appName = env->GetStringUTFChars(jAppName, NULL);
   int n = strlen(appName);
   char *buf = new char[n+3];
   strncpy(buf+1, appName, n);
   buf[0] = '"';
   buf[n+1] = '"';
   buf[n+2] = 0;
   int result = WinExec(buf, SW_SHOWNORMAL);
   delete [] buf;
   env->ReleaseStringUTFChars(jAppName, gAppName);
   if( result > 31 ) return 0;
   return -1;
}

JNIEXPORT jint JNICALL Java_edu_mit_csail_uid_Win32Util_closeApp(JNIEnv *env, jobject jobj, jstring jAppName){
   gAppName = env->GetStringUTFChars(jAppName, NULL);
   BOOL result = EnumWindows((WNDENUMPROC)killWindow, 0);
   env->ReleaseStringUTFChars(jAppName, gAppName);
   return result;
}
