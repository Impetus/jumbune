
<div class="pageTopPane">
	<h2 class="pageTitle">Schedule Jobs</h2>
</div>


<div id="scheduleJobsBox" class="fleft">
	<div class="widget-container">
		<div class="widget-header">Schedule Jobs</div>
		<div class="widget-body">
			<table id="scheduleJobsTable"></table>
			<form method="POST" action="GatherScheduledJobResult"
				name="scheduleForm" id="scheduleForm">
				<input type="hidden" id="scheduledJobName" name="scheduledJobName"
					value="" />
			</form>
		</div>
	</div>

</div>

<script language="javascript">	$(document).ready(function () {
	
		var schduleCount=0;

		var jsonStrData = <%=request.getParameter("data")%>;
		jsonStrData = JSON.stringify(jsonStrData);
		var scheduleJobsGridData = [];
		
		if(jsonStrData)
		{
			var parseData = $.parseJSON(jsonStrData);
			
			$.each(parseData, function(dataKey, dataVal){	
				
				schduleCount++;				
				var eachScheduleJobsJsonObj = { "id":schduleCount,"name":"<a href='#' class='scheduleJobName' rel='"+dataVal+"'>"+dataKey+"</a>","status":dataVal };
				scheduleJobsGridData.push(eachScheduleJobsJsonObj);
				
			});

			var scheduleDataJson={"response": scheduleJobsGridData};    								    
			pureJobsGrid = jQuery("#scheduleJobsTable");
			pureJobsGrid.jqGrid({
				datastr: scheduleDataJson,
				datatype: "jsonstring",
				height: 100,
				scroll: true,
				hidegrid:false,
				loadui: "disable",
				colNames:["ID","Name","Status"], 
				colModel:
					[ 
						{name:'id',index:'id', width:35, align:"center"},
						{name:'name',index:'name', width:210},
						{name:'status',index:'status', width:110}
									
					],												
				rowNum: 10000,		
				jsonReader: {
				repeatitems: false,
				root: "response"
				}
			});
		}


	});


	$('.scheduleJobName').click(function (){
		var jobName = $(this).text();
		var status = $(this).attr('rel');
		//status=status.toString();

		if(status=='Completed'){
			$('#scheduledJobName').val(jobName);
			$('#scheduleForm').submit();
		}else if(status=='In-progress'){
			alert('Scheduled job is in progress');
		}else{
			alert('Scheduled job is not triggered yet');
		}
		
				
	})
</script>


