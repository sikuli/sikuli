def setUp(self):
  openApp("/Applications/Capivara.app")
  wait("1251752520329.png", 20000)
  print "setUp"

def tearDown(self):
  closeApp("Capivara.app")
  untilNotExist("1251846232833.png", 20000)
  print "tearDown"

def _test_menu_exit_dialog(self):
  #switchApp("Capivara.app") #: 2
  click("sikuli-tmp3121548677422860342.png") #: 22 259 142
  click("sikuli-tmp1459249673637065831.png") #: 29 261 164
  assertExist("sikuli-tmp3897738172539716804.png") #: 59 655 399
  type("") #: 59
  click("sikuli-tmp551229748930756279.png") #: 75 629 460
  assertExist("sikuli-tmp6313803195505872338.png") #: 90 428 222
  click("sikuli-tmp3944229326861027201.png") #: 100 260 141
  click("sikuli-tmp720512291005162694.png") #: 115 265 170
  click("sikuli-tmp4455865577589413782.png") #: 124 697 457

def _test_text_changed_in_label_and_tab(self):
  #switchApp("Capivara.app") #: 3
  assertExist("sikuli-tmp3980760898137554984.png") #: 24 375 210
  assertExist("sikuli-tmp7696402253711967398.png") #: 32 273 644
  click("sikuli-tmp4376585662760091657.png") #: 38 211 244
  click("sikuli-tmp5498847969356587486.png") #: 50 531 484
  assertExist("sikuli-tmp5157198959230606765.png") #: 64 380 210
  assertExist("sikuli-tmp4567839860411636375.png") #: 71 277 644


def _test_new_host_in_favorites(self):
  #switchApp("Capivara.app") #: 1
  click("sikuli-tmp8637296554751145570.png") #: 9 269 256
  click("sikuli-tmp1954898772136863741.png") #: 19 840 325
  click("sikuli-tmp1293081230760374275.png") #: 27 546 541
  click("sikuli-tmp4940404680280374319.png") #: 36 623 297
  type("localhost\tuser") #: 51
  click("sikuli-tmp4809703967035719746.png") #: 55 635 396
  click("sikuli-tmp3901115475856231233.png") #: 59 635 386
  click("sikuli-tmp614756829548665374.png") #: 72 469 539
  type("\n") #: 80
  click("sikuli-tmp3563165220683511837.png") #: 96 470 270
  click("sikuli-tmp1437285562986891023.png") #: 105 720 541
  click("sikuli-tmp8955152716033326533.png") #: 115 600 495
  click("sikuli-tmp7209019535681747479.png") #: 124 435 220

def _test_cancel_connection_settings(self):
  #switchApp("Capivara.app") #: 7
  click("sikuli-tmp1925400289022233301.png") #: 15 265 255
  assertExist("1251852945606.png")
  click("sikuli-tmp7093734834872313447.png") #: 25 632 292
  click("sikuli-tmp3055440036675249305.png") #: 35 681 493

def _test_ok_connection_settings(self):
  #switchApp("Capivara.app") #: 2
  click("sikuli-tmp2866771075194708902.png") #: 11 268 261
  click("sikuli-tmp7693373236456551146.png") #: 20 561 373
  click("sikuli-tmp3943818238029316189.png") #: 27 551 373
  click("sikuli-tmp3804579369696955537.png") #: 34 597 492
  click("sikuli-tmp8041801353065660100.png") #: 53 439 221

def _test_toolbar_dialog(self):
  #switchApp("Capivara.app") #: 3
  click("sikuli-tmp3069601706442112907.png") #: 8 314 183
  assertExist("sikuli-tmp1249076757460070013.png") #: 33 546 402
  assertExist("sikuli-tmp3592503518297957204.png") #: 41 688 397
  click("sikuli-tmp5542843458381092898.png") #: 51 731 462
  assertNotExist("sikuli-tmp1249076757460070013.png")
  assertNotExist("sikuli-tmp3592503518297957204.png")

def test_sort_column(self):
  #switchApp("Capivara.app") #: -2
  assertExist("sikuli-tmp263496711155585212.png") #: 7 297 306
  click("sikuli-tmp3891114513666313429.png") #: 10 297 306
  click("sikuli-tmp3355893708069341318.png") #: 22 297 309
  assertExist("sikuli-tmp8618634754325720738.png") #: 30 297 309
  assertExist("sikuli-tmp4998887963310803768.png") #: 47 448 306
  click("sikuli-tmp3578879900050348223.png") #: 48 448 306
  assertExist("sikuli-tmp3616812625186588517.png") #: 63 448 306
  assertExist("sikuli-tmp203322021735647551.png") #: 78 291 308

def test_menu_tree(self):
  #switchApp("Capivara.app") #: 7
  click("sikuli-tmp3593075200832739791.png") #: 15 358 144
  click("sikuli-tmp3765827397352866794.png") #: 23 358 166
  click("sikuli-tmp8463884094763679313.png") #: 37 376 228
  assertExist("sikuli-tmp178963797137190522.png") #: 55 448 251
  click("sikuli-tmp5847325328519024444.png") #: 96 442 248
  click("sikuli-tmp7143054650472383687.png") #: 158 846 590

def test_enabled_disabled_buttons(self):
  #switchApp("Capivara.app") #: -1
  assertExist("sikuli-tmp813512145549351083.png") #: 10 300 253
  click("1251860047401.png") #: 15 270 253
  click("sikuli-tmp359238314228933729.png") #: 28 592 492
  assertExist("sikuli-tmp5465931220643971515.png") #: 40 301 254

def test_resize_splitter(self):
  assertNotExist(Pattern("1251918419142.png").similar(0.90))
  find("1251918393013.png")
  target = find.region
  dragDrop(target, [target.x+300, target.y])
  assertExist(Pattern("1251918419142.png").similar(0.90))

