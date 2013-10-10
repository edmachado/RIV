<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="field" required="true" %>
<%@ attribute name="helpText" %>
<%@ attribute name="helpTitle" %>
<%@ attribute name="multiline" %>
<fieldset>
	<legend>
		<c:choose>
			<c:when test="${not empty helpText}"><tags:help text="${helpText}" title="${helpTitle}"><jsp:doBody/></tags:help></c:when>
			<c:otherwise><jsp:doBody/></c:otherwise>
		</c:choose>
	</legend>
	<div class="dataentry">
		<c:choose>
			<c:when test="${empty multiline}">
				<form:input path="${field}" cssClass="text" size="120"/>
			</c:when>
			<c:otherwise>
				<form:textarea path="${field}" cols="98" rows="15" />
			</c:otherwise>
		</c:choose>
		<form:errors path="${field}" cssClass="error" element="div" />
	</div>
</fieldset>