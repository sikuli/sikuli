Pattern
=======

.. py:class:: Pattern

A pattern is used, to associate an image file with additional attributes used in find
operations and when acting on a match object.

**Minimum Similarity:** 

While using a :py:meth:`Region.find` operation, 
if only an image file is provided, Sikuli searches
the region using a default minimum similarity of 0.7.
This default value can be changed in :py:attr:`Settings.MinSimilarity`.

Using :py:meth:`similar() <Pattern.similar>` you can associate a specific similarity
value, that will be used as the minimum value, when this pattern object is searched. 
The IDE supports adjusting the minimum similarity of captured images using the Preview Pane
(internally in the script, the images are turned into a pattern object automatically).

**Click Point:**

Normally when clicking on a match, the center pixel of the associated
rectangle is used. With a pattern object, you can define a different click point 
relative to the center using :py:meth:`targetOffset() <Pattern.targetOffset>`.

.. py:class:: Pattern

	.. py:method:: Pattern(string)

		:param string: a path to an image file
		:return: a new pattern object

		This will initialize a new pattern object without any additional attributes.
		As long as no pattern methods are used additionally, it is the same as just
		using the image file name itself in the find operation.

	.. py:method:: similar(similarity)

		Return a new Pattern object containing the same attributes (image, click
		point) with the minimum similarity set to the specified value.

		:param similarity: the minimum similarity to use in a find operation. The
			value should be between 0 and 1.
		:return: a new pattern object

	.. py:method:: exact()

		Return a new Pattern object containing the same attributes (image, click
		point) with the minimum similarity set to 1.0, which means exact match is
		required.

		:return: a new pattern object

	.. py:method:: targetOffset(dx, dy)

		Return a new Pattern object containing the same attributes (image,
		similarity), but a different definition for the click. By
		default, the click point is the center of the found match. By setting the
		target offset, it is possible to specify a click point other than the
		center. *dx* and *dy* will be used to calculate the position relative to the
		center.

		:param dx: x offset from the center
		:param dy: y offset from the center
		:return: a new pattern object

	.. py:method:: getFilename()

		Get the filename of the image contained in the Pattern object.

		:return: a filename as a string

	.. py:method:: getTargetOffset()

		Get the target offset of the Pattern object.

		:return: a :py:class:`Location` object as the target offset
