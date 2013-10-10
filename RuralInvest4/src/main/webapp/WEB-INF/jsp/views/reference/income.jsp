<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="reference.incomes"/></title></head>
<body>
	<div class="datatitle"><spring:message code="reference.reference"/></div>
	<c:if test="${empty status.errorMessages}">
	<div align="right"><a href="#" onClick="toggle('tblRefIncomes')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblRefIncomes" style="display:none">
		<tags:table titleKey="reference.incomes">
			<display:table list="${probase.refIncomes}" id="row" requestURI="" cellspacing="0" cellpadding="0"
				 export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="reference.income.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="reference.income.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="reference.income.unitCost" sortProperty="unitCost" sortable="true">
					<tags:formatDecimal value="${row.unitCost}" />
				</display:column>
				<display:column titleKey="reference.income.transport" sortProperty="transport" sortable="true" >
					<tags:formatDecimal value="${row.transport}" />
				</display:column>
			</display:table>
		</tags:table>
	</div>
	</c:if>
	<form:form name="form" method="post" commandName="referenceItem">
		<tags:errors />
		<fieldset>
			<legend><spring:message code="misc.addItem"/> (<spring:message code="reference.incomes"/>)</legend>
			<tags:dataentry field="description" labelKey="reference.income.description" helpText="reference.income.description.help" inputClass="text" size="20" maxLength="30"/>
			<tags:dataentry field="unitType" labelKey="reference.income.unitType" helpText="reference.income.unitType.help" inputClass="text" size="20"/>
			<tags:dataentry field="unitCost" labelKey="reference.income.unitCost" helpText="reference.income.unitCost.help" currency="true" />
			<tags:dataentry field="transport" labelKey="reference.income.transport" helpText="reference.income.transport.help" currency="true" />
		</fieldset>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
</body></html>