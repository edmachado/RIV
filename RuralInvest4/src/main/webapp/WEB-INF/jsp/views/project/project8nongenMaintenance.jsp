<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${projectItem.project}" scope="request"/>
<html><head><title><spring:message code="projectNongenGeneral"/></title>
<script>
$(function() { CalculateDonated(); CalculateTotal(); });
</script>
</head>
<body>
	<div class="datatitle"><spring:message code="projectGeneral"/></div>
	<div align="right"><a onClick="toggle('tblGeneral')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblGeneral" style="display:none">
		<tags:table titleKey="projectNongenGeneral">
			<display:table list="${project.nongenMaintenance}" id="gen" requestURI="" cellspacing="0" cellpadding="0"
				export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectNongenGeneral.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectNongenGeneral.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectNongenGeneral.unitNum" property="unitNum" sortable="true"/>
				<display:column titleKey="projectNongenGeneral.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${gen.unitCost}"/>
				</display:column>
				<display:column titleKey="projectNongenGeneral.total" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${gen.total}"/>
				</display:column>
				<display:column titleKey="projectNongenGeneral.donated" sortable="true" sortProperty="donated">
					<tags:formatCurrency value="${gen.donated}"/>
				</display:column>
				<display:column titleKey="projectNongenGeneral.ownResource" sortable="true" sortProperty="ownResource">
					<tags:formatCurrency value="${gen.ownResource}"/>
				</display:column>
			</display:table>
		</tags:table>
	</div>
	
	<form:form name="form" method="post" commandName="projectItem">
		<tags:errors />
		<div style="display:inline-block;width:470px">
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="projectNongenGeneral"/>)</legend>
				<tags:dataentry field="description" labelKey="projectNongenGeneral.description" helpText="projectNongenGeneral.description.help" inputClass="text" size="20" maxLength="30"/>
				<tags:dataentry field="unitType" labelKey="projectNongenGeneral.unitType" helpText="projectNongenGeneral.unitType.help" inputClass="text" size="20"/>
				<tags:dataentry field="unitNum" labelKey="projectNongenGeneral.unitNum" helpText="projectNongenGeneral.unitNum.help" onmouseout="CalculateTotal()"/>
				<tags:dataentry field="unitCost" labelKey="projectNongenGeneral.unitCost" helpText="projectNongenGeneral.unitCost.help" currency="true" onmouseout="CalculateTotal()"/>
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="projectNongenGeneral.total" helpText="projectNongenGeneral.total.help" currency="true" calculated="true" />
				<tags:donations donors="${projectItem.project.donors}" labelKey="projectNongenGeneral.donated" helpText="projectNongenGeneral.donated.help" onmouseout="CalculateFinance()" />
				<tags:datadivider color="orange"/>
				<tags:dataentry field="ownResource" labelKey="projectNongenGeneral.ownResource" helpText="projectNongenGeneral.ownResource.help" calculated="true" currency="true" />
			</fieldset>
		</div>
		<div style="display:inline-block;">
			<tags:refItemChooser type="0" linked="${projectItem.linkedTo}" notLinked="'unitNum,statePublic,other1'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="CalculateTotal();" />
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="CalculateTotal" calc="*" callWhenDone="CalculateOwn" />
<tags:jscriptCalc fieldA="total" fieldB="donated" fieldC="ownResource" functionName="CalculateOwn" calc="-" />
</body></html>