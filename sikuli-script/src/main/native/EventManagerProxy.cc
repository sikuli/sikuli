#include "edu_mit_csail_uid_EventManager.h"
#include "event-manager.h"

#include<iostream>

using namespace std;

#define CLASS_SIKULI_EVENT "edu/mit/csail/uid/SikuliEvent"

jobjectArray& convertToJavaEvents(JNIEnv *env, const vector<Event> &events, jobjectArray &ret){

#ifdef DEBUG
   cerr << "[JNI] number of detected events: " << events.size() << endl;
#endif
   jclass jClassSikuliEvent = env->FindClass(CLASS_SIKULI_EVENT);
   ret = (jobjectArray)env->NewObjectArray(events.size(), jClassSikuliEvent, NULL);
   vector<Event>::const_iterator it;
   int i=0;
   for(it=events.begin();it != events.end();++it, ++i){
      jobject evt = env->AllocObject(jClassSikuliEvent);
      jfieldID evt_type = env->GetFieldID(jClassSikuliEvent, "type", "I");
      jfieldID evt_handler_id = env->GetFieldID(jClassSikuliEvent, "handler_id", "I");
      jfieldID evt_x = env->GetFieldID(jClassSikuliEvent, "x", "I");
      jfieldID evt_y = env->GetFieldID(jClassSikuliEvent, "y", "I");
      jfieldID evt_w = env->GetFieldID(jClassSikuliEvent, "w", "I");
      jfieldID evt_h = env->GetFieldID(jClassSikuliEvent, "h", "I");
      env->SetIntField(evt, evt_type, it->type);
      env->SetIntField(evt, evt_handler_id, it->handler_id);
      env->SetIntField(evt, evt_x, it->x);
      env->SetIntField(evt, evt_y, it->y);
      env->SetIntField(evt, evt_w, it->w);
      env->SetIntField(evt, evt_h, it->h);
      env->SetObjectArrayElement(ret, i, evt);
   }
   return ret;
}

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
 * Signature: (JILjava/lang/String;FIIIII)V
 */
JNIEXPORT void JNICALL Java_edu_mit_csail_uid_EventManager_addObserver

  (JNIEnv *env, jobject jobj, jlong jSemInstance, 
   jint evt_type, jstring jTargetImgFilename, jfloat jSimilarity,
   jint handler_id, jint x, jint y, jint w, jint h){
   SikuliEventManager *sem=reinterpret_cast<SikuliEventManager*>(jSemInstance); 
   const char *fname = env->GetStringUTFChars(jTargetImgFilename, NULL);
#ifdef DEBUG
   cerr << "[JNI] addObserver " << evt_type << " " 
        << fname  << " " << jSimilarity 
        << handler_id << " " << x << " " << y << " " << w << " " << h << endl;
#endif
   sem->addObserver(evt_type, fname, jSimilarity, handler_id, x, y, w, h);
   env->ReleaseStringUTFChars(jTargetImgFilename, fname);
}

/*
 * Class:     edu_mit_csail_uid_EventManager
 * Method:    _update
 * Signature: (J[BII)[Ljava/util/EventObject;
 */
JNIEXPORT jobjectArray JNICALL Java_edu_mit_csail_uid_EventManager__1update
  (JNIEnv *env, jobject jobj, jlong jSemInstance,
   jbyteArray screenImg, jint w, jint h){

   const int bpp = 24; 
   const int bpr = w*3; 
   IplImage* img;
   jint len;
   unsigned char* result;

   img = cvCreateImageHeader(cvSize(w,h),8,bpp/8); //create the "shell"

   len = env->GetArrayLength(screenImg);
   result = (unsigned char *)malloc(len + 1);
   if (result == 0) {
      cerr << "out of memory\n";
      env->DeleteLocalRef(screenImg);
      return 0;
   }
   env->GetByteArrayRegion(screenImg, 0, len,(jbyte *)result);
   cvSetData(img,result,bpr);    //set the buffer

   SikuliEventManager *sem=reinterpret_cast<SikuliEventManager*>(jSemInstance); 
   vector<Event> events = sem->update(img);
   jobjectArray ret = NULL;
   if(!events.empty())
      ret = convertToJavaEvents(env, events, ret);

   cvReleaseImageHeader(&img);
   free(result);
   return ret;
  
}

/*
 * Class:     edu_mit_csail_uid_EventManager
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_edu_mit_csail_uid_EventManager_destroy
  (JNIEnv *env, jobject jobj, jlong jSemInstance){
  
   SikuliEventManager *sem=reinterpret_cast<SikuliEventManager*>(jSemInstance); 
   delete sem;
#ifdef DEBUG
   cerr << "[JNI] destroy EventManager\n";
#endif
}

