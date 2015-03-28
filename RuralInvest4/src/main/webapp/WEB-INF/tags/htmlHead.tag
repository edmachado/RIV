<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><meta http-equiv="content-type" content="text/html; charset=utf-8"/><meta http-equiv="X-UA-Compatible" content="IE=edge;chrome=1" />
	<link rel="SHORTCUT ICON" href="<c:url value="/img/riv.ico"/>"/>
	<script language="javascript" src="<c:url value="/scripts/general.js"/>" type="text/javascript"></script>
	<script language="javascript" src="<c:url value="/scripts/jquery-1.9.1.js"/>" type="text/javascript"></script>
	<script language="javascript" src="<c:url value="/scripts/jquery-ui-1.10.3.custom.min.js"/>" type="text/javascript"></script>
	<link href="<c:url value="/styles/riv-theme/jquery-ui-1.10.3.custom.css"/>" rel="stylesheet"/>
	<c:if test="${lang ne 'ar'}"><link rel="stylesheet" type="text/css" href="<c:url value="/styles/style.css"/>" media="screen"/></c:if>
	<c:if test="${lang eq 'ar'}"><link rel="stylesheet" type="text/css" href="<c:url value="/styles/style_rtl.css"/>" media="screen"/></c:if>
	<!--[if lt IE 9]>
	<link rel="stylesheet" type="text/css" href="<c:url value="/styles/style-ie8.css"/>" />
	<![endif]-->
	<script>$(function() { $( document ).tooltip(); });
	var curSepThou='${rivConfig.setting.thousandSeparator}';var curSepDec='${rivConfig.setting.decimalSeparator}';var decLength=${rivConfig.setting.decimalLength};
	</script>