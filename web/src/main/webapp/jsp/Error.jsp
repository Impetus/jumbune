
<div class="pageTopPane">
	<h2 class="pageTitle">Errors & Exceptions</h2>	
</div>

<div id="pureJobsErrorsBox" class="fleft" style="display:none;">
	<div class="widget-container">
		<div class="widget-header">Pure Jobs Errors</div>
		<div class="widget-body">		
			<div id="pureJobsErrorDiv"></div>
		</div>
	</div>
</div>
<div id="CpuHeapErrorsBox" class="fleft" style="display:none;">
	<div class="widget-container">
		<div class="widget-header">Static Profiling Error </div>
		<div class="widget-body">		
			<div id="CpuHeapErrorDiv"></div>
		</div>
	</div>
</div>
<div id="instrumentalJobsErrorsBox" class="fleft" style="display:none;">
	<div class="widget-container">
		<div class="widget-header">Instrumented Jobs Errors</div>
		<div class="widget-body">		
			<div id="insJobsErrorDiv"></div>
		</div>
	</div>
</div>

<div id="profilingErrorsBox" class="fleft" style="display:none;">
	<div class="widget-container">
		<div class="widget-header">Profiling Errors</div>
		<div class="widget-body">		
			<div id="profilingErrorDiv"></div>
		</div>
	</div>
</div>

<div id="dataScienceErrorsBox" class="fleft" style="display:none;">
	<div class="widget-container">
		<div class="widget-header">Data Science Errors</div>
		<div class="widget-body">		
			<div id="dataScienceErrorDiv"></div>
		</div>
	</div>
</div>

<div id="debugAnalyzerErrorsBox" class="fleft" style="display:none;">
	<div class="widget-container">
		<div class="widget-header">Debug Analyzer Errors</div>
		<div class="widget-body">		
			<div id="debugAnalyzerErrorDiv"></div>
		</div>
	</div>
</div>

<div id="dataValidationErrorsBox" class="fleft" style="display:none;">
	<div class="widget-container">
		<div class="widget-header">Data Validation Errors</div>
		<div class="widget-body">		
			<div id="dataValidationErrorDiv"></div>
		</div>
	</div>
</div>

<div id="tuningErrorsBox" class="fleft" style="display:none;">
	<div class="widget-container">
		<div class="widget-header">Tuning Errors</div>
		<div class="widget-body">		
			<div id="tuningErrorDiv"></div>
		</div>
	</div>
</div>



<script language="javascript">


$(document).ready(function () { 

	var tabLength = $( "#tabs" ).tabs('length');	
	$( "#tabs" ).bind( "tabsshow", function(event, ui) {
		if(tabLength == ui.index+1)
		{			
			$('#pureJobsErrortable tr:nth-child(even)').addClass("eventablerow");
			$('#insJobsErrortable tr:nth-child(even)').addClass("eventablerow");
		}
		
	});
});

</script>