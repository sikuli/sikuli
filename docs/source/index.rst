.. sikuli documentation master file, created by
   sphinx-quickstart on Thu Dec 23 11:32:37 2010.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Sikuli Documentation
====================

.. sidebar:: Getting Help

   Looking for specific information?
   
   * Try the :doc:`Table of Contents <toc>` 
   * Look through the :ref:`genindex`
   * Use the :ref:`search`
   
   See `other people's questions <https://answers.launchpad.net/sikuli>`_ 
   or `ask a question <https://answers.launchpad.net/sikuli/+addquestion>`_ yourself.
   
   If you think you've found bugs, search or report bugs in 
   our `bug tracker <https://bugs.launchpad.net/sikuli>`_.

This document is being maintained by the `Sikuli Doc Team
<https://launchpad.net/~sikuli-doc/+members>`_.  

If you have any questions or ideas about this document, 
you are welcome to `contact us <https://launchpad.net/~sikuli-doc>`_. 

For questions regarding the
functions and features of Sikuli itself please use the `Sikuli Questions and
Answers Board <https://answers.launchpad.net/sikuli>`_.  

For hints and links of
how to get more information and help, please see the sidebar.

How to use this document
------------------------

Sikuli Script is built as a Jython (Python for the Java platform)
library. You can use any syntax of the Python language.  If you are new to
programming, you can still enjoy using Sikuli to automate simple repetitive
tasks without learning Python. A good start might be to have a look at the :doc:`tutorials <tutorials/index>`.

However, if you would like to write more powerful and
complex scripts, which might even be structured in modules, you have to dive into the `Python Language
<http://jythonpodcast.hostjava.net/jythonbook/en/1.0/>`_.

The preface of each chapter in this documentaton briefly describes 
a class or a group of methods. It
provides general usage information and hints that apply to all methods in 
that chapter. We recommend to read it through before using the related features.

**If you are totally new with Sikuli**, it might be a good idea to just read
through this documentation sequentially. An alternative way might be to jump to the
chapters that you are interested in by scanning the :doc:`table of contents <toc>`. 
A way in the middle would be reading the core classes:
:py:class:`Region`, then :py:class:`Match`, and finally :py:class:`Screen`.

**For the users of the previous versions (0.9.x and 0.10.x)** 
:doc:`What's new in Sikuli X <new-in-sikulix>` is a good start. 
After that, you can go to any
places of interest using the :doc:`table of contents<toc>` or 
use the :ref:`genindex` to browse all classes, 
methods and functions in alphabetical order.

Getting Started
---------------

Tutorials
^^^^^^^^^

.. toctree::
   :maxdepth: 2

   tutorials/index

* Slide: `Practical Sikuli: using screenshots for GUI automation and testing <http://www.slideshare.net/vgod/practical-sikuli-using-screenshots-for-gui-automation-and-testing>`_.

FAQ
^^^
.. toctree::
   :maxdepth: 1
   :glob:

   faq/*
   
* `Read more FAQs on Launchpad <https://answers.launchpad.net/sikuli/+faqs>`_



Complete Guide to Sikuli Script
-------------------------------
.. toctree::
   :maxdepth: 3

   new-in-sikulix
   sikuli-script-index

Extensions
----------
.. toctree::
   :maxdepth: 2

   extensions/index



For Hackers and Developers
--------------------------

.. toctree::
   :maxdepth: 2
   :glob:

   devs/*
   contributing
   changes


Academic Papers
---------------

* `Sikuli: Using GUI Screenshots for Search and Automation <http://groups.csail.mit.edu/uid/projects/sikuli/sikuli-uist2009.pdf>`_, UIST 2009 (PDF).
* `GUI Testing Using Computer Vision <http://groups.csail.mit.edu/uid/projects/sikuli/sikuli-chi2010.pdf>`_, CHI 2010 (PDF).


Great 3rd-Party Articles 
------------------------

* `How-To: Sikuli and Robot Framework Integration <http://blog.mykhailo.com/2011/02/how-to-sikuli-and-robot-framework.html>`_.
* `Automating Flash, AJAX, Popups and more using Ruby, Watir and Sikuli <http://www.software-testing.com.au/blog/2010/08/16/automating-flash-ajax-popups-and-more-using-ruby-watir-and-sikuli/>`_.

