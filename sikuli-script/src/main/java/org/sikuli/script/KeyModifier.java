/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.script;

import java.awt.event.InputEvent;

public class KeyModifier {
   public static int CTRL = InputEvent.CTRL_MASK;
   public static int SHIFT = InputEvent.SHIFT_MASK;
   public static int ALT = InputEvent.ALT_MASK;
   public static int META = InputEvent.META_MASK;
   public static int CMD = InputEvent.META_MASK;
   public static int WIN = InputEvent.META_MASK;

   @Deprecated
   public static int KEY_CTRL = InputEvent.CTRL_MASK;
   @Deprecated
   public static int KEY_SHIFT = InputEvent.SHIFT_MASK;
   @Deprecated
   public static int KEY_ALT = InputEvent.ALT_MASK;
   @Deprecated
   public static int KEY_META = InputEvent.META_MASK;
   @Deprecated
   public static int KEY_CMD = InputEvent.META_MASK;
   @Deprecated
   public static int KEY_WIN = InputEvent.META_MASK;
}

