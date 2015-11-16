@echo off
setlocal enableextensions disabledelayedexpansion

:: possible locations under HKLM\SOFTWARE of JavaSoft registry data
set "javaNativeVersion="
set "java32ON64=Wow6432Node\"

:: for variables
::    %%k = HKLM\SOFTWARE subkeys where to search for JavaSoft key
::    %%j = full path of "Java Runtime Environment" key under %%k
::    %%v = current java version
::    %%e = path to java

set "javaDir="
set "javaVersion="
for %%k in ( "%javaNativeVersion%" "%java32ON64%") do if not defined javaDir (
    for %%j in (
        "HKLM\SOFTWARE\%%~kJavaSoft\Java Runtime Environment"
    ) do for /f "tokens=3" %%v in (
        'reg query "%%~j" /v "CurrentVersion" 2^>nul ^| find /i "CurrentVersion"'
    ) do for /f "tokens=2,*" %%d in (
        'reg query "%%~j\%%v" /v "JavaHome"   2^>nul ^| find /i "JavaHome"'
    ) do ( set "javaDir=%%~e" & set "javaVersion=%%v" )
)

if not defined javaDir (
    echo Installing missing required component.
    echo It could take a few minutes. Please wait.
    jre\jre.exe STATIC=1
    set RIV_JAVA="%ALLUSERSPROFILE%\Oracle\Java\javapath\java.exe"
) else (
    echo JAVA_HOME="%javaDir%"
    echo JAVA_VERSION="%javaVersion%"
    set RIV_JAVA="%javaDir%\bin\java.exe"
    for %%A in ("1.1" "1.2" "1.3" "1.4" "1.5") do if "%VAR%"==%%A do set "doit=yes"
    if "%doit%" == "yes" (
        echo Upgrading required component.
        echo It could take a few minutes. Please wait.
        jre\jre.exe STATIC=1
        set RIV_JAVA="%ALLUSERSPROFILE%\Oracle\Java\javapath\java.exe"
    )
)

start "RIV Installer" /b %RIV_JAVA% -jar "%CD%\lib\riv.jar" > nul 2>&1
endlocal
rem pause