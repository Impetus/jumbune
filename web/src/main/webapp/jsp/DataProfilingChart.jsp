<style>
.dvrightdatabox {float:right;border-left:solid 1px #ccc;width:60%}
#dvReportTablePager_left {width: 20px !important;}
#dvReportTablePager_center {width: 250px !important;}
</style>
<!--div class="pageTopPane">
	<h2 class="pageTitle">DataProfiling</h2>
	<div id="dataValidationErrorLinks" class="errorLinkBox"></div>
</div-->
<!--div style="clear:both"></div-->

<div class="widget-container" id="dataProfilingFieldDataWrap">
<table id="records_table" border='1'>
</table>
</div>

	<!--div class="widget-header">Data Profiling Table  <a href="javascript:void(0);" id="resetDVFieldData" ui:target="">Reset</a></div>
	<div class="widget-body">
		<div class="dvdatabox">
			<table id="dvTable"></table>
		</div>
		<div class="dvrightdatabox">
			<table id="dvReportTable"></table>
			<div id="dvReportTablePager" ></div>
		</div>
		<div id="dvFieldChart" class="fright"></div>
</div-->
	<div id="chart1">
<script type="text/javascript">

function enableDataProfilingTab(dpJson) {
	
    var profiledOutput = [];
    var counter = 0 ;
	var result = $.parseJSON(dpJson);
	$.each(result, function(k, v) {
		if(counter<=4){
		profiledOutput.push([k,v]);
		}
		counter = counter + 1 ;
});

        $.jqplot('chart1', [profiledOutput],
{
		grid:
		{
			drawBorder:false,
			shadow:false,
		},
		gridPadding:{top:0,right:0,bottom:0,left:0},
		seriesColors:["#baadea","#7f8eda","#f4bd29","#93570a","#84dcf2"],
		legend:{show:true},
		seriesDefaults:
		{
		renderer:$.jqplot.PieRenderer,
		rendererOptions:
        {
        showDataLabels:true,
        dataLabelPositionFactor:.75,
        shadowOffset:0,
        fill: false,
        lineWidth:5,
        sliceMargin:4,
        startAngle:-90,
        highlightMouseOver:false,
        padding:10
        }
    }
});

    }
</script>
</div>
