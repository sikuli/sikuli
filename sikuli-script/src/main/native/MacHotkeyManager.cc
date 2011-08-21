/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
#include "org_sikuli_script_internal_hotkey_MacHotkeyManager.h"

#include <Carbon/Carbon.h>
#include <CoreFoundation/CoreFoundation.h>

#include<iostream>
#include<map>

#include "sikuli-debug.h"

using namespace std;
using namespace sikuli;

#define HOTKEY_LISTENER_METHOD "invokeHotkeyPressed"
#define HOTKEY_LISTENER_SIGNATURE "(Lorg/sikuli/script/HotkeyEvent;)V"
#define HOTKEY_EVENT_CLASS "org/sikuli/script/HotkeyEvent"

struct CallbackData {
   JavaVM *vm;
   int hotkey, mods;
   int jHotkey, jModifiers;
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
   env->CallVoidMethod(ret, initMethod, data->jHotkey, data->jModifiers);
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

static map<int, CallbackData*> regHotkeys;
static int gHotkeyId = 0;

OSStatus shortcutHandler( EventHandlerCallRef inCaller, EventRef inEvent, 
                          void* args )
{
   EventHotKeyID hkId;
   GetEventParameter(inEvent, kEventParamDirectObject, typeEventHotKeyID, NULL,
                     sizeof(hkId), NULL, &hkId);
   CallbackData *data = regHotkeys[hkId.id];
   int hotkey = data->hotkey;
   dout("MacHotkeyManager") << "shortcut pressed. " << hotkey << endl;
   callJavaMethod(data->vm, data->listener, data);
   return noErr;
}


bool unregisterHotkey(CallbackData *data){
   map<int, CallbackData*>::iterator it;
   for(it = regHotkeys.begin(); it != regHotkeys.end(); ++it){
      CallbackData *itdata = it->second;
      if( itdata->hotkey == data->hotkey && itdata->mods == data->mods){
         UnregisterEventHotKey(itdata->ref);
         data->id = itdata->id;
         data->ref = itdata->ref;
         regHotkeys.erase(it);
         return true;
      }
   }
   return false;
}


bool installShortcutHandler( CallbackData *data ){
   EventTypeSpec shortcutEvents[] = {
      { kEventClassKeyboard, kEventHotKeyPressed },
   };
   
   if(gHotkeyId == 0){
      OSErr err = InstallApplicationEventHandler( &shortcutHandler,
        GetEventTypeCount(shortcutEvents), shortcutEvents, NULL, NULL);
      if (err != noErr)
         cerr << "InstallApplicationEventHandler failed" << endl;
   }

   bool registered = unregisterHotkey(data); 
   if(!registered){
      data->id.id = gHotkeyId++;
      data->id.signature='htk1';
   }

   OSStatus err = RegisterEventHotKey(data->hotkey, data->mods,
                      data->id, GetApplicationEventTarget(), 0, 
                      &(data->ref));
   if(!err){
      regHotkeys[data->id.id] = data;
      return true;
   }
   return false;
}

/*
 * Class:     org_sikuli_script_internal_hotkey_MacHotkeyManager
 * Method:    installGlobalHotkey
 * Signature: (IIIILorg/sikuli/script/internal/hotkey/HotkeyListener;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_sikuli_script_internal_hotkey_MacHotkeyManager_installGlobalHotkey
(JNIEnv *env, jobject jobj, jint jHotkey, jint jModifiers, jint hotkey, jint modifiers, jobject listener){
   dout("MacHotkeyManager") << "install global hotkey: " << hotkey << " mod: " << modifiers << endl;
   JavaVM* vm = NULL;
   env->GetJavaVM(&vm);
   jobject gListener = env->NewGlobalRef(listener);
   env->DeleteLocalRef(listener);
   CallbackData *data = new CallbackData(vm, hotkey, modifiers, gListener);
   data->jHotkey = jHotkey;
   data->jModifiers = jModifiers;
   return installShortcutHandler(data);
}


/*
 * Class:     org_sikuli_script_internal_hotkey_MacHotkeyManager
 * Method:    uninstallGlobalHotkey
 * Signature: (II)Z
 */
JNIEXPORT jboolean JNICALL 
Java_org_sikuli_script_internal_hotkey_MacHotkeyManager_uninstallGlobalHotkey
(JNIEnv *env, jobject jobj, jint hotkey, jint modifiers){
   CallbackData *data = new CallbackData(NULL, hotkey, modifiers, NULL);
   return unregisterHotkey(data);
}

/*
 * Class:     org_sikuli_script_internal_hotkey_MacHotkeyManager
 * Method:    cleanUp
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sikuli_script_internal_hotkey_MacHotkeyManager_cleanUp
(JNIEnv *env, jobject jobj){
   map<int, CallbackData*>::iterator it;
   for(it = regHotkeys.begin(); it != regHotkeys.end(); ++it){
      CallbackData *itdata = it->second;
      UnregisterEventHotKey(itdata->ref);
   }
   regHotkeys.clear();
   gHotkeyId = 0;
}
