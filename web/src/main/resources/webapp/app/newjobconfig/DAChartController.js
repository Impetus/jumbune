/* NewJobConfig controller */
'use strict';
angular.module('dachart.ctrl', [])
    
    .controller('DAChartController', ['$scope', '$rootScope','common', 'JobFactory', '$compile', '$location',  function ($scope, $rootScope, common, JobFactory, $compile, $location) {
        var self = this;
        self.showDV = true;

        //Report Json Read
        self.dpWithCriteriaJson = common.getDataProfilerWithCriteriaJsonData();
        self.dpNoCriteriaJson = common.getDataProfilerNoCriteriaJsonData();
        self.dataQualityJson = common.getDataQualityTimelineJsonData();
        self.dataValidationJson = common.getDataValidationJsonData();
        //End of Report Json Read

        self.dataAnalysisDetails = common.dataAnalysisDetails;
        console.log(self.dataAnalysisDetails.length)
        for (var i=0; i<self.dataAnalysisDetails.length; i++) {
            self.showDV = true;
            console.log(self.dataAnalysisDetails[i]);
            if(self.dataAnalysisDetails[i].enableField === 'dq') {
                /**
                * Data Quality Timeline
                * @type {number[]}
                */
                self.isNoCriteria = false;
                console.log('dq')


                var CleanTuplesDQ = [];
                var nullChecksDQ = [];
                var dataTypeChecksDQ = [];
                var numberOfFields = [];
                var keyArr = [];

                for(var x=0; x< self.dataQualityJson.length; x++){
                    for(var key in self.dataQualityJson[x]){
                        if(!isNaN(key)) {
                            keyArr.push(key);
                            CleanTuplesDQ.push(Number(self.dataQualityJson[x][key].cleanTuple));
                            nullChecksDQ.push(Number(self.dataQualityJson[x][key].jsonReport.NullCheck.dirtyTuple));
                            dataTypeChecksDQ.push(Number(self.dataQualityJson[x][key].jsonReport.DataType.dirtyTuple));
                            numberOfFields.push(Number(self.dataQualityJson[x][key].jsonReport.NumberOfFields.dirtyTuple));
                        }
                    }
                };

                self.dataAnalysisDetails[i].chartModel = {
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



                /*var l1 = [15, 15, 6, 6, 20];
                var l2 = [15, 15, 12, 12, 9];
                var l3 = [11, 11, 3, 3, 8];
                var l4 = [11, 11, 3, 3, 8];
                var a = "Mon";
                var b = "Tue";
                var c = "Wed";
                var d = "Thr";
                var e = "Fri";

                self.dataAnalysisDetails[i].chartModel = {
                    chartData : [l1, l2, l3, l4],
                    chartOptions : {
                        title: "Data Quality Timeline",
                        stackSeries: true,
                        showMarker: false,
                        seriesDefaults: {
                            fill: true
                        },
                        axes: {
                            xaxis: {
                                renderer: $.jqplot.CategoryAxisRenderer,
                                ticks: [a, b, c, d, e]
                            }
                        }
                    }
                };*/
                continue;
            }
            
            if(self.dataAnalysisDetails[i].enableField === 'dp' && (self.dataAnalysisDetails[i].isCriteriaBased === 'false')) {
                console.log('dp false')
                self.isNoCriteria = false;
                /**
                * Data Profiling - No Criteria
                * @type {*[]}
                */

                var chartData = [];
                var keyArray = [];
                for(var y=0; y<self.dpNoCriteriaJson.length; y++) {
                    for (var key in self.dpNoCriteriaJson[y]) {
                        if(key.indexOf('$')=== -1) {
                            keyArray = [];
                            keyArray.push(key);
                            keyArray.push(self.dpNoCriteriaJson[y][key]);
                            chartData.push(keyArray);
                        }
                    }
                }

                self.dataAnalysisDetails[i].chartModel = {
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


                /*var pieChartData = [['4948', 58784],['392A', 58414],['6214',57926],['null',25276],['3',25120]];

                self.dataAnalysisDetails[i].chartModel = {
                    chartData : [pieChartData],
                    chartOptions : {
                        title: "Data Profiling - With no Criteria",
                        seriesDefaults: {
                            renderer: jQuery.jqplot.PieRenderer,
                            rendererOptions: {
                                // Turn off filling of slices.
                                fill: false,
                                showDataLabels: true,
                                // Add a margin to seperate the slices.
                                sliceMargin: 4,
                                // stroke the slices with a little thicker line.
                                lineWidth: 5
                            }
                        },
                        legend: { show:true, location: 'e' }
                    }
                };*/
                continue;
            }

            if(self.dataAnalysisDetails[i].enableField === 'dp' && (self.dataAnalysisDetails[i].isCriteriaBased === 'true')) {
                /**
                * Data Profiling - With Criteria
                * @type {*[]}
                */
                console.log('dp true')
                self.isNoCriteria = true;


                var fieldValues= [];    // ["1", "5"]
                var dataValues = [];    // ["100102", "12216"]
                var ruleValues = [];    // [">=120049950.000", "<=1981260901.000"]
                for(var z=0; z<self.dpWithCriteriaJson.length; z++) {
                    for (var key in self.dpWithCriteriaJson[z]) {
                        if(!isNaN(key)) {
                            fieldValues.push(key);
                            for(var keyVal in self.dpWithCriteriaJson[z][key]){
                                if(!isNaN(keyVal)) {
                                    dataValues.push(keyVal);
                                }else{
                                    ruleValues.push(self.dpWithCriteriaJson[z][key][keyVal]);
                                }
                            }
                        }
                    }
                }
                self.dtextF1 = ("Field-"+fieldValues[0]).concat(" ["+ruleValues[0]+"]");
                self.dtextF2 = ("Field-"+fieldValues[1]).concat(" ["+ruleValues[1]+"]");

                self.fillColorF1 = 'green';
                self.fillColorF2 = 'red';

                /*self.dtext = 25;
                self.dataInfo = '';
                self.dPercent = 85;
                self.fillColor = 'green';*/
                continue;
            }

            if(self.dataAnalysisDetails[i].enableField === 'dv') {
                /**
                * Data validation
                * @type {*[]}
                */
                console.log('dv')
                self.isNoCriteria = false;
                self.showDV = false;


                var colors = ["#DB843D", "#AA4643","#A47D7C","#80699B","#BA683F","#92A8CD","#A47D7C","#B5CA92"];
                var colors1 = [];
                //colors1.push("#298A08");
                colors1.push(colors[4]);
                colors1.push(colors[1]);
                colors1.push(colors[2]);


                var cleanTuple = '';
                var violatedTuple = '';
                for(var j=0; j<self.dataValidationJson.length; j++){
                    cleanTuple = self.dataValidationJson[j].validation.cleanTuple;
                    violatedTuple = self.dataValidationJson[j].validation.totalTupleProcessed;
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


                for(var k=0; k<self.dataValidationJson.length; k++){
                    nullCheck = self.dataValidationJson[k].validation.jsonReport.NullCheck.totalViolations;
                    dataType = self.dataValidationJson[k].validation.jsonReport.DataType.totalViolations;
                    regex = self.dataValidationJson[k].validation.jsonReport.Regex.totalViolations;
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


               /* var cleanTuple = '';
                var violatedTuple = '';
                for(var j=0; j<self.dataValidationJson.length; j++){
                    cleanTuple = self.dataValidationJson[j].validation.cleanTuple;
                    violatedTuple = self.dataValidationJson[j].validation.totalTupleProcessed;
                };

                var data = [['Violated Tuple', violatedTuple],['Clean Tuple', cleanTuple]];
                self.dataAnalysisDetails[i].chartModel = {
                    chartData : [data],
                    chartOptions : {
                        title: "Data Violations - Clean v/s violated data",
                        seriesDefaults: {
                            shadow: false,
                            renderer: jQuery.jqplot.PieRenderer,
                            rendererOptions: { sliceMargin: 4, showDataLabels: true }
                        },
                        legend: { show:true }
                    }
                };

                var nullCheck = '';
                var dataType = '';
                var regex = '';

                for(var k=0; k<self.dataValidationJson.length; k++){
                    nullCheck = self.dataValidationJson[k].validation.jsonReport.NullCheck.totalViolations;
                    dataType = self.dataValidationJson[k].validation.jsonReport.DataType.totalViolations;
                    regex = self.dataValidationJson[k].validation.jsonReport.Regex.totalViolations;
                };

                var s1 = [['Null Check',nullCheck], ['Data Type',dataType], ['Regex',regex]];
                var s2 = [['Null Check',nullCheck], ['Data Type',dataType], ['Regex',regex]];
                self.dataAnalysisDetails[i].chartModel = {
                    chartData : [s1,s2],
                    chartOptions : {
                        title: "Data Violations",
                        seriesDefaults: {
                            // make this a donut chart.
                            renderer:$.jqplot.DonutRenderer,
                            rendererOptions:{
                                // Donut's can be cut into slices like pies.
                                sliceMargin: 3,
                                // Pies and donuts can start at any arbitrary angle.
                                startAngle: -90,
                                showDataLabels: true,
                                // By default, data labels show the percentage of the donut/pie.
                                // You can show the data 'value' or data 'label' instead.
                                dataLabels: 'value'
                            }
                        }
                    }
                };*/
                continue;
            }
        }


        self.back = function () {
            $location.path('/index');
        };

    }]);