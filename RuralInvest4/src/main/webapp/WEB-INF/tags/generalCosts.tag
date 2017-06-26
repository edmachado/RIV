<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="costs" required="true" type="java.util.Map" %>
<%@ attribute name="duration" required="true" %>
<%@ attribute name="perYear" required="true" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="without" %>

<tags:table titleKey="${type}">
	<c:forEach var="year" begin="0" end="${duration-1}">
		<c:if test="${year eq 0 or perYear}">
			<c:set var="genTotal" value="0"/><c:set var="genOwn" value="0"/>
			<c:set var="beginStyle"><c:if test="${year eq 0}">display:block</c:if><c:if test="${year ne 0}">display:none</c:if></c:set>
			<div id="${type}${year}<c:if test="${without}">Without</c:if>" style="${beginStyle}">
				<c:set var="htmlId">${type}<c:if test="${without}">Without</c:if></c:set>
				<display:table list="${costs.get(year)}" id="row" cellspacing="0" cellpadding="0" export="false" htmlId="${htmlId}Table${year}"> 
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="${type}.description" property="parent.description" style="text-align:${left};" headerClass="left"/><%-- sortable="true" --%> 
					<display:column titleKey="${type}.unitType" style="text-align:${left};" headerClass="left"><%-- sortable="true" --%>
						<c:if test="${not fn:contains(type,'Personnel') }">
							${row.parent.unitType}
						</c:if>
						<c:if test="${fn:contains(type,'Personnel') }">
							<c:if test="${row.parent.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
							<c:if test="${row.parent.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
							<c:if test="${row.parent.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
							<c:if test="${row.parent.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
						</c:if>
					</display:column>
					<display:column titleKey="${type}.unitCost"><%-- sortable="true" sortProperty="unitCost"> --%>
						<tags:formatCurrency value="${row.parent.unitCost}"/>
					</display:column>
					<display:column titleKey="${type}.unitNum" sortProperty="unitNum"><%-- sortable="true" --%>
						<tags:formatDecimal value="${row.unitNum}"/>
					</display:column>
					<display:column titleKey="${type}.totalCost"><%-- sortable="true" sortProperty="total" --%>
						<tags:formatCurrency value="${row.total}"/><c:set var="genTotal" value="${genTotal+row.total}"/>
					</display:column>
					<display:column titleKey="${type}.ownResources"><%-- sortable="true" sortProperty="ownResources" --%>
						<tags:formatCurrency value="${row.ownResources}"/><c:set var="genOwn" value="${genOwn+row.ownResources}"/>
					</display:column>
					<display:column titleKey="${type}.external"><%-- sortable="true" sortProperty="external" --%>
						<tags:formatCurrency value="${row.external}"/>
					</display:column>
					<display:column title="&nbsp;">
						<c:if test="${not empty row.parent.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty row.parent.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<a name="copy" href="../item/${row.parent.projItemId}/copy">
								<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
							</a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${row_rowNum ne 1}">
								<a name="moveUp" href="../item/${row.parent.projItemId}/move?up=false">
									<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${row_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${row_rowNum ne fn:length(project.generals)}">
								<a name="moveDown" href="../item/${row.parent.projItemId}/move?up=true">
									<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${row_rowNum eq fn:length(project.generals)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" style="margin-left:5px;" media="html">
							<a href="../item/${row.parent.projItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="javascript:confirmDelete('../item/${row.parent.projItemId}/delete');"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
					<display:footer>
						<tr height="1"><td height="1" colspan="13" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/>
						<td><tags:formatCurrency value="${genTotal}" /></td>
						<td><tags:formatCurrency value="${genOwn}" /></td>
						<td><tags:formatCurrency value="${genTotal-genOwn}" /></td>
						<td/><td/><td/><td/><td/><td/></tr>
					</display:footer>
				</display:table>
			</div>
		</c:if>
	</c:forEach>

	<c:if test="${accessOK}">
		<div class="addNew"><a id="new${htmlId}" href="../item/-1?type=${type}<c:if test="${without}">Without</c:if>&projectId=${project.projectId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
	</c:if>
</tags:table>
