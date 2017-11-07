<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><html>
<head><title><spring:message code="import.import"/></title><c:set var="accessOK" value="true" scope="request"/>
<script type="text/javascript">
function checkFile() {
	if ($("#file").val()=='') {
		$('#message').text('<spring:message code="import.file"/>');
		$('#errorbox').css('display','block');
		return false;
	} else {
		formSubmit();
	}
}
</script></head>
<body>
<form id="form" name="form" method="post" onsubmit="return checkFile();" enctype="multipart/form-data">
	<div id="errorbox" <c:if test="${empty error}">style="display:none;"</c:if>>
		<div class="header_error">&gt; <spring:message code="error"/></div>
		<span id="message">${error}</span>
	</div>
	<spring:message code="import.info"/><br/><br/>
	<input name="file" id="file" type="file" />
	<input type="hidden" name="allowComplete"/>
	<sec:csrfInput />
	<tags:submit><spring:message code="import.import"/></tags:submit>
</form>
</body></html>