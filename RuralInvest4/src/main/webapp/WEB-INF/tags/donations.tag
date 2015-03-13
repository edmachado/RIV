<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="donors" required="true" type="java.util.Set" %><%@ attribute name="labelKey" required="true" %><%@ attribute name="helpText" required="true" %><%@ attribute name="onmouseout" required="true" %>

<tags:dataentry field="donated" button="manage" calculated="true" labelKey="${labelKey}" helpText="${helpText}" currency="true" onmouseout="${onmouseout}"/>
<div id="donations" style="display:block; border:1px solid #aaa; margin-left:5px">
	<c:forEach var="donor" items="${donors}">
		<tags:dataentry field="donations[${donor.donorId}]" label="${donor.description}" currency="true" />
	</c:forEach>
</div>

<script language="JavaScript">
	$( "#donation-button" ).click(function() {
		if ($('#donations').is(':visible')) {
			$("#donations").slideUp(100);
		} else {
			$("#donations").slideDown(100 );
		}
		$("#donation-button > span").toggleClass("ui-icon-circle-triangle-e ui-icon-circle-triangle-s");
	});
	
	function CalculateDonated() {
		var donatedsum=0.0;
		$("#donations :input:not(:disabled)").each(function(i,n) {
			donatedsum += parseFloat(formatToNum($(n).val()));	
		});
		$('#donated').val(numToFormat(round(parseFloat(donatedsum), decLength)));
	}
	
	$("#donations :input:not(:disabled)").mouseleave(function(e) {
		CalculateDonated();
		${onmouseout};
	});
</script>