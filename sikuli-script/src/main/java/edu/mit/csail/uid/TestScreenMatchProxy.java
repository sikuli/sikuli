package edu.mit.csail.uid;

public class TestScreenMatchProxy {

   public static void main(String[] argv){
      ScreenMatchProxy matcher = new ScreenMatchProxy();
      if( argv.length < 2 ){
         System.err.println("screenMatch [desktop image] [target image]");
         return;
      }
      Match[] matches;
      if( argv[1].endsWith(".png") || argv[1].endsWith(".jpg") )
         matches = matcher.screenMatch(argv[1], argv[0], 0.7, 10);
      else
         matches = matcher.screenMatchByOCR(argv[1], argv[0], 0, 5);
      for(int i=0;i<matches.length;i++){
         System.out.println( 
               matches[i].getX() + " " + matches[i].getY() + " " + 
               matches[i].getW() + " " + matches[i].getH() + " " + 
               matches[i].score); 
      }
  
   }
}
