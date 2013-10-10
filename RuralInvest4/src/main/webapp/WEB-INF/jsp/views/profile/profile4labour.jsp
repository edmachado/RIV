<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="profile" value="${profileItem.profile}" scope="request"/>
<html><head><title>
	<c:if test="${profile.incomeGen}"><spring:message code="profile.incomeGen"/></c:if>
	<c:if test="${!profile.incomeGen}"><spring:message code="profile.nonIncomeGen"/></c:if>
</title></head>
<body>
	<div class="datatitle"><spring:message code="profile.investCosts"/></div>
	<div align="right"><a href="#" onClick="toggle('tblLabour')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblLabour" style="display:none">
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
						<tags:formatCurrency value="${lab.total}"/>
					</display:column>
					<display:column titleKey="profileLabour.ownResource" sortable="true" sortProperty="ownResource">
						<tags:formatCurrency value="${lab.ownResource}"/>
					</display:column>
					<display:column titleKey="profileLabour.externalResources" sortable="true" sortProperty="donated">
						<tags:formatCurrency value="${lab.donated}"/>
					</display:column>
					<display:column title="&nbsp;">
						<c:if test="${not empty lab.linkedTo}"><img src="img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty lab.linkedTo}"><img src="img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
				</display:table>
		</tags:table>
	</div>
	
	<form:form name="form" method="post" commandName="profileItem">
		<tags:errors />
		<div style="display:inline-block;width:500px">
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="profileLabour"/>)</legend>
				<tags:dataentry field="description" labelKey="profileLabour.description"   inputClass="text" size="20" maxLength="50"/>
				<tags:dataentry field="unitType" labelKey="profileLabour.unitType"  helpText="profileLabour.unitType.help"  inputClass="text" size="20" maxLength="50"/>
				<tags:dataentry field="unitNum" labelKey="profileLabour.unitNum"  helpText="profileLabour.unitNum.help" 
					onmouseout="CalculateTotal()" />
				<tags:dataentry field="unitCost" labelKey="profileLabour.unitCost" currency="true"  calcSign="*"
					onmouseout="CalculateTotal()" />
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="profileLabour.totalCost" currency="true" helpText="profileLabour.totalCost.help" calculated="true" /><%--value="${profileLabour.unitNum*profileLabour.unitCost}" --%>
				<tags:dataentry field="ownResource" labelKey="profileLabour.ownResource" currency="true" onmouseout="CalculateExt()" helpText="profileLabour.ownResource.help" calcSign="-" />
				<tags:datadivider color="red"/>
				<tags:dataentry field="donated" labelKey="profileLabour.externalResources" currency="true" helpText="profileLabour.externalResources.help" calculated="true" /><%--value="${(profileLabour.unitNum*profileLabour.unitCost)-profileLabour.ownResource}" --%>					
			</fieldset>
		</div>
		<div style="display:inline-block;">
			<tags:refItemChooser type="2" linked="${profileItem.linkedTo}" notLinked="'unitNum,ownResource'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="CalculateTotal();" />
	 	</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="CalculateTotal" callWhenDone="CalculateExt" calc="*"/>
<tags:jscriptCalc fieldA="total" fieldB="ownResource" fieldC="donated" functionName="CalculateExt" calc="-"/>
</body></html>