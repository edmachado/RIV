
netsh firewall set portopening protocol=TCP port=$HTTP_PORT_NO name=RuralInvest4 mode=ENABLE profile=All

set SERVICE_PATH=$INSTALL_PATH\service
cd "%SERVICE_PATH%"
set SERVICE_NAME=RuralInvest4
set PR_INSTALL=%SERVICE_PATH%\prunsrv.exe

REM Service log configuration
set PR_LOGPREFIX=%SERVICE_NAME%
set PR_LOGPATH=%SERVICE_PATH%\logs
set PR_STDOUTPUT=%PR_LOGPATH%\stdout.txt
set PR_STDERROR=%PR_LOGPATH%\stderr.txt
set PR_LOGLEVEL=Debug 

REM Path to java installation
set PR_JVM=%JAVA_HOME%\bin\client\jvm.dll
set PR_CLASSPATH=RIV4-service.jar

REM Startup configuration
set PR_STARTUP=auto
set PR_STARTMODE=jvm
set PR_STARTCLASS=org.fao.riv.service.Launch
set PR_STARTMETHOD=start
set PR_STARTPARAMS= -port $HTTP_PORT_NO
 
REM Shutdown configuration
set PR_STOPMODE=jvm
set PR_STOPCLASS=org.fao.riv.service.Launch
set PR_STOPMETHOD=stop

REM Install service
prunsrv.exe //IS//%SERVICE_NAME%