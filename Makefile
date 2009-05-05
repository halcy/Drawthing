all:
	javac Drawthing.java Point.java FreehandDrawPanel.java
test: Drawthing.class
	java Drawthing
