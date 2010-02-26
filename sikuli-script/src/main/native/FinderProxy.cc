#include "edu_mit_csail_uid_Finder.h"
#include "finder.h"

#include<iostream>

#define CLASS_MATCH "edu/mit/csail/uid/Match"

using namespace std;

/*
 * Class:     edu_mit_csail_uid_Finder
 * Method:    createFinder
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_edu_mit_csail_uid_Finder_createFinder
  (JNIEnv *env, jobject jobj, jstring jScreenFilename){
   const char *fname = env->GetStringUTFChars(jScreenFilename, NULL);
   Finder *finder = new Finder(fname);
   env->ReleaseStringUTFChars(jScreenFilename, fname);
   return reinterpret_cast<jlong>(finder);
}

/*
 * Class:     edu_mit_csail_uid_Finder
 * Method:    find
 * Signature: (Ljava/lang/String;D)V
 */
JNIEXPORT void JNICALL Java_edu_mit_csail_uid_Finder_find
  (JNIEnv *env, jobject jobj, jlong jfinder, jstring jTemplateFilename, double minSimilarity){
   Finder *finder = reinterpret_cast<Finder*>(jfinder);
   const char *fname = env->GetStringUTFChars(jTemplateFilename, NULL);
   finder->find(fname, minSimilarity);
   env->ReleaseStringUTFChars(jTemplateFilename, fname);
}
/*
 * Class:     edu_mit_csail_uid_Finder
 * Method:    hasNext
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_edu_mit_csail_uid_Finder_hasNext
  (JNIEnv *env, jobject jobj, jlong jFinder){
   Finder *finder = reinterpret_cast<Finder*>(jFinder);
   return finder->hasNext();
}

/*
 * Class:     edu_mit_csail_uid_Finder
 * Method:    next
 * Signature: ()Ledu/mit/csail/uid/Match;
 */
JNIEXPORT jobject JNICALL Java_edu_mit_csail_uid_Finder_next
  (JNIEnv *env, jobject jobj, jlong jFinder){
   Finder *finder = reinterpret_cast<Finder*>(jFinder);
   Match m = finder->next();
   jclass jClassMatch = env->FindClass(CLASS_MATCH);
   jobject ret = env->AllocObject(jClassMatch);
   jfieldID match_x = env->GetFieldID(jClassMatch, "x", "I");
   jfieldID match_y = env->GetFieldID(jClassMatch, "y", "I");
   jfieldID match_w = env->GetFieldID(jClassMatch, "w", "I");
   jfieldID match_h = env->GetFieldID(jClassMatch, "h", "I");
   jfieldID match_score = env->GetFieldID(jClassMatch, "score", "D");
   //jfieldID match_parent = env->GetFieldID(jClassMatch, "parent", "Ljava/lang/String;");
   env->SetIntField(ret, match_x, m.x);
   env->SetIntField(ret, match_y, m.y);
   env->SetIntField(ret, match_w, m.w);
   env->SetIntField(ret, match_h, m.h);
   env->SetDoubleField(ret, match_score, m.score);
   //env->SetObjectField(ret, match_parent, jscreen); 
   // FIXME: drop the parent filed
   return ret;
}

/*
 * Class:     edu_mit_csail_uid_Finder
 * Method:    destroy
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_mit_csail_uid_Finder_destroy
  (JNIEnv *env, jobject jobj, jlong jFinder){
   Finder *finder = reinterpret_cast<Finder*>(jFinder);
   delete finder;
   cerr << "[JNI] destroy finder\n";
}
