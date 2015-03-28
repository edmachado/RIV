<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step13"/></title>
<script>
$(function() {
	$('#indicators tbody tr:even').addClass("odd");
});
</script>
</head>
<body>
<div style="display:inline-block;width:49%;">
	<h2><spring:message code="profile.report.title"/></h2>
	
	<c:set var="space"><c:if test="${lang!='ar'}">&nbsp;</c:if><c:if test="${lang eq'ar'}">&nbsp;</c:if></c:set>
	<ul>
		<li>
			<a href="../../report/${project.projectId}/projectSummary.pdf" target="_blank" ><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a id="xls_summary" href="../../report/${project.projectId}/projectSummary.xlsx" target="_blank" ><img src="../../img/xls.gif" alt="Excel" border="0">${space}Excel</a> - 
			<spring:message code="project.report.summary"/>
		</li>
		<li>
			<a href="../../report/${project.projectId}/projectDescription.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a id="xls_description" href="../../report/${project.projectId}/projectDescription.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" border="0">${space}Excel</a> - 
			<spring:message code="project.report.general"/>
		</li>
		<li>
			<a href="../../report/${project.projectId}/projectInvestDetail.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a id="xls_invest" href="../../report/${project.projectId}/projectInvestDetail.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" border="0">${space}Excel</a> - 
			<spring:message code="project.report.investDetail"/>
		</li>
		<li>
			<a href="../../report/${project.projectId}/projectGeneralDetail.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a id="xls_general" href="../../report/${project.projectId}/projectGeneralDetail.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
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
				<a id="xls_prod_pattern" href="../../report/${project.projectId}/projectProduction.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.production"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectChronology.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a id="xls_chronology" href="../../report/${project.projectId}/projectChronology.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.chronology"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectBlock.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a id="xls_blocks" href="../../report/${project.projectId}/projectBlocks.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.blockDetail"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectParameters.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a id="xls_parameters" href="../../report/${project.projectId}/projectParameters.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.parameters"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectCashFlowFirst.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a id="xls_cash_flow_first" href="../../report/${project.projectId}/projectCashFlowFirst.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.cashFlowFirst"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectCashFlow.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a id="xls_cash_flow" href="../../report/${project.projectId}/projectCashFlow.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.cashFlow"/>
			</li>
			<li>
				<a href="../../report/${project.projectId}/projectProfitability.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a id="xls_profitability" href="../../report/${project.projectId}/projectProfitability.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
				<spring:message code="project.report.profitability"/>
			</li>
		</c:if>
		<li>
			<a href="../../report/${project.projectId}/projectRecommendation.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a id="xls_recommendation" href="../../report/${project.projectId}/projectRecommendation.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
			<spring:message code="project.report.recommendation"/>
		</li>
		<li>
			<a href="../../report/${project.projectId}/projectComplete.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a id="xls_complete" href="../../report/${project.projectId}/projectComplete.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> - 
			<spring:message code="project.report.complete"/>
		</li>
	</ul>
	<div>
		<h2><spring:message code="project.report.summaryTables"/></h2>
		<ul>
			<li><a href="javascript:showSummary('summaryBlocks', 300, 1000);"><spring:message code="project.report.summary.block"/></a></li>
			<c:if test="${project.incomeGen}">
				<li><a href="javascript:showSummary('summaryWc', 300, 1000);"><spring:message code="project.report.wcCalculation"/></a></li>
				<li><a href="javascript:showSummary('summaryCashFlow', 200, 1000);"><spring:message code="project.report.cashFlow"/></a></li>
				<li><a href="javascript:showSummary('summaryProfitability', 220, 1000);"><spring:message code="project.report.profitability"/></a></li>
			</c:if>
			<c:if test="${not project.incomeGen}">
				<li><a href="javascript:showSummary('summaryContributions', 300, 1000);"><spring:message code="project.report.contributionSummary"/></a></li>
				<li><a href="javascript:showSummary('summaryCashFlow', 200, 1000);"><spring:message code="project.report.cashFlowNongen"/></a></li>
			</c:if>
		</ul>
		</div>
	</div>
	<div style="display:inline-block;width:45%;">
		<h2><spring:message code="mainMenu.config.columns"/></h2>
		<tags:projectIndicators/>
	</div>
	
	<c:set var="onsubmit">
		<c:if test="${project.incomeGen}">javascript:search(false, 'igpj', '');</c:if>
		<c:if test="${not project.incomeGen}">javascript:search(false, 'nigpj', '');</c:if>
	</c:set>
	<tags:submit onSubmit="${onsubmit}"><spring:message code="misc.finish"/></tags:submit>
	

	<c:set var="blockDetailTitle"><c:if test="${project.incomeGen}"><spring:message code="project.report.blockDetail"/></c:if><c:if test="${not project.incomeGen}"><spring:message code="project.report.activityDetail"/></c:if></c:set>
	
	
	<tags:summaryCashFlow />
	<tags:summaryBlocks />
	<c:if test="${project.incomeGen}">
		<tags:summaryWc />
		<tags:summaryProfitability/>
	</c:if>
	<c:if test="${not project.incomeGen }">
		<tags:summaryContributions/>
	</c:if>
	
</body></html>