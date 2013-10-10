<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><div id="top">
	<div id="user" align="right"> <a href="<%=request.getContextPath()%>/config/user/${user.userId}">(Welcome ${user.description})</a> | <a id="logoff" href="<%=request.getContextPath()%>/j_spring_security_logout">Log off</a>&nbsp;</div>
	<div id="search">
		<img src="<%=request.getContextPath()%>/img/logo.gif" alt="RuralInvest"/>
		<div>
			<span>Quick Search:</span>
			<form name="quickSearch" action="<%=request.getContextPath()%>/search/quick" method="post" style="margin:0;display:inline-block;">
			<input name="freeText" id="myFreeText" size="16"/>
			<input type="hidden" name="unfinished"/>
			<select name="objType" id="profOrProj">
				<option value="igpj">project ($)</option>
				<option value="nigpj">project (:)</option>
				<option value="igpf">profile ($)</option>
				<option value="nigpf">profile (:)</option>
			</select>
			<input class="button" type="submit" value="go" />
			</form>
		</div>
	</div>
	<tags:menu/>
</div>