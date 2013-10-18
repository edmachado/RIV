<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="lang">en</c:set><c:set var="ctx"><%=request.getContextPath()%></c:set>
<c:set var="padding">padding-<c:if test="${lang=='ar'}">right</c:if><c:if test="${lang!='ar'}">left</c:if>:</c:set>
<style>#menu2{${padding}<spring:message code="style.menu2"/>px;} #menu3{${padding}<spring:message code="style.menu3"/>px;} #menu4{${padding}<spring:message code="style.menu4"/>px;} #menu5{${padding}<spring:message code="style.menu5"/>px;} #menu6{${padding}<spring:message code="style.menu6"/>px;} #menu7{${padding}<spring:message code="style.menu7"/>px;} #menu8{${padding}<spring:message code="style.menu8"/>px;}</style>
<div id="nav">&nbsp;&nbsp;&nbsp;
	<span><a onmouseover="menuItemOver(1)" href="${ctx}/home" id="goHome"><spring:message code="mainMenu.home"/></a></span>
	<span><a onmouseover="menuItemOver(2)"><spring:message code="mainMenu.profiles"/> ($)</a></span>
	<span><a onmouseover="menuItemOver(3)"><spring:message code="mainMenu.profiles"/> (:)</a></span>
	<span><a onmouseover="menuItemOver(4)"><spring:message code="mainMenu.projects"/> ($)</a></span>
	<span><a onmouseover="menuItemOver(5)"><spring:message code="mainMenu.projects"/> (:)</a></span>
	<span><a onmouseover="menuItemOver(6)"><spring:message code="mainMenu.searchReports"/></a></span>
	<span><a onmouseover="menuItemOver(7)"><spring:message code="mainMenu.help"/></a></span>
	<span><a onmouseover="menuItemOver(8)"><spring:message code="mainMenu.config"/></a></span>
	<div id="menu1" style="display:block">&nbsp;</div>
	<div id="menu2">
		<span><u>&nbsp;</u><a id="newIgProfile"
		href="${ctx}/profile/step1/-1?incgen=true"><spring:message code="mainMenu.create"/></a><a id="importProfileIg"
		href="${ctx}/profile/import"><spring:message code="mainMenu.import"/></a><a
		href="javascript:search(true, 'igpf', '');"><spring:message code="mainMenu.search"/></a><a
		href="javascript:search(false, 'igpf', '');"><spring:message code="mainMenu.showAll"/></a><u>&nbsp;</u></span>
	</div>
	<div id="menu3">
		<span><u>&nbsp;</u><a id="newNigProfile"
		href="${ctx}/profile/step1/-1?incgen=false"><spring:message code="mainMenu.create"/></a><a id="importProfileNig"
		href="${ctx}/profile/import"><spring:message code="mainMenu.import"/></a><a
		href="javascript:search(true, 'nigpf', '');"><spring:message code="mainMenu.search"/></a><a
		href="javascript:search(false, 'nigpf', '');"><spring:message code="mainMenu.showAll"/></a><u>&nbsp;</u></span>
	</div>
	<div id="menu4">
		<span><u>&nbsp;</u><a id="newIgProject"
		href="${ctx}/project/step1/-1?incgen=true"><spring:message code="mainMenu.create"/></a><a id="importProjectIg"
		href="${ctx}/project/import"><spring:message code="mainMenu.import"/></a><a
		href="javascript:search(true, 'igpj', '');"><spring:message code="mainMenu.search"/></a><a id="allIgpj"
		href="javascript:search(false, 'igpj', '');"><spring:message code="mainMenu.showAll"/></a><u>&nbsp;</u></span>
	</div>
	<div id="menu5">
		<span><u>&nbsp;</u><a id="newNigProject"
		href="${ctx}/project/step1/-1?incgen=false"><spring:message code="mainMenu.create"/></a><a id="importProjectNig"
		href="${ctx}/project/import"><spring:message code="mainMenu.import"/></a><a
		href="javascript:search(true, 'nigpj', '');"><spring:message code="mainMenu.search"/></a><a
		href="javascript:search(false, 'nigpj', '');"><spring:message code="mainMenu.showAll"/></a><u>&nbsp;</u></span>
	</div>
	<div id="menu6">
		<span><u>&nbsp;</u><a href="${ctx}/search/new"><spring:message code="mainMenu.searchItem"/></a><u>&nbsp;</u></span>
	</div>
	<div id="menu7">
		<span><u>&nbsp;</u><a href="${ctx}/help/manuals"><spring:message code="mainMenu.help.manual"/></a><a
		href="${ctx}/help/faq"><spring:message code="mainMenu.help.faq"/></a><a
		href="${ctx}/help/about"><spring:message code="mainMenu.help.about"/></a><u>&nbsp;</u></span>
	</div>
	<div id="menu8">
		<span><u>&nbsp;</u><a href="${ctx}/config/settings" id="gotoSettings"><spring:message code="mainMenu.config.settings"/></a><a
		href="${ctx}/config/user" id="gotoUsers"><spring:message code="mainMenu.config.users"/></a><a
		href="${ctx}/config/office" id="gotoOffices"><spring:message code="mainMenu.config.offices"/></a><a
		href="${ctx}/config/category" id="gotoCategories"><spring:message code="mainMenu.config.categories"/></a><a
		href="${ctx}/config/beneficiary" id="gotoBenefs"><spring:message code="mainMenu.config.beneficiaries"/></a><a
		href="${ctx}/config/enviroCategory" id="gotoEnviros"><spring:message code="mainMenu.config.enviroCategories"/></a><a
		href="${ctx}/config/status" id="gotoStatuses"><spring:message code="mainMenu.config.statuses"/></a><a
		href="${ctx}/config/indicators" id="gotoIndicators"><spring:message code="mainMenu.config.columns"/></a><c:if test="${rivConfig.setting.admin1Enabled}"><a 
		href="${ctx}/config/appConfig1" id="gotoAppConfig1">${rivConfig.setting.admin1Title}</a></c:if><c:if test="${rivConfig.setting.admin2Enabled}"><a 
		href="${ctx}/config/appConfig2" id="gotoAppConfig2">${rivConfig.setting.admin2Title}</a></c:if><a
		href="${ctx}/config/import" id="gotoImportSettings"><spring:message code="mainMenu.import"/></a><u>&nbsp;</u></span>
	</div>
</div>