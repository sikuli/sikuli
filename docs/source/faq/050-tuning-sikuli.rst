Sikuli is too slow or not robust. What can I do?
================================================

You can tune a parameter of the vision algorithm, :ref:`MinTargetSize <min-target-size>`, to speed up the matching process or make it more robust.


.. sikulicode::

   from org.sikuli.script.natives import Vision

   Vision.setParameter("MinTargetSize", 6) # A small value such as 6 makes the matching algorithm be faster.
   Vision.setParameter("MinTargetSize", 18) # A large value such as 18 makes the matching algorithm be more robust.


