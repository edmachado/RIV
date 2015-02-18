<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="profile.step5"/></title></head>
<body>
<form:form name="form" method="post" commandName="profile">
	<tags:errors />
	<div align="left">
		<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadTemplate" href="../../report/${profile.profileId}/profileGeneral.xlsx?template=true" target="_blank"><spring:message code="export.downloadTemplate"/></a><br/>
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadExcel" href="../../report/${profile.profileId}/profileGeneral.xlsx" target="_blank"><spring:message code="export.download"/></a><br/>
	 	<c:if test="${accessOK}"><a id="importExcel" href="#"><img src="../../img/xls.gif" alt="Excel" title="Excel"/> <spring:message code="import.importExcel"/></a></c:if>
	</div>
	<tags:tableContainer titleKey="profileGeneral">
	
	<c:if test="${profile.withWithout}"><c:set var="tableTitle">project.with</c:set></c:if>
	<tags:table titleKey="${tableTitle}">
		<display:table htmlId="generalTable" list="${profile.glsGeneral}" requestURI="" cellspacing="0" cellpadding="0"
			export="false" id="general">
			<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
			<display:column titleKey="profileGeneral.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="profileGeneral.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="profileGeneral.unitNum" sortProperty="unitNum" sortable="true">
				<tags:formatDecimal value="${general.unitNum}"/>
			</display:column>
			<display:column titleKey="profileGeneral.unitCost" sortable="true" sortProperty="unitCost">
				<tags:formatCurrency value="${general.unitCost}"/>
			</display:column>
			<display:column titleKey="profileGeneral.totalCost" sortable="true" sortProperty="total" >
				<tags:formatCurrency value="${general.total}"/><c:set var="withTotal" value="${withTotal+general.total}"/>
			</display:column>
			<display:column title="&nbsp;">
				<c:if test="${not empty general.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
				<c:if test="${empty general.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
			</display:column>
			<c:if test="${accessOK}">
				<display:column title="&nbsp;" media="html">
					<a name="copy" href="../item/${general.profItemId}/copy">
						<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
					</a>
				</display:column>
				<display:column title="&nbsp;" media="html">
					<c:if test="${general_rowNum ne 1}">
						<a name="moveUp" href="../item/${general.profItemId}/move?up=false">
							<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
						</a>
					</c:if>
					<c:if test="${general_rowNum eq 1}">
						<img src="../../img/spacer.gif" width="16" height="16" border="0">
					</c:if>
				</display:column>
				<display:column title="&nbsp;" media="html">
					<c:if test="${general_rowNum ne fn:length(profile.glsGeneral)}">
						<a name="moveDown" href="../item/${general.profItemId}/move?up=true">
							<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
						</a>
					</c:if>
					<c:if test="${general_rowNum eq fn:length(profile.glsGeneral)}">
						<img src="../../img/spacer.gif" width="16" height="16" border="0">
					</c:if>
				</display:column>
				<display:column title="&nbsp;" style="margin-left:5px;" media="html">
					<a href="../item/${general.profItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
				</display:column>
				<display:column title="&nbsp;" media="html">
					<a href="../item/${general.profItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
				</display:column>
			</c:if>
			<display:footer>
				<tr height="1"><td height="1" colspan="13" class="Sum1"/></tr>
				<tr><td/><td/><td/><td/>
				<td><tags:formatCurrency value="${withTotal}" /></td>
				<td/><td/><td/><td/><td/><td/></tr>
			</display:footer>
		</display:table>
		<c:if test="${accessOK}">
			<div class="addNew"><a id="newGeneral" href="../item/-1?type=general&profileId=${profile.profileId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
		</c:if>
	</tags:table>
	
	
	<c:if test="${profile.withWithout}">
		<tags:table titleKey="project.without">
		<display:table htmlId="generalTableWo" list="${profile.glsGeneralWithout}" requestURI="" cellspacing="0" cellpadding="0"
			export="false" id="general">
			<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
			<display:column titleKey="profileGeneral.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="profileGeneral.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="profileGeneral.unitNum" sortProperty="unitNum" sortable="true">
				<tags:formatDecimal value="${general.unitNum}"/>
			</display:column>
			<display:column titleKey="profileGeneral.unitCost" sortable="true" sortProperty="unitCost">
				<tags:formatCurrency value="${general.unitCost}"/>
			</display:column>
			<display:column titleKey="profileGeneral.totalCost" sortable="true" sortProperty="total">
				<tags:formatCurrency value="${general.total}"/><c:set var="withoutTotal" value="${withoutTotal+general.total}"/>
			</display:column>
			<display:column title="&nbsp;">
				<c:if test="${not empty general.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
				<c:if test="${empty general.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
			</display:column>
			<c:if test="${accessOK}">
				<display:column title="&nbsp;" media="html">
					<a name="copy" href="../item/${general.profItemId}/copy">
						<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
					</a>
				</display:column>
				<display:column title="&nbsp;" media="html">
					<c:if test="${general_rowNum ne 1}">
						<a name="moveUp" href="../item/${general.profItemId}/move?up=false">
							<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
						</a>
					</c:if>
					<c:if test="${general_rowNum eq 1}">
						<img src="../../img/spacer.gif" width="16" height="16" border="0">
					</c:if>
				</display:column>
				<display:column title="&nbsp;" media="html">
					<c:if test="${general_rowNum ne fn:length(profile.glsGeneralWithout)}">
						<a name="moveDown" href="../item/${general.profItemId}/move?up=true">
							<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
						</a>
					</c:if>
					<c:if test="${general_rowNum eq fn:length(profile.glsGeneralWithout)}">
						<img src="../../img/spacer.gif" width="16" height="16" border="0">
					</c:if>
				</display:column>
				<display:column title="&nbsp;" style="margin-left:5px;" media="html">
					<a href="../item/${general.profItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
				</display:column>
				<display:column title="&nbsp;" media="html">
					<a href="../item/${general.profItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
				</display:column>
			</c:if>
			<display:footer>
				<tr height="1"><td height="1" colspan="13" class="Sum1"/></tr>
				<tr><td/><td/><td/><td/>
				<td><tags:formatCurrency value="${withoutTotal}" /></td>
				<td/><td/><td/><td/><td/><td/></tr>
			</display:footer>
		</display:table>
		<c:if test="${accessOK}">
			<div class="addNew"><a id="newGeneralWo" href="../item/-1?type=generalWithout&profileId=${profile.profileId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
		</c:if>
	</tags:table>
	</c:if>
</tags:tableContainer>
<tags:submit><spring:message code="misc.goto"/> 
	<c:if test="${profile.incomeGen}"><spring:message code="profile.step6"/></c:if>
	<c:if test="${not profile.incomeGen}"><spring:message code="profile.step6.nongen"/></c:if>
</tags:submit>
</form:form>
<tags:excelImport submitUrl="../../import/profile/general/${profile.profileId}"/>
</body>
</html>