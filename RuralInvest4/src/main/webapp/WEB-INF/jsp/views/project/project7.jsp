<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step7"/></title></head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />
	
	<div align="left">
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadTemplate" href="../../report/${project.projectId}/projectInvestDetail.xlsx?template=true" target="_blank"><spring:message code="export.downloadTemplate"/></a><br/>
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a href="../../report/${project.projectId}/projectInvestDetail.xlsx" target="_blank"><spring:message code="export.download"/></a><br/>
 		<c:if test="${accessOK}"><a id="importExcel" href="#"><img src="../../img/xls.gif" alt="Excel" title="Excel"/> <spring:message code="import.importExcel"/></a></c:if>
 	</div>
	<c:set var="withTableTitle">
		<c:if test="${project.withWithout}"><spring:message code="project.invest"/> <spring:message code="project.with"/></c:if>
		<c:if test="${not project.withWithout}"><spring:message code="project.invest"/></c:if>
	</c:set>
	
	<tags:tableContainer title="${withTableTitle}">
		<tags:table titleKey="projectInvestAsset">
			<display:table htmlId="assetsTable" list="${project.assets}" requestURI="" cellspacing="0" cellpadding="0"
				 export="false" id="asset">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectInvestAsset.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectInvestAsset.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectInvestAsset.unitNum" sortProperty="unitNum" sortable="true">
					<tags:formatDecimal value="${asset.unitNum}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.unitCost" sortProperty="unitCost" sortable="true">
					<tags:formatCurrency value="${asset.unitCost}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.totalCost" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${asset.total}"/><c:set var="assetTotal" value="${assetTotal+asset.total}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.ownResources" sortable="true" sortProperty="ownResources">
					<tags:formatCurrency value="${asset.ownResources}"/><c:set var="assetOwn" value="${assetOwn+asset.ownResources}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.donated" sortable="true" sortProperty="donated">
					<tags:formatCurrency value="${asset.donated}"/><c:set var="assetDonated" value="${assetDonated+asset.donated}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.financed" sortable="true" sortProperty="financed">
					<tags:formatCurrency value="${asset.financed}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.econLife" property="econLife" sortable="true" />
				<display:column titleKey="projectInvestAsset.maintCost" sortProperty="maintCost" sortable="true">
					<tags:formatCurrency value="${asset.maintCost}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.salvage" sortProperty="salvage" sortable="true">
					<tags:formatCurrency value="${asset.salvage}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.replace" sortable="true" style="text-align:center;" headerClass="centered">
					<c:if test="${asset.replace}"><spring:message code="misc.yes"/></c:if>
					<c:if test="${not asset.replace}"><spring:message code="misc.no"/></c:if>
				</display:column>
				<display:column titleKey="projectInvestAsset.yearBegin" property="yearBegin" style="text-align:center;" headerClass="centered" sortable="true"/>
				
				<display:column title="&nbsp;">
					<c:if test="${not empty asset.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
					<c:if test="${empty asset.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
				</display:column>
				
				<c:if test="${accessOK}">
					<display:column title="&nbsp;" media="html">
						<a name="copy" href="../item/${asset.projItemId}/copy">
							<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
						</a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${asset_rowNum ne 1}">
							<a name="moveUp" href="../item/${asset.projItemId}/move?up=false">
								<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${asset_rowNum eq 1}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${asset_rowNum ne fn:length(project.assets)}">
							<a name="moveDown" href="../item/${asset.projItemId}/move?up=true">
								<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${asset_rowNum eq fn:length(project.assets)}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" style="margin-left:5px;" media="html">
						<a href="../item/${asset.projItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<a href="../item/${asset.projItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
				</c:if>
				<display:footer>
				  	<tr height="1"><td height="1" colspan="19" class="Sum1"/></tr>
					<tr><td/><td/><td/><td/>
					<td><tags:formatCurrency value="${assetTotal}" /></td>
					<td><tags:formatCurrency value="${assetOwn}" /></td>
					<td><tags:formatCurrency value="${assetDonated}" /></td>
					<td><tags:formatCurrency value="${assetTotal-assetOwn-assetDonated}" /></td>
					<td/><td/><td/><td/><td/><td/><td/><td/><td/><td/><td/></tr>
			  	</display:footer>
			</display:table>
			<c:if test="${accessOK}">
				<div class="addNew"><a id="newAsset" href="../item/-1?type=asset&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
			</c:if>
		</tags:table>
	
		<tags:table titleKey="projectInvestLabour">
			<display:table list="${project.labours}" id="lab" requestURI="" cellspacing="0" cellpadding="0"
				 export="false" htmlId="LabourTable">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectInvestLabour.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectInvestLabour.unitType" sortable="true" style="text-align:${left};" headerClass="left">
					<c:if test="${lab.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
					<c:if test="${lab.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
					<c:if test="${lab.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
					<c:if test="${lab.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
				</display:column>
				<display:column titleKey="projectInvestLabour.unitNum" sortProperty="unitNum" sortable="true">
					<tags:formatDecimal value="${lab.unitNum}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${lab.unitCost}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.totalCost" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${lab.total}"/><c:set var="labourTotal" value="${labourTotal+lab.total}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.ownResources" sortable="true" sortProperty="ownResources">
					<tags:formatCurrency value="${lab.ownResources}"/><c:set var="labourOwn" value="${labourOwn+lab.ownResources}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.donated" sortable="true" sortProperty="donated">
					<tags:formatCurrency value="${lab.donated}"/><c:set var="labourDonated" value="${labourDonated+lab.donated}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.financed" sortable="true" sortProperty="financed">
					<tags:formatCurrency value="${lab.financed}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.yearBegin" property="yearBegin" sortable="true" style="text-align:center;" headerClass="centered"/>
				
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
						<c:if test="${lab_rowNum ne fn:length(project.labours)}">
							<a name="moveDown" href="../item/${lab.projItemId}/move?up=true">
								<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${lab_rowNum eq fn:length(project.labours)}">
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
				  	<tr height="1"><td height="1" colspan="15" class="Sum1"/></tr>
					<tr><td/><td/><td/><td/>
					<td><tags:formatCurrency value="${labourTotal}" /></td>
					<td><tags:formatCurrency value="${labourOwn}" /></td>
					<td><tags:formatCurrency value="${labourDonated}" /></td>
					<td><tags:formatCurrency value="${labourTotal-labourOwn-labourDonated}" /></td>
					<td/><td/><td/><td/><td/><td/><td/></tr>
			  	</display:footer>
			</display:table>
			<c:if test="${accessOK}">
				<div class="addNew"><a id="newLabour" href="../item/-1?type=labour&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
			</c:if>
		</tags:table>
		
		<tags:table titleKey="projectInvestService">
			<display:table list="${project.services}" id="serv" requestURI="" cellspacing="0" cellpadding="0"
				 export="false" htmlId="ServicesTable">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectInvestService.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectInvestService.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectInvestService.unitNum" sortProperty="unitNum" sortable="true">
					<tags:formatDecimal value="${serv.unitNum}"/>
				</display:column>
				<display:column titleKey="projectInvestService.unitCost" sortable="true">
					<tags:formatCurrency value="${serv.unitCost}"/>
				</display:column>
				<display:column titleKey="projectInvestService.totalCost" sortProperty="total" sortable="true">
					<tags:formatCurrency value="${serv.total}"/><c:set var="serviceTotal" value="${serviceTotal+serv.total}"/>
				</display:column>
				<display:column titleKey="projectInvestService.ownResources" sortProperty="ownResources" sortable="true">
					<tags:formatCurrency value="${serv.ownResources}"/><c:set var="serviceOwn" value="${serviceOwn+serv.ownResources}"/>
				</display:column>
				<display:column titleKey="projectInvestService.donated" sortProperty="donated" sortable="true">
					<tags:formatCurrency value="${serv.donated}"/><c:set var="serviceDonated" value="${serviceDonated+serv.donated}"/>
				</display:column>
				<display:column titleKey="projectInvestService.financed" sortProperty="financed" sortable="true">
					<tags:formatCurrency value="${serv.financed}"/>
				</display:column>
				<display:column titleKey="projectInvestService.yearBegin" property="yearBegin" sortable="true" style="text-align:center;" headerClass="centered"/>
				
				<display:column title="&nbsp;">
					<c:if test="${not empty serv.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
					<c:if test="${empty serv.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
				</display:column>
				
				<c:if test="${accessOK}">
					<display:column title="&nbsp;" media="html">
						<a name="copy" href="../item/${serv.projItemId}/copy">
							<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
						</a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${serv_rowNum ne 1}">
							<a name="moveUp" href="../item/${serv.projItemId}/move?up=false">
								<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${serv_rowNum eq 1}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${serv_rowNum ne fn:length(project.services)}">
							<a name="moveDown" href="../item/${serv.projItemId}/move?up=true">
								<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${serv_rowNum eq fn:length(project.services)}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" style="margin-left:5px;" media="html">
						<a href="../item/${serv.projItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<a href="../item/${serv.projItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
				</c:if>
				<display:footer>
				  	<tr height="1"><td height="1" colspan="15" class="Sum1"/></tr>
					<tr><td/><td/><td/><td/>
					<td><tags:formatCurrency value="${serviceTotal}" /></td>
					<td><tags:formatCurrency value="${serviceOwn}" /></td>
					<td><tags:formatCurrency value="${serviceDonated}" /></td>
					<td><tags:formatCurrency value="${serviceTotal-serviceOwn-serviceDonated}" /></td>
					<td/><td/><td/><td/><td/><td/><td/></tr>
			  	</display:footer>
			</display:table>
			<c:if test="${accessOK}">
				<div class="addNew"><a id="newService" href="../item/-1?type=service&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
			</c:if>
		</tags:table>	
	</tags:tableContainer>

	<c:if test="${project.withWithout}">
		<c:set var="withoutTableTitle">
			<c:if test="${project.withWithout}"><spring:message code="project.invest"/> <spring:message code="project.without"/></c:if>
		</c:set>
		
		<tags:tableContainer title="${withoutTableTitle}">
			<tags:table titleKey="projectInvestAsset">
				<display:table htmlId="assetsTableWo" list="${project.assetsWithout}" requestURI="" cellspacing="0" cellpadding="0"
					 export="false" id="asset">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="projectInvestAsset.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="projectInvestAsset.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="projectInvestAsset.unitNum" sortProperty="unitNum" sortable="true">
						<tags:formatDecimal value="${asset.unitNum}"/>
					</display:column>
					<display:column titleKey="projectInvestAsset.unitCost" sortProperty="unitCost" sortable="true">
						<tags:formatCurrency value="${asset.unitCost}"/>
					</display:column>
					<display:column titleKey="projectInvestAsset.totalCost" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${asset.total}"/><c:set var="assetWithoutTotal" value="${assetWithoutTotal+asset.total}"/>
					</display:column>
					<display:column titleKey="projectInvestAsset.ownResources" sortable="true" sortProperty="ownResources">
						<tags:formatCurrency value="${asset.ownResources}"/><c:set var="assetWithoutOwn" value="${assetWithoutOwn+asset.ownResources}"/>
					</display:column>
					<display:column titleKey="projectInvestAsset.donated" sortable="true" sortProperty="donated">
						<tags:formatCurrency value="${asset.donated}"/><c:set var="assetWithoutDonated" value="${assetWithoutDonated+asset.donated}"/>
					</display:column>
					<display:column titleKey="projectInvestAsset.financed" sortable="true" sortProperty="financed">
						<tags:formatCurrency value="${asset.financed}"/>
					</display:column>
					<display:column titleKey="projectInvestAsset.econLife" property="econLife" sortable="true" />
					<display:column titleKey="projectInvestAsset.maintCost" sortProperty="maintCost" sortable="true">
						<tags:formatCurrency value="${asset.maintCost}"/>
					</display:column>
					<display:column titleKey="projectInvestAsset.salvage" sortProperty="salvage" sortable="true">
						<tags:formatCurrency value="${asset.salvage}"/>
					</display:column>
					<display:column titleKey="projectInvestAsset.replace" sortable="true" style="text-align:center;" headerClass="centered">
						<c:if test="${asset.replace}"><spring:message code="misc.yes"/></c:if>
						<c:if test="${not asset.replace}"><spring:message code="misc.no"/></c:if>
					</display:column>
					<display:column titleKey="projectInvestAsset.yearBegin" property="yearBegin" style="text-align:center;" headerClass="centered" sortable="true"/>
					
					<display:column title="&nbsp;">
						<c:if test="${not empty asset.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty asset.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
					
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<a name="copy" href="../item/${asset.projItemId}/copy">
								<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
							</a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${asset_rowNum ne 1}">
								<a name="moveUp" href="../item/${asset.projItemId}/move?up=false">
									<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${asset_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${asset_rowNum ne fn:length(project.assetsWithout)}">
								<a name="moveDown" href="../item/${asset.projItemId}/move?up=true">
									<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${asset_rowNum eq fn:length(project.assetsWithout)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" style="margin-left:5px;" media="html">
							<a href="../item/${asset.projItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../item/${asset.projItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
					<display:footer>
					  	<tr height="1"><td height="1" colspan="19" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/>
						<td><tags:formatCurrency value="${assetWithoutTotal}" /></td>
						<td><tags:formatCurrency value="${assetWithoutOwn}" /></td>
						<td><tags:formatCurrency value="${assetWithoutDonated}" /></td>
						<td><tags:formatCurrency value="${assetWithoutTotal-assetWithoutOwn-assetWithoutDonated}" /></td>
						<td/><td/><td/><td/><td/><td/><td/><td/><td/><td/><td/></tr>
				  	</display:footer>
				</display:table>
				<c:if test="${accessOK}">
					<div class="addNew"><a id="newAssetWo" href="../item/-1?type=assetWithout&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>
		
			<tags:table titleKey="projectInvestLabour">
				<display:table list="${project.laboursWithout}" id="lab" requestURI="" cellspacing="0" cellpadding="0"
					 export="false" htmlId="LabourTableWo">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="projectInvestLabour.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="projectInvestLabour.unitType" sortable="true" style="text-align:${left};" headerClass="left">
						<c:if test="${lab.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
						<c:if test="${lab.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
						<c:if test="${lab.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
						<c:if test="${lab.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
					</display:column>
					<display:column titleKey="projectInvestLabour.unitNum" sortProperty="unitNum" sortable="true">
						<tags:formatDecimal value="${lab.unitNum}"/>
					</display:column>
					<display:column titleKey="projectInvestLabour.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${lab.unitCost}"/>
					</display:column>
					<display:column titleKey="projectInvestLabour.totalCost" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${lab.total}"/><c:set var="labourWithoutTotal" value="${labourWithoutTotal+lab.total}"/>
					</display:column>
					<display:column titleKey="projectInvestLabour.ownResources" sortable="true" sortProperty="ownResources">
						<tags:formatCurrency value="${lab.ownResources}"/><c:set var="labourWithoutOwn" value="${labourWithoutOwn+lab.ownResources}"/>
					</display:column>
					<display:column titleKey="projectInvestLabour.donated" sortable="true" sortProperty="donated">
						<tags:formatCurrency value="${lab.donated}"/><c:set var="labourWithoutDonated" value="${labourWithoutDonated+lab.donated}"/>
					</display:column>
					<display:column titleKey="projectInvestLabour.financed" sortable="true" sortProperty="financed">
						<tags:formatCurrency value="${lab.financed}"/>
					</display:column>
					<display:column titleKey="projectInvestLabour.yearBegin" property="yearBegin" sortable="true" style="text-align:center;" headerClass="centered"/>
					
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
							<c:if test="${lab_rowNum ne fn:length(project.laboursWithout)}">
								<a name="moveDown" href="../item/${lab.projItemId}/move?up=true">
									<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${lab_rowNum eq fn:length(project.laboursWithout)}">
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
					  	<tr height="1"><td height="1" colspan="15" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/>
						<td><tags:formatCurrency value="${labourWithoutTotal}" /></td>
						<td><tags:formatCurrency value="${labourWithoutOwn}" /></td>
						<td><tags:formatCurrency value="${labourWithoutDonated}" /></td>
						<td><tags:formatCurrency value="${labourWithoutTotal-labourWithoutOwn-labourWithoutDonated}" /></td>
						<td/><td/><td/><td/><td/><td/><td/></tr>
				  	</display:footer>
				</display:table>
				<c:if test="${accessOK}">
					<div class="addNew"><a id="newLabourWo" href="../item/-1?type=labourWithout&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>
			
			<tags:table titleKey="projectInvestService">
				<display:table list="${project.servicesWithout}" id="serv" requestURI="" cellspacing="0" cellpadding="0"
					 export="false" htmlId="ServicesTableWo">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="projectInvestService.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="projectInvestService.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="projectInvestService.unitNum" sortProperty="unitNum" sortable="true">
						<tags:formatDecimal value="${serv.unitNum}"/>
					</display:column>
					<display:column titleKey="projectInvestService.unitCost" sortable="true">
						<tags:formatCurrency value="${serv.unitCost}"/>
					</display:column>
					<display:column titleKey="projectInvestService.totalCost" sortProperty="total" sortable="true">
						<tags:formatCurrency value="${serv.total}"/><c:set var="serviceWithoutTotal" value="${serviceWithoutTotal+serv.total}"/>
					</display:column>
					<display:column titleKey="projectInvestService.ownResources" sortProperty="ownResources" sortable="true">
						<tags:formatCurrency value="${serv.ownResources}"/><c:set var="serviceWithoutOwn" value="${serviceWithoutOwn+serv.ownResources}"/>
					</display:column>
					<display:column titleKey="projectInvestService.donated" sortProperty="donated" sortable="true">
						<tags:formatCurrency value="${serv.donated}"/><c:set var="serviceWithoutDonated" value="${serviceWithoutDonated+serv.donated}"/>
					</display:column>
					<display:column titleKey="projectInvestService.financed" sortProperty="financed" sortable="true">
						<tags:formatCurrency value="${serv.financed}"/>
					</display:column>
					<display:column titleKey="projectInvestService.yearBegin" property="yearBegin" sortable="true" style="text-align:center;" headerClass="centered"/>
					
					<display:column title="&nbsp;">
						<c:if test="${not empty serv.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty serv.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
					
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<a name="copy" href="../item/${serv.projItemId}/copy">
								<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
							</a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${serv_rowNum ne 1}">
								<a name="moveUp" href="../item/${serv.projItemId}/move?up=false">
									<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${serv_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${serv_rowNum ne fn:length(project.servicesWithout)}">
								<a name="moveDown" href="../item/${serv.projItemId}/move?up=true">
									<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${serv_rowNum eq fn:length(project.servicesWithout)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" style="margin-left:5px;" media="html">
							<a href="../item/${serv.projItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../item/${serv.projItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
					<display:footer>
					  	<tr height="1"><td height="1" colspan="15" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/>
						<td><tags:formatCurrency value="${serviceWithoutTotal}" /></td>
						<td><tags:formatCurrency value="${serviceWithoutOwn}" /></td>
						<td><tags:formatCurrency value="${serviceWithoutDonated}" /></td>
						<td><tags:formatCurrency value="${serviceWithoutTotal-serviceWithoutOwn-serviceWithoutDonated}" /></td>
						<td/><td/><td/><td/><td/><td/><td/></tr>
				  	</display:footer>
				</display:table>
				<c:if test="${accessOK}">
					<div class="addNew"><a id="newServiceWo" href="../item/-1?type=serviceWithout&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>	
		</tags:tableContainer>
	</c:if>
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step8"/></tags:submit>
</form:form>
<tags:excelImport submitUrl="../../import/project/invest/${project.projectId}"/>
</body></html>