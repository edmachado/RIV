<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${projectItem.project}" scope="request"/>
<html><head><title><spring:message code="project.step8"/></title></head>
<body>
	<div class="datatitle">
		<c:if test="${not project.withWithout}"><spring:message code="projectGeneral"/></c:if>
		<c:if test="${project.withWithout and without}"><spring:message code="projectGeneral.without"/></c:if>
		<c:if test="${project.withWithout and not without}"><spring:message code="projectGeneral.with"/></c:if>
	</div>
	<div align="right"><a href="#" onClick="toggle('tblSupplies')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblSupplies" style="display:none">
		<tags:table titleKey="projectGeneralSupplies">
			<c:if test="${empty without}"><c:set var="generalsList" scope="request" value="${project.generals}"/></c:if>
			<c:if test="${without}"><c:set var="generalsList" scope="request" value="${project.generalWithouts}"/></c:if>
			<display:table name="generalsList" id="row" requestURI="" cellspacing="0" cellpadding="0" export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectGeneralSupplies.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectGeneralSupplies.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectGeneralSupplies.unitNum" sortProperty="unitNum" sortable="true">
						<tags:formatDecimal value="${row.unitNum}"/>
					</display:column>
				<display:column titleKey="projectGeneralSupplies.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${row.unitCost}"/>
				</display:column>
				<display:column titleKey="projectGeneralSupplies.totalCost" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${row.total}"/>
				</display:column>
				<display:column titleKey="projectGeneralSupplies.ownResources" sortable="true" sortProperty="ownResources" >
					<tags:formatCurrency value="${row.unitCost}"/>
				</display:column>
				<display:column titleKey="projectGeneralSupplies.external" sortable="true" sortProperty="external">
					<tags:formatCurrency value="${row.external}"/>
				</display:column>
			</display:table>
		</tags:table>
	</div>
	
	<form:form name="form" method="post" commandName="projectItem">
		<tags:errors />
		<div style="display:inline-block;width:470px">
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="projectGeneralSupplies"/>)</legend>
				<tags:dataentry field="description" labelKey="projectGeneralSupplies.description" helpText="projectGeneralSupplies.description.help" inputClass="text" size="20" maxLength="30"/>
				<tags:dataentry field="unitType" labelKey="projectGeneralSupplies.unitType" helpText="projectGeneralSupplies.unitType.help" inputClass="text" size="20"/>
				<tags:dataentry field="unitNum" labelKey="projectGeneralSupplies.unitNum" helpText="projectGeneralSupplies.unitNum.help" onmouseout="CalculateTotal()"/>
				<tags:dataentry field="unitCost" labelKey="projectGeneralSupplies.unitCost" helpText="projectGeneralSupplies.unitCost.help" currency="true" onmouseout="CalculateTotal()"/>
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="projectGeneralSupplies.totalCost" helpText="projectGeneralSupplies.totalCost.help" currency="true" calculated="true" />
				<!-- value="${projectGeneralSupplies.unitCost*projectGeneralSupplies.unitNum}" -->
				<tags:dataentry field="ownResources" labelKey="projectGeneralSupplies.ownResources" helpText="projectGeneralSupplies.ownResources.help" currency="true" onmouseout="CalculateExt()"/>
				<tags:datadivider color="orange"/>
				<tags:dataentry field="external" labelKey="projectGeneralSupplies.external" helpText="projectGeneralSupplies.external.help" currency="true" calculated="true" />
				<!-- value="${(projectGeneralSupplies.unitCost*projectGeneralSupplies.unitNum)-projectGeneralSupplies.ownResources}" -->
			</fieldset>
		</div>
		<div style="display:inline-block;">
			<tags:refItemChooser type="0" linked="${projectItem.linkedTo}" notLinked="'unitNum,ownResources'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="CalculateTotal();" />
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="CalculateTotal" calc="*" callWhenDone="CalculateExt" />
<tags:jscriptCalc fieldA="total" fieldB="ownResources" fieldC="external" functionName="CalculateExt" calc="-" />
</body></html>