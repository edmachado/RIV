<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="config" scope="request"/><c:set var="currentStep" value="${step}" scope="request"/>
<html><head><title>${pageTitle}</title>
<script>
var pageUrl = document.URL;
$(function() { $("#confirmDelete").dialog({
			bgiframe: true, autoOpen: false, resizable: false, height:300, width:400, modal: true,
			overlay: { backgroundColor: '#000', opacity: 0.5 },
			buttons: {
				Cancel: function() { $(this).dialog('close'); },
				'<spring:message code="misc.deleteItem"/>': function() { location.href=$('#deleteUrl').val(); }		
			}
}); });
</script>
</head>
<body>
<tags:tableContainer title="${pageTitle}">
	<tags:table title="${pageTitle}">
	<display:table name="appConfigList" requestURI="" defaultsort="1" cellpadding="0" cellspacing="0" id="row"
		class="border-bottom-green">
		<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
		<display:column title="${description}" property="description" sortable="true" style="text-align:center;" headerClass="centered"/>
		<c:if test="${type eq 'category'}">
			<display:column titleKey="projectCategory.type" style="text-align:center;" headerClass="centered" >
				<c:if test="${row.incomeGen}"><spring:message code="projectCategory.incomeGenerating"/></c:if>
				<c:if test="${!row.incomeGen}"><spring:message code="projectCategory.nonIncomeGenerating"/></c:if>
			</display:column>
		</c:if>
		<c:if test="${rivConfig.admin}">
			<display:column title="&nbsp;" style="text-align:right;">
				<c:if test="${row.configId gt 0}">
					<a href="javascript:location.href=pageUrl+'/'+${row.configId}"><img src="../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"></a>
				</c:if>
			</display:column>
			<display:column>
				<c:if test="${usage[row.configId] eq null and row.configId!=-4 and row.configId!=-3 and row.configId!=-5 and row.configId!=-2 and row.configId!=-6  and row.configId!=-7}">
					<a onclick="confirmDelete(document.URL+'/delete/${row.configId}');" >
						<img src="../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0">
					</a>
				</c:if>
			</display:column>
		</c:if>
	</display:table>
	<c:if test="${rivConfig.admin}">
			<div class="addNew"><a id="addNewAppConfig" href="javascript:location.href=pageUrl+'/-1'">${addNew} <img src="../img/${addImage}" width="16" height="16" border="0"></a></div>
		</c:if>
	</tags:table></tags:tableContainer>
	<div id="confirmDelete" title="<spring:message code="misc.deleteItem"/>">
		<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
		${deleteWarn}</p><input id="deleteUrl" type="hidden" value=""/>
	</div>
</body></html>