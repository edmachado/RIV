<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="linked" required="true" type="riv.objects.reference.ReferenceItem" %>
<%@ attribute name="calculation" required="true" %>
<%@ attribute name="descField" required="true" %>
<%@ attribute name="unitTypeField" required="true" %>
<%@ attribute name="unitCostField" required="true" %>
<%@ attribute name="notLinked" required="true" %>
<%@ attribute name="transField" %>
<div id="refItemChooser">
	<c:set var="initLinkedTo"><c:if test="${linked ne null}">${linked.refItemId}</c:if></c:set>
	<input type="hidden" name="linkedToId" id="linkedToId" value="${initLinkedTo}"/>
	<div id="divnotlinked" style="display:none;">
		<p><spring:message code="reference.item.linked.no"/></p>
		<b><a href="javascript:openReference();"><spring:message code="reference.item.link"/></a></b>
		<br/><br/>
		<spring:message code="reference.item.link.or"/>
		<br/><br/>
		<input type="checkbox" name="addLink" id="addLink"/>				
		<spring:message code="reference.item.add"/>
		
	</div>
	<div id="divlinked" style="display:none;">
		<spring:message code="reference.item.linked.yes"/><br/><br/>
			<input type="checkbox" name="linked" id="linked" onclick="linked_changed();" <c:if test="${linked ne null}">checked</c:if>/> <spring:message code="reference.item.linked"/>
			<br/><spring:message code="reference.item.linkedTo"/> <span id="linkedItemDesc">${linked.description}</span>
			<br/><br/>
			<b><a href="javascript:openReference();"><spring:message code="reference.item.link.different"/></a></b>
	</div>
</div>
<div id="needTransport" style="display:none;margin:0 20px;padding:10px 10px;border:1px solid black">
	<spring:message code="reference.transport"/><br/>
	<input class="curlabel" type="text" tabindex="100" size="4" value="${rivConfig.setting.currencySym}" disabled="true"/><input id="txtaddTransport" name="addTransport" class="num" type="text" onkeyup="updateTransport(this, '${transField}');"/>
</div>

<script>
<%--when there is an error AND the item is linked AND there is a transportation cost AND that cost is missing...
show the needTransport section--%>
if ($('#errorbox')!=null && $('#linkedToId').val()!=null) {
	var transField = document.getElementById('${transField}');
	if (transField!=null && transField.value=="") {
		document.getElementById("needTransport").style.display="block";
	}
}

	jQuery(function(){
	   linked_changed();
	 }); 

	 function addReference(id, desc, unitType, unitCost, trans) {
			// update input fields
			document.getElementById('linkedToId').value=id;
			//document.getElementById('${descField}').value=desc;
			document.getElementById('${unitCostField}').value=numToFormat(unitCost);
			document.getElementById('${unitTypeField}').value=unitType;
			if (trans != null) document.getElementById('${transField}').value=numToFormat(trans);
			// update desc of ref item
			$('#linkedItemDesc').text(desc);

			// switch linked info
			document.getElementById('linked').checked=false;
			$('#linked').trigger('click');

			// clear non-linked fields
			//var notLinked = ${notLinked}.split(',');
			//for (i=0; i<notLinked.length; i++)
			//	$('#'+notLinked[i]).attr('value','');

			// call javascript calculations
			${calculation}

			// if transport needed show dialog
			if (trans == '') $('#needTransport').css('display','block');
			else {
				$('#needTransport').css('display','none');
				$('#txtaddTransport').val('');
			}

			// enable unitType dropdown before submitting
			$('#submit').parent().attr('href','javascript:newSubmit();');

			// close popup
			$('#refItems').dialog('close');
		}

		function newSubmit() {
			$('#txtunitType').attr('disabled',false);
			document.form.submit();
		}

		function updateTransport(txtFrom, idTxtTo) {
			 commasKeyup(txtFrom);
			$('#'+idTxtTo).val(txtFrom.value);
		}
	
		function disableInput(input, disable) {
			var backcolor = disable ? 'inherit' : '#fff';
			if ($(input).attr('class')!=null && $(input).attr('class').match("^curLabel")) { // currency label
				$(input).css('backgroundColor',backcolor);
			} else if ($(input).prop('tagName') != null && $(input).prop('tagName').toLowerCase() != 'select') { // text input
				$(input).attr('readonly',disable); 
				$(input).css('backgroundColor',backcolor);
				if ($(input).attr('class')=='num') disableInput($(input).prev('input'), disable);
			} else { // dropdown
				$(input).attr('disabled',disable);
			}
		}

		function openReference() {
			$('#refItems').dialog('open');
		}
	
	function linked_changed() {
		var inputs = ['${unitCostField}',  '${unitTypeField}'<c:if test="${not empty transField}">, '${transField}'</c:if>];
		var islinked = document.getElementById('linked').checked;
		for (i=0; i<inputs.length; i++) disableInput('#'+inputs[i], islinked);
		if (islinked) {
			$('#divlinked').css('display','block');
			$('#divnotlinked').css('display','none');
			document.getElementById('addLink').checked=false;
		} else {
			document.getElementById('linkedToId').value='';
			$('#divlinked').css('display','none');
			$('#divnotlinked').css('display','block');
			$('#needTransport').css('display','none');
			$('#txtaddTransport').val('');
		}
	}

	$(function() { $("#refItems").dialog({
				bgiframe: true,	autoOpen: false, height: 500, width:400, modal: false,
				title: '<spring:message code="reference.reference"/>',
				buttons: { Cancel: function() { $(this).dialog('close'); }	}
	}); });
</script>
<c:if test="${empty project}"><c:set var="project" value="${profile}"/></c:if>

<c:choose>
	<c:when test="${type==0}">
		<div id="refItems" title="<spring:message code="reference.costs"/>" style="display:none;">
			<tags:table titleKey="reference.costs">
				<display:table list="${project.refCosts}" id="item" htmlId="refItems" requestURI="" class="data-table" cellspacing="0" cellpadding="0" export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="reference.cost.description" property="description"  style="text-align:left;" headerClass="left"/>
					<display:column titleKey="reference.cost.unitType" property="unitType"  style="text-align:left;" headerClass="left"/>
					<display:column titleKey="reference.cost.unitCost" sortProperty="unitCost">
						<tags:formatCurrency value="${item.unitCost}"/>
					</display:column> 
					<c:if test="${not empty transField}">
						<display:column titleKey="reference.cost.transport" sortProperty="transport" >
						<tags:formatCurrency value="${item.transport}"/>
					</display:column>
					</c:if>
					<display:column>
						<a href="javascript:addReference('${item.refItemId}','${item.description}','${item.unitType}','${item.unitCost}'<c:if test="${not empty transField}">,'${item.transport}'</c:if>);"><spring:message code="reference.item.link.imperative"/></a>
					</display:column>
				</display:table>
			</tags:table>
		</div>
	</c:when>
	<c:when test="${type==1}">
		<div id="refItems" title="<spring:message code="reference.incomes"/>" style="display:none;" >
			<tags:table titleKey="reference.incomes">
				<display:table list="${project.refIncomes}" id="item" htmlId="refItems" requestURI="" class="data-table" cellspacing="0" cellpadding="0" export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="reference.income.description" property="description"  style="text-align:left;" headerClass="left"/>
					<display:column titleKey="reference.income.unitType" property="unitType"  style="text-align:left;" headerClass="left"/>
					<display:column titleKey="reference.income.unitCost" sortProperty="unitCost">
						<tags:formatCurrency value="${item.unitCost}"/>
					</display:column> 
					<c:if test="${not empty transField}">
						<display:column titleKey="reference.income.transport" sortProperty="transport">
						<tags:formatCurrency value="${item.transport}"/>
					</display:column>
					</c:if>
					<display:column>
						<a href="javascript:addReference('${item.refItemId}','${item.description}','${item.unitType}','${item.unitCost}'<c:if test="${not empty transField}">,'${item.transport}'</c:if>);"><spring:message code="reference.item.link.imperative"/></a>
					</display:column>
				</display:table>
			</tags:table>
		</div>
	</c:when>
	<c:when test="${type==2}">
		<div id="refItems" title="<spring:message code="reference.labours"/>" style="display:none;" >
			<tags:table titleKey="reference.labours">
				<display:table list="${project.refLabours}" id="item" htmlId="refItems" requestURI="" class="data-table" cellspacing="0" cellpadding="0" export="false">
					<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
					<display:column titleKey="reference.labour.description" property="description"  style="text-align:left;" headerClass="left"/>
					<display:column titleKey="reference.labour.unitType"  style="text-align:left;" headerClass="left">
						<c:if test="${item.unitType=='0'}"><spring:message code="units.pyears"/></c:if>
						<c:if test="${item.unitType=='1'}"><spring:message code="units.pmonths"/></c:if>
						<c:if test="${item.unitType=='2'}"><spring:message code="units.pweeks"/></c:if>
						<c:if test="${item.unitType=='3'}"><spring:message code="units.pdays"/></c:if>
						<c:if test="${item.unitType ne '0' and item.unitType ne '1' and item.unitType ne '2' and item.unitType ne '3'}">${item.unitType}</c:if>
					</display:column>
					<display:column titleKey="reference.labour.unitCost" sortProperty="unitCost">
						<tags:formatCurrency value="${item.unitCost}"/>
					</display:column> 
					<display:column>
						<a href="javascript:addReference('${item.refItemId}','${item.description}','${item.unitType}',
							'${item.unitCost}');"><spring:message code="reference.item.link.imperative"/></a>
					</display:column>
				</display:table>
			</tags:table>
		</div>
	</c:when>
</c:choose>