<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="config" scope="request"/><c:set var="currentStep" value="1" scope="request"/>
<html><head><title><spring:message code="mainMenu.config"/></title>
</head>
<body>
	<c:set var="accessOK" value="${user.administrator && rivConfig.admin}" scope="request"/>
	<form:form name="form" method="post" commandName="setting" action="" enctype="multipart/form-data">
		<tags:errors />
		<form:hidden path="settingId"/>
		<div style="width:600px;">
			<c:if test="${rivConfig.complete}">
				<a href="export/settings.riv" id="export">
					<img width="16" border="0" height="16" title="<spring:message code="settings.export"/>" alt="<spring:message code="settings.export"/>" src="../img/export_riv.gif">
					<spring:message code="settings.export"/>
				</a><br/>
			</c:if>
			
			<fieldset>
				<legend>i. <spring:message code="settings.1"/></legend>
				<tags:dataentry field="orgName" labelKey="settings.organization" inputClass="text" size="40" maxLength="100" helpText="settings.organization.help" />
				<div class="dataentry">
					<tags:help text="settings.language.help"><form:label path="lang"><spring:message code="settings.language"/></form:label></tags:help>
					<input type="hidden" name="exLang" value="${setting.lang}"/>
					<form:select path="lang">
						<form:option value="en">English</form:option>
						<form:option value="es">Español</form:option>
						<form:option value="fr">Français</form:option>
						<form:option value="sw">Kiswahili</form:option>
						<form:option value="mn">Монгол</form:option>
						<form:option value="pt_BR">Português (Brasil)</form:option>
						<form:option value="pt_PT">Português (Portugal)</form:option>
						<form:option value="ru">Русский</form:option>
						<form:option value="tr">Türkçe</form:option>
						<form:option value="ar">العربية</form:option>
					</form:select>
				</div>
				
				<div class="dataentry">
					<b><spring:message code="settings.logo"/></b><br/>
					<img src="orgLogo" border="0"/><br/>
					<c:if test="${accessOK}">
						<spring:message code="settings.changeLogo"/><br/>
						<input type="file" name="tempLogo" id="fileSelect" accept="image/gif, image/jpeg, image/jpg" />
							gif/jpeg &lt;70 kb
					</c:if>
				</div>
			</fieldset>
			
			<fieldset>
				<legend>ii. <spring:message code="settings.2"/></legend>
				<input type="hidden" name="exDiscountRate" value="${setting.discountRate}"/>
				<tags:dataentry field="discountRate" labelKey="settings.discountRate" calcSignKey="units.inPercent" helpText="settings.discountRate.help" maxLength="10" />
				<tags:dataentry field="maxDuration" labelKey="settings.maxDuration" calcSignKey="units.years" helpText="settings.maxDuration.help" maxLength="5" />
			</fieldset>
			
			<fieldset>
				<legend>iii. <spring:message code="settings.3"/></legend>
				<tags:dataentry field="currencyName" labelKey="settings.currency.name" inputClass="text" size="20" maxLength="50" helpText="settings.currency.name.help" />
				<tags:dataentry field="currencySym" labelKey="settings.currency.symbol" inputClass="text" maxLength="20" helpText="settings.currency.symbol.help" />
				<tags:dataentry field="exchRate" labelKey="settings.exchRate" calcSignKey="units.perUSD" helpText="settings.exchRate.help" />
				<tags:dataentry field="decimalLength" labelKey="settings.currency.decimalLength" inputClass="text" size="5" maxLength="2" helpText="settings.currency.decimalLength.help" />
              	<tags:dataentry field="decimalSeparator" labelKey="settings.currency.decimalSeparator" inputClass="text" size="5" maxLength="1" helpText="settings.currency.decimalSeparator.help" />
              	<tags:dataentry field="thousandSeparator" labelKey="settings.currency.thousandsSeparator" inputClass="text" size="5" maxLength="1" helpText="settings.currency.thousandsSeparator.help" />
            </fieldset>
			
			<fieldset>
				<legend>iv. <spring:message code="settings.4"/></legend>
				<tags:dataentry field="location1" labelKey="settings.location1" inputClass="text" size="20" helpText="settings.location1.help" />
				<tags:dataentry field="location2" labelKey="settings.location2" inputClass="text" size="20" helpText="settings.location2.help" />
				<tags:dataentry field="location3" labelKey="settings.location3" inputClass="text" size="20" helpText="settings.location3.help"  />										
			</fieldset>
			
			<fieldset>
				<legend>
					<tags:help text="settings.5.help" title="settings.5">v. <spring:message code="settings.5"/></tags:help>
				</legend>
				<tags:dataentry field="loan1Max" labelKey="settings.loan.maxDuration" calcSignKey="units.years" helpText="settings.loan.maxDuration.help" />
			  	<tags:dataentry field="loan1GraceCapital" labelKey="settings.loan.graceCapital" calcSignKey="units.years" helpText="settings.loan.graceCapital.help" />
			  	<tags:dataentry field="loan1GraceInterest" labelKey="settings.loan.graceInterest" calcSignKey="units.years" helpText="settings.loan.graceInterest.help" />
			</fieldset>
			
			<fieldset class="fieldset">
				<legend>
					<tags:help text="settings.6.help" title="settings.6">vi. <spring:message code="settings.6"/></tags:help>
				</legend>
				<tags:dataentry field="loan2Max" labelKey="settings.loan.maxDuration" calcSignKey="units.years" helpText="settings.loan.maxDuration.help" />
				<tags:dataentry field="loan2GraceCapital" labelKey="settings.loan.graceCapital" calcSignKey="units.years" helpText="settings.loan.graceCapital.help" />
				<tags:dataentry field="loan2GraceInterest" labelKey="settings.loan.graceInterest" calcSignKey="units.years" helpText="settings.loan.graceInterest.help" />
			</fieldset>
			
			<fieldset class="fieldset">
				<legend>
					<tags:help text="settings.7.help" title="settings.7">vii. <spring:message code="settings.7"/></tags:help>
				</legend>
				<tags:dataentry field="link1Text" labelKey="settings.link1.text" inputClass="text" size="50" maxLength="100" />
			  	<tags:dataentry field="link1Url" labelKey="settings.link1.url" inputClass="text" size="50" maxLength="100" />
				<tags:dataentry field="link2Text" labelKey="settings.link2.text" inputClass="text" size="50" maxLength="100" />
			  	<tags:dataentry field="link2Url" labelKey="settings.link2.url" inputClass="text" size="50" maxLength="100" />
				<tags:dataentry field="link3Text" labelKey="settings.link3.text" inputClass="text" size="50" maxLength="100" />
			  	<tags:dataentry field="link3Url" labelKey="settings.link3.url" inputClass="text" size="50" maxLength="100" />
				<tags:dataentry field="link4Text" labelKey="settings.link4.text" inputClass="text" size="50" maxLength="100" />
			  	<tags:dataentry field="link4Url" labelKey="settings.link4.url" inputClass="text" size="50" maxLength="100" />
			</fieldset>
			
			<fieldset class="fieldset">
				<legend>
					<tags:help text="settings.8.help" title="settings.8">viii. <spring:message code="settings.8"/></tags:help>
				</legend>
				<tags:dataentry field="admin1Title" labelKey="customFields.appConfig1" inputClass="text" size="50" maxLength="100" />
				<tags:dataentry field="admin1Help" labelKey="adminText.help" inputClass="text" size="50" maxLength="500" />
				<tags:dataentryCheckbox field="admin1Enabled" labelKey="misc.enabled" />
				<tags:dataentry field="admin2Title" labelKey="customFields.appConfig2" inputClass="text" size="50" maxLength="100" />
				<tags:dataentry field="admin2Help" labelKey="adminText.help" inputClass="text" size="50" maxLength="500" />
				<tags:dataentryCheckbox field="admin2Enabled" labelKey="misc.enabled" />
			</fieldset>
		
			<fieldset class="fieldset">
				<legend>
					<tags:help text="settings.9.help" title="settings.9">ix. <spring:message code="settings.9"/></tags:help>
				</legend>
				<b><spring:message code="adminText.1"/></b><br/>
				<tags:dataentry field="adminMisc1Title" labelKey="adminText.title" inputClass="text" size="50" maxLength="100" />
				<tags:dataentry field="adminMisc1Help" labelKey="adminText.help" inputClass="text" size="50" maxLength="500" />
				<tags:dataentryCheckbox field="adminMisc1Enabled" labelKey="misc.enabled" />
				<b><spring:message code="adminText.2"/></b><br/>
				<tags:dataentry field="adminMisc2Title" labelKey="adminText.title" inputClass="text" size="50" maxLength="100" />
				<tags:dataentry field="adminMisc2Help" labelKey="adminText.help" inputClass="text" size="50" maxLength="500" />
				<tags:dataentryCheckbox field="adminMisc2Enabled" labelKey="misc.enabled" />
				<b><spring:message code="adminText.3"/></b><br/>
				<tags:dataentry field="adminMisc3Title" labelKey="adminText.title" inputClass="text" size="50" maxLength="100" />
				<tags:dataentry field="adminMisc3Help" labelKey="adminText.help" inputClass="text" size="50" maxLength="500" />
				<tags:dataentryCheckbox field="adminMisc3Enabled" labelKey="misc.enabled" />
		</fieldset>
		</div>
		<tags:submit><spring:message code="settings.saveSettings"/></tags:submit>
	</form:form>
</body>