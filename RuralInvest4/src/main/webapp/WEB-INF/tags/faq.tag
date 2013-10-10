<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="q" required="true" %><%@ attribute name="a" required="true" %>
 <c:set var="faqnum" scope="request">${faqnum+1}</c:set>
 <a style="cursor:help" onClick="toggle('faq${faqnum}')"><b><spring:message code="${q}"/></b></a>
 <br/>
 <div class="faq" id="faq${faqnum}" style="display:none">&gt; <spring:message code="${a}"/></div>
 <br/>