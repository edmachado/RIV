<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step12"/></title><tags:calendarJs/></head>
<body>
<form:form name="form" method="post" commandName="project">
	<spring:message code="project.recommendation.seeReports"/> <a href="../step13/${project.projectId}"><b><spring:message code="project.step13"/></b></a>.
	
	<tags:errors />
	<fieldset>
      	<legend><spring:message code="project.recommendation"/></legend>      
		<div> <!-- container -->
			<div style="float:left;">
				<div class="dataentry">
					<form:radiobutton path="reccCode" value="1"/> <spring:message code="project.recommendation.implement"/><br/>
					<form:radiobutton path="reccCode" value="2"/> <spring:message code="project.recommendation.reject"/><br/>
					<form:radiobutton path="reccCode" value="3"/> <spring:message code="project.recommendation.review"/><br/>
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
	
	<h2><spring:message code="project.report.summaryTables"/></h2>
	<ul>
		<li><a id="showIndicators" href="javascript:showSummary('summaryIndicators', 400,600);"><spring:message code="mainMenu.config.columns"/></a></li>
		<c:if test="${project.incomeGen}">
			<li><a href="javascript:showSummary('summaryWc',300,1000);"><spring:message code="project.report.wcCalculation"/></a></li>
		</c:if>
		<c:if test="${not project.incomeGen}">
			<li><a href="javascript:showSummary('summaryContributions', 300, 1000);"><spring:message code="project.report.contributionSummary"/></a></li>
			<li><a href="javascript:showSummary('summaryCashFlow', 200, 1000);"><spring:message code="project.report.cashFlowNongen"/></a></li>
		</c:if>	
		<li><a href="javascript:showSummary('summaryBlocks', 300, 1000);"><spring:message code="project.report.summary.block"/></a></li>			
		
	</ul>
	
	
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step13"/></tags:submit>
</form:form>

<div id="summaryIndicators" class="summary" style="background-color:#f5f5f5" title='<spring:message code="mainMenu.config.columns"/>	 '>
	<tags:projectIndicators/>
</div>
<c:if test="${project.incomeGen}">
	<tags:summaryWc />
	<tags:summaryBlocks />
	<tags:summaryCashFlow />
</c:if>
<c:if test="${not project.incomeGen }">
	<tags:summaryBlocks />
	<tags:summaryContributions/>
	<tags:summaryCashFlow />
</c:if>
</body></html>