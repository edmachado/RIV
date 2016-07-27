<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step8"/></title>
<script>
$(function() {
	$('#yearByYear').buttonset();
	$('#form input').on('change', function() {
		   if($('input[name=simpleApproach]:checked', '#form').val()=='true') {
			   // simple selected
			   $('#confirmSimple').dialog('open');
		   } else {
			   location.href='../step8/${project.projectId}/perYearGenerals?simple=false';
		   }
		});
	$('#confirmSimple').dialog({
		bgiframe: true, autoOpen: false, resizable: false, height:200, width:360, modal: true,
		overlay: { backgroundColor: '#000', opacity: 0.5 },
		buttons: {
			Cancel: function() { $(this).dialog('close'); },
			'<spring:message code="misc.confirm"/>': function() {
				location.href='../step8/${project.projectId}/perYearGenerals?simple=true';
			}		
		}
	});
});
var selectedYear=0;
</script>

</head>
<body>
<form:form name="form" id="form" method="post" commandName="project">
	<tags:errors />
	
	<div align="left">
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadTemplate" href="../../report/${project.projectId}/projectGeneralDetail.xlsx?template=true" target="_blank"><spring:message code="export.downloadTemplate"/></a><br/>
		<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadExcel" href="../../report/${project.projectId}/projectGeneralDetail.xlsx" target="_blank"><spring:message code="export.download"/></a><br/>
	 	<c:if test="${accessOK}"><a id="importExcel" href="#"><img src="../../img/xls.gif" alt="Excel" title="Excel"/> <spring:message code="import.importExcel"/></a></c:if>
 	</div>
	<c:set var="withTableTitle">
		<c:if test="${project.withWithout}">projectGeneral.with</c:if>
		<c:if test="${not project.withWithout}">projectGeneral</c:if>
	</c:set>
	
	<br/>
	<form:errors path="generals" cssClass="error" element="div" />
	<form:errors path="personnels" cssClass="error" element="div" />
		
 	<c:if test="${accessOK}">
	 	<div id="yearByYear" style="margin:10px 5px;">
			<input type="radio" name="simpleApproach" id="simplified" value="true" <c:if test="${not project.perYearGeneralCosts}">checked="checked"</c:if>><label for="simplified" title='<spring:message code="project.general.method.simplified.help"/>'><spring:message code="project.general.method.simplified"/></label>
			<input type="radio" name="simpleApproach" id="perYear" value="false" <c:if test="${project.perYearGeneralCosts}">checked="checked"</c:if>><label for="perYear" title='<spring:message code="project.general.method.perYear.help"/>'><spring:message code="project.general.method.perYear"/></label>
		</div>
	</c:if>
	<b>
		<c:if test="${not project.perYearGeneralCosts}"><spring:message code="project.general.method.simplified.help"/></c:if>
		<c:if test="${project.perYearGeneralCosts}"><spring:message code="project.general.method.perYear.help"/></c:if>
 	</b>
	
	
	<br/>
	<style>
	span.yearBox { display:inline-block; border:1px solid #b5b6b5; margin:10px 4px 0 4px; width:25px; height:25px; background-color:#efce6c; text-align:center; }
 	span.selected { font-weight:bold; border:2px solid #036;}
 	span.yearBox a { display:block; margin:5px 0 0 0; }
 	span.yearBox:hover { background-color:#3a487c; }
	span.yearBox:hover a { color:#efce6c; }
 	span.selected:hover,yearBox:hover { background-color:#efce6c; }
 	span.selected a { cursor:default; }
 	span.selected a:hover { color:#036; }
	</style>
	<script>
		function showYear(year) {
			toggle('projectGeneralSupplies'+year);
			toggle('projectGeneralPersonnel'+year);
			toggle('projectGeneralSupplies'+selectedYear);
			toggle('projectGeneralPersonnel'+selectedYear);

			$('#yearBox'+selectedYear).toggleClass('selected',false);
			selectedYear=year;
			$('#yearBox'+year).toggleClass('selected',true);
		}
	
	</script>
	
	<c:if test="${project.perYearGeneralCosts}">
		<c:forEach var="year" begin="0" end="${project.duration-1}">
			<span id="yearBox${year}" class="yearBox<c:if test='${year eq 0}'> selected</c:if>">
				<a href="#" onclick="javascript:showYear(${year});">${year+1}</a>
			</span>
		</c:forEach>
		<br/>
	</c:if>
	
	<tags:tableContainer titleKey="${withTableTitle}">
		<tags:generalCosts costs="${generalsForTable}" type="projectGeneralSupplies" duration="${project.duration}" perYear="${project.perYearGeneralCosts}" />
		<tags:generalCosts costs="${personnelsForTable}" type="projectGeneralPersonnel" duration="${project.duration}" perYear="${project.perYearGeneralCosts}" />
	</tags:tableContainer>
	
	<c:if test="${project.withWithout}">
		<tags:tableContainer titleKey="projectGeneral.without">
			<tags:generalCosts costs="${generalsWoForTable}" type="projectGeneralSupplies" without="true" duration="${project.duration}" perYear="${project.perYearGeneralCosts}" />
			<tags:generalCosts costs="${personnelsWoForTable}" type="projectGeneralPersonnel" without="true"  duration="${project.duration}" perYear="${project.perYearGeneralCosts}" />
		</tags:tableContainer>
	</c:if>

	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step9"/></tags:submit>
</form:form>
<div id="confirmSimple" title='<spring:message code="misc.confirm"/>'>
	<span class="ui-icon ui-icon-alert" style="display:inline-block"></span> <spring:message code="project.general.method.confirm"/> 
</div>
<tags:excelImport submitUrl="../../import/project/general/${project.projectId}"/>
</body></html>
