<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<html><head><title><spring:message code="search.searchResults"/></title></head>
<body>
<script>
$(function() { $("#download-dialog").dialog({
			bgiframe: true,	autoOpen: false, height: 350, width:600,	modal: true,
			title: '<spring:message code="export.download"/>',
			buttons: { Cancel: function() { $(this).dialog('close'); }	}
}); 
$("#confirmDelete").dialog({
			bgiframe: true, autoOpen: false, resizable: false, height:140, modal: true,
			overlay: { backgroundColor: '#000', opacity: 0.5 },
			buttons: {
				Cancel: function() { $(this).dialog('close'); },
				'<spring:message code="misc.deleteItem"/>': function() { location.href=$('#deleteUrl').val(); }		
			}
});
});
</script>
<c:set var="currentStep" value="2" scope="request"/><c:set var="menuType" value="search" scope="request"/>
	<c:set var="titleKey"><c:if test="${filter.incomeGen}">profile.profiles.incomeGen</c:if><c:if test="${not filter.incomeGen}">profile.profiles.nonIncomeGen</c:if></c:set>
		<tags:table titleKey="${titleKey}" pager="true">
			<display:table name="results" export="false" id="row" pagesize="${user.pageSize}" requestURI="" cellspacing="0" cellpadding="0" htmlId="results">
				<tags:pagingProperties/>
				<display:column titleKey="profile.profileName" property="profileName" sortable="true" style="text-align:left;" headerClass="left"/>
				<display:column titleKey="profile.technician" property="technician.description" sortable="true" style="text-align:left" headerClass="left"/>
				<display:column titleKey="profile.status" sortProperty="status.description" sortable="true" style="text-align:left" headerClass="left">
					<tags:appConfigDescription ac="${row.status}"/>
				</display:column>
				<display:column titleKey="profile.fieldOffice" sortProperty="fieldOffice.description" sortable="true" style="text-align:left" headerClass="left">
					<tags:appConfigDescription ac="${row.fieldOffice}"/>
				</display:column>
				<display:column titleKey="profile.benefNum" sortProperty="benefNum" sortable="true">
					<tags:formatDecimal value="${row.benefNum}"/><c:set var="totals0" value="${totals0+row.benefNum}"/>
				</display:column>
				<display:column titleKey="profile.investTotal" sortable="true" sortProperty="investmentTotal">
					<tags:formatCurrency value="${row.investmentTotal}"/><c:set var="totals1" value="${totals1+row.investmentTotal}"/>
				</display:column>
				<display:column titleKey="profile.investOwn" sortable="true" sortProperty="investmentOwn">
					<tags:formatCurrency value="${row.investmentOwn}"/><c:set var="totals2" value="${totals2+row.investmentOwn}"/>
				</display:column>
				<display:column titleKey="profile.investExt" sortable="true" sortProperty="investmentExt">
					<tags:formatCurrency value="${row.investmentExt}"/><c:set var="totals3" value="${totals3+row.investmentExt}"/>
				</display:column>
				<c:if test="${filter.incomeGen}">
					<display:column titleKey="profile.incomeAfterAnnual" sortable="true" sortProperty="incomeAfterAnnual">
						<tags:formatCurrency value="${row.incomeAfterAnnual}"/><c:set var="totals4" value="${totals4+row.incomeAfterAnnual}"/>
					</display:column>
					<display:column titleKey="profile.yearsToRecover" sortable="true" sortProperty="yearsToRecover">
						<tags:formatDecimal value="${row.yearsToRecover}"/><c:set var="totals5" value="${totals5+row.yearsToRecover}"/>
					</display:column>
				</c:if>
				<c:if test="${not filter.incomeGen}">
					<display:column titleKey="profile.investmentPerBenef" sortable="true" sortProperty="investPerBenef">
						<tags:formatDecimal value="${row.investPerBenef}"/><c:set var="totals6" value="${totals6+row.investPerBenef}"/>
					</display:column>
					<display:column  titleKey="profile.costPerBenef" sortable="true" sortProperty="costPerBenef">
						<tags:formatDecimal value="${row.costPerBenef}"/><c:set var="totals7" value="${totals7+row.costPerBenef}"/>
					</display:column>
				</c:if>
				<display:column title="&nbsp;" style="padding-left:5px;" media="html">
					<a href="../profile/step1/${row.profileId}"><img src="../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" title="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
				</display:column>
				<display:column title="&nbsp;" media="html">
					<a onclick="openDownloadDialog('profile','${row.profileId}/${row.downloadName}.riv');" ><img src="../img/export_riv.gif" alt="<spring:message code="misc.download"/>" title="<spring:message code="misc.download"/>" width="16" height="16" border="0"/></a>
				</display:column>
				<display:column title="&nbsp;" style="padding-left:5px;" media="html">
					<c:if test="${row.shared==true || row.technician.userId==user.userId }">
					<a onclick="confirmDelete('../profile/step1/${row.profileId}/delete');"> <img src="../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" title="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
					</c:if>
				</display:column>
				<display:footer>
					<tr height="1"><td height="1" colspan="13" class="Sum1"/></tr>
					<tr class="odd">
						<td style="text-align: left;"><spring:message code="misc.sum"/></td><td colspan="3"/>
						<td><tags:formatCurrency value="${totals0}" /></td>
						<td><tags:formatCurrency value="${totals1}" /></td>
						<td><tags:formatCurrency value="${totals2}" /></td>
						<td><tags:formatCurrency value="${totals3}" /></td>
						<c:if test="${filter.incomeGen}">
							<td><tags:formatCurrency value="${totals4}" /></td>
							<td/>
						</c:if>
						<c:if test="${not filter.incomeGen}">
							<td colspan="2"/>
						</c:if>
						<td colspan="3"/>
					</tr>
					<tr class="odd">
						<td style="text-align: left;"><spring:message code="misc.average"/></td><td colspan="3"/>
						<td><tags:formatCurrency value="${totals0/row_rowNum}" /></td>
						<td><tags:formatCurrency value="${totals1/row_rowNum}" /></td>
						<td><tags:formatCurrency value="${totals2/row_rowNum}" /></td>
						<td><tags:formatCurrency value="${totals3/row_rowNum}" /></td>
						<c:if test="${filter.incomeGen}">
							<td><tags:formatDecimal value="${totals4/row_rowNum}" /></td>
							<td><tags:formatDecimal value="${totals5/row_rowNum}" /></td>
						</c:if>
						<c:if test="${not filter.incomeGen}">
							<td><tags:formatDecimal value="${totals6/row_rowNum}" /></td>
							<td><tags:formatDecimal value="${totals7/row_rowNum}" /></td>
						</c:if>
						<td colspan="3"/>
					</tr>
<%-- 					<tr><td colspan="12"  style="text-align: left;"><spring:message code="profile.report.results.total"/>${row_rowNum}</td></tr> --%>
				</display:footer>
			</display:table>
		</tags:table>
		
		<c:if test="${fn:length(results) gt 0}">
			<spring:message code="export.search"/>: 
			<a href="report/profileResults.pdf" target="_blank"><img src="../img/pdf.gif" alt="PDF" border="0"/>  PDF</a>
			<a href="report/profileResults.xlsx" target="_blank"><img src="../img/xls.gif" alt="XLS" border="0"/>  XLS</a>
			<a href="results/profileResults.zip" target="_blank"><img src="../img/zip.png" alt="ZIP" border="0"/> ZIP</a><br/>
			<br/><br/>
		</c:if>
	<div id="confirmDelete" title="<spring:message code="misc.deleteItem"/>">
		<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
		<spring:message code="misc.confirmDel"/></p><input id="deleteUrl" type="hidden" value=""/>
	</div>
		<div id="download-dialog" style="display:none;">
			<spring:message code="export.choice"/>
			<ul><li><b><spring:message code="export.local.title"/></b><br/>
			<spring:message code="export.local.desc"/><br/>
			<a id="dd_local">
			<img src="../img/export_riv.gif" alt="<spring:message code="export.local.title"/>" title="<spring:message code="export.local.title"/>" width="16" height="16" border="0"/>
			<spring:message code="export.local"/></a><br/><br/></li>
			<li><b><spring:message code="export.generic.title"/></b><br/>
			<spring:message code="export.generic.desc"/><br/>
			<a id="dd_generic">
			<img src="../img/export_riv.gif" alt="<spring:message code="export.generic.title"/>" title="<spring:message code="export.generic.title"/>" width="16" height="16" border="0"/>
			<spring:message code="export.generic"/></a></li></ul>
		</div>
</body></html>