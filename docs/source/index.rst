.. sikuli documentation master file, created by
   sphinx-quickstart on Thu Dec 23 11:32:37 2010.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Sikuli Documentation
====================

.. sidebar:: Getting Help

   * Looking for specific information?
     Try the :doc:`table of contents <toc>`, 
     :ref:`genindex`, or :ref:`search`.
   * See `other people's questions <https://answers.launchpad.net/sikuli>`_,
     or `ask a question <https://answers.launchpad.net/sikuli/+addquestion>`_.
   * If you think you've found bugs, search or report bugs in 
     our `bug tracker <https://bugs.launchpad.net/sikuli>`_.

This document was set up and is being maintained by `RaiMan
<https://launchpad.net/~raimund-hocke>`_ (Raimund Hocke) with the great support
by *Tsung-Hsiang (Sean) Chang* (one of the Sikuli developers).  If you have any
questions or ideas about this document, you are welcome to directly contact
*RaiMan* using the mail address on his `personal Sikuli Launchpad page
<https://launchpad.net/~raimund-hocke>`_.  For questions regarding the
functions and features of Sikuli itself please use the `Sikuli Questions and
Answers Board <https://answers.launchpad.net/sikuli>`_.  For hints and links of
how to get more information and help, please see [#otherplaces Other places to
get Information] in this document.

How to use this document
------------------------

Since Sikuli Script is built as a Jython (Python for the Java platform)
library, you can use any syntax of the Python language.  If you are new to
programming, you can still enjoy using Sikuli to automate simple repetitive
tasks without learning Python. But if you would like to write more powerful and
complicated scripts, you may want to dive into the `Python Language
<http://jythonpodcast.hostjava.net/jythonbook/en/1.0/>`_.

The preface of a chapter briefly describes a class or a group of methods. It
contains general usage and hints that apply to all methods in that chapter. We
recommend you to read it before using those methods.

**If you are totally new with Sikuli**, it would be a good idea to just read
through this document sequentially. An alternative may be to jump to the
chapters that you are interested in by scanning the table of contents. In any
case, it’s’‘strongly recommended’’ to carefully read through this entry chapter
**Basics**. A way in the middle would be, going to Class :py:class:`Region`,
then to Class :py:class:`Match` and ending at Class :py:class:`Screen`.

**For the users of previous versions (0.9.x and 0.10.x)** :doc:`What's new in
Sikuli X <new-in-sikulix>` is a good start. After that, you can go to any
places of interest using the table of contents below or use the [#navigator
navigator] to browse all classes, methods and functions in alphabetical order
(as every class shows its methods this way at the beginning of its chapter).



Getting Started
---------------

Tutorials
^^^^^^^^^

.. toctree::
   :maxdepth: 2

   tutorials/index

FAQ
^^^
.. toctree::
   :maxdepth: 1
   :glob:

   faq/*



Complete Guide to Sikuli Script
-------------------------------
.. toctree::
   :maxdepth: 3

   new-in-sikulix
   globals
   region
   screen
   location
   match
   pattern
   finder
   keys



For Hackers and Developers
--------------------------

.. toctree::
   :maxdepth: 2

   devs/index
   contributing
   changes


