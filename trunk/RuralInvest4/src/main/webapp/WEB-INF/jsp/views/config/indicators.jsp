<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><c:set var="accessOK" value="true" scope="request"/>
<c:set var="menuType" value="config" scope="request" /><c:set var="currentStep" value="8" scope="request" />
<html><head><title><spring:message code="mainMenu.config.columns"/></title></head>
<body>
	<div class="datatitle"><spring:message code="mainMenu.config"/></div>
	<form:form name="form" id="indicators" method="post" commandName="user" >
		<div style="width:500px;">
			<tags:errors />
			<fieldset>
				<legend>i. <spring:message code="mainMenu.config.columns"/></legend>
				<div class="dataentry"><spring:message code="settings.projectResults"/></div>
				<tags:dataentryCheckbox field="resultTechnician" labelKey="project.technician" />
				<tags:dataentryCheckbox field="resultFieldOffice" labelKey="project.fieldOffice" />
				<tags:dataentryCheckbox field="resultStatus" labelKey="project.status" />
				<tags:dataentryCheckbox field="resultProjectCategory" labelKey="project.category" />
				<tags:dataentryCheckbox field="resultBenefCategory" labelKey="project.benefType" />
				<tags:dataentryCheckbox field="resultEnviron" labelKey="project.enviroCat" />
				<c:if test="${setting.admin1Enabled}">
					<tags:dataentryCheckbox field="resultAdminCategory1" label="${setting.admin1Title}" />
				</c:if>
				<c:if test="${setting.admin2Enabled}">
					<tags:dataentryCheckbox field="resultAdminCategory2" label="${setting.admin2Title}" />
				</c:if>
				<tags:dataentryCheckbox field="resultInvestTotal" labelKey="project.investTotal" />
				<tags:dataentryCheckbox field="resultInvestOwn" labelKey="project.investOwn" />
				<tags:dataentryCheckbox field="resultInvestDonated" labelKey="project.investExt" />
				<tags:dataentryCheckbox field="resultInvestFinanced" labelKey="project.investCredit" />
				<tags:dataentryCheckbox field="resultWorkingCapital" labelKey="project.results.workCapital" />
				<tags:dataentryCheckbox field="resultWcOwn" labelKey="project.results.workCapital.own" />
				<tags:dataentryCheckbox field="resultWcDonated" labelKey="project.results.workCapital.donated" />
				<tags:dataentryCheckbox field="resultWcFinanced" labelKey="project.results.workCapital.financed" />
				<tags:dataentryCheckbox field="resultTotalCosts" labelKey="project.results.totalCosts" />
				<tags:dataentryCheckbox field="resultTotalCostsOwn" labelKey="project.results.totalCosts.own" />
				<tags:dataentryCheckbox field="resultTotalCostsDonated" labelKey="project.results.totalCosts.donated" />
				<tags:dataentryCheckbox field="resultTotalCostsFinanced" labelKey="project.results.totalCosts.financed" />
				<tags:dataentryCheckbox field="resultNpv" labelKey="project.npv.desc" />
				<tags:dataentryCheckbox field="resultIrr" labelKey="project.irr.desc" />
				<tags:dataentryCheckbox field="resultNpvWith" labelKey="project.npvWithDonation.desc" />
				<tags:dataentryCheckbox field="resultIrrWith" labelKey="project.irrWithDonation.desc" />
				<tags:dataentryCheckbox field="resultBenefDirect" labelKey="project.benefs.direct" />
				<tags:dataentryCheckbox field="resultBenefIndirect" labelKey="project.benefs.indirect" />
				<tags:dataentryCheckbox field="resultInvestDirect" labelKey="project.investPerDirect" />
				<tags:dataentryCheckbox field="resultInvestIndirect" labelKey="project.investPerIndirect" />
				<tags:dataentryCheckbox field="resultAnnualEmploy" labelKey="project.annualEmployment" />
				<tags:dataentryCheckbox field="resultAnnualNetIncome" labelKey="project.annualIncome" />
			</fieldset>
			</div>
		<tags:submit><spring:message code="settings.saveSettings"/></tags:submit>
	</form:form>
</body></html>