<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="submitUrl" required="true" %>
<div id="upload-dialog" title="<spring:message code="import.importExcel"/>">
	<p><spring:message code="import.excel.instruction"/></p>
	<div id="uploader"></div>
</div>
<form id="qq-form" name="qq-form" action="${submitUrl}" method="post" enctype="multipart/form-data">
<sec:csrfInput />
</form>