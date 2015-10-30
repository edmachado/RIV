@echo off
rem configure
echo Please wait while FAO RuralInvest collects your settings...
set RIV_JAVA=java
java -version > nul 2> nul
IF %ERRORLEVEL% NEQ 0 (
    echo Installing missing required component. It could take a few minutes. Please wait.
    jre\jre.exe INSTALL_SILENT=1 STATIC=1 INSTALLDIR="%PROGRAMFILES(X86)%\java99"
    set RIV_JAVA="%PROGRAMFILES(X86)%\java99\bin\java"
)
%RIV_JAVA% -jar %CD%\lib\riv.jar