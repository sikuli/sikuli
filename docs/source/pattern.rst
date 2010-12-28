Pattern
=======

A pattern is used, to associate an image file with additional attributes used in find
operations and when acting on a match object.

**Minimum similarity:** When using just an image file in a find operation, the search
will be successful and return a match object, if the similarity of a possible match
is 0.7 or higher. With a pattern object, you can associate a specific similarity
value, that will be used as the minimum value, when this pattern object is searched
(similar(), exact()). The IDE supports adjusting the minimum similarity with
captured images (internally in the script, the images are turned into a pattern
object definition)

click point: normally when clicking on a match, the center pixel of the associated
rectangle is used. With a pattern object, you can define a different click point
(targetOffset()).

.. py:class:: Pattern

	.. py:method:: Pattern(string)

		:param string: a path to an image file
		:return: a new pattern object

		This will initialize a new pattern object without any additional attributes.
		As long as no pattern methods are used additionally, it is the same as just
		using the image file name itself in the find operation.

	.. py:method:: similar(similarity)

		Derive a new Pattern object containing the same attributes (image, click
		point) with the minimum similarity set to the specified value.

		:param similarity: the minimum similarity to use in a find operation. The
			value should be between 0 and 1.
		:return: a new pattern object

	.. py:method:: exact()

		Derive a new Pattern object containing the same attributes (image, click
		point) with the minimum similarity set to 1.0, which means exact match is
		required.

		:return: a new pattern object

	.. py:method:: targetOffset(dx, dy)

		Derive a new Pattern object containing the same attributes (image,
		similarity), but a different definition for the click point can be used. By
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

		Get the target of set of the Pattern object.

		:return: a :py:class:`Location` object indicating the target offset
