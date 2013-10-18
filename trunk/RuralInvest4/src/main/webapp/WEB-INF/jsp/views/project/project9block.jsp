<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:if test="${block.project.incomeGen}"><c:set var="blockType">projectBlock</c:set></c:if>
<c:if test="${not block.project.incomeGen}"><c:set var="blockType">projectActivity</c:set></c:if>
<html><head><title><spring:message code="${blockType}.name"/> 
<c:if test="${block.project.withWithout and not isWithout}">(<spring:message code="projectBlock.with.with"/>)</c:if><c:if test="${block.project.withWithout and isWithout}">(<spring:message code="projectBlock.with.without"/>)</c:if>
</title></head>
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
			<div class="dataentry">
				<tags:help title="${blockType}.cycleLength" text="${blockType}.cycleLength.help"><label><spring:message code="${blockType}.cycleLength"/></label></tags:help>
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
		</fieldset>
		
		<c:if test="${block.project.incomeGen}">
			<fieldset>
				<legend><tags:help title="projectBlock.chronology" text="projectBlock.chronology.help"><spring:message code="projectBlock.chronology"/></tags:help></legend>
				<b><spring:message code="projectBlock.instructions"/></b><br/>
				<tags:chronology block="${block}" edit="true"/>
			</fieldset>
		</c:if>
		<fieldset>
			<legend><tags:help title="${blockType}.pattern" text="${blockType}.pattern.help"><spring:message code="${blockType}.pattern"/></tags:help></legend>
			<tags:prodPattern block="${product.block}" edit="true"/>
		</fieldset>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
</body>
</html>