<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp"%>
<c:set var="menuType" value="config" scope="request" /><c:set var="currentStep" value="2" scope="request" />
<html><head><title><spring:message code="mainMenu.config.users.addEdit" /></title></head>
<body>
	<form:form id="userForm" name="form" method="post" commandName="user">
		<tags:errors />
		<div style="width:500px;">
			<fieldset>
				<legend><spring:message code="user.changePassword"/></legend> 
				<div class="dataentry"><spring:message code="user.username.description"/></div>
				<tags:dataentry field="username" labelKey="user.username" inputClass="text" size="20"/>
				<div class="dataentry"><span class="helpSpacer"></span>
					<label><spring:message code="user.password"/></label>
					<form:password path="password" size="20" maxLength="20" class="text" />
				</div>
				<div class="dataentry"><span class="helpSpacer"></span>
					<label><spring:message code="user.repeatedPassword"/></label>
					<input type="password" size="20" maxLength="20" name="passwordRepeat" class="text" />
				</div>
			</fieldset>
		</div>	
		<tags:submit><spring:message code="user.saveUser"/></tags:submit>
	</form:form>
</body></html>