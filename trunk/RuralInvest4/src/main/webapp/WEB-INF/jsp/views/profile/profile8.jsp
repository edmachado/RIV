<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="profile.step8"/></title><tags:calendarJs/></head>
<body>
<form:form name="form" method="post" commandName="profile">
	<tags:errors />
	
	<fieldset>
      	<legend><spring:message code="profile.recommendation"/></legend>      
		<div> <!-- container -->
			<div style="float:left;">
				<div class="dataentry">
					<form:radiobutton path="reccCode" value="1"/> <spring:message code="profile.recommendation.implement"/><br/>
					<form:radiobutton path="reccCode" value="2"/> <spring:message code="profile.recommendation.reject"/><br/>
					<form:radiobutton path="reccCode" value="3"/> <spring:message code="profile.recommendation.approve"/>
				</div>
			</div>
			<div style="padding-left:300px;">
				<div class="dataentry">
					<spring:message code="profile.recommendation.date"/>
					<spring:bind path="profile.reccDate">
		    			<input name="reccDate" id="reccDate" value="${status.value}" type="text" size="12"/>
		    		</spring:bind>
				</div>
			</div>
			<br style="clear:both;"/>
		</div>
	</fieldset>
	
	<tags:textbox field="reccDesc" multiline="true" helpTitle="profile.justification" helpText="profile.justification.help"><spring:message code="profile.justification"/></tags:textbox>
	
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="profile.step9"/></tags:submit>
</form:form>
</body></html>