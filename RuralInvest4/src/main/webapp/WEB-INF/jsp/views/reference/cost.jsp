<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="reference.costs"/></title></head><body>
	<div class="datatitle"><spring:message code="reference.reference"/></div>
		<div align="right"><a href="#" onClick="toggle('tblRefCosts')"><spring:message code="misc.toggle"/></a></div>
		<div id="tblRefCosts" style="display:none">
			<tags:table titleKey="reference.costs">
				<display:table list="${probase.refCosts}" id="row" requestURI="" cellspacing="0" cellpadding="0"
					 export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="reference.cost.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
					<display:column titleKey="reference.cost.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
					<display:column titleKey="reference.cost.unitCost" sortProperty="unitCost" sortable="true">
						<tags:formatDecimal value="${row.unitCost}" />
					</display:column>
					<display:column titleKey="reference.cost.transport" sortProperty="transport" sortable="true">
						<tags:formatDecimal value="${row.transport}" />
					</display:column>
				</display:table>
			</tags:table>
		</div>
		<form:form name="form" method="post" commandName="referenceItem">
			<tags:errors />
			<div style="display:inline-block;width:470px">
				<fieldset>
					<legend><spring:message code="misc.addItem"/> (<spring:message code="reference.costs"/>)</legend>
					<tags:dataentry field="description" labelKey="reference.cost.description" helpText="reference.cost.description.help" inputClass="text" size="20" maxLength="30"/>
					<tags:dataentry field="unitType" labelKey="reference.cost.unitType" helpText="reference.cost.unitType.help" inputClass="text" size="20"/>
					<tags:dataentry field="unitCost" labelKey="reference.cost.unitCost" helpText="reference.cost.unitCost.help" currency="true" />
					<tags:dataentry field="transport" labelKey="reference.cost.transport" helpText="reference.cost.transport.help" currency="true" />
				</fieldset>
			</div>
		<c:set var="cancelUrl">
			<c:choose>
				<c:when test="${not probase.project}">../step7/${probase.proId}</c:when>
				<c:when test="${probase.incomeGen}">../step10/${probase.proId}</c:when>
				<c:otherwise>../step11/${probase.proId}</c:otherwise>
			</c:choose>
		</c:set>
		<tags:submit cancel="${cancelUrl}"><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
</body></html>