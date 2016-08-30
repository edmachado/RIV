<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="config" scope="request"/><c:set var="currentStep" value="2" scope="request"/>
<html><head><title><spring:message code="user.users"/></title></head>
<body>
<tags:tableContainer titleKey="mainMenu.config.users">
			<tags:table titleKey="user.users">
	<display:table name="users" htmlId="users" id="row" requestURI="" cellpadding="0" cellspacing="0" defaultsort="1">
		<display:column titleKey="user.description" property="description" sortable="true" style="text-align:left;" headerClass="left" />
		<c:if test="${rivConfig.qa}">
			<display:column title="Last login" headerClass="left" class="left" sortable="true" sortProperty="lastLogin">
				<fmt:formatDate value="${row.lastLogin}" type="both" pattern="dd/MM/yy HH:mm" />
			</display:column>
		</c:if>
		<display:column titleKey="user.organization" property="organization" sortable="true" style="text-align:left;" headerClass="left" />
		<display:column titleKey="user.location" property="location" sortable="true" style="text-align:left;" headerClass="left" />
		<display:column titleKey="user.telephone" property="telephone" sortable="true" style="text-align:left;" headerClass="left" />
		<display:column titleKey="user.email" property="email" autolink="true" sortable="true" style="text-align:left;" headerClass="left" />
		<display:column titleKey="user.administrator" sortable="true" headerClass="left" >
			<c:if test="${row.administrator}"><img src="../img/star.png" alt="<spring:message code="user.administrator"/>" title="<spring:message code="user.administrator"/>" width="16" height="16" border="0"></c:if>
		</display:column>
		<display:column title="&nbsp;">
			<c:if test="${user.userId == row.userId}">
				<a href="user/${row.userId}">
					<img src="../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" title="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0">
				</a>
			</c:if>
		</display:column>
		<display:column>
			<c:if test="${user.administrator && user.userId != row.userId}">
				<a onclick="confirmDelete('user/${row.userId}/delete');" >
					<img src="../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0">
				</a>
			</c:if>
		</display:column>
		<display:footer>
			<TR height="1"><TD colspan="9" class="Sum1" height="1"></TD></TR>
		</display:footer>
	</display:table>
	<div class="addNew"><a id="addUser" href="user/-1"><spring:message code="user.addUser"/> <img src="../img/user.gif" width="16" height="16" alt="" border="0"></a></div>
		
	</tags:table></tags:tableContainer>	
	<div id="confirmDelete" title="<spring:message code="misc.deleteItem"/>">
		<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
		<spring:message code="user.delete.warn"/></p><input id="deleteUrl" type="hidden" value=""/>
	</div>
</body></html>