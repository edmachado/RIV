<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="projectInvestAsset"/></title>
<style>
input.invalid { border:1px solid red; }
span.inline_invalid { display:none; } 
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
	
	<tags:profToProj message="profileToProject.asset" />
	
	<c:set var="assetTitle">
		<c:if test="${project.incomeGen}"><spring:message code="projectInvestAsset"/></c:if>
		<c:if test="${not project.incomeGen}"><spring:message code="projectInvestAssetNongen"/></c:if>
	</c:set>
	
	<c:if test="${project.profileUpgrade eq 1}">
		<c:set var="tableSource" value="${project.assets}"/>
		<c:set var="tableSourceName" value="assets"/>
		<c:if test="${project.withWithout}">
			<c:set var="assetTitle">${assetTitle} <spring:message code="project.with"/></c:set>
		</c:if>
	</c:if>
	<c:if test="${project.profileUpgrade eq 2}">
		<c:set var="tableSource" value="${project.assetsWithout}"/>
		<c:set var="tableSourceName" value="assetsWithout"/>
		<c:set var="assetTitle">${assetTitle} <spring:message code="project.without"/></c:set>
	</c:if>
			
	<tags:table title="${assetTitle}">
		<display:table htmlId="assetsTable" list="${tableSource}" requestURI="" cellspacing="0" cellpadding="0"
			 export="false" id="asset">
			<display:setProperty name="basic.empty.showtable">true</display:setProperty><display:column titleKey="projectInvestAsset.description" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="projectInvestAsset.unitType" property="unitType" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="projectInvestAsset.unitNum" >
				<tags:formatDecimal value="${asset.unitNum}"/>
			</display:column>
			<display:column titleKey="projectInvestAsset.unitCost" >
				<tags:formatCurrency value="${asset.unitCost}"/>
			</display:column>
			<display:column titleKey="projectInvestAsset.totalCost" >
				<tags:formatCurrency value="${asset.total}"/>
			</display:column>
			<display:column titleKey="projectInvestAsset.ownResources" >
				<tags:formatCurrency value="${asset.ownResources}"/>
			</display:column>
			<display:column titleKey="projectInvestAsset.donated" >
				<tags:formatCurrency value="${asset.donated}"/>
			</display:column>
			<display:column titleKey="projectInvestAsset.financed" >
				<tags:formatCurrency value="${asset.financed}"/>
			</display:column>
			<display:column titleKey="projectInvestAsset.econLife" property="econLife"  />
			<display:column titleKey="projectInvestAsset.maintCost" >
				<form:input path="${tableSourceName}[${asset_rowNum -1}].maintCost" cssErrorClass="invalid" onkeyup="javascript:commasKeyup(this);" style="text-align:right" size="11" maxLength="11" />
	            <form:errors path="${tableSourceName}[${asset_rowNum -1}].maintCost" cssClass="inline_invalid" />
			</display:column>
			<display:column titleKey="projectInvestAsset.salvage">
				<tags:formatCurrency value="${asset.salvage}"/>
			</display:column>
			<display:column titleKey="projectInvestAsset.replace" style="text-align:center;" headerClass="centered">
				<form:checkbox path="${tableSourceName}[${asset_rowNum -1}].replace" value="${asset.replace}"/>
			</display:column>
			<display:column titleKey="projectInvestAsset.yearBegin" style="text-align:center;" headerClass="centered">
				<form:select path="${tableSourceName}[${asset_rowNum -1}].yearBegin" >
					<c:forEach var="i" begin="1" end="${project.duration}">
						<form:option value="${i}" label="${i}"/>
					</c:forEach>
				</form:select>
			</display:column>
		</display:table>
	</tags:table>
	
	<c:choose>
		<c:when test="${project.profileUpgrade eq 1 and project.withWithout and fn:length(project.assetsWithout) >0}">
			<c:set var="gotolabel">
				<spring:message code="misc.goto"/> 
				<c:if test="${project.incomeGen}"><spring:message code="projectInvestAsset"/></c:if>
				<c:if test="${not project.incomeGen}"><spring:message code="projectInvestAssetNongen"/></c:if>
				<spring:message code="project.without"/>
			</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="gotolabel"><spring:message code="misc.goto"/> <spring:message code="profileToProject.investLabour"/></c:set>
		</c:otherwise> 
	</c:choose>
	
	<tags:submit>${gotolabel}</tags:submit>
</form:form>
</body></html>