<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@tag import="riv.web.config.RivConfig"%><%@ attribute name="value" required="true" %><%@ attribute name="noDecimals" %>
<c:if test="${value ne null and value ne '' and value ne 'NaN' and value ne 'Infinity'}">
	<c:if test="${empty noDecimals}"><%=((RivConfig)request.getAttribute("rivConfig")).getSetting().getDecimalFormat().format(Double.parseDouble(value))%></c:if>
	<c:if test="${not empty noDecimals}"><%=((RivConfig)request.getAttribute("rivConfig")).getSetting().getDecimalFormat().format((int)(Double.parseDouble(value)+ 0.5))%></c:if>
</c:if>