package edu.mit.csail.uid;

public class App {
   protected static OSUtil _osUtil = Env.createOSUtil();
   protected String _appName;

   public App(String appName) throws AppNotFound{
      _appName = appName;
      int ret = _osUtil.openApp(appName);
      if(ret !=0)
         throw new AppNotFound(appName);
   }

   public static App open(String appName) throws AppNotFound{
      return new App(appName);
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

   public int close(){
      return close(_appName);
   }

   public String name(){
      return _appName;
   }

   public Region window(){
      //FIXME
      return null;
   }

   //public Region window(int winId){ }
}

