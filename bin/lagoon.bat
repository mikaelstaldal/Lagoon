@echo off

rem Windows batch script for running Lagoon

rem SET LAGOON_HOME=D:\lagoon

java -DresourceDir=%LAGOON_HOME%\resource -classpath %CLASSPATH%;%LAGOON_HOME%\lib\lagoon.jar;%LAGOON_HOME%\lib\xtree.jar nu.staldal.lagoon.LagoonCLI %1 %2 %3 %4 %5 %6 %7 %8 %9
