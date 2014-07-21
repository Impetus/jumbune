		<style>
		.validateMsg{
			 color: #000000;
			 padding: 2px 0 2px 40px;
			 font-size:11px;
		}
		#dsFractionType{
			border: medium none;
			font-weight: bold;
			width: 100%;
		}
		</style>
		<div id="yaml-dialog-modal" class="commonBox">		
			<form method="POST" action="ExecutionServlet" name="yamlForm" id="yamlForm" enctype="multipart/form-data">
				<div id="wizard" class="swMain">
					<ul>
						<li><a href="#step-1"><label class="stepNumber">Basic</label></a></li>
						<li class="stepLine"></li>

						<li><a href="#step-2"><label class="stepNumber">Validation</label></a></li>
						<li class="stepLine"></li>

						<li><a href="#step-4"><label class="stepNumber">Jobs</label></a></li>
						<li class="stepLine"></li>

						<li><a href="#step-5"><label class="stepNumber">Debugger</label></a></li>
						<li class="stepLine"></li>

						<li><a href="#step-6"><label class="stepNumber">Profiling</label></a></li>
						<li class="stepLine"></li>											

						<li style="width:12px;">&nbsp;</li>

						<li id="previewTab" style="display:none;"><a href="#step-9"><label class="stepNumber">Preview</label></a></li>
					</ul>
					<div id="step-1">

						<div class="fieldsetBox innerFieldsetBox">
							<div class="commonBox previewInfo">
								<div class="lbl">
									<label>Jumbune Job Name</label><span class="asterix"> </span>
								</div>
								<div class="fld">
									<input type="text" name="jumbuneJobName" id="jobName" class="inputbox validate[required]" />	
								</div>				
							</div>
						</div>

						<div class="fieldsetBox innerFieldsetBox">
							<div class="paddBott">Master Node Information</div>
								<fieldset>					
					<div class="fixWidthBox">
						<div class="lbl">
							<label>Hadoop Home</label><span class="asterix"> </span>
						</div>
						<div class="fld">
							<input type="text" name="hadoopHome" id="hadoopField" class="inputbox validate[required]" />
							&nbsp;<span id="msg_hadoopField" class="asterix"></span>
						</div>				
					</div>
									
									<div class="fixWidthBox">
										<div class="lbl">
											<label>Worker Node Working Directory</label><span class="asterix"> *</span>
										</div>
										<div class="fld">
											<input type="text" name="sJumbuneHome" id="sJumbuneHome" class="inputbox" required />		
										</div>				
									</div>

									<div class="fixWidthBox">
										<div class="lbl">
											<label>User</label><span class="asterix"> </span>
										</div>
										<div class="fld">
											<input type="text" name="master.user" id="user" class="inputbox validate[required]" />
											&nbsp;<span id="msg_user" class="asterix"></span>
										</div>				
									</div>

									<div class="fixWidthBox">
										<div class="lbl">
											<label>Host</label><span class="asterix"> </span>
										</div>
										<div class="fld">
											<input type="text" name="master.host" id="host" class="inputbox mediumInput validate[required]"  maxlength="15" />
											&nbsp;<span id="msg_host" class="asterix"></span>
										</div>				
									</div>					

									<div class="fixWidthBox">
										<div class="lbl">
											<label>RSA/DSA File</label><span class="asterix"> </span>
										</div>
										<div class="fld">
											<input type="text" name="master.rsaFile" id="rsaFile" class="inputbox validate[required]"/>
											&nbsp;<span id="msg_rsaFile" class="asterix"></span>
										</div>				
									</div>

									<!-- <div class="fixWidthBox">
										<div class="lbl">
											<label>DSA File</label><span class="asterix"> </span>
										</div>
										<div class="">
											<input type="text" name="master.dsaFile" id="dsaFile" class="inputbox validate[required]"/>
											&nbsp;<span id="msg_dsaFile" class="asterix"></span>
										</div>				
									</div>-->

									<div class="fixWidthBox">
										<input type="checkbox" name="loadMasterSlaveInfo" id="loadMasterSlaveInfo"  value="1"/>
										&nbsp;&nbsp;<label for="loadMasterSlaveInfo">Load Master/Slave Information</label>
														
									</div>

								</fieldset>
						</div>

						<div class="fieldsetBox innerFieldsetBox">
							<div class="paddBott">Worker Node Information</div>
							<fieldset>				
								<div class="fixWidthBox">
									<div class="lbl">
										<label>No. of Worker Nodes</label>							
									</div>
									<div class="fld">							
										<input type="text" name="noOfSlaves" id="noOfSlaves" class="inputbox smallInput" />&nbsp;<a href="javascript:void(0);" id="noOfSlavesBtn" class="addSign">Add</a>							
									</div>								
								</div>

								<div id="copySlaveMasterBox" class="fixWidthBox" style="display:none;">							
										<input type="checkbox" name="copySlaveMaster" id="copySlaveMaster" value="1"/>&nbsp;&nbsp;
										<label for="copySlaveMaster">Copy information to all Worker Nodes from Master Node</label>						
								</div>

								<div id="slaveFieldBox">						
									
								</div>

							</fieldset>
						</div>					

					</div>

					<div id="step-2">

						<div class="fieldsetBox">
						<div class="paddBott">Data validation</div>
						<fieldset>				
							<div class="commonBox previewInfo">
								<div class="lbl">
									<label>Enable Data Validation</label>							
								</div>	
								<div class="fld">							
									<input type="checkbox" name="enableDataValidation" id="enableValidation" value="TRUE" />
								</div>	
							</div>

							<div id="validationFieldsBox" style="display:none;">

								<div class="fixWidthBox previewInfo">
									<div class="lbl">
										<label>hdfs Input Path</label>
									</div>
									<div class="fld">
										<input type="text" name="hdfsInputPath" id="hdfsInputPath" class="inputbox"/>
									</div>				
								</div>

								<div class="fixWidthBox">
									<div class="lbl">
										<label>Record Separator</label>
									</div>
									<div class="fld">
										<input type="text" name="dataValidation.recordSeparator" id="recordSeparator" class="inputbox"/>
									</div>				
								</div>								

								<div class="fixWidthBox">
									<div class="lbl">
										<label>Field Separator</label>
									</div>
									<div class="fld">
										<input type="text" name="dataValidation.fieldSeparator" id="fieldSeparator" class="inputbox"/>
									</div>				
								</div>						

								<div id="noOfFieldsBox" class="fixWidthBoxFull" style="display:none;">
									<div class="lbl">
										<label>No. of fields</label>								
									</div>	
									<div class="fld">								
										<input type="text" name="dataValidation.numOfFields" id="noOfFields" class="inputbox smallInput"/>&nbsp;<a href="javascript:void(0);" id="noOfFieldsBtn" class="addSign">Add</a>
									</div>	
								</div>

								<div id="extraDataValidationHeaderBox" class="commonBox bold borderBottom" style="display:none;">
									<div class="fixWidthValidationCheckBox" style="text-align:center;">Field Number</div>
									<div class="fixWidthValidationCheckBox" style="text-align:center;">Is Validate</div>
									<div id="extraDataValidationHeaderRow">
										<div class="fixWidthValidationBox" style="text-align:center;">Null Check</div>
										<div class="fixWidthValidationBox" style="text-align:center;">Field Type</div>
										<div class="fixWidthValidationBox" style="text-align:center;">RegEx</div>
									</div>
								</div>

								<div id="dataValidationFieldBox">
									
								</div>							

								
							</div>
						</fieldset>
						</div>				

					</div>


					<div id="step-4">						
							<div class="fieldsetBox innerFieldsetBox">
								<div class="paddBott">Job jar Information</div>
									<fieldset>				
										<div class="commonBox">
											<div class="lbl">
												<label>Job jar location</label>
											</div>
											<div class="fld">
												<input type="radio" name="jobjar.machinePath" id="localMachinePath" value="1" checked="true"/>
												<label for="localMachinePath">Local machine path</label>&nbsp;&nbsp;

												<input type="radio" name="jobjar.machinePath" id="masterMachinePath" value="2"/>
												<label for="masterMachinePath">Master machine path</label>
											</div>				
										</div>

										<div class="commonBox">
											<div id="localMachinePathFieldBox" class="previewInfo">
												<div class="lbl">
													&nbsp;<label style="display:none;">Job jar location</label>
												</div>
												<div class="fld">
													<label class="fleft">Browse local machine path</label>&nbsp;
													<input type="file" name="inputFile" id="localMachineFile" size="11" class="inputbox"/>			
												</div>				
											</div>

											<div id="masterMachinePathFieldBox" class=""></div>				
										</div>
									</fieldset>
							</div>				
							<div class="fieldsetBox innerFieldsetBox clear">
								<div class="paddBott">Dependent jar Information</div>
								<fieldset>		
									
									<div class="fixWidthBox">
										<div class="lbl">
											<label>Input Type</label>
										</div>
										<div class="fld">
											<select name="classpath.userSupplied.source" id="jarInputType" class="inputboxes">
												<option value="-1">Please Select</option>
												<option value="1">accumulated in job jar</option>
												<option value="2">local filesystem path</option>				
												<option value="3">slave machine path</option>	
												<option value="4">master machine path</option>
											</select>
										</div>				
									</div>
									
									<div id="userSuppliedResource" class="fixWidthBox" style="display:none;">
										<div class="lbl">
											<label>Resource</label>
										</div>
										<div class="fld">
											<textarea class="inputbox" name="classpath.userSupplied.resource" id="resource"></textarea>		
										</div>				
									</div>

									<div id="userSuppliedExclude" class="fixWidthBox" style="display:none;">
										<div class="lbl">
											<label>Exclude</label>
										</div>
										<div class="fld">
											<textarea class="inputbox" name="classpath.userSupplied.exclude" id="exclude"></textarea>
										</div>				
									</div>	
									
									<div id="machineClassPathMainBox">									

									</div>

									<div class="commonBox">
									<br/>
									<strong>Following jars are provided by Jumbune (Please do not include this in dependent jar): -</strong><br/><br/>
									<ul>
										<li class="fleft" style="padding-right:23px;">asm-4.0.jar</li>
										<li class="fleft" style="padding-right:23px;">asm-tree-4.0.jar</li>			
									</ul>
									</div>

									
								</fieldset>
							</div>				
							<div class="fieldsetBox">
								<div class="paddBott">Jobs</div>
								<fieldset>				
									<div class="fixWidthBox">
										<div class="lbl">
											<label>No. of Jobs</label>							
										</div>
										<div class="fld">							
											<input type="text" name="noOfJobs" id="noOfJobs" class="inputbox smallInput"/>
											&nbsp;<span id="msg_noOfJobs" class="asterix"></span>
											&nbsp;<a href="javascript:void(0);" id="noOfJobsBtn" class="addSign">Add</a>	
										</div>
									</div>

									<div id="includeClassJarBox" class="fixWidthBox" style="display:none;">
										<input type="checkbox" name="includeClassJar" id="includeClassJar" value="TRUE"/>
										<label for="includeClassJar">Job Class defined in the Manifest</label>
									</div>

									<div id="jobsFields" class="clear">
									</div>					

								</fieldset>
							</div>
					
					</div>

					<div id="step-5">

						<div class="fieldsetBox">
							<div class="paddBott">Debugger</div>
							<fieldset>
							
								<div class="commonBox previewInfo">
									<div class="lbl">
										<label>Enable Debugger</label>
									</div>
									<div class="fld">
											<input type="checkbox" name="debugAnalysis" id="debugAnalysis" value="TRUE"/>	
									</div>				
								</div>	
								
								<div id="debugAnalysisBox" style="display:none;">

									<div class="fixWidthBox">
										<div class="lbl">
											<label>Ifblock</label>
										</div>
										<div class="fld">
												<input type="radio" name="debuggerConf.logLevel.ifblock" id="ifblockTrue" value="TRUE"/> 
												<label for="ifblockTrue">True</label>

												<input type="radio" name="debuggerConf.logLevel.ifblock" id="ifblockFalse" value="FALSE" checked="true"/> 
												<label for="ifblockFalse">False</label>					
										</div>				
									</div>

									<div id="ifBlockNestingLevel" class="fixWidthBox" style="display:none;">
										<div class="lbl">
											<label>Max if nesting level</label>
										</div>
										<div class="fld">
											<input type="text" name="debuggerConf.maxIfBlockNestingLevel" id="ifBlockNestingLevel" class="inputbox" value="4"/>
										</div>				
									</div>		
									
									<div class="clear"></div>

									<div class="fixWidthBox">
										<div class="lbl">
											<label>Partitioner</label>
										</div>
										<div class="fld">
											<input type="radio" name="debuggerConf.logLevel.partitioner" id="partitionerTrue" value="TRUE"/> 
											<label for="partitionerTrue">True</label>

											<input type="radio" name="debuggerConf.logLevel.partitioner" id="partitionerFalse" value="FALSE" checked="true"/> 
											<label for="partitionerFalse">False</label>							
										</div>				
									</div>
									
									<div id="partitionerFieldBox" style="display:none;">
										
										<div class="fixWidthBox">
											<div class="lbl">
												<label>Sample interval</label>								
											</div>
											<div class="fld">
												<input type="text" name="partitionerSampleInterval" id="partitionerSampleInterval" class="inputbox"/>
											</div>	
										</div>
										
									</div>

									<div class="commonBox">
										<div class="lbl">
											<label>Switch</label>
										</div>
										<div class="fld">
											<input type="radio" name="debuggerConf.logLevel.switchcase" id="switchTrue" value="TRUE"/> 
											<label for="switchTrue">True</label>

											<input type="radio" name="debuggerConf.logLevel.switchcase" id="switchFalse" value="FALSE" checked="true"/> 
											<label for="switchFalse">False</label>							
										</div>				
									</div>


									<div class="commonBox">
										<div class="fld">
											<input type="checkbox" id="instrumentRegex" name="debuggerConf.logLevel.instrumentRegex" value="FALSE"/>
										</div>
										<div class="fld">
											<label>Use Regex</label>							
										</div>
										<!--<div class="fld">	
											<input type="radio" name="debuggerConf.logLevel.instrumentRegex" id="instrumentRegexTrue" value="TRUE"/> 
											<label for="instrumentRegexTrue">True</label>

											<input type="radio" name="debuggerConf.logLevel.instrumentRegex" id="instrumentRegexFalse" value="FALSE"/> 
											<label for="instrumentRegexFalse">False</label>							
										</div>-->
									</div>

									<div class="clear"></div>
									<div id="instrumentRegexField" style="">						
										<div class="commonBox">
											<a class="addSign addMoreSign" href="javascript:void(0);" onclick="javascript:instrumentRegexAddField();">Add More Regex</a>
										</div>
										<div class="fieldsetBox clear">
											<div class="paddBott"><a style="margin-top:-1px;" href="javascript:void(0);" onclick="javascript:instrumentRegexRemoveField(0);" class="removeSign">Remove</a>Regex 1</div>
											<fieldset>
												<div id="instrumentRegexFieldRow0">

													<div class="fixWidthBox">
														<div class="lbl">
															<label>Class Name</label>
														</div>
														<div class="fld">
															<input type="text" name="regexValidations[0].classname" id="regexClassName0" class="inputbox" disabled/>
														</div>				
													</div>
											

													<div class="fixWidthBox">
														<div class="lbl">
															<label>Key</label>
														</div>
														<div class="fld">
															<input type="text" name="regexValidations[0].key" id="mapKey0" class="inputbox" disabled/>
														</div>				
													</div>

													<div class="fixWidthBox">
														<div class="lbl">
															<label>Value</label>
														</div>
														<div class="fld">
															<input type="text" name="regexValidations[0].value" id="mapValue0" class="inputbox" disabled/>
														</div>				
													</div>
												</div>
											</fieldset>
										</div>
									</div>


									<div class="commonBox">
										<div class="fld chk">
											<input type="checkbox" name="debuggerConf.logLevel.instrumentUserDefValidate" id="instrumentUserDefValidate" value="FALSE"/>
										</div>
										<div class="">
											<label>User Defined Validations</label>							
										</div>
									<!--	<div class="fld">	
											<input type="radio" name="debuggerConf.logLevel.instrumentUserDefValidate" id="instrumentUserDefValidateTrue" value="TRUE"/> 
											<label for="instrumentUserDefValidateTrue">True</label>

											<input type="radio" name="debuggerConf.logLevel.instrumentUserDefValidate" id="instrumentUserDefValidateFalse" value="FALSE"/> 
											<label for="instrumentUserDefValidateFalse">False</label>	
										</div>-->
									</div>

									<div class="clear"></div>
									<div id="instrumentUserDefValidateField" class="fleft" style="display:none;">						
										<div class="commonBox">
											<a class="addSign addMoreSign" href="javascript:void(0);" onclick="javascript:instrumentUserDefValidateAddField();">Add More Validation</a>
										</div>
										<div class="fieldsetBox clear">
										<div class="paddBott"><a style="margin-top:-1px;" href="javascript:void(0);" onclick="javascript:instrumentUserDefValidateRemoveField(0);" class="removeSign">Remove</a>User Validation 1</div>
										<fieldset>
										<div id="instrumentUserDefValidateFieldRow0">	
										
											<div class="fixWidthBox">
												<div class="lbl">
													<label>Class Name</label>
												</div>
												<div class="fld">
													<input type="text" name="userValidations[0].classname" id="userClassName0" class="inputbox" disabled/>
												</div>				
											</div>

											<div class="fixWidthBox">
												<div class="lbl">
													<label>Key</label>
												</div>
												<div class="fld">
													<input type="text" name="userValidations[0].key" id="mapKeyValidator0" class="inputbox" disabled/>
												</div>				
											</div>

											<div class="fixWidthBox">
												<div class="lbl">
													<label>Value</label>
												</div>
												<div class="fld">
													<input type="text" name="userValidations[0].value" id="mapValueValidator0" class="inputbox" disabled/>
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

					<div id="step-6">

						<div class="fieldsetBox">
						<div class="paddBott">Profiling</div>
						<fieldset>				
							<div class="commonBox previewInfo">
								<div class="lbl">
									<label>Enable Profiling</label>							
								</div>	
								<div class="fld">							
									<input type="checkbox" name="hadoopJobProfile" id="hadoopJobProfile" value="TRUE" />
								</div>	
							</div>					

							<div id="ProfileFieldsBox" style="display:none;">

							<div class="fixWidthBox">
								<div class="lbl">
									<label>MasterJmx Port</label>							
								</div>	
								<div class="fld">							
									<input type="text" name="profilingParams.masterJmxPort" id="profilingParams.masterJmxPort" value="5677" />
								</div>	
							</div>

							<div class="fixWidthBox">
								<div class="lbl">
									<label>Worker NodeJmx Port</label>							
								</div>	
								<div class="fld">							
									<input type="text" name="profilingParams.dataNodeJmxPort" id="profilingParams.dataNodeJmxPort" value="5679" />
								</div>	
							</div>

							<div class="fixWidthBox">
								<div class="lbl">
									<label>taskTrackerJmx Port</label>							
								</div>	
								<div class="fld">							
									<input type="text" name="profilingParams.taskTrackerJmxPort" id="profilingParams.taskTrackerJmxPort" value="5678" />
								</div>	
							</div>

								<div class="fixWidthBox">
									<div class="lbl">
										<label>CPU Sample</label>
									</div>
									<div class="fld">
										<select name="profilingParams.cpu" id="cpuSample" class="inputboxes">
											<option value="">Please Select</option>	
											<option value="samples">samples</option>	
											<option value="times">times</option>								 
										</select>
									</div>				
								</div>

								<div class="fixWidthBox">
									<div class="lbl">
										<label>Heap Sites</label>
									</div>
									<div class="fld">
										<select name="profilingParams.heap" id="heapSites" class="inputboxes">
											<option value="">Please Select</option>
											<option value="sites">sites</option>	
											<option value="dumps">dumps</option>
										</select>
									</div>				
								</div>

								<div class="fixWidthBox">
									<div class="lbl">
										<label>Interval</label>
									</div>
									<div class="fld">
										<input type="text" name="profilingParams.statsInterval" id="interval" class="inputbox"/>
									</div>				
								</div>

								<div class="fixWidthBox">
									<div class="lbl">
										<label>Map instances to be profiled</label>
									</div>
									<div class="fld">
										<input type="text" name="profilingParams.mapers" id="mapInstancesProfiled" class="inputbox"/>
									</div>				
								</div>

								<div class="fixWidthBox">
									<div class="lbl">
										<label>Reducer instances to be profiled</label>
									</div>
									<div class="fld">
										<input type="text" name="profilingParams.reducers" id="reducerInstancesProfiled" class="inputbox"/>
									</div>				
								</div>		
								<div>
							<input type="checkbox" name="runJobFromJumbune" id="runJobFromJumbune" value="TRUE"/> Run Job From Jumbune &nbsp;&nbsp;&nbsp; 
							Existing Job Name <input type="text" name="existingJobName" />
						</div>			
							</div>
						</fieldset>
						</div>				

					</div>					



					<div id="step-9">				
						<div id="validateMsgBox" style="display:none;color:#8CC63F;background:fff;padding:10px;">
							msg box
						</div>
						<div id="previewMainBox">
							<div id="previewLabelBox" class="fixWidthBox">					
							</div>

							<div id="previewInputBox" class="fixWidthBox">					
							</div>

						</div>
					</div>


					<input type="hidden" name="yamlJsonData" id="yamlJsonData" value=""/>
				</div>	
			
			</form>			

		</div>
	


<div class="clear"></div>
<script type="text/javascript">

	$("#yamlForm").validationEngine('attach', {promptPosition : "topLeft", scroll: false});
	
	function removeJobRow(rowId)
	{
		$('#extraRow'+rowId).parent("fieldset").parent(".fieldsetBox").remove();
	}	

	function removeSlaveRow(rowId)
	{
		$('#extraslaveRow'+rowId).parent("fieldset").parent(".fieldsetBox").remove();
	}		
	
	function instrumentRegexAddField()
	{
		if(!$("#instrumentRegex").is(":checked")) {
			return;
		}
		var regexFieldCount  = $('#instrumentRegexField').find("input[id^='mapKey']").length;
		var fieldCount = parseInt(regexFieldCount)+parseInt(1);
				
		var fieldHtml = '<div class="fieldsetBox clear"><div class="paddBott"><a style="margin-top:-1px;" href="javascript:void(0);" onclick="javascript:instrumentRegexRemoveField('+regexFieldCount+');" class="removeSign">Remove</a>Regex '+fieldCount+'</div><fieldset><div id="instrumentRegexFieldRow'+regexFieldCount+'"><div class="fixWidthBox"><div class="lbl"><label>Class Name</label></div><div class="fld"><input type="text" name="regexValidations[0].classname" id="regexClassName'+regexFieldCount+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Key</label></div><div class="fld"><input type="text" name="regexValidations['+regexFieldCount+'].key" id="mapKey'+regexFieldCount+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Value</label></div><div class="fld"><input type="text" name="regexValidations['+regexFieldCount+'].value" id="mapValue'+regexFieldCount+'" class="inputbox"/></div></div></div></fieldset></div>';

		$('#instrumentRegexField').append(fieldHtml);
	}

	function instrumentRegexRemoveField(rowId)
	{
		$('#instrumentRegexFieldRow'+rowId).parent("fieldset").parent(".fieldsetBox").remove();
	}

	function instrumentUserDefValidateAddField()
	{
		var validateFieldCount  = $('#instrumentUserDefValidateField').find("input[id^='mapKeyValidator']").length;	
		var fieldCount = parseInt(validateFieldCount)+parseInt(1);	
		
		var validateFieldHtml = '<div class="fieldsetBox clear"><div class="paddBott"><a style="margin-top:-1px;" href="javascript:void(0);" onclick="javascript:instrumentUserDefValidateRemoveField('+validateFieldCount+');" class="removeSign">Remove</a>User Validation '+fieldCount+'</div><fieldset><div id="instrumentUserDefValidateFieldRow'+validateFieldCount+'"><div class="fixWidthBox"><div class="lbl"><label>Class Name</label></div><div class="fld"><input type="text" disabled name="userValidations['+validateFieldCount+'].classname" id="userClassName'+validateFieldCount+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Key</label></div><div class="fld"><input disabled type="text" name="userValidations['+validateFieldCount+'].key" id="mapKeyValidator'+validateFieldCount+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Value</label></div><div class="fld"><input disabled type="text" name="userValidations['+validateFieldCount+'].value" id="validateValue'+validateFieldCount+'" class="inputbox"/></div></div></div></div></fieldset></div>';

		$('#instrumentUserDefValidateField').append(validateFieldHtml);
	}

	function instrumentUserDefValidateRemoveField(rowId)
	{
		$('#instrumentUserDefValidateFieldRow'+rowId).parent("fieldset").parent(".fieldsetBox").remove();
	}

	function addMoreHostFields(i)
	{	
		var hostFieldCount  = $('#morehostField'+i).find("input[name^='slaves["+i+"].hosts']").length;

		var hostFieldHtml = '<div class="clear" id="moreInnerHostBox'+hostFieldCount+i+'"><div class="fixWidthBox"><div class="lbl"><label>Host</label><span class="asterix"> *</span></div><div class="fld"><input type="text" name="slaves['+i+'].hosts[]" id="slaveHost'+hostFieldCount+i+'" class="inputbox mediumInput validate[required]" /><a href="javascript:void(0);" onclick="javascript:removeMoreHost('+hostFieldCount+i+');" class="removeSign">Remove</a></div></div></div>';

		$('#morehostField'+i).append(hostFieldHtml);
	}

	function removeMoreHost(rowId)
	{		
		$('#moreInnerHostBox'+rowId).remove();
	}



	

	function removeTxtOnFocus(obj)
	{		
		if(obj.value==" ") 
		obj.value='';
	}

	function addTxtOnBlur(obj)
	{		
		if(obj.value=="") 
		obj.value='&nbsp;';		
	}

	var isValid = false;
	$(document).ready(function (){		console.log("yaml formpopulate.jsp");	
		// RSA and DSA field populate auto
		$('#user').blur(function (){  console.log("blur on user field");
			var userVal = $('#user').val();
			$('#rsaFile').val("/home/"+userVal+"/.ssh/id_rsa");
			/* $('#dsaFile').val("/home/"+userVal+"/.ssh/id_dsa"); */				
		});
		
		// no. of jobs field onblur code
		$('#noOfJobsBtn').click(function (){			
			var jobsCount = $('#noOfJobs').val();
			if(jobsCount)
			{
				var jobHtml="";
				for(var i=0; i<=jobsCount-1; i++)
				{
					var fieldCount = parseInt(i)+parseInt(1);
					jobHtml +='<div class="fieldsetBox"><div class="paddBott">';
					if ( i > 0 ) {
						jobHtml +='<a style="margin-top:-1px;" href="javascript:void(0);" onclick="javascript:removeJobRow('+i+');" class="removeSign">Remove</a>';
					}
					jobHtml +='Field '+fieldCount+'</div><fieldset><div id="extraRow'+i+'" class="commonBox"><div class="fixWidthBox previewInfo"><div class="lbl"><label>Name</label></div><div class="fld"><input type="text" name="jobs['+i+'].name" id="name'+i+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Job Class Name</label></div><div class="fld"><input type="text" name="jobs['+i+'].jobClass" id="jobClassName'+i+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Parameters</label></div><div class="fld"><input type="text" name="jobs['+i+'].parameters" id="jobsParams'+i+'" class="inputbox"/></div></div></div></fieldset></div>';
				}
				$('#jobsFields').html(jobHtml);
				$('#includeClassJarBox').show();
				$('#includeClassJar').attr('checked', false);
			}
			else
			{				
				$('#jobsFields').html('');
				$('#includeClassJarBox').hide();
			}
		});


		
		// Job Class defined in the Manifest checkbox click event code		
		$("#includeClassJar").click(function (){
			
			var includeClassJarCheck = $('#includeClassJar').prop('checked');
			var setCount = $('#jobsFields').find("input[id^='jobClassName']").closest('.fixWidthBox').length;

			$('#noOfJobs').val(1);

			for(var i=0; i<=setCount-1; i++)
			{
				if(i != 0)
				{	
					console.log($('#jobsFields').find('#extraRow'+i).closest('.fieldsetBox').html());
					$('#jobsFields').find('#extraRow'+i).closest('.fieldsetBox').remove();
				}

			}

			if(includeClassJarCheck == true)
			{
				$('#jobsFields').find("input[id^='jobClassName']").val('');				
				$('#jobsFields').find("input[id^='jobClassName']").closest('.fixWidthBox').hide();
			}
			else
			{
				$('#jobsFields').find("input[id^='jobClassName']").closest('.fixWidthBox').show();
			}


		
		});


		// no. of salves field onblur code
		$('#noOfSlavesBtn').click(function (){			
			var slavesCount = $('#noOfSlaves').val();
			if(slavesCount)
			{
				var slaveHtml="";
				var addLink="";
				for(var i=0; i<=slavesCount-1; i++)
				{
					var fieldCount = parseInt(i)+parseInt(1);
					
					addLink = '<a href="javascript:void(0);" onclick="javascript:addMoreHostFields('+i+');" class="addSign">Add</a>';					

					slaveHtml +='<div class="fieldsetBox clear"><div class="paddBott">';
					
					if ( i > 0 ) {
						slaveHtml += '<a class="removeSign" onclick="javascript:removeSlaveRow(' + i + ');" href="javascript:void(0);" style="margin-top:-1px;">Remove</a>';
					}
					
					slaveHtml +='Worker Node '+fieldCount+'</div><fieldset><div id="extraslaveRow'+i+'" class="clear"><div class="fixWidthBox"><div class="lbl"><label>User</label><span class="asterix"> </span></div><div class="fld"><input type="text" name="slaves['+i+'].user" id="slaveUser'+i+'" class="inputbox validate[required]" /></div></div><div id="morehostField'+i+'" class="clear"><div class="fixWidthBox" style="width:670px !important;"><div class="lbl"><label>Host</label><span class="asterix"> </span></div><div class="fld fld-extend"><input type="text" name="slaves['+i+'].hosts['+i+']" id="slaveHost'+i+'0" class="inputbox mediumInput validate[required]" />'+addLink+'</div></div></div></div></fieldset></div>';
				}
				$('#slaveFieldBox').html(slaveHtml);
				$('#copySlaveMasterBox').show('slow');
			}
			else
			{
				$('#copySlaveMasterBox').hide('slow');
				$('#slaveFieldBox').html('');
				
			}
		});


		// no. of fields field onblur code
		$('#noOfFieldsBtn').click(function (){			
			var dataValidationFieldCount = $('#noOfFields').val();
			if(dataValidationFieldCount)
			{
				var dataValidationFieldHtml="";
				var defaultVal = 'onfocus="javascript:removeTxtOnFocus(this);" onblur="javascript:addTxtOnBlur(this);" value="&nbsp;"';

				for(var i=0; i<=dataValidationFieldCount-1; i++)
				{
					var fieldCount = parseInt(i)+parseInt(1);					
					
					dataValidationFieldHtml +='<div class="commonBox clear borderBottom"><div class="fixWidthValidationCheckBox"  style="text-align: center;">'+fieldCount+'</div><div class="fixWidthValidationCheckBox" style="display:none;"><div class="fld"><input type="hidden" name="dataValidation.fieldValidationList['+i+'].fieldNumber" id="fieldNumber'+i+'" value="'+i+'" checked/></div></div><div id="extraDataValidationRow'+i+'" class="" ><div class="fixWidthValidationBox"><div class="fld"><select name="dataValidation.fieldValidationList['+i+'].nullCheck" id="nullCheck'+i+'"><option value="&nbsp;">Please Select</option><option value="mustBeNull">must be null</option><option value="notNull">must not be null</option></select></div></div><div class="fixWidthValidationBox" style="display:none;"><div class="fld"><select name="dataValidation.fieldValidationList['+i+'].dataType" id="dataType'+i+'" class="inputboxes"><option value="&nbsp;">Please Select</option><option value="int_type">int</option><option value="float_type">float</option><option value="byte_type">byte</option><option value="char_type">char</option><option value="boolean_type">boolean</option><option value="double_type">double</option><option value="other">other</option></select></div></div><div class="fixWidthValidationBox" style="display:none;"><div class="fld"><input type="text" disabled name="dataValidation.fieldValidationList['+i+'].regex" id="dataValidationRegex'+i+'" class="inputbox" '+defaultVal+'/></div></div></div></div></div>';
				}
				
				$('#dataValidationFieldBox').html(dataValidationFieldHtml);
				$('#extraDataValidationHeaderBox').show();
			}
			else
			{				
				$('#dataValidationFieldBox').html('');
				$('#extraDataValidationHeaderBox').hide();				
			}
		});

		
		
		// fieldSeparator field onblur code
		$('#fieldSeparator').blur(function (){
			var fieldSeparatorValue = $(this).val();
			if(fieldSeparatorValue)
			{
				$('#noOfFieldsBox').show('slow');
			}
			else
			{
				$('#noOfFieldsBox').hide('slow');
			}
		});

		// debugger if block click event code
		$("input[name='debuggerConf.logLevel.ifblock']").click(function (){
			var ifBlockCheck = $("input[name='debuggerConf.logLevel.ifblock']").prop('checked');		
			if(ifBlockCheck)
			{
				$('#ifBlockNestingLevel').show('slow');
			}
			else
			{
				$('#ifBlockNestingLevel').hide('slow');
			}
		
		});
	
		// instrument regex select box onchange event code
		$("input[name='debuggerConf.logLevel.instrumentRegex']").click(function (){
			var instrumentRegexCheck = $('#instrumentRegexTrue').prop('checked');		
			if(instrumentRegexCheck)
			{
				$('#instrumentRegexField input[type="text"]').removeAttr("disabled");
				$('#instrumentRegex').attr("value", "TRUE");				
			}
			else
			{
			    $('#instrumentRegexField input[type="text"]').attr("disabled", true);	
			    $('#instrumentRegex').attr("value", "FALSE");			
			}
		
		});
		
		// instrument user defind validation select box onchange event code		
		$("input[name='debuggerConf.logLevel.instrumentUserDefValidate']").click(function (){
			var instrumentUserDefValidateCheck = $('#instrumentUserDefValidate').prop('checked');											
			if (instrumentUserDefValidateCheck) {											
					$('#instrumentUserDefValidateField input[type="text"]').removeAttr("disabled");
					$("#instrumentUserDefValidate").attr("value", "TRUE");
			} else {												
					$('#instrumentUserDefValidateField [type="text"]').attr("disabled", true);													
					$("#instrumentUserDefValidate").attr("value", "FALSE");
			}
			
		});

		// partitioner select box onchange event code		
		$("input[name='debuggerConf.logLevel.partitioner']").click(function (){
			
			var partitionerCheck = $('#partitionerTrue').prop('checked');			
			if(partitionerCheck == true)
			{
				$('#partitionerFieldBox').show();
			}
			else
			{
				$('#partitionerFieldBox').hide();
			}
		
		});


		// jarInputType select box onchange event code		
		$('#jarInputType').change(function (){
			var jarInputTypeVal = $(this).val();			
			if(jarInputTypeVal=='2')
			{
				$('#machineClassPathMainBox').html('<div id="localMachineClassPathBox" class="fixWidthBox"> <div class="lbl"> <label>Local machine path</label> </div> <div class="fld"> <textarea class="inputbox" name="classpath.userSupplied.folder" id="localMachineClassPath"></textarea></div></div>');
				$('#userSuppliedResource').show('slow');
				$('#userSuppliedExclude').show('slow');
				
			}
			else if(jarInputTypeVal=='3')
			{				
				$('#machineClassPathMainBox').html('<div id="slaveMachineClassPathBox" class="fixWidthBox"> <div class="lbl"> <label>Slave machine path</label> </div> <div class="fld"> <textarea class="inputbox" name="classpath.userSupplied.folder" id="slaveMachineClassPath"></textarea></div></div>');
				$('#userSuppliedResource').show('slow');
				$('#userSuppliedExclude').show('slow');
			}
			else if(jarInputTypeVal=='4')
			{
				
				$('#machineClassPathMainBox').html('<div id="masterMachineClassPathBox" class="fixWidthBox"> <div class="lbl"> <label>Master machine path</label> </div> <div class="fld"> <textarea class="inputbox" name="classpath.userSupplied.folder" id="masterMachineClassPath"></textarea></div></div>');
				$('#userSuppliedResource').show('slow');
				$('#userSuppliedExclude').show('slow');				
			}
			else
			{
				$('#machineClassPathMainBox').html('');
				$('#userSuppliedResource').hide('slow');
				$('#userSuppliedExclude').hide('slow');
			}
			
		
		});
		

		// enable profile checkbox click event code
		$('#localMachinePath').click(function(){
			
			if($('#localMachinePath').prop('checked')==true)
			{				
				$('#localMachinePathFieldBox').html('<div class="fleft">&nbsp;</div><div class="fld"><label class="fleft">Browse local machine path</label>&nbsp;<input type="file" name="inputFile" id="localMachineFile" size="11" class="inputbox"/></div>');

				$('#masterMachinePathFieldBox').html('');
			}			
		
		});

		// enable profile checkbox click event code
		$('#masterMachinePath').click(function(){ 
			
			if($('#masterMachinePath').prop('checked')==true)
			{				
				$('#localMachinePathFieldBox').html('');

				$('#masterMachinePathFieldBox').html('<div class="fleft">&nbsp;</div><div class="fld"><label class="fleft">Master machine path</label>&nbsp;<input type="text" name="inputFile" id="masterMachineFile" class="inputbox"/></div>');
			}			
		
		});

		// enable debuger checkbox click event code
		$('#debugAnalysis').click(function(){
			
			if($('#debugAnalysis').prop('checked')==true)
			{				
				$('#debugAnalysisBox').show('slow');
			}
			else
			{				
				$('#debugAnalysisBox').hide('slow');
			}
		
		});
		

		// enable profile checkbox click event code
		$('#hadoopJobProfile').click(function(){
			
			if($('#hadoopJobProfile').prop('checked')==true)
			{				
				$('#ProfileFieldsBox').show('slow');
			}
			else
			{				
				$('#ProfileFieldsBox').hide('slow');
			}
		
		});

		// enable validation checkbox click event code
		$('#enableValidation').click(function(){

			if($('#enableValidation').prop('checked')==true)
			{				
				$('#validationFieldsBox').show('slow');
			}
			else
			{				
				$('#validationFieldsBox').hide('slow');
			}
		
		});


		
		

		// copySlaveMatser checkbox click event code
		$('#copySlaveMaster').click(function(){

			
			var masterUser = $('#user').val();
			var masterHost = $('#host').val();

			var slaveFieldCount  = $('body').find("input[name^='slaves']").length;
			
			if($('#copySlaveMaster').prop('checked')==true)
			{
				for(var i=0; i<=slaveFieldCount-1; i++)
				{					
					$('#slaveUser'+i).val(masterUser);
					$('#slaveHost'+i).val(masterHost);			
				}
			}
			else
			{
				for(var i=0; i<=slaveFieldCount-1; i++)
				{
					$('#slaveUser'+i).val('');
					$('#slaveHost'+i).val('');			
				}
			}
		
		});

		// data validation field hide/show
		$('body').find("input[id^='fieldNumber']").live('click',function(){
			
			var inputId = $(this).val();
			
			if($('#fieldNumber'+inputId).prop('checked')==true)
			{
				$('#extraDataValidationRow'+inputId).show('slow');
			}
			else
			{
				//$('#extraDataValidationRow'+inputId).hide('slow');
				$('#extraDataValidationRow'+inputId).parent('.commonBox').remove(); 
			}			

			
			$('#nullCheck'+inputId).live('change', function (){
				var nullCheckVal = $(this).val();	
				
				if(nullCheckVal=='notNull')
				{
					$('#dataType'+inputId).closest('.fixWidthValidationBox').show('slow');
					$('#dataValidationRegex'+inputId).closest('.fixWidthValidationBox').show('slow');					
				}
				else
				{
					$('#dataType'+inputId).closest('.fixWidthValidationBox').hide('slow');
					$('#dataValidationRegex'+inputId).closest('.fixWidthValidationBox').hide('slow');	
				}
				
		
			});

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
			
			//$(this).parents('.commonBox').remove();
			$(this).parent().parent().parent().remove();  		

		});
	  
		
		// Smart Wizard        
        $('#wizard').smartWizard({transitionEffect:'slideleft', keyNavigation: false, enableAllSteps: true, onLeaveStep:leaveAStepCallback, onShowStep:checkStepNumber, onFinish:onFinishCallback});

		//$('a.buttonFinish').addClass('buttonDisabled');
 
      function leaveAStepCallback(obj){
        var step_num= obj.attr('rel'); // get the current step number	
        return validateSteps(step_num); // return false to stay on step and true to continue navigation
      }
       
	  function checkStepNumber(obj){
        var step_num= obj.attr('rel'); // get the current step number	
		if(step_num == 7)
		  {
			$('#yaml-dialog-modal .actionBar a').hide('slow');
			if($('#yaml-dialog-modal .actionBar').find('a#validateWizardBtn').text() == "")
			{
				$('#yaml-dialog-modal .actionBar').append('<a id="validateWizardBtn"  style= "display:none" class="buttonPrevious validateWizard" href="#">Validate</a>');
				$('#yaml-dialog-modal .actionBar').append('<a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');
				console.log("run append 2");
			}
		  }
		  else
		  {
			  $('#yaml-dialog-modal .actionBar a#validateWizardBtn').remove();
			  $('#yaml-dialog-modal .actionBar a#runWizardBtn').remove();			  
			  $('#yaml-dialog-modal .actionBar a').show('slow');
		  }		
        return; // return false to stay on step and true to continue navigation
      }

      function onFinishCallback(obj){
		 
       if(validateAllSteps(obj))
		{	
			$('#previewTab').show();
			$('#previewTab a').trigger('click');  
			//$('#yaml-dialog-modal .actionBar a').hide('slow');			
			/*if($('#yaml-dialog-modal .actionBar').find('a#validateWizardBtn').text() == "")
			{
				$('#yaml-dialog-modal .actionBar').append('<a id="validateWizardBtn" class="buttonPrevious validateWizard" href="javascript:void(0);">Validate</a>');
			}*/

			var formData = form2js('yamlForm', '.', true,
			function(node)
			{
				if (node.id && node.id.match(/callbackTest/))
				{
					return { name: node.id, value: node.innerHTML };
				}
			});
						
			//console.log(formData);
			var finalJson = JSON.stringify(formData, null, '\t');
			var regExp=/[\\]+/g;
			finalJson = finalJson.replace(regExp, "\\");	
			
			//data preview function
			previewData(obj);
			
			//console.log("Final Json " + finalJson);	
			$('#yamlJsonData').val(finalJson);

			return false;
			//$('#yamlForm').submit();	
			
		}
	   
      }	
	  
	  function previewData(obj)
		{
		  $('#previewInputBox').html('');
		  $('#previewLabelBox').html('');
		  $('.previewInfo').find('div.fld :input').each(function() {		   
			 
				if(($(this).attr("type") == 'checkbox' || $(this).attr("type") == 'radio') && ($(this).prop('checked') == true))
				{				
					$('#previewInputBox').append('<div class="commonBox">'+$(this).val()+'</div>'); 				
					$('#previewLabelBox').append('<div class="commonBox">'+$(this).parents('.previewInfo').find('div.lbl').text()+'</div>');	
				}
				else if(($(this).attr("type") == 'text' || $(this).attr("type") == 'file') && ($(this).val() != ""))
				{
					$('#previewInputBox').append('<div class="commonBox">'+$(this).val()+'</div>'); 				
					$('#previewLabelBox').append('<div class="commonBox">'+$(this).parents('.previewInfo').find('div.lbl').text()+'</div>');	
				}
			});	

		}	 

	 $('#validateWizardBtn').live('click', function (){
		 var jsonData = $('#yamlJsonData').val();

			$.ajax({
			  type: "POST",
			  url: "",
			  data: jsonData
			}).done(function( jsonData ) {
				
				//var jsonData = '{ "Suggestions":{ "Job Profiling":[ "Job Profiling-States interval should be greater than 2000", "Job Profiling- Job profiling is not enabled" ], "Data Validation":[ "DataValidation-Field Seprator - field seprator contain spaces ", "DataValidation-Field Seprator - field seprator contain spaces ", "DATA VALIDATION- you did not select data validation check" ], "Jobs Validation":[ "Jobs - field \u00272\u0027 job jar class field is blank" ] }, "Failures":{ "Job Profiling":[ "Job Profiling-Masteer Node jmx port is invalid ", "Job Profiling-Data node Jmx port is invalid ", "mapper string contains spaces in Profiling" ], "Data Validation":[ "Data Validation - hadoop input path direcstory does not exist" ], "Jobs Validation":[ "Jobs - field \u00271\u0027 job jar name contain spaces", "Jobs - field \u00275\u0027 job jar name field can not be left blank" ], "Home Validation":[ "hadoop home- directory does not exist", "hadoop Home- Directory name must be started and ends with /", "sJumbuneHome -Directory name must ends with /", "Master Host- ip address is not valid" ] } }';

				//var jsonData = JSON.stringify(jsonData);
				var jsonData = '';
							
				if(typeof jsonData != 'undefined' && jsonData.length > 2)
				{

					var parsedJsonData = JSON.parse(jsonData);
					var suggestString = "";
					var ErrorString = "";
					
					$('#validateMsgBox').html('');
					
				
					$.each(parsedJsonData, function(key, value) { 	
						
					  
					  if(key == "Failures")
						{

							$.each(value, function(suggestKey, suggestValue) {
								
								$.each(suggestValue, function(suggestDataKey, suggestDataValue) {
									
									ErrorString += '<div class="validateMsg">'+suggestDataValue+'</div>';			

								});

							});

							$('#validateMsgBox').show('slow').append('<div class="status error"><span>Failures: </span>'+ErrorString+'</div>');  
							  $('#yaml-dialog-modal .actionBar a#runWizardBtn').remove();							  
							//  $('#yaml-dialog-modal .actionBar a#validateWizardBtn').show('slow');		  
							  console.log("appended run1");
							  $('#yaml-dialog-modal .actionBar').append('<a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');

							
						}
					else if(key == "Suggestions")
						{

							$.each(value, function(suggestKey, suggestValue) {
								console.log(suggestKey+", "+suggestValue);
								$.each(suggestValue, function(suggestDataKey, suggestDataValue) {

									suggestString += '<div class="validateMsg">'+suggestDataValue+'</div>';								

								});

							});

							$('#validateMsgBox').show('slow').append('<div class="status info"><span>Suggestions: </span>'+suggestString+'</div>');
							$('#yaml-dialog-modal .actionBar a').hide('slow');
							//$('#yaml-dialog-modal .actionBar').append('<a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');

						}
					  
					})
					return;
				}
				else
				{
					$('#validateMsgBox').show('slow').append('<div class="status success"><span>Information: </span>Validate Successfully.</div>');
					$('#yaml-dialog-modal .actionBar a').hide('slow');
					//$('#yaml-dialog-modal .actionBar').append('<a id="runWizardBtn" class="buttonPrevious" href="javascript:void(0);">Run</a>');
				}
				
			});
	  });

	  $('#runWizardBtn').die("click").live('click', function (){
			$('#yamlForm').submit();	
	  });	  
	  
       
      // Your Step validation logic
      function validateSteps(step){
        var isStepValid = true;

		// validate step 1
		  if(step == 1){
			if(validateStep1() == false ){
			  isStepValid = false;			  
			  $('#wizard').smartWizard('showMessage','Please correct the errors in step'+step+ ' and click next.');
			  $('#wizard').smartWizard('setError',{stepnum:step,iserror:true});         
			}else{			  
			  $('#wizard').smartWizard('setError',{stepnum:step,iserror:false});
			}
		  } 
		 
		 // validate step 2
		  if(step == 2){
			if(validateStep2() == false ){
			  isStepValid = false; 
			  $('#wizard').smartWizard('showMessage','Please correct the errors in step'+step+ ' and click next.');
			  $('#wizard').smartWizard('setError',{stepnum:step,iserror:true});         
			}else{			  
			  $('#wizard').smartWizard('setError',{stepnum:step,iserror:false});
			}
		  } 
		
		return isStepValid;
      }
      function validateAllSteps(obj){  
		//var isStepValid = true;
		var step_num= obj.attr('rel'); // get the current step number	
		   
        // all step validation logic    
        return validateSteps(step_num);
      }  
	  
		
	  // validate step 1 function
	  function validateStep1(){       
		var isValid = false;
		
		if($("#yamlForm").validationEngine('validate') == true){
			isValid = true;					
		}		    
		
       return isValid;
    }

	// validate step 1 function
	  function validateStep2(){
      var isValid = false;

	  if($("#yamlForm").validationEngine('validate') == true){
			isValid = true;					
		}
       
       return isValid;
    }	
	



	
	function populateForm()
	{
		var popData = JSON.stringify(eval(<%=request.getParameter("populateData")%>));
		data = JSON.parse(popData);
		js2form(document.getElementById('yamlForm'), data);
	}
	populateForm();      
            	
</script>

