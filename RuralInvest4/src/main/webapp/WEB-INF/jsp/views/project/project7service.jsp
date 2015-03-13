<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${projectItem.project}" scope="request"/>
<html><head><title><spring:message code="projectInvestService"/></title></head>
<body>
	<div class="datatitle">
		<c:if test="${not project.withWithout}"><spring:message code="project.invest"/></c:if>
		<c:if test="${project.withWithout and without}"><spring:message code="project.invest"/> <spring:message code="project.without"/></c:if>
		<c:if test="${project.withWithout and not without}"><spring:message code="project.invest"/> <spring:message code="project.with"/></c:if>
	</div>
	<div align="right"><a href="#" onClick="toggle('tblService')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblService" style="display:none">
		<tags:table titleKey="projectInvestService">
			<c:if test="${projectItem.getClass().getSimpleName() eq 'ProjectItemService'}"><c:set var="tableSource" value="${project.services}"/></c:if>
			<c:if test="${projectItem.getClass().getSimpleName() eq 'ProjectItemServiceWithout'}"><c:set var="tableSource" value="${project.servicesWithout}"/></c:if>
			<display:table list="${tableSource}" id="serv" requestURI="" cellspacing="0" cellpadding="0"
				 export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectInvestService.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectInvestService.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectInvestService.unitNum" sortProperty="unitNum" sortable="true">
						<tags:formatDecimal value="${serv.unitNum}"/>
					</display:column>
				<display:column titleKey="projectInvestService.unitCost" sortable="true">
					<tags:formatCurrency value="${serv.unitCost}"/>
				</display:column>
				<display:column titleKey="projectInvestService.totalCost" sortable="true">
					<tags:formatCurrency value="${serv.unitNum * serv.unitCost }"/>
				</display:column>
				<display:column titleKey="projectInvestService.ownResources" sortable="true">
					<tags:formatCurrency value="${serv.ownResources}"/>
				</display:column>
				<display:column titleKey="projectInvestService.donated" property="donated" sortable="true">
					<tags:formatCurrency value="${serv.donated}"/>
				</display:column>
				<display:column titleKey="projectInvestService.financed" sortable="true">
					<tags:formatCurrency value="${(serv.unitNum * serv.unitCost) - serv.ownResources - serv.donated }"/>
				</display:column>
				<display:column titleKey="projectInvestService.yearBegin" property="yearBegin" sortable="true" style="text-align:center;" headerClass="centered"/>
			</display:table>
		</tags:table>	
	</div>
	
	<form:form name="form" method="post" commandName="projectItem">
		<tags:errors />
		<div style="display:inline-block;width:470px">
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="projectInvestService"/>)</legend>
				<tags:dataentry field="description" labelKey="projectInvestService.description" helpText="projectInvestService.description.help" inputClass="text" size="20" maxLength="30"/>
				<tags:dataentry field="unitType" labelKey="projectInvestService.unitType" helpText="projectInvestService.unitType.help" inputClass="text" size="20"/>
				<tags:dataentry field="unitNum" labelKey="projectInvestService.unitNum" helpText="projectInvestService.unitNum.help" onmouseout="CalculateTotal()"/>
				<tags:dataentry field="unitCost" labelKey="projectInvestService.unitCost" helpText="projectInvestService.unitCost.help" currency="true" onmouseout="CalculateTotal()"/>
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="projectInvestService.totalCost" helpText="projectInvestService.totalCost.help" currency="true" calculated="true" />
				<tags:donations donors="${projectItem.project.donors}" labelKey="projectInvestService.donated" helpText="projectInvestService.donated.help" onmouseout="CalculateFinance()"/>
				<tags:dataentry field="ownResources" labelKey="projectInvestService.ownResources" helpText="projectInvestService.ownResources.help" currency="true" onmouseout="CalculateFinance()"/>
				<tags:datadivider color="orange"/>
				<tags:dataentry field="financed" labelKey="projectInvestService.financed" helpText="projectInvestService.financed.help" currency="true" calculated="true" />
				<!-- value="${ (projectInvestService.unitNum*projectInvestService.unitCost)- projectInvestService.donated- projectInvestService.ownResources }" -->
				<tags:dataentry field="yearBegin" labelKey="projectInvestService.yearBegin" helpText="projectInvestService.yearBegin.help" />
			</fieldset>
		</div>
		<div style="display:inline-block;">
			<tags:refItemChooser type="0" linked="${projectItem.linkedTo}" notLinked="'unitNum,donated,ownResources'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="CalculateTotal();" />
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
	
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="CalculateTotal" calc="*" callWhenDone="CalculateFinance" />
<tags:jscriptCalc fieldA="total" fieldB="donated" fieldC="financed" fieldD="ownResources" functionName="CalculateFinance" calc="-" calc2="-" />
</body></html>