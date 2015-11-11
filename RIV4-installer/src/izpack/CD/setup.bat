rem @echo off
echo Please wait while FAO RuralInvest collects your settings...
set RIV_JAVA=java
for /f "tokens=2-8 delims=.:/ " %%a in ("%date% %time%") do set JTMP=%%c-%%a-%%b_%%d-%%e-%%f.%%g
java -version > %JTMP% 2&>1
findstr /r "version 1.[6789]" %JTMP%
IF %ERRORLEVEL% NEQ 0 (
    echo Installing missing required component.
    echo It could take a few minutes. Please wait.
    jre\jre.exe STATIC=1
    set RIV_JAVA="%ALLUSERSPROFILE%\Oracle\Java\javapath\java.exe"
)
del %JTMP%
echo Please don't close this window.
start "RIV Installer" /b %RIV_JAVA% -jar "%CD%\lib\riv.jar" > nul 2> nul
pause