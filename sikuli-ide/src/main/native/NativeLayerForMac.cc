#include "org_sikuli_ide_NativeLayerForMac.h"

#include <Carbon/Carbon.h>
#include <CoreFoundation/CoreFoundation.h>

#include<iostream>
#include<map>

using namespace std;

struct CallbackData {
   JavaVM *vm;
   int hotkey, mods;
   const char *func, *func_t;
   jobject cls;

   CallbackData(JavaVM *vm_, int hotkey_, int mods_, const char* func_, 
                const char* func_t_, jobject cls_){
      vm = vm_;
      hotkey = hotkey_;
      mods = mods_;
      func = func_;
      func_t = func_t_;
      cls = cls_;
   }
};


void callJavaMethod(JavaVM *jvm, jobject obj, 
                    const char* func, const char* func_t){
   JNIEnv *env;
   jvm->GetEnv((void**)&env, JNI_VERSION_1_4);
   jvm->AttachCurrentThread((void **)&env, NULL);
   jclass cls = env->GetObjectClass(obj);
   jmethodID mid = env->GetMethodID(cls, func, func_t);
   if( mid == NULL ){
      cerr << "Callback method " << func << " not found." << endl;
      return;
   }
   env->CallVoidMethod(obj, mid);
}

OSStatus shortcutHandler( EventHandlerCallRef inCaller, EventRef inEvent, 
                          void* inIdDataMap )
{
   EventHotKeyID hkId;
   GetEventParameter(inEvent, kEventParamDirectObject, typeEventHotKeyID, NULL,
                     sizeof(hkId), NULL, &hkId);
   map<int,CallbackData*> 
      *idDataMap = reinterpret_cast<map<int,CallbackData*>*>(inIdDataMap);
   CallbackData *data = (*idDataMap)[hkId.id];
   int hotkey = data->hotkey;
   cout << "[JNI] shortcut pressed. " << hotkey << endl;
   callJavaMethod(data->vm, data->cls, data->func, data->func_t);
   return noErr;
}

void installShortcutHandler( CallbackData *data ){
   static map<string, int> callbackIdMap;
   static map<int, CallbackData*> idDataMap;
   static map<string, EventHotKeyRef> callbackRefMap;
   EventTypeSpec shortcutEvents[] = {
      { kEventClassKeyboard, kEventHotKeyPressed },
   };
   EventHotKeyRef myHotKeyRef;
   EventHotKeyID myHotKeyID;
   myHotKeyID.signature='htk1';
   map<string, int>::iterator it;
   if( (it=callbackIdMap.find(data->func)) == callbackIdMap.end() ){
      myHotKeyID.id = callbackIdMap.size()+1;
      callbackIdMap[data->func] = myHotKeyID.id;
      cout << "[JNI] " << data->func << " id: " << myHotKeyID.id << endl;
   }
   else{
      myHotKeyID.id = it->second;
      myHotKeyRef = callbackRefMap[data->func];
      UnregisterEventHotKey(myHotKeyRef);
   }
   idDataMap[myHotKeyID.id] = data;

   if(callbackIdMap.size() == 1){
      InstallApplicationEventHandler(&shortcutHandler, 1, shortcutEvents,
                                     &idDataMap, NULL);
   }

   OSStatus err = RegisterEventHotKey(data->hotkey, data->mods,
                      myHotKeyID, GetApplicationEventTarget(), 0, 
                      &myHotKeyRef);
   
   if(err)
      cerr << "Error registering shortcut handler.. " << err << endl;
   else
      callbackRefMap[data->func] = myHotKeyRef;
}

JNIEXPORT void JNICALL Java_org_sikuli_ide_NativeLayerForMac_installGlobalHotkey (JNIEnv *env, jobject jobj, jint hotkey, jint modifiers, jobject jIde, 
       jstring jCallbackName, jstring jCallbackType){

   cout << "[JNI] install global hotkey: " << hotkey << endl;
   const char *cName = env->GetStringUTFChars(jCallbackName, NULL);
   const char *cType = env->GetStringUTFChars(jCallbackType, NULL);
   JavaVM* vm = NULL;
   env->GetJavaVM(&vm);
   jobject ide = env->NewGlobalRef(jIde);
   env->DeleteLocalRef(jIde);
   CallbackData *data = new CallbackData(vm, hotkey, modifiers, 
                                         cName, cType, ide);
   installShortcutHandler(data);
}
