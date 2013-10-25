<%@page contentType="text/html" pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="${lang}"<c:if test="${lang=='ar'}"> dir="rtl"</c:if>>
<head><title><spring:message code="ruralInvest"/> :: <spring:message code="mainMenu.home"/></title><tags:htmlHead/></head>
<body><tags:toprow/>
<div id="home">
	<div id="homeheader"><!-- first row of titles -->
		<div> &gt; <spring:message code="home.incGen"/></div><!-- 
		 --><div> &gt; <spring:message code="home.nonIncGen"/></div><!-- 
		 --><div> &gt; <spring:message code="home.weblinks"/></div>
	</div><!-- 
--><div style="margin-bottom:-5px;"><div class="homecontainer">
			<div class="hometitle" style="background-color:#EFCE6C"> &gt; <spring:message code="profile"/></div>
			<div class="homeitems">
				<a href="profile/step1/-1?incgen=true"  title="<spring:message code="home.createIGProf"/>"><img src="img/new.gif" width="16" height="12" border="0"><spring:message code="home.createNew"/></a>
				<a id="igpf_no" href="javascript:search(true, 'igpf', '');"><img src="img/complete.gif" border="0"><spring:message code="home.inProgress"/> (${homeData.dbStats[1]})</a>
				<br/>
				<a id="igpf_yes" href="javascript:search(false, 'igpf', '');"><img src="img/all.gif" width="16" height="12" border="0"><spring:message code="home.showAll"/> (${homeData.dbStats[0]})</a>
			</div>
			<div class="hometitle" style="background-color:#E7AE0F"> &gt; <spring:message code="project"/></div>
			<div class="homeitems">
				<a href="project/step1/-1?incgen=true" title="<spring:message code="home.createIGProj"/>"><img src="img/new.gif" width="16" height="12" border="0"><spring:message code="home.createNew"/></a>
				<a id="igpj_no" href="javascript:search(true, 'igpj', '');"><img src="img/complete.gif" border="0"><spring:message code="home.inProgress"/> (${homeData.dbStats[5]})</a>
				<br/>
				<a id="igpj_yes" href="javascript:search(false, 'igpj', '');"><img src="img/all.gif" width="16" height="12" border="0"><spring:message code="home.showAll"/> (${homeData.dbStats[4]})</a>
			</div>
		</div><img width="248" height="140" alt="FAO in action" src="img/faoinaction5x248.jpg"><div class="homecontainer" style="background:#F5F5F5">
				<div style="height:65px;">
					<c:if test="${not empty rivConfig.setting.link1Text}">
						<div><a href="http://${rivConfig.setting.link1Url}" target="_blank"><img src="img/external.gif" width="11" height="10" border="0"/>${rivConfig.setting.link1Text}</a></div>
					 </c:if>
					 <c:if test="${not empty rivConfig.setting.link2Text}">
						<div><a href="http://${rivConfig.setting.link2Url}" target="_blank"><img src="img/external.gif" width="11" height="10" border="0"/>${rivConfig.setting.link2Text}</a></div>
					 </c:if>
					 <c:if test="${not empty rivConfig.setting.link3Text}">
						<div><a href="http://${rivConfig.setting.link3Url}" target="_blank"><img src="img/external.gif" width="11" height="10" border="0"/>${rivConfig.setting.link3Text}</a></div>
					 </c:if>
					 <c:if test="${not empty rivConfig.setting.link4Text}">
						<div><a href="http://${rivConfig.setting.link4Url}" target="_blank"><img src="img/external.gif" width="11" height="10" border="0"/>${rivConfig.setting.link4Text}</a></div>
					 </c:if>
				</div>
				<div class="hometitle" style="background-color:#D7D7D7"> &gt; <spring:message code="home.inProgress"/></div>
		<div style="height:51px;">
			<c:set var="i" value="0" scope="request"/>
				<c:forEach var="ip" items="${homeData.inProgress}">
				<div>
					<c:if test="${i<3}">
						<c:set var="link">
							<c:if test="${ip.project}">project/step1/${ip.id}</c:if>
							<c:if test="${not ip.project}">profile/step1/${ip.id}</c:if>
						</c:set>
						<c:choose>
							<c:when test="${empty ip.name}"><c:set var="pname" value="[...]"/></c:when>
							<c:when test="${fn:length(ip.name) gt 26}"><c:set var="pname" value="${fn:substring(ip.name, 0, 25)}..."/></c:when>
							<c:otherwise><c:set var="pname" value="${ip.name}"/></c:otherwise>
						</c:choose>
						<span class="inprogress-date"><a href="${link}" title="${ip.name}">${pname}</a></span>
						<span><fmt:formatDate value="${ip.lastModified}" type="both" pattern="dd/MM/yy" /></span>
					</c:if>
					<c:set var="i" value="${i+1}"/>
				</div>
				</c:forEach>
		
		</div>
		</div>
	</div><div><img width="248" height="140" alt="FAO in action" src="img/faoinaction1x248.jpg"><div class="homecontainer">
			<div class="hometitle" style="color:#fff;background-color:#8C94B1"> &gt; <spring:message code="profile"/></div>
			<div class="homeitems">
				<a href="profile/step1/-1?incgen=false" title="<spring:message code="home.createNIGProf"/>"><img src="img/new.gif" width="16" height="12" border="0">
					<spring:message code="home.createNew"/></a>
				<a id="nigpf_no" href="javascript:search(true, 'nigpf', '');"><img src="img/complete.gif" border="0">
					<spring:message code="home.inProgress"/> (${homeData.dbStats[3]})</a>
				<br/>
				<a id="nigpf_yes" href="javascript:search(false, 'nigpf', '');"><img src="img/all.gif" width="16" height="12" border="0">
					<spring:message code="home.showAll"/> (${homeData.dbStats[2]})</a>
			</div>
			<div class="hometitle" style="color:#fff;background-color:#344378"> &gt; <spring:message code="project"/></div>
			<div class="homeitems">
				<a href="project/step1/-1?incgen=false" title="<spring:message code="home.createNIGProj"/>"><img src="img/new.gif" width="16" height="12" border="0">
					<spring:message code="home.createNew"/></a>
				<a id="nigpj_no" href="javascript:search(true, 'nigpj', '');"><img src="img/complete.gif" border="0">
					 <spring:message code="home.inProgress"/> (${homeData.dbStats[7]})</a>
				<br/>
				<a id="nigpj_yes" href="javascript:search(false, 'nigpj', '');"><img src="img/all.gif" width="16" height="12" border="0">
					<spring:message code="home.showAll"/> (${homeData.dbStats[6]})</a>
			</div>
		</div><img width="248" height="140" alt="FAO in action" src="img/faoinaction4x248.jpg"/>
	</div>
</div>

<div id="belowHome">
	<p><tags:help title="home.help" text="home.help.help"><b><spring:message code="home.help"/></b></tags:help></p>
	<p>
		<b><spring:message code="home.help.faq"/></b><br/>
		<a href="help/faq.htm"><spring:message code="home.help.faq.info"/></a>
	</p>
</div>
</body></html>