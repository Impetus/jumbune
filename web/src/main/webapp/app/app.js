	'use strict';

	angular.module('directives', []);
	angular.module('factories', []);
	angular.module('optimizeGraph.ctrl', []);

	var appModule = angular.module('jumbune', ['ngRoute', 'ngSanitize', 'ngResource', 'dashboard.ctrl', 'cluster.ctrl', 'profile.ctrl', 'addCluster.ctrl', 'quality.ctrl', 'commmonService', 'newjobconfig.ctrl', 'configuration.ctrl', 'jobconfig.ctrl', 'dataconfig.ctrl', 'jobwidget.ctrl', 'datajob.ctrl', 'defineJobInfo.ctrl', 'jobpreview.ctrl', 'dataValidate.ctrl','dataCleansing.ctrl', 'dataValidateJSON.ctrl', 'dataValidateXML.ctrl', 'dataValidateComp.ctrl', 'formButtonDir.ctrl', 'directives', 'factories', 'optimizeGraph.ctrl', 'bistel.chart', 'dpdqpreview.ctrl', 'dpdqchart.ctrl', 'dapreview.ctrl', 'dachart.ctrl', 'jobchart.ctrl', 'analyzeCluster.ctrl', 'analyzeData.ctrl', 'analyzeJob.ctrl', 'analyzeDataXml.ctrl', 'analyzeDataJson.ctrl','dataComparisonCtrl.ctrl','dataCleansingCtrl.ctrl', 'analyzeDataText.ctrl', 'analyzeDataProfiling.ctrl']);

	appModule.config(function($routeProvider) {

		$routeProvider.when('/index', {
			templateUrl: 'app/dashboard/dashboard.html',
			controller: 'DashboardController',
			controllerAs: 'DashboardController',
			title: 'dashboard'
		})
		.when('/cluster', {
			templateUrl: 'app/clusters-config/define-cluster-config.html',
			controller: 'ClusterController'
		})
		.when('/add-dp', {
			templateUrl: 'app/dataSuite-config/add-data-quality.html',
			controller: 'QualityController'
		})
		.when('/recurring-info', {
			templateUrl: 'app/analyzeData/wizard/recurring.html',
			controller: 'NewJobConfigController',
			controllerAs: "NewJobConfigController"
		})
		.when('/add-cluster', {
			templateUrl: 'app/cluster/basic-cluster.html',
			controller: 'AddClusterController'
		})
		.when('/manage-cluster', {
			templateUrl: 'app/cluster/configuration.html',
			controller: 'ConfigurationController',
			controllerAs: "ConfigurationController"
		})
		.when('/data-profiling', {
			templateUrl: 'app/analyzeData/wizard/profiling/data-profiling-dtl.html',
			controller: 'ProfileController'
		})
		.when('/add-data-quality', {
			templateUrl: 'app/analyzeData/wizard/quality/data-quality-dtl.html',
			controller: 'QualityController'
		})
		.when('/add-new-job-configuration', {
			templateUrl: 'app/analyzeJob/wizard/newjobconfiguration.html',
			controller: 'NewJobConfigController',
			controllerAs: "NewJobConfigController"
		})
		.when('/add-analyze-data-configuration', {
			templateUrl: 'app/analyzeData/wizard/newAnalyzeData.html',
			controller: 'NewJobConfigController',
			controllerAs: "NewJobConfigController"
		})
		.when('/add-new-cluster-configuration', {
			templateUrl: 'app/analyzeCluster/wizard/newClusterConfig.html',
			controller: 'NewJobConfigController',
			controllerAs: "NewJobConfigController"
		})
		.when('/add-optimized-job-configuration', {
			templateUrl: 'app/optimizeJob/wizard/newOptimizedJobConfig.html',
			controller: 'NewJobConfigController',
			controllerAs: "NewJobConfigController"
		})
		.when('/define-optimized-info', {
			templateUrl: 'app/optimizeJob/wizard/tuning-tab-form.html',
			controller: 'TuningController'
		})
		.when('/data-configuration', {
			templateUrl: 'app/newjobconfig/dataconfiguration.html',
			controller: 'DataConfigController',
			controllerAs: 'DataConfigController'
		})
		.when('/define-analyzeData-info', {
			templateUrl: 'app/analyzeData/wizard/profiling/data-profiling-dtl.html',
			controller: 'ProfileController'
		})
		.when('/define-analyzeData-cleansing', {
			templateUrl: 'app/analyzeData/result/dataCleansing/dataCleansing.html',
			controller: 'AnalyzeDataCleansing'
		})
		.when('/job-configuration', {
			templateUrl: 'app/newjobconfig/jobconfiguration.html',
			controller: 'JobConfigController'
		})
		.when('/job-widget', {
			templateUrl: 'app/analyzeJob/wizard/jobwidget.html'
		})
		.when('/data-validation', {
			templateUrl: 'app/analyzeData/wizard/dataValidation/dataValidation.html'
		})
		.when('/data-cleansing', {
			templateUrl: 'app/analyzeData/wizard/dataCleansing/dataCleansingPage.html',
			controller: 'dataCleansingController'
		})
		.when('/data-validation-json', {
			templateUrl: 'app/analyzeData/wizard/dataValidationJSON/dataValidationJSON.html',
			controller: 'dataValidationControllerJSON'
		})
		.when('/data-validation-xml', {
			templateUrl: 'app/analyzeData/wizard/dataValidationXML/dataValidationXML.html',
			controller: 'dataValidationControllerXML'
		})
		.when('/data-validation-comp', {
			templateUrl: 'app/analyzeData/wizard/dataComparison/dataValidationComp.html',
			controller: 'dataValidationControllerComp'
		})
		.when('/data-job-configuration', {
			templateUrl: 'app/newjobconfig/datajob.html',
			controller: 'DataJobController'
		})
		.when('/define-job-info', {
			templateUrl: 'app/analyzeJob/wizard/definejobinfo.html',
			controller: 'DefineJobInfoController',
			controllerAs: 'DefineJobInfoController'
		})
		.when('/job-preview', {
			templateUrl: 'app/newjobconfig/jobPreview.html',
			controller: 'JobPreviewController',
			controllerAs: 'JobPreviewController'
		})
		.when('/dpdq-preview', {
			templateUrl: 'app/clusters-config/dpdqPreview.html',
			controller: 'DPDQPreviewController',
			controllerAs: 'DPDQPreviewController'
		})
		.when('/dpdq-chart', {
			templateUrl: 'app/clusters-config/dpdqChart.html',
			controller: 'DPDQChartController',
			controllerAs: 'DPDQChartController'
		})
		.when('/data-analysis-preview', {
			templateUrl: 'app/newjobconfig/dataanalysispreview.html',
			controller: 'DataAnalysisPreviewController',
			controllerAs: 'DataAnalysisPreviewController'
		})
		.when('/data-analysis-chart', {
			templateUrl: 'app/newjobconfig/daChart.html',
			controller: 'DAChartController',
			controllerAs: 'DAChartController'
		})
		.when('/job-chart', {
			templateUrl: 'app/newjobconfig/jobChart.html',
			controller: 'JobChartController',
			controllerAs: 'JobChartController'
		})
		.when('/analyze-cluster', {
			templateUrl: 'app/analyzeCluster/result/analyzeCluster.html',
			controller: 'AnalyzeCluster'
		})
		.when('/analyze-data', {
			templateUrl: 'app/analyzeData/result/dataValidation/dataValidationGraph.html',
			controller: 'AnalyzeDataText'
		})
		.when('/analyze-data-json', {
			templateUrl: 'app/analyzeData/result/dataValidationJSON/dataValidationGraphJSON.html',
			controller: 'AnalyzeDataJson'
		})
		.when('/analyze-data-comp', {
			templateUrl: 'app/analyzeData/result/dataComparison/dataComparisonGraph.html',
			controller: 'AnalyzeDataComp'
		})
		.when('/analyze-data-hdfs', {
			templateUrl: 'app/analyzeData/wizard/dataValidationXML/dataValidationXmlHdfs.html'
		})
		.when('/analyze-data-xml', {
			templateUrl: 'app/analyzeData/result/dataValidationXML/dataValidationGraphXML.html',
			controller: 'AnalyzeDataXml'
		})
		.when('/analyze-data-quality', {
			templateUrl: 'app/analyzeData/result/quality/analyzeData.html',
			controller: 'AnalyzeData',
			controllerAs: 'AnalyzeData'
		})
		.when('/analyze-data-profiling', {
			templateUrl: 'app/analyzeData/result/profiling/dataProfilingGraph.html',
			controller: 'AnalyzeDataProfiling'
		})
		.when('/analyze-job', {
			templateUrl: 'app/analyzeJob/result/analyzeJob.html',
			controller: 'AnalyzeJob'
		})
		.when('/optimize-graph', {
			templateUrl: 'app/optimizeJob/result/optimizegraph.html',
			controller: 'optimizeGraphController'
		})
		.otherwise({
			redirectTo: '/index'
		});
	})
	.run(['$rootScope', 'common', function($rootScope, common) {
		$rootScope.root = {};
		$rootScope.root.basePath = 'data/';
		$rootScope.root.failMessageGeneric = "The information entered is either invalid or incomplete. Please correct to proceed";
		$rootScope.root.common = common;
	}]);

