<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="message" required="true" %>
<div id="profToProject">
	<h2><spring:message code="profileToProject"/></h2>
	<p><spring:message code="profileToProject.text"/><br/><br/></p>
	<p><b><spring:message code="${message}"/></b></p>
</div>