/* Dashboard controller */
'use strict';
angular.module('dashboard.ctrl', ["ngAnimate"])
	.controller('DashboardController', ['$scope', '$rootScope', 'common', '$location', 'editClusterFactory', 'deleteClusterFactory', 'getRecentJobFactory', 'GetScheduledTuningJobsListFactory', 'getJobDataFactory', 'getExampleDataFactory', 'getExampleListFactory', 'licenseValidateFactory', 'ClusterResultFactoryNew', 'deleteJobFactory', 'GetScheduledDQTJobsListFactory', 'getSupportedFeaturesFactory', 'getIsMaprDistributionFactory', 'getJenkinsBuildNumberFactory',

		function($scope, $rootScope, common, $location, editClusterFactory, deleteClusterFactory, getRecentJobFactory, GetScheduledTuningJobsListFactory, getJobDataFactory, getExampleDataFactory, getExampleListFactory, licenseValidateFactory, ClusterResultFactoryNew, deleteJobFactory, GetScheduledDQTJobsListFactory, getSupportedFeaturesFactory, getIsMaprDistributionFactory,getJenkinsBuildNumberFactory) {

			var self = this;
			self.selectedTab = false;
			self.jobNames = [];
			self.viewAllJobs = false;
			self.clusterCreated = false;

			$scope.infoMessageVal = true;
			$scope.getClusterList = [];
			$scope.getlicense = {};
			$scope.getJobList = [];
			$scope.getExampleList = [];
			$scope.scheduledTuningJobList = [];
			$scope.scheduledDQTJobList = [];
			$scope.deleted = false;
			$scope.manageClusterClicked = false;
			$scope.jobToDelete = '';
			$scope.licenseExpireTrue = false;

			self.hideAllDropdown = function() {
				$scope.manageClusterValue = true;
				$scope.recentJobsValue = true;
				$scope.scheduledJobsValue = true;
				$scope.licenseValue = true;
				$scope.exampleValue = true;
				$scope.acThreadsValue = true;
				$scope.aboutUsValue = true;
			};

			self.saveJobNameToDelete = function(jobName) {
				$scope.jobToDelete = jobName;
				$('#jobDeleteConfirmationDialog').modal('show');
			};

			self.hideAllDropdown();

			self.showClusterDropdown = function() {
				self.hideAllDropdown();
				//Show
				$scope.manageClusterValue = false;
				//$scope.hoverEdit = false;
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

			self.showLicenseDropdown = function() {
				self.hideAllDropdown();
				//Show
				$scope.licenseValue = false;
			};

			self.showExampleDropdown = function() {
				self.hideAllDropdown();
				//Show
				$scope.exampleValue = false;
			};

			self.showACThreads = function() {
				self.hideAllDropdown();
				$scope.acThreadsValue = false;
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

			//ANAYLYZE ZOB
			$scope.showOptimizeJob = false;
			$scope.showAnalyzeDataJob = false;
			$scope.showAnalyzeJob = false;
			$scope.showAnalyzeClusterJob = false;

			//var scheduled = ['ClusterAnalysis', 'JobQuality']


			self.optimizeJob = function() {
				//$location.path("/analyze-cluster");
				$location.path("/add-optimized-job-configuration").search({ module: 'optimizeJob' });
			};



			self.analyzeData = function() {
				//$location.path("/analyze-cluster");
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

				editClusterFactory.getCluster({},
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
				getSupportedFeaturesFactory.getSupportedFeaturesList({},
					function(data) {
						$scope.getFeaturesList = data;
						for (var arr in $scope.getFeaturesList) {
							if ($scope.getFeaturesList[arr] == 'ClusterAnalysis') {
								$scope.showAnalyzeClusterJob = true;
							} else if ($scope.getFeaturesList[arr] == 'JobQuality') {
								$scope.showAnalyzeJob = true;
							} else if ($scope.getFeaturesList[arr] == 'DataQuality') {
								$scope.showAnalyzeDataJob = true;
							} else if ($scope.getFeaturesList[arr] == 'OptimizeJob') {
								$scope.showOptimizeJob = true;
							}
						}
						if ($scope.getFeaturesList.length == 3) {
							if ($scope.showAnalyzeDataJob && $scope.showAnalyzeClusterJob) {
								$("#showDataQualityJob").css("margin-left", 235);
							} else {
								if ($scope.showAnalyzeDataJob == true) {
									$("#showDataQualityJob").css("margin-left", 235);
								}
								if ($scope.showAnalyzeClusterJob == true) {
									$("#showClusterAnalysisJob").css("margin-left", 235);
								}
							}
						}
						if ($scope.getFeaturesList.length == 1) {
							$("#show" + $scope.getFeaturesList[0] + "Job").css("margin-left", 235);
						}
					},
					function(e) {
						console.log(e);
					});

				$scope.isACThreadMapEmpty = true;
				
				/*getACThreadsListFactory.getACThreadsList({},
					function(data) {
						$scope.acThreadsList = data;
						if (data.length == 0) {
							$scope.isACThreadMapEmpty = true;
						} else {
							$scope.isACThreadMapEmpty = false;
						}
					},
					function(e) {
						console.log(e);
					});*/

				/*licenseValidateFactory.submitLicense({},
					function(data) {
						$scope.getlicense = data;
						common.setNodeSize($scope.getlicense)
					},
					function(e) {
						console.log(e);
					});*/
					licenseValidateFactory.submitLicense({},
					function(data) {
						common.setNodeSize(data);
						//var currentDate = new Date().getTime();
						var currentDate = data.currentTime;
						if ( data['Valid Until'] ) {
						    var expiryDate = data['Valid Until'];
							var milliseconds = (expiryDate - currentDate);
							var daysDiff = milliseconds/86400000;
							if ( daysDiff <= 3 ) {
								$scope.licenseExpireTrue = true;
							}
						    var temp = new Date(data['Valid From']).toString();
							if ( daysDiff <=3 ) {
								  data['Valid From'] = temp.substring(4);
							} else {
								data['Valid From'] = temp.substring(4, 15);
							}

						    temp = new Date(data['Valid Until']).toString();
							if ( daysDiff <=3 ) {
								  data['Valid Until'] = temp.substring(4);
							} else {
								data['Valid Until'] = temp.substring(4, 15);
							}
							
						} else {
							var temp = new Date(data['Valid From']).toString();
						    data['Valid From'] = temp.substring(4, 16) + temp.substring(25);
						}
						$scope.getlicense = data;
					},
					function(e) {
						console.log(e);
					});
			};


			self.updateScheduledJobsList = function() {
				GetScheduledTuningJobsListFactory.getScheduledJobsList({},
					function(data) {
						$scope.scheduledTuningJobList = data;
					},
					function(e) {
						console.log(e);
					});
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
						} else if (jobType == 'Optimize Job') {
							$location.path('add-optimized-job-configuration');
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
						} else if (jobType == 'Optimize Job') {
							$location.path('add-optimized-job-configuration');
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

			self.displayScheduledJobResult = function(jobName) {
				common.setWidgetObject();
				common.setOptimizeJobName(jobName);            
				$location.path('/optimize-graph');
			}

			self.displayJobResult = function(jobName, jobType) {
				common.setOptimizeJobName(jobName);
				common.setWidgetObject();
				if (jobType == 'Optimize Job') {
					$location.path('/optimize-graph');
				} else if (jobType == 'Analyze Job') {
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
						} else if (jobConfig.isDataCleansingEnabled == 'TRUE') {
							$location.path('/define-analyzeData-cleansing');
						} else {
							$location.path('/analyze-data');
						}
					});
				}
			}
		}
	]);
