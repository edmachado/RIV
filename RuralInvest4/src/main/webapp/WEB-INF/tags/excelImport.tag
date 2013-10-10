<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="submitUrl" required="true" %>
<link href="<c:url value="/styles/fineuploader-3.9.0-3.min.css"/>" rel="stylesheet">
<script src="<c:url value="/scripts/all.fineuploader-3.9.0-3.min.js"/>"></script>
<script src="<c:url value="/scripts/excelImport.js"/>"></script>
<script>
var submitUrl = '${submitUrl}';
</script>
	<div id="upload-dialog" title="<spring:message code="import.importExcel"/>">
		<p><spring:message code="import.excel.instruction"/></p>
		<div id="uploader-button"><spring:message code="import.excel.file"/></div>
		<div id="jquery-wrapped-fine-uploader"></div>
		<div id="uploader-error"></div>
	</div>