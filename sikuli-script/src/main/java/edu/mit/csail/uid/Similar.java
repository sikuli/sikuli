package edu.mit.csail.uid;
public class Similar extends Pattern {
   public Similar(Pattern parent_, float similarity_){
      parent = parent_;
      imgURL = parent_.imgURL;
      similarity = similarity_;
   }
}


