    'use strict';

    var serviceModule = angular.module('commmonService', []);

    serviceModule.service('common', ['$window', '$location', '$timeout', '$compile', '$http',
        function($window, $location,  $timeout, $compile,$http) {

        var self = this;
            this.jobs = new Map();
            this.clusterNameArr = [];
            this.clusterNameObj = {};
            this.selectedClusterForRun = null;
            var sharedData = [];
            var sharedDataDQ = [];
            var sharedDataDP = [];
            var clusterDetail = {
                clusterMode : "",
                currCluster : ""
            };
            var dashboardMode = "";
            var dataProfilingGroup = [];
            var currTab = '';
            this.dataAnalysis = false;
            this.jobAnalysis = false;
            this.dataQualityNameArr = [];
            this.dataProfilingNameArr = [];
            this.dataValidation = false;
            var savedDataQualityObject = {};
            var savedDataProfilingObject = {};
        this.saveEnabledFields = {};

            var savedJobs = [];

            var localDP = false;
            var localDQ = false;

        var isDuplicateNameDQ = false;
            var isDuplicateNameDP = false;
            this.newJobConfig = null;
            this.clusterMonitoringJsonData = '';
            this.dataProfilerWithCriteriaJsonData = '';
            this.dataProfilerNoCriteriaJsonData = '';
            this.dataQualityTimelineJsonData = '';
            this.dataValidationJsonData = '';
            this.debuggerJsonData = '';
            this.jobProfilingJsonData = '';
            this.selectedJobName = '';
            this.savedJobJson = '';
            this.enableDataQualityTimeline = false;
            this.clusterConfigDtl = {};

            this.jobMode = 'add';
            this.widgetData = angular.copy({});
            this.jobDetails = {};
            this.defineJobInfoObj = {};
            this.jobConfigMethods = [
                                        {"name":"Debugger", "visible": false},
                                        {"name":"Job Profiling", "visible": false}
                                    ];

            this.jobPreviewObj = {};
            this.regexJson = [];
            this.regexUdvJson = [];
            this.defaultJobConfigMethods = [{"name":"Debugger", "visible": false},{"name":"Job Profiling", "visible": false}];

            //reset job info data while creating new job
            this.setJobMode = function(mode) {
                self.jobMode = mode;
                if(mode === 'add') {
                    self.widgetData = angular.copy({});
                    self.jobDetails = angular.copy({});
                    self.defineJobInfoObj = angular.copy({});
                    this.jobConfigMethods = angular.copy(self.defaultJobConfigMethods);
                }
            };

            this.setJobPreviewJson = function(obj) {
                this.jobPreviewObj = angular.copy(obj);
            };

            this.getJobPreviewJson = function() {
                return this.jobPreviewObj;
            };

            this.setRegexJson = function(obj) {
                this.regexJson = angular.copy(obj);
            };

            this.getRegexJson = function() {
                return this.regexJson;
            };

            this.setUdvRegexJson = function(obj) {
                this.regexUdvJson = angular.copy(obj);
            };

            this.getUdvRegexJson = function() {
                return this.regexUdvJson;
            };




            this.setSelectedClusterNameForRun = function(name){
                  this.selectedClusterForRun = name;
            }
            this.getSelectedClusterNameForRun = function(){
                  return  this.selectedClusterForRun;
            };

            this.setSelectedJobName = function(name){
                  this.selectedJobName = name;
            }
            this.getSelectedJobName= function(){
                  return self.selectedJobName;
            };

            this.setClusterName = function(name) {

                if((this.clusterNameArr.indexOf(name) > -1) && (clusterDetail.clusterMode !== 'edit')) {
                    return false;
                 } else if( (name !== clusterDetail.currCluster) && (typeof clusterDetail.currCluster != 'undefined') && (this.clusterNameArr.indexOf(name) > -1) ) {
                     return false;
                }
                var oldClusterIndex = this.clusterNameArr.indexOf(clusterDetail.currCluster);

                if((oldClusterIndex > -1) && (clusterDetail.clusterMode === 'edit')) {
                    this.clusterNameArr.splice(oldClusterIndex,1,name)
                } else {
                    this.clusterNameArr.push(name);
                }
                return true;

            };

        this.setValidationOnNameinDQ = function(name) {
                for(var i=0; i<this.dataQualityNameArr.length; i++){
                    if(this.dataQualityNameArr[i].name === name){
                        isDuplicateNameDQ = true;
                        break;
                    }else{
                        isDuplicateNameDQ = false;
                    }
                }
                if(isDuplicateNameDQ) {
                    return false;
                }
                return true;
            };

            this.setValidationOnNameinDP = function(name) {
                for(var i=0; i<this.dataProfilingNameArr.length; i++){
                    if(this.dataProfilingNameArr[i].name === name){
                        isDuplicateNameDP = true;
                        break;
                    }else{
                        isDuplicateNameDP = false;
                    }
                }
                if(isDuplicateNameDP) {
                    return false;
                }
                return true;
            };

            this.updateObject = function(name,obj,suitName) {
                if(this.editingName == name)
                {
                    if(suitName == 'Profiling'){
                        for(var i=0; i<this.dataProfilingNameArr.length; i++){
                            if(this.dataProfilingNameArr[i].name === name){
                                this.dataProfilingNameArr[i].HDFSInputPath= obj.HDFSInputPath,
                                this.dataProfilingNameArr[i].comparisonVal= obj.comparisonVal,
                                this.dataProfilingNameArr[i].isCriteriaBased= obj.isCriteriaBased,
                                this.dataProfilingNameArr[i].isDataProfiling= obj.isDataProfiling,
                                this.dataProfilingNameArr[i].isEnableRow = obj.isEnableRow,
                                this.dataProfilingNameArr[i].isNoCriteria= obj.isNoCriteria,
                                this.dataProfilingNameArr[i].noOfFields= obj.noOfFields,
                                this.dataProfilingNameArr[i].nullCheck= obj.nullCheck,
                                this.dataProfilingNameArr[i].tupleFS= obj.tupleFS,
                                this.dataProfilingNameArr[i].tupleRS= obj.tupleRS
                            }
                        }
                    }
                    else if(suitName == 'Quality'){
                        for(var i=0; i<this.dataQualityNameArr.length; i++){
                            if(this.dataQualityNameArr[i].name === name){
                                this.dataQualityNameArr[i].isEnableQT = obj.isEnableQT,
                                this.dataQualityNameArr[i].name = this.editingName,
                                this.dataQualityNameArr[i].scheduleJob = obj.timeType,
                                this.dataQualityNameArr[i].time = obj.time,
                                this.dataQualityNameArr[i].repeatInstance = obj.repeatInstance,
                                this.dataQualityNameArr[i].repeatTime = obj.repeatTime,
                                this.dataQualityNameArr[i].cronExpression = obj.cronExpression,
                                this.dataQualityNameArr[i].HDFSInputPath = obj.HDFSInputPath,
                                this.dataQualityNameArr[i].tupleRS = obj.tupleRS,
                                this.dataQualityNameArr[i].tupleFS = obj.tupleFS,
                                this.dataQualityNameArr[i].noOfFields = obj.noOfFields
                                for(var j=0; j < obj.noOfFields;j++){
                                    this.dataQualityNameArr[i].regEx[j] = obj.regEx[j];
                                    this.dataQualityNameArr[i].nullCheck[j] = obj.nullCheck[j];
                                    this.dataQualityNameArr[i].fieldType[j] = obj.fieldType[j];
                                    this.dataQualityNameArr[i].isEnableRow[j] = obj.isEnableRow[j];
                                }
                            }
                        }
                    }
                }else{
                }
                return true;
            };
            this.editing = false;
            this.setEditing = function(flagVal){
                this.editing = flagVal;
            };

            this.getEditing = function(){
                return this.editing;
            };

            this.editingName = '';
            this.setEditingName = function(dataName){
                this.editingName = dataName;
            };

            this.getEditingName = function(){
                return this.editingName;
            };

            this.getClusterName = function() {
                return this.clusterNameArr;
            };

            this.getFieldCaption = function(key){
                return sharedData[key];
            };

            this.setFieldCaption = function(key,value){
                sharedData[key] = value;
            };
        this.saveScheduleData = {};

        this.setScheduleJobData = function(scheduleData){
        self.saveScheduleData = scheduleData;
        };
        this.getScheduleJobData = function(){
            return self.saveScheduleData;
        };

        this.setClusterMode = function(mode,name) {
            clusterDetail.clusterMode = mode;
            clusterDetail.currCluster = name;
        };

        this.getClusterMode = function() {
            return clusterDetail;
        };
            this.setJobConfigMethod = function(index, checked) {
                var jobLen = this.jobConfigMethods.length;
                for (var i=0; i<jobLen; i++) {
                    if(i === index) {
                        this.jobConfigMethods[i].visible = checked;
                    }
                }
            };

            this.getJobConfigMethod = function() {
                return angular.copy(this.jobConfigMethods);
            };

            this.backPath = '';
            this.setBackVal = function (val) {
                this.backPath = val;
            };
            this.getBackVal = function () {
                return this.backPath;
            };



            this.dataQualities = [{"name": "Data quality-1"}, {"name": "Data quality-2"}, {"name": "Data quality-3"}, {"name": "Data quality-4"}, {"name": "Data quality-5"}];
            this.dataProfilers = [{"name": "Data profiler-1"}, {"name": "Data profiler-2"}, {"name": "Data profiler-3"}, {"name": "Data profiler-4"}, {"name": "Data profiler-5"}];

            this.setDataQuality  = function (val) {
                self.dataQualities = val;
            };
            this.getDataQuality = function () {
                return self.dataQualities;
            };

            this.setDataProfiler = function (val) {
                self.dataProfilers = val;
            };
            this.getDataProfiler = function () {
                return self.dataProfilers;
            };


            this.setJobAnalysis = function (val) {
                this.jobAnalysis = val;
            };
            this.getJobAnalysis = function () {
                return self.jobDetails.job;
            };
            this.setDataAnalysis = function (val) {
                this.dataAnalysis = val;
            };
            this.getDataAnalysis = function () {
                return self.jobDetails.data;
            };

            this.setDataConfigDtl = function(selectedDataFields){
                self.saveEnabledFields = selectedDataFields;
            }
            this.getDataConfigDtl = function(){
                return self.saveEnabledFields;
            }

            this.setDDV = function (val) {
                this.dataValidation = val;
            };
            this.getDDV = function () {
                return this.dataValidation;
            };
            this.getDQFieldCaption = function(key){
                return sharedDataDQ[key];
            };

            this.setDQFieldCaption = function(key,value){
                sharedDataDQ[key] = value;
            };

            this.getDPFieldCaption = function(key){
                return sharedDataDP[key];
            };

            this.setDPFieldCaption = function(key,value){
                sharedDataDP[key] = value;
            };

            this.setCurrentTab = function(tab){
                $window.sessionStorage.setItem('currentTab', tab);
                if(!tab) {
                    $window.sessionStorage.setItem('currentTab', 'DataQuality');
                }
            };

            this.getCurrentTab = function(){
                return $window.sessionStorage.getItem('currentTab');
            };

            this.setSavedDQ = function(obj){
                savedDataQualityObject = obj;
            };

            this.getSavedDQ = function(){
                return savedDataQualityObject;
            };

            this.setSavedDP = function(obj){
                savedDataProfilingObject = obj;
            };

            this.getSavedDP = function(){
                return savedDataProfilingObject;
            };

            this.setLocalDP  = function(param){
                localDP = param;
            };

            this.getLocalDP = function(){
                return localDP;
            };

            this.setLocalDQ  = function(param){
                localDQ = param;
            };

            this.getLocalDQ = function(){
                return localDQ;
            };

        this.chkPrevFormIsValid = function(prevForm, $scope){
                switch(prevForm) {
                    case 'Debugger':{
                        if($scope.debuggerTab.debuggerForm.$valid){
                            this.prevSuccessDiv('method-debugger');
                            this.debuggerCompleted = true;
                        }else{
                            this.prevDiv('method-debugger');
                            this.debuggerCompleted = false;
                        }
                        break;
                    }
                    case 'WhatIf':{
                        if($scope.whatIfTab.whatIfForm.$valid){
                            this.prevSuccessDiv('method-WhatIf');
                            this.whatIfCompleted = true;
                        }else{
                            this.prevDiv('method-WhatIf');
                            this.whatIfCompleted = false;
                        }
                        $scope.whatIfTab.handleErrorMessages();
                        break;
                    }
                    case 'JobProfiling':{
                        if($scope.jobProfilingTab.jobProfilingForm.$valid){
                            this.prevSuccessDiv('method-job-profiling');
                            this.jobProfilingCompleted = true;
                        }else{
                            this.prevDiv('method-job-profiling');
                            this.jobProfilingCompleted = false;
                        }
                        $scope.jobProfilingTab.handleErrorMessages();
                        break;
                    }
                    case 'DataValidation':{
                        $scope.dataValidationTab.handleErrorMessages();
                        break;
                    }
                }
            }

            this.activeForm = function(activeDivId){
                switch(activeDivId) {
                    case 'Debugger':{
                        this.activeDiv(activeDivId,'method-debugger');
                        break;
                    }
                    case 'WhatIf':{
                        this.activeDiv(activeDivId,'method-WhatIf');
                        break;
                    }
                    case 'JobProfiling':{
                        this.activeDiv(activeDivId,'method-job-profiling');
                        break;
                    }
                    case 'DataValidation':{
                        this.activeDiv(activeDivId,'method-data-validation');
                        break;
                    }
                }
            }

            this.activeDiv = function(id,className){
                angular.element("#"+id+"").addClass('active');
            }

            this.prevDiv = function(className){
                angular.element("."+className).addClass('incomplete');
            }

            this.prevSuccessDiv = function(className){
                if(angular.element("."+className).hasClass('incomplete')){
                    angular.element("."+className).removeClass('incomplete');
                }
                angular.element("."+className).addClass('complete');
            }
            this.isFormsValid = false;
            this.isCurrentFormValid = false;
            this.findSelectedTabs = function(){
                var selectedTabs = self.getJobConfigMethod();
                angular.forEach(selectedTabs, function(element){
                    if(element.visible){
                        if(element.name==='Debugger'){
                            self.isCurrentFormValid = true;
                        } else if(element.name==='What If'){
                            self.isCurrentFormValid = true;
                        } else if(element.name==='Job Profiling'){
                            self.isCurrentFormValid = true;
                        }
                    }
                });
                return self.isCurrentFormValid
            };
            this.getAllFormValid = function(){
                var isCurrentFormValid = true;
                var selectedTabs = self.getJobConfigMethod();
                angular.forEach(selectedTabs, function(element){
                    if(element.visible){
                        if(element.name==='Debugger'){
                            isCurrentFormValid = isCurrentFormValid && self.debuggerCompleted;
                        } else if(element.name==='What If'){
                            isCurrentFormValid = isCurrentFormValid && self.whatIfCompleted;
                        } else if(element.name==='Job Profiling'){
                            isCurrentFormValid = isCurrentFormValid && self.jobProfilingCompleted;
                        }
                    }
                });
                if(isCurrentFormValid){
                    self.isFormsValid = true;
                }else{
                    self.isFormsValid = false;
                }
                return self.isFormsValid;
            };
            this.messageObj = {
                displayBlock: false,
                blockMessage: "",
                success: false
            };

            this.showMessage = function(type, messageString){
                self.messageObj.success = (type === 'success');
                self.messageObj.displayBlock = true;
                self.messageObj.blockMessage = messageString;
                $timeout(function(){
                    self.messageObj.displayBlock = false;
                    self.messageObj.blockMessage = "";
                },3000);
            };

            this.appendToElement = function(ele, html) {
                angular.element(ele).append(html);
            };


            this.saveDefineJobInfo = function(obj){
                if(obj){
                   self.defineJobInfoObj = angular.copy(obj);
                }
            };
            this.getDefineJobInfo = function(){
                return self.defineJobInfoObj;
            };

            self.setJobDetails = function(job) {
                self.jobDetails = angular.copy(job);
            };
            self.getJobDetails = function() {
                return self.jobDetails;
            };

            self.createJobJson = function(){
                var varJobDetails = self.getJobDetails();
                var vargetDefineJobInfo = self.getDefineJobInfo();
                var varJobWidget = self.getWidgetInfo();
                var varJobName = self.getSelectedJobName();
                var varJobConfigMethods = this.jobConfigMethods;
                this.savedJobJson = {
                        jobName : varJobName,
                        JobDetails:varJobDetails,
                        jobConfigMethods: varJobConfigMethods,
                        getDefineJobInfo : vargetDefineJobInfo,
                        JobWidget : varJobWidget
                };
                this.jobs.put(varJobName,this.savedJobJson);
            };

            self.openJobByJson = function (JsonObj) {
                var varjobName = JsonObj.JobName;
                var varJobDetails = JsonObj.JobDetails;
                var vargetDefineJobInfo = JsonObj.getDefineJobInfo;
                var varJobWidget = JsonObj.JobWidget;
                this.jobConfigMethods = JsonObj.jobConfigMethods;
                self.setSelectedJobName(varjobName);
                self.setJobDetails(varJobDetails);
                self.saveDefineJobInfo(vargetDefineJobInfo);
                self.widgetData=varJobWidget;
            };

            self.openJobByNameFromMap = function(jobName){
                if(this.jobs.size()){
                   var job = this.jobs.get(jobName);
                   self.openJobByJson(job);
                }
            };

            self.getJobs = function(){
                return this.jobs;
            };

            self.getJobNames = function(){
                return this.jobs.getKeys();
            };

            self.clearJobDetails = function() {
                var defaultObj = {
                    data: false,
                    job: false,
                    monitor: false,
                    jobName: "",
                    clusterName: ""
                };
                self.setJobDetails(defaultObj);
            };

            self.getAddJarVal = function(){
                if(angular.element('#addjarText').isDefined || angular.element('#addjarText').val() !== ''){
                    return angular.element('#addjarText').val();
                }
            };

            this.setWidgetInfo = function(tabName, obj){
                self.widgetData[tabName] = angular.copy(obj);
            };

            this.getWidgetInfo = function() {
                return self.widgetData;
            };
            self.widgetInfoArr = [];
            this.savedScheduleJobJson = {};
            self.setWidgetObject = function(){
                        var varJobWidget = self.getWidgetInfo();
                        var varJobName = self.getSelectedJobName();
                if(varJobWidget.tuningDtl != undefined) {
                if(varJobWidget.tuningDtl.schedule){
                    self.savedScheduleJobJson = {
                                    jobName : varJobName,
                                    JobWidget : varJobWidget
                    };
                }
                }
                self.widgetInfoArr.push(self.savedScheduleJobJson);
            }

            self.getWidgetArray = function(){
                return self.widgetInfoArr;
            }


            //setter-getter for reports

            this.setClusterMonitoringJsonData = function(data){
                this.clusterMonitoringJsonData = data;
            };

            this.getClusterMonitoringJsonData = function(){
                return this.clusterMonitoringJsonData;
            };

            this.setDataProfilerWithCriteriaJsonData = function(data){
                this.dataProfilerWithCriteriaJsonData = data;
            };
            this.getDataProfilerWithCriteriaJsonData = function(){
                return this.dataProfilerWithCriteriaJsonData;
            };

            this.setDataProfilerNoCriteriaJsonData = function(data){
                this.dataProfilerNoCriteriaJsonData = data;
            };
            this.getDataProfilerNoCriteriaJsonData = function(){
                return this.dataProfilerNoCriteriaJsonData;
            };

            this.setDataQualityTimelineJsonData = function(data){
                this.dataQualityTimelineJsonData = data;
            };
            this.getDataQualityTimelineJsonData = function(){
                return this.dataQualityTimelineJsonData;
            };

            this.setDataValidationJsonData = function(data){
                this.dataValidationJsonData = data;
            };
            this.getDataValidationJsonData = function(){
                return this.dataValidationJsonData;
            };

            this.setDebuggerJsonData = function(data){
                this.debuggerJsonData = data;
            };
            this.getDebuggerJsonData = function(){
                return this.debuggerJsonData;
            };

            this.setJobProfilingJsonData = function(data){
                this.jobProfilingJsonData = data;
            };
            this.getJobProfilingJsonData = function(){
                return this.jobProfilingJsonData;
            };

        this.setEnableDataQualityTimeline = function(dataQualityTimeline){
        self.enableDataQualityTimeline = dataQualityTimeline;
        };
        this.getEnableDataQualityTimeline = function(){
            return self.enableDataQualityTimeline;
        };

        this.setClusterConfig = function(clusterObj){
            self.clusterConfigDtl = clusterObj;
        };
        this.getClusterConfig = function(){
            return self.clusterConfigDtl;
        };

        this.serverJSON_2_uiJSON = function(json) {
            var finalConvertedObj = {};
            finalConvertedObj.jobName = json.jumbuneJobName;
            finalConvertedObj.JobDetails = {
                "data": false,
                "job": false,
                "monitor": false,
                "jobName": json.jumbuneJobName,
                "clusterName": "Dummy Cluster1"
            };

            finalConvertedObj.jobConfigMethods = [
                {
                    "name": "Debugger",
                    "visible": false
                },
                {
                    "name": "Job Profiling",
                    "visible": false
                }

            ];

            var debuggerObj = {}, regexValidations=[], userValidations=[];
            var tempDebugOb = {};
            if(typeof json.debuggerConf.logLevel != undefined){
                debuggerObj.useRegex = [];
                debuggerObj.udv = [];
                finalConvertedObj.JobDetails.job = true;
                finalConvertedObj.jobConfigMethods[0].visible = true;
                debuggerObj.useRegexChk = json.debuggerConf.logLevel.instrumentRegex==='TRUE'?true:false;
                debuggerObj.usrDefChk = json.debuggerConf.logLevel.instrumentUserDefValidate==='TRUE'?true:false;
                debuggerObj.enableLoggingOfUnmatched = json.logKeyValues==='TRUE'?true:false;

                regexValidations = json.regexValidations;
                if(regexValidations){
                    for(var i=0; i<regexValidations.length; i++) {
                        tempDebugOb = {};
                        tempDebugOb.classname = regexValidations[i].classname;
                        tempDebugOb.key = regexValidations[i].key;
                        tempDebugOb.value = regexValidations[i].value;

                        debuggerObj.useRegex.push(tempDebugOb);
                    }
                }


                userValidations = json.userValidations;
                if(userValidations) {
                    for(var i=0; i<userValidations.length; i++) {
                        tempDebugOb = {};
                        tempDebugOb.classname = userValidations[i].classname;
                        tempDebugOb.key = userValidations[i].key;
                        tempDebugOb.value = userValidations[i].value;
                        debuggerObj.udv.push(tempDebugOb);
                    }
                }


            }
            var jobProfilingObj = {};
            jobProfilingObj.enableprofilingCheck = json.enableJobProfiling;
            if(json.hadoopJobProfile === 'TRUE'){
                finalConvertedObj.JobDetails.job = true;
                finalConvertedObj.jobConfigMethods[1].visible = true;
                jobProfilingObj.runFromJumbune = json.enableStaticJobProfiling==='TRUE'?true:false;
            }

            var dataValidationObj = {};
            dataValidationObj.enableDataValidation = json.enableDataValidation;
            if(json.enableDataValidation){
                this.dataValidation = true;
                finalConvertedObj.JobDetails.data = true;
                dataValidationObj.hdfsInputPath = json.hdfsInputPath;
                dataValidationObj.tupleRecordSeparator = json.dataValidation.recordSeparator;
                dataValidationObj.tupleFieldSeparator = json.dataValidation.fieldSeparator;
                dataValidationObj.fieldCount = json.numOfFields;
                dataValidationObj.enableRowData = [];
                if(json.dataValidation.numOfFields>0) {
                    var tempObj = {};
                    for(var i=0; i<json.dataValidation.fieldValidationList.length;i++){
                        tempObj = angular.copy({});
                        tempObj.enable = json.dataValidation.fieldValidationList[i].enable;
                        tempObj.fieldNumber = json.dataValidation.fieldValidationList[i].fieldNumber;
                        tempObj.nullCheck = json.dataValidation.fieldValidationList[i].nullCheck;
                        tempObj.fieldType = json.dataValidation.fieldValidationList[i].dataType;
                        tempObj.regEx = json.dataValidation.fieldValidationList[i].regex;
                        dataValidationObj.enableRowData.push(tempObj);
                    }
                }
            }

            var jobsObj = json.jobs;
            var defineJobObj = {};
            defineJobObj = {};
            defineJobObj.fieldArray = [];
            defineJobObj.allJobInfo = [];
            defineJobObj.noOfJobs = jobsObj.length;

            if(json.isLocalSystemJar === 'TRUE') {
                defineJobObj.systemType = "local";
                defineJobObj.filePathLocal = json.inputFile;
            } else {
                defineJobObj.systemType = "server";
                defineJobObj.filePathServer = json.inputFile;
            }

            if(jobsObj.length) {
                for(var i=0; i<jobsObj.length; i++) {
                    tempObj = {};
                    tempObj.jobName = jobsObj[i].name;
                    tempObj.jobParameter = jobsObj[i].parameters;
                    if(!defineJobObj.includeClassJar !== 'TRUE'){
                        tempObj.jobClassName = jobsObj[i].classname;
                    }
                    defineJobObj.fieldArray.push(i);
                    defineJobObj.allJobInfo.push(tempObj);
                }
            }

            if(json.includeClassJar==='TRUE') {
                defineJobObj.jarManifest = true;
            }

            finalConvertedObj.JobWidget = {
                "debuggerDtl" : debuggerObj,
                "whatIfDtl" : whatIfObj,
                "jobProfilingDtl" : jobProfilingObj,
                "dataValidation" : dataValidationObj
            };

            finalConvertedObj.getDefineJobInfo = defineJobObj;
            return finalConvertedObj;

        };
        this.dataAnalysisChart = '';


        this.dataSuiteDetails = '';
        this.dataAnalysisDetails = '';
        self.jobJarFile = {};
        self.setJobJarFile = function(file, form) {
            self.jobJarFile.file = file;
            self.jobJarFile.formid = form;
        };
        self.getJobJarFile = function() {
            return self.jobJarFile.file;
        };
        this.analyzeData = {};
        self.setAnalyzeDataDetail = function(obj) {
            this.analyzeData = angular.copy(obj)
        }
        self.getAnalyzeDataDetail = function() {
            return this.analyzeData;
        }

        this.commonJobName = '';
        self.setJobName = function(name) {
            this.commonJobName = name;
        }
        self.getJobName = function() {
            return this.commonJobName;
        }
        this.fieldNumberData = {};
        self.setfieldNumberDetail = function(obj) {
            this.fieldNumberData = angular.copy(obj)
        }
        self.getfieldNumberDetail = function() {
            return this.fieldNumberData;
        }
        this.dQTFlag = '';
        self.setDQTFlag = function(obj) {
            this.dQTFlag = angular.copy(obj)
        }
        self.getDQTFlag = function() {
            return this.dQTFlag;
        }
        this.dQTRecFlag = '';
        self.setDQTRecFlag = function(obj) {
            this.dQTRecFlag = angular.copy(obj)
        }
        self.getDQTRecFlag = function() {
            return this.dQTRecFlag;
        }
        this.profilingFlag = '';
        self.setProfilingFlag = function(obj) {
            this.profilingFlag = angular.copy(obj)
        }
        self.getProfilingFlag = function() {
            return this.profilingFlag;
        }
        this.dataValidationFlag = '';
        self.setDataValFlag = function(obj) {
            this.dataValidationFlag = angular.copy(obj)
        }
        self.getDataValFlag = function() {
            return this.dataValidationFlag;
        }
        this.jsonDataValidationFlag = '';
        self.setJsonDataValFlag = function(obj) {
            this.jsonDataValidationFlag = angular.copy(obj)
        }
        self.getJsonDataValFlag = function() {
            return this.jsonDataValidationFlag;
        }
        this.tunningFlag = '';
        self.setTunningFlag = function(obj) {
            this.tunningFlag = angular.copy(obj)
        }
        self.getTunningFlag = function() {
            return this.tunningFlag;
        }
        this.numFieldFlag = '';
        self.setNumField = function(obj) {
            this.numFieldFlag = angular.copy(obj)
        }
        self.getNumField = function() {
            return this.numFieldFlag;
        }
        this.recurringData = {};
        self.setDataRecurring = function(obj) {
            this.recurringData = angular.copy(obj)
        }
        self.getDataRecurring = function() {
            return this.recurringData;
        }
         this.hdfsInputPathData = {};
        self.setHdfsInputPath = function(obj) {
            this.hdfsInputPathData = angular.copy(obj)
        }
        self.getHdfsInputPath = function() {
            return this.hdfsInputPathData;
        }
        this.recnetResponseData = {};
        self.setResonseData = function(obj) {
            this.recnetResponseData = angular.copy(obj)
        }
        self.getResonseData = function() {
            return this.recnetResponseData;
        }
        this.setClusterNme = {};
        self.setClustrName = function(obj) {
             this.setClusterNme   = angular.copy(obj)
        }
        self.getClustrName = function() {
            return this.setClusterNme;
        }
        this.setaddThrdDta = {};
        self.setaddThrdData = function(obj) {
             this.setaddThrdDta   = angular.copy(obj)
        }
        self.getaddThrdData = function() {
            return this.setaddThrdDta;
        }
        this.countFlag = false;
        self.setCountFlag = function(flag) {
            this.countFlag = angular.copy(flag)
        }
        self.getCountFlag = function() {
            return this.countFlag;
        }
        this.countVar = 0;
        self.setCountVar = function(obj) {
            this.countVar = angular.copy(obj)
        }
        self.getCountVar = function() {
            return this.countVar;
        }
        this.setJSONData = {}
        self.setdataValidationJSONData = function(obj){
            this.setJSONData = angular.copy(obj)
        }
        self.getdataValidationJSONData = function() {
            return this.setJSONData;
        }
        this.setJSONDataJSON = {}
        self.setAnalyzeDataDetailJSON = function(obj){
            this.setJSONDataJSON = angular.copy(obj)
        }
        self.getAnalyzeDataDetailJSON = function() {
            return this.setJSONDataJSON;
        }
          this.setXMLData = {}
        self.setdataValidationXMLData = function(obj){
            this.setXMLData = angular.copy(obj)
        }
        self.getdataValidationXMLData = function() {
            return this.setXMLData;
        }
        this.setXMLAnalyzeData = {}
        self.setAnalyzeDataDetailXML = function(obj){
            this.setXMLAnalyzeData = angular.copy(obj)
        }
        self.getAnalyzeDataDetailXML = function() {
            return this.setXMLAnalyzeData;
        }
        this.setXMLDataFlag = ''
        self.setXmlDataValFlag = function(obj){
            this.setXMLDataFlag = angular.copy(obj)
        }
        self.getXmlDataValFlag = function() {
            return this.setXMLDataFlag;
        }
        this.setconfData = {}
        self.setConfigurationData = function(obj){
            this.setconfData = angular.copy(obj)
        }
        self.getConfigurationData = function() {
            return this.setconfData;
        }
        this.setClusterSize = {}
        self.setNodeSize = function(obj){
            this.setClusterSize = angular.copy(obj)
        }
        self.getNodeSize = function() {
            return this.setClusterSize;
        }

		this.setIsMaprValue = null;
        self.setIsMapr = function(obj) {
            this.setIsMaprValue = angular.copy(obj);
        }
        self.getIsMapr = function() {
            return this.setIsMaprValue;
        }
        this.setActive = {}
        self.setActiveTab = function(obj){
            this.setActive = angular.copy(obj)
        }
        self.getActiveTab = function() {
            return this.setActive;
        }
        this.setJobDetailsFlag = false;
        self.setJobDetailsFlagRes = function(flag) {
            this.setJobDetailsFlag = angular.copy(flag)
        }
        self.getsJobDetailsFlagRes = function() {
            return this.setJobDetailsFlag;
        }
        this.setChargeBack = undefined;
        self.setChargeBackConf = function(obj){
            this.setChargeBack = angular.copy(obj)
        }
        self.getChargeBackConf = function() {
            return this.setChargeBack;
        }
        this.setAcTab = null;
        self.setAcTabDetail = function(obj) {
            this.setAcTab = angular.copy(obj);
        }
        self.getAcTabDetail = function() {
            return this.setAcTab;
        }
        this.setRealmdata = null;
        self.setRealmData = function(obj) {
            this.setRealmdata = angular.copy(obj);
        }
        self.getRealmData = function() {
            return this.setRealmdata;
        }
    }]);




