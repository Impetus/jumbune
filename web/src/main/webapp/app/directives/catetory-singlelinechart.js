'use strict';

var directiveModule = angular.module('directives');

directiveModule.directive('ngCategoryLineChart1', ['$filter', '$document', '$compile', '$parse',
    function ($filter, $document, $compile, $parse) {
        var outPut;
        if(outPut == undefined){
       //     console.log(outPut);
            outPut = [];            
        }     

        return {
            restrict: 'AE',
            scope: {
                optionData: '=',
                category: '=',
                getValue: '=',
                selectedNodes:'=',
                nodeServiceValues : '='

            },
            template: function (element, attrs) {
                var template = '<section class="multiselect-parent dropdown-multiselect"><div class="header clearfix ">'
                    +'<span class="pull-left"   >{{category}}</span>'
                    +'<div style="position: relative;float: right;"  ><span class="pull-right multiselect-parent " ><i ng-click="toggleDropdowns()" class="fa fa-cog" title="Settings"></i>'
                    +'</span>'
                    +'<ul class="dropdown-menu  pull-right dropdown-menu-form " ng-style="{display: openPopup ? \'block\' : \'none\'}" >'
                    +'<li  class="dropdown-linechart"><div ><input type="radio"  ng-model="intervalMode" value ="Duration" >Duration &nbsp;&nbsp;&nbsp; : <input type="text" style="width:50px" ng-model="durationTextValue" > <select style="width:96px" ng-disabled="!timeInterval" ng-model="durationUnit"><option ></option><option value="m" >Minuts</option><option value="h">Hours</option><option value="d">Days</option></select></div></li>'
                    +'<li  class="dropdown-linechart"><div><input type="radio"  ng-model="intervalMode" value="Range" >Rang From : <input ng-model="rangeFrom" type="text" jumbune-datepicker ng-disabled="timeInterval" ></div></li>'
                    +'<li  class="dropdown-linechart"><div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Rang To : <input type="text" ng-model="rangeTo" jumbune-datepicker ng-disabled="timeInterval"></span> </div></li>'
                    +'<li class="dropdown-linechart" ><div>&nbsp;Aggr. Function : <select ng-model="aggregateFunction"><option value="min">Min</option><option value="max">Max</option><option value="mean">Mean</option> </select></div></li>'
                    +'<li class="dropdown-linechart" ><div>&nbsp;<button class="btn btn-default"" ng-click="setValue()">Ok</button></div></li>'
                    +'</ul></div>'

                    '</section>';

                element.html(template);
            },
            link: function ($scope, $element, $attrs) {
            //	console.log("in directive scope data",$scope.optionData)
            //	console.log("in directive attrs data",$attrs.optionData)
                var nodeKey = $attrs.tabNodeId;
                $scope.durationTextValue = 10;
                $scope.durationUnit = "m";
                $scope.aggregateFunction = "mean";
                if (nodeKey == 'Cluster Wide') { 
                	$scope.getValue[$scope.category] = {"nodeKey":nodeKey,"duration":$scope.durationTextValue+""+$scope.durationUnit,"rangeFrom":$scope.rangeFrom,"rangeTo":$scope.rangeTo,"aggregateFunction":$scope.aggregateFunction};
                } else {
                	 $scope.nodeServiceValues[$scope.category] = {"nodeKey":nodeKey,"duration":$scope.durationTextValue+""+$scope.durationUnit,"rangeFrom":$scope.rangeFrom,"rangeTo":$scope.rangeTo,"aggregateFunction":$scope.aggregateFunction};
                     outPut.push($scope.nodeServiceValues[nodeKey]);   
                }
                var $dropdownTrigger = $element.children()[0];
                $scope.timeInterval = false;
                $scope.intervalMode = "Duration";

                $scope.setValue = function(){
                	if (nodeKey == 'Cluster Wide') { 
                		$scope.getValue[$scope.category] = {"nodeKey":nodeKey,"duration":$scope.durationTextValue+""+$scope.durationUnit,"rangeFrom":$scope.rangeFrom,"rangeTo":$scope.rangeTo,"aggregateFunction":$scope.aggregateFunction}
                	} else {
                		$scope.nodeServiceValues[$scope.category] = {"nodeKey":nodeKey,"duration":$scope.durationTextValue+""+$scope.durationUnit,"rangeFrom":$scope.rangeFrom,"rangeTo":$scope.rangeTo,"aggregateFunction":$scope.aggregateFunction};
                	}
                    
                    $scope.closeDropdown();
                }
                $scope.firstRender = false;

               $scope.$watch("intervalMode",function(value){
                   switch (value){
                       case "Duration":
                           $scope.timeInterval = true;
                           break;

                       case "Range" :
                           $scope.timeInterval = false;
                           $scope.duration = "";
                           break;
                   }
               });
                $scope.singleChartData = {};
                
               if($scope.optionData[$scope.category])
                $scope.singleChartData = $scope.optionData[$scope.category];
        //       console.log($scope.singleChartData)
                
               
                redraw();

                $scope.$watch("optionData",function(value){
                $scope.singleChartData = $scope.optionData[$scope.category];
   //             console.log(["SVG",d3.select($element[0]).select(".dropdown-multiselect").select("svg")])
                if(d3.select($element[0]).select(".dropdown-multiselect").select("svg")[0])
                {
                	d3.select($element[0]).select(".dropdown-multiselect").select("svg").remove()
                }
                redraw();
                $scope.toggleDropdowns = function () {
                    $scope.openPopup = !$scope.openPopup;
                };

                $scope.closeDropdown = function(){
                    $scope.openPopup = false;
                }
                 $document.on('click', function (e) {
                     var target = e.target.parentElement;
                     var parentFound = false;

                     while (angular.isDefined(target) && target !== null && !parentFound) {
                     if (_.contains(target.className.split(' '), 'multiselect-parent') && !parentFound) {
                     if(target === $dropdownTrigger) {
                     parentFound = true;
                     }
                     }
                     target = target.parentElement;
                     }

                     if (!parentFound) {
                     $scope.$apply(function () {
                     $scope.openPopup = false;
                     });
                     }
                 });


                function redraw(){
                //	 console.log($scope.singleChartData)
                if(!($scope.singleChartData)) return ;
                    $scope.categoryData={};
                    var lenghtOfData = 0;
                 //   console.log($scope.selectedNodes)
                    angular.forEach($scope.selectedNodesForIndividualTabs[tab.content],function(data,i){
                    	 console.log($scope.selectedNodes,data,i)
						if($scope.singleChartData[data]){
					//		 console.log("in if")
                        	$scope.categoryData[data] = $scope.singleChartData[data];
                        	lenghtOfData += $scope.categoryData[data].length;
                        }
                    })
                    if(!lenghtOfData) return;
			$scope.firstRender = true;
			var timeSeries = $scope.singleChartData.time;
               		 $scope.parseDate = d3.time.format("%Y-%m-%d %H:%M:%S").parse;
                    var margin = {top: 20, right: 10, bottom: 30, left: 80},
                        width = 450 - margin.left - margin.right,
                        height = 300 - margin.top - margin.bottom;



                    var x = d3.time.scale()
                        .range([0, width]);

                    var y = d3.scale.linear()
                        .range([height, 0]);

                    $scope.color = d3.scale.category10();

                    var xAxis = d3.svg.axis()
                        .scale(x)
                        .orient("bottom");

                    var line = d3.svg.line()
                        .interpolate("basis")
                        .x(function(d) { return x(d.date); })
                        .y(function(d) { return y(d.temperature); });

					
                    var svg = d3.select($element[0]).select(".dropdown-multiselect").append("div").append("svg")
                        .attr("width", width + margin.left + margin.right)
                        .attr("height", height + margin.top + margin.bottom)
                        .append("g")
                        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

                    $scope.color.domain(d3.keys($scope.categoryData).filter(function(key) { return key !== "date"; }));

                    $scope.cities = $scope.color.domain().map(function(name) {
                        return {
                            name: name,
                            values: $scope.singleChartData[name].map(function(d) {
                                return {date: d[1], temperature: d[0]};
                            })
                        };
                    });



                    x.domain(d3.extent(timeSeries, function(d) {  return $scope.parseDate(d[0]); }));

                    y.domain([
                        d3.min($scope.cities, function(c) { return d3.min(c.values, function(v) { return v.temperature; }); }),
                        d3.max($scope.cities, function(c) { return d3.max(c.values, function(v) { return v.temperature; }); })
                    ]);

                    var unit = $scope.singleChartData.unit;

                    var MAX = d3.max($scope.cities, function(c) { return d3.max(c.values, function(v) { return v.temperature; });

                                        var yAxis = d3.svg.axis()
                        .scale(y)
                        .tickFormat(function (d) {
                            if (d == 0) {
                                return 0;
                            }
                            var suffix;
                            switch (unit) {
                                case "memory" :
                                    if ((d / 1073741824) >= 1) {
                                        d = d / 1073741824;
                                        // if max values less than 20 GB
                                        if (MAX > 21474836480) {
                                            d = Math.ceil(d);
                                        } else {
                                            d = d.toFixed(2); 
                                        }
                                        suffix = " GB";
                                    } else if ((d / 1048576) >= 1) {
                                        d = d / 1048576;
                                        if (MAX > 20971520) {
                                            d = Math.ceil(d);
                                        } else {
                                            d = d.toFixed(1);
                                        }
                                        suffix = " MB";
                                    } else if ((d / 1024) >= 1) {
                                        d = d / 1024;
                                        if (MAX > 20480) {
                                            d = Math.ceil(d);
                                        } else {
                                            d = d.toFixed(1);
                                        }
                                        suffix = " KB";
                                    } else {
                                        d = Math.ceil(d);
                                        suffix = " Bytes";
                                    }
                                    break;
                                    
                                case "memoryMB" :

                                    suffix = " MB";
                                    break;
                                    
                                case "timeMillis" :

                                    if ((d / 3600000) > 1) {
                                        d = d / 3600000;
                                        if (MAX > 72000000) {
                                            d = Math.ceil(d);
                                            suffix = " hrs";
                                        } else {
                                            var minutes = Math.ceil( d - Math.floor(d) ) * 60;
                                            if (minutes != 0 ) {
                                                return Math.floor(d) + " hr " +  minutes  + " min"; 
                                            } else {
                                                return Math.floor(d) + " hr ";
                                            }
                                            
                                        }
                                        
                                    } else if ((d / 60000) >= 1) {
                                        d = d / 60000;
                                        d = Math.ceil(d);

                                        if (MAX > 1200000) {
                                            d = Math.ceil(d);
                                            suffix = " min";
                                        } else {
                                            var sec = Math.ceil( d - Math.floor(d) ) * 60;
                                            if (sec != 0 ) {
                                                return Math.floor(d) + " min " +  minutes  + " sec"; 
                                            } else {
                                                return Math.floor(d) + " min ";
                                            }
                                        }

                                    } else if ((d / 1000) >= 1) {
                                        d = d / 1000;
                                        if (MAX > 20000) {
                                            d = Math.floor(d);
                                            suffix = " min";
                                        } else {
                                            var ms = Math.ceil( d - Math.floor(d) ) * 1000;
                                            if (ms != 0 ) {
                                                return Math.floor(d) + " sec " +  ms  + " ms"; 
                                            } else {
                                                return Math.floor(d) + " sec ";
                                            }
                                        }

                                    } else {
                                        suffix = " milli sec.";
                                    }
                                    break;
                                    
                                case "timeNanos" :

                                    if ((d / 1000000000) >= 1) {
                                        d = d / 1000000000;
                                        suffix = " sec.";
                                    } else if ((d / 1000000) >= 1) {
                                        d = d / 1000000;
                                        suffix = " μs";
                                    } else {
                                        suffix = " ns";
                                    }
                                    d = Math.floor(d);
                                    break;
                                    
                                case "percent" :
                                    suffix = " %";
                                    break;
                                    
                                case "percent1" :
                                    d = d * 100;
                                    suffix = " %";
                                    break;
                                    
                                default :
                                    if ((d / 1000000000) >= 1) {
                                        d = d / 1000000000;
                                        suffix = " B";
                                        if ( (Math.ceil( d - Math.floor(d) ) * 1000) == 0) {
                                            d = Math.floor(d);
                                        } else {
                                            d = d.toFixed(2);
                                        }
                                    } else if ((d / 1000000) >= 1) {
                                        d = d / 1000000;
                                        suffix = " M";
                                        if ( (Math.ceil( d - Math.floor(d) ) * 1000) == 0) {
                                            d = Math.floor(d);
                                        } else {
                                            d = d.toFixed(2);
                                        }
                                    } else if ((d / 1000) >= 1) {
                                        d = d / 1000;
                                        if ( (Math.ceil( d - Math.floor(d) ) * 1000) == 0) {
                                            d = Math.floor(d);
                                        } else {
                                            d = d.toFixed(2);
                                        }
                                        suffix = " K";
                                    } else {
                                        suffix = "";
                                    }
                            }
                            
                            return d = d + suffix;
                        })
                        .orient("left");

                    svg.append("g")
                        .attr("class", "x axis")
                        .attr("transform", "translate(0," + height + ")")
                        .call(xAxis);

                    svg.append("g")
                        .attr("class", "y axis")
                        .call(yAxis)                        
                        .append("text")
                        .attr("transform", "rotate(-90)")
                        .attr("y", 6)
                        .attr("dy", ".71em")
                        .style("text-anchor", "end");
                    var city = svg.selectAll(".city")                    	
                        .data($scope.cities)
                        .enter().append("g")
                        .attr("class", "city");

                    city.append("path")
                        .attr("class", "line")
                        .attr("d", function(d) { return line(d.values); })
                        .attr("title",function(d){ return d.name})
                        .style("stroke", function(d) { return $scope.color(d.name); });
                }
                $scope.$on('$destroy', function() {
                    delete $scope.getValue[$scope.category];
                    delete $scope.nodeServiceValues[$scope.category];
                });
            }


        };
    }]);