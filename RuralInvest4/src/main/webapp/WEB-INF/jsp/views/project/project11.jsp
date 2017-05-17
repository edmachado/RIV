<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step11"/></title>
<tags:jscriptCalc fieldA="investmentTotal" fieldB="loan2Amt" fieldC="loan1Amt" functionName="Calculate" calc="-"/>
<tags:jscriptCalc fieldA="wcAmountRequired" fieldB="capitalDonate" fieldC="wcAmountFinanced" fieldD="capitalOwn" functionName="CalcFinance" calc="-" calc2="-" />
</head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors /><form:hidden path="investmentTotal"/>
	<div style="display:inline-block;width:480px;margin-right:10px;">
			<fieldset>
				<legend>
					<tags:help title="project.loan1" text="project.loan1.help"><spring:message code="project.loan1" /></tags:help>
				</legend>
					<tags:dataentry field="loan1Amt" labelKey="project.loan.amount" helpText="project.loan.amount.help" calculated="true" currency="true" />
					<tags:dataentry field="loan1Interest" 
						labelKey="project.loan.interest"
						helpText="project.loan.interest.help" inputClass="num" calcSign="%" />
					<tags:dataentry field="loan1Duration" 
						labelKey="project.loan.duration"
						helpText="project.loan.duration.help" calcSignKey="units.years" />
					<tags:dataentry field="loan1GraceCapital" labelKey="project.loan.graceCapital"
						helpText="project.loan.graceCapital.help"
						calcSignKey="units.years" />
					<tags:dataentry field="loan1GraceInterest" labelKey="project.loan.graceInterest"
						helpText="project.loan.graceInterest.help"
						calcSignKey="units.years" />
			</fieldset>
			
			<fieldset>
            	<legend>
            		<tags:help title="project.workingCapital" text="project.workingCapital.help"><spring:message code="project.workingCapital"/></tags:help>
            	</legend>
<!--             	<div class="dataentry"> -->
<%--             		<label><a href="javascript:showSummary('summaryWc',300,1000);"><img height="11" border="0" width="11" vspace="2" src="<%=request.getContextPath()%>/img/help.gif">  --%>
<%--             		<spring:message code="project.workingCapital.how"/></a></label> --%>
<!-- 				</div>	 -->
            	<tags:dataentry field="wcAmountRequired" labelKey="project.amtRequired" helpText="project.amtRequired.help" calculated="true" currency="true"/>
           		<tags:dataentry field="wcAmountFinanced" labelKey="project.amtFinanced" helpText="project.amtFinanced.help"  calculated="true" currency="true" />
           		<tags:dataentry field="wcFinancePeriod" labelKey="project.period" helpText="project.period.help"  calculated="true" calcSignKey="units.months" />
           		<tags:dataentry field="capitalInterest" labelKey="project.capitalInterest" helpText="project.capitalInterest.help" inputClass="num" calcSign="%"/>
       			<tags:dataentry field="capitalDonate" labelKey="project.capitalDonate" helpText="project.capitalDonate.help" currency="true" onmouseout="CalcFinance()"/>
       			<tags:dataentry field="capitalOwn" labelKey="project.capitalOwn" helpText="project.capitalOwn.help" currency="true" onmouseout="CalcFinance()"/>	
           	</fieldset>

		</div>
	<div style="display:inline-block">
		<fieldset>
		 	<legend>
		 		<tags:help title="project.loan2" text="project.loan2.help"><spring:message code="project.loan2"/></tags:help>
          	</legend>
           	<tags:dataentry field="loan2Amt"  labelKey="project.loan.amount" helpText="project.loan.amount.help" currency="true" onmouseout="Calculate()"/>
	   		<tags:dataentry field="loan2Interest" labelKey="project.loan.interest" helpText="project.loan.interest.help" inputClass="num" calcSign="%" />
	   		<tags:dataentry field="loan2Duration" labelKey="project.loan.duration" helpText="project.loan.duration.help" calcSignKey="units.years" />
	   		<tags:dataentry field="loan2GraceCapital" labelKey="project.loan.graceCapital" helpText="project.loan.graceCapital.help" calcSignKey="units.years" />
	   		<tags:dataentry field="loan2GraceInterest" labelKey="project.loan.graceInterest" helpText="project.loan.graceInterest.help" calcSignKey="units.years" />
			<tags:projYear field="loan2InitPeriod" labelKey="project.loan2InitPeriod" helpText="project.loan2InitPeriod.help" />
		</fieldset>
	</div>
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step12"/></tags:submit>
</form:form>
<tags:summaryWc />
</body></html>