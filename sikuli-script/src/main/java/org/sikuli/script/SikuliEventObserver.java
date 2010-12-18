package org.sikuli.script;

import java.util.*;


public interface SikuliEventObserver extends EventListener {
   public void targetAppeared(AppearEvent e);
   public void targetVanished(VanishEvent e);
   public void targetChanged(ChangeEvent e);
}
