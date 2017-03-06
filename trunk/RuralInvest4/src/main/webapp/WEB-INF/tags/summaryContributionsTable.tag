<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="donorOrder" required="true" %>
<tags:table>
		<table cellspacing="0" cellpadding="0">
			<thead><tr><th style="width:100px;"></th><c:forEach begin="0" end="${project.duration-1}" var="i"><th>${i+1}</th></c:forEach></tr></thead>
			<tbody>
				<tr>
					<td class="left" style="width:100px;"><spring:message code="project.report.contributionSummary.investments"/></td>
					<c:forEach begin="0" end="${project.duration-1}" var="y">
						<td style="width:70px;"><tags:formatDecimal value="${summary[donorOrder][0][y]}" noDecimals="true" />
					</c:forEach>
				</tr>
				<tr>
					<td class="left" style="width:100px;"><spring:message code="project.report.contributionSummary.general"/></td>
					<c:forEach begin="0" end="${project.duration-1}" var="y">
						<td><tags:formatDecimal value="${summary[donorOrder][1][y]}" noDecimals="true" />
					</c:forEach>
				</tr>
				<tr>
					<td class="left" style="width:100px;"><spring:message code="project.report.contributionSummary.operation"/></td>
					<c:forEach begin="0" end="${project.duration-1}" var="y">
						<td><tags:formatDecimal value="${summary[donorOrder][2][y]}" noDecimals="true" />
					</c:forEach>
				</tr>
				<c:if test="${not project.incomeGen}">
					<tr>
						<td class="left" style="width:100px;"><spring:message code="project.report.contributionSummary.contributions"/></td>
						<c:forEach begin="0" end="${project.duration-1}" var="y">
							<td><tags:formatDecimal value="${summary[donorOrder][3][y]}" noDecimals="true" />
						</c:forEach>
					</tr>
				</c:if>
			</tbody>
			<tfoot>
			<tr height="1">
			<td class="Sum1" height="1" colspan="${project.duration+1}"></td>
			</tr>
				<tr>
					<td class="left" style="width:100px;"><spring:message code="project.report.contributionSummary.total"/></td>
					<c:forEach begin="0" end="${project.duration-1}" var="y">
						<td><tags:formatDecimal value="${summary[donorOrder][4][y]}" noDecimals="true" />
					</c:forEach>
				</tr>
			</tfoot>
		
		
		</table>
	</tags:table>