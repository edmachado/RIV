<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="profile.step9"/></title>
</head>
<body>
	<h2><spring:message code="profile.report.title"/></h2>
	
	<spring:message code="profile.report.description"/><br/><br/>
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
			<a href="../../report/${profile.profileId}/profileProduct.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a href="../../report/${profile.profileId}/profileProduct.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel"  border="0"> Excel</a> - 
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
	<c:set var="onsubmit">
		<c:if test="${profile.incomeGen}">javascript:search(false, 'igpf', '');</c:if>
		<c:if test="${not profile.incomeGen}">javascript:search(false, 'nigpf', '');</c:if>
	</c:set>
	<tags:submit onSubmit="${onsubmit}"><spring:message code="misc.finish"/></tags:submit>
</body></html>