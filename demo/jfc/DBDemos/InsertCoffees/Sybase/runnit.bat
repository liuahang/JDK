@echo off
rem Run the InsertCoffees demo
rem
rem @(#)runnit.bat      1.1 97/09/22

if "%JDBCHOME%" == "" goto nohome

set _BUILDHOME=%JDBCHOME%\build
set _CLASSPATH=classes;%JDBCHOME%\InsertJv;%CLASSPATH%
set CMD=java -classpath %_CLASSPATH% InsertCoffees
echo %CMD%
%CMD%
set _BUILDHOME=
set _CLASSPATH=
goto done

:nohome
echo No JDBCHOME environment variable set.

:done
