<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="profile.step9"/></title>
<script>
$(function() {
	$('#indicators tbody tr:even').addClass("odd");
});
</script>
</head>
<body>
	<div style="display:inline-block;width:49%;">
		<h2><spring:message code="profile.report.title"/></h2>
		<ul>
			<li>
				<a href="../../report/${profile.profileId}/profileSummary.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${profile.profileId}/profileSummary.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel"  border="0"> Excel</a> - 
				<spring:message code="profile.report.summary"/> 
			</li>
			<li>
				<a href="../../report/${profile.profileId}/profileInvest.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${profile.profileId}/profileInvest.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel"  border="0"> Excel</a> - 
				<spring:message code="profile.report.investDetail"/>
			</li>
			<li>
				<a href="../../report/${profile.profileId}/profileGeneral.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${profile.profileId}/profileGeneral.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel"  border="0"> Excel</a> - 
				<spring:message code="profile.report.costsDetail"/>
			</li>
			<li>	
				<a href="../../report/${profile.profileId}/profileProducts.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${profile.profileId}/profileProducts.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel"  border="0"> Excel</a> - 
				<c:if test="${profile.incomeGen}"><spring:message code="profile.report.productDetail"/></c:if>
				<c:if test="${not profile.incomeGen}"><spring:message code="profile.report.productDetailNongen"/></c:if>
			</li>
			<li>
				<a href="../../report/${profile.profileId}/profilePrelimAnalysis.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${profile.profileId}/profilePrelimAnalysis.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel"  border="0"> Excel</a> - 
				<c:if test="${profile.incomeGen}"><spring:message code="profile.report.preliminary"/></c:if>
				<c:if test="${not profile.incomeGen}"><spring:message code="profile.report.benefits"/></c:if>
			</li>
			<li>
				<a href="../../report/${profile.profileId}/profileRecommendation.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${profile.profileId}/profileRecommendation.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel"  border="0"> Excel</a> - 
				<spring:message code="profile.report.recommendation"/>
			</li>
			<li>
				<a href="../../report/${profile.profileId}/profileComplete.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
				<a href="../../report/${profile.profileId}/profileComplete.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel"  border="0"> Excel</a> - 
				<spring:message code="profile.report.complete"/>
			</li>
		</ul>
	</div>
	<div style="display:inline-block;width:45%;">
		<h2><spring:message code="mainMenu.config.columns"/></h2>
		<tags:table>
			<table id="indicators" cellspacing="0" cellpadding="0">
				<tbody>
					<tr>
						<td><spring:message code="profile.profileName"/></td>
						<td style="text-align:left;">${result.profileName}</td>
					</tr>
					<tr>
						<td><spring:message code="profile.technician"/></td>
						<td style="text-align:left;">${result.technician.description}</td>
					</tr>
					<tr>
						<td><spring:message code="profile.status"/></td>
						<td style="text-align:left;"><tags:appConfigDescription ac="${result.status}"/></td>
					</tr>
					<tr>
						<td><spring:message code="profile.fieldOffice"/></td>
						<td style="text-align:left;"><tags:appConfigDescription ac="${result.fieldOffice}"/></td>
					</tr>
					<tr>
						<td><spring:message code="profile.investTotal"/></td>
						<td><tags:formatCurrency value="${result.investmentTotal}"/></td>
					</tr>
					<tr>
						<td><spring:message code="profile.investOwn"/></td>
						<td><tags:formatCurrency value="${result.investmentOwn}"/></td>
					</tr>
					<tr>
						<td><spring:message code="profile.investExt"/></td>
						<td><tags:formatCurrency value="${result.investmentExt}"/></td>
					</tr>
					<c:if test="${profile.incomeGen}">
						<tr>
							<td><spring:message code="profile.incomeAfterAnnual"/></td>
							<td><tags:formatCurrency value="${result.incomeAfterAnnual}"/></td>
						</tr>
						<tr>
							<td><spring:message code="profile.yearsToRecover"/></td>
							<td><tags:formatDecimal value="${result.yearsToRecover}"/></td>
						</tr>
					</c:if>
					<c:if test="${not profile.incomeGen}">
						<tr>
							<td><spring:message code="profile.investmentPerBenef"/></td>
							<td><tags:formatDecimal value="${result.investPerBenef}"/></td>
						</tr>
						<tr>
							<td><spring:message code="profile.costPerBenef"/></td>
							<td><tags:formatDecimal value="${result.costPerBenef}"/></td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</tags:table>
	</div>
	
	<c:set var="onsubmit">
		<c:if test="${profile.incomeGen}">javascript:search(false, 'igpf', '');</c:if>
		<c:if test="${not profile.incomeGen}">javascript:search(false, 'nigpf', '');</c:if>
	</c:set>
	<tags:submit onSubmit="${onsubmit}"><spring:message code="misc.finish"/></tags:submit>
</body></html>