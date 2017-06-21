'use strict';

angular.module('analyzeCluster.ctrl').directive('ngDataCenterConfig', ['$document', function($document) {

		return {
			restrict: 'E',
			scope: {
				serviceData: '=',
				openPopup: '=',
				dcX: '=',
				dcY: '='
			},
			templateUrl: 'app/analyzeCluster/result/directives/ng-data-center-config.directive.html',
			link: function($scope, $element, $attrs) {
				var $dropdownTrigger = $element.children()[0];
				$scope.cpuOperatorBad = "GREATER_THAN_OP";
				$scope.cpuValueBad = 75;
				$scope.cpuOperatorGood = "LESS_THAN_OP";
				$scope.cpuValueGood = 25;
				$scope.memOperatorBad = "GREATER_THAN_OP";
				$scope.memValueBad = 4;
				$scope.memOperatorGood = "LESS_THAN_OP";
				$scope.memValueGood = 4;

				$scope.serviceData = { "color": [{ "bad": { "operator": $scope.cpuOperatorBad, "val": $scope.cpuValueBad }, "good": { "operator": $scope.cpuOperatorGood, "val": $scope.cpuValueGood }, "category": "systemStats.cpu", "stat": "CpuUsage" }, { "bad": { "operator": $scope.memOperatorBad, "val": $scope.memValueBad * 1073741824 }, "good": { "operator": $scope.memOperatorGood, "val": $scope.memValueGood * 1073741824 }, "category": "systemStats.memory", "stat": "UsedMemory" }] };

				$scope.closeDropdown = function() {
					$scope.openPopup = false;
				}
				$scope.setValue = function() {
					$scope.serviceData = { "color": [{ "bad": { "operator": $scope.cpuOperatorBad, "val": $scope.cpuValueBad }, "good": { "operator": $scope.cpuOperatorGood, "val": $scope.cpuValueGood }, "category": "systemStats.cpu", "stat": "CpuUsage" }, { "bad": { "operator": $scope.memOperatorBad, "val": $scope.memValueBad * 1073741824 }, "good": { "operator": $scope.memOperatorGood, "val": $scope.memValueGood * 1073741824 }, "category": "systemStats.memory", "stat": "UsedMemory" }] }
					$scope.closeDropdown();
				}
				// $scope.safeApply = function(fn) {
				// 	var phase = this.$root.$$phase;
				// 	if (phase == '$apply' || phase == '$digest') {
				// 		if (fn && (typeof(fn) === 'function')) {
				// 			fn();
				// 		}
				// 	} else {
				// 		this.$apply(fn);
				// 	}
				// };
				// $document.on('click', function(e) {
				// 	var target = e.target.parentElement;
				// 	var parentFound = false;

				// 	while (angular.isDefined(target) && target !== null && !parentFound) {
				// 		if (_.contains(target.className.split(' '), 'multiselect-parent') && !parentFound) {
				// 			if (target === $dropdownTrigger) {
				// 				parentFound = true;
				// 			}
				// 		}
				// 		target = target.parentElement;
				// 	}

				// 	if (!parentFound) {
				// 		$scope.safeApply(function() {
				// 			$scope.openPopup = false;
				// 		});
				// 	}
				// });
			}


		};
	}
	]);
