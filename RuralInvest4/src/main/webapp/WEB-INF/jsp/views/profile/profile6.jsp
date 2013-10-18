<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:if test="${profile.incomeGen}"><c:set var="title"><spring:message code="profile.step6"/></c:set><c:set var="prodType">profileProduct</c:set></c:if>
<c:if test="${!profile.incomeGen}"><c:set var="title"><spring:message code="profile.step6.nongen"/></c:set><c:set var="prodType">profileActivity</c:set></c:if>
<html><head><title>${title}</title>
<script>
$(function() {
	<c:if test="${profile.withWithout}">	$("#tabs").tabs();
	 if($("#tabs-without").find(window.location.hash).size()>0){
		 $("#tabs").tabs("option", "active", 1);
		} </c:if>
	$("#confirmDelete").dialog({
	bgiframe: true, autoOpen: false, resizable: false, height:300, width:400, modal: true,
	overlay: { backgroundColor: '#000', opacity: 0.5 },
	buttons: {
		Cancel: function() { $(this).dialog('close'); },
		'<spring:message code="misc.deleteItem"/>': function() { location.href=$('#deleteUrl').val(); }		
	}
});
});
</script>
</head>
<body>
<form:form name="form" method="post" commandName="profile">
	<tags:errors />
	<div align="right">
		<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a href="../../report/${profile.profileId}/profileProduct.xlsx?template=true"><spring:message code="export.downloadTemplate"/></a>
 	</div>
 	<br/>
 	<div id="tabs">
		<c:if test="${profile.withWithout}"><ul style="margin:0;">
			<li><a href="#tabs-with"><spring:message code="profileProduct.with.with"/></a></li>
			<li><a href="#tabs-without"><spring:message code="profileProduct.with.without"/></a></li>
		</ul></c:if>
		<div id="tabs-with">
		 	<c:if test="${accessOK}">
				<div align="left"><a id="addProduct" href="../product/-1?profileId=${profile.profileId}"><img src="../../img/product.gif" border="0"/>&nbsp;<c:if test="${profile.incomeGen}"><spring:message code="${prodType}.addNew"/></c:if><c:if test="${!profile.incomeGen}"><spring:message code="profileActivity.addNew"/></c:if>&nbsp;&nbsp;</a></div>
			</c:if>
			<c:forEach var="productEntry" items="${profile.products}">
				<tags:product product="${productEntry}" prodType="${prodType}"/>
				<br/>
			</c:forEach>
		</div>
		<c:if test="${profile.withWithout}">
			<div id="tabs-without">
				<c:if test="${accessOK}">
					<div align="left"><a id="addProductWithout" href="../product/-1?profileId=${profile.profileId}&without=true"><img src="../../img/product.gif" border="0"/>&nbsp;<c:if test="${profile.incomeGen}"><spring:message code="${prodType}.addNew"/></c:if><c:if test="${!profile.incomeGen}"><spring:message code="profileActivity.addNew"/></c:if>&nbsp;&nbsp;</a></div>
				</c:if>
				<c:forEach var="productEntry" items="${profile.productsWithout}">
					<tags:product product="${productEntry}" prodType="${prodType}"/>
					<br/>
				</c:forEach>
			</div>
		</c:if>
	</div>
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="profile.step7"/></tags:submit>
</form:form>
<div id="confirmDelete" title="<spring:message code="misc.deleteItem"/>">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	<spring:message code="misc.confirmDel"/></p><input id="deleteUrl" type="hidden" value=""/>
</div>
<tags:excelImport submitUrl="../../import/profile/product/"/>
</body></html>