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
	<sec:csrfMetaTags />
	<script>
	var csrfHeader = $("meta[name='_csrf_header']").attr("content");
	var csrfToken = $("meta[name='_csrf']").attr("content");
	$(function() { 
		$( document ).tooltip(); 
		$('input.curlabel').focus( function(){ $(this).next('input').focus(); });
		$("#confirmDelete").dialog({
			bgiframe: true, autoOpen: false, resizable: false, height:140, modal: true,
			overlay: { backgroundColor: '#000', opacity: 0.5 },
			buttons: {
				Cancel: function() { $(this).dialog('close'); },
				Confirm: {
					text:'<spring:message code="misc.deleteItem"/>',
					id:'confirm-delete-button',
					click: function() { location.href=$('#deleteUrl').val(); }		
				}
			}
		});
	});
	$(document).keydown(function(event) { // Ctrl-S and Cmd-S trigger save button
	    if((event.ctrlKey || event.metaKey) && event.which == 83) { if ($('#submit').length) { $('#submit').click(); event.preventDefault(); return false; }
	};});
	var curSepThou='${rivConfig.setting.thousandSeparator}';var curSepDec='${rivConfig.setting.decimalSeparator}';var decLength=${rivConfig.setting.decimalLength};
	</script>