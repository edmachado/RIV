<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step2"/></title>
<script>
$(function() {
	loadDonors();
});
var statePub="<spring:message code='project.donor.statePublic'/>";
var viewEdit="<spring:message code='misc.viewEditItem'/>";
var deleteItem="<spring:message code='misc.deleteItem'/>";
var noDeleteItem="<spring:message code='project.donor.noDelete'/>";
var projId=${project.projectId};
function cType(type) {
	if (type==0) return "<spring:message code='projectContribution.contribType.govtCentral'/>";
	if (type==1) return "<spring:message code='projectContribution.contribType.govtLocal'/>";
	if (type==2) return "<spring:message code='projectContribution.contribType.ngoLocal'/>";
	if (type==3) return "<spring:message code='projectContribution.contribType.ngoIntl'/>";
	if (type==4) return "<spring:message code='projectContribution.contribType.other'/>";
	return "<spring:message code='projectContribution.contribType.beneficiary'/>";
}
</script>
<script language="javascript" src="<c:url value="/scripts/donors.js"/>" type="text/javascript"></script>
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
	
	<tags:textbox field="benefDesc" multiline="true" helpText="project.benefDesc.help" helpTitle="project.benefDesc"
	qualitativeEnabled="${rivConfig.setting.qualBenefDescEnabled}" qualitativeValue="${project.benefDescQualitative}" qualitativeField="justificationQualitative">
		iv. <spring:message code="project.benefDesc"/></tags:textbox>
	
	
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
				<tbody id="dtbody"></tbody>
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