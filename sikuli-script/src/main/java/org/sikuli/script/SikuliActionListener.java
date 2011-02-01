package org.sikuli.script;

import java.util.EventListener;

public interface SikuliActionListener extends EventListener{
   public void targetClicked(SikuliAction action);
   public void targetDoubleClicked(SikuliAction action);
   public void targetRightClicked(SikuliAction action);
}
