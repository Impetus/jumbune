<%@ page language="java" contentType="text/html"
	import="org.jumbune.utils.exception.JumbuneException"
	isErrorPage="true"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta http-equiv="Cache-control" content="no-cache">
<meta http-equiv="Expires" content="-1">
<title>Jumbune</title>
</head>
<body>
	<div id="mainBox">
		<jsp:include page="Header.jsp" />

		<div class="fpcontentbox">

			<div class="contentinnerbox">
				<div align="center">
					<div id="wrap">
						<table height="25%" width="100%" border="1" cellpadding="0"
							cellspacing="0">
							<tbody>
								<tr>
									<td valign="top" width="100%">
										<div style="color: 'red'">
										<%
										if(exception != null){
											String msgException = exception.getMessage();																			    
										%>
												<h3>
													There has been a problem during processing because of below
													exception. Please try again later. </br>													
													<%= msgException %>
												</h3>
										<%
										}else{
										%>
											<h3>There has been a problem during processing. Please try again later.</h3>
										<%
										}
										%>

											<br>
									</td>
								</tr>

							</tbody>
						</table>
					</div>
				</div>
				<div class="clear"></div>
				<div class="fpcontentpanewrap"></div>

				<div id="yamlFormModel"></div>
				<div id="yamlFileUploadModel" style="display: none;">File
					Uploaded Successfully!</div>
				<div class="clear"></div>
				<jsp:include page="Footer.jsp" />
			</div>
		</div>
	</div>
</body>
</html>
