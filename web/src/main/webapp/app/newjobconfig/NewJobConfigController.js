/* NewJobConfig controller */
'use strict';
angular.module('newjobconfig.ctrl', [])

.controller('NewJobConfigController', ['$scope', '$http', '$rootScope', 'common', '$location', 'editClusterFactory', 'analyzeClusterFactory', 'deleteRecurringFactory', 'getRecurringFactory', 'validateLicenseFactory', 'getJobValidateFactory','$timeout','getDefaultRootFactory',
    // function ($scope, $rootScope, common, $location,editClusterFactory,analyzeClusterFactory,deleteRecurringFactory,getRecurringFactory) {
    function($scope, $http, $rootScope, common, $location, editClusterFactory, analyzeClusterFactory, deleteRecurringFactory, getRecurringFactory, validateLicenseFactory, getJobValidateFactory,$timeout,getDefaultRootFactory) {
        var self = this;
        self.clusterList = [];
        self.selectedCluster = "";
        self.dataAnalysis = false;
        self.jobAnalysis = false;
        self.monitoring = false;
        $scope.hideRecurring = false;
        $scope.errorMessageShow = false;
        $scope.errorMessageShowHdfs = false;
        $scope.hideJob = false;
        $scope.errorSelctedClus = false;
        $scope.errorHdfspath = false;
        $scope.errordlcdata = false;
        $scope.errorCleandata = false;
        self.jobName = "";
        self.jobSubUser = "";
        //$scope.getClusterList=[];
        var dataAnalysisArr = [];
        //  self.getClusterList = [];
        //self.getClusterList = {"DQT_04":"/final01","DQT_03":"/final01"};
        self.getClusterList = [];

        var dataAnalysisObj = {};

        //this.dataValidation = 'false';
        this.dataValidation = undefined;
        //this.dataValidation;
        $scope.errorFile = false;
        $scope.errorFileXML = false;
        this.manualRadio = "TEXT";
        this.dataValidationXml = 'xml';
        this.dataValidationJson = 'json';
        //self.enableProfiling = 'false';
        self.enableProfiling = undefined;
        self.enableDataComp = undefined;
        //self.enableProfiling;
        self.dataValidationTab = false;
        self.selectedTab = false;
        self.hideManageTable = false;

        $scope.change = function(device) {
            angular.forEach($scope.devices, function(item) {
                item.checked = false;
            });
            device.checked = true;
        };
        self.dataValidationShow = function() {
            self.active = 'dataValidationShow';
            self.activeTab = 'Data Validation';
            self.showTableAnalyzeData = true;
            self.showDataFormat = true;
            self.showhdfsPath = true;
            self.showhdlc = false;
            self.showdqt = false;
            self.showdlc = false;
        }
        self.dataProfilingShow = function() {
            self.active = 'dataProfilingShow';
            self.activeTab = 'Data Profiling';
            self.showTableAnalyzeData = true;
            self.showdqt = false;
            self.showDataFormat = false;
            self.showhdfsPath = true;
            self.showhdlc = false;
            self.showdlc = false;
        }
        self.dataQualityShow = function() {
            self.active = 'dataQualityShow';
            self.activeTab = 'Data Quality';
            self.showTableAnalyzeData = true;
            self.showDataFormat = false;
            self.showhdfsPath = true;
            self.showhdlc = false;
            self.showdqt = true;
            self.showdlc = false;
        }
        self.dataLCShow = function() {
            self.active = 'dataLCShow';
            self.activeTab = 'Data Cleansing';
            self.showTableAnalyzeData = true;
            self.showDataFormat = false;
            self.showhdfsPath = true;
            self.showhdlc = true;
            self.showdqt = false;
            self.showdlc = true;
        }
        self.dataSourceShow = function() {
            self.active = 'dataSCShow';
            self.activeTab = 'Data Source Comparison';
            self.showTableAnalyzeData = true;
            self.showDataFormat = false;
            self.showhdfsPath = false;
            self.showdqt = false;
            self.showhdlc = false;
            self.showdlc = false;
        }
        self.viewRecurring = function() {
            $scope.hideRecurring = true;
        }
        self.viewJob = function() {
            $scope.hideRecurring = false;
        }

        self.init = function() {
        var getBackFlag = common.getsJobDetailsFlagRes();
        var backActiveTab = common.getActiveTab();
            if (getBackFlag) {
                self.activeTab = backActiveTab;
                if ( backActiveTab == 'Data Validation') {
                    self.dataValidationShow();
                }
                if ( backActiveTab == 'Data Quality' ) {
                    self.dataQualityShow();
                }
                /*if ( backActiveTab == 'Data Source Comparison' ) {
                    self.dataSourceShow();
                }*/
                if ( backActiveTab == 'Data Cleansing' ) {
                    self.dataLCShow();
                }
                if ( backActiveTab == 'Data Profiling' ) {
                    self.dataProfilingShow();
                }
            } else {
                self.showTableAnalyzeData = false;
                self.showDataFormat = false;
                self.showhdfsPath = true;
                self.showhdlc = true;
                self.showdqt = false;
                self.showdlc = false;
            }
            //$("a.my-tool-tip").tooltip();
            $("td.my-tool-tip").tooltip();
            $("h2.my-tool-tip").tooltip();
            $(".my-tool-tip").tooltip();
            self.dataValidation = common.getDDV();
            this.selectedDataFields = {};
            $scope.jobConfigMethods = common.getJobConfigMethod();
            $scope.recentJobResponse = common.getResonseData();


            self.clusterList = common.getClusterName();
            editClusterFactory.getCluster({

                },

                function(data) {

                    self.clusterList = data;

                },
                function(e) {
                    console.log(e);
                });
            var req = {
                method: 'GET',
                url: 'apis/jobanalysis/jobhdfsdetails',
                headers: { 'Content-Type': undefined },
                transformRequest: angular.identity,
                data: self.getClusterList,
            };
            $http(req).then(function(data) {
                self.getClusterList = data;
                if (self.getClusterList.data.length == 0) {
                    self.hideManageTable = true;
                } else {}

            }, function(error) {
                console.log("error", error)
            });

            //self.clusterList = ["First","secnd"]
            var jobDetails = common.getJobDetails();
            if (!jobDetails) {
                jobDetails = { data: false, job: false, monitor: false, jobName: "", clusterName: "" };
            }

            var flag = self.isObjEmpty(jobDetails);
            if (flag) {
                self.jobFlag = "Add";
            } else {
                self.jobFlag = "Update";
            }
            self.dataAnalysis = jobDetails.data;
            self.jobAnalysis = jobDetails.job;
            self.monitoring = jobDetails.monitor;
            self.jobName = jobDetails.jobName;
            self.selectedCluster = jobDetails.clusterName;
            var searchModule = $location.search().module;

            if (searchModule) {} else {
                $scope.autoFillFunObj = { analyzeJob: self.autoFillAnalyzeJob(), optimizedJob  : self.autoFillOptimizeJob(), analyzeData: self.autoFillAnalyzeData() } 
                $scope.autoFillFunObj.searchModule;
            }
        };
        self.autoFillAnalyzeJob = function() {
            //$scope.recentJobResponse = common.getResonseData();
            $scope.NewJobConfigController.jobName = $scope.recentJobResponse.jumbuneJobName;
            $scope.NewJobConfigController.selectedCluster = $scope.recentJobResponse.operatingCluster;
        }
        self.autoFillAnalyzeData = function() {
            $scope.recentJobResponse = common.getResonseData();
            //$scope.setCurrentTab();
            $scope.NewJobConfigController.jobName = $scope.recentJobResponse.jumbuneJobName;
            $scope.NewJobConfigController.jobSubUser = $scope.recentJobResponse.operatingUser;
            $scope.NewJobConfigController.selectedCluster = $scope.recentJobResponse.operatingCluster;
            $scope.NewJobConfigController.hdfsInputPath = $scope.recentJobResponse.hdfsInputPath;
            $scope.NewJobConfigController.parameters = $scope.recentJobResponse.parameters;
             if ($scope.recentJobResponse.enableDataProfiling == 'TRUE') {
                //$scope.NewJobConfigController.enableProfiling = 'dataProfiling';
                //$location.path('/define-analyzeData-info');
                self.active = 'dataProfilingShow';
                self.activeTab = 'Data Profiling';
                self.showTableAnalyzeData = true;
                self.showdqt = false;
                self.showhdfsPath = true;
            }
         /*   if ($scope.recentJobResponse.enableDataProfiling == 'FALSE') {
                //$scope.NewJobConfigController.enableProfiling = 'dataQuality';
                self.showTableAnalyzeData = true;
                self.showdqt = true;
                self.activeTab = 'Data Quality';
            }*/
            if ($scope.recentJobResponse.isDataSourceComparisonEnabled == 'TRUE') {
                //$scope.NewJobConfigController.enableProfiling = 'dataComparison';
                self.showTableAnalyzeData = true;
                self.showdqt = false;
                self.active = 'dataSCShow'
                self.showhdfsPath = false;
                self.activeTab = 'Data Source Comparison';
            }
            /*if ($scope.recentJobResponse.enableDataValidation == 'FALSE') {
                //$scope.NewJobConfigController.dataValidation = 'false';
                self.showTableAnalyzeData = true;
                self.showdqt = false;
            }*/
            if ($scope.recentJobResponse.enableDataQualityTimeline == 'TRUE') {
                //$scope.NewJobConfigController.dataValidation = 'true';
                self.showTableAnalyzeData = true;
                self.showdqt = true;
                self.active = 'dataQualityShow'
                self.activeTab = 'Data Quality';
                self.showhdfsPath = true;
            }
            if ($scope.recentJobResponse.enableDataValidation == 'TRUE') {
                //$scope.NewJobConfigController.dataValidation = 'false';
                self.showTableAnalyzeData = true;
                self.showdqt = false;
                self.activeTab = 'Data Validation';
                self.active = 'dataValidationShow'
                self.showDataFormat = true;
                self.showhdfsPath = true;
                $scope.NewJobConfigController.manualRadio = 'TEXT';
            }
            if ($scope.recentJobResponse.enableJsonDataValidation == 'TRUE') {
                self.showTableAnalyzeData = true;
                self.showdqt = false;
                self.activeTab = 'Data Validation';
                self.active = 'dataValidationShow'
                self.showDataFormat = true;
                self.showhdfsPath = true;
                $scope.NewJobConfigController.manualRadio = 'JSON';
            }
            if ($scope.recentJobResponse.enableXmlDataValidation == 'TRUE') {
                self.showTableAnalyzeData = true;
                self.showdqt = false;
                self.activeTab = 'Data Validation';
                self.active = 'dataValidationShow'
                self.showDataFormat = true;
                self.showhdfsPath = true;
                $scope.NewJobConfigController.manualRadio = 'XML';
            }
            if ($scope.recentJobResponse.isDataCleansingEnabled == 'TRUE') {
                self.activeTab = 'Data Cleansing';
                self.active = 'dataLCShow';
                self.showTableAnalyzeData = true;
                self.showDataFormat = false;
                self.showhdfsPath = true;
                self.showhdlc = true;
                self.showdqt = false;
                self.showdlc = true;
                $scope.NewJobConfigController.dlcRoot = $scope.recentJobResponse.dataCleansing.dlcRootLocation;
                $scope.NewJobConfigController.cleanDataRoot = $scope.recentJobResponse.dataCleansing.cleanDataRootLocation;
            }



        }

        self.autoFillOptimizeJob = function() {
            $scope.recentJobResponse = common.getResonseData();
            $scope.NewJobConfigController.jobName = $scope.recentJobResponse.jumbuneJobName;
            $scope.NewJobConfigController.selectedCluster = $scope.recentJobResponse.operatingCluster;

        }

        self.disableCheckbox = function() {
            return (self.jobName === "" || self.clusterList.length === 0 || !self.selectedCluster)
        };
        self.disableCheckboxAnalyzeData = function() {
            return (self.jobName === "" || self.clusterList.length === 0 || !self.selectedCluster || self.jobSubUser === "")/* return (self.jobName === "" || self.clusterList.length === 0 || !self.selectedCluster || self.jobSubUser === "" || self.enableProfiling === undefined
                || self.enableProfiling === "" || !self.enableProfiling )*/
        };
        self.disableSelectCluster = function() {
            return (!self.jobName || !self.clusterList.length);
        };

        self.disableSubmitSelectCluster = function() {
            return (self.clusterList.length === 0 || !self.selectedCluster)
        };
        self.nextTunning = function() {
            //$location.path('/job-widget');
            var errorSelctedClusValue = document.getElementById("selectedCluster").value;
                if (!errorSelctedClusValue) {
                    $scope.errorSelctedClus = true;
                    return false;
                } else {
                    $scope.errorSelctedClus = false;
                }
            getJobValidateFactory.getJobValidate(
                //{requestType : 'DELETE'clusterName : 'key'},
                { jobName: self.jobName }, {},
                function(data) {
                    if (data.STATUS == "ERROR" && data.jobName) {
                        $scope.errorMessage = data.jobName;
                        $scope.errorMessageShow = true;
                    } else {
                        var temp = {
                            "jobName": self.jobName,
                            "selCluster": self.selectedCluster
                        }

                        common.setNewJobDetail(temp);
                        $location.path('/define-optimized-info');
                    }

                },
                function(e) {
                    console.log(e);
                });
            //$location.path('/define-optimized-info').search({flag: 'nextTunning'});
        };
        self.backTunning = function() {
            $location.path('/');
        };

        self.next = function() {
           var errorSelctedClusValue = document.getElementById("selectedCluster").value;
            if (!errorSelctedClusValue) {
                $scope.errorSelctedClus = true;
                return false;
            } else {
                $scope.errorSelctedClus = false;
            }
            getJobValidateFactory.getJobValidate(
                //{requestType : 'DELETE'clusterName : 'key'},
                { jobName: self.jobName }, {},
                function(data) {
                    if (data.STATUS == "ERROR" && data.jobName) {
                        $scope.errorMessage = data.jobName;
                        $scope.errorMessageShow = true;
                    } else {
                        var jobDetails = {
                            jobName: self.jobName,
                            clusterName: self.selectedCluster
                        };
                        //common.setSelectedJobName(self.jobName);
                        common.setJobDetails(jobDetails);
                        $location.path('/define-job-info');
                        //$location.path('/job-widget');
                    }

                },
                function(e) {
                    console.log(e);
                });

        };
        self.submitRecurring = function(key, clusterName) {
            var clusList = common.getAnalyzeDataDetail();

            var dataRecurringSubmit = {
                "dataQualityTimeLineConfig": { "showJobResult": "TRUE" },
                "enableDataQualityTimeline": "TRUE",
                "jumbuneJobName": key,
                "operatingCluster": clusterName
            }
            common.setOptimizeJobName(key);
            common.setDataRecurring(dataRecurringSubmit);
            common.setDQTRecFlag(dataRecurringSubmit.enableDataQualityTimeline);
            var mergedObject = angular.extend({}, dataRecurringSubmit);
            var jsonDataIs = JSON.stringify(mergedObject);
            $scope.content = new FormData();
            $scope.content.append("jsonData", jsonDataIs);
            var req = {
                method: 'POST',
                url: 'apis/jobanalysis/save',
                headers: { 'Content-Type': undefined },
                transformRequest: angular.identity,
                data: $scope.content,
            };

            $http(req).then(function(data) {
                $location.path('/analyze-data-quality');

            }, function(error) {
                console.log("error", error)
            });

        };
        self.backJob = function() {
            $location.path('/');
        };
        /** Function to delete already running DQT jobs */
        self.deleteRecurring = function(key, index) {

            var listDeleted = key;
            deleteRecurringFactory.deleteRecurringForm(
                //{requestType : 'DELETE'clusterName : 'key'},
                { jobName: listDeleted }, {},
                function(data) {
                    if (data) {
                        self.getClusterList.data.splice(index, 1);
                        //delete self.getClusterList.data[key];
                    }

                },
                function(e) {});

        };
        $scope.setCurrentTab = function(tab) {
            common.setCurrentTab(tab);
            $scope.switchTab = tab;
        };
        $scope.$watch('NewJobConfigController.selectedTab', function(newVal) {
            common.setCurrentTab(newVal);
        });

        $scope.$watch('NewJobConfigController.selectedTab', function(newVal) {
            common.setCurrentTab(newVal);
            if (newVal == 'DataQuality') {
                $scope.hideRecurring = true;
                $location.path('/add-data-quality')
            }
        });
        $scope.getDefaultRootFun = function() {
             getDefaultRootFactory.getDefaultRoot(
                { clusterName: self.selectedCluster }, {},
                function(data) {
                   self.dlcRoot = data.dlcRoot;
                },
                function(e) {
                    console.log(e);
                });

        }
        self.nextAnalyzeData = function() {
            var currTab = common.getCurrentTab();
            var errorSelctedClusValue = document.getElementById("selectedCluster").value;
                if (!errorSelctedClusValue) {
                    $scope.errorSelctedClus = true;
                    return false;
                } else {
                    $scope.errorSelctedClus = false;
                }
                var errorHdfspathValue = document.getElementById("hdfspath").value;
                if (!errorHdfspathValue) {
                    $scope.errorHdfspath = true;
                    return false;
                } else {
                    $scope.errorHdfspath = false;
                }
            if (self.showTableAnalyzeData == true && self.showDataFormat == true && document.querySelector('input[name="capabilityRadios"]:checked').value == "TEXT" && self.active == 'dataValidationShow') {
                var filePath = common.getJobJarFile();
                getJobValidateFactory.getJobValidate({
                        jobName: self.jobName
                    }, {},
                    function(data) {

                        var AnalyzeDataJsonValidation = {
                            hdfsInputPath: self.hdfsInputPath,
                            jumbuneJobName: self.jobName,
                            operatingUser: self.jobSubUser,
                            operatingCluster: self.selectedCluster,
                            parameters: self.parameters
                        };
                        var mergedObject = angular.extend({}, AnalyzeDataJsonValidation);
                        var jsonDataIs = JSON.stringify(mergedObject);
                        $scope.content = new FormData();
                        $scope.content.append("jsonData", jsonDataIs);
                        var req = {
                            method: 'POST',
                            url: '/apis/validateservice/validatejobinput',
                            headers: {
                                'Content-Type': undefined
                            },
                            transformRequest: angular.identity,
                            data: $scope.content,
                        };

                        $http(req).then(function(data1) {

                            if (data.STATUS == "ERROR") {
                                $scope.errorMessage = data.jobName;
                                $scope.errorMessageShow = true;
                            } else {
                                $scope.errorMessageShow = false;
                            }

                            if (data1.data.STATUS == "ERROR") {
                                $scope.errorMessageHdfs = data1.data.hdfsInputPath;
                                $scope.errorMessageShowHdfs = true;
                            } else {
                                $scope.errorMessageShowHdfs = false;
                            }

                            if (data.STATUS == "SUCCESS" && data1.data.STATUS == "SUCCESS") {
                                var AnalyzeData = {
                                    hdfsInputPath: self.hdfsInputPath,
                                    jumbuneJobName: self.jobName,
                                    operatingUser: self.jobSubUser,
                                    operatingCluster: self.selectedCluster,
                                    parameters: self.parameters
                                };


                                common.setAnalyzeDataDetail(AnalyzeData);
                                $location.path('/data-validation');
                            }
                        }, function(error) {
                            console.log("error", error)
                        });

                    },
                    function(e) {
                        console.log(e);
                    });


            }
            var filePathName = common.getJobJarFile();
            if (self.showTableAnalyzeData == true && self.showDataFormat == true && document.querySelector('input[name="capabilityRadios"]:checked').value == "JSON") {
                var filePathLocalValue = document.getElementById("filePathLocal").value;
                if (!filePathLocalValue) {
                    $scope.errorFile = true;
                    return false;
                } else {
                    $scope.errorFile = false;
                }
                var filePath = common.getJobJarFile();
                getJobValidateFactory.getJobValidate({
                        jobName: self.jobName
                    }, {},
                    function(data) {

                        var AnalyzeDataJsonValidation = {
                            hdfsInputPath: self.hdfsInputPath,
                            jumbuneJobName: self.jobName,
                            operatingUser: self.jobSubUser,
                            operatingCluster: self.selectedCluster,
                            parameters: self.parameters
                        };
                        var mergedObject = angular.extend({}, AnalyzeDataJsonValidation);
                        var jsonDataIs = JSON.stringify(mergedObject);
                        $scope.content = new FormData();
                        $scope.content.append("jsonData", jsonDataIs);
                        var req = {
                            method: 'POST',
                            url: '/apis/validateservice/validatejobinput',
                            headers: { 'Content-Type': undefined },
                            transformRequest: angular.identity,
                            data: $scope.content,
                        };

                        $http(req).then(function(data1) {

                            if (data.STATUS == "ERROR") {
                                $scope.errorMessage = data.jobName;
                                $scope.errorMessageShow = true;
                            } else {
                                $scope.errorMessageShow = false;
                            }

                            if (data1.data.STATUS == "ERROR") {
                                $scope.errorMessageHdfs = data1.data.hdfsInputPath;
                                $scope.errorMessageShowHdfs = true;
                            } else {
                                $scope.errorMessageShowHdfs = false;
                            }

                            if (data.STATUS == "SUCCESS" && data1.data.STATUS == "SUCCESS") {
                                var AnalyzeData = {
                                    jumbuneJobName: self.jobName,
                                    jobSubmissionUser: self.jobSubUser,
                                    selectedCluster: self.selectedCluster,
                                    hdfsInputPath: self.hdfsInputPath,
                                    parameters: self.parameters
                                };
                                common.setAnalyzeDataDetailJSON(AnalyzeData);
                                var filenameis = $scope.filePath;
                                var filePath = filePathName;
                                var jsonDataIs = JSON.stringify(AnalyzeData);
                                $scope.content = new FormData();
                                $scope.content.append("inputFile", filePath);
                                $scope.content.append("jsonData", jsonDataIs);

                                if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
                                    $scope.$apply();
                                }

                                var req = {
                                    method: 'POST',
                                    url: '/apis/dvreport/json',
                                    headers: { 'Content-Type': undefined },
                                    transformRequest: angular.identity,
                                    data: $scope.content,
                                };

                                $http(req).then(function(data2) {
                                    $scope.jsonDATA = angular.copy(data2)
                                    if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
                                        $scope.$apply();
                                    }
                                    common.setdataValidationJSONData(data2)
                                    $location.path('/data-validation-json');
                                }, function(error) {
                                    console.log("error", error)
                                });
                            }

                        }, function(error) {
                            console.log("error", error)
                        });

                    },
                    function(e) {
                        console.log(e);
                    });
            }
            var filePath = common.getJobJarFile();
            if (self.showTableAnalyzeData == true && self.showDataFormat == true && document.querySelector('input[name="capabilityRadios"]:checked').value == "XML") {
                var filePathLocalXML = document.getElementById("filePathLocalXML").value;
                var filePathLocalValueXML = document.getElementById("filePathLocalXML").value;
                if (!filePathLocalValueXML) {
                    $scope.errorFileXML = true;
                    return false;
                } else {
                    $scope.errorFileXML = false;
                }

                getJobValidateFactory.getJobValidate({
                        jobName: self.jobName
                    }, {},
                    function(data) {

                        var filePath = common.getJobJarFile();
                        var AnalyzeDataJsonValidation = {
                            hdfsInputPath: self.hdfsInputPath,
                            jumbuneJobName: self.jobName,
                            operatingUser: self.jobSubUser,
                            operatingCluster: self.selectedCluster,
                            parameters: self.parameters
                        };
                        var mergedObject = angular.extend({}, AnalyzeDataJsonValidation);
                        var jsonDataIs = JSON.stringify(mergedObject);
                        $scope.content = new FormData();
                        $scope.content.append("jsonData", jsonDataIs);
                        var req = {
                            method: 'POST',
                            url: '/apis/validateservice/validatejobinput',
                            headers: { 'Content-Type': undefined },
                            transformRequest: angular.identity,
                            data: $scope.content,
                        };

                        $http(req).then(function(data1) {

                            if (data.STATUS == "ERROR") {
                                $scope.errorMessage = data.jobName;
                                $scope.errorMessageShow = true;
                            } else {
                                $scope.errorMessageShow = false;
                            }

                            if (data1.data.STATUS == "ERROR") {
                                $scope.errorMessageHdfs = data1.data.hdfsInputPath;
                                $scope.errorMessageShowHdfs = true;
                            } else {
                                $scope.errorMessageShowHdfs = false;
                            }

                            if (data.STATUS == "SUCCESS" && data1.data.STATUS == "SUCCESS") {
                                var AnalyzeData = {
                                    jumbuneJobName: self.jobName,
                                    jobSubmissionUser: self.jobSubUser,
                                    selectedCluster: self.selectedCluster,
                                    hdfsInputPath: self.hdfsInputPath,
                                    parameters: self.parameters
                                };
                                common.setAnalyzeDataDetailXML(AnalyzeData);

                                var jsonDataIs = JSON.stringify(AnalyzeData);
                                $scope.content = new FormData();
                                $scope.content.append("inputFile", filePath);
                                $scope.content.append("jsonData", jsonDataIs);

                                if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
                                    $scope.$apply();
                                }

                                var req = {
                                    method: 'POST',
                                    url: '/apis/xmldvreport/inferSchema',
                                    headers: { 'Content-Type': undefined },
                                    transformRequest: angular.identity,
                                    data: $scope.content,
                                };

                                $http(req).then(function(data) {
                                    $scope.xmlDATA = angular.copy(data)
                                    if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
                                        $scope.$apply();
                                    }
                                    common.setdataValidationXMLData(data)
                                    $location.path('/data-validation-xml');
                                }, function(error) {
                                    console.log("error", error)
                                });
                            }
                        }, function(error) {
                            console.log("error", error)
                        });

                    },
                    function(e) {
                        console.log(e);
                    });
            }
            //var dataProradio = document.getElementById('dataProfilingRadio');
            var dataQualityradio = document.getElementById('dataQuality');
            /*if (dataProradio.checked == true) {
                currTab = 'DataProfiling'
            }*/
            if (self.active == 'dataProfilingShow' && self.showTableAnalyzeData == true) {
                getJobValidateFactory.getJobValidate({
                        jobName: self.jobName
                    }, {},
                    function(data) {

                        var filePath = common.getJobJarFile();
                        var AnalyzeDataJsonValidation = {
                            hdfsInputPath: self.hdfsInputPath,
                            jumbuneJobName: self.jobName,
                            operatingUser: self.jobSubUser,
                            operatingCluster: self.selectedCluster,
                            parameters: self.parameters
                        };
                        var mergedObject = angular.extend({}, AnalyzeDataJsonValidation);
                        var jsonDataIs = JSON.stringify(mergedObject);
                        $scope.content = new FormData();
                        $scope.content.append("jsonData", jsonDataIs);
                        var req = {
                            method: 'POST',
                            url: '/apis/validateservice/validatejobinput',
                            headers: { 'Content-Type': undefined },
                            transformRequest: angular.identity,
                            data: $scope.content,
                        };

                        $http(req).then(function(data1) {

                            if (data.STATUS == "ERROR") {
                                $scope.errorMessage = data.jobName;
                                $scope.errorMessageShow = true;
                            } else {
                                $scope.errorMessageShow = false;
                            }

                            if (data1.data.STATUS == "ERROR") {
                                $scope.errorMessageHdfs = data1.data.hdfsInputPath;
                                $scope.errorMessageShowHdfs = true;
                            } else {
                                $scope.errorMessageShowHdfs = false;
                            }

                            if (data.STATUS == "SUCCESS" && data1.data.STATUS == "SUCCESS") {
                                var AnalyzeData = {
                                    jobName: self.jobName,
                                    jobSubUser: self.jobSubUser,
                                    clusterName: self.selectedCluster,
                                    hdfsInputPath: self.hdfsInputPath,
                                    parameters: self.parameters
                                };
                                common.setAnalyzeDataDetail(AnalyzeData);
                                $location.path('/define-analyzeData-info')
                            }
                        }, function(error) {
                            console.log("error", error)
                        });

                    },
                    function(e) {
                        console.log(e);
                    });

            }
            var dataSCradio = document.getElementById('dataSourceComparison');
            if (self.active == 'dataSCShow' && self.showTableAnalyzeData == true) {
                    //$location.path('/data-validation-comp')
                getJobValidateFactory.getJobValidate({
                        jobName: self.jobName
                    }, {},
                    function(data) {

                        var filePath = common.getJobJarFile();
                        var DataSourceValidation = {
                            jumbuneJobName: self.jobName,
                            operatingUser: self.jobSubUser,
                            operatingCluster: self.selectedCluster,
                            parameters: self.parameters
                        };
                        var mergedObject = angular.extend({}, DataSourceValidation);
                        var jsonDataIs = JSON.stringify(mergedObject);
                        $scope.content = new FormData();
                        $scope.content.append("jsonData", jsonDataIs);
                        var req = {
                            method: 'POST',
                            url: '/apis/validateservice/validatejobinput',
                            headers: { 'Content-Type': undefined },
                            transformRequest: angular.identity,
                            data: $scope.content,
                        };

                        $http(req).then(function(data1) {

                            if (data.STATUS == "ERROR") {
                                $scope.errorMessage = data.jobName;
                                $scope.errorMessageShow = true;
                            } else {
                                $scope.errorMessageShow = false;
                            }

                            if (data.STATUS == "SUCCESS") {
                                var AnalyzeData = {
                                    jobName: self.jobName,
                                    jobSubUser: self.jobSubUser,
                                    clusterName: self.selectedCluster,
                                    parameters: self.parameters
                                };
                                common.setAnalyzeDataDetailXML(AnalyzeData);
                                $location.path('/data-validation-comp')
                            }
                        }, function(error) {
                            console.log("error", error)
                        });

                    },
                    function(e) {
                        console.log(e);
                    });
            }
            if (self.active == 'dataQualityShow' && self.showTableAnalyzeData == true || self.showdqt == true) {
                //$location.path('/recurring-info');
                getJobValidateFactory.getJobValidate({
                        jobName: self.jobName
                    }, {},
                    function(data) {

                        var filePath = common.getJobJarFile();
                        var AnalyzeDataJsonValidation = {
                            hdfsInputPath: self.hdfsInputPath,
                            jumbuneJobName: self.jobName,
                            operatingUser: self.jobSubUser,
                            operatingCluster: self.selectedCluster,
                            parameters: self.parameters
                        };
                        var mergedObject = angular.extend({}, AnalyzeDataJsonValidation);
                        var jsonDataIs = JSON.stringify(mergedObject);
                        $scope.content = new FormData();
                        $scope.content.append("jsonData", jsonDataIs);
                        var req = {
                            method: 'POST',
                            url: '/apis/validateservice/validatejobinput',
                            headers: { 'Content-Type': undefined },
                            transformRequest: angular.identity,
                            data: $scope.content,
                        };

                        $http(req).then(function(data1) {

                            if (data.STATUS == "ERROR") {
                                $scope.errorMessage = data.jobName;
                                $scope.errorMessageShow = true;
                            } else {
                                $scope.errorMessageShow = false;
                            }

                            if (data1.data.STATUS == "ERROR") {
                                $scope.errorMessageHdfs = data1.data.hdfsInputPath;
                                $scope.errorMessageShowHdfs = true;
                            } else {
                                $scope.errorMessageShowHdfs = false;
                            }

                            if (data.STATUS == "SUCCESS" && data1.data.STATUS == "SUCCESS") {
                                var AnalyzeData = {
                                    jobName: self.jobName,
                                    jobSubUser: self.jobSubUser,
                                    clusterName: self.selectedCluster,
                                    hdfsInputPath: self.hdfsInputPath,
                                    parameters: self.parameters
                                };
                                common.setAnalyzeDataDetail(AnalyzeData);
                                $location.path('/add-data-quality');
                            }
                        }, function(error) {
                            console.log("error", error)
                        });

                    },
                    function(e) {
                        console.log(e);
                    });
            }

            if (self.active == 'dataLCShow' && self.showTableAnalyzeData == true) {
                var errordlcdataValue = document.getElementById("dlcRoot").value;
                if (!errordlcdataValue) {
                    $scope.errordlcdata = true;
                    return false;
                } else {
                    $scope.errordlcdata = false;
                }
                var errorCleandataValue = document.getElementById("cleanDataRoot").value;
                if (!errorCleandataValue) {
                    $scope.errorCleandata = true;
                    return false;
                } else {
                    $scope.errorCleandata = false;
                }
                getJobValidateFactory.getJobValidate({
                        jobName: self.jobName
                    }, {},
                    function(data) {
                        var filePath = common.getJobJarFile();
                        var AnalyzeDataJsonValidation = {
                            hdfsInputPath: self.hdfsInputPath,
                            jumbuneJobName: self.jobName,
                            operatingUser: self.jobSubUser,
                            operatingCluster: self.selectedCluster,
                            parameters: self.parameters,
                            dlcRootLocation: self.dlcRoot,
                            cleanDataRootLocation: self.cleanDataRoot
                        };
                        var mergedObject = angular.extend({}, AnalyzeDataJsonValidation);
                        var jsonDataIs = JSON.stringify(mergedObject);
                        $scope.content = new FormData();
                        $scope.content.append("jsonData", jsonDataIs);
                        var req = {
                            method: 'POST',
                            url: '/apis/validateservice/validatejobinput',
                            headers: { 'Content-Type': undefined },
                            transformRequest: angular.identity,
                            data: $scope.content,
                        };

                        $http(req).then(function(data1) {

                            if (data.STATUS == "ERROR") {
                                $scope.errorMessage = data.jobName;
                                $scope.errorMessageShow = true;
                            } else {
                                $scope.errorMessageShow = false;
                            }

                            if (data1.data.STATUS == "ERROR") {
                                $scope.errorMessageHdfs = data1.data.hdfsInputPath;
                                $scope.errorMessageShowHdfs = true;
                            } else {
                                $scope.errorMessageShowHdfs = false;
                            }

                            if (data.STATUS == "SUCCESS" && data1.data.STATUS == "SUCCESS") {
                                var AnalyzeData = {
                                    jumbuneJobName: self.jobName,
                                    operatingUser: self.jobSubUser,
                                    operatingCluster: self.selectedCluster,
                                    hdfsInputPath: self.hdfsInputPath,
                                    parameters: self.parameters,
                                    dlcRootLocation: self.dlcRoot,
                                    cleanDataRootLocation: self.cleanDataRoot
                                };
                                common.setAnalyzeDataDetail(AnalyzeData);
                                common.setOptimizeJobName(self.jobName);
                                //$location.path('/define-analyzeData-cleansing')
                                $location.path('/data-cleansing')
                            }
                        }, function(error) {
                            console.log("error", error)
                        });

                    },
                    function(e) {
                        console.log(e);
                    });

            }


        };
        self.backAnalyzeData = function() {
            self.showTableAnalyzeData = false;
            //$location.path('/');
            //$location.path('/add-analyze-data-configuration');
        };
        self.backAnalyzeDataModule = function() {
			$location.path('/');
		};
        this.isFormInvalid = function() {};

        this.setDataAnalysis = function(enableFieldLabel, name, enableField, isCriteriaBased) {
            dataAnalysisObj = {};
            dataAnalysisObj.enableFieldLabel = enableFieldLabel;
            dataAnalysisObj.name = name;
            dataAnalysisObj.enableField = enableField;
            dataAnalysisObj.isCriteriaBased = isCriteriaBased;
            dataAnalysisArr.push(dataAnalysisObj);
            common.dataAnalysisDetails = dataAnalysisArr;
        };

        self.disableNextButton = function() {
            //return !(!self.disableCheckbox() && (self.dataAnalysis || self.jobAnalysis || self.monitoring));
            return !(!self.disableCheckbox());
        };
        self.disableNextAnalyzeData = function() {
            //return !(!self.disableCheckbox() && (self.dataAnalysis || self.jobAnalysis || self.monitoring));
            return !(!self.disableCheckboxAnalyzeData());
        };

        self.setJobConfigMethod = function(index, checked) {
            common.setJobConfigMethod(index, checked);
        };

        self.submit = function() {
            var selectClus = self.selectedCluster;
            /*analyzeClusterFactory.submitAnalyzeCluster({
                },selectClus,
                function(data){
                    console.log("submitAnalyzeCluster",data)
                },
                function(e){
                    console.log(e);
                });*/
            var selectClus = self.selectedCluster;
            common.setSelectedClusterNameForRun(selectClus);
            validateLicenseFactory.validateLicense(
                //{requestType : 'DELETE'clusterName : 'key'},
                { clusterName: selectClus }, {},
                function(data) {
                    //if(data == true) {
                    //var test = JSON.parse(data)
                    //$location.path('/analyze-cluster');
                    // }


                },
                function(e) {
                    console.log("error")
                });
            $location.path('/analyze-cluster');


        };
        self.cancelAnalyzeClus = function() {
            $location.path('/index');

        };
        self.cancelRecurring = function() {
            //$location.path('/add-analyze-data-configuration');
            $location.path('/');

        };

        self.isObjEmpty = function(map) {
            for (var key in map) {
                if (map.hasOwnProperty(key)) {
                    return false;
                }
            }
            return true;
        }
    }
]);
