<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><html>
<head><title><spring:message code="attach.new"/></title>
<script type="text/javascript">
function checkFile() {
	if ($("#file").val()=='') {
		$('#message').text('Choose a file to import.');
		$('#errorbox').css('display','block');
		return false;
	} else {
		formSubmit();
	}
}
</script></head>
<body>
	<form:form id="form" name="form" method="post" commandName="probase" onsubmit="return checkFile();" enctype="multipart/form-data">
		<div id="errorbox" <c:if test="${empty error}">style="display:none;"</c:if>>
			<div class="header_error">&gt; <spring:message code="error"/></div>
			<span id="message">${error}</span>
		</div>
<%-- 		<spring:message code="attach.new"/><br/><br/> --%>
		<input type="file" name="file" id="file" /><br/><br/>
		
		<spring:message code="attach.used"/>: ${dirSize}<br/>
		<spring:message code="attach.free"/>: ${freeSpace} / 3 Mb
		<tags:submit><spring:message code="import.import"/></tags:submit>
	</form:form>
</body></html>