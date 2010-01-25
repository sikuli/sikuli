package edu.mit.csail.uid;

public class Pattern {
   protected String imgURL = null;
   protected Pattern parent = null;
   protected float similarity = 0.7f;
   protected int numMatches = 10;

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

   public Pattern firstN(int numMatches_){
      numMatches = numMatches_;
      return this;
   }

   public String toString(){
     String ret = "Pattern(\"" + imgURL + "\")";
     ret += ".similar(" + similarity +")";
     ret += ".firstN(" + numMatches + ")";
     return ret;
   }

}

