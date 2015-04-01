<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="donorOrder" required="true" %>
<tags:table>
		<table cellspacing="0" cellpadding="0">
			<thead><tr><th style="width:100px;"></th><c:forEach begin="0" end="${project.duration-1}" var="i"><th>${i+1}</th></c:forEach></tr></thead>
			<tbody>
				<tr>
					<td class="left" style="width:100px;">INVESTMENTS</td>
					<c:forEach begin="0" end="${project.duration-1}" var="y">
						<td style="width:70px;"><tags:formatDecimal value="${summary[donorOrder][0][y]}" noDecimals="true" />
					</c:forEach>
				</tr>
				<tr>
					<td class="left" style="width:100px;">GENERAL COSTS</td>
					<c:forEach begin="0" end="${project.duration-1}" var="y">
						<td><tags:formatDecimal value="${summary[donorOrder][1][y]}" noDecimals="true" />
					</c:forEach>
				</tr>
				<tr>
					<td class="left" style="width:100px;">OPERATIONS</td>
					<c:forEach begin="0" end="${project.duration-1}" var="y">
						<td><tags:formatDecimal value="${summary[donorOrder][2][y]}" noDecimals="true" />
					</c:forEach>
				</tr>
				<tr>
					<td class="left" style="width:100px;">CONTRIBUTIONS</td>
					<c:forEach begin="0" end="${project.duration-1}" var="y">
						<td><tags:formatDecimal value="${summary[donorOrder][3][y]}" noDecimals="true" />
					</c:forEach>
				</tr>
			</tbody>
			<tfoot>
			<tr height="1">
			<td class="Sum1" height="1" colspan="${project.duration+1}"></td>
			</tr>
				<tr>
					<td class="left" style="width:100px;">TOTAL</td>
					<c:forEach begin="0" end="${project.duration-1}" var="y">
						<td><tags:formatDecimal value="${summary[donorOrder][4][y]}" noDecimals="true" />
					</c:forEach>
				</tr>
			</tfoot>
		
		
		</table>
	</tags:table>