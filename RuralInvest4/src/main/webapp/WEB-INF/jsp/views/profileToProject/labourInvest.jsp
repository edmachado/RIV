<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="profileToProject.investLabour"/></title>
	<c:if test="${project.profileUpgrade eq 3}">
		<c:set var="tableSource" value="${project.laboursFromProfile}"/>
		<c:set var="labourSource" value="${project.labours}"/>
		<c:set var="labourSourceName" value="labours"/>
		<c:set var="serviceSource" value="${project.services}"/>
		<c:set var="serviceSourceName" value="services"/>
		<c:if test="${project.withWithout}">
			<c:set var="labourTitle"><spring:message code="projectInvestLabour"/> <spring:message code="project.with"/></c:set>
			<c:set var="serviceTitle"><spring:message code="projectInvestService"/> <spring:message code="project.with"/></c:set>
		</c:if>
		<c:if test="${not project.withWithout}">
			<c:set var="labourTitle"><spring:message code="projectInvestLabour"/></c:set>
			<c:set var="serviceTitle"><spring:message code="projectInvestService"/></c:set>
		</c:if>
	</c:if>
	<c:if test="${project.profileUpgrade eq 4}">
		<c:set var="tableSource" value="${project.laboursFromProfileWithout}"/>
		<c:set var="labourSource" value="${project.laboursWithout}"/>
		<c:set var="labourSourceName" value="laboursWithout"/>
		<c:set var="labourTitle"><spring:message code="projectInvestLabour"/> <spring:message code="project.without"/></c:set>
		<c:set var="serviceSource" value="${project.servicesWithout}"/>
		<c:set var="serviceSourceName" value="servicesWithout"/>
		<c:set var="serviceTitle"><spring:message code="projectInvestService"/> <spring:message code="project.without"/></c:set>
	</c:if>
	<style>
input.invalid { border:1px solid red; }
span.inline_invalid { display:none; } 
#LabourTable td:nth-child(2) { text-decoration: line-through; }
<c:if test="${fn:length(tableSource) gt 0}">
#submit { background-color:#cccccc; }
</c:if>
/* #submit.notYet { background-color: red; } */
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
	$("#LabourTable tbody").addClass('connectedSortable');
	$("#ServicesTable tbody").addClass('connectedSortable');
    $( "#lab tbody, #LabourTable tbody, #ServicesTable tbody" )
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
            	
            	if (table=='ServicesTable') {
	            	if ($(tr).find('td:eq(1) select').length>0) { // remove input fields 
	            		var unitType = $(tr).find('td:eq(1) option:selected').text();
	            		$(tr).find('td:eq(1)').html(unitType);
	            		var unitNum = $(tr).find('td:eq(2) input').val();
            			$(tr).find('td:eq(2)').html(unitNum);
            			var unitCost = $(tr).find('td:eq(3) input').val();
            			$(tr).find('td:eq(3)').html(unitCost);
            			var own = $(tr).find('td:eq(4) input').val();
            			$(tr).find('td:eq(4)').html(own);	
	//            	} else { // no need to add input fields         		
	            	}
            	} else { // labour 
            		if ($(tr).find('td:eq(1) select').length==0) { // add input fields
            			var oldType = $(tr).find('td:eq(1)').text().trim();
            			$(tr).find('td:eq(1)').append('<select name="unitType"><option value="0"><spring:message code="units.pyears"/></option><option value="1"><spring:message code="units.pmonths"/></option><option value="2"><spring:message code="units.pweeks"/></option><option value="3"><spring:message code="units.pdays"/></option></select>');
            			$(tr).find('td:eq(1) select > option').each(function() {
            				if (this.text==oldType) {
            					removeText($(tr).find('td:eq(1)'));
            					$(tr).find('td:eq(1) select').val(this.value);
            					$(tr).find('td:eq(1)').css('text-align','left');
            				}
            			});
            			
            			var unitNum = $(tr).find('td:eq(2)').text().trim();
            			$(tr).find('td:eq(2)').html('<input name="unitNum" class="num" value="'+unitNum+'"/>');
            			var unitCost = $(tr).find('td:eq(3)').text().trim();
            			$(tr).find('td:eq(3)').html('<input name="unitCost" class="num" value="'+unitCost+'" onkeyup="javascript:commasKeyup(this);"/>');
            			var own = $(tr).find('td:eq(4)').text().trim();
            			$(tr).find('td:eq(4)').html('<input name="ownResources" class="num" value="'+own+'" onkeyup="javascript:commasKeyup(this);"/>');
            		}
            	}
            },
            update: function(event, ui) {
            	$(ui.item).parent().children("tr.empty").css('display','none');
            },
            stop: function(event, ui) {
            	if ($("#LabourTable tbody tr:not(.empty)").length==0) {
            		$("#LabourTable tbody tr.empty").css('display','block');
            	}
            	if($("#ServicesTable tbody tr:not(.empty)").length==0) {
            		$("#ServicesTable tbody tr.empty").css('display','block');
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
	
	var labours = [];
	var services = [];
	
	$("#LabourTable tbody tr").each(function() {
		if ($(this).find('td').length>1) {
			labours.push({
				"description": 	$(this).find('td:eq(0)').text().trim(),
				"unitType" : $(this).find('td:eq(1) option:selected').val(),
				"unitNum" :  $(this).find('td:eq(2) input').val(),
				"unitCost" : formatToNum($(this).find('td:eq(3) input').val()),
				"ownResources" : formatToNum($(this).find('td:eq(4) input').val()),
				"yearBegin" : $(this).find('td:eq(5) option:selected').val()
			});
		}
	});
	labourData.value=encodeURIComponent(JSON.stringify(labours));
	
	$("#ServicesTable tbody tr").each(function() {
		if ($(this).find('td').length>1) {
			services.push({
				"description": 	$(this).find('td:eq(0)').text().trim(),
				"unitType" : $(this).find('td:eq(1)').text().trim(),
				"unitNum" :  $(this).find('td:eq(2)').text().trim(),
				"unitCost" : formatToNum($(this).find('td:eq(3)').text().trim()),
				"ownResources" : formatToNum($(this).find('td:eq(4)').text().trim()),
				"yearBegin" : $(this).find('td:eq(5) option:selected').val()
			});
		}
	});
	
	serviceData.value=encodeURIComponent(JSON.stringify(services));
}
		</script>
	</c:if>
</head>
<body>
<form:form name="form" method="post" modelAttribute="project">
	<tags:errors />
	
	<tags:profToProj message="profileToProject.investLabour.text" />
	
	<c:if test="${fn:length(tableSource) gt 0}">
		<input type="hidden" name="labourData" id="labourData" />
		<input type="hidden" name="serviceData" id="serviceData" />
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
				<display:column titleKey="projectInvestLabour.ownResources">
					<tags:formatCurrency value="${lab.ownResources}"/>
				</display:column>
				<display:column titleKey="projectInvestLabour.yearBegin" property="yearBegin"  style="text-align:center;" headerClass="centered"/>
			</display:table>
		</tags:table>
	</c:if>
	
	<tags:table title="${labourTitle}">
		<display:table list="${labourSource}" id="lab" requestURI="" cellspacing="0" cellpadding="0"
			 export="false" htmlId="LabourTable">
			<display:setProperty name="basic.empty.showtable">true</display:setProperty>
			<display:column titleKey="projectInvestLabour.description" property="description" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="projectInvestLabour.unitType" sortable="true" style="text-align:${left};" headerClass="left">
				<tags:formSelectLabour path="${labourSourceName}[${lab_rowNum -1}].unitType" />
			</display:column>
			<display:column titleKey="projectInvestLabour.unitNum">
				<form:input path="${labourSourceName}[${lab_rowNum -1}].unitNum" cssErrorClass="invalid" style="text-align:right" size="11" maxLength="11" />
	            <form:errors path="${labourSourceName}[${lab_rowNum -1}].unitNum" cssClass="inline_invalid" />
			</display:column>
			<display:column titleKey="projectInvestLabour.unitCost">
				<form:input path="${labourSourceName}[${lab_rowNum -1}].unitCost" cssErrorClass="invalid" onkeyup="javascript:commasKeyup(this);" style="text-align:right" size="11" maxLength="11" />
	            <form:errors path="${labourSourceName}[${lab_rowNum -1}].unitCost" cssClass="inline_invalid" />
			</display:column>
			<display:column titleKey="projectInvestLabour.ownResources">
				<form:input path="${labourSourceName}[${lab_rowNum -1}].ownResources" cssErrorClass="invalid" onkeyup="javascript:commasKeyup(this);" style="text-align:right" size="11" maxLength="11" />
	            <form:errors path="${labourSourceName}[${lab_rowNum -1}].ownResources" cssClass="inline_invalid" />
			</display:column>
			<display:column titleKey="projectInvestLabour.yearBegin" style="text-align:center;" headerClass="centered">
				<form:select path="${labourSourceName}[${lab_rowNum -1}].yearBegin" >
					<c:forEach var="i" begin="1" end="${project.duration}">
						<form:option value="${i}" label="${i}"/>
					</c:forEach>
				</form:select>
			</display:column>
			
		</display:table>
	</tags:table>
	
	<tags:table title="${serviceTitle}">
		<display:table list="${serviceSource}" id="serv" requestURI="" cellspacing="0" cellpadding="0"
			 export="false" htmlId="ServicesTable">
			<display:setProperty name="basic.empty.showtable">true</display:setProperty>
			<display:column titleKey="projectInvestService.description" property="description" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="projectInvestService.unitType" property="unitType" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="projectInvestService.unitNum" sortProperty="unitNum" >
				<tags:formatDecimal value="${serv.unitNum}"/>
			</display:column>
			<display:column titleKey="projectInvestService.unitCost">
				<tags:formatCurrency value="${serv.unitCost}"/>
			</display:column>
			<display:column titleKey="projectInvestService.ownResources">
				<tags:formatCurrency value="${serv.ownResources}"/>
			</display:column>
			<display:column titleKey="projectInvestService.yearBegin" style="text-align:center;" headerClass="centered">
				<form:select path="${serviceSourceName}[${serv_rowNum -1}].yearBegin" >
					<c:forEach var="i" begin="1" end="${project.duration}">
						<form:option value="${i}" label="${i}"/>
					</c:forEach>
				</form:select>
			</display:column>
		</display:table>
	</tags:table>
	
	<c:choose>
		<c:when test="${project.profileUpgrade eq 3 and project.withWithout and fn:length(project.laboursFromProfileWithout) >0}">
			<c:set var="gotolabel">
				<spring:message code="misc.goto"/> <spring:message code="project.without"/>
			</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="gotolabel"><spring:message code="misc.goto"/> <spring:message code="profileToProject.generalLabour"/></c:set>
		</c:otherwise> 
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