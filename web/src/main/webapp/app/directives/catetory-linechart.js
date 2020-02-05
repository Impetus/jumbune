'use strict';

var directiveModule = angular.module('directives');

directiveModule.directive('ngCategoryLineChart', ['$filter', '$http', '$document', '$compile', '$parse', '$rootScope',
	function($filter, $http, $document, $compile, $parse, $rootScope) {
		var outPut;
		if (outPut == undefined) {
			outPut = [];
		}

		return {
			restrict: 'AE',
			scope: {
				optionData       : '=',
				metereQueue      : '=',
				category         : '=',
				getValue         : '=',
				selectedNodes    : '=',
				nodeServiceValues: '='

			},
			template: function(element, attrs) {

				var template = '<section class="multiselect-parent dropdown-multiselect" style="width:100%;margin: 9px;font-size: 17px;"><div class="header clearfix ">' + '<span class="pull-left" style="word-break: break-all;" ng-show="!metereQueue">{{category}}</span>' + '<div ng-show="!metereQueue" style="position: relative;float: right;"  ><span class="pull-right multiselect-parent " ><i ng-click="toggleDropdowns()" class="fa fa-cog" title="Settings"></i>' + '<div ng-show="!metereQueue" style="position: relative;float: right;"  ><span class="pull-right multiselect-parent " ><i ng-click="closeSettingFun(category)" class="fa fa-times" title="Close" style="margin-left:10px;"></i>'

				+'</span>' + '<ul class="dropdown-menu  pull-right dropdown-menu-form " ng-style="{display: openPopup ? \'block\' : \'none\'}" >' + '<li  class="dropdown-linechart"><div ><input type="radio" ng-model="intervalMode" value ="Duration" >Duration &nbsp;&nbsp;&nbsp; : <input type="text" ng-disabled="!timeInterval" style="width:50px" ng-model="durationTextValue" > <select style="width:96px" ng-disabled="!timeInterval" ng-model="durationUnit"><option value="m" >Minutes</option><option value="h">Hours</option><option value="d">Days</option></select></div></li>' + '<li  class="dropdown-linechart"><div><input type="radio"  ng-model="intervalMode" value="Range" >Range From : <input ng-model="rangeFrom" type="text" jumbune-datepicker ng-disabled="timeInterval" ></div></li>' + '<li  class="dropdown-linechart"><div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Range To : <input type="text" ng-model="rangeTo" jumbune-datepicker ng-disabled="timeInterval"></span> </div></li>' + '<li class="dropdown-linechart" ><div>&nbsp;Aggr. Function : <select ng-model="aggregateFunction"><option value="min">Min</option><option value="max">Max</option><option value="mean">Mean</option><option value="" >No Aggr. Function</option> </select></div></li>' + '<li class="dropdown-linechart" ><div>&nbsp;<button class="btn btn-default"" ng-click="setValue()">Ok</button></div></li>' + '</ul></div>'

				'</section>';

				element.html(template);
			},
			link: function($scope, $element, $attrs) {
				$scope.$watch("optionData", function(value, oldValue) {
					if (!value) {
						return;
					}
					$scope.singleChartData = {};
					$scope.singleChartData = $scope.optionData[$scope.category];
					if (d3.select($element[0]).select(".dropdown-multiselect").select("svg")[0]) {
						d3.select($element[0]).select(".dropdown-multiselect").select("svg").remove()
					}
					redraw();
				});
				$scope.closeSettingFun = function(category) {
					if ($attrs.tabNodeId == 'All Nodes') {
						$rootScope.$broadcast('scanner-started', category);
					} else if ($attrs.tabNodeId != 'All Nodes') {
						$rootScope.$broadcast('scanner-single', category,$attrs.tabNodeId);
					}

				}
				var nodeKey = $attrs.tabNodeId;
				$scope.durationTextValue = 10;
				$scope.durationUnit = "m";
				$scope.aggregateFunction = "mean";
				if (!$scope.metereQueue) {
					if (nodeKey == 'All Nodes') {
						$scope.getValue[$scope.category] = { "nodeKey": nodeKey, "duration": $scope.durationTextValue + "" + $scope.durationUnit, "rangeFrom": $scope.rangeFrom, "rangeTo": $scope.rangeTo, "aggregateFunction": $scope.aggregateFunction };

					} else {
						$scope.nodeServiceValues[$scope.category + nodeKey] = { "nodeKey": nodeKey, "duration": $scope.durationTextValue + "" + $scope.durationUnit, "rangeFrom": $scope.rangeFrom, "rangeTo": $scope.rangeTo, "aggregateFunction": $scope.aggregateFunction };
						outPut.push($scope.nodeServiceValues[nodeKey]);
					}
				}
				var $dropdownTrigger = $element.children()[0];
				$scope.timeInterval  = false;
				$scope.intervalMode  = "Duration";

				$scope.setValue = function() {
					if (!$scope.metereQueue) {
						if (nodeKey == 'All Nodes') {
							$scope.getValue[$scope.category] = { "nodeKey": nodeKey, "duration": $scope.durationTextValue + "" + $scope.durationUnit, "rangeFrom": $scope.rangeFrom, "rangeTo": $scope.rangeTo, "aggregateFunction": $scope.aggregateFunction }
						} else {
							$scope.nodeServiceValues[$scope.category + nodeKey] = { "nodeKey": nodeKey, "duration": $scope.durationTextValue + "" + $scope.durationUnit, "rangeFrom": $scope.rangeFrom, "rangeTo": $scope.rangeTo, "aggregateFunction": $scope.aggregateFunction };
						}

						$scope.closeDropdown();
					}

				}
				$scope.firstRender = false;

				$scope.$watch("intervalMode", function(value) {
					switch (value) {
						case "Duration":
							$scope.timeInterval = true;
							break;

						case "Range":
							$scope.timeInterval = false;
							$scope.duration = "";
							break;
					}
				});
				$scope.toggleDropdowns = function() {
					$scope.openPopup = !$scope.openPopup;
				};

				$scope.closeDropdown = function() {
					$scope.openPopup = false;
				}
				$document.on('click', function(e) {
					var target = e.target.parentElement;
					var parentFound = false;

					while (angular.isDefined(target) && target !== null && !parentFound) {
						if ( _.isString(target.className) && !parentFound) {
							if (target === $dropdownTrigger) {
								parentFound = true;
							}
						}
						target = target.parentElement;
					}

					if (!parentFound) {
						$scope.$apply(function() {
							$scope.openPopup = false;
						});
					}
				});

				function getTooltipLabel(unit, d, MAX) {
					if (d == 0) {
						return 0;
					}
					var suffix;
					switch (unit) {
						case "memory":
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

						case "memoryMB":
							if ((d / 1048576) >= 1) {
								d = d / 1048576;
								// if max values less than 20 TB
								if (MAX > 20971520) {
									d = Math.ceil(d);
								} else {
									d = d.toFixed(2);
								}
								suffix = " TB";
							} else if ((d / 1024) >= 1) {
								d = d / 1048;
								// if max values less than 20 GB
								if (MAX > 20480) {
									d = Math.ceil(d);
								} else {
									d = d.toFixed(2);
								}
								suffix = " GB";
							} else {
								d = Math.ceil(d);
								suffix = " MB";
							}
							break;

						case "timeMillis":

							if ((d / 3600000) > 1) {
								d = d / 3600000;
								if (MAX > 72000000) {
									d = Math.ceil(d);
									suffix = " hr";
								} else {
									var minutes = Math.ceil(d - Math.floor(d)) * 60;
									if (minutes != 0) {
										return Math.floor(d) + " hr " + minutes + " m";
									} else {
										return Math.floor(d) + " hr ";
									}

								}

							} else if ((d / 60000) >= 1) {
								d = d / 60000;
								d = Math.ceil(d);

								if (MAX > 1200000) {
									d = Math.ceil(d);
									suffix = " m";
								} else {
									var sec = Math.ceil(d - Math.floor(d)) * 60;
									if (sec != 0) {
										return Math.floor(d) + " m " + minutes + " s";
									} else {
										return Math.floor(d) + " m ";
									}
								}

							} else if ((d / 1000) >= 1) {
								d = d / 1000;
								if (MAX > 20000) {
									d = Math.floor(d);
									suffix = " s";
								} else {
									var ms = Math.ceil(d - Math.floor(d)) * 1000;
									if (ms != 0) {
										return Math.floor(d) + " s " + ms + " ms";
									} else {
										return Math.floor(d) + " s ";
									}
								}

							} else {
								suffix = " ms";
							}
							break;

						case "timeNanos":

							if ((d / 1000000000) >= 1) {
								d = d / 1000000000;
								suffix = " s.";
							} else if ((d / 1000000) >= 1) {
								d = d / 1000000;
								suffix = " μs";
							} else {
								suffix = " ns";
							}
							d = Math.floor(d);
							break;

						case "percent":
							suffix = " %";
							break;

						case "percent1":
							d = d * 100;
							suffix = " %";
							break;

						default:
							if ((d / 1000000000) >= 1) {
								d = d / 1000000000;
								suffix = " B";
								if ((Math.ceil(d - Math.floor(d)) * 1000) == 0) {
									d = Math.floor(d);
								} else {
									d = d.toFixed(2);
								}
							} else if ((d / 1000000) >= 1) {
								d = d / 1000000;
								suffix = " M";
								if ((Math.ceil(d - Math.floor(d)) * 1000) == 0) {
									d = Math.floor(d);
								} else {
									d = d.toFixed(2);
								}
							} else if ((d / 1000) >= 1) {
								d = d / 1000;
								if ((Math.ceil(d - Math.floor(d)) * 1000) == 0) {
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
				}


				function redraw() {
					if (!($scope.singleChartData)) return;
					$scope.categoryData = {};
					var lenghtOfData = 0;
					angular.forEach($scope.selectedNodes, function(data, i) {
						if ($scope.singleChartData[data]) {
							$scope.categoryData[data] = $scope.singleChartData[data];
							lenghtOfData += $scope.categoryData[data].length;
						}
					})
					if (!lenghtOfData) return;
					$scope.firstRender = true;
					var timeSeries = $scope.singleChartData.time;
					
					var margin = { top: 30, right: 50, bottom: 60, left: 50 },
						height = 400 - margin.top - margin.bottom;
					var trans = 80;
					var width = window.innerWidth || document.body.clientWidth;
					if ($scope.metereQueue) {
						height = 160;
						margin = { top: 10, right: 50, bottom: 60, left: 10 };
						trans = 70;
						width = document.getElementById("mquId").offsetWidth - 100;
					}
					if (!$scope.metereQueue) {
						if (width < 800) {
							width = width - 150;
						} else {
							width = width / 2 - 200;
						}
					}
					var unit = $scope.singleChartData.unit;

					var x = d3.time.scale()
						.range([0, width]);

					var y = d3.scale.linear()
						.range([height, 0]);
					$scope.color = d3.scale.category10();
					if ($scope.metereQueue) {
						$scope.color = d3.scale.ordinal()
							.range(['#2196F3', '#4CAF50', '#FF5722', '#0D47A1', '#2E7D32', '#F44336', '#03A9F4', '#8BC34A', '#5E35B1', '#00BCD4', '#5D4037', '#C0CA33', '#E91E63', '#009688', '#546E7A'])
					}


					var xAxis = d3.svg.axis()
						.scale(x)
						.orient("bottom");

					var line = d3.svg.line()
						.x(function(d) {
							return x(d.date);
						})
						.y(function(d) {
							return y(d.temperature);
						});

					// Removing existing graph to display updated graph
					var myNode = $element[0].querySelector('.dropdown-multiselect');
					// Removing child other than firstChild
					if (!myNode.lastChild.isEqualNode(myNode.firstChild)) {
						myNode.removeChild(myNode.lastChild);
					}
					var myDiv = d3.select(myNode).append("div");
					if ($scope.metereQueue) {
						myDiv.attr("class", "queueName")
					} else {
						myDiv.attr("class", "allNodesGraph")
					}

					if ($scope.metereQueue) {
						var svg = myDiv.append("svg")
						.attr("width", width + margin.left + margin.right)
						.attr("height", height)
						.attr("viewBox", "40 0 " + width +" 220")
						.append("g")
						.attr("transform", "translate(" + trans + "," + margin.top + ")");
					} else {
						var svg = myDiv.append("svg")
						.attr("width", width + margin.left + margin.right)
						.attr("height", height + margin.top + margin.bottom)
						.append("g")
						.attr("transform", "translate(" + trans + "," + margin.top + ")");
					}

					$scope.color.domain(d3.keys($scope.categoryData).filter(function(key) {
						return key !== "date";
					}));

					$scope.cities = $scope.color.domain().map(function(name) {
						return {
							name: name,
							values: $scope.singleChartData[name].map(function(d) {
								return { date: d.time, temperature: d.value };
							})
						};
					});
					var queueNameCircle = $scope.cities[0].name;
					x.domain(d3.extent(timeSeries, function(d) {
						return d;
					}));

					var MAX = d3.max($scope.cities, function(c) {
						return d3.max(c.values, function(v) {
							return v.temperature;
						});
					});

					if (MAX == 0) {
						MAX = 100;
					}

					y.domain([0, MAX]);

					var yAxis = d3.svg.axis()
						.scale(y)
						.tickFormat(function(d) {
							if (d == 0) {
								return 0;
							}
							var suffix;
							switch (unit) {
								case "memory":
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

								case "memoryMB":
									if ((d / 1048576) >= 1) {
										d = d / 1048576;
										// if max values less than 20 TB
										if (MAX > 20971520) {
											d = Math.ceil(d);
										} else {
											d = d.toFixed(2);
										}
										suffix = " TB";
									} else if ((d / 1024) >= 1) {
										d = d / 1048;
										// if max values less than 20 GB
										if (MAX > 20480) {
											d = Math.ceil(d);
										} else {
											d = d.toFixed(2);
										}
										suffix = " GB";
									} else {
										d = Math.ceil(d);
										suffix = " MB";
									}
									break;
								case "timeMillis":

									if ((d / 3600000) > 1) {
										d = d / 3600000;
										if (MAX > 72000000) {
											d = Math.ceil(d);
											suffix = " hr";
										} else {
											var minutes = Math.ceil(d - Math.floor(d)) * 60;
											if (minutes != 0) {
												return Math.floor(d) + " hr " + minutes + " m";
											} else {
												return Math.floor(d) + " hr ";
											}

										}

									} else if ((d / 60000) >= 1) {
										d = d / 60000;
										d = Math.ceil(d);

										if (MAX > 1200000) {
											d = Math.ceil(d);
											suffix = " m";
										} else {
											var sec = Math.ceil(d - Math.floor(d)) * 60;
											if (sec != 0) {
												return Math.floor(d) + " m " + minutes + " s";
											} else {
												return Math.floor(d) + " m ";
											}
										}

									} else if ((d / 1000) >= 1) {
										d = d / 1000;
										if (MAX > 20000) {
											d = Math.floor(d);
											suffix = " s";
										} else {
											var ms = Math.ceil(d - Math.floor(d)) * 1000;
											if (ms != 0) {
												return Math.floor(d) + " s " + ms + " ms";
											} else {
												return Math.floor(d) + " s ";
											}
										}

									} else {
										suffix = " ms";
									}
									break;

								case "timeNanos":

									if ((d / 1000000000) >= 1) {
										d = d / 1000000000;
										suffix = " s.";
									} else if ((d / 1000000) >= 1) {
										d = d / 1000000;
										suffix = " μs";
									} else {
										suffix = " ns";
									}
									d = Math.floor(d);
									break;

								case "percent":
									suffix = " %";
									break;

								case "percent1":
									d = d * 100;
									suffix = " %";
									break;

								default:
									if ((d / 1000000000) >= 1) {
										d = d / 1000000000;
										suffix = " B";
										if ((Math.ceil(d - Math.floor(d)) * 1000) == 0) {
											d = Math.floor(d);
										} else {
											d = d.toFixed(2);
										}
									} else if ((d / 1000000) >= 1) {
										d = d / 1000000;
										suffix = " M";
										if ((Math.ceil(d - Math.floor(d)) * 1000) == 0) {
											d = Math.floor(d);
										} else {
											d = d.toFixed(2);
										}
									} else if ((d / 1000) >= 1) {
										d = d / 1000;
										if ((Math.ceil(d - Math.floor(d)) * 1000) == 0) {
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

					if ($scope.metereQueue) {
						svg.append("g")
						.attr("class", "x axis")
						.attr("transform", "translate(0," + height + ")")
						.call(xAxis)
						.selectAll("text")
						.style("text-anchor", "end")
						.style("font-size", "10px")
						.attr("dx", "-.8em")
						.attr("dy", ".15em")
						.attr("transform", function(d) {
							return "rotate(-65)"
							});
					} else {
						svg.append("g")
						.attr("class", "x axis")
						.attr("transform", "translate(0," + height + ")")
						.call(xAxis);
					}

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

					var chartsTooltip = document.getElementById("chartsTooltip");
					chartsTooltip.style.display = "none";

					city.append("path")
						.attr("class", "line")
						.attr("d", function(d) {
							return line(d.values);
						})
						.attr("title", function(d) {
							return d.name
						})
						.style("stroke", function(d) {
							return $scope.color(d.name);
						})
						.style("stroke-width", 2.5)
						.on("mouseover", function(d) {
							d3.select(this).style("stroke-width", '6px');
							chartsTooltip.style.display = "";
							chartsTooltip.innerHTML = d.name;
							chartsTooltip.style.left = (d3.event.pageX) + "px";
							chartsTooltip.style.top = (d3.event.pageY - 28) + "px";

						})
						.on("mouseout", function(d) {
							chartsTooltip.style.display = "none";
							d3.select(this).style("stroke-width", '2.5px');
						});
					var tempData = $scope.cities;
					var keyNameData, keyNameValues;
					var newArr = [];
					for (var key in tempData) {
						keyNameData = tempData[key].name;
						keyNameValues = tempData[key].values;
						for (var ij = 0; ij < keyNameValues.length; ij++) {
							keyNameValues[ij]["name"] = keyNameData;
							newArr.push(keyNameValues[ij])
						}
					}
					city.append("g").selectAll("circle")
						.data(newArr).enter().append("circle")
						.attr("cx", function(d) {
							return x(d.date);
						})
						.attr("cy", function(d) {
							return y(d.temperature);
						})
						.attr("r", 10)
						.style('opacity', 1e-6)
						.on("mouseover", function(d) {
							chartsTooltip.style.display = "";
							var suffix;
							if (unit == 'number') {
								suffix = '';
							} else if (unit == 'timeMillis') {
								suffix = 'ms';
							} else if (unit == 'memoryMB') {
								suffix = 'MB';
							} else if (unit == 'percent' || unit == 'percent1') {
								suffix = '%';
							} else if (unit == 'timeNanos') {
								suffix = 'ns';
							} else if (unit == 'memory') {
								suffix = 'Bytes';
							}
							if (suffix != '') {
								suffix = ' ' + suffix;
							}
							chartsTooltip.innerHTML = d.name + " - " + getTooltipLabel(unit, d.temperature, MAX);
							chartsTooltip.style.left = (d3.event.pageX) + "px";
							chartsTooltip.style.top = (d3.event.pageY - 28) + "px";

						}).on("mouseout", function(d) {
							chartsTooltip.style.display = "none";
							d3.select(this).style("stroke-width", '2.5px');
						});

				}
				if (!$scope.metereQueue && nodeKey == 'All Nodes') {
					$scope.$on('$destroy', function() {
						delete $scope.getValue[$scope.category];
					});
				}

			}


		};
	}
]);
directiveModule.directive('ngQueueLineChart', ['$filter', '$document', '$compile', '$parse', '$rootScope',
	function($filter, $document, $compile, $parse, $rootScope) {
		var outPut;
		if (outPut == undefined) {
			outPut = [];
		}

		return {
			restrict: 'AE',
			scope: {
				optionData: '=',
				selectedNodes: '='

			},
			template: function(element, attrs) {

				var template = '<section class="multiselect-parent dropdown-multiselect" style="width:100%;margin: 9px;font-size: 17px;"><div class="header clearfix ">' + '<span class="pull-left" style="word-break: break-all;"></span>' + '<div style="position: relative;float: right;"  ><span class="pull-right multiselect-parent " >' + '<div style="position: relative;float: right;"  ><span class="pull-right multiselect-parent " >'

				+'</span>' + '</div>'

				'</section>';

				element.html(template);
			},
			link: function($scope, $element, $attrs) {
				$scope.$watch("optionData", function(value, oldValue) {
					if (!value) {
						return;
					}
					$scope.singleChartData = {};
					$scope.singleChartData = $scope.optionData;
					if (d3.select($element[0]).select(".dropdown-multiselect").select("svg")[0]) {
						d3.select($element[0]).select(".dropdown-multiselect").select("svg").remove()
					}
					redraw();
				});

				function redraw() {
					if (!($scope.singleChartData)) return;
					$scope.categoryData = {};
					var lenghtOfData = 0;
					angular.forEach($scope.selectedNodes, function(data, i) {
						if ($scope.singleChartData[data]) {
							$scope.categoryData[data] = $scope.singleChartData[data];
							lenghtOfData += $scope.categoryData[data].length;
						}
					})
					if (!lenghtOfData) return;
					$scope.firstRender = true;
					var timeSeries = $scope.singleChartData.time;
					var margin     = { top: 0, right: 0, bottom: 60, left: 50 },
						height     = 400;
					var trans      = 80;
					var width      = 850;
					var unit       = $scope.singleChartData.unit;
					var x          = d3.time.scale().range([margin.left, width - margin.right]);
					var y          = d3.scale.linear().range([height - margin.top - 40, margin.bottom]);
					$scope.color = d3.scale.ordinal()
						.range(['#2196F3', '#4CAF50', '#FF5722', '#0D47A1', '#2E7D32', '#F44336', '#03A9F4', '#8BC34A', '#5E35B1', '#00BCD4', '#5D4037', '#C0CA33', '#E91E63', '#009688', '#546E7A']);

					var xAxis = d3.svg.axis()
						.scale(x)
						.orient("bottom");
					var line = d3.svg.line()
						.x(function(d) {
							return x(d.date);
						})
						.y(function(d) {
							return y(d.temperature);
						});

					// Removing existing graph to display updated graph
					var myNode = $element[0].querySelector('.dropdown-multiselect');
					// Removing child other than firstChild
					if (!myNode.lastChild.isEqualNode(myNode.firstChild)) {
						myNode.removeChild(myNode.lastChild);
					}
					var myDiv = d3.select(myNode).append("div");
					myDiv.attr("class", "queueUser")

					var svg = myDiv.append("svg")
						.attr("width", width + 'px')
						.attr("height", height + 'px')
						.append("g");

					$scope.color.domain(d3.keys($scope.categoryData).filter(function(key) {
						return key !== "date";
					}));

					$scope.cities = $scope.color.domain().map(function(name) {
						return {
							name: name,
							values: $scope.singleChartData[name].map(function(d) {
								return { date: d[1], temperature: d[0] };
							})
						};
					});
					var queueNameCircle = $scope.cities[0].name;
					x.domain(d3.extent(timeSeries, function(d) {
						return d;
					}));

					var MAX = d3.max($scope.cities, function(c) {
						return d3.max(c.values, function(v) {
							return v.temperature;
						});
					});

					if (MAX == 0) {
						MAX = 100;
					}

					y.domain([0, MAX]);

					var yAxis = d3.svg.axis()
						.scale(y)
						.tickFormat(function(d) {
							if (d == 0) {
								return 0;
							}
							var suffix;
							switch (unit) {
								case "memory":
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

								case "memoryMB":

									suffix = " MB";
									break;

								case "timeMillis":

									if ((d / 3600000) > 1) {
										d = d / 3600000;
										if (MAX > 72000000) {
											d = Math.ceil(d);
											suffix = " hr";
										} else {
											var minutes = Math.ceil(d - Math.floor(d)) * 60;
											if (minutes != 0) {
												return Math.floor(d) + " hr " + minutes + " m";
											} else {
												return Math.floor(d) + " hr ";
											}

										}

									} else if ((d / 60000) >= 1) {
										d = d / 60000;
										d = Math.ceil(d);

										if (MAX > 1200000) {
											d = Math.ceil(d);
											suffix = " m";
										} else {
											var sec = Math.ceil(d - Math.floor(d)) * 60;
											if (sec != 0) {
												return Math.floor(d) + " m " + minutes + " s";
											} else {
												return Math.floor(d) + " m ";
											}
										}

									} else if ((d / 1000) >= 1) {
										d = d / 1000;
										if (MAX > 20000) {
											d = Math.floor(d);
											suffix = " s";
										} else {
											var ms = Math.ceil(d - Math.floor(d)) * 1000;
											if (ms != 0) {
												return Math.floor(d) + " s " + ms + " ms";
											} else {
												return Math.floor(d) + " s ";
											}
										}

									} else {
										suffix = " ms";
									}
									break;

								case "timeNanos":

									if ((d / 1000000000) >= 1) {
										d = d / 1000000000;
										suffix = " s.";
									} else if ((d / 1000000) >= 1) {
										d = d / 1000000;
										suffix = " μs";
									} else {
										suffix = " ns";
									}
									d = Math.floor(d);
									break;

								case "percent":
									suffix = " %";
									break;

								case "percent1":
									d = d * 100;
									suffix = " %";
									break;

								default:
									if ((d / 1000000000) >= 1) {
										d = d / 1000000000;
										suffix = " B";
										if ((Math.ceil(d - Math.floor(d)) * 1000) == 0) {
											d = Math.floor(d);
										} else {
											d = d.toFixed(2);
										}
									} else if ((d / 1000000) >= 1) {
										d = d / 1000000;
										suffix = " M";
										if ((Math.ceil(d - Math.floor(d)) * 1000) == 0) {
											d = Math.floor(d);
										} else {
											d = d.toFixed(2);
										}
									} else if ((d / 1000) >= 1) {
										d = d / 1000;
										if ((Math.ceil(d - Math.floor(d)) * 1000) == 0) {
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
						.attr("transform", "translate(0," + (height - 40) + ")")
						.call(xAxis);

					svg.append("g")
						.attr("class", "y axis")
						.attr("transform", "translate(" + (margin.left) + ", -" + 0 + ")")
						.call(yAxis)
						.append("text")
						.attr("y", 6)
						.attr("dy", ".71em")
						.style("text-anchor", "end");

					/*svg.append("g")
						.attr("class", "y axis")
						.call(yAxis)
						.append("text")
						.attr("transform", "translate(" + (margin.left) + ", -" - 100 + ")")
						.attr("y", 6)
						.attr("dy", ".71em")
						.style("text-anchor", "end");*/

					var city = svg.selectAll(".city")
						.data($scope.cities)
						.enter().append("g")
						.attr("class", "city");

					var chartsTooltip = document.getElementById("chartsTooltip");
					chartsTooltip.style.display = "none";
					chartsTooltip.style.textAlign = "left";

					city.append("path")
						.attr("class", "line")
						.attr("d", function(d) {
							return line(d.values);
						})
						.attr("title", function(d) {
							return d.name
						})
						.style("stroke", function(d) {
							return $scope.color(d.name);
						})
						.style("stroke-width", 2.5)
						.on("mouseover", function(d) {
							d3.select(this).style("stroke-width", '6px');
							chartsTooltip.style.display = "";
							chartsTooltip.innerHTML = d.name;
							chartsTooltip.style.left = (d3.event.pageX) + "px";
							chartsTooltip.style.top = (d3.event.pageY) + "px";

						})
						.on("mouseout", function(d) {
							chartsTooltip.style.display = "none";
							d3.select(this).style("stroke-width", '2.5px');
						});

					var tempData = $scope.cities;
					var keyNameData, keyNameValues;
					var newArr = [];
					for (var key in tempData) {
						keyNameData = tempData[key].name;
						keyNameValues = tempData[key].values;
						for (var ij = 0; ij < keyNameValues.length; ij++) {
							keyNameValues[ij]["name"] = keyNameData;
							newArr.push(keyNameValues[ij])
						}
					}
					city.append("g").selectAll("circle")
						.data(newArr).enter().append("circle")
						.attr("cx", function(d) {
							return x(d.date);
						})
						.attr("cy", function(d) {
							return y(d.temperature);
						})
						.attr("r", 10)
						.style('opacity', 1e-6)
						.on("mousemove", function(d) {
							chartsTooltip.style.display = "";
							var suffix;
							if (unit == 'number') {
								suffix = '';
							} else if (unit == 'timeMillis') {
								suffix = 'ms';
							} else if (unit == 'memoryMB') {
								suffix = 'MB';
							} else if (unit == 'percent' || unit == 'percent1') {
								suffix = '%';
							} else if (unit == 'timeNanos') {
								suffix = 'ns';
							} else if (unit == 'memory') {
								suffix = 'Bytes';
							}
							if (suffix != '') {
								suffix = ' ' + suffix;
							}

							chartsTooltip.innerHTML = "User : " + d.name + "<br/>Queue Utilization : " + d.temperature + suffix;
							chartsTooltip.style.left = (d3.event.pageX - 170) + "px";
							chartsTooltip.style.top = (d3.event.pageY - 45) + "px";

						}).on("mouseout", function(d) {
							chartsTooltip.style.display = "none";
							d3.select(this).style("stroke-width", '2.5px');
						});

				}
			}


		};
	}
]);
