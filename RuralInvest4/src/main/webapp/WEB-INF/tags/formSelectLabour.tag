<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="path" required="true" %>
<c:set var="pyears"><spring:message code='units.pyears'/></c:set><c:set var="pmonths"><spring:message code='units.pmonths'/></c:set><c:set var="pweeks"><spring:message code='units.pweeks'/></c:set><c:set var="pdays"><spring:message code='units.pdays'/></c:set>
<form:select path="${path}" >
	<form:option value="0" label="${pyears}"/>
	<form:option value="1" label="${pmonths}"/>
	<form:option value="2" label="${pweeks}"/>
	<form:option value="3" label="${pdays}"/>
</form:select>