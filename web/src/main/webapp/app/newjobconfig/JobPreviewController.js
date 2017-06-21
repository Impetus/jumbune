/* NewJobConfig controller */
'use strict';
angular.module('jobpreview.ctrl', [])
    
    .controller('JobPreviewController', ['$scope', '$rootScope','common', 'JobFactory', '$compile', '$location', 'Wizard', '$http', function ($scope, $rootScope, common, JobFactory, $compile, $location, Wizard, $http) {
        var self = this;
        //Report Json Read
        self.clusterMonitoringJson = common.getClusterMonitoringJsonData();
        self.dpWithCriteriaJson = common.getDataProfilerWithCriteriaJsonData();
        self.dpNoCriteriaJson = common.getDataProfilerNoCriteriaJsonData();
        self.dataQualityJson = common.getDataQualityTimelineJsonData();
        self.dataValidationJson = common.getDataValidationJsonData();
        self.debuggerJson = common.getDebuggerJsonData();
        self.jobProfilingJson = common.getJobProfilingJsonData();
        self.tuningJson = common.getTuningJsonData();
        //End of Report Json Read

        self.user = null;
        self.errorJson = {};
		self.displayJSON = {};
		self.cluster = {};
		self.getJobDtl ={};
		self.defJobDtl ={};
		self.widgetsDtl ={};
		self.dataConfigDtl ={};
		self.dataAnalysisChart = common.dataAnalysisChart;
        self.cancel = function(){
            $location.path('/job-widget');
        }
        self.init = function() {
        	self.json = null;
        	self.validJson = false;
            self.cluster =  common.getSelectedClusterNameForRun();
			self.getJobDtl = common.getJobDetails();
			self.defJobDtl = common.getDefineJobInfo();
			self.widgetsDtl = common.getWidgetInfo();
			self.dataConfigDtl = common.getDataConfigDtl();
			self.enableDataQualityTimeline = common.getEnableDataQualityTimeline();
			//self.clusterConfigDtl = common.getClusterConfig();
			self.clusterConfigDtl = common.getFieldCaption(self.cluster);
			self.getJobDtl.monitor && (self.displayJSON.Monitor =  'Cluster Monitoring');
			
			if(self.defJobDtl.choosedSystem !== undefined){
				self.displayJSON.Choosed_System = 'Choosed System - '+self.defJobDtl.choosedSystem;
				self.defJobDtl.isJarManifest && (self.displayJSON.Jar_Manifest =  'Jar Manifesting');
			}
			if(self.widgetsDtl.debuggerDtl !== undefined){
				self.widgetsDtl.debuggerDtl.enableDebChk && (self.displayJSON.enableDebugger =  'Debugger');
				//self.widgetsDtl.debuggerDtl.useRegexChk && (self.displayJSON.useRegexChk =  'Use Regex');
				self.displayJSON.useRegexChk =  'Use Regex';
				self.displayJSON.usrDefChk =  'User Defined Validations';
				//self.widgetsDtl.debuggerDtl.usrDefChk && (self.displayJSON.usrDefChk =  'User Defined Validations');
			}
			if(self.widgetsDtl.whatIfDtl !== undefined){
				self.widgetsDtl.whatIfDtl.enableWhatIf && (self.displayJSON.enableWhatIf =  'What If');
			}
			if(self.widgetsDtl.jobProfilingDtl !== undefined){
				self.widgetsDtl.jobProfilingDtl.enableprofilingCheck && (self.displayJSON.enableprofilingCheck =  'Job Profiling');
				self.widgetsDtl.jobProfilingDtl.runFromJumbune && (self.displayJSON.runFromJumbune =  'Run From Jumbune');
			}
			if(self.widgetsDtl.tuningDtl !== undefined){
				self.widgetsDtl.tuningDtl.enableTuningChk && (self.displayJSON.enableTuning =  'Tuning');
			}
			if(self.widgetsDtl.dataValidation !== undefined){
				//self.widgetsDtl.dataValidation.enableDataValidation && (self.displayJSON.enableDataValidation =  'Data Validation');
				self.displayJSON.enableDataValidation =  'Data Validation';
			}
			if(self.dataConfigDtl !== undefined){
				//self.dataConfigDtl.enblDataValidation && (self.displayJSON.enblDataValidation = 'Data Validation');
				self.displayJSON.enableDataValidation =  'Data Validation';
				self.dataConfigDtl.enblDataQuality && (self.displayJSON.enblDataValidation = 'View Data Quality');
				self.dataConfigDtl.enblDataProfiler && (self.displayJSON.enblDataValidation = 'View Data Profiler');
			}
			console.log('Final JSON of Enable Fields',self.displayJSON);
        };

        self.gotoTabField = function(field) {
          console.log(field);
        };

        self.saveJob = function () {
	    	$("#saveJSONForm").submit();
        };

        self.runJob = function(){
	    	self.saveFinalMultipartReq();
        };

        self.validJson = false;

        self.validateJson = function(obj) {
        	var json = obj.json;
        	console.log("on validate...", json);
    		Wizard.validate({
            }, json, function(data) {
            	if(data && data.Failures) {
            		self.errorJson = data;
            	} else {
            		console.log("Validated Successfully:::", data);
            		self.validJson = true;
            	}
            }, function(e) {
            	console.log("ERROR::: in validation::", e);
            });
        };
		
		self.saveFinalMultipartReq = function() {
			var reqObj = {
    			method: 'POST',
    			url: "/ExecutionServlet",
    			headers: {'Content-Type': undefined},
    			transformRequest: function(data) {
    				var formData = new FormData();
    				console.log("TransformingRequest::::", common.getJobJarFile());
    				formData.append("file", common.getJobJarFile().file);
    				formData.append("json", data.json);
    				return formData;
    			},
    			data: {json: self.json, file: ""}
    		};

    		$http(reqObj);
		};
		
		self.json = null;
		self.JsonToServer = function(){
			self.validJson = false;

			var isFairSchedulerEnabled = false;
			var useStandardWordCountJar = true;
			
			/* Save JSON */
			var saveJSON = {};
			saveJSON = {
				"enableDataQualityTimeline" : self.enableDataQualityTimeline,
				"debugAnalysis": "TRUE",
				"jumbuneJobName": self.getJobDtl.jobName,
				"hadoopJobProfile": "TRUE",
				"enableYarn": "TRUE",
				"enableDataValidation": "TRUE",
				"enableStaticJobProfiling": "TRUE",
                "partitionerSampleInterval":0,
                "slaveWorkingDirectory": "/home/impadmin/EETeam/",
				"profilingParams": 
					{
						"mapers": "0-1",
						"reducers": "0-1",
						"statsInterval": 5000
					}
			};
			saveJSON.master = {};
			if(self.clusterConfigDtl !== undefined){
				saveJSON.master.resourceManagerJmxPort = "5680";
				saveJSON.master.user = self.clusterConfigDtl.userMN;
				saveJSON.master.host = self.clusterConfigDtl.hostMN;
				saveJSON.master.location = "/home/impadmin/jumbune/jobJars/DEMO_ALL_1/dv/";
				saveJSON.master.rsaFile = self.clusterConfigDtl.fileTypeMN;
				saveJSON.master.agentPort = self.clusterConfigDtl.agentPortMN;
				saveJSON.master.nameNodeJmxPort = self.clusterConfigDtl.jmxPortMN;
				saveJSON.master.jobTrackerJmxPort = self.clusterConfigDtl.jobTrackerPortMN;
				saveJSON.master.isNodeAvailable = false;
			}
			
			saveJSON.slaves = [];
			if(self.clusterConfigDtl !== undefined){
				var noOfObjInSlaves = self.clusterConfigDtl.nodeArr.length;
				var noofhosts = "";
				for(var i=0; i< noOfObjInSlaves; i++){                                    //Missed key by sanjay
					saveJSON.slaves[i] = {"user":self.clusterConfigDtl.nodeArr[i].userDN,"location":"/home/impadmin/EETeam/jobJars/45/dv/*"};
					saveJSON.slaves[i].hosts = [];
					noofhosts = self.clusterConfigDtl.nodeArr[i].hostDN.length;
					for(var j=0; j< noofhosts; j++){
						saveJSON.slaves[i].hosts.push(self.clusterConfigDtl.nodeArr[i].hostDN[j].host);
					}
				}
			}
			
			saveJSON.jobs = [];
			var noOfJobs = self.defJobDtl.allJobInfo.length;
			for(var i=0; i< noOfJobs;i++){
				saveJSON.jobs[i] = {
					"name": self.defJobDtl.allJobInfo[i].jobName,
					"parameters": self.defJobDtl.allJobInfo[i].jobParameter 
				}
			}
			
			if(self.widgetsDtl.dataValidation !== undefined){
				saveJSON.enableDataValidation = self.widgetsDtl.dataValidation.enableDataValidation;
				if(saveJSON.enableDataValidation == true){
					
					saveJSON.dataValidation = {};
					saveJSON.dataValidation.fieldValidationList = [];
					var tempObj = {};
					saveJSON.hdfsInputPath = self.widgetsDtl.dataValidation.hdfsInputPath;
					saveJSON.dataValidation.recordSeparator = self.widgetsDtl.dataValidation.tupleRecordSeparator;
					saveJSON.dataValidation.fieldSeparator = self.widgetsDtl.dataValidation.tupleFieldSeparator;
					saveJSON.dataValidation.numOfFields = self.widgetsDtl.dataValidation.fieldCount;
					var fieldCount = saveJSON.dataValidation.numOfFields;
					for(var index = 0;index < fieldCount; index++){
						tempObj = {
							fieldNumber:index+1,
							nullCheck: self.widgetsDtl.dataValidation.enableRowData[index].nullCheck,
							dataType: self.widgetsDtl.dataValidation.enableRowData[index].fieldType,
							regEx: self.widgetsDtl.dataValidation.enableRowData[index].regEx
						};
						saveJSON.dataValidation.fieldValidationList[index] = tempObj;
					}
				}	
			}
			
			if(self.defJobDtl !== undefined){
				saveJSON.includeClassJar = self.defJobDtl.isJarManifest;
				saveJSON.inputFile = self.defJobDtl.addJar;
			}
			
			if(self.widgetsDtl.jobProfilingDtl !== undefined){
				saveJSON.enableJobProfiling = self.widgetsDtl.jobProfilingDtl.enableprofilingCheck;
				if(saveJSON.enableJobProfiling == true){
					saveJSON.runJobFromJumbune = self.widgetsDtl.jobProfilingDtl.runFromJumbune;
				}
			}
			
			if(self.widgetsDtl.debuggerDtl !== undefined){
				saveJSON.debuggerConf ={};
				saveJSON.debuggerConf.logLevel ={};
				saveJSON.debuggerConf.logLevel.instrumentRegex = self.widgetsDtl.debuggerDtl.useRegexChk;
				saveJSON.debuggerConf.logLevel.instrumentUserDefValidate = self.widgetsDtl.debuggerDtl.usrDefChk;
				saveJSON.debuggerConf.maxIfBlockNestingLevel = 0;
				saveJSON.logKeyValues = self.widgetsDtl.debuggerDtl.enableLoggingOfUnmatched;
				saveJSON.regexValidations = [];
				saveJSON.regexValidations.push(self.widgetsDtl.debuggerDtl.useRegex);
				saveJSON.userValidations = [];
				saveJSON.userValidations.push(self.widgetsDtl.debuggerDtl.udv);
			}

            saveJSON.classpath = {};
            saveJSON.classpath.userSupplied = {"source": "-1"};

            saveJSON.isLocalSystemJar = "FALSE";
            saveJSON.enableDataProfiling = "FALSE";
            saveJSON.criteriaBasedDataProfiling = "FALSE";
			
			if(self.widgetsDtl.tuningDtl !== undefined){
				saveJSON.selfTuning = !self.widgetsDtl.tuningDtl.enableTuningChk;
				saveJSON.clusterTuning = {};
				saveJSON.clusterTuning.applyConfig = self.widgetsDtl.tuningDtl.enableTuningChk;
				if(saveJSON.clusterTuning.applyConfig == true){
					if(self.widgetsDtl.tuningDtl.resourceShareRadios == 'fairScheduler'){
						isFairSchedulerEnabled = true;
					}
					if(self.widgetsDtl.tuningDtl.mapReduceJarRadios == 'userSuppliedJar'){
						useStandardWordCountJar = false;
					}
					saveJSON.clusterTuning.hadoopPerformance = self.widgetsDtl.tuningDtl.jobTypeOpn;
					saveJSON.clusterTuning.isFairSchedulerEnabled = isFairSchedulerEnabled;
					saveJSON.clusterTuning.availableMapTasks = self.widgetsDtl.tuningDtl.mapSlotsText;
					saveJSON.clusterTuning.availableReduceTasks = self.widgetsDtl.tuningDtl.reduceSlotsText;
					saveJSON.clusterTuning.jobInputPath = self.widgetsDtl.tuningDtl.jobDataPathText;
					saveJSON.clusterTuning.outputFolder = self.widgetsDtl.tuningDtl.outputFileText;
					saveJSON.clusterTuning.useStandardWordCountJar = useStandardWordCountJar;
					/* "objective": "REDUCE_TIME_OF_JOB_EXECUTION", NA */
				}	
			}
            saveJSON.slaveParam=  {
                "nodeManagerJmxPort": "5680",
                    "dataNodeJmxPort": "5679",
                    "taskTrackerJmxPort": "5678"
            }

			if(self.widgetsDtl.whatIfDtl !== undefined){
				saveJSON.enableWhatIf = self.widgetsDtl.whatIfDtl.enableWhatIf;
				if(saveJSON.enableWhatIf == true){
					saveJSON.selectWhatIf = self.widgetsDtl.whatIfDtl.selectBoxWhatIf;
					saveJSON.toField = self.widgetsDtl.whatIfDtl.toField;
				}
			}

			console.log('saveJSON',saveJSON);

			//json.jumbuneJobName = saveJSON.jumbuneJobName;
			
			self.json = JSON.stringify(saveJSON);
			console.log("sadfsdf:::", self.json, saveJSON);
			self.validateJson({"json": self.json});
		};
		

/**
 * Data Profiling - Criteria
 * @type {number[]}
 */
        var fieldValues= [];    // ["1", "5"]
        var dataValues = [];    // ["100102", "12216"]
        var ruleValues = [];    // [">=120049950.000", "<=1981260901.000"]
        for(var i=0; i<self.dpWithCriteriaJson.length; i++) {
            for (var key in self.dpWithCriteriaJson[i]) {
                if(!isNaN(key)) {
                    fieldValues.push(key);
                    for(var keyVal in self.dpWithCriteriaJson[i][key]){
                        if(!isNaN(keyVal)) {
                            dataValues.push(keyVal);
                        }else{
                            ruleValues.push(self.dpWithCriteriaJson[i][key][keyVal]);
                        }
                    }
                }
            }
        }
        self.dtextF1 = ("Field-"+fieldValues[0]).concat(" ["+ruleValues[0]+"]");
        self.dtextF2 = ("Field-"+fieldValues[1]).concat(" ["+ruleValues[1]+"]");

        self.fillColorF1 = 'green';
        self.fillColorF2 = 'red';

/**
 * Data Profiling - No Criteria
 * @type {*[]}
 */
       var chartData = [];
        var keyArray = [];
        for(var i=0; i<self.dpNoCriteriaJson.length; i++) {
            for (var key in self.dpNoCriteriaJson[i]) {
                if(key.indexOf('$')=== -1) {
                    keyArray = [];
                    keyArray.push(key);
                    keyArray.push(self.dpNoCriteriaJson[i][key]);
                    chartData.push(keyArray);
                }
            }
        }

        self.pieChartModel = {
            chartData : [chartData],
            chartOptions : {
                grid:
                {
                    drawBorder:false,
                    shadow:false
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
                },
                axes:{
                    xaxis: {
                        tickOptions:{
                            showGridline: false,
                            showMark: false,
                            showLabel: false,
                            shadow: false
                        }
                    },
                    yaxis: {
                        tickOptions:{
                            showGridline: false,
                            showMark: false,
                            showLabel: false,
                            shadow: false
                        }
                    }
                }
            }
        };
//END OF DATA PROFILING


/**
 * Data Quality Timeline
 * @type {number[]}
 */
        var CleanTuplesDQ = [];
        var nullChecksDQ = [];
        var dataTypeChecksDQ = [];
        var numberOfFields = [];
        var keyArr = [];

        for(var i=0; i< self.dataQualityJson.length; i++){
            for(var key in self.dataQualityJson[i]){
                if(!isNaN(key)) {
                    keyArr.push(key);
                    CleanTuplesDQ.push(Number(self.dataQualityJson[i][key].cleanTuple));
                    nullChecksDQ.push(Number(self.dataQualityJson[i][key].jsonReport.NullCheck.dirtyTuple));
                    dataTypeChecksDQ.push(Number(self.dataQualityJson[i][key].jsonReport.DataType.dirtyTuple));
                    numberOfFields.push(Number(self.dataQualityJson[i][key].jsonReport.NumberOfFields.dirtyTuple));
                }
            }
        };

        self.areaChartModel = {
            chartData : [CleanTuplesDQ, nullChecksDQ, dataTypeChecksDQ, numberOfFields],
            chartOptions : {
                stackSeries: true,
                seriesColors:["#74B3F2","#DFE2F5","#F79328","#F7D5B0"],
                showMarker: false,
                legend:{show:true},
                seriesDefaults: {
                    fill: true
                },
                axes: {
                    xaxis: {
                        renderer: $.jqplot.CategoryAxisRenderer,
                        ticks: keyArr
                    }
                }
            }
        };
//END OF DATA QUALITY TIMELINE


/**
 * Data Validation
 * @type {*[]}
 */
        var colors = ["#DB843D", "#AA4643","#A47D7C","#80699B","#BA683F","#92A8CD","#A47D7C","#B5CA92"];
        var colors1 = [];
        //colors1.push("#298A08");
        colors1.push(colors[4]);
        colors1.push(colors[1]);
        colors1.push(colors[2]);


        var cleanTuple = '';
        var violatedTuple = '';
        for(var i=0; i<self.dataValidationJson.length; i++){
            cleanTuple = self.dataValidationJson[i].validation.cleanTuple;
            violatedTuple = self.dataValidationJson[i].validation.totalTupleProcessed;
        };

        var data = [['Violated Tuple', violatedTuple],['Clean Tuple', cleanTuple]];
        self.pieChartValidationModel = {
            chartData : [data],
            chartOptions : {
                seriesColors: colors1,
                grid: {
                    drawBorder: false,
                    shadow: false,
                    background: 'transparent'
                },
                legend:{show:true},
                seriesDefaults: {
                    // make this a donut chart.
                    renderer: $.jqplot.DonutRenderer,
                    shadow: false,
                    rendererOptions: {
                        diameter: undefined, // diameter of pie, auto computed by default.
                        innerDiameter: 0,
                        padding: 20, // padding between pie and neighboring legend or plot margin.
                        sliceMargin: 1, // gap between slices.
                        fill: true, // render solid (filled) slices.
                        shadowOffset: 2, // offset of the shadow from the chart.
                        shadowDepth: 5, // Number of strokes to make when drawing shadow.  Each stroke
                        // offset by shadowOffset from the last.
                        shadowAlpha: 0.07, // Opacity of the shadow
                        showDataLabels: true
                    }
                },
                series: [{
                    seriesColors: colors1,
                    rendererOptions: {
                        dataLabelPositionFactor: 1.4,

                        dataLabelFormatString: '%0.2f %'
                    }
                } ],
                axes:{
                    xaxis: {
                        tickOptions:{
                            showGridline: false,
                            showMark: false,
                            showLabel: false,
                            shadow: false
                        }
                    },
                    yaxis: {
                        tickOptions:{
                            showGridline: false,
                            showMark: false,
                            showLabel: false,
                            shadow: false
                        }
                    }
                }
            }
        };

        var nullCheck = '';
        var dataType = '';
        var regex = '';


        for(var i=0; i<self.dataValidationJson.length; i++){
            nullCheck = self.dataValidationJson[i].validation.jsonReport.NullCheck.totalViolations;
            dataType = self.dataValidationJson[i].validation.jsonReport.DataType.totalViolations;
            regex = self.dataValidationJson[i].validation.jsonReport.Regex.totalViolations;
        };

        var s1 = [['Null Check',nullCheck], ['Data Type',dataType], ['Regex',regex]];
        var s2 = [['Null Check',nullCheck], ['Data Type',dataType], ['Regex',regex]];
        self.donutChartModel = {
            chartData : [s1,s2],
            chartOptions : {
                seriesColors: colors1,
                grid: {
                    drawBorder: false,
                    shadow: false,
                    background: 'transparent'
                },
                legend:{show:true},
                seriesDefaults: {
                    // make this a donut chart.
                    renderer: $.jqplot.DonutRenderer,
                    shadow: false,
                    rendererOptions: {
                        diameter: undefined, // diameter of pie, auto computed by default.
                        innerDiameter: 0,
                        padding: 20, // padding between pie and neighboring legend or plot margin.
                        sliceMargin: 1, // gap between slices.
                        fill: true, // render solid (filled) slices.
                        shadowOffset: 2, // offset of the shadow from the chart.
                        shadowDepth: 5, // Number of strokes to make when drawing shadow.  Each stroke
                        // offset by shadowOffset from the last.
                        shadowAlpha: 0.07, // Opacity of the shadow
                        showDataLabels: true
                    }
                },
                series: [{
                    seriesColors: colors1,
                    rendererOptions: {
                        dataLabelPositionFactor: 1.4,

                        dataLabelFormatString: '%0.2f %'
                    }
                } ],
                axes:{
                    xaxis: {
                        tickOptions:{
                            showGridline: false,
                            showMark: false,
                            showLabel: false,
                            shadow: false
                        }
                    },
                    yaxis: {
                        tickOptions:{
                            showGridline: false,
                            showMark: false,
                            showLabel: false,
                            shadow: false
                        }
                    }
                }
            }
        };
//END OF DATA VALIDATION


/**
 * Tuning - plot chart
 * @type {*[]}
 */
        var tuningData = [];
        for(var i=0; i< self.tuningJson.length; i++){
            tuningData.push(self.tuningJson[i].executionTimeInMsecs/1000);   //Converted miliseconds to seconds
        };

        self.plotChartModel = {
            chartData : [tuningData],
            chartOptions : {
                seriesColors:["#41BECC"],
                legend:{show:true},
                axesDefaults: {
                    labelRenderer: $.jqplot.CanvasAxisLabelRenderer
                },
                seriesDefaults: {
                    rendererOptions: {
                        smooth: true
                    }
                },
                axes: {
                    xaxis: {
                        label: "Iteration",
                        pad: 0
                    },
                    yaxis: {
                        label: "Execution Time(sec)"
                    }
                }
            }
        };
//END OF TUNING


/**
 * Job Profiling
  * @type {*[]}
 */

        var memUsage = [];
        var memUsageArray = [];
        var cpuUsage = [];
        var cpuUsageArray = [];
        for(var i=0; i< self.jobProfilingJson.length; i++){
            var memUsageObj = self.jobProfilingJson[i].graphData.memUsage;
            for(var key in memUsageObj){
                memUsage = [];
                memUsage.push(key);
                memUsage.push(memUsageObj[key]);
                memUsageArray.push(memUsage);
            }
            var cpuUsageObj = self.jobProfilingJson[i].graphData.cpuUsage;
            for(var key in cpuUsageObj){
                cpuUsage = [];
                cpuUsage.push(key);
                cpuUsage.push(cpuUsageObj[key]);
                cpuUsageArray.push(cpuUsage);
            }
        };

        /*var lineChartData1 = [[1, 6.29], [2, 8.21], [3, 8.92], [4, 7.33], [5, 7.91], [6, 3.6], [7, 6.88],
            [8, 1.5], [9, 0.08], [10, 6.36], [11, 0.5]];
        var lineChartData2 = [[5, 6.29], [6, 8.21], [7, 8.92], [8, 7.33], [9, 7.91], [10, 3.6], [11, 6.88],
            [18, 1.5], [19, 0.08], [20, 6.36], [21, 0.5]];*/

        var scatterChartData = [[10, 1], [11, 2], [12, 3], [13, 4], [14, 5], [15, 6], [16, 7],
            [17, 8], [18, 9]];
        var s1 = [2, 6, 7, 10];
        var s2 = [7, 5, 3, 4];
        var s3 = [14, 9, 3, 8];
        self.lineChartModel = {
            chartData: [memUsageArray, cpuUsageArray, scatterChartData, s1, s2, s3],
            chartOptions: {
                stackSeries: true,
                multiCanvas: false,
                copyData: false,
                height: 350,
                cursor: {
                    show: true,
                    zoom: true
                },
                legend: {
                    show: false,
                    location: 's'
                },
                series: [
                    {
                        color: 'purple',
                        showMarker: true,
                        showLine: true,
                        //fill: true,
                        //fillAndStroke: true,
                        markerOptions: {
                            style: 'filledCircle',
                            size: 5
                        },
                        rendererOptions: {
                            smooth: false
                        }
                    },
                    {
                        color: 'blue',
                        showMarker: true,
                        showLine: true,
                        //fill: true,
                        //fillAndStroke: true,
                        markerOptions: {
                            style: 'filledCircle',
                            size: 5
                        },
                        rendererOptions: {
                            smooth: false
                        }
                    },
                    {
                        color: 'red',
                        showMarker: true,
                        showLine: false,
                        markerOptions: {
                            style: 'filledCircle',
                            size: 12,
                            stroke: true,
                            shadow: true
                        }
                    }
                    ,
                    {
                        renderer: $.jqplot.BarRenderer,
                        rendererOptions: {
                            // Put a 30 pixel margin between bars.
                            barMargin: 30,
                            // Highlight bars when mouse button pressed.
                            // Disables default highlighting on mouse over.
                            highlightMouseDown: true
                        },
                        pointLabels: {show: true}
                    },
                    {
                        renderer: $.jqplot.BarRenderer,
                        rendererOptions: {
                            // Put a 30 pixel margin between bars.
                            barMargin: 30,
                            // Highlight bars when mouse button pressed.
                            // Disables default highlighting on mouse over.
                            highlightMouseDown: true
                        },
                        pointLabels: {show: true}
                    },
                    {
                        renderer: $.jqplot.BarRenderer,
                        rendererOptions: {
                            // Put a 30 pixel margin between bars.
                            barMargin: 30,
                            // Highlight bars when mouse button pressed.
                            // Disables default highlighting on mouse over.
                            highlightMouseDown: true
                        },
                        pointLabels: {show: true}
                    }
                ],
                axes: {
                    xaxis: {
                        label: 'Execution Time',
                        autoscale: false,
                        rendererOptions: {
                            //tickInset: .2
                        },
                        tickOptions: {
                            formatString: '%d'

                        }
                    },
                    yaxis: {
                        label: 'Percentage Utilization',
                        autoscale: false,
                        tickOptions: {
                            formatString: '%d'

                        },
                        rendererOptions: {
                            tickInset: .2
                        }
                    }
                }
            }
        };
//END OF JOB PROFILING
    }]);
