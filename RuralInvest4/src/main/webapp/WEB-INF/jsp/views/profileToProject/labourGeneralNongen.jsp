<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="profileToProject.generalLabour"/></title>
	<c:set var="tableSource" value="${project.generalsFromProfile}"/>
	<style>
input.invalid { border:1px solid red; }
span.inline_invalid { display:none; } 
#LabourTable td:nth-child(2) { text-decoration: line-through; }
<c:if test="${fn:length(tableSource) gt 0}">
#submit { background-color:#cccccc; }
</c:if>
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
	$("#LabourTable tbody").addClass('connectedSortable');
	$("#InputTable tbody").addClass('connectedSortable');
    $( "#lab tbody, #GeneralTable tbody, #LabourTable tbody, #InputTable tbody" )
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
            	
            	if (table=='GeneralTable' || table=='InputTable') {
	            	if ($(tr).find('td:eq(1) select').length>0) { // remove input fields 
	            		var unitType = $(tr).find('td:eq(1) option:selected').text();
	            		$(tr).find('td:eq(1)').html(unitType);
	            		var unitNum = $(tr).find('td:eq(2) input').val();
            			$(tr).find('td:eq(2)').html(unitNum);
            			var unitCost = $(tr).find('td:eq(3) input').val();
            			$(tr).find('td:eq(3)').html(unitCost);
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
            			$(tr).find('td:eq(3)').html('<input name="unitCost" class="num" value="'+unitCost+'" onkeyup="javascript:commasKeyup(this);" />');
            		}
            	}
            },
            update: function(event, ui) {
            	$(ui.item).parent().children("tr.empty").css('display','none');
            },
            stop: function(event, ui) {
            	if ($("#InputTable tbody tr:not(.empty)").length==0) {
            		$("#InputTable tbody tr.empty").css('display','block');
            	}
            	if ($("#LabourTable tbody tr:not(.empty)").length==0) {
            		$("#LabourTable tbody tr.empty").css('display','block');
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
	
	var input = [];
	var labour = [];
	var general = [];
	
	$("#InputTable tbody tr").each(function() {
		if ($(this).find('td').length>1) {
			input.push({
				"description": 	$(this).find('td:eq(0)').text().trim(),
				"unitType" : $(this).find('td:eq(1)').text().trim(),
				"unitNum" : $(this).find('td:eq(2)').text().trim(),
				"unitCost" : formatToNum($(this).find('td:eq(3)').text().trim()),
			});
		}
	});
	
	inputData.value=encodeURIComponent(JSON.stringify(input));
	
	$("#LabourTable tbody tr").each(function() {
		if ($(this).find('td').length>1) {
			labour.push({
				"description": 	$(this).find('td:eq(0)').text().trim(),
				"unitType" : $(this).find('td:eq(1) option:selected').val(),
				"unitNum" : $(this).find('td:eq(2) input').val(),
				"unitCost" : formatToNum($(this).find('td:eq(3) input').val())
			});
		}
	});
	
	labourData.value=encodeURIComponent(JSON.stringify(labour));
	
	$("#GeneralTable tbody tr").each(function() {
		if ($(this).find('td').length>1) {
			general.push({
				"description": 	$(this).find('td:eq(0)').text().trim(),
				"unitType" : $(this).find('td:eq(1)').text().trim(),
				"unitNum" : $(this).find('td:eq(2)').text().trim(),
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
	
	<tags:profToProj message="profileToProject.generalLabourNongen.text" />
	
	<c:if test="${fn:length(tableSource) gt 0}">
		<input type="hidden" name="generalData" id="generalData" />
		<input type="hidden" name="labourData" id="labourData" />
		<input type="hidden" name="inputData" id="inputData" />
	
		<tags:table>
			<display:table list="${tableSource}" id="lab" requestURI="" cellspacing="0" cellpadding="0" export="false">
				<display:setProperty name="basic.empty.showtable">true</display:setProperty>
				<display:column titleKey="projectNongenInput.description" property="description" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectNongenInput.unitType" property="unitType" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="projectNongenInput.unitNum">
					<tags:formatDecimal value="${lab.unitNum}"/>
				</display:column>
				<display:column titleKey="projectNongenInput.unitCost">
					<tags:formatCurrency value="${lab.unitCost}"/>
				</display:column>
			</display:table>
		</tags:table>
	</c:if>

	<tags:table titleKey="projectNongenInput">
		<display:table list="${project.nongenMaterials}" id="sup" requestURI="" cellspacing="0" cellpadding="0"
			 export="false" htmlId="InputTable">
			<display:setProperty name="basic.empty.showtable">true</display:setProperty>
			<display:column titleKey="projectNongenInput.description" property="description" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="projectNongenInput.unitType" property="unitType" style="text-align:left;" headerClass="left"/>
			<display:column titleKey="projectNongenInput.unitNum">
				<tags:formatDecimal value="${sup.unitNum}"/>
			</display:column>
			<display:column titleKey="projectNongenInput.unitCost">
				<tags:formatCurrency value="${sup.unitCost}"/>
			</display:column>
		</display:table>
	</tags:table>

	<tags:table titleKey="projectNongenLabour">
		<display:table list="${project.nongenLabours}" id="per" requestURI="" cellspacing="0" cellpadding="0"
			 export="false" htmlId="LabourTable">
			<display:setProperty name="basic.empty.showtable">true</display:setProperty>
			<display:column titleKey="projectNongenLabour.description" property="description" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="projectNongenLabour.unitType" style="text-align:${left};" headerClass="left">
				<tags:formSelectLabour path="nongenLabours[${per_rowNum -1}].unitType" />
			</display:column>
			<display:column titleKey="projectNongenLabour.unitNum" >
				<form:input path="nongenLabours[${per_rowNum -1}].unitNum" cssErrorClass="invalid" style="text-align:right" size="11" maxLength="11" />
	            <form:errors path="nongenLabours[${per_rowNum -1}].unitNum" cssClass="inline_invalid" />
			</display:column>
			<display:column titleKey="projectNongenLabour.unitCost">
				<form:input path="nongenLabours[${per_rowNum -1}].unitCost" cssErrorClass="invalid" onkeyup="javascript:commasKeyup(this);" style="text-align:right" size="11" maxLength="11" />
	            <form:errors path="nongenLabours[${per_rowNum -1}].unitCost" cssClass="inline_invalid" />
			</display:column>
		</display:table>
	</tags:table>
	
	<tags:table titleKey="projectNongenGeneral">
		<display:table list="${project.nongenMaintenance}" id="gen" requestURI="" cellspacing="0" cellpadding="0"
			 export="false" htmlId="GeneralTable">
			<display:setProperty name="basic.empty.showtable">true</display:setProperty>
			<display:column titleKey="projectNongenGeneral.description" property="description" style="text-align:${left};" headerClass="left"/>
			<display:column titleKey="projectNongenGeneral.unitType" property="unitType" style="text-align:left;" headerClass="left"/>
			<display:column titleKey="projectNongenGeneral.unitNum">
				<tags:formatDecimal value="${gen.unitNum}"/>
			</display:column>
			<display:column titleKey="projectNongenGeneral.unitCost">
				<tags:formatCurrency value="${gen.unitCost}"/>
			</display:column>
		</display:table>
	</tags:table>
	
	<c:set var="gotolabel"><spring:message code="misc.goto"/> <spring:message code="project.step9"/></c:set>
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