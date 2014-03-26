@echo off
jre\jre.exe /s ADDLOCAL=ALL STATIC=1 AUTOUPDATECHECK=0 JAVAUPDATE=0
jre\bin\java.exe -cp lib fao.JavaDiscovery