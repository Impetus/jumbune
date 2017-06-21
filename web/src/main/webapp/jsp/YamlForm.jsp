<%@page import="java.util.Properties"%>
<%@page import="java.io.InputStream"%>
<script>
var uploader_mr;
</script>
<body>
	<%
		InputStream stream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("distributionInfo.properties");
		Properties props = new Properties();
		props.load(stream);
		String hadoopDistValue = props.getProperty("HadoopDistribution");
	%>
<div id="yaml-dialog-modal" class="commonBox">

	<form method="POST" action="ExecutionServlet" name="yamlForm"
		id="yamlForm" enctype="multipart/form-data">

		<div id="wizard" class="swMain">
			<ul>
				<li><a href="#step-1"><label class="stepNumber">Basic</label>
				</a>
				</li>
				<li class="stepLine"></li>

				<li><a href="#step-4"><label class="stepNumber">M/R Jobs</label>
				</a>
				</li>
				<li class="stepLine"></li>

				<li><a href="#step-5"><label class="stepNumber">Flow Debugging</label>
				</a>
				</li>
				<li class="stepLine"></li>
				<%
				if (!hadoopDistValue.equals("m")) {
				%>
					<li><a href="#step-6"><label class="stepNumber">Profiling</label>
					</a>				
					</li>
				<%					
					}
				%>
				<li class="stepLine"></li>
				
				<li><a href="#step-2"><label class="stepNumber">HDFS Validation</label>
				</a>
				</li>
				<li class="stepLine"></li>

				<li><a href="#step-7"><label class="stepNumber">Tuning</label>
				</a>
				</li>
				<li class="stepLine"></li>


				<li><a href="#step-8"><label class="stepNumber">Schedule</label>
				</a>
				</li>
				<li style="width: 12px;">&nbsp;</li>

				<li id="previewTab" style="display: none;"><a href="#step-9"><label
						class="stepNumber">Preview</label>
				</a>
				</li>
			</ul>
			<div id="step-1" class="step-content">

			<div class="status note">
					<span>Note: </span>Collects basic information about Hadoop master node and worker node.
				</div>


				<div class="fieldsetBox innerFieldsetBox">
					<div class="commonBox previewInfo" style="width:495px">
						<div class="lbl">
							<label>Jumbune Job Name</label>
						</div>
						<div class="fld">
						<!--<input type="text" name="jumbuneJobName" id="jobName" class="inputbox" parsley-trigger="change" parsley-type="email"/>-->
						<input type="text" name="jumbuneJobName" id="jobName" class="inputbox validate[maxSize[60],custom[onlyNumberCharacterAndUnderscore]]" />
						</div>
					</div>
				</div>
				<div class="clear"></div>
				<div class="fieldsetBox innerFieldsetBox">
					<div class="paddBott">Master node Information</div>
					<fieldset>

						<div class="fixWidthBox">
							<div class="lbl">
								<label>User</label><span class="asterix"> </span>
							</div>
							<div class="fld">
								<input type="text" name="master.user" id="user" class="inputbox"/>
								&nbsp;<span id="msg_user" class="asterix"></span>
							</div>
						</div>

						<div class="fixWidthBox">
							<div class="lbl">
								<label>Host</label><span class="asterix"> </span>
							</div>
							<div class="fld">
								<input type="text" name="master.agent.agentHost" id="host" class="inputbox mediumInput" maxlength="15" />
								&nbsp;<span id="msg_host" class="asterix"></span>
							</div>
						</div>

						<div class="fixWidthBox">
							<div class="lbl">
								<label>RSA/DSA file</label><span class="asterix"> </span>
							</div>
							<div class="fld">
								<input type="text" name="master.rsaFile" id="rsaFile"
									class="inputbox" /> &nbsp;<span id="msg_rsaFile"
									class="asterix"></span>
							</div>
						</div>

						<!-- <div class="fixWidthBox">
							<div class="lbl">
								<label>DSA file</label>
							</div>
							<div class="">
								<input type="text" name="master.dsaFile" id="dsaFile"
									class="inputbox" /> &nbsp;<span id="msg_dsaFile"
									class="asterix"></span>
							</div>
						</div>-->
						
						<div class="fixWidthBox">
							<div class="lbl">
								<label>Agent Port</label><span id="agentPort" class="asterix"> </span>
							</div>
							<div class="fld">								
								<input type="text" name="master.agent.agentPort" id="agentPort" class="inputbox smallInput" />								
							</div>
						</div>
						
						<div class="fixWidthBox">
								<div class="lbl">
									<label>Jmx port on master node</label><span class="asterix"> </span>
								</div>
								<div class="fld">
									<input type="text" name="master.nameNodeJmxPort"
										id="master.nameNodeJmxPort" value="5677" class="inputbox smallInput"/>
								</div>
							</div>

							<div class="fixWidthBox">
								<div class="lbl">
									<label>Jmx port on JobTracker</label><span class="asterix"> </span>
								</div>
								<div class="fld">
									<input type="text" name="master.jobTrackerJmxPort"
										id="master.jobTrackerJmxPort" value="5680" class="inputbox smallInput"/>
								</div>
							</div>
							<div class="fixWidthBox">
								<div class="lbl">
									<label>Jmx port on ResourceManager</label><span class="asterix"> </span>
								</div>
								<div class="fld">
									<input type="text" name="master.resourceManagerJmxPort"
										id="master.resourceManagerJmxPort" value="5680" class="inputbox smallInput"/>
								</div>
							</div>
						
					</fieldset>
				</div>

				<div class="fieldsetBox innerFieldsetBox">
					<div class="paddBott">Worker node Information</div>
					<fieldset>
						<div class="fixWidthBox clear">
							<div class="lbl">
								<label>Work directory on worker nodes</label><span class="asterix"> </span>
							</div>
							<div class="fld">
								<input type="text" name="slaveWorkingDirectory" id="slaveWorkingDirectory"
									class="inputbox" />
							</div>
						</div>
						<div class="fixWidthBox clear">
								<div class="lbl">
									<label>Jmx port on worker node</label><span class="asterix"> </span>
								</div>
								<div class="fld">
									<input type="text" name="slaveParam.dataNodeJmxPort"
										id="slaveParam.dataNodeJmxPort" value="5679" class="inputbox smallInput"/>
								</div>
							</div>

							<div class="fixWidthBox">
								<div class="lbl">
									<label>Jmx port on TaskTracker</label><span class="asterix"> </span>
								</div>
								<div class="fld">
									<input type="text" name="slaveParam.taskTrackerJmxPort"
										id="slaveParam.taskTrackerJmxPort" value="5678" class="inputbox smallInput"/>
								</div>
							</div>
							<div class="fixWidthBox">
								<div class="lbl">
									<label>Jmx port on NodeManager</label><span class="asterix"> </span>
								</div>
								<div class="fld">
									<input type="text" name="slaveParam.nodeManagerJmxPort"
										id="slaveParam.nodeManagerJmxPort" value="5678" class="inputbox smallInput"/>
								</div>
							</div>
						<div class="fixWidthBox clear" style="width:650px !important;">
							<div class="lbl">
								<label>No. of unique users</label><span class="asterix"> </span>
							</div>
							<div class="fld">
								<input type="text" name="noOfSlaves" id="noOfSlaves"
									class="inputbox smallInput validate[custom[integer],min[1]]" value="1"/>&nbsp;<a
									href="javascript:void(0);" id="noOfSlavesBtn" class="addSign">Add</a>
							</div>
						</div>

						<div id="copySlaveMasterBox" class="fixWidthBox"
							style="display: none; width:380px;">
							<input type="checkbox" name="copySlaveMaster"
								id="copySlaveMaster" value="1" />&nbsp;&nbsp; <label
								for="copySlaveMaster" style="float: left;">Copy information to all worker nodes
								from master node</label>
						</div>


						<div id="slaveFieldBox"></div>
					</fieldset>
				</div>

			</div>

			<div id="step-2" class="step-content">

				<div class="status note">
					<span>Note: </span>Creates report which depicts HDFS data
					discrepancies. Validates HDFS data against data violation
					constraints.
				</div>

				<div class="fieldsetBox">
					<div class="paddBott">HDFS Data Validation</div>
					<fieldset>
						<div class="commonBox previewInfo">							
							<div class="fld chk">
								<input type="checkbox" name="enableDataValidation"
									id="enableValidation" value="TRUE" previewText="Validations are enabled" />
							</div>
							<div class="">
								<label>Enable Data Validation</label>
							</div>
						</div>

						<div id="validationFieldsBox">

							<div class="fixWidthBox previewInfo">
								<div class="lbl">
									<label>HDFS Input Path</label>
								</div>
								<div class="fld">
									<input type="text" name="hdfsInputPath" id="hdfsInputPath"
										class="inputbox" disabled previewText="HDFS input path is set."/>
								</div>
							</div>

							<div class="fixWidthBox">
								<div class="lbl">
									<label>Tuple Record Separator</label>
								</div>
								<div class="fld">
									<input type="text" name="dataValidation.recordSeparator"
										id="recordSeparator" class="inputbox" disabled/>
								</div>
							</div>

							<div class="fixWidthBox">
								<div class="lbl">
									<label>Tuple Field Separator</label>
								</div>
								<div class="fld">
									<input type="text" name="dataValidation.fieldSeparator"
										id="fieldSeparator" class="inputbox" disabled/>
								</div>
							</div>

							<div id="noOfFieldsBox" class="fixWidthBoxFull" >
								<div class="lbl">
									<label>No. of fields</label>
								</div>
								<div class="fld">
									<input type="text" name="dataValidation.numOfFields"
										id="noOfFields" class="inputbox smallInput" disabled value="1"/>&nbsp;<a
										href="javascript:void(0);" id="noOfFieldsBtn" class="addSign">Add</a>
								</div>
							</div>

							<div id="extraDataValidationHeaderBox"
								class="commonBox bold borderBottom" style="display: none;">
								<div class="fixWidthValidationCheckBox" style="text-align:center;">Enable Row</div>
								<div class="fixWidthValidationCheckBox" style="text-align:center;">Field Number</div>
								<div class="fixWidthValidationCheckBox" style="display:none;">Validate?</div>
								<div id="extraDataValidationHeaderRow">
									<div class="fixWidthValidationBox" style="text-align: center;">Null
										Check</div>
									<div class="fixWidthValidationBox" style="text-align: center;">Field
										Type</div>
									<div class="fixWidthValidationBox" style="text-align: center;">RegEx</div>
								</div>
								<!-- div class="fixWidthValidationCheckBox remove-col">&nbsp;</div -->
								
								
								
							</div>

							<div id="dataValidationFieldBox"></div>


							

						</div>
					</fieldset>
				</div>

			</div>

			<div id="step-4" class="step-content">

				<div class="status note">
					<span>Note: </span>Tab collects mapreduce job jar and dependent
					jar information.
				</div>
				
				<div class="fieldsetBox innerFieldsetBox">
					<div class="paddBott">Specify job jar information</div>
					<fieldset>
						<div class="commonBox fld">
							<div class="lbl" style="padding: 0px; width: 110px;">
								<label>Choose system</label>
							</div>
							<div class="fld">
								<input type="radio" name="jobjar.machinePath"
									id="localMachinePath" value="1" checked="true" /> <label
									for="localMachinePath">Local system</label> <input
									type="radio" name="jobjar.machinePath" id="masterMachinePath"
									value="2" /> <label for="masterMachinePath">Jumbune system</label>
							</div>
						</div>
                   <input type="hidden" id="isLocalSystemJar" name="isLocalSystemJar" value="somevalue">
						<div class="commonBox">
							<div id="localMachinePathFieldBox" class="previewInfo">
								<div class="fleft">
									<label style="display: none;">Choose system</label>&nbsp;
								</div>
								<div class="fld">
									<label class="fleft">Jar path</label>&nbsp; <input
										type="file" name="inputFile" id="localMachineFile" size="11" class="inputbox" />
								</div>
							</div>

							<div id="masterMachinePathFieldBox" class=""></div>
						</div>
					</fieldset>
				</div>
				<div class="fieldsetBox innerFieldsetBox clear">
					<div class="paddBott">Add dependent jars</div>
					<fieldset>

						<div class="fixWidthBoxFull">
							<div class="fld">
							<div style="float:right;"><a id="pickfiles_mrf" href="javascript:void(0);" class="buttonFinish" style="float:left !important;margin:0 !important;z-index:1 !important;">Add jars</a><br>  &nbsp;&nbsp;<input type="text" id="jarInputType" class="inputboxes" readonly="readonly" style="display:none;"></div>
							<div id="displayFileUpload_files" style="display: block" class="uploadfilewrap_multiple">
							
							<div  id="filelist"></div>
							
							</div>
							
							  <!-- <a id="uploadfiles_mr" href="#">[Upload files]</a>  -->
							
							
							 <input type="text" name="classpath.userSupplied.source" value="-1" class="inputboxes" style="display:none !important;">
								<!-- <select name="classpath.userSupplied.source" id="jarInputType"
									class="inputboxes">
									<option value="-1">Please Select</option>
									<option value="1">accumulated in job jar</option>
									<option value="2">Available in hadoop lib</option>
									<option value="3">master machine path</option>
									<option value="4">slave machine path</option>
									<option value="5">local filesystem path</option>
								</select> -->
								
							</div>
						</div>

						<div id="userSuppliedResource" class="fixWidthBox"
							style="display: none;">
							<div class="lbl">
								<label>Files</label>
							</div>
							<div class="fld">
								<textarea class="inputbox"
									name="classpath.userSupplied.resource" id="resource"></textarea>
							</div>
						</div>

						<div id="userSuppliedExclude" class="fixWidthBox"
							style="display: none;">
							<div class="lbl">
								<label>Exclude</label>
							</div>
							<div class="fld">
								<textarea class="inputbox" name="classpath.userSupplied.exclude"
									id="exclude"></textarea>
							</div>
						</div>

						<div id="machineClassPathMainBox"></div>

						<!-- <div class="commonBox">
							<br /> <strong>Following jars are provided by Jumbune
								(Please do not include these jars):-</strong><br />
							<br />
							<ul>
								<li class="fleft" style="padding-right: 23px;">asm-4.0.jar</li>
								<li class="fleft" style="padding-right: 23px;">asm-tree-4.0.jar</li>
							</ul>
						</div> -->


					</fieldset>
				</div>
				<div class="fieldsetBox">
					<div class="paddBott">Jobs</div>
					<fieldset>
						<div class="fixWidthBox">
							<div class="lbl">
								<label>No. of jobs</label>
							</div>
							<div class="fld">
								<input type="text" name="noOfJobs" id="noOfJobs"
									class="inputbox smallInput" value="1" /> &nbsp;<span id="msg_noOfJobs"
									class="asterix"></span> &nbsp;<a href="javascript:void(0);"
									id="noOfJobsBtn" class="addSign">Add</a>
							</div>
						</div>

						<div id="includeClassJarBox" class="fixWidthBox"
							style="display: none;">
							<input type="checkbox" name="includeClassJar"
								id="includeClassJar" value="TRUE" /> <label
								for="includeClassJar">Job class defined in the jar
								manifest</label>
						</div>

						<div id="jobsFields" class="clear"></div>

					</fieldset>
				</div>

			</div>

			<div id="step-5" class="step-content">

				<div class="status note">
					<span>Note: </span>Creates report which helps to detect mapreduce
					implementation faults. Performs mapreduce job flow analysis.
				</div>

				<div class="fieldsetBox">
					<div class="paddBott">Debugger</div>
					<fieldset>

						<div class="commonBox previewInfo">
							<div class="fld chk">
								<input type="checkbox" name="debugAnalysis" id="debugAnalysis"
									value="TRUE" previewText="Debug Analysis is enabled."/>
							</div>
							<div class="">
								<label>Enable Debugger</label>
							</div>							
						</div>

						<div id="debugAnalysisBox">
							<div class="commonBox clear">
								<div class="fld chk">
									<input type="checkbox" name="logKeyValues" id="logKeyValues" />
								</div>
								<div class="">
									<label>Enable Logging of Unmatched Keys / Values</label>
								</div>
							</div>
							<div class="commonBox clear">
								<div class="fld chk">
									<input type="checkbox" id="instrumentRegex" name="debuggerConf.logLevel.instrumentRegex" value="FALSE" disabled/>
								</div>
								<div class="">
									<label>Use Regex</label>
								</div>
								<!--<div class="fld">
									<input type="radio"
										name="debuggerConf.logLevel.instrumentRegex"
										id="instrumentRegexTrue" value="TRUE" /> <label
										for="instrumentRegexTrue">True</label> <input type="radio"
										name="debuggerConf.logLevel.instrumentRegex"
										id="instrumentRegexFalse" value="FALSE" checked="true" /> <label
										for="instrumentRegexFalse">False</label>
								</div>-->
							</div>

							<div class="clear"></div>
							<div id="instrumentRegexField" style="">
								<div class="commonBox">
									<a class="addSign addMoreSign" href="javascript:void(0);"
										onclick="javascript:instrumentRegexAddField();" style="display:none">Add More
										Regex</a>
								</div>
								<div class="fieldsetBox clear">
									<div class="paddBott">
										<a style="margin-top: -1px;" href="javascript:void(0);"
											onclick="javascript:instrumentRegexRemoveField(0);"
											class="removeSign">Remove</a>Regex 1
									</div>
									<fieldset>
										<div id="instrumentRegexFieldRow0">

											<div class="fixWidthBox">
												<div class="lbl">
													<label>Mapper/Reducer Name</label>
												</div>
												<div class="fld">
													<input type="text" name="regexValidations[0].classname"
														id="regexClassName0" class="inputbox" disabled/>
												</div>
											</div>


											<div class="fixWidthBox">
												<div class="lbl">
													<label>Regex on Key</label>
												</div>
												<div class="fld">
													<input type="text" name="regexValidations[0].key"
														id="mapKey0" class="inputbox" disabled/>
												</div>
											</div>

											<div class="fixWidthBox">
												<div class="lbl">
													<label>Regex on Value</label>
												</div>
												<div class="fld">
													<input type="text" name="regexValidations[0].value"
														id="mapValue0" class="inputbox" disabled/>
												</div>
											</div>
										</div>
									</fieldset>
								</div>
							</div>


							<div class="commonBox clear">
								<div class="fld chk">
									<input type="checkbox" name="debuggerConf.logLevel.instrumentUserDefValidate" id="instrumentUserDefValidate" value="FALSE" disabled/>
								</div>
								<div class="">
									<label>User Defined Validations</label>
								</div>
								<!--<div class="fld">
									<input type="radio"
										name="debuggerConf.logLevel.instrumentUserDefValidate"
										id="instrumentUserDefValidateTrue" value="TRUE" /> <label
										for="instrumentUserDefValidateTrue">True</label> <input
										type="radio"
										name="debuggerConf.logLevel.instrumentUserDefValidate"
										id="instrumentUserDefValidateFalse" value="FALSE"
										checked="true" /> <label for="instrumentUserDefValidateFalse">False</label>
								</div>-->
							</div>

							<div class="clear"></div>
							<div id="instrumentUserDefValidateField" class="fleft">
								<div class="commonBox">
									<a class="addSign addMoreSign" href="javascript:void(0);"
										onclick="javascript:instrumentUserDefValidateAddField();" style="display:none">Add
										More Validation</a>
								</div>
								<div class="fieldsetBox clear">
									<div class="paddBott">
										<a style="margin-top: -1px;" href="javascript:void(0);"
											onclick="javascript:instrumentUserDefValidateRemoveField(0);"
											class="removeSign">Remove</a>User Validation 1
									</div>
									<fieldset>
										<div id="instrumentUserDefValidateFieldRow0">

											<div class="fixWidthBox">
												<div class="lbl">
													<label>Mapper/Reducer Name</label>
												</div>
												<div class="fld">
													<input type="text" name="userValidations[0].classname"
														id="userClassName0" class="inputbox" disabled/>
												</div>
											</div>

											<div class="fixWidthBox">
												<div class="lbl">
													<label>Key Validator Class</label>
												</div>
												<div class="fld">
													<input type="text" name="userValidations[0].key"
														id="mapKeyValidator0" class="inputbox" disabled/>
												</div>
											</div>

											<div class="fixWidthBox">
												<div class="lbl">
													<label>Value Validator Class</label>
												</div>
												<div class="fld">
													<input type="text" name="userValidations[0].value"
														id="mapValueValidator0" class="inputbox" disabled/>
												</div>
											</div>
										</div>
									</fieldset>
								</div>
							</div>
						</div>


					</fieldset>
				</div>

			</div>
				<%
				if (!hadoopDistValue.equals("m")) {
				%>
			<div id="step-6" class="step-content">

				<div class="status note">
					<span>Note: </span>Creates cluster wide MapReduce Job execution
					and cluster Profiling report.
				</div>

				<div class="fieldsetBox">
					<div class="paddBott">Profiling</div>
					<fieldset>
						<div class ="commonBox previewInfo">
						<!-- <input type="hidden" name="enableStaticJobProfiling"
									id="enableStaticJobProfiling" value="TRUE" /> -->
						
						<div class="fld chk">
								<input type="checkbox" name="enableStaticJobProfiling"
									id="enableStaticJobProfiling" value="TRUE" previewText="Job Profiling is enabled."/>
									
									<input type="hidden" name="profilingParams.mapers"
										id="mapInstancesProfiled" class="inputbox" value="0-1" previewText="Map Instances are profiled." />
										
										<input type="hidden" name="profilingParams.reducers"
										id="reducerInstancesProfiled" class="inputbox" value="0-1" previewText="Reducer Instances are profiled."/>	
							</div>
							<div class="">
								<label class="bold">Job Profiling</label>
						</div>
						<div>
							<input type="checkbox" name="runJobFromJumbune" id="runJobFromJumbune" value="TRUE"/> Run From Jumbune &nbsp;&nbsp;&nbsp; 
							Existing Job Name <input type="text" name="existingJobName" id="existingJobName" />
						</div>
						</div>
						<div id="ProfilePreviewBox" style="display: none;width:100%;margin-left:10px;">
							<div class="fixWidthBox">
								<div class="lbl">
									<label>Map instances to be profiled</label>
								</div>
								<div class="fld">
									<!-- <input type="text" name="profilingParams.mapers"
										id="mapInstancesProfiled" class="inputbox" /> -->
								</div>
							</div>

							<div class="fixWidthBox">
								<div class="lbl">
									<label>Reducer instances to be profiled</label>
								</div>
								<div class="fld">
									<!-- <input type="text" name="profilingParams.reducers"
										id="reducerInstancesProfiled" class="inputbox" /> -->
								</div>
							</div>
						</div>
						<div class="commonBox previewInfo">							
							<div class="fld chk">
								<input type="checkbox" name="hadoopJobProfile"
									id="hadoopJobProfile" value="TRUE" previewText="Cluster Monitoring is enabled." />
							</div>
							<div class="">
								<label class="bold">Cluster Monitoring</label>
							</div>
						</div>

						<div id="ProfileFieldsBox" style="display: none;width:100%;margin-left:10px;">

							

							

							

							<div class="fixWidthBox">
							<input type="hidden" name="profilingParams.statsInterval"
										id="interval" class="inputbox" value="5000" />
								<!-- <div class="lbl">
									<label>Interval (in msecs)</label>
								</div>
								<div class="fld">
									
								</div> -->
							</div>

							

						</div>
					</fieldset>
				</div>

			</div>

<%					
					}
				%>

			<div id="step-7" class="step-content">

				<div class="status note">
					<span>Note: </span>Performs automatic self tuning of the Hadoop
					cluster. Creates report depicting best Hadoop MapReduce Job
					configuration for supplied Jar.
				</div>

				<div class="fieldsetBox">
					<div class="paddBott">Cluster Tuning</div>
					<fieldset>
						<div class="commonBox previewInfo">
							<div class="lbl">
								<label>Enable Tuning</label>
							</div>
							<div class="fld">
								<input type="checkbox" name="selfTuning" id="selfTuning"
									value="TRUE" />
							</div>
						</div>

						<div id="tuningFieldsBox" style="display: none;">
							
							<div class="commonBox">
								<div class="lbl">
									<label>Enable Quick Tuning</label>
								</div>
								<div class="fld">
									<input type="checkbox" id="quickTuning" name="clusterTuning.quickTuning"  />
								</div>
							</div>
							
							<div id="quickTuningJobInput"></div>
							<div id="slowTuning">
							<div class="commonBox">
								<div class="lbl">
									<label>Objective</label>
								</div>
								<div class="fld">
									<input type="radio" name="clusterTuning.objective"
										id="tuneClusterReduceTimeOfJobExecution" value="REDUCE_TIME_OF_JOB_EXECUTION" checked="true" /> <label
										for="tuneReduceTimeOfJobExecution">Reduce time of Job execution</label> <input
										type="radio" name="clusterTuning.objective"
										id="tuneClusterJobRunSuccess" value="MAKE_MY_JOB_RUN_SUCCESSFULLY" /> <label
										for="tuneClusterJobRunSuccess">Make my job run successfully</label>
								</div>
							</div>
							
							<div class="commonBox" >
								<div class="lbl">
									<label>Resource Share</label>
								</div>
								<div class="fld">
									<input type="radio" name="clusterTuning.isFairSchedulerEnabled" id="clusterTuningDefineInFairScheduler" value="TRUE" checked="true" /> <label
										for="clusterTuningDefineInFairScheduler">Defined in Fair Scheduler</label> <input
										type="radio" name="clusterTuning.isFairSchedulerEnabled"
										id="clusterTuningManual" value="FALSE" /> <label
										for="clusterTuningManual">Manual</label>
								</div>
							</div>
							
							<div class="commonBox" id="divClusterTuningManual" style="display:none">
								<div class="fixWidthBoxFull">
									<div class="lbl lbl-full">
										<label>Max resources can be consumed on worker nodes</label>
									</div>
								</div>
								
								<div class="fixWidthBoxFull">
									<div class="lbl">
										<label for="clusterTuningMapSlots">Map slots</label>
									</div>
									<div class="fld">	
										<input type="text" name="clusterTuning.availableMapTasks" id="clusterTuningMapSlots" class="inputbox"  />
									</div>
								</div>
								
								<div class="fixWidthBoxFull">
									<div class="lbl">
										 <label for="clusterTuningReduceSlots" style="margin-left:10px;">Reduce slots</label>
									</div>
									<div class="fld">	
										 <input type="text" name="clusterTuning.availableReduceTasks" id="clusterTuningReduceSlots"  class="inputbox" /> 			
									</div>
								</div>
							
							</div>
							
							<div style="padding-bottom:15px;">&nbsp;</div>
							
							
							
							

							<!-- <div class="commonBox">
								<div class="lbl">
									<label>Solution Duration</label>
								</div>
								<div class="fld">
									<input type="text" name="clusterTuning.solutionDuration"
										id="solutionDuration" class="inputbox" />
								</div>
							</div> -->
							<div class="fieldsetBox innerFieldsetBox" style="margin:0 !important;">
								<div class="paddBott">Job Information</div>
									<fieldset>
									
										<div class="commonBox" >
											<div class="lbl">
												<label>MapReduce Jar</label>
											</div>
											<div class="fld">
												<input type="radio" name="clusterTuning.useStandardWordCountJar"
													id="tuneClusterWordCount" value="TRUE" checked="true" /> <label
													for="tuneClusterWordCount">Standard word count</label> <input
													type="radio" name="clusterTuning.useStandardWordCountJar"
													id="tuneClusterUserSupplied" value="FALSE" /> <label
													for="tuneClusterUserSupplied">User Supplied jar</label>
											</div>
										</div>
										<div class="commonBox">
											<div class="lbl">
												<label>Job Input Data Path</label>
											</div>
											<div class="fld">
												<input type="text" name="clusterTuning.jobInputPath" id="dataSize"
													class="inputbox" /> <!-- <span>&nbsp;(in mb)<span> -->
											</div>
										</div>
                                                                            
                                                                                <div class="commonBox">
											<div class="lbl">
												<label>Output Directory/File</label>
											</div>
											<div class="fld">
												<input type="text" name="clusterTuning.outputFolder" id="txtoutputFolder"
													class="inputbox" /> <!-- <span>&nbsp;(in mb)<span> -->
											</div>
										</div>
			
										<!-- <div class="commonBox previewInfo">
											<div class="lbl">
												<label>App Intensitivity</label>
											</div>
											<div class="fld">
												<select style="width: 170px;"
													name="clusterTuning.hadoopPerformance" id="hadoopPerformance"
													class="inputboxes">
													<option value="">Please Select</option>
													<option value="READ_INTENSIVE">Read Intensive</option>
													<option value="MIX_READ_EXECUTION_INTENSIVE">Read &
														Execution Intensive</option>
													<option value="EXECUTION_INTENSIVE">Execution
														Intensive</option>
													<option value="MIX_WRITE_EXECUTION_INTENSIVE">Write &
														Execution Intensive</option>
													<option value="WRITE_INTENSIVE">Write Intensive</option>
													<option value="READ_WRITE_INTENSIVE">Read & Write
														Intensive</option>
												</select>
											</div>
			
										</div> -->
			
										 <div class="commonBox">
											<div class="lbl">
												<label>Job Type </label>
											</div>
											<div class="fld">
												<select name="clusterTuning.hadoopPerformance"
													id="incrementalInterval" class="inputboxes">
													<option value="">Please Select</option>
													<option value="READ_INTENSIVE">Job is analyzing huge data set</option>
													<option value="EXECUTION_INTENSIVE">Job is computation intensive</option>
													<option value="WRITE_INTENSIVE">Job intended to produce huge data set</option>
													<option value="READ_WRITE_INTENSIVE">Job is read-write intensive</option>
													<option value="VARYING_SIZED_FILE_READ_INTENSIVE">Data set contains varying sized files</option>
													<option value="SMALL_FILES_READ_INTENSIVE">Data set contains small sized files</option>
													<option value="CANT_BE_CLASSIFIED_IN_ABOVE">Can't be classified in above</option>
												</select>
											</div>
										</div>
										
									<div class="commonBox">
										<div class="lbl">
											<label>Limit recommendation time</label>
										</div>
										<div class="fld">
											<input type="checkbox" name="clusterTuning.limitTuningTime" id="limitTuningTime" checked="checked" />
										</div>
										<div class="lbl" style="width: 75px;">
											<label>Duration</label>
										</div>
										<div class="fld">
											<input type="text" name="clusterTuning.tuningTime" id="tuningTime" class="inputbox" placeholder="in minutes" style="min-width: 150px;max-width:150px" disabled/> 
										</div>
									</div>
									
								</fieldset>	
							</div>
							<!-- <div class="commonBox">
								<div class="lbl">
									<label>Incremental data size</label>
								</div>
								<div class="fld">
									<input type="text" name="clusterTuning.incrementalDataSize"
										id="incrementalDataSize" value="1" class="inputbox" />
								</div>
							</div>



							<div class="commonBox">
								<div class="lbl">
									<label>Output Folder</label>
								</div>
								<div class="fld">
									<input type="text" name="clusterTuning.outputFolder"
										id="solutionDuration" class="inputbox" />
								</div>
							</div> -->
							</div>
						</div>
					</fieldset>
				</div>

			</div>

			<div id="step-8" class="step-content">

				<div class="status note">
					<span>Note: </span>Helps to schedule Jumbune Job execution.
				</div>

				<div class="fieldsetBox">
					<div class="paddBott">Schedule entire workflow</div>
					<fieldset>
						<div class="commonBox">
							<div class="lbl">
								<label>Schedule Jumbune Job</label>
							</div>
							<div class="fld">
								<input type="text" name="jumbuneScheduleTaskTiming"
									id="jumbuneScheduleTaskTiming" value="" class="inputbox" />
							</div>
						</div>

					</fieldset>
				</div>

			</div>

			<div id="step-9" class="step-content">
         			<div id="step9LoaderWrap">
					<div class="txtCenter">
						<img src="./skins/images/loading.gif" width="200px" />
					</div>		
				</div>
	
				<div id="validateMsgBox"></div>
				<div id="previewMainBox">
					<div id="previewInputBox" class=""></div>

				</div>
			</div>


			<input type="hidden" name="jsonData" id="jsonData" value="" />
		</div>

	</form>

	<div style="display: none;">
		<form action="SaveJSONServlet" name="saveJSONForm" id="saveJSONForm"
			method="POST">
			<input type="hidden" name="saveJsonData" id="saveJsonData"
				value="" />
		</form>
	</div>


</div>



<div class="clear"></div>
<script type="text/javascript">

	var yamlValidate = false;
	$("#yamlForm").validationEngine('attach', {
		promptPosition : "topLeft",
		scroll : false
	});
  
     $('#logKeyValues')
	.change(
			function() {
					
				if ($('#logKeyValues').prop('checked') == true) {
					$('#logKeyValues').val("TRUE");
				} else  {
					$('#logKeyValues').val("FALSE");
				}
			});     
       

	function removeJobRow(rowId) {
		$('#extraRow' + rowId).parent("fieldset").parent(".fieldsetBox").remove();
	}

	function removeSlaveRow(rowId) {
		$('#extraslaveRow' + rowId).parent("fieldset").parent(".fieldsetBox").remove();
		validateInputBoxes();
	}

	function instrumentRegexAddField() {
		if(!$("#instrumentRegex").is(":checked")) {
			return;
		}
		var regexFieldCount = $('#instrumentRegexField').find("input[id^='mapKey']").length;
		var fieldCount = parseInt(regexFieldCount) + parseInt(1);

		var fieldHtml = '<div class="fieldsetBox clear"><div class="paddBott"><a style="margin-top:-1px;" href="javascript:void(0);" onclick="javascript:instrumentRegexRemoveField('
				+ regexFieldCount
				+ ');" class="removeSign">Remove</a>Regex '
				+ fieldCount
				+ '</div><fieldset><div id="instrumentRegexFieldRow'+regexFieldCount+'"><div class="fixWidthBox"><div class="lbl"><label>Mapper/Reducer Name</label></div><div class="fld"><input type="text" name="regexValidations['+regexFieldCount+'].classname" id="regexClassName'+regexFieldCount+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Regex on Key</label></div><div class="fld"><input type="text" name="regexValidations['+regexFieldCount+'].key" id="mapKey'+regexFieldCount+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Regex on Value</label></div><div class="fld"><input type="text" name="regexValidations['+regexFieldCount+'].value" id="mapValue'+regexFieldCount+'" class="inputbox"/></div></div></div></fieldset></div>';

		$('#instrumentRegexField').append(fieldHtml);
	}

	function instrumentRegexRemoveField(rowId) {
		$('#instrumentRegexFieldRow' + rowId).parent("fieldset").parent(".fieldsetBox").remove();
		$('#instrumentRegexField').find('.fieldsetBox').find('.paddBott').each(function(index){
			$(this).find('.regex-title-txt').html('Regex '+(index+1));
		});
	}

	function instrumentUserDefValidateAddField() {
		var validateFieldCount = $('#instrumentUserDefValidateField').find("input[id^='mapKeyValidator']").length;
		var fieldCount = parseInt(validateFieldCount) + parseInt(1);

		var validateFieldHtml = '<div class="fieldsetBox clear"><div class="paddBott"><a style="margin-top:-1px;" href="javascript:void(0);" onclick="javascript:instrumentUserDefValidateRemoveField('
				+ validateFieldCount
				+ ');" class="removeSign">Remove</a>User Validation '
				+ fieldCount
				+ '</div><fieldset><div id="instrumentUserDefValidateFieldRow'+validateFieldCount+'"><div class="fixWidthBox"><div class="lbl"><label>Mapper/Reducer Name</label></div><div class="fld"><input type="text" name="userValidations['+validateFieldCount+'].classname" id="userClassName'+validateFieldCount+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Key Validator Class</label></div><div class="fld"><input type="text" name="userValidations['+validateFieldCount+'].key" id="mapKeyValidator'+validateFieldCount+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Value Validator Class</label></div><div class="fld"><input type="text" name="userValidations['+validateFieldCount+'].value" id="validateValue'+validateFieldCount+'" class="inputbox"/></div></div></div></div></fieldset></div>';

		$('#instrumentUserDefValidateField').append(validateFieldHtml);
	}

	function instrumentUserDefValidateRemoveField(rowId) {
		$('#instrumentUserDefValidateFieldRow' + rowId).parent("fieldset").parent(".fieldsetBox").remove();
		$('#instrumentUserDefValidateField').find('.fieldsetBox').find('.paddBott').each(function(index){
			$(this).find('.user-val-title-txt').html('User Validation '+(index+1));
		});
	}

	function addMoreHostFields(i) {
		var hostFieldCount = $('#morehostField' + i).find("input[name^='slaves[" + i + "].hosts']").length;

		var hostFieldHtml = '<div class="clear" id="moreInnerHostBox'+hostFieldCount+i+'"><div class="fixWidthBox"><div class="lbl"><label>Host</label><span class="asterix"> </span></div><div class="fld"><input type="text" name="slaves['+i+'].hosts['+ hostFieldCount +']" id="slaveHost'+hostFieldCount+i+'" class="inputbox mediumInput validate[required]" /><a href="javascript:void(0);" onclick="javascript:removeMoreHost('
				+ hostFieldCount
				+ i
				+ ');" class="removeSign">Remove</a></div></div></div>';

		$('#morehostField' + i).append(hostFieldHtml);
		validateInputBoxes();
	}

	function removeMoreHost(rowId) {
		$('#moreInnerHostBox' + rowId).remove();
		validateInputBoxes();
	}

	function removeTxtOnFocus(obj) {
		if (obj.value == " ")
			obj.value = '';
	}

	function addTxtOnBlur(obj) {
		if (obj.value == "")
			obj.value = '&nbsp;';
	}
	
	function validateInputBoxes() {						
		var isEmpty = false;
		var jtId = "master.jobTrackerJmxPort";
		var rmId = "master.resourceManagerJmxPort";
		var dnId = "slaveParam.taskTrackerJmxPort";		
		var nmId = "slaveParam.nodeManagerJmxPort";				
		$("#step-1 input[type='text']:visible").each(function() {						
			if($(this).attr('id')==jtId || $(this).attr('id')==rmId )
			{
				if( ($(this).attr('id')==jtId) && ($(this).val()=="") && document.getElementById(rmId).value == ""){
					isEmpty = true;
				}
				if($(this).attr('id')==rmId && $(this).val()=="" && document.getElementById(jtId).value == ""){
					isEmpty = true;
				}
			}else	if($(this).attr('id')==dnId || $(this).attr('id')==nmId )
			{
				if( ($(this).attr('id')==dnId) && ($(this).val()=="") && document.getElementById(nmId).value == ""){
					isEmpty = true;
				}
				if($(this).attr('id')==nmId && $(this).val()=="" && document.getElementById(dnId).value == ""){
					isEmpty = true;
				}
			}else if($(this).val() == "") {										
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
			
		
		if($("#enableStaticJobProfiling").is(':checked')){
			if(!$("#runJobFromJumbune").is(':checked'))
				if($("#existingJobName").val()==""){						
						$(".buttonFinish").addClass("disableNextStep");
				}
				
		}
			
	}
						
	var isValid = false;
	$(document)
			.ready(
					function() {
						
						$('#tuningTime').keypress(function(event) {
							  // Backspace, tab, enter, end, home, left, right
							  // We don't support the del key in Opera because del == . == 46.
							  var controlKeys = [8, 9, 13, 35, 36, 37, 39];
							  // IE doesn't support indexOf
							  var isControlKey = controlKeys.join(",").match(new RegExp(event.which));
							  // Some browsers just don't raise events for control keys. Easy.
							  // e.g. Safari backspace.
							  if (!event.which || // Control keys in most browsers. e.g. Firefox tab is 0
							      (49 <= event.which && event.which <= 57) || // Always 1 through 9
							      (48 == event.which && $(this).attr("value")) || // No 0 first digit
							      isControlKey) { // Opera assigns values for control keys.
							    return;
							  } else {
							    event.preventDefault();
							  }
							});
						
						$("#step-1 input[type='text']:visible").live("propertychange keyup input paste", function() { 
							validateInputBoxes();
						});
						
						$("#debugAnalysis, #enableStaticJobProfiling, #hadoopJobProfile, #enableValidation").live("click", function() {
							validateInputBoxes();
						});
						
						uploader_mr = new plupload.Uploader({
						runtimes : 'gears,html5,flash,silverlight,browserplus',
						browse_button : 'pickfiles_mrf',
						container : 'displayFileUpload_files',
						max_file_size : '1000mb',
						url : 'UploadJobJarServlet',
						flash_swf_url : '/plupload/js/plupload.flash.swf',
						silverlight_xap_url : '/plupload/js/plupload.silverlight.xap',
						filters : [
					            {title : "Jar files", extensions : "jar"}					            
					        ]
						
						
					});
						uploader_mr.init();
						uploader_mr.bind('BeforeUpload', function(up, file) {
						    up.settings.url =  "UploadJobJarServlet?jobName="+$("#jobName").val();
						});
				
						
				
						uploader_mr.bind('FilesAdded', function(up, files) {
							var deleteHandle = function(uploaderObject, fileObject) {
						        return function(event) {
						            event.preventDefault();
						            uploaderObject.removeFile(fileObject);
						            $(this).closest("div#" + fileObject.id).remove();
						        };
						    };
							
						$.each(files, function(i, file) {
							var file_name=$("#jarInputType").val();
							$("#jarInputType").val(file_name+","+file.name);
							//for(var x in file)
								//console.log("x="+x+" value"+file[x]);
							$('#filelist').append(
								'<div id="' + file.id + '">' +
								file.name + ' (' + plupload.formatSize(file.size) + ') <b></b>&nbsp;&nbsp;<a href="javascript:void(0);" id="deleteFile' + files[i].id + '" style="color: #DA4D79;">Remove</a>' +
							'</div>');
							$('#deleteFile' + files[i].id).click(deleteHandle(up, files[i]));
						});
				
						up.refresh(); // Reposition Flash/Silverlight
					});
				
						uploader_mr.bind('UploadProgress', function(up, file) {
						$('#' + file.id + " b").html(file.percent + "%");
						
					});
				
						uploader_mr.bind('Error', function(up, err) {
						//$('#filelist').append("<div>Error: " + err.code +
						//	", Message: " + err.message +
						//	(err.file ? ", File: " + err.file.name : "") +
						//	"</div>"
						//);
						alert("File not supported:"+err.file.name);
				
						up.refresh(); // Reposition Flash/Silverlight
					});
				
						uploader_mr.bind('FileUploaded', function(up, file) {
						$('#' + file.id + " b").html("100%");
						//console.log(" File Uploaded -"+file.id);
					});
						
						uploader_mr.bind('StateChanged', function(uploader_mr) {							
					        if (uploader_mr.files.length === (uploader_mr.total.uploaded + uploader_mr.total.failed)) {
					        	//console.log("Completed Files");
					        	$('#yamlForm').submit();
					        }
					    });
						
						/*document.getElementById('uploadfiles_mr').onclick = function() {
							
							uploader_mr.start();
							//toggle();
							return false;

						};*/
	
						
						
						$("#scheduleDate").datetimepicker({
							minDate : 0,
							dateFormat : 'dd mm D',
							timeFormat : 'mm hh'
						});
						/*var jumbuneScheduleDate = new Date();
        					var currentMonth = jumbuneScheduleDate.getMonth(); 
        					var currentDate = jumbuneScheduleDate.getDate(); 
        					var currentYear = jumbuneScheduleDate.getFullYear();*/
						$("#jumbuneScheduleTaskTiming").datetimepicker({
							/*minDate : new Date(currentYear, currentMonth, currentDate),*/
							minDate : 0,
							dateFormat : 'dd mm D',
							timeFormat : 'mm hh'
						});

						// RSA and DSA field populate auto
						$('#user').blur(
								function() { 
									var userVal = $('#user').val();
									$('#rsaFile')
											.val(
													"/home/" + userVal
															+ "/.ssh/id_rsa");
									/* $('#dsaFile')
											.val(
													"/home/" + userVal
															+ "/.ssh/id_dsa"); */
								});

						// no. of jobs field onblur code
						$('#noOfJobsBtn')
								.click(
										function() {
											var jobsCount = $('#noOfJobs')
													.val();
											if (jobsCount) {
												var jobHtml = "";
												for ( var i = 0; i <= jobsCount - 1; i++) {
													var fieldCount = parseInt(i)
															+ parseInt(1);
													jobHtml += '<div class="fieldsetBox"><div class="paddBott">';
													if ( i > 0 ) {
														jobHtml += '<a style="margin-top:-1px;" href="javascript:void(0);" onclick="javascript:removeJobRow('
															+ i
															+ ');" class="removeSign">Remove</a>';
													}
													jobHtml += 'Job '
															+ fieldCount
															+ '</div><fieldset><div id="extraRow'+i+'" class="commonBox"><div class="fixWidthBox previewInfo"><div class="lbl"><label>Name</label></div><div class="fld"><input type="text" name="jobs['+i+'].name" id="name'+i+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Job Class Name</label></div><div class="fld"><input type="text" name="jobs['+i+'].jobClass" id="jobClassName'+i+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Parameters</label></div><div class="fld"><input type="text" name="jobs['+i+'].parameters" id="jobsParams'+i+'" class="inputbox"/></div></div></div></fieldset></div>';
												}
												$('#jobsFields').html(jobHtml);
												$('#includeClassJarBox').show(
														'slow');
												$('#includeClassJar').attr(
														'checked', false);
											} else {
												$('#jobsFields').html('');
												$('#includeClassJarBox').hide(
														'slow');
											}
										});

						// Job Class defined in the Manifest checkbox click event code		
						$("#includeClassJar")
								.click(
										function() {

											var includeClassJarCheck = $(
													'#includeClassJar').prop(
													'checked');
											var setCount = $('#jobsFields')
													.find(
															"input[id^='jobClassName']")
													.closest('.fixWidthBox').length;

											$('#noOfJobs').val(1);

											for ( var i = 0; i <= setCount - 1; i++) {
												if (i != 0) {
													$('#jobsFields')
															.find(
																	'#extraRow'
																			+ i)
															.closest(
																	'.fieldsetBox')
															.remove();
												}

											}

											if (includeClassJarCheck == true) {
											$('#jobsFields').find("input[id^='jobClassName']").val('');												
												$('#jobsFields')
														.find(
																"input[id^='jobClassName']")
														.closest('.fixWidthBox')
														.hide();
											} else {
												$('#jobsFields')
														.find(
																"input[id^='jobClassName']")
														.closest('.fixWidthBox')
														.show();
											}

										});
						$(".hostRange").live("click", function(){
							var id=$(this).attr("id");
							
							if($(this).attr('checked')){
								$("#"+id+"_From_Value").css("display","block");
								$("#"+id+"_To_Value").css("display","block");
								$("#"+id+"_From_Value_space").css("display","block");
							}
							else{
								$("#"+id+"_From_Value").css("display","none");
								$("#"+id+"_To_Value").css("display","none");
								$("#"+id+"_From_Value_space").css("display","none");
								$("#step-1 input[type='text']:visible").change();								
							}
							validateInputBoxes();
							//alert(id);
							//hostRangeValue
						});
						
						$("#runJobFromJumbune").live("click", function(){
							if($(this).is(":checked")){
								$("#existingJobName").attr("disabled", "disabled");
								$("#existingJobName").val('');								
								$(".buttonFinish").removeClass("disableNextStep");
								
							}else{
								$("#existingJobName").attr("disabled", false);								
								$(".buttonFinish").addClass("disableNextStep");								
							}					
							
						});	

						$("#enableStaticJobProfiling").live("click", function(){
							if($("#enableStaticJobProfiling").is(':checked')){
								$("#runJobFromJumbune").attr('checked','checked');	
								$("#existingJobName").attr("disabled", "disabled");
								$("#existingJobName").val('');				
							}else{
								$("#runJobFromJumbune").attr('checked',false);			
							}
						});	
						
									

						// no. of salves field onblur code
						$('#noOfSlavesBtn')
								.click(
										function() {																						
											var slavesCount = $('#noOfSlaves')
													.val();
											if (slavesCount) {
												var slaveHtml = "";
												var addLink = "";
												for ( var i = 0; i <= slavesCount - 1; i++) {
													var fieldCount = parseInt(i)
															+ parseInt(1);

													addLink = '<a href="javascript:void(0);" onclick="javascript:addMoreHostFields('
															+ i
															+ ');" class="addSign">Add</a>';

													slaveHtml += '<div class="fieldsetBox clear"><div class="paddBott">';
													
													if ( i > 0 ) {
														slaveHtml += '<a class="removeSign" onclick="javascript:removeSlaveRow(' + i + ');" href="javascript:void(0);" style="margin-top:-1px;">Remove</a>';
													}
													
													slaveHtml += 'Worker Node '
															+ fieldCount
															+ '</div><fieldset><div id="extraslaveRow'+i+'" class="clear fixWidthBoxBox"><div class="fixWidthBox"><div class="lbl"><label>User</label><span class="asterix"> </span></div><div class="fld"><input type="text" name="slaves['+i+'].user" id="slaveUser'+i+'" class="inputbox" /></div></div><div id="morehostField'+i+'" class="clear" ><div class="fixWidthBox" style="width:690px !important;"><div class="lbl"><label>Host</label><span class="asterix"> </span></div><div class="fld fld-extend"><input type="text" name="slaves['+i+'].hosts['+i+']" id="slaveHost'+i+'0" class="inputbox mediumInput" />'
															+ addLink
															+ '<span style="float:left; padding-left:22px;padding-right:7px;">Specify Range <input type="checkbox" class="hostRange" name="slaves['+i+'].enableHostRange" id="enableHostRange'+i+'" value="TRUE"/></span><input type="text" size="10" name="slaves['+i+'].hostRangeFromValue" id="enableHostRange'+i+'_From_Value" style="display:none;float:left;min-width:97px;" class="inputbox validate[required]"><span style="float:left;display:none" id="enableHostRange'+i+'_From_Value_space">-</span><input type="text" size="10" name="slaves['+i+'].hostRangeToValue" id="enableHostRange'+i+'_To_Value" style="display:none;float:left;min-width:97px;" class="inputbox validate[required]"></div></div></div></div></fieldset></div>';
												}
												$('#slaveFieldBox').html(
														slaveHtml);
												$('#copySlaveMasterBox').show(
														'slow');
											} else {
												$('#copySlaveMasterBox').hide(
														'slow');
												$('#slaveFieldBox').html('');

											}
											validateInputBoxes();
										});

						// no. of fields field onblur code
						$('#noOfFieldsBtn')
								.click(
										function() {
											
											var dataValidationFieldCount = $(
													'#noOfFields').val();
											if (dataValidationFieldCount) {
												var dataValidationFieldHtml = "";

												for ( var i = 0; i <= dataValidationFieldCount - 1; i++) {
													var fieldCount = parseInt(i)
															+ parseInt(1);

													dataValidationFieldHtml += '<div class="commonBox clear borderBottom"><div class="fixWidthValidationCheck"><input type="checkbox" id="validationChk'+i+'"/></div><div class="fixWidthValidationCheckBox disabledRow" style="text-align: center;">'
															+ fieldCount
															+ '</div><div class="fixWidthValidationCheckBox disabledRow" style="display:none;"><div class="fld"><input disabled type="hidden" name="dataValidation.fieldValidationList['+i+'].fieldNumber" id="fieldNumber'+i+'" value="'+i+'"/></div></div><div id="extraDataValidationRow'+i+'" class=""><div class="fixWidthValidationBox disabledRow"><div class="fld"><select name="dataValidation.fieldValidationList['+i+'].nullCheck" id="nullCheck'+i+'" disabled><option value="&nbsp;">Please Select</option><option value="mustBeNull">must be null</option><option value="notNull">must not be null</option></select></div></div><div class="fixWidthValidationBox disabledRow"><div class="fld"><select disabled name="dataValidation.fieldValidationList['+i+'].dataType" id="dataType'+i+'" class="inputboxes"><option value="&nbsp;">Please Select</option><option value="int_type">int</option><option value="float_type">float</option><option value="long_type">long</option><option value="double_type">double</option></select></div></div><div class="fixWidthValidationBox disabledRow"><div class="fld"><input type="text" disabled="true" name="dataValidation.fieldValidationList['+i+'].regex" id="dataValidationRegex'+i+'" class="inputbox" /></div></div></div></div></div>';
												}
												
												$('#dataValidationFieldBox')
														.html(
																dataValidationFieldHtml);
												$(
														'#extraDataValidationHeaderBox')
														.show();
											} else {
												$('#dataValidationFieldBox')
														.html('');
												$(
														'#extraDataValidationHeaderBox')
														.hide();
											}
										});

						

						// fieldSeparator field onblur code
						/* $('#fieldSeparator').blur(function() {
							var fieldSeparatorValue = $(this).val();
							if (fieldSeparatorValue) {
								$('#noOfFieldsBox').show('slow');
							} else {
								$('#noOfFieldsBox').hide('slow');
							}
						}); */

						// debugger if block click event code
						$("input[name='debuggerConf.logLevel.ifblock']")
								.click(
										function() {
											var ifBlockCheck = $(
													"input[name='debuggerConf.logLevel.ifblock']")
													.prop('checked');
											if (ifBlockCheck) {
												$('#ifBlockNestingLevel').show(
														'slow');
											} else {
												$('#ifBlockNestingLevel').hide(
														'slow');
											}

										});

						// instrument regex select box onchange event code
						$("input[name='debuggerConf.logLevel.instrumentRegex']").click(function() {
								var instrumentRegexCheck = $('#instrumentRegex').prop('checked');													
								if (instrumentRegexCheck) {
									$('#instrumentRegexField input[type="text"]').removeAttr("disabled");
									$('#instrumentRegexField .addMoreSign').show();
									$('#instrumentRegex').attr("value", "TRUE");
								} else {
									$("#instrumentRegexField input[type='text']").val('');
									$('#instrumentRegexField [type="text"]').attr("disabled", true);
									$('#instrumentRegexField .addMoreSign').hide();
									$('#instrumentRegex').attr("value","FALSE");
								}

							});

						// instrument user defind validation select box onchange event code		
						$(
								"input[name='debuggerConf.logLevel.instrumentUserDefValidate']")
								.click(
										function() {
											var instrumentUserDefValidateCheck = $('#instrumentUserDefValidate')
													.prop('checked');											
											if (instrumentUserDefValidateCheck) {											
													$('#instrumentUserDefValidateField input[type="text"]').removeAttr("disabled");
													$('#instrumentUserDefValidateField .addMoreSign').show();
													$("#instrumentUserDefValidate").attr("value", "TRUE");
											} else {											
													$('#instrumentUserDefValidateField [type="text"]').attr("disabled", true).val('');	
													$('#instrumentUserDefValidateField .addMoreSign').hide();												
													$("#instrumentUserDefValidate").attr("value", "FALSE");
											}

										});

						// partitioner select box onchange event code		
						$("input[name='debuggerConf.logLevel.partitioner']")
								.click(
										function() {

											var partitionerCheck = $(
													'#partitionerTrue').prop(
													'checked');
											if (partitionerCheck == true) {
												$('#partitionerFieldBox')
														.show();
											} else {
												$('#partitionerFieldBox')
														.hide();
											}

										});
						$(".fixWidthValidationCheck input[type='checkbox']").die("click").live("click",function() {
							var validationCheck = $(this).prop('checked');
								if (validationCheck == true) {
									$(this).parent().parent().find(".fixWidthValidationCheckBox, .fixWidthValidationBox").removeClass("disabledRow");
									$(this).parent().parent().find("input[type='text'], select").removeAttr("disabled");
								} else {
									$(this).parent().parent().find(".fixWidthValidationCheckBox, .fixWidthValidationBox").addClass("disabledRow");
									$(this).parent().parent().find("input[type='text'], select").attr("disabled", true);
								}
						});

						// jarInputType select box onchange event code		
						$('#jarInputType')
								.change(
										function() {
											var jarInputTypeVal = $(this).val();
											if (jarInputTypeVal == '3') {
												$('#machineClassPathMainBox')
														.html(
																'<div id="masterMachineClassPathBox" class="fixWidthBox"> <div class="lbl"> <label>File Path Machine path</label> </div> <div class="fld"> <textarea class="inputbox" name="classpath.userSupplied.folder" id="masterMachineClassPath"></textarea></div></div>');
												$('#userSuppliedResource')
														.show('slow');
												$('#userSuppliedExclude').show(
														'slow');
											} else if (jarInputTypeVal == '4') {
												$('#machineClassPathMainBox')
														.html(
																'<div id="slaveMachineClassPathBox" class="fixWidthBox"> <div class="lbl"> <label>Slave machine path</label> </div> <div class="fld"> <textarea class="inputbox" name="classpath.userSupplied.folder" id="slaveMachineClassPath"></textarea></div></div>');
												$('#userSuppliedResource')
														.show('slow');
												$('#userSuppliedExclude').show(
														'slow');
											} else if (jarInputTypeVal == '5') {
												$('#machineClassPathMainBox')
														.html(
																'<div id="localMachineClassPathBox" class="fixWidthBox"> <div class="lbl"> <label>Local machine path</label> </div> <div class="fld"> <textarea class="inputbox" name="classpath.userSupplied.folder" id="localMachineClassPath"></textarea></div></div>');
												$('#userSuppliedResource')
														.show('slow');
												$('#userSuppliedExclude').show(
														'slow');
											} else {
												$('#machineClassPathMainBox')
														.html('');
												$('#userSuppliedResource')
														.hide('slow');
												$('#userSuppliedExclude').hide(
														'slow');
											}

										});

						// enable profile checkbox click event code
						$('#localMachinePath')
								.click(
										function() {
											if ($('#localMachinePath').prop(
													'checked') == true) {
												$('#localMachinePathFieldBox')
														.html(
																'<div class="fleft"><label style="display:none;">Job jar location</label>&nbsp;</div><div class="fld"><label class="fleft">Jar Path</label>&nbsp;<input type="file" name="inputFile" id="localMachineFile" size="11" class="inputbox"/></div>');

												$('#masterMachinePathFieldBox')
														.html('');
											}

										});

						// enable profile checkbox click event code
						$('#masterMachinePath')
								.click(
										function() {

											if ($('#masterMachinePath').prop(
													'checked') == true) {
												$('#localMachinePathFieldBox')
														.html('');

												$('#masterMachinePathFieldBox')
														.html(
																'<div class="fleft"><label style="display:none;">Job jar location</label>&nbsp;</div><div class="fld"><label class="fleft">Jar Path</label>&nbsp;<input type="text" name="inputFile" id="masterMachineFile" class="inputbox"/></div>');
											}

										});

						//use entore checkbox click in debugginh section
						$('#useEntireWorkingSet').click(function() {
	
							if ($('#useEntireWorkingSet').prop('checked') == true)
							{
								$('#sampleWorkingSetBox').hide();
							}
							else
							{
								$('#sampleWorkingSetBox').show();
							}

						});

						// enable debuger checkbox click event code
						$('#debugAnalysis')
							.click(
									function() {
										if ($('#debugAnalysis').prop(
												'checked') == true) {
												$('#logKeyValues').removeAttr("disabled");
												$("#instrumentUserDefValidate, #instrumentRegex").removeAttr("disabled");
												var instrumentRegexCheck = $('#instrumentRegex').prop('checked');													
												if (instrumentRegexCheck) { 
													$('#instrumentRegexField input[type="text"]').removeAttr("disabled");
													$('#instrumentRegexField .addMoreSign').show();
													$('#instrumentRegex').attr("value", "TRUE");
												} else {
													$("#instrumentRegexField input[type='text']").val('');
													$('#instrumentRegexField [type="text"]').attr("disabled", true);
													$('#instrumentRegexField .addMoreSign').hide();
													$('#instrumentRegex').attr("value","FALSE");
												}
												
												var instrumentUserDefValidateCheck = $('#instrumentUserDefValidate')
												.prop('checked');											
												if (instrumentUserDefValidateCheck) {											
														$('#instrumentUserDefValidateField input[type="text"]').removeAttr("disabled");
														$('#instrumentUserDefValidateField .addMoreSign').show();
														$("#instrumentUserDefValidate").attr("value", "TRUE");
												} else {												
														$('#instrumentUserDefValidateField [type="text"]').attr("disabled", true).val('');	
														$('#instrumentUserDefValidateField .addMoreSign').hide();												
														$("#instrumentUserDefValidate").attr("value", "FALSE");
												}
										
										} else {
												$("#logKeyValues").attr("checked", false);
												$('#logKeyValues').val("FALSE");
												$('#logKeyValues').attr("disabled", true);
												$("#instrumentUserDefValidate, #instrumentRegex").attr("disabled", true).attr("checked", false);
											$('#debugAnalysisBox')
													.find(
															'input[type="text"], select')
													.val('');
													$('#instrumentRegexField [type="text"]').attr("disabled", true);
													$('#instrumentRegexField .addMoreSign').hide();
													$('#instrumentRegex').attr("value","FALSE");
													$('#instrumentUserDefValidateField [type="text"]').attr("disabled", true).val('');
													$('#instrumentUserDefValidateField .addMoreSign').hide();												
													$("#instrumentUserDefValidate").attr("value", "FALSE");
												
										}
									});


						// enable profile checkbox click event code
						/* $('#hadoopJobProfile')
								.click(
										function() {

											if ($('#hadoopJobProfile').prop(
													'checked') == true) {
												$('#ProfileFieldsBox').show(
														'slow');
											} else {
												$('#ProfileFieldsBox').hide(
														'slow');
												$('#ProfileFieldsBox')
														.find(
																'input[type="text"], select')
														.val('');
											}

										});
						 $('#enableStaticJobProfiling')
						.click(
								function() {
									if ($('#enableStaticJobProfiling').prop(
									'checked') == true) {
										$('#ProfilePreviewBox').show(
												'slow');
									} else {
										$('#ProfilePreviewBox').hide(
												'slow');
										$('#ProfilePreviewBox')
												.find(
														'input[type="text"], select')
												.val('');
									}
								}); */

						// enable validation checkbox click event code
						$('#enableValidation')
								.click(
										function() {

											if ($('#enableValidation').prop(
													'checked') == true) {												
												$("#noOfFields").val(1);
												$('#noOfFieldsBtn').click();
												$('#validationFieldsBox input[type="text"]').removeAttr('disabled');
											} else {
												$('#validationFieldsBox input[type="text"]').attr('disabled', true);
												$('#validationFieldsBox')
														.find(
																'input[type="text"], select')
														.val('');
												$('#validationFieldsBox')
														.find(
																'input[type="checkbox"]')
														.prop('checked', false);
												$(".fixWidthValidationCheck input[type='checkbox']").parent().parent().find(".fixWidthValidationCheckBox, .fixWidthValidationBox").addClass("disabledRow");
												$(".fixWidthValidationCheck input[type='checkbox']").parent().parent().find("input[type='text'], select, input[type='checkbox']").attr("disabled", true);														
											}

										});
						// enable Scheduling checkbox click event code
						$('#enableScheduling')
								.live(
										'click',
										function() {

											if ($('#enableScheduling').prop(
													'checked') == true) {
												$(
														'#dataValidationSchedulingBox')
														.show('slow');
											} else {
												$(
														'#dataValidationSchedulingBox')
														.hide('slow');
												$(
														'#dataValidationSchedulingBox')
														.find(
																'input[type="text"], select')
														.val('');
											}

										});

						// enable tuning checkbox click event code
						$('#selfTuning')
								.click(
										function() {

											if ($('#selfTuning')
													.prop('checked') == true) {
												$('#tuningFieldsBox').show(
														'slow');
											} else {
												$('#tuningFieldsBox').hide(
														'slow');
												$('#tuningFieldsBox')
														.find(
																'input[type="text"], select')
														.val('');
											}

										});
						// copySlaveMatser checkbox click event code
						$('#copySlaveMaster')
								.click(
										function() {

											var masterUser = $('#user').val();
											var masterHost = $('#host').val();

											var slaveFieldCount = $('body')
													.find(
															"input[name^='slaves']").length;

											if ($('#copySlaveMaster').prop(
													'checked') == true) {
												for ( var i = 0; i <= slaveFieldCount - 1; i++) {
													$('#slaveUser' + i).val(
															masterUser);
													$('#slaveHost' + i).val(
															masterHost);
												}
											} else {
												for ( var i = 0; i <= slaveFieldCount - 1; i++) {
													$('#slaveUser' + i).val('');
													$('#slaveHost' + i).val('');
												}
											}

										});

						// data validation field hide/show
						$('body')
								.find("input[id^='fieldNumber']")
								.live(
										'click',
										function() {

											var inputId = $(this).val();

											if ($('#fieldNumber' + inputId).prop('checked') == true) {
												$('#extraDataValidationRow' + inputId).show('slow');
												$('#dataType' + inputId).closest('.fixWidthValidationBox').show('slow');
												$('#dataValidationRegex' + inputId).closest('.fixWidthValidationBox').show('slow');
											} else {
												//$('#extraDataValidationRow'+inputId).hide('slow');
												$('#extraDataValidationRow' + inputId).parent('.commonBox').remove();
												$('#dataType' + inputId).closest('.fixWidthValidationBox').hide('slow');
												$('#dataValidationRegex' + inputId).closest('.fixWidthValidationBox').hide('slow');
											}
										});
						
$('#validationFieldsBox').find("a[id^='removeHDFSValFields']").live('click',function(){
			
			//var inputId = $(this).parents('.commonBox').find("input[id^='fieldNumber']").val();
			
			/*if($('#removeHDFSValFields'+inputId).prop('checked')==true)
			{
				$('#extraDataValidationRow'+inputId).show('slow');
			}
			else
			{
				//$('#extraDataValidationRow'+inputId).hide('slow');
				$('#extraDataValidationRow'+inputId).parent('.commonBox').remove(); 
			}	*/
			if(!$(this).parent().hasClass("disabledRow")) {
				$(this).parent().parent().parent().remove(); 
			}		

		});						
						
						$("input:radio[name='clusterTuning.isFairSchedulerEnabled']")
						.click(
								function() {									
									if($(this).val()=="FALSE")
										$("#divClusterTuningManual").show('slow');
									else
										$("#divClusterTuningManual").hide('slow');
								
								});
						

						// Smart Wizard        
						$('#wizard').smartWizard({
							transitionEffect : 'slideleft',
							keyNavigation : false,
							enableAllSteps : true,
							onLeaveStep : leaveAStepCallback,
							onShowStep : checkStepNumber,
							onFinish : onFinishCallback
						});

						//$('a.buttonFinish').addClass('buttonDisabled');

						function leaveAStepCallback(obj) {
							var step_num = obj.attr('rel'); // get the current step number	
							return validateSteps(step_num); // return false to stay on step and true to continue navigation
						}

						function checkStepNumber(obj) {
							var step_num = obj.attr('rel'); // get the current step number	
							<%
							if (!hadoopDistValue.equals("m")) {
							%>
							if (step_num == 8) {
								$('#yaml-dialog-modal .actionBar a').hide();
								if ( yamlValidate == true ) {
									$('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="buttonPrevious" href="javascript:void(0);">Save Json</a><a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');	
								} else {
									$('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="disableNextStep buttonPrevious" href="javascript:void(0);">Save Json</a><a id="runWizardBtn" class="disableNextStep buttonPrevious" href="javascript:void(0);">Run</a>');	
								}
								//$('#yaml-dialog-modal .actionBar a').hide('slow');
								/*if ($('#yaml-dialog-modal .actionBar').find('a#validateWizardBtn').text() == "") {
									$('#yaml-dialog-modal .actionBar').append('<a id="validateWizardBtn" style= "display:none" class="buttonPrevious validateWizard" href="javascript:void(0);">Validate</a>');
								   $('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="buttonPrevious" href="javascript:void(0);">Save Yaml</a><a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');
								}*/
							} else if (step_num == 7) {
								$('a.buttonNext').addClass('buttonDisabled');
								$('#yaml-dialog-modal .actionBar a#validateWizardBtn').remove();
								$('#yaml-dialog-modal .actionBar a#runWizardBtn').remove();
								$('#yaml-dialog-modal .actionBar a#saveYamlBtn').remove();
								$('#yaml-dialog-modal .actionBar a').show();
							} else {
								$('a.buttonNext').removeClass('buttonDisabled');
								$('#yaml-dialog-modal .actionBar a#validateWizardBtn').remove();
								$('#yaml-dialog-modal .actionBar a#runWizardBtn').remove();
								$('#yaml-dialog-modal .actionBar a#saveYamlBtn').remove();
								$('#yaml-dialog-modal .actionBar a').show();
							}
							<%
							}else
							{
							%>
								if (step_num == 7) {
									$('#yaml-dialog-modal .actionBar a').hide();
									if ( yamlValidate == true ) {
										$('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="buttonPrevious" href="javascript:void(0);">Save Json</a><a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');	
									} else {
										$('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="disableNextStep buttonPrevious" href="javascript:void(0);">Save Json</a><a id="runWizardBtn" class="disableNextStep buttonPrevious" href="javascript:void(0);">Run</a>');	
									}
									//$('#yaml-dialog-modal .actionBar a').hide('slow');
									/*if ($('#yaml-dialog-modal .actionBar').find('a#validateWizardBtn').text() == "") {
										$('#yaml-dialog-modal .actionBar').append('<a id="validateWizardBtn" style= "display:none" class="buttonPrevious validateWizard" href="javascript:void(0);">Validate</a>');
									   $('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="buttonPrevious" href="javascript:void(0);">Save Yaml</a><a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');
									}*/
								} else if (step_num == 6) {
									$('a.buttonNext').addClass('buttonDisabled');
									$('#yaml-dialog-modal .actionBar a#validateWizardBtn').remove();
									$('#yaml-dialog-modal .actionBar a#runWizardBtn').remove();
									$('#yaml-dialog-modal .actionBar a#saveYamlBtn').remove();
									$('#yaml-dialog-modal .actionBar a').show();
								} else {
									$('a.buttonNext').removeClass('buttonDisabled');
									$('#yaml-dialog-modal .actionBar a#validateWizardBtn').remove();
									$('#yaml-dialog-modal .actionBar a#runWizardBtn').remove();
									$('#yaml-dialog-modal .actionBar a#saveYamlBtn').remove();
									$('#yaml-dialog-modal .actionBar a').show();
								}
							<%
							}
							%>
							if (step_num == 1) {
								//auto-opening the "no. of unique users" node 1
								//$("#noOfSlavesBtn").trigger("click");
							}
							if (step_num == 3) {
								if ( $("#instrumentRegex").val() == "FALSE" ) {
									$("#instrumentRegex").attr("checked", false);
								}
								if ( $("#instrumentUserDefValidate").val() == "FALSE" ) {
									$("#instrumentUserDefValidate").attr("checked", false);
								}
							}
							return; // return false to stay on step and true to continue navigation
						}

						function onFinishCallback(obj) {	
							if (validateAllSteps(obj)) {
								yamlValidate = false;
								$("#step9LoaderWrap").show();
								$('#previewTab').show();
								$('#previewTab a').trigger('click');
								$('#jsonData').val('');
								$('#saveJsonData').val('');
								$('#validateMsgBox').html('');
								
								var formData = form2js(
									'yamlForm',
									'.',
									true,
									function(node) {
										if (node.id && node.id.match(/callbackTest/)) {
											return {
												name : node.id,
												value : node.innerHTML
											};
										}
									});
								
								if ( typeof formData.debugAnalysis === "undefined" ) {
									formData["debugAnalysis"] = "FALSE";
									formData["logKeyValues"] = "FALSE";
								}
								
								if ( typeof formData.debuggerConf === "undefined" ) {
									formData["debuggerConf"] = {
										"logLevel": {
											"instrumentRegex": "FALSE",
											"instrumentUserDefValidate": "FALSE"
										}
									}
								}
								
								if ( typeof formData.dataValidation !== "undefined" ) {
									var validationFields = formData.dataValidation.fieldValidationList;
									for ( var i=0; i<validationFields.length; i++ ) {
										var fieldNum = $("#dataValidationFieldBox .commonBox").eq(i).find("input[id^='fieldNumber']").val();
										validationFields[i].fieldNumber = fieldNum;
									}
								}
						
								if($("input:radio[name='jobjar.machinePath']:checked").val()==1)
								{formData["isLocalSystemJar"]="TRUE"
								}
								if($("input:radio[name='jobjar.machinePath']:checked").val()==2)
								{formData["isLocalSystemJar"]="FALSE"
								}
								
								
								
								var finalJson = JSON.stringify(formData, null, '\t');
								var regExp = /\\\\/g;
								finalJson = finalJson.replace(regExp, "\\");

								//data preview function
								previewData(obj);
												
								$('#jsonData').val(finalJson);
								$('#saveJsonData').val(finalJson);
								var jsonData = $('#jsonData').val();											
								$.ajax({
									type : "POST",
									url : "ValidateJsonServlet",
									data : jsonData
								})
								.done(function(jsonData) {
									$("#step9LoaderWrap").hide(); // hide loader image after validation popups
									$(".stepContainer").find(".err-field").removeClass("err-field");
									$(".stepContainer").find(".sugg-field").removeClass("sugg-field");
									$('#validateMsgBox').empty();
									var jsonData = JSON.stringify(jsonData);
									<%
									if (!hadoopDistValue.equals("m")) {
									%>
									if (typeof jsonData != 'undefined' && jsonData.length > 2) {
										var parsedJsonData = JSON.parse(jsonData);
										var suggestString = "";
										var ErrorString = "";
										$.each(parsedJsonData, function(key, value) {
											if (key == "Suggestions") {
												yamlValidate = true;
												suggestString += '<div class="normalMsg">';
												$.each(value, function(suggestKey, suggestValue) {
													suggestString += '<dl><dt>'+ suggestKey +'</dt><dd>';
													$.each(suggestValue, function(suggestDataKey, suggestDataValue) {
														suggestString += '<div><a href="javascript:void(0)" target="'+suggestKey+'">' + suggestDataValue + '</a></div>';
														$("[name='"+suggestDataKey+"']").addClass("sugg-field");
													});
													suggestString += '</dd></dl>';
												});
												suggestString += '</div>';
												
												$('#validateMsgBox').show('slow').append('<div class="status info"><span>Suggestions: </span>' + suggestString + '</div>');
												
												if ( $("#wizard ul a.selected").attr("rel") == 8 ) {
													$('#yaml-dialog-modal .actionBar a').hide();
													
													if ( $('#yaml-dialog-modal .actionBar #saveYamlBtn').size() > 0 ) {
														$('#yaml-dialog-modal .actionBar #saveYamlBtn').removeClass("disableNextStep").show();
														$('#yaml-dialog-modal .actionBar #runWizardBtn').removeClass("disableNextStep").show();
													} else {
														$('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="buttonPrevious" href="javascript:void(0);">Save Json</a><a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');
													}
												}
											} else if (key == "Failures") {
												yamlValidate = false;
												ErrorString += '<div class="validateMsg">';
												$.each(value, function(suggestKey, suggestValue) {
													ErrorString += '<dl><dt>'+ suggestKey +'</dt><dd>';
													$.each(suggestValue, function(suggestDataKey, suggestDataValue) {
														ErrorString += '<div><a href="javascript:void(0)" target="'+suggestKey+'">' + suggestDataValue + '</a></div>';
														$("[name='"+suggestDataKey+"']").addClass("err-field");
													});
													ErrorString += '</dd></dl>';
												});
												ErrorString += '</div>';
												
												$('#validateMsgBox').show('slow').append('<div class="status error"><span>Failures: </span>' + ErrorString + '</div>');
												
												if ( $("#wizard ul a.selected").attr("rel") == 8 ) {
													if ( $('#yaml-dialog-modal .actionBar #saveYamlBtn').size() > 0 ) {
														$('#yaml-dialog-modal .actionBar #saveYamlBtn').addClass("disableNextStep").show();
														$('#yaml-dialog-modal .actionBar #runWizardBtn').addClass("disableNextStep").show();
													} else {
														$('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="buttonPrevious" href="javascript:void(0);">Save Json</a><a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');
													}
												}

												//$('#yaml-dialog-modal .actionBar a#validateWizardBtn').show('slow');																 
												//$('#yaml-dialog-modal .actionBar').append('<a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');
											}
										});
																
										/*-----For Mulitple files for MR Jobs */
										/*
										if (uploader_mr.total.uploaded == 0) {
											// Files in queue upload them first
											if (uploader_mr.files.length > 0) {
												// When all files are uploaded submit form
												uploader_mr.bind('UploadProgress', function() {
													if (uploader_mr.total.uploaded == uploader_mr.files.length){
														alert('submitting the form');
														//document.create.submit();
														
													}
												})

												
											} else
												//alert('You must at least upload one file.');

											$('#yamlForm').submit();
										}*/
										
										return;
									} else { 
										yamlValidate = true;
										$('#validateMsgBox').show('slow').append('<div class="status success"><span>Information: </span>Validated Successfully.</div>');
																				
										/* $('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="buttonPrevious" href="javascript:void(0);">Save Yaml</a><a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');*/
										
										if ( $("#wizard ul a.selected").attr("rel") == 8 ) {
											$('#yaml-dialog-modal .actionBar a').hide();
											if ( $('#yaml-dialog-modal .actionBar #saveYamlBtn').size() > 0 ) {
												$('#yaml-dialog-modal .actionBar #saveYamlBtn').removeClass("disableNextStep").show();
												$('#yaml-dialog-modal .actionBar #runWizardBtn').removeClass("disableNextStep").show();
											} else {
												$('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="buttonPrevious" href="javascript:void(0);">Save Json</a><a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');
											}
										}
									}
									<%
									}else{
									%>
									if (typeof jsonData != 'undefined' && jsonData.length > 2) {
										var parsedJsonData = JSON.parse(jsonData);
										var suggestString = "";
										var ErrorString = "";
										$.each(parsedJsonData, function(key, value) {
											if (key == "Suggestions") {
												yamlValidate = true;
												suggestString += '<div class="normalMsg">';
												$.each(value, function(suggestKey, suggestValue) {
													suggestString += '<dl><dt>'+ suggestKey +'</dt><dd>';
													$.each(suggestValue, function(suggestDataKey, suggestDataValue) {
														suggestString += '<div><a href="javascript:void(0)" target="'+suggestKey+'">' + suggestDataValue + '</a></div>';
														$("[name='"+suggestDataKey+"']").addClass("sugg-field");
													});
													suggestString += '</dd></dl>';
												});
												suggestString += '</div>';
												
												$('#validateMsgBox').show('slow').append('<div class="status info"><span>Suggestions: </span>' + suggestString + '</div>');
												
												if ( $("#wizard ul a.selected").attr("rel") == 5 ) {
													$('#yaml-dialog-modal .actionBar a').hide();
													
													if ( $('#yaml-dialog-modal .actionBar #saveYamlBtn').size() > 0 ) {
														$('#yaml-dialog-modal .actionBar #saveYamlBtn').removeClass("disableNextStep").show();
														$('#yaml-dialog-modal .actionBar #runWizardBtn').removeClass("disableNextStep").show();
													} else {
														$('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="buttonPrevious" href="javascript:void(0);">Save Json</a><a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');
													}
												}
											} else if (key == "Failures") {
												yamlValidate = false;
												ErrorString += '<div class="validateMsg">';
												$.each(value, function(suggestKey, suggestValue) {
													ErrorString += '<dl><dt>'+ suggestKey +'</dt><dd>';
													$.each(suggestValue, function(suggestDataKey, suggestDataValue) {
														ErrorString += '<div><a href="javascript:void(0)" target="'+suggestKey+'">' + suggestDataValue + '</a></div>';
														$("[name='"+suggestDataKey+"']").addClass("err-field");
													});
													ErrorString += '</dd></dl>';
												});
												ErrorString += '</div>';
												
												$('#validateMsgBox').show('slow').append('<div class="status error"><span>Failures: </span>' + ErrorString + '</div>');
												
												if ( $("#wizard ul a.selected").attr("rel") == 5 ) {
													if ( $('#yaml-dialog-modal .actionBar #saveYamlBtn').size() > 0 ) {
														$('#yaml-dialog-modal .actionBar #saveYamlBtn').addClass("disableNextStep").show();
														$('#yaml-dialog-modal .actionBar #runWizardBtn').addClass("disableNextStep").show();
													} else {
														$('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="buttonPrevious" href="javascript:void(0);">Save Json</a><a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');
													}
												}

												//$('#yaml-dialog-modal .actionBar a#validateWizardBtn').show('slow');																 
												//$('#yaml-dialog-modal .actionBar').append('<a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');
											}
										});
																
										/*-----For Mulitple files for MR Jobs */
										/*
										if (uploader_mr.total.uploaded == 0) {
											// Files in queue upload them first
											if (uploader_mr.files.length > 0) {
												// When all files are uploaded submit form
												uploader_mr.bind('UploadProgress', function() {
													if (uploader_mr.total.uploaded == uploader_mr.files.length){
														alert('submitting the form');
														//document.create.submit();
														
													}
												})

												
											} else
												//alert('You must at least upload one file.');

											$('#yamlForm').submit();
										}*/
										
										return;
									} else { 
										yamlValidate = true;
										$('#validateMsgBox').show('slow').append('<div class="status success"><span>Information: </span>Validated Successfully.</div>');
																				
										/* $('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="buttonPrevious" href="javascript:void(0);">Save Yaml</a><a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');*/
										
										if ( $("#wizard ul a.selected").attr("rel") == 5 ) {
											$('#yaml-dialog-modal .actionBar a').hide();
											if ( $('#yaml-dialog-modal .actionBar #saveYamlBtn').size() > 0 ) {
												$('#yaml-dialog-modal .actionBar #saveYamlBtn').removeClass("disableNextStep").show();
												$('#yaml-dialog-modal .actionBar #runWizardBtn').removeClass("disableNextStep").show();
											} else {
												$('#yaml-dialog-modal .actionBar').append('<a id="saveYamlBtn" class="buttonPrevious" href="javascript:void(0);">Save Json</a><a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');
											}
										}
									}
									<%
									}
									%>
								});
								return false;
							}
						}

						function previewData(obj) { 
							$('#previewInputBox').html('');													
							$('.previewInfo')
									.find('div.fld :input')
									.each(
											function() {
												if (($(this).attr("type") == 'checkbox' || $(
														this).attr("type") == 'radio')
														&& ($(this).prop(
																'checked') == true)) {
																	
													$('#previewInputBox')
															.append(
																	'<div class="commonBox">'
																			+ $(this).attr("previewText")																					
																			+ '</div>');
													
												} else if (($(this)
														.attr("type") == 'text' || $(
														this).attr("type") == 'file')
														&& ($(this).val() != "")) {
													if ($(this).attr('id') == 'localMachineFile') {
														console
																.log("indexOF = "
																		+ $(
																				this)
																				.val()
																				.indexOf(
																						'fakepath'));
														if ($(this)
																.val()
																.indexOf(
																		'fakepath') > 0) {
															var path = $(this)
																	.val()
																	.split(
																			'fakepath\\');
															$(
																	'#previewInputBox')
																	.append(
																			'<div class="commonBox">'
																					+ path[1]
																					+ '</div>');
														} else {
															$(
																	'#previewInputBox')
																	.append(
																			'<div class="commonBox">'
																					+ $(
																							this).attr("previewText")
																							
																					+ '</div>');
														}

														
													} else {
														var lblVal = $(this).parent().parent(".previewInfo").find("label").html();														
														$('#previewInputBox')
																.append(
																		'<div class="commonBox">'+lblVal+':&nbsp;<b>'
																				+ $(
																						this).val()
																						
																				+ '</b></div>');														
													}
												}
											});

						}						

	$('#runWizardBtn').die("click").live('click', function() { 
		if ( !$(this).hasClass("disableNextStep") ) {
			if(uploader_mr.files.length>0)
				uploader_mr.start();
				else
				$('#yamlForm').submit();
		}
	});

						$('#saveYamlBtn').live('click', function() { 
							//$('#validateWizardBtn').click();
							if ( !$(this).hasClass("disableNextStep") ) {
								uploader.start();
								$('#saveJSONForm').submit();
							}
						});

						// Your Step validation logic
						function validateSteps(step) {
							var isStepValid = true;

							// validate step 1
							if (step == 1) {
								if($(".buttonNext").hasClass("disableNextStep")) { 
										return;
								}
								/*if($(".buttonFinish").hasClass("disableNextStep")) { 
										return;
								}*/
								if (validateStep1() == false) {	
									isStepValid = false;
									$('#wizard')
											.smartWizard(
													'showMessage',
													'Please correct the errors in step'
															+ step
															+ ' and click next.');
									$('#wizard').smartWizard('setError', {
										stepnum : step,
										iserror : true
									});
									//$('.step-content').prepend($(".msgBox"));
									$(".msgBox").css("display","block");
								} else {
/*
									$('#wizard').smartWizard('setError', {
										stepnum : step,
										iserror : false
									});*/
$(".msgBox").css("display","none");

								}
							}

							// validate step 2
							if (step == 2) {
								if (validateStep2() == false) {
									isStepValid = false;
									$('#wizard')
											.smartWizard(
													'showMessage',
													'Please correct the errors in step'
															+ step
															+ ' and click next.');
									$('#wizard').smartWizard('setError', {
										stepnum : step,
										iserror : true
									});
								} else {
									$('#wizard').smartWizard('setError', {
										stepnum : step,
										iserror : false
									});
								}
							}

							return isStepValid;
						}
						function validateAllSteps(obj) {
							//var isStepValid = true;
							var step_num = obj.attr('rel'); // get the current step number	

							// all step validation logic    
							return validateSteps(step_num);
						}

						// validate step 1 function
						function validateStep1() {
							var isValid = false;
							$('#step-1 .fieldsetBox input[type="text"]').each(function () {								if($(this).val() == "") {	
									console.log("empty value in first step :"+ $(this).attr);	
									return isValid;
								}
							});
						/*	if($("#jobName").val() == "" || $("#user").val() == "" || $("#host").val() == "" || $("#rsaFile").val() == "" || $("#agentPort").val() == "" || $("#master.nameNodeJmxPort").val() == "" || $("#master.jobTrackerJmxPort").val() == "" || $("#sJumbuneHome").val() == "" || $("#slaveParam.dataNodeJmxPort").val() == "" || $("#slaveParam.taskTrackerJmxPort").val() == "" || $("#noOfSlaves").val() == "") {
								isValid == false;															
								console.log("check invalid");
								return isValid;
							}*/							
							if ($("#yamlForm").validationEngine('validate') == true /*&& ($("#jobName").val() != "" && $("#user").val() != "" && $("#host").val() != "" && $("#rsaFile").val() != "" && $("#agentPort").val() != "" && $("#master.nameNodeJmxPort").val() != "" && ($("#master.jobTrackerJmxPort").val() != "" || $("#master.resourceManagerJmxPort").val() != "")  || $("#sJumbuneHome").val() != "" && $("#slaveParam.dataNodeJmxPort").val() != "" && $("#slaveParam.taskTrackerJmxPort").val() != "" && $("#noOfSlaves").val() != "")*/ ) {																
								isValid = true;
							}

							return isValid;
						}

						// validate step 1 function
						function validateStep2() {
							var isValid = false;

							if ($("#yamlForm").validationEngine('validate') == true) {
								isValid = true;
							}

							return isValid;
						}

						$("#slider-range-min").slider(
								{
									range : "min",
									value : 1,
									min : 1,
									max : 10,
									slide : function(event, ui) {
										var newVal = ui.value / 100;
									}
								});
						$('#supportOptionsLink').click(function() {
							$('#supportOptionsBox').toggle('slow');
						});

						var populatejson =
<%=request.getParameter("populateData")%>
	;

						if (typeof populatejson != 'undefined'
								&& populatejson != null && populatejson != '') {
							if (populatejson.logKeyValues =="TRUE") {
								$("#logKeyValues").attr("checked", true);
								$("#logKeyValues").val("TRUE");
							}
							if (populatejson.selfTuning == "TRUE" && populatejson.clusterTuning.quickTuning == "TRUE") {
								$('#quickTuning').prop('checked', true);
								showQuickTuning();
								$('#quickTuningJobID').val(populatejson.clusterTuning.quickTuningJobID);
							}
							if (populatejson.clusterTuning!= null && populatejson.clusterTuning.limitTuningTime == "TRUE") {
									$('#limitTuningTime').prop('checked',true);
									$('#limitTuningTime').val('TRUE');
									$('#tuningTime').removeAttr('disabled');
									if (populatejson.clusterTuning.tuningTime != null) {
										$('#tuningTime').val(populatejson.clusterTuning.tuningTime);
									}
							}
							
							function populateForm() {
								var popData = JSON
										.stringify(eval(populatejson));
								data = JSON.parse(popData);
								js2form(document.getElementById('yamlForm'),
										data);
							}

							populateForm();
							if ($('#enableValidation').prop('checked') != true) {
								$('#validationFieldsBox').find(
										'input[type="text"], select').val('');
								$('#validationFieldsBox').find(
										'input[type="checkbox"]').prop(
										'checked', false);
							}

							if ($('#debugAnalysis').prop('checked') != true) {
								$('#debugAnalysisBox').find(
										'input[type="text"], select').val('');
								$('#debugAnalysisBox').find(
										'input[type="checkbox"]').prop(
										'checked', false);
							}

							if ($('#hadoopJobProfile').prop('checked') != true) {
								$('#ProfileFieldsBox').find(
										'input[type="text"], select').val('');
								$('#ProfileFieldsBox').find(
										'input[type="checkbox"]').prop(
										'checked', false);
							}
							
							if ($('#selfTuning').prop('checked') != true) {
								$('#tuningFieldsBox').find(
										'input[type="text"], select').val('');
								$('#tuningFieldsBox').find(
										'input[type="checkbox"]').prop(
										'checked', false);
							}
										
							
							/* if ($('#enableStaticJobProfiling').prop('checked') != true) {
								$('#ProfilePreviewBox').find(
										'input[type="text"], select').val('');
								$('#ProfilePreviewBox').find(
										'input[type="checkbox"]').prop(
										'checked', false);
							}
							if ($('#enableStaticJobProfiling').prop('checked') == true) {
								$('#ProfilePreviewBox').show('slow');
							} */
						}
						
						$("#validateMsgBox a").die().live("click", function(e) {
							e.preventDefault();
							var target = $(this).attr("target");
							if ( target == "Basic" || target == "Basic Validation" ) {
								$("a[href='#step-1']").trigger("click");								
							} else if ( target == "M/R-Jobs" ) {
								$("a[href='#step-4']").trigger("click");								
							} else if ( target == "Flow-Debugging" ) {
								$("a[href='#step-5']").trigger("click");								
							} else if ( target == "Profiling" ) {
								$("a[href='#step-6']").trigger("click");								
							} else if ( target == "HDFS-Validation" ) {
								$("a[href='#step-2']").trigger("click");								
							} else if ( target == "Self Tuning Job" ) {
								$("a[href='#step-7']").trigger("click");								
							}
							else if ( target == "Scheduling Job" ) {
								$("a[href='#step-8']").trigger("click");								
							}
						});
						
						$("#noOfSlaves, #noOfFields").keyup(function() {
							var val = $(this).val();
							if ( val == 0 ) {
								$(this).val(1);
							}
						});
						
					});
	function hideQuickTuning() {
		$('#slowTuning').show();
		$('#tuningFieldsBox').find('input[type="text"], select').val('');
		$('#quickTuning').val("FALSE");
		$('#quickTuningJobInput').html("");
	}
	
	function showQuickTuning() {
		$('#slowTuning').hide();
		$('#quickTuning').val("TRUE");
		$('#quickTuningJobInput').html('<div class="commonBox"><div class="lbl"><label>Job ID</label></div><div class="fld"><input  type="text" id="quickTuningJobID" class="inputbox" name="clusterTuning.quickTuningJobID"></div></div>');
	}
	
	$('#quickTuning').click(function() {
			if ($('#quickTuning').prop('checked') == true) {
				showQuickTuning();
			} else {
				hideQuickTuning();
			}
	});
	
	$('#limitTuningTime').click(function() {
				if ($('#limitTuningTime').prop('checked') == true) {
					$('#limitTuningTime').val('TRUE');
					$('#tuningTime').removeAttr('disabled');
				} else {
					$('#limitTuningTime').val('FALSE');
					$('#tuningTime').attr('disabled', true);	
				}
	});
</script>
</body>
