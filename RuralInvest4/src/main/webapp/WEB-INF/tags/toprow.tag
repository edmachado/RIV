<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><div id="top">
	<div id="user" align="right">&nbsp;
		<a id="welcome" href="<%=request.getContextPath()%>/config/user/${user.userId}"><spring:message code="header.welcome"/> ${user.description}</a> | 
		<c:if test="${user.administrator}"><a id="adminPage" href="<%=request.getContextPath()%>/config/admin"><spring:message code="admin.page"/></a> | </c:if>
		<form:form id="logout-form" action="${pageContext.request.contextPath}/logout" method="POST">
		 	<a id="logoff" onclick="document.getElementById('logout-form').submit();"><spring:message code="header.logOff"/></a>&nbsp;
		</form:form>
	</div>
	<div id="search">
		<img src="<%=request.getContextPath()%>/img/logo.gif" alt="RuralInvest"/>
		<div>
			<span><spring:message code="header.quickSearch"/>:</span>
			<form name="quickSearch" action="<%=request.getContextPath()%>/search/quick" method="post" style="margin:0;display:inline-block;">
			<sec:csrfInput />
			<input name="freeText" id="myFreeText" size="16"/>
			<input type="hidden" name="unfinished"/>
			<select name="objType" id="profOrProj">
				<option value="igpj"><spring:message code="header.project"/> ($)</option>
				<option value="nigpj"><spring:message code="header.project"/> (:)</option>
				<option value="igpf"><spring:message code="header.profile"/> ($)</option>
				<option value="nigpf"><spring:message code="header.profile"/> (:)</option>
			</select>
			<input class="button" type="submit" value="<spring:message code='header.go'/>" />
			</form>
		</div>
	</div>
	<tags:menu/>
</div>