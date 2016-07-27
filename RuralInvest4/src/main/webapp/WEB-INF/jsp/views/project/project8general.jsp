<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${projectItem.project}" scope="request"/>
<html><head>
	<title><spring:message code="${type}"/></title>
</head>
<body>
	<div class="datatitle">
		<c:if test="${not project.withWithout}"><spring:message code="projectGeneral"/></c:if>
		<c:if test="${project.withWithout and without}"><spring:message code="projectGeneral.without"/></c:if>
		<c:if test="${project.withWithout and not without}"><spring:message code="projectGeneral.with"/></c:if>
	</div>
	
	<form:form name="form" method="post" commandName="projectItem">
		<tags:errors />
		<c:if test="${not project.perYearGeneralCosts}">
			<form:errors path="years" cssClass="error" element="div" />
		</c:if>
		<div style="display:inline-block;width:470px">
			<tags:dataentry field="description" labelKey="${type}.description" helpText="${type}.description.help" inputClass="text" size="20" maxLength="30"/>
			<c:if test="${fn:contains(type,'Suppl') }">
				<tags:dataentry field="unitType" labelKey="${type}.unitType" helpText="${type}.unitType.help" inputClass="text" size="20"/>
			</c:if>
			<c:if test="${not fn:contains(type,'Suppl') }">
				<div class="dataentry">
					<tags:help text="projectGeneralPersonnel.unitType.help" title="projectGeneralPersonnel.unitType"><label><spring:message code="projectGeneralPersonnel.unitType"/></label></tags:help>
					<form:select path="unitType">
						<form:option value="0"><spring:message code="units.pyears"/></form:option>
						<form:option value="1"><spring:message code="units.pmonths"/></form:option>
						<form:option value="2"><spring:message code="units.pweeks"/></form:option>
						<form:option value="3"><spring:message code="units.pdays"/></form:option>
					</form:select>
				</div>
			</c:if>
			<tags:dataentry field="unitCost" labelKey="${type}.unitCost" helpText="${type}.unitCost.help" currency="true" onmouseout="CalculateTotal()"/>
			
			<c:if test="${not project.perYearGeneralCosts}">
				<tags:dataentry field="years[0].unitNum" labelKey="${type}.unitNum" helpText="${type}.unitNum.help" onmouseout="CalculateTotal()"/>
				<tags:datadivider color="green"/>
				<tags:dataentry field="years[0].total" labelKey="${type}.totalCost" helpText="${type}.totalCost.help" currency="true" calculated="true" />
				<tags:dataentry field="years[0].ownResources" labelKey="${type}.ownResources" helpText="${type}.ownResources.help" currency="true" onmouseout="CalculateExt()"/>
				<tags:datadivider color="orange"/>
				<tags:dataentry field="years[0].external" labelKey="${type}.external" helpText="${type}.external.help" currency="true" calculated="true" />
			</c:if>
		</div>
		<c:set var="refType"><c:if test="${fn:contains(type,'Suppl') }">0</c:if><c:if test="${not fn:contains(type,'Suppl') }">2</c:if></c:set>
		<div style="display:inline-block;">
			<tags:refItemChooser type="${refType}" linked="${projectItem.linkedTo}" notLinked="'unitNum,ownResources'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="CalculateTotal();" />
		</div>
	
		<c:if test="${project.perYearGeneralCosts}">	
			<tags:generalCostPerYear itemCode="${type}" />
		</c:if>
		
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
<c:if test="${not project.perYearGeneralCosts}">
	<tags:jscriptCalc fieldA="years0\\\.unitNum" fieldB="unitCost" fieldC="years0\\\.total" functionName="CalculateTotal" calc="*" callWhenDone="CalculateExt" />
	<tags:jscriptCalc fieldA="years0\\\.total" fieldB="years0\\\.ownResources" fieldC="years0\\\.external" functionName="CalculateExt" calc="-" />
</c:if>
</body></html>