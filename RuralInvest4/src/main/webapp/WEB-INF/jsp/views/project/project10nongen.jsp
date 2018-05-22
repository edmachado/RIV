<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="project.step10.nongen"/></title>
<style>
.onlyPerYear { 
<c:if test="${not project.perYearContributions}">display:none;</c:if> 
}
#summaryContributions { display:block; }
</style>
<script>
$(function() {
	$('#summaryContributions > div > table > tbody > tr:even').addClass("odd");
	$('#yearByYear').buttonset();
	$( document ).tooltip();
	$('#confirmSimple').dialog({
		bgiframe: true, autoOpen: false, resizable: false, height:200, width:360, modal: true,
		overlay: { backgroundColor: '#000', opacity: 0.5 },
		buttons: {
			Cancel: function() { 
				$("#perYear").prop("checked", true).button('refresh');
				$(this).dialog('close'); 
			},
			'<spring:message code="misc.confirm"/>': function() {
				location.href='../step10/${project.projectId}/perYearContributions?simple=true';
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
var selectedYear=0;
function showYear(year) {
	toggle('contrib'+year);
	toggle('contrib'+selectedYear);

	$('#yearBox'+selectedYear).toggleClass('selected',false);
	selectedYear=year;
	$('#yearBox'+year).toggleClass('selected',true);
}
</script>
<tags:excelImportHead submitUrl="../../import/project/contribution/${project.projectId}"/>
</head>
<body>
<form:form id="contribForm" name="form" method="post" commandName="project">
	<tags:errors />
	
	<div align="left">
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a id="downloadTemplate" href="../../report/${project.projectId}/projectContributions.xlsx?template=true" target="_blank"><spring:message code="export.downloadTemplate"/></a><br/>
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a href="../../report/${project.projectId}/projectContributions.xlsx" target="_blank"><spring:message code="export.download"/></a><br/>
		<c:if test="${accessOK}"><a id="importExcel" href="#"><img src="../../img/xls.gif" alt="Excel" title="Excel"/> <spring:message code="import.importExcel"/></a></c:if>
 	</div>
 	
 	<c:if test="${accessOK}">
	 	<div id="yearByYear" style="margin:10px 5px;">
			<input type="radio" name="simpleApproach" id="simplified" value="true" <c:if test="${not project.perYearContributions}">checked="checked"</c:if>><label for="simplified" title='<spring:message code="projectContribution.method.simplified.help"/>'><spring:message code="projectContribution.method.simplified"/></label>
			<input type="radio" name="simpleApproach" id="perYear" value="false" <c:if test="${project.perYearContributions}">checked="checked"</c:if>><label for="perYear" title='<spring:message code="projectContribution.method.perYear.help"/>'><spring:message code="projectContribution.method.perYear"/></label>
		</div>
	</c:if>
	<b>
		<c:if test="${not project.perYearContributions}"><spring:message code="projectContribution.method.simplified.help"/></c:if>
		<c:if test="${project.perYearContributions}"><spring:message code="projectContribution.method.perYear.help"/></c:if>
 	</b>
 	
 	<c:if test="${project.perYearContributions}">
		<tags:yearSelector end="${project.duration-1}"/>
	</c:if>
	
	<tags:tableContainer titleKey="projectContribution">
		<c:set var="end">
			<c:if test="${project.perYearContributions}">${project.duration-1}</c:if>
			<c:if test="${not project.perYearContributions}">0</c:if>
		</c:set>
		<c:forEach var="year" begin="0" end="${end}">
			<c:set var="total" value="0"/>			
			<c:set var="beginStyle"><c:if test="${year eq 0}">display:block</c:if><c:if test="${year ne 0}">display:none</c:if></c:set>	
			<div id="contrib${year}" style="${beginStyle}">
				<tags:table>
					<display:table list="${project.contributions}" id="contrib" requestURI="" cellspacing="0" cellpadding="0"
							export="false" htmlId="contributionTable${year}">
						<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
<%-- 						<display:column title="year">${contrib.years[(year).intValue()].year}</display:column> --%>
						<display:column titleKey="projectContribution.description" property="description" sortable="true" style="text-align:left;" headerClass="left"/>
						<display:column titleKey="projectContribution.contributor" style="text-align:left;" headerClass="left">
							<c:choose>
								<c:when test="${donors[contrib.donorOrderBy].notSpecified}"><spring:message code="project.donor.notSpecified"/></c:when>
								<c:when test="${donors[contrib.donorOrderBy].description eq 'state-public'}"><spring:message code="project.donor.statePublic"/></c:when>
								<c:otherwise>${donors[contrib.donorOrderBy].description}</c:otherwise>
							</c:choose>
						</display:column>
						<display:column titleKey="projectContribution.unitType" property="unitType" sortable="true" style="text-align:left;" headerClass="left"/>
						<display:column titleKey="projectContribution.unitCost" sortable="true" sortProperty="unitCost">
							<tags:formatCurrency value="${contrib.unitCost}"/>
						</display:column>
						<display:column titleKey="projectContribution.unitNum" sortProperty="unitNum" sortable="true">
							<tags:formatDecimal value="${contrib.years[(year).intValue()].unitNum}"/>
						</display:column>
						<display:column titleKey="projectContribution.totalCost" sortable="true" sortProperty="total">
							<tags:formatCurrency value="${contrib.years[(year).intValue()].total}"/><c:set var="total" value="${total+contrib.years[(year).intValue()].total}"/>
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
								<c:if test="${contrib_rowNum ne fn:length(project.contributions)}">
									<a name="moveDown" href="../item/${contrib.projItemId}/move?up=true">
										<img src="../../img/arrow_down.png" title="<spring:message code="misc.moveDown"/>" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
									</a>
								</c:if>
								<c:if test="${contrib_rowNum eq fn:length(project.contributions)}">
									<img src="../../img/spacer.gif" width="16" height="16" border="0">
								</c:if>
							</display:column>
							 
							<display:column title="&nbsp;" style="margin-left:5px;" media="html">
								<a href="../item/${contrib.projItemId}"><img src="../../img/edit.png" title="<spring:message code="misc.viewEditItem"/>" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
							</display:column>
							<display:column title="&nbsp;" media="html">
								<a name="delItem" href="javascript:confirmDelete('../item/${contrib.projItemId}/delete');">
									<img src="../../img/delete.gif" title="<spring:message code="misc.deleteItem"/>" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0">
								</a>
							</display:column>
						</c:if>
						<display:footer>
							<tr height="1"><td height="1" colspan="11" class="Sum1"/></tr>
							<tr><td/><td/><td/><td/><td/>
							<td><tags:formatCurrency value="${total}" /></td>
							<td/><td/><td/><td/><td/></tr>
						</display:footer>
					</display:table>
					<c:if test="${accessOK}">
						<div class="addNew"><a id="newContrib" href="../item/-1?type=contrib&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
					</c:if>
				</tags:table>
			</div>
		</c:forEach>
	</tags:tableContainer>
 	
 	<div id="yearlyFlow" style="display:block;">
 		<h2><spring:message code="projectContribution.yearlyFlow"/></h2>
 		<tags:table>
 			<table cellspacing="0" cellpadding="0">
 				<thead>
 					<tr>
 						<th class="left"><spring:message code="units.year"/></th>
 						<c:forEach begin="1" end="${project.duration}" var="i">
 							<th>${i}</th>
 						</c:forEach>
 					</tr>
 				</thead>
 				<tbody>
 					<tr class="odd">
 						<td></td>
 						<c:forEach var="year" items="${years}">
 							<td><tags:formatCurrency value="${year.total}" noDecimals="true" /></td>
 						</c:forEach>
 					</tr>
 				</tbody>
 			</table>
 		</tags:table>
 	</div>
 
	<div id="contribSummary" style="display:block;">
 		<h2><spring:message code="project.report.contributionSummary"/></h2>
 		<tags:summaryContributions/>
 	</div>
 	
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="project.step10"/></tags:submit>
</form:form>
<tags:confirmDelete/>
<div id="confirmSimple" title='<spring:message code="misc.confirm"/>'>
	<span class="ui-icon ui-icon-alert" style="display:inline-block"></span> <spring:message code="projectContribution.method.confirm"/> 
</div>
<tags:excelImport submitUrl="../../import/project/contribution/${project.projectId}"/>
</body></html>