<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<display:setProperty name="basic.msg.empty_list"><spring:message code="misc.noItems"/></display:setProperty>
<display:setProperty name="paging.banner.one_item_found"><spring:message code="paging.banner.one_item_found"/></display:setProperty>
<display:setProperty name="paging.banner.all_items_found"><spring:message code="paging.banner.all_items_found"/></display:setProperty>
<display:setProperty name="paging.banner.full">[<a href="{1}"><spring:message code="misc.first"/></a> | <a href="{2}"><spring:message code="misc.previous"/></a>] {0} [<a href="{3}"><spring:message code="misc.next"/></a> | <a href="{4}"><spring:message code="misc.last"/></a>]</display:setProperty>
<display:setProperty name="paging.banner.first">[<spring:message code="misc.first"/>/<spring:message code="misc.previous"/>] {0} [<a href="{3}"><spring:message code="misc.next"/></a> | <a href="{4}"><spring:message code="misc.last"/></a>]</display:setProperty>
<display:setProperty name="paging.banner.last">[<a href="{1}"><spring:message code="misc.first"/></a> | <a href="{2}"><spring:message code="misc.previous"/></a>] {0} [<spring:message code="misc.next"/>/<spring:message code="misc.last"/>]</display:setProperty>
<%-- <display:setProperty name="paging.banner.page.link"><a href="{1}" title="<spring:message code="paging.banner.page.link"/>{0}">{0}</a></display:setProperty> --%>
				