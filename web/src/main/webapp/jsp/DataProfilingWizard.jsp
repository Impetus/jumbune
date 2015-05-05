<%@page import="java.util.Properties"%>
<%@page import="java.io.InputStream"%>

<body>

<script>
(function($){

$.fn.smartWizard = function(method) {
    var args = arguments;
    var rv = undefined;
    var allObjs = this.each(function() {
        var wiz = $(this).data('smartWizard');
        if (typeof method == 'object' || ! method || ! wiz) {
            var options = $.extend({}, $.fn.smartWizard.defaults, method || {});
            if (! wiz) {
                wiz = new SmartWizard($(this), options);
                $(this).data('smartWizard', wiz);
            }
        } else {
            if (typeof SmartWizard.prototype[method] == "function") {
                rv = SmartWizard.prototype[method].apply(wiz, Array.prototype.slice.call(args, 1));
                return rv;
            } else {
                $.error('Method ' + method + ' does not exist on jQuery.smartWizard');
            }
        }
    });
    if (rv === undefined) {
        return allObjs;
    } else {
        return rv;
    }
};

// Default Properties and Events
$.fn.smartWizard.defaults = {
    selected: 0,  // Selected Step, 0 = first step
    keyNavigation: true, // Enable/Disable key navigation(left and right keys are used if enabled)
    enableAllSteps: false,
    transitionEffect: 'fade', // Effect on navigation, none/fade/slide/slideleft
    contentURL:null, // content url, Enables Ajax content loading
    contentCache:true, // cache step contents, if false content is fetched always from ajax url
    cycleSteps: false, // cycle step navigation
    enableFinishButton: false, // make finish button enabled always
	hideButtonsOnDisabled: true, // when the previous/next/finish buttons are disabled, hide them instead?
    errorSteps:[],    // Array Steps with errors
    labelNext:'Next',
    labelPrevious:'Previous',
    labelFinish:'Run',
    noForwardJumping: false,
    onLeaveStep: null, // triggers when leaving a step
    onShowStep: null,  // triggers when showing a step
    onFinish: null  // triggers when Finish button is clicked
};

})(jQuery);
</script>

<style>
#validationFieldsBox .fixWidthValidationCheckBox {
	width:98px;
}
</style>


	<%
		InputStream stream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("distributionInfo.properties");
		Properties props = new Properties();
		props.load(stream);
		String hadoopDistValue = props.getProperty("HadoopDistribution");
	%>
<div id="yaml-dialog-modal" class="commonBox">

	<form method="POST" action="DataQualityServlet" name="yamlForm"
		id="yamlForm" enctype="multipart/form-data">
		<div id="wizard" class="swMain">
			<ul>
				<li><a href="#step-1"><label class="stepNumber">Basic</label>
				</a>
				</li>
				<li class="stepLine"></li>

				<li><a href="#step-4"><label class="stepNumber">Data Profiling</label>
				</a>
				</li>
					
			    <li class="stepLine"></li>
				<li><a href="#step-5"><label class="stepNumber">Data Quality Timeline</label>
				</a>
				</li>
				
				<li style="width: 12px;">&nbsp;</li>

				
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
								<input type="text" name="master.host" id="host" class="inputbox mediumInput" maxlength="15" />
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
								<input type="text" name="master.agentPort" id="agentPort" class="inputbox smallInput" />
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

			
			<div id="step-4" class="step-content">

				<div class="status note">
					<span>Note: </span>Tab collects Data Profiling information.
				</div>
				
				<div class="fieldsetBox innerFieldsetBox clear">
					<fieldset>

						<div id="validationFieldsBox">

							<div class="fixWidthBox previewInfo">
								<div class="lbl">
									<label>HDFS Input Path</label>
								</div>
								<div class="fld">
									<input type="text" name="hdfsInputPath" id="hdfsInputPath"
										class="inputbox" previewText="HDFS input path is set."/>
								</div>
							</div>

							<div class="fixWidthBox">
								<div class="lbl">
									<label>Tuple Record Separator</label>
								</div>
								<div class="fld">
									<input type="text" name="dataProfilingBean.recordSeparator"
										id="recordSeparator" class="inputbox"/>
								</div>
							</div>

							<div class="fixWidthBox">
								<div class="lbl">
									<label>Tuple Field Separator</label>
								</div>
								<div class="fld">
									<input type="text" name="dataProfilingBean.fieldSeparator"
										id="fieldSeparator" class="inputbox"/>
								</div>
							</div>
							
						<div class="commonBox previewInfo">
							<div class="fld chk">
								<input type="checkbox" name="enableDataProfiling" value="TRUE"
									id="enableDataProfiling"/>
							</div>
							<div class="">
								<label>Enable Data Profiling</label>
							</div>
						</div>
						
						<div class="commonBox fld">
                            <div class="lbl" style="padding: 0px; width: 110px;">
                           <label>Choose system</label>
                   </div>
                     <div class="fld">
                      <input id="criteria" type="radio" value="TRUE" name="criteriaBasedDataProfiling" onclick=performAction(this.value)>
                   <label for="criteria">Criteria Based</label>
                    <input id="noCriteria" type="radio" value="FALSE" name="criteriaBasedDataProfiling" onclick=performAction(this.value) checked>
                       <label for="noCriteria">No Criteria</label>
					</div>
				</div>
                             
							<div id="noOfFieldsBox" class="fixWidthBoxFull" >
								<div class="lbl">
									<label>No. of fields</label>
								</div>
								<div class="fld">
									<input type="text" name="dataProfilingBean.numOfFields"
										id="noOfFields" class="inputbox smallInput" disabled value="1"/>&nbsp;<a
										href="javascript:void(0);" id="noOfFieldsBtn" class="addSign">Add</a>
								</div>
							</div>
							<div id="extraDataValidationHeaderBox"
								class="commonBox bold borderBottom" style="display: none;">
								<div class="fixWidthValidationCheckBox" style="text-align:center;">Enable Row</div>
								<div class="fixWidthValidationCheckBox" style="text-align:center;">Field Number</div>
								<div class="fixWidthValidationBox" style="text-align: center;">Check Type</div>
								<div class="fixWidthValidationBox" style="text-align: center;">Comparison Value</div>
							</div>
							
							<div id="dataValidationFieldBox"></div>

						</div>
			
	</fieldset>
				</div>
             </div>
             
         
         
        <!-- 
        ----------------------------Data Quality Timeline Ends here---------------------------------------------------------- 
        --> 
         <div id="step-5" class="step-content">

				<div class="status note">
					<span>Note: </span>Tab collects mapreduce job jar and dependent
					jar information..........
				</div>
				
				<div class="fieldsetBox innerFieldsetBox clear">
					<fieldset>
                           
                           
                       	<div class="commonBox previewInfo">
							<div class="fld chk">
								<input type="checkbox" name="enableDataQualityTimeline" value="FALSE"
									id="enableDataQualityTimeline"/>
							</div>
							<div class="">
								<label>Enable Quality Timeline</label>
							</div>
						</div>                           
                            
              
                        <div class="commonBox fld" id="schedulingOptions">
						    	<div class="fld">
							          	<input type="radio" name="dataQualityTimeLineConfig.removeJob"
										id="schedule" value="FALSE"/>			   
								   <label>Schedule</label>
    						   <input type="radio" name="dataQualityTimeLineConfig.removeJob"
										id="removeSchedule" value="FALSE"/>							  
								   <label>Remove</label>
								 <input type="radio" name="dataQualityTimeLineConfig.removeJob"
										id="showResult" value="FALSE"/>							  
								   <label>Show Result</label> 
								  <input type="hidden" id="showResultHidden" name="dataQualityTimeLineConfig.showJobResult"/> 
							   </div>
							   							   
						</div>
								
								<div id="schedulingFields" style="display:none;"> 
								
									
								
				  			<div id="validationFieldsBox">
                         
                   <div class="clear"> </div>
				  	<div class="commonBox fld">
				  				<div class="lbl" style="padding: 0px; width: 110px;" >
									<label><b>Schedule Job</b></label>
				  				</div>
				  				<div class="fld">
								<input type="radio" name="dataQualityTimeLineConfig.enableCronExpression" checked="true" id="withoutCron" value="FALSE" />
                                <label>Specify Time</label>
                                <input type="radio" name="dataQualityTimeLineConfig.enableCronExpression" id="withCron" value="TRUE" />
                            	 <label>Cron Expression</label>
                            	</div>
                                
					</div>
					<!--div class="fixWidthBox"></div-->
                    <!--div class="clear"> </div-->
                    
                    <div id="cronInput">
                    	
					</div>                         
					
					<div class="clear"> </div>                        
							<div class="fixWidthBox">
								<div class="lbl">
									<label>HDFS Input Path</label>
								</div>
								<div class="fld">
									<input type="text" name="hdfsInputPath" id="hdfsInputPath"
										class="inputbox" previewText="HDFS input path is set."/>
								</div>
							</div>

							<div class="fixWidthBox">
								<div class="lbl">
									<label>Tuple Record Separator</label>
								</div>
								<div class="fld">
									<input type="text" name="dataValidation.recordSeparator"
										id="recordSeparator" class="inputbox"/>
								</div>
							</div>

							<div class="fixWidthBox">
								<div class="lbl">
									<label>Tuple Field Separator</label>
								</div>
								<div class="fld">
									<input type="text" name="dataValidation.fieldSeparator"
										id="fieldSeparator" class="inputbox"/>
								</div>
							</div>

							<div id="noOfFieldsBoxDQT" class="fixWidthBoxFull" >
								<div class="lbl">
									<label>No. of fields</label>
								</div>
								<div class="fld">
									<input type="text" name="dataValidation.numOfFields"
										id="noOfFieldsDQT" class="inputbox smallInput"  value="1"/>&nbsp;<a
										href="javascript:void(0);" id="noOfFieldsBtnDQT" class="addSign">Add</a>
								</div>
							</div>

							<div id="extraDataValidationHeaderBoxDQT"
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

							<div id="dataValidationFieldBoxDQT"></div>


							

						</div>

						
			 </div>	
                  	<div id="removeSchedulingFields" style="display:none;"> 
                         <div class="fixWidthBox">
								<div class="lbl">
									<label>Jumbune Job ID</label>
								</div>
								<div class="fld">
									<input type="text" name="dataQualityTimeLineConfig.jobName"
										id="remove" class="inputbox"/>
								</div>
							</div>
                                                             	
                             	</div>			 
			 
			 
			 
	</fieldset>
				</div>
             </div>

      <!--
         ----------------------------Data Quality Timeline Ends here----------------------------------------------------------
       -->  
             
             

			

		


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
$(document).ready(function(){
    $("#withCron").click(function(){
    	 $(this).attr('value', this.checked ? "TRUE": "FLASE");
    	$('#cronInput').html('<div class="fixWidthBox"><div class="lbl"><label>Cron Expression</label></div> <div class="fld"><input type="text" name="dataQualityTimeLineConfig.cronExpression" id="cronExpression" class="inputbox" /></div></div>');
    });
    $("#withoutCron").click(function(){
    	 $(this).attr('value', this.checked ? "FALSE": "TRUE");
    	$('#cronInput').html('<div class="fixWidthBox"> <div class="lbl"><label>Time</label> </div><div class="fld"> <input type="text" name="dataQualityTimeLineConfig.time" id="time" class="inputbox hasDatePicker"/></div></div><div class="fixWidthBox"> <div class="lbl"> <label>Repeat Every</label>  	</div>								    <div class="fld-interval-dropdown"> <select name="dataQualityTimeLineConfig.interval" id = "dQTInterval" value=1 class ="inputbox" ><option value=1 >1</option></select>	</div> <div class="fld-duration-dropdown"> <select name="dataQualityTimeLineConfig.schedulingEvent" id="duration" class="inputbox"> <option value="MINUTE">Minute</option> <option value="HOURLY">Hour</option><option value="DAILY">Day</option><option value="WEEKLY">Week</option><option value="MONTHLY">Month</option><option value="YEARLY">Year</option></select>  </div></div> ');
    	$('#time').datetimepicker({
    		dateFormat : 'mm/dd/yy',
    		timeFormat: 'hh:mm:ss' 
    	});
    	$( '#duration' ).click(function() {

	 if ($('#duration').find('option:selected').text() == 'Hour') {
        $('#dQTInterval').prop('disabled', false);
        $('#dQTInterval').html("<option value=1 >1</option>           <option value=2 >2</option><option value=3 >3</option>           <option value=4 >4</option><option value=5 >5</option>           <option value=6 >6</option><option value=7 >7</option>           <option value=8 >8</option><option value=9 >9</option>           <option value=10 >10</option><option value=11 >11</option>           <option value=12 >12</option><option value=13 >13</option>           <option value=14 >14</option><option value=15 >15</option>           <option value=16 >16</option><option value=17 >17</option>           <option value=18 >18</option><option value=19 >19</option>           <option value=20 >20</option><option value=21 >21</option>           <option value=22 >22</option><option value=23 >23</option>           <option value=24 >24</option>");
        
    }
    if ($('#duration').find('option:selected').text() == 'Day') {
      $('#dQTInterval').prop('disabled', false);
      $('#dQTInterval').html("<option value=1 >1</option>           <option value=2 >2</option><option value=3 >3</option>           <option value=4 >4</option><option value=5 >5</option>           <option value=6 >6</option><option value=7 >7</option>           <option value=8 >8</option><option value=9 >9</option>           <option value=10 >10</option><option value=11 >11</option>           <option value=12 >12</option><option value=13 >13</option>           <option value=14 >14</option><option value=15 >15</option>           <option value=16 >16</option><option value=17 >17</option>           <option value=18 >18</option><option value=19 >19</option>           <option value=20 >20</option><option value=21 >21</option>           <option value=22 >22</option><option value=23 >23</option>           <option value=24 >24</option><option value=25 >25</option>           <option value=26 >26</option><option value=27 >27</option>           <option value=28 >28</option><option value=29 >29</option>           <option value=30 >30</option><option value=31 >31</option>");
    }  
    if ($('#duration').find('option:selected').text() == 'Minute') {
        $('#dQTInterval').prop('disabled', true);
        $('#dQTInterval').html("<option>1</option>");  
    	}  
       if ($('#duration').find('option:selected').text() == 'Week') {
       $('#dQTInterval').prop('disabled', true);
       $('#dQTInterval').html("<option>1</option>"); 
    }  
       if ($('#duration').find('option:selected').text() == 'Month') {
        $('#dQTInterval').prop('disabled', true);
        $('#dQTInterval').html("<option>1</option>"); 
    }  
       if ($('#duration').find('option:selected').text() == 'Year') {
        $('#dQTInterval').prop('disabled', true);
        $('#dQTInterval').html("<option>1</option>"); 
    }  
    
	
});
    });
});

$( '#duration' ).click(function() {

	 if ($('#duration').find('option:selected').text() == 'Hour') {
        $('#dQTInterval').prop('disabled', false);
        $('#dQTInterval').html("<option>1</option>           <option>2</option><option>3</option>           <option>4</option><option>5</option>           <option>6</option><option>7</option>           <option>8</option><option>9</option>           <option>10</option><option>11</option>           <option>12</option><option>13</option>           <option>14</option><option>15</option>           <option>16</option><option>17</option>           <option>18</option><option>19</option>           <option>20</option><option>21</option>           <option>22</option><option>23</option>           <option>24</option>");
        
    }
    if ($('#duration').find('option:selected').text() == 'Day') {
      $('#dQTInterval').prop('disabled', false);
      $('#dQTInterval').html("<option>1</option>           <option>2</option><option>3</option>           <option>4</option><option>5</option>           <option>6</option><option>7</option>           <option>8</option><option>9</option>           <option>10</option><option>11</option>           <option>12</option><option>13</option>           <option>14</option><option>15</option>           <option>16</option><option>17</option>           <option>18</option><option>19</option>           <option>20</option><option>21</option>           <option>22</option><option>23</option>           <option>24</option><option>25</option>           <option>26</option><option>27</option>           <option>28</option><option>29</option>           <option>30</option><option>31</option>");
    }  
    if ($('#duration').find('option:selected').text() == 'Minute') {
        $('#dQTInterval').prop('disabled', true);
        $('#dQTInterval').html("<option>1</option>");  
    	}  
       if ($('#duration').find('option:selected').text() == 'Week') {
       $('#dQTInterval').prop('disabled', true);
       $('#dQTInterval').html("<option>1</option>"); 
    }  
       if ($('#duration').find('option:selected').text() == 'Month') {
        $('#dQTInterval').prop('disabled', true);
        $('#dQTInterval').html("<option>1</option>"); 
    }  
       if ($('#duration').find('option:selected').text() == 'Year') {
        $('#dQTInterval').prop('disabled', true);
        $('#dQTInterval').html("<option>1</option>"); 
    }  
    
	
});

document.getElementById('enableDataQualityTimeline').onclick = function() {
  $(this).attr('value', this.checked ? "TRUE": "FALSE");
   if ( this.checked ) {
$("#schedulingOptions").show();
   }
else {
$("#schedulingOptions").hide();

}   


}

document.getElementById('schedule').onclick = function() {
  $(this).attr('value', this.checked ? "FALSE": "TRUE")
    if ( this.checked ) {
    	$("#withoutCron").click();
     $("#removeSchedulingFields").find('input:text').val('');
    $("#removeSchedulingFields").attr("style","display:none;")
  $("#schedulingFields").removeAttr("style")
   $("#showResultHidden").removeAttr('value');
  $('#noOfFieldsBtnDQT').click();
    } else {
   $("#schedulingFields").attr("style","display:none;")
    }
};

document.getElementById('removeSchedule').onclick = function() {
  $(this).attr('value', this.checked ? "TRUE": "FALSE")
    if ( this.checked ) {
    $("#schedulingFields").find('input:text').val('');
    $("#schedulingFields").hide()
    $("#showResultHidden").removeAttr('value');
  //  $("#schedulingFields select").empty().append('');
  $("#removeSchedulingFields").removeAttr("style")  
    } else {
   $("#removeSchedulingFields").attr("style","display:none;")
    }
};

document.getElementById('showResult').onclick = function() {
  $(this).attr('value', this.checked ? "FALSE": "TRUE")
    if ( this.checked ) {
    $("#schedulingFields").find('input:text').val('');
    $("#schedulingFields").hide()
    $("#removeSchedulingFields").show()
    $("#showResultHidden").attr('value',"TRUE")
  
  $("#removeSchedulingFields").removeAttr("style")  
    } else {
   $("#removeSchedulingFields").attr("style","display:none;")
    }
};




				$("#time").datetimepicker({
							dateFormat : 'mm/dd/yy',
							timeFormat: 'hh:mm:ss' 
						});

          

$('#noOfFieldsBtnDQT')
								.click(
										function() {
											var dataValidationFieldCount = $(
													'#noOfFieldsDQT').val();
											if (dataValidationFieldCount) {
												var dataValidationFieldHtml = "";

												for ( var i = 0; i <= dataValidationFieldCount - 1; i++) {
													var fieldCount = parseInt(i)
															+ parseInt(1);

													dataValidationFieldHtml += '<div class="commonBox clear borderBottom"><div class="fixWidthValidationCheck"><input type="checkbox" id="validationChk'+i+'"/></div><div class="fixWidthValidationCheckBox disabledRow" style="text-align: center;">'
															+ fieldCount
															+ '</div><div class="fixWidthValidationCheckBox disabledRow" style="display:none;"><div class="fld"><input disabled type="text" name="dataValidation.fieldValidationList['+i+'].fieldNumber" id="fieldNumber'+i+'" value="'+i+'"/></div></div><div id="extraDataValidationRow'+i+'" class=""><div class="fixWidthValidationBox disabledRow"><div class="fld"><select name="dataValidation.fieldValidationList['+i+'].nullCheck" id="nullCheck'+i+'" disabled><option value="&nbsp;">Please Select</option><option value="mustBeNull">must be null</option><option value="notNull">must not be null</option></select></div></div><div class="fixWidthValidationBox disabledRow"><div class="fld"><select disabled name="dataValidation.fieldValidationList['+i+'].dataType" id="dataType'+i+'" class="inputboxes"><option value="&nbsp;">Please Select</option><option value="int_type">int</option><option value="float_type">float</option><option value="long_type">long</option><option value="double_type">double</option></select></div></div><div class="fixWidthValidationBox disabledRow"><div class="fld"><input type="text" disabled="true" name="dataValidation.fieldValidationList['+i+'].regex" id="dataValidationRegex'+i+'" class="inputbox" /></div></div></div></div></div>';
												}
												$('#dataValidationFieldBoxDQT')
														.html(
																dataValidationFieldHtml);
												$(
														'#extraDataValidationHeaderBoxDQT')
														.show();
											} else {
												$('#dataValidationFieldBoxDQT')
														.html('');
												$(
														'#extraDataValidationHeaderBoxDQT')
														.hide();
											}
										});

		
						


//----------------------------------------------------------------------------------------------------------------------------
// enable data profiling checkbox click event code
				$('#enableDataProfiling')
								.change(
										function() {
												
											if ($('#enableDataProfiling').prop('checked') == true) {
												$("#criteria").removeAttr("disabled")
												$("#noCriteria").removeAttr("disabled")
												$('#enableDataProfiling').val("TRUE");
											
										//		$('#enableNonRuleBasedDataProfiling').prop('checked', false);
											} else  {
												$("#noCriteria").click();
												$("#criteria").attr("disabled",true)
												$("#noCriteria").attr("disabled",true)
												$('#enableDataProfiling').val("FALSE");
											//	$('#enableNonRuleBasedDataProfiling').prop('checked', true);
												
											}
										});
		
							/*
				$('#input[name='criteriaBasedDataProfiling']')
								.live('change', function() { alert("done.........."); });
										*/
							
							function performAction(val)
							{     if(val=="TRUE")
							     {
									 
									 $("#noOfFields").val(1);
												$('#noOfFieldsBtn').click();
												$("#noOfFields").attr('disabled', false);
								 }
							     else 
							     {
									 $('#validationFieldsBox').find('input[type="text"], select').val('');
												$('#validationFieldsBox').find('input[type="checkbox"]').prop('checked', false);
												$("#noOfFields").attr('disabled', true);
												$(".fixWidthValidationCheck input[type='checkbox']").parent().parent().find(".fixWidthValidationCheckBox, .fixWidthValidationBox").addClass("disabledRow");
												$(".fixWidthValidationCheck input[type='checkbox']").parent().parent().find("input[type='text'], select, input[type='checkbox']").attr("disabled", true);
									 
								 }
							
							
							}
							
							
										/*
											$('#noCriteria')
								.click(
										function() {
												
												$('#validationFieldsBox').find('input[type="text"], select').val('');
												$('#validationFieldsBox').find('input[type="checkbox"]').prop('checked', false);
												$("#noOfFields").attr('disabled', true);
												$(".fixWidthValidationCheck input[type='checkbox']").parent().parent().find(".fixWidthValidationCheckBox, .fixWidthValidationBox").addClass("disabledRow");
												$(".fixWidthValidationCheck input[type='checkbox']").parent().parent().find("input[type='text'], select, input[type='checkbox']").attr("disabled", true);
											} 
										});
									*/
										
									
							// data validation field hide/show
						$('body')
								.find("input[id^='fieldNumber']")
								.live(
										'click',
										function() {

											var inputId = $(this).val();

											if ($('#fieldNumber' + inputId).prop('checked') == true) {
												$('#extraDataValidationRow' + inputId).show('slow');
												$('#dataProfilingOperand' + inputId).closest('.fixWidthValidationBox').show('slow');
												$('#comparisonValue' + inputId).closest('.fixWidthValidationBox').show('slow');
											} else {
												//$('#extraDataValidationRow'+inputId).hide('slow');
												$('#extraDataValidationRow' + inputId).parent('.commonBox').remove();
												$('#dataProfilingOperand' + inputId).closest('.fixWidthValidationBox').hide('slow');
												$('#comparisonValue' + inputId).closest('.fixWidthValidationBox').hide('slow');
											}
										});
										
			// no. of fields field onblur code
			/*
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
															+ '</div><div class="fixWidthValidationCheckBox disabledRow" style="display:none;"><div class="fld"><input disabled type="hidden" name="dataProfilingBean.fieldProfilingRules['+i+'].fieldNumber" id="fieldNumber'+i+'" value="'+i+'"/></div></div><div id="extraDataValidationRow'+i+'" class=""><div class="fixWidthValidationBox disabledRow"><div class="fld"><select name="dataProfilingBean.fieldProfilingRules['+i+'].dataProfilingOperand" id="dataProfilingOperand'+i+'" disabled><option value="&nbsp;">Please Select</option><option value="GREATER_THAN_EQUAL_TO">Greater Than Equal To</option><option value="LESS_THAN_EQUAL_TO">Less Than Equal To</option></select></div></div><div class="fixWidthValidationBox disabledRow"><div class="fld"><select disabled name="dataProfilingBean.fieldProfilingRules['+i+'].comparisonValue" id="comparisonValue'+i+'" class="text">ComparisonValue</div></div>';
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
										
		*/
						$("#noOfFields").keyup(function() {
							var val = $(this).val();
							if ( val == 0 ) {
								$(this).val(1);
							}
						});
						
				
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

												dataValidationFieldHtml += '<div class="commonBox clear borderBottom"><div class="fixWidthValidationCheck"><input type="checkbox" id="validationChk'+i+'" /></div><div class="fixWidthValidationCheckBox disabledRow" style="text-align: center;">'+ fieldCount+ '</div><div class="fixWidthValidationCheckBox disabledRow" style="display:none;"><div class="fld"><input  type="hidden" name="dataProfilingBean.fieldProfilingRules['+i+'].fieldNumber" id="fieldNumber'+i+'" value="'+fieldCount+'"/></div></div><div id="extraDataValidationRow'+i+'" class=""><div class="fixWidthValidationBox disabledRow"><div class="fld"><select name="dataProfilingBean.fieldProfilingRules['+i+'].dataProfilingOperand" id="dataProfilingOperand'+i+'" disabled><option value="&nbsp;">Please Select</option><option value="GREATER_THAN_EQUAL_TO">Greater than or equal to</option><option value="LESS_THAN_EQUAL_TO">Less than or equal to</option></select></div></div><div class="fixWidthValidationBox disabledRow"><div class="fld"><input type="text" disabled="true" name="dataProfilingBean.fieldProfilingRules['+i+'].comparisonValue" id="comparisonValue'+i+'" onkeypress="return isNumber(event)" maxlength="18" class="inputbox" /></div></div></div></div>';
												}
												dataValidationFieldHtml+='<div style="color:#dd4814;border-bottom:none" class="commonBox clear borderBottom"><b>NOTE: For comparison value, only numbers can be compared. Date, String can not be compared.<b></div>';
								
												
												
												
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


function isNumber(evt) {
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode == 46) {
    	return true;
    }
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        return false;
    }
    return true;
}				



//------------------------------------------------------------------------------------------------------------------------------


	var yamlValidate = false;
	$("#yamlForm").validationEngine('attach', {
		promptPosition : "topLeft",
		scroll : false
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

			
	}
						
	var isValid = false;
	$(document)
			.ready(
					function() {													
						$("#step-1 input[type='text']:visible").live("propertychange keyup input paste", function() { 
							validateInputBoxes();
						});
						
					
						
						$("#schedulingOptions").hide();				
						

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
												$('#fieldNumber' + inputId).attr("disabled",true);
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
						 if (step_num == 4) {
									$('a.buttonNext').addClass('buttonDisabled');
									$('#yaml-dialog-modal .actionBar a#validateWizardBtn').remove();
									$('#yaml-dialog-modal .actionBar a#runWizardBtn').remove();
									$('#yaml-dialog-modal .actionBar a#saveYamlBtn').remove();
									$('#yaml-dialog-modal .actionBar a').show();
								} else {
									$('a.buttonNext').hide();
								
									$('#yaml-dialog-modal .actionBar a#validateWizardBtn').remove();
									$('#yaml-dialog-modal .actionBar a#runWizardBtn').remove();
									$('#yaml-dialog-modal .actionBar a#saveYamlBtn').remove();
									$('#yaml-dialog-modal .actionBar a').show();
								}
						
							if (step_num == 1) {
									$('a.buttonFinish').addClass('buttonDisabled');
								//auto-opening the "no. of unique users" node 1
								//$("#noOfSlavesBtn").trigger("click");
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
								
							//json modified
								
								
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
									url : "DataQualityServlet",
									data : jsonData
							
								})
								.done(function(jsonData) {
								    var json = $('#jsonData').val();
								    //alert(json)
										$("#yamlForm").submit();
								
									
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
							}
						});
						
						$("#noOfSlaves, #noOfFields").keyup(function() {
							var val = $(this).val();
							if ( val == 0 ) {
								$(this).val(1);
							}
						});
						
					});
</script>
</body>

