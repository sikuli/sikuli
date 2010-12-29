from sphinx import addnodes
from sphinx.util.compat import Directive
from sphinx.util.compat import make_admonition
from docutils import nodes

class platform_node(nodes.Admonition, nodes.Element): pass

class PlatformDirective(Directive):
    has_content = True
    required_arguments = 1
    optional_arguments = 0
    final_argument_whitespace = True
    option_spec = {}

    def run(self):
        ret = make_admonition(
            platform_node, self.name, [self.arguments[0]], self.options,
            self.content, self.lineno, self.content_offset, self.block_text,
            self.state, self.state_machine)
        return ret


def MakePlatformDirective(platform):
   class CustomPlatformDirective(Directive):
       has_content = True
       required_arguments = 0
       optional_arguments = 0
       final_argument_whitespace = True
       option_spec = {}

       def run(self):
           ret = make_admonition(
               platform_node, self.name, [platform], self.options,
               self.content, self.lineno, self.content_offset, self.block_text,
               self.state, self.state_machine)
           return ret
   return CustomPlatformDirective


def visit_platform_node(self, node):
    self.visit_admonition(node)

def depart_platform_node(self, node):
    self.depart_admonition(node)

def setup(app):
   app.add_node(platform_node,
                html=(visit_platform_node, depart_platform_node),
                latex=(visit_platform_node, depart_platform_node),
                text=(visit_platform_node, depart_platform_node),
                man=(visit_platform_node, depart_platform_node))

   app.add_directive('platform', PlatformDirective)
   app.add_directive('windows', MakePlatformDirective('Windows'))
   app.add_directive('mac', MakePlatformDirective('Mac OS X'))
   app.add_directive('linux', MakePlatformDirective('Linux'))
