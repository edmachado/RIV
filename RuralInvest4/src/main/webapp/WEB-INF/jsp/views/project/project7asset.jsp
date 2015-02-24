<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${projectItem.project}" scope="request"/>
<html><head><title><spring:message code="projectInvestAsset"/></title></head>
<body>
<!-- 	<div class="datatitle"> -->
<%-- 		<c:if test="${not project.withWithout}"><spring:message code="project.invest"/></c:if> --%>
<%-- 		<c:if test="${project.withWithout and without}"><spring:message code="project.invest"/> <spring:message code="project.without"/></c:if> --%>
<%-- 		<c:if test="${project.withWithout and not without}"><spring:message code="project.invest"/> <spring:message code="project.with"/></c:if> --%>
<!-- 	</div> -->
	
	<div align="right"><a href="#" onClick="toggle('tblAssets')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblAssets" style="display:none">
		<tags:table titleKey="projectInvestAsset">
			<c:if test="${projectItem.getClass().getSimpleName() eq 'ProjectItemAsset'}"><c:set var="tableSource" value="${project.assets}"/></c:if>
			<c:if test="${projectItem.getClass().getSimpleName() eq 'ProjectItemAssetWithout'}"><c:set var="tableSource" value="${project.assetsWithout}"/></c:if>
			<display:table list="${tableSource}" id="row" requestURI="" cellspacing="0" cellpadding="0"
				 export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="projectInvestAsset.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectInvestAsset.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectInvestAsset.unitNum" property="unitNum" sortable="true" />
				<display:column titleKey="projectInvestAsset.unitCost" sortable="true" sortProperty="unitCost">
					<tags:formatCurrency value="${row.unitCost}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.totalCost" sortable="true" sortProperty="total" >
					<tags:formatCurrency value="${row.total}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.ownResources" sortable="true" sortProperty="ownResources">
					<tags:formatCurrency value="${row.ownResources}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.donated" sortable="true" sortProperty="donated" >
					<tags:formatCurrency value="${row.donated}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.financed" sortable="true" sortProperty="financed">
					<tags:formatCurrency value="${row.financed}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.econLife" property="econLife" sortable="true" />
				<display:column titleKey="projectInvestAsset.maintCost" sortable="true" sortProperty="maintCost" >
					<tags:formatCurrency value="${row.maintCost}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.salvage" sortProperty="salvage" sortable="true" >
					<tags:formatCurrency value="${row.salvage}"/>
				</display:column>
				<display:column titleKey="projectInvestAsset.replace" sortable="true" style="text-align:center;" headerClass="centered">
					<c:if test="${row.replace}"><spring:message code="misc.yes"/></c:if>
					<c:if test="${not row.replace}"><spring:message code="misc.no"/></c:if>
				</display:column>
				<display:column titleKey="projectInvestAsset.yearBegin" property="yearBegin" style="text-align:center;" headerClass="centered" sortable="true"/>
				<display:footer>
				  	<tr height="1"><td height="1" colspan="15" class="Sum1"/></tr>
					<tr><td/><td/><td/><td/>
					<td><tags:formatCurrency value="${totals.column5}" /></td>
					<td><tags:formatCurrency value="${totals.column6}" /></td>
					<td><tags:formatCurrency value="${totals.column7}" /></td>
					<td><tags:formatCurrency value="${totals.column8}" /></td>
					<td/><td/><td/><td/><td/><td/><td/></tr>
			  	</display:footer>
			</display:table>
		</tags:table>
	</div>
	
	<form:form name="form" method="post" commandName="projectItem">
		<tags:errors />
		<div style="display:inline-block;width:470px">
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="projectInvestAsset"/>)</legend>
				<tags:dataentry field="description" labelKey="projectInvestAsset.description" helpText="projectInvestAsset.description.help" inputClass="text" size="20" maxLength="30"/>
				<tags:dataentry field="unitType" labelKey="projectInvestAsset.unitType" helpText="projectInvestAsset.unitType.help" inputClass="text" size="20"/>
				<tags:dataentry field="unitNum" labelKey="projectInvestAsset.unitNum" helpText="projectInvestAsset.unitNum.help" onmouseout="CalculateTotal()"/>
				<tags:dataentry field="unitCost" labelKey="projectInvestAsset.unitCost" helpText="projectInvestAsset.unitCost.help" currency="true" onmouseout="CalculateTotal()"/>
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="projectInvestAsset.totalCost" helpText="projectInvestAsset.totalCost.help" currency="true" calculated="true" />
				<!-- value="${projectInvestAsset.unitNum*projectInvestAsset.unitCost}" -->
				<tags:dataentry field="donated" labelKey="projectInvestAsset.donated" helpText="projectInvestAsset.donated.help" currency="true" onmouseout="CalculateFinance()"/>
				<tags:dataentry field="ownResources" labelKey="projectInvestAsset.ownResources" helpText="projectInvestAsset.ownResources.help" currency="true" onmouseout="CalculateFinance()"/>
				<tags:datadivider color="orange"/>
				<tags:dataentry field="financed" labelKey="projectInvestAsset.financed" helpText="projectInvestAsset.financed.help" currency="true" calculated="true" />
				<!-- value="${(projectInvestAsset.unitNum*projectInvestAsset.unitCost) - projectInvestAsset.ownResources - projectInvestAsset.donated}" -->
				<tags:dataentry field="econLife" labelKey="projectInvestAsset.econLife" helpText="projectInvestAsset.econLife.help" />
				<tags:dataentry field="maintCost" labelKey="projectInvestAsset.maintCost" helpText="projectInvestAsset.maintCost.help" currency="true" />
				<tags:dataentry field="salvage" labelKey="projectInvestAsset.salvage" helpText="projectInvestAsset.salvage.help" currency="true"/>
				<tags:dataentryCheckbox field="replace" labelKey="projectInvestAsset.replace" helpText="projectInvestAsset.replace.help" helpTitle="projectInvestAsset.replace" />
				<tags:dataentry field="yearBegin" labelKey="projectInvestAsset.yearBegin" helpText="projectInvestAsset.yearBegin.help" />
			</fieldset>
		</div>
		<div style="display:inline-block;">
			<tags:refItemChooser type="0" linked="${projectItem.linkedTo}" notLinked="'unitNum,donated,ownResources,financed,econLife,mainCost,salvage'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="CalculateTotal();" />
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
	
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="CalculateTotal" calc="*" callWhenDone="CalculateFinance" />
<tags:jscriptCalc fieldA="total" fieldB="donated" fieldC="financed" fieldD="ownResources" functionName="CalculateFinance" calc="-" calc2="-" />
<script language="JavaScript">
	function CalculateReserve() {
		with (Math) {
			var number = document.form.unitNum.value.split(',').join('');		
			var unit = document.form.unitCost.value.split(',').join('');
			var life = document.form.econLife.value.split(',').join('');
			var salvage = document.form.salvage.value.split(',').join('');
			var total = unit * number;
			var reserve = (total - salvage*number) / life;
		}
		if (reserve == 'NaN') reserve=''; else reserve=round(reserve,2); document.form.reserve.value=reserve;
	}
</script>
</body></html>