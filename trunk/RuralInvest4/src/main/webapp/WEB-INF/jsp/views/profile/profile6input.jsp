<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="profile" value="${profileProductItem.profileProduct.profile}" scope="request"/>
<c:if test="${profile.incomeGen}"><c:set var="title"><spring:message code="profileProductInput"/></c:set></c:if>
<c:if test="${not profile.incomeGen}"><c:set var="title"><spring:message code="profileProductInput"/></c:set></c:if>
<html><head><title>${title}</title></head>
<body>
	
	<div align="right"><a href="#" onClick="toggle('tblInput')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblInput" style="display:none">
		<c:set var="inputTitle"><spring:message code="profileProductInput"/> <tags:blockExplanation block="${profileProductItem.profileProduct}"/></c:set>
		<tags:table title="${inputTitle}">
			<display:table name="${profileProductItem.profileProduct.profileInputs}" id="inp" requestURI="" class="data-table" cellspacing="0" cellpadding="0"
				export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="profileProductInput.desc" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="profileProductInput.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="profileProductInput.unitNum" property="unitNum" sortable="true" />
				<display:column titleKey="profileProductInput.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${inp.unitCost}" />
				</display:column>
				<display:column titleKey="profileProductInput.transport" sortable="true" sortProperty="transport">
					<tags:formatCurrency value="${inp.transport}" />
				</display:column>
						 <display:column titleKey="profileProductInput.total" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${inp.total}" />
					</display:column>				
			</display:table>
		</tags:table>
	</div>
		
	<form:form name="form" method="post" commandName="profileProductItem">
		<tags:errors/>
		<c:if test="${not profileProductItem.profileProduct.profile.incomeGen}"><c:set var="notIncomeGen" value=".nongen"/></c:if>
		<div style="display:inline-block;width:470px">
			<fieldset>
				<legend><spring:message code="profileProductInput"/> (${profileProductItem.profileProduct.description})<br/> <tags:blockExplanation block="${profileProductItem.profileProduct}"/></legend>
				<tags:dataentry field="description" labelKey="profileProductInput.desc" size="20" maxLength="50" helpText="profileProductInput.desc${notIncomeGen}.help" />
				<tags:dataentry field="unitType" labelKey="profileProductInput.unitType" helpText="profileProductInput.unitType.help" maxLength="50"/>
				<tags:dataentry field="unitNum" labelKey="profileProductInput.unitNum" onmouseout="Calculate()" helpText="profileProductInput.unitNum${notIncomeGen}.help" />
				<tags:dataentry field="unitCost" labelKey="profileProductInput.unitCost" onmouseout="Calculate()" currency="true" helpText="profileProductInput.unitCost.help" />
				<tags:dataentry field="transport" labelKey="profileProductInput.transport" onmouseout="Calculate()" currency="true" helpText="profileProductInput.transport.help" />
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="profileProductInput.total" calculated="true" currency="true" helpText="profileProductInput.total.help" />
			</fieldset>
		</div>
		<div style="display:inline-block;">
			<tags:refItemChooser type="0" linked="${profileProductItem.linkedTo}" notLinked="'unitNum'" descField="description" unitTypeField="unitType" unitCostField="unitCost" transField="transport" calculation="Calculate();" />
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
	<tags:jscriptCalc fieldA="unitCost" fieldB="transport" fieldC="total" fieldD="unitNum" functionName="Calculate" calc="+" calc2="*"/>
</body>
</html>