<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="fieldA" required="true" %><%@ attribute name="fieldB" required="true" %><%@ attribute name="fieldC" required="true" %>
<%@ attribute name="functionName" required="true" %>
<%@ attribute name="fieldD" %><%@ attribute name="fieldE" %>
<%@ attribute name="calc" required="true" %><%@ attribute name="calc2" %><%@ attribute name="calc3" %>
<%@ attribute name="callWhenDone" %><%@ attribute name="nonCurrency" %>

<script language="JavaScript">
	function ${functionName}() { C='';
		with (Math) {
			A = formatToNum($('#${fieldA}').val()); 
			B = formatToNum($('#${fieldB}').val()); 
			C = A${calc}B; 
			<c:if test="${not empty fieldD}">
				D = formatToNum($('#${fieldD}').val()); 
				C = C${calc2}D;
			</c:if>
			<c:if test="${not empty fieldE}">
				Ee = formatToNum($('#${fieldE}').val()); 
				C = C${calc3}Ee;
			</c:if>	
		}if (C == 'NaN') { C = ''; }
		<c:if test="${nonCurrency}">$('#${fieldC}').val((C+'').replace('.',curSepDec));</c:if>
		<c:if test="${empty nonCurrency}">$('#${fieldC}').val(numToFormat(round(C, decLength)));</c:if>
		<c:if test="${not empty callWhenDone}">${callWhenDone}();</c:if>
	}
</script>