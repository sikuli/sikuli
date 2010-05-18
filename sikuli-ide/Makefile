UNAME=$(shell uname)
ifeq "$(UNAME)" "Darwin"
   OS=$(shell uname -r)
   ifneq (,$(findstring 10., $(OS)))
      TARGET_OS=osx-10.6
   endif
   ifneq (,$(findstring 9., $(OS)))
      TARGET_OS=osx-10.5
   endif
endif
ifeq "$(UNAME)" "Linux"
endif


JAVAC=javac
JAVA=java


LIB_PATH=../lib
#CLASSPATH=$(LIB_PATH)/junit-3.8.1.jar:$(LIB_PATH)/jmf.jar:$(LIB_PATH)/sikuli-script.jar:$(LIB_PATH)/sikuli-redo.jar
CLASSPATH=$(LIB_PATH)/junit-3.8.1.jar:$(LIB_PATH)/sikuli-script.jar:$(LIB_PATH)/swing-layout-1.0.1.jar:$(LIB_PATH)/commons-cli-1.2.jar
RESOURCE_PATH=resources
MAIN_CLASS=edu.mit.csail.uid.SikuliIDE
JAVA_ARGS=-Dsikuli.console=true -Dsikuli.Debug=3 -Dfile.encoding=UTF-8 -Xms256m -Xmx1024m #-Duser.language=zh -Duser.region=TW
ARGS=#evaluation/TestJEdit.sikuli 

JAR=sikuli-ide.jar

APP=$(TARGET_OS)/Sikuli-IDE.app
RECORDER=../sikuli-redo/target/MacRecorder.app
RELEASE_APP_ZIP=Sikuli-IDE-`date "+%Y%m%d"`.zip

TARGET_PATH=target
SOURCE_PATH=src/main/java
CLASSES_OUT_PATH=target/classes
NATIVE_SRC_PATH=src/main/native
NATIVE_TARGET_PATH=target/lib
JAR_PATH=target/jar
RELEASE_PATH=../release
APP_FRAMEWORKS_PATH=$(TARGET_PATH)/$(APP)/Contents/Frameworks

SRC=$(shell find $(SOURCE_PATH) -name \*.java -and -not -regex '.*/NativeLayerFor.*.java')
CLASSES=$(shell cd $(SOURCE_PATH); find . -name \*.java -and -not -regex '.*/NativeLayerFor.*.java'| perl -ple 's!^./(.*)\.java!$(CLASSES_OUT_PATH)/$$1.class!' )

ifeq "$(UNAME)" "Darwin"
   SRC+=$(SOURCE_PATH)/edu/mit/csail/uid/NativeLayerForMac.java
   CLASSES+=$(CLASSES_OUT_PATH)/edu/mit/csail/uid/NativeLayerForMac.class
   JAVA_ARGS+=-Xdock:name="Sikuli IDE" 
endif

ifeq "$(UNAME)" "Linux"
   SRC+=$(SOURCE_PATH)/edu/mit/csail/uid/NativeLayerForLinux.java
   CLASSES+=$(CLASSES_OUT_PATH)/edu/mit/csail/uid/NativeLayerForLinux.class
   CLASSPATH:=$(CLASSPATH):$(LIB_PATH)/jxgrabkey/lib/JXGrabKey.jar
endif


RESOURCES=$(RESOURCE_PATH)/icons $(RESOURCE_PATH)/META-INF $(RESOURCE_PATH)/scripts $(RESOURCE_PATH)/i18n
TARGET_RESOURCES=$(RESOURCES:$(RESOURCE_PATH)/%=$(CLASSES_OUT_PATH)/%)
ifeq "$(UNAME)" "Linux"
   MANIFEST=META-INF/MANIFEST-linux.MF
else
   MANIFEST=META-INF/MANIFEST.MF
endif

JFLAGS=-sourcepath $(SOURCE_PATH) -d $(CLASSES_OUT_PATH) -cp $(CLASSPATH) 

default: test

#$(CLASSES_OUT_PATH)/%.class: $(SOURCE_PATH)/%.java
#	$(JAVAC) $(JFLAGS) $<

$(CLASSES_OUT_PATH)/%: $(RESOURCE_PATH)/%
	cp -rf $< $(CLASSES_OUT_PATH)


$(CLASSES): $(SRC)
	$(JAVAC) $(JFLAGS) $(SRC)

$(CLASSES_OUT_PATH):
	mkdir -p $(CLASSES_OUT_PATH)

$(RELEASE_PATH):
	mkdir -p $(RELEASE_PATH)

$(NATIVE_TARGET_PATH):
	mkdir -p $(NATIVE_TARGET_PATH)

$(JAR_PATH):
	mkdir -p $(JAR_PATH)


native-lib: native-lib-linux
	make -C $(NATIVE_SRC_PATH)


native-lib-linux: $(NATIVE_TARGET_PATH)
ifeq "$(UNAME)" "Linux"
  ifeq "$(shell uname -m)" "x86_64"
	cp -f $(LIB_PATH)/jxgrabkey/lib/libJXGrabKey-64.so $(NATIVE_TARGET_PATH)/libJXGrabKey.so
  else
	cp -f $(LIB_PATH)/jxgrabkey/lib/libJXGrabKey-32.so $(NATIVE_TARGET_PATH)/libJXGrabKey.so
  endif
endif

debug:
	jdb -Dsikuli.Debug=5  -classpath $(CLASSPATH):$(CLASSES_OUT_PATH) $(MAIN_CLASS) /Users/vgod/test.sikuli

test:all
	rm -rf tmplib
	if [ -e $(NATIVE_TARGET_PATH) ]; \
	then cp -rf $(NATIVE_TARGET_PATH) $(CLASSES_OUT_PATH)/META-INF; \
	fi
	$(JAVA) $(JAVA_ARGS) -cp $(CLASSES_OUT_PATH):$(CLASSPATH) $(MAIN_CLASS) $(ARGS) 


all: $(CLASSES_OUT_PATH) $(CLASSES) $(TARGET_RESOURCES) native-lib tags

clean:
	rm $(CLASSES)

prepare-jar: $(NATIVE_TARGET_PATH)
	cp -rf $(CLASSES_OUT_PATH)/* $(JAR_PATH)
	mkdir -p $(JAR_PATH)/META-INF
	cp -rf $(NATIVE_TARGET_PATH) $(JAR_PATH)/META-INF

	
jar: $(JAR_PATH) all prepare-jar
	cd $(JAR_PATH); jar cvmf $(MANIFEST) ../$(JAR) *; cd -

install-jar: jar
	cp $(JAR_PATH)/../$(JAR) $(LIB_PATH)

release: minor2-release

major-release: inc-major update-ver do_release

minor1-release: inc-minor1 update-ver do_release

minor2-release: inc-minor2 update-ver do_release

inc-major:
	perl -pi -e 's/(\d+)\.(\d+)\.(\d+)/($$1+1).".0.0"/xe' latest-ver

inc-minor1:
	perl -pi -e 's/(\d+)\.(\d+)\.(\d+)/"$$1.".($$2+1).".0"/xe' latest-ver

inc-minor2:
	perl -pi -e 's/(\d+)\.(\d+)\.(\d+)/"$$1.$$2.".($$3+1)/xe' latest-ver

tags:
	-ctags -R --language-force=java  -f .tags src/main/java ../sikuli-script/src/main/java

update-prop:
	for f in resources/i18n/IDE/*.po; do \
	   echo convert $$f; \
	   po2prop -t resources/i18n/IDE_en_US.properties $$f `echo $$f | sed -e 's/IDE-\(.*\).po/..\/IDE_\1.properties/'` ; \
	done

update-po:
	prop2po resources/i18n/IDE_en_US.properties > resources/i18n/IDE/IDE.pot

include Makefile.$(UNAME)
