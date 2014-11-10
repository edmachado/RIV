<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step10.nongen"/></title></head>
<body>
<script>
$(function() {
	$('#yearByYear').buttonset();
	$( document ).tooltip();
	$('#confirmSimple').dialog({
		bgiframe: true, autoOpen: false, resizable: false, height:200, width:360, modal: true,
		overlay: { backgroundColor: '#000', opacity: 0.5 },
		buttons: {
			Cancel: function() { $(this).dialog('close'); },
			'TRANSLATE Confirm': function() {
				location.href='../step10/${project.projectId}/perYearContributions?simple=true';
			}		
		}
	});
	$("#copyYear").dialog({
			bgiframe: true, autoOpen: false, resizable: false, height:200, width:360, modal: true,
			overlay: { backgroundColor: '#000', opacity: 0.5 },
			buttons: {
				Cancel: function() { $(this).dialog('close'); },
				"Copy": {
					text: '<spring:message code="projectContribution.copy.title"/>',
					id: "copyYearButton",
					click: function() {
						if ($.isNumeric($('#targetYear').val())) {
							location.href=$('#copyUrl').val()+$('#targetYear').val(); 
						} else {
							$('#copyYear-error').append('<div class="alert alert-error"><spring:message code="projectContribution.copy.range" arguments="${project.duration}"/></div>');
						}
					}		
				}
			}
	});
	$('#contribForm input').on('change', function() {
		   if($('input[name=simpleApproach]:checked', '#contribForm').val()=='true') {
			   // simple selected
			   $('#confirmSimple').dialog('open');
		   } else {
			   location.href='../step10/${project.projectId}/perYearContributions?simple=false';
		   }
		});
});
</script>

<style>
.onlyPerYear { 
<c:if test="${not project.perYearContributions}">display:none;</c:if> 
}
</style>
<form:form id="contribForm" name="form" method="post" commandName="project">
	<tags:errors />
	
	<div align="left">
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a href="../../report/${project.projectId}/projectContributions.xlsx?template=true" target="_blank"><spring:message code="export.downloadTemplate"/></a><br/>
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadTemplate" href="../../report/${project.projectId}/projectContributions.xlsx" target="_blank"><spring:message code="export.download"/></a><br/>
		<c:if test="${accessOK}"><!-- --> Under development... <!-- <a id="importExcel" href="#"><img src="../../img/xls.gif" alt="Excel" title="Excel"/> <spring:message code="import.importExcel"/></a> --></c:if>
 	</div>
 	
 	<c:if test="${not project.perYearContributions}">
 		<c:forEach begin="1" end="41" step="10" var="outer">
 			<c:if test="${project.duration>=outer}">
 				<tags:table>
		 			<table cellspacing="0" cellpadding="0">
		 				<thead>
		 					<tr>
		 						<th class="left">TRANSLATE year</th>
			 					<c:forEach begin="0" end="9" var="inner" >
			 						<c:if test="${project.duration>=outer+inner}">
			 							<th>${outer+inner}</th>
			 						</c:if>
			 					</c:forEach>
		 					</tr>
		 				</thead>
		 				<tbody>
		 					<tr>
		 						<td style="text-align:left"><spring:message code="projectContribution.yearlyFlow"/></td> 
		 						<c:forEach begin="0" end="9" var="inner">
			 						<c:if test="${project.duration>=outer+inner}">
		 								<td><tags:formatCurrency value="${years[(outer+inner-1)].total}"/></td>
		 							</c:if>
		 						</c:forEach>
		 					</tr>
		 				</tbody>
		 			</table>
		 		</tags:table>
 			</c:if>
 		</c:forEach>
 	</c:if>
	
	<tags:tableContainer titleKey="projectContribution">
		<div id="yearByYear" style="margin:10px 5px;">
			<input type="radio" name="simpleApproach" id="simplified" value="true" <c:if test="${not project.perYearContributions}">checked="checked"</c:if>><label for="simplified" title='TRANSLATE With the "Simplified approach", contributions are the same for every year of the project.'>Simplified</label>
			<input type="radio" name="simpleApproach" id="perYear" value="false" <c:if test="${project.perYearContributions}">checked="checked"</c:if>><label for="perYear" title='TRANSLATE With the "Per-year approach", contributions may be different for each year of the project.'>Per-year</label>
		</div>
		
		<c:forEach var="year" items="${years}">
			<c:set var="yearNum" value="${year.year}"/>
			<c:if test="${project.perYearContributions or yearNum eq 1}">
				<div class="onlyPerYear">
					<a name="year${yearNum}" id="year${yearNum}"></a>
					<h3>TRANSLATE Year ${yearNum}</h3> 
					<c:set var="total" value="0"/>
					<spring:message code="projectContribution.yearlyFlow"/> <tags:formatCurrency value="${year.total}"/> 
				</div>
				
				<tags:table>
					<display:table list="${contribsByYear[yearNum]}" id="contrib" requestURI="" cellspacing="0" cellpadding="0"
							export="false" htmlId="contributionTable${yearNum}">
						<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					
						<display:column titleKey="projectContribution.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
						<display:column titleKey="projectContribution.contribType" sortable="true" style="text-align:left;" headerClass="left">
							<c:if test="${contrib.contribType=='0'}"><spring:message code="projectContribution.contribType.govtCentral"/></c:if>
							<c:if test="${contrib.contribType=='1'}"><spring:message code="projectContribution.contribType.govtLocal"/></c:if>
							<c:if test="${contrib.contribType=='2'}"><spring:message code="projectContribution.contribType.ngoLocal"/></c:if>
							<c:if test="${contrib.contribType=='3'}"><spring:message code="projectContribution.contribType.ngoIntl"/></c:if>
							<c:if test="${contrib.contribType=='5'}"><spring:message code="projectContribution.contribType.beneficiary"/></c:if>
							<c:if test="${contrib.contribType=='4'}"><spring:message code="projectContribution.contribType.other"/></c:if>
						</display:column>
						<display:column titleKey="projectContribution.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
						<display:column titleKey="projectContribution.unitNum" sortProperty="unitNum" sortable="true">
							<tags:formatDecimal value="${contrib.unitNum}"/>
						</display:column>
						<display:column titleKey="projectContribution.unitCost" sortable="true" sortProperty="unitCost">
							<tags:formatCurrency value="${contrib.unitCost}"/>
						</display:column>
						<display:column titleKey="projectContribution.totalCost" sortable="true" sortProperty="total">
							<tags:formatCurrency value="${contrib.total}"/><c:set var="total" value="${total+contrib.total}"/>
						</display:column>
						<c:if test="${accessOK}">
							<display:column title="&nbsp;" media="html">
								<a name="copy" href="../item/${contrib.projItemId}/copy">
									<img src="../../img/duplicate.gif" title="<spring:message code="misc.copy"/>" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
								</a>
							</display:column>
							<display:column title="&nbsp;" media="html">
								<c:if test="${contrib_rowNum ne 1}">
									<a name="moveUp" href="../item/${contrib.projItemId}/move?up=false">
										<img src="../../img/arrow_up.png" title="<spring:message code="misc.moveUp"/>" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
									</a>
								</c:if>
								<c:if test="${contrib_rowNum eq 1}">
									<img src="../../img/spacer.gif" width="16" height="16" border="0">
								</c:if>
							</display:column>
							<display:column title="&nbsp;" media="html">
								<c:if test="${contrib_rowNum ne fn:length(contribsByYear[yearNum])}">
									<a name="moveDown" href="../item/${contrib.projItemId}/move?up=true">
										<img src="../../img/arrow_down.png" title="<spring:message code="misc.moveDown"/>" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
									</a>
								</c:if>
								<c:if test="${contrib_rowNum eq fn:length(contribsByYear[yearNum])}">
									<img src="../../img/spacer.gif" width="16" height="16" border="0">
								</c:if>
							</display:column>
							 
							<display:column title="&nbsp;" style="margin-left:5px;" media="html">
								<a href="../item/${contrib.projItemId}"><img src="../../img/edit.png" title="<spring:message code="misc.viewEditItem"/>" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
							</display:column>
							<display:column title="&nbsp;" media="html">
								<a name="delItem" href="../item/${contrib.projItemId}/delete">
									<img src="../../img/delete.gif" title="<spring:message code="misc.deleteItem"/>" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0">
								</a>
							</display:column>
						</c:if>
						<display:footer>
							<tr height="1"><td height="1" colspan="12" class="Sum1"/></tr>
							<tr><td/><td/><td/><td/><td/>
							<td><tags:formatCurrency value="${total}" /></td>
							<td/><td/><td/><td/><td/></tr>
						</display:footer>
					</display:table>
					<c:if test="${accessOK}">
						<div class="addNew"><a id="newContrib${yearNum}" href="../item/-1?type=contrib&projectId=${project.projectId}&year=${yearNum}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
						<c:if test="${fn:length(contribsByYear[yearNum]) gt 0}">
							<div class="addNew onlyPerYear"><a id="copyYear${yearNum}" href="javascript:copyContrib('../step10/${project.projectId}/copyContrib/${yearNum}/');"><img border="0" src="../../img/duplicate.gif"><spring:message code="projectContribution.copy"/></a>&nbsp;&nbsp;</div>
						</c:if>
					</c:if>
				</tags:table>
			</c:if>
		</c:forEach>

	</tags:tableContainer>
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step10"/></tags:submit>
</form:form>
<div id="copyYear" title="<spring:message code='projectContribution.copy.title'/>">
	<p><spring:message code='projectContribution.copy'/><br/>
	<spring:message code='projectContribution.copy.toYear' /> <input type="text" id="targetYear" name="targetYear" size="4" />
	</p>
	<div id="copyYear-error" style="margin-top:4px"></div>
	<input id=copyUrl type="hidden" value=""/>
</div>
<div id="confirmSimple" title="TRANSLATE confirm">
	TRANSLATE The contributions of year 1 will be used as the basis for yearly contributions. Contributions currently in other years will be discarded. Continue?
</div>
<tags:excelImport submitUrl="../../import/project/contribution/${project.projectId}"/>
</body></html>