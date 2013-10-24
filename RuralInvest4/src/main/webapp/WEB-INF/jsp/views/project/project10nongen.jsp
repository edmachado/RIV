<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step10.nongen"/></title></head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />
	
	<div align="left">
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a href="../../report/${project.projectId}/projectContributions.xlsx?template=true" target="_blank"><spring:message code="export.downloadTemplate"/></a><br/>
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadTemplate" href="../../report/${project.projectId}/projectContributions.xlsx" target="_blank"><spring:message code="export.download"/></a><br/>
		<c:if test="${accessOK}"><a id="importExcel" href="#"><img src="../../img/xls.gif" alt="Excel" title="Excel"/> <spring:message code="import.importExcel"/></a></c:if>
 	</div>
	
	<tags:tableContainer titleKey="projectContribution">
		<tags:table>						
			<display:table list="${project.contributions}" id="contrib" requestURI="" cellspacing="0" cellpadding="0"
					export="false" htmlId="contributionTable">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectContribution.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectContribution.contribType" sortable="true" style="text-align:left;" headerClass="left">
					<c:if test="${contrib.contribType=='0'}"><spring:message code="projectContribution.contribType.govtCentral"/></c:if>
					<c:if test="${contrib.contribType=='1'}"><spring:message code="projectContribution.contribType.govtLocal"/></c:if>
					<c:if test="${contrib.contribType=='2'}"><spring:message code="projectContribution.contribType.ngoLocal"/></c:if>
					<c:if test="${contrib.contribType=='3'}"><spring:message code="projectContribution.contribType.ngoIntl"/></c:if>
					<c:if test="${contrib.contribType=='4'}"><spring:message code="projectContribution.contribType.other"/></c:if>
				</display:column>
				<display:column titleKey="projectContribution.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectContribution.unitNum" sortProperty="unitNum" sortable="true">
					<tags:formatDecimal value="${contrib.unitNum}"/>
				</display:column>
				<display:column titleKey="projectContribution.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${contrib.unitCost}"/>
				</display:column>
				<display:column titleKey="projectContribution.totalCost" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${contrib.total}"/><c:set var="total" value="${total+contrib.total}"/>
				</display:column>
				<c:if test="${accessOK}">
					<display:column title="&nbsp;" media="html">
						<a name="copy" href="../item/${contrib.projItemId}/copy">
							<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
						</a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${contrib_rowNum ne 1}">
							<a name="moveUp" href="../item/${contrib.projItemId}/move?up=false">
								<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${contrib_rowNum eq 1}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${contrib_rowNum ne fn:length(project.contributions)}">
							<a name="moveDown" href="../item/${contrib.projItemId}/move?up=true">
								<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${contrib_rowNum eq fn:length(project.contributions)}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" style="margin-left:5px;" media="html">
						<a href="../item/${contrib.projItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<a name="delItem" href="../item/${contrib.projItemId}/delete">
							<img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0">
						</a>
					</display:column>
				</c:if>
				<display:footer>
					<tr height="1"><td height="1" colspan="11" class="Sum1"/></tr>
					<tr><td/><td/><td/><td/><td/>
					<td><tags:formatCurrency value="${total}" /></td>
					<td/><td/><td/><td/><td/></tr>
				</display:footer>
			</display:table>
			<div class="addNew"><a id="newContrib" href="../item/-1?type=contrib&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
		</tags:table>
	</tags:tableContainer>
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step10"/></tags:submit>
</form:form>
<tags:excelImport submitUrl="../../import/project/contribution/${project.projectId}"/>
</body></html>