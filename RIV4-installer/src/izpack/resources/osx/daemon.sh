#!/bin/bash

# NOTE: this is an OSX launchd wrapper shell script for RuralInvest
function shutdown() {
    date
    java -cp /Applications/RuralInvest/service/RIV4-service.jar org.fao.riv.service.Launch stop
}

date
# Uncomment to increase RuralInvest's maximum heap allocation
# export JAVA_OPTS=-Xmx512M $JAVA_OPTS

java -cp /Applications/RuralInvest/service/RIV4-service.jar org.fao.riv.service.Launch $HTTP_PORT_NO &

# Allow any signal which would kill a process to stop RuralInvest
trap shutdown HUP INT QUIT ABRT KILL ALRM TERM TSTP
