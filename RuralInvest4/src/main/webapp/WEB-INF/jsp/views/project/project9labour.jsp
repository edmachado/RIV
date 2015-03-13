<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${blockItem.block.project}" scope="request"/>
<html><head><title><spring:message code="projectBlockLabour"/></title></head>
<body>
		<div align="right"><a href="#" onClick="toggle('tblLabour')"><spring:message code="misc.toggle"/></a></div>
		<div id="tblLabour" style="display:none">
			<c:set var="labourName"><spring:message code="projectBlockLabour"/> (<spring:message code="units.perUnitperCycle"/>)</c:set>
			<tags:table title="${labourName}">
				<display:table name="${blockItem.block.labours}" id="lab" requestURI="" cellpadding="0" cellspacing="0"
					export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="projectBlockLabour.desc" property="description" sortable="true" style="text-align:left;" headerClass="left"/>								
					<display:column titleKey="projectBlockLabour.unitType" sortable="true" style="text-align:left;" headerClass="left">
						<c:if test="${lab.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
						<c:if test="${lab.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
						<c:if test="${lab.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
						<c:if test="${lab.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
					</display:column>								
					<display:column titleKey="projectBlockLabour.unitNum" property="unitNum" sortable="true" />
					<display:column titleKey="projectBlockLabour.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${lab.unitCost}"/>
					</display:column>
					<display:column titleKey="projectBlockLabour.qtyIntern" sortProperty="qtyIntern" sortable="true">
						<tags:formatCurrency value="${lab.qtyIntern}"/>
					</display:column>
					<display:column titleKey="projectBlockLabour.qtyExtern" sortable="true" sortProperty="extern">
						<tags:formatCurrency value="${lab.extern}"/>
					</display:column>
					<display:column titleKey="projectBlockLabour.total" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${lab.total}"/>
					</display:column>
					<c:if test="${not project.incomeGen}">
						<display:column titleKey="projectBlockLabour.donated" sortable="true" sortProperty="donated">
							<tags:formatCurrency value="${lab.donated}"/>
						</display:column>
					</c:if>
					<display:column titleKey="projectBlockLabour.totalCash" sortable="true" sortProperty="totalCash">
						<tags:formatCurrency value="${lab.totalCash}"/>
					</display:column>
				</display:table>
			</tags:table>
		</div>
		
<form:form name="form" method="post" commandName="blockItem">
	
		<tags:errors />
		<div style="display:inline-block;width:450px;">
				<fieldset>
					<legend><span class="header"><spring:message code="projectBlockLabour"/> (${blockItem.block.description})<br/><tags:blockExplanation block="${blockItem.block}"/></span></legend>
					
					<tags:dataentry field="description" labelKey="projectBlockLabour.desc" helpText="projectBlockLabour.desc.help" maxLength="30" inputClass="text" size="20"/>
					<div class="dataentry">
						<tags:help text="projectBlockLabour.unitType.help" title="projectBlockLabour.unitType"><label><spring:message code="projectBlockLabour.unitType"/></label></tags:help>
						<form:select path="unitType">
							<form:option value="0"><spring:message code="units.pyears"/></form:option>
							<form:option value="1"><spring:message code="units.pmonths"/></form:option>
							<form:option value="2"><spring:message code="units.pweeks"/></form:option>
							<form:option value="3"><spring:message code="units.pdays"/></form:option>
						</form:select>
					</div>
					<c:if test="${blockItem.block.cycles}"><c:set var="unitHelp" value="projectBlockLabour.unitNum.help"/></c:if><c:if test="${not blockItem.block.cycles}"><c:set var="unitHelp" value="projectBlockLabour.unitNum.nocycle.help"/></c:if>
					<tags:dataentry field="unitNum" labelKey="projectBlockLabour.unitNum" onmouseout="CalculateTotal()" helpText="${unitHelp}" />
					<tags:dataentry field="qtyIntern" labelKey="projectBlockLabour.qtyIntern" helpText="projectBlockLabour.qtyIntern.help" onmouseout="CalculateTotal()" />
					<tags:datadivider color="green"/>
					<tags:dataentry field="extern" labelKey="projectBlockLabour.qtyExtern" helpText="projectBlockLabour.qtyExtern.help" calculated="true" />
					<tags:dataentry field="unitCost" labelKey="projectBlockLabour.unitCost" onmouseout="CalculateTotal()" helpText="projectBlockLabour.unitCost.help" currency="true"  />
					<tags:datadivider color="orange"/>
					<tags:dataentry field="total" labelKey="projectBlockLabour.total" helpText="projectBlockLabour.total.help" calculated="true" currency="true" />
					<c:if test="${not project.incomeGen}">
						<tags:donations labelKey="projectBlockLabour.donated" onmouseout="CalculateTotal()" helpText="projectBlockLabour.donated.help" donors="${project.donors}"></tags:donations>
					</c:if>
					<tags:dataentry field="totalCash" labelKey="projectBlockLabour.totalCash" helpText="projectBlockLabour.totalCash.help" calculated="true" currency="true" />
				</fieldset>
				</div>
				<div style="display:inline-block;width:45%;">
					<tags:refItemChooser type="2" linked="${blockItem.linkedTo}" notLinked="'unitNum,ownResources,qtyIntern'" descField="description" unitTypeField="unitType" unitCostField="unitCost" transField="transport" calculation="CalculateTotal();" />
				</div>
	<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
</form:form>
<tags:jscriptCalc fieldA="unitCost" fieldB="unitNum" fieldC="total" functionName="CalculateTotal" calc="*" callWhenDone="CalculateExt" />
<tags:jscriptCalc fieldA="unitNum" fieldB="qtyIntern" fieldC="extern" functionName="CalculateExt" calc="-" callWhenDone="CalculateTotalCash"  nonCurrency="true" />
<tags:jscriptCalc fieldA="unitCost" fieldB="extern" fieldC="totalCash" functionName="CalculateTotalCash" calc="*" />
</body>
</html>