<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="onSubmit" %><%@ attribute name="buttonId" %><%@ attribute name="cancel" %>
<style>
#cancel button  {
	background-color: #CCCCCC;
	color: #000066;
	padding: 5px 0;
}
</style>
<c:if test="${accessOK}"><c:if test="${empty onSubmit}"><c:set var="onSubmit" value="javascript:formSubmit()"/></c:if>
	<c:if test="${empty buttonId}"><c:set var="buttonId" value="submit"/></c:if>
	<div align="right"><button id="${buttonId}" name="${buttonId}" onclick="${onSubmit}"><jsp:doBody/></button></div>
	<c:if test="${not empty cancel}">
		<div align="right" id="cancel">
			<a href="${cancel}"><button class="submit"><spring:message code="misc.cancel"/></button></a>
		</div>
	</c:if>
</c:if>
<c:if test="${!accessOK}">
	<div align="right"><div id="nosubmit"><jsp:doBody/></div><div id="submit-disabled"><spring:message code="misc.noAccess"/></div></div>
</c:if>