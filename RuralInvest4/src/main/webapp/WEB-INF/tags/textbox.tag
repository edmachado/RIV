<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="field" required="true" %><%@ attribute name="helpText" %><%@ attribute name="helpTitle" %><%@ attribute name="multiline" %>
<%@ attribute name="qualitativeEnabled" %><%@ attribute name="qualitativeField" %><%@ attribute name="qualitativeValue" %>
<fieldset>
	<legend>
		<c:choose>
			<c:when test="${not empty helpText}"><tags:help text="${helpText}" title="${helpTitle}"><jsp:doBody/></tags:help></c:when>
			<c:otherwise><jsp:doBody/></c:otherwise>
		</c:choose>
	</legend>
	<div class="dataentry">
		<c:if test="${qualitativeEnabled}">
			<c:forEach var="i" begin="1" end="5">
				<img id="${qualitativeField}Star${i}" onclick="stellate('${qualitativeField}',${i})"
				<c:if test="${i le qualitativeValue}">src="../../img/star2.gif"</c:if>
				<c:if test="${i gt qualitativeValue}">src="../../img/star1.gif"</c:if>
				>
			</c:forEach>
		 <form:hidden id="${qualitativeField}" path="${qualitativeField}" />
		 <br/>
		</c:if>
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