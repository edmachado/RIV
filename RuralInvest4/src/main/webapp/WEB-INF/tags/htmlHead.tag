<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><meta http-equiv="content-type" content="text/html; charset=utf-8"/>
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
	<script>$(function() { 
		$( document ).tooltip(); 
		$('input.curlabel').focus( function(){ $(this).next('input').focus(); });
	});
// 	$(document).keydown(function(event) { // Ctrl-S and Cmd-S click save button
// 	    if((event.ctrlKey || event.metaKey) && event.which == 83) { if ($('#submit').length) { $('#submit').click(); event.preventDefault(); return false; }
// 	};});
	var curSepThou='${rivConfig.setting.thousandSeparator}';var curSepDec='${rivConfig.setting.decimalSeparator}';var decLength=${rivConfig.setting.decimalLength};
	</script>