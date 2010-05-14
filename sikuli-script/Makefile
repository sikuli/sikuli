JAVAC=javac
PYTHON_DOC=pythondoc.py
PYTHON_DOC_FLAGS=-Dstyle=css/pydoc.css -p sikuli

JAR=sikuli-script.jar
JYTHON_JAR=$(LIB_PATH)/jython-2.5.1.jar
JYTHON_LIB=$(LIB_PATH)/jython-lib-2.5.1.zip
JYTHON_CLASS=org.python.util.jython
JYTHON_SRC_CLASS=sikuli/

LIB_PATH=$(shell pwd)/../lib
CLASSPATH=$(INCLUDE_LIB_PATH):$(JYTHON_JAR)

DOC_PATH=doc/
SOURCE_PATH=src/main/java
TARGET_PATH=target/classes
NATIVE_SRC_PATH=src/main/native
NATIVE_TARGET_PATH=target/lib
JYTHON_SRC_PATH=src/main/python/sikuli
TEST_PATH=src/test/java
TEST_TARGET_PATH=target/test-classes
INCLUDE_LIB_PATH=lib
NATIVE_LOADER_PATH=../lib/mx-native-loader-1.2
JAR_PATH=target/jar/
RELEASE_TMP_PATH=target/sikuli-script

PY_TEST_PATH=src/test/python
PYTHON_TESTS= basic bundle capture click dragdrop env findAll keys mouse multiscreen observe print-objs region showActions sikuli type vdict wait with

SRC=$(shell find $(SOURCE_PATH) -name \*.java)
CLASSES=$(shell cd $(SOURCE_PATH); find . -name \*.java | perl -ple 's!^./(.*)\.java!$(TARGET_PATH)/$$1.class!' )
JFLAGS=-sourcepath $(SOURCE_PATH) -d $(TARGET_PATH) -cp $(CLASSPATH) 

JUNIT_JAR=../lib/junit-3.8.1.jar
TEST_SRC=$(shell find $(TEST_PATH) -name \*.java)
TEST_CLASSES=$(shell cd $(TEST_PATH); find . -name \*.java | perl -ple 's!^./(.*)\.java!$(TEST_TARGET_PATH)/$$1.class!' )
TEST_CLASSPATH=$(JAR_PATH):$(CLASSPATH):$(JUNIT_JAR):$(TEST_TARGET_PATH):$(TARGET_PATH)
TEST_JFLAGS=-sourcepath $(TEST_PATH) -d $(TEST_TARGET_PATH) -cp $(TEST_CLASSPATH)


default: jar

$(TEST_TARGET_PATH)/%.class: $(TEST_PATH)/%.java
	$(JAVAC) $(TEST_JFLAGS) $<

$(TARGET_PATH)/%.class: $(SOURCE_PATH)/%.java
	$(JAVAC) $(JFLAGS) $<

$(TEST_TARGET_PATH):
	mkdir -p $(TEST_TARGET_PATH)

$(TARGET_PATH):
	mkdir -p $(TARGET_PATH)

$(JAR_PATH):
	mkdir -p $(JAR_PATH)

$(RELEASE_TMP_PATH):
	mkdir -p $(RELEASE_TMP_PATH)


all: $(TARGET_PATH) $(CLASSES) native-lib

doc: python-doc java-doc install-doc

java-doc:
	javadoc -sourcepath $(SOURCE_PATH) -d $(DOC_PATH) -classpath $(CLASSPATH)  edu.mit.csail.uid


python-doc:
	cd $(JYTHON_SRC_PATH)/..; $(PYTHON_DOC) $(PYTHON_DOC_FLAGS) $(JYTHON_SRC_CLASS)
	for f in $(JYTHON_SRC_PATH)/../*.html;do mv $$f $(DOC_PATH); done

install-doc:
	cp -r $(DOC_PATH)/* ../../www/doc

update-python:
	cp -rf $(JYTHON_SRC_PATH) $(JAR_PATH)/Lib/

test: test-python test-vdict test-java

test-native:
	make -C src/test/native

test-python: update-python
	for t in $(PYTHON_TESTS); do \
		java -Dsikuli.Debug=3 -cp $(JAR_PATH) -Dpython.home=$(JAR_PATH) $(JYTHON_CLASS) $(PY_TEST_PATH)/test-$${t}.py; \
	done

test-vdict-py:
	rm -rf tmplib
	java -cp $(JAR_PATH) -Dpython.home=$(JAR_PATH) $(JYTHON_CLASS) $(PY_TEST_PATH)/TestSikuliScript.py
	java -cp $(JAR_PATH) -Dpython.home=$(JAR_PATH) $(JYTHON_CLASS) $(PY_TEST_PATH)/TestVDict.py
	
test-vdict: $(TEST_CLASSES) 
	rm -rf tmplib
	java -cp $(TEST_CLASSPATH) junit.textui.TestRunner edu.mit.csail.uid.VDictTest

test-java: $(TEST_TARGET_PATH) $(TEST_CLASSES) 
	rm -rf tmplib
	java -cp $(TEST_CLASSPATH) junit.textui.TestRunner edu.mit.csail.uid.SikuliScriptTest

clean:
	rm $(CLASSES)
	rm -rf tmplib

native-loader:
	if [ ! -e $(NATIVE_LOADER_PATH)/target/classes ]; \
	  then cd $(NATIVE_LOADER_PATH); mvn compile; \
	fi
	#cp -fr $(NATIVE_LOADER_PATH)/target/classes/* $(INCLUDE_LIB_PATH)

native-lib:
	make -C $(NATIVE_SRC_PATH)

prepare-jar: $(JAR_PATH) native-loader
	@echo Preparing JAR..
	cp -rf $(TARGET_PATH)/* $(JAR_PATH)
	mkdir -p $(JAR_PATH)/META-INF
	cp -rf $(NATIVE_TARGET_PATH) $(JAR_PATH)/META-INF
	cp -RLf $(INCLUDE_LIB_PATH)/* $(JAR_PATH) # Linux -rLf
	@echo Including jython-*.jar ...
	if [ ! -e $(JAR_PATH)/org/python ]; \
	  then cd $(JAR_PATH); jar xf $(JYTHON_JAR); \
	fi
	mkdir -p $(JAR_PATH)/Lib
	cp -rf $(JYTHON_SRC_PATH) $(JAR_PATH)/Lib/
	@echo Including jython libs
	if [ ! -e $(JAR_PATH)/Lib/glob.py ]; \
	  then cd $(JAR_PATH)/Lib; unzip -nq $(JYTHON_LIB); \
	fi
	
jar: all prepare-jar
	@echo Creating JAR..
	cd $(JAR_PATH); jar cfm ../$(JAR) ../MANIFEST.txt *; cd -

install-jar: jar
	cp target/$(JAR) $(LIB_PATH)

update-so:
	cd $(JAR_PATH); jar ufm ../../../lib/sikuli-script.jar ../MANIFEST.txt META-INF

release: $(RELEASE_TMP_PATH) 
	rm -rf $(RELEASE_TMP_PATH)/*
	cp target/sikuli-script.{bat,sh} $(RELEASE_TMP_PATH)
	chmod +x $(RELEASE_TMP_PATH)/sikuli-script.sh
	cp $(LIB_PATH)/sikuli-script.jar $(RELEASE_TMP_PATH)
	cp -r $(LIB_PATH)/{win32,tmplib} $(RELEASE_TMP_PATH)
	cd target; zip -r ../../release/Sikuli-Script-all-`date "+%Y%m%d"`.zip sikuli-script -x '*/.svn/*'

