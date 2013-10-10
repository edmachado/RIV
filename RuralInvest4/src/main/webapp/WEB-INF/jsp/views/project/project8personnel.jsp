<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${projectItem.project}" scope="request"/>
<html><head><title><spring:message code="project.step8"/></title></head>
<body>
	<div class="datatitle">
		<c:if test="${not project.withWithout}"><spring:message code="projectGeneral"/></c:if>
		<c:if test="${project.withWithout and without}"><spring:message code="projectGeneral.without"/></c:if>
		<c:if test="${project.withWithout and not without}"><spring:message code="projectGeneral.with"/></c:if>
	</div>
	<div align="right"><a href="#" onClick="toggle('tblPersonnel')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblPersonnel" style="display:none">
		<tags:table titleKey="projectGeneralPersonnel">
			<c:if test="${empty without}"><c:set var="pList" scope="request" value="${project.personnels}"/></c:if>
			<c:if test="${without}"><c:set var="pList" scope="request" value="${project.personnelWithouts}"/></c:if>
			<display:table name="pList" id="gen" requestURI="" cellspacing="0" cellpadding="0"
				 export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectGeneralPersonnel.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectGeneralPersonnel.unitType" sortable="true" style="text-align:left;" headerClass="left">
					<c:if test="${gen.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
					<c:if test="${gen.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
					<c:if test="${gen.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
					<c:if test="${gen.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
				</display:column>
				<display:column titleKey="projectGeneralPersonnel.unitNum" sortProperty="unitNum" sortable="true">
						<tags:formatDecimal value="${gen.unitNum}"/>
					</display:column>
				<display:column titleKey="projectGeneralPersonnel.unitCost" sortable="true" sortProperty="unitCost" >
						<tags:formatCurrency value="${gen.unitCost}"/>
					</display:column>
				<display:column titleKey="projectGeneralPersonnel.totalCost" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${gen.total}"/>
				</display:column>
				<display:column titleKey="projectGeneralPersonnel.ownResources" sortable="true" sortProperty="ownResources" >
						<tags:formatCurrency value="${gen.ownResources}"/>
					</display:column>
				<display:column titleKey="projectGeneralPersonnel.external" sortable="true" sortProperty="external">
						<tags:formatCurrency value="${row.unitNum}"/>
					</display:column>
			</display:table>
		</tags:table>	
	</div>
	
	<form:form name="form" method="post" commandName="projectItem">
		<tags:errors />
		<div style="display:inline-block;width:470px">
			<fieldset>
				<legend><span class="header"><spring:message code="misc.addItem"/> (<spring:message code="projectGeneralPersonnel"/>)</span></legend>
									
				<tags:dataentry field="description" labelKey="projectGeneralPersonnel.description" helpText="projectGeneralPersonnel.description.help" inputClass="text" size="20" maxLength="30"/>
				<div class="dataentry">
					<tags:help text="projectGeneralPersonnel.unitType.help" title="projectGeneralPersonnel.unitType"><label><spring:message code="projectGeneralPersonnel.unitType"/></label></tags:help>
					<form:select path="unitType">
						<form:option value="0"><spring:message code="units.pyears"/></form:option>
						<form:option value="1"><spring:message code="units.pmonths"/></form:option>
						<form:option value="2"><spring:message code="units.pweeks"/></form:option>
						<form:option value="3"><spring:message code="units.pdays"/></form:option>
					</form:select>
				</div>
				<tags:dataentry field="unitNum" labelKey="projectGeneralPersonnel.unitNum" helpText="projectGeneralPersonnel.unitNum.help" onmouseout="CalculateTotal()"/>
				<tags:dataentry field="unitCost" labelKey="projectGeneralPersonnel.unitCost" helpText="projectGeneralPersonnel.unitCost.help" currency="true" onmouseout="CalculateTotal()"/>
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="projectGeneralPersonnel.totalCost" helpText="projectGeneralPersonnel.totalCost.help" currency="true" calculated="true"/>
				<tags:dataentry field="ownResources" labelKey="projectGeneralPersonnel.ownResources" helpText="projectGeneralPersonnel.ownResources.help" currency="true" onmouseout="CalculateExt()"/>
				<tags:datadivider color="orange"/>
				<tags:dataentry field="external" labelKey="projectGeneralPersonnel.external" helpText="projectGeneralPersonnel.external.help" currency="true" calculated="true" />
				<!-- value="${(projectGeneralPersonnel.unitCost*projectGeneralPersonnel.unitNum)-projectGeneralPersonnel.ownResources}" -->
			</fieldset>
		</div>
		<div style="display:inline-block;">
			<tags:refItemChooser type="2" linked="${projectItem.linkedTo}" notLinked="'unitNum,ownResources'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="CalculateTotal();" />
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="CalculateTotal" calc="*" callWhenDone="CalculateExt" />
<tags:jscriptCalc fieldA="total" fieldB="ownResources" fieldC="external" functionName="CalculateExt" calc="-" />
</body></html>