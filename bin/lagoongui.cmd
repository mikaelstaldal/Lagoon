@echo off

rem OS/2 batch script for running Lagoon GUI

rem SET LAGOON_HOME=D:\lagoon

java -classpath %CLASSPATH%;%LAGOON_HOME%\lagoon.jar;%LAGOON_HOME%\xmlutil.jar;%LAGOON_HOME%\lsp.jar nu.staldal.lagoon.LagoonGUI %1 %2 %3 %4 %5 %6 %7 %8 %9
