<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="donors" required="true" type="java.util.Set" %><%@ attribute name="labelKey" required="true" %><%@ attribute name="helpText" required="true" %><%@ attribute name="onmouseout" required="true" %>
<c:if test="${fn:length(project.donors) eq 1}">
	<tags:dataentry field="donations[0]" labelKey="${labelKey}" helpText="${helpText}" currency="true" onmouseout="CalculateDonated();" />
	<input type="hidden" name="donated" id="donated" />
	<script>
		function CalculateDonated() {
			$('#donated').val($('#donations0').val());
			${onmouseout};
		}
	</script>
</c:if>
<c:if test="${fn:length(project.donors) gt 1}">
	<spring:bind path="*"><c:if test="${empty status.errorMessages}"><c:set var="boxDisplay">display:none;</c:set></c:if></spring:bind>
	<tags:dataentry field="donated" button="manage" calculated="true" labelKey="${labelKey}" helpText="${helpText}" currency="true" onmouseout="${onmouseout}"/>
	<div id="donations" style="${boxDisplay} border:1px solid #aaa; margin-left:5px">
		<c:forEach var="donor" items="${donors}">
			<c:if test="${not donor.notSpecified}">
				<c:set var="desc"><c:choose>
					<c:when test="${donor.description eq 'state-public'}"><spring:message code="project.donor.statePublic"/></c:when>
					<c:otherwise>${donor.description}</c:otherwise>
				</c:choose></c:set>
				<tags:dataentry field="donations[${donor.orderBy}]" label="${desc}" currency="true" />
			</c:if>
			<c:if test="${donor.notSpecified}"><c:set var="notSpecifiedOrder" value="${donor.orderBy}"/></c:if>
		</c:forEach>
		<tags:dataentry field="donations[${notSpecifiedOrder}]" labelKey="project.donor.notSpecified" currency="true" />
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
			$("#donations :input:not(:disabled,.curlabel)").each(function(i,n) {
				donatedsum += parseFloat(formatToNum($(n).val()));	
			});
			$('#donated').val(numToFormat(round(parseFloat(donatedsum), decLength)));
		}
		$("#donations :input:not(:disabled)").mouseleave(function(e) {
			CalculateDonated();
			${onmouseout};
		});
	</script>
</c:if>