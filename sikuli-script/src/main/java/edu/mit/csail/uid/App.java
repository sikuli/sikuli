package edu.mit.csail.uid;

public class App {
   protected static OSUtil _osUtil = Env.getOSUtil();
   protected String _appName;

   public App(String appName) {
      _appName = appName;
   }

   public static App open(String appName) throws AppNotFound{
      App app = new App(appName);
      if(_osUtil.openApp(appName)!=0)
         throw new AppNotFound(appName);
      return app;
   }

   public static int close(String appName){
      return _osUtil.closeApp(appName);
   }

   public static int focus(String appName){
      return _osUtil.switchApp(appName);
   }

   public App focus(){
      focus(_appName);
      return this;
   }

   public App open() throws AppNotFound{
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

