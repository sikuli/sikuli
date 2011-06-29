# Copyright 2010-2011, Sikuli.org
# Released under the MIT License.

# Setup the environment
import os, sys, site, urllib2, tempfile
for p in sys.path:
   if os.path.isdir(p):
      TESTS_DIR = p
      break

cwd = os.getcwdu()
site_dir = os.path.join(os.getcwdu(), 'site-packages')
if not os.path.exists(site_dir): os.mkdir(site_dir)
site.addsitedir(site_dir)
sys.executable = ''
os.environ['PYTHONPATH'] = os.pathsep.join(sys.path)

# Get ez_setup:
ez_setup_path = os.path.join(site_dir, 'ez_setup.py')
if not os.path.exists(ez_setup_path):
    f = file(ez_setup_path, 'w')
    f.write(urllib2.urlopen('http://peak.telecommunity.com/dist/ez_setup.py').read())
    f.close()

# Install nose if not present
try:
    import nose
except ImportError:
    import ez_setup
    ez_setup.main(['--install-dir', site_dir, 'nose'])
    for mod in sys.modules.keys():
        if mod.startswith('nose'):
            del sys.modules[mod]
    for path in sys.path:
        if path.startswith(site_dir):
            sys.path.remove(site_dir)
    site.addsitedir(site_dir)
    import nose


print "find tests in ", TESTS_DIR
# Run Tests!
nose.run(argv=['nosetests', '-v', '--with-xunit', '--where='+TESTS_DIR, '--xunit-file='+cwd+'/TEST-Sikuli-Python-Suite.xml'] + sys.argv[1:])


