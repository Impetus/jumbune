'use strict';

angular.module('analyzeCluster.ctrl')
.directive("queueHorizontalBarGraph", function() {
	return {
		restrict: 'E',
		scope: {
			isFairScheduler: '=',
			redrawFunction: '=',
			hasError: '=',
			loader: '='
		},
		templateUrl: 'app/analyzeCluster/result/directives/queue-horizontal-bar-graph.directive.html',
		link: function(scope, element, attrs) {

			var div = element[0].children[0];
			var svg = div.children[0];
			var xAxisSvg = element[0].children[1];
			var xAxisSvgHeight = 25;
			scope.parentHeight = element[0].parentElement.offsetHeight;
			var divHeight = scope.parentHeight - xAxisSvgHeight;
			div.style.height = divHeight + 'px';

			svg.setAttribute('width', div.getBoundingClientRect().width);
			var svgWidth = div.getBoundingClientRect().width;

			var tooltip = document.getElementById('tooltip');

			var paddingRight = 15;
			var paddingLeft = 15;
			var paddingTop = 10;

			// Sum of barPaddingRatio and barHeightRatio Ratio should be 1 or 100 (recommended)
			var barPaddingRatio = 0.4;
			var barHeightRatio = 0.5;
			var barMinPadding = 20;
			var barMaxHeight = 50;
			var barMinHeight = 20;
			var barHeight;
			var barPadding;
			var yLabelPaddingRight = 10;
			var valueLabelPaddingLeft = 10;
			var noOfXAxisLines = 6;

			var ctx = element[0].children[2].getContext("2d");
			ctx.font = '14px sans-serif';

			var unit;
			

			scope.dataAvailable = false;

			scope.redrawFunction = function(data) {
				if (data == null || data == undefined || data.length == 0) {
					scope.dataAvailable = true;
					return;
				}
				scope.dataAvailable = true;
				if (scope.isFairScheduler) {
					unit = 'memory';
				} else {
					unit = 'percent';
				}

				data.sort(function(a, b) {
					return b.utilization - a.utilization;
				});


				var height = data.length * (barMinPadding + barMinHeight) + barMinPadding;

				if (height < divHeight) {
					height = divHeight;
					if (barMaxHeight * (2 * data.length + 1) < divHeight) {
						barPadding = (divHeight - (data.length * barMaxHeight)) / (data.length + 1);
						barHeight = barMaxHeight;
					} else {
						var temp = divHeight / (barHeightRatio * data.length + barPaddingRatio * (data.length + 1));
						barPadding = temp * barPaddingRatio;
						if (barPadding >= barMinPadding) {
							barHeight = temp * barHeightRatio;
						} else {
							barHeight = (divHeight - (barMinPadding * (data.length + 1))) / data.length;
						}
					}
				} else {
					barHeight = barMinHeight;
					barPadding = barMinPadding;
				}
				svg.style.height = height + 'px';


				var MAX;
				if (unit == 'percent') {
					MAX = 100;
				} else {
					MAX = getMaxValue(data);
					if (MAX % 10 > 0) {
						MAX = MAX + 10 - (MAX % 10);
					}
				}

				scope.MAX = MAX;
				for (var i = 0; i < data.length; i++) {
					data[i]['printedUtilization'] = getLabelWithUnit(data[i].utilization);
					data[i]['valueLabelWidth'] = getTextWidth(data[i]['printedUtilization']);
				}
				var maxQueueNameWidth = getMaxQueueNameWidth(data);
				var spaceForNames = paddingLeft + maxQueueNameWidth + yLabelPaddingRight;
				scope.spaceForNames = spaceForNames;
				var maxValueLabelWidth = getMaxValueLabelWidth(data);
				var maxBarWidth = svgWidth - spaceForNames - valueLabelPaddingLeft - maxValueLabelWidth - paddingRight;
				var scale = maxBarWidth / MAX;
				var heightCounter = 0;

				var color = generateColor('#E3F2FD', '#0D47A1', data.length);

				/* Creating x Axis vertical Lines Data*/
				scope.xAxisLines = [];
				scope.xAxisLabels = [];
				for (var i = 0; i < noOfXAxisLines; i++) {
					var temp = (maxBarWidth / (noOfXAxisLines - 1)) * i;
					scope.xAxisLines.push({
						'x1': temp,
						'x2': temp,
						'y1': paddingTop,
						'y2': height
					});

					/* Creating x-axis labels */
					var temp1 = (MAX / (noOfXAxisLines - 1)) * i;
					temp1 = getLabelWithUnit(temp1);
					scope.xAxisLabels.push({
						'x': spaceForNames + temp - getTextWidth(temp1) / 2,
						'y': 20,
						'text': temp1
					});
				}

				scope.queuesData = [];

				for (var i = 0; i < data.length; i++) {
					heightCounter = i * barHeight + (i + 1) * barPadding;

					var queue = {
						'gTranslateY': heightCounter,
						"queueNameLabel": {
							'x': paddingLeft,
							'dy': barHeight / 2 + 6,
							'text': data[i].queueName
						},
						'bar': {
							'height': barHeight,
							'width': (data[i].utilization * scale),
							'x': spaceForNames,
							'y': 0,
							'fill': color[i]
						},
						'valueLabel': {
							'x': spaceForNames + (data[i].utilization * scale) + valueLabelPaddingLeft,
							'dy': barHeight / 2 + 6,
							'text': data[i].printedUtilization
						}
					};

					if (scope.isFairScheduler == true) {
						queue['sfs'] = {
							'x1': spaceForNames + data[i].steadyFairShare * scale,
							'x2': spaceForNames + data[i].steadyFairShare * scale,
							'y1': -8,
							'y2': barHeight + 8,
							'text': data[i].steadyFairShare,
						};
					}
					scope.queuesData.push(queue);
				}
			}
			scope.redrawFunction();

			/* Adding mousemove event on Steady Fair Share Line */
			scope.showTooltipFS = function(event, queue) {
				tooltip.innerHTML = 'Steady Fair Share : ' + getLabelWithUnit(Number(queue.sfs.text));
				tooltip.style.opacity = 1;
				tooltip.style.top = (event.pageY - 30) + 'px';
				tooltip.style.left = (event.pageX - 190) + 'px';
			}

			/* Adding mousemove event on bar/rectangle */
			scope.showTooltipBar = function(event, queue) {
				tooltip.innerHTML = 'Average Queue Utilization : ' + queue.valueLabel.text;
				tooltip.style.opacity = 1;
				tooltip.style.top = (event.pageY - 30) + 'px';
				tooltip.style.left = (event.pageX - 120) + 'px';
			}

			scope.hideTooltip = function() {
				tooltip.style.opacity = 0;
			}

			function getMaxValueLabelWidth(data) {
				var max = 0;
				for (var i = 0; i < data.length; i++) {
					if (max < data[i].valueLabelWidth) {
						max = data[i].valueLabelWidth;
					}
				}
				return max;
			}

			function getMaxQueueNameWidth(data) {
				var max = 0,
					temp;
				for (var i = 0; i < data.length; i++) {
					temp = getTextWidth(data[i].queueName);
					if (max < temp) {
						max = temp;
					}
				}
				return max;
			}

			function getMaxValue(data) {
				var max = 0;
				for (var i = 0; i < data.length; i++) {
					if (max < data[i].utilization) {
						max = data[i].utilization;
					}
					if (scope.isFairScheduler) {
						if (max < data[i].steadyFairShare) {
							max = data[i].steadyFairShare;
						}
					}

				}
				if (max == 0) {
					max = 1024;
				}
				return max;
			}

			function getLabelWithUnit(d) {
				var suffix;
				var MAX = scope.MAX;
				switch (unit) {
					case "percent":
						return getFormattedPercent(d);
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
						return d + suffix;
				}
			}

			function getFormattedPercent(number) {
				var formattedPer = number.toFixed(2);
				var length = formattedPer.length;
				if (formattedPer.charAt(length - 1) == '0') { 
					if (formattedPer.charAt(length - 2) == '0') {
						return formattedPer.substring(0, length - 3) + '%';
					} else {
						return formattedPer.substring(0, length - 1) + '%';
					}
				} else {
					return formattedPer + '%';
				}
			}

			function getTextWidth(text) {

				var x = ctx.measureText(text).width;
				return x;
			}

			function generateColor(colorStart, colorEnd, colorCount) {
				// The beginning of your gradient
				var start = convertToRGB(colorStart);
				// The end of your gradient
				var end = convertToRGB(colorEnd);
				// The number of colors to compute
				var len = colorCount;
				//Alpha blending amount
				var alpha = 0.0;
				var saida = [];
				for (var i = 0; i < len; i++) {
					var c = [];
					alpha += (1.0 / len);
					c[0] = start[0] * alpha + (1 - alpha) * end[0];
					c[1] = start[1] * alpha + (1 - alpha) * end[1];
					c[2] = start[2] * alpha + (1 - alpha) * end[2];
					saida.push(convertToHex(c));
				}
				return saida;
			}

			function hex(c) {
				var s = "0123456789abcdef";
				var i = parseInt(c);
				if (i == 0 || isNaN(c)) return "00";
				i = Math.round(Math.min(Math.max(0, i), 255));
				return s.charAt((i - i % 16) / 16) + s.charAt(i % 16);
			}
			/* Convert an RGB triplet to a hex string */
			function convertToHex(rgb) {
				return '#' + hex(rgb[0]) + hex(rgb[1]) + hex(rgb[2]);
			}
			/* Remove '#' in color hex string */
			function trim(s) {
				return (s.charAt(0) == '#') ? s.substring(1, 7) : s
			}
			/* Convert a hex string to an RGB triplet */
			function convertToRGB(hex) {
				var color = [];
				color[0] = parseInt((trim(hex)).substring(0, 2), 16);
				color[1] = parseInt((trim(hex)).substring(2, 4), 16);
				color[2] = parseInt((trim(hex)).substring(4, 6), 16);
				return color;
			}

		}
	};
});