<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<div id="summaryWc" title='<spring:message code="project.workingCapital.how"/>' class="summary">
	<p>
		<spring:message code="project.workingCapital.explain"/>
		<br/><br/>
	</p>
	<p>
		<spring:message code="misc.moreDetails"/><br/>
		<a href="../../report/${project.projectId}/projectWorkingCapital.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0"> <spring:message code="project.report.workingcapital"/> </a>
		<br/>
		<a href="../../report/${project.projectId}/projectWorkingCapital.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> <spring:message code="project.report.workingcapital"/> </a>
	</p>
</div>