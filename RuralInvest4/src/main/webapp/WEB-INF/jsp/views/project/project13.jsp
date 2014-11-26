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
	<div>
		<h2><spring:message code="project.report.summaryTables"/></h2>
		<ul>
<%-- 			<li><a href="javascript:showSummary('summaryIndicators');"><spring:message code="mainMenu.config.columns"/></a></li> --%>
			<c:if test="${project.incomeGen}">
<%-- 				<li><a href="javascript:showSummary('summaryBlocks');"><spring:message code="project.report.blockDetail"/></a></li> --%>
				<li><a href="javascript:showSummary('summaryWc');"><spring:message code="project.report.wcCalculation"/></a></li>
<%-- 				<li><a href="javascript:showSummary('summaryCashFlow');"><spring:message code="project.report.cashFlow"/></a></li> --%>
<%-- 				<li><a href="javascript:showSummary('summaryProfitability');"><spring:message code="project.report.profitability"/></a></li> --%>
			</c:if>
			<c:if test="${not project.incomeGen}">
<%-- 				<li><a href="javascript:showSummary('summaryBlocks');"><spring:message code="project.report.activityDetail"/></a></li> --%>
				<li><a href="javascript:showSummary('summaryContributions');"><spring:message code="project.report.contributionSummary"/></a></li>
			</c:if>
		</ul>
		</div>
	</div>
	<div style="display:inline-block;width:45%;">
		<h2><spring:message code="mainMenu.config.columns"/></h2>
		<tags:table>
			<table id="indicators" cellspacing="0" cellpadding="0">
				<tbody>
					<tr>
						<td><spring:message code="project.projectName"/></td>
						<td class="left">${result.projectName}</td>
					</tr>
					<tr>
						<td><spring:message code="project.userCode"/></td>
						<td class="left">${result.userCode}</td>
					</tr>
					<tr>
						<td><spring:message code="project.technician"/></td>
						<td class="left">${result.technician.description}</td>
					</tr>
					<tr>
						<td><spring:message code="project.fieldOffice"/></td>
						<td class="left"><tags:appConfigDescription ac="${result.fieldOffice}"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.status"/></td>
						<td class="left"><tags:appConfigDescription ac="${result.status }"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.category"/></td>
						<td class="left"><tags:appConfigDescription ac="${result.projCategory }"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.benefType"/></td>
						<td class="left"><tags:appConfigDescription ac="${result.beneficiary }"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.enviroCat"/></td>
						<td class="left"><tags:appConfigDescription ac="${result.enviroCategory}"/></td>
					</tr>
					<c:if test="${rivConfig.setting.admin1Enabled}">
						<tr>
							<td>${rivConfig.setting.admin1Title}</td>
							<td class="left"><tags:appConfigDescription ac="${result.appConfig1}"/></td>
						</tr>
					</c:if>
					<c:if test="${rivConfig.setting.admin2Enabled}">
						<tr>
							<td>${rivConfig.setting.admin2Title}</td>
							<td class="left"><tags:appConfigDescription ac="${result.appConfig2}"/></td>
						</tr>
					</c:if>
					<tr>
						<td><spring:message code="project.investTotal"/></td>
						<td><tags:formatCurrency value="${result.investmentTotal}"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.investOwn"/></td>
						<td><tags:formatCurrency value="${result.investmentDonated}"/></td>
					</tr>
					<tr>
						<td>
							<c:if test="${result.incomeGen}"><spring:message code="project.investCredit"/></c:if>
							<c:if test="${not project.incomeGen}"><spring:message code="project.investCredit.nongen"/></c:if>
						</td>
						<td><tags:formatCurrency value="${result.investmentFinanced}"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.annualEmployment"/></td>
						<td><tags:formatCurrency value="${result.annualEmployment}"/></td>
					</tr>
					
					<c:if test="${result.incomeGen}">
						<tr>
							<td><spring:message code="project.annualIncome"/></td>
							<td><tags:formatCurrency value="${result.annualNetIncome}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.workCapital"/></td>
							<td><tags:formatCurrency value="${result.workingCapital}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.workCapital.own"/></td>
							<td><tags:formatCurrency value="${result.wcOwn}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.workCapital.donated"/></td>
							<td><tags:formatCurrency value="${result.wcDonated}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.workCapital.financed"/></td>
							<td><tags:formatCurrency value="${result.wcFinanced}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.totalCosts"/></td>
							<td><tags:formatCurrency value="${result.totalCosts}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.totalCosts.own"/></td>
							<td><tags:formatCurrency value="${result.totalCostsOwn}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.totalCosts.donated"/></td>
							<td><tags:formatCurrency value="${result.totalCostsDonated}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.totalCosts.financed"/></td>
							<td><tags:formatCurrency value="${result.totalCostsFinanced}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.npv.desc"/></td>
							<td><tags:formatCurrency value="${result.npv}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.irr.desc"/></td>
							<td>
								<c:if test="${result.irr*100 gt 1000 or row.irr*100 lt -1000}"><spring:message code="misc.undefined"/></c:if>
								<c:if test="${result.irr*100 le 1000 and row.irr*100 ge -1000}">
									<tags:formatDecimal value="${result.irr*100}"/>
								</c:if>
							</td>
						</tr>
						<tr>
							<td><spring:message code="project.npvWithDonation.desc"/></td>
							<td><tags:formatCurrency value="${result.npvWithDonation}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.irrWithDonation.desc"/></td>
							<td>
								<c:if test="${row.irrWithDonation*100 gt 1000 or row.irrWithDonation*100 lt -1000}"><spring:message code="misc.undefined"/></c:if>
								<c:if test="${row.irrWithDonation*100 le 1000 and row.irrWithDonation*100 ge -1000}">
									<tags:formatDecimal value="${row.irrWithDonation*100}"/>
								</c:if>
							</td>
						</tr>
					</c:if>
					<c:if test="${not result.incomeGen}">
						<tr>
							<td><spring:message code="project.investPerDirect"/></td>
							<td><tags:formatCurrency value="${row.investPerBenefDirect}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.investPerIndirect"/></td>
							<td><tags:formatCurrency value="${row.investPerBenefIndirect}"/></td>
						</tr>
					</c:if>
					<tr>
						<td><spring:message code="project.benefs.direct"/></td>
						<td>${result.beneDirect}</td>
					</tr>
					<tr>
						<td><spring:message code="project.benefs.indirect"/></td>
						<td>${result.beneIndirect}</td>
					</tr>
					<tr>
						<td><spring:message code=""/></td>
						<td>
					</tr>
				</tbody>
			</table>
		</tags:table>	
	</div>
	
	<c:set var="onsubmit">
		<c:if test="${project.incomeGen}">javascript:search(false, 'igpj', '');</c:if>
		<c:if test="${not project.incomeGen}">javascript:search(false, 'nigpj', '');</c:if>
	</c:set>
	<tags:submit onSubmit="${onsubmit}"><spring:message code="misc.finish"/></tags:submit>
	

	<c:set var="blockDetailTitle"><c:if test="${project.incomeGen}"><spring:message code="project.report.blockDetail"/></c:if><c:if test="${not project.incomeGen}"><spring:message code="project.report.activityDetail"/></c:if></c:set>
	<div id="summaryBlocks" class="summary" title='${blockDetailTitle}'>
	
	</div>
	<c:if test="${project.incomeGen}">
		<div id="summaryWc" class="summary" title='<spring:message code="project.report.wcCalculation"/>'>
			<tags:summaryWc />
		</div>
<%-- 		<div id="summaryCashFlow" class="summary" title='<spring:message code="project.report.cashFlow"/>'> --%>
		
<!-- 		</div> -->
<%-- 		<div id="summaryProfitability" class="summary" title='<spring:message code="project.report.profitability"/>'> --%>
		
<!-- 		</div> -->
	</c:if>
	<c:if test="${not project.incomeGen }">
		<div id="summaryContributions" class="summary" title='<spring:message code="project.report.contributions"/>'>
			<tags:summaryContributions/>
		</div>
	</c:if>
	
</body></html>