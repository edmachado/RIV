<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@tag import="riv.web.config.RivConfig"%><%@tag import="riv.util.CurrencyFormat"%>
<%@ attribute name="value" required="true" %><%@ attribute name="noDecimals" %>
<c:choose>
<c:when test="${value=='NaN' or value=='Infinity'}"><c:set var="value" value="0" /></c:when>
<c:when test="${not empty value and empty noDecimals}"> <%=((RivConfig)request.getAttribute("rivConfig")).getSetting().getCurrencyFormatter().formatCurrency(Double.parseDouble(value),CurrencyFormat.ALL)%></c:when>
<c:when test="${not empty value and not empty noDecimals}"> <%=((RivConfig)request.getAttribute("rivConfig")).getSetting().getCurrencyFormatter().formatCurrency(Double.parseDouble(value),CurrencyFormat.NODECIMALS)%></c:when>
</c:choose><%-- <c:when test="${empty value}"></c:when> --%>