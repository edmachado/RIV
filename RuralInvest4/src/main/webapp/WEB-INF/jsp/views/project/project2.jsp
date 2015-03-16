<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step2"/></title>
<script>
$(function() {
	loadDonors();
});
function deleteDonor(donorId) {
	$.ajax({ url: '../donor/'+donorId+'/delete', type:"GET", dataType: 'json',
		success: function( data, textStatus, jqXHR ) {
			$('#donorTable tbody').empty();
			loadDonors();
		 },
		 error: function(jqXHR, status, error) {
			 alert('error: '+obj.message);
		 }
	});
}
function editDonor() {
	url = '../donor/'+$('#donor-id').val();
	if ($('#donor-id').val()==-1) { url = url+'?projectId=${project.projectId}'; } 
	$.ajax({ url: url, type:"POST", dataType: 'json',
		data: { description: $('#donor-description').val(), contribType:$('#donor-contribType').val() },
		 success: function( data, textStatus, jqXHR ) {
			$('#donorTable tbody').empty();
			loadDonors();
			$( "#dialog-donor" ).dialog("close");
			$('#donor-description').val('');
			$('#donor-contribType').val([]);
		 },
		 error: function(jqXHR, status, error) {
			 $('#donorAlert').show();
			 $.each(JSON.parse(jqXHR.responseText), function(idx, obj) {
				label=$('label[for='+obj.field+']').text();
				$('#donor-error-field').text(label);
				$('#donor-error-message').text(obj.message);
			});
		 }
	});
}
function loadDonors() {
	$.getJSON("../../home/${project.projectId}/donors.json", function(data) {
		 var items = [];
		 var table=document.getElementById('#donorTable');
		 $.each(data, function(key) {
			 tr = $('<tr/>');
			 $(tr).attr('id','donor'+data[key].orderBy);
			 $(tr).attr('donorId',data[key].donorId);
			 if (data[key].orderBy % 2 == 0) {
				 $(tr).attr('class','odd');
			 }
			 // description
			 td1 = $('<td/>');
			 $(td1).attr('class','left');
			 if (data[key].notSpecified) {
				 text='<spring:message code="project.donor.notSpecified"/>';
			 } else if (data[key].contribType==4 && data[key].description=='state-public') {
				 text='<spring:message code="project.donor.statePublic"/>';
			 } else {
				 text=data[key].description;
			 }
			 td1.text(text).html();
			 $(tr).append(td1);
			 
			 // contrib type
			 td2 = $('<td/>');
			 $(td2).attr('class','left');
			 $(td2).attr('code',data[key].contribType);
			 ct=cType(data[key].contribType);
			 td2.text(ct).html();
			 $(tr).append(td2);
			 
			 // edit link
			 td3 = $('<td/>');
			 if (!data[key].notSpecified) {
				 a = addLink('javascript:showme('+data[key].orderBy+');','../../img/edit.png','<spring:message code="misc.viewEditItem"/>');
				 $(td3).append(a);
			 }
			 $(tr).append(td3);
			 
			 td4 = $('<td/>');
			 if (!data[key].notSpecified &! data[key].inUse) {
				 d = addLink('javascript:deleteDonor('+data[key].donorId+')','../../img/delete.gif','<spring:message code="misc.deleteItem"/>');
				 $(td4).append(d);
			 }
			 $(tr).append(td4);
			 
			 $('#donorTable tbody').append(tr);
			 
		 });
	});
}
function showme(order) {
	if (order==-1) {
		$('#donor-id').val('-1');
		$('#donor-description').val('');
		$('#donor-contribType').val(0);
	} else {
		$('#donor-id').val($('#donor'+order).attr('donorId'));
		$('#donor-description').val($('#donor'+order+' td:first-child').text());
		$('#donor-contribType').val($('#donor'+order+' td:nth-child(2)').attr('code'));
	}
	$('#donorAlert').hide();
	$( "#dialog-donor" ).dialog({
		 height: 210,
		 width:500,
		 modal: true
	});
}
function cType(type) {
	if (type==0) return '<spring:message code="projectContribution.contribType.govtCentral"/>';
	if (type==1) return '<spring:message code="projectContribution.contribType.govtLocal"/>';
	if (type==2) return '<spring:message code="projectContribution.contribType.ngoLocal"/>';
	if (type==3) return '<spring:message code="projectContribution.contribType.ngoIntl"/>';
	if (type==4) return '<spring:message code="projectContribution.contribType.other"/>';
	return '<spring:message code="projectContribution.contribType.beneficiary"/>';
}
function addLink(href,img,alt) {
	a = $('<a/>');
	 $(a).attr('href',href);
	 edit = $('<img/>');
	 $(edit).attr('src',img);
	 $(edit).attr('alt',alt);
	 $(edit).attr('width','16');
	 $(edit).attr('height','16');
	 $(edit).attr('border','0');
	 $(a).append(edit);
	 return a;
}
function Calculate() {
	with (Math) {
		var dirMen = (document.form.beneDirectMen.value);
		var dirWomen = (document.form.beneDirectWomen.value);
		var dirChild = (document.form.beneDirectChild.value);
		
		var inMen = (document.form.beneIndirectMen.value);
		var inWomen = (document.form.beneIndirectWomen.value);
		var inChild = (document.form.beneIndirectChild.value);
	
		var totalDirect = round(1*dirMen + 1*dirWomen + 1*dirChild);
		var totalIndirect =  round(1*inMen+1*inWomen+1*inChild);
	}
	
	document.form.beneDirectTotal.value = totalDirect;
	document.form.beneIndirectTotal.value = totalIndirect;
}
</script>
</head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />
	
	<c:if test="${not empty quickAnalysis}">
	<input type="hidden" name="quickAnalysis" value="true">
	</c:if>
	
	<tags:textbox field="benefName" helpText="project.benefName.help" helpTitle="project.benefName">i. <spring:message code="project.benefName"/></tags:textbox>
	
	<fieldset style="display:inline-block;width:47%;margin-right:10px;">
   		<legend>
   			<tags:help title="project.step2.2" text="project.step2.2.help">ii. <spring:message code="project.step2.2"/></tags:help>
        </legend>
		<tags:dataentry field="beneDirectMen" labelKey="project.benefMen" onmouseout="Calculate()"/>
     	<tags:dataentry field="beneDirectWomen" labelKey="project.benefWomen" onmouseout="Calculate()"/>
     	<tags:dataentry field="beneDirectChild" labelKey="project.benefChild" onmouseout="Calculate()"/>
     	<tags:datadivider color="green"/>
     	<tags:dataentry field="beneDirectTotal" labelKey="project.benefTotal" calculated="true" />
     	<tags:dataentry field="beneDirectNum" labelKey="project.benefFamilies"/>
   </fieldset>
	<fieldset style="display:inline-block;width:47%">
		<legend>
			<tags:help title="project.step2.3" text="project.step2.3.help" >iii. <spring:message code="project.step2.3"/></tags:help>
		 </legend>
		<tags:dataentry field="beneIndirectMen" labelKey="project.benefMen" onmouseout="Calculate()"/>
		<tags:dataentry field="beneIndirectWomen" labelKey="project.benefWomen" onmouseout="Calculate()"/>
		<tags:dataentry field="beneIndirectChild" labelKey="project.benefChild" onmouseout="Calculate()"/>
		<tags:datadivider color="green"/>
		<tags:dataentry field="beneIndirectTotal" labelKey="project.benefTotal" calculated="true" />		                          		
		<tags:dataentry field="beneIndirectNum" labelKey="project.benefFamilies"/>
	</fieldset>	
	
	<tags:textbox field="benefDesc" multiline="true" helpText="project.benefDesc.help" helpTitle="project.benefDesc">iv. <spring:message code="project.benefDesc"/></tags:textbox>
	
	
	<fieldset style="display:inline-block;width:47%;padding-left:10px">
		<legend>
			<tags:help title="project.donors" text="project.donors.help" >v. <spring:message code="project.donors"/></tags:help>
		 </legend>

		<tags:table>
			<table id="donorTable" cellspacing="0" cellpadding="0">
				<thead><tr>
					<th class="left"><spring:message code="project.donor.description"/></th>
					<th class="left"><spring:message code="project.donor.type"/></th>
					<th></th><th></th>
				</tr></thead>
				<tbody></tbody>
			</table>
		</tags:table>
		<c:if test="${accessOK}">
			<div class="addNew"><a id="newDonor" href="javascript:showme(-1);"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
		</c:if>
	</fieldset>
	
	<tags:submit><spring:message code="misc.goto"/>
		<c:if test="${empty quickAnalysis}"><spring:message code="project.step3"/></c:if>
		<c:if test="${not empty quickAnalysis}"><spring:message code="project.step7"/></c:if>
	</tags:submit>
	
	<div id="dialog-donor" style="background-color:#f5f5f5;display:none;" title="<spring:message code='misc.addItem'/>">
		<div id="donorAlert" style="display:none;" class="ui-widget">
		    <div class="ui-state-error ui-corner-all" style="padding: 0 .7em;"> 
		        <p>
		            <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
		            <strong><span id="donor-error-field"></span></strong>: <span id="donor-error-message"></span>
		        </p>
		    </div>
		</div>
		<fieldset><input type="hidden" id="donor-id"/>
			<legend><spring:message code="misc.addItem"/> (<spring:message code="project.donor.description"/>)</legend>
			<div class="dataentry">
				<label for="donor-description">
					<tags:help title="project.donor.description" text="project.donor.description.help"><spring:message code="project.donor.description"/></tags:help>
				</label>
				<input tabindex="1" id="donor-description" name="donor-description" class="text" maxLength="150" type="text" value="" size="20"/>
			</div>
			<div class="dataentry">
				<label>
					<tags:help title="project.donor.type" text="project.donor.type.help"><spring:message code="project.donor.type"/></tags:help>
				</label>
				<select name="donor-contribType" id="donor-contribType" tabindex="2">
					<option value="0"><spring:message code="projectContribution.contribType.govtCentral"/></option>
					<option value="1"><spring:message code="projectContribution.contribType.govtLocal"/></option>
					<option value="2"><spring:message code="projectContribution.contribType.ngoLocal"/></option>
					<option value="3"><spring:message code="projectContribution.contribType.ngoIntl"/></option>
					<option value="5"><spring:message code="projectContribution.contribType.beneficiary"/></option>
					<option value="4"><spring:message code="projectContribution.contribType.other"/></option>
				</select>
			</div>
		</fieldset>
		<div align="right"><button id="saveDonor" tabindex="3" class="submit" onclick="javascript:editDonor();"><spring:message code="misc.saveItem"/></button></div>
	</div>
	</form:form>
</body></html>