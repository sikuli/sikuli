import os
import re
import glob
import sys

def grep(string,list):
   expr = re.compile(string)
   return filter(expr.search,list)

if locals().has_key('bundle_path'):
   path = bundle_path
#path = sys.argv[1]

f_py = glob.glob(path + "/*.py")
pngFilter = re.compile("\"([^\"]+\.png)\"", re.I)
goodFiles = []

if len(f_py) > 0: 
   src = open(f_py[0], "r")
   for line in src:
      m = pngFilter.findall(line)
      if m:
         goodFiles += m
   src.close()
   for png in glob.glob(path + "/*.png"):
      if not os.path.basename(png) in goodFiles:
         os.remove(png)

