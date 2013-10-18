<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="help" scope="request"/><c:set var="currentStep" value="2" scope="request"/>
<html><head><title><spring:message code="mainMenu.help.faq"/></title></head>
<body>
	<h2><spring:message code="mainMenu.help.faq"/></h2>
	<tags:faq q="faq.1.q" a="faq.1.a"/>
	<tags:faq q="faq.2.q" a="faq.2.a"/>
	<tags:faq q="faq.3.q" a="faq.3.a"/>
	<tags:faq q="faq.4.q" a="faq.4.a"/>
	<tags:faq q="faq.5.q" a="faq.5.a"/>
	<tags:faq q="faq.6.q" a="faq.6.a"/>
	<tags:faq q="faq.7.q" a="faq.7.a"/>
</body></html>