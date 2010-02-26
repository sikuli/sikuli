OUTPUT_LIB_NAMES=libScreenMatchProxy libVDictProxy

#############################################################
include Makefile.common

DEV_ROOT=../../..
include ../java/Makefile

OUTPUT_LIBS=$(OUTPUT_LIB_NAMES:%=$(JNI_TARGET_DIR)/%.$(DYN_LIB_POSTFIX))

default: lib

jni: $(JNI_HEADERS) 

lib: $(OUTPUT_LIBS)

$(JNI_TARGET_DIR)/%.$(DYN_LIB_POSTFIX):
	$(MAKE) -f Makefile.$(basename $(notdir $@))

clean:
	rm -f $(OUTPUT_LIBS) *.o
