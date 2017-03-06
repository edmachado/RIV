<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="search.searchResults"/></title></head>
<body>
<script>
$(function() { 
$("#confirmDelete").dialog({
			bgiframe: true, autoOpen: false, resizable: false, height:140, modal: true,
			overlay: { backgroundColor: '#000', opacity: 0.5 },
			buttons: {
				Cancel: function() { $(this).dialog('close'); },
				'<spring:message code="misc.deleteItem"/>': function() { location.href=$('#deleteUrl').val(); }		
			}
});
});
</script>
<c:set var="currentStep" value="2" scope="request"/><c:set var="menuType" value="search" scope="request"/>
<c:set var="titleKey"><c:if test="${param['type']=='igpf'}">profile.profiles.incomeGen</c:if><c:if test="${param['type']=='nigpf'}">profile.profiles.nonIncomeGen</c:if></c:set>
<tags:tableContainer titleKey="home.inProgress">
	<tags:table titleKey="${titleKey}" pager="true">
		<display:table name="results" requestURI="" id="row" pagesize="${user.pageSize}" export="false" cellspacing="0" cellpadding="0" htmlId="results">
			<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
			<display:column titleKey="profile.profileName" property="profileName" sortable="true" style="text-align:left" headerClass="left" />
<%-- 			<display:column titleKey="profile.technician" property="technician.description" sortable="true" style="text-align:left" headerClass="left" /> --%>
			<display:column titleKey="profile.creationDate" sortable="true" sortProperty="prepDate" style="text-align:center;" headerClass="centered">
				<fmt:formatDate value="${row.prepDate}" type="both" pattern="dd/MM/yy HH:mm" />
			</display:column>
			<display:column titleKey="profile.lastUpdate" sortable="true" sortProperty="lastUpdate" style="text-align:center;" headerClass="centered">					
				<fmt:formatDate value="${row.lastUpdate}" type="both" pattern="dd/MM/yy HH:mm" />
			</display:column>
			<display:column title="&nbsp;" media="html">
				<a href="../profile/step1/${row.profileId}"><img src="../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" title="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"></a>
			</display:column>
			<display:column title="&nbsp;" style="padding-left:5px;" media="html">
				<c:if test="${row.shared==true || row.technician.userId==user.userId }">
				<a onclick="confirmDelete('../profile/step1/${row.profileId}/delete');">
				<img src="../img/delete.gif" title="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0">
				</a></c:if>
			</display:column>
		</display:table>
	</tags:table>
</tags:tableContainer>
<div id="confirmDelete" title="<spring:message code="misc.deleteItem"/>">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	<spring:message code="misc.confirmDel"/></p><input id="deleteUrl" type="hidden" value=""/>
</div>
</body></html>