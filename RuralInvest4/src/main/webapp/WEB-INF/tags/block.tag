<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="blockEntry" required="true" type="riv.objects.project.BlockBase" %>
<%@ attribute name="blockType" required="true" %>
<c:set var="project" value="${blockEntry.project}"/>

<c:if test="${blockEntry.getClass().simpleName eq 'BlockWithout'}">
	<c:set var="unique">${fn:length(blockEntry.project.blocks) + blockEntry.orderBy}</c:set>
	<c:set var="bCollection" value="${project.blocksWithout}"/>
</c:if>
<c:if test="${blockEntry.getClass().simpleName eq 'Block'}">
	<c:set var="unique">${blockEntry.orderBy}</c:set>
	<c:set var="bCollection" value="${project.blocks}"/>
</c:if>

<c:set var="incTotal" value="0"/><c:set var="incTotalCash" value="0"/>
		<c:set var="inpTotal" value="0"/><c:set var="inpTotalCash" value="0"/>
		<c:set var="labTotal" value="0"/><c:set var="labTotalCash" value="0"/>
	
		<c:set var="lengthUnit">
			<c:if test="${blockEntry.lengthUnit==0}"><spring:message code="units.months"/></c:if>
			<c:if test="${blockEntry.lengthUnit==1}"><spring:message code="units.weeks"/></c:if>
			<c:if test="${blockEntry.lengthUnit==2}"><spring:message code="units.days.calendar"/></c:if>
			<c:if test="${blockEntry.lengthUnit==3}"><spring:message code="units.days.week"/></c:if>
		</c:set>
		<a name="b${blockEntry.blockId}" id="b${blockEntry.blockId}"></a>
		<tags:tableContainer title="${blockEntry.description}">
				<fieldset>
				  	<legend> 
				  		<tags:help title="${blockType}.desc" text="${blockType}.desc.help"><spring:message code="${blockType}.desc"/></tags:help>
				  	</legend>
				  	
				  	<div style="display:inline-block;width:45%;">
				  		
				  		<div class="dataentry">
				  			<tags:help title="${blockType}.prodUnit" text="${blockType}.prodUnit.help"><label><spring:message code="${blockType}.prodUnit"/></label></tags:help>
				  			<span id="${unique}unitType">${blockEntry.unitType}</span>
				  		</div>
				  		<c:if test="${blockEntry.cycles}">
							<div class="dataentry">
					  			<tags:help title="${blockType}.cycleLength" text="${blockType}.cycleLength.help"><label><spring:message code="${blockType}.cycleLength"/></label></tags:help>
					  			<span id="${unique}cycleLength"><tags:formatDecimal value="${blockEntry.cycleLength}"/></span> <span id="${unique}lengthUnit">${lengthUnit}</span>
					  		</div>
					  		<div class="dataentry">
					  			<tags:help title="${blockType}.cyclePerYear" text="${blockType}.cycles.help"><label><spring:message code="${blockType}.cyclePerYear"/></label></tags:help>
					  			<span id="${unique}cyclePerYear"><tags:formatDecimal value="${blockEntry.cyclePerYear}"/></span> <spring:message code="units.perYear"/>
					  		</div>
					  		<c:if test="${project.incomeGen}">
						  		<div class="dataentry">
						  			<tags:help title="${blockType}.cycleFirstYear" text="${blockType}.cycleFirstYear.help"><label><spring:message code="${blockType}.cycleFirstYear"/></label></tags:help>
						  			<span id="${unique}cycleFirstYear"><tags:formatDecimal value="${blockEntry.cycleFirstYear}"/></span> <spring:message code="units.perYear"/>
						  		</div>
						  		<div class="dataentry">
						  			<tags:help title="${blockType}.cycleFirstYearIncome" text="${blockType}.cycleFirstYearIncome.help"><label><spring:message code="${blockType}.cycleFirstYearIncome"/></label></tags:help>
										<span id="${unique}cycleFirstYearIncome"><tags:formatDecimal value="${blockEntry.cycleFirstYearIncome}"/></span> <spring:message code="units.perYear"/>
						  		</div>
							</c:if>
						</c:if>
				  	</div>
				  	
				  	<div style="display:inline-block;width:45%;">
				  		<div class="dataentry">
				  			<a id="downloadExcel${unique}" href="../../report/${blockEntry.blockId}/projectBlock.xlsx"><img src="../../img/xls.gif" alt="Excel" title="Excel"/> <spring:message code="export.download"/></a>
							<c:if test="${accessOK}"><a id="upload${unique}" href="javascript:uploadBlock(${blockEntry.blockId});"><img src="../../img/xls.gif" alt="Excel" title="Excel"/> <spring:message code="import.importExcel"/></a></c:if>
				  		</div>
				  		<c:if test="${accessOK}">
							<div class="dataentry">
								<a id="edit${unique}" href="../block/${blockEntry.blockId}"><img src="../../img/edit.png" title="<spring:message code="${blockType}.editDesc"/>" alt="<spring:message code="${blockType}.editDesc"/>" border="0"/></a>
								<a id="delete${unique}" href="javascript:confirmDelete('../block/${blockEntry.blockId}/delete');"><img src="../../img/delete.gif" border="0" title="<spring:message code="${blockType}.delete"/>" alt="<spring:message code="${blockType}.delete"/>"/></a>
								<a id="clone${unique}" href="../block/${blockEntry.blockId}/clone"><img src="../../img/duplicate.gif" title="<spring:message code="${blockType}.clone"/>" alt="<spring:message code="${blockType}.clone"/>" border="0"/></a>
					    		<c:if test="${blockEntry.project.withWithout}">
						    		<c:set var="swtch">
						    			<c:if test="${blockEntry.propertiesType eq 'block'}"><spring:message code="projectBlock.cloneWithout"/></c:if>
						    			<c:if test="${blockEntry.propertiesType eq 'blockWo'}"><spring:message code="projectBlock.cloneWith"/></c:if>
						    		</c:set>
						    		<a id="cloneType${unique}" href="../block/${blockEntry.blockId}/clone?changeType=true"><img src="../../img/duplicate1.jpg" title='${swtch}' alt='${swtch}' border="0"/></a>
					    		</c:if>
					    		<c:if test="${fn:length(bCollection)>1}">
						    		<c:if test="${blockEntry.orderBy ne 0}">
										<a name="moveUp" href="../block/${blockEntry.blockId}/move?up=false"><img src="../../img/arrow_up.png" title="<spring:message code="misc.moveUp"/>" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0"/></a>
									</c:if>
									<c:if test="${blockEntry.orderBy ne fn:length(bCollection)-1}">
										<a name="moveDown" href="../block/${blockEntry.blockId}/move?up=true"><img src="../../img/arrow_down.png" title="<spring:message code="misc.moveDown"/>" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0"></a>
									</c:if>
								</c:if>
							</div>
				    	</c:if>
				  	</div>
				  	
						<div style="margin:10px 0 0 0;">
						<c:if test="${project.incomeGen}">
							<tags:help title="${blockType}.chronology" text="${blockType}.chronology.help"><b><spring:message code="${blockType}.chronology"/></b></tags:help>
							<br/><tags:chronology block="${blockEntry}" edit="false"/>
							<br/>
						</c:if>
						<tags:help title="${blockType}.pattern" text="${blockType}.pattern.help"><b><spring:message code="${blockType}.pattern"/></b></tags:help>
						<br/>
						<tags:prodPattern block="${blockEntry}" edit="false"/>
					</div>
				</fieldset>

				<c:if test="${project.incomeGen}">	
<%-- 					<span class="error"><form:errors field="incomes"/></span>			 --%>
					<c:set var="incomeName"><spring:message code="projectBlockIncome"/>  <tags:blockExplanation block="${blockEntry}" /></c:set>
					<tags:table title="${incomeName}">
						<display:table list="${blockEntry.incomes}" id="inc" requestURI="" cellspacing="0" cellpadding="0"
							export="false" htmlId="incomeTable${unique}">
							<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
							<display:column titleKey="projectBlockIncome.desc" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
							<display:column titleKey="projectBlockIncome.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
							<display:column titleKey="projectBlockIncome.unitNum" sortProperty="unitNum" sortable="true">
								<tags:formatDecimal value="${inc.unitNum}"/>
							</display:column>
							<display:column titleKey="projectBlockIncome.unitCost" sortable="true" sortProperty="unitCost">
								<tags:formatCurrency value="${inc.unitCost}"/>
							</display:column>
							<display:column titleKey="projectBlockIncome.qtyIntern" sortProperty="qtyIntern" sortable="true">
								<tags:formatDecimal value="${inc.qtyIntern}"/>
							</display:column>
							<display:column titleKey="projectBlockIncome.qtyExtern" sortable="true" sortProperty="extern">
								<tags:formatDecimal value="${inc.extern}"/>
							</display:column>
							<display:column titleKey="projectBlockIncome.transport" sortable="true" sortProperty="transport">
								<tags:formatCurrency value="${inc.transport}"/>
							</display:column>
							<display:column titleKey="projectBlockIncome.total" sortable="true" sortProperty="total">
								<tags:formatCurrency value="${inc.total}"/><c:set var="incTotal" value="${incTotal+inc.total}"/>
							</display:column>
							<display:column titleKey="projectBlockIncome.totalCash" sortable="true" sortProperty="totalCash">
								<tags:formatCurrency value="${inc.totalCash}"/><c:set var="incTotalCash" value="${incTotalCash+inc.totalCash}"/>
							</display:column>
							<display:column title="&nbsp;">
								<c:if test="${not empty inc.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
								<c:if test="${empty inc.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
							</display:column>
							<c:if test="${accessOK}">
								<display:column title="&nbsp;" media="html">
									<a name="copy" href="../blockItem/${inc.prodItemId}/copy">
										<img src="../../img/duplicate.gif" title="<spring:message code="misc.copy"/>" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
									</a>
								</display:column>
								<display:column title="&nbsp;" media="html">
									<c:if test="${inc_rowNum ne 1}">
										<a name="moveUp" href="../blockItem/${inc.prodItemId}/move?up=false">
											<img src="../../img/arrow_up.png" title="<spring:message code="misc.moveUp"/>" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
										</a>
									</c:if>
									<c:if test="${inc_rowNum eq 1}">
										<img src="../../img/spacer.gif" width="16" height="16" border="0">
									</c:if>
								</display:column>
								<display:column title="&nbsp;" media="html">
									<c:if test="${inc_rowNum ne fn:length(blockEntry.incomes)}">
										<a name="moveDown" href="../blockItem/${inc.prodItemId}/move?up=true">
											<img src="../../img/arrow_down.png" title="<spring:message code="misc.moveDown"/>" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
										</a>
									</c:if>
									<c:if test="${inc_rowNum eq fn:length(blockEntry.incomes)}">
										<img src="../../img/spacer.gif" width="16" height="16" border="0">
									</c:if>
								</display:column>
								<display:column title="&nbsp;" media="html">
									<a href="../blockItem/${inc.prodItemId}"><img src="../../img/edit.png" title="<spring:message code="misc.viewEditItem"/>" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"></a>
								</display:column>
								<display:column title="&nbsp;" media="html">
									<a href="javascript:confirmDelete('../blockItem/${inc.prodItemId}/delete');"><img src="../../img/delete.gif" title="<spring:message code="misc.deleteItem"/>" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"></a>
								</display:column>
							</c:if>
							<display:footer>
								<tr height="1"><td height="1" colspan="15" class="Sum1"/></tr>
								<tr><td/><td/><td/><td/><td/><td/><td/>
								<td><tags:formatCurrency value="${incTotal}" /></td>
								<td><tags:formatCurrency value="${incTotalCash}" /></td>
								<td/><td/><td/><td/><td/><td/></tr>
							</display:footer>
						</display:table> 
						<c:if test="${accessOK}">
							<div class="addNew"><a id="newIncome${unique}" href="../blockItem/-1?itemType=income&blockId=${blockEntry.blockId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
						</c:if>
					</tags:table>
				</c:if>
			<c:if test="${not project.incomeGen}">
				<c:set var="incomeName"><spring:message code="projectActivityCharge"/>  <tags:blockExplanation block="${blockEntry}" /></c:set>
				<tags:table title="${incomeName}">
					<display:table list="${blockEntry.incomes}" id="inc" requestURI="" cellspacing="0" cellpadding="0"
							export="false" htmlId="incomeTable${unique}"> 
							<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty> 
							<display:column titleKey="projectActivityCharge.desc" property="description" sortable="true" style="text-align:${left};" headerClass="left"/> 
							<display:column titleKey="projectActivityCharge.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/> 
							<display:column titleKey="projectActivityCharge.unitNum"  sortProperty="unitNum" sortable="true"> 
								<tags:formatDecimal value="${inc.unitNum}"/> 
							</display:column> 
							<display:column titleKey="projectActivityCharge.unitCost" sortable="true" sortProperty="unitCost"> 
								<tags:formatCurrency value="${inc.unitCost}"/> 
							</display:column> 
							<display:column titleKey="projectActivityCharge.total" sortable="true" sortProperty="total"> 
								<tags:formatCurrency value="${inc.total}"/> <c:set var="incTotal" value="${incTotal+inc.total}"/>
							</display:column> 
							<display:column title="&nbsp;">
							<c:if test="${not empty inc.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
							<c:if test="${empty inc.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
						</display:column>
							<c:if test="${accessOK}"> 
								<display:column title="&nbsp;" media="html"> 
									<a name="copy" href="../blockItem/${inc.prodItemId}/copy"> 
										<img src="../../img/duplicate.gif" title="<spring:message code="misc.copy"/>" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/> 
								</a>
							</display:column>
							<display:column title="&nbsp;" media="html">
								<c:if test="${inc_rowNum ne 1}">
									<a name="moveUp" href="../blockItem/${inc.prodItemId}/move?up=false">
										<img src="../../img/arrow_up.png" title="<spring:message code="misc.moveUp"/>"  alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
									</a>
								</c:if>
								<c:if test="${inc_rowNum eq 1}">
									<img src="../../img/spacer.gif" width="16" height="16" border="0">
								</c:if>
							</display:column>
							<display:column title="&nbsp;" media="html">
								<c:if test="${inc_rowNum ne fn:length(blockEntry.incomes)}">
									<a name="moveDown" href="../blockItem/${inc.prodItemId}/move?up=true">
										<img src="../../img/arrow_down.png" title="<spring:message code="misc.moveDown"/>" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
									</a>
								</c:if>
								<c:if test="${inc_rowNum eq fn:length(blockEntry.incomes)}">
									<img src="../../img/spacer.gif" width="16" height="16" border="0">
								</c:if>
							</display:column>
							<display:column title="&nbsp;" style="margin-left:5px;" media="html">
								<a href="../blockItem/${inc.prodItemId}"><img src="../../img/edit.png" title="<spring:message code="misc.viewEditItem"/>" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"></a>
							</display:column>
							<display:column title="&nbsp;" media="html">
								<a href="javascript:confirmDelete('../blockItem/${inc.prodItemId}/delete');"><img src="../../img/delete.gif" title="<spring:message code="misc.deleteItem"/>" alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"></a>
							</display:column>
						</c:if>
						<display:footer>
							<tr height="1"><td height="1" colspan="11" class="Sum1"/></tr>
							<tr><td/><td/><td/><td/>
							<td><tags:formatCurrency value="${incTotal}" /></td>
							<td/><td/><td/><td/><td/><td/>
						</display:footer>
					</display:table> 
					<c:if test="${accessOK}">
						<div class="addNew"><a id="newIncome${unique}" href="../blockItem/-1?itemType=income&blockId=${blockEntry.blockId}"><img src="../../img/add.gif" title="<spring:message code="misc.addItem"/>" alt="<spring:message code="misc.addItem"/>" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
					</c:if>
				</tags:table>
			</c:if>
			<c:set var="inputName"><spring:message code="projectBlockInput"/>  <tags:blockExplanation block="${blockEntry}" /></c:set>
			<tags:table title="${inputName}">
				<display:table name="${blockEntry.inputs}" id="inp" requestURI="" cellspacing="0" cellpadding="0"
					export="false" htmlId="inputTable${unique}">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="projectBlockInput.desc" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="projectBlockInput.unitType" property="unitType" sortable="true" style="text-align:${left};" headerClass="left"/>
					<display:column titleKey="projectBlockInput.unitNum"  sortProperty="unitNum" sortable="true">
							<tags:formatDecimal value="${inp.unitNum}"/>
						</display:column>
					<display:column titleKey="projectBlockInput.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${inp.unitCost}"/>
					</display:column>
					<display:column titleKey="projectBlockInput.qtyIntern" sortProperty="qtyIntern" sortable="true">
						<tags:formatDecimal value="${inp.qtyIntern}"/>
					</display:column>
					<display:column titleKey="projectBlockInput.qtyExtern" sortable="true" sortProperty="extern">
						<tags:formatDecimal value="${inp.extern}"/>
					</display:column>
					<display:column titleKey="projectBlockInput.transport" sortable="true" sortProperty="transport">
						<tags:formatCurrency value="${inp.transport}"/>
					</display:column>
					<display:column titleKey="projectBlockInput.total" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${inp.total}"/><c:set var="inpTotal" value="${inpTotal+inp.total}"/>
					</display:column>
					<c:if test="${not project.incomeGen}">
						<display:column titleKey="projectBlockInput.donated" sortable="true" sortProperty="donated">
							<tags:formatCurrency value="${inp.donated}"/><c:set var="inpDonatedTotal" value="${inpDonatedTotal+inp.donated}"/>
						</display:column>
					</c:if>	
					<display:column titleKey="projectBlockInput.totalCash" sortable="true" sortProperty="totalCash">
						<tags:formatCurrency value="${inp.totalCash}"/><c:set var="inpTotalCash" value="${inpTotalCash+inp.totalCash}"/>
					</display:column>
					<display:column title="&nbsp;">
						<c:if test="${not empty inp.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty inp.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<a name="copy" href="../blockItem/${inp.prodItemId}/copy">
								<img src="../../img/duplicate.gif" title="<spring:message code="misc.copy"/>" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
							</a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${inp_rowNum ne 1}">
								<a name="moveUp" href="../blockItem/${inp.prodItemId}/move?up=false">
									<img src="../../img/arrow_up.png" title="<spring:message code="misc.moveUp"/>" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${inp_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${inp_rowNum ne fn:length(blockEntry.inputs)}">
								<a name="moveDown" href="../blockItem/${inp.prodItemId}/move?up=true">
									<img src="../../img/arrow_down.png" title="<spring:message code="misc.moveDown"/>" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${inp_rowNum eq fn:length(blockEntry.inputs)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" style="margin-left:5px;" media="html">
							<a href="../blockItem/${inp.prodItemId}"><img src="../../img/edit.png" title="<spring:message code="misc.viewEditItem"/>" alt="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="javascript:confirmDelete('../blockItem/${inp.prodItemId}/delete');"><img src="../../img/delete.gif" title="<spring:message code="misc.deleteItem"/>"  alt="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"></a>
						</display:column>
					</c:if>
					<display:footer>
						<c:set var="colspan"><c:if test="${project.incomeGen}">15</c:if><c:if test="${not project.incomeGen}">16</c:if></c:set>
						<tr height="1"><td height="1" colspan="${colspan}" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/><td/><td/><td/>
						<td><tags:formatCurrency value="${inpTotal}" /></td>
						<c:if test="${not project.incomeGen}"><td><tags:formatCurrency value="${inpDonatedTotal}" /></td></c:if>
						<td><tags:formatCurrency value="${inpTotalCash}" /></td>
						<td/><td/><td/><td/><td/><td/></tr>
					</display:footer>
				</display:table>
				<c:if test="${accessOK}">
					<div class="addNew"><a id="newInput${unique}" href="../blockItem/-1?itemType=input&blockId=${blockEntry.blockId}"><img src="../../img/add.gif" title="<spring:message code="misc.addItem"/>" alt="<spring:message code="misc.addItem"/>" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>
			
			<c:set var="labourName"><spring:message code="projectBlockLabour"/>  <tags:blockExplanation block="${blockEntry}" /></c:set>
			<tags:table title="${labourName}">
				<display:table name="${blockEntry.labours}" id="lab" requestURI="" cellpadding="0" cellspacing="0"
					export="false" htmlId="labourTable${unique}">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="projectBlockLabour.desc" property="description" sortable="true" style="text-align:${left};" headerClass="left"/>								
					<display:column titleKey="projectBlockLabour.unitType" sortable="true" style="text-align:${left};" headerClass="left">
						<c:if test="${lab.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
						<c:if test="${lab.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
						<c:if test="${lab.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
						<c:if test="${lab.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
					</display:column>								
					<display:column titleKey="projectBlockLabour.unitNum" sortProperty="unitNum" sortable="true">
							<tags:formatDecimal value="${lab.unitNum}"/>
						</display:column>
					<display:column titleKey="projectBlockLabour.unitCost" sortable="true" sortProperty="unitCost">
						<tags:formatCurrency value="${lab.unitCost}"/>
					</display:column>
					<display:column titleKey="projectBlockLabour.qtyIntern" sortProperty="qtyIntern" sortable="true">
						<tags:formatDecimal value="${lab.qtyIntern}"/>
					</display:column>
					<display:column titleKey="projectBlockLabour.qtyExtern" sortable="true" style="text-align:center;" sortProperty="extern" >
						<tags:formatDecimal value="${lab.extern}"/>
					</display:column>
					<display:column titleKey="projectBlockLabour.total" sortable="true" sortProperty="total">
						<tags:formatCurrency value="${lab.total}"/><c:set var="labTotal" value="${labTotal+lab.total}"/>
					</display:column>
					<c:if test="${not project.incomeGen}">
						<display:column titleKey="projectBlockLabour.donated" sortable="true" sortProperty="donated">
							<tags:formatCurrency value="${lab.donated}"/><c:set var="labDonatedTotal" value="${labDonatedTotal+lab.donated}"/>
						</display:column>
					</c:if>	
					<display:column titleKey="projectBlockLabour.totalCash" sortable="true" sortProperty="totalCash">
						<tags:formatCurrency value="${lab.totalCash}"/><c:set var="labTotalCash" value="${labTotalCash+lab.totalCash}"/>
					</display:column>
					<display:column title="&nbsp;">
						<c:if test="${not empty lab.linkedTo}"><img src="../../img/linked.png" width="16" height="16" border="0"></c:if>
						<c:if test="${empty lab.linkedTo}"><img src="../../img/spacer.gif" width="16" height="16" border="0"></c:if>
					</display:column>
					<c:if test="${accessOK}">
						<display:column title="&nbsp;" media="html">
							<a name="copy" href="../blockItem/${lab.prodItemId}/copy">
								<img src="../../img/duplicate.gif" title="<spring:message code="misc.copy"/>" alt="<spring:message code="misc.copy"/>" width="16" height="16" border="0"/>
							</a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${lab_rowNum ne 1}">
								<a name="moveUp" href="../blockItem/${lab.prodItemId}/move?up=false">
									<img src="../../img/arrow_up.png" title="<spring:message code="misc.moveUp"/>" alt="<spring:message code="misc.moveUp"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${lab_rowNum eq 1}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<c:if test="${lab_rowNum ne fn:length(blockEntry.labours)}">
								<a name="moveDown" href="../blockItem/${lab.prodItemId}/move?up=true">
									<img src="../../img/arrow_down.png" title="<spring:message code="misc.moveDown"/>" alt="<spring:message code="misc.moveDown"/>" width="16" height="16" border="0">
								</a>
							</c:if>
							<c:if test="${lab_rowNum eq fn:length(blockEntry.labours)}">
								<img src="../../img/spacer.gif" width="16" height="16" border="0">
							</c:if>
						</display:column>
						<display:column title="&nbsp;" style="margin-left:5px;" media="html">
							<a href="../blockItem/${lab.prodItemId}"><img src="../../img/edit.png" alt="<spring:message code="misc.viewEditItem"/>" title="<spring:message code="misc.viewEditItem"/>" width="16" height="16" border="0"></a>
						</display:column>
						<display:column title="&nbsp;" media="html">
							<a href="javascript:confirmDelete('../blockItem/${lab.prodItemId}/delete');"><img src="../../img/delete.gif" alt="<spring:message code="misc.deleteItem"/>" title="<spring:message code="misc.deleteItem"/>" width="16" height="16" border="0"></a>
						</display:column>
					</c:if>
					<display:footer>
						<tr height="1"><td height="1" colspan="${(colspan-1)}" class="Sum1"/></tr>
						<tr><td/><td/><td/><td/><td/><td/>
							<td><tags:formatCurrency value="${labTotal}" /></td>
							<c:if test="${not project.incomeGen}"><td><tags:formatCurrency value="${labDonatedTotal}" /></td></c:if>
							<td><tags:formatCurrency value="${labTotalCash}" /></td>
						<td/><td/><td/><td/><td/><td/></tr>
					</display:footer>
				</display:table>
				<c:if test="${accessOK}">
					<div class="addNew"><a id="newLabour${unique}" href="../blockItem/-1?itemType=labour&blockId=${blockEntry.blockId}"><img src="../../img/add.gif" width="20" height="20" border="0"/> <spring:message code="misc.addItem"/></a>&nbsp;&nbsp;</div>
				</c:if>
			</tags:table>
		</tags:tableContainer><br/>