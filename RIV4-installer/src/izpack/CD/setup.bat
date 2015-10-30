@echo off
echo %JAVA_HOME% | Findstr/I "."
IF ERRORLEVEL 1 jre\jre.exe INSTALLCFG=setup.ini
%JAVA_HOME%\jre\bin\java.exe -cp lib fao.JavaDiscovery