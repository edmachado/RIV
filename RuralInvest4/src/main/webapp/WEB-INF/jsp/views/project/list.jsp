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
	<c:set var="titleKey"><c:if test="${filter.incomeGen}">project.projects.incomeGen</c:if><c:if test="${not filter.incomeGen}">project.projects.nonIncomeGen</c:if></c:set>
		<tags:table titleKey="${titleKey}">
			<display:table name="results" id="row" requestURI="" export="false" cellspacing="0" cellpadding="0"
				 htmlId="results"><!-- decorator="rivWeb.decorators.ProjectList" -->
				<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
				<display:column style="text-align:left;" headerClass="left" titleKey="project.projectName" property="projectName" sortable="true" />
				<display:column style="text-align:left" headerClass="left" titleKey="project.userCode" property="userCode" sortable="true"/>
				
				<c:if test="${user.resultTechnician}"><c:set var="cols" value="${cols+1}"/>
					<display:column style="text-align:left; ${tech}" headerClass="left" titleKey="project.technician" property="technician.description" sortable="true"/>
				</c:if>
				
				<c:if test="${user.resultFieldOffice}"><c:set var="cols" value="${cols+1}"/>
					<display:column style="text-align:left;" headerClass="left" titleKey="project.fieldOffice" sortable="true">
						<tags:appConfigDescription ac="${row.fieldOffice}"/>
					</display:column>
				</c:if>
				<c:if test="${user.resultStatus}"><c:set var="cols" value="${cols+1}"/>
					<display:column style="text-align:left;" headerClass="left" titleKey="project.status" sortable="true">
						<tags:appConfigDescription ac="${row.status}"/>
					</display:column>
				</c:if>
				<c:if test="${user.resultProjectCategory}"><c:set var="cols" value="${cols+1}"/>
					<display:column style="text-align:left;" headerClass="left" titleKey="project.category" sortable="true">
						<tags:appConfigDescription ac="${row.projCategory}"/>
					</display:column>
				</c:if>
				<c:if test="${user.resultBenefCategory}"><c:set var="cols" value="${cols+1}"/>
					<display:column style="text-align:left;" headerClass="left" titleKey="project.benefType" sortable="true">
						<tags:appConfigDescription ac="${row.beneficiary}"/>
					</display:column>
				</c:if>
				<c:if test="${user.resultEnviron}"><c:set var="cols" value="${cols+1}"/>
					<display:column style="text-align:center;" headerClass="centered" titleKey="project.enviroCat" sortable="true">
						<tags:appConfigDescription ac="${row.enviroCategory}"/>
					</display:column>
				</c:if>
				<c:if test="${rivConfig.setting.admin1Enabled and user.resultAdminCategory1}"><c:set var="cols" value="${cols+1}"/>
					<display:column style="text-align:left;" headerClass="left" title="${rivConfig.setting.admin1Title}" sortable="true">
						<tags:appConfigDescription ac="${row.appConfig1}"/>
					</display:column>
				</c:if>
				<c:if test="${rivConfig.setting.admin2Enabled and user.resultAdminCategory2}"><c:set var="cols" value="${cols+1}"/>
					<display:column style="text-align:left;" headerClass="left" title="${rivConfig.setting.admin2Title}" sortable="true">
						<tags:appConfigDescription ac="${row.appConfig2}"/>
					</display:column>
				</c:if>
				<c:if test="${user.resultInvestTotal}"><c:set var="cols" value="${cols+1}"/>
					<display:column titleKey="project.investTotal" sortable="true" sortProperty="investmentTotal">
					  	<tags:formatCurrency value="${row.investmentTotal}"/><c:set var="investTotal" value="${investTotal+row.investmentTotal}"/>
					</display:column>
				</c:if>
				<c:if test="${user.resultInvestOwn}"><c:set var="cols" value="${cols+1}"/>
					<display:column titleKey="project.investOwn" sortable="true" sortProperty="investmentOwn">
						<tags:formatCurrency value="${row.investmentOwn}"/><c:set var="investOwn" value="${investOwn+row.investmentOwn}"/>
					</display:column>
				</c:if>
				<c:if test="${user.resultInvestDonated}"><c:set var="cols" value="${cols+1}"/>
					<display:column titleKey="project.investExt" sortable="true" sortProperty="investmentDonated">
						<tags:formatCurrency value="${row.investmentDonated}"/><c:set var="investDonated" value="${investDonated+row.investmentDonated}"/>
					</display:column>
				</c:if>
				<c:if test="${user.resultInvestFinanced}"><c:set var="cols" value="${cols+1}"/>
					<c:set var="financedTitle">
						<c:if test="${filter.incomeGen}">project.investCredit</c:if>
						<c:if test="${not filter.incomeGen}">project.investCredit.nongen</c:if>
					</c:set>
					<display:column titleKey="${financedTitle}" sortable="true" sortProperty="investmentFinanced">
						<tags:formatCurrency value="${row.investmentFinanced}"/><c:set var="investFinanced" value="${investFinanced+row.investmentFinanced}"/>
					</display:column>
				</c:if>
				<c:if test="${user.resultAnnualEmploy}"><c:set var="cols" value="${cols+1}"/>
					<display:column titleKey="project.annualEmployment" sortable="true" sortProperty="annualEmployment">
						<tags:formatCurrency value="${row.annualEmployment}"/><c:set var="annualEmployment" value="${annualEmployment+row.annualEmployment}"/>
					</display:column>
				</c:if>	
				<c:if test="${filter.incomeGen}">
					<c:if test="${user.resultAnnualNetIncome}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.annualIncome" sortable="true" sortProperty="annualNetIncome">
							<tags:formatCurrency value="${row.annualNetIncome}"/><c:set var="annualNetIncome" value="${annualNetIncome+row.annualNetIncome}"/>
						</display:column>
					</c:if>
					<c:if test="${user.resultWorkingCapital}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.results.workCapital" sortable="true" sortProperty="workingCapital">
							<tags:formatCurrency value="${row.workingCapital}"/><c:set var="wc" value="${wc+row.workingCapital}"/>
						</display:column>
					</c:if>
					<c:if test="${user.resultWcOwn}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.results.workCapital.own" sortable="true" sortProperty="wcOwn">
							<tags:formatCurrency value="${row.wcOwn}"/><c:set var="wcOwn" value="${wcOwn+row.wcOwn}"/>
						</display:column>
					</c:if>
					<c:if test="${user.resultWcDonated}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.results.workCapital.donated" sortable="true" sortProperty="wcDonated">
							<tags:formatCurrency value="${row.wcDonated}"/>	<c:set var="wcDonated" value="${wcDonated+row.wcDonated}"/>
						</display:column>
					</c:if>
					<c:if test="${user.resultWcFinanced}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.results.workCapital.financed" sortable="true" sortProperty="wcFinanced">
							<tags:formatCurrency value="${row.wcFinanced}"/><c:set var="wcFinanced" value="${wcFinanced+row.wcFinanced}"/>	
						</display:column>
					</c:if>
					<c:if test="${user.resultTotalCosts}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.results.totalCosts" sortable="true" sortProperty="totalCosts">
							<tags:formatCurrency value="${row.totalCosts}"/><c:set var="costsTotal" value="${costsTotal+row.totalCosts}"/>	
						</display:column>
					</c:if>
					<c:if test="${user.resultTotalCostsOwn}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.results.totalCosts.own" sortable="true" sortProperty="totalCostsOwn">
							<tags:formatCurrency value="${row.totalCostsOwn}"/><c:set var="costsOwn" value="${costsOwn+row.totalCostsOwn}"/>	
						</display:column>
					</c:if>
					<c:if test="${user.resultTotalCostsDonated}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.results.totalCosts.donated" sortable="true" sortProperty="totalCostsDonated">
							<tags:formatCurrency value="${row.totalCostsDonated}"/><c:set var="costsDonated" value="${costsDonated+row.totalCostsDonated}"/>		
						</display:column>
					</c:if>
					<c:if test="${user.resultTotalCostsFinanced}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.results.totalCosts.financed" sortable="true" sortProperty="totalCostsFinanced">
							<tags:formatCurrency value="${row.totalCostsFinanced}"/><c:set var="costsFinanced" value="${costsFinanced+row.totalCostsFinanced}"/>	
						</display:column>
					</c:if>
					<c:if test="${user.resultNpv}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.npv.desc" sortable="true" sortProperty="npv">
							<tags:formatCurrency value="${row.npv}"/><c:set var="npv" value="${npv+row.npv}"/>
						</display:column>
					</c:if>
					<c:if test="${user.resultIrr}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.irr.desc" sortable="true" sortProperty="irr">
							<c:if test="${row.irr*100 gt 1000 or row.irr*100 lt -1000}"><spring:message code="misc.undefined"/></c:if>
							<c:if test="${row.irr*100 le 1000 and row.irr*100 ge -1000}">
								<tags:formatDecimal value="${row.irr*100}"/><c:set var="irr" value="${irr+row.irr}"/>
							</c:if>
						</display:column>
					</c:if>
					
					<c:if test="${user.resultNpvWith}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.npvWithDonation.desc" sortable="true" sortProperty="npvWithDonation">
							<tags:formatCurrency value="${row.npvWithDonation}"/><c:set var="npvWith" value="${npvWith+row.npvWithDonation}"/>
						</display:column>
					</c:if>
					<c:if test="${user.resultIrrWith}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.irrWithDonation.desc" sortable="true" sortProperty="irrWithDonation">
							<c:if test="${row.irrWithDonation*100 gt 1000 or row.irrWithDonation*100 lt -1000}"><spring:message code="misc.undefined"/></c:if>
							<c:if test="${row.irrWithDonation*100 le 1000 and row.irrWithDonation*100 ge -1000}">
								<tags:formatDecimal value="${row.irrWithDonation*100}"/><c:set var="irrWith" value="${irrWith+row.irrWithDonation}"/>
							</c:if>
						</display:column>
					</c:if>
				</c:if>
				<c:if test="${not model.filter.incomeGen}">
					<c:if test="${user.resultInvestDirect}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.investPerDirect" sortable="true" sortProperty="investPerBenefDirect">
							<tags:formatCurrency value="${row.investPerBenefDirect}"/><c:set var="investPerBenefDirect" value="${investPerBenefDirect+row.investPerBenefDirect}"/>
						</display:column>
					</c:if>			
					<c:if test="${user.resultInvestIndirect}"><c:set var="cols" value="${cols+1}"/>
						<display:column titleKey="project.investPerIndirect" sortable="true" sortProperty="investPerBenefIndirect">
							<tags:formatCurrency value="${row.investPerBenefIndirect}"/><c:set var="investPerBenefIndirect" value="${investPerBenefIndirect+row.investPerBenefIndirect}"/>
						</display:column>
					</c:if>
				</c:if>
				<c:if test="${user.resultBenefDirect}"><c:set var="cols" value="${cols+1}"/>
					<display:column titleKey="project.benefs.direct" property="beneDirect" sortable="true">
						<c:set var="benefDirect" value="${benefDirect+row.beneDirect}"/>
					</display:column>
				</c:if>
				<c:if test="${user.resultBenefIndirect}"><c:set var="cols" value="${cols+1}"/>
					<display:column titleKey="project.benefs.indirect" property="beneIndirect" sortable="true">
						<c:set var="benefIndirect" value="${benefIndirect+row.beneIndirect}"/>
					</display:column>
				</c:if>
				<display:column title="&nbsp;" media="html">
					<a href="../project/step1/${row.projectId}"><img src="../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" title="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
				</display:column>
				<display:column title="&nbsp;" media="html">
					<a onclick="openDownloadDialog('project','${row.projectId}/${row.downloadName}.riv');" ><img src="../img/export_riv.gif" alt="<spring:message code="misc.download"/>" title="<spring:message code="misc.download"/>" width="16" height="16" border="0"/>
				</a>
				</display:column>
				<display:column title="&nbsp;" media="html">
					<c:if test="${row.shared==true || row.technician.userId==user.userId }">
					<a onclick="confirmDelete('../project/step1/${row.projectId}/delete');"> <img src="../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" title="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"></a>
					</c:if>
				</display:column>
				<display:footer>
				<tr height="1"><td height="1" colspan="${cols+5}" class="Sum1"/></tr>
					<tr class="odd">
						<td style="text-align: left;"><spring:message code="misc.sum"/></td>
						<td/><c:if test="${user.resultTechnician}"><td/></c:if>
						<c:if test="${user.resultFieldOffice}"><td/></c:if>
						<c:if test="${user.resultStatus}"><td/></c:if>
						<c:if test="${user.resultProjectCategory}"><td/></c:if>
						<c:if test="${user.resultBenefCategory}"><td/></c:if>
						<c:if test="${user.resultEnviron}"><td/></c:if>
						<c:if test="${rivConfig.setting.admin1Enabled and user.resultAdminCategory1}"><td/></c:if>
						<c:if test="${rivConfig.setting.admin2Enabled and user.resultAdminCategory2}"><td/></c:if>
						<c:if test="${user.resultInvestTotal}"><td><tags:formatCurrency value="${investTotal}" noDecimals="true"/></td></c:if>
						<c:if test="${user.resultInvestOwn}"><td><tags:formatCurrency value="${investOwn}" noDecimals="true"/></td></c:if>
						<c:if test="${user.resultInvestDonated}"><td><tags:formatCurrency value="${investDonated}" noDecimals="true"/></td></c:if>
						<c:if test="${user.resultInvestFinanced}"><td><tags:formatCurrency value="${investFinanced}" noDecimals="true"/></td></c:if>
						<c:if test="${user.resultAnnualEmploy}"><td><tags:formatCurrency value="${annualEmployment}" noDecimals="true"/></td></c:if>
						<c:if test="${filter.incomeGen}">
							<c:if test="${user.resultAnnualNetIncome}"><td><tags:formatCurrency value="${annualNetIncome}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultWorkingCapital}"><td><tags:formatCurrency value="${wc}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultWcOwn}"><td><tags:formatCurrency value="${wcOwn}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultWcDonated}"><td><tags:formatCurrency value="${wcDonated}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultWcFinanced}"><td><tags:formatCurrency value="${wcFinanced}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultTotalCosts}"><td><tags:formatCurrency value="${costsTotal}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultTotalCostsOwn}"><td><tags:formatCurrency value="${costsOwn}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultTotalCostsDonated}"><td><tags:formatCurrency value="${costsDonated}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultTotalCostsFinanced}"><td><tags:formatCurrency value="${costsFinanced}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultNpv}"><td></td></c:if>
							<c:if test="${user.resultIrr}"><td></td></c:if>
							<c:if test="${user.resultNpvWith}"><td></td></c:if>
							<c:if test="${user.resultIrrWith}"><td></td></c:if>
						</c:if>
						<c:if test="${not model.filter.incomeGen}">
							<c:if test="${user.resultInvestDirect}"><td><tags:formatCurrency value="${investPerBenefDirect}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultInvestIndirect}"><td><tags:formatCurrency value="${investPerBenefIndirect}" noDecimals="true"/></td></c:if>
						</c:if>
						<c:if test="${user.resultBenefDirect}"><td><tags:formatCurrency value="${benefDirect}" noDecimals="true"/></td></c:if>
						<c:if test="${user.resultBenefIndirect}"><td><tags:formatCurrency value="${benefIndirect}" noDecimals="true"/></td></c:if>
						<td colspan="3"></td>
					</tr>
					<tr class="even">
						<td style="text-align: left;"><spring:message code="misc.average"/></td>
						<td/>
						<c:if test="${user.resultTechnician}"><td/></c:if>
						<c:if test="${user.resultFieldOffice}"><td/></c:if>
						<c:if test="${user.resultStatus}"><td/></c:if>
						<c:if test="${user.resultProjectCategory}"><td/></c:if>
						<c:if test="${user.resultBenefCategory}"><td/></c:if>
						<c:if test="${user.resultEnviron}"><td/></c:if>
						<c:if test="${rivConfig.setting.admin1Enabled and user.resultAdminCategory1}"><td/></c:if>
						<c:if test="${rivConfig.setting.admin2Enabled and user.resultAdminCategory2}"><td/></c:if>
						<c:if test="${user.resultInvestTotal}"><td><tags:formatCurrency value="${investTotal/fn:length(results)}" noDecimals="true"/></td></c:if>
						<c:if test="${user.resultInvestOwn}"><td><tags:formatCurrency value="${investOwn/fn:length(results)}" noDecimals="true"/></td></c:if>
						<c:if test="${user.resultInvestDonated}"><td><tags:formatCurrency value="${investDonated/fn:length(results)}" noDecimals="true"/></td></c:if>
						<c:if test="${user.resultInvestFinanced}"><td><tags:formatCurrency value="${investFinanced/fn:length(results)}" noDecimals="true"/></td></c:if>
						<c:if test="${user.resultAnnualEmploy}"><td><tags:formatCurrency value="${annualEmployment/fn:length(results)}" noDecimals="true"/></td></c:if>
						<c:if test="${filter.incomeGen}">
							<c:if test="${user.resultAnnualNetIncome}"><td><tags:formatCurrency value="${annualNetIncome/fn:length(results)}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultWorkingCapital}"><td><tags:formatCurrency value="${wc/fn:length(results)}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultWcOwn}"><td><tags:formatCurrency value="${wcOwn/fn:length(results)}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultWcDonated}"><td><tags:formatCurrency value="${wcDonated/fn:length(results)}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultWcFinanced}"><td><tags:formatCurrency value="${wcFinanced/fn:length(results)}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultTotalCosts}"><td><tags:formatCurrency value="${costsTotal/fn:length(results)}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultTotalCostsOwn}"><td><tags:formatCurrency value="${costsOwn/fn:length(results)}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultTotalCostsDonated}"><td><tags:formatCurrency value="${costsDonated/fn:length(results)}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultTotalCostsFinanced}"><td><tags:formatCurrency value="${costsFinanced/fn:length(results)}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultNpv}"><td><tags:formatCurrency value="${npv/fn:length(results)}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultIrr}"><td></td></c:if>
							<c:if test="${user.resultNpvWith}"><td><tags:formatCurrency value="${npvWith/fn:length(results)}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultIrrWith}"><td></td></c:if>
						</c:if>
						<c:if test="${not model.filter.incomeGen}">
							<c:if test="${user.resultInvestDirect}"><td><tags:formatDecimal value="${investPerBenefDirect/fn:length(results)}" noDecimals="true"/></td></c:if>
							<c:if test="${user.resultInvestIndirect}"><td><tags:formatDecimal value="${investPerBenefIndirect/fn:length(results)}" noDecimals="true"/></td></c:if>
						</c:if>
						<c:if test="${user.resultBenefDirect}"><td><tags:formatDecimal value="${benefDirect/fn:length(results)}" noDecimals="true"/></td></c:if>
						<c:if test="${user.resultBenefIndirect}"><td><tags:formatDecimal value="${benefIndirect/fn:length(results)}" noDecimals="true"/></td></c:if>
						<td colspan="3"></td>
					</tr>
					<tr><td colspan="12"  style="text-align: left;"><spring:message code="project.report.results.total"/> ${row_rowNum}</td></tr>
				</display:footer>
			</display:table>
		</tags:table>
		<c:if test="${fn:length(results) gt 0}">
			<spring:message code="settings.export"/>: 
			<a href="report/projectResults.pdf" target="_blank"><img src="../img/pdf.gif" alt="PDF" border="0"/> PDF</a>
			<a href="report/projectResults.xlsx" target="_blank"><img src="../img/xls.gif" alt="XLS" border="0"/> XLS</a>
			<a href="results/projectResults.zip" target="_blank"><img src="../img/zip.png" alt="ZIP" border="0"/> ZIP</a><br/>
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
	<img src="../img/export_riv.gif" alt="export" title="export" width="16" height="16" border="0"/>
	<spring:message code="export.local"/></a><br/><br/></li>
	<li><b><spring:message code="export.generic.title"/></b><br/>
	<spring:message code="export.generic.desc"/><br/>
	<a id="dd_generic">
	<img src="../img/export_riv.gif" alt="export" title="export" width="16" height="16" border="0"/>
	<spring:message code="export.generic"/></a></li></ul>
</div>
</body></html>