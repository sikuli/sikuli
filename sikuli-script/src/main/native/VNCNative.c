#include "VNCNative.h"
#include <rfb/rfbclient.h>

/*
 * Class:     org_sikuli_script_VNCNative
 * Method:    rfbGetClient
 * Signature: (III)J
 */
JNIEXPORT jlong JNICALL Java_org_sikuli_script_VNCNative_rfbGetClient
  (JNIEnv *env, jclass cls, jint bitsPerSample, jint samplesPerPixel, jint bytesPerPixel) {
	return (unsigned long long)rfbGetClient(bitsPerSample, samplesPerPixel, bytesPerPixel);
}



static char *currentPassword;
static char *GetCurrentPassword(struct _rfbClient *client) {
	return currentPassword;
}

/*
 * Class:     org_sikuli_script_VNCNative
 * Method:    rfbInitClient
 * Signature: (J[Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_sikuli_script_VNCNative_rfbInitClient
  (JNIEnv *env, jclass cls, jlong client, jobjectArray args,jstring password) {
	const char **cargs;
	int c;

	jint count = (*env)->GetArrayLength(env, args);
	cargs=malloc(sizeof(cargs[0])*(count+1));
	for(c=0; c<count;++c) {
		jobject element = (*env)->GetObjectArrayElement(env,args,c);
		cargs[c]=(*env)->GetStringUTFChars(env,element,0);
	}
	cargs[count]=NULL;

	const char *nativeString;
	if(password!=NULL) {
		nativeString=(*env)->GetStringUTFChars(env,password,0);
		currentPassword=malloc(strlen(nativeString));
		strcpy(currentPassword,nativeString);
		((rfbClient *)client)->GetPassword=GetCurrentPassword;
	}
	rfbInitClient((rfbClient *)client,&count,(char **)cargs);

	if(password!=NULL)
		(*env)->ReleaseStringUTFChars(env, password, nativeString);
	for(c=0; c<count;++c) {
		(*env)->ReleaseStringUTFChars(env,(*env)->GetObjectArrayElement(env,args,c),cargs[c]);
	}
}

/*
 * Class:     org_sikuli_script_VNCNative
 * Method:    WaitForMessage
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_sikuli_script_VNCNative_WaitForMessage
  (JNIEnv *env, jclass cls, jlong client, jint usecs) {
	return WaitForMessage((rfbClient *)client,usecs);
}

/*
 * Class:     org_sikuli_script_VNCNative
 * Method:    HandleRFBServerMessage
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_sikuli_script_VNCNative_HandleRFBServerMessage
  (JNIEnv *env, jclass cls, jlong client) {
	HandleRFBServerMessage((rfbClient *)client);
}

/*
 * Class:     org_sikuli_script_VNCNative
 * Method:    CopyScreenToData
 * Signature: (J[B)Z
 */
JNIEXPORT void JNICALL Java_org_sikuli_script_VNCNative_CopyScreenToData
  (JNIEnv *env, jclass cls, jlong client, jintArray output,jint x,jint y,jint width,jint height) {
	int outputUpto=0;
	int outputLen=(*env)->GetArrayLength(env,output);
	int frameWidth=((rfbClient *)client)->width;
	int frameHeight=((rfbClient *)client)->height;
	const int *frameBuffer=(int *) ((rfbClient *)client)->frameBuffer;
	const int *frameBufferEnd=frameBuffer+(frameWidth*frameHeight);

	frameBuffer+=x+(y*frameWidth);
	while(outputUpto<outputLen && (frameBuffer+width*4)<frameBufferEnd) {
		(*env)->SetIntArrayRegion( env,output,outputUpto,width,frameBuffer);
		outputUpto+=width;
		frameBuffer+=frameWidth;
	}
	
}


/*
 * Class:     org_sikuli_script_VNCNative
 * Method:    GetWidth
 * Signature: (J)Z
 */
JNIEXPORT jint JNICALL Java_org_sikuli_script_VNCNative_GetWidth
  (JNIEnv *env, jclass cls, jlong client) {
	return ((rfbClient *)client)->width;
}

/*
 * Class:     org_sikuli_script_VNCNative
 * Method:    GetHeight
 * Signature: (J)Z
 */
JNIEXPORT jint JNICALL Java_org_sikuli_script_VNCNative_GetHeight
  (JNIEnv *env, jclass cls, jlong client) {
	return ((rfbClient *)client)->height;
}



/*
 * Class:     org_sikuli_script_VNCNative
 * Method:    SendPointerEvent
 * Signature: (JIII)Z
 */
JNIEXPORT jboolean JNICALL Java_org_sikuli_script_VNCNative_SendPointerEvent
  (JNIEnv *env, jclass cls, jlong client, jint x, jint y, jint buttons) {
	SendPointerEvent((rfbClient *)client,x,y,buttons);
}

/*
 * Class:     org_sikuli_script_VNCNative
 * Method:    SendKeyEvent
 * Signature: (JIZ)Z
 */
JNIEXPORT jboolean JNICALL Java_org_sikuli_script_VNCNative_SendKeyEvent
  (JNIEnv *env, jclass cls, jlong client, jint c, jboolean down) {
	SendKeyEvent((rfbClient *)client,c,down);
}


