
<div id="treeGridBox">
	<div class="pageTopPane">
		<h2 class="pageTitle">Cluster-wide MapReduce Job Execution Analysis(Debug) Report - </h2>
		<div class="pageTopPaneRight" id="debugRptViewOpt">
			<div class="tblChartOpts">
				<span class="optLbl">Table View -</span><br />
				<a href="javascript:void(0);" id="tblView" class="viewOpt" ui:target="debugreportTableView"><span class="selView"></span></a>
			</div>
			<div class="tblChartOptsSap"></div>
			<div class="tblChartOpts">
				<span class="optLbl">Chart View -</span><br />
				<a href="javascript:void(0);" id="pieChartView" class="viewOpt" ui:target="debugreportPieChartView"><span></span></a>
			</div>
		</div>
	</div>

	<div id="debugreportTableView" class="debugReportTblChartViewCnt">	

		<div class="commonBox breadcrumbwrap">
			<span class="fleft"><b>You are here:</b>&nbsp;</span><div id="breadcrumBox" class="fleft" style=""></div>
		</div>

		<div class="viewChangeLinks">
			<a id="jobsViewLink" href="javascript:void(0);" class="active">J</a>
			<a id="mapsViewLink"  href="javascript:void(0);">M</a>
			<a id="instViewLink"  href="javascript:void(0);">I</a>
		</div>
		
		<div id="debugMainGridBox" class="analyserReportWrap">
			<div class="widget-container jmritablewrap">
				<div class="widget-header"><div class="title">Jobs</div><div class="fright"><a id="jobsChainSorting" href="#" >chain</a></div><div id="jobActiveGrid" class="activegrid" style="display:block;"></div></div>
				<div class="widget-body">
					<div id="jobptreegrid"></div>
				</div>
			</div>

			<div class="widget-container jmritablewrap">
				<div class="widget-header"><div class="title">Mappers/Reducers</div><div class="fright"><a id="mapsChainSorting" href="#" >chain</a></div><div id="mapActiveGrid" class="activegrid"></div></div>
				<div class="widget-body">
					<div id="mapptreegrid"></div>
				</div>
			</div>

			<div class="widget-container jmritablewrap">
				<div class="widget-header"><div class="title">Instances</div><div id="insActiveGrid" class="activegrid"></div></div>
				<div class="widget-body">
					<div id="insptreegrid"></div>
				</div>
			</div>
		</div>	
		
		<div class="legendwrap">
			<div class="commonBox">
				<ul id="classLegendBox">
					<li><span class="mapper_ident"></span>Mapper</li>
					<li><span class="reducer_ident"></span>Reducer</li>
				</ul>
			</div>	
			<div class="commonBox">
				<ul id="nodeLegendBox"></ul>
			</div>
		</div>

		<div class="widget-container" id="insSubCounter" style="display:none;">
			<div class="widget-header"></div>
			<div class="widget-body relative">
				<div class="taskcountersdivlegend">
					<span class="rootmap">Map</span>
					<span class="method">Method</span>
					<span class="counter">Counter</span>
				</div>
				<div class="clear taskcountersdiv" id="taskCountersDiv"><ul></ul></div>
				<div id="taskCountersTableWrap" class="taskcounterstablewrap"></div>
			</div>
		</div>	
	</div>

	<div class="clear"></div>
	<div id="debugreportPieChartView" class="debugReportTblChartViewCnt" style="display:none;">
		<div class="topBar widget-header"></div>
		<table width="100%" height="30px" cellpadding="0" cellspacing="0">
			<tr>
				<th width="40%">&nbsp;</th>
				<th width="15%" align="center">Input Keys</th>
				<th width="15%" align="center" class="selCol">Context Writes</th>
				<th width="15%" align="center">Unmatched Keys</th>
				<th width="15%" align="center" class="last">Unmatched Values</th>
			</tr>
		</table>
		<div id="debugReportAccordion"></div>
	</div>
</div>

<script type="text/javascript">


$('<table id="jobtreegrid"></table>').appendTo('#jobptreegrid');
$('<table id="maptreegrid"></table>').appendTo('#mapptreegrid');
$('<table id="instreegrid"></table>').appendTo('#insptreegrid');
var logJobChurningData=[];	
var logMapChurningData=[];
var logInsChurningData=[];
allTaskCounterData=[];


var logAnalysisJSONString= '{"debugAnalysis":{"logMap":{"job_201204231517_0075":{"jobMap":{"com.impetus.neustar.portps.reducer.PortOutReportReducer":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_r_000000_0":{"instanceMap":{"reduce":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"43","counterMap":{"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"44","totalInputKeys":1,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":1}},"totalInputKeys":1,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":1},"loop1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"50","counterMap":{"com.impetus.neustar.portps.reducer.PortOutReportReducer.getTNReportData":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":229,"totalUnmatchedKeys":1,"totalUnmatchedValues":1},"loop2":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"72","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"com.impetus.neustar.portps.reducer.PortOutReportReducer.addNewLine":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":1,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":231,"totalUnmatchedKeys":2,"totalUnmatchedValues":2}},"totalInputKeys":1,"totalContextWrites":231,"totalUnmatchedKeys":2,"totalUnmatchedValues":2}},"totalInputKeys":1,"totalContextWrites":231,"totalUnmatchedKeys":2,"totalUnmatchedValues":2}},"totalInputKeys":1,"totalContextWrites":231,"totalUnmatchedKeys":2,"totalUnmatchedValues":2},"com.impetus.neustar.portps.mappers.old.Mapper1":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_m_000001_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":375,"totalContextWrites":375,"totalUnmatchedKeys":375,"totalUnmatchedValues":375}},"totalInputKeys":375,"totalContextWrites":375,"totalUnmatchedKeys":375,"totalUnmatchedValues":375},"attempt_201204231517_0075_m_000000_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":460,"totalContextWrites":460,"totalUnmatchedKeys":460,"totalUnmatchedValues":460}},"totalInputKeys":460,"totalContextWrites":460,"totalUnmatchedKeys":460,"totalUnmatchedValues":460},"com.impetus.neustar.portps.mappers.old.Mapper2":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_m_000001_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":144,"totalContextWrites":144,"totalUnmatchedKeys":144,"totalUnmatchedValues":144}},"totalInputKeys":144,"totalContextWrites":144,"totalUnmatchedKeys":144,"totalUnmatchedValues":144},"attempt_201204231517_0075_m_000000_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":229,"totalContextWrites":229,"totalUnmatchedKeys":229,"totalUnmatchedValues":229}},"totalInputKeys":229,"totalContextWrites":229,"totalUnmatchedKeys":229,"totalUnmatchedValues":229},"com.impetus.neustar.portps.mappers.old.Mapper3":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_m_000001_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":144,"totalContextWrites":144,"totalUnmatchedKeys":144,"totalUnmatchedValues":144}},"totalInputKeys":144,"totalContextWrites":144,"totalUnmatchedKeys":144,"totalUnmatchedValues":144},"attempt_201204231517_0075_m_000000_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":229,"totalContextWrites":229,"totalUnmatchedKeys":229,"totalUnmatchedValues":229}},"totalInputKeys":229,"totalContextWrites":229,"totalUnmatchedKeys":229,"totalUnmatchedValues":229},"com.impetus.neustar.portps.mappers.old.PortOutReportMapper":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_m_000001_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"com.impetus.neustar.portps.PortPsHelper.tokenize":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"loop1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"43","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"37","counterMap":{"com.impetus.neustar.portps.PortPsHelper.isValid":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"61","counterMap":{"com.impetus.neustar.portps.PortPsHelper.isEmptyOrNull":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"63","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"else":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"41","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.handleNestedIf":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"61","counterMap":{"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"62","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"73","counterMap":{"else":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"76","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"switch1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"45","counterMap":{"case1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"49","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":2,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":2,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"attempt_201204231517_0075_m_000000_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"com.impetus.neustar.portps.PortPsHelper.tokenize":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"loop1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"43","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"37","counterMap":{"com.impetus.neustar.portps.PortPsHelper.isValid":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"61","counterMap":{"com.impetus.neustar.portps.PortPsHelper.isEmptyOrNull":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"63","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"else":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"41","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.handleNestedIf":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"61","counterMap":{"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"62","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"73","counterMap":{"else":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"76","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"switch1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"45","counterMap":{"case1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"49","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":2,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":2,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":4,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":4,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":923,"totalContextWrites":1149,"totalUnmatchedKeys":920,"totalUnmatchedValues":920},"job_201204231517_0076":{"jobMap":{"com.impetus.neustar.portps.reducer.PortOutReportReducer":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_r_000000_0":{"instanceMap":{"reduce":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"43","counterMap":{"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"44","totalInputKeys":1,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":1}},"totalInputKeys":1,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":1},"loop1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"50","counterMap":{"com.impetus.neustar.portps.reducer.PortOutReportReducer.getTNReportData":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":229,"totalUnmatchedKeys":1,"totalUnmatchedValues":1},"loop2":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"72","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"com.impetus.neustar.portps.reducer.PortOutReportReducer.addNewLine":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":1,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":231,"totalUnmatchedKeys":2,"totalUnmatchedValues":2}},"totalInputKeys":1,"totalContextWrites":231,"totalUnmatchedKeys":2,"totalUnmatchedValues":2}},"totalInputKeys":1,"totalContextWrites":231,"totalUnmatchedKeys":2,"totalUnmatchedValues":2}},"totalInputKeys":1,"totalContextWrites":231,"totalUnmatchedKeys":2,"totalUnmatchedValues":2},"com.impetus.neustar.portps.mappers.old.Mapper1":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_m_000001_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":375,"totalContextWrites":375,"totalUnmatchedKeys":375,"totalUnmatchedValues":375}},"totalInputKeys":375,"totalContextWrites":375,"totalUnmatchedKeys":375,"totalUnmatchedValues":375},"attempt_201204231517_0075_m_000000_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":460,"totalContextWrites":460,"totalUnmatchedKeys":460,"totalUnmatchedValues":460}},"totalInputKeys":460,"totalContextWrites":460,"totalUnmatchedKeys":460,"totalUnmatchedValues":460},"com.impetus.neustar.portps.mappers.old.Mapper2":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_m_000001_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":144,"totalContextWrites":144,"totalUnmatchedKeys":144,"totalUnmatchedValues":144}},"totalInputKeys":144,"totalContextWrites":144,"totalUnmatchedKeys":144,"totalUnmatchedValues":144},"attempt_201204231517_0075_m_000000_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":229,"totalContextWrites":229,"totalUnmatchedKeys":229,"totalUnmatchedValues":229}},"totalInputKeys":229,"totalContextWrites":229,"totalUnmatchedKeys":229,"totalUnmatchedValues":229},"com.impetus.neustar.portps.mappers.old.Mapper3":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_m_000001_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":144,"totalContextWrites":144,"totalUnmatchedKeys":144,"totalUnmatchedValues":144}},"totalInputKeys":144,"totalContextWrites":144,"totalUnmatchedKeys":144,"totalUnmatchedValues":144},"attempt_201204231517_0075_m_000000_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":229,"totalContextWrites":229,"totalUnmatchedKeys":229,"totalUnmatchedValues":229}},"totalInputKeys":229,"totalContextWrites":229,"totalUnmatchedKeys":229,"totalUnmatchedValues":229},"com.impetus.neustar.portps.mappers.old.PortOutReportMapper":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_m_000001_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"com.impetus.neustar.portps.PortPsHelper.tokenize":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"loop1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"43","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"37","counterMap":{"com.impetus.neustar.portps.PortPsHelper.isValid":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"61","counterMap":{"com.impetus.neustar.portps.PortPsHelper.isEmptyOrNull":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"63","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"else":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"41","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.handleNestedIf":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"61","counterMap":{"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"62","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"73","counterMap":{"else":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"76","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"switch1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"45","counterMap":{"case1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"49","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":2,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":2,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"attempt_201204231517_0075_m_000000_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"com.impetus.neustar.portps.PortPsHelper.tokenize":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"loop1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"43","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"37","counterMap":{"com.impetus.neustar.portps.PortPsHelper.isValid":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"61","counterMap":{"com.impetus.neustar.portps.PortPsHelper.isEmptyOrNull":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"63","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"else":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"41","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.handleNestedIf":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"61","counterMap":{"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"62","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"73","counterMap":{"else":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"76","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"switch1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"45","counterMap":{"case1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"49","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":2,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":2,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":4,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":4,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":923,"totalContextWrites":1149,"totalUnmatchedKeys":920,"totalUnmatchedValues":920},"job_201204231517_0077":{"jobMap":{"com.impetus.neustar.portps.reducer.PortOutReportReducer":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_r_000000_0":{"instanceMap":{"reduce":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"43","counterMap":{"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"44","totalInputKeys":1,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":1}},"totalInputKeys":1,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":1},"loop1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"50","counterMap":{"com.impetus.neustar.portps.reducer.PortOutReportReducer.getTNReportData":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":229,"totalUnmatchedKeys":1,"totalUnmatchedValues":1},"loop2":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"72","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"com.impetus.neustar.portps.reducer.PortOutReportReducer.addNewLine":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":1,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":231,"totalUnmatchedKeys":2,"totalUnmatchedValues":2}},"totalInputKeys":1,"totalContextWrites":231,"totalUnmatchedKeys":2,"totalUnmatchedValues":2}},"totalInputKeys":1,"totalContextWrites":231,"totalUnmatchedKeys":2,"totalUnmatchedValues":2}},"totalInputKeys":1,"totalContextWrites":231,"totalUnmatchedKeys":2,"totalUnmatchedValues":2},"com.impetus.neustar.portps.mappers.old.Mapper1":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_m_000001_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":375,"totalContextWrites":375,"totalUnmatchedKeys":375,"totalUnmatchedValues":375}},"totalInputKeys":375,"totalContextWrites":375,"totalUnmatchedKeys":375,"totalUnmatchedValues":375},"attempt_201204231517_0075_m_000000_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":460,"totalContextWrites":460,"totalUnmatchedKeys":460,"totalUnmatchedValues":460}},"totalInputKeys":460,"totalContextWrites":460,"totalUnmatchedKeys":460,"totalUnmatchedValues":460},"com.impetus.neustar.portps.mappers.old.Mapper2":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_m_000001_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":144,"totalContextWrites":144,"totalUnmatchedKeys":144,"totalUnmatchedValues":144}},"totalInputKeys":144,"totalContextWrites":144,"totalUnmatchedKeys":144,"totalUnmatchedValues":144},"attempt_201204231517_0075_m_000000_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":229,"totalContextWrites":229,"totalUnmatchedKeys":229,"totalUnmatchedValues":229}},"totalInputKeys":229,"totalContextWrites":229,"totalUnmatchedKeys":229,"totalUnmatchedValues":229},"com.impetus.neustar.portps.mappers.old.Mapper3":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_m_000001_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":144,"totalContextWrites":144,"totalUnmatchedKeys":144,"totalUnmatchedValues":144}},"totalInputKeys":144,"totalContextWrites":144,"totalUnmatchedKeys":144,"totalUnmatchedValues":144},"attempt_201204231517_0075_m_000000_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":85,"totalContextWrites":85,"totalUnmatchedKeys":85,"totalUnmatchedValues":85}},"totalInputKeys":229,"totalContextWrites":229,"totalUnmatchedKeys":229,"totalUnmatchedValues":229}},"totalInputKeys":229,"totalContextWrites":229,"totalUnmatchedKeys":229,"totalUnmatchedValues":229},"com.impetus.neustar.portps.mappers.old.PortOutReportMapper":{"mapReduceMap":{"192.168.49.71":{"nodeMap":{"attempt_201204231517_0075_m_000001_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"com.impetus.neustar.portps.PortPsHelper.tokenize":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"loop1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"43","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"37","counterMap":{"com.impetus.neustar.portps.PortPsHelper.isValid":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"61","counterMap":{"com.impetus.neustar.portps.PortPsHelper.isEmptyOrNull":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"63","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"else":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"41","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.handleNestedIf":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"61","counterMap":{"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"62","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"73","counterMap":{"else":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"76","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"switch1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"45","counterMap":{"case1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"49","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":2,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":2,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"attempt_201204231517_0075_m_000000_0":{"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"com.impetus.neustar.portps.PortPsHelper.tokenize":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"loop1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"43","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"37","counterMap":{"com.impetus.neustar.portps.PortPsHelper.isValid":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"61","counterMap":{"com.impetus.neustar.portps.PortPsHelper.isEmptyOrNull":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"63","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"else":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"41","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.handleNestedIf":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"61","counterMap":{"if1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"62","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"IfBlock1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"73","counterMap":{"else":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"76","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0},"switch1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"45","counterMap":{"case1":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"49","counterMap":{"com.impetus.neustar.portps.mappers.old.PortOutReportMapper.print":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":1,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":2,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":2,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":4,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":4,"totalContextWrites":0,"totalUnmatchedKeys":0,"totalUnmatchedValues":0}},"totalInputKeys":923,"totalContextWrites":1149,"totalUnmatchedKeys":920,"totalUnmatchedValues":920}},"mrChain":{"job_201204231517_0075":[{"name":"com.impetus.neustar.portps.mappers.old.PortOutReportMapper","inputKeys":4,"contextWrites":0},{"name":"com.impetus.neustar.portps.mappers.old.Mapper1","inputKeys":460,"contextWrites":460},{"name":"com.impetus.neustar.portps.mappers.old.Mapper2","inputKeys":229,"contextWrites":229},{"name":"com.impetus.neustar.portps.mappers.old.Mapper3","inputKeys":229,"contextWrites":229}]},"jobChain":[{"name":"job_201204231517_0076","inputKeys":923,"contextWrites":1149},{"name":"job_201204231517_0077","inputKeys":923,"contextWrites":1149}]}}';

var logAnalysisJSONStringObj =  $.parseJSON(logAnalysisJSONString);
var numOfJobs=0;

$.each(logAnalysisJSONStringObj, function(logName, logBean){
var logMap=logBean["logMap"];
var jobChain=logBean["jobChain"];
var mrChain=logBean["mrChain"];

$.each(logMap, function(jobId, jobBean){	
	numOfJobs++;
	var totalInputKeys=jobBean["totalInputKeys"];
	var totalContextWrites=jobBean["totalContextWrites"];
	var totalUnmatchedKeys=jobBean["totalUnmatchedKeys"];
	var totalUnmatchedValues=jobBean["totalUnmatchedValues"];
	var jobMap=jobBean["jobMap"];
	var jobChainName="";
	var jobChainCounter="";
	var jobChainCount=1;

	$.each(jobChain, function(jobChainId, jobChainBean){	
		
		if(jobId == jobChainBean["name"])
		{
			jobChainName="<img src='../skins/images/down_arrow.gif' />";
			jobChainCounter = jobChainCount;
			//var jobChainInputKeys=jobChainBean["inputKeys"];
			//var jobChainContextWrites=jobChainBean["contextWrites"];
		}		
		jobChainCount++;

	});
	if(!jobChainCounter)
	{
		jobChainCounter = 9999;
	}
	//console.log(jobMap);
	var logChurningJsonObj = { "chain":jobChainName, "id":jobChainCounter,"elementName":"<div class='jobDetails'>"+jobId+"<form></form></div>","totalInputKeys":totalInputKeys,"totalContextWrites":totalContextWrites,"totalUnmatchedKeys":totalUnmatchedKeys,"totalUnmatchedValues":totalUnmatchedValues,level:"0", parent:"", isLeaf:false, expanded:false, loaded:true};
	logJobChurningData.push(logChurningJsonObj);

		var numOfMapReduce=0;
		$.each(jobMap, function(mapReduceName, mapReduceBean){														 
			numOfMapReduce++;
			var totalMapReduceInputKeys=mapReduceBean["totalInputKeys"];
			var totalMapReduceContextWrites=mapReduceBean["totalContextWrites"];
			var totalMapReduceUnmatchedKeys=mapReduceBean["totalUnmatchedKeys"];
			var totalMapReduceUnmatchedValues=mapReduceBean["totalUnmatchedValues"];
			var mapReduceMap=mapReduceBean["mapReduceMap"];
			var mapChainName="";
			var mapChainCounter="";
			var mapChainCount=1;
			
			$.each(mrChain, function(mrJobChainId, mrJobChainBean){
								
				if(jobId == mrJobChainId)
				{	
					//var marJobChain=mrJobChainBean[jobId];
										
					$.each(mrJobChainBean, function(mrMapChainId, mrMapChainBean){					

					if(mapReduceName == mrMapChainBean["name"])
					{
						mapChainName="<img src='../skins/images/down_arrow.gif' />";
						mapChainCounter = mapChainCount;
						//var mapChainInputKeys=mrMapChainBean["inputKeys"];
						//var mapChainContextWrites=mrMapChainBean["contextWrites"];
					}		
					mapChainCount++;

					});
				}

			});
			if(!mapChainCounter)
			{
				mapChainCounter = 9999999;
			}

			
			var mapReduceJsonObj = { "chain":mapChainName, "id":mapChainCounter,"elementName":"<div class='classDetails'>"+mapReduceName+"<form></form></div>","totalInputKeys":totalMapReduceInputKeys,"totalContextWrites":totalMapReduceContextWrites,"totalUnmatchedKeys":totalMapReduceUnmatchedKeys,"totalUnmatchedValues":totalMapReduceUnmatchedValues,level:"1", parent:numOfJobs, isLeaf:false, expanded:false, loaded:true};
			
			logMapChurningData.push(mapReduceJsonObj);  

		var numOfNodes=0;
		$.each(mapReduceMap, function(nodeName, nodeBean){
			numOfNodes++;
			var totalNodeInputKeys=nodeBean["totalInputKeys"];
			var totalNodeContextWrites=nodeBean["totalContextWrites"];
			var totalNodeUnmatchedKeys=nodeBean["totalUnmatchedKeys"];
			var totalNodeUnmatchedValues=nodeBean["totalUnmatchedValues"];
			var nodeMap=nodeBean["nodeMap"];
			var numOfInstance=0;
			var nodeJsonObj = { "id":numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes,"elementName":"<div class='nodeDetails'>"+nodeName+"<form></form></div>","totalInputKeys":totalNodeInputKeys,"totalContextWrites":totalNodeContextWrites,"totalUnmatchedKeys":totalNodeUnmatchedKeys,"totalUnmatchedValues":totalNodeUnmatchedValues,level:"2", parent:numOfJobs+'_'+numOfMapReduce, isLeaf:false, expanded:false, loaded:true};
			//logChurningData.push(nodeJsonObj); 
			//$('#nodeLegendBox').append(nodeName);
			$.each(nodeMap, function(instanceName, instanceBean){

				numOfInstance++;
				var totalInstanceInputKeys=instanceBean["totalInputKeys"];
				var totalInstanceContextWrites=instanceBean["totalContextWrites"];
				var totalInstanceUnmatchedKeys=instanceBean["totalUnmatchedKeys"];
				var totalInstanceUnmatchedValues=instanceBean["totalUnmatchedValues"];
				var taskName=instanceName;
				var instanceMap=instanceBean["instanceMap"];
				var instanceJsonObj = { "id":numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes+'_'+numOfInstance,"elementName":"<div class='modal' style='cursor:pointer'>"+taskName+" <form><input type='hidden' name='elementName' value="+instanceName+" id='elementName' /></form></div>","totalInputKeys":totalInstanceInputKeys,"totalContextWrites":totalInstanceContextWrites,"totalUnmatchedKeys":totalInstanceUnmatchedKeys,"totalUnmatchedValues":totalInstanceUnmatchedValues,level:"3", parent:numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes, isLeaf:true, expanded:false, loaded:true};
				logInsChurningData.push(instanceJsonObj); 

				var taskCounterData=[];
				$.each(instanceMap, function(counterName, counterBean){
					if(counterName.indexOf("contextWrite")!=-1)
					{
						var totalFilteredIn=counterBean["totalFilteredIn"];
						var totalCounterContextWrites=counterBean["totalContextWrites"];
						var totalCounterUnmatchedKeys=counterBean["totalUnmatchedKeys"];
						var totalCounterUnmatchedValues=counterBean["totalUnmatchedValues"];
						var totalFilteredOut=counterBean["totalFilteredOut"];
						var counterMap2=counterBean["counterMap"];
						var counterJsonObj = {"counterName":counterName,"filteredIn":'-',"contextWrites":totalCounterContextWrites,"unmatchedKeys":totalCounterUnmatchedKeys,"unmatchedValues":totalCounterUnmatchedValues,"filteredOut":'-',level:"0", parent:counterName, isLeaf:true, expanded:false, loaded:true};
						taskCounterData.push(counterJsonObj); 
					}
					else
					{													
						var totalFilteredIn=counterBean["totalFilteredIn"];
						var totalCounterContextWrites=counterBean["totalContextWrites"];
						var totalCounterUnmatchedKeys=counterBean["totalUnmatchedKeys"];
						var totalCounterUnmatchedValues=counterBean["totalUnmatchedValues"];
						var totalFilteredOut=counterBean["totalFilteredOut"];
						var counterMap2=counterBean["counterMap"];
						var counterJsonObj = {"counterName":counterName,"filteredIn":totalFilteredIn,"contextWrites":totalCounterContextWrites,"unmatchedKeys":totalCounterUnmatchedKeys,"unmatchedValues":totalCounterUnmatchedValues,"filteredOut":totalFilteredOut,level:"0", parent:counterName, isLeaf:true, expanded:false, loaded:true};
						taskCounterData.push(counterJsonObj); 
					}
					
					if(typeof counterMap2 != 'undefined')
					{
						$.each(counterMap2, function(counterName2, counterBean2){
							
							var totalFilteredIn=counterBean2["totalFilteredIn"];
							var totalCounterContextWrites=counterBean2["totalContextWrites"];
							var totalCounterUnmatchedKeys=counterBean2["totalUnmatchedKeys"];
							var totalCounterUnmatchedValues=counterBean2["totalUnmatchedValues"];
							var totalFilteredOut=counterBean2["totalFilteredOut"];
							var counterMap3=counterBean2["counterMap"];	
							
							var counterJsonObj = {"counterName":counterName2,"filteredIn":'-',"contextWrites":totalCounterContextWrites,"unmatchedKeys":totalCounterUnmatchedKeys,"unmatchedValues":totalCounterUnmatchedValues,"filteredOut":'-',level:"1", parent:counterName, isLeaf:true, expanded:false, loaded:true};
							//console.log(counterJsonObj);
							taskCounterData.push(counterJsonObj);

							if(typeof counterMap3 != 'undefined')
							{
								$.each(counterMap3, function(counterName3, counterBean3){
								
								var totalFilteredIn=counterBean3["totalFilteredIn"];
								var totalCounterContextWrites=counterBean3["totalContextWrites"];
								var totalCounterUnmatchedKeys=counterBean3["totalUnmatchedKeys"];
								var totalCounterUnmatchedValues=counterBean3["totalUnmatchedValues"];
								var totalFilteredOut=counterBean3["totalFilteredOut"];
								var counterMap4=counterBean3["counterMap"];	
								//console.log(counterMap3);
								var counterJsonObj = {"counterName":counterName3,"filteredIn":'-',"contextWrites":totalCounterContextWrites,"unmatchedKeys":totalCounterUnmatchedKeys,"unmatchedValues":totalCounterUnmatchedValues,"filteredOut":'-',level:"2", parent:counterName, isLeaf:true, expanded:false, loaded:true};
								//console.log(counterJsonObj);
								taskCounterData.push(counterJsonObj);	
								
									if(typeof counterMap4 != 'undefined')
									{
										$.each(counterMap4, function(counterName4, counterBean4){
										
										var totalFilteredIn=counterBean4["totalFilteredIn"];
										var totalCounterContextWrites=counterBean4["totalContextWrites"];
										var totalCounterUnmatchedKeys=counterBean4["totalUnmatchedKeys"];
										var totalCounterUnmatchedValues=counterBean4["totalUnmatchedValues"];
										var totalFilteredOut=counterBean4["totalFilteredOut"];
										var counterMap5=counterBean4["counterMap"];	
										//console.log(counterMap4);
										var counterJsonObj = {"counterName":counterName4,"filteredIn":'-',"contextWrites":totalCounterContextWrites,"unmatchedKeys":totalCounterUnmatchedKeys,"unmatchedValues":totalCounterUnmatchedValues,"filteredOut":'-',level:"3", parent:counterName, isLeaf:true, expanded:false, loaded:true};
										//console.log(counterJsonObj);
										taskCounterData.push(counterJsonObj);							

										});					
									}

								});					
							}

						});

					}

				});

				allTaskCounterData[instanceName]=taskCounterData;
			});
		});
	});	
});
});


//job Grid
var logJobAnalysisJson={"response": logJobChurningData};    								    
jobGrid = jQuery("#jobtreegrid");
jobGrid.jqGrid({
	datastr: logJobAnalysisJson,
	datatype: "jsonstring",
	height: 199,
	sortable:true,
	sortname:"id",
	sortorder:"desc",	
	hidegrid:false,
	loadui: "disable",
	scrollOffset: 0,
	colNames:["Chain", "ID", "Name","Input Keys","Context Writes","Unmatched Keys", "Unmatched Values"], 
	colModel:
		[ 			
			{name:'chain',index:'chain', width:20, align:"center", hidden:true},
			{name:'id',index:'id', width:10, align:"center", hidden:true},
			{name:'elementName',index:'elementName', width:217, align:"left", classes:"jobfirstcol"},
			{name:'totalInputKeys',index:'totalInputKeys', width:70,align:"center"}, 
			{name:'totalContextWrites',index:'totalContextWrites', width:70,align:"center"},
			{name:'totalUnmatchedKeys',index:'totalUnmatchedKeys', width:70,align:"center"},
			{name:'totalUnmatchedValues',index:'totalUnmatchedValues', width:70,align:"center"}
		],												
	rowNum: 10000,
	jsonReader: {
		repeatitems: false,
		root: "response"
	}
});


//job map/reduce grid
var logMapAnalysisJson={"response": logMapChurningData};    								    
mapGrid = jQuery("#maptreegrid");
mapGrid.jqGrid({
	datastr: logMapAnalysisJson,
	datatype: "jsonstring",
	height: 178,
	hidegrid:false,
	loadui: "disable",
	scrollOffset: 0,
	colNames:["Chain", "ID", "Name","Input Keys","Context Writes","Unmatched Keys", "Unmatched Values"], 
	colModel:
		[ 
			{name:'chain',index:'chain', width:20, align:"center", hidden:true},
			{name:'id',index:'id', width:10, align:"center", hidden:true},
			{name:'elementName',index:'elementName', width:216, align:"left", formatter:classTitleShort, classes:"mrfirstcol"},
			{name:'totalInputKeys',index:'totalInputKeys', width:70,align:"center", hidden: true}, 
			{name:'totalContextWrites',index:'totalContextWrites', width:70, align:"center", hidden: true},
			{name:'totalUnmatchedKeys',index:'totalUnmatchedKeys', width:70, align:"center", hidden: true},
			{name:'totalUnmatchedValues',index:'totalUnmatchedValues', width:70, align:"center", hidden: true}
		],												
	rowNum: 10000,
	footerrow:true,	
	userDataOnFooter: true, 
	altRows: true, 
	jsonReader: {
		repeatitems: false,
		root: "response"
	}
});

function classTitleShort (cellvalue, options, rowObject) {
	// do something here
	if(cellvalue.indexOf(".") != -1 ) {
		var valueArray = cellvalue.split('.'); 
		valueArray = "<span class='legendBullets mrlegendBullets mapper_ident'></span><div class='classDetails'>"+valueArray[valueArray.length-1]+"</div>";
		return valueArray;
	}else {
		return cellvalue;
	}
}

function classTitleShortFromInstanceTable (cellvalue, options, rowObject)
{

	if(cellvalue.indexOf(".") != -1 )  
	{
	   var valueArray = cellvalue.split('.'); 
	   valueArray = "<div class='classDetails'>"+valueArray[valueArray.length-1]+"</div>";
	   return valueArray;
	}
	else
	{
		return cellvalue;
	}
	
}

//job intance
var logInsAnalysisJson={"response": logInsChurningData};    								    
insGrid = jQuery("#instreegrid");
insGrid.jqGrid({
	datastr: logInsAnalysisJson,
	datatype: "jsonstring",
	height: 157,
	hidegrid:false,
	loadui: "disable",
	scrollOffset: 0,
	colNames:["chain", "ID", "Name","Input Keys","Context Writes","Unmatched Keys", "Unmatched Values"], 
	colModel:
		[ 
			{name:'chain',index:'chain', width:20,align:"center", hidden: true},
			{name:'id',index:'id', width:20,align:"center", hidden: true},
			{name:'elementName',index:'elementName', width:216, align:"left", formatter:classTitleShortFromInstanceTable, classes:"insfirstcol"},
			{name:'totalInputKeys',index:'totalInputKeys', width:70,align:"center", hidden: true}, 
			{name:'totalContextWrites',index:'totalContextWrites', width:70,align:"center", hidden: true},
			{name:'totalUnmatchedKeys',index:'totalUnmatchedKeys', width:70,align:"center", hidden: true},
			{name:'totalUnmatchedValues',index:'totalUnmatchedValues', width:70,align:"center", hidden: true}
		],												
	rowNum: 10000,
	footerrow:true,
	userDataOnFooter: true, 
	altRows: true, 
	jsonReader: {
	repeatitems: false,
	root: "response"
	}
});

$("#jobsViewLink").click(function(){
	$(".viewChangeLinks").find("a").each(function() {
		$(this).removeClass("active");
	});
	$(this).addClass("active");

	$("#debugMainGridBox").find(".activegrid").each(function() {
		$(this).fadeOut(300);
	});
	$("#jobActiveGrid").fadeIn(1500);
	
	$("#jobptreegrid").animate({width: "524px"}, 500);
	$("#mapptreegrid, #insptreegrid").animate({width: "223px"}, 500);

	$('#jobtreegrid').jqGrid("showCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
	$('#maptreegrid, #instreegrid').jqGrid("hideCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
	
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv td').hide();
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(0) td:eq(0)').show();
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1) td:eq(0)').show();

	// Set all grid width
	$('#jobtreegrid').jqGrid().setGridWidth(522);
	$('#maptreegrid, #instreegrid').jqGrid().setGridWidth(221);
});

$("#mapsViewLink").click(function(){
	$(".viewChangeLinks").find("a").each(function() {
		$(this).removeClass("active");
	});
	$(this).addClass("active");

	$("#debugMainGridBox").find(".activegrid").each(function() {
		$(this).fadeOut(300);
	});
	$("#mapActiveGrid").fadeIn(1500);

	$("#jobptreegrid").animate({width: "224px"}, 500);
	$("#mapptreegrid").animate({width: "523px"}, 500);
	$("#insptreegrid").animate({width: "223px"}, 500);

	$('#maptreegrid').jqGrid("showCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
	$('#jobtreegrid, #instreegrid').jqGrid("hideCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
	
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv td').hide();
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(0) td:eq(0)').show();
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1) td:eq(0)').show();
	
	//autoAdjustGridHeight();
	// Set all grid width
	$('#jobtreegrid').jqGrid().setGridWidth(222);
	$('#maptreegrid').jqGrid().setGridWidth(521);
	$('#instreegrid').jqGrid().setGridWidth(221);
});

$("#instViewLink").click(function(){
	$(".viewChangeLinks").find("a").each(function() {
		$(this).removeClass("active");
	});
	$(this).addClass("active");

	$("#debugMainGridBox").find(".activegrid").each(function() {
		$(this).fadeOut(300);
	});
	$("#insActiveGrid").fadeIn(1500);

	$("#jobptreegrid").animate({width: "224px"}, 500);
	$("#mapptreegrid").animate({width: "223px"}, 500);
	$("#insptreegrid").animate({width: "523px"}, 500);

	$('#instreegrid').jqGrid("showCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
	$('#jobtreegrid, #maptreegrid').jqGrid("hideCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
	
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv td').show();

	// Set all grid width
	$('#jobtreegrid').jqGrid().setGridWidth(222);
	$('#maptreegrid').jqGrid().setGridWidth(221);
	$('#instreegrid').jqGrid().setGridWidth(521);
});

//Click on jobs 
$("#jobtreegrid tr").live('click', function(){
	var jobTitle = $(this).find('.jobDetails').text();
		
	numOfMapReduce=0;
	var logJobChurningData=[];	
	var logMapChurningData=[];
	var logMapFooterChurningData=[];
	var logInsChurningData=[];
	
	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];
		$.each(logMap, function(jobId, jobBean){
			if(jobId == jobTitle)
			{
				numOfJobs++;
				var totalInputKeys=jobBean["totalInputKeys"];
				var totalContextWrites=jobBean["totalContextWrites"];
				var totalUnmatchedKeys=jobBean["totalUnmatchedKeys"];
				var totalUnmatchedValues=jobBean["totalUnmatchedValues"];
				var jobMap=jobBean["jobMap"];
				var logChurningJsonObj = { "chain":" ", "id":numOfJobs,"elementName":"<div class='jobDetails'>"+jobId+"<form></form></div>","totalInputKeys":totalInputKeys,"totalContextWrites":totalContextWrites,"totalUnmatchedKeys":totalUnmatchedKeys,"totalUnmatchedValues":totalUnmatchedValues,level:"0", parent:"", isLeaf:false, expanded:false, loaded:true};
				logMapFooterChurningData.push(logChurningJsonObj);
			}
		});
	});

	// push maps/reduces
	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];
	var mrChain=logBean["mrChain"];

		$.each(logMap[jobTitle].jobMap, function(mapReduceName, mapReduceBean){
			numOfMapReduce++;
			var totalMapReduceInputKeys=mapReduceBean["totalInputKeys"];
			var totalMapReduceContextWrites=mapReduceBean["totalContextWrites"];
			var totalMapReduceUnmatchedKeys=mapReduceBean["totalUnmatchedKeys"];
			var totalMapReduceUnmatchedValues=mapReduceBean["totalUnmatchedValues"];
			var mapReduceMap=mapReduceBean["mapReduceMap"];
			var mapChainName="";
			var mapChainCounter="";
			var mapChainCount=1;
			
			$.each(mrChain, function(mrJobChainId, mrJobChainBean){
								
				if(jobTitle == mrJobChainId)
				{	
					//var marJobChain=mrJobChainBean[jobId];
										
					$.each(mrJobChainBean, function(mrMapChainId, mrMapChainBean){					

					if(mapReduceName == mrMapChainBean["name"])
					{
						mapChainName="<img src='../skins/images/down_arrow.gif' />";
						mapChainCounter = mapChainCount;
						//var mapChainInputKeys=mrMapChainBean["inputKeys"];
						//var mapChainContextWrites=mrMapChainBean["contextWrites"];
					}		
					mapChainCount++;

					});
				}

			});
			if(!mapChainCounter)
			{
				mapChainCounter = 9999999;
			}

			var mapReduceJsonObj = { "chain":mapChainName, "id":mapChainCounter, "elementName":"<div class='classDetails'>"+mapReduceName+"<form></form></div>","totalInputKeys":totalMapReduceInputKeys,"totalContextWrites":totalMapReduceContextWrites,"totalUnmatchedKeys":totalMapReduceUnmatchedKeys,"totalUnmatchedValues":totalMapReduceUnmatchedValues,level:"1", parent:numOfJobs, isLeaf:false, expanded:false, loaded:true};		
			logMapChurningData.push(mapReduceJsonObj);	
		});
	});
	mapGrid.clearGridData();
	mapGrid.jqGrid('footerData','set',logMapFooterChurningData[0]);
	mapGrid.jqGrid("setGridParam", { 'data': logMapChurningData }).trigger("reloadGrid");	
	
	trIndex =$(this).index();
	
	$("#maptreegrid tr:nth-child(2)").trigger('click');

});


//Click on maps/reduces
$("#maptreegrid tr").live('click',function(){	
	var jobTitle = $("#jobtreegrid tr:nth-child("+parseInt(trIndex+1)+")").find('.jobDetails').text();	
	var mapTitle = $(this).find('.classDetails').text();	
	var insTitle = $(this).find('.modal').text();
		
	// push instances
	var logJobChurningData=[];	
	var logMapChurningData=[];
	var logNodChurningData=[];
	var logInsChurningData=[];
	var logInsFooterChurningData=[];
	numOfInstance=0;
	numOfNodes=0;
	numOfMapReduce=0;

	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];

		$.each(logMap, function(jobId, jobBean){
			if(jobId == jobTitle)
			{
				//Insert job name in breadcrum
				$('#breadcrumBox').html(jobId);

				numOfJobs++;
				var totalInputKeys=jobBean["totalInputKeys"];
				var totalContextWrites=jobBean["totalContextWrites"];
				var totalUnmatchedKeys=jobBean["totalUnmatchedKeys"];
				var totalUnmatchedValues=jobBean["totalUnmatchedValues"];
				var jobMap=jobBean["jobMap"];
				

				var logChurningJsonObj = { "chain":" ", "id":99999,"elementName":"<div class='jobDetails'>"+jobId+"<form></form></div>","totalInputKeys":totalInputKeys,"totalContextWrites":totalContextWrites,"totalUnmatchedKeys":totalUnmatchedKeys,"totalUnmatchedValues":totalUnmatchedValues,level:"0", parent:"", isLeaf:false, expanded:false, loaded:true};			
				logInsFooterChurningData.push(logChurningJsonObj);
			}
		});
	});

	
	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];

		$.each(logMap[jobTitle].jobMap, function(mapReduceName, mapReduceBean){
			numOfMapReduce++;	
			var valueArray = mapReduceName.split('.');
					
			if(mapTitle == valueArray[valueArray.length-1])
			{
				//Insert mapper/reducer name in breadcrum			
				$('#breadcrumBox').append("<span class='raquo'>&raquo;</span>"+ mapTitle);

				var totalMapReduceInputKeys=mapReduceBean["totalInputKeys"];
				var totalMapReduceContextWrites=mapReduceBean["totalContextWrites"];
				var totalMapReduceUnmatchedKeys=mapReduceBean["totalUnmatchedKeys"];
				var totalMapReduceUnmatchedValues=mapReduceBean["totalUnmatchedValues"];
				var mapReduceMap=mapReduceBean["mapReduceMap"];
				var mapReduceJsonObj = { "chain":" ", "id":9999999, "id":numOfJobs+'_'+numOfMapReduce,"elementName":"<div class='classDetails'>"+mapReduceName+"<form></form></div>","totalInputKeys":totalMapReduceInputKeys,"totalContextWrites":totalMapReduceContextWrites,"totalUnmatchedKeys":totalMapReduceUnmatchedKeys,"totalUnmatchedValues":totalMapReduceUnmatchedValues,level:"1", parent:numOfJobs, isLeaf:false, expanded:false, loaded:true};	
				logInsFooterChurningData.push(mapReduceJsonObj);	
			

				$.each(mapReduceMap, function(nodeName, nodeBean){
					numOfNodes++;
					var nodeMap=nodeBean["nodeMap"];				
					logNodChurningData.push(nodeName);				
					var nodeColor = colorCodes[numOfNodes];

					$.each(nodeMap, function(instanceName, instanceBean){				
						numOfInstance++;
						var totalInstanceInputKeys=instanceBean["totalInputKeys"];
						var totalInstanceContextWrites=instanceBean["totalContextWrites"];
						var totalInstanceUnmatchedKeys=instanceBean["totalUnmatchedKeys"];
						var totalInstanceUnmatchedValues=instanceBean["totalUnmatchedValues"];
						var taskName=instanceName;
						
						var instanceMap=instanceBean["instanceMap"];
						var instanceJsonObj = { "id":numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes+'_'+numOfInstance,"elementName":"<span class='legendBullets' style='background-color:"+nodeColor+"'></span><div class='modal' style='cursor:pointer'>"+taskName+" <form><input type='hidden' name='elementName' value="+instanceName+" id='elementName' /></form></div>","totalInputKeys":totalInstanceInputKeys,"totalContextWrites":totalInstanceContextWrites,"totalUnmatchedKeys":totalInstanceUnmatchedKeys,"totalUnmatchedValues":totalInstanceUnmatchedValues,level:"3", parent:numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes, isLeaf:true, expanded:false, loaded:true};
						
						logInsChurningData.push(instanceJsonObj);		
					});
				});
			
			}
			else
				var mapReduceMap=mapReduceBean["mapReduceMap"];

		});
	});
	
	insGrid.clearGridData();	
	
	$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1)').remove();
	
	insGrid.jqGrid("setGridParam", { 'data': logInsChurningData }).trigger("reloadGrid");
	insGrid.jqGrid('footerData','set',logInsFooterChurningData[0]);
	var copyMapHtml = $(this).clone().html().replace(/maptreegrid/g, "instreegrid");	
	//console.log($(this).clone().html());
	$(this).clone().insertAfter($('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(0)'));
	$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1)').html().replace(/maptreegrid/g, "instreegrid");
	$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1)').removeClass().addClass('ui-widget-content footrow footrow-ltr');
	
	

	$('#nodeLegendBox').html('');
	for(var i=0;i<logNodChurningData.length;i++)
	{		
		$('#nodeLegendBox').append("<li><span class='legendBullets' style='background-color:"+colorCodes[i+1]+"'></span>"+logNodChurningData[i]+"</li>");
	}

	$('#insptreegrid').find('div.ui-jqgrid-sdiv td').show();
	$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(0) td:eq(0), div.ui-jqgrid-sdiv tr:eq(0) td:eq(1)').hide();
	$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1) td:eq(0), div.ui-jqgrid-sdiv tr:eq(1) td:eq(1)').hide();

	// Set vertical scroller to Job grid
	var jobBDivHeight = $("#jobptreegrid .ui-jqgrid-bdiv").height();
	var jobBDivTableHeight = $("#jobptreegrid .ui-jqgrid-bdiv div:first").height();
	if(jobBDivTableHeight >= jobBDivHeight) {
		jobGrid.jqGrid("setGridParam", { 'scrollOffset': 18 });
	}

	// Set vertical scroller to Mappers/Reducers grid
	var mapBDivHeight = $("#mapptreegrid .ui-jqgrid-bdiv").height();
	var mapBDivTableHeight = $("#mapptreegrid .ui-jqgrid-bdiv div:first").height();
	if(mapBDivTableHeight >= mapBDivHeight) {
		mapGrid.jqGrid("setGridParam", { 'scrollOffset': 18 });
	}
	
	// Set vertical to Instance grid
	var insBDivHeight = $("#insptreegrid .ui-jqgrid-bdiv").height();
	var insBDivTableHeight = $("#insptreegrid .ui-jqgrid-bdiv div:first").height();
	if(insBDivTableHeight >= insBDivHeight) {
		insGrid.jqGrid("setGridParam", { 'scrollOffset': 18 });
	}
	
	
});

$("#jobtreegrid tr:nth-child(2)").trigger('click'); // default trigger first row click of jobs table

// Instance counter sub table 
var mapCounterData = new Array();
$(document).ready(function() {
	$(".modal").live('click', function(){
		var serializedData=$(this).find('form').serialize();		
		var  tempName=serializedData.split('=');
		var elementName=tempName[1];
		
		//Insert instance name in breadcrum		
		if($('#breadcrumBox:contains('+elementName+')').length==0)
		$('#breadcrumBox').append("<span class='raquo'>&raquo;</span>"+ elementName);
		
		$("#insSubCounter").show().find(".widget-header").html(elementName);
		

		$.each(logAnalysisJSONStringObj, function(logName, logBean){
		var logMap=logBean["logMap"];
			$.each(logMap, function(jobId, jobBean){
				var jobMap=jobBean["jobMap"];
				$.each(jobMap, function(mapReduceName, mapReduceBean){
					var mapReduceMap=mapReduceBean["mapReduceMap"];
					$.each(mapReduceMap, function(nodeName, nodeBean){
						var nodeMap=nodeBean["nodeMap"];
						$.each(nodeMap, function(instanceName, instanceBean){
							if(elementName == instanceName) {
								var instanceMap=instanceBean["instanceMap"];
								var taskCounterData='';
								$.each(instanceMap, function(counterName, counterBean){
									var counterMap2=counterBean["counterMap"];
									mapCounterData[0] = counterMap2;
									taskCounterData += '<li id="phtml_1"><a class="rootmap" title="Map" href="javascript:void(0);" onclick="javascript:createTreeTable(0);"></a>';
									if(typeof counterMap2 != 'undefined'){	
										taskCounterData +='<ul>';
										taskCounterData = createTreeNode(counterMap2, taskCounterData);
										taskCounterData +='</ul></li>';
									}
								});
								$('#taskCountersDiv ul:first').html(taskCounterData);
							}
						});
					});
				});	
			});
		});

		$("#taskCountersDiv").jstree({
			"plugins" : ["themes","html_data"],
			"core" : { "initially_open" : [ "phtml_1" ] }
		}).bind("before.jstree", function (e, data) {
			if(data.func === "close_node") {
				e.stopImmediatePropagation();
				return false;
			}
		});
		setTimeout(function () { $("#taskCountersDiv").jstree("open_all"); }, 500);
		$("#taskCountersDiv").find("a.rootmap").trigger("click");
	});


	$('#mapptreegrid').find('div.ui-jqgrid-sdiv').insertBefore($('#mapptreegrid').find('div.ui-jqgrid-bdiv'));
	$('#insptreegrid').find('div.ui-jqgrid-sdiv').insertBefore($('#insptreegrid').find('div.ui-jqgrid-bdiv'));
});

// Create tree
function createTreeNode(counterMap, taskCounterData) {
	var dataType='';
	$.each(counterMap, function(counterName, counterBean){
		var counterMapObj = counterBean["counterMap"];
		dataType = counterBean["counterDetails"];
		var lngth = mapCounterData.length;
		mapCounterData[lngth] = counterMapObj;
		if(dataType == "method") {
			taskCounterData +='<li><a class="method" href="javascript:void(0);" title="'+counterName+'" onclick="javascript:createTreeTable('+lngth+');"></a>';
		}else {
			taskCounterData +='<li><a class="counters" href="javascript:void(0);" title="'+counterName+' (Line no: '+counterBean["counterDetails"]+')" onclick="javascript:createTreeTable('+lngth+');"></a>';
		}

		if(typeof counterMapObj != 'undefined') {
			taskCounterData +='<ul>';
			taskCounterData = createTreeNode(counterMapObj, taskCounterData);
			taskCounterData +='</ul></li>';
		}
	});
	return taskCounterData;
}

// Create tree table
function createTreeTable(itemObj) {
	var taskCounterData = new Array();
	if(mapCounterData[itemObj]) {
		$.each(mapCounterData[itemObj], function(counterKey, counterVal) {
			var totalFilteredIn = counterVal["totalFilteredIn"];
			var totalContextWrites = counterVal["totalContextWrites"];
			var totalUnmatchedKeys = counterVal["totalUnmatchedKeys"];
			var totalUnmatchedValues = counterVal["totalUnmatchedValues"];
			var totalFilteredOut = counterVal["totalFilteredOut"];
			var counterDetails = counterVal["counterDetails"];
				
			//if(counterDetails != "method") {
				var counterJsonObj = {"counterName":counterKey,"totalFilteredIn":totalFilteredIn,"totalContextWrites":totalContextWrites,"totalUnmatchedKeys":totalUnmatchedKeys,"totalUnmatchedValues":totalUnmatchedValues,"totalFilteredOut":totalFilteredOut};
				taskCounterData.push(counterJsonObj);
			//}
		});
	}else {
		taskCounterData = [];
	}

	var taskCounters = {"response": taskCounterData};
	if(jQuery.isEmptyObject(taskCounters["response"]) == true) {
		$("#taskCountersTableWrap").html('<div class="status info"><span>Information: </span>No data available.</div>');
		$("#taskCountersTableWrap .status").delay(300).fadeTo(150, 0.5).fadeTo(150, 1).fadeTo(150, 0.5).fadeTo(150, 1);
	}else {
		$("#taskCountersTableWrap").html('<table id="taskCountersTable"></table>');
	}

	$("#taskCountersTable").jqGrid({ 
		datastr: taskCounters,
		datatype: "jsonstring",
		height: "auto",
		hidegrid:false,			
		colNames:['Counter Name','Filtered In','Context Writes','Unmatched Keys','Unmatched Values','Filtered Out'],
		colModel:[ 
					{name:'counterName',index:'counterName'},
					{name:'totalFilteredIn',index:'totalFilteredIn', width:70, align:'center'}, 
					{name:'totalContextWrites',index:'totalContextWrites', width:70, align:'center'},
					{name:'totalUnmatchedKeys',index:'totalUnmatchedKeys', width:70, align:'center'},
					{name:'totalUnmatchedValues',index:'totalUnmatchedValues', width:70, align:'center'},
					{name:'totalFilteredOut',index:'totalFilteredOut', width:70, align:'center'}
		],			
		rowNum:1000, 
		jsonReader: {
			repeatitems: false,
			root: "response"
		}
	});
	
}

// Chart View Start
var chartViewContent='';
var chartViewCounter=0;
var mapReduceChartArr=new Array();
var insChartArr=new Array();
$(function () {//createDebugReportPieCharts() {

	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];	
		$.each(logMap, function(jobId, jobBean) {
			chartViewContent = '<h3><table width="100%" height="100%" cellpadding="0" cellspacing="0"><tr><td width="40%" align="left"><div class="seljob">'+jobId+'</div></td><td width="15%" align="center">'+jobBean["totalInputKeys"]+'</td><td width="15%" align="center" class="selCol">'+jobBean["totalContextWrites"]+'</td><td width="15%" align="center">'+jobBean["totalUnmatchedKeys"]+'</td><td width="15%" align="center" class="last">'+jobBean["totalUnmatchedValues"]+'</td></tr></table></h3><div><div class="debugreportchartholder" id="MapReduceChartHolder_'+chartViewCounter+'"></div><div class="debugreportchartholder" id="insChartHolder_'+chartViewCounter+'"></div><div class="charttitle">Mappers/Reducers</div><div class="charttitle">Instances</div></div>';
			$("#debugReportAccordion").append(chartViewContent);
			createMapReducePieChart("MapReduceChartHolder_"+chartViewCounter, jobId, jobBean);
			createInstancePieChart("insChartHolder_"+chartViewCounter, jobId, jobBean);
			chartViewCounter++;
			chartViewContent = '';
		});
	});
	
	$("#debugReportAccordion").accordion();
});

function createMapReducePieChart(mrChartHolder, jobId, jobBean) {
	var colorCounter = 0;
	mapReduceChartArr = new Array();
	var colors = Highcharts.getOptions().colors;
	
	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];
		$.each(logMap[jobId].jobMap, function(mapReduceName, mapReduceBean){
			if(colorCounter < 1){
				mapReduceChartArr.push({
					name: mapReduceName,
					y: Math.round(((mapReduceBean["totalContextWrites"]*100)/jobBean["totalContextWrites"])*100)/100,
					color: colors[colorCounter],
					sliced: true,
					selected: true
				});
			}else {
				mapReduceChartArr.push({
					name: mapReduceName,
					y: Math.round(((mapReduceBean["totalContextWrites"]*100)/jobBean["totalContextWrites"])*100)/100,
					color: colors[colorCounter]
				});
			}
			colorCounter++;
		});
	});
	
	// Create the chart
	var mrChart = new Highcharts.Chart({
		chart: {
			renderTo: mrChartHolder,
			plotBackgroundColor: null,
			plotBorderWidth: null,
			plotShadow: false,
			width: 450,
			height: 250
		},
		title: {
			text: ''
		},
		tooltip: false,
		plotOptions: {
			pie: {
				allowPointSelect: true,
				cursor: 'pointer',
				dataLabels: {
					formatter: function() {
						var labelName = this.point.name.split("$");
						return '<b>'+ labelName[labelName.length-1] +':</b> ' + this.y + '%';
					}
				},
				showInLegend: false
			}
		},
		series: [{
			type: 'pie',
			point: {
				events: {
					click: function() { }
				}
			},
			data: mapReduceChartArr
		}]
	});
}

function createInstancePieChart(iChartHolder, jobId, jobBean) {
	var colorCounter = 0;
	insChartArr = new Array();
	var colors = Highcharts.getOptions().colors;

	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];
		$.each(logMap[jobId].jobMap, function(mapReduceName, mapReduceBean){
			var mapReduceMap=mapReduceBean["mapReduceMap"];
			if(colorCounter < 1) {
				$.each(mapReduceMap, function(nodeName, nodeBean){
					var nodeMap=nodeBean["nodeMap"];
					$.each(nodeMap, function(instanceName, instanceBean){
						insChartArr.push({
							name: instanceName,
							y: Math.round(((instanceBean["totalContextWrites"]*100)/mapReduceBean["totalContextWrites"])*100)/100,
							color: colors[colorCounter]
						});
						colorCounter++;
					});
				});
			}
		});
	});

	// Create the chart
	var colors = Highcharts.getOptions().colors;
	var iChart = new Highcharts.Chart({
		chart: {
			renderTo: iChartHolder,
			plotBackgroundColor: null,
			plotBorderWidth: null,
			plotShadow: false,
			width: 450,
			height: 250
		},
		title: {
			text: ''
		},
		tooltip: false,
		plotOptions: {
			pie: {
				allowPointSelect: true,
				cursor: 'pointer',
				dataLabels: {
					formatter: function() {
						return '<b>'+ this.point.name +':</b> ' + this.y + '%';
					}
				},
				showInLegend: false
			}
		},
		series: [{
			type: 'pie',
			point: {
				events: {
					click: function() { }
				}
			},
			data: insChartArr
		}]
	});
}

$("#debugRptViewOpt").find("a.viewOpt").click(function() {
	$("#debugRptViewOpt").find("a.viewOpt").each(function() {
		$(this).find("span").removeClass("selView");
	});
	$(this).find("span").addClass("selView");
	var targetView = $(this).attr("ui:target");
	$("div.debugReportTblChartViewCnt").each(function() {
		$(this).css({"display":"none"});
	});
	$("div#"+targetView).css({"display":"block"});
	if(targetView == "debugreportPieChartView") {
		//createDebugReportPieCharts();
		$("#debugReportAccordion").find("div.ui-accordion-content").each(function() {
			$(this).css({"height":"auto"});
		});
	}
});

$("#jobsChainSorting").click(function (){
	$("#jobtreegrid").jqGrid('setGridParam',{sortname: 'id', sortorder: 'asc'});
	$('#jobtreegrid').jqGrid("showCol", ["chain"]);
	$('#jobtreegrid').trigger("reloadGrid");
});

$("#mapsChainSorting").click(function (){
	$("#maptreegrid").jqGrid('setGridParam',{sortname: 'id', sortorder: 'asc'});
	$('#maptreegrid').jqGrid("showCol", ["chain"]);
	$('#maptreegrid').trigger("reloadGrid");
});


// Chart View End
</script>