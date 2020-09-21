JAVAC=javac
JAVA=java
sources = $(wildcard *.java)
classes = $(sources:.java=.class)

FLAGS=-cp .:./lib/org/apache/commons-cli-1.4.jar

all : $(classes) app

.PHONY: app
app :
	$(JAVAC) $(FLAGS) src/app/SmallProjectThree.java

run : all app
	$(JAVA) $(FLAGS) PrimitiveRoots
	$(JAVA) $(FLAGS) src/app/SmallProjectThree.java --debug false --certainty 100


.PHONY: details
details :
	$(JAVA) $(FLAGS) PrimitiveRoots --debug true
	$(JAVA) $(FLAGS) src/app/SmallProjectThree.java --debug true --certainty 100

.PHONY: clean
clean :
	rm -f *.class

%.class : %.java
	$(JAVAC) $(FLAGS) $<
