<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="description" required="true" %>
<%@ attribute name="enabledField" required="true" %>
<%@ attribute name="weightField" required="true" %>
<tr>
	<td class="left"><spring:message code="${description}"/></td>
	<td><tags:dataentryCheckbox field="${enabledField}" /></td>
	<td>
		<div class="dataentry">
			<form:select path="${weightField}">
				<c:forEach var="i" begin="1" end="10">
					<form:option value="${i}" label="${i}"/>
				</c:forEach>
			</form:select>
		</div>
	</td>
</tr>