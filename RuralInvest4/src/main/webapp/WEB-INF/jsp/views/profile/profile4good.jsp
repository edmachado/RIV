<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="profile" value="${profileItem.profile}" scope="request"/>
<html><head><title>
	<c:if test="${profile.incomeGen}"><spring:message code="profile.incomeGen"/></c:if>
	<c:if test="${!profile.incomeGen}"><spring:message code="profile.nonIncomeGen"/></c:if>
</title></head>
<body>
	<div class="datatitle"><spring:message code="profile.investCosts"/></div>
	<div align="right"><a href="#" onClick="toggle('tblGoods')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblGoods" style="display:none">
		<tags:table titleKey="profileGoods">
			<c:if test="${profileItem.getClass().getSimpleName() eq 'ProfileItemGood'}"><c:set var="tableSource" value="${profileItem.profile.glsGoods}"/></c:if>
			<c:if test="${profileItem.getClass().getSimpleName() eq 'ProfileItemGoodWithout'}"><c:set var="tableSource" value="${profileItem.profile.glsGoodsWithout}"/></c:if>
			
			<display:table htmlId="goodsListTable" list="${tableSource}" id="row" requestURI="" cellspacing="0" cellpadding="0"
					 export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="profileGoods.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="profileGoods.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="profileGoods.unitNum" sortProperty="unitNum" sortable="true" style="text-align:center;" headerClass="left">
						<tags:formatDecimal value="${row.unitNum}" />
					</display:column>
					<display:column titleKey="profileGoods.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${row.unitCost}"/>
					</display:column>
					<display:column titleKey="profileGoods.totalCost" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${row.total}"/>
					</display:column>
					<display:column titleKey="profileGoods.ownResource" sortable="true" sortProperty="ownResource">
						<tags:formatCurrency value="${row.ownResource}"/>
					</display:column>
					<display:column titleKey="profileGoods.externalResources" sortable="true" sortProperty="donated">
						<tags:formatCurrency value="${row.donated}"/>
					</display:column>
					<display:column titleKey="profileGoods.econLife" sortProperty="econLife" sortable="true">
						<tags:formatDecimal value="${row.econLife}"/>
					</display:column>
					<display:column titleKey="profileGoods.salvage" sortable="true" sortProperty="salvage">
						<tags:formatCurrency value="${row.salvage}"/>
					</display:column>
					<display:column titleKey="profileGoods.reserve" sortable="true" sortProperty="reserve">
						<tags:formatCurrency value="${row.reserve}"/>
					</display:column>
				</display:table>
		</tags:table>
	</div>
	
	
	<form:form name="form" method="post" commandName="profileItem">
		<tags:errors />
		<div style="display:inline-block;width:470px">
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="profileGoods"/>)</legend>
				<tags:dataentry field="description" labelKey="profileGoods.description" inputClass="text" size="20" maxLength="50" />
				<tags:dataentry field="unitType" labelKey="profileGoods.unitType" helpText="profileGoods.unitType.help" inputClass="text" size="20" maxLength="50" />
				<tags:dataentry field="unitNum" labelKey="profileGoods.unitNum" helpText="profileGoods.unitNum.help" 
					onmouseout="CalculateTotal()" />
				<tags:dataentry field="unitCost" labelKey="profileGoods.unitCost" currency="true" calcSign="*" 
					onmouseout="CalculateTotal()"/>
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="profileGoods.totalCost" currency="true" helpText="profileGoods.totalCost" calculated="true" />
				<tags:dataentry field="ownResource" labelKey="profileGoods.ownResource" currency="true" helpText="profileGoods.ownResource.help" calcSign="-" 
					onmouseout="CalculateExt()"/>
				<tags:datadivider color="red"/>
				<tags:dataentry field="donated" labelKey="profileGoods.externalResources" currency="true" helpText="profileGoods.externalResources.help" calculated="true" />
				<tags:dataentry field="econLife" labelKey="profileGoods.econLife" helpText="profileGoods.econLife.help" calcSignKey="units.years" 
					onmouseout="CalculateReserve()"/>
				<tags:dataentry field="salvage" labelKey="profileGoods.salvage" currency="true" helpText="profileGoods.salvage.help" 
					onmouseout="CalculateReserve()"/>
				<tags:datadivider color="blue"/>
				<tags:dataentry field="reserve" labelKey="profileGoods.reserve" currency="true" helpText="profileGoods.reserve.help" calculated="true"  />
			
			</fieldset>
		</div>
		<div style="display:inline-block;">
			<tags:refItemChooser type="0" linked="${profileItem.linkedTo}" notLinked="'unitNum,donated,ownResource,econLife,salvage'" descField="description" unitTypeField="unitType" unitCostField="unitCost" calculation="CalculateTotal();" />
	 	</div>
		<tags:submit cancel="../step4/${profile.profileId}"><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
	
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="CalculateTotal" calc="*" callWhenDone="CalculateExt" />
<tags:jscriptCalc fieldA="total" fieldB="ownResource" fieldC="donated" functionName="CalculateExt" calc="-" callWhenDone="CalculateReserve" />

<script language="JavaScript">
	function CalculateReserve() {
		with (Math) {
			var number = formatToNum($('#unitNum').val());//document.form.unitNum.value.split(',').join('');		
			var unit = formatToNum($('#unitCost').val());//document.form.unitCost.value.split(',').join('');
			var life = formatToNum($('#econLife').val());//document.form.econLife.value.split(',').join('');
			var salvage = formatToNum($('#salvage').val());document.form.salvage.value.split(',').join('');
			var total = unit * number;
			var reserve = (total - salvage*number) / life;
		}
		if (reserve == 'NaN') reserve=''; else reserve=round(reserve,decLength); 
		$('#reserve').val(numToFormat(reserve));
	}
</script>
</body></html>