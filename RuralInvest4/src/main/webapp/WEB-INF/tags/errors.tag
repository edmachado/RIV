<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<spring:bind path="*">
	<c:if test="${not empty status.errorMessages}">
		<div id="errorbox">
			<div class="header_error">&gt; <spring:message code="error"/></div>
			<spring:message code="error.pleaseCorrect"/><br>
			<ul>
				<c:forEach items="${status.errorMessages}" var="error">
  					<li><c:out value="${error}"/></li>
				</c:forEach>
			</ul>
		</div>
	</c:if>
</spring:bind>