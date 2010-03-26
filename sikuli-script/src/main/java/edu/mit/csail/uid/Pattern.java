package edu.mit.csail.uid;

public class Pattern {
   protected String imgURL = null;
   protected float similarity = 0.7f;

   int dx=0, dy=0;

   public Pattern(){ }
   public Pattern(String imgURL_){
      imgURL = imgURL_;
   }

   public Pattern similar(float similarity_){
      similarity = similarity_;
      return this;
   }

   public Pattern exact(){
      similarity = 1.0f;
      return this;
   }

   public Pattern targetOffset(int dx_, int dy_){
      dx = dx_;
      dy = dy_;
      return this;
   }

   public String toString(){
     String ret = "Pattern(\"" + imgURL + "\")";
     ret += ".similar(" + similarity +")";
     return ret;
   }

   Location getTargetOffset(){
      return new Location(dx, dy);
   }

}

