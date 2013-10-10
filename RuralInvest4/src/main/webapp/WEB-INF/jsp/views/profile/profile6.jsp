<%@ page pageEncoding="UTF-8"%><%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:if test="${profile.incomeGen}"><c:set var="title"><spring:message code="profile.step6"/></c:set><c:set var="prodType">profileProduct</c:set></c:if>
<c:if test="${!profile.incomeGen}"><c:set var="title"><spring:message code="profile.step6.nongen"/></c:set><c:set var="prodType">profileActivity</c:set></c:if>
<html><head><title>${title}</title>
<script>
$(function() { $("#confirmDelete").dialog({
	bgiframe: true, autoOpen: false, resizable: false, height:300, width:400, modal: true,
	overlay: { backgroundColor: '#000', opacity: 0.5 },
	buttons: {
		Cancel: function() { $(this).dialog('close'); },
		'<spring:message code="misc.deleteItem"/>': function() { location.href=$('#deleteUrl').val(); }		
	}
});
});
</script>
</head>
<body>
<form:form name="form" method="post" commandName="profile">
	<tags:errors />
	<div align="right">
		<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a href="../../report/${profile.profileId}/profileProduct.xlsx?template=true"><spring:message code="export.downloadTemplate"/></a><br/>
	 	<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a href="../../report/${profile.profileId}/profileProduct.xlsx"><spring:message code="export.download"/></a><br/>
	 	<%-- 
		<img src="../../img/xls.gif" alt="Excel" title="Excel"/> <a href="importXls.htm?type=profileInvestment"><spring:message code="import.importExcel"/></a>
		--%>
 	</div>
 	<c:if test="${accessOK}">
		<div align="left"><a id="addProduct" href="../product/-1?profileId=${profile.profileId}"><img src="../../img/product.gif" border="0"/>&nbsp;<c:if test="${profile.incomeGen}"><spring:message code="${prodType}.addNew"/></c:if><c:if test="${!profile.incomeGen}"><spring:message code="profileActivity.addNew"/></c:if>&nbsp;&nbsp;</a></div>
	</c:if>
	
	<c:forEach var="productEntry" items="${profile.products}">
		<c:set var="incTotal" value="0"/><c:set var="inpTotal" value="0"/><c:set var="labTotal" value="0"/>
		<c:set var="product" value="${productEntry}" scope="request"/><a name="b${product.productId}"></a>
		<c:set var="incomeTitle"><c:if test="${profile.incomeGen}"><spring:message code="profileProductIncome"/></c:if><c:if test="${not profile.incomeGen}"><spring:message code="profileActivityCharge"/></c:if> <tags:blockExplanation block="${productEntry}"/></c:set>
		<c:set var="inputTitle"><spring:message code="profileProductInput"/>  <tags:blockExplanation block="${productEntry}"/></c:set>
		<c:set var="labourTitle"><spring:message code="profileProductLabour"/>  <tags:blockExplanation block="${productEntry}"/></c:set>
				
		<tags:tableContainer title="${product.description}">
<%-- 			<c:if test="${profile.incomeGen}"> --%>
				<fieldset><legend><spring:message code="${prodType}.desc"/></legend>
					<table id="descriptionTable" width="100%" border="0" cellspacing="2" cellpadding="2">
					   <tr>
					   
				   		<td colspan="2">
							<img src="../../img/xls.gif" alt="Excel" title="Excel"/> 
							<a href="xlsDownload.xls?type=profileProduct&id=${product.productId}"><spring:message code="export.download"/></a><br/>
 						</td>
					   	<c:if test="${accessOK}">
					   		<td>
					   		<a href="../product/${product.productId}/clone"><img src="../../img/duplicate.gif" border="0"/> <spring:message code="${prodType}.clone"/></a>
					   		</td>
					   	</c:if>
					   	<c:if test="${not accessOK}">
					   		<td/>
					   	</c:if>
	 					<td colspan="2"/>
					   	</tr>
					    <tr>
							<td><tags:help title="${prodType}.name" text="${prodType}.name.help"><b><spring:message code="${prodType}.name"/></b></tags:help></td>
							<td><span id="${product.orderBy}description">${product.description}</span></td>
							<c:if test="${accessOK}">
								<td><a href="../product/${product.productId}"><img src="../../img/edit.png" border="0"/> <spring:message code="${prodType}.editDesc"/></a></td>
						    	<td><a href="javascript:confirmDelete('../product/${product.productId}/delete');"><img src="../../img/delete.gif" border="0"/> <spring:message code="${prodType}.delete"/></a></td>
					   		</c:if>
					   		<c:if test="${not accessOK}"><td colspan="2"/></td></c:if>
							
					    </tr>
					    <tr>
							<td><tags:help title="${prodType}.prodUnit" text="${prodType}.prodUnit.help"><b><spring:message code="${prodType}.prodUnit"/></b></tags:help></td>
							<td><span id="${product.orderBy}unitType">${product.unitType}</span></td>
							<td><tags:help title="${prodType}.cycleLength" text="${prodType}.cycleLength.help"><b><spring:message code="${prodType}.cycleLength"/></b></tags:help></td>
							<td><span id="${product.orderBy}cycleLength"><tags:formatDecimal value="${product.cycleLength}"/></span>
								<c:if test="${product.lengthUnit==0}"><spring:message code="units.months"/></c:if>
								<c:if test="${product.lengthUnit==1}"><spring:message code="units.weeks"/></c:if>
								<c:if test="${product.lengthUnit==2}"><spring:message code="units.days.calendar"/></c:if>
								<c:if test="${product.lengthUnit==3}"><spring:message code="units.days.week"/></c:if>
							</td>
					    </tr>
						<tr>
							<td><tags:help title="${prodType}.numUnits" text="${prodType}.numUnits.help"><b><spring:message code="${prodType}.numUnits"/></b></tags:help></td>
							<td><span id="${product.orderBy}unitNum"><tags:formatDecimal value="${product.unitNum}"/></span></td>
							<td><tags:help title="${prodType}.cycles" text="${prodType}.cycles.help"><b><spring:message code="${prodType}.cycles"/></b></tags:help></td>
							<td><span id="${product.orderBy}cyclePerYear"><tags:formatDecimal value="${product.cyclePerYear}"/></span> <spring:message code="units.perYear"/></td>
						</tr>
						<tr>
							<c:if test="${profile.withWithout}">
	 							<td><tags:help title="profileProduct.with.description" text="profileProduct.with.description.help">
									<b><spring:message code="${prodType}.with.description"/></b></tags:help></td>
								<td>
									<c:if test="${product.withWithout}"><spring:message code="projectBlock.with.with"/></c:if>
									<c:if test="${!product.withWithout}"><spring:message code="projectBlock.with.without"/></c:if>
								</td>	
							</c:if>
							<c:if test="${not profile.withWithout}">
								<td colspan="2"/>
							</c:if>
						
							<c:if test="${accessOK}">
								<td>
									<c:if test="${product.orderBy ne 0}">
										<a name="moveUp" href="../product/${product.productId}/move?up=false">
										<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
										</a>
									</c:if>
									
									<c:if test="${product.orderBy ne fn:length(profile.products)-1}">
									<a name="moveDown" href="../product/${product.productId}/move?up=true">
										<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
									</a>
									</c:if>
								</td>
								<td/>
							</c:if>
							<c:if test="${not accessOK}">
					    		<td colspan="2"/>
					    	</c:if>
						</tr>
					</table>
			</fieldset>
			<br/>
			<tags:table title="${incomeTitle}">
				<display:table list="${product.profileIncomes}" id="inc" requestURI="" cellspacing="0" cellpadding="0"
					export="false" htmlId="incomeTable${product.orderBy}">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="profileProductIncome.desc" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="profileProductIncome.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="profileProductIncome.unitNum" sortProperty="unitNum" sortable="true">
						<tags:formatDecimal value="${inc.unitNum}"/>
					</display:column>
					<display:column titleKey="profileProductIncome.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${inc.unitCost}"/>
					</display:column>
					<c:if test="${profile.incomeGen}">
						<display:column titleKey="profileProductIncome.transport" sortable="true" sortProperty="transport">
							<tags:formatCurrency value="${inc.transport}"/>
						</display:column>
					</c:if>
					<display:column titleKey="profileProductIncome.total" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${inc.total}"/><c:set var="incTotal" value="${incTotal+inc.total}"/>
					</display:column>
					<display:column title="&nbsp;">
						<c:if test="${not empty inc.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty inc.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<a name="copy" href="../prodItem/${inc.prodItemId}/copy">
								<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
							</a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${inc_rowNum ne 1}">
								<a name="moveUp" href="../prodItem/${inc.prodItemId}/move?up=false">
									<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${inc_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${inc_rowNum ne fn:length(product.profileIncomes)}">
								<a name="moveDown" href="../prodItem/${inc.prodItemId}/move?up=true">
									<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${inc_rowNum eq fn:length(product.profileIncomes)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../prodItem/${inc.prodItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../prodItem/${inc.prodItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
					<display:footer>
						<tr height="1"><td height="1" colspan="12" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/>
					<c:if test="${profile.incomeGen}"><td/></c:if>
					<td><tags:formatCurrency value="${incTotal}" /></td><td/><td/><td/><td/><td/><td/></tr>
					</display:footer>
				</display:table> 
				<c:if test="${accessOK}">
					<div class="addNew"><a id="newIncome${product.orderBy}" href="../prodItem/-1?itemType=income&productId=${product.productId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>
<%-- 			</c:if> --%>
			<%--<c:if test="${not profile.incomeGen}">
				<fieldset>
				  	<legend>
				  		<span class="header-small"><spring:message code="profileActivity.desc"/></span>
				  	</legend>
							<table width="100%" border="0" cellspacing="2" cellpadding="2">
						   	<tr>
						   		<td>
									<img src="../../img/xls.gif" alt="Excel" title="Excel"/> 
									<a href="xlsDownload.xls?type=profileActivity&id=${product.productId}"><spring:message code="export.download"/></a><br/>
		 						</td>
						   		<c:if test="${accessOK}">
							   		<td>
							   			<a href="cloneProduct.htm?productId=${product.productId}"><img src="../../img/duplicate.gif" border="0"/> <spring:message code="profileActivity.clone"/></a>
							   		</td>
							   		<td><a href="profProduct.htm?productId=${product.productId}"><img src="../../img/edit.png" border="0"/> <spring:message code="profileActivity.editDesc"/></a></td>
							    	<td><a href="javascript:confirmDelete('deleteProfProduct.htm?productId=${product.productId}');"><img src="../../img/delete.gif" border="0"/> <spring:message code="profileActivity.delete"/></a></td>
							   	</c:if>
							   	
						   		<c:if test="${not accessOK}"><td colspan="3"/></td></c:if>
							   	</tr>
					    <tr>
							<td><tags:help title="profileActivity.name" text="profileActivity.name.help"><b><spring:message code="profileActivity.name"/></b></tags:help></td>
							<td><c:out value="${product.description}"/></td>
							<td><tags:help title="profileActivity.cycleLength" text="profileActivity.cycleLength.help"><b><spring:message code="profileActivity.cycleLength"/></b></tags:help></td>
							<td><c:out value="${product.cycleLength}"/>
								<c:if test="${product.lengthUnit==0}"><spring:message code="units.months"/></c:if>
								<c:if test="${product.lengthUnit==1}"><spring:message code="units.weeks"/></c:if>
								<c:if test="${product.lengthUnit==2}"><spring:message code="units.days.calendar"/></c:if>
								<c:if test="${product.lengthUnit==3}"><spring:message code="units.days.week"/></c:if>
							</td>
					    </tr>
					    <tr>
							<td><tags:help title="profileActivity.prodUnit" text="profileActivity.prodUnit.help"><b><spring:message code="profileActivity.prodUnit"/></b></tags:help></td>
							<td><c:out value="${product.unitType}"/></td>
							<td><tags:help title="profileActivity.cycles" text="profileActivity.cycles.help">
								<b><spring:message code="profileActivity.cycles"/></b></tags:help></td>
							<td><c:out value="${product.cyclePerYear}"/> <spring:message code="units.perYear"/></td>
					    </tr>
						<tr>
							<td><tags:help title="profileActivity.numUnits" text="profileActivity.numUnits.help">
								<b><spring:message code="profileActivity.numUnits"/></b></tags:help></td>
							<td><c:out value="${product.unitNum}"/></td>
							
							<c:if test="${accessOK}">
								<td>
									<c:if test="${product.orderBy ne 0}">
										<a name="moveUp" href="moveProduct.htm?up=false&productId=${product.productId}">
										<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
										</a>
									</c:if>
									
									<c:if test="${product.orderBy ne fn:length(profile.products)-1}">
									<a name="moveDown" href="moveProduct.htm?up=true&productId=${product.productId}">
										<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
									</a>
									</c:if>
								</td>
								<td/>
							</c:if>
							<c:if test="${not accessOK}">
					    		<td colspan="2"/>
					    	</c:if>
						</tr>
					</table>
				</fieldset>
				<br/>
				<tags:table title="${incomeTitle}">
					<display:table list="${product.profileIncomes}" id="inc" requestURI="" cellspacing="0" cellpadding="0"
						export="false" htmlId="incomeTable${product.orderBy}">
						<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
						<display:column titleKey="profileActivityCharge.desc" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
						<display:column titleKey="profileActivityCharge.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
						<display:column titleKey="profileActivityCharge.unitNum" sortProperty="unitNum" sortable="true">
							<tags:formatDecimal value="${inc.unitNum}"/>
						</display:column>
						<display:column titleKey="profileActivityCharge.unitCost" sortable="true" sortProperty="unitCost">
							<tags:formatCurrency value="${inc.unitCost}"/>
						</display:column>
						<display:column titleKey="profileActivityCharge.total" sortable="true" sortProperty="total">
							<tags:formatCurrency value="${inc.total}"/>
						</display:column>
						<display:column title="&nbsp;">
							<c:if test="${not empty inc.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
							<c:if test="${empty inc.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
						</display:column>
						<c:if test="${accessOK}">
							<display:column title="&nbsp;" media="html">
								<a name="copy" href="copyProfProdItem.htm?itemId=${inc.prodItemId}">
									<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
								</a>
							</display:column>
							<display:column title="&nbsp;" media="html">
								<c:if test="${inc_rowNum ne 1}">
									<a name="moveUp" href="moveProfProdItem.htm?up=false&itemId=${inc.prodItemId}">
										<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
									</a>
								</c:if>
								<c:if test="${inc_rowNum eq 1}">
									<img src="../../img/spacer.gif" width="16" height="16" border="0">
								</c:if>
							</display:column>
							<display:column title="&nbsp;" media="html">
								<c:if test="${inc_rowNum ne fn:length(product.profileIncomes)}">
									<a name="moveDown" href="moveProfProdItem.htm?up=true&itemId=${inc.prodItemId}">
										<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
									</a>
								</c:if>
								<c:if test="${inc_rowNum eq fn:length(product.profileIncomes)}">
									<img src="../../img/spacer.gif" width="16" height="16" border="0">
								</c:if>
							</display:column>
							<display:column title="&nbsp;" media="html">
								<a href="../prodItem/${inc.prodItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"></a>
							</display:column>
						
							<display:column title="&nbsp;" href="deleteProfProdItem.htm" paramProperty="prodItemId" paramId="itemId" media="html">
								<img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0">
							</display:column>
						</c:if>
						<display:footer>
							<tr height="1"><td height="1" colspan="11" class="Sum1"/></tr>
							<tr><td/><td/><td/><td/><td><tags:formatCurrency value="${product.incomeTotal}" /></td><td/><td/><td/><td/><td/><td/></tr>
						</display:footer>
					</display:table> 
					<c:if test="${accessOK}">
						<div class="addNew"><a id="newIncome${product.orderBy}" href="profProdItem.htm?itemClass=0&productId=${product.productId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
					</c:if>
				</tags:table>
			</c:if> --%>
			
			<tags:table title="${inputTitle}">
				<display:table name="${product.profileInputs}" id="inp" requestURI="" class="data-table" cellspacing="0" cellpadding="0"
					export="false" htmlId="inputTable${product.orderBy}">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="profileProductInput.desc" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="profileProductInput.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="profileProductInput.unitNum" sortProperty="unitNum" sortable="true">
						<tags:formatDecimal value="${inp.unitNum}"/>
					</display:column>
					<display:column titleKey="profileProductInput.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${inp.unitCost}"/>
					</display:column>
					<display:column titleKey="profileProductInput.transport" sortable="true" sortProperty="transport">
						<tags:formatCurrency value="${inp.transport}"/>
					</display:column>
					<display:column titleKey="profileProductInput.total" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${inp.total}"/><c:set var="inpTotal" value="${inpTotal+inp.total}"/>
					</display:column>
					<display:column title="&nbsp;">
						<c:if test="${not empty inp.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty inp.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
				
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<a name="copy" href="../prodItem/${inp.prodItemId}/copy">
								<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
							</a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${inp_rowNum ne 1}">
								<a name="moveUp" href="../prodItem/${inp.prodItemId}/move?up=false">
									<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${inp_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${inp_rowNum ne fn:length(product.profileInputs)}">
								<a name="moveDown" href="../prodItem/${inp.prodItemId}/move?up=true">
									<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${inp_rowNum eq fn:length(product.profileInputs)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../prodItem/${inp.prodItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../prodItem/${inp.prodItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
					<display:footer>
						<tr height="1"><td height="1" colspan="13" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/><td/><td><tags:formatCurrency value="${inpTotal}" /></td><td/><td/><td/><td/><td/><td/></tr>
					</display:footer>
				</display:table>
				<c:if test="${accessOK}">
					<div class="addNew"><a id="newInput${product.orderBy}" href="../prodItem/-1?itemType=input&productId=${product.productId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>
			
			<tags:table title="${labourTitle}">
				<display:table name="${product.profileLabours}" id="lab" requestURI="" class="data-table" cellpadding="0" cellspacing="0"
					export="false" htmlId="labourTable${product.orderBy}">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="profileProductLabour.desc" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="profileProductLabour.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="profileProductLabour.unitNum" sortProperty="unitNum" sortable="true">
						<tags:formatDecimal value="${lab.unitNum}"/>
					</display:column>
					<display:column titleKey="profileProductLabour.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${lab.unitCost}"/>
					</display:column>
					<display:column titleKey="profileProductLabour.total" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${lab.total}"/><c:set var="labTotal" value="${labTotal+lab.total}"/>
					</display:column>
					<display:column title="&nbsp;">
						<c:if test="${not empty lab.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty lab.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
				
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<a name="copy" href="../prodItem/${lab.prodItemId}/copy">
								<img src="../../img/duplicate.gif" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
							</a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${lab_rowNum ne 1}">
								<a name="moveUp" href="../prodItem/${lab.prodItemId}/move?up=false">
									<img src="../../img/arrow_up.png" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${lab_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${lab_rowNum ne fn:length(product.profileLabours)}">
								<a name="moveDown" href="../prodItem/${lab.prodItemId}/move?up=true">
									<img src="../../img/arrow_down.png" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${lab_rowNum eq fn:length(product.profileLabours)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../prodItem/${lab.prodItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="../prodItem/${lab.prodItemId}/delete"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
					<display:footer>
						<tr height="1"><td height="1" colspan="12" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/><td><tags:formatCurrency value="${labTotal}" /></td><td/><td/><td/><td/><td/><td/></tr>
					</display:footer>
				</display:table>
				<c:if test="${accessOK}">
					<div class="addNew"><a id="newLabour${product.orderBy}" href="../prodItem/-1?itemType=labour&productId=${product.productId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>
		</tags:tableContainer><br/>
	</c:forEach>
						
	
	<tags:submit><spring:message code="misc.goto"/> <spring:message code="profile.step7"/></tags:submit>
</form:form>
<div id="confirmDelete" title="<spring:message code="misc.deleteItem"/>">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
	<spring:message code="misc.confirmDel"/></p><input id="deleteUrl" type="hidden" value=""/>
</div>
</body></html>