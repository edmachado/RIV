<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${projectItem.project}" scope="request"/>
<html><head><title><spring:message code="projectNongenLabour"/></title></head>
<body>
	<div class="datatitle"><spring:message code="projectGeneral"/></div>
	<div align="right"><a onClick="toggle('tblLabour')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblLabour" style="display:none">
		<tags:table titleKey="projectNongenLabour">
			<display:table list="${project.nongenLabours}" id="lab" requestURI="" cellspacing="0" cellpadding="0"
				export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectNongenLabour.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectNongenLabour.unitType" sortable="true" style="text-align:left;" headerClass="left">
					<c:if test="${lab.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
					<c:if test="${lab.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
					<c:if test="${lab.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
					<c:if test="${lab.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
				</display:column>
				<display:column titleKey="projectNongenLabour.unitNum" property="unitNum" sortable="true" />
				<display:column titleKey="projectNongenLabour.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${lab.unitCost}"/>
				</display:column>
				<display:column titleKey="projectNongenLabour.total" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${lab.total}"/>
				</display:column>
				<display:column titleKey="projectNongenLabour.statePublic" sortable="true" sortProperty="statePublic">
					<tags:formatCurrency value="${lab.statePublic}"/>
				</display:column>
				<display:column titleKey="projectNongenLabour.other1" sortable="true" sortProperty="other1">
					<tags:formatCurrency value="${lab.other1}"/>
				</display:column>
				<display:column titleKey="projectNongenLabour.ownResource" sortable="true" sortProperty="ownResource">
					<tags:formatCurrency value="${lab.ownResource}"/>
				</display:column>
			</display:table>
		</tags:table>
	</div>
	
	<form:form name="form" method="post" commandName="projectItem">
		<tags:errors />
		<div style="display:inline-block;width:470px">
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="projectNongenLabour"/>)</legend>
				<tags:dataentry field="description" labelKey="projectNongenLabour.description" helpText="projectNongenLabour.description.help" inputClass="text" size="20" maxLength="30"/>
				<div class="dataentry">
					<tags:help text="projectNongenLabour.unitType.help" title="projectNongenLabour.unitType"><label><spring:message code="projectNongenLabour.unitType"/></label></tags:help>
					<form:select path="unitType">
						<form:option value="0"><spring:message code="units.pyears"/></form:option>
						<form:option value="1"><spring:message code="units.pmonths"/></form:option>
						<form:option value="2"><spring:message code="units.pweeks"/></form:option>
						<form:option value="3"><spring:message code="units.pdays"/></form:option>
					</form:select>
				</div>
				<tags:dataentry field="unitNum" labelKey="projectNongenLabour.unitNum" helpText="projectNongenLabour.unitNum.help" onmouseout="CalculateTotal()"/>
				<tags:dataentry field="unitCost" labelKey="projectNongenLabour.unitCost" helpText="projectNongenLabour.unitCost.help" currency="true" onmouseout="CalculateTotal()"/>
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="projectNongenLabour.total" helpText="projectNongenLabour.total.help" currency="true" calculated="true" />
				<!--  value="${projectNongenLabour.unitCost*projectNongenLabour.unitNum}" -->
				<tags:dataentry field="statePublic" labelKey="projectNongenLabour.statePublic" helpText="projectNongenLabour.statePublic.help" currency="true"  onmouseout="CalculateOwn()"/>
				<tags:dataentry field="other1" labelKey="projectNongenLabour.other1" helpText="projectNongenLabour.other1.help" currency="true"  onmouseout="CalculateOwn()"/>
				<tags:datadivider color="orange"/>
				<tags:dataentry field="ownResource" labelKey="projectNongenLabour.ownResource" helpText="projectNongenLabour.ownResource.help" calculated="true" currency="true" />
				<!-- value="${(projectNongenLabour.unitCost*projectNongenLabour.unitNum)-projectNongenLabour.statePublic-projectNongenLabour.other1}" -->
			</fieldset>
		</div>
		<div style="display:inline-block;">
				<tags:refItemChooser type="2" linked="${projectItem.linkedTo}" notLinked="'unitNum,statePublic,other1'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="CalculateTotal();" />
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="CalculateTotal" calc="*" callWhenDone="CalculateOwn" />
<tags:jscriptCalc fieldA="total" fieldB="statePublic" fieldC="ownResource" fieldD="other1" functionName="CalculateOwn" calc="-" calc2="-" />
</body></html>