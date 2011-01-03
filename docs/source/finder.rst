Finder
======

.. py:class:: Finder

A Finder object implements an iterator of matches and allows to search for a visual
object in an image file that you provide (e.g. a screenshot taken and saved in a
file before). After setting up the finder object and doing a find operation, you can
iterate through the found matches if any.

Important to know:

*	per definition, an iterator can be stepped through only once - it is empty
	afterwards
*	it has to be destroyed using ``finder.destroy()``, especially when
	used with ``for:`` or ``while:``
*	when used in a ``with:`` construct, it is destroyed automatically

Compared with the region based find operation, no exception FindFailed is
raised in case nothing is found at all (use ``hasNext()`` to check). The finder object 
can be compared to what you get with ``region.getLastMatches()`` when using :py:meth:`findAll() <Region.findAll>`.

**Note**: With this version, there is no chance, to get the number of matches in
advance. If you would iterate through to count, afterwards your finder would be
empty. So in this case, you have to save your matches somehow (one possible solution
see example below).

The workflow always is, that you first do a find operation and afterwards go through the
matches found. After a complete iteration, the finder object is empty. So you
could start a new find operation again.

.. py:class:: Finder

	.. py:method:: Finder(path-to-imagefile)

		Create a new finder object.

		:param path-to-imagefile: filename to a source image to search within
	
	.. py:method:: find(path-to-imagefile, [similarity])

		Find a given image within a source image previously specified in the
		constructor of the finder object.
		
		:param path-to-imagefile: the target image to search for
		:param similarity: the minimum similarity a match should have. If omitted,
			the default is used.
	
	.. py:method:: hasNext()

		Check whether there are more matches available that satisfy the minimum
		similarity requirement.

		:return: *True* if more matches exist.

	.. py:method:: next()

		Get the next match. 

		:return: a :py:class:`Match` object.

		The returnd reference to a match object is no longer available in the finder
		object afterwards. So if it is needed later on, it has to be saved to
		another variable.


Example 1: basic operations using a Finder

.. sikulicode::
	
	# create a Finder with your saved screenshot
	f = Finder("stars.png")
	img= "star.png" # the image you are searching
	
	f.find(img) # find all matches
	
	while f.hasNext(): # loop as long there is a first and more matches
		print "found: ", f.next() # access the next match in the row
	
	print f.hasNext() # is False, because f is empty now
	f.destroy() # release the memory used by finder
	
Example 2: we want to know how many matches (based on the previous example).

.. sikulicode::
	
	# create a Finder with your saved screenshot
	f = Finder("stars.png")
	img= "star.png" # the image you are searching
	
	f.find(img) # find all matches
	mm = [] # an empty list

	while f.hasNext(): # loop as long there is a first and more matches
		mm.append(f.next())	# access next match and add to mm

	print f.hasNext() # is False, because f is empty now
	f.destroy() # release the memory used by finder
	
	# now we have our matches saved in the list mm
	print len(mm) # the number of matches

	# we want to use our matches
	for m in mm:
		print m 