UNAME=$(shell uname)
UNAME_R=$(shell uname -r)

TARGET_DIR=../../../target/lib
OS=$(shell uname -rs)


CXX=g++
DEBUG=-Wall #-g -DDEBUG

ifneq (,$(findstring 10., $(UNAME_R)))
	ARCH=-arch i386 -arch x86_64
endif
ifneq (,$(findstring 9., $(UNAME_R)))
	ARCH=-arch i386
endif

CXXFLAGS= -I/System/Library/Frameworks/JavaVM.framework/Headers/ $(ARCH) $(DEBUG) -framework CoreFoundation.framework -framework Carbon.framework
LDFLAGS= $(DEBUG) $(ARCH) -dynamiclib -L/System/Library/Frameworks/ -framework CoreFoundation -framework Carbon -framework JavaVM 
OBJS = NativeLayerForMac.o

# Mac only
DYN_LIB = $(TARGET_DIR)/libNativeLayerForMac.jnilib 

JAVA_SRC=NativeLayerForMac.java

TEST_PROXY_SRC=TestScreenMatchProxy.java
TEST_SRC=TestSikuliScript.java

COMMON_LIB_PATH=../../../../lib
CLASSES_TARGET_PATH=../../../target/classes
CLASSPATH=../java:$(COMMON_LIB_PATH)/sikuli-script.jar:$(COMMON_LIB_PATH)/junit-3.8.1.jar
PACKAGE=edu.mit.csail.uid

default: check-os 

check-os:
ifeq "$(UNAME)" "Darwin"
	make lib
endif


$(DYN_LIB): $(OBJS)
	mkdir -p $(TARGET_DIR)
	$(CXX) $(LDFLAGS) $(OBJS) -o $(DYN_LIB)

exe: $(BIN)

$(BIN): $(BIN_OBJS) 
	$(CXX) $(BIN_LDFLAGS) $(BIN_OBJS) -o $(BIN)

lib: $(DYN_LIB)

jni:
	javac -sourcepath ../java -d $(CLASSES_TARGET_PATH) -cp $(CLASSPATH) `find ../java -name \*.java`
	javah -classpath $(CLASSES_TARGET_PATH) $(JAVA_SRC:%.java=$(PACKAGE).%) 



test-proxy: lib jni
	java -cp .:$(CLASSPATH) $(TEST_PROXY_SRC:%.java=$(PACKAGE).%) ../testimages/desktop.jpg ../testimages/word.jpg

test-ocr: lib jni
	java -cp .:$(CLASSPATH) $(TEST_PROXY_SRC:%.java=$(PACKAGE).%) testimages/mac-desktop.png "System Preferences"

test: lib jni
	java -Dsikuli.Debug=9 -cp .:$(CLASSPATH) $(TEST_SRC:%.java=$(PACKAGE).%) 

jar: lib jni
	jar cvf SikuliScript.jar edu/mit/csail/uid/*.class   libScreenMatchProxy.jnilib

clean:
	rm -f $(BIN_OBJS) $(OBJS) $(DYN_LIB) $(BIN)
