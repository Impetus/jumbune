/* NewJobConfig controller */
'use strict';
angular.module('dpdqchart.ctrl', [])
    
    .controller('DPDQChartController', ['$scope', '$rootScope','common', 'JobFactory', '$compile', '$location',  function ($scope, $rootScope, common, JobFactory, $compile, $location) {
        var self = this;

        //Report Json Read
        self.dpWithCriteriaJson = common.getDataProfilerWithCriteriaJsonData();
        self.dpNoCriteriaJson = common.getDataProfilerNoCriteriaJsonData();
        self.dataQualityJson = common.getDataQualityTimelineJsonData();
        //End of Report Json Read

        self.dataSuiteDetails = common.dataSuiteDetails;

        for (var i=0; i<common.dataSuiteDetails.length; i++) {
            self.isNoCriteria = false;

            if(common.dataSuiteDetails[i].enableField === 'dq') {
                /**
                * Data Quality Timeline
                * @type {number[]}
                */

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

                common.dataSuiteDetails[i].chartModel = {
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

                common.dataSuiteDetails[i].chartModel = {
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
            }
            
            if(common.dataSuiteDetails[i].enableField === 'dp' && (common.dataSuiteDetails[i].isCriteriaBased === 'false')) {

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

                common.dataSuiteDetails[i].chartModel = {
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

                common.dataSuiteDetails[i].chartModel = {
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
            }

            if(common.dataSuiteDetails[i].enableField === 'dp' && (common.dataSuiteDetails[i].isCriteriaBased === 'true')) {
                /**
                * Data Profiling - With Criteria
                * @type {*[]}
                */
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
            }
        }


        self.back = function () {
            $location.path('/index');
        };

    }]);