<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="profile.step1"/></title>
 <script>
$(function() {
$( "#radioWithWithout" ).buttonset();
$( "#radioShared" ).buttonset();
});
</script>
</head>
<body>
<form:form name="form" method="post" commandName="profile">
	<tags:errors />
	
	<div style="text-align:right;width:100%">
		<tags:help text="profile.clone.help" title="profile.clone">
			<a id="clone" href="${profile.profileId}/clone"><spring:message code="profile.clone"/> <img src="../../img/duplicate.gif" border="0"/></a>
		</tags:help>&nbsp;
		<a id="upgrade" href="${profile.profileId}/upgrade"><spring:message code="profile.upgrade"/> <img src="../../img/upgrade.gif" border="0"/></a>&nbsp;
	</div>
		
	<div> <!-- container div -->
		<div style="float:left;margin-right:12px;"> <!-- left div -->

		<fieldset>
			<legend>i. <spring:message code="profile.step1.1"/></legend>
				<c:if test="${empty param['rename']}">
					<tags:dataentry field="profileName" labelKey="profile.profileName" size="44" inputClass="text" maxLength="100" />
				</c:if>
				<c:if test="${not empty param['rename']}">
					<div class="dataentry">
						<span class="helpSpacer"></span>
						<label><spring:message code="profile.profileName"/></label>
						<div style="display:inline-block;">
							${profile.profileName}<br/>
							<form:input path="profileName" cssClass="text" size="44" maxlength="100" />
						</div>
						<span style="display:inline-block;margin-left:15px;"><img vspace="4" align="left" src="../../img/locked.gif"></span>
					</div>
				</c:if>
				
				<c:set var="exchRateCalcSign">${rivConfig.setting.currencyName}&nbsp;<spring:message code="units.perUSD"/></c:set>   
				<tags:dataentry field="exchRate" labelKey="profile.exchRate" size="8" inputClass="text" calcSign="${exchRateCalcSign}"	helpText="profile.exchRate.help" />				
				<div class="dataentry">
					<label><tags:help text="profile.createdBy.help"><spring:message code="profile.creationDate"/></tags:help></label>
				 	<fmt:formatDate value="${profile.prepDate}" type="both" pattern="dd/MM/yy HH:mm" />
				</div>
				<div class="dataentry">
					<label><spring:message code="profile.lastUpdate"/></label>
				 	<fmt:formatDate value="${profile.lastUpdate}" type="both" pattern="dd/MM/yy HH:mm" />
				</div>
				<tags:dataentry field="benefNum" labelKey="profile.benefNum" helpText="profile.benefNum.help" helpTitle="profile.benefNum" size="8" inputClass="text" calcSignKey="units.people" />
				<tags:dataentry field="benefFamilies" labelKey="profile.benefFamilies" inputClass="text" size="8" />
                <c:if test="${profile.incomeGen}">
                	<input name="oldWithWithout" type="hidden" value="${profile.withWithout}"/>
					<div class="dataentry">
						<b><spring:message code="profile.withWithoutMessage"/></b><br/>
						<div id="radioWithWithout">
							<form:radiobutton path="withWithout" id="yes" value="true" />
							<form:label path="withWithout" for="yes"><spring:message code="misc.yes"/></form:label>
							<form:radiobutton path="withWithout" id="no" value="false" />
							<form:label path="withWithout" for="no"><spring:message code="misc.no"/></form:label>
						</div>
					</div>
					<c:if test="${profile.withWithout && (profile.wizardStep==null || profile.wizardStep gt 6)}"><script type="text/javascript">
					$("input[name='withWithout']").change(function() {
						if ($(this).val()=='false') {
							 $( "#dialog-noWithWithout" ).dialog({
								 height: 140,
								 modal: true
							});
						}	
					});
					</script></c:if>
				</c:if>
			</fieldset>
			
			<fieldset>
                <legend>ii. <spring:message code="profile.step1.2"/></legend>
				<div class="dataentry">
					<label><spring:message code="profile.fieldOffice"/></label>
					<form:select path="fieldOffice" id="fieldOffice">
						<form:options items="${fieldOfficeValues}" itemValue="configId" itemLabel="description" />
					</form:select>
				</div>
				<tags:dataentry field="location1" label="${rivConfig.setting.location1}" inputClass="text" size="20" maxLength="50" />
				<tags:dataentry field="location2" label="${rivConfig.setting.location2}" inputClass="text" size="20" maxLength="50" />
				<tags:dataentry field="location3" label="${rivConfig.setting.location3}" inputClass="text" size="20" maxLength="50" />
			</fieldset>
		</div> <!-- end left div -->
		<div class="float:right"> <!-- right div -->
			<fieldset>
	 			<legend>
	 				<tags:help title="profile.status" text="profile.status.help">iii. <spring:message code="profile.status"/></tags:help>
				</legend>
				<div class="dataentry">
					<label><spring:message code="profile.status"/></label>
					<form:select path="status" id="status">
						<form:options items="${statusValues}" itemValue="configId" itemLabel="description" />
					</form:select>
				</div>
			</fieldset>
			<fieldset>
  				<legend>
  					<tags:help title="profile.step1.3" text="profile.step1.3.help">iv. <spring:message code="profile.step1.3"/></tags:help>
  				</legend>
			    <div class="dataentry">
			    	<label><spring:message code="user.name"/></label>
			    	${profile.technician.description}
			    </div>
			    <div class="dataentry">
			    	<label><spring:message code="user.organization"/></label>
			    	${profile.technician.organization}
			    </div>
			    <div class="dataentry">
			    	<label><spring:message code="user.email"/></label>
			    	${profile.technician.email}
			    </div>
			    <div class="dataentry">
			    	<label><spring:message code="user.telephone"/></label>
			    	${profile.technician.telephone}
			    </div>
			    <div class="dataentry">
			    	<label><spring:message code="user.location"/></label>
			    	${profile.technician.location}
			    </div>
			</fieldset>
			
			<fieldset>
              	<legend>
              		<tags:help title="profile.step1.4" text="profile.step1.4.help">v. <spring:message code="profile.step1.4"/></tags:help>
              	</legend>
                 <div class="dataentry">
                 	<spring:message code="profile.sharingMessage"/><br>
                 	<div id="radioShared">
	                	<form:radiobutton path="shared" id="yesShared" value="true"/> 
	                	<form:label path="shared" for="yesShared"><spring:message code="misc.yes"/></form:label>
	                	<form:radiobutton path="shared" id="noShared" value="false"/> 
	                	<form:label path="shared" for="noShared"><spring:message code="misc.no"/></form:label>
                 	</div>
            	</div>
            </fieldset>
			
			<fieldset>
				<legend>
					<tags:help title="profile.step1.6" text="profile.step1.6.help">vi. <spring:message code="profile.step1.6" /></tags:help> 
				</legend>
				<c:if test="${empty profile.profileId}">
					<div class="dataentry"><spring:message code="attach.newProfile"/></div>
				</c:if>
				<c:if test="${not empty profile.profileId}">
					<c:set var="left"><c:if test="${lang!='ar'}">left</c:if><c:if test="${lang=='ar'}">right</c:if></c:set>
					<div class="dataentry">
						<div class="tableOuter">
							<display:table htmlId="attachedFiles" list="${files}" id="row" requestURI="" cellspacing="0" cellpadding="0">
								<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
								<display:column style="text-align:${left};">
									<a href="../${profile.profileId}/attach/${row.id}/${row.filename}" target="_blank">${row.filename}</a>
								</display:column>
								<c:if test="${accessOK}">
									<display:column media="html">
										<a href="../${profile.profileId}/attach/${row.id}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
									</display:column>
								</c:if>
							</display:table>
						</div>
						<spring:message code="attach.free"/>: ${freeSpace} / 3.0 Mb<br/>
						<c:if test="${accessOK}">
							<b><a id="attachFile" href="../${profile.profileId}/attach"><spring:message code="attach.new"/></a></b>
						</c:if>
					</div>
				</c:if>
			</fieldset>
		
		
		</div> <!-- end right div -->
		<br style="clear:both;"/>
	</div> <!-- end container div -->
			<tags:submit><spring:message code="misc.goto"/> <spring:message code="profile.step2"/></tags:submit>
	</form:form>
	<div id="dialog-noWithWithout" style="display:none;" title="<spring:message htmlEscape="true" code="project.withWithout"/>">
	<p><spring:message htmlEscape="true" code="profile.withWithout.remove"/></p>
	</div>
</body></html>