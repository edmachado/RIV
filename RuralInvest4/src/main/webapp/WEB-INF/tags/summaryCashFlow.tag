<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<div id="summaryCashFlow" class="summary" title='<spring:message code="project.report.cashFlowNongen"/> '>
		<tags:table>
				<display:table list="${cashFlowSummary}" id="row" requestURI=""  cellspacing="0" cellpadding="0"
					export="false" htmlId="cashFlowSummary">
					<display:column title="" class="left">
						<c:choose>
							<c:when test="${row_rowNum eq 1}">
								<spring:message code="reference.incomes"/>
							</c:when>
							<c:when test="${row_rowNum eq 2}">
								<spring:message code="project.report.cashFlowNongen.costs"/>
							</c:when>
							<c:when test="${row_rowNum eq 3}">
								<b><spring:message code="project.report.summary.total"/></b>
							</c:when>
							<c:when test="${row_rowNum eq 4}">
								<b><spring:message code="project.report.summary.cumulative"/></b>
							</c:when>
						</c:choose>
					</display:column>
					<c:forEach var="i" begin="0" end="${project.duration-1}">
						<display:column title="${i+1}"  style="${style }">
							<tags:formatCurrency value="${row[i]}" noDecimals="true"/>
						</display:column>
					</c:forEach>
				</display:table>
			</tags:table>
		</div>