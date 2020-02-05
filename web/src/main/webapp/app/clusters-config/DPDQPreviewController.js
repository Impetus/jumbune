/* NewJobConfig controller */
'use strict';
angular.module('dpdqpreview.ctrl', [])
    
    .controller('DPDQPreviewController', ['$scope', '$rootScope','common', 'JobFactory', '$compile', '$location',  function ($scope, $rootScope, common, JobFactory, $compile, $location) {
        var self = this;

        //Report Json Read
        self.dpWithCriteriaJson = common.getDataProfilerWithCriteriaJsonData();
        self.dpNoCriteriaJson = common.getDataProfilerNoCriteriaJsonData();
        self.dataQualityJson = common.getDataQualityTimelineJsonData();
        //End of Report Json Read

        self.dataSuiteDetails = common.dataSuiteDetails;


        self.runJob = function () {
            $location.path('/dpdq-chart');
        };

    }]);