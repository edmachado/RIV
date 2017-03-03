<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${projectItem.project}" scope="request"/>
<html><head><title><spring:message code="projectNongenInput"/></title>
<script>
$(function() { CalculateDonated(); CalculateTotal(); });
</script>
</head>
<body>
	<div class="datatitle"><spring:message code="projectGeneral"/></div>
	<div align="right"><a onClick="toggle('tblInput')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblInput" style="display:none">
		<tags:table titleKey="projectNongenInput">
			<display:table list="${project.nongenMaterials}" id="row" requestURI="" cellspacing="0" cellpadding="0"
				export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectNongenInput.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectNongenInput.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectNongenInput.unitNum" property="unitNum" sortable="true"/>
				<display:column titleKey="projectNongenInput.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${row.unitCost}"/>
				</display:column>
				<display:column titleKey="projectNongenInput.total" sortable="true" sortProperty="total">
					<tags:formatCurrency value="${row.total}"/>
				</display:column>
				<display:column titleKey="projectNongenInput.donated" sortable="true" sortProperty="donated">
					<tags:formatCurrency value="${row.donated}"/>
				</display:column>
				<display:column titleKey="projectNongenInput.ownResource" sortable="true" sortProperty="ownResource">
					<tags:formatCurrency value="${row.ownResource}"/>
				</display:column>
			</display:table>
		</tags:table>
	</div>
	
	<form:form name="form" method="post" commandName="projectItem">
		<tags:errors />
		<div style="display:inline-block;width:470px">
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="projectNongenInput"/>)</legend>
				<tags:dataentry field="description" labelKey="projectNongenInput.description" helpText="projectNongenInput.description.help" inputClass="text" size="20" maxLength="30"/>
				<tags:dataentry field="unitType" labelKey="projectNongenInput.unitType" helpText="projectNongenInput.unitType.help" inputClass="text" size="20"/>
				<tags:dataentry field="unitNum" labelKey="projectNongenInput.unitNum" helpText="projectNongenInput.unitNum.help" onmouseout="CalculateTotal()"/>
				<tags:dataentry field="unitCost" labelKey="projectNongenInput.unitCost" helpText="projectNongenInput.unitCost.help" currency="true" onmouseout="CalculateTotal()"/>
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="projectNongenInput.total" helpText="projectNongenInput.total.help" currency="true" calculated="true" />
				<tags:donations donors="${projectItem.project.donors}" labelKey="projectInvestAsset.donated" helpText="projectInvestAsset.donated.help" onmouseout="CalculateTotal()" />
				<tags:datadivider color="orange"/>
				<tags:dataentry field="ownResource" labelKey="projectNongenInput.ownResource" helpText="projectNongenInput.ownResource.help" calculated="true" currency="true" />
			</fieldset>
		</div>
		<div style="display:inline-block;">
			<tags:refItemChooser type="0" linked="${projectItem.linkedTo}" notLinked="'unitNum,statePublic,other1'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="CalculateTotal();" />
		</div>
		<tags:submit cancel="../step8/${project.projectId}"><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="CalculateTotal" calc="*" callWhenDone="CalculateOwn" />
<tags:jscriptCalc fieldA="total" fieldB="donated" fieldC="ownResource" functionName="CalculateOwn" calc="-"  />
</body></html>