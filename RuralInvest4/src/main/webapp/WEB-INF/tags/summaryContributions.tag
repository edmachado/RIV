<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<div id="summaryContributions" class="summary" title='<spring:message code="project.report.contributions"/>'>
	<spring:message code="project.report.summaryTables.info"/> <spring:message code="project.report.contributions"/>
	<a href="../../report/${project.projectId}/projectContributions.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
	<a href="../../report/${project.projectId}/projectContributions.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a>			
	
 		<tags:table>
			<table id="summaryContributionsTable" cellspacing="0" cellpadding="0">
				<thead>
					<tr>
						<th class="left"><spring:message code="projectContribution.contributor" /></th>
						<th class="left"><spring:message code="projectContribution.contribType" /></th>
						<c:forEach begin="1" end="${project.duration}" var="i"><th>${i}</th></c:forEach>
					</tr>
				</thead>
				<tbody><c:set var="num" value="1" />
					<c:forEach var="donor" items="${project.donors}">
						<c:if test="${not donor.notSpecified}">
							<tr>
								<td class="left">
									<c:choose>
										<c:when test="${donor.description eq 'state-public'}"><spring:message code="project.donor.statePublic"/></c:when>
										<c:otherwise>${donor.description}</c:otherwise>
									</c:choose>
								</td>
								<td class="left">
									<tags:contribType type="${donor.contribType}"/>
								</td>
								<c:set var="row" value="${project.contributionSummary[donor.orderBy]}"/>
								<c:forEach begin="1" end="${project.duration}" var="i">	
									<td>
										<c:if test="${project.perYearContributions}"><tags:formatDecimal value="${row[i-1]}" noDecimals="true" /></c:if>
										<c:if test="${not project.perYearContributions}"><tags:formatDecimal value="${row[0]}" noDecimals="true" /></c:if>
									</td>
								</c:forEach>				
							</tr>
						</c:if>
						<c:if test="${donor.notSpecified}"><c:set var="notSpecifiedDonor" value="${donor}"/></c:if>
					</c:forEach>
					<tr>
						<td class="left"><spring:message code="project.donor.notSpecified"/></td>
						<td class="left">
							<tags:contribType type="${notSpecifiedDonor.contribType}"/>
						</td>
						<c:set var="row" value="${project.contributionSummary[notSpecifiedDonor.orderBy]}"/>
						<c:forEach begin="1" end="${project.duration}" var="i">	
							<td>
								<c:if test="${project.perYearContributions}"><tags:formatDecimal value="${row[i-1]}" noDecimals="true" /></c:if>
								<c:if test="${not project.perYearContributions}"><tags:formatDecimal value="${row[0]}" noDecimals="true" /></c:if>
							</td>
						</c:forEach>				
					</tr>
				</tbody>
				<tfoot>
					<tr height="1"><td height="1" colspan="${project.duration+2}" class="Sum1"/></tr>
					<tr>
						<td/><td/>
						<c:forEach begin="1" end="${project.duration}" var="i">	
							<c:set var="total" value="0"/>
							<c:forEach var="donor" items="${project.donors}">
								<c:set var="row" value="${project.contributionSummary[donor.orderBy]}"/>
								<c:set var="total">
									<c:if test="${project.perYearContributions}">${total+row[i-1]}</c:if>
									<c:if test="${not project.perYearContributions}">${total+row[0]}</c:if>
								</c:set>
							</c:forEach>
							<td>
								<tags:formatDecimal value="${total}" noDecimals="true" />
							</td>
						</c:forEach>
					</tr>
				</tfoot>
				</table>
		 </tags:table>
	</div>