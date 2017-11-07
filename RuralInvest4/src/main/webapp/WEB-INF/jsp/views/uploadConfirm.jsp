<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><html>
<head><title>Import</title><c:set var="accessOK" value="true" scope="request"/></head>
<body>
<form id="form" name="form" method="post" action="import/confirm">
	<sec:csrfInput />
	<div><b>${filename}</b></div>
	<c:if test="${not empty exists}">
		<div id="confirmOverwrite" style="margin:10px 0;">
			<span class="error"><spring:message code="import.exists"/></span><br/>
			<input class="radio" name="overwriteOk" type="radio" value="true" />
			<spring:message code="import.overwrite"/>
			<input class="radio" name="overwriteOk" type="radio" value="false" checked="true" />
			<spring:message code="import.copy"/> 
		</div>
	</c:if>
	<c:if test="${not empty generic}">
		<div id="generic" style="margin:10px 0;">
			<span class="error"><spring:message code="import.generic.info"/></span><br/>
			<spring:message code="import.generic.confirm"/><br/>
			<input type="text" name="genericExchange" value="${rivConfig.setting.exchRate}"/>
			${rivConfig.setting.currencyName} <spring:message code="units.perUSD"/>
		</div>
	</c:if>
	<tags:submit><spring:message code="import.import"/></tags:submit>
</form>
</body>
</html>