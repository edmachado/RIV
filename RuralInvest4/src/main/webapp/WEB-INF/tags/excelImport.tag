<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="submitUrl" required="true" %>
<script>
var failMsg='';
var submitUrlBase='${submitUrl}';
var uploadBlockId='';
</script>
<link href="<c:url value="/styles/fineuploader-3.9.0-3.min.css"/>" rel="stylesheet">
<script src="<c:url value="/scripts/all.fineuploader-3.9.0-3.min.js"/>"></script>
<script src="<c:url value="/scripts/excelImport.js"/>"></script>
<div id="upload-dialog" title="<spring:message code="import.importExcel"/>">
	<p><spring:message code="import.excel.instruction"/></p>
	<div id="uploader-button"><spring:message code="import.excel.file"/></div>
	<div id="jquery-wrapped-fine-uploader"></div>
	<div id="uploader-error" style="display:none;">
		<div class="alert alert-error"><h3><spring:message code="import.excel.fail"/></h3>
			<div id="uploader-error-message"></div>
		</div>
	</div>
</div>