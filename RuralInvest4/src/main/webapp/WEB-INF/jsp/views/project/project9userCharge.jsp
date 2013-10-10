<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${blockItem.block.project}" scope="request"/>
<html><head><title><spring:message code="project.step9.nongen"/></title></head>
<body>
		<div align="right"><a href="#" onClick="toggle('tblIncome')"><spring:message code="misc.toggle"/></a></div>
		<div id="tblIncome" style="display:none">
			<c:set var="blockExplanation">(<spring:message code="misc.per.unitType"/> ${projectItem.block.unitType} <spring:message code="misc.per.cycleLength"/> ${blockItem.block.cycleLength} ${lengthUnit})</c:set>
			<c:set var="incomeName"><spring:message code="projectBlockIncome"/> (<spring:message code="units.perUnitperCycle"/>)</c:set>
			<tags:table title="${incomeName}">
				<display:table list="${blockItem.block.incomes}"  id="inc" requestURI="" cellspacing="0" cellpadding="0"
						export="false"> 
						<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty> 
						<display:column titleKey="projectActivityCharge.desc" property="description" sortable="true" style="text-align:${left};" headerClass="left"/> 
						<display:column titleKey="projectActivityCharge.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/> 
						<display:column titleKey="projectActivityCharge.unitNum"  sortProperty="unitNum" sortable="true"> 
							<tags:formatDecimal value="${inc.unitNum}"/> 
						</display:column> 
						<display:column titleKey="projectActivityCharge.unitCost" sortable="true" sortProperty="unitCost"> 
							<tags:formatCurrency value="${inc.unitCost}"/> 
						</display:column> 
						<display:column titleKey="projectActivityCharge.total" sortable="true" sortProperty="total"> 
							<tags:formatCurrency value="${inc.total}"/> <c:set var="incTotal" value="${incTotal+inc.total}"/>
						</display:column> 
						<display:column title="&nbsp;">
						<c:if test="${not empty inc.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty inc.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
				</display:table> 
			</tags:table>
		</div>
		
<form:form name="form" method="post" commandName="blockItem">
	
		<tags:errors />
		<div style="display:inline-block;width:450px;">
			<fieldset>
				<legend><span class="header"><spring:message code="projectActivityCharge"/> (${blockItem.block.description})<br/><tags:blockExplanation block="${blockItem.block}"/></span></legend>
				<tags:dataentry field="description" labelKey="projectActivityCharge.desc" helpText="projectActivityCharge.desc.help" maxLength="30" inputClass="text" size="20"/>
				<tags:dataentry field="unitType" labelKey="projectActivityCharge.unitType" helpText="projectActivityCharge.unitType.help"  inputClass="text" size="20" maxLength="30" />
				<tags:dataentry field="unitNum" labelKey="projectActivityCharge.unitNum" onmouseout="Calculate()" helpText="projectActivityCharge.unitNum.help" />
				<tags:dataentry field="unitCost" labelKey="projectActivityCharge.unitCost" onmouseout="Calculate()" helpText="projectActivityCharge.unitCost.help" currency="true"  />
				<tags:datadivider color="orange"/>
				<tags:dataentry field="total" labelKey="projectActivityCharge.total" helpText="projectActivityCharge.total.help" calculated="true" currency="true" />
			</fieldset>
		</div>
		<div style="display:inline-block;width:45%;">
			<tags:refItemChooser type="1" linked="${blockItem.linkedTo}" notLinked="'unitNum,ownResources,qtyIntern'" descField="description" unitTypeField="unitType" unitCostField="unitCost" transField="transport" calculation="CalculateTotal();" />
		</div>
	<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
</form:form>
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="Calculate" calc="*"  />
</body>
</html>