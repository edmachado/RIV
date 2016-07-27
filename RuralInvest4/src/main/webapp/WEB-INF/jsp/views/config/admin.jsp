<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="config" scope="request"/><c:set var="currentStep" value="2" scope="request"/>
<html><head><title><spring:message code="admin.page"/></title>
<script language="javascript" src="../scripts/jquery.fileDownload.js" type="text/javascript"></script>
</head>
<body>
<h2><a class="fileDownloadSimpleRichExperience" href="export/backup.riv" alt="<spring:message code="admin.download"/>"><spring:message code="admin.download"/></a></h2>
<p><spring:message code="admin.download.desc"/></p>
<h2><a href="" title="<spring:message code="admin.restore"/>" alt="<spring:message code="admin.restore"/>"><spring:message code="admin.restore"/></a></h2>
<p><spring:message code="admin.restore.desc"/></p>
<script>
$(document).on("click", "a.fileDownloadSimpleRichExperience", function () {
    $.fileDownload($(this).prop('href'), {
        preparingMessageHtml: "<spring:message code='admin.download.prepare'/>",
        failMessageHtml: "<spring:message code='admin.download.error'/>"
    });
    return false; //this is critical to stop the click event which will trigger a normal file download!
});
</script>
</body></html>