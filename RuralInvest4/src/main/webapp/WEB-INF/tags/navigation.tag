<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="wizardStep" %><%@ attribute name="currentId" %><jsp:useBean id="navItems" class="riv.util.NavItems" scope="application"/><c:set var="includeId" value="${menuType == 'profile' || menuType=='profileNoninc' || menuType == 'project' || menuType == 'projectNoninc'}"/>
<c:set var="ctx"><%=request.getContextPath()%></c:set>
<c:set var="i" value="0"/><c:set var="items" value="${navItems.items[menuType]}"/><c:forEach var="step" items="${navItems.items[menuType]}">
<c:choose>
	<c:when test="${menuType eq 'config' and i+1 eq 9}"><c:set var="title" value="${rivConfig.setting.admin1Title}"/></c:when>
	<c:when test="${menuType eq 'config' and i+1 eq 10}">
		<c:set var="title" value="${rivConfig.setting.admin2Title}"/>
		<c:if test="${not rivConfig.setting.admin1Enabled}"><c:set var="stepNum">${i}</c:set></c:if>
	</c:when>
	<c:otherwise><c:set var="title"><spring:message code="${step[0]}"/></c:set></c:otherwise>
</c:choose>
<c:if test="${empty stepNum}"><c:set var="stepNum" value="${i+1}"/></c:if>
<c:if test="${menuType ne 'config' or (i+1 ne 9 and i+1 ne 10) or (i+1 eq 9 and rivConfig.setting.admin1Enabled)  or (i+1 eq 10 and rivConfig.setting.admin2Enabled)}">
	
	<c:choose>
		<c:when test="${currentStep==i+1}">
			<div class="active">
				<label><span>${stepNum}</span></label>
				<span>${title}</span>
			</div>
		</c:when>
		<c:otherwise>
			<div class="inactive">
				<label><span>${stepNum}</span></label>
				<c:choose>
					<c:when test="${wizardStep==-1 || (wizardStep != '' && wizardStep<i+1) }">
						<span>${title}</span>
					</c:when>
					<c:otherwise>
						<a id="step${i+1}" href="${ctx}/${step[1]}<c:if test="${includeId}">${currentId}</c:if>">
							<span class="step-label" style="cursor: hand;">${title}</span>
						</a>
					</c:otherwise>
				</c:choose>
			</div>
		</c:otherwise>
	</c:choose>
</c:if>
<c:set var="i" value="${i+1}"/><c:set var="stepNum"></c:set></c:forEach>