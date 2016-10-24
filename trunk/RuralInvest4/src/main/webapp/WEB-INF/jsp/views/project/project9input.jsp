<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${blockItem.block.project}" scope="request"/>
<html><head><title><spring:message code="projectBlockInput"/></title></head>
<body>
		<div align="right"><a href="#" onClick="toggle('tblInput')"><spring:message code="misc.toggle"/></a></div>
			<div id="tblInput" style="display:none">
				<c:set var="inputName"><spring:message code="projectBlockInput"/> (<spring:message code="units.perUnitperCycle"/>)</c:set>
				<tags:table title="${inputName}">
					<display:table name="${blockItem.block.inputs}" id="inp" requestURI="" cellspacing="0" cellpadding="0"
						export="false">
						<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
						<display:column titleKey="projectBlockInput.desc" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
						<display:column titleKey="projectBlockInput.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
						<display:column titleKey="projectBlockInput.unitNum" sortProperty="unitNum" sortable="true">
							<tags:formatDecimal value="${inp.unitNum}"></tags:formatDecimal>
						</display:column>
						<display:column titleKey="projectBlockInput.qtyIntern" sortProperty="qtyIntern" sortable="true">
							<tags:formatDecimal value="${inp.qtyIntern}"></tags:formatDecimal>
						</display:column>
						<display:column titleKey="projectBlockInput.qtyExtern" sortable="true" sortProperty="extern">
							<tags:formatDecimal value="${inp.extern}"></tags:formatDecimal>
						</display:column>
						<display:column titleKey="projectBlockInput.unitCost" sortable="true" sortProperty="unitCost">
							<tags:formatCurrency value="${inp.unitCost}"/>
						</display:column>
						<display:column titleKey="projectBlockInput.transport" sortable="true" sortProperty="transport">
							<tags:formatCurrency value="${inp.transport}"/>
						</display:column>
						<display:column titleKey="projectBlockInput.total" sortable="true" sortProperty="total">
							<tags:formatCurrency value="${inp.total}"/>
						</display:column>
						<c:if test="${not project.incomeGen}">
							<display:column titleKey="projectBlockInput.donated" sortable="true" sortProperty="donated">
								<tags:formatCurrency value="${inp.donated}"/>
							</display:column>
						</c:if>
						<display:column titleKey="projectBlockInput.totalCash" sortable="true" sortProperty="totalCash">
							<tags:formatCurrency value="${inp.totalCash}"/>
						</display:column>
					</display:table>
				</tags:table>
			</div>
		
<form:form name="form" method="post" commandName="blockItem">
	
		<tags:errors />
		<div style="display:inline-block;width:450px;">
				<fieldset>
					<legend><span class="header"><spring:message code="projectBlockInput"/> (${blockItem.block.description})<br/><tags:blockExplanation block="${blockItem.block}"/></span></legend>
					
					<tags:dataentry field="description" labelKey="projectBlockInput.desc" helpText="projectBlockInput.desc.help" maxLength="30" inputClass="text" size="20"/>
					<tags:dataentry field="unitType" labelKey="projectBlockInput.unitType" helpText="projectBlockInput.unitType.help"  inputClass="text" size="20" maxLength="30" />
					<c:if test="${blockItem.block.cycles}"><c:set var="unitHelp" value="projectBlockInput.unitNum.help"/></c:if><c:if test="${not blockItem.block.cycles}"><c:set var="unitHelp" value="projectBlockInput.unitNum.nocycle.help"/></c:if>
					<tags:dataentry field="unitNum" labelKey="projectBlockInput.unitNum" onmouseout="CalculateTotal()" helpText="${unitHelp}" />
					<tags:dataentry field="qtyIntern" labelKey="projectBlockInput.qtyIntern" helpText="projectBlockInput.qtyIntern.help" onmouseout="CalculateTotal()" />
					<tags:datadivider color="green"/>
					<tags:dataentry field="extern" labelKey="projectBlockInput.qtyExtern" helpText="projectBlockInput.qtyExtern.help" calculated="true" />
					<tags:dataentry field="unitCost" labelKey="projectBlockInput.unitCost" onmouseout="CalculateTotal()" helpText="projectBlockInput.unitCost.help" currency="true"  />
					<tags:dataentry field="transport" labelKey="projectBlockInput.transport" onmouseout="CalculateTotal()" currency="true" helpText="projectBlockInput.transport.help" />
					<tags:datadivider color="orange"/>
					<tags:dataentry field="total" labelKey="projectBlockInput.total" helpText="projectBlockInput.total.help" calculated="true" currency="true" />
					<c:if test="${not project.incomeGen}">
						<tags:donations labelKey="projectBlockInput.donated" onmouseout="CalculateTotalCash();" helpText="projectBlockInput.donated.help" donors="${project.donors}"></tags:donations>
					</c:if>
					<tags:dataentry field="totalCash" labelKey="projectBlockInput.totalCash" helpText="projectBlockInput.totalCash.help" calculated="true" currency="true" />
				</fieldset>
				</div>
				<div style="display:inline-block;width:45%;">
					<tags:refItemChooser type="0" linked="${blockItem.linkedTo}" notLinked="'unitNum,ownResources,qtyIntern'" descField="description" unitTypeField="unitType" unitCostField="unitCost" transField="transport" calculation="CalculateTotal();" />
				</div>
	<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
</form:form>
<tags:jscriptCalc fieldA="unitCost" fieldB="transport" fieldC="total" fieldD="unitNum" functionName="CalculateTotal" calc="+" calc2="*" callWhenDone="CalculateExt" />
<tags:jscriptCalc fieldA="unitNum" fieldB="qtyIntern" fieldC="extern" functionName="CalculateExt" calc="-" callWhenDone="CalculateTotalCash" nonCurrency="true"/>
<c:if test="${project.incomeGen}">
	<tags:jscriptCalc fieldA="unitCost" fieldB="transport" fieldC="totalCash" fieldD="extern" functionName="CalculateTotalCash" calc="+" calc2="*" />
</c:if>
<c:if test="${not project.incomeGen}">
	<tags:jscriptCalc fieldA="unitCost" fieldB="transport" fieldC="totalCash" fieldD="extern" fieldE="donated" functionName="CalculateTotalCash" calc="+" calc2="*" calc3="-" />
</c:if>
</body>
</html>