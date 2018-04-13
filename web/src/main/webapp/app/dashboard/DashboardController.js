/* Dashboard controller */
'use strict';
angular.module('dashboard.ctrl', ["ngAnimate"])
	.controller('DashboardController', ['$scope', '$http', '$rootScope', 'common', '$location', 'editClusterFactory', 'getManageClusterFactory', 'deleteClusterFactory', 'getRecentJobFactory', 'getJobDataFactory', 'getExampleDataFactory', 'getExampleListFactory', 'ClusterResultFactoryNew', 'deleteJobFactory', 'GetScheduledDQTJobsListFactory', 'getSupportedFeaturesFactory', 'getIsMaprDistributionFactory', 'getJenkinsBuildNumberFactory',

		function($scope, $http, $rootScope, common, $location, editClusterFactory, getManageClusterFactory, deleteClusterFactory, getRecentJobFactory, getJobDataFactory, getExampleDataFactory, getExampleListFactory, ClusterResultFactoryNew, deleteJobFactory, GetScheduledDQTJobsListFactory, getSupportedFeaturesFactory, getIsMaprDistributionFactory, getJenkinsBuildNumberFactory) {

			var self = this;
			self.selectedTab = false;
			self.jobNames = [];
			self.viewAllJobs = false;
			self.clusterCreated = false;
			$scope.infoMessageVal = true;
			$scope.getClusterList = [];
			$scope.getJobList = [];
			$scope.getExampleList = [];
			$scope.scheduledDQTJobList = [];
			$scope.deleted = false;
			$scope.manageClusterClicked = false;
			$scope.jobToDelete = '';
			$scope.inValidRealm = false;
			$scope.hideManageClusterButtons = common.getHideManageClusterButtons();
			self.hideAllDropdown = function() {
				$scope.manageClusterValue = true;
				$scope.recentJobsValue = true;
				$scope.scheduledJobsValue = true;
				$scope.exampleValue = true;
				$scope.aboutUsValue = true;
			};

			self.saveJobNameToDelete = function(jobName) {
				$scope.jobToDelete = jobName;
				$('#jobDeleteConfirmationDialog').modal('show');
			};

			self.hideAllDropdown();

			self.showClusterDropdown = function() {
				self.hideAllDropdown();
				$scope.manageClusterValue = false;
			};

			self.showRecentJobsDropdown = function() {
				self.hideAllDropdown();
				//Show
				$scope.recentJobsValue = false;
			};
			self.showSheduledJobsDropdown = function() {
				self.hideAllDropdown();
				//Show
				$scope.scheduledJobsValue = false;
				self.updateScheduledJobsList();
			};

			self.showExampleDropdown = function() {
				self.hideAllDropdown();
				//Show
				$scope.exampleValue = false;
			};
			self.showAboutUs = function() {
				self.hideAllDropdown();
				$scope.aboutUsValue = false;
			};

			//Analyze cluster
			self.analyzeCluster = function() {
				$location.path("/add-new-cluster-configuration");
				if (self.clusterCreated == false) {
					$scope.infoMessageVal = false;
					setTimeout(function() {
						$scope.$apply($scope.infoMessageVal = true);
					}, 5000);
				}
			};

			//Analyze job
			self.analyzeJob = function() {
				$location.path("/analyze-job");
				if (self.clusterCreated == false) {
					$scope.infoMessageVal = false;
					setTimeout(function() {
						$scope.$apply($scope.infoMessageVal = true);
					}, 5000);
				}
			};
			$scope.showAnalyzeClusterJob = false;

			self.analyzeData = function() {
				common.setJobDetailsFlagRes(false);
				$location.path("/add-analyze-data-configuration").search({ module: 'analyzeData' });
			};

			//Set container height
			$("#homeContainer").height($(window).height() - 116);
			$(window).resize(function() {
				$("#homeContainer").height($(window).height() - 116);
			});

			self.viewLessRecentJobs = function() {
				return (self.jobNames.length <= 8) ? self.jobNames.length : 8;
			};

			self.viewLessScheduleJobs = function() {
				return (self.scheduleJob.length <= 8) ? self.scheduleJob.length : 8;
			};

			$scope.$watch('DashboardController.selectedTab', function(newVal) {
				common.setCurrentTab(newVal);
			});

			self.showContent = function($fileContent) {
				common.setJobMode('edit');
				var content = $fileContent;
				common.openJobByJson(JSON.parse(content));
				$location.path("/add-new-job-configuration");
			};

			self.init = function() {
				$('[data-toggle="tooltip"]').tooltip();
				common.setClusterMode('add', '');
				common.setJobDetails(null);
				self.scheduleJob = common.getWidgetArray();
				self.noofRows = self.viewLessScheduleJobs();
				self.addActive = 'recentJob';

				getIsMaprDistributionFactory.isMaprDistribution({},
					function(data) {
						var isMapr = data.isMapr;
						common.setIsMapr(isMapr);
					},
					function(e) {
						console.log(e);
					});

				getManageClusterFactory.getManageCluster({},
					function(data) {
						$scope.getClusterList = data;
					},
					function(e) {
						console.log(e);
					});
				getJenkinsBuildNumberFactory.jenkinsBuilNumber({},
					function(data) {
						$scope.jenkinsBuilNo = data.buildno;
					},
					function(e) {
						console.log(e);
					});

				getRecentJobFactory.getJob({},
					function(data) {
						$scope.getJobList = data;
					},
					function(e) {
						console.log(e);
					});

				getExampleListFactory.getList({},
					function(data) {
						$scope.getExampleList = data;
					},
					function(e) {
						console.log(e);
					});
			};
			self.updateScheduledJobsList = function() {
				GetScheduledDQTJobsListFactory.getScheduledJobsList({},
					function(data) {
						$scope.scheduledDQTJobList = data;
					},
					function(e) {
						console.log(e);
					});
			}
			self.getJobRequest = function(jobType, jobName) {
				getJobDataFactory.getJobForm({ jobName: jobName }, {},
					function(data) {
						var jobConfig = data;
						common.setResonseData(jobConfig)
						if (jobType == 'Analyze Job') {
							$location.path('add-new-job-configuration');
						} else if (jobType == 'Analyze Data') {
							$location.path('add-analyze-data-configuration')
						} 
					},
					function(e) {
						console.log(e)
					});
			}

			self.getExampleRequest = function(jobType, jobName) {
				getExampleDataFactory.getExampleList({ jobName: jobName }, {},
					function(data) {
						var jobConfig = data;
						common.setResonseData(jobConfig)
						if (jobType == 'Analyze Job') {
							$location.path('add-new-job-configuration');
						} else if (jobType == 'Analyze Data') {
							$location.path('add-analyze-data-configuration')
						} 
					},
					function(e) {
						console.log(e)
					});
			}

			self.openNewJob = function(event) {
				common.setJobMode('add');
				common.setSelectedClusterNameForRun(null);
				$location.path("/add-new-job-configuration").search({ module: 'analyzeJob' });
			};

			self.viewAllRecords = function(btnName) {
				if (btnName == 'View_All') {
					self.noofRows = self.scheduleJob.length;
					self.viewAllJobs = true;
				} else if (btnName == 'View_Less') {
					self.noofRows = self.viewLessScheduleJobs();
					self.viewAllJobs = false;
				}
			};

			self.deleteClusterName = function(key) {
				var listDeleted = $scope.getClusterList[key];
				deleteClusterFactory.submitDeleteForm({ clusterName: listDeleted }, {},
					function(data) {
						if (data) {
							$scope.getClusterList.splice(key, 1);
						}
					},
					function(e) {});
			};

			self.editCluster = function(cluster) {
				common.setClusterMode('edit', cluster);
				$location.path('/add-cluster');
			};

			self.manageCluster = function(cluster) {
				common.setConfigurationData(cluster)
				$location.path('/manage-cluster');
			};

			self.deleteJob = function() {
				$('#jobDeleteConfirmationDialog').modal('hide');
				var jobName = $scope.jobToDelete;
				deleteJobFactory.deleteJob({ jobName: jobName }, {},
					function(data) {
						if (data) {
							getRecentJobFactory.getJob({},
								function(data) {
									$scope.getJobList = data;
								},
								function(e) {
									console.log(e);
								});
								self.updateScheduledJobsList();
						}
					},
					function(e) {
						console.log("Unable to remove job", e);
					});
			};

			self.displayJobResult = function(jobName, jobType) {
				common.setJobName(jobName);
				common.setWidgetObject();
				if (jobType == 'Analyze Job') {
					common.setJobDetails({ 'jobName': jobName });
					$location.path('/analyze-job');
				} else if (jobType == 'Analyze Data') {
					getJobDataFactory.getJobForm({ jobName: jobName }, {}, function(jobConfig) {
						if (jobConfig.enableJsonDataValidation == 'TRUE') {
							$location.path('/analyze-data-json');
						} else if (jobConfig.enableXmlDataValidation == 'TRUE') {
							$location.path('/analyze-data-xml');
						} else if (jobConfig.enableDataProfiling == 'TRUE') {
							common.setNumField(jobConfig.dataProfilingBean.numOfFields);
							$location.path('/analyze-data-profiling');
						} else if (jobConfig.enableDataQualityTimeline == 'TRUE') {
							$location.path('/analyze-data-quality');
						} else if (jobConfig.isDataSourceComparisonEnabled == 'TRUE') {
                            $location.path('/analyze-data-comp');
						} else {
							$location.path('/analyze-data');
						}
					});
				}
			}
			
		}
	]);
