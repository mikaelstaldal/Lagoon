@echo off

rem OS/2 batch script for running Lagoon

rem SET LAGOON_HOME=D:\lagoon

java -classpath %CLASSPATH%;%LAGOON_HOME%/lib/lagoon.jar;%LAGOON_HOME%/lib/xtree.jar nu.staldal.lagoon.BuildSitemap %1 %2 %3 %4 %5 %6 %7 %8 %9
