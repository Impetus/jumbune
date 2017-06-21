<div id="freeow" class="freeow freeow-top-right"></div>


<div class="pageTopPane">
	<h2 class="pageTitle">Monitoring</h2>
	<div class="iphone" style="float: right";>
		<div class="timer-block">
			<span class="interval-label"><input type="checkbox" name="toggleProfilerState" id="toggleProfilerStateFld" checked /></span>
			<span class=""><b>Monitor</b></span>
			<!-- div id="toggleProfilerState" class="toggleDisable"></div -->
		</div>
		<div class="timeInterval">
			<span class="interval-label">Monitoring Interval:</span> <span><input
				type="text" id="timeInterval" size="10" placeholder="In Secs" value="10"
				class="input">  sec</span>
		</div>
		<div class="timeInterval">
			<span class="interval-label">Monitor Off Time:</span> 
			<span class="profiler-off-time"> &nbsp; <!-- --:--:--         --/--/---- --> </span>
		</div>
	</div>
	<div id="profilingErrorLinks" class="errorLinkBox"></div>
</div>

<div class="commonBox">
	<div class="fleft">
		<div class="fleft">
			<div id="dcTabLink" class="dataCenterTab profilerTab tabSelected">
				<img src="../skins/images/data_center.png" alt="Data Center" />
			</div>
			<div id="catTabLink" class="catCenterTab profilerTab">
				<img src="../skins/images/categories.png" alt="Categories" />
			</div>
			<div id="dataLoadTabLink" class="dataLoadCenterTab profilerTab">
				<img src="../skins/images/data_load.png" alt="Data Load" />
			</div>
			<div id="hdfsDataDistributionTabLink"
				class="dataLoadCenterTab profilerTab">
				<img src="../skins/images/replicate.png" alt="Data Load" />
			</div>
		</div>
		<div id="dataCenterContainer" class="dataCenterBox"
			style="position: relative; min-height: 514px;">
			<div id="genSetImg"></div>
			<ul id="dataCenterList" class="accordianList">

			</ul>
			<div id="generalSettingBox">
				<form id="generalSettingForm" method="post">
					<div id="generalSettingDataBox"></div>

					<fieldset>
						<div>
							<div class="commonBox">
								<div class="paddLeftRight">
									<button type="button" id="genSetSaveBtn" class="button">Save</button>
								</div>
								<div class="paddLeftRight">
									<button type="button" id="genSetCancelBtn" class="button">Cancel</button>
								</div>

							</div>
					</fieldset>
				</form>

			</div>
		</div>

		<div id="categoriesContainer" class="dataCenterBox"
			style="display: none; min-height: 514px;">
			<form id="settingForm" method="post">
				<ul id="categoriesList" class="accordianList">

				</ul>
			</form>
		</div>

		<div id="dataLoadContainer" class="dataCenterBox"
			style="display: none; min-height: 514px;">
			<ul id="dataLoadNodeList" class="accordianList">

			</ul>
		</div>

		<div id="hdfsDataDistributionContainer" class="dataCenterBox"
			style="display: none; min-height: 514px;">
			<div id="hdfsFieldBox">
				<div class="commonBox">
					<div class="lbl">
						<label>HDFS Path</label>
					</div>
					<div class="fld">
						<input type="text" class="inputbox" id="hdfsFileName"
							name="hdfsFileName">
					</div>
				</div>
				<div class="commonBox">
					<div class="lbl">&nbsp;</div>
					<div class="fld">
						<button id="hdfsSubmitBtn">
							<span>Submit</span>
						</button>
					</div>
				</div>
			</div>
			<div id="hdfsDCBox" style="display: none;"></div>
		</div>

	</div>

	<div class="fleft">
		<div id="favWidgetBox" class="profilingWidget"
			style="position: relative;">
			<span class="favStar"> </span> <span class="favHeader">
				Favorites
				<div class="nodeIpCls fright bold"></div> </span>
			<div id="favInnerBox"></div>
		</div>

		<div id="trendWidgetBox" class="profilingWidget"
			style="clear: both; margin-top: 10px; position: relative;">
			<span class="trendStar"> </span> <span class="favHeader">
				Trends
				<div class="nodeIpCls fright bold"></div> </span>
			<div id="trendInnerBox"></div>
		</div>

		<div class="profilingWidget" id="hdfsDataBox" style="display: none;">
			<div class="favHeader" id="nodeRelationTitleBox">Cluster-wide HDFS Block Health</div>

			<div id="hdfsDataInformation" class="fleft" style="width: 500px;">

			</div>
		</div>

		<div class="profilingWidget" id="hdfsDataBoxNodeInfo"
			style="display: none; clear: both; margin-top: 10px; position: relative;">
			<div class="favHeader" id="nodeRelationTitleBox">File Blocks Placement</div>
			<div id="hdfsDataNodeInformation" class="fleft" style="width: 500px;">

			</div>
		</div>

	</div>

	<div id="passwordModel" style="display: none;">
		<form id="nodeListForm" name="nodeListForm" method="POST">
			<div class="fright">
				<a id="copyToAllLink" href="javascript:void(0);">Copy To All</a>
			</div>
			<div class="fleft">
				<div id="passwordModelBox" class="password-model-box"></div>
			</div>
		</form>
	</div>
</div>




<script type="text/JavaScript">

//For todays date;
	Date.prototype.today = function(){ 
    	return ((this.getDate() < 10)?"0":"") + this.getDate() +"/"+(((this.getMonth()+1) < 10)?"0":"") + (this.getMonth()+1) +"/"+ this.getFullYear() 
	};
	Date.prototype.timeNow = function(){
		return ((this.getHours() < 10)?"0":"") + this.getHours() +":"+ ((this.getMinutes() < 10)?"0":"") + this.getMinutes() +":"+ ((this.getSeconds() < 10)?"0":"") + this.getSeconds();
	};

	var newDate = new Date();
	var datetime = " " + newDate.timeNow() + " &nbsp; &nbsp; " + newDate.today();
	$('.profiler-off-time').html(datetime);		
								
	$('.profiler-off-time').prev().html("Monitor On Time:");

	<!--var categoriesJson = '{"clusterWide":{"jobTracker":["Hostname","jobs_running","map_slots","reduce_slots","occupied_map_slots","occupied_reduce_slots","memHeapUsedM","gcCount","gcTimeMillis","trackers","trackers_graylisted","trackers_blacklisted"], "nameNode":["HostName","snapshot_avg_time","BlockCapacity","CapacityRemainingGB","CapacityTotalGB","CapacityUsedGB","PercentUsed","Safemode","SafemodeTime","TotalBlocks","UnderReplicatedBlocks","CorruptBlocks","MissingBlocks","gcCount","gcTimeMillis","TotalFiles"]},"hadoopJMX": {"dfs": ["DfsUsed", "StorageInfo", "Capacity", "Remaining"],"rpc":{"dataNode":["RpcPort","RpcProcessingTime_num_ops","RpcProcessingTime_avg_time","RpcQueueTime_num_ops","RpcQueueTime_avg_time","rpcAuthorizationSuccesses","rpcAuthorizationFailures","rpcAuthenticationSuccesses","rpcAuthenticationFailures"], "taskTracker":["RpcPort","RpcProcessingTime_num_ops","RpcProcessingTime_avg_time","RpcQueueTime_num_ops","RpcQueueTime_avg_time","rpcAuthorizationSuccesses","rpcAuthorizationFailures","rpcAuthenticationSuccesses","rpcAuthenticationFailures"]},"io": ["bytes_read", "reads_from_local_client", "writes_from_local_client", "writes_from_remote_client", "reads_from_remote_client", "bytes_written", "blocks_read", "blocks_written", "blocks_verified", "blocks_removed", "blocks_replicated"],"dataNodeMisc":["ReceivedBytes", "block_verification_failures", "gcCount", "blockChecksumOp_num_ops", "snapshot_num_ops", "publish_stdev_time", "copyBlockOp_avg_time", "memNonHeapCommittedM", "loginFailure_num_ops", "tag.processName", "gcTimeMillis", "memNonHeapUsedM", "snapshot_imax_time", "blockReports_avg_time", "VolumeInfo", "writeBlockOp_num_ops", "tag.context", "copyBlockOp_num_ops", "Version", "publish_imax_time", "loginFailure_avg_time", "snapshot_avg_time", "replaceBlockOp_avg_time", "dropped_pub_all", "threadsBlocked", "logWarn", "HostName", "tag.port", "publish_imin_time", "snapshot_min_time", "callQueueLen", "publish_avg_time", "NamenodeAddress", "memHeapCommittedM", "readBlockOp_num_ops", "tag.hostName", "logFatal", "writeBlockOp_avg_time", "threadsWaiting", "logError", "publish_num_ops", "heartBeats_num_ops", "num_sinks", "NumOpenConnections", "replaceBlockOp_num_ops", "logInfo", "publish_min_time", "heartBeats_avg_time", "threadsTimedWaiting", "blockReports_num_ops", "num_sources", "publish_max_time", "loginSuccess_avg_time", "snapshot_stdev_time", "readBlockOp_avg_time", "loginSuccess_num_ops", "SentBytes", "snapshot_imin_time", "blocks_get_local_pathinfo", "threadsNew", "memHeapUsedM", "threadsTerminated", "blockChecksumOp_avg_time", "snapshot_max_time", "threadsRunnable"],"ttMisc":["shuffle_success_outputs", "ReceivedBytes", "tasks_failed_timeout", "done_avg_time", "getMapCompletionEvents_avg_time", "getMapCompletionEvents_num_ops", "shuffle_output_bytes", "gcCount", "snapshot_num_ops", "publish_stdev_time", "memNonHeapCommittedM", "loginFailure_num_ops", "statusUpdate_avg_time", "tag.processName", "gcTimeMillis", "memNonHeapUsedM", "snapshot_imax_time", "tag.context", "publish_imax_time", "Version", "loginFailure_avg_time", "snapshot_avg_time", "shuffle_handler_busy_percent", "dropped_pub_all", "canCommit_num_ops", "HttpPort", "threadsBlocked", "logWarn", "JobTrackerUrl", "commitPending_avg_time", "tag.port", "getTask_num_ops", "publish_imin_time", "reduces_running", "snapshot_min_time", "callQueueLen", "reduceTaskSlots", "publish_avg_time", "tasks_completed", "getProtocolVersion_num_ops", "commitPending_num_ops", "shuffle_exceptions_caught", "memHeapCommittedM", "tag.hostName", "ping_avg_time", "done_num_ops", "logFatal", "threadsWaiting", "logError", "publish_num_ops", "Healthy", "tag.sessionId", "statusUpdate_num_ops", "num_sinks", "NumOpenConnections", "logInfo", "publish_min_time", "TasksInfoJson", "threadsTimedWaiting", "shuffle_failed_outputs", "Hostname", "getTask_avg_time", "getProtocolVersion_avg_time", "num_sources", "ConfigVersion", "publish_max_time", "mapTaskSlots", "ping_num_ops", "loginSuccess_avg_time", "snapshot_stdev_time", "tasks_failed_ping", "loginSuccess_num_ops", "SentBytes", "maps_running", "snapshot_imin_time", "threadsNew", "memHeapUsedM", "threadsTerminated", "snapshot_max_time", "threadsRunnable", "canCommit_avg_time"],"nameNodeMisc":["Threads", "register_num_ops", "versionRequest_num_ops", "rollFsImage_num_ops", "JournalTransactionsBatchedInSync", "snapshot_num_ops", "Free", "memNonHeapCommittedM", "FilesTotal", "snapshot_imax_time", "CapacityRemaining", "TotalLoad", "tag.context", "DeleteFileOps", "publish_imax_time", "loginFailure_avg_time", "getBlockLocations_num_ops", "getListing_avg_time", "RpcProcessingTime_num_ops", "logWarn", "getFileInfo_num_ops", "register_avg_time", "publish_imin_time", "versionRequest_avg_time", "snapshot_min_time", "Used", "RpcProcessingTime_avg_time", "tag.hostName", "PercentRemaining", "logFatal", "renewLease_avg_time", "RpcQueueTime_num_ops", "sendHeartbeat_num_ops", "ExcessBlocks", "rename_num_ops", "rpcAuthenticationFailures", "GetListingOps", "GetBlockLocations", "delete_num_ops", "blockReport_num_ops", "create_num_ops", "Transactions_avg_time", "publish_min_time", "getEditLogSize_num_ops", "threadsTimedWaiting", "PendingDeletionBlocks", "rollFsImage_avg_time", "delete_avg_time", "ScheduledReplicationBlocks", "getFileInfo_avg_time", "rename_avg_time", "publish_max_time", "rollEditLog_avg_time", "blockReceived_avg_time", "getEditLogSize_avg_time", "RpcQueueTime_avg_time", "loginSuccess_num_ops", "SentBytes", "FSState", "snapshot_imin_time", "memHeapUsedM", "threadsNew", "threadsTerminated", "FilesDeleted", "snapshot_max_time", "Total", "addBlock_avg_time", "ReceivedBytes", "UpgradeFinalized", "create_avg_time", "publish_stdev_time", "loginFailure_num_ops", "PendingReplicationBlocks", "tag.processName", "memNonHeapUsedM", "FilesInGetListingOps", "FilesCreated", "Version", "BlocksTotal", "addBlock_num_ops", "fsImageLoadTime", "dropped_pub_all", "FilesRenamed", "threadsBlocked", "FileInfoOps", "NonDfsUsedSpace", "getBlockLocations_avg_time", "renewLease_num_ops", "Syncs_avg_time", "tag.port", "setPermission_num_ops", "complete_num_ops", "callQueueLen", "publish_avg_time", "getProtocolVersion_num_ops", "CapacityTotal", "FilesAppended", "setReplication_num_ops", "blockReport_avg_time", "setPermission_avg_time", "DeadNodes", "rpcAuthorizationSuccesses", "memHeapCommittedM", "CapacityUsed", "getListing_num_ops", "threadsWaiting", "publish_num_ops", "logError", "CreateFileOps", "Transactions_num_ops", "num_sinks", "mkdirs_avg_time", "NameDirStatuses", "DecomNodes", "NumOpenConnections", "logInfo", "rpcAuthenticationSuccesses", "rollEditLog_num_ops", "mkdirs_num_ops", "AddBlockOps", "rpcAuthorizationFailures", "getProtocolVersion_avg_time", "LiveNodes", "num_sources", "loginSuccess_avg_time", "snapshot_stdev_time", "sendHeartbeat_avg_time", "Syncs_num_ops", "complete_avg_time", "blockReceived_num_ops", "threadsRunnable", "setReplication_avg_time"],"jobTrackerMisc":["getJobProfile_num_ops", "blacklisted_maps", "running_maps", "jobs_failed", "snapshot_num_ops", "jobs_submitted", "memNonHeapCommittedM", "heartbeats", "snapshot_imax_time", "running_300", "GraylistedNodesInfoJson", "tag.context", "running_60", "publish_imax_time", "loginFailure_avg_time", "snapshot_avg_time", "SummaryJson", "getBuildVersion_num_ops", "RpcProcessingTime_num_ops", "logWarn", "running_1440", "jobs_preparing", "publish_imin_time", "snapshot_min_time", "RpcProcessingTime_avg_time", "heartbeat_avg_time", "tag.hostName", "logFatal", "getQueueAdmins_num_ops", "RpcQueueTime_num_ops", "waiting_maps", "rpcAuthenticationFailures", "tag.sessionId", "reserved_reduce_slots", "getNewJobId_num_ops", "publish_min_time", "getJobStatus_num_ops", "threadsTimedWaiting", "ConfigVersion", "publish_max_time", "jobs_completed", "reserved_map_slots", "RpcQueueTime_avg_time", "loginSuccess_num_ops", "getTaskCompletionEvents_num_ops", "SentBytes", "snapshot_imin_time", "running_reduces", "AliveNodesInfoJson", "threadsNew", "threadsTerminated", "snapshot_max_time", "getJobStatus_avg_time", "getStagingAreaDir_num_ops", "ReceivedBytes", "BlacklistedNodesInfoJson", "maps_completed", "publish_stdev_time", "loginFailure_num_ops", "tag.processName", "memNonHeapUsedM", "getJobCounters_avg_time", "getQueueAdmins_avg_time", "heartbeat_num_ops", "Version", "maps_failed", "QueueInfoJson", "dropped_pub_all", "getNewJobId_avg_time", "reduces_failed", "running_0", "trackers_decommissioned", "threadsBlocked", "tag.port", "getJobProfile_avg_time", "callQueueLen", "publish_avg_time", "getProtocolVersion_num_ops", "rpcAuthorizationSuccesses", "memHeapCommittedM", "threadsWaiting", "publish_num_ops", "logError", "num_sinks", "NumOpenConnections", "logInfo", "blacklisted_reduces", "getJobCounters_num_ops", "getTaskCompletionEvents_avg_time", "submitJob_avg_time", "rpcAuthenticationSuccesses", "maps_launched", "reduces_launched", "jobs_killed", "rpcAuthorizationFailures", "maps_killed", "getBuildVersion_avg_time", "getProtocolVersion_avg_time", "num_sources", "reduces_killed", "reduces_completed", "loginSuccess_avg_time", "getSystemDir_num_ops", "snapshot_stdev_time", "tag.Queue", "submitJob_num_ops", "getStagingAreaDir_avg_time", "getSystemDir_avg_time", "ThreadCount", "waiting_reduces", "threadsRunnable"]},"systemStats":{"cpu":["numberOfCores", "threadsPerCore", "cpuUsage"], "memory":["activememory", "swapcache", "inactivememory", "buffermemory", "usedmemory", "freeswap", "usedswap", "totalmemory", "swapcache", "totalswap", "freememory"], "os":["contextswitches", "interrupts", "forks", "time", "pagedout", "usercputicks", "cputicks", "pagedin", "swappedin", "swappedout"]}}';-->
	var categoriesJson=$('#var_categoriesJson').val();
	//	alert("categories json"+categoriesJson);
	var defaultCategories = [ "freememory", "usedmemory", "DfsUsed" ];
	var defaultCategories = ["freememory", "totalmemory" ];
	var defaultTrendsCategories = [ "cpuUsage" ];

	var TotalCount = 0;
	var TotalRackCount = 0;
	var TotalInnerRackCount = 0;
	var TotalNodeCount = 0;
	var nodeClass = '';
	trendArr = [];
	var saveColorFlag = false;
	var nameNodeIp = '';
	var AjaxCallStopFirst=false;
	var AjaxCallStopSecond=false;
	setDisableButtonInitialValue();
	
	function replaceAll(find, replace, str) {
		  return str.split(find).join(replace);
	}
	
	function setDisableButtonInitialValue() {

		var disableProfilerValue = getCookie('DisableProfiler');
		setCookie('DisableProfiler', 'FALSE', 365);

	}
	$('[placeholder]').focus(function() {
		var input = $(this);
		if (input.val() == input.attr('placeholder')) {
			input.val('');
			input.removeClass('placeholder');
		}
	}).blur(function() {
		var input = $(this);
		if (input.val() == '' || input.val() == input.attr('placeholder')) {
			input.addClass('placeholder');
			input.val(input.attr('placeholder'));
		}
	}).blur();
	$('.toggleDisable').each(
			function() {
				$(this).toggles(
						{
							clickable : !$(this).hasClass('noclick'),
							dragable : false,
							click : ($(this).attr('rel')) ? $('.'
									+ $(this).attr('rel')) : undefined,
							on : !$(this).hasClass('off'),
							checkbox : ($(this).data('checkbox')) ? $('.'
									+ $(this).data('checkbox')) : undefined,
							ontext : $(this).data('ontext') || 'ON',
							offtext : $(this).data('offtext') || 'OFF'
						});
			});
	//profilingClusterTabData(profileClusterJson);
	categoriesTabData(categoriesJson);

	//Main function to represent dc, rack & nodes for data center tab
	function profilingClusterTabData(profileClusterJson) {
		var dataCenterHTML = '';
		profileParsedJson = $.parseJSON(profileClusterJson);

		$
				.each(
						profileParsedJson['dataCenters'],
						function(profileJsonKey, profileJsonVal) {
							TotalCount++;
							TotalRackCount = 0;
							dataCenterHTML += '<li>';
							$
									.each(
											profileJsonVal,
											function(profileDataCenterJsonKey,
													profileDataCenterJsonVal) {

												if (profileDataCenterJsonKey == 'clusterId') {
													dataCenterHTML += '<a class="toggle" href="javascript:void(0);">'
															+ profileDataCenterJsonVal
															+ '</a>';
												}
												if (profileDataCenterJsonKey == 'racks') {
													$
															.each(
																	profileDataCenterJsonVal,
																	function(
																			profileJsonInnerKey,
																			profileJsonInnerVal) {
																		TotalRackCount++;
																		TotalInnerRackCount++;
																		dataCenterHTML += '<div class="dataCenterMainBox fleft" style="clear:both;"><div class="rackBox"><fieldset><legend>'
																				+ profileJsonInnerVal.rackId
																				+ '</legend><div>';
																		$
																				.each(
																						profileJsonInnerVal,
																						function(
																								profileJsonRackInnerKey,
																								profileJsonRackInnerVal) {
																							if (profileJsonRackInnerKey == 'nodes') {
																								$
																										.each(
																												profileJsonInnerVal['nodes'],
																												function(
																														profileJsonNodeListInnerKey,
																														profileJsonNodeListInnerVal) {
																													TotalNodeCount++;
																													if (typeof profileJsonNodeListInnerVal['performance'] != 'undefined') {
																														if (profileJsonNodeListInnerVal['performance'] == 'Good') {

																															dataCenterHTML += '<div class="nodeBox green"><span>'
																																	+ profileJsonNodeListInnerVal['nodeIp']
																																	+ '</span></div>';
																														} else if (profileJsonNodeListInnerVal['performance'] == 'Average') {
																															dataCenterHTML += '<div class="nodeBox orange"><span>'
																																	+ profileJsonNodeListInnerVal['nodeIp']
																																	+ '</span></div>';
																														} else if (profileJsonNodeListInnerVal['performance'] == 'Unavailable') {
																															dataCenterHTML += '<div class="nodeBoxWithoutClick gray"><input type="hidden" value="'+profileJsonNodeListInnerVal['nodeIp']+': '+ profileJsonNodeListInnerVal['message'] +'" /><span>'
																															+ profileJsonNodeListInnerVal['nodeIp']
																															+ ': '
																															+ profileJsonNodeListInnerVal['message']
																															+ '</span></div>';
																														} else {
																															dataCenterHTML += '<div class="nodeBox"><span>'
																																	+ profileJsonNodeListInnerVal['nodeIp']
																																	+ '</span></div>';
																														}
																													} else {
																														dataCenterHTML += '<div class="nodeBox"><span>'
																																+ profileJsonNodeListInnerVal['nodeIp']
																																+ '</span></div>';
																													}
																												});
																							}
																						});
																		dataCenterHTML += '</div></fieldset></div></div>';
																	});
												}
											});

							dataCenterHTML += '</li>';

							$('ul#dataCenterList').html(dataCenterHTML);
						});

		if (profileParsedJson['nameNodeIP']) {
			nameNodeIp = profileParsedJson['nameNodeIP'];
			nodeDivClick(nameNodeIp);
			nameNodeIp = '';
		}
	}

	//General setting categories function for categories tab
	function categoriesTabData(categoriesJson) {
		var catHTML = '';
		//alert('categories json'+categoriesJson);	
		catParsedJson = $.parseJSON(categoriesJson);
		$
				.each(
						catParsedJson,
						function(catJsonKey, catJsonVal) {
							catHTML += '<li><a class="toggle" href="javascript:void(0);">'
									+ catJsonKey
									+ '</a><div class="catMainBox fleft"><ul class="subAccordianMainlist">';
							$
									.each(
											catJsonVal,
											function(catArrJsonKey,
													catArrJsonVal) {
												if (typeof catArrJsonVal == 'object') {
													catHTML += '<li class="subAccordianInnerlist"><a class="toggle" href="javascript:void(0);">'
															+ catArrJsonKey
															+ '</a><div class="catMainBox fleft"><ul class="accordianList">';
													$
															.each(
																	catArrJsonVal,
																	function(
																			catArrInnerJsonKey,
																			catArrInnerJsonVal) {

																		if (typeof catArrInnerJsonVal == 'object') {
																			catHTML += '<li class="subAccordianInnerlist"><a class="toggle" href="javascript:void(0);">'
																					+ catArrInnerJsonKey
																					+ '</a><div class="catMainBox fleft"><ul class="accordianList">';
																			$
																					.each(
																							catArrInnerJsonVal,
																							function(
																									catArrInner2JsonKey,
																									catArrInner2JsonVal) {
																								catHTML += '<li><span class="fleft">'
																										+ catArrInner2JsonVal
																										+ '</span><span class="fright"><input class="setting-ico" type="checkbox" name="favourities.'+catJsonKey+'.'+catArrJsonKey+'.'+catArrInnerJsonKey+'[]" value="'+catArrInner2JsonVal+'"><label class="fav-ico">'
																										+ catJsonKey
																										+ '.'
																										+ catArrJsonKey
																										+ '.'
																										+ catArrInnerJsonKey
																										+ '</label><input class="setting-ico" type="checkbox" name="trends.'+catJsonKey+'.'+catArrJsonKey+'.'+catArrInnerJsonKey+'[]" value="'+catArrInner2JsonVal+'"><label class="trend-ico">'
																										+ catJsonKey
																										+ '.'
																										+ catArrJsonKey
																										+ '.'
																										+ catArrInnerJsonKey
																										+ '</label><input class="setting-ico" type="checkbox" id="color.'+catJsonKey+'.'+catArrJsonKey+'.'+catArrInnerJsonKey+'[]" value="'+catArrInner2JsonVal+'"><label class="color-ico">'
																										+ catJsonKey
																										+ '.'
																										+ catArrJsonKey
																										+ '.'
																										+ catArrInnerJsonKey
																										+ '</label></span></li>';
																							});
																			catHTML += '</ul></div></li>';
																		} else {
																			catHTML += '<li><span class="fleft">'
																					+ catArrInnerJsonVal
																					+ '</span><span class="fright"><input class="setting-ico" type="checkbox" name="favourities.'+catJsonKey+'.'+catArrJsonKey+'[]" value="'+catArrInnerJsonVal+'"><label class="fav-ico">'
																					+ catJsonKey
																					+ '.'
																					+ catArrJsonKey
																					+ '</label><input class="setting-ico" type="checkbox" name="trends.'+catJsonKey+'.'+catArrJsonKey+'[]" value="'+catArrInnerJsonVal+'"><label class="trend-ico">'
																					+ catJsonKey
																					+ '.'
																					+ catArrJsonKey
																					+ '</label><input class="setting-ico" type="checkbox" id="color.'+catJsonKey+'.'+catArrJsonKey+'[]" value="'+catArrInnerJsonVal+'"><label class="color-ico">'
																					+ catJsonKey
																					+ '.'
																					+ catArrJsonKey
																					+ '</label></span></li>';
																		}
																	});
													catHTML += '</ul></div></li>';
												} else {
													catHTML += '<li><span class="fleft">'
															+ catArrJsonVal
															+ '</span><span class="fright"><input class="setting-ico" type="checkbox" name="favourities.'+catJsonKey+'[]" value="'+catArrJsonVal+'"><label class="fav-ico">'
															+ catJsonKey
															+ '</label><input class="setting-ico" type="checkbox" name="trends.'+catJsonKey+'[]" value="'+catArrJsonVal+'"><label class="trend-ico">'
															+ catJsonKey
															+ '</label><input class="setting-ico" type="checkbox" id="color.'+catJsonKey+'[]" value="'+catArrJsonVal+'"><label class="color-ico">'
															+ catJsonKey
															+ '</label></span></li>';
												}
											});
							catHTML += '</ul></div></li>';
							$('ul#categoriesList').html(catHTML);
						});

	}

	// General setting checkbox checked n unchecked and icon position change according to the selection.
	function selectSetting(obj) {
		element = obj.previousSibling;
		if (element.checked == true && element.type == "checkbox") {
			obj.style.backgroundPosition = "0 0";
			element.checked = false;
		} else {
			if (element.type == "checkbox") {
				obj.style.backgroundPosition = "0 -" + 6 * 2 + "px";
			}
			element.checked = true;
		}
	}

	//Prepare form data in json format using this function
	function formSubmit(formName) {

		var formData = form2js(formName, '.', true, function(node) {
			if (node.id && node.id.match(/callbackTest/)) {
				return {
					name : node.name,
					value : node.innerHTML
				};
			}
		});

		var finalJson = JSON.stringify(formData, null, '\t');
		console.log(finalJson);
		return finalJson;

	}

	//Prepare Color criteria setting box HTML according to the selected category
	function colorCriteriaHtml(obj) {
		element = obj.previousSibling;
		var catName = obj.innerHTML + "." + element.value;
		var setCount = $('#generalSettingDataBox').find('fieldset').length;
		var colorHtml = '';

		if (element.checked == true && element.type == "checkbox") {
			colorHtml = '<fieldset id="colorCriteria_'+element.value+'"><div style="position:relative;"><legend>'
					+ catName
					+ '</legend><div class="commonBox"><div class="paddLeftRight"><div class="nodeBox"><span></span></div> Bad</div> <div class="paddLeftRight"><select name="color['+setCount+'].bad.operator"><option value="LESS_THAN_OP"><</option><option value="LESS_THAN_EQUALTO_OP"><=</option><option value="EQUALT0_OP">=</option><option value="GREATER_THAN_OP">></option><option value="GREATER_THAN_EQUALTO_OP">>=</option></select></div> <div class="paddLeftRight"><input type="text" size="5" name="color['+setCount+'].bad.val" value="0" ></div></div><div class="commonBox"><div class="paddLeftRight"><div class="nodeBox green"><span></span></div> Good</div> <div class="paddLeftRight"><select name="color['+setCount+'].good.operator"><option value="LESS_THAN_OP"><</option><option value="LESS_THAN_EQUALTO_OP"><=</option><option value="EQUALT0_OP">=</option><option value="GREATER_THAN_OP" selected>></option><option value="GREATER_THAN_EQUALTO_OP">>=</option></select></div> <div class="paddLeftRight"><input type="text" size="5" name="color['+setCount+'].good.val" value="0" ><input type="hidden" name="color['+setCount+'].category" value="'+obj.innerHTML+'"><input type="hidden" name="color['+setCount+'].stat" value="'+element.value+'"></div></div><img rel="'+element.value+'" src="../skins/images/profiler_close.png" class="colorCloseImg"></div></fieldset>';
			$('#generalSettingDataBox').append(colorHtml);
		} else {
			$('#colorCriteria_' + element.value).remove();
		}
	}

	//Prepare trends chart html in trend widget according to the selected category
	function trendChartHtml(obj) {
		element = obj.previousSibling;
		var catName = element.value;
		var catNameWithUnderScore = replaceAll(".","_",catName);
		var colorHtml = '';

		if (element.checked == true && element.type == "checkbox") {
			$('#trendInnerBox')
					.append(
							'<div id="trend_'+catNameWithUnderScore+'" class="commonBox" style="position:relative;"><div class="fleft" ><span class="catTxt">'
									+ obj.innerHTML
									+ '</span><br><span class="statTxt">'
									+ catName
									+ '</span></div><div id="profilingChart_'+catNameWithUnderScore+'" class="fright" ></div><img class="trendCloseImg" src="../skins/images/profiler_close.png" rel="'+catName+'"></div>');
			trendArr[catName] = [];
		} else {
			delete trendArr[catName];
			$('#trend_' + catName).remove();
		}

		trendChart(catName);
	}

	//Prepare trends chart graph in trend widget according to the selected category
	function trendChart(catName) {
		$.jqplot.config.defaultHeight = 80;
		$.jqplot.config.defaultWidth = 450;
		catName = replaceAll(".","_",catName);
		$('#profilingChart_' + catName).html('');

		var plot = $.jqplot('profilingChart_' + catName, [ [ 1 ] ], {
			axesDefaults : {
				labelRenderer : $.jqplot.CanvasAxisLabelRenderer
			},
			seriesDefaults : {
				rendererOptions : {
					smooth : true
				},
				axes : {
					xaxis : {
						labelRenderer : $.jqplot.CanvasAxisLabelRenderer
					},
					yaxis : {
						labelRenderer : $.jqplot.CanvasAxisLabelRenderer
					}
				}
			}
		});

	}

	//Render default favroties & trends html
	function defaultSelectedCategories() {
		// default selected fav.
		if (defaultCategories.length > 0) {
			for (i = 0; i < defaultCategories.length; i++) {
				var inptuElement = $('#categoriesContainer').find(
						'input[value="' + defaultCategories[i] + '"]');
				selectSetting(inptuElement[0].nextSibling);
				element = inptuElement[0];
				var elementId = replaceAll(".","_",element.value);	
				if (element.checked == true) {
					$('#favInnerBox')
							.append(
									'<div id="'+elementId+'" class="favBox"><div class="favLeft fleft" ><span class="catTxt">'
											+ inptuElement[0].nextSibling.innerHTML
											+ '</span><br><span class="statTxt">'
											+ element.value
											+ '</span></div><div class="favRight" >-</div><img rel="'+element.value+'" src="../skins/images/profiler_close.png" class="favCloseImg"></div>');
				} else {
					$('#' + element.value).remove();
				}
			}
		}

		// default selected trends
		if (defaultTrendsCategories.length > 0) {
			for (i = 0; i < defaultTrendsCategories.length; i++) {
				var catName = defaultTrendsCategories[i];
				var catIDName = replaceAll(".","_",catName);
				var inptuElement = $('#categoriesContainer').find(
						'input[value="' + catName + '"]');
				selectSetting(inptuElement[1].nextSibling);
				element = inptuElement[1];

				if (element.checked == true && element.type == "checkbox") {
					$('#trendInnerBox')
							.append(
									'<div id="trend_'+catIDName+'" class="commonBox" style="position:relative;"><div class="fleft" ><span class="catTxt">'
											+ inptuElement[0].nextSibling.innerHTML
											+ '</span><br><span class="statTxt">'
											+ catName
											+ '</span></div><div id="profilingChart_'+catIDName+'" class="fright" ></div><img class="trendCloseImg" src="../skins/images/profiler_close.png" rel="'+catName+'"></div>');
					trendArr[catName] = [];

				} else {
					delete trendArr[catName];
					$('#trend_' + catName).remove();
				}

				trendChart(catName);

			}
		}
	}

	//dc/rack/node common render function
	function dataLoadTabData(profileClusterJson, boxID) {
		var dataCenterHTML = '';
		profileParsedJson = $.parseJSON(profileClusterJson);
		$
				.each(
						profileParsedJson['dataCenters'],
						function(profileJsonKey, profileJsonVal) {
							TotalCount++;
							TotalRackCount = 0;
							dataCenterHTML += '<li>';
							$
									.each(
											profileJsonVal,
											function(profileDataCenterJsonKey,
													profileDataCenterJsonVal) {

												if (profileDataCenterJsonKey == 'clusterId') {
													dataCenterHTML += '<a class="toggle" href="javascript:void(0);">'
															+ profileDataCenterJsonVal
															+ '</a>';
												}
												if (profileDataCenterJsonKey == 'racks') {
													$
															.each(
																	profileDataCenterJsonVal,
																	function(
																			profileJsonInnerKey,
																			profileJsonInnerVal) {
																		TotalRackCount++;
																		TotalInnerRackCount++;
																		dataCenterHTML += '<div class="dataCenterMainBox fleft" style="clear:both;"><div class="rackBox"><fieldset><legend>'
																				+ profileJsonInnerVal.rackId
																				+ '</legend><div>';
																		$
																				.each(
																						profileJsonInnerVal,
																						function(
																								profileJsonRackInnerKey,
																								profileJsonRackInnerVal) {
																							if (profileJsonRackInnerKey == 'nodes') {
																								$
																										.each(
																												profileJsonInnerVal['nodes'],
																												function(
																														profileJsonNodeListInnerKey,
																														profileJsonNodeListInnerVal) {
																													TotalNodeCount++;
																													if (typeof profileJsonNodeListInnerVal['performance'] != 'undefined') {
																														if (profileJsonNodeListInnerVal['performance'] == 'Good') {

																															dataCenterHTML += '<div class="nodeBoxWithoutClick green"><span>'
																																	+ profileJsonNodeListInnerVal['nodeIp']
																																	+ ' : <b>'
																																	+ profileJsonNodeListInnerVal['dataLoadStats']
																																	+ '%</b></span></div>';
																														} else if (profileJsonNodeListInnerVal['performance'] == 'Average') {
																															dataCenterHTML += '<div class="nodeBoxWithoutClick orange"><span>'
																																	+ profileJsonNodeListInnerVal['nodeIp']
																																	+ ' : <b>'
																																	+ profileJsonNodeListInnerVal['dataLoadStats']
																																	+ '%</b></span></div>';
																														} else if (profileJsonNodeListInnerVal['performance'] == 'Unavailable') {
																															dataCenterHTML += '<div class="nodeBoxWithoutClick gray"><span>'
																																	+ profileJsonNodeListInnerVal['nodeIp']
																																	+ ' : <b>'
																																	+ profileJsonNodeListInnerVal['message']
																																	+ '</b></span></div>';
																														} else {
																															dataCenterHTML += '<div class="nodeBoxWithoutClick"><span>'
																																	+ profileJsonNodeListInnerVal['nodeIp']
																																	+ ' : <b>'
																																	+ profileJsonNodeListInnerVal['dataLoadStats']
																																	+ '%</b></span></div>';
																														}
																													} else {
																														dataCenterHTML += '<div class="nodeBoxWithoutClick"><span>'
																																+ profileJsonNodeListInnerVal['nodeIp']
																																+ ' : <b>'
																																+ profileJsonNodeListInnerVal['dataLoadStats']
																																+ '%</b></span></div>';
																													}
																												});
																							}
																						});
																		dataCenterHTML += '</div></fieldset></div></div>';
																	});
												}
											});

							dataCenterHTML += '</li>';

							$(boxID).html(dataCenterHTML);
						});
	}

	//Prepare dc, rack & node html for network latency tab.
	function networkLatencyTabData(profileClusterJson, boxId) {
		var dataCenterHTML = '';
		var nodeDetails = '';
		profileParsedJson = $.parseJSON(profileClusterJson);
		$
				.each(
						profileParsedJson['dataCenters'],
						function(profileJsonKey, profileJsonVal) {
							TotalCount++;
							TotalRackCount = 0;
							dataCenterHTML += '<li>';
							$
									.each(
											profileJsonVal,
											function(profileDataCenterJsonKey,
													profileDataCenterJsonVal) {

												if (profileDataCenterJsonKey == 'clusterId') {
													dataCenterHTML += '<a class="toggle" href="javascript:void(0);">'
															+ profileDataCenterJsonVal
															+ '</a>';
												}
												if (profileDataCenterJsonKey == 'racks') {
													$
															.each(
																	profileDataCenterJsonVal,
																	function(
																			profileJsonInnerKey,
																			profileJsonInnerVal) {
																		TotalRackCount++;
																		TotalInnerRackCount++;
																		dataCenterHTML += '<div class="dataCenterMainBox fleft" style="clear:both;"><div class="rackBox"><fieldset><legend>'
																				+ profileJsonInnerVal.rackId
																				+ '</legend><div>';
																		$
																				.each(
																						profileJsonInnerVal,
																						function(
																								profileJsonRackInnerKey,
																								profileJsonRackInnerVal) {
																							if (profileJsonRackInnerKey == 'nodes') {
																								$
																										.each(
																												profileJsonInnerVal['nodes'],
																												function(
																														profileJsonNodeListInnerKey,
																														profileJsonNodeListInnerVal) {
																													TotalNodeCount++;
																													if (typeof profileJsonNodeListInnerVal['performance'] != 'undefined') {
																														if (typeof profileJsonNodeListInnerVal['performance'] != 'undefined') {
																															if (profileJsonNodeListInnerVal['performance'] == 'Good') {
																																dataCenterHTML += '<div class="nodeBoxWithoutClick green"><span><input type="checkbox" name="NODE_LIST[]" value="'+profileJsonNodeListInnerVal['nodeIp']+'" /></span></div>';
																															} else if (profileJsonNodeListInnerVal['performance'] == 'Average') {
																																dataCenterHTML += '<div class="nodeBoxWithoutClick orange"><span><input type="checkbox" name="NODE_LIST[]" value="'+profileJsonNodeListInnerVal['nodeIp']+'" /></span></div>';
																															} else if (profileJsonNodeListInnerVal['performance'] == 'Unavailable') {
																																dataCenterHTML += '<div class="nodeBoxWithoutClick gray"><span><input type="checkbox" name="NODE_LIST[]" value="'+profileJsonNodeListInnerVal['nodeIp']+': '+profileJsonNodeListInnerVal['message']+'" disabled/></span></div>';
																															} else {
																																dataCenterHTML += '<div class="nodeBoxWithoutClick"><span><input type="checkbox" name="NODE_LIST[]" value="'+profileJsonNodeListInnerVal['nodeIp']+'" /></span></div>';
																															}
																														}
																													} else {
																														dataCenterHTML += '<div class="nodeBoxWithoutClick"><span><input type="checkbox" name="NODE_LIST[]" value="'+profileJsonNodeListInnerVal['nodeIp']+'" /></span></div>';
																													}
																												});
																							}
																						});
																		dataCenterHTML += '</div></fieldset></div></div>';
																	});
												}
											});

							dataCenterHTML += '</li>';

							$(boxId).html(dataCenterHTML);
						});

	}

	//dc/rack/node common render function
	function hdfsDataDistributionTabData(profileClusterJson, boxID) {
		var dataCenterHTML = '';
		var distributedDataInfoHtml = "<ul>";
		profileParsedJson = $.parseJSON(profileClusterJson);

		if(profileParsedJson['distributedDataInfo'])	
		{
			$.each(profileParsedJson['distributedDataInfo'], function(distributedDataKey, distributedDataVal){
			if(distributedDataKey!="suggestionList"){
			distributedDataInfoHtml += "<li><span class='clabel'>"+distributedDataKey+"</span><span class='cval'>"+distributedDataVal+"</span></li>";
			}else{
					$("#freeow").freeow("Suggestion", distributedDataVal, {
 					classes: ["smokey"],
 					autoHide: true
					});
			}
				
		});
		distributedDataInfoHtml += "</ul>";
		$('#hdfsDataInformation').html(distributedDataInfoHtml);
	}

		$
				.each(
						profileParsedJson['dataCenters'],
						function(profileJsonKey, profileJsonVal) {
							TotalCount++;
							TotalRackCount = 0;
							dataCenterHTML += '<li>';
							$
									.each(
											profileJsonVal,
											function(profileDataCenterJsonKey,
													profileDataCenterJsonVal) {

												if (profileDataCenterJsonKey == 'clusterId') {
													dataCenterHTML += '<a class="toggle" href="javascript:void(0);">'
															+ profileDataCenterJsonVal
															+ '</a>';
												}
												if (profileDataCenterJsonKey == 'racks') {
													$
															.each(
																	profileDataCenterJsonVal,
																	function(
																			profileJsonInnerKey,
																			profileJsonInnerVal) {
																		TotalRackCount++;
																		TotalInnerRackCount++;
																		dataCenterHTML += '<div class="dataCenterMainBox fleft" style="clear:both;"><div class="rackBox"><fieldset><legend>'
																				+ profileJsonInnerVal.rackId
																				+ '</legend><div>';
																		$
																				.each(
																						profileJsonInnerVal,
																						function(
																								profileJsonRackInnerKey,
																								profileJsonRackInnerVal) {
																							if (profileJsonRackInnerKey == 'nodes') {
																								$
																										.each(
																												profileJsonInnerVal['nodes'],
																												function(
																														profileJsonNodeListInnerKey,
																														profileJsonNodeListInnerVal) {
																													TotalNodeCount++;
																													if (typeof profileJsonNodeListInnerVal['performance'] != 'undefined') {
																														if (profileJsonNodeListInnerVal['performance'] == 'Good') {

																															dataCenterHTML += '<div class="hdfsNodeBox green"><span>'
																																	+ profileJsonNodeListInnerVal['nodeIp']
																																	+ '</span></div>';
																														} else if (profileJsonNodeListInnerVal['performance'] == 'Average') {
																															dataCenterHTML += '<div class="hdfsNodeBox orange"><span>'
																																	+ profileJsonNodeListInnerVal['nodeIp']
																																	+ '</span></div>';
																														} else if (profileJsonNodeListInnerVal['performance'] == 'Unavailable') {
																															dataCenterHTML += '<div class="nodeBoxWithoutClick gray"><span>'
																																	+ profileJsonNodeListInnerVal['nodeIp']
																																	+ '</span></div>';
																														} else {
																															dataCenterHTML += '<div class="hdfsNodeBox"><span>'
																																	+ profileJsonNodeListInnerVal['nodeIp']
																																	+ '</span></div>';
																														}
																													} else {
																														dataCenterHTML += '<div class="hdfsNodeBox"><span>'
																																+ profileJsonNodeListInnerVal['nodeIp']
																																+ '</span></div>';
																													}
																												});
																							}
																						});
																		dataCenterHTML += '</div></fieldset></div></div>';
																	});
												}
											});

							dataCenterHTML += '</li>';

							$(boxID).html(dataCenterHTML);
						});
	}

	// Call after node click with node IP
	function nodeDivClick(nodeIp) {
		$('.nodeIpCls').html(nodeIp);
		for (key in trendArr) {
			trendArr[key] = [];
		}
		var disableProfiler = getCookie('DisableProfiler');
		setCookie('lastUseNodeIP', nodeIp, 365);
		if (disableProfiler == 'TRUE') {
			return;
		}

		clearInterval(nodeTimerId);
		nodeTimerId = setInterval(function() {
			nodeDetailsViewAjax(nodeIp)
		}, ajaxInterval);
	}

	//Get node details with ajax according to the selected fav & trends
	function nodeDetailsViewAjax(nodeIp) {
		var profileJsonNodeListInnerVal = formSubmit('settingForm');
		setCookie('settingFormCookie', profileJsonNodeListInnerVal, 365);

		var params = 'VIEW_NAME=NODE_VIEW&nodeConfig='
				+ profileJsonNodeListInnerVal + '&nodeIp=' + nodeIp;
		if (saveColorFlag == true) {
			var generalSettingFormJson = formSubmit('generalSettingForm');
			params += '&colorConfig=' + generalSettingFormJson;
		}

		var nodeDetailsHtml = '';
		var linePlot;
		if(AjaxCallStopFirst==false){

		$
				.ajax(
						{
							type : "POST",
							url : "ProfilerServlet",
							data : params,
							error : function(xhr, ajaxOptions, thrownError) {
								$('.loaderMainBox')
										.html(
												'<div class="status info"><span>Information: </span>Connection to the server got lost.</div>');
								//		alert('Connection to the server got lost.');										
								//		clearInterval(nodeTimerId);	
								AjaxCallStopFirst=true;		
							}
						})
				.done(
						function(finalJSON) {
							var parsedJson = $.parseJSON(finalJSON);

							//favourities json parsing
							if (parsedJson['favourities']) {
								$
										.each(
												parsedJson['favourities'],
												function(favJsonKey, favJsonVal) {
													$
															.each(
																	favJsonVal,
																	function(
																			favInnerJsonkey,
																			favInnerJsonVal) {
																		if (typeof favInnerJsonVal == 'object') {
																			$
																					.each(
																							favInnerJsonVal,
																							function(
																									favInnerObjJsonkey,
																									favInnerObjJsonVal) {
																								if (typeof favInnerObjJsonVal == 'object') {
																									$
																											.each(
																													favInnerObjJsonVal,
																													function(
																															favInner2ObjJsonkey,
																															favInner2ObjJsonVal) {
																														$(
																																'#'
																																		+ replaceAll(".","_",favInner2ObjJsonkey))
																																.find(
																																		'.favRight')
																																.html(
																																		favInner2ObjJsonVal)
																																.effect(
																																		"highlight",
																																		{},
																																		2000);
																													});
																								} else {
																									$(
																											'#'
																													+ replaceAll(".","_",favInnerObjJsonkey))
																											.find(
																													'.favRight')
																											.html(
																													favInnerObjJsonVal)
																											.effect(
																													"highlight",
																													{},
																													2000);
																								}
																							});
																		} else {
																			$(
																					'#'
																							+ replaceAll(".","_",favInnerJsonkey))
																					.find(
																							'.favRight')
																					.html(
																							favInnerJsonVal)
																					.effect(
																							"highlight",
																							{},
																							2000);
																		}
																	});
												});
							}

							if (parsedJson['colorConfig']) {
								$
										.each(
												parsedJson['colorConfig'],
												function(colorJsonKey,
														colorJsonVal) {
													if (colorJsonVal == 'Bad') {
														$('#' + colorJsonKey)
																.css(
																		{
																			'color' : '#f00',
																			'font-weight' : 'bold',
																			'border-bottom' : 'solid 1px #f00'
																		});
													} else if (colorJsonVal == 'Good') {
														$('#' + colorJsonKey)
																.css(
																		{
																			'color' : 'green',
																			'font-weight' : 'bold',
																			'border-bottom' : 'solid 1px green'
																		});
													} else {
														$('#' + colorJsonKey)
																.css(
																		{
																			'color' : 'orange',
																			'font-weight' : 'bold',
																			'border-bottom' : 'solid 1px orange'
																		});
													}
												});
							}

							//trends json parsing
							if (parsedJson['trends']) {
								$
										.each(
												parsedJson['trends'],
												function(trendJsonKey,
														trendJsonVal) {
													$
															.each(
																	trendJsonVal,
																	function(
																			trendInnerJsonkey,
																			trendInnerJsonVal) {
																		if (typeof trendInnerJsonVal == 'object') {
																			$
																					.each(
																							trendInnerJsonVal,
																							function(
																									trendInnerObjJsonkey,
																									trendInnerObjJsonVal) {
																								trendInnerObjJsonkeyWithUnderScore = replaceAll(".","_",trendInnerObjJsonkey);
																								$(
																										"#profilingChart_"
																												+ trendInnerObjJsonkeyWithUnderScore)
																										.html(
																												'');

																								trendArr[trendInnerObjJsonkey]
																										.push(Number(trendInnerObjJsonVal));
																								if (trendArr[trendInnerObjJsonkey].length > 10) {
																									trendArr[trendInnerObjJsonkey]
																											.shift();
																								}
																								linePlot = $
																										.jqplot(
																												'profilingChart_'
																														+ trendInnerObjJsonkeyWithUnderScore,
																												[ trendArr[trendInnerObjJsonkey] ],
																												{
																													axesDefaults : {
																														labelRenderer : $.jqplot.CanvasAxisLabelRenderer
																													},
																													seriesDefaults : {
																														rendererOptions : {
																															smooth : true
																														}
																													},
																													axes : {
																														xaxis : {
																															labelRenderer : $.jqplot.CanvasAxisLabelRenderer
																														},
																														yaxis : {
																															labelRenderer : $.jqplot.CanvasAxisLabelRenderer
																														}
																													}
																												});
																								console
																										.log(trendArr[trendInnerObjJsonkey]);
																								customJQPlotTooltip(
																										'line',
																										"profilingChart_"
																												+ trendInnerObjJsonkeyWithUnderScore,
																										linePlot,
																										''); // chartHolder, var in which you store your jqPlot
																								$(
																										'#profilingChart_'
																												+ trendInnerObjJsonkeyWithUnderScore
																												+ ' .jqplot-yaxis-label')
																										.css(
																												{
																													left : '5px'
																												});
																							});
																		} else {trendInnerObjJsonkeyWithUnderScore = replaceAll(".","_",trendInnerJsonkey);
																			$(
																					"#profilingChart_"
																							+ trendInnerObjJsonkeyWithUnderScore)
																					.html(
																							'');

																			trendArr[trendInnerJsonkey]
																					.push(Number(trendInnerJsonVal));
																			if (trendArr[trendInnerJsonkey].length > 10) {
																				trendArr[trendInnerJsonkey]
																						.shift();
																			}
																			linePlot = $
																					.jqplot(
																							'profilingChart_'
																									+ trendInnerObjJsonkeyWithUnderScore,
																							[ trendArr[trendInnerJsonkey] ],
																							{
																								axesDefaults : {
																									labelRenderer : $.jqplot.CanvasAxisLabelRenderer
																								},
																								seriesDefaults : {
																									rendererOptions : {
																										smooth : true
																									}
																								},
																								axes : {
																									xaxis : {
																										labelRenderer : $.jqplot.CanvasAxisLabelRenderer
																									},
																									yaxis : {
																										labelRenderer : $.jqplot.CanvasAxisLabelRenderer
																									}
																								}
																							});
																			console
																					.log(trendArr[trendInnerJsonkey]);
																			customJQPlotTooltip(
																					'line',
																					"profilingChart_"
																							+ trendInnerObjJsonkeyWithUnderScore,
																					linePlot,
																					''); // chartHolder, var in which you store your jqPlot
																			$(
																					'#profilingChart_'
																							+ trendInnerObjJsonkeyWithUnderScore
																							+ ' .jqplot-yaxis-label')
																					.css(
																							{
																								left : '5px'
																							});
																		}
																	});
												});
							}

						});
		}

	}

	function customNodeDetailTooptip(obj, toolTipText) {
		var mouseX = obj.offset().left; //these are going to be how jquery knows where to put the div that will be our tooltip
		var mouseY = obj.offset().top - 40;

		var cssObj = {
			'position' : 'absolute',
			'left' : mouseX + 'px', //usually needs more offset here
			'top' : mouseY + 'px',
			'border-radius' : '5px',
			'box-shadow' : '0 0 4px 2px #666',
			'border' : 'solid 1px #333'
		};
		$('#chartpseudotooltip').html(
				"<div>" + toolTipText
						+ "</div><div class='toolTipBottomArrow'></div>")
				.show();
		$('#chartpseudotooltip').css(cssObj);
	}

	function getCookie(c_name) {
		var i, x, y, ARRcookies = document.cookie.split(";");
		for (i = 0; i < ARRcookies.length; i++) {
			x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
			y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
			x = x.replace(/^\s+|\s+$/g, "");
			if (x == c_name) {
				return unescape(y);
			}
		}
	}

	function setCookie(c_name, value, exdays) {
		var exdate = new Date();
		exdate.setDate(exdate.getDate() + exdays);
		var c_value = escape(value)
				+ ((exdays == null) ? "" : "; expires=" + exdate.toUTCString());
		document.cookie=c_name + "=" + c_value;
		}

	function checkCookie() {
		var settingFormCookie = getCookie("settingFormCookie");

		if (settingFormCookie != null && settingFormCookie != '') {
			defaultCategories = [];
			var parsedJson = $.parseJSON(settingFormCookie);

			//favourities json parsing
			if (parsedJson['favourities']) {
				$.each(parsedJson['favourities'], function(favJsonKey,
						favJsonVal) {
					$.each(favJsonVal, function(favInnerJsonkey,
							favInnerJsonVal) {
						if (typeof favInnerJsonVal == 'object') {
							$.each(favInnerJsonVal, function(
									favInnerObjJsonkey, favInnerObjJsonVal) {
								if (typeof favInnerObjJsonVal == 'object') {
									$.each(favInnerObjJsonVal, function(
											favInner2ObjJsonkey,
											favInner2ObjJsonVal) {
										defaultCategories
												.push(favInner2ObjJsonVal);
									});
								} else {
									defaultCategories.push(favInnerObjJsonVal);
								}

							});
						} else {
							defaultCategories.push(favInnerJsonVal);
						}
					});
				});
			}

			//trends json parsing
			if (parsedJson['trends']) {
				defaultTrendsCategories = [];
				$.each(parsedJson['trends'], function(trendJsonKey,
						trendJsonVal) {
					$.each(trendJsonVal, function(trendInnerJsonkey,
							trendInnerJsonVal) {
						if (typeof trendInnerJsonVal == 'object') {
							$.each(trendInnerJsonVal,
									function(trendInnerObjJsonkey,
											trendInnerObjJsonVal) {
										defaultTrendsCategories
												.push(trendInnerObjJsonVal);
										console.log(defaultTrendsCategories);
									});
						} else {
							defaultTrendsCategories.push(trendInnerJsonVal);
							console.log(defaultTrendsCategories);
						}
					});
				});
			}

		}

		var generalSettingFormCookie = getCookie("generalSettingFormCookie");
		if (generalSettingFormCookie != null && generalSettingFormCookie != '') {
			var colorHtml = '';
			var catName = '';
			var parsedJson = $.parseJSON(generalSettingFormCookie);
			console.log(parsedJson);
			var inptuElement = '';
			for (i = 0; i < parsedJson.color.length; i++) {
				catName = parsedJson.color[i].category + '.'
						+ parsedJson.color[i].stat;
				colorHtml = '<fieldset id="colorCriteria_'+parsedJson.color[i].stat+'"><div style="position:relative;"><legend>'
						+ catName
						+ '</legend><div class="commonBox"><div class="paddLeftRight"><div class="nodeBox"><span></span></div> Bad</div> <div class="paddLeftRight"><select name="color['+i+'].bad.operator"><option value="LESS_THAN_OP"><</option><option value="LESS_THAN_EQUALTO_OP"><=</option><option value="EQUALT0_OP">=</option><option value="GREATER_THAN_OP">></option><option value="GREATER_THAN_EQUALTO_OP">>=</option></select></div> <div class="paddLeftRight"><input type="text" size="5" name="color['+i+'].bad.val" value="'+parsedJson.color[i].bad.val+'"></div></div><div class="commonBox"><div class="paddLeftRight"><div class="nodeBox green"><span></span></div> Good</div> <div class="paddLeftRight"><select name="color['+i+'].good.operator"><option value="LESS_THAN_OP"><</option><option value="LESS_THAN_EQUALTO_OP"><=</option><option value="EQUALT0_OP">=</option><option value="GREATER_THAN_OP">></option><option value="GREATER_THAN_EQUALTO_OP">>=</option></select></div> <div class="paddLeftRight"><input type="text" size="5" name="color['+i+'].good.val" value="'+parsedJson.color[i].good.val+'"><input type="hidden" name="color['+i+'].category" value="'+parsedJson.color[i].category+'"><input type="hidden" name="color['+i+'].stat" value="'+parsedJson.color[i].stat+'"></div></div><img rel="'+parsedJson.color[i].stat+'" src="../skins/images/profiler_close.png" class="colorCloseImg"></div></fieldset>';
				inptuElement = $('#categoriesContainer').find(
						'input[value=' + parsedJson.color[i].stat + ']');
				selectSetting(inptuElement[2].nextSibling);
				$('#generalSettingDataBox').append(colorHtml);
				$(
						"#colorCriteria_" + parsedJson.color[i].stat
								+ " select[name='color[" + i
								+ "].bad.operator'] option[value='"
								+ parsedJson.color[i].bad.operator + "']")
						.attr("selected", true);
				$(
						"#colorCriteria_" + parsedJson.color[i].stat
								+ " select[name='color[" + i
								+ "].good.operator'] option[value='"
								+ parsedJson.color[i].good.operator + "']")
						.attr("selected", true);
			}

		}
	}

	$(document)
			.ready(
					function() {

						$('#dataCenterList').find('li div.dataCenterMainBox')
								.toggle('up');

						$('#dataCenterList li a.toggle').live(
								'click',
								function() {
									$(this).toggleClass('selected');
									$(this).next('div.dataCenterMainBox')
											.toggle('down');
								});

						$('#categoriesList').find('li div.catMainBox').hide();

						$('#categoriesList li a.toggle').live(
								'click',
								function() {
									$(this).toggleClass('selected');
									$(this).next('div.catMainBox').toggle(
											'down');
								});

						$('#networkNodeList').find('li div.dataCenterMainBox')
								.hide();

						$('#networkNodeList li a.toggle').live(
								'click',
								function() {
									$(this).toggleClass('selected');
									$(this).next('div.dataCenterMainBox')
											.toggle('down');
								});

						$('#dcTabLink').live(
								'click',
								function() {
									$('.tabSelected')
											.removeClass('tabSelected')
											.addClass('profilerTab');
									$(this).addClass('tabSelected');
									$('#hdfsDataBoxNodeInfo').hide();

									$('#dataCenterContainer').show();
									$('#categoriesContainer').hide();


									$('#dataLoadContainer').hide();
									$('#hdfsDataDistributionContainer').hide();

									$('#favWidgetBox').show();
									$('#trendWidgetBox').show();


									$('#hdfsDataBox').hide();
									$('#favWidgetBox').show();
									$('#trendWidgetBox').show();

								});

						$('#catTabLink').live(
								'click',
								function() {
									$('.tabSelected')
											.removeClass('tabSelected')
											.addClass('profilerTab');
									$(this).addClass('tabSelected');

									$('#categoriesContainer').show();
									$('#dataCenterContainer').hide();


									$('#dataLoadContainer').hide();
									$('#hdfsDataDistributionContainer').hide();
									$('#hdfsDataBoxNodeInfo').hide();
									$('#favWidgetBox').show();
									$('#trendWidgetBox').show();

											$('#hdfsDataBox').hide();
											$('#favWidgetBox').show();
											$('#trendWidgetBox').show();

										});

						$('#dataLoadTabLink')
								.live(
										'click',
										function() {
											$('.tabSelected').removeClass(
													'tabSelected').addClass(
													'profilerTab');
											$(this).addClass('tabSelected');
											$('#hdfsDataBoxNodeInfo').hide();
											$('#categoriesContainer').hide();
											$('#dataCenterContainer').hide();
											$('#hdfsDataBox').hide();
											$('#dataLoadContainer').show();
											$('#hdfsDataDistributionContainer')
													.hide();
											$('#favWidgetBox').show();
											$('#trendWidgetBox').show();
											$('#dataLoadContainer')
													.html(
															'<div class="txtCenter"><img src="./skins/images/loading.gif" width="300px"></div>');

											var ajaxReq = $
													.ajax(
															{
																type : "POST",
																url : "ProfilerServlet?VIEW_NAME=DATALOAD_VIEW"
															})
													.done(
															function(finalJSON) {
																if (finalJSON) {
																	$(
																			'#dataLoadContainer')
																			.html(
																					'<ul id="dataLoadNodeList" class="accordianList"></ul>');
																	callProfilerOnSuccess(finalJSON);

																}
															});

											$('#hdfsDataBox').hide();

										});

						$('#hdfsDataDistributionTabLink').live(
								'click',
								function() {
									$('.tabSelected')
											.removeClass('tabSelected')
											.addClass('profilerTab');
									$(this).addClass('tabSelected');
									$('#hdfsDataBoxNodeInfo').show();
									$('#categoriesContainer').hide();
									$('#dataCenterContainer').hide();
									$('#dataLoadContainer').hide();
									$('#hdfsDataDistributionContainer').show();
									$('#hdfsFieldBox').show();
									$('#hdfsDCBox').hide();
									$('#hdfsDataBox').show();
									$('#favWidgetBox').hide();
									$('#trendWidgetBox').hide();

								});

						$('#hdfsSubmitBtn')
								.live(
										'click',
										function() {
											$('#hdfsFieldBox').hide();
											$('#hdfsDCBox')
													.html(
															'<div class="txtCenter"><img src="./skins/images/loading.gif" width="300px"></div>')
													.show();
											var hdfsFileName = $(
													'#hdfsFileName').val();
											if (hdfsFileName == '') {
												alert('Please enter HDFS file name');
												return;
											}

											var ajaxReq = $
													.ajax(
															{
																type : "POST",
																url : "ProfilerServlet?VIEW_NAME=DATA_DISTRIBUTION_VIEW&HDFS_PATH="
																		+ hdfsFileName
															})
													.done(
															function(finalJSON) {
																if (finalJSON) {

																	$(
																			'#hdfsDCBox')
																			.html(
																					'<ul id="hdfsDataDistributionNodeList" class="accordianList"></ul>');
																	callProfilerOnSuccess(finalJSON);

																}
															});

										});

						$('.trend-ico').live('click', function() {
							selectSetting(this);
							trendChartHtml(this);
						});

						$('.color-ico')
								.live(
										'click',
										function() {

											//color icon select code
											selectSetting(this);
											colorCriteriaHtml(this);

											//auto select fav. code
											element = this.previousSibling;
											trendLabelObj = element.previousSibling;
											trendinElement = trendLabelObj.previousSibling;
											favLabelObj = trendinElement.previousSibling;
											favElement = favLabelObj.previousSibling;
											if (favElement.checked == false) {
												selectSetting(favLabelObj);
											}

											if (element.checked == true) {
												if ($('#' + element.value).length == 0) {
													$('#favInnerBox')
															.append(
																	'<div id="'+element.value+'" class="favBox"><div class="favLeft fleft" ><span class="catTxt">'
																			+ $(
																					this)
																					.text()
																			+ '</span><br><span class="statTxt">'
																			+ element.value
																			+ '</span></div><div class="favRight" >-</div><img rel="'+element.value+'" src="../skins/images/profiler_close.png" class="favCloseImg"></div>');
												}
											} else {
												selectSetting(favLabelObj);
												$('#' + element.value).remove();
											}

										});

						$('.fav-ico')
								.live(
										'click',
										function() {
											selectSetting(this);
											element = this.previousSibling;
											if (element.checked == true) {
												$('#favInnerBox')
														.append(
																'<div id="'+replaceAll(".","_",element.value)+'" class="favBox"><div class="favLeft fleft" ><span class="catTxt">'
																		+ $(
																				this)
																				.text()
																		+ '</span><br><span class="statTxt">'
																		+ element.value
																		+ '</span></div><div class="favRight" >-</div><img rel="'+element.value+'" src="../skins/images/profiler_close.png" class="favCloseImg"></div>');
											} else {
												$('#' + element.value).remove();
											}
											//formSubmit('settingForm');		 		
										});

						$('#genSetSaveBtn')
								.click(
										function() {
											$('#generalSettingBox').hide();
											clearInterval(profileTimerId);
											profileTimerId = setInterval(
													function() {
														var generalSettingFormJson = formSubmit('generalSettingForm');
														setCookie(
																'generalSettingFormCookie',
																generalSettingFormJson,
																365);
														callProfileServletForJSON(generalSettingFormJson);
													}, ajaxInterval);
											saveColorFlag = true;

										});

						$('#genSetCancelBtn').click(function() {
							$('#generalSettingBox').hide();
						});

						$('#genSetImg')
								.live(
										'click',
										function() {

											if ($('#generalSettingDataBox')
													.find('fieldset').length == 0) {
												alert('Please select category for color criteria');
												return false;
											}
											$('#generalSettingBox').show();

										});

						$('.favCloseImg')
								.live(
										'click',
										function(e) {

											var category = $(this).attr('rel');
											var inptuElement = $(
													'#categoriesContainer')
													.find(
															'input[value="'
																	+ category
																	+ '"]');
											selectSetting(inptuElement[0].nextSibling);
											//$('#' + inptuElement[0].value)
												//	.remove();
											$(e.target).parent().remove();

										});

						$('.trendCloseImg')
								.live(
										'click',
										function() {

											var category = $(this).attr('rel');
											var inptuElement = $(
													'#categoriesContainer')
													.find(
															'input[value="'
																	+ category
																	+ '"]');
											selectSetting(inptuElement[1].nextSibling);
											var categorySepratedWithUnderScore =replaceAll(".","_",category); 
											$('#trend_' + categorySepratedWithUnderScore).remove();

										});

						$('.colorCloseImg')
								.live(
										'click',
										function() {

											var category = $(this).attr('rel');
											var inptuElement = $(
													'#categoriesContainer')
													.find(
															'input[value='
																	+ category
																	+ ']');
											selectSetting(inptuElement[2].nextSibling);
											$('#colorCriteria_' + category)
													.remove();
											if ($('#generalSettingDataBox')
													.find('fieldset').length == 0) {
												$('#generalSettingBox').hide();
											}

										});

						$('.nodeBox').live('click', function() {
							var nodeIp = $(this).find('span').html();
							nodeDivClick(nodeIp);
						});

						$('.hdfsNodeBox')
								.live(
										'click',
										function() {
											var nodeIp = $(this).find('span')
													.html();
											var params = 'VIEW_NAME=DATA_DISTRIBUTION_VIEW&NODE_IP='
													+ nodeIp;

											$
													.ajax(
															{
																type : "POST",
																url : "ProfilerServlet",
																data : params,
																error : function(
																		xhr,
																		ajaxOptions,
																		thrownError) {
																	alert('Connection to the server got lost.');
																}
															})
													.done(
															function(finalJSON) {

																var jsonObject = $
																		.parseJSON(finalJSON);
																var distributedDataInfoHtml = "<ul>";
																$
																		.each(
																				jsonObject,
																				function(
																						distributedDataKey,
																						distributedDataVal) {
																					if (typeof distributedDataVal != 'object') {
																						distributedDataInfoHtml += "<li><span class='clabel'>"
																								+ distributedDataKey
																								+ "</span><span class='cval'>"
																								+ distributedDataVal
																								+ "</span></li>";
																					} else {
																						$
																								.each(
																										distributedDataVal,
																										function(
																												a,
																												b) {
																											distributedDataInfoHtml += "<li><span class='clabel'>"
																													+ a
																													+ "</span><span class='cval'>"
																													+ b
																													+ "</span></li>";
																										});

																					}

																				});
																distributedDataInfoHtml += "</ul>";
																$(
																		'#hdfsDataNodeInformation')
																		.html(
																				distributedDataInfoHtml);

																$(
																		'#favWidgetBox')
																		.hide();
																$(
																		'#trendWidgetBox')
																		.hide();
															});

										});

						$('.nodeBox, .hdfsNodeBox').live(
								'mouseover',
								function() {
									var availToolTipText = $(this).find('input').val();
									if (availToolTipText) {
										customNodeDetailTooptip($(this), availToolTipText);
									} else {
										var toolTipText = $(this).find('span').html();
										customNodeDetailTooptip($(this), toolTipText);
									}
								});
								
						$('.nodeBoxWithoutClick').live(
								'mouseover',
								function() {
									var appendableText = "<br /> <div>No data found.</div>"							
									if($(this).hasClass('gray')){
										var availToolTipText = $(this).find('input').val();
										if (availToolTipText) {
											availToolTipText = availToolTipText;											
											customNodeDetailTooptip($(this), availToolTipText);
										} else {
											var toolTipText = $(this).find('span').html();
											toolTipText = toolTipText + appendableText;
											customNodeDetailTooptip($(this), toolTipText);
										}									
									} else {
										var availToolTipText = $(this).find('input').val();
										if (availToolTipText) {
											customNodeDetailTooptip($(this), availToolTipText);
										} else {
											var toolTipText = $(this).find('span').html();
											customNodeDetailTooptip($(this), toolTipText);
										}
									}
								});
								
						$('.nodeBox, .nodeBoxWithoutClick, .hdfsNodeBox').live(
								'mouseout', function() {
									$('#chartpseudotooltip').html('').hide();
								});

						$('#copyToAllLink').click(
								function() {
									var getPass = $('#node0').val();
									$("#passwordModelBox").find('input')
											.each(function() {
												$(this).val(getPass);
											});
								});
						$('#toggleProfilerStateFld').live('click', function() {
							var profilerState = getCookie('DisableProfiler');
							if (profilerState == 'TRUE') {
								$("#timeInterval").removeAttr("disabled");
								//enable the profiler
								var usedNodeIP = getCookie("lastUseNodeIP");
								nodeTimerId = setInterval(function() {
									nodeDetailsViewAjax(usedNodeIP)
								}, ajaxInterval);
								profileTimerId = setInterval(
										function() {
											var generalSettingFormJson = formSubmit('generalSettingForm');
											setCookie(
													'generalSettingFormCookie',
													generalSettingFormJson,
													365);
											callProfileServletForJSON(generalSettingFormJson);
										}, ajaxInterval);
								setCookie('DisableProfiler', "FALSE", 365);
								
								var newDate1 = new Date();
								var datetime1 = " " + newDate1.timeNow() + " &nbsp; &nbsp; " + newDate1.today();
								$('.profiler-off-time').html(datetime1);		
								
								$('.profiler-off-time').prev().html("Monitor On Time:");						
								
								//$('.profiler-off-time').html(" ");

							} else {
								$("#timeInterval").attr("disabled","disabled");
								//disable the profiler	
								clearInterval(nodeTimerId);
								clearInterval(profileTimerId);
								setCookie('DisableProfiler', "TRUE", 365);
								var newDate2 = new Date();
								var datetime2 = " " + newDate2.timeNow() + " &nbsp; &nbsp; " + newDate2.today();
								$('.profiler-off-time').html(datetime2);
								
								$('.profiler-off-time').prev().html("Monitor Off Time:");
							}

						});
						
						$("#timeInterval")
								.blur(
										function() {
											var nodeIP = getCookie('lastUseNodeIP');
											var checkProfiler = getCookie('DisableProfiler');
											var newInterval = parseInt($(
													"#timeInterval").val());
											newInterval = newInterval * 1000;
											if (newInterval > 5000) {
												ajaxInterval = newInterval;
												clearInterval(profileTimerId);
												clearInterval(nodeTimerId);
												if (checkProfiler != 'TRUE') {
													nodeTimerId = setInterval(
															function() {
																nodeDetailsViewAjax(nodeIP)
															}, ajaxInterval);
													profileTimerId = setInterval(
															function() {
																var generalSettingFormJson = formSubmit('generalSettingForm');
																setCookie(
																		'generalSettingFormCookie',
																		generalSettingFormJson,
																		365);
																callProfileServletForJSON(generalSettingFormJson);
															}, ajaxInterval);
												}

											}
										});
						checkCookie();

						defaultSelectedCategories();

						

						$(".favBox").draggable({
							containment : "#favWidgetBox",
							cursor : "move"
						});

					});
</script>