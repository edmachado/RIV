<%@page contentType="text/html" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="${rivConfig.setting.lang}"<c:if test="${rivConfig.setting.lang=='ar'}"> dir="rtl"</c:if>>
<head>
    <title>RuralInvest :: <decorator:title default="RuralInvest"/></title>
    <tags:htmlHead/>
	<decorator:head/>
</head>
<body>
	<%-- Helper tools for testing (only if testing version running) --%>
	<c:if test="${rivConfig.qa}">
	<script> 
	function test(input) {$(":text").val(input); $('textarea').val(input);} 
	var threek1 = "x"; for (var i=0;i<300;i++) {threek1=threek1+"01234567890";}
	</script>
	<div style="text-align:center">Testing: <a onclick="javascript:test('');">add blanks</a> | <a onclick="javascript:test('-1');">add negative</a> |
	<a onclick="javascript:test(threek1);">add 3001 characters</a>
	</div></c:if>
	<%-- End testing --%>
	
	<tags:toprow/>
	<div id="title"><decorator:title default="RuralInvest"/></div>
	<table id="main">
		<tr>
			<td id="side"><tags:navigation /></td>
	   		<td id="content"><decorator:body/></td>
	    </tr>
	</table>
</body>
</html>