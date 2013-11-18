<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="block" type="riv.objects.project.BlockBase"  %><%@ attribute name="edit" %>
<c:if test="${empty project}"><c:set var="project" value="${block.project}"/></c:if>
<c:set var="unique"><c:if test="${block.getClass().simpleName eq 'BlockWithout'}">${fn:length(project.blocks) + block.orderBy}</c:if><c:if test="${block.getClass().simpleName eq 'Block'}">${block.orderBy}</c:if></c:set>

<c:set var="qtyText">
	<c:if test="${project.incomeGen}"><spring:message code="projectBlock.pattern.qty"/></c:if>
	<c:if test="${not project.incomeGen}"><spring:message code="projectActivity.pattern.qty"/></c:if>
</c:set>
<table id="prodPattern${unique}" border="0" cellspacing="0" cellpadding="1" bgcolor="#B5B6B5" align="left" style="border: 1px solid #B5B6B5;">
<tbody>
		<tr class="data-header">								
			<th style="text-align:left; font-size : 11px;" width="150"><spring:message code="projectBlock.pattern.years"/></th>
			<c:forEach var="i" begin="1" end="${project.duration}"><th style="font-size : 11px;">${i}</th></c:forEach>
		</tr>
		<tr class="odd">
			<td style="text-align:left; font-size : 11px;">${qtyText}</td>
				<c:forEach var="i" begin="1" end="${project.duration}">
					<td class="production-year"<c:if test="${edit==false}"> id="${unique}prod${i}"</c:if>>
						<c:set var="qty"><tags:formatDecimal value="${block.patterns[i].qty}"/></c:set>
						<c:if test="${edit==true}"><input type="text" id="pat${i}" name="pat${i}" size="1" value="${qty}"/></c:if>
						<c:if test="${edit==false}">${qty}</c:if>					
					
				</c:forEach>
</tr></tbody></table>