#----------------------------------------------------------------------
#
#	Makefile for the MeterProcess code; this builds an executable
#	JAR file, 'meterprocess'
#
#---------------------------------------------------------------------
# $Id: Makefile,v 1.1 2022/04/23 19:31:23 cmb Exp $
#----------------------------------------------------------------------

EXE=meterprocess

%.class : %.java
	javac --release 8 $<

all:	$(EXE)

class:	$(patsubst %.java,%.class,$(wildcard *.java))

$(EXE): class
	jar cfm $(EXE) manifest.mf *.class
	chmod u+x $(EXE)

install:	all
	cp $(EXE) /home/cmb/bin/bin/

clean:
	rm -f $(patsubst %.java,%.class,$(wildcard *.java)) $(EXE)
