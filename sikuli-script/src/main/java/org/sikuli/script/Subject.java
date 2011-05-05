/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

public interface Subject {
   public void addObserver(Observer o);
   public void notifyObserver();
}

