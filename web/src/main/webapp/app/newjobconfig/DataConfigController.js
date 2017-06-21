/* Dashboard controller */
'use strict';
angular.module('dataconfig.ctrl', [])
    
.controller('DataConfigController', ['common', '$location', function(common, $location) {
    var self = this;
    this.dataValidation = false;
    this.dataQuality = false;
    this.dataProfiler = false;
    this.dataQualityVal = '';
    this.dataProfilerVal = '';
    this.dataQualities = [];
    this.dataProfilers = [];

   
    var dataAnalysisArr = [];
    var dataAnalysisObj = {};

    this.init = function () {
        self.dataQualities = common.dataQualityNameArr;
        self.dataProfilers = common.dataProfilingNameArr;
        self.dataValidation = common.getDDV();
        this.selectedDataFields = {};
    };

    this.cancel = function () {
        $location.path('/add-new-job-configuration');
    };

    this.next = function () {
        self.dataValidation && (this.selectedDataFields.enblDataValidation = true);
        self.dataQuality && (this.selectedDataFields.enblDataQuality = true);
        self.dataProfiler && (this.selectedDataFields.enblDataProfiler = true);
        common.setDataConfigDtl(self.selectedDataFields);
        if(self.dataValidation){
            //common.dataAnalysisChart = 3;
            if (self.dataValidation) {
                this.setDataAnalysis('Data Validation', '', 'dv', '');
            }
            if (self.dataQuality) {
                this.setDataAnalysis('Data Quality', self.dataQualityVal, 'dq', '');
            }
            if (self.dataProfiler) {
                this.setDataAnalysis('Data Profiling', self.dataProfilerVal, 'dp', 'false');
            }


            $location.path('/data-validation');
        }else{
            /*if (self.dataQuality && self.dataProfiler) {
                common.dataAnalysisChart = 4;
            } else if (self.dataQuality) {
                common.dataAnalysisChart = 1;
            } else if (self.dataProfiler) {
                common.dataAnalysisChart = 2;
            }*/

            if (self.dataValidation) {
                this.setDataAnalysis('Data Validation', '', 'dv', '');
            }
            if (self.dataQuality) {
                this.setDataAnalysis('Data Quality', self.dataQualityVal, 'dq', '');
            }
            if (self.dataProfiler) {
                this.setDataAnalysis('Data Profiling', self.dataProfilerVal, 'dp', 'false');
            }
            
            $location.path('/data-analysis-preview');
        }
    };

    this.setDataAnalysis = function (enableFieldLabel, name, enableField, isCriteriaBased) {
        
        dataAnalysisObj = {};

        dataAnalysisObj.enableFieldLabel = enableFieldLabel;
        dataAnalysisObj.name = name;
        dataAnalysisObj.enableField = enableField;
        dataAnalysisObj.isCriteriaBased = isCriteriaBased;
        dataAnalysisArr.push(dataAnalysisObj);
        common.dataAnalysisDetails = dataAnalysisArr;
    };

    this.isFormInvalid = function() {
        if(self.dataValidation || self.dataQuality || self.dataProfiler){
            if(self.dataQuality || self.dataProfiler) {
                if(self.dataQuality && !self.dataQualityVal) {
                    return true;
                }
                if(self.dataProfiler && !self.dataProfilerVal) {
                    return true;
                }
            }
            return false;
        }else {
            return true;
        }
    };
}]);