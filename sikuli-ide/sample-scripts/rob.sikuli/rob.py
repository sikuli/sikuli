while not find(Pattern("1254347537847.png").similar(0.93)):
    sleep(1)
    click(Pattern("1254503762385.png").similar(0.69).firstN(1))

click("1254504009523.png")
popup("Rob just said something!")


