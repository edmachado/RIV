<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="project" value="${projectItem.project}" scope="request"/>
<html><head><title><spring:message code="project.step10.nongen"/></title></head>
<body>
	<div class="datatitle"><spring:message code="projectContribution"/></div>
	<form:form name="form" method="post" commandName="projectItem">
		<tags:errors />
		<div style="display:inline-block;width:470px">
				<c:if test="${project.perYearContributions && projectItem.projItemId eq 0}">
					<div class="dataentry">
						<tags:help text="projectContribution.contribType.help" title="projectContribution.contribType"><label><spring:message code="projectContribution.addAll"/></label></tags:help>
						<input id="allYears" type="checkbox" value="false" name="allYears">	
					</div>
				</c:if>
				
				<tags:dataentry field="description" labelKey="projectContribution.description" helpText="projectContribution.description.help" inputClass="text" size="20" maxLength="30"/>
				<div class="dataentry">
					<tags:help text="projectContribution.contributor.help" title="projectContribution.contributor"><label><spring:message code="projectContribution.contributor"/></label></tags:help>
					<form:select path="donorOrderBy">
						<c:forEach var="donor" items="${project.donors}">
							<c:if test="${not donor.notSpecified}">
								<c:set var="label">
									<c:if test="${donor.description eq 'state-public'}"><spring:message code="project.donor.statePublic"/></c:if>
									<c:if test="${donor.description ne 'state-public'}">${donor.description}</c:if>
								</c:set>
								<form:option value="${donor.orderBy}" label="${label}"/>
							</c:if>
							<c:if test="${donor.notSpecified}"><c:set var="notSpecifiedOrder" value="${donor.orderBy}"/></c:if>
						</c:forEach>
						<c:set var="notSpecifiedLabel"><spring:message code="project.donor.notSpecified"/></c:set>
						<form:option value="${notSpecifiedOrder}" label="${notSpecifiedLabel}"/>
					</form:select>
				</div>
				<tags:dataentry field="unitType" labelKey="projectContribution.unitType" helpText="projectContribution.unitType.help" />
				<tags:dataentry field="unitCost" labelKey="projectContribution.unitCost" helpText="projectContribution.unitCost.help" currency="true" onmouseout="Calculate()"/>
				<c:if test="${not project.perYearContributions}">
					<tags:dataentry field="years[0].unitNum" labelKey="projectContribution.unitNum" helpText="projectContribution.unitNum.help" onmouseout="Calculate()"/>
					<tags:datadivider color="green"/>
					<tags:dataentry field="years[0].total" labelKey="projectContribution.totalCost" helpText="projectContribution.totalCost.help" currency="true" calculated="true" />
				</c:if>
			<input type="hidden" name="linkedToId"/>
		</div>
		
		<c:if test="${project.perYearContributions}">	
			<tags:generalCostPerYear itemCode="projectContribution" />
		</c:if>
		
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
	<c:if test="${not project.perYearContributions}">
		<tags:jscriptCalc fieldA="years0\\\.unitNum" fieldB="unitCost" fieldC="years0\\\.total" functionName="Calculate" calc="*"/>
	</c:if>
</body></html>