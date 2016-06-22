@echo off
mshta vbscript:Execute("msgbox ""Please extract (unzip) the installer before running it."",0,""RuralInvest"":close")
rem mshta.exe vbscript:Execute("msgbox ""message"",0,""title"":close")
exit /b 0