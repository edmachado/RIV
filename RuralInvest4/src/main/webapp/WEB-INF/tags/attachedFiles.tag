<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:if test="${empty project.projectId}">
	<div class="dataentry"><spring:message code="attach.newProject"/></div>
</c:if>
<c:if test="${not empty project.projectId}">
	<c:set var="left"><c:if test="${lang!='ar'}">left</c:if><c:if test="${lang=='ar'}">right</c:if></c:set>
	<div class="dataentry">
		<div class="tableOuter">
			<display:table htmlId="attachedFiles" list="${files}" id="row" requestURI="" cellspacing="0" cellpadding="0">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column style="text-align:${left};">
					<a href="../${project.projectId}/attach/${row.id}/${row.filename}" target="_blank">${row.filename}</a>
				</display:column>
				<c:if test="${accessOK}">
					<display:column media="html">
						<a href="../${project.projectId}/attach/${row.id}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
					</display:column>
				</c:if>
			</display:table>
		</div>
		<spring:message code="attach.free"/>: ${freeSpace} / 3.0 Mb<br/>
		<c:if test="${accessOK}">
			<b><a id="attachFile" href="../${project.projectId}/attach"><spring:message code="attach.new"/></a></b>
		</c:if>
	</div>
</c:if>