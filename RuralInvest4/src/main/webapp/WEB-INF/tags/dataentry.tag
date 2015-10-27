<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="field" required="true" %>
<%@ attribute name="labelKey" %>
<%@ attribute name="label" %>
<%@ attribute name="inputClass" %>
<%@ attribute name="size" %>
<%@ attribute name="maxLength" %>
<%@ attribute name="helpText" %>
<%@ attribute name="helpTitle" %>
<%@ attribute name="calcSignKey" %>
<%@ attribute name="calcSign" %>

<%@ attribute name="onmouseout" %>
<%@ attribute name="currency" %>
<%@ attribute name="calculated" %>

<%@ attribute name="button" %>

<c:if test="${empty inputClass}"><c:set var="inputClass" value="num"/></c:if>
<c:if test="${empty size}">
	<c:if test="${inputClass eq 'num'}"><c:set var="size" value="11"/></c:if>
	<c:if test="${inputClass ne 'num'}"><c:set var="size" value="17"/></c:if>
</c:if>
<c:if test="${empty maxLength}"><c:set var="maxLength" value="${size}"/></c:if>
<c:if test="${not empty labelKey}"><c:set var="label"><spring:message code="${labelKey}"/></c:set></c:if>

<div class="dataentry">
	<c:if test="${not empty helpText}"><form:label path="${field}"><tags:help text="${helpText}" title="${helpTitle}">${label}</tags:help></form:label></c:if>
	<c:if test="${empty helpText}"><form:label path="${field}">${label}</form:label></c:if>
	
	<c:if test="${inputClass eq 'num'}">
		<c:if test="${not empty currency}"><c:set var="curlabelValue" value="${rivConfig.setting.currencySym}"/></c:if>
	
		<c:if test="${not empty calculated}"><c:set var="readonlyClass" value=" readonly"/></c:if>
		<input size="5" class="curlabel ${readonlyClass}"  value="${curlabelValue}"/><%--disabled --%>
	</c:if>
	
	<c:if test="${empty calculated}">
		<c:if test="${not empty currency}"><c:set var="onkeyup" value="javascript:commasKeyup(this);"/></c:if>
		<form:input path="${field}" cssClass="${inputClass}" size="${size}" maxLength="${maxLength}" onkeyup="${onkeyup}" onmouseout="${onmouseout}"/>
	</c:if>
	
	<c:if test="${not empty calculated}">
		<c:set var="inputClass" value="${inputClass} readonly"/>
		<c:set var="value"><tags:formatCurrency value="${value}"/></c:set>
		<form:input path="${field}" disabled="true" size="${size}" class="${inputClass}"/>
		<%--<input size="${size}" type="text" class="${inputClass}" id="${field}" disabled value="${value}"/> --%>
	</c:if>
	
	<c:choose>
		<c:when test="${not empty calcSignKey}"><span><spring:message code="${calcSignKey}"/></span></c:when>
		<c:when test="${not empty calcSign}"><span>${calcSign}</span></c:when>
		<c:when test="${not empty button}"><span><a id="donation-button" href="#"><span class="ui-icon ui-icon-circle-triangle-e" style="display:inline-block"></span></a></span></c:when>
		<c:otherwise></c:otherwise>
	</c:choose>
	<form:errors path="${field}" cssClass="error" element="div" />
</div>