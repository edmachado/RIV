<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="end" required="true" %>
<div id="yearSelector">
	<c:forEach var="year" begin="0" end="${end}">
		<span id="yearBox${year}" class="yearBox<c:if test='${year eq 0}'> selected</c:if>">
			<a href="#" onclick="javascript:showYear(${year});">${year+1}</a>
		</span>
	</c:forEach>
	<br/>
</div>	