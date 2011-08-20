/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
#include "org_sikuli_script_internal_hotkey_MacHotkeyManager.h"

#include <Carbon/Carbon.h>
#include <CoreFoundation/CoreFoundation.h>

#include<iostream>
#include<vector>

using namespace std;

#define HOTKEY_LISTENER_METHOD "hotkeyPressed"
#define HOTKEY_LISTENER_SIGNATURE "(Lorg/sikuli/script/internal/hotkey/HotkeyEvent;)V"
#define HOTKEY_EVENT_CLASS "org/sikuli/script/internal/hotkey/HotkeyEvent"

struct CallbackData {
   JavaVM *vm;
   int hotkey, mods;
   jobject listener;
   EventHotKeyRef ref;
   EventHotKeyID id;

   CallbackData(JavaVM *vm_, int hotkey_, int mods_, jobject listener_){
      vm = vm_;
      hotkey = hotkey_;
      mods = mods_;
      listener = listener_;
   }
};

jobject CallbackDataToHotkeyEvent(JNIEnv* env, CallbackData* data){
   jclass clsHkEvent = env->FindClass(HOTKEY_EVENT_CLASS);
   jobject ret = env->AllocObject(clsHkEvent);
   jmethodID initMethod = env->GetMethodID(clsHkEvent, "init", "(II)V");
   env->CallVoidMethod(ret, initMethod, data->hotkey, data->mods);
   env->DeleteLocalRef(clsHkEvent);
   return ret;
}



void callJavaMethod(JavaVM *jvm, jobject listener, CallbackData* data){
   JNIEnv *env;
   jvm->GetEnv((void**)&env, JNI_VERSION_1_4);
   jvm->AttachCurrentThread((void **)&env, NULL);
   jclass cls = env->GetObjectClass(listener);
   jmethodID mid = env->GetMethodID(cls, HOTKEY_LISTENER_METHOD, HOTKEY_LISTENER_SIGNATURE); 
   if( mid == NULL ){
      cerr << "Callback method not found." << endl;
      return;
   }
   jobject hkEvent = CallbackDataToHotkeyEvent(env, data);
   env->CallVoidMethod(listener, mid, hkEvent);
}

static vector<CallbackData*> regHotkeys;
static bool firstTime = true;

OSStatus shortcutHandler( EventHandlerCallRef inCaller, EventRef inEvent, 
                          void* args )
{
   EventHotKeyID hkId;
   GetEventParameter(inEvent, kEventParamDirectObject, typeEventHotKeyID, NULL,
                     sizeof(hkId), NULL, &hkId);
   CallbackData *data = regHotkeys[hkId.id-1];
   int hotkey = data->hotkey;
   cout << "[JNI] shortcut pressed. " << hotkey << endl;
   callJavaMethod(data->vm, data->listener, data);
   return noErr;
}


bool installShortcutHandler( CallbackData *data ){
   EventTypeSpec shortcutEvents[] = {
      { kEventClassKeyboard, kEventHotKeyPressed },
   };
   bool registered = false; 

   vector<CallbackData*>::iterator it;
   for(it = regHotkeys.begin(); it != regHotkeys.end(); ++it){
      CallbackData *itdata = *it;
      if( itdata->hotkey == data->hotkey && itdata->mods == data->mods){
         registered = true;
         UnregisterEventHotKey(itdata->ref);
         data->id = itdata->id;
         data->ref = itdata->ref;
         break;
      }
   }
   
   if(!registered){
      data->id.id = regHotkeys.size()+1;
      data->id.signature='htk1';
      cout << "data: " << data->id.id << endl;
   }
   if(firstTime){
      firstTime = false;
      OSErr err = InstallApplicationEventHandler( &shortcutHandler,
        GetEventTypeCount(shortcutEvents), shortcutEvents, NULL, NULL);
      if (err != noErr)
         cerr << "InstallApplicationEventHandler failed" << endl;
   }

   OSStatus err = RegisterEventHotKey(data->hotkey, data->mods,
                      data->id, GetApplicationEventTarget(), 0, 
                      &(data->ref));
   if(err)
      return false;

   if(!registered)
      regHotkeys.push_back(data);
   return true;
}

JNIEXPORT jboolean JNICALL 
Java_org_sikuli_script_internal_hotkey_MacHotkeyManager_installGlobalHotkey 
(JNIEnv *env, jobject jobj, jint hotkey, jint modifiers, jobject listener){
   cout << "[JNI] install global hotkey: " << hotkey << " mod: " << modifiers << endl;
   JavaVM* vm = NULL;
   env->GetJavaVM(&vm);
   jobject gListener = env->NewGlobalRef(listener);
   env->DeleteLocalRef(listener);
   CallbackData *data = new CallbackData(vm, hotkey, modifiers, gListener);
   return installShortcutHandler(data);
}
