<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step2"/></title></head>
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
			<display:table list="${project.donors}" id="row" requestURI="" cellspacing="0" cellpadding="0">
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column titleKey="project.donor.description" headerClass="left" style="text-align:${left};">
					<c:choose>
						<c:when test="${row.notSpecified}"><spring:message code="project.donor.notSpecified"/></c:when>
						<c:when test="${row.contribType eq 4 and row.description eq 'state-public'}"><spring:message code="project.donor.statePublic"/></c:when>
						<c:otherwise>${row.description}</c:otherwise>
					</c:choose>
				</display:column>
				<display:column titleKey="project.donor.type" headerClass="left" style="text-align:${left};">
					<tags:contribType type="${row.contribType}"/>
				</display:column>
				<c:if test="${accessOK}">
					<display:column title="&nbsp;" style="margin-left:5px;" media="html">
						<c:if test="${not row.notSpecified}">
							<a href="../donor/${row.donorId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
						</c:if>
					</display:column>
<%-- 					<display:column title="&nbsp;" media="html"> --%>
<%-- 						<c:if test="${not row.notSpecified}"> --%>
<%-- 							<a href="../donor/${row.donorId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a> --%>
<%-- 						</c:if> --%>
<%-- 					</display:column> --%>
				</c:if>
			</display:table>
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