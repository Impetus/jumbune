/**
 * Copyright (c) 2010 Maxim Vasiliev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author Maxim Vasiliev
 * Date: 19.09.11
 * Time: 23:40
 */

var js2form = (function()
{
	"use strict";

	var _subArrayRegexp = /^\[\d+?\]/,
			_subObjectRegexp = /^[a-zA-Z_][a-zA-Z_0-9]+/,
			_arrayItemRegexp = /\[[0-9]+?\]$/,
			_lastIndexedArrayRegexp = /(.*)(\[)([0-9]*)(\])$/,
			_arrayOfArraysRegexp = /\[([0-9]+)\]\[([0-9]+)\]/g,
			_inputOrTextareaRegexp = /INPUT|TEXTAREA/i;

	/**
	 *
	 * @param rootNode
	 * @param data
	 * @param delimiter
	 * @param nodeCallback
	 * @param useIdIfEmptyName
	 */
	function js2form(rootNode, data, delimiter, nodeCallback, useIdIfEmptyName)
	{
		if (arguments.length < 3) delimiter = '.';
		if (arguments.length < 4) nodeCallback = null;
		if (arguments.length < 5) useIdIfEmptyName = false;

		var fieldValues, formFieldsByName;

		//generate html dynamically
		generateHTML(data);
		fieldValues = object2array(data);
		formFieldsByName = getFields(rootNode, useIdIfEmptyName, delimiter, {}, true);
		
		//put custom fields value
		putCustomValues(data);

		for (var i = 0; i < fieldValues.length; i++)
		{
			var fieldName = fieldValues[i].name,
				fieldValue = fieldValues[i].value;
			
			if (typeof formFieldsByName[fieldName] != 'undefined')
			{
					setValue(formFieldsByName[fieldName], fieldValue);	
							
				
			}
			else if (typeof formFieldsByName[fieldName.replace(_arrayItemRegexp, '[]')] != 'undefined')
			{
				setValue(formFieldsByName[fieldName.replace(_arrayItemRegexp, '[]')], fieldValue);
			}
			
						
		}	

	}
	
	function backDateAndTimeCheck(tmVal){
				var toCheck = [], mCheck = false, dCheck = false, hrCheck = false, minCheck = false;
				toCheck = tmVal.split(' ');
				console.log(toCheck+" toCheck");
				var todayDate = new Date();
				var curMinutes = todayDate.getMinutes()+1;
				var curHours = todayDate.getHours();
				var curDate = todayDate.getDate();
				var curMonth = todayDate.getMonth()+1;
				
				if(curMonth > toCheck[3]){
					return false;
				} else if(curMonth == toCheck[3]){
					mCheck = true;
				}
				
				if(curDate > toCheck[2]){
					return false;
				} else if(curDate == toCheck[2]){
					dCheck = true;
				}
				
				if(curHours > toCheck[1]){
					return false;
				} else if(curHours == toCheck[1]){
					hrCheck = true;
				}
				
				if(curMinutes > toCheck[0]){
					return false;
				} else if(curMinutes == toCheck[0]){
					minCheck = true;
				}
				
				if(mCheck == true && dCheck == true && hrCheck == true && minCheck == true){
					return true;
				} else {
					return false;
				}
	}

	function putCustomValues(data)
	{	

		if(typeof data.slaves != 'undefined' && typeof data.slaves.length != 'undefined')
		{			
			$('#noOfSlaves').val(data.slaves.length);
		}

		if(typeof data.jobs != 'undefined')
		{	
			$('#noOfJobs').val(data.jobs.length);
		}

		if(typeof data.userValidations != 'undefined' && typeof data.userValidations.length != 'undefined')
		{
			$('#noOfFields').val(data.userValidations.length);
		}

		if(typeof data.dataValidation != 'undefined' && typeof data.dataValidation.fieldSeparator != 'undefined')
		{			
			if(typeof data.dataValidation.fieldValidationList != 'undefined')
			{
				$('#noOfFields').val(data.dataValidation.fieldValidationList.length);				
			}
		}

		if(typeof data.inputFile != 'undefined' )
		{
			$('#masterMachinePath').trigger('click');					
		}

		if(typeof data.sampleFraction != 'undefined' )
                {
			if(data.sampleFraction <= 0.03) {
				$("#dsFractionType").html("Low");
				$("#dsFractionVal").val(data.sampleFraction*100);
			}else if(data.sampleFraction > 0.03 && data.sampleFraction <= 0.07) {
				$("#dsFractionType").html("Decent");
				$("#dsFractionVal").val(data.sampleFraction*100);
			}else if(data.sampleFraction > 0.07) {
				$("#dsFractionType").html("High");
				$("#dsFractionVal").val(data.sampleFraction*100);
			}

          		$( "#fractionSlider" ).slider('value', data.sampleFraction*100);                                                                  
                }

		if(typeof data.includeClassJar !='undefined' && data.includeClassJar == 'TRUE')
		{
			$('#jobsFields').find("input[id^='jobClassName']").closest('.fixWidthBox').hide();
		}
		

	}

	function generateHTML(data)
	{
		if(typeof data.dataValidation!= 'undefined'){
			if(typeof data.dataValidation.recordSeparator!= 'undefined')
			data.dataValidation.recordSeparator=data.dataValidation.recordSeparator.replace("\n", "\\n");
		}
		
		
		if(typeof data.slaves!= 'undefined' && typeof data.slaves.length != 'undefined')
		{
				var slaveHtml="";
				var addLink="";				
				for(var i=0; i<=data.slaves.length-1; i++)
				{
					var fieldCount = parseInt(i)+parseInt(1);
					
					var strRange='none';
					if(data.slaves[i].enableHostRange=='on')
					{
						strRange='block'
					}
					addLink = '<a href="javascript:void(0);" onclick="javascript:addMoreHostFields('+i+');" class="addSign">Add</a>';					

					//slaveHtml +='<div class="fieldsetBox clear"><div class="paddBott"><a class="removeSign" onclick="javascript:removeSlaveRow('+i+');" href="javascript:void(0);" style="margin-top:-1px;">Remove</a>DataNode '+fieldCount+'</div><fieldset><div id="extraslaveRow'+i+'" class="clear"><div class="fixWidthBox"><div class="lbl"><label>User</label></div><div class="fld"><input type="text" name="slaves['+i+'].user" id="slaveUser'+i+'" class="inputbox" /></div></div><div id="morehostField'+i+'" class="clear"><div class="fixWidthBox"><div class="lbl"><label>Host</label></div><div class="fld"><input type="text" name="slaves['+i+'].hosts['+i+']" id="slaveHost'+i+'0" class="inputbox mediumInput" />'+addLink+'</div></div></div></div></fieldset></div>';
					slaveHtml += '<div class="fieldsetBox clear"><div class="paddBott">';
					
					if ( i > 0 ) {
						slaveHtml += '<a class="removeSign" onclick="javascript:removeSlaveRow('
							+ i
							+ ');" href="javascript:void(0);" style="margin-top:-1px;">Remove</a>';
					}
						
					slaveHtml += 'Worker Node '
						+ fieldCount
						+ '</div><fieldset><div id="extraslaveRow'+i+'" class="clear"><div class="fixWidthBox"><div class="lbl"><label>User</label><span class="asterix"></span></div><div class="fld"><input type="text" name="slaves['+i+'].user" id="slaveUser'+i+'" class="inputbox" /></div></div><div id="morehostField'+i+'" class="clear" ><div class="fixWidthBox" style="width:690px !important;"><div class="lbl"><label>Host</label><span class="asterix"></span></div><div class="fld fld-extend"><input type="text" name="slaves['+i+'].hosts['+i+']" id="slaveHost'+i+'0" class="inputbox mediumInput" />'
						+ addLink
						+ '<span style="float:left; padding-left:22px;padding-right:7px;">Specify Range <input type="checkbox" class="hostRange" name="slaves['+i+'].enableHostRange" id="enableHostRange'+i+'" /></span><input type="text" size="10" name="slaves['+i+'].hostRangeFromValue" id="enableHostRange'+i+'_From_Value" style="display:'+strRange+';float:left;min-width:97px;" class="inputbox"><span style="float:left;display:'+strRange+'" id="enableHostRange'+i+'_From_Value_space">-</span><input type="text" size="10" name="slaves['+i+'].hostRangeToValue" id="enableHostRange'+i+'_To_Value" style="display:'+strRange+';float:left;min-width:97px;" class="inputbox"></div></div></div></div></fieldset></div>';
				}				
				$('#slaveFieldBox').html(slaveHtml);
				$('#copySlaveMasterBox').show('slow');
				
				
				for(var i=0; i<=data.slaves.length-1; i++)
				{
					if(typeof data.slaves[i].hosts.length != 'undefined')
					{	
						var hostFieldHtml="";					
						for(var j=0; j<=data.slaves[i].hosts.length-1; j++)
						{
							if(j!=0)
							{
								hostFieldHtml +='<div class="clear" id="moreInnerHostBox'+j+i+'"><div class="fixWidthBox"><div class="lbl"><label>Host</label><span class="asterix"></span></div><div class="fld"><input type="text" name="slaves['+i+'].hosts[]" id="slaveHost'+j+i+'" class="inputbox mediumInput" /><a href="javascript:void(0);" onclick="javascript:removeMoreHost('+j+i+');" class="removeSign">Remove</a></div></div></div>';	
							}
						}						
						$('#morehostField'+i).append(hostFieldHtml);
					}

				}
			
		}

		if(typeof data.inputFile != 'undefined' )
		{			
			$('#localMachinePathFieldBox').html('');

			$('#masterMachinePathFieldBox').html('<div class="fleft">&nbsp;</div><div class="fld"><label class="fleft">File path</label>&nbsp;<input type="text" name="inputFile" id="masterMachineFile" class="inputbox"/></div>');	
		}		

		if(typeof data.debuggerConf!=undefined) {
			if(typeof data.regexValidations != 'undefined' && typeof data.regexValidations.length != 'undefined' && typeof data.debuggerConf.logLevel.instrumentRegex != 'undefined')
			{					
					
					if(data.debuggerConf.logLevel.instrumentRegex == "TRUE")
					{
						if($('#debugAnalysis').attr('value') == "TRUE")  {
							$('#instrumentRegexField input[type="text"]').removeAttr("disabled");
							$('#instrumentRegexField .addMoreSign').show();
						}
						var fieldHtml="";										
						$('#instrumentRegex').attr("value", "TRUE");												
						
						for(var i=0; i<=data.regexValidations.length-1; i++)
						{
							var fieldCount = parseInt(i)+parseInt(1);

							var fieldHtml = '<div class="fieldsetBox clear"><div class="paddBott"><a style="margin-top:-1px;" href="javascript:void(0);" onclick="javascript:instrumentRegexRemoveField('+i+');" class="removeSign">Remove</a>Regex '+fieldCount+'</div><fieldset><div id="instrumentRegexFieldRow'+i+'"><div class="fixWidthBox"><div class="lbl"><label>Mapper/Reducer Name</label></div><div class="fld"><input type="text" name="regexValidations['+i+'].classname" id="regexClassName'+i+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Regex on Key</label></div><div class="fld"><input type="text" name="regexValidations['+i+'].key" id="mapKey'+i+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Regex on Value</label></div><div class="fld"><input type="text" name="regexValidations['+i+'].value" id="mapValue'+i+'" class="inputbox"/></div></div></div></fieldset></div>';
							
							if(i!=0)
							$('#instrumentRegexField').append(fieldHtml);				
							
						}	
					}
				
			}
		}
		if(typeof data.userValidations != 'undefined' && typeof data.userValidations.length != 'undefined' && typeof data.debuggerConf.logLevel.instrumentUserDefValidate != 'undefined')
		{						
				if(data.debuggerConf.logLevel.instrumentUserDefValidate == "TRUE")
				{
					if($('#debugAnalysis').attr('value') == "TRUE")  {
						$('#instrumentUserDefValidateField input[type="text"]').removeAttr("disabled");
						$('#instrumentUserDefValidateField .addMoreSign').show();
					}
					var validateFieldHtml="";						
					$('#instrumentUserDefValidate').removeAttr("disabled").attr("value", "TRUE");		
					
					for(var i=0; i<=data.userValidations.length-1; i++)
					{
						var fieldCount = parseInt(i)+parseInt(1);

						var validateFieldHtml = '<div class="fieldsetBox clear"><div class="paddBott"><a style="margin-top:-1px;" href="javascript:void(0);" onclick="javascript:instrumentUserDefValidateRemoveField('+i+');" class="removeSign">Remove</a> User Validation  '+fieldCount+'</div><fieldset><div id="instrumentUserDefValidateFieldRow'+i+'"><div class="fixWidthBox"><div class="lbl"><label>Mapper/Reducer Name</label></div><div class="fld"><input type="text" name="userValidations['+i+'].classname" id="userClassName'+i+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Key Validator Class</label></div><div class="fld"><input type="text" name="userValidations['+i+'].key" id="mapKeyValidator'+i+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Value Validator Class</label></div><div class="fld"><input type="text" name="userValidations['+i+'].value" id="validateValue'+i+'" class="inputbox"/></div></div><div class="commonBox"></div></div></div></fieldset></div>';		
						
						if(i!=0)
						$('#instrumentUserDefValidateField').append(validateFieldHtml);					
						
					}
				}
			
		}		
		
				

		if(typeof data.jobjar != 'undefined' && typeof data.jobjar.machinePath != 'undefined')
		{				
			if(data.jobjar.machinePath == 1)
			{
				$('#localMachinePathFieldBox').html('<div class="lbl"><label style="display:none;">Job jar location</label>&nbsp;</div><div class="fld"><label class="fleft">Browse Local System Path</label>&nbsp;<input type="file" name="instrumentInputFile" id="localMachineFile" size="11"/></div>');

				$('#masterMachinePathFieldBox').html('');
			}
			else if(data.jobjar.machinePath == 2)
			{
				$('#localMachinePathFieldBox').html('');

				$('#masterMachinePathFieldBox').html('<div class="lbl"><label style="display:none;">Job jar location</label>&nbsp;</div><div class="fld"><label class="fleft">Master machine path</label>&nbsp;<input type="text" name="instrumentInputFile" id="masterMachineFile" class="inputbox"/></div>');
			}

		}

		if(typeof data.jobs != 'undefined')
		{			
			var jobHtml="";
			
			for(var i=0; i<=data.jobs.length-1; i++)
			{
				var fieldCount = parseInt(i)+parseInt(1);
				
				jobHtml +='<div class="fieldsetBox"><div class="paddBott">';
				if ( i > 0 ) {
					jobHtml +='<a style="margin-top:-1px;" href="javascript:void(0);" onclick="javascript:removeJobRow('+i+');" class="removeSign">Remove</a>';
				}
				jobHtml +='Job '+fieldCount+'</div><fieldset><div id="extraRow'+i+'" class="commonBox"><div class="fixWidthBox"><div class="lbl"><label>Name</label></div><div class="fld"><input type="text" name="jobs['+i+'].name" id="name'+i+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Job Class Name</label></div><div class="fld"><input type="text" name="jobs['+i+'].jobClass" id="jobClassName'+i+'" class="inputbox"/></div></div><div class="fixWidthBox"><div class="lbl"><label>Parameters</label></div><div class="fld"><input type="text" name="jobs['+i+'].parameters" id="jobsParams'+i+'" class="inputbox"/></div></div></div></fieldset></div>';
			}
			$('#jobsFields').html(jobHtml);

		}	
		
		
		if(typeof data.dataValidation != 'undefined' && typeof data.dataValidation.fieldSeparator != 'undefined')
		{
			
			$('#noOfFieldsBox').show('slow');
			if(typeof data.dataValidation.fieldValidationList != 'undefined')
			{
				var dataValidationFieldHtml="";

				for(var i=0; i<=data.dataValidation.fieldValidationList.length-1; i++)
				{	
					var loopVariable =  parseInt(data.dataValidation.fieldValidationList[i].fieldNumber);
					var fieldCount = loopVariable+parseInt(1);						
					if(data.dataValidation.fieldValidationList[i].nullCheck != undefined || data.dataValidation.fieldValidationList[i].regex != undefined || data.dataValidation.fieldValidationList[i].dataType != undefined) {
						dataValidationFieldHtml += '<div class="commonBox clear borderBottom"><div class="fixWidthValidationCheck"><input type="checkbox" id="validationChk'+i+'" checked/></div><div class="fixWidthValidationCheckBox">'
															+ fieldCount
															+ '</div><div class="fixWidthValidationCheckBox" style="display:none;"><div class="fld"><input type="hidden" name="dataValidation.fieldValidationList['+i+'].fieldNumber" id="fieldNumber'+i+'" value="'+i+'"/></div></div><div id="extraDataValidationRow'+i+'" class=""><div class="fixWidthValidationBox"><div class="fld"><select name="dataValidation.fieldValidationList['+i+'].nullCheck" id="nullCheck'+i+'"><option value="&nbsp;">Please Select</option><option value="mustBeNull">must be null</option><option value="notNull">must not be null</option></select></div></div><div class="fixWidthValidationBox"><div class="fld"><select name="dataValidation.fieldValidationList['+i+'].dataType" id="dataType'+i+'" class="inputboxes"><option value="&nbsp;">Please Select</option><option value="int_type">int</option><option value="float_type">float</option><option value="long_type">long</option><option value="double_type">double</option></select></div></div><div class="fixWidthValidationBox"><div class="fld"><input type="text" name="dataValidation.fieldValidationList['+i+'].regex" id="dataValidationRegex'+i+'" class="inputbox" /></div></div></div></div></div>';
					}		
					else {
					dataValidationFieldHtml += '<div class="commonBox clear borderBottom"><div class="fixWidthValidationCheck"><input type="checkbox" id="validationChk'+i+'"/></div><div class="fixWidthValidationCheckBox disabledRow">'
															+ fieldCount
															+ '</div><div class="fixWidthValidationCheckBox disabledRow" style="display:none;"><div class="fld"><input disabled type="hidden" name="dataValidation.fieldValidationList['+i+'].fieldNumber" id="fieldNumber'+i+'" value="'+i+'"/></div></div><div id="extraDataValidationRow'+i+'" class=""><div class="fixWidthValidationBox disabledRow"><div class="fld"><select name="dataValidation.fieldValidationList['+i+'].nullCheck" id="nullCheck'+i+'" disabled><option value="&nbsp;">Please Select</option><option value="mustBeNull">must be null</option><option value="notNull">must not be null</option></select></div></div><div class="fixWidthValidationBox disabledRow"><div class="fld"><select disabled name="dataValidation.fieldValidationList['+i+'].dataType" id="dataType'+i+'" class="inputboxes"><option value="&nbsp;">Please Select</option><option value="int_type">int</option><option value="float_type">float</option><option value="long_type">long</option><option value="double_type">double</option></select></div></div><div class="fixWidthValidationBox disabledRow"><div class="fld"><input type="text" disabled="true" name="dataValidation.fieldValidationList['+i+'].regex" id="dataValidationRegex'+i+'" class="inputbox" /></div></div></div></div></div>';
					}
				}						
				$('#dataValidationFieldBox').html(dataValidationFieldHtml);					
				$('#extraDataValidationHeaderBox').show();
				
				for(var i=0; i<=data.dataValidation.fieldValidationList.length-1; i++)
				{
				//alert(data.dataValidation.fieldValidationList[i]);
					if(typeof data.dataValidation.fieldValidationList[i] != 'undefined' && typeof data.dataValidation.fieldValidationList[i].fieldNumber != 'undefined')
					{												
						$('#extraDataValidationRow'+i).show('slow');
						//console.log($('#extraDataValidationRow'+i).find(".fixWidthValidationCheckBox, .fixWidthValidationBox"));
						//console.log($('#extraDataValidationRow'+i).find("input[type='text'], select"));
						//$('#extraDataValidationRow'+i).find(".fixWidthValidationCheckBox, .fixWidthValidationBox").removeClass("disabledRow");
						//$('#extraDataValidationRow'+i).find("input[type='text'], select").removeAttr("disabled");
						$('#dataType'+i).closest('.fixWidthValidationBox').show('slow');
						$('#dataValidationRegex'+i).closest('.fixWidthValidationBox').show('slow');				
					

					}
				}
			}

		}		

		//Check profile section is enable 
		if(typeof data.debugAnalysis != 'undefined' && data.debugAnalysis == "TRUE")
		{			
			$('#debugAnalysisBox').show('slow');			
			$("#instrumentRegex, #instrumentUserDefValidate").removeAttr("disabled");
			//use entore checkbox click in debugginh section				
			if (typeof data.debuggerConf.debugOnActualData != 'undefined' && debuggerConf.debugOnActualData == 'TRUE')
			{
				$('#sampleWorkingSetBox').hide();
			}
			
		}

		

		
		//Check userSupplied inputType is enable 
		if(typeof data.classpath != 'undefined')
		{
			if(typeof data.classpath.userSupplied.source != 'undefined')
			{
				if(data.classpath.userSupplied.source==2)
				{
					$('#machineClassPathMainBox').html('<div id="localMachineClassPathBox" class="fixWidthBox"> <div class="lbl"> <label>Local machine path</label> </div> <div class="fld"> <input type="file" name="classpath.userSupplied.folder" id="localMachineClassPath"  size="11"/> </div></div>');
					$('#userSuppliedResource').show('slow');
					$('#userSuppliedExclude').show('slow');	
					
				}				
				else if(data.classpath.userSupplied.source==4)
				{
					$('#machineClassPathMainBox').html('<div id="slaveMachineClassPathBox" class="fixWidthBox"> <div class="lbl"> <label>Slave machine path</label> </div> <div class="fld"> <textarea class="inputbox" name="classpath.userSupplied.folder" id="slaveMachineClassPath"></textarea></div></div>');
					$('#userSuppliedResource').show('slow');
					$('#userSuppliedExclude').show('slow');	
					
				}
				else if(data.classpath.userSupplied.source==3)
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
				
			}
		}		

			
		
		//Check profile section is enable 
		if(typeof data.hadoopJobProfile != 'undefined' && data.hadoopJobProfile == "TRUE")
		{			
			$('#ProfileFieldsBox').show('slow');
		}
		
		
		//Check dataValidation section is enable 
		if(typeof data.enableDataValidation != 'undefined' && data.enableDataValidation == "TRUE")
		{			
			$('#validationFieldsBox input[type="text"]').removeAttr('disabled')	;
			
		}
		
		

		//Check includeClassJar checkbox is selected
		if(typeof data.jobs != 'undefined' && data.jobs.length > 0)
		{
			$('#includeClassJarBox').show('slow');			
		}

		
	}

	function setValue(field, value)
	{
		var children, i, l;

		if (field instanceof Array)
		{
			for(i = 0; i < field.length; i++)
			{
				if (field[i].value == value) field[i].checked = true;
			}
		}
		else if (_inputOrTextareaRegexp.test(field.nodeName))
		{
			field.value = value;
		}
		else if (/SELECT/i.test(field.nodeName))
		{
			children = field.getElementsByTagName('option');
			for (i = 0,l = children.length; i < l; i++)
			{
				if (children[i].value == value)
				{
					children[i].selected = true;
					if (field.multiple) break;
				}
				else if (!field.multiple)
				{
					children[i].selected = false;
				}
			}
		}
	}

	function getFields(rootNode, useIdIfEmptyName, delimiter, arrayIndexes, shouldClean)
	{
		if (arguments.length < 4) arrayIndexes = {};

		var result = {},
			currNode = rootNode.firstChild,
			name, nameNormalized,
			subFieldName,
			i, j, l,
			options;

		while (currNode)
		{
			name = '';

			if (currNode.name && currNode.name != '')
			{
				name = currNode.name;
			}
			else if (useIdIfEmptyName && currNode.id && currNode.id != '')
			{
				name = currNode.id;
			}

			if (name == '')
			{
				var subFields = getFields(currNode, useIdIfEmptyName, delimiter, arrayIndexes, shouldClean);
				for (subFieldName in subFields)
				{
					if (typeof result[subFieldName] == 'undefined')
					{
						result[subFieldName] = subFields[subFieldName];
					}
					else
					{
						for (i = 0; i < subFields[subFieldName].length; i++)
						{
							result[subFieldName].push(subFields[subFieldName][i]);
						}
					}
				}
			}
			else
			{
				if (/SELECT/i.test(currNode.nodeName))
				{
					for(j = 0, options = currNode.getElementsByTagName('option'), l = options.length; j < l; j++)
					{
						if (shouldClean)
						{
							options[j].selected = false;
						}

						nameNormalized = normalizeName(name, delimiter, arrayIndexes);
						result[nameNormalized] = currNode;
					}
				}
				else if (/INPUT/i.test(currNode.nodeName) && /CHECKBOX|RADIO/i.test(currNode.type))
				{
					if(shouldClean)
					{
						currNode.checked = false;
					}

					nameNormalized = normalizeName(name, delimiter, arrayIndexes);
					nameNormalized = nameNormalized.replace(_arrayItemRegexp, '[]');
					if (!result[nameNormalized]) result[nameNormalized] = [];
					result[nameNormalized].push(currNode);
				}
				else
				{
					if (shouldClean)
					{
						currNode.value = '';
					}

					nameNormalized = normalizeName(name, delimiter, arrayIndexes);
					result[nameNormalized] = currNode;
				}
			}

			currNode = currNode.nextSibling;
		}

		return result;
	}

	/**
	 * Normalizes names of arrays, puts correct indexes (consecutive and ordered by element appearance in HTML)
	 * @param name
	 * @param delimiter
	 * @param arrayIndexes
	 */
	function normalizeName(name, delimiter, arrayIndexes)
	{
		var nameChunksNormalized = [],
				nameChunks = name.split(delimiter),
				currChunk,
				nameMatches,
				nameNormalized,
				currIndex,
				newIndex,
				i;

		name = name.replace(_arrayOfArraysRegexp, '[$1].[$2]');
		for (i = 0; i < nameChunks.length; i++)
		{
			currChunk = nameChunks[i];
			nameChunksNormalized.push(currChunk);
			nameMatches = currChunk.match(_lastIndexedArrayRegexp);
			if (nameMatches != null)
			{
				nameNormalized = nameChunksNormalized.join(delimiter);
				currIndex = nameNormalized.replace(_lastIndexedArrayRegexp, '$3');
				nameNormalized = nameNormalized.replace(_lastIndexedArrayRegexp, '$1');

				if (typeof (arrayIndexes[nameNormalized]) == 'undefined')
				{
					arrayIndexes[nameNormalized] = {
						lastIndex: -1,
						indexes: {}
					};
				}

				if (currIndex == '' || typeof arrayIndexes[nameNormalized].indexes[currIndex] == 'undefined')
				{
					arrayIndexes[nameNormalized].lastIndex++;
					arrayIndexes[nameNormalized].indexes[currIndex] = arrayIndexes[nameNormalized].lastIndex;
				}

				newIndex = arrayIndexes[nameNormalized].indexes[currIndex];
				nameChunksNormalized[nameChunksNormalized.length - 1] = currChunk.replace(_lastIndexedArrayRegexp, '$1$2' + newIndex + '$4');
			}
		}

		nameNormalized = nameChunksNormalized.join(delimiter);
		nameNormalized = nameNormalized.replace('].[', '][');
		return nameNormalized;
	}

	function object2array(obj, lvl)
	{
		var result = [], i, name;

		if (arguments.length == 1) lvl = 0;

        if (obj == null)
        {
            result = [{ name: "", value: null }];
        }
        else if (typeof obj == 'string' || typeof obj == 'number' || typeof obj == 'date' || typeof obj == 'boolean')
        {
            result = [
                { name: "", value : obj }
            ];
        }
        else if (obj instanceof Array)
        {
            for (i = 0; i < obj.length; i++)
            {
                name = "[" + i + "]";
                result = result.concat(getSubValues(obj[i], name, lvl + 1));
            }
        }
        else
        {
            for (i in obj)
            {
                name = i;
                result = result.concat(getSubValues(obj[i], name, lvl + 1));
            }
        }

		return result;
    }

	function getSubValues(subObj, name, lvl)
	{
		var itemName;
		var result = [], tempResult = object2array(subObj, lvl + 1), i, tempItem;

		for (i = 0; i < tempResult.length; i++)
		{
			itemName = name;
			if (_subArrayRegexp.test(tempResult[i].name))
			{
				itemName += tempResult[i].name;
			}
			else if (_subObjectRegexp.test(tempResult[i].name))
			{
				itemName += '.' + tempResult[i].name;
			}

			tempItem = { name: itemName, value: tempResult[i].value };
			result.push(tempItem);
		}

		return result;
	}

	return js2form;

})();
