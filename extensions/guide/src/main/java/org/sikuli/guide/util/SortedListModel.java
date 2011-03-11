package org.sikuli.guide.util;

import javax.swing.*;
import java.util.*;

public class SortedListModel extends AbstractListModel {
   SortedSet<Object> model;

   public SortedListModel() {
      model = new TreeSet<Object>();
   }

   public int getSize() {
      return model.size();
   }

   public Object getElementAt(int index) {
      return model.toArray()[index];
   }

   public void addElement(Object element) {
      if (model.add(element)) {
         fireContentsChanged(this, 0, getSize());
      }
   }
   public void addAllElements(Object elements[]) {
      Collection<Object> c = Arrays.asList(elements);
      model.addAll(c);
      fireContentsChanged(this, 0, getSize());
   }

   public void clear() {
      model.clear();
      fireContentsChanged(this, 0, getSize());
   }

   public boolean contains(Object element) {
      return model.contains(element);
   }

   public Object firstElement() {
      return model.first();
   }

   public Iterator iterator() {
      return model.iterator();
   }

   public Object lastElement() {
      return model.last();
   }

   public boolean removeElement(Object element) {
      boolean removed = model.remove(element);
      if (removed) {
         fireContentsChanged(this, 0, getSize());
      }
      return removed;
   }

   public void removeAllElements() {
      model.clear();
   }
}
