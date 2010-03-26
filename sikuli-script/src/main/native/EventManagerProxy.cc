#include "edu_mit_csail_uid_EventManager.h"
#include "event-manager.h"


/*
 * Class:     edu_mit_csail_uid_EventManager
 * Method:    createEventManager
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_edu_mit_csail_uid_EventManager_createEventManager
  (JNIEnv *env, jobject jobj){
   SikuliEventManager *sem = new SikuliEventManager();
   return reinterpret_cast<jlong>(sem);
}


/*
 * Class:     edu_mit_csail_uid_EventManager
 * Method:    addObserver
 * Signature: (JILjava/lang/String;IIIII)V
 */
JNIEXPORT void JNICALL Java_edu_mit_csail_uid_EventManager_addObserver
  (JNIEnv *env, jobject jobj, jlong jSemInstance, 
   jint evt_type, jstring jTargetImgFilename, 
   jint handler_id, jint x, jint y, jint w, jint h){
 
}
/*
 * Class:     edu_mit_csail_uid_EventManager
 * Method:    _update
 * Signature: (J[BII)V
 */
JNIEXPORT void JNICALL Java_edu_mit_csail_uid_EventManager__1update
  (JNIEnv *env, jobject jobj, jlong jSemInstance,
   jbyteArray jScreenImg, jint w, jint h){
  
}
