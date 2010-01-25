def setUp(self):
  openApp("/Applications/jEdit.app")
  wait("1251934858090.png", 20000)
  close = ("1252543927906.png")
#  if find(close):
 #   click(close)

def tearDown(self):
  closeApp("jEdit.app")
  untilNotExist("1251934858090.png", 20000)


def _test_textarea_add_del_by_key(self):
  type("hello world") #: 18 653 379
  assertExist("1251935855830.png") #: 36 335 258
  type("a",KEY_CTRL) #: 42 336 273
  type("\n") #: 62 343 306
  assertNotExist("1251935855830.png")

def _test_textarea_add_del_by_menu(self):
  type("hello world") #: 18 653 379
  assertExist("1251935855830.png") #: 36 335 258
  click("sikuli-tmp5380338041156422621.png") #: 61 272 199
  click("1252464678895.png") #: 61 311 364
  type("\n")
  assertNotExist("1251935855830.png")

def _test_new_tab_by_key(self):
  #switchApp("jEdit.app") #: 20 828 50
  type("n",KEY_CTRL) #: 83
  assertExist(Pattern("1252468449730.png").similar(0.95)) #: 92 697 238

def _test_new_tab_by_menu(self):
  #switchApp("jEdit.app") #: 20 828 50
  click("1252464885796.png")
  click("1252464913040.png")
  assertExist(Pattern("1252468449730.png").similar(0.95)) #: 92 697 238

def _test_new_tab_by_toolbar(self):
  click("1252465947795.png")
  assertExist(Pattern("1252468449730.png").similar(0.95)) #: 92 697 238


def _verify_find(self):
  sleep(0.5)
  type("hello") #: 64
  type("\n")
  click(Pattern("sikuli-tmp2975365024117744215.png").similar(0.90)) #: 75 656 474
  assertExist(Pattern("1251992827281.png").similar(0.94)) #: 89 238 292
  type("\b")
  assertExist("1252468957296.png")


def _test_find_by_key(self):
  type("a long hello world\n") #: 23 1633 235
  type("f",KEY_CTRL) #: 44
  self._verify_find()

def _test_find_by_menu(self):
  type("a long hello world\n") #: 23 1633 235
  click("1252465448664.png")
  click("1252465471331.png")
  self._verify_find()

def _test_find_by_toolbar(self):
  type("a long hello world\n") #: 23 1633 235
  click("1252465600816.png") #: 41 652 229
  self._verify_find()


def _test_textfield_on_toolbar(self):
  #switchApp("jEdit.app") #: 6
  type("a long hello world\n") #: 23 1633 235
  assertExist("1252465600816.png") #: 41 652 229
  type("f",KEY_CTRL) #: 44
  assertExist(Pattern("1251992001027.png").similar(0.85))
  type("hello") #: 64
  assertExist("sikuli-tmp4086672873866342028.png") #: 60 652 229



def _test_toolbar_print_dialog(self):
  #switchApp("jEdit.app") #: 27 709 394
  click("sikuli-tmp4352240503080313680.png") #: 23 333 222
  assertExist("sikuli-tmp8112869683331726330.png") #: 41 639 193
  click("sikuli-tmp4141222245757853401.png") #: 55 717 311


def _test_menu_submenu(self):
  #switchApp("jEdit.app") #: 0 349 246
  click("1252466202256.png") #: 3 332 202
  click("sikuli-tmp5401391059437403861.png") #: 14 336 437
  assertExist("sikuli-tmp7392942859563998493.png") #: 50 450 418
  type("test") #: 62 452 418
  click("sikuli-tmp9215889431322126220.png") #: 67 616 475
  click("1252466202256.png") #: 79 333 203
  click("sikuli-tmp2374069704297729209.png") #: 95 358 458
  click("sikuli-tmp6052924096894227588.png") #: 106 598 469
  click("1252466202256.png") #: 113 319 197
  click("1251991796848.png")
  assertExist("1251991815967.png")

def _test_scrollbar(self):
  for i in range(1,50):
    type("line %d\n" % i)
  tail = Pattern("1252459262893.png").similar(0.92)
  wait(tail, 10000)
  assertExist(tail)
  thumb_at_top = Pattern("1252460684431.png").similar(0.98)
  assertNotExist(thumb_at_top)
  dragDrop("1252459301599.png", [0, -1000])
  assertExist(thumb_at_top)
  assertNotExist(tail)
  assertExist(Pattern("1252459378483.png").similar(0.92))
  dragDrop(Pattern("1252461238330.png").similar(0.90), [0, 1000])
  assertExist(tail)

def test_quit(self):
  type("test") #: 14
  type("e",KEY_CTRL) #: 14
  type("q",KEY_CTRL) #: 16
  click("sikuli-tmp7290609466120197560.png") #: 27 784 503
  assertExist(Pattern("sikuli-tmp6679020069743006810.png").similar(0.90)) #: 37 177 288
