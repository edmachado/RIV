<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step1"/></title>
<script>
$(function() {
$( "#radioWithWithout" ).buttonset();
$( "#radioShared" ).buttonset();
});
</script></head>
<body>
<form:form name="form" method="post" commandName="project">
	<tags:errors />

	<c:if test="${not empty project.projectId}">
		<div style="text-align:right;width:100%">
			<tags:help text="project.clone.help" title="project.clone">
				<a href="${project.projectId}/clone"><spring:message code="project.clone"/> <img src="../../img/duplicate.gif" border="0"/></a>
			</tags:help>&nbsp;
			<c:if test="${user.administrator and rivConfig.qa}"><div><a target="_blank" id="properties" style="display:none;" href="${project.projectId}/project.properties">Download properties file</a></div></c:if>
		</div>
	</c:if>
	<div> <!-- container div -->
		<div style="float:left;margin-right:12px;"> <!-- left div -->

		<fieldset>
			<legend>i. <spring:message code="project.step1.1"/></legend>
				<c:if test="${empty param['rename']}">
					<tags:dataentry field="projectName" labelKey="project.projectName" size="44" inputClass="text" maxLength="100" />
				</c:if>
				<c:if test="${not empty param['rename']}">
					<div class="dataentry">
						<span class="helpSpacer"></span>
						<label><spring:message code="project.projectName"/></label>
						<div style="display:inline-block;">
							${project.projectName}<br/>
							<form:input path="projectName" cssClass="text" size="44" maxlength="100" />
						</div>
						<span style="display:inline-block;margin-left:15px;"><img vspace="4" align="left" src="../../img/locked.gif"></span>
					</div>
				</c:if>
				<tags:dataentry field="userCode" labelKey="project.userCode" helpTitle="project.userCode" helpText="project.userCode.help" inputClass="text" size="20" maxLength="20" />
				
				<c:set var="exchRateCalcSign">${rivConfig.setting.currencyName}&nbsp;<spring:message code="units.perUSD"/></c:set>   
				<tags:dataentry field="exchRate" labelKey="project.exchRate" size="8" calcSign="${exchRateCalcSign}"	helpText="project.exchRate.help" />				
				
				<c:if test="${project.incomeGen}">
					<tags:dataentry field="inflationAnnual" labelKey="project.inflationAnnual" helpTitle="project.inflationAnnual" helpText="project.inflationAnnual.help" inputClass="num" size="8" maxLength="8" calcSignKey="units.inPercent" />
				</c:if>
				
<%-- 				<c:if test="${empty project.projectId}"> --%>
<%-- 					<input type="hidden" name="oldStartupMonth" value="${project.startupMonth}"/> --%>
<%-- 				</c:if> --%>
				<div class="dataentry">
					<label><tags:help text="project.startupMonth" title="project.startupMonth.help"><spring:message code="project.startupMonth"/></tags:help></label>
					<form:select path="startupMonth">
						<c:forEach var="i" begin="1" end="12">
							<c:set var="label"><spring:message code="calendar.month.${i}" /></c:set>
							<form:option value="${i}" label="${label}"/>
						</c:forEach>
					</form:select>
				</div>
			
				<div class="dataentry">
					<label><tags:help text="project.benefType.help" title="project.benefType"><spring:message code="project.benefType"/></tags:help></label>
					<form:select path="beneficiary">
						<form:options items="${rivConfig.beneficiaries.values()}" itemValue="configId" itemLabel="description" />
					</form:select>
				</div>
				
				
				<div class="dataentry">
					<label><tags:help text="project.enviroCat.help" title="project.enviroCat"><spring:message code="project.enviroCat"/></tags:help></label>
					<form:select path="enviroCategory">
						<form:options items="${rivConfig.enviroCategories.values()}" itemValue="configId" itemLabel="description" />
					</form:select>
				</div>
				
<%-- 				<input type="hidden" name="oldDuration" value="${project.duration}"/> --%>
				<tags:dataentry field="duration" labelKey="project.duration" helpTitle="project.duration" helpText="project.duration.help" calcSignKey="units.years" size="8" maxLength="3" />
				
				<div class="dataentry">
					<label><tags:help text="project.category.help" title="project.category"><spring:message code="project.category"/></tags:help></label>
					<form:select path="projCategory">
						<c:if test="${project.incomeGen}">
							<form:options items="${rivConfig.categoriesIG.values()}" itemValue="configId" itemLabel="description" />
						</c:if>
						<c:if test="${!project.incomeGen}">
							<form:options items="${rivConfig.categoriesNig.values()}" itemValue="configId" itemLabel="description" />
						</c:if>
					</form:select>
				</div>
				
				<c:if test="${rivConfig.setting.admin1Enabled}">
					<div class="dataentry">
					<label><tags:help noKey="true" text="${rivConfig.setting.admin1Help}" title="${rivConfig.setting.admin1Title}">${rivConfig.setting.admin1Title}</tags:help></label>
					<form:select path="appConfig1">
						<form:options items="${rivConfig.appConfig1s.values()}" itemValue="configId" itemLabel="description" />
					</form:select>
					</div>
				</c:if>
				
				<c:if test="${rivConfig.setting.admin2Enabled}">
				<div class="dataentry">
					<label><tags:help noKey="true" text="${rivConfig.setting.admin2Help}" title="${rivConfig.setting.admin2Title}">${rivConfig.setting.admin2Title}</tags:help></label>
					<form:select path="appConfig2">
						<form:options items="${rivConfig.appConfig2s.values()}" itemValue="configId" itemLabel="description" />
					</form:select>
				</div>
				</c:if>
								
				<div class="dataentry">
					<label><tags:help text="project.createdBy.help"><spring:message code="project.createdBy"/></tags:help></label>
				 	${project.createdBy}
				</div>
				
				<div class="dataentry">
					<label><tags:help text="project.creationDate.help" title="project.creationDate"><spring:message code="project.creationDate"/></tags:help></label>
					<fmt:formatDate value="${project.prepDate}" type="both" pattern="dd/MM/yy HH:mm" />
				</div>

				<div class="dataentry">
					<label><spring:message code="project.lastUpdate"/></label>
				 	<fmt:formatDate value="${project.lastUpdate}" type="both" pattern="dd/MM/yy HH:mm" />
				</div>
			</fieldset>
			
			<fieldset>
                <legend>ii. <spring:message code="project.step1.2"/></legend>
				<div class="dataentry">
					<label><spring:message code="project.fieldOffice"/></label>
					<form:select path="fieldOffice" id="fieldOffice">
						<form:options items="${rivConfig.fieldOffices.values()}" itemValue="configId" itemLabel="description" />
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
	 				<tags:help title="project.step1.3" text="project.step1.3.help">iii. <spring:message code="project.step1.3"/></tags:help>
				</legend>
				<div class="dataentry">
					<tags:help title="project.status" text="project.status.help"><label><spring:message code="project.status"/></label></tags:help>
					<form:select path="status" id="status">
						<form:options items="${rivConfig.statuses.values()}" itemValue="configId" itemLabel="description" />
					</form:select>
				</div>	
				<c:if test="${project.incomeGen}">
					<div class="dataentry">
<%-- 						<input name="oldWithWithout" type="hidden" value="${project.withWithout}"/> --%>
						<tags:help text="project.withWithout.help" title="project.withWithout"></tags:help>
						<spring:message code="project.withWithoutMessage" />
						<div id="radioWithWithout">
							<form:radiobutton path="withWithout" id="yes" value="true" />
							<form:label path="withWithout" for="yes"><spring:message code="misc.yes"/></form:label>
							<form:radiobutton path="withWithout" id="no" value="false" />
							<form:label path="withWithout" for="no"><spring:message code="misc.no"/></form:label>
						</div>
					</div>
					<c:if test="${project.withWithout && (project.wizardStep==null || project.wizardStep gt 9)}"><script type="text/javascript">
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
  				<legend>
  					<tags:help title="project.step1.4" text="project.step1.4.help">iv. <spring:message code="project.step1.4"/></tags:help>
  				</legend>
			    <div class="dataentry">
			    	<label><spring:message code="user.name"/></label>
			    	${project.technician.description}
			    </div>
			   <div class="dataentry">
			    	<label><spring:message code="user.organization"/></label>
			    	${project.technician.organization}
			    </div>
			    <div class="dataentry">
			    	<label><spring:message code="user.email"/></label>
			    	${project.technician.email}
			    </div>
			    <div class="dataentry">
			    	<label><spring:message code="user.telephone"/></label>
			    	${project.technician.telephone}
			    </div>
			    <div class="dataentry">
			    	<label><spring:message code="user.location"/></label>
			    	${project.technician.location}
			    </div>
			</fieldset>
			
			<fieldset>
              	<legend>
              		<tags:help title="project.step1.5" text="project.step1.5.help">v. <spring:message code="project.step1.5"/></tags:help>
              	</legend>
                 <div class="dataentry">
                 	<spring:message code="project.sharingMessage"/><br>
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
					<tags:help title="project.step1.6" text="project.step1.6.help">vi. <spring:message code="project.step1.6" /></tags:help> 
				</legend>
				<c:if test="${empty project.projectId}">
					<div class="dataentry"><spring:message code="attach.newProject"/></div>
				</c:if>
				<c:if test="${not empty project.projectId}">
					<c:set var="left"><c:if test="${lang!='ar'}">left</c:if><c:if test="${lang=='ar'}">right</c:if></c:set>
					<div class="dataentry">
						<div class="tableOuter">
							<display:table htmlId="attachedFiles" list="${files}" id="row" requestURI="" cellspacing="0" cellpadding="0">
								<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
								<display:column style="text-align:${left};">
									<a href="../${project.projectId}/attach/${row.id}/${row.filename}" target="_blank">${row.filename}</a>
								</display:column>
								<c:if test="${accessOK}">
									<display:column media="html">
										<a href="../${project.projectId}/attach/${row.id}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
									</display:column>
								</c:if>
							</display:table>
						</div>
						<spring:message code="attach.free"/>: ${freeSpace} / 3.0 Mb<br/>
						<c:if test="${accessOK}">
							<b><a id="attachFile" href="../${project.projectId}/attach"><spring:message code="attach.new"/></a></b>
						</c:if>
					</div>
				</c:if>
			</fieldset>
			<c:if test="${empty project.projectId}">
				<fieldset>
					<legend>vii. <spring:message code="project.quickAnalysis"/></legend>
					<div class="dataentry">
						<input type="checkbox" name="quickAnalysis" value="true"> <spring:message code="project.quickAnalysis"/> 
					</div>
				</fieldset>
			</c:if>
		
		</div> <!-- end right div -->
		<br style="clear:both;"/>
	</div> <!-- end container div -->
			<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step2"/></tags:submit>
	</form:form>
	<div id="dialog-noWithWithout" style="display:none;" title="<spring:message htmlEscape="true" code="project.withWithout"/>">
	<p><spring:message htmlEscape="true" code="project.withWithout.remove"/></p>
	</div>
</body></html>