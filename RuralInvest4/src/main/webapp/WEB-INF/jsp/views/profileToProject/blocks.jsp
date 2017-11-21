<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step9"/></title>
<style>
input.invalid { border:1px solid red; }
span.inline_invalid { display:none; } 
table.labourTable td:nth-child(2) { text-decoration: line-through; }
</style>
<script>
$(function () {
    $("input[class='invalid']").each(function() {
    	$(this).attr('title',$(this).next().text());
    });
});
</script>
</head>
<body>
<form:form name="form" method="post" modelAttribute="project">
	<tags:errors />
	
	<c:if test="${project.incomeGen}"><tags:profToProj message="profileToProject.blocks" /></c:if>
	<c:if test="${not project.incomeGen}"><tags:profToProj message="profileToProject.activities" /></c:if>
	
	<c:if test="${project.profileUpgrade eq 7}">
		<c:set var="tableSource" value="${project.blocks}"/>
		<c:set var="pathPrefix" value="blocks"/>
	</c:if>
	<c:if test="${project.profileUpgrade eq 8}">
		<c:set var="tableSource" value="${project.blocksWithout}"/>
		<c:set var="pathPrefix" value="blocksWithout"/>
	</c:if>
		
	<c:forEach var="blockEntry" items="${tableSource}">
		<c:choose>
			<c:when test="${project.profileUpgrade eq 8}"><c:set var="blockTitle">${blockEntry.description} <spring:message code="project.without"/></c:set></c:when>
			<c:when test="${project.withWithout}"><c:set var="blockTitle">${blockEntry.description} <spring:message code="project.with"/></c:set></c:when>
			<c:otherwise><c:set var="blockTitle">${blockEntry.description}</c:set></c:otherwise>
		</c:choose>
		<tags:table title="${blockTitle}">
			<display:table list="${blockEntry.labours}" id="lab" requestURI="" cellspacing="0" cellpadding="0"
							export="false" htmlId="labourTable${unique}" class="labourTable"> 
				<display:column titleKey="projectBlockLabour.desc" property="description" style="text-align:${left};" headerClass="left"/>
				<display:column titleKey="projectBlockLabour.unitType" sortable="true" style="text-align:${left};" headerClass="left">
					<tags:formSelectLabour path="${pathPrefix}[${blockEntry.orderBy}].labours[${lab_rowNum -1}].unitType" />
				</display:column>
				<display:column titleKey="projectBlockLabour.unitNum">
					<form:input path="${pathPrefix}[${blockEntry.orderBy}].labours[${lab_rowNum -1}].unitNum" cssErrorClass="invalid" onkeyup="javascript:commasKeyup(this);" style="text-align:right" size="11" maxLength="11" />
	            	<form:errors path="${pathPrefix}[${blockEntry.orderBy}].labours[${lab_rowNum -1}].unitNum" cssClass="inline_invalid" />
				</display:column>
				<display:column titleKey="projectBlockLabour.unitCost">
					<form:input path="${pathPrefix}[${blockEntry.orderBy}].labours[${lab_rowNum -1}].unitCost" cssErrorClass="invalid" onkeyup="javascript:commasKeyup(this);" style="text-align:right" size="11" maxLength="11" />
	            	<form:errors path="${pathPrefix}[${blockEntry.orderBy}].labours[${lab_rowNum -1}].unitCost" cssClass="inline_invalid" />
				</display:column>		
			</display:table>
		</tags:table>
	</c:forEach>
	
	<c:choose>
		<c:when test="${project.profileUpgrade eq 7 and project.withWithout and fn:length(project.blocksWithout) gt 0}"><c:set var="gotolabel"><spring:message code="misc.goto"/>  <spring:message code="project.step9"/> <spring:message code="project.without"/></c:set></c:when>
		<c:otherwise><c:set var="gotolabel"><spring:message code="misc.finish"/></c:set></c:otherwise>
	</c:choose>	
	<tags:submit>${gotolabel}</tags:submit>
</form:form>
</body></html>