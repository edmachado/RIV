<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="profile" value="${profileProductItem.profileProduct.profile}" scope="request"/>
<c:if test="${profile.incomeGen}"><c:set var="title"><spring:message code="profileProductIncome"/></c:set></c:if>
<c:if test="${not profile.incomeGen}"><c:set var="title"><spring:message code="profileActivityCharge"/></c:set></c:if>
<c:set var="product" value="${profileProductItem.profileProduct}" scope="request"/>
<html><head><title>${title}</title>
</head><body>
	<c:set var="incomeTitle"><c:if test="${profile.incomeGen}"><spring:message code="profileProductIncome"/></c:if><c:if test="${not profile.incomeGen}"><spring:message code="profileActivityCharge"/></c:if> <tags:blockExplanation block="${product}"/></c:set>
					
	<c:if test="${profile.incomeGen}">
		<div align="right"><a href="#" onClick="toggle('tblIncomes')"><spring:message code="misc.toggle"/></a></div>
		<div id="tblIncomes" style="display:none">
			<tags:table title="${incomeTitle}">
				<display:table list="${profileProductItem.profileProduct.profileIncomes}" id="inc" requestURI="" cellspacing="0" cellpadding="0"
					export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="profileProductIncome.desc" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
					<display:column titleKey="profileProductIncome.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
					<display:column titleKey="profileProductIncome.unitNum" property="unitNum" sortable="true" />
					<display:column titleKey="profileProductIncome.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatDecimal value="${inc.unitCost}" />
					</display:column>
					<display:column titleKey="profileProductIncome.transport" sortable="true" sortProperty="transport">
						<tags:formatDecimal value="${inc.unitCost}" />
					</display:column>
					<display:column titleKey="profileProductIncome.total" sortable="true" sortProperty="total">
						<tags:formatDecimal value="${inc.total}" />
					</display:column>
				</display:table> 
			</tags:table>
		</div>
	</c:if>
	<c:if test="${not profile.incomeGen}">
		<div align="right"><a href="#" onClick="toggle('tblCharges')"><spring:message code="misc.toggle"/></a></div>
		<div id="tblCharges" style="display:none">
			<tags:table title="${incomeTitle}">
				<display:table list="${profileProductItem.profileProduct.profileIncomes}" id="inc" requestURI="" cellspacing="0" cellpadding="0"
					export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="profileActivityCharge.desc" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
					<display:column titleKey="profileActivityCharge.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
					<display:column titleKey="profileActivityCharge.unitNum" property="unitNum" sortable="true"/>
					<display:column titleKey="profileActivityCharge.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${inc.unitCost}" />
					</display:column>
					<display:column titleKey="profileActivityCharge.total" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${inc.total}" />
					</display:column>
					<display:footer>
						<tr height="1"><td height="1" colspan="5" class="Sum1"/></tr>
						<tr><td colspan="4"/><td><tags:formatCurrency value="${profileActivityCharge.profileProduct.incomeTotal}" /></tr>
					</display:footer>
				</display:table> 
			</tags:table>
		</div>
	</c:if>
 
	<form:form name="form" method="post" commandName="profileProductItem">
		<tags:errors/>
		<c:if test="${profile.incomeGen}">
			<div style="display:inline-block;width:470px">
				<fieldset>
					<legend><spring:message code="profileProductIncome"/> (${profileProductItem.profileProduct.description })<br/><tags:blockExplanation  block="${product}"/></legend>	
						<tags:dataentry field="description" labelKey="profileProductIncome.desc" size="20" maxLength="50" inputClass="text" helpText="profileProductIncome.desc.help" />
						<tags:dataentry field="unitType" labelKey="profileProductIncome.unitType" helpText="profileProductIncome.unitType.help" size="20" maxLength="50" inputClass="text" />
						<tags:dataentry field="unitNum" labelKey="profileProductIncome.unitNum" onmouseout="Calculate()" helpText="profileProductIncome.unitNum.help" />
						<tags:dataentry field="unitCost" labelKey="profileProductIncome.unitCost" onmouseout="Calculate()" helpText="profileProductIncome.unitCost.help" currency="true"  />
						<tags:dataentry field="transport" labelKey="profileProductIncome.transport" onmouseout="Calculate()" currency="true" helpText="profileProductIncome.transport.help" />
						<tags:datadivider color="green"/>
						<tags:dataentry field="total" labelKey="profileProductIncome.total" helpText="profileProductIncome.total.help" calculated="true" currency="true" />	
				</fieldset>
			</div>
			<div style="display:inline-block;">
				<tags:refItemChooser type="1" linked="${profileProductItem.linkedTo}" notLinked="'unitNum'" descField="description" unitTypeField="unitType" unitCostField="unitCost" transField="transport" calculation="Calculate();" />
			</div>
		</c:if>
		<c:if test="${not profile.incomeGen}">
			<div style="display:inline-block;width:470px">
				<fieldset>
					<legend><span class="header"><spring:message code="profileActivityCharge"/> (${profileProductItem.profileProduct.description })<br/><tags:blockExplanation  block="${product}"/></span></legend>
					<table>
							<tags:dataentry field="description" labelKey="profileActivityCharge.desc" size="20" maxLength="50" helpText="profileActivityCharge.desc.help" />
							<tags:dataentry field="unitType" labelKey="profileActivityCharge.unitType" helpText="profileActivityCharge.unitType.help" maxLength="50" />
							<tags:dataentry field="unitNum" labelKey="profileActivityCharge.unitNum" onmouseout="Calculate()" helpText="profileActivityCharge.unitNum.help" />
							<tags:dataentry field="unitCost" labelKey="profileActivityCharge.unitCost" onmouseout="Calculate()" helpText="profileActivityCharge.unitCost.help" currency="true"  />
							<tags:datadivider color="green"/>
							<tags:dataentry field="total" labelKey="profileActivityCharge.total" helpText="profileActivityCharge.total.help" calculated="true" currency="true" />
					</table>
				</fieldset>
			</div>
			<div style="display:inline-block;">
				<tags:refItemChooser type="1" linked="${profileProductItem.linkedTo}" notLinked="'unitNum'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="Calculate();" />
			</div>
		</c:if>
		<tags:submit cancel="../step6/${profile.profileId}"><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
<c:if test="${profile.incomeGen}">	
	<tags:jscriptCalc fieldA="unitCost" fieldB="transport" fieldC="total" fieldD="unitNum" functionName="Calculate" calc="-" calc2="*"/>
</c:if>	
<c:if test="${not profile.incomeGen}">
	<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="Calculate" calc="*"/>
</c:if>
</body>
</html>