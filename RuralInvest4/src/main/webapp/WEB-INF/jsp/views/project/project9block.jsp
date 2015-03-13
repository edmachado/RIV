<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:if test="${block.project.incomeGen}"><c:set var="blockType">projectBlock</c:set></c:if>
<c:if test="${not block.project.incomeGen}"><c:set var="blockType">projectActivity</c:set></c:if>
<html><head><title><spring:message code="${blockType}.name"/> 
<c:if test="${block.project.withWithout and not isWithout}">(<spring:message code="projectBlock.with.with"/>)</c:if><c:if test="${block.project.withWithout and isWithout}">(<spring:message code="projectBlock.with.without"/>)</c:if>
</title>
<c:set var="noCycles"><c:if test="${not block.cycles}">.noCycles</c:if></c:set>
<c:set var="prodDescription">${blockType}.pattern.qty</c:set>
<c:set var="prodDescriptionHelp">${blockType}.pattern.help</c:set>
<c:set var="prodDescriptionNoCycles">${blockType}.pattern.noCycles.qty</c:set>
<c:set var="prodDescriptionNoCyclesHelp">${blockType}.pattern.noCycles.help</c:set>
<script>
$(function() {
	$("#cycles1").change(function() {
		toggle("cycledata");
		if(this.checked) {
			$('#prodPattern0').prev().children().first().attr('title','<spring:message code="${prodDescriptionHelp}"/>');
			$('#prodDescription').text('<spring:message code="${prodDescription}"/>');
	    } else {
			$('#prodPattern0').prev().children().first().attr('title','<spring:message code="${prodDescriptionNoCyclesHelp}"/>');
			$('#prodDescription').text('<spring:message code="${prodDescriptionNoCycles}"/>');
	    }
	});
});
</script>
</head>
<body>
	<form:form name="form" method="post" commandName="block">
		<tags:errors />   
		<fieldset>
			<legend><spring:message code="${blockType}.desc"/></legend>
			
			<c:if test="${empty param['rename']}">
				<tags:dataentry field="description" labelKey="${blockType}.name" helpText="${blockType}.name.help" helpTitle="${blockType}.name" size="20" maxLength="30" inputClass="text"/>
			</c:if>
			<c:if test="${not empty param['rename']}">
				<div class="dataentry">
					<span class="helpSpacer"></span>
					<label><spring:message code="${blockType}.name"/></label>
					<div style="display:inline-block;">
						${block.description}<br/>
						<form:input path="description" cssClass="text" size="20" maxlength="30" />
					</div>
					<span style="display:inline-block;margin-left:15px;"><img vspace="4" align="left" src="../../img/locked.gif"></span>
				</div>
			</c:if>
			<tags:dataentry field="unitType" labelKey="${blockType}.prodUnit" helpText="${blockType}.prodUnit" inputClass="text" size="20" maxLength="20" />
			<tags:dataentryCheckbox field="cycles" labelKey="projectBlock.usesCycles" helpText="projectBlock.usesCycles.help" helpTitle="projectBlock.usesCycles" />
			
			<div id="cycledata" <c:if test="${not block.cycles}">style="display:none;"</c:if><c:if test="${block.cycles}">style="display:block;"</c:if>>
				<div class="dataentry">
					<label><tags:help title="${blockType}.cycleLength" text="${blockType}.cycleLength.help"><spring:message code="${blockType}.cycleLength"/></tags:help></label>
					<input class="curlabel" type="text" tabindex="100" size="4" value="" readonly="readonly" disabled="disabled"/>
					<form:input path="cycleLength" cssClass="num" size="11"/>
					<form:select path="lengthUnit">
						<form:option value="0"><spring:message code="units.months"/></form:option>
						<form:option value="1"><spring:message code="units.weeks"/></form:option>
						<form:option value="2"><spring:message code="units.days.calendar"/></form:option>
						<form:option value="3"><spring:message code="units.days.week"/></form:option>
					</form:select>
				</div>
				<tags:dataentry field="cyclePerYear" labelKey="${blockType}.cyclePerYear" helpText="${blockType}.cycles.help" calcSignKey="units.perYear" />
				<c:if test="${block.project.incomeGen}">
					<tags:dataentry field="cycleFirstYear" labelKey="projectBlock.cycleFirstYear" helpText="projectBlock.cycleFirstYear.help" calcSignKey="units.perYear" />
					<tags:dataentry field="cycleFirstYearIncome" labelKey="projectBlock.cycleFirstYearIncome" helpText="projectBlock.cycleFirstYearIncome.help" calcSignKey="units.perYear" />
				</c:if>
			</div>
			
		</fieldset>
		
		<c:if test="${block.project.incomeGen}">
			<fieldset>
				<legend><tags:help title="projectBlock.chronology" text="projectBlock.chronology.help"><spring:message code="projectBlock.chronology"/></tags:help></legend>
				<b><spring:message code="projectBlock.instructions"/></b><br/>
				<div class="error"><form:errors path="chronError"/></div>
				<tags:chronology block="${block}" edit="true"/>
			</fieldset>
		</c:if>
		<fieldset>
			<legend><tags:help title="${blockType}.pattern" text="${prodDescriptionHelp}"><spring:message code="${blockType}.pattern"/></tags:help></legend>
			<div class="error"><form:errors path="patternsError"/></div>
			<tags:prodPattern block="${product.block}" edit="true"/>
		</fieldset>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
</body>
</html>