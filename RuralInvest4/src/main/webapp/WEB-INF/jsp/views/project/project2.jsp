<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step2"/></title></head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />
	
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
	
	
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step3"/></tags:submit>
	</form:form>
	<SCRIPT LANGUAGE="JavaScript">
	<!--
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
	-->
</SCRIPT>
</body></html>