<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta http-equiv="Cache-control" content="no-cache">
	<meta http-equiv="Expires" content="-1">
	<title>Hadoop Testing Framework</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/skins/leap_style2.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/skins/fonts-min.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/htf_style.css">
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/jquery-1.7.1.min.js"></script>
	<script>
	$(document).ready(function() {
		$("#submit").click(function(){
											
			if($("#fileName").val().length==0){
				$("#file-error").text('Please select a file to start execution.');
				return false;
			}
			var ext = $('#fileName').val().split('.').pop().toLowerCase();
			if($.inArray(ext, ['yaml']) == -1) {
				$("#file-error").text('Please select a YAML file');
				return false;
			}
			
		});
		
		$("#clear-logs").click(function(){
			$.ajax({
				type: "POST",
		      	 url  	 : "ClearLogs",
		      	 data 	 : "",
		      	 cache 	 : false,
		      	 success : function(data){
		      		if(data.success==true){
		      			alert('Logs Cleared Successfully ');
		      		}
		      		else{
		      			alert('There was a problem trying to clear logs');
		      		}
		      	},
				error:function (xhr, ajaxOptions, thrownError){
					alert('There was a problem trying to clear logs');
           		 }  
				 
		      });
			
		});
		
	});
	$(function() {
		
		$('.info-image').hover(
			function() {				
				var tooltip = $(this).closest('.field').find('.info-message');
				var pos = $(this).position(); 
				tooltip.css({ 'top':(pos.top+27)+'px', 'left':(pos.left-90)+'px' });
				tooltip.show();
			}, 
			function() {
				$(this).closest('.field').find('.info-message').hide();
			}
		);
	});
	
	
	
	

	</script>		
	
</head>

<body onload="clearForm()">
	<div align="center">
			<div id="wrap">
				<table height="100%" width="100%" border="1" cellpadding="0" cellspacing="0">
					<tbody>
						<tr height="4%">
	   						<td colspan="2" align="left">
	   							<div id="header-inside">
									<div id="logo" style="font-size:45px; font-family:tahoma; color:white; padding-top:25px; padding-left:20px;">
											Hadoop Testing Framework
									</div>
								</div>	
								
						  </td>
				       </tr>
				       <tr height="94%">
	    					<td valign="top" width="100%">    
	         					<div style="color:#009900">
	         					<h3>Home Page</h3>
	         					</div>
								 <br><br><br>
								 	<b><div id="file-error" style="color:red"></div></b>
								 	<form action="HTFServlet" method="post" enctype="multipart/form-data">
							        	 <table >
							                    <tr>
							                    	<center><td colspan="2">
							                    					<div class='field'>
																		<center><label><b>Upload your YAML file here</b> <img src='images/info.png' class='info-image'/></label></center>
																		<div class='info-message' style='height:40px; width:100px;'>
																			<div class='speech-icon'></div><p>This is the YAML file which contains all the configurations.</p>
																		</div>
																	</div>
							                    			</td>
							                    	</center>
							                    </tr>
							                    <tr>
							                    	<td><b>File Name:</b></td>
							                    	<td><input name="fileName" id="fileName" type="file"></td>
							                    </tr>
							                    <tr>
							                    	<td colspan="2"><p align="center"><input type="submit" id="submit" value="Start Execution"></p></td>
							                    </tr>
							             </table>
							         </form>
	
	         					<br/><br/><br/>
										<p><b>For clearing logs genrerated by previous execution,</b></p>							         					
	         							<b>Click here </b><input type="submit" id="clear-logs" value="Clear Logs">
	         					
							</td>
						</tr>						
					
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>