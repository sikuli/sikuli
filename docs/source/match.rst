Match
=====

An object of class Match represents the result of a successful find operation. It
has the rectangle dimension of the image, that was used to search. It knows the
point of its upper left corner on an existing monitor, where it was found. You can
act on it using the applicable methods of Class :py:class:`Region`. 

Methods of Match
----------------

Since the class Match extends class :py:class:`Region`, all methods of the Region
class canbe used with a match object.

Creating a Match, Getting Attributes
------------------------------------

A match object is created as the result of an explicit :ref:`find operation
<FindinginsideaRegionandWaitingforaVisualEvent>`. It can be
saved in a variable for later use with actions like click().

It has the rectangle dimension of the image, that was used to search. It knows the
point of its upper left corner on an existing monitor, where it was found. It knows
the similarity it was found with and a click point to be used, if set by a pattern. 


