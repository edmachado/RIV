<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step8"/></title></head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />
	<div align="left">
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a href="../../report/${project.projectId}/projectGeneralDetail.xlsx?template=true"><spring:message code="export.downloadTemplate"/></a><br/>
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a href="../../report/${project.projectId}/projectGeneralDetail.xlsx"><spring:message code="export.download"/></a><br/>
	 	<%-- 
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a href="importXls.htm?type=profileInvestment&pid=${profile.profileId}"><spring:message code="import.importExcel"/></a>
 		--%>
 	</div>
	
	<tags:tableContainer titleKey="projectGeneral">
		<tags:table titleKey="projectNongenInput">
			<display:table list="${project.nongenMaterials}" id="row" requestURI="" cellspacing="0" cellpadding="0"
				export="false" htmlId="inputTable">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectNongenInput.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectNongenInput.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectNongenInput.unitNum" sortProperty="unitNum" sortable="true">
					<tags:formatDecimal value="${row.unitNum}"/>
				</display:column>
				<display:column titleKey="projectNongenInput.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${row.unitCost}"/>
				</display:column>
				<display:column titleKey="projectNongenInput.total" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${row.total}"/><c:set var="inpTotal" value="${inpTotal+row.total}"/>
				</display:column>
				<display:column titleKey="projectNongenInput.statePublic" sortable="true" sortProperty="statePublic">
					<tags:formatCurrency value="${row.statePublic}"/><c:set var="inpState" value="${inpState+row.statePublic}"/>
				</display:column>
				<display:column titleKey="projectNongenInput.other1" sortable="true" sortProperty="other1">
					<tags:formatCurrency value="${row.other1}"/><c:set var="inpOther" value="${inpOther+row.other1}"/>
				</display:column>
				<display:column titleKey="projectNongenInput.ownResource" sortable="true" sortProperty="ownResource">
					<tags:formatCurrency value="${row.ownResource}"/>
				</display:column>
				<display:column title="&nbsp;">
					<c:if test="${not empty row.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
					<c:if test="${empty row.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
				</display:column>
				<c:if test="${accessOK}">
					<display:column title="&nbsp;" media="html">
						<a name="copy" href="../item/${row.projItemId}/copy">
							<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
						</a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${row_rowNum ne 1}">
							<a name="moveUp" href="../item/${row.projItemId}/move?up=false">
								<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${row_rowNum eq 1}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${row_rowNum ne fn:length(project.nongenMaterials)}">
							<a name="moveDown" href="../item/${row.projItemId}/move?up=true">
								<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${row_rowNum eq fn:length(project.nongenMaterials)}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" style="margin-left:5px;" media="html">
						<a href="../item/${row.projItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<a href="../item/${row.projItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
				</c:if>
				<display:footer>
					<tr height="1"><td height="1" colspan="14" class="Sum1"/></tr>
					<tr><td/><td/><td/><td/>
					<td><tags:formatCurrency value="${inpTotal}" /></td>
					<td><tags:formatCurrency value="${inpState}" /></td>
					<td><tags:formatCurrency value="${inpOther}" /></td>
					<td><tags:formatCurrency value="${inpTotal-inpState-inpOther}" /></td>
					<td/><td/><td/><td/><td/><td/></tr>
				</display:footer>
			</display:table>
			<c:if test="${accessOK}">
				<div class="addNew"><a id="addMaterial" href="../item/-1?type=nongenMaterial&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
			</c:if>
		</tags:table>
	
	<tags:table titleKey="projectNongenLabour">
			<display:table list="${project.nongenLabours}" id="lab" requestURI="" cellspacing="0" cellpadding="0"
				export="false" htmlId="labourTable">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectNongenLabour.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectNongenLabour.unitType" sortable="true" style="text-align:${left};" headerClass="left">
					<c:if test="${lab.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
					<c:if test="${lab.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
					<c:if test="${lab.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
					<c:if test="${lab.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
				</display:column>
				<display:column titleKey="projectNongenLabour.unitNum" sortProperty="unitNum" sortable="true">
					<tags:formatDecimal value="${lab.unitNum}"/>
				</display:column>
				<display:column titleKey="projectNongenLabour.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${lab.unitCost}"/>
				</display:column>
				<display:column titleKey="projectNongenLabour.total" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${lab.total}"/><c:set var="labTotal" value="${labTotal+lab.total}"/>
				</display:column>
				<display:column titleKey="projectNongenLabour.statePublic" sortable="true" sortProperty="statePublic">
					<tags:formatCurrency value="${lab.statePublic}"/><c:set var="labState" value="${labState+lab.statePublic}"/>
				</display:column>
				<display:column titleKey="projectNongenLabour.other1" sortable="true" sortProperty="other1">
					<tags:formatCurrency value="${lab.other1}"/><c:set var="labOther" value="${labOther+lab.other1}"/>
				</display:column>
				<display:column titleKey="projectNongenLabour.ownResource" sortable="true" sortProperty="ownResource">
					<tags:formatCurrency value="${lab.ownResource}"/>
				</display:column>
				<display:column title="&nbsp;">
					<c:if test="${not empty lab.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
					<c:if test="${empty lab.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
				</display:column>
				<c:if test="${accessOK}">
					<display:column title="&nbsp;" media="html">
						<a name="copy" href="../item/${lab.projItemId}/copy">
							<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
						</a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${lab_rowNum ne 1}">
							<a name="moveUp" href="../item/${lab.projItemId}/move?up=false">
								<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${lab_rowNum eq 1}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${lab_rowNum ne fn:length(project.nongenLabours)}">
							<a name="moveDown" href="../item/${lab.projItemId}/move?up=true">
								<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${lab_rowNum eq fn:length(project.nongenLabours)}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" style="margin-left:5px;" media="html">
						<a href="../item/${lab.projItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<a href="../item/${lab.projItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
				</c:if>
				<display:footer>
					<tr height="1"><td height="1" colspan="14" class="Sum1"/></tr>
					<tr><td/><td/><td/><td/>
					<td><tags:formatCurrency value="${labTotal}" /></td>
					<td><tags:formatCurrency value="${labState}" /></td>
					<td><tags:formatCurrency value="${labOther}" /></td>
					<td><tags:formatCurrency value="${labTotal-labState-labOther}" /></td>
					<td/><td/><td/><td/><td/><td/></tr>
				</display:footer>
			</display:table>
			<c:if test="${accessOK}">
				<div class="addNew"><a id="addLabour" href="../item/-1?type=nongenLabour&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
			</c:if>
		</tags:table>
	
	<tags:table titleKey="projectNongenGeneral">
			<display:table list="${project.nongenMaintenance}" id="gen" requestURI="" cellspacing="0" cellpadding="0"
				export="false" htmlId="generalTable">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectNongenGeneral.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectNongenGeneral.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectNongenGeneral.unitNum" sortProperty="unitNum" sortable="true">
					<tags:formatDecimal value="${gen.unitNum}"/>
				</display:column>
				<display:column titleKey="projectNongenGeneral.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${gen.unitCost}"/>
				</display:column>
				<display:column titleKey="projectNongenGeneral.total" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${gen.total}"/><c:set var="genTotal" value="${genTotal+gen.total}"/>
				</display:column>
				<display:column titleKey="projectNongenGeneral.statePublic" sortable="true" sortProperty="statePublic">
					<tags:formatCurrency value="${gen.statePublic}"/><c:set var="genState" value="${genState+row.statePublic}"/>
				</display:column>
				<display:column titleKey="projectNongenGeneral.other1" sortable="true" sortProperty="other1">
					<tags:formatCurrency value="${gen.other1}"/><c:set var="genOther" value="${genOther+gen.other1}"/>
				</display:column>
				<display:column titleKey="projectNongenGeneral.ownResource" sortable="true" sortProperty="ownResource">
					<tags:formatCurrency value="${gen.ownResource}"/>
				</display:column>
				<display:column title="&nbsp;">
					<c:if test="${not empty gen.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
					<c:if test="${empty gen.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
				</display:column>
				<c:if test="${accessOK}">
					<display:column title="&nbsp;" media="html">
						<a name="copy" href="../item/${gen.projItemId}/copy">
							<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
						</a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${gen_rowNum ne 1}">
							<a name="moveUp" href="../item/${gen.projItemId}/move?up=false">
								<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${gen_rowNum eq 1}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${gen_rowNum ne fn:length(project.nongenMaintenance)}">
							<a name="moveDown" href="../item/${gen.projItemId}/move?up=true">
								<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${gen_rowNum eq fn:length(project.nongenMaintenance)}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" style="margin-left:5px;" media="html">
						<a href="../item/${gen.projItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<a href="../item/${gen.projItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
				</c:if>
				<display:footer>
					<tr height="1"><td height="1" colspan="14" class="Sum1"/></tr>
					<tr><td/><td/><td/><td/>
					<td><tags:formatCurrency value="${genTotal}" /></td>
					<td><tags:formatCurrency value="${genState}" /></td>
					<td><tags:formatCurrency value="${genOther}" /></td>
					<td><tags:formatCurrency value="${genTotal-genState-genOther}" /></td>
					<td/><td/><td/><td/><td/><td/></tr>
				</display:footer>
			</display:table>
			<c:if test="${accessOK}">
				<div class="addNew"><a id="addMaintenance" href="../item/-1?type=nongenMaintenance&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
			</c:if>
		</tags:table>
	</tags:tableContainer>
					
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step9"/></tags:submit>
	
</form:form>
</body></html>