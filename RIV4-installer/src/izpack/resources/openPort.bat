netsh advfirewall firewall add rule name="Open port $HTTP_PORT_NO for RuralInvest" dir=in action=allow protocol=TCP localport=$HTTP_PORT_NO
