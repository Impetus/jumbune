/* NewJobConfig controller */
'use strict';
angular.module('dapreview.ctrl', [])
    
    .controller('DataAnalysisPreviewController', ['$scope', '$rootScope','common', 'JobFactory', '$compile', '$location',  function ($scope, $rootScope, common, JobFactory, $compile, $location) {
        var self = this;

        self.getJobDtl ={};
        self.cluster = {};
        self.cluster =  common.getSelectedClusterNameForRun();
        self.getJobDtl = common.getJobDetails();

        //Report Json Read
        self.dpWithCriteriaJson = common.getDataProfilerWithCriteriaJsonData();
        self.dpNoCriteriaJson = common.getDataProfilerNoCriteriaJsonData();
        self.dataQualityJson = common.getDataQualityTimelineJsonData();
        //End of Report Json Read

        self.dataAnalysisDetails = common.dataAnalysisDetails;


        self.runJob = function () {
            $location.path('/data-analysis-chart');
        };

    }]);