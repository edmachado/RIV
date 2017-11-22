<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<tags:table>
			<table id="indicators" cellspacing="0" cellpadding="0">
				<tbody>
					<tr>
						<td><spring:message code="project.projectName"/></td>
						<td class="left" id="result_projectName">${result.projectName}</td>
					</tr>
					<tr>
						<td><spring:message code="project.userCode"/></td>
						<td class="left" id="result_userCode">${result.userCode}</td>
					</tr>
					<tr>
						<td><spring:message code="project.user"/></td>
						<td class="left" id="result_technician">${result.technician.description}</td>
					</tr>
					<tr>
						<td><spring:message code="project.fieldOffice"/></td>
						<td class="left" id="result_fieldOffice"><tags:appConfigDescription ac="${result.fieldOffice}"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.status"/></td>
						<td class="left" id="result_status"><tags:appConfigDescription ac="${result.status }"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.category"/></td>
						<td class="left" id="result_category"><tags:appConfigDescription ac="${result.projCategory }"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.benefType"/></td>
						<td class="left" id="result_benefType"><tags:appConfigDescription ac="${result.beneficiary }"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.enviroCat"/></td>
						<td class="left" id="result_enviroCat"><tags:appConfigDescription ac="${result.enviroCategory}"/></td>
					</tr>
					<c:if test="${rivConfig.setting.admin1Enabled}">
						<tr>
							<td>${rivConfig.setting.admin1Title}</td>
							<td class="left" id="result_appConfig1"><tags:appConfigDescription ac="${result.appConfig1}"/></td>
						</tr>
					</c:if>
					<c:if test="${rivConfig.setting.admin2Enabled}">
						<tr>
							<td>${rivConfig.setting.admin2Title}</td>
							<td class="left" id="result_appConfig2"><tags:appConfigDescription ac="${result.appConfig2}"/></td>
						</tr>
					</c:if>
<%-- 					<c:if test="${rivConfig.setting.qualitativeEnabled}"> --%>
						<tr>
							<td><spring:message code="qualitativeAnalysis"/></td>
							<td id="result_qualitative"><fmt:formatNumber type = "percent" value = "${result.qualitative}" /></td>
						</tr>
<%-- 					</c:if> --%>
					<tr>
						<td><spring:message code="project.investTotal"/></td>
						<td id="result_investTotal"><tags:formatCurrency value="${result.investmentTotal}"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.investOwn"/></td>
						<td id="result_investOwn"><tags:formatCurrency value="${result.investmentOwn}"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.investExt"/></td>
						<td id="result_investDonated"><tags:formatCurrency value="${result.investmentDonated}"/></td>
					</tr>
					<tr>
						<td>
							<c:if test="${result.incomeGen}"><spring:message code="project.investCredit"/></c:if>
							<c:if test="${not project.incomeGen}"><spring:message code="project.investCredit.nongen"/></c:if>
						</td>
						<td id="result_investmentFinanced"><tags:formatCurrency value="${result.investmentFinanced}"/></td>
					</tr>
					<tr>
						<td><spring:message code="project.annualEmployment"/></td>
						<td id="result_annualEmployment"><tags:formatCurrency value="${result.annualEmployment}"/></td>
					</tr>
					
					<c:if test="${result.incomeGen}">
						<tr>
							<td><spring:message code="project.annualIncome"/></td>
							<td id="result_annualIncome"><tags:formatCurrency value="${result.annualNetIncome}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.workCapital"/></td>
							<td id="result_workCapital"><tags:formatCurrency value="${result.workingCapital}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.workCapital.own"/></td>
							<td id="result_wcOwn"><tags:formatCurrency value="${result.wcOwn}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.workCapital.donated"/></td>
							<td id="result_wcDonated"><tags:formatCurrency value="${result.wcDonated}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.workCapital.financed"/></td>
							<td id="result_wcFinanced"><tags:formatCurrency value="${result.wcFinanced}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.totalCosts"/></td>
							<td id="result_totalCosts"><tags:formatCurrency value="${result.totalCosts}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.totalCosts.own"/></td>
							<td id="result_totalCostsOwn"><tags:formatCurrency value="${result.totalCostsOwn}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.totalCosts.donated"/></td>
							<td id="result_totalCostsDonated"><tags:formatCurrency value="${result.totalCostsDonated}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.results.totalCosts.financed"/></td>
							<td id="result_totalCostsFinanced"><tags:formatCurrency value="${result.totalCostsFinanced}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.npv.desc"/></td>
							<td id="result_npv"><tags:formatCurrency value="${result.npv}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.irr.desc"/></td>
							<td id="result_irr">
								<c:if test="${result.irr*100 gt 1000 or row.irr*100 lt -1000}"><spring:message code="misc.undefined"/></c:if>
								<c:if test="${result.irr*100 le 1000 and row.irr*100 ge -1000}">
									<tags:formatDecimal value="${result.irr*100}"/>
								</c:if>
							</td>
						</tr>
						<tr>
							<td><spring:message code="project.npvWithDonation.desc"/></td>
							<td id="result_npvWithDonation"><tags:formatCurrency value="${result.npvWithDonation}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.irrWithDonation.desc"/></td>
							<td id="result_irrWithDonation">
								<c:if test="${result.irrWithDonation*100 gt 1000 or result.irrWithDonation*100 lt -1000}"><spring:message code="misc.undefined"/></c:if>
								<c:if test="${result.irrWithDonation*100 le 1000 and result.irrWithDonation*100 ge -1000}">
									<tags:formatDecimal value="${result.irrWithDonation*100}"/>
								</c:if>
							</td>
						</tr>
					</c:if>
					<c:if test="${not result.incomeGen}">
						<tr>
							<td><spring:message code="project.investPerDirect"/></td>
							<td id="result_investPerDirect"><tags:formatCurrency value="${result.investPerBenefDirect}"/></td>
						</tr>
						<tr>
							<td><spring:message code="project.investPerIndirect"/></td>
							<td id="result_investPerIndirect"><tags:formatCurrency value="${result.investPerBenefIndirect}"/></td>
						</tr>
					</c:if>
					<tr>
						<td><spring:message code="project.benefs.direct"/></td>
						<td id="result_beneDirect">${result.beneDirect}</td>
					</tr>
					<tr>
						<td><spring:message code="project.benefs.indirect"/></td>
						<td id="result_beneIndirect">${result.beneIndirect}</td>
					</tr>
				</tbody>
			</table>
		</tags:table>	