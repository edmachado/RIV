<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="product" required="true" type="riv.objects.profile.ProfileProductBase" %>
<%@ attribute name="prodType" required="true" %>

<c:set var="unique"><c:if test="${product.getClass().simpleName eq 'ProfileProductWithout'}">${fn:length(product.profile.products) + product.orderBy}</c:if><c:if test="${product.getClass().simpleName eq 'ProfileProduct'}">${product.orderBy}</c:if></c:set>
<c:set var="incTotal" value="0"/><c:set var="inpTotal" value="0"/><c:set var="labTotal" value="0"/>
		<a name="b${product.productId}" id="b${product.productId}"></a>
		<c:set var="incomeTitle"><c:if test="${profile.incomeGen}"><spring:message code="profileProductIncome"/></c:if><c:if test="${not profile.incomeGen}"><spring:message code="profileActivityCharge"/></c:if> <tags:blockExplanation block="${product}"/></c:set>
		<c:set var="inputTitle"><spring:message code="profileProductInput"/>  <tags:blockExplanation block="${product}"/></c:set>
		<c:set var="labourTitle"><spring:message code="profileProductLabour"/>  <tags:blockExplanation block="${product}"/></c:set>
				
		<tags:tableContainer title="${product.description}">
				<fieldset><legend><spring:message code="${prodType}.desc"/></legend>
					<table id="descriptionTable" width="100%" border="0" cellspacing="2" cellpadding="2">
					  <tr>
							<td><tags:help title="${prodType}.name" text="${prodType}.name.help"><b><spring:message code="${prodType}.name"/></b></tags:help></td>
							<td><span id="${unique}description">${product.description}</span></td>
							<td>
								<img src="../../img/xls.gif" alt="Excel" title="Excel"/> 
								<a id="downloadExcel${unique}" href="../../report/${product.productId}/profileProduct.xlsx"><spring:message code="export.download"/></a>
							</td>
							<td>	
								<c:if test="${accessOK}"><a id="upload${product.orderBy}" href="javascript:uploadBlock(${product.productId});"><img src="../../img/xls.gif" alt="Excel" title="Excel"/> <spring:message code="import.importExcel"/></a></c:if>
	 							<br/>
	 						</td>
					    </tr>
					    <tr>
							<td><tags:help title="${prodType}.prodUnit" text="${prodType}.prodUnit.help"><b><spring:message code="${prodType}.prodUnit"/></b></tags:help></td>
							<td><span id="${unique}unitType">${product.unitType}</span></td>
							<c:if test="${accessOK}">
								<td><a href="../product/${product.productId}"><img src="../../img/edit.png" border="0"/> <spring:message code="${prodType}.editDesc"/></a></td>
						    	<td><a id="delete${unique}" href="javascript:confirmDelete('../product/${product.productId}/delete');"><img src="../../img/delete.gif" border="0"/> <spring:message code="${prodType}.delete"/></a></td>
					   		</c:if>
					   		<c:if test="${not accessOK}"><td colspan="2"/></td></c:if>
							
							
					    </tr>
						<tr>
							<td><tags:help title="${prodType}.numUnits" text="${prodType}.numUnits.help"><b><spring:message code="${prodType}.numUnits"/></b></tags:help></td>
							<td><span id="${unique}unitNum"><tags:formatDecimal value="${product.unitNum}"/></span></td>
							<td>
							   	<c:if test="${accessOK}">
							   		<a href="../product/${product.productId}/clone"><img src="../../img/duplicate.gif" border="0"/> <spring:message code="${prodType}.clone"/></a>
							   	</c:if>
						   	</td>
		 					<td/>
						</tr>
						<tr>
							<td><tags:help title="${prodType}.cycleLength" text="${prodType}.cycleLength.help"><b><spring:message code="${prodType}.cycleLength"/></b></tags:help></td>
							<td><span id="${unique}cycleLength"><tags:formatDecimal value="${product.cycleLength}"/></span>
								<c:if test="${product.lengthUnit==0}"><spring:message code="units.months"/></c:if>
								<c:if test="${product.lengthUnit==1}"><spring:message code="units.weeks"/></c:if>
								<c:if test="${product.lengthUnit==2}"><spring:message code="units.days.calendar"/></c:if>
								<c:if test="${product.lengthUnit==3}"><spring:message code="units.days.week"/></c:if>
							</td>
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
						 <tr>
						   
							<td><tags:help title="${prodType}.cycles" text="${prodType}.cycles.help"><b><spring:message code="${prodType}.cycles"/></b></tags:help></td>
							<td><span id="${unique}cyclePerYear"><tags:formatDecimal value="${product.cyclePerYear}"/></span> <spring:message code="units.perYear"/></td>
							
	 						<td colspan="2"/>
					   	</tr>
					</table>
			</fieldset>
			<br/>
<%-- 			<span class="error"><form:errors field="profileIncomes"/></span> --%>
			<tags:table title="${incomeTitle}">
				<display:table list="${product.profileIncomes}" id="inc" requestURI="" cellspacing="0" cellpadding="0"
					export="false" htmlId="incomeTable${unique}">
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
							<a href="javascript:confirmDelete('../prodItem/${inc.prodItemId}/delete');"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
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
					<div class="addNew"><a id="newIncome${unique}" href="../prodItem/-1?itemType=income&productId=${product.productId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>
			
			<tags:table title="${inputTitle}">
				<display:table name="${product.profileInputs}" id="inp" requestURI="" class="data-table" cellspacing="0" cellpadding="0"
					export="false" htmlId="inputTable${unique}">
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
							<a href="javascript:confirmDelete('../prodItem/${inp.prodItemId}/delete');"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
					<display:footer>
						<tr height="1"><td height="1" colspan="13" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/><td/><td><tags:formatCurrency value="${inpTotal}" /></td><td/><td/><td/><td/><td/><td/></tr>
					</display:footer>
				</display:table>
				<c:if test="${accessOK}">
					<div class="addNew"><a id="newInput${unique}" href="../prodItem/-1?itemType=input&productId=${product.productId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>
			
			<tags:table title="${labourTitle}">
				<display:table name="${product.profileLabours}" id="lab" requestURI="" class="data-table" cellpadding="0" cellspacing="0"
					export="false" htmlId="labourTable${unique}">
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
							<a href="javascript:confirmDelete('../prodItem/${lab.prodItemId}/delete');"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"/></a>
						</display:column>
					</c:if>
					<display:footer>
						<tr height="1"><td height="1" colspan="12" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/><td><tags:formatCurrency value="${labTotal}" /></td><td/><td/><td/><td/><td/><td/></tr>
					</display:footer>
				</display:table>
				<c:if test="${accessOK}">
					<div class="addNew"><a id="newLabour${unique}" href="../prodItem/-1?itemType=labour&productId=${product.productId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>
		</tags:tableContainer>