/* NewJobConfig controller */
'use strict';
angular.module('jobchart.ctrl', [])
    
    .controller('JobChartController', ['$scope', '$rootScope','common', 'JobFactory', '$compile', '$location',  function ($scope, $rootScope, common, JobFactory, $compile, $location) {
        var self = this;


        //Report Json Read
        self.clusterMonitoringJson = common.getClusterMonitoringJsonData();
        self.dpWithCriteriaJson = common.getDataProfilerWithCriteriaJsonData();
        self.dpNoCriteriaJson = common.getDataProfilerNoCriteriaJsonData();
        self.dataQualityJson = common.getDataQualityTimelineJsonData();
        self.dataValidationJson = common.getDataValidationJsonData();
        self.debuggerJson = common.getDebuggerJsonData();
        self.jobProfilingJson = common.getJobProfilingJsonData();
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

        self.chartArr = [];
        self.chartObj = {};

        self.init = function() {
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
                self.widgetsDtl.debuggerDtl.useRegexChk && (self.displayJSON.useRegexChk =  'Use Regex');
                self.widgetsDtl.debuggerDtl.usrDefChk && (self.displayJSON.usrDefChk =  'User Defined Validations');
            }
            if(self.widgetsDtl.whatIfDtl !== undefined){
                self.widgetsDtl.whatIfDtl.enableWhatIf && (self.displayJSON.enableWhatIf =  'What If');
            }
            if(self.widgetsDtl.jobProfilingDtl !== undefined){
                self.widgetsDtl.jobProfilingDtl.enableprofilingCheck && (self.displayJSON.enableprofilingCheck =  'Job Profiling');
                self.widgetsDtl.jobProfilingDtl.runFromJumbune && (self.displayJSON.runFromJumbune =  'Run From Jumbune');
            }
            if(self.widgetsDtl.dataValidation !== undefined){
                self.widgetsDtl.dataValidation.enableDataValidation && (self.displayJSON.enableDataValidation =  'Data Validation');
            }
            if(self.dataConfigDtl !== undefined){
                self.dataConfigDtl.enblDataValidation && (self.displayJSON.enblDataValidation = 'Data Validation');
                self.dataConfigDtl.enblDataQuality && (self.displayJSON.enblDataValidation = 'View Data Quality');
                self.dataConfigDtl.enblDataProfiler && (self.displayJSON.enblDataValidation = 'View Data Profiler');
            }
            console.log('Final JSON of Enable Fields',self.displayJSON);

            for (var key in self.displayJSON) {
                if (self.displayJSON[key] === "Job Profiling") {
                    /**
                    * Job Profiling
                    * @type {*[]}
                    */
                    ////////////////////////////
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
                    var scatterChartData = [[10, 1], [11, 2], [12, 3], [13, 4], [14, 5], [15, 6], [16, 7],
                        [17, 8], [18, 9]];
                    var s1 = [2, 6, 7, 10];
                    var s2 = [7, 5, 3, 4];
                    var s3 = [14, 9, 3, 8];
                    self.chartModel = {
                        chartTitle: 'Job Profiling',
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

                    self.chartArr.push(self.chartModel);
                }

                if (self.displayJSON[key] === "Debugger") {
                }

                if (self.displayJSON[key] === "What If") {
                }

                if (self.displayJSON[key] === "Data Validation") {
                    /**
                    * Data Validation
                    * @type {*[]}
                    */


                    ///////////////////////////
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
                    self.chartModel = {
                        chartTitle: 'Clean v/s Violated data',
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
                    self.chartArr.push(self.chartModel);

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
                    self.chartModel = {
                        chartTitle: 'Data Violations',
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

                    self.chartArr.push(self.chartModel);
                }

                if (self.displayJSON[key] === "Data Quality") {

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
                        chartTitle: 'Data Quality',
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
                    self.chartArr.push(self.chartModel);
                }

                if (self.displayJSON[key] === "Data Profiling") {
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
                    self.chartArr.push(self.chartModel);
                }

                if (self.displayJSON[key] === "Cluster Monitoring") {
                }
            }
            console.log(self.chartArr);
        };

        self.back = function () {
            $location.path('/index');
        };
    }]);