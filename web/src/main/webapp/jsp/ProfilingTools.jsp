<div id="freeow" class="freeow freeow-top-right"></div>
<div class="pageTopPane">
	<h2 class="pageTitle">Profiling Tools</h2>
	
	<div id="profilingErrorLinks1" class="errorLinkBox"></div>
</div>
<div class="commonBox">
	<div class="fleft">
		<div class="fleft">
			

			
			<div id="networkTabLink1" class="networkCenterTab profilerTab "><img src="../skins/images/netlanc.png" alt="Network Latency"/></div>
			<div id="dataLoadTabLink1" class="dataLoadCenterTab profilerTab"><img src="../skins/images/data_load.png" alt="Data Load"/></div>
			<div id="hdfsDataDistributionTabLink1" class="dataLoadCenterTab profilerTab"><img src="../skins/images/replicate.png" alt="Data Load"/></div>
		</div>
	</div>
	<div id="dataCenterContainer1" class="dataCenterBox" style="position:relative; min-height:501px;">


			<ul id="dataCenterList1" class="accordianList">
			</ul>
			<div id="generalSettingBox1">
				<form id="generalSettingForm1" method="post">						
					<div id="generalSettingDataBox1">
						
						
						
					</div>				
				
					<fieldset>
					</fieldset>	
				</form>
							
			</div>		
		</div>
	<div id="networkContainer1" class="dataCenterBox" style="display: none; min-height: 501px;">
			<ul id="networkNodeList1" class="accordianList">

			</ul>
		</div>

		<div id="dataLoadContainer1" class="dataCenterBox"
			style="display: none; min-height: 501px;">
			<ul id="dataLoadNodeList1" class="accordianList">

			</ul>
		</div>
		
		<div id="hdfsDataDistributionContainer1" class="dataCenterBox"
			style="display: none; min-height: 501px;">
			<div id="hdfsFieldBox1">
				<div class="commonBox">
					<div class="lbl">
						<label>HDFS Path</label>
					</div>
					<div class="fld">
						<input type="text" class="inputbox" id="hdfsFileName1"
							name="hdfsFileName">
					</div>
				</div>
				<div class="commonBox">
					<div class="lbl">&nbsp;</div>
					<div class="fld">
						<button id="hdfsSubmitBtn1">
							<span>Submit</span>
						</button>
					</div>
				</div>
			</div>
			<div id="hdfsDCBox1" style="display: none;"></div>
		</div>
		<div class="fleft">
		<div class="profilingWidget" id="hypertreeNodeBox1" style="display:none;">
			<div class="favHeader" id="f1">Network Latency Graph</div>
			<div id="hypertreeNodeBodyBox" class="fleft" style="width:500px;">		
				
			</div>
		</div>

		<div class="profilingWidget" id="hdfsDataBox1" style="display:none;">
			<div class="favHeader" id="nodeRelationTitleBox1">HDFS Information</div>
			<div id="hdfsDataInformation1" class="fleft" style="width:500px;">		
				
			</div>
		</div>
		<div class="profilingWidget" id="hdfsDataBoxNodeInfo" style="clear: both; margin-top: 10px; position: relative;">
			<div class="favHeader" id="nodeRelationTitleBox1">Block Node Information</div>
			<div id="hdfsDataNodeInformation" class="fleft" style="width:500px;">		
				
			</div>
		</div>
		
		
	</div>
	<div id="passwordModel1" style="display:none;">
		<form id="nodeListForm1" name="nodeListForm" method="POST">
			<div class="fright"><a id="copyToAllLink1" href="javascript:void(0);">Copy To All</a></div>
			<div class="fleft"><ul></ul></div>					
		</form>
	</div>
</div>

<script type="text/JavaScript">
	var TotalCount = 0;
	var TotalRackCount = 0;
	var TotalInnerRackCount = 0;
	var TotalNodeCount = 0;
	var nodeClass = '';
	var nameNodeIp = '';
	//Prepare form data in json format using this function
	//dc/rack/node common render function

function dataLoadTabData1(profileClusterJson, boxID)
{
	var dataCenterHTML='';	
	profileParsedJson = $.parseJSON(profileClusterJson);
	$.each(profileParsedJson['dataCenters'], function(profileJsonKey, profileJsonVal){
		TotalCount++;
		TotalRackCount = 0;		
		dataCenterHTML +='<li>';
		$.each(profileJsonVal, function(profileDataCenterJsonKey, profileDataCenterJsonVal){						
			
			if(profileDataCenterJsonKey == 'clusterId')
			{
				dataCenterHTML +='<a class="toggle" href="javascript:void(0);">'+profileDataCenterJsonVal+'</a>';					
			}
			if(profileDataCenterJsonKey == 'racks')
			{
				$.each(profileDataCenterJsonVal, function(profileJsonInnerKey, profileJsonInnerVal){
					TotalRackCount++;
					TotalInnerRackCount++;						
					dataCenterHTML +='<div class="dataCenterMainBox fleft" style="clear:both;"><div class="rackBox"><fieldset><legend>'+profileJsonInnerVal.rackId+'</legend><div>';
					$.each(profileJsonInnerVal, function(profileJsonRackInnerKey, profileJsonRackInnerVal){
						if(profileJsonRackInnerKey == 'nodes')
						{
							$.each(profileJsonInnerVal['nodes'], function(profileJsonNodeListInnerKey, profileJsonNodeListInnerVal){					
								TotalNodeCount++;	
								if(typeof profileJsonNodeListInnerVal['performance'] != 'undefined')
									{										
										if(profileJsonNodeListInnerVal['performance'] == 'Good')
										{
											
											dataCenterHTML +='<div class="nodeBoxWithoutClick green"><span>'+profileJsonNodeListInnerVal['nodeIp']+' : <b>'+profileJsonNodeListInnerVal['dataLoadStats']+'%</b></span></div>';	
										}
										else if(profileJsonNodeListInnerVal['performance'] == 'Average')
										{											
											dataCenterHTML +='<div class="nodeBoxWithoutClick orange"><span>'+profileJsonNodeListInnerVal['nodeIp']+' : <b>'+profileJsonNodeListInnerVal['dataLoadStats']+'%</b></span></div>';	
										}
										else if(profileJsonNodeListInnerVal['performance'] == 'Unavailable')
										{											
											dataCenterHTML +='<div class="nodeBoxWithoutClick gray"><span>'+profileJsonNodeListInnerVal['nodeIp']+' : <b>'+profileJsonNodeListInnerVal['message']+'</b></span></div>';	
										}
										else
										{											
											dataCenterHTML +='<div class="nodeBoxWithoutClick"><span>'+profileJsonNodeListInnerVal['nodeIp']+' : <b>'+profileJsonNodeListInnerVal['dataLoadStats']+'%</b></span></div>';	
										}
									}
									else
									{
										dataCenterHTML +='<div class="nodeBoxWithoutClick"><span>'+profileJsonNodeListInnerVal['nodeIp']+' : <b>'+profileJsonNodeListInnerVal['dataLoadStats']+'%</b></span></div>';	
									}																	
							});
						}
					});						
					dataCenterHTML +='</div></fieldset></div></div>';
				});
			}		
		});
			
		dataCenterHTML +='</li>';
						
		$(boxID).html(dataCenterHTML);				
	});		
}
 
	function networkLatencyTabData1(profileClusterJson, boxId)
	{
		var dataCenterHTML='';	
		var nodeDetails = '';
		profileParsedJson = $.parseJSON(profileClusterJson);		
		$.each(profileParsedJson['dataCenters'], function(profileJsonKey, profileJsonVal){
			TotalCount++;
			TotalRackCount = 0;		
			dataCenterHTML +='<li>';
			$.each(profileJsonVal, function(profileDataCenterJsonKey, profileDataCenterJsonVal)	{						
				
				if(profileDataCenterJsonKey == 'clusterId')
				{
					dataCenterHTML +='<a class="toggle" href="javascript:void(0);">'+profileDataCenterJsonVal+'</a>';					
				}
				if(profileDataCenterJsonKey == 'racks')
				{
					$.each(profileDataCenterJsonVal, function(profileJsonInnerKey, profileJsonInnerVal){
						TotalRackCount++;
						TotalInnerRackCount++;						
						dataCenterHTML +='<div class="dataCenterMainBox fleft" style="clear:both;"><div class="rackBox"><fieldset><legend>'+profileJsonInnerVal.rackId+'</legend><div>';
						$.each(profileJsonInnerVal, function(profileJsonRackInnerKey, profileJsonRackInnerVal){
							if(profileJsonRackInnerKey == 'nodes')
							{
								$.each(profileJsonInnerVal['nodes'], function(profileJsonNodeListInnerKey, profileJsonNodeListInnerVal){					
									TotalNodeCount++;		
									if(typeof profileJsonNodeListInnerVal['performance'] != 'undefined')
									{	
										if(typeof profileJsonNodeListInnerVal['performance'] != 'undefined')
										{										
											if(profileJsonNodeListInnerVal['performance'] == 'Good')
											{												
												dataCenterHTML +='<div class="nodeBoxWithoutClick green"><span><input type="checkbox" name="NODE_LIST[]" value="'+profileJsonNodeListInnerVal['nodeIp']+'" /></span></div>';
											}
											else if(profileJsonNodeListInnerVal['performance'] == 'Average')
											{												
												dataCenterHTML +='<div class="nodeBoxWithoutClick orange"><span><input type="checkbox" name="NODE_LIST[]" value="'+profileJsonNodeListInnerVal['nodeIp']+'" /></span></div>';
											}
											else if(profileJsonNodeListInnerVal['performance'] == 'Unavailable')
											{
												dataCenterHTML +='<div class="nodeBoxWithoutClick gray"><span><input type="checkbox" name="NODE_LIST[]" value="'+profileJsonNodeListInnerVal['nodeIp']+': '+profileJsonNodeListInnerVal['message']+'" disabled/></span></div>';	
											}
											else
											{												
												dataCenterHTML +='<div class="nodeBoxWithoutClick"><span><input type="checkbox" name="NODE_LIST[]" value="'+profileJsonNodeListInnerVal['nodeIp']+'" /></span></div>';
											}
										}										
									}	
									else
									{												
										dataCenterHTML +='<div class="nodeBoxWithoutClick"><span><input type="checkbox" name="NODE_LIST[]" value="'+profileJsonNodeListInnerVal['nodeIp']+'" /></span></div>';
									}																
								});
							}
						});						
						dataCenterHTML +='</div></fieldset></div></div>';
					});
				}		
			});
				
			dataCenterHTML +='</li>';		
						
			$(boxId).html(dataCenterHTML);				
		});	
			
	}
	//dc/rack/node common render function
	function hdfsDataDistributionTabData1(profileClusterJson, boxID)
	{
		var dataCenterHTML='';	
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
		$('#hdfsDataInformation1').html(distributedDataInfoHtml);
	}	

	$.each(profileParsedJson['dataCenters'], function(profileJsonKey, profileJsonVal){
		TotalCount++;
		TotalRackCount = 0;		
		dataCenterHTML +='<li>';
		$.each(profileJsonVal, function(profileDataCenterJsonKey, profileDataCenterJsonVal){						
			
			if(profileDataCenterJsonKey == 'clusterId')
			{
				dataCenterHTML +='<a class="toggle" href="javascript:void(0);">'+profileDataCenterJsonVal+'</a>';					
			}
			if(profileDataCenterJsonKey == 'racks')
			{
				$.each(profileDataCenterJsonVal, function(profileJsonInnerKey, profileJsonInnerVal){
					TotalRackCount++;
					TotalInnerRackCount++;						
					dataCenterHTML +='<div class="dataCenterMainBox fleft" style="clear:both;"><div class="rackBox"><fieldset><legend>'+profileJsonInnerVal.rackId+'</legend><div>';
					$.each(profileJsonInnerVal, function(profileJsonRackInnerKey, profileJsonRackInnerVal){
						if(profileJsonRackInnerKey == 'nodes')
						{
							$.each(profileJsonInnerVal['nodes'], function(profileJsonNodeListInnerKey, profileJsonNodeListInnerVal){					
								TotalNodeCount++;	
								if(typeof profileJsonNodeListInnerVal['performance'] != 'undefined')
									{										
										if(profileJsonNodeListInnerVal['performance'] == 'Good')
										{
											
											dataCenterHTML +='<div class="hdfsNodeBox green"><span>'+profileJsonNodeListInnerVal['nodeIp']+'</span></div>';	
										}
										else if(profileJsonNodeListInnerVal['performance'] == 'Average')
										{											
											dataCenterHTML +='<div class="hdfsNodeBox orange"><span>'+profileJsonNodeListInnerVal['nodeIp']+'</span></div>';	
										}
										else if(profileJsonNodeListInnerVal['performance'] == 'Unavailable')
										{											
											dataCenterHTML +='<div class="nodeBoxWithoutClick gray"><span>'+profileJsonNodeListInnerVal['nodeIp']+'</span></div>';	
										}
										else
										{											
											dataCenterHTML +='<div class="hdfsNodeBox"><span>'+profileJsonNodeListInnerVal['nodeIp']+'</span></div>';	
										}
									}
									else
									{
										dataCenterHTML +='<div class="hdfsNodeBox"><span>'+profileJsonNodeListInnerVal['nodeIp']+'</span></div>';	
									}																	
							});
						}
					});						
					dataCenterHTML +='</div></fieldset></div></div>';
				});
			}		
		});
			
		dataCenterHTML +='</li>';
						
		$(boxID).html(dataCenterHTML);				
	});		
}

	function formSubmit1(formName1)
	{
		var formData = form2js(formName1, '.', true, function(node) {
				if (node.id
						&& node.id
								.match(/callbackTest/)) {
					return {
						name : node.name,
						value : node.innerHTML
					};
				}
			});

		var finalJson = JSON.stringify(formData, null, '\t');	
		console.log(finalJson) ;
		return finalJson;

	}
	
	$(document).ready(function() { 
		
		$('#networkNodeList1').find('li div.dataCenterMainBox').show();

						$('#networkNodeList1 li a.toggle').live(
								'click',
								function() {
									$(this).toggleClass('selected');
									$(this).next('div.dataCenterMainBox')
											.toggle('down');
								});
		$('#networkTabLink1').live('click',function () {	
			$('.tabSelected').removeClass('tabSelected').addClass('profilerTab');	
			$(this).addClass('tabSelected');;

			$('#dataCenterContainer1').hide();
			
			$('#networkContainer1').show();
			$('#networkContainer1').html('<div class="txtCenter"><img src="./skins/images/loading.gif" width="300px"></div>');
			$('#dataLoadContainer1').hide();
			$('#hdfsDataDistributionContainer1').hide();
			
			var generalSettingFormJson = formSubmit1('generalSettingForm1');
			var ajaxReq = $.ajax({
					  type: "POST",			  
					  url: "ProfilerServlet?VIEW_NAME=NETWORK_LATENCY_VIEW",
					  data: generalSettingFormJson
					}).done(function(finalJSON) {
						if(finalJSON)
						{		
							$('#networkContainer1').html('<form id="networkForm1" name="networkForm" method="POST"><ul id="networkNodeList1" class="accordianList"></ul><div id="submitBtnBox1" style="float:left; padding-left:5px;"><button id="networkSubmit1" type="button"><span>Submit</span></button></div></form>');				
							callProfilerOnSuccess(finalJSON);	
												
						}
					});

			$('#hdfsDataBox1').hide();
			$('#hdfsDataBoxNodeInfo').hide();
			
			
		});

		$('#networkSubmit1').live('click', function () {
			
			var formData = form2js('networkForm1', '.', true, function(node) { if (node.id && node.id.match(/callbackTest/)) {return {name : node.id, value : node.innerHTML };	} });
			$('#networkForm1').get(0).reset();
			if(typeof formData.NODE_LIST == 'undefined')
			{
				alert('Please select nodes from list');
				return;
			}
			$("#passwordModel1 ul:first").html('<li><span class="lbl bold">Node List</span><span class="fld bold">Password</span></li>');		
			$.each(formData.NODE_LIST, function(nodeJsonKey, nodeJsonVal){
				$("#passwordModel1 ul:first").append('<li><span class="lbl">'+nodeJsonVal+'</span><span class="fld"><input type="password" id="node'+nodeJsonKey+'" name="'+nodeJsonVal+'" class="inputbox"></span></li>');
			});
			$("#passwordModel1 ul:first").append('<li><span class="lbl">&nbsp;</span><span class="fld"><button id="nodeListSubmit1"><span>Submit</span></button></span></li>');
	
			$("#passwordModel1").dialog({
				dialogClass: 'modalSelectLocation',
				height:300,
				width:400,
				draggable:false,
				resizable:false,
				modal: true
			}); 	
								
			return false;
		});
		//hdfs data distribution tab 
		$('#hdfsDataDistributionTabLink1').live('click',function () {	
			$('.tabSelected').removeClass('tabSelected').addClass('profilerTab');	
			$(this).addClass('tabSelected');
			
			$('#dataCenterContainer1').hide();
			$('#networkContainer1').hide();
			$('#dataLoadContainer1').hide();
			$('#hdfsDataDistributionContainer1').show();
			$('#hypertreeNodeBox1').hide();
			$('#hdfsFieldBox1').show();
			$('#hdfsDCBox1').hide();	

			$('#hdfsDataBox1').show();
			$('#hdfsDataBoxNodeInfo').show();
			
		});
		// Node List Submit button click
		$('#nodeListSubmit1').live('click', function () {
			$("#passwordModel1").dialog('close');
			$('#hypertreeNodeBox1').show();
				
			$('#hypertreeNodeBodyBox').html('<div class="txtCenter"><img src="./skins/images/loading.gif"></div>');	
			
			var formData = form2js('nodeListForm1', '..', true, function(node) { if (node.id && node.id.match(/callbackTest/)) {return {name : node.id, value : node.innerHTML };	} });
			var nodeListJson = JSON.stringify(formData);
			//nodeListJson = '{"192.168.49.52":"impetus121","192.168.49.60":"impetus121"}';		
			$.ajax({type: "POST",
				url: "ProfilerServlet", 
				data: 'VIEW_NAME=NETWORK_LATENCY_RESULT&NODE_LIST='+nodeListJson}).done(function(resp) {
				$('#hypertreeNodeBodyBox').html('');			
				init(resp);			
			});
			return false;
		});
		
		$('#dataLoadTabLink1').live('click',function () {	
		$('.tabSelected').removeClass('tabSelected').addClass('profilerTab');	
		$(this).addClass('tabSelected');
		$('#hypertreeNodeBox1').hide();
		$('#dataCenterContainer1').hide();
		$('#networkContainer1').hide();

		$('#dataLoadContainer1').show();
		$('#hdfsDataDistributionContainer1').hide();

		$('#dataLoadContainer1').html('<div class="txtCenter"><img src="./skins/images/loading.gif" width="300px"></div>');			
		
		var ajaxReq = $.ajax({
					  type: "POST",			  
					  url: "ProfilerServlet?VIEW_NAME=DATALOAD_VIEW"					  
					}).done(function(finalJSON) {
						if(finalJSON)
						{		
							$('#dataLoadContainer1').html('<ul id="dataLoadNodeList1" class="accordianList"></ul>');				
							callProfilerOnSuccess(finalJSON);
							
						}
					});	

			$('#hdfsDataBox1').hide();
			$('#hdfsDataBoxNodeInfo').hide();
			
			
		});
		$('#hdfsSubmitBtn1').live('click',function () {

			$('#hdfsFieldBox1').hide();
			$('#hdfsDCBox1').html('<div class="txtCenter"><img src="./skins/images/loading.gif" width="300px"></div>').show();	
			var hdfsFileName = $('#hdfsFileName1').val();
			if(hdfsFileName == '')
			{
				alert('Please enter HDFS file name');
				return;
			}

			var ajaxReq = $.ajax({
					  type: "POST",			  
					  url: "ProfilerServlet?VIEW_NAME=DATA_DISTRIBUTION_VIEW&HDFS_PATH="+hdfsFileName					  
					}).done(function(finalJSON) {
						if(finalJSON)
						{
								
							$('#hdfsDCBox1').html('<ul id="hdfsDataDistributionNodeList1" class="accordianList"></ul>');				
							callProfilerOnSuccess(finalJSON);
							
						}
					});
		
		});
	$('.hdfsNodeBox').live('click', function (){ 
			var nodeIp = $(this).find('span').html();
			var params = 'VIEW_NAME=DATA_DISTRIBUTION_VIEW&NODE_IP='+nodeIp;	

			$.ajax({
			  type: "POST",			  
			  url: "ProfilerServlet",
			  data: params,
			  error: function (xhr, ajaxOptions, thrownError) {				
				alert('Connection to the server got lost.');					
			  }
			}).done(function(finalJSON) {	
				
				var jsonObject = $.parseJSON(finalJSON);
				var distributedDataInfoHtml="<ul>";
				var chartData = new Array();
				$.each(jsonObject, function(distributedDataKey, distributedDataVal){
				if(typeof distributedDataVal != 'object'){
				distributedDataInfoHtml += "<li><span class='clabel'>"+distributedDataKey+"</span><span class='cval'>"+distributedDataVal+"</span></li>";
				}
				else
				{
					$.each(distributedDataVal,function(a,b){
					distributedDataInfoHtml += "<li><span class='clabel'>"+a+"</span><span class='cval'>"+b+"</span></li>";
					});
      				
				}
						
						

						
				});	
				distributedDataInfoHtml += "</ul>";			
				$('#hdfsDataNodeInformation').html(distributedDataInfoHtml);
				

			});

				
		});
		$('#copyToAllLink1').click(function () {
			var getPass = $('#node0').val();
			$("#passwordModel1 ul:first").find('input').each(function () {
				$(this).val(getPass);		
			});
		});
		
	
	});
	
	

</script>
	