<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="menuType" value="search" scope="request"/><c:set var="currentStep" value="1" scope="request"/>
<html><head><title><spring:message code="search.searchReport"/></title>
</head>
<body>
	<form:form name="form" method="post" commandName="filterCriteria">
		<c:set var="hidden"><c:if test="${filterCriteria.objType=='igpf' || filter.objType=='nigpf'}">hidden</c:if></c:set>
		<h2><spring:message code="search.searchCriteria"/></h2>
		<div id="searchForm">
			<div class="dataentry">
				<b><spring:message code="search.searchFor"/></b><br/>
				<form:input size="16" path="freeText"/>
				<form:select id="type" path="objType" onchange="filterType_onchange(this)">
					<form:option value="igpj"><spring:message code="header.project"/> ($)</form:option>
					<form:option value="nigpj"><spring:message code="header.project"/> (:)</form:option>
					<form:option value="igpf"><spring:message code="header.profile"/> ($)</form:option>
					<form:option value="nigpf"><spring:message code="header.profile"/> (:)</form:option>
				</form:select>
				<input name="submit" type="submit" class="button" value="<spring:message code="search.search"/>"/>				
			</div>
			<br/>	
			
			<b><spring:message code="search.filterBy"/></b>
			<%-- INITIAL FILTER --%>
			<div class="filter-head" style="background-color:#EBEBEB">
				<img src="img/filter.gif" border="0" title="Filter">
				&raquo; <spring:message code="search.initialFilter"/>
			</div>
			<div class="filter-body" style="background-color:#EBEBEB">
				<div>
					<spring:message code="profile.fieldOffice"/><br/>
					<form:select path="offices" multiple="true" size="4">
						<form:options items="${rivConfig.fieldOffices.values()}" itemValue="configId" itemLabel="description"/>
					</form:select>
				</div>
				<div>
					<spring:message code="project.status"/><br/>
					<form:select path="statuses" multiple="true" size="4">
						<form:options items="${rivConfig.statuses.values()}" itemValue="configId" itemLabel="description"/>
					</form:select>
				</div>
			</div>
			<div style="background-color:#EBEBEB"><spring:message code="search.useCtrl"/></div>
			
			<%-- TYPE FILTER --%>
			<div class="filter-head" style="background-color:#E1E1E1">
				<img src="img/filter.gif" style="float:left;" border="0" title="Filter">
				&raquo; <spring:message code="search.typeFilter"/>
			</div>
			<div class="filter-body" style="background-color:#E1E1E1">
				<div>
					<spring:message code="project.technician"/><br/>
					<form:select path="users" multiple="true" size="4">
						<form:options items="${rivConfig.users.values()}" itemValue="userId" itemLabel="description"/>
					</form:select>
				</div>
				<div class="toggle ${hidden}">
					<spring:message code="project.category"/><br/>
					<form:select path="categories" multiple="true" size="4">
						<form:options items="${rivConfig.categories.values()}" itemValue="configId" itemLabel="description"/>
					</form:select>
				</div>
				<div class="toggle ${hidden}">
					<spring:message code="project.benefType"/><br/>
					<form:select path="beneficiaries" multiple="true" size="4">
						<form:options items="${rivConfig.beneficiaries.values()}" itemValue="configId" itemLabel="description"/>
					</form:select>
				</div>
				<div class="toggle ${hidden}">
					<spring:message code="project.enviroCat"/><br/>
					<form:select path="enviroCategories" multiple="true" size="4" style="width:120px;">
						<form:options items="${rivConfig.enviroCategories.values()}" itemValue="configId" itemLabel="description"/>
					</form:select>
				</div>
			</div>
			<c:if test="${rivConfig.setting.admin1Enabled || rivConfig.setting.admin2Enabled}">
				<div class="filter-body" style="background-color:#E1E1E1" class="toggle ${hidden}">
					<c:if test="${rivConfig.setting.admin1Enabled}">
						<div>
							${rivConfig.setting.admin1Title}<br/>
							<form:select path="appConfig1s" multiple="true" size="4">
								<form:options items="${rivConfig.appConfig1s.values()}" itemValue="configId" itemLabel="description"/>
							</form:select>
						</div>
					</c:if>
					<c:if test="${rivConfig.setting.admin2Enabled}">
						<div>
							${rivConfig.setting.admin2Title}<br/>
							<form:select path="appConfig2s" multiple="true" size="4">
								<form:options items="${rivConfig.appConfig2s.values()}" itemValue="configId" itemLabel="description"/>
							</form:select>
						</div>
					</c:if>
				</div>
			</c:if>
			<div style="background-color:#E1E1E1"><spring:message code="search.useCtrl"/></div>
			
			<%-- FINANCIAL FILTER --%>
			<div class="filter-head toggle ${hidden}" style="background-color:#D7D7D7">
				<img src="img/filter.gif" style="float:left;" border="0" title="Filter">
				&raquo; <spring:message code="search.financialFilter"/>
			</div>
			<div class="filter-body financial toggle ${hidden}" style="background-color:#D7D7D7">
				<div class="dataentry">
					<span><spring:message code="project.irr.long"/></span>
					<form:select path="irrCriteria" id="selectIrr">
						<form:option value="0" label=""/>
						<form:option value="1"><spring:message code="search.equals"/></form:option>
						<form:option value="2"><spring:message code="search.greaterThan"/></form:option>
						<form:option value="3"><spring:message code="search.lessThan"/></form:option>
					</form:select> 
					<form:input type="text" path="irrValue" id="irrPercentage" maxlength="11" size="8" value="" class="percent" />
					%
				</div>
				<div class="dataentry">
					<span><spring:message code="project.npv.long" /></span>
					<form:select path="npvCriteria" id="selectNpv">
						<form:option value="0" label="" />
						<form:option value="1"><spring:message code="search.equals"/></form:option>
						<form:option value="2"><spring:message code="search.greaterThan"/></form:option>
						<form:option value="3"><spring:message code="search.lessThan"/></form:option>
					</form:select> 
					<input type="text" disabled="" class="curlabel" value="${rivConfig.setting.currencySym}" size="4" />
					<form:input type="text" path="npvValue" id="npvValue" maxlength="11" size="8" value="" class="num" />
				</div>
				<div class="dataentry">
					<span><spring:message code="project.investTotal"/></span>
					<form:select path="totInvestCriteria" id="selectInvestment">
						<form:option value="0" label=""/>
						<form:option value="1"><spring:message code="search.equals"/></form:option>
						<form:option value="2"><spring:message code="search.greaterThan"/></form:option>
						<form:option value="3"><spring:message code="search.lessThan"/></form:option>
					</form:select> 
					<input type="text" disabled="disabled" class="curlabel" value="${rivConfig.setting.currencySym}" size="4" readonly="readonly" />
					<form:input type="text" path="totInvestValue" id="investmentValue" maxlength="11" size="8" value="" class="num" />		
				</div>
				<div class="dataentry">
					<span><spring:message code="project.investExt"/></span>
					<form:select path="externCriteria" id="selectExtern">
						<form:option value="0" label="" />
						<form:option value="1"><spring:message code="search.equals"/></form:option>
						<form:option value="2"><spring:message code="search.greaterThan"/></form:option>
						<form:option value="3"><spring:message code="search.lessThan"/></form:option>
					</form:select> 
					<input type="text" disabled="" class="curlabel" value="${rivConfig.setting.currencySym}" size="4" />
					<form:input type="text" path="externValue" id="externalValue" maxlength="11" size="8" value="" class="num" />
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>