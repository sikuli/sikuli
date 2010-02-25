include Makefile.common

LDFLAGS+=-ltesseract_full -ltiff 
BIN_LDFLAGS+=-ltesseract_full -ltiff 

OBJS = finder.o event-manager.o ScreenMatchProxy.o ocr-matcher.o template-matcher.o myocr.o screendiff.o TimingBlock.o
BIN_OBJS = $(OBJS) screenmatch.o
BIN = screenmatch

DYN_LIB = $(TARGET_DIR)/libScreenMatchProxy.$(DYN_LIB_POSTFIX) 

JAVA_SRC=ScreenMatchProxy.java

TEST_PROXY_SRC=TestScreenMatchProxy.java
TEST_SRC=TestSikuliScript.java

default: lib vdict

vdict:
	make -f Makefile.vdict

$(DYN_LIB): $(OBJS)
	mkdir -p $(TARGET_DIR)
	$(CXX) $(OBJS) -o $(DYN_LIB) $(LDFLAGS) 

exe: $(BIN)

$(BIN): $(BIN_OBJS) 
	$(CXX) $(BIN_OBJS) -o $(BIN) $(BIN_LDFLAGS) 

lib: $(DYN_LIB)

jni:
	javac -sourcepath ../java -d $(CLASSES_TARGET_PATH) -cp $(CLASSPATH) `find ../java -name \*.java`
	javah -classpath $(CLASSES_TARGET_PATH) $(JAVA_SRC:%.java=$(PACKAGE).%) 


bin: $(BIN)


test-proxy: lib jni
	java -cp .:$(CLASSPATH) $(TEST_PROXY_SRC:%.java=$(PACKAGE).%) ../testimages/desktop.jpg ../testimages/word.jpg

test-ocr: lib jni
	java -cp .:$(CLASSPATH) $(TEST_PROXY_SRC:%.java=$(PACKAGE).%) testimages/mac-desktop.png "System Preferences"

test: lib jni
	java -Dsikuli.Debug=9 -cp .:$(CLASSPATH) $(TEST_SRC:%.java=$(PACKAGE).%) 

clean:
	rm -f $(BIN_OBJS) $(OBJS) $(DYN_LIB) $(BIN)
