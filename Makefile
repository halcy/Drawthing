JAVA_FILES = Drawthing.java Point.java FreehandDrawPanel.java

JCFLAGS = -g -Xlint:unchecked
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JCFLAGS) $*.java

all: $(JAVA_FILES:.java=.class)

clean:
	rm *.class
test: Drawthing.class
	java Drawthing

