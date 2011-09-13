Key Constants
=============

.. py:class:: Key

Applicable usage situations for these predefined constants of special keys and key
modifiers can be found in :ref:`Acting on a Region <ActingonaRegion>` and :ref:`Low
Level Mouse and Keyboard Actions <LowLevelMouseAndKeyboardActions>`.


Special Keys
------------

The methods supporting the use of special keys are :py:meth:`type() <Region.type>`,
:py:meth:`keyDown() <Region.keyDown>`, and :py:meth:`keyUp() <Region.keyUp>`.

Usage: `Key.CONSTANT` (where CONSTANT is one of the following key names).

String concatenation with with other text or other key constants is possible using "+". ::

	type("some text" + Key.TAB + "more text" + Key.TAB + Key.ENTER)
	# or eqivalent
	type("some text\tmore text\n")	

**miscellanous keys** ::

	ENTER, TAB, ESC, BACKSPACE, DELETE, INSERT

.. versionadded:: X1.0-rc3

**miscellanous keys** ::

	SPACE

**function keys** ::

	F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15

**navigation keys** ::

	HOME, END, LEFT, RIGHT, DOWN, UP, PAGE_DOWN, PAGE_UP

**special keys** ::

	PRINTSCREEN, PAUSE, CAPS_LOCK, SCROLL_LOCK, NUM_LOCK

.. versionadded:: X1.0-rc2
	
**Note:** The status ( on / off ) of the keys ``Key.CAPS_LOCK``, ``Key.NUM_LOCK`` and ``Key.SCROLL_LOCK`` can 
be evaluated with the method :py:meth:`Env.isLockOn() <Env.isLockOn>`.

**numpad keys** ::

	NUM0, NUM1, NUM2, NUM3, NUM4, NUM5, NUM6, NUM7, NUM8, NUM9
	SEPARATOR, ADD, MINUS, MULTIPLY, DIVIDE

**modifier keys** ::

	ALT, CMD, CTRL, META, SHIFT, WIN

These modifier keys **cannot** be used as a key modifier with functions
like :py:meth:`type() <Region.type>`, :py:meth:`rightClick() <Region.rightClick>`, etc. 
They can **only** be used with :py:meth:`keyDown() <Region.keyDown>` and :py:meth:`keyUp() <Region.keyUp>`.
If you need key modifiers, use :py:class:`KeyModifier` instead.

Key Modifiers
-------------

Methods where key modifiers can be used include: :py:meth:`click() <Region.click>`,
:py:meth:`dragDrop() <Region.dragDrop>` , :py:meth:`doubleClick()
<Region.doubleClick>` , :py:meth:`rightClick() <Region.rightClick>`,
:py:meth:`type() <Region.type>`.

.. deprecated:: X1.0-rc3

**the oldies but goldies** ::

	KEY_ALT, KEY_CTRL, KEY_SHIFT

**system specific Win/Mac** ::

	KEY_WIN, KEY_CMD 
	KEY_META (a synonym for KEY_WIN or KEY_CMD on Windows and Mac respectively).

The old modifiers with a *KEY_* prefix are deprecated. Use ``KeyModifier.CTRL``, ``KeyModifier.ALT``, ``KeyModifier.SHIFT``, ``KeyModifier.META`` instead.


.. versionadded:: X1.0-rc3
.. py:class:: KeyModifier

Usage: `KeyModifier.CONSTANT` (where CONSTANT is one of the following key names).

   .. py:data:: CTRL
      equivalent to the old KEY_CTRL
   .. py:data:: SHIFT
      equivalent to the old KEY_SHIFT
   .. py:data:: ALT
      equivalent to the old KEY_ALT
   .. py:data:: META
      equivalent to the old KEY_META
   .. py:data:: CMD
      equivalent to the old KEY_CMD (and KEY_META)
   .. py:data:: WIN
      equivalent to the old KEY_WIN (and KEY_META)

	
The modifier constants can be combined to the modifier parameter by either using "+" or "|", if more than one key modifier is needed. ::

	type(Key.ESC, KeyModifier.CTRL + KeyModifier.ALT)
	# or equivalent
	type(Key.ESC, KeyModifier.CTRL | KeyModifier.ALT)

They should **only** be used in the
modifiers parameter with functions like :py:meth:`type() <Region.type>`, :py:meth:`rightClick() <Region.rightClick>`, etc. 

They should **never** be used with :py:meth:`keyDown() <Region.keyDown>` or :py:meth:`keyUp() <Region.keyUp>`.

*Note for Java programming*: These constants are mapped to the according constants of the Java environment
in the class ``java.awt.event.InputEvent``. 

