#include <iostream>
#include <jni.h>
#include <time.h>
#include "edu_mit_csail_uid_ScreenMatchProxy.h"
#include "template-matcher.h"
#include "ocr-matcher.h"
#include "screendiff.h"

#define CLASS_MATCH "edu/mit/csail/uid/Match"
#define DEBUG_DIFF

using namespace std;

jobjectArray& initMatches(JNIEnv *env, int size, jobjectArray& ret){
   jclass jClassMatch = env->FindClass(CLASS_MATCH);
   jobject jMatch = env->AllocObject(jClassMatch);
   ret = (jobjectArray)env->NewObjectArray(size, jClassMatch, jMatch);
   return ret;
}



jobjectArray genericMatch
(bool by_ocr, JNIEnv *env, jobject jobj, jstring jtarget, jstring jscreen, jdouble threshold, jint numMatches)
{
   const char *fname_target = env->GetStringUTFChars(jtarget, NULL);
   const char *fname_screen = env->GetStringUTFChars(jscreen, NULL);

   if( fname_target && fname_screen ){
      cout << "[JNI] Run screenMatch: " << fname_target << " " << fname_screen << endl;
   }
   Matches matches;
   if(by_ocr)
      matches = match_by_ocr(fname_screen, fname_target, numMatches, threshold);
   else
      matches = match_by_template(fname_screen, fname_target, numMatches, threshold);

   jobjectArray ret;
   ret = initMatches(env,  matches.size(), ret);

   jclass jClassMatch = env->FindClass(CLASS_MATCH);

   int i=0;
   for(Matches::iterator it = matches.begin(); it != matches.end(); ++it, ++i){
      //cout << it->x << " " << it->y << " " << it->score << endl;
      jobject match = env->AllocObject(jClassMatch);
      jfieldID match_x = env->GetFieldID(jClassMatch, "x", "I");
      jfieldID match_y = env->GetFieldID(jClassMatch, "y", "I");
      jfieldID match_w = env->GetFieldID(jClassMatch, "w", "I");
      jfieldID match_h = env->GetFieldID(jClassMatch, "h", "I");
      jfieldID match_score = env->GetFieldID(jClassMatch, "score", "D");
      jfieldID match_parent = env->GetFieldID(jClassMatch, "parent", "Ljava/lang/String;");
      env->SetIntField(match, match_x, it->x);
      env->SetIntField(match, match_y, it->y);
      env->SetIntField(match, match_w, it->w);
      env->SetIntField(match, match_h, it->h);
      env->SetDoubleField(match, match_score, it->score);
      env->SetObjectField(match, match_parent, jscreen);
      env->SetObjectArrayElement(ret, i, match);
   }
   //cout << "[JNI] done" << endl;
   return ret;

}

JNIEXPORT jobjectArray JNICALL Java_edu_mit_csail_uid_ScreenMatchProxy_screenMatch
(JNIEnv *env, jobject jobj, jstring target, jstring screen, 
 jdouble threshold, jint numMatches)
{
   return genericMatch(false, env, jobj, target, screen, threshold, numMatches);
}

/*
 * Class:     edu_mit_csail_uid_ScreenMatchProxy
 * Method:    screenMatchByOCR
 * Signature: (Ljava/lang/String;Ljava/lang/String;DI)[Ledu/mit/csail/uid/Match;
 */
JNIEXPORT jobjectArray JNICALL Java_edu_mit_csail_uid_ScreenMatchProxy_screenMatchByOCR
(JNIEnv *env, jobject jobj, jstring target, jstring screen, 
 jdouble threshold, jint numMatches)
{
   return genericMatch(true, env, jobj, target, screen, threshold, numMatches);
}


JNIEXPORT jobjectArray JNICALL Java_edu_mit_csail_uid_ScreenMatchProxy_screenDiff
(JNIEnv *env, jobject jobj, jstring jbefore, jstring jafter)
{
  
   const char *fname_before = env->GetStringUTFChars(jbefore, NULL);
   const char *fname_after = env->GetStringUTFChars(jafter, NULL);

   if( fname_before && fname_after ){
      cout << "[JNI] Run screenDiff: " << fname_before << " " 
                                       << fname_after << endl;
   }
   vector<DiffRegion> matches;

#ifdef DEBUG_DIFF
   char rnd_output[512];
   sprintf(rnd_output, "/tmp/diff-%d.png", time(0));
   matches = screendiff(fname_before, fname_after, rnd_output);
   cout << "[JNI] Diff output: " << rnd_output << endl;
#endif

   jobjectArray ret;
   ret = initMatches(env,  matches.size(), ret);

   jclass jClassMatch = env->FindClass(CLASS_MATCH);

   int i=0;
   for(vector<DiffRegion>::iterator it = matches.begin(); it != matches.end(); 
                                                                     ++it, ++i){
      //cout << it->x << " " << it->y << " " << it->score << endl;
      jobject match = env->AllocObject(jClassMatch);
      jfieldID match_x = env->GetFieldID(jClassMatch, "x", "I");
      jfieldID match_y = env->GetFieldID(jClassMatch, "y", "I");
      jfieldID match_w = env->GetFieldID(jClassMatch, "w", "I");
      jfieldID match_h = env->GetFieldID(jClassMatch, "h", "I");
      jfieldID match_score = env->GetFieldID(jClassMatch, "score", "D");
      env->SetIntField(match, match_x, it->x);
      env->SetIntField(match, match_y, it->y);
      env->SetIntField(match, match_w, it->w);
      env->SetIntField(match, match_h, it->h);
      env->SetDoubleField(match, match_score, 0.0);
      env->SetObjectArrayElement(ret, i, match);
   }
   //cout << "[JNI] done" << endl;
   return ret;
}
