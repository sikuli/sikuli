package org.sikuli.ide;

public interface Subject {
   public void addObserver(Observer o);
   public void notifyObserver();
}

