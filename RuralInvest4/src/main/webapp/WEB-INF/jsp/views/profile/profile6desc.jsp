<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:if test="${profileProduct.profile.incomeGen}"><c:set var="title"><spring:message code="profile.step6"/><c:if test="${profileProduct.profile.withWithout and not isWithout}"> (<spring:message code="profileProduct.with.with"/>)</c:if><c:if test="${profileProduct.profile.withWithout and isWithout}"> (<spring:message code="profileProduct.with.without"/>)</c:if></c:set><c:set var="prodType">profileProduct</c:set></c:if>
<c:if test="${!profileProduct.profile.incomeGen}"><c:set var="title"><spring:message code="profile.step6.nongen"/></c:set><c:set var="prodType">profileActivity</c:set></c:if>
<html><head><title>${title}</title></head>
<body>
	<form:form name="form" method="post" commandName="profileProduct">
		<tags:errors/>
		<div style="width:500px;">
				<fieldset>
					<legend><spring:message code="${prodType}.desc"/></legend>
					
					<c:if test="${empty param['rename']}">
						<tags:dataentry field="description" labelKey="${prodType}.desc" size="20" maxLength="50" inputClass="text"/>
					</c:if>
					<c:if test="${not empty param['rename']}">
						<div class="dataentry">
							<span class="helpSpacer"></span>
							<label><spring:message code="${prodType}.desc"/></label>
							<div style="display:inline-block;">
								${profileProduct.description}<br/>
								<form:input path="description" cssClass="text" size="20" maxlength="30" />
							</div>
							<span style="display:inline-block;margin-left:15px;"><img vspace="4" align="left" src="../../img/locked.gif"></span>
						</div>
					</c:if>
					<tags:dataentry field="unitType" labelKey="${prodType}.prodUnit" helpText="${prodType}.prodUnit.help" inputClass="text" size="20" maxLength="50"/>
					<tags:dataentry field="unitNum" labelKey="${prodType}.numUnits" helpText="${prodType}.numUnits.help" />
					<div class="dataentry">
						<tags:help title="${prodType}.cycleLength" text="${prodType}.cycleLength.help"><label><spring:message code="${prodType}.cycleLength"/></label></tags:help>
						<form:input class="num" size="5" path="cycleLength" cssStyle="border-left:1px solid #003366 !important" />
						<form:select path="lengthUnit">
							<form:option value="0"><spring:message code="units.months"/></form:option>
							<form:option value="1"><spring:message code="units.weeks"/></form:option>
							<form:option value="2"><spring:message code="units.days.calendar"/></form:option>
							<form:option value="3"><spring:message code="units.days.week"/></form:option>
						</form:select>
					</div>
					<tags:dataentry field="cyclePerYear" labelKey="${prodType}.cycles" helpText="${prodType}.cycles.help" calcSignKey="units.perYear" />
<%-- 					<c:if test="${profileProduct.profile.withWithout}"> --%>
<!-- 						<div class="dataentry"> -->
<%-- 							<tags:help title="profileProduct.with.description" text="profileProduct.with.description.help"><label><spring:message code="profileProduct.with.description"/></label></tags:help> --%>
<%-- 							<form:select path="withWithout"> --%>
<%-- 								<form:option value="true"><spring:message code="profileProduct.with.with"/></form:option> --%>
<%-- 								<form:option value="false"><spring:message code="profileProduct.with.without"/></form:option> --%>
<%-- 							</form:select> --%>
<!-- 						</div> -->
<%-- 					</c:if> --%>
				</fieldset>
		</div>
		<tags:submit><spring:message code="misc.saveItem"/></tags:submit>
	</form:form>
</body>
</html>