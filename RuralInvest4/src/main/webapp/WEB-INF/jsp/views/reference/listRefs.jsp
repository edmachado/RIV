<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="reference.reference"/></title>
<c:if test="${not empty project}"><c:set var="probase" value="${project}"/></c:if><c:if test="${empty project}"><c:set var="probase" value="${profile}"/></c:if>
</head>
<body>
<form:form name="form" method="post" action="">
	<tags:tableContainer titleKey="reference.reference">
		<tags:table titleKey="reference.incomes">
				<display:table list="${probase.refIncomes}" htmlId="IncomeTable" id="item" requestURI="" cellspacing="0" cellpadding="0" export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="reference.income.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
					<display:column titleKey="reference.income.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
					<display:column titleKey="reference.income.unitCost" sortProperty="unitCost" sortable="true">
						<tags:formatCurrency value="${item.unitCost}"/>
					</display:column>
					<display:column titleKey="reference.income.transport" sortProperty="transport" sortable="true">
						<tags:formatCurrency value="${item.transport}"/>
					</display:column>
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<c:if test="${item_rowNum ne 1}">
								<a href="moveRefItem.htm?up=false&itemId=${item.refItemId}">
									<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${item_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${item_rowNum ne fn:length(probase.refIncomes)}">
								<a href="moveRefItem.htm?up=true&itemId=${item.refItemId}">
									<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${item_rowNum eq fn:length(probase.refIncomes)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" style="margin-left:5px;" media="html">
							<a href="../refItem/${item.refItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../refItem/${item.refItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
			 	</display:table>
				
				<div class="addNew"><a id="addIncome" href="../refItem/-1?type=income&proId=${probase.proId}&isProject=${probase.project}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				<c:if test="${accessOK}"></c:if>
			</tags:table>
	
			<tags:table titleKey="reference.costs">
				<display:table list="${probase.refCosts}" htmlId="GoodsTable" id="cost" requestURI="" class="data-table" cellspacing="0" cellpadding="0" export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="reference.cost.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
					<display:column titleKey="reference.cost.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
					<display:column titleKey="reference.cost.unitCost" sortProperty="unitCost" sortable="true">
						<tags:formatCurrency value="${cost.unitCost}"/>
					</display:column>
					<display:column titleKey="reference.cost.transport" sortProperty="transport" sortable="true">
						<tags:formatCurrency value="${cost.transport}"/>
					</display:column>
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<c:if test="${cost_rowNum ne 1}">
								<a href="../refItem/${cost.refItemId}/move?up=false">
									<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${cost_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${cost_rowNum ne fn:length(probase.refCosts)}">
								<a href="../refItem/${cost.refItemId}/move?up=true">
									<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${cost_rowNum eq fn:length(probase.refCosts)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" style="margin-left:5px;" media="html">
							<a href="../refItem/${cost.refItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../refItem/${cost.refItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
				</display:table>
				
				<div class="addNew"><a id="addInput" href="../refItem/-1?type=cost&proId=${probase.proId}&isProject=${probase.project}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				<c:if test="${accessOK}"></c:if>
			</tags:table>
		<!-- c:if test="${probase.project}"-->
			<tags:table titleKey="reference.labours">
				<display:table list="${probase.refLabours}" htmlId="LabourTable" id="item" requestURI="" class="data-table" cellspacing="0" cellpadding="0" export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="reference.labour.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
					<c:if test="${probase.project}">
						<display:column titleKey="reference.labour.unitType" sortable="true" style="text-align:left;" headerClass="left">
							<c:if test="${item.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
							<c:if test="${item.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
							<c:if test="${item.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
							<c:if test="${item.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
						</display:column>
					</c:if>
					<c:if test="${!probase.project}">
						<display:column titleKey="reference.labour.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
					</c:if>
					<display:column titleKey="reference.labour.unitCost" sortProperty="unitCost" sortable="true" >
						<tags:formatCurrency value="${item.unitCost}"/>
					</display:column>
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<c:if test="${item_rowNum ne 1}">
								<a href="moveRefItem.htm?up=false&itemId=${item.refItemId}">
									<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${item_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${item_rowNum ne fn:length(probase.refLabours)}">
								<a href="moveRefItem.htm?up=true&itemId=${item.refItemId}">
									<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${item_rowNum eq fn:length(probase.refLabours)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" style="margin-left:5px;" media="html">
							<a href="../refItem/${item.refItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../refItem/${item.refItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
				</display:table>
				
				<div class="addNew"><a id="addLabour" href="../refItem/-1?type=labour&proId=${probase.proId}&isProject=${probase.project}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				<c:if test="${accessOK}"></c:if>
			</tags:table>
		<!--/c:if-->
	</tags:tableContainer>
	<c:set var="nextStep"><c:if test="${project.incomeGen}">project.step11</c:if><c:if test="${not project.incomeGen}">project.step12</c:if></c:set>
	<tags:submit>&raquo;&nbsp;<spring:message code="misc.goto"/> <spring:message code="${nextStep}"/></tags:submit>
</form:form>
</body></html>