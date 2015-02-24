<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${blockItem.block.project}" scope="request"/>
<c:if test="${project.incomeGen}"><c:set var="title"><spring:message code="projectBlockIncome"/></c:set></c:if>
<c:if test="${not project.incomeGen}"><c:set var="title"><spring:message code="projectActivityCharge"/></c:set></c:if>
<html><head><title>${title}</title></head>
<body>
		<div align="right"><a href="#" onClick="toggle('tblIncome')"><spring:message code="misc.toggle"/></a></div>
		<div id="tblIncome" style="display:none">
		
			<c:set var="blockExplanation">(<spring:message code="misc.per.unitType"/> ${projectItem.block.unitType} <spring:message code="misc.per.cycleLength"/> ${blockItem.block.cycleLength} ${lengthUnit})</c:set>
		
			<c:set var="incomeName"><spring:message code="projectBlockIncome"/> (<spring:message code="units.perUnitperCycle"/>)</c:set>
			<tags:table title="${incomeName}">
				<display:table list="${blockItem.block.incomes}"  id="inc" requestURI="" cellspacing="0" cellpadding="0"
					export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="projectBlockIncome.desc" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
					<display:column titleKey="projectBlockIncome.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
					<display:column titleKey="projectBlockIncome.unitNum" sortable="true" sortProperty="unitNum">
						<tags:formatDecimal value="${inc.unitNum}"/>
					</display:column>
					<c:if test="${project.incomeGen}">
						<display:column titleKey="projectBlockIncome.qtyIntern" sortProperty="qtyIntern" sortable="true">
							<tags:formatDecimal value="${inc.qtyIntern}"/>
						</display:column>
						<display:column titleKey="projectBlockIncome.qtyExtern" sortable="true" sortProperty="extern">
							<tags:formatDecimal value="${inc.extern}"/>
						</display:column>
					</c:if>
					<display:column titleKey="projectBlockIncome.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${inc.unitCost}"/>
					</display:column>
					<c:if test="${project.incomeGen}">
						<display:column titleKey="projectBlockIncome.transport" sortable="true" sortProperty="transport">
							<tags:formatCurrency value="${inc.transport}"/>
						</display:column>
					</c:if>
					<display:column titleKey="projectBlockIncome.total" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${inc.total}"/>
					</display:column>
					<c:if test="${project.incomeGen}">
						<display:column titleKey="projectBlockIncome.totalCash" sortable="true" sortProperty="totalCash">
							<tags:formatCurrency value="${inc.totalCash}"/>
						</display:column>
					</c:if>
				</display:table>
			</tags:table>
		</div>
		
<form:form name="form" method="post" commandName="blockItem">
	
		<tags:errors />
		<div style="display:inline-block;width:450px;">
			<fieldset>
				<legend><span class="header"><spring:message code="projectBlockIncome"/> (${blockItem.block.description})<br/><tags:blockExplanation block="${blockItem.block}"/></span></legend>
				
				<tags:dataentry field="description" labelKey="projectBlockIncome.desc" helpText="projectBlockIncome.desc.help" maxLength="30" inputClass="text" size="20"/>
				<tags:dataentry field="unitType" labelKey="projectBlockIncome.unitType" helpText="projectBlockIncome.unitType.help"  inputClass="text" size="20" maxLength="30" />
				<c:if test="${blockItem.block.cycles}"><c:set var="unitHelp" value="projectBlockIncome.unitNum.help"/></c:if><c:if test="${not blockItem.block.cycles}"><c:set var="unitHelp" value="projectBlockIncome.unitNum.nocycle.help"/></c:if>
				<tags:dataentry field="unitNum" labelKey="projectBlockIncome.unitNum" onmouseout="CalculateTotal()" helpText="${unitHelp}" />
				<tags:dataentry field="qtyIntern" labelKey="projectBlockIncome.qtyIntern" helpText="projectBlockIncome.qtyIntern.help" onmouseout="CalculateTotal()" />
				<tags:datadivider color="green"/>
				<tags:dataentry field="extern" labelKey="projectBlockIncome.qtyExtern" helpText="projectBlockIncome.qtyExtern.help" calculated="true" />
				<tags:dataentry field="unitCost" labelKey="projectBlockIncome.unitCost" onmouseout="CalculateTotal()" helpText="projectBlockIncome.unitCost.help" currency="true"  />
				<tags:dataentry field="transport" labelKey="projectBlockIncome.transport" onmouseout="CalculateTotal()" currency="true" helpText="projectBlockIncome.transport.help" />
				<tags:datadivider color="orange"/>
				<tags:dataentry field="total" labelKey="projectBlockIncome.total" helpText="projectBlockIncome.total.help" calculated="true" currency="true" />
				<tags:dataentry field="totalCash" labelKey="projectBlockIncome.totalCash" helpText="projectBlockIncome.totalCash.help" calculated="true" currency="true" />
			</fieldset>
		</div>
		<div style="display:inline-block;width:45%;">
			<tags:refItemChooser type="1" linked="${blockItem.linkedTo}" notLinked="'unitNum,ownResources,qtyIntern'" descField="description" unitTypeField="unitType" unitCostField="unitCost" transField="transport" calculation="CalculateTotal();" />
		</div>
	<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
</form:form>	
<tags:jscriptCalc fieldA="unitCost" fieldB="transport" fieldC="total" fieldD="unitNum" functionName="CalculateTotal" calc="-" calc2="*" callWhenDone="CalculateExt" />
<tags:jscriptCalc fieldA="unitNum" fieldB="qtyIntern" fieldC="extern" functionName="CalculateExt" calc="-" callWhenDone="CalculateTotalCash" nonCurrency="true"/>
<tags:jscriptCalc fieldA="unitCost" fieldB="transport" fieldC="totalCash" fieldD="extern" functionName="CalculateTotalCash" calc="-" calc2="*" />
</body>
</html>