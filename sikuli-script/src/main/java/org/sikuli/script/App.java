package org.sikuli.script;

public class App {
   protected static OSUtil _osUtil = Env.getOSUtil();
   protected String _appName;

   public App(String appName) {
      _appName = appName;
   }

   public static App open(String appName) {
      if(_osUtil.openApp(appName)!=0){
         Debug.error("App.open failed: " + appName + " not found");
         return null;
      }
      App app = new App(appName);
      return app;
   }

   public static int close(String appName){
      return _osUtil.closeApp(appName);
   }

   public static App focus(String appName){
      if(_osUtil.switchApp(appName)!=0){
         Debug.error("App.focus failed: " + appName + " not found");
         return null;
      }
      App app = new App(appName);
      return app;
   }

   public App focus(){
      focus(_appName);
      return this;
   }

   public App open() {
      return open(_appName);
   }

   public int close(){
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

