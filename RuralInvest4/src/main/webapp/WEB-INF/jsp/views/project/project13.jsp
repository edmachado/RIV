<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step13"/></title></head>
<body>
<h2><spring:message code="profile.report.title"/></h2>
	<spring:message code="project.report.description"/><br/>
	<c:set var="space"><c:if test="${lang!='ar'}"> </c:if><c:if test="${lang=='ar'}">Ø§</c:if></c:set>
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
	
	<c:set var="onsubmit">
		<c:if test="${project.incomeGen}">javascript:search(false, 'igpj', '');</c:if>
		<c:if test="${not project.incomeGen}">javascript:search(false, 'nigpj', '');</c:if>
	</c:set>
	<tags:submit onSubmit="${onsubmit}"><spring:message code="misc.finish"/></tags:submit>
	
</body></html>