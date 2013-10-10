<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step12"/></title><tags:calendarJs/></head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />
	
	<fieldset>
      	<legend><spring:message code="project.recommendation"/></legend>      
		<div> <!-- container -->
			<div style="float:left;">
				<div class="dataentry">
					<form:radiobutton path="reccCode" value="1"/> <spring:message code="project.recommendation.implement"/><br/>
					<form:radiobutton path="reccCode" value="2"/> <spring:message code="project.recommendation.reject"/><br/>
				</div>
			</div>
			<div style="padding-left:300px;">
				<div class="dataentry">
					<spring:message code="project.recommendation.date"/>
					<spring:bind path="project.reccDate">
		    			<input name="reccDate" id="reccDate" value="${status.value}" type="text" size="12"/>
		    		</spring:bind>
				</div>
			</div>
			<br style="clear:both;"/>
		</div>
	</fieldset>
	
	<tags:textbox field="reccDesc" multiline="true" helpTitle="project.recc.justification" helpText="project.recc.justification.help"><spring:message code="project.recc.justification"/></tags:textbox>
	
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step13"/></tags:submit>
</form:form>
</body></html>