package org.sikuli.script;

public class App {
   protected static OSUtil _osUtil = Env.getOSUtil();
   protected String _appName;
   protected int _pid; 

   public App(String appName) {
      _appName = appName;
      _pid = 0;
   }

   protected App(String appName, int pid){
      _appName = appName;
      _pid = pid;
   }

   public static App open(String appName) {
      return (new App(appName)).open();
   }

   public static int close(String appName){
      return _osUtil.closeApp(appName);
   }

   public static App focus(String appName){
      return (new App(appName)).focus();
   }

   public static App focus(String appName, int num){
      return (new App(appName)).focus(num);
   }

   public App focus(){
      return focus(0);
   }

   public App focus(int num){
      if(Env.isWindows() && _pid != 0){
         if(((Win32Util)_osUtil).switchApp(_pid, num)<0){
            Debug.error("App.focus failed: " + _appName + 
                        "(" + _pid +") not found");
            return null;
         }
      }
      else{
         _pid = _osUtil.switchApp(_appName, num);
         if(_pid == 0){
            Debug.error("App.focus failed: " + _appName + " not found");
            return null;
         }
      }
      return this;
   }

   public App open() {
      if(Env.isWindows()){
         int pid = _osUtil.openApp(_appName);
         Debug.log("open " + _appName + " PID: " + pid);
         if(pid == 0){
            Debug.error("App.open failed: " + _appName + " not found");
            return null;
         }
         _pid = pid;
      }
      else{
         if(_osUtil.openApp(_appName)<0){
            Debug.error("App.open failed: " + _appName + " not found");
            return null;
         }
      }
      return this;
   }

   public int close(){
      if(Env.isWindows() && _pid != 0)
         return ((Win32Util)_osUtil).closeApp(_pid);
      return close(_appName);
   }

   public String name(){
      return _appName;
   }

   public Region window(){
      return _osUtil.getWindow(_appName);
   }

   public Region window(int winNum){
      return _osUtil.getWindow(_appName, winNum);
   }

   public static Region focusedWindow(){
      return _osUtil.getFocusedWindow();
   }

}

