<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step2"/></title>
 <script>
$(function() {
	$.getJSON("../../home/${project.projectId}/donors.json", function(data) {
		 var items = [];
		 var table=document.getElementById('#donorTable');
		 $.each(data, function(key) {
			 tr = $('<tr/>');
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
			 ct=cType(data[key].contribType);
			 td2.text(ct).html();
			 $(tr).append(td2);
			 // edit link
			 td3 = $('<td/>');
			 if (!data[key].notSpecified) {
				 a = addLink('../donor/'+data[key].donorId,'../../img/edit.png','<spring:message code="misc.viewEditItem"/>');
				 $(td3).append(a);
			 }
			 $(tr).append(td3);
			 
			 td4 = $('<td/>');
			 if (!data[key].notSpecified) {
				 d = addLink('#','../../img/delete.gif','<spring:message code="misc.deleteItem"/>');
				 $(td4).append(d);
			 }
			 $(tr).append(td4);
			 
			 $('#foo').append(tr);
			 
		 });
	});
});
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
				<thead>
					<th style="text-align:left"><spring:message code="project.donor.description"/></th>
					<th style="text-align:left"><spring:message code="project.donor.type"/></th>
					<th></th><th></th>
				</thead>
				<tbody id="foo"/>
			</table>
		</tags:table>
		<c:if test="${accessOK}">
			<div class="addNew"><a id="newDonor" href="../donor/-1?projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
		</c:if>
	</fieldset>
	
	<tags:submit><spring:message code="misc.goto"/>
		<c:if test="${empty quickAnalysis}"><spring:message code="project.step3"/></c:if>
		<c:if test="${not empty quickAnalysis}"><spring:message code="project.step7"/></c:if>
	</tags:submit>
	</form:form>
	<SCRIPT LANGUAGE="JavaScript">
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
</SCRIPT>
</body></html>