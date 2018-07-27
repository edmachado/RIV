<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ page pageEncoding="UTF-8"%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">
<html><head><title>RuralInvest login</title><META http-equiv="Content-Type" content="text/html;charset=UTF-8"><link rel="SHORTCUT ICON" href="img/riv.ico"/>
	<script language="javascript" src="<c:url value="/scripts/general.js"/>" type="text/javascript"></script>
	<script language="javascript" src="<c:url value="/scripts/jquery-1.9.1.js"/>" type="text/javascript"></script>
	<script language="javascript" src="<c:url value="/scripts/jquery-ui-1.10.3.custom.min.js"/>" type="text/javascript"></script>
	<link href="<c:url value="/styles/riv-theme/jquery-ui-1.10.3.custom.css"/>" rel="stylesheet"/>
 
	<c:if test="${lang ne 'ar'}"><link rel="stylesheet" type="text/css" href="<c:url value="/styles/style.css"/>" media="screen"/></c:if>
	<c:if test="${lang eq 'ar'}"><link rel="stylesheet" type="text/css" href="<c:url value="/styles/style_rtl.css"/>" media="screen"/></c:if>
	<!--[if lt IE 9]>
	<link rel="stylesheet" type="text/css" href="<c:url value="/styles/style-ie8.css"/>" />
	<![endif]-->
</head>
<body  onload='document.login.username.focus();'>

<table width="750" border="0" cellspacing="0" cellpadding="0">
  <tr><td width="1"><img src="img/spacer.gif" width="1" height="1" alt=""/></td>
    <td width="347" align="left" valign="top" bgcolor="#F5F5F5">
    	<form name="login" action="login" method="post"> <sec:csrfInput />
			<table border="0" cellspacing="0" cellpadding="2">
			  <tr><td colspan="3" valign="top" align="left"><br> <img src="img/logo40.gif" width="320" height="46" hspace="6" alt="<spring:message code="ruralInvest"/>"><br><br><br></td></tr>
		      <tr> 
		        <td><img src="img/spacer.gif" width="4" height="1" alt=""></td>
		        <td colspan="3"><span class="header_grey">&gt;&nbsp;<spring:message code="login"/></span></td>
		      </tr>
		      <tr> 
		        <td><img src="img/spacer.gif" width="4" height="1" alt=""></td>
		        <td><spring:message code="login.username"/></td>
		        <td><input type="text" name="username" id="username" size="30"/></td>
		        <td/>
		      </tr>
		      <tr> 
		        <td><img src="img/spacer.gif" width="4" height="1" alt=""/></td>
		        <td><spring:message code="login.password"/></td>	
		        <td><input type="password" name="password" id="password" size="30"/></td>
		        <td/>
		      </tr>
		      <c:if test="${not empty param.error}"><tr><td/><td colspan="3"><font style="color:red"><spring:message code="login.incorrect"/></font></td></tr></c:if>
		      <tr> 
		        <td colspan="4" align="right"> 
		        	<input name="submit" type="submit" class="button" value="<spring:message code="header.go"/>"/></td>	
		      </tr>
	    	</table>
	    </form>
    </td>
    <td width="1"><img src="img/spacer.gif" width="1" height="1" alt=""></td>
    <td width="400" align="left" valign="top"><img id="mainPhoto" src="img/spacer.gif"  width="400" height="225" alt="<spring:message code="misc.faoInAction"/>" /></td>
    <td width="1"><img src="img/spacer.gif" width="1" height="1" alt=""></td>
  </tr>
  <tr><td height="20" colspan="5" bgcolor="#E7AE0F">&nbsp;</td></tr>
</table>
<script>
	randomPhoto('mainPhoto',1,8);
</script>
<c:if test="${rivConfig.demo}">
<script>
	$('#username').val('<spring:message code="demo.username"/>');
	$('#password').val('<spring:message code="demo.password"/>');
</script>
	<div id="login-demo">
		<center>
		<select id="page-lang" onchange="window.location.href='login?lang='+$('#page-lang').val();">
			<option value="en"<c:if test="${pageLang eq 'en'}"> selected</c:if>>English</option>
			<option value="es"<c:if test="${pageLang eq 'es'}"> selected</c:if>>Español</option>
			<option value="fr"<c:if test="${pageLang eq 'fr'}"> selected</c:if>>Français</option>
			<option value="sw"<c:if test="${pageLang eq 'sw'}"> selected</c:if>>Kiswahili</option>
			<option value="mn"<c:if test="${pageLang eq 'mn'}"> selected</c:if>>Монгол</option>
			<option value="pt_BR"<c:if test="${pageLang eq 'pt_BR'}"> selected</c:if>>Português (Brasil)</option>
			<option value="pt_PT"<c:if test="${pageLang eq 'pt_PT'}"> selected</c:if>>Português (Portugal)</option>
			<option value="ru"<c:if test="${pageLang eq 'ru'}"> selected</c:if>>Русский</option>
			<option value="tr"<c:if test="${pageLang eq 'tr'}"> selected</c:if>>Türkçe</option>
			<option value="ar"<c:if test="${pageLang eq 'ar'}"> selected</c:if>>العربية</option>
		</select>
		</center>
		<h2 style="text-align:center"><spring:message code="demo.welcome"/><br/><spring:message code="demo.version"/> ${rivConfig.version}</h2>
		
		<p><spring:message code="demo.para1"/></p>
<%-- 		<p><spring:message code="demo.para2"/></p> --%>
		<p><spring:message code="demo.para3"/></p>
		<ul>
			<li><spring:message code="profile.profiles.incomeGen"/></li>
			<li><spring:message code="profile.profiles.nonIncomeGen"/></li>
			<li><spring:message code="project.projects.incomeGen"/></li>
			<li><spring:message code="project.projects.nonIncomeGen"/></li>
		</ul>
		<p><spring:message code="demo.profile"/></p>
		<p><spring:message code="demo.project"/></p>
		<p><spring:message code="demo.guide"/></p>
		<p style="text-align:center;">*************************************</p>
		<p><spring:message code="demo.examples.1"/> <img src="img/edit.png" border="0"/> <spring:message code="demo.examples.2"/></p>
		<p><spring:message code="demo.create"/></p>
	</div>
</c:if>
</body></html>