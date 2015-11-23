<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step8"/></title></head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />
	
	<div align="left">
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadTemplate" href="../../report/${project.projectId}/projectGeneralDetail.xlsx?template=true" target="_blank"><spring:message code="export.downloadTemplate"/></a><br/>
		<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadExcel" href="../../report/${project.projectId}/projectGeneralDetail.xlsx" target="_blank"><spring:message code="export.download"/></a><br/>
	 	<c:if test="${accessOK}"><a id="importExcel" href="#"><img src="../../img/xls.gif" alt="Excel" title="Excel"/> <spring:message code="import.importExcel"/></a></c:if>
 	</div>
	<c:set var="withTableTitle">
		<c:if test="${project.withWithout}">projectGeneral.with</c:if>
		<c:if test="${not project.withWithout}">projectGeneral</c:if>
	</c:set>
	
	<br/>
	<form:errors path="generals" cssClass="error" element="div" />
	<form:errors path="personnels" cssClass="error" element="div" />
	
	<tags:tableContainer titleKey="${withTableTitle}">
		<tags:table titleKey="projectGeneralSupplies">
			<display:table list="${project.generals}" id="row" requestURI=""  cellspacing="0" cellpadding="0"
				export="false" htmlId="suppliesTable">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectGeneralSupplies.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectGeneralSupplies.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectGeneralSupplies.unitNum" sortProperty="unitNum" sortable="true">
					<tags:formatDecimal value="${row.unitNum}"/>
				</display:column>
				<display:column titleKey="projectGeneralSupplies.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${row.unitCost}"/>
				</display:column>
				<display:column titleKey="projectGeneralSupplies.totalCost" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${row.total}"/><c:set var="genTotal" value="${genTotal+row.total}"/>
				</display:column>
				<display:column titleKey="projectGeneralSupplies.ownResources" sortable="true" sortProperty="ownResources">
					<tags:formatCurrency value="${row.ownResources}"/><c:set var="genOwn" value="${genOwn+row.ownResources}"/>
				</display:column>
				<display:column titleKey="projectGeneralSupplies.external" sortable="true" sortProperty="external">
					<tags:formatCurrency value="${row.external}"/>
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
						<c:if test="${row_rowNum ne fn:length(project.generals)}">
							<a name="moveDown" href="../item/${row.projItemId}/move?up=true">
								<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${row_rowNum eq fn:length(project.generals)}">
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
					<tr height="1"><td height="1" colspan="13" class="Sum1"/></tr>
					<tr><td/><td/><td/><td/>
					<td><tags:formatCurrency value="${genTotal}" /></td>
					<td><tags:formatCurrency value="${genOwn}" /></td>
					<td><tags:formatCurrency value="${genTotal-genOwn}" /></td>
					<td/><td/><td/><td/><td/><td/></tr>
				</display:footer>
			</display:table>
			<c:if test="${accessOK}">
				<div class="addNew"><a id="newSupply" href="../item/-1?type=general&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
			</c:if>
		</tags:table>
	
		<tags:table titleKey="projectGeneralPersonnel">
			<display:table list="${project.personnels}" id="gen" requestURI=""  cellspacing="0" cellpadding="0"
				 export="false" htmlId="personnelTable">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectGeneralPersonnel.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectGeneralPersonnel.unitType" sortable="true" style="text-align:${left};" headerClass="left">
					<c:if test="${gen.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
					<c:if test="${gen.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
					<c:if test="${gen.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
					<c:if test="${gen.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
				</display:column>
				<display:column titleKey="projectGeneralPersonnel.unitNum" sortProperty="unitNum" sortable="true">
					<tags:formatDecimal value="${gen.unitNum}"/>
				</display:column>
				<display:column titleKey="projectGeneralPersonnel.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${gen.unitCost}"/>
				</display:column>
				<display:column titleKey="projectGeneralPersonnel.totalCost" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${gen.total}"/><c:set var="persTotal" value="${persTotal+gen.total}"/>
				</display:column>
				<display:column titleKey="projectGeneralPersonnel.ownResources" sortable="true" sortProperty="ownResources">
					<tags:formatCurrency value="${gen.ownResources}"/><c:set var="persOwn" value="${persOwn+gen.ownResources}"/>
				</display:column>
				<display:column titleKey="projectGeneralPersonnel.external" sortable="true" sortProperty="external">
					<tags:formatCurrency value="${gen.external}"/>
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
						<c:if test="${gen_rowNum ne fn:length(project.personnels)}">
							<a name="moveDown" href="../item/${gen.projItemId}/move?up=false">
								<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${gen_rowNum eq fn:length(project.personnels)}">
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
					<tr height="1"><td height="1" colspan="13" class="Sum1"/></tr>
					<tr><td/><td/><td/><td/>
					<td><tags:formatCurrency value="${persTotal}" /></td>
					<td><tags:formatCurrency value="${persOwn}" /></td>
					<td><tags:formatCurrency value="${persTotal-persOwn}" /></td>
					<td/><td/><td/><td/><td/><td/></tr>
				</display:footer>
			</display:table>
			<c:if test="${accessOK}">
				<div class="addNew"><a id="newPersonnel" href="../item/-1?type=personnel&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
			</c:if>
		</tags:table>	
	</tags:tableContainer>
	
	<c:if test="${project.withWithout}">
		<tags:tableContainer titleKey="projectGeneral.without">
			<tags:table titleKey="projectGeneralSupplies">
				<display:table list="${project.generalWithouts}" id="genWO" requestURI=""  cellspacing="0" cellpadding="0"
					export="false" htmlId="suppliesWoTable">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="projectGeneralSupplies.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="projectGeneralSupplies.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="projectGeneralSupplies.unitNum" sortProperty="unitNum" sortable="true">
						<tags:formatDecimal value="${genWO.unitNum}"/>
					</display:column>
					<display:column titleKey="projectGeneralSupplies.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${genWO.unitCost}"/>
					</display:column>
					<display:column titleKey="projectGeneralSupplies.totalCost" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${genWO.total}"/><c:set var="genWoTotal" value="${genWoTotal+genWO.total}"/>
					</display:column>
					<display:column titleKey="projectGeneralSupplies.ownResources" sortable="true" sortProperty="ownResources">
						<tags:formatCurrency value="${genWO.ownResources}"/><c:set var="genWoOwn" value="${genWoOwn+genWO.ownResources}"/>
					</display:column>
					<display:column titleKey="projectGeneralSupplies.external" sortable="true" sortProperty="external">
						<tags:formatCurrency value="${genWO.external}"/>
					</display:column>
					<display:column title="&nbsp;">
						<c:if test="${not empty genWO.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty genWO.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<a name="copy" href="../item/${genWO.projItemId}/copy">
								<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
							</a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${genWO_rowNum ne 1}">
								<a name="moveUp" href="../item/${genWO.projItemId}/move?up=false">
									<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${genWO_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${genWO_rowNum ne fn:length(project.generalWithouts)}">
								<a name="moveDown" href="../item/${genWO.projItemId}/move?up=true">
									<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${genWO_rowNum eq fn:length(project.generalWithouts)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" style="margin-left:5px;" media="html">
							<a href="../item/${genWO.projItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../item/${genWO.projItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
					<display:footer>
						<tr height="1"><td height="1" colspan="13" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/>
						<td><tags:formatCurrency value="${genWoTotal}" /></td>
						<td><tags:formatCurrency value="${genWoOwn}" /></td>
						<td><tags:formatCurrency value="${genWoTotal-genWoOwn}" /></td>
						<td/><td/><td/><td/><td/><td/></tr>
					</display:footer>
				</display:table>
				<c:if test="${accessOK}">
					<div class="addNew"><a id="newSupplyWo" href="../item/-1?type=generalWithout&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>
			
			<tags:table titleKey="projectGeneralPersonnel">
				<display:table list="${project.personnelWithouts}" id="genP" requestURI="" cellspacing="0" cellpadding="0"
					 export="false" htmlId="personnelWoTable">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="projectGeneralPersonnel.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="projectGeneralPersonnel.unitType" sortable="true" style="text-align:${left};" headerClass="left">
						<c:if test="${genP.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
						<c:if test="${genP.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
						<c:if test="${genP.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
						<c:if test="${genP.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
					</display:column>
					<display:column titleKey="projectGeneralPersonnel.unitNum" sortProperty="unitNum" sortable="true">
						<tags:formatDecimal value="${genP.unitNum}"/>
					</display:column>
					<display:column titleKey="projectGeneralPersonnel.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${genP.unitCost}"/>
					</display:column>
					<display:column titleKey="projectGeneralPersonnel.totalCost" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${genP.total}"/><c:set var="persWoTotal" value="${persWoTotal+genP.total}"/>
					</display:column>
					<display:column titleKey="projectGeneralPersonnel.ownResources" sortable="true" sortProperty="ownResources">
						<tags:formatCurrency value="${genP.ownResources}"/><c:set var="persWoOwn" value="${persWoOwn+genP.ownResources}"/>
					</display:column>
					<display:column titleKey="projectGeneralPersonnel.external" sortable="true" sortProperty="external">
						<tags:formatCurrency value="${genP.external}"/>
					</display:column>
					<display:column title="&nbsp;">
						<c:if test="${not empty genP.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty genP.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<a name="copy" href="../item/${genP.projItemId}/copy">
								<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
							</a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${genP_rowNum ne 1}">
								<a name="moveUp" href="../item/${genP.projItemId}/move?up=false">
									<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${genP_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${genP_rowNum ne fn:length(project.personnelWithouts)}">
								<a name="moveDown" href="../item/${genP.projItemId}/move?up=true">
									<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${genP_rowNum eq fn:length(project.personnelWithouts)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" style="margin-left:5px;" media="html">
							<a href="../item/${genP.projItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../item/${genP.projItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
					<display:footer>
						<tr height="1"><td height="1" colspan="14" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/>
						<td><tags:formatCurrency value="${persWoTotal}" /></td>
						<td><tags:formatCurrency value="${persWoOwn}" /></td>
						<td><tags:formatCurrency value="${persWoTotal-persWoOwn}" /></td>
						<td/><td/><td/><td/><td/><td/></tr>
					</display:footer>
				</display:table>
				<c:if test="${accessOK}">
					<div class="addNew"><a  id="newPersonnelWo" href="../item/-1?type=personnelWithout&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>
		</tags:tableContainer>
	</c:if>
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step9"/></tags:submit>
</form:form>
<tags:excelImport submitUrl="../../import/project/general/${project.projectId}"/>
</body></html>