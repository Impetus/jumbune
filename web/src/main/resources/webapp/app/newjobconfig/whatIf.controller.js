/* jobwidget controller */
'use strict';
angular.module('jobwidget.ctrl').controller('WhatIfController', ['$scope', '$rootScope','common', '$location','$timeout', function($scope, $rootScope, common, $location,$timeout) {
	$scope.whatIfTab.checked = false;
    $scope.whatIfTab.changeOpt = ['DFS Block size', 'IO Sort Mb', 'MapRed Child Java Opts'];
	$scope.whatIfTab.selectBoxWhatIf = "";
	$scope.whatIfTab.toField = "";
    $scope.whatIfTab.selectWhatIfValidate= false;
    $scope.whatIfTab.toFieldValidate = false;

	$scope.whatIfTab.init = function () {
		if(common.jobMode === 'edit') {
			var jobwidget = common.getWidgetInfo();
			if(jobwidget && (jobwidget["whatIfDtl"] !== undefined)) {
				$scope.whatIfTab.selectWhatIf = jobwidget["whatIfDtl"].enableWhatIf;
				$scope.whatIfTab.selectBoxWhatIf= jobwidget["whatIfDtl"].selectBoxWhatIf ;
				$scope.whatIfTab.toField = jobwidget["whatIfDtl"].toField;
			}
		}
	};
    $scope.whatIfTab.handleErrorMessages = function(){
        if($scope.whatIfTab.selectBoxWhatIf){
            $scope.whatIfTab.selectWhatIfValidate=false;
        }else{
            $scope.whatIfTab.selectWhatIfValidate= true;
        }
        if($scope.whatIfTab.toField){
            $scope.whatIfTab.toFieldValidate = false;
        }else{
            $scope.whatIfTab.toFieldValidate = true;
        }
    }

}]);