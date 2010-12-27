import sys, cgi
import glob
import re
import keyword, token, tokenize
import string, cStringIO, StringIO

SIKULI_KEYWORDS = [
	"find", "wait",
	"click", "clickAll", "repeatClickAll", "doubleClick",
	"doubleClickAll", "repeatDoubleClickAll", "rightClick",
	"dragDrop", "type", "sleep", "popup", "capture", "input",
	"assertExist", "assertNotExist" ]

HEADER = """
<pre class="sikuli-code">
"""


FOOTER = """
</pre>
"""

_KEYWORD = token.NT_OFFSET + 1
_SIKULI_KEYWORD = token.NT_OFFSET + 3

_colors = {
	  token.NUMBER:		  'dig',
	  token.STRING:		  'str',
	  tokenize.COMMENT:   'cmt',
	  _KEYWORD:			  'kw',
	  _SIKULI_KEYWORD:	  'skw',
}

if locals().has_key('local_convert'):
   LOCAL_CONVERT = True
else:
   LOCAL_CONVERT = False

class Parser:

	def __init__(self, raw, out = sys.stdout):
		self.raw = string.strip(raw.expandtabs(4))
		self.out = out

	def format(self, filename):
		global HEADER
		# store line offsets in self.lines
		self.lines = [0, 0]
		pos = 0
		while 1:
		   pos = string.find(self.raw, '\n', pos) + 1
		   if not pos: break
		   self.lines.append(pos)
		self.lines.append(len(self.raw))

		# parse the source and write it
		self.pos = 0
		text = StringIO.StringIO(self.raw)
		HEADER = HEADER.replace("$FILE", filename)
		if LOCAL_CONVERT:
		   HEADER = HEADER.replace("$HIDE_INFO", "display: none;")
		self.out.write(HEADER)
		try:
		   tokenize.tokenize(text.readline, self)
		except tokenize.TokenError, ex:
		   msg = ex[0]
		   line = ex[1][0]
		   self.out.write("<h3>ERROR: %s</h3>%s\n" % (
			  msg, self.raw[self.lines[line]:]))
		   self.out.write('</font></pre>')

		self.out.write(FOOTER)

	def __call__(self, toktype, toktext, (srow,scol), (erow,ecol), line):
		if 1:
		   print "type", toktype, token.tok_name[toktype], "text", toktext,
		   print "start", srow,scol, "end", erow,ecol, "<br>"

		# calculate new positions
		oldpos = self.pos
		newpos = self.lines[srow] + scol
		self.pos = newpos + len(toktext)

		print "%d-%d" % (oldpos, newpos)
		# handle newlines
		if toktype in [token.NEWLINE, tokenize.NL]:
		   self.out.write('\n')
		   return

		# skip indenting tokens
		# hack to force tabspace = 4
		if toktype in [token.INDENT]:#, token.DEDENT]:
		   #newpos = newpos/2
		   newpos = self.pos - len(toktext)/2
		   self.out.write(self.raw[oldpos:newpos])
		   print "[I]%d-%d" % (oldpos, newpos)
		   return

	   # send the original whitespace, if needed
		if newpos > oldpos:
		   self.out.write(self.raw[oldpos:newpos])

		# map token type to a color group
		if token.LPAR <= toktype and toktype <= token.OP:
		   toktype = token.OP
		elif toktype == token.NAME and keyword.iskeyword(toktext):
		   toktype = _KEYWORD
		elif toktype == token.NAME and toktext in SIKULI_KEYWORDS:
		   toktype = _SIKULI_KEYWORD
		color = ''
		if toktype in _colors:
		   color = _colors.get(toktype)

		if toktype == token.STRING and toktext.endswith(".png\""):
		   m = re.search('[\'\"](.*)[\'\"]',toktext)
		   filename = m.group(1) 
		   self.out.write('<img src="images/' + filename + '"/>')
		   return

		if color:
		   self.out.write('<span class="%s">' % (color))
		   self.out.write(cgi.escape(toktext))
		   self.out.write('</span>')
		else:
		   self.out.write(cgi.escape(toktext))


from sphinx.util.compat import Directive
from docutils import nodes

class SikuliCodeDirective(Directive):
	required_arguments = 0 
	optional_arguments = 0
	final_argument_whitespace = True
	#option_spec = dict([(key, directives.flag) for key in VARIANTS])
	has_content = True

	def run(self):
		self.assert_has_content()
		#txt = "<b>%s</b>" % self.content
		#print ' '.join(self.content)
		#x = self.content[0]
		#x = x.encode('ascii'
		txtout = StringIO.StringIO()
		txtin = '\n'.join(self.content)
		p = Parser(txtin,txtout)
		p.format('test')
		#print txti
		print txtout.getvalue()
		txt = txtout.getvalue()
		return [nodes.raw('', txt, format='html')]



def setup(app):
	app.add_directive('sikulicode', SikuliCodeDirective)
