<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="profile" value="${profileItem.profile}" scope="request" />
<html><head><title>
	<c:if test="${profile.incomeGen}"><spring:message code="profile.incomeGen"/></c:if>
	<c:if test="${!profile.incomeGen}"><spring:message code="profile.nonIncomeGen"/></c:if>
</title></head>
<body>
	<div class="datatitle">
		<c:if test="${not profile.withWithout}"><spring:message code="profileGeneral"/></c:if>
		<c:if test="${profile.withWithout and without}"><spring:message code="profileGeneral"/> <spring:message code="project.without"/></c:if>
		<c:if test="${profile.withWithout and not without}"><spring:message code="profileGeneral"/> <spring:message code="project.with"/></c:if>
	</div>			
			
			<div align="right"><a href="#" onClick="toggle('tblGeneral')"><spring:message code="misc.toggle"/></a></div>
			<div id="tblGeneral" style="display:none">
				<tags:table titleKey="profileGeneral">
					<c:if test="${(not profile.withWithout) or without}"><c:set var="list" value="${profile.glsGeneral}"/></c:if>
					<c:if test="${profile.withWithout and without}"><c:set var="list" value="${profile.glsGeneralWithout}"/></c:if>
						<display:table htmlId="generalTable" list="${list}" requestURI="" cellspacing="0" cellpadding="0"
							export="false" id="general">
							<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
							<display:column titleKey="profileGeneral.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
							<display:column titleKey="profileGeneral.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
							<display:column titleKey="profileGeneral.unitNum" sortProperty="unitNum" sortable="true">
								<tags:formatDecimal value="${general.unitNum}"/>
							</display:column>
							<display:column titleKey="profileGeneral.unitCost" sortable="true" sortProperty="unitCost">
								<tags:formatCurrency value="${general.unitCost}"/>
							</display:column>
							<display:column titleKey="profileGeneral.totalCost" sortable="true" sortProperty="total" >
								<tags:formatCurrency value="${general.total}"/>
							</display:column>
							<display:column title="&nbsp;">
								<c:if test="${not empty general.linkedTo}"><img src="img/linked.png" width="16" height="16" border="0"></c:if>
								<c:if test="${empty general.linkedTo}"><img src="img/spacer.gif" width="16" height="16" border="0"></c:if>
							</display:column>
							
						</display:table>
				</tags:table>
			</div>
	
	<form:form name="form" method="post" commandName="profileItem">
		<tags:errors />
		<div style="display:inline-block;width:450px">
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="profileGeneral"/>)</legend>
				<tags:dataentry field="description" labelKey="profileGeneral.description"  helpText="profileGeneral.description.help" inputClass="text" size="20" maxLength="50"/>
				<tags:dataentry field="unitType" labelKey="profileGeneral.unitType"  helpText="profileGeneral.unitType.help" inputClass="text" size="20" maxLength="50"/>
				<tags:dataentry field="unitNum" labelKey="profileGeneral.unitNum"  helpText="profileGeneral.unitNum.help" onmouseout="Calculate()"/>
				<tags:dataentry field="unitCost" labelKey="profileGeneral.unitCost" currency="true" calcSign="*" onmouseout="Calculate()"/>
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="profileGeneral.totalCost" currency="true" helpText="profileGeneral.totalCost.help" calculated="true" />
			</fieldset>
		</div>
		<div style="display:inline-block;">
			<tags:refItemChooser type="0" linked="${profileItem.linkedTo}" notLinked="'unitNum'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="Calculate();" />
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="Calculate" calc="*"/>
</body></html>