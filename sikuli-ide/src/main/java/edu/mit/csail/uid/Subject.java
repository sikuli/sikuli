package edu.mit.csail.uid;

public interface Subject {
   public void addObserver(Observer o);
   public void notifyObserver();
}

