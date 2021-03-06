<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:if test="${project.incomeGen}"><c:set var="blockType" value="projectBlock" /></c:if>
<c:if test="${not project.incomeGen}"><c:set var="blockType" value="projectActivity" /></c:if>
<html>
	<head>
		<title><c:if test="${project.incomeGen}"><spring:message code="project.step9"/></c:if><c:if test="${not project.incomeGen}"><spring:message code="project.step9.nongen"/></c:if></title>
		<c:if test="${project.withWithout}"><script>
			$(function() { $("#tabs").tabs();
				 if($("#tabs-without").find(window.location.hash).size()>0){ $("#tabs").tabs("option", "active", 1); } 
			});
		</script></c:if>
		<style>
			div.dataentry {margin-left:0;}
			div.dataentry label { line-height:1em; }
			#tabs ul { margin:0; }
			div.dataentry { font-size:11px;}
		</style>
		<tags:excelImportHead submitUrl="../../import/project/block/"/>
	</head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />
	<span class="error"><form:errors field="incomes"/></span>
	
	<div align="right">
		<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadTemplate" href="../../report/${project.projectId}/projectBlock.xlsx?template=${project.incomeGen}" target="_blank"><spring:message code="export.downloadTemplate"/></a><br/>
 	</div>
 	<br/>
	<div id="tabs">
		<c:if test="${project.withWithout}"><ul>
			<li><a href="#tabs-with"><spring:message code="projectBlock.with.with"/></a></li>
			<li><a href="#tabs-without"><spring:message code="projectBlock.with.without"/></a></li>
		</ul></c:if>
		<div id="tabs-with">
			<c:if test="${accessOK}">
				<div align="left" style="margin-bottom:5px;" >
					<a id="addBlock" href="../block/-1?projectId=${project.projectId}">
						<img src="../../img/product.gif" border="0"/>
						<spring:message code="${blockType}.addNew"/>
					</a>
				</div>
			</c:if>
			<c:forEach var="blockEntry" items="${project.blocks}">
				<tags:block blockType="${blockType}" blockEntry="${blockEntry}"></tags:block>
			</c:forEach>
		</div>
		<c:if test="${project.withWithout}"><div id="tabs-without">
			<c:if test="${accessOK}">
				<div align="left" style="margin-bottom:5px;" >
					<a id="addBlockWithout" href="../block/-1?projectId=${project.projectId}&without=true">
						<img src="../../img/product.gif" border="0"/>
						<spring:message code="${blockType}.addNew"/>
					</a>
				</div>
			</c:if>
			<c:forEach var="blockEntry" items="${project.blocksWithout}">
				<tags:block blockType="${blockType}" blockEntry="${blockEntry}"></tags:block>
			</c:forEach>
		</div></c:if>
	</div>
	<tags:submit>&raquo;&nbsp;<spring:message code="misc.goto"/> 
		<c:if test="${project.incomeGen}"><spring:message code="project.step10"/></c:if>
		<c:if test="${not project.incomeGen}"><spring:message code="project.step10.nongen"/></c:if>
	</tags:submit>
</form:form>
<tags:confirmDelete/>
<tags:excelImport submitUrl="../../import/project/block/"/>
</body></html>