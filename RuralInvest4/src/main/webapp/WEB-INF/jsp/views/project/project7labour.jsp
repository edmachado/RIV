<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${projectItem.project}" scope="request"/>
<html><head><title><spring:message code="projectInvestLabour"/></title>
<script>
$(function() { CalculateDonated(); CalculateTotal(); });
</script>
</head>
<body>
	<div class="datatitle">
		<c:if test="${not project.withWithout}"><spring:message code="project.invest"/></c:if>
		<c:if test="${project.withWithout and without}"><spring:message code="project.invest"/> <spring:message code="project.without"/></c:if>
		<c:if test="${project.withWithout and not without}"><spring:message code="project.invest"/> <spring:message code="project.with"/></c:if>
	</div>
	<div align="right"><a href="#" onClick="toggle('tblLabour')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblLabour" style="display:none">
		<tags:table titleKey="projectInvestLabour">
			<c:if test="${projectItem.getClass().getSimpleName() eq 'ProjectItemLabour'}"><c:set var="tableSource" value="${project.labours}"/></c:if>
			<c:if test="${projectItem.getClass().getSimpleName() eq 'ProjectItemLabourWithout'}"><c:set var="tableSource" value="${project.laboursWithout}"/></c:if>
			<display:table list="${tableSource}" id="lab" requestURI="" cellspacing="0" cellpadding="0"
				 export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectInvestLabour.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectInvestLabour.unitType" sortable="true" style="text-align:left;" headerClass="left">
					<c:if test="${lab.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
					<c:if test="${lab.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
					<c:if test="${lab.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
					<c:if test="${lab.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
				</display:column>
				<display:column titleKey="projectInvestLabour.unitNum" sortProperty="unitNum" sortable="true" >
					<tags:formatDecimal value="${lab.unitNum}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.unitCost" sortable="true">
					<tags:formatCurrency value="${lab.unitCost}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.totalCost" sortable="true">
					<tags:formatCurrency value="${lab.total }"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.ownResources" property="ownResources" sortable="true">
					<tags:formatCurrency value="${lab.ownResources}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.donated" property="donated" sortable="true">
					<tags:formatCurrency value="${lab.donated}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.financed" sortable="true">
					<tags:formatCurrency value="${lab.financed}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.yearBegin" property="yearBegin" sortable="true" style="text-align:center;" headerClass="centered"/>
			</display:table>
		</tags:table>
	</div>
	
	<form:form name="form" method="post" commandName="projectItem">
		<tags:errors />
		<div style="display:inline-block;width:470px">
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="projectInvestLabour"/>)</legend>
				<tags:dataentry field="description" labelKey="projectInvestLabour.description" helpText="projectInvestLabour.description.help" inputClass="text" size="20" maxLength="30"/>
				<div class="dataentry">
					<tags:help text="projectInvestLabour.unitType.help" title="projectInvestLabour.unitType"><label><spring:message code="projectInvestLabour.unitType"/></label></tags:help>
					<form:select path="unitType">
						<form:option value="0"><spring:message code="units.pyears"/></form:option>
						<form:option value="1"><spring:message code="units.pmonths"/></form:option>
						<form:option value="2"><spring:message code="units.pweeks"/></form:option>
						<form:option value="3"><spring:message code="units.pdays"/></form:option>
					</form:select>
				</div>
				<tags:dataentry field="unitNum" labelKey="projectInvestLabour.unitNum" helpText="projectInvestLabour.unitNum.help" onmouseout="CalculateTotal()"/>
				<tags:dataentry field="unitCost" labelKey="projectInvestLabour.unitCost" helpText="projectInvestLabour.unitCost.help" currency="true" onmouseout="CalculateTotal()"/>
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="projectInvestLabour.totalCost" helpText="projectInvestLabour.totalCost.help" currency="true" calculated="true" />
				
				<tags:donations donors="${projectItem.project.donors}" labelKey="projectInvestLabour.donated" helpText="projectInvestLabour.donated.help" onmouseout="CalculateFinance()"/>
				
				<tags:dataentry field="ownResources" labelKey="projectInvestLabour.ownResources" helpText="projectInvestLabour.ownResources.help" currency="true" onmouseout="CalculateFinance()"/>
				<tags:datadivider color="orange"/>
				<tags:dataentry field="financed" labelKey="projectInvestLabour.financed" helpText="projectInvestLabour.financed.help" currency="true" calculated="true" />
				<!-- value="${(projectInvestLabour.unitNum * projectInvestLabour.unitCost) - projectInvestLabour.ownResources - projectInvestLabour.donated }" -->
				<tags:projYear field="yearBegin" labelKey="projectInvestLabour.yearBegin" helpText="projectInvestLabour.yearBegin.help" />
			</fieldset>
		</div>
		<div style="display:inline-block;">
			<tags:refItemChooser type="2" linked="${projectItem.linkedTo}" notLinked="'unitNum,donated,ownResources'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="CalculateTotal();" />
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
	
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="CalculateTotal" calc="*" callWhenDone="CalculateFinance" />
<tags:jscriptCalc fieldA="total" fieldB="donated" fieldC="financed" fieldD="ownResources" functionName="CalculateFinance" calc="-" calc2="-" />
</body></html>