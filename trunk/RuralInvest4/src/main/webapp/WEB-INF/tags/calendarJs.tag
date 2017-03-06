<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<script>$(function() {
		$("#reccDate").datepicker({dateFormat: 'dd/mm/yy', showOn: 'button', buttonImage: '../../img/calendar.gif', buttonImageOnly: true, 
		dayNamesMin: ["<spring:message code="calendar.sun"/>","<spring:message code="calendar.mon"/>","<spring:message code="calendar.tue"/>","<spring:message code="calendar.wed"/>","<spring:message code="calendar.thu"/>","<spring:message code="calendar.fri"/>","<spring:message code="calendar.sat"/>"],
		monthNames: [<c:forEach var="i" begin="1" end="12">'<spring:message code="calendar.month.${i}"/>',</c:forEach>]
}); });</script>