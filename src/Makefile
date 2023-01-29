#----------------------------------------------------------------------
#
#	Makefile for the TMA03_Q2 directory
#
#---------------------------------------------------------------------
# $Id: Makefile,v 1.1 2022/04/23 19:31:23 cmb Exp $
#----------------------------------------------------------------------

%.class : %.java
	javac --release 8 $<

all:	$(patsubst %.java,%.class,$(wildcard *.java))

clean:
	rm -f $(patsubst %.java,%.class,$(wildcard *.java))
