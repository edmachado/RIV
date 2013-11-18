<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="profile.step4"/></title></head>

<body>
<form:form name="form" method="post" commandName="profile">
	<tags:errors />
	<div align="left">
		<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadTemplate" href="../../report/${profile.profileId}/profileInvest.xlsx?template=true" target="_blank"><spring:message code="export.downloadTemplate"/></a><br/>
		<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a href="../../report/${profile.profileId}/profileInvest.xlsx" target="_blank"><spring:message code="export.download"/></a><br/>
		<c:if test="${accessOK}"><a id="importExcel" href="#"><img src="../../img/xls.gif" alt="Excel" title="Excel"/> <spring:message code="import.importExcel"/></a></c:if>
	</div>
	<tags:tableContainer titleKey="profile.investCosts">
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
							<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
						</a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${row_rowNum ne 1}">
							<a name="moveUp" href="../item/${row.profItemId}/move?up=false">
								<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0"/>
							</a>
						</c:if>
						<c:if test="${row_rowNum eq 1}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<c:if test="${row_rowNum ne fn:length(profile.glsGoods)}">
							<a name="moveDown" href="../item/${row.profItemId}/move?up=true">
								<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0"/>
							</a>
						</c:if>
						<c:if test="${row_rowNum eq fn:length(profile.glsGoods)}">
							<img src="../../img/spacer.gif" width="16" height="16" border="0">
						</c:if>
					</display:column>
					<display:column title="&nbsp;" style="margin-left:5px;" media="html">
						<a href="../item/${row.profItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
					<display:column title="&nbsp;" media="html">
						<a href="../item/${row.profItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
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
						<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
					</a>
				</display:column>
				<display:column title="&nbsp;" media="html">
					<c:if test="${lab_rowNum ne 1}">
						<a name="moveUp" href="../item/${lab.profItemId}/move?up=false">
							<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
						</a>
					</c:if>
					<c:if test="${lab_rowNum eq 1}">
						<img src="../../img/spacer.gif" width="16" height="16" border="0">
					</c:if>
				</display:column>
				<display:column title="&nbsp;" media="html">
					<c:if test="${lab_rowNum ne fn:length(profile.glsLabours)}">
						<a name="moveDown" href="../item/${lab.profItemId}/move?up=true">
							<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
						</a>
					</c:if>
					<c:if test="${lab_rowNum eq fn:length(profile.glsLabours)}">
						<img src="../../img/spacer.gif" width="16" height="16" border="0">
					</c:if>
				</display:column>
				<display:column title="&nbsp;" style="margin-left:5px;" media="html">
					<a id="editLabour${lab.orderBy}" href="../item/${lab.profItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
				</display:column>
				<display:column title="&nbsp;" media="html">
					<a href="../item/${lab.profItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
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
</tags:tableContainer>
<tags:submit><spring:message code="misc.goto"/> <spring:message code="profile.step5"/></tags:submit>
</form:form>
<tags:excelImport submitUrl="../../import/profile/invest/${profile.profileId}"/>
</body>
</html>