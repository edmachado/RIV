<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step13"/></title>
<style>
	#indicators dt { 
		display:inline-block;
		width:110px;
		text-align:right;
		font-weight:bold;
		color:green;
	}
	#indicators dt:after {
		content: ":";
	}
	#indicators dd { 
		display:inline-block;
		margin:0 0 0 110px; 
		padding: 0 0 0.5em 0; 
	}
</style>

</head>
<body>
<div style="display:inline-block;width:49%;">
<h2><spring:message code="profile.report.title"/></h2>
	
	<c:set var="space"><c:if test="${lang!='ar'}"> </c:if><c:if test="${lang=='ar'}">&nbsp;</c:if></c:set>
	<ul>
		<li>
			<a href="../../report/${project.projectId}/projectSummary.pdf" target="_blank" ><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a href="../../report/${project.projectId}/projectSummary.xlsx" target="_blank" ><img src="../../img/xls.gif" alt="Excel" border="0">${space}Excel</a> - 
			<spring:message code="project.report.summary"/>
		</li>
		<li>
			<a href="../../report/${project.projectId}/projectDescription.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a href="../../report/${project.projectId}/projectDescription.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" border="0">${space}Excel</a> - 
			<spring:message code="project.report.general"/>
		</li>
		<li>
			<a href="../../report/${project.projectId}/projectInvestDetail.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a href="../../report/${project.projectId}/projectInvestDetail.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" border="0">${space}Excel</a> - 
			<spring:message code="project.report.investDetail"/>
		</li>
		<li>
			<a href="../../report/${project.projectId}/projectGeneralDetail.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a href="../../report/${project.projectId}/projectGeneralDetail.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
			<spring:message code="project.report.generalCostsDetail"/>
		</li>
		<c:if test="${not project.incomeGen}">
			<li>
				<a href="../../report/${project.projectId}/projectProduction.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${project.projectId}/projectProduction.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.production.nonGen"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectBlock.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${project.projectId}/projectBlocks.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.activityDetail"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectContributions.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${project.projectId}/projectContributions.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.contributions"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectCashFlowNongen.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${project.projectId}/projectCashFlowNongen.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.cashFlowNongen"/>
			</li>
		</c:if>				
		<c:if test="${project.incomeGen}">
			<li>
				<a href="../../report/${project.projectId}/projectProduction.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${project.projectId}/projectProduction.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.production"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectChronology.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${project.projectId}/projectChronology.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.chronology"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectBlock.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${project.projectId}/projectBlocks.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.blockDetail"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectParameters.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${project.projectId}/projectParameters.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.parameters"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectCashFlowFirst.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${project.projectId}/projectCashFlowFirst.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.cashFlowFirst"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectCashFlow.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${project.projectId}/projectCashFlow.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.cashFlow"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectProfitability.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${project.projectId}/projectProfitability.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.profitability"/>
			</li>
		</c:if>
		<li>
			<a href="../../report/${project.projectId}/projectRecommendation.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a href="../../report/${project.projectId}/projectRecommendation.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
			<spring:message code="project.report.recommendation"/>
		</li>
		<li>
			<a href="../../report/${project.projectId}/projectComplete.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a href="../../report/${project.projectId}/projectComplete.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
			<spring:message code="project.report.complete"/>
		</li>
	</ul>
	</div>
	<div style="display:inline-block;width:45%;">
		<h2>Summary tables</h2>
		<ul>
			<li><a href="javascript:showSummary('summaryIndicators');"><spring:message code="mainMenu.config.columns"/></a></li>
			<c:if test="${project.incomeGen}">
				<li><a href="javascript:showSummary('summaryBlocks');"><spring:message code="project.report.blockDetail"/></a></li>
				<li><a href="javascript:showSummary('summaryWc');"><spring:message code="project.report.wcCalculation"/></a></li>
				<li><a href="javascript:showSummary('summaryCashFlow');"><spring:message code="project.report.cashFlow"/></a></li>
				<li><a href="javascript:showSummary('summaryProfitability');"><spring:message code="project.report.profitability"/></a></li>
			</c:if>
			<c:if test="${not project.incomeGen}">
				<li><a href="javascript:showSummary('summaryBlocks');"><spring:message code="project.report.activityDetail"/></a></li>
				<li><a href="javascript:showSummary('summaryContributions');"><spring:message code="project.report.contributionSummary"/></a></li>
			</c:if>
		</ul>
	</div>
	
	<c:set var="onsubmit">
		<c:if test="${project.incomeGen}">javascript:search(false, 'igpj', '');</c:if>
		<c:if test="${not project.incomeGen}">javascript:search(false, 'nigpj', '');</c:if>
	</c:set>
	<tags:submit onSubmit="${onsubmit}"><spring:message code="misc.finish"/></tags:submit>
	
	<div id="summaryIndicators" class="summary" title='<spring:message code="mainMenu.config.columns"/>'>
		<dl id="indicators">
			<dt><spring:message code="project.projectName"/></dt><dd>${project.projectName} </dd>
			<dt><spring:message code="project.userCode"/></dt><dd>${project.userCode} </dd>
			<dt><spring:message code="project.technician"/></dt><dd>${project.technician.description} </dd>
			<dt><spring:message code="project.fieldOffice"/></dt><dd> </dd>
			<dt><spring:message code="project.status"/></dt><dd></dd>
			<dt><spring:message code="project.category"/></dt><dd></dd>
			<dt><spring:message code="project.benefType"/></dt><dd></dd>
			<dt><spring:message code="project.enviroCat"/></dt><dd></dd>
<%-- 			<dt><spring:message code=""/></dt><dd>${project.}</dd> --%>
		</dl>
	</div>
	<c:set var="blockDetailTitle"><c:if test="${project.incomeGen}"><spring:message code="project.report.blockDetail"/></c:if><c:if test="${not project.incomeGen}"><spring:message code="project.report.activityDetail"/></c:if></c:set>
	<div id="summaryBlocks" class="summary" title='${blockDetailTitle}'>
	
	</div>
	<c:if test="${project.incomeGen}">
		<div id="summaryWc" class="summary" title='<spring:message code="project.report.wcCalculation"/>'>
			<tags:summaryWc />
		</div>
		<div id="summaryCashFlow" class="summary" title='<spring:message code="project.report.cashFlow"/>'>
		
		</div>
		<div id="summaryProfitability" class="summary" title='<spring:message code="project.report.profitability"/>'>
		
		</div>
	</c:if>
	<c:if test="${not project.incomeGen }">
		<div id="summaryContributions" class="summary" title='<spring:message code="project.report.contribution"/>'>
			<tags:summaryContributions/>
		</div>
	</c:if>
	
</body></html>