<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="itemCode" required="true" %>

<style>
#multiyear input { border:1px solid #003366 !important; }
#multiyear input.error { border-color: red !important; }
</style>
<script>
$(function() {
	$('#unitCost').on("keyup", function() {
		for (var i=0; i<${project.duration}; i++) {
			calculateYear(i);
		}
	});
	$('#multiyear input:not(:disabled,.curlabel)').on("keydown", function(e) { 
		if (e.which==38 || e.which==40) {
			
			var regex = /\D+(\d+)(\w+)/g;
			var matches = regex.exec(this.id);
			var year = matches[1];
			var type = matches[2];
			
			switch (e.which) {
			case 38: // up
				if (type=='ownResources') {
					$('#years'+year+'unitNum').focus();
				}
				break;
			case 40: // down
				if (type=='unitNum') {
					$('#years'+year+'ownResources').focus();
				}
				break;
// 			case 37: // left
// 				if (year!=0) {
// 					$('#years'+(year-1)+type).focus();
// 				}
// 				break;
// 			case 39: // right
// 				if (year<${project.duration-1}) {
// 					$('#years'+(Number(year)+1)+type).focus();
// 				}
// 				break;
			}

			e.preventDefault();
		}
	});
	$('#multiyear input:not(:disabled,.curlabel)').on("keyup", function(e) { 
		var code = e.keyCode || e.which;
		if (code==9) { return; } // tab key pressed
		
		if (!replaceNonNum(this)) {
			commasKeyup(this);
			
			// get year
			var year = this.id.match(/\d+/g);
			calculateYear(year);
		}
	});
});

function calculateYear(year) {
	var unitCost = formatToNum($('#unitCost').val());
	var qty = formatToNum($('#years'+year+'unitNum').val());
	var own = formatToNum($('#years'+year+'ownResources').val());
	var total = unitCost*qty;
	var totalCash = total-own;
	$('#years'+year+'total').val(numToFormat(round(total, decLength)));
	$('#years'+year+'external').val(numToFormat(round(totalCash, decLength)));
}
</script>
<div>
	<form:errors path="years" cssClass="error" element="div" />
	<tags:table>
		<table id="multiyear" cellpadding="0" cellspacing="0">
			<thead>
				<tr>
					<th class="left"><span style="display:inline-block; width:10em;"/>Year</th>
					<c:forEach var="year" begin="0" end="${project.duration-1}"><th>${year+1}</th></c:forEach>
				</tr>
			</thead> 
			<tbody>
				<tr>
					<td class="left"><spring:message code="${itemCode}.unitNum"/></td>
					<c:forEach var="year" begin="0" end="${project.duration-1}">
						<td><c:set var="inputId">years${year}unitNum</c:set>
						<c:set var="yearVal"><tags:formatDecimal value="${projectItem.years[year].unitNum}"/></c:set>
							<input id="${inputId}" class="<c:if test="${fn:contains(yearsErrors,inputId)}">error </c:if> num" type="text" size="5" value="${yearVal}" name="years[${year}].unitNum"/>
						</td>
					</c:forEach>
				</tr>
				<tr>
					<td class="left"><spring:message code="${itemCode}.totalCost"/></td>
					<c:forEach var="year" begin="0" end="${project.duration-1}">
						<td><c:set var="inputId">years${year}total</c:set>
							<input id="${inputId}" disabled="disabled" class="<c:if test="${fn:contains(yearsErrors,inputId)}">error </c:if> num" type="text" size="5" value="<tags:formatCurrency value="${projectItem.years[year].total}"/>" name="years[${year}].total"/></td>
					</c:forEach>
				</tr>
				<tr>
					<td class="left"><spring:message code="${itemCode}.ownResources"/></td>
					<c:forEach var="year" begin="0" end="${project.duration-1}">
						<td><c:set var="inputId">years${year}ownResources</c:set>
							
							<input id="${inputId}" class="<c:if test="${fn:contains(yearsErrors,inputId)}">error </c:if> num" type="text" size="5" value="<tags:formatCurrency value="${projectItem.years[year].ownResources}"/>" name="years[${year}].ownResources"/></td>
					</c:forEach>
				</tr>
				<tr>
					<td class="left"><spring:message code="${itemCode}.external"/></td>
					<c:forEach var="year" begin="0" end="${project.duration-1}">
						<td><c:set var="inputId">years${year}external</c:set>
						<input id="${inputId}" class="<c:if test="${fn:contains(yearsErrors,inputId)}">error </c:if> num" disabled="disabled" type="text" size="5" value="<tags:formatCurrency value="${projectItem.years[year].external}"/>" name="years[${year}].totalCash"/>
					</td></c:forEach>
				</tr>
			</tbody>
		</table>
	</tags:table>
</div>