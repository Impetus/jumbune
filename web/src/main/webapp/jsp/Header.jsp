<div id="headerBox">
	<div class="logoBox">
	<!-- <jsp:include page="Meta.jsp" /> -->
		<a href="/Home"><img src="${pageContext.servletContext.contextPath}/skins/images/logo_text.png" alt="Jumbune Logo"/></a>
	</div>

	<div class="topNavBox">
		<ul>
			<li><a href="/Home">Home</a></li>			
		</ul>
	</div>

	<% String jobName = (String) request.getAttribute("JobName"); 
	if (jobName!=null){%>
		<div style="float:right;clear:both;font-weight: bold;font-size:14px;">
			Job Name : <%=jobName%>
			<span class="kill-job-link" id="killJobLink">&nbsp;|&nbsp;<a href="javascript:void(0)">Kill Job</a></span>
		</div>
		
	<%}%>
</div>