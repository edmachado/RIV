<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="block" required="true" type="riv.objects.project.BlockBase" %><%@ attribute name="edit" required="true" %>
<c:if test="${empty project}"><c:set var="project" value="${block.project}"/></c:if>
<c:set var="unique"><c:if test="${block.getClass().simpleName eq 'BlockWithout'}">${fn:length(block.project.blocks) + block.orderBy}</c:if><c:if test="${block.getClass().simpleName eq 'Block'}">${block.orderBy}</c:if></c:set>
<table id="blockChron" onselectstart="return false" bgcolor="#CCCCCC" cellpadding="0" cellspacing="1">
<thead>
	<tr bgcolor="#CCCCCC"><td>&nbsp;</td>
		<c:forEach var="i" begin="0" end="11">
			<td class="noColor" style="text-align:center;" colspan="2">
				<c:if test="${i+project.startupMonth<=12}"><spring:message code="calendar.month.${i+project.startupMonth}"/></c:if>
				<c:if test="${i+project.startupMonth>12}"><spring:message code="calendar.month.${(i+project.startupMonth)%12}"/></c:if>
			</td> 
		</c:forEach>
	</tr></thead><tbody>
	<c:forEach var="i" begin="0" end="2">
		<tr bgcolor="#F5F5F5" height="16">
			<td width="255" class="noColor"><b>
				<c:if test="${i==0}"><spring:message code="projectBlock.chronology.production"/></c:if>
				<c:if test="${i==1}"><spring:message code="projectBlock.chronology.harvest"/></c:if>
				<c:if test="${i==2}"><spring:message code="projectBlock.chronology.payment"/></c:if>
			</b>
			<c:if test="${edit}">
				<a href="#" onclick="selectAllChron(${i},${unique}, true)" style="margin-left:10px;font-style:italic;"><spring:message code="projectBlock.chronology.selectAll"/></a>
				| 
			 	<a href="#" onclick="selectAllChron(${i},${unique}, false)" style="font-style:italic;"><spring:message code="projectBlock.chronology.deselectAll"/></a></c:if>
			</td>
			<c:forEach var="j" begin="0" end="11"><c:forEach var="k" begin="0" end="1">
				<c:set var="name">${i}-${j}-${k}</c:set>
					<td width="16" id="${unique}-${name}"<c:if test="${edit}"> onclick="selDeselChron(${unique},'${name}')" onmouseover="style.cursor='pointer';" </c:if> 
					<c:if test='${block.chrons[name]!=null}'>style="background:#e7ae0f;"</c:if>>
					<c:if test="${edit}"><input type="hidden" id="ch${name}" name="ch${name}" value="${block.chrons[name]!=null}" /></c:if>&nbsp;
					</td></c:forEach></c:forEach>
		</tr>
	</c:forEach>
</tbody></table>