<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="profile.step4"/></title>
<script>
$(function() {
	<c:if test="${profile.withWithout}">	$("#tabs").tabs();
	 if(window.location.hash=='#without'){ $("#tabs").tabs("option", "active", 1);} 
	</c:if>
	$( document ).tooltip();
});
</script>
<style>
	#tabs ul { margin:0; }
</style>
</head>

<body>
<form:form name="form" method="post" commandName="profile">
	<tags:errors />
	<div align="left">
		<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadTemplate" href="../../report/${profile.profileId}/profileInvest.xlsx?template=true" target="_blank"><spring:message code="export.downloadTemplate"/></a><br/>
		<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadExcel" href="../../report/${profile.profileId}/profileInvest.xlsx" target="_blank"><spring:message code="export.download"/></a><br/>
		<c:if test="${accessOK}"><a id="importExcel" href="#"><img src="../../img/xls.gif" alt="Excel" title="Excel"/> <spring:message code="import.importExcel"/></a></c:if>
	</div>
	<br/>
	<div id="tabs">
		<c:if test="${profile.withWithout}"><ul>
			<li><a href="#tabs-with"><spring:message code="projectBlock.with.with"/></a></li>
			<li><a href="#tabs-without"><spring:message code="projectBlock.with.without"/></a></li>
		</ul></c:if>
		<div id="tabs-with">
			<span class="error"><form:errors path="glsGoods" /></span>
			<tags:table titleKey="profileGoods">
				<display:table htmlId="goodsListTable" list="${profile.glsGoods}" id="row" requestURI="" cellspacing="0" cellpadding="0"
					 export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="profileGoods.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="profileGoods.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="profileGoods.unitNum" sortProperty="unitNum" sortable="true" style="text-align:center;" headerClass="left">
						<tags:formatDecimal value="${row.unitNum}" />
					</display:column>
					<display:column titleKey="profileGoods.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${row.unitCost}"/>
					</display:column>
					<display:column titleKey="profileGoods.totalCost" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${row.total}"/><c:set var="goodTotal" value="${goodTotal+row.total}"/>
					</display:column>
					<display:column titleKey="profileGoods.ownResource" sortable="true" sortProperty="ownResource">
						<tags:formatCurrency value="${row.ownResource}"/><c:set var="goodOwn" value="${goodOwn+row.ownResource}"/>
					</display:column>
					<display:column titleKey="profileGoods.externalResources" sortable="true" sortProperty="donated">
						<tags:formatCurrency value="${row.donated}"/>
					</display:column>
					<display:column titleKey="profileGoods.econLife" sortProperty="econLife" sortable="true">
						<tags:formatDecimal value="${row.econLife}"/>
					</display:column>
					<display:column titleKey="profileGoods.salvage" sortable="true" sortProperty="salvage">
						<tags:formatCurrency value="${row.salvage}"/>
					</display:column>
					<display:column titleKey="profileGoods.reserve" sortable="true" sortProperty="reserve">
						<tags:formatCurrency value="${row.reserve}"/><c:set var="goodReserve" value="${goodReserve+row.reserve}"/>
					</display:column>
					<display:column title="&nbsp;">
						<c:if test="${not empty row.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty row.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<a name="copy" href="../item/${row.profItemId}/copy">
								<img src="../../img/duplicate.gif" title="<spring:message code="misc.copy"/>" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
							</a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${row_rowNum ne 1}">
								<a name="moveUp" href="../item/${row.profItemId}/move?up=false">
									<img src="../../img/arrow_up.png" title="<spring:message code="misc.moveUp"/>" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0"/>
								</a>
							</c:if>
							<c:if test="${row_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${row_rowNum ne fn:length(profile.glsGoods)}">
								<a name="moveDown" href="../item/${row.profItemId}/move?up=true">
									<img src="../../img/arrow_down.png" title="<spring:message code="misc.moveDown"/>" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0"/>
								</a>
							</c:if>
							<c:if test="${row_rowNum eq fn:length(profile.glsGoods)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" style="margin-left:5px;" media="html">
							<a href="../item/${row.profItemId}"><img src="../../img/edit.png" title="<spring:message code="misc.viewEditItem"/>" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="javascript:confirmDelete('../item/${row.profItemId}/delete');"><img src="../../img/delete.gif" title="<spring:message code="misc.deleteItem"/>" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
					<display:footer>
						<tr height="1"><td height="1" colspan="16" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/>
						<td><tags:formatCurrency value="${goodTotal}" /></td>
						<td><tags:formatCurrency value="${goodOwn}" /></td>
						<td><tags:formatCurrency value="${goodTotal-goodOwn}" /></td>
						<td/><td/>
						<td><tags:formatCurrency value="${goodReserve}" /></td>
						<td/><td/><td/><td/><td/><td/>
						</tr>
					</display:footer>
				</display:table>
			<c:if test="${accessOK}">
				<div class="addNew"><a id="newGood" href="../item/-1?type=good&profileId=${profile.profileId}">
					<img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;
				</div>
			</c:if>
		</tags:table>
		
		<span class="error"><form:errors path="glsLabours"  /></span>
		<tags:table titleKey="profileLabour">
			<display:table htmlId="labourListTable" list="${profile.glsLabours}" id="lab" requestURI="" cellspacing="0" cellpadding="0"
				 export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="profileLabour.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="profileLabour.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="profileLabour.unitNum" sortProperty="unitNum" sortable="true">
					<tags:formatDecimal value="${lab.unitNum}"/>
				</display:column>
				<display:column titleKey="profileLabour.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${lab.unitCost}"/>
				</display:column>
				<display:column titleKey="profileLabour.totalCost" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${lab.total}"/><c:set var="labourTotal" value="${labourTotal+lab.total}"/>
				</display:column>
				<display:column titleKey="profileLabour.ownResource" sortable="true" sortProperty="ownResource">
					<tags:formatCurrency value="${lab.ownResource}"/><c:set var="labourOwn" value="${labourOwn+lab.ownResource}"/>
				</display:column>
				<display:column titleKey="profileLabour.externalResources" sortable="true" sortProperty="donated">
					<tags:formatCurrency value="${lab.donated}"/>
				</display:column>
				<display:column title="&nbsp;">
					<c:if test="${not empty lab.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
					<c:if test="${empty lab.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
				</display:column>
				<c:if test="${accessOK}">
					<display:column title="&nbsp;" media="html">
						<a name="copy" href="../item/${lab.profItemId}/copy">
							<img src="../../img/duplicate.gif" title="<spring:message code="misc.copy"/>" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
						</a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${lab_rowNum ne 1}">
							<a name="moveUp" href="../item/${lab.profItemId}/move?up=false">
								<img src="../../img/arrow_up.png" title="<spring:message code="misc.moveUp"/>" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${lab_rowNum eq 1}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${lab_rowNum ne fn:length(profile.glsLabours)}">
							<a name="moveDown" href="../item/${lab.profItemId}/move?up=true">
								<img src="../../img/arrow_down.png" title="<spring:message code="misc.moveDown"/>" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${lab_rowNum eq fn:length(profile.glsLabours)}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" style="margin-left:5px;" media="html">
						<a id="editLabour${lab.orderBy}" href="../item/${lab.profItemId}"><img src="../../img/edit.png" title="<spring:message code="misc.viewEditItem"/>" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<a href="javascript:confirmDelete('../item/${lab.profItemId}/delete');"><img src="../../img/delete.gif" title="<spring:message code="misc.deleteItem"/>" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
				</c:if>
				<display:footer>
					<tr height="1"><td height="1" colspan="15" class="Sum1"/></tr>
					<tr><td/><td/><td/><td/>
					<td><tags:formatCurrency value="${labourTotal}" /></td>
					<td><tags:formatCurrency value="${labourOwn}" /></td>
					<td><tags:formatCurrency value="${labourTotal-labourOwn}" /></td>
					<td/><td/><td/><td/><td/><td/></tr>
				</display:footer>
			</display:table>
			<c:if test="${accessOK}">
				<div class="addNew"><a id="newLabour" href="../item/-1?type=labour&profileId=${profile.profileId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
			</c:if>
		</tags:table>
	</div>
	<c:if test="${profile.withWithout}"><div id="tabs-without">
		<tags:table titleKey="profileGoods">
				<display:table htmlId="goodsWoListTable" list="${profile.glsGoodsWithout}" id="goodWo" requestURI="" cellspacing="0" cellpadding="0"
					 export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="profileGoods.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="profileGoods.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="profileGoods.unitNum" sortProperty="unitNum" sortable="true" style="text-align:center;" headerClass="left">
						<tags:formatDecimal value="${goodWo.unitNum}" />
					</display:column>
					<display:column titleKey="profileGoods.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${goodWo.unitCost}"/>
					</display:column>
					<display:column titleKey="profileGoods.totalCost" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${goodWo.total}"/><c:set var="goodWoTotal" value="${goodWoTotal+goodWo.total}"/>
					</display:column>
					<display:column titleKey="profileGoods.ownResource" sortable="true" sortProperty="ownResource">
						<tags:formatCurrency value="${goodWo.ownResource}"/><c:set var="goodWoOwn" value="${goodWoOwn+goodWo.ownResource}"/>
					</display:column>
					<display:column titleKey="profileGoods.externalResources" sortable="true" sortProperty="donated">
						<tags:formatCurrency value="${goodWo.donated}"/>
					</display:column>
					<display:column titleKey="profileGoods.econLife" sortProperty="econLife" sortable="true">
						<tags:formatDecimal value="${goodWo.econLife}"/>
					</display:column>
					<display:column titleKey="profileGoods.salvage" sortable="true" sortProperty="salvage">
						<tags:formatCurrency value="${goodWo.salvage}"/>
					</display:column>
					<display:column titleKey="profileGoods.reserve" sortable="true" sortProperty="reserve">
						<tags:formatCurrency value="${goodWo.reserve}"/><c:set var="goodWoReserve" value="${goodWoReserve+goodWo.reserve}"/>
					</display:column>
					<display:column title="&nbsp;">
						<c:if test="${not empty goodWo.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty goodWo.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<a name="copy" href="../item/${goodWo.profItemId}/copy">
								<img src="../../img/duplicate.gif" title="<spring:message code="misc.copy"/>" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
							</a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${goodWo_rowNum ne 1}">
								<a name="moveUp" href="../item/${goodWo.profItemId}/move?up=false">
									<img src="../../img/arrow_up.png" title="<spring:message code="misc.moveUp"/>" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0"/>
								</a>
							</c:if>
							<c:if test="${goodWo_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${goodWo_rowNum ne fn:length(profile.glsGoodsWithout)}">
								<a name="moveDown" href="../item/${goodWo.profItemId}/move?up=true">
									<img src="../../img/arrow_down.png" title="<spring:message code="misc.moveDown"/>" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0"/>
								</a>
							</c:if>
							<c:if test="${goodWo_rowNum eq fn:length(profile.glsGoodsWithout)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" style="margin-left:5px;" media="html">
							<a href="../item/${goodWo.profItemId}"><img src="../../img/edit.png" title="<spring:message code="misc.viewEditItem"/>" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="javascript:confirmDelete('../item/${goodWo.profItemId}/delete');"><img src="../../img/delete.gif" title="<spring:message code="misc.deleteItem"/>"  alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
					<display:footer>
						<tr height="1"><td height="1" colspan="16" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/>
						<td><tags:formatCurrency value="${goodWoTotal}" /></td>
						<td><tags:formatCurrency value="${goodWoOwn}" /></td>
						<td><tags:formatCurrency value="${goodWoTotal-goodWoOwn}" /></td>
						<td/><td/>
						<td><tags:formatCurrency value="${goodWoReserve}" /></td>
						<td/><td/><td/><td/><td/><td/>
						</tr>
					</display:footer>
				</display:table>
			<c:if test="${accessOK}">
				<div class="addNew"><a id="newGoodWo" href="../item/-1?type=goodWithout&profileId=${profile.profileId}">
					<img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;
				</div>
			</c:if>
		</tags:table>
		<tags:table titleKey="profileLabour">
			<display:table htmlId="labourWoListTable" list="${profile.glsLaboursWithout}" id="lab" requestURI="" cellspacing="0" cellpadding="0"
				 export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="profileLabour.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="profileLabour.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="profileLabour.unitNum" sortProperty="unitNum" sortable="true">
					<tags:formatDecimal value="${lab.unitNum}"/>
				</display:column>
				<display:column titleKey="profileLabour.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${lab.unitCost}"/>
				</display:column>
				<display:column titleKey="profileLabour.totalCost" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${lab.total}"/><c:set var="labourWoTotal" value="${labourWoTotal+lab.total}"/>
				</display:column>
				<display:column titleKey="profileLabour.ownResource" sortable="true" sortProperty="ownResource">
					<tags:formatCurrency value="${lab.ownResource}"/><c:set var="labourWoOwn" value="${labourWoOwn+lab.ownResource}"/>
				</display:column>
				<display:column titleKey="profileLabour.externalResources" sortable="true" sortProperty="donated">
					<tags:formatCurrency value="${lab.donated}"/>
				</display:column>
				<display:column title="&nbsp;">
					<c:if test="${not empty lab.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
					<c:if test="${empty lab.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
				</display:column>
				<c:if test="${accessOK}">
					<display:column title="&nbsp;" media="html">
						<a name="copy" href="../item/${lab.profItemId}/copy">
							<img src="../../img/duplicate.gif" title="<spring:message code="misc.copy"/>" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
						</a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${lab_rowNum ne 1}">
							<a name="moveUp" href="../item/${lab.profItemId}/move?up=false">
								<img src="../../img/arrow_up.png" title="<spring:message code="misc.moveUp"/>" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${lab_rowNum eq 1}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${lab_rowNum ne fn:length(profile.glsLaboursWithout)}">
							<a name="moveDown" href="../item/${lab.profItemId}/move?up=true">
								<img src="../../img/arrow_down.png" title="<spring:message code="misc.moveDown"/>" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
							</a>
						</c:if>
						<c:if test="${lab_rowNum eq fn:length(profile.glsLaboursWithout)}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" style="margin-left:5px;" media="html">
						<a id="editLabour${lab.orderBy}" href="../item/${lab.profItemId}"><img src="../../img/edit.png" title="<spring:message code="misc.viewEditItem"/>" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<a href="javascript:confirmDelete('../item/${lab.profItemId}/delete');"><img src="../../img/delete.gif" title="<spring:message code="misc.deleteItem"/>" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
				</c:if>
				<display:footer>
					<tr height="1"><td height="1" colspan="15" class="Sum1"/></tr>
					<tr><td/><td/><td/><td/>
					<td><tags:formatCurrency value="${labourWoTotal}" /></td>
					<td><tags:formatCurrency value="${labourWoOwn}" /></td>
					<td><tags:formatCurrency value="${labourWoTotal-labourWoOwn}" /></td>
					<td/><td/><td/><td/><td/><td/></tr>
				</display:footer>
			</display:table>
			<c:if test="${accessOK}">
				<div class="addNew"><a id="newLabourWo" href="../item/-1?type=labourWithout&profileId=${profile.profileId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
			</c:if>
		</tags:table>
	</div></c:if>
	</div>
<tags:submit><spring:message code="misc.goto"/> <spring:message code="profile.step5"/></tags:submit>
</form:form>
<tags:confirmDelete/>
<tags:excelImport submitUrl="../../import/profile/invest/${profile.profileId}"/>
</body>
</html>