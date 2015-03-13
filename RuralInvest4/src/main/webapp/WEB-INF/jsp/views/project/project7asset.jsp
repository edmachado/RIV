<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${projectItem.project}" scope="request"/>
<html><head><title><spring:message code="projectInvestAsset"/></title>
<style>
.ui-icon {
  background-image: url(../../styles/riv-theme/images/ui-icons_454545_256x240.png);
}
</style>
</head>
<body>
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
				<tags:dataentry field="donated" button="manage" calculated="true" labelKey="projectInvestAsset.donated" helpText="projectInvestAsset.donated.help" currency="true" onmouseout="CalculateFinance()"/>
			
				<div id="donations" style="display:block; border:1px solid #aaa; margin-left:5px">
					<c:forEach var="donor" items="${projectItem.project.donors}">
						<tags:dataentry field="donations[${donor.donorId}]" label="${donor.description}" currency="true" />
					</c:forEach>
				</div>
				<tags:dataentry field="ownResources" labelKey="projectInvestAsset.ownResources" helpText="projectInvestAsset.ownResources.help" currency="true" onmouseout="CalculateFinance()"/>
				<tags:datadivider color="orange"/>
				<tags:dataentry field="financed" labelKey="projectInvestAsset.financed" helpText="projectInvestAsset.financed.help" currency="true" calculated="true" />
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
	$( "#donation-button" ).click(function() {
		if ($('#donations').is(':visible')) {
			$("#donations").slideUp(100);
		} else {
			$("#donations").slideDown(100 );
		}
		$("#donation-button > span").toggleClass("ui-icon-circle-triangle-e ui-icon-circle-triangle-s");
	});
	
	$("#donations :input:not(:disabled)").mouseleave(function(e) {
		var donatedsum=0.0;
		$("#donations :input:not(:disabled)").each(function(i,n) {
			donatedsum += parseFloat(formatToNum($(n).val()));	
		});
		$('#donated').val(numToFormat(round(parseFloat(donatedsum), decLength)));
		CalculateFinance();
	});
</script>
</body></html>
