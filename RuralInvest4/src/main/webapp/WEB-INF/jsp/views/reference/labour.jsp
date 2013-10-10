<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="reference.labours"/></title></head>
<body>
	<div class="datatitle"><spring:message code="reference.reference"/></div>
	<div align="right"><a href="#" onClick="toggle('tblRefLabours')"><spring:message code="misc.toggle"/></a></div>
	<div id="tblRefLabours" style="display:none">
		<tags:table titleKey="reference.labours">
			<display:table list="${probase.refLabours}" id="row" requestURI="" cellspacing="0" cellpadding="0"
				 export="false">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="reference.labour.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="reference.labour.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="reference.labour.unitCost" sortProperty="unitCost" sortable="true">
					<tags:formatDecimal value="${row.unitCost}" />
				</display:column>
			</display:table>
		</tags:table>
	</div>

	<form:form name="form" method="post" commandName="referenceItem">
		<tags:errors />
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="reference.labours"/>)</legend>
				<tags:dataentry field="description" labelKey="reference.labour.description" helpText="reference.labour.description.help" inputClass="text" size="20" maxLength="30"/>
				<c:if test="${probase.project}">
					<div class="dataentry">
						<tags:help text="reference.labour.unitType.help" title="reference.labour.unitType"><label><spring:message code="reference.labour.unitType"/></label></tags:help>
						<form:select path="unitType">
							<form:option value="0"><spring:message code="units.pyears"/></form:option>
							<form:option value="1"><spring:message code="units.pmonths"/></form:option>
							<form:option value="2"><spring:message code="units.pweeks"/></form:option>
							<form:option value="3"><spring:message code="units.pdays"/></form:option>
						</form:select>
					</div>
				</c:if>
				<c:if test="${!probase.project}">
					<tags:dataentry field="unitType" labelKey="reference.labour.unitType" helpText="reference.labour.unitType.help" inputClass="text" size="20" maxLength="30"/>
				</c:if>
				<tags:dataentry field="unitCost" labelKey="reference.labour.unitCost" helpText="reference.labour.unitCost.help" currency="true" />
			</fieldset>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
</body></html>