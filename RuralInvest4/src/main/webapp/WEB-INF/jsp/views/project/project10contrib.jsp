<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${projectItem.project}" scope="request"/>
<html><head><title><spring:message code="project.step10.nongen"/></title></head>
<body>
	<div class="datatitle"><spring:message code="projectContribution"/></div>
<%-- 	<div align="right"><a href="#" onClick="toggle('tblContrib')"><spring:message code="misc.toggle"/></a></div> --%>
<!-- 	<div id="tblContrib" style="display:none"> -->
<%-- 		<tags:table titleKey="projectContribution">						 --%>
<%-- 			<display:table list="${project.contributions}" id="contrib" requestURI="" class="data-table" cellspacing="0" cellpadding="0" --%>
<%-- 					export="false"> --%>
<%-- 				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty> --%>
<%-- 				<display:column titleKey="projectContribution.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/> --%>
<%-- 				<display:column titleKey="projectContribution.contribType" sortable="true" style="text-align:left;" headerClass="left"> --%>
<%-- 					<c:if test="${contrib.contribType=='0'}"><spring:message code="projectContribution.contribType.govtCentral"/></c:if> --%>
<%-- 					<c:if test="${contrib.contribType=='1'}"><spring:message code="projectContribution.contribType.govtLocal"/></c:if> --%>
<%-- 					<c:if test="${contrib.contribType=='2'}"><spring:message code="projectContribution.contribType.ngoLocal"/></c:if> --%>
<%-- 					<c:if test="${contrib.contribType=='3'}"><spring:message code="projectContribution.contribType.ngoIntl"/></c:if> --%>
<%-- 					<c:if test="${contrib.contribType=='5'}"><spring:message code="projectContribution.contribType.beneficiary"/></c:if> --%>
<%-- 					<c:if test="${contrib.contribType=='4'}"><spring:message code="projectContribution.contribType.other"/></c:if> --%>
<%-- 				</display:column> --%>
<%-- 				<display:column titleKey="projectContribution.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/> --%>
<%-- 				<display:column titleKey="projectContribution.unitNum" property="unitNum" sortable="true"/> --%>
<%-- 				<display:column titleKey="projectContribution.unitCost" sortable="true" sortProperty="unitCost"> --%>
<%-- 					<tags:formatCurrency value="${contrib.unitCost}"/> --%>
<%-- 				</display:column> --%>
<%-- 				<display:column titleKey="projectContribution.totalCost" sortable="true" sortProperty="total"> --%>
<%-- 					<tags:formatCurrency value="${contrib.total}"/> --%>
<%-- 				</display:column> --%>
<%-- 			</display:table> --%>
<%-- 		</tags:table> --%>
<!-- 	</div> -->
	
	<form:form name="form" method="post" commandName="projectItem">
		<tags:errors />
		<div style="display:inline-block;width:470px">
			<fieldset>
				<legend><spring:message code="misc.addItem"/> (<spring:message code="projectContribution"/> - <spring:message code="units.year"/> ${projectItem.year}) </legend>
				<c:if test="${projectItem.projItemId eq 0}">
					<div class="dataentry">
						<tags:help text="projectContribution.contribType.help" title="projectContribution.contribType"><label><spring:message code="projectContribution.addAll"/></label></tags:help>
						<input id="allYears" type="checkbox" value="false" name="allYears">	
					</div>
				</c:if>
				
				<tags:dataentry field="description" labelKey="projectContribution.description" helpText="projectContribution.description.help" inputClass="text" size="20" maxLength="30"/>
				<div class="dataentry">
					<tags:help text="projectContribution.contribType.help" title="projectContribution.contribType"><label><spring:message code="projectContribution.contribType"/></label></tags:help>
					<form:select path="contribType">
						<form:option value="0"><spring:message code="projectContribution.contribType.govtCentral"/></form:option>
						<form:option value="1"><spring:message code="projectContribution.contribType.govtLocal"/></form:option>
						<form:option value="2"><spring:message code="projectContribution.contribType.ngoLocal"/></form:option>
						<form:option value="3"><spring:message code="projectContribution.contribType.ngoIntl"/></form:option>
						<form:option value="5"><spring:message code="projectContribution.contribType.beneficiary"/></form:option>
						<form:option value="4"><spring:message code="projectContribution.contribType.other"/></form:option>
					</form:select>
				</div>
				<tags:dataentry field="contributor" labelKey="projectContribution.contributor" helpText="projectContribution.contributor.help" />
				<tags:dataentry field="unitType" labelKey="projectContribution.unitType" helpText="projectContribution.unitType.help" />
				<tags:dataentry field="unitNum" labelKey="projectContribution.unitNum" helpText="projectContribution.unitNum.help" onmouseout="Calculate()"/>
				<tags:dataentry field="unitCost" labelKey="projectContribution.unitCost" helpText="projectContribution.unitCost.help" currency="true" onmouseout="Calculate()"/>
				<tags:datadivider color="green"/>
				<tags:dataentry field="total" labelKey="projectContribution.totalCost" helpText="projectContribution.totalCost.help" currency="true" calculated="true" />
				<!-- value="${projectContribution.unitNum*projectContribution.unitCost}" -->
			</fieldset><input type="hidden" name="linkedToId"/>
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
<tags:jscriptCalc fieldA="unitNum" fieldB="unitCost" fieldC="total" functionName="Calculate" calc="*"/>
</body></html>