#!/usr/bin/python
from __future__ import absolute_import
import launchpadlib
from launchpadlib.launchpad import Launchpad

FILE_TYPES = dict(tarball='Code Release Tarball',
                  readme='README File',
                  release_notes='Release Notes',
                  changelog='ChangeLog File',
                  installer='Installer file')

def upload_file(lp, project_name, release_version,
                filename, description,
                content_type, file_type,
                signature_filename=None):
    """Upload a file, and optionally its signature, to Launchpad.

    :param lp: Launchpad instance
    :param project_name: the project that owns the uploaded file
    :param release_version: the release the file will be associated, e.g. 1.0
    :param filename: the name of the file to be uploaded.  For this demo it is
                     the name that will be associated with the upload and the
                     file where the contents reside, in the current directory
    :param description: a short sentence describing the file like 'minimal
                        installer'
    :param content_type: the content MIME type, e.g. 'text/plain'
    :param file_type: one of the acceptable file types for upload as shown in
                      the FILE_TYPES dictionary.
    :param signature_filename: the optional name of the GPG signature file.
    """
    try:
        # Look up the project using the Launchpad instance.
        proj = lp.projects[project_name]
        # Find the release in the project's releases collection.
        release = None
        for rel in proj.releases:
            if rel.version == release_version:
                release = rel
                break
        assert release is not None, (
            "Release %s could not be found for project %s." % (release_version,
                                                               project_name))

        # Get the file contents.
        file_content = open(filename, 'r').read()
        # Get the signature, if available.
        if signature_filename is not None:
            signature_content = open(signature_filename, 'r').read()
        else:
            signature_content=None
        # Create a new product release file.
        product_release_file = release.add_file(filename=filename,
                                                description=description,
                                                file_content=file_content,
                                                content_type=content_type,
                                                file_type=file_type,
                                                signature_filename=signature_filename,
                                                signature_content=signature_content)
    except Exception, e:
        # Handle error here.
        print "An error happened in the upload."
        print e
        product_release_file = None

    return product_release_file is not None


MIME = { 'exe':'application/octet-stream',
         'zip':'application/zip',
         'dmg':'application/octet-stream'}
files = [
     {'file':'linux-x86_64.zip', 'desc':'Sikuli X (Linux 64bit)'},
     {'file':'linux.zip', 'desc':'Sikuli X (Linux 32bit)'},
     {'file':'osx-10.6.dmg', 'desc':'Sikuli X (Mac OS X 10.6)'},
     {'file':'win32.zip', 'desc':'Sikuli X (Windows portable zip)'},
     {'file':'win32.exe', 'desc':'Sikuli X (Windows installer)'},
]

project = 'sikuli'
version = 'x1.0-rc2'
lp = Launchpad.login_with('sikuli','production')
filename_prefix = "Sikuli-X-1.0rc2"
for f in files:
   filename = filename_prefix + "-" + f['file'] 
   signature_filename = filename + ".asc"
   description = f['desc']
   file_type = FILE_TYPES['installer']
   content_type = MIME[filename[-3:]]
   print "Uploading " + filename
   upload_file(lp, project, version, filename, description, content_type,
               file_type, signature_filename)
# The signature file is created by executing:
# gpg --armor --sign --detach-sig <file>
#signature_filename = 'README.txt.asc'

