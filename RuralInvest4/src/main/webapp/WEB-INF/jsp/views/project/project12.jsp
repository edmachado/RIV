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
		<c:if test="${project.incomeGen}">
			<li><a href="javascript:showSummary('summaryWc');"><spring:message code="project.report.wcCalculation"/></a></li>
			<li><a href="javascript:showSummary('summaryCashFlowFirstYear');"><spring:message code="project.report.cashFlowFirst"/></a></li>
			<li><a href="javascript:showSummary('summaryBlocks');"><spring:message code="project.report.summary.block"/></a></li>			
		</c:if>
		<c:if test="${not project.incomeGen}">
			<li><a href="javascript:showSummary('summaryBlocks');"><spring:message code="project.report.summary.block"/></a></li>
			<li><a href="javascript:showSummary('summaryContributions');"><spring:message code="project.report.contributionSummary"/></a></li>
			<li><a href="javascript:showSummary('summaryCashFlow');"><spring:message code="project.report.cashFlowNongen"/></a></li>
		</c:if>
	</ul>
	
	
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step13"/></tags:submit>
</form:form>

<c:if test="${project.incomeGen}">
	<tags:summaryWc />
	<tags:summaryCashFlowFirstYear />
	<tags:summaryCashFlow />
	<tags:summaryBlocks />
</c:if>
<c:if test="${not project.incomeGen }">
	<tags:summaryContributions/>
	<tags:summaryCashFlow />
	<tags:summaryBlocks />
</c:if>
</body></html>