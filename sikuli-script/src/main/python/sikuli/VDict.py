from edu.mit.csail.uid import VDictProxy
import java.io.File

## 
# VDict implements a visual dictionary that has Python's conventional dict
# interfaces.
#
# A visual dictionary is a data type for storing key-value pairs using 
# images as keys. Using a visual dictionary, a user can easily automate 
# the tasks of saving and retrieving arbitrary data objects by images. 
# The syntax of the visual dictionary data type is modeled after that of 
# the built-in Python dictionary data type.

class VDict(VDictProxy):

   ##
   # the default similarity for fuzzy matching. The range of this is from
   # 0 to 1.0, where 0 matches everything and 1.0 does exactly matching.
   # <br/>
   # The default similarity is 0.7.
   _DEFAULT_SIMILARITY = 0.7

   _DEFAULT_GET_ITEM_N = 0
   _bundlePath = "."
   _keys = {}

   ##
   # Constructs a new visual dictionary with the same mapping as the given dict.
   #
   def __init__(self, dict=None):
      if dict:
         for k in dict.keys():
            self[k] = dict[k]

   def _setBundlePath(cls, path):
      VDict._bundlePath = path

   _setBundlePath = classmethod(_setBundlePath)

   def _getInBundle(self, f):
      if java.io.File(f).isAbsolute(): return f
      return self._bundlePath + "/" + f

   ##
   # Returns the number of keys in this visual dictionary.
   #
   def __len__(self):
      return self.size()

   ##
   # Maps the specified key to the specified item in this visual dictionary.
   #
   def __setitem__(self, key, item):
      bkey = self._getInBundle(key)
      self.insert(bkey, item)
      self._keys[key] = item

   ##
   # Tests if the specified object looks like a key in this visual dictionary
   # with the default similarity.
   #
   def __contains__(self, key):
      return len(self.get(key)) > 0

   ##
   # Returns all values to which the specified key is fuzzily matched in 
   # this visual dictionary with the default similarity.
   # <br/>
   # This is a wrapper for the {@link #VDict.get get} method.
   def __getitem__(self, key):
      return self.get(key)

   ##
   # Deletes the key and its corresponding value from this visual dictionary.
   #
   def __delitem__(self, key):
      bkey = self._getInBundle(key)
      self.erase(bkey)
      del self._keys[key]

   ##
   # Returns a list of the keys in this visual dictionary.
   #
   def keys(self):
      return self._keys.keys()

   ##
   # Returns the value to which the specified key is exactly matched in 
   # this visual dictionary.
   #
   def get_exact(self, key):
      if key == None: return None
      key = self._getInBundle(key)
      return self.lookup(key)

   ##
   # Returns the values to which the specified key is fuzzily matched in 
   # this visual dictionary with the given similarity and the given maximum 
   # number of return items.
   # @param similarity the similarity for matching.
   # @param n maximum number of return items.
   #
   def get(self, key, similarity=_DEFAULT_SIMILARITY, n=_DEFAULT_GET_ITEM_N):
      if key == None: return None
      key = self._getInBundle(key)
      return self.lookup_similar_n(key, similarity, n)

   ##
   # Returns the value to which the specified key is best matched in 
   # this visual dictionary with the given similarity.
   # @param similarity the similarity for matching.
   #
   def get1(self, key, similarity=_DEFAULT_SIMILARITY):
      if key == None: return None
      key = self._getInBundle(key)
      return self.lookup_similar(key, similarity)
   

