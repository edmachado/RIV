<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="profileToProject.generalLabour"/></title>
	<c:if test="${project.profileUpgrade eq 5}">
		<c:set var="tableSource" value="${project.generalsFromProfile}"/>
		<c:set var="generalSource" value="${project.generals}"/>
		<c:set var="generalSourceName" value="generals"/>
		<c:set var="personnelSource" value="${project.personnels}"/>
		<c:set var="personnelSourceName" value="personnels"/>
		<c:if test="${project.withWithout}">
			<c:set var="generalTitle"><spring:message code="projectGeneralSupplies"/> <spring:message code="project.with"/></c:set>
			<c:set var="personnelTitle"><spring:message code="projectGeneralPersonnel"/> <spring:message code="project.with"/></c:set>
		</c:if>
		<c:if test="${not project.withWithout}">
			<c:set var="generalTitle"><spring:message code="projectGeneralSupplies"/></c:set>
			<c:set var="personnelTitle"><spring:message code="projectGeneralPersonnel"/></c:set>
		</c:if>
	</c:if>
	<c:if test="${project.profileUpgrade eq 6}">
		<c:set var="tableSource" value="${project.generalsFromProfileWithout}"/>
		<c:set var="generalSource" value="${project.generalWithouts}"/>
		<c:set var="generalSourceName" value="generalWithouts"/>
		<c:set var="generalTitle"><spring:message code="projectGeneralSupplies"/> <spring:message code="project.without"/></c:set>
		<c:set var="personnelSource" value="${project.personnelWithouts}"/>
		<c:set var="personnelSourceName" value="personnelWithouts"/>
		<c:set var="personnelTitle"><spring:message code="projectGeneralPersonnel"/> <spring:message code="project.without"/></c:set>
	</c:if>
	<style>
input.invalid { border:1px solid red; }
span.inline_invalid { display:none; } 
#PersonnelTable td:nth-child(2) { text-decoration: line-through; }
#submit { background-color:#cccccc; }
	</style>
	
	<script>
$(document).ready(function() {
	$("input[class='invalid']").each(function() {
    	$(this).attr('title',$(this).next().text());
    });
});
    </script>
    
<c:if test="${fn:length(tableSource) gt 0}">
	<script>
	$(document).ready(function() {
	$("#lab tbody").addClass('connectedSortable');
	$("#GeneralTable tbody").addClass('connectedSortable');
	$("#PersonnelTable tbody").addClass('connectedSortable');
    $( "#lab tbody, #GeneralTable tbody, #PersonnelTable tbody" )
        .sortable({
            connectWith: ".connectedSortable",
            items: "> tr",
            zIndex: 999990,
            cursor: "move",
            beforeStop: function(event, ui) {
            	var tr = ui.item;
            	var table = tr.parent().parent().attr('id');
            	
            	if (table=='lab') {
            		$(this).sortable('cancel');
            	}
            	
            	if ($(tr).find('td:eq(5) select').length==0) {
            		$(tr).find('td:eq(5)').html(
       						'<select name="year"><option value="1" selected="selected">1</option>'+
       					'<c:forEach var="i" begin="2" end="${project.duration}"><option value="${i}">${i}</option></c:forEach>'+
       						'</select>'
       				);
        		}
            	
            	if (table=='GeneralTable') {
	            	if ($(tr).find('td:eq(1) select').length>0) { // remove input fields 
	            		var unitType = $(tr).find('td:eq(1) option:selected').text();
	            		$(tr).find('td:eq(1)').html(unitType);
	            		var unitNum = $(tr).find('td:eq(2) input').val();
            			$(tr).find('td:eq(2)').html(unitNum);
            			var unitCost = $(tr).find('td:eq(3) input').val();
            			$(tr).find('td:eq(3)').html(unitCost);
	//            	} else { // no need to add input fields         		
	            	}
            	} else { // personnel 
            		if ($(tr).find('td:eq(1) select').length==0) { // add input fields
            			var oldType = $(tr).find('td:eq(1)').text().trim();
            			$(tr).find('td:eq(1)').append('<select name="unitType"><option value="0"><spring:message code="units.pyears"/></option><option value="1"><spring:message code="units.pmonths"/></option><option value="2"><spring:message code="units.pweeks"/></option><option value="3"><spring:message code="units.pdays"/></option></select>');
            			$(tr).find('td:eq(1) select > option').each(function() {
            				if (this.text==oldType) {
            					removeText($(tr).find('td:eq(1)'));
            					$(tr).find('td:eq(1) select').val(this.value);
            				}
            			});
            			var unitNum = $(tr).find('td:eq(2)').text().trim();
            			$(tr).find('td:eq(2)').html('<input name="unitNum" class="num" value="'+unitNum+'"/>');
            			var unitCost = $(tr).find('td:eq(3)').text().trim();
            			$(tr).find('td:eq(3)').html('<input name="unitCost" class="num" value="'+unitCost+'" onkeyup="javascript:commasKeyup(this);" />');
            		}
            	}
            },
            update: function(event, ui) {
            	$(ui.item).parent().children("tr.empty").css('display','none');
            },
            stop: function(event, ui) {
            	if ($("#PersonnelTable tbody tr:not(.empty)").length==0) {
            		$("#PersonnelTable tbody tr.empty").css('display','block');
            	}
            	if($("#GeneralTable tbody tr:not(.empty)").length==0) {
            		$("#GeneralTable tbody tr.empty").css('display','block');
            	}
            	if($("#lab tbody tr").length==0) {
            		$("#lab").parent().css('display','none');
            		$("#submit").css('background-color','#000066');
            	}
            }
        })
        .disableSelection()
    ;    
});

function collectAndSend() {
	if ($("#lab tbody tr").length!=0) {
		$( "#dragAll" ).dialog({
			 height: 140,
			 modal: true
		});
		return false;
	}
	
	var general = [];
	var personnel = [];
	
	$("#PersonnelTable tbody tr").each(function() {
		if ($(this).find('td').length>1) {
			personnel.push({
				"description": 	$(this).find('td:eq(0)').text().trim(),
				"unitType" : $(this).find('td:eq(1) option:selected').val(),
				"unitNumJson" : $(this).find('td:eq(2) input').val(),
				"unitCost" : formatToNum($(this).find('td:eq(3) input').val())
			});
		}
	});
	
	personnelData.value=encodeURIComponent(JSON.stringify(personnel));
	
	$("#GeneralTable tbody tr").each(function() {
		if ($(this).find('td').length>1) {
			general.push({
				"description": 	$(this).find('td:eq(0)').text().trim(),
				"unitType" : $(this).find('td:eq(1)').text().trim(),
				"unitNumJson" : $(this).find('td:eq(2)').text().trim(),
				"unitCost" : formatToNum($(this).find('td:eq(3)').text().trim()),
			});
		}
	});
	
	generalData.value=encodeURIComponent(JSON.stringify(general));
}
</script>
</c:if>	
</head>
<body>
<form:form name="form" method="post" modelAttribute="project" >
	<tags:errors />
	
	<tags:profToProj message="profileToProject.generalLabour.text" />
	
	<c:if test="${fn:length(tableSource) gt 0}">
		<input type="hidden" name="generalData" id="generalData" />
		<input type="hidden" name="personnelData" id="personnelData" />
	
		<tags:table>
			<display:table list="${tableSource}" id="lab" requestURI="" cellspacing="0" cellpadding="0"
					 export="false">
				<display:setProperty name="basic.empty.showtable">true</display:setProperty>
				<display:column titleKey="projectInvestLabour.description" property="description" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectInvestLabour.unitType" property="unitType" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectInvestLabour.unitNum">
					<tags:formatDecimal value="${lab.unitNum}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.unitCost">
					<tags:formatCurrency value="${lab.unitCost}"/>
				</display:column>
			</display:table>
		</tags:table>
	</c:if>

	<tags:table title="${generalTitle }">
		<display:table list="${generalSource}" id="sup" requestURI="" cellspacing="0" cellpadding="0"
			 export="false" htmlId="GeneralTable">
			<display:setProperty name="basic.empty.showtable">true</display:setProperty>
			<display:column titleKey="projectGeneralSupplies.description" property="description" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="projectGeneralSupplies.unitType" property="unitType" style="text-align:left;" headerClass="left"/>
			<display:column titleKey="projectGeneralSupplies.unitNum">
				<tags:formatDecimal value="${sup.years[(1).intValue()].unitNum}"/>
			</display:column>
			<display:column titleKey="projectGeneralSupplies.unitCost">
				<tags:formatCurrency value="${sup.unitCost}"/>
			</display:column>
		</display:table>
	</tags:table>

	<tags:table title="${personnelTitle}">
		<display:table list="${personnelSource}" id="per" requestURI="" cellspacing="0" cellpadding="0"
			 export="false" htmlId="PersonnelTable">
			<display:setProperty name="basic.empty.showtable">true</display:setProperty>
			<display:column titleKey="projectGeneralPersonnel.description" property="description" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="projectGeneralPersonnel.unitType" style="text-align:${left};" headerClass="left">
				<tags:formSelectLabour path="${personnelSourceName}[${per_rowNum -1}].unitType" />
			</display:column>
			<display:column titleKey="projectGeneralPersonnel.unitNum" >
				<form:input path="${personnelSourceName}[${per_rowNum -1}].years[0].unitNum" cssErrorClass="invalid" style="text-align:right" size="11" maxLength="11" />
	            <form:errors path="${personnelSourceName}[${per_rowNum -1}].years[0].unitNum" cssClass="inline_invalid" />
			</display:column>
			<display:column titleKey="projectGeneralPersonnel.unitCost">
				<form:input path="${personnelSourceName}[${per_rowNum -1}].unitCost" cssErrorClass="invalid" onkeyup="javascript:commasKeyup(this);" style="text-align:right" size="11" maxLength="11" />
	            <form:errors path="${personnelSourceName}[${per_rowNum -1}].unitCost" cssClass="inline_invalid" />
			</display:column>
		</display:table>
	</tags:table>
	
	<c:choose>
		<c:when test="${project.profileUpgrade eq 5 and project.withWithout and fn:length(project.generalsFromProfileWithout) gt 0}"><c:set var="gotolabel"><spring:message code="misc.goto"/> <spring:message code="project.without"/></c:set></c:when>
		<c:otherwise><c:set var="gotolabel"><spring:message code="misc.goto"/> <spring:message code="project.step9"/></c:set></c:otherwise>
	</c:choose>
	
	<c:if test="${fn:length(tableSource) gt 0}">
		<tags:submit onSubmit="return collectAndSend();">${gotolabel}</tags:submit>
	</c:if>
	<c:if test="${fn:length(tableSource) eq 0}">
		<tags:submit>${gotolabel}</tags:submit>
	</c:if>
</form:form>
<div id="dragAll" style="display:none;" title="<spring:message code="profileToProject.complete"/>">
	<p><spring:message code="profileToProject.complete.text"/></p>
</div>
</body></html>