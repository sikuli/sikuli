Can I do X or Y or Z in Sikuli?
=======================

If you are wondering if Sikuli can do X or Y, there are few rules that may answer you.

* If you can do X with Java, you can also do it in Sikuli. For example, you can create a GUI with Java Swing, so you can do it in the same way in Sikuli. 
* If you can do X with Python, you probably can do it in Sikuli as well. This actually depends on what Python modules you use. If the modules are written in pure Python, you can use them in Sikuli. If they are written in C, unfortunately, you can't.
Can I write a loop in Sikuli?
-----------------------------

Yes. Sikuli is based on Jython. 
You can use all constructs that are available in Jython. See Jython's `While loop <http://www.jython.org/jythonbook/en/1.0/LangSyntax.html#while-loop>`_ and `For loop <http://www.jython.org/jythonbook/en/1.0/LangSyntax.html#for-loop>`_.


Can I create a GUI in Sikuli?
-----------------------------

Yes, you can create GUIs with Java Swing or any other Java/Jython GUI toolkits.
See `Jython's Swing examples <http://wiki.python.org/jython/SwingExamples>`_ for examples.



Can I connect to MySQL/MS SQL/PostgreSQL or any database systems in Sikuli?
-----------------------------------------------------------------

You can use `JDBC <http://www.oracle.com/technetwork/java/javase/jdbc/index.html>`_ or `zxJDBC <http://www.jython.org/jythonbook/en/1.0/DatabasesAndJython.html>`_.


Can I read/write files in Sikuli?
---------------------------------

Yes. See Jython's `File I/O <http://www.jython.org/jythonbook/en/1.0/InputOutput.html#file-i-o>`_.


