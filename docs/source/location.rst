Location
========

This class is there as a convenience, to handle single points on the screen directly
by its position (x, y). It is mainly used in the actions on a region, to directly
denote the click point. It contains methods, to *move* a point around on the screen. 

.. py:class:: Location

	.. py:method:: Location(x,y)
		
		:param x: x position
		:param y: y position

		:return: a new location object representing the position (x,y) on the screen


	.. py:method:: getX()
			getY()

		Get the x or y value of a location object

		It is possible to get the values directly by ``location.x`` or ``location.y``.
		It is also possible to set these values directly by ``location.x = value``
		or ``location.y = value``.
		
		**Note:** ``getX()``and ``getY()`` currently (versions 0.10.2 and X 1.0rc2) return float
		values (Java: double), whereas ``location.x`` and ``location.y`` return integer values.

	.. py:method:: setLocation(x, y)

		Set the location of this object to the specified coordinates.

	.. py:method:: offset(dx, dy)

		Get a new location which is *dx* and *dy* pixels away horizontally and
		vertically from the current location.

	.. py:method:: above(dy)

		Get a new location which is *dy* pixels vertically above the current
		location.

	.. py:method:: below(dy)

		Get a new location which is *dy* pixels vertically below the current
		location.

	.. py:method:: left(dx)

		Get a new location which is *dx* pixels horizontally to the left of the
		current location.

	.. py:method:: right(dx)

		Get a new location which is *dx* pixels horizontally to the right of the
		current location.

	
	

