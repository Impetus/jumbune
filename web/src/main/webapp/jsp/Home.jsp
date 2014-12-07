<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta http-equiv="Cache-control" content="no-cache">
<meta http-equiv="Expires" content="-1">
<title>Jumbune</title>
<style>
	body { background:#282834 !important; }
</style>
</head>
<body>
	
	<div class="top-wrapper"> 
	  <!-- Wrapper start-->
	  <div class="wrapper"> 
		
		<!-- Header start-->
		<div class="header">
		  <div class="navbar-header">
			<jsp:include page="Meta.jsp" />
			<a href="/Home"><img src="${pageContext.servletContext.contextPath}/skins/images/logo_text.png" alt="Jumbune Logo"/></a>
		</div>
		  <div class="navbar-right">
		  </div>
		  <div class="clear"></div>
		</div>
		<!-- Header end-->
		<div class="clear"></div>
		<!-- Container start-->
		<div class="container">
		  <div class="midblock">
			<p> <br />
			  </p>
			<p class="last"></p>
			<div class="clear"></div>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>
			<br/>			
		  </div>
		</div>
		<!-- Container end--> 
	  </div>
	  <!-- Wrapper end--> 
	  
	  <!-- Green Block start-->
	  <div class="green_block">
		<div class="wrapper">
		  <div class="container">

			<ul class="bottm_nav">
			  <li>
				<div class="icon_block"><a id="createYamlBtn" href="javascript:void(0)"><span class="icon"><img src="skins/images/create_icon.png" /></span></a></div>
				<h3>New</h3>
				<p>Start with a new wizard to create a job</p>
			  </li>			
			  <li>
				<div class="icon_block"><a id="uploadfiles" href="javascript:void(0)"><span class="icon"><img src="skins/images/upload_icon.png" /></span></a></div>
				<h3>Open</h3>
				<p>Browse filesystem to import a job json</p>
				<div id="displayFileUpload"  class="uploadfilewrap">
					<input type="text" readonly="readonly" value="Click here to browse" id="pickfiles" style="z-index:1 !important;display:none"  />								
				</div>
			  </li>
			  <li>
				<div class="icon_block"><a id="yamlRepositry" href="javascript:void(0)"><span class="icon"><img src="skins/images/select_icon.png" /></span></a></div>
				<h3>Select</h3>
				<p>Select a job json from recent ones</p>
			  </li>
			</ul>
			<div class="clear"></div>
		  </div>
		</div>
	  </div>
	  <!-- Green Block end--> 
	</div>
	<!-- Footer start-->
	<div class="footer"><a target="_blank" href="http://jumbune.org/terms.html">Terms of Use</a></div>
	<!-- Footer end-->
	
	<div id="yamlFormModel"></div>
	<div id="yamlRepositryModelBox" style="display:none;"><table id="yamlRepositryModel"></table></div>
	<div id="yamlFileUploadModel" style="display:none;">File Uploaded Successfully!</div>
	<div class="clear"></div>


	<script language="javascript">
		$(document).ready(function() {
			function validateInputBoxes() {
				//console.log("in");
				var isEmpty = false;						
				$("#step-1 input[type='text']:visible").each(function() {						
					if($(this).val() == "") {										
						isEmpty = true;
					}												
				});
								
				if(isEmpty == true) {
					$(".buttonNext").addClass("disableNextStep");
				}
				else {
					$(".buttonNext").removeClass("disableNextStep");
				}
				
				if( $("#debugAnalysis").is(':checked') || $("#enableStaticJobProfiling").is(':checked') || $("#hadoopJobProfile").is(':checked') || $("#enableValidation").is(':checked') ) {
					$(".buttonFinish").removeClass("disableNextStep");
				} else {
					$(".buttonFinish").addClass("disableNextStep");
				}
			}	
				
			$('#createYamlBtn').click(function() {
				$("#yamlFormModel").load('jsp/YamlForm.jsp').dialog({
					dialogClass : 'modalSelectLocation',
					height : 600,
					width : 880,
					resizable : false,
					draggable:false,
					modal : true,
				close: function(event, ui) { 
					$('#yamlForm').validationEngine('hideAll');
					$('#pickfiles').val('Click here to browse');
					
				},
				open : function() { 
					setTimeout(function() {					
						$('#noOfSlavesBtn').click();
						$('#noOfJobsBtn').click();							
						//$("#step-1 input[type='text']:visible").propertychange();					
						validateInputBoxes();
						
					},1000);
						
			    }
				});
			});
			
			$("#displayFileUpload input[type='file']").live("change", function() {					
					uploader.start();
			});


			$('#yamlRepositry').click(function() {
				var yamlJobsGridData = [];
				var eachJobsJsonObj = '';
				$.ajax({
				  type: "POST",
				  cache: false,			  
				  url: 'JumbuneHistoryJobPickerServlet?selectJsonList=TRUE'
				  }).done(function( resp ) {
				  if(resp != null || $.trim(resp)!='')
					{
						var yamlArr = resp.split(',');
						var isBlank=false;
							
						for(i=0;i<yamlArr.length;i++)
						{
							
							var eachJobsJsonObj = { "id":i+1,"name":"<a href='javascript:void(0);' class='yamlJobName' rel='"+yamlArr[i]+"'>"+yamlArr[i]+"</a>" };
				yamlJobsGridData.push(eachJobsJsonObj);
							if($.trim(yamlArr[i])==="")
								isBlank=true;
						}
						
						if(isBlank==true && yamlArr.length==1){
							alert("No Yaml file found in repository! Please create or upload Yaml.")
							return;
						}
						
						var yamlDataJson={"response": yamlJobsGridData}; 				    
						
						jQuery("#yamlRepositryModel").jqGrid({
							datastr: yamlDataJson,
							datatype: "jsonstring",
							height: 200,
							scroll: true,
							hidegrid:false,
							loadui: "disable",
							colNames:["ID","Name"], 
							colModel:
								[ 
									{name:'id',index:'id', width:35, align:"center"},
									{name:'name',index:'name', width:320}			
								],												
							rowNum: 10000,		
							jsonReader: {
							repeatitems: false,
							root: "response"
							}
						});

						$("#yamlRepositryModelBox").dialog({
							dialogClass: 'modalSelectLocation',
							height:300,
							width:400,
							draggable:false,
							resizable:false,
							modal: true
						});
					}
				  });
				
			});

		$('.yamlJobName').live('dblclick', function() {
			$("#yamlRepositryModelBox").dialog('close');
			$.ajax({
				  type: "POST",
				  cache: false,			  
				  url: 'JumbuneHistoryJobPickerServlet?selectJsonList=FALSE&selectedJsonFileName='+$(this).attr('rel')
				  }).done(function( resp ) {
					// Called when a file has finished uploading
					$("#yamlFormModel").load('jsp/YamlForm.jsp', {"populateData" : resp}).dialog({
						dialogClass : 'modalSelectLocation',
						height : 600,
						width : 880,
						resizable : false,
						draggable:false,
						modal : true,
								close: function(event, ui) {
									$('#yamlForm').validationEngine('hideAll');
									$('#pickfiles').val('Click here to browse');
								}
					});
					$('#noOfSlavesBtn').trigger("click");
					$('#noOfJobsBtn').click();
				});
		});
		$('#pickfiles').css("z-index","1")

		});

		var uploader = new plupload.Uploader({
			runtimes : 'html5,silverlight',
			browse_button : 'uploadfiles',
			container : 'displayFileUpload',
			max_file_size : '10mb',
			url : 'UploadServlet',
			multi_selection : false,
			resize : {
				width : 320,
				height : 240,
				quality : 90
			},
			silverlight_xap_url : '/skins/js/plupload.silverlight.xap',
			filters : [ {
				title : "Jar files",
				extensions : "jar"
			}, {
				title : "JSON files",
				extensions : "json"
			} ],
			// Post init events, bound after the internal events
			init : { 
				FileUploaded : function(up, file, info) { 
					var res = info.response;
					res = $.parseJSON(res);
					console.log(res['ErrorAndException']);
					if(typeof res['ErrorAndException'] != 'undefined'){
							alert(res['ErrorAndException']);
							return true;
						}
					// Called when a file has finished uploading
					$("#yamlFormModel").load('jsp/YamlForm.jsp', {"populateData" : info.response}).dialog({
						dialogClass : 'modalSelectLocation',
						height : 600,
						width : 880,
						resizable : false,
						draggable:false,
						modal : true,
								close: function(event, ui) {
									$('#yamlForm').validationEngine('hideAll');
								}
					});
					$('#noOfSlavesBtn').trigger("click");
				}				
			}			
		});

		uploader
				.bind(
						'FilesAdded',
						function(up, files) {
							/*for ( var i in files) {
								document.getElementById('pickfiles').value = files[i].name + ' (' + plupload.formatSize(files[i].size) + ')';
							}*/
							$.each(files, function(i, file) {
								//var file_name=$("#pickfiles").val();
								$("#pickfiles").val(files.name+ ' (' + plupload.formatSize(file.size) + ')');
							});
							//$('#uploadfiles').removeAttr('disabled');
							//$('#uploadfiles').css('cursor','pointer');
							//$('#uploadfiles').css('opacity',1);
							up.refresh(); // Reposition Flash/Silverlight							
						});

		uploader
				.bind(
						'UploadProgress',
						function(up, file) { 
// 							document.getElementById(file.id)
// 									.getElementsByTagName('b')[0].innerHTML = '<span>'
// 									+ file.percent + "%</span>";
						});
		uploader.bind('Error', function(up, err) {			
			alert("Please select JSON file");	
			up.refresh(); // Reposition Flash/Silverlight
		});
		document.getElementById('uploadfiles').onclick = function() { 
			uploader.start();
		//	$("#pickfiles").click();
			/*setTimeout(function()	 {
				var fileName = $('#pickfiles').val();
				console.log(fileName);
				if(fileName.indexOf(".json") <= 0 ){
					$('#displayFileUpload').css('border','1px solid #FF0000');
					alert('Please select JSON file');
					return false;
				}
			},1000);
			$('#displayFileUpload').css('border','none');
			//$(this).css('opacity',0.2);
			uploader.start();
			setTimeout(function(){
				$('#pickfiles').val('Click here to browse');
				$('#pickfiles').css('z-index',1);	
			},1000);
			
			//toggle();*/
			//return false;
		}; 
		uploader.init();
	
		function toggle() {
			var ele = document.getElementById("displayFileUpload");
			if (ele.style.display == "block") {
				ele.style.display = "none";

			}
			//document.getElementById("filelist").style.display = "block";
		}
	</script>

</body>
</html>	
