<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="admin" scope="request"/><c:set var="currentStep" value="1" scope="request"/>
<html><head><title><spring:message code="admin.page"/></title>
<script language="javascript" src="../scripts/jquery.fileDownload.js" type="text/javascript"></script>
<link href="../styles/fineuploader-3.9.0-3.min.css" rel="stylesheet"/>
<script src="../scripts/all.fineuploader-3.9.0-3.min.js"></script>
<script src="../scripts/excelImport.js"></script>
<script>
$(document).on("click", "a.fileDownloadSimpleRichExperience", function () {
    $.fileDownload($(this).prop('href'), {
        preparingMessageHtml: "<spring:message code='admin.download.prepare'/>",
        failMessageHtml: "<spring:message code='admin.download.error'/>"
    });
    return false; //this is critical to stop the click event which will trigger a normal file download!
});
var failMsg=''; var uploadBlockId='';
var submitUrlBase;
var uploadDescRestore='<spring:message code="admin.restore.desc"/>';
var uploadDescConfig='FOOCIAO';
var titleRestore='<spring:message code="admin.restore"/>';
var titleConfig='UPLOAD CONFIG';
</script>
</head>
<body>

<h2><a id="import" href="javascript:uploadConfig();" href="" title="UPLOAD CONFIG" alt="UPLOAD CONFIG">
	<img src="../img/up.gif" alt="UPLOAD CONFIG"/>
	UPLOAD CONFIG
</a></h2>
<p>IMPORT CONFIGURATION FROM SETTINGS.RIV FILE</p>

<h2><a id="backup" class="fileDownloadSimpleRichExperience" href="export/backup.riv" alt="<spring:message code="admin.download"/>">
	<img src="../img/export_riv.gif" alt="<spring:message code="admin.download"/>"/>
	<spring:message code="admin.download"/>
</a></h2>
<p><spring:message code="admin.download.desc"/></p>

<h2><a id="restore" href="javascript:uploadRestore();" href="" title="<spring:message code="admin.restore"/>" alt="<spring:message code="admin.restore"/>">
	<img src="../img/up.gif" alt="<spring:message code="admin.restore"/>"/>
	<spring:message code="admin.restore"/>
</a></h2>
<p><spring:message code="admin.restore.desc"/></p>

<c:if test="${user.administrator}"><h2><a id="reset" style="display:none;" href="reset">Reset RuralInvest</a></h2></c:if>

<div id="upload-dialog">
	<p id="upload-description"><spring:message code="admin.restore.desc"/></p>
	<div id="uploader-button"><spring:message code="import.file"/></div><%--<spring:message code="admin.restore.button"/> --%>
	<div id="jquery-wrapped-fine-uploader"></div>
	<div id="uploader-error" style="display:none;">
		<div class="alert alert-error"><h3><spring:message code="admin.restore.fail"/></h3>
			<div id="uploader-error-message"></div>
		</div>
	</div>
</div>
<form id="upload-test-form" action="admin/import" method="post" enctype="multipart/form-data"></form>
</body></html>