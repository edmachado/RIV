<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp"%>
<c:set var="menuType" value="config" scope="request" /><c:set var="currentStep" value="2" scope="request" />
<html><head><title><spring:message code="mainMenu.config.users.addEdit" /></title></head>
<body>
	<form:form id="userForm" name="form" method="post" commandName="user">
		<div style="width:500px">
			<tags:errors/>
			<fieldset>
				<legend><spring:message code="user.addUser"/></legend> 
				<c:if test="${currentUser.userId==user.userId}">
					<div class="dataentry">
					<a id="changeUser" href="${user.userId}?changePassword" class="button">&nbsp;<spring:message code="user.changePassword"/>&nbsp;</a>
					</div>
				</c:if>
				<c:if test="${empty user.userId}">
					<tags:dataentry field="username" helpText="user.username.help" labelKey="user.username" inputClass="text" size="20"/>
					<div class="dataentry">
						<tags:help text="user.password.help" title="user.password"><label><spring:message code="user.password"/></label></tags:help>
						<form:password path="password" maxlength="20" size="20" class="text"/>
					</div>
					<div class="dataentry">
						<tags:help text="user.repeatedPassword.help" title="user.repeatedPassword"><label><spring:message code="user.repeatedPassword"/></label></tags:help>
						<input type="password" maxlength="20" size="20" name="passwordRepeat" class="text"/>
					</div>
				</c:if>
			<tags:dataentry field="description" helpText="user.description.help" labelKey="user.description" inputClass="text" size="20" maxLength="80"/>
			<div class="dataentry">
				<span class="helpSpacer"></span>
				<form:label path="lang"><spring:message code="user.language"/></form:label>
				<form:select path="lang">
					<form:option value="en">English</form:option>
					<form:option value="es">Español</form:option>
					<form:option value="fr">Français</form:option>
					<form:option value="ru">Русский</form:option>
					<form:option value="ru">Монгол</form:option>
					<form:option value="tr">Türkçe</form:option>
					<form:option value="pt">Português</form:option>
					<form:option value="ar">العربية</form:option>
				</form:select>
			</div>
			<tags:dataentry field="organization" helpText="user.organization.help" labelKey="user.organization" inputClass="text" size="20" maxLength="100" />
			<tags:dataentry field="location" helpText="user.location.help" labelKey="user.location" inputClass="text" size="20" maxLength="50" />
			<tags:dataentry field="telephone" helpText="user.telephone.help" labelKey="user.telephone" inputClass="text" size="20" maxLength="50" />
			<tags:dataentry field="email" helpText="user.email.help" labelKey="user.email" inputClass="text" size="50" maxLength="255" />	
		</fieldset> 
		</div>
			<tags:submit><spring:message code="user.saveUser"/></tags:submit>
		</form:form>
</body></html>