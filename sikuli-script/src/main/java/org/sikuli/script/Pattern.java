package org.sikuli.script;

public class Pattern {
   String imgURL = null;
   float similarity = 0.7f;

   int dx=0, dy=0;

   public Pattern(){ }
   public Pattern(Pattern p){
      imgURL = p.imgURL;
      similarity = p.similarity;
   }

   public Pattern(String imgURL_){
      imgURL = imgURL_;
   }

   public Pattern similar(float similarity_){
      Pattern ret = new Pattern(this);
      ret.similarity = similarity_;
      return ret;
   }

   public Pattern exact(){
      Pattern ret = new Pattern(this);
      ret.similarity = 1.0f;
      return ret;
   }

   public Pattern targetOffset(int dx_, int dy_){
      Pattern ret = new Pattern(this);
      ret.dx = dx_;
      ret.dy = dy_;
      return ret;
   }

   public String toString(){
     String ret = "Pattern(\"" + imgURL + "\")";
     ret += ".similar(" + similarity +")";
     if(dx!=0 || dy!=0)
        ret += ".targetOffset(" + dx + "," + dy +")";
     return ret;
   }

   public Location getTargetOffset(){
      return new Location(dx, dy);
   }

   public String getFilename(){
      return imgURL;
   }

}

