<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="field" required="true" %>
<%@ attribute name="labelKey" required="true" %>
<%@ attribute name="helpText" required="true" %>
<div class="dataentry">
	<label><tags:help text="${helpText}" title="${labelKey}"><spring:message code="${labelKey}"/></tags:help></label>
		<form:select path="${field}">
			<c:forEach var="i" begin="1" end="${project.duration}">
				<form:option value="${i}" label="${i}"/>
			</c:forEach>
		</form:select>
</div>