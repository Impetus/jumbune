/* jobwidget controller */
'use strict';
angular.module('jobwidget.ctrl').controller('JobProfilingController', ['$scope', '$rootScope','common', '$location','$timeout', function($scope, $rootScope, common, $location,$timeout) {
	
	$scope.jobProfilingTab.existingJobName = "";
	$scope.jobProfilingTab.profilingCheck = false;
	$scope.jobProfilingTab.runFromJumbune = false;
    $scope.jobProfilingTab.extJobNameValidation = false;
	$scope.jobProfilingTab.onJobProfilingClick = function () {
		if($scope.jobProfilingTab.profilingCheck){
			$scope.jobProfilingTab.runFromJumbune = true;
		}
	};

	$scope.jobProfilingTab.init = function () {
		if(common.jobMode === 'edit') {
			var jobwidget = common.getWidgetInfo();
			if(jobwidget && (jobwidget["jobProfilingDtl"] !== undefined)) {
				$scope.jobProfilingTab.profilingCheck = jobwidget["jobProfilingDtl"].enableprofilingCheck;
				$scope.jobProfilingTab.runFromJumbune = jobwidget["jobProfilingDtl"].runFromJumbune ;
				$scope.jobProfilingTab.existingJobName = jobwidget["jobProfilingDtl"].existingJobName;
			}
		}
	};
    $scope.jobProfilingTab.handleErrorMessages = function(){
        if($scope.jobProfilingTab.runFromJumbune==false  ){
            if($scope.jobProfilingTab.existingJobName=="" || $scope.jobProfilingTab.existingJobName==undefined )
                $scope.jobProfilingTab.extJobNameValidation =  true;
        }else{
            $scope.jobProfilingTab.extJobNameValidation = false;
        }
    }

}]);