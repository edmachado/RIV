<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="admin" scope="request"/><c:set var="currentStep" value="1" scope="request"/>
<html><head><title><spring:message code="admin.page"/></title>
<script language="javascript" src="../scripts/jquery.fileDownload.js" type="text/javascript"></script>
<link href="../styles/uploader/fine-uploader.min.css" rel="stylesheet"/>
<script src="../scripts/fine-uploader.min.js"></script>
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
var uploadDescRestore="<spring:message code='admin.restore.desc'/>";
var uploadDescConfig="<spring:message code='import.config.text'/>";
var uploadFailRestore="<spring:message code='admin.restore.fail'/>";
var uploadFailConfig="<spring:message code='import.config.error'/>";
var titleRestore="<spring:message code='admin.restore'/>";
var titleConfig="<spring:message code='import.config'/>";
</script>
<tags:excelImportHead submitUrl="admin/restore"/>
</head>
<body>

<c:if test="${user.administrator}"><h2><a id="reset" style="display:none;" href="reset">.</a></h2></c:if>

<c:if test="${not rivConfig.complete}">
	<div id="welcome">
		<h1><spring:message code="admin.welcome"/></h1>
		<p><spring:message code="admin.firstUse"/></p>
		<c:if test="${rivConfig.admin}">
			<br/>
			<spring:message code="admin.manualConfig.text"/>
			<a href="settings"><spring:message code="admin.manualConfig.link"/></a>
		</c:if>
	</div>
</c:if>

<h2><a id="import" href="javascript:uploadConfig();" href="" alt="<spring:message code='import.config'/>">
	<img src="../img/up.gif" alt="<spring:message code='import.config'/>"/>
	<spring:message code='import.config'/>
</a></h2>
<p><spring:message code="import.config.text"/></p>

<h2><a id="restore" href="javascript:uploadRestore();" href="" alt="<spring:message code="admin.restore"/>">
	<img src="../img/up.gif" alt="<spring:message code="admin.restore"/>"/>
	<spring:message code="admin.restore"/>
</a></h2>
<p><spring:message code="admin.restore.desc"/></p>

<c:if test="${rivConfig.complete}">
	<h2><a id="backup" class="fileDownloadSimpleRichExperience" href="export/backup.riv" alt="<spring:message code="admin.download"/>">
		<img src="../img/export_riv.gif" alt="<spring:message code="admin.download"/>"/>
		<spring:message code="admin.download"/>
	</a></h2>
	<p><spring:message code="admin.download.desc"/></p>
</c:if>

<div id="upload-dialog" title="<spring:message code="import.importExcel"/>">
	<p id="upload-description"><spring:message code="admin.restore.desc"/></p>
	<div id="uploader-error" style="display:none;">
	<div class="alert alert-error" style="margin-bottom:0"><h3 id="upload-fail"></h3>
		<div id="uploader-error-message"></div>
	</div>
	</div>
	<div id="uploader"></div>
</div>
<form id="qq-form" name="qq-form" method="post" enctype="multipart/form-data">
<sec:csrfInput />
</form>
</body></html>