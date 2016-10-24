<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="profile" value="${profileProductItem.profileProduct.profile}" scope="request"/>
<c:if test="${profile.incomeGen}"><c:set var="title"><spring:message code="profileProductLabour"/></c:set></c:if>
<c:if test="${not profile.incomeGen}"><c:set var="title"><spring:message code="profileProductLabour"/></c:set></c:if>
<html><head><title>${title}</title></head>
<body>
	<div align="right"><a href="#" onClick="toggle('tblInput')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblInput" style="display:none">
		<c:set var="product" value="${profileProductItem.profileProduct}" scope="request"/>
		<c:set var="labourTitle"><spring:message code="profileProductLabour"/> <tags:blockExplanation block="${profileProductItem.profileProduct}"/></c:set>
		<tags:table title="${labourTitle}">
			<display:table name="${profileProductItem.profileProduct.profileLabours}" id="lab" requestURI="" cellpadding="0" cellspacing="0"
				export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="profileProductLabour.desc" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="profileProductLabour.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="profileProductLabour.unitNum" property="unitNum" sortable="true" />
				<display:column titleKey="profileProductLabour.unitCost" sortable="true" sortProperty="unitCost" >
					<tags:formatCurrency value="${lab.unitCost}" />
				</display:column>
				<display:column titleKey="profileProductLabour.total" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${lab.total}" />
				</display:column>	
			</display:table>
		</tags:table>
	</div>		
	
	<form:form name="form" method="post" commandName="profileProductItem">
		<tags:errors/>
		<c:if test="${not profileProductItem.profileProduct.profile.incomeGen}"><c:set var="notIncomeGen" value=".nongen"/></c:if>
		<div style="display:inline-block;width:470px">
			<fieldset>
				<legend><spring:message code="profileProductLabour"/> (${profileProductItem.profileProduct.description})<br/><tags:blockExplanation  block="${profileProductItem.profileProduct}"/></legend>
				<tags:dataentry field="description" labelKey="profileProductLabour.desc" size="20" maxLength="30" helpText="profileProductLabour.desc${notIncomeGen}.help" />
				<tags:dataentry field="unitType" labelKey="profileProductLabour.unitType" helpText="profileProductLabour.unitType.help" />
				<tags:dataentry field="unitNum" labelKey="profileProductLabour.unitNum" onmouseout="Calculate()" helpText="profileProductLabour.unitNum${notIncomeGen}.help" />
				<tags:dataentry field="unitCost" labelKey="profileProductLabour.unitCost" onmouseout="Calculate()" currency="true" helpText="profileProductLabour.unitCost.help" />
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="profileProductLabour.total" helpText="profileProductLabour.total.help" calculated="true" currency="true" />
			</fieldset>
		</div>
		<div style="display:inline-block;">
			<tags:refItemChooser type="2" linked="${profileProductItem.linkedTo}" notLinked="'unitNum'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="Calculate();" />
		</div> 
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
	<tags:jscriptCalc fieldA="unitCost" fieldB="unitNum" fieldC="total" functionName="Calculate" calc="*" />
</body>
</html>