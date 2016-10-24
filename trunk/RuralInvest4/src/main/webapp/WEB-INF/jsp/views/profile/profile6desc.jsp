<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:if test="${profileProduct.profile.incomeGen}"><c:set var="title"><spring:message code="profile.step6"/><c:if test="${profileProduct.profile.withWithout and not isWithout}"> (<spring:message code="profileProduct.with.with"/>)</c:if><c:if test="${profileProduct.profile.withWithout and isWithout}"> (<spring:message code="profileProduct.with.without"/>)</c:if></c:set><c:set var="prodType">profileProduct</c:set></c:if>
<c:if test="${!profileProduct.profile.incomeGen}"><c:set var="title"><spring:message code="profile.step6.nongen"/></c:set><c:set var="prodType">profileActivity</c:set></c:if>
<html><head><title>${title}</title>
<c:set var="noCycles"><c:if test="${not profileProduct.cycles}">.noCycles</c:if></c:set>
<script>
$(function() {
	$("#cycles1").change(function() {
		toggle("cycledata");
	});
});
</script>
</head>
<body>
	<form:form name="form" method="post" commandName="profileProduct">
		<tags:errors/>
		<div style="width:500px;">
				<fieldset>
					<legend><spring:message code="${prodType}.desc"/></legend>
					
					<c:if test="${empty param['rename']}">
						<tags:dataentry field="description" labelKey="${prodType}.name" helpText="${prodType}.name.help" size="20" maxLength="50" inputClass="text"/>
					</c:if>
					<c:if test="${not empty param['rename']}">
						<div class="dataentry">
							<span class="helpSpacer"></span>
							<label><spring:message code="${prodType}.name"/></label>
							<div style="display:inline-block;">
								${profileProduct.description}<br/>
								<form:input path="description" cssClass="text" size="20" maxlength="30" />
							</div>
							<span style="display:inline-block;margin-left:15px;"><img vspace="4" align="left" src="../../img/locked.gif"></span>
						</div>
					</c:if>
					<tags:dataentry field="unitType" labelKey="${prodType}.prodUnit" helpText="${prodType}.prodUnit.help" inputClass="text" size="20" maxLength="50"/>
					<tags:dataentry field="unitNum" labelKey="${prodType}.numUnits" helpText="${prodType}.numUnits.help" />
					<tags:dataentryCheckbox field="cycles" labelKey="${prodType}.usesCycles" helpText="${prodType}.usesCycles.help" helpTitle="${prodType}.usesCycles" />
					
					<div id="cycledata" <c:if test="${not profileProduct.cycles}">style="display:none;"</c:if><c:if test="${profileProduct.cycles}">style="display:block;"</c:if>>
						<div class="dataentry">
							<label><tags:help title="${prodType}.cycleLength" text="${prodType}.cycleLength.help"><spring:message code="${prodType}.cycleLength"/></tags:help></label>
							<input class="curlabel" type="text" tabindex="100" size="4" value="" readonly="readonly" disabled="disabled"/>
							<form:input path="cycleLength" cssClass="num" size="11" />
							<form:select path="lengthUnit">
								<form:option value="0"><spring:message code="units.months"/></form:option>
								<form:option value="1"><spring:message code="units.weeks"/></form:option>
								<form:option value="2"><spring:message code="units.days.calendar"/></form:option>
								<form:option value="3"><spring:message code="units.days.week"/></form:option>
							</form:select>
						</div>
						<tags:dataentry field="cyclePerYear" labelKey="${prodType}.cycles" helpText="${prodType}.cycles.help" calcSignKey="units.perYear" />
					</div>
				</fieldset>
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
</body>
</html>