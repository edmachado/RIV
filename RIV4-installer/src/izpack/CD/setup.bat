@echo off
echo Please wait while FAO RuralInvest collects your settings...
set RIV_JAVA=java
set JTMP=%temp%\jtmp
java -version > %JTMP% 2> %JTMP%
findstr /r "version 1.[678]" %JTMP%
IF %ERRORLEVEL% NEQ 0 (
    echo Installing missing required component.
    echo It could take a few minutes. Please wait.
    jre\jre.exe STATIC=1
    set RIV_JAVA="%ALLUSERSPROFILE%\Oracle\Java\javapath\java.exe"
)
del %JTMP%
echo Please don't close this window.
start "RIV Installer" /b %RIV_JAVA% -jar "%CD%\lib\riv.jar" > nul 2> nul