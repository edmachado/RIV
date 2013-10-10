@echo off

rem Create Log Dir

Set LOGPATH=C:\Logs
IF NOT EXIST "%LOGPATH%" mkdir "%LOGPATH%"

rem Set working Path and EXE name (fill in the EXE full name)
rem Place EXE in a folder named Sources

Set EXENAME1=jre-7u15-windows-i586.EXE

Set EXENAME2=jre-7u15-windows-x64.EXE


SET LIB=lib

REM This will automatically close IE if open
taskkill /f /im iexplore.exe /t


rem Installing

echo Installing %EXENAME1% ...

%LIB%\%EXENAME1% /s "/passive /norestart AUTOUPDATECHECK=0 IEXPLORER=1 JAVAUPDATE=0 JU=0 EULA=1"

echo Installing %EXENAME2% ...

%LIB%\%EXENAME2% /s "/passive /norestart AUTOUPDATECHECK=0 IEXPLORER=1 JAVAUPDATE=0 JU=0 EULA=1"

java -jar %LIB%\riv.jar