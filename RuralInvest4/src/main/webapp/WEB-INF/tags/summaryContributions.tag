<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<div id="summaryContributions" class="summary" title='<spring:message code="project.report.contributions"/>'>
	<spring:message code="project.report.summaryTables.info"/> <spring:message code="project.report.contributions"/>
	<a href="../../report/${project.projectId}/projectContributions.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
	<a href="../../report/${project.projectId}/projectContributions.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a>			
	
	<c:set var="cSummary" value="${project.contributionSummary}"/>
 		<tags:table>
		 	<display:table list="${cSummary}" id="contrib" requestURI="" cellspacing="0" cellpadding="0"
								export="false" htmlId="summaryContributionsTable">
		 		<display:column titleKey="projectContribution.contribType" sortable="true" sortProperty="key.contributionType" style="text-align:left;" headerClass="left">
					<c:if test="${contrib.key.contributionType=='0'}"><spring:message code="projectContribution.contribType.govtCentral"/></c:if>
					<c:if test="${contrib.key.contributionType=='1'}"><spring:message code="projectContribution.contribType.govtLocal"/></c:if>
					<c:if test="${contrib.key.contributionType=='2'}"><spring:message code="projectContribution.contribType.ngoLocal"/></c:if>
					<c:if test="${contrib.key.contributionType=='3'}"><spring:message code="projectContribution.contribType.ngoIntl"/></c:if>
					<c:if test="${contrib.key.contributionType=='5'}"><spring:message code="projectContribution.contribType.beneficiary"/></c:if>
					<c:if test="${contrib.key.contributionType=='4'}"><spring:message code="projectContribution.contribType.other"/></c:if>
				</display:column>
				<display:column titleKey="projectContribution.contributor" property="key.contributor" sortable="true" style="text-align:left;" headerClass="left"/>
				<c:forEach begin="1" end="${project.duration}" var="i">
					<display:column title="${i}">
						<c:if test="${project.perYearContributions}"><tags:formatDecimal value="${contrib.contributions[i-1]}" noDecimals="true" /></c:if>
						<c:if test="${not project.perYearContributions}"><tags:formatDecimal value="${contrib.contributions[0]}" noDecimals="true" /></c:if>
					</display:column>
				</c:forEach>
				<display:footer>
					<tr height="1"><td height="1" colspan="${project.duration+2}" class="Sum1"/></tr>
					<tr>
						<td/><td/>
						<c:forEach begin="1" end="${project.duration}" var="i">
							<c:set var="total" value="0"/>
							<c:forEach items="${cSummary}" var="myContrib">
								<c:set var="total">
									<c:if test="${project.perYearContributions}">${total+myContrib.contributions[i-1]}</c:if>
									<c:if test="${not project.perYearContributions}">${total+myContrib.contributions[0]}</c:if>
								</c:set>
							</c:forEach>
							<td>
								<tags:formatDecimal value="${total}" noDecimals="true" />
							</td>
						</c:forEach>
					</tr>
				</display:footer>
		 	</display:table>
		 </tags:table>
	</div>