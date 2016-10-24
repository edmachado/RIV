<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<spring:bind path="*">
	<c:if test="${not empty status.errorMessages}">
		<div id="errorbox">
			<div class="header_error">&gt; <spring:message code="error"/></div>
			<spring:message code="error.pleaseCorrect"/>
			<div style="display:none;"><ul>
				<c:forEach items="${status.errorMessages}" var="error">
  					<li><c:out value="${error}"/></li>
				</c:forEach>
			</div>
		</div>
	</c:if>
</spring:bind>