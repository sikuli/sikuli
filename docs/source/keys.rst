Key Constants
=============


Applicable usage situations for these predefined constants of special keys and key
modifiers can be found in :ref:`Acting on a Region <ActingonaRegion>` and :ref:`Low
Level Mouse and Keyboard Actions <LowLevelMouseAndKeyboardActions>`.

Key Modifiers
-------------

Methods where key modifiers can be used include: :py:meth:`click() <Region.click>`,
:py:meth:`dragDrop() <Region.dragDrop>` , :py:meth:`doubleClick()
<Region.doubleClick>` , :py:meth:`rightClick() <Region.rightClick>`,
:py:meth:`type() <Region.type>`.

**the oldies but goldies**

KEY_ALT, KEY_CTRL, KEY_SHIFT

**system specific Win/Mac**

KEY_WIN, KEY_CMD, KEY_META (a synonym for KEY_WIN or KEY_CMD on
	Windows and Mac respectively).

Note: These constants are mapped to the according constants of the Java environment
in the class java.awt.event.InputEvent. They should only be used only as the
modifiers parameter in functions like type(), click(), etc. 
They should never be used with keyDown() and keyUp().

Special Keys
------------

The methods supporting the use of special keys are :py:meth:`type() <Region.type>`,
:py:meth:`keyDown() <Region.keyDown>`, and :py:meth:`keyUp() <Region.keyUp>`.

Usage: `Key.CONSTANT` (where CONSTANT is one of the following key names).
Concatenation with strings with "+" can be used.

**miscellanous keys**

ENTER, TAB, ESC, BACKSPACE, DELETE, INSERT

**function keys**

F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15

**navigation keys**

HOME, END, LEFT, RIGHT, DOWN, UP, PAGE_DOWN, PAGE_UP

**special keys**

PRINTSCREEN, PAUSE, CAPS_LOCK, SCROLL_LOCK, NUM_LOCK

**numpad keys**

NUM0, NUM1, NUM2, NUM3, NUM4, NUM5, NUM6, NUM7, NUM8, NUM9, SEPARATOR, ADD, MINUS,
MULTIPLY, DIVIDE

**key modifiers**

ALT, CMD, CTRL, META, SHIFT, WIN

Note: These key modifier constants can not be used as the parameter with functions
like type(), click(), etc. They can only be used with keyDown() and keyUp().
