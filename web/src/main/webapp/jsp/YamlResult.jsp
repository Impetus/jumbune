
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Hadoop YAML Result</title>
</head>
<body>

	<%
		String tabs = (String) request.getAttribute("tabs");
		String clusterProfilerCategoriesJson=(String)request.getAttribute("clusterProfilerCategoriesJson");
		int stats_interval = 10000;
		if (request.getAttribute("stats_interval") != null) {
			stats_interval = (Integer) request
					.getAttribute("stats_interval");   
			
		}
		String jobJson=(String)request.getAttribute("jobJson");
		
	%>


	<div id="mainBox">
		<jsp:include page="Header.jsp" />
		<div id="contentBox">
			<div class="contentinnerbox">
				<div id="tabs">
					<div id="exportlinkBox" class="exportlink" style="display: none;">
						<a id="exportXlData" href="javascript:void(0);">Export</a>
					</div>

					<ul id="tabUl">

						<%
							if (tabs.contains("Dashboard")) {
						%>
						<li class="ui-state-default"><a href="#dashboardTabContent"
							class="first"><span>Dashboard</span>
						</a>
						</li>
						<%
							}
							if (tabs.contains("Cluster Profiling")) {
						%>

						<li class="ui-state-default"><a href="#profilingTabContent"
							class="first"><span>Cluster Monitor</span>
						</a>
						</li>
						<%
							}
							if (tabs.contains("Static Profiling")) {
						%>
						<li class="ui-state-default"><a
							href="#staticProfilingTabContent" class="first"><span>M/R
									JVM Profiler</span>
						</a>
						</li>
						<%
							}
							if (tabs.contains("Debug Analysis")) {
						%>
						<li class="ui-state-default"><a
							href="#debugAnalysisTabContent" class="first"><span>Flow
									Debugger</span>
						</a>
						</li>
						<%
							}
							if (tabs.contains("Pure Jobs")) {
						%>
						<li class="ui-state-default"><a href="#pureJobsTabContent"
							class="first"><span>M/R Job Counters</span>
						</a>
						</li>
						<%
							}
							if (tabs.contains("Data Validation")) {
						%>
						<li class="ui-state-default"><a
							href="#dataValidationTabContent" class="first"><span>HDFS
									Validator</span>
						</a>
						</li>
						<%
							}
							
						%>
						<li class="ui-state-default"><a href="#errorTabContent"
							class="first"><span>Failed Jobs</span>
						</a>
						</li>
					</ul>

					<div id="tabContainer">
						<div id="killJobResponse" class="status success">
							<span>Job has been killed </span>
							<a href="/Home">Back to Home Page</a>
						</div>
						
						<%
							if (tabs.contains("Dashboard")) {
						%>
						<div id="dashboardTabContent">
							<div id="dashboardJSPContent">
								<jsp:include page="Dashboard.jsp" />
							</div>
							<div id="dashboardTabLoader" class="loaderMainBox"
								style="display: none;">
								<div class="txtCenter">
									<img src="./skins/images/loading.gif" />
								</div>
							</div>
						</div>

						<%
							}
							if (tabs.contains("Data Validation")) {
						%>
						<div id="dataValidationTabContent">
							<div id="dataValidationJSPContent" style="display: none;">
								<jsp:include page="Datavalidation.jsp" />
							</div>
							<div id="dataValidationTabLoader" class="loaderMainBox">
								<div class="txtCenter">
									<img src="./skins/images/loading.gif" />
								</div>
							</div>
						</div>

						<%
							}
							if (tabs.contains("Cluster Profiling")) {
						%>
						<div id="profilingTabContent">
							<div id="profilingJSPContent" style="display: none;">
							<input type="hidden" id="var_categoriesJson" value='<%=clusterProfilerCategoriesJson%>'>
								<jsp:include page="Profiler.jsp" />
							</div>
							<div id="profilingTabLoader" class="loaderMainProfileBox">
								<div class="txtCenter">
									<img src="./skins/images/loading.gif" />
								</div>
							</div>
						</div>


						<%
							}
							if (tabs.contains("Static Profiling")) {
						%>
						<div id="staticProfilingTabContent">
							<div id="staticProfilingJSPContent" style="display: none;">
								<jsp:include page="StaticProfiler.jsp" />
							</div>
							<div id="staticProfilingTabLoader" class="loaderMainBox">
								<div class="txtCenter">
									<img src="./skins/images/loading.gif" />
								</div>
							</div>
						</div>

						<%
							}
							if (tabs.contains("Debug Analysis")) {
						%>
						<div id="debugAnalysisTabContent">
							<div id="debugAnalysisJSPContent" style="display: none;">
								<jsp:include page="DebugAnalysis.jsp" />
							</div>
							<div id="debugAnalysisTabLoader" class="loaderMainBox">
								<div class="txtCenter">
									<img src="./skins/images/loading.gif" />
								</div>
							</div>
						</div>

						<%
							}
							if (tabs.contains("Pure Jobs")) {
						%>
						<div id="pureJobsTabContent">
							<div id="pureJobsJSPContent" style="display: none;">
								<jsp:include page="PureNInstrument.jsp" />
							</div>
							<div id="pureJobsTabLoader" class="loaderMainBox">
								<div class="txtCenter">
									<img src="./skins/images/loading.gif" />
								</div>
							</div>
						</div>

						<%
							}
							
						%>
						<div id="errorTabContent">
							<div id="errorJSPContent">
								<jsp:include page="Error.jsp" />
							</div>
						</div>
					</div>

				</div>
			</div>
			<div class="clear"></div>
			<jsp:include page="Footer.jsp" />
			<div id="chartpseudotooltip"></div>
		</div>
	</div>



	<script language="javascript">
	var ajaxInterval = '<%=stats_interval%>';	
	profilerSchInterval = '<%=stats_interval%>';

		var counter = 0;
		var stopAjaxCall = false;
		var stopProfileAjaxCall = false;
		var showExcel = false;
		var timerId;
		var profileTimerId;
		var profileAvailNodeTimerId;
		var nodeTimerId;
		var profileAjaxArr = [];
		var nameNodeFlag = false;
		var tabsAJAXCount = 0;
		var tabsLength =0;
		var isHiddenkillJobLink = false;
		var loadedModules = [];
		

		$(document)
				.ready(
						function() {
								//Click on export button
							$('#exportXlData')
									.click(
											function() {
												$
														.ajax(
																{
																	type : "POST",
																	cache : false,
																	async : true,
																	url : "ExportExcelServlet"
																})
														.done(
																function(
																		response) {
																	var URL = response;
																	if (response
																			.indexOf(" Export Utility failure") != -1) {
																		alert(response);
																	} else {
																		window
																				.open(
																						URL,
																						"_blank");
																	}
																});

											});
							$("#homeLink").hide();
							$(".logoBox a").bind("click", function() {
								return false;
							});
							
							$("#tabs").tabs({
								spinner : 'Retrieving data...'
							}, {
								selected : 0
							}, {
								fx : {
									opacity : 'toggle'
								}
							}, {
								select: function( event, ui ) {
									var activeTabIdx = $(ui.tab).parent().index();
									// ID OF anchor tag of ACTIVE TAB
									var selector = '#tabs > ul > li > a';
									var activeTabHREF = $(selector).eq(activeTabIdx).attr('href');
									if ( isHiddenkillJobLink ) {
										if ( activeTabHREF == "#profilingTabContent" ) {
											$("#killJobLink").hide();
											$("#killJobResponse").hide();
										} else {
											$("#killJobLink").show();
											$("#killJobResponse").show();
										}
									}
								}
							});
							$("#tabs").tabs("refresh");
                                                
	<%if (tabs.contains("Static Profiling")) {%>
	$('#summary-profiler').show();
		timerId = setInterval(callServletForJSON, ajaxInterval);
	<%}%>

	<%if (tabs.contains("Debug Analysis")) {%>
		$('#summary-debugger').show();
		timerId = setInterval(callServletForJSON, ajaxInterval);
	<%}%>
	
	<%if (tabs.contains("Pure Jobs")) {%>
		timerId = setInterval(callServletForJSON, ajaxInterval);
	<%}%>

	<%if (tabs.contains("Data Validation")) {%>
		$('#summary-data-validation').show();
		timerId = setInterval(callServletForJSON, ajaxInterval);
	<%}%>
		
	<%if (tabs.contains("Cluster Profiling")) {%>
		profileTimerId = setInterval(function() {callProfileServletForJSON('');}, ajaxInterval);
	<%}%>
		tabsLength = $('#tabs').tabs("length");
							$('#tabs').tabs("option", "disabled",
									[ tabsLength - 1 ]);		
									
		var activeTabIdx = $('#tabs').find('li.ui-state-active').index();
		var selector = '#tabs > ul > li > a';
		var activeTabHREF = $(selector).eq(activeTabIdx).attr('href');
		if ( activeTabHREF == "#profilingTabContent" ) {
			$("#killJobLink").hide();
		}
		
		if ( $("#tabUl li").not(".ui-state-disabled").size() == 1 && activeTabHREF == "#profilingTabContent" ) {
			$("#homeLink").show();
			$(".logoBox a").unbind("click");
		}
		
								
		$("#killJobLink a").click(function() {
			var jobJson11=JSON.stringify(<%=jobJson%>);
			$.ajax({
				type : "POST",
				url : "ResultServlet",
				data : {'killJob': "TRUE"},
				error : function(xhr, ajaxOptions, thrownError) {}
			}).done(function(response) {
				$("#killJobResponse").show();
				$("#killJobLink").hide();
				isHiddenkillJobLink = true;
				$(".logoBox a").unbind("click");
				stopAjaxCall = true;
				timerId = window.clearInterval(timerId);
				$(".ui-tabs-panel").each(function() {
					var contents = $(this).html();
					var id = $(this).attr("id");
					if ( jQuery.inArray(id, loadedModules) == -1 && id != "profilingTabContent" && id != 'dashboardTabContent' ) {
						$(this).html('<div style="display:none;">'+ contents +'</div>');
						$("#dashboardTabContent").find("div[data-target='"+ id +"']").hide();
					}
				});
						});
		});

						});

		function callServletForJSON() {
			if (stopAjaxCall == false) {
				$.ajax(
								{
									type : "POST",
									cache : false,
									async : false,
									url : "ResultServlet",
									error : function(xhr, ajaxOptions,
											thrownError) {
										$('.loaderMainBox')
												.html(
														'<div class="status info"><span>Information: </span>Connection to the server got lost.</div>');
										stopAjaxCall = true;
									}
								})
						.done(
								function(finalJSON) {
									finalJSON = jQuery.parseJSON(finalJSON);

									profilerSchInterval = finalJSON.stats_interval;

									if (typeof profilerSchInterval == 'undefined') {
										profilerSchInterval = ajaxInterval;
									}

									finalJSON = jQuery.parseJSON(finalJSON.reports);

									$.each(
													finalJSON,
													function(finalJSONKey,
															finalJSONVal) {

														if (finalJSONKey == "PURE_PROFILING") {
															loadedModules.push('staticProfilingTabContent');
															$('#staticProfilingJSPContent').show();
															$('#staticProfilingTabLoader').remove();
															enableStaticProfilingTab(finalJSONVal);
															
														}
														if (finalJSONKey == "DATA_VALIDATION") {
															loadedModules.push('dataValidationTabContent');
															$('#dataValidationJSPContent').show();
															$('#dataValidationTabLoader').remove();
															showExcel = true;
															enableDataValidationTab(finalJSONVal);
															
														}
														if (finalJSONKey == "DEBUG_ANALYZER") {
															loadedModules.push('debugAnalysisTabContent');
															$('#debugAnalysisJSPContent').show();
															$('#debugAnalysisTabLoader').remove();
															showExcel = true;
															enableDebugAnalysis(finalJSONVal);
															
														}
														if (finalJSONKey == "PURE_JAR_COUNTER") {
															loadedModules.push('pureJobsJSPContent');
															$(
																	'#pureJobsJSPContent')
																	.show();
															$(
																	'#pureJobsTabLoader')
																	.hide();
															showExcel = true;
															enablePureJobGrid(finalJSONVal);
														}
														if (finalJSONKey == "INSTRUMENTED_JAR_COUNTER") {
															loadedModules.push('pureJobsTabContent');
															$(
																	'#pureJobsJSPContent')
																	.show();
															$(
																	'#pureJobsTabLoader')
																	.hide();
															showExcel = true;
															enableInstrumentJobGrid(finalJSONVal);
														}
														if (finalJSONKey == "DATA_SCIENCE_REQUEST") {
															// todo
															populateDsForm(finalJSONVal);
														}
														if (finalJSONKey == "AJAXCALL") {
															checkAllAJAXComplete();
															$('.loaderMainBox').html('<div class="status info"><span>Information: </span>Unable to process the module as the dependent component(s) failed</div>');
															
															if (showExcel == true) {
																$(
																		'#exportlinkBox')
																		.show(
																				'slow');
															}
														}
														if (showExcel == true) {
															$(
																	'#exportlinkBox')
																	.show(
																			'slow');
														}

													});
								});
			} else {
				clearInterval(timerId);
			}
		}

		function callProfileServletForJSON(generalSettingJson) {

			var ajaxParam = '';
			if (generalSettingJson) {
				ajaxParam += 'general_settings=' + generalSettingJson;
			}

			if (generalSettingJson && nameNodeFlag ==  false) {
	                        ajaxParam += '&NAME_NODE=TRUE';
	                        nameNodeFlag = true;
                        }
			else if (nameNodeFlag ==  false) {
	                        ajaxParam += 'NAME_NODE=TRUE';
	                        nameNodeFlag = true;
                        }

			if (stopProfileAjaxCall == false) {
				var ajaxReq = $
						.ajax(
								{
									type : "POST",
									cache : false,
									url : "ProfilerServlet",
									data : ajaxParam,
									error : function(xhr, ajaxOptions,
											thrownError) {
										$('.loaderMainBox')
												.html(
														'<div class="status info"><span>Information: </span>Connection to the server got lost.</div>');
										stopProfileAjaxCall = true;
									}
								})
						.done(
								function(finalJSON) {
									console.log("finalJson profiling: - "
											+ finalJSON);
									finalJSON = jQuery.parseJSON(finalJSON);

									profilerSchInterval = finalJSON.stats_interval;

									if (typeof profilerSchInterval == 'undefined') {
										profilerSchInterval = ajaxInterval;
									}

									finalJSON = jQuery
											.parseJSON(finalJSON.reports);

									$
											.each(
													finalJSON,
													function(finalJSONKey,
															finalJSONVal) {
														if (finalJSONKey == "CLUSTER_VIEW") {
															$(
																	'#profilingJSPContent')
																	.show();
															$(
																	'#profilingTabLoader')
																	.hide();

															profilingClusterTabData(finalJSONVal);
															counter++;
														}  else if (finalJSONKey == "AJAXCALL") {
															checkAllAJAXComplete();
															$(
																	'.loaderMainProfileBox')
																	.html(
																			'<div class="status info"><span>Information: </span>Unable to process the module as the dependent component(s) failed</div>');
															stopProfileAjaxCall = true;
														}

													});
								});

				profileAjaxArr.push(ajaxReq);

			} else {
				clearInterval(profileTimerId);
			}
		}

		function callProfilerOnSuccess(finalJSON) {
			console.log("finalJson on success: - " + finalJSON);
			finalJSON = jQuery.parseJSON(finalJSON);

			profilerSchInterval = finalJSON.stats_interval;

			if (typeof profilerSchInterval == 'undefined') {
				profilerSchInterval = ajaxInterval;
			}

			finalJSON = jQuery.parseJSON(finalJSON.reports);

			$
					.each(
							finalJSON,
							function(finalJSONKey, finalJSONVal) {
								if (finalJSONKey == "NETWORK_LATENCY_VIEW") {
									$('#profilingJSPContent').show();
									$('#profilingTabLoader').hide();
									$('#dataCenterMainBox').show();
									$('#dataCenterLoaderBox').hide();

									networkLatencyTabData(finalJSONVal,
											'ul#networkNodeList');
									counter++;
								} else if (finalJSONKey == "DATALOAD_VIEW") {
									$('#profilingJSPContent').show();
									$('#profilingTabLoader').hide();
									$('#dataCenterMainBox').show();
									$('#dataCenterLoaderBox').hide();

									dataLoadTabData(finalJSONVal, 'ul#dataLoadNodeList');
									counter++;
								} else if (finalJSONKey == "DATA_DISTRIBUTION_VIEW") {
									$('#profilingJSPContent').show();
									$('#profilingTabLoader').hide();
									$('#dataCenterMainBox').show();
									$('#dataCenterLoaderBox').hide();

									hdfsDataDistributionTabData(finalJSONVal, 'ul#hdfsDataDistributionNodeList');
									counter++;
								} else if (finalJSONKey == "AJAXCALL") {
									$('.loaderMainBox')
											.html(
													'<div class="status info"><span>Information: </span>Unable to process the module as the dependent component(s) failed</div>');
									stopProfileAjaxCall = true;
								}

							});
		}

		function customJQPlotTooltip(chartType, chartHolderId, plotVar, sign) {
			$('#' + chartHolderId)
					.bind(
							'jqplotDataMouseOver',
							function(ev, seriesIndex, pointIndex, data) {
								var borderColor = '';
								var mouseX = ev.pageX; //these are going to be how jquery knows where to put the div that will be our tooltip
								var mouseY = ev.pageY - 40;
								if (chartType == "pie") {
									borderColor = plotVar.series[seriesIndex].seriesColors[pointIndex];
									$('#chartpseudotooltip').html(
											"<b>" + data[0] + "</b> : "
													+ data[1] + " " + sign)
											.show();
								} else if (chartType == "donut") {
									borderColor = plotVar.series[seriesIndex].seriesColors[pointIndex];
									$('#chartpseudotooltip')
											.html(
													"<b>"
															+ data[0]
															+ "</b> : "
															+ plotVar.series[seriesIndex].dataLabels[pointIndex]
															+ " " + sign)
											.show();
								} else {
									borderColor = plotVar.series[seriesIndex].seriesColors[seriesIndex];
									$('#chartpseudotooltip').html(
											"<b>" + data[0] + "</b> : "
													+ data[1] + " " + sign)
											.show();
								}

								var cssObj = {
									'position' : 'absolute',
									'left' : mouseX + 'px', //usually needs more offset here
									'top' : mouseY + 'px',
									'borderColor' : borderColor
								};
								$('#chartpseudotooltip').css(cssObj);
							});

			$('#' + chartHolderId).bind('jqplotDataUnhighlight', function(ev) {
				$('#chartpseudotooltip').html('').hide();
			});
		}
		
		function checkAllAJAXComplete() {
			$("#homeLink").show();
			$("#killJobLink").hide();
			isHiddenkillJobLink = false;
			$(".logoBox a").unbind("click");
			stopAjaxCall = true;
			timerId = window.clearInterval(timerId);
		}
	</script>
</body>
</html>
