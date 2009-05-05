all:
	javac Drawthing.java MinimumSize.java Point.java FreehandDrawPanel.java
test: all
	java Drawthing
