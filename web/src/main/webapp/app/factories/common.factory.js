'use strict';

angular.module('factories')
	.factory('Wizard', ['$resource', '$rootScope',
		function($resource, $rootScope) {
			return $resource(':action', {}, {
				submit: {
					method: 'POST',
					isArray: false,
					params: {
						action: 'ExecutionServlet'
					}
				},

				validate: {
					method: 'POST',
					isArray: false,
					params: {
						action: 'ValidateJSONServlet'
					}
				}
			});
		}
	]).factory('AddClusterFactory', ['$resource', function($resource) {
		return $resource('/apis/cluster/', {}, {
			submitClusterForm: {
				method: 'POST',
				isArray: false
			}
		})
	}]);

angular.module('factories')
	.factory('clusterConfigurationFactory', ['$resource',
		function($resource) {
			return {
				getData: $resource('/apis/adminconfig/clusterconfiguration/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName'
						}
					}
				}),
				getDefaultData: $resource('/apis/adminconfig/defaultclusterconfiguration', {}, {
					get: {
						method: 'GET',
						isArray: false
					}
				}),
				saveData: $resource('/apis/adminconfig/saveclusterconfigurations', {}, {
					post: {
						method: 'POST',
						isArray: false
					}
				}),
				getClusterQueuesList: $resource('/apis/adminconfig/cluster-queues-list/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: true,
						params: {
							clusterName: '@clusterName'
						}
					}
				}),
				getRUMQueuesList: $resource('/apis/adminconfig/cluster-leaf-queues-list/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: true,
						params: {
							clusterName: '@clusterName'
						}
					}
				})
			}
		}
	]);

angular.module('factories')
	.factory('DashboardFactory', ['$resource', '$rootScope',
		function($resource, $rootScope) {
			return {
				dataCenter: $resource('/apis/clusteranalysis/datacenterheatmap', {}, {
					post: {
						method: 'POST',
						isArray: false,
						headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
					}
				}),
				initClusterFac: $resource('/apis/clusteranalysis/initcluster/:clusterName', {}, {
					post: {
						method: 'POST',
						// isArray: false,
						headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
						params: {
							clusterName: '@clusterName'
						}
					}
				})
			}
		}
	]);

angular.module('factories')
	.factory('editClusterFactory', ['$resource',
		function($resource) {
			return $resource('/apis/cluster/', {}, {
				getCluster: {
					method: 'GET',
					isArray: true
				}
			});
		}
	]);
angular.module('factories')
    .factory('DataValidationFactories', ['$resource', '$rootScope',
        function($resource, $rootScope) {
            return $resource($rootScope.root.basePath + ':file', {}, {
                getNullCheck: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        file: 'nullcheck.json'
                    }
                },
                getFieldType: {
                    method: 'GET',
                    isArray: true,
                    params: {
                        file: 'fieldtype.json'
                    }
                }
            });
        }
 ]);

//existing-clusters.json
angular.module('factories')
	.factory('ClusterResultFactory', ['$resource', '$rootScope',
		function($resource, $rootScope) {
			return $resource($rootScope.root.basePath + ':file', {}, {
				getData: {
					method: 'GET',
					isArray: false,
					params: {
						file: 'clusters-result.json'
					}
				},
				getChartData: {
					method: 'GET',
					isArray: false,
					params: {
						file: 'chartsdata.json'
					}
				}
			});
		}
	]);

angular.module('factories')
	.factory('DashboardFactory', ['$resource', '$rootScope',
		function($resource, $rootScope) {
			return {
				dataCenter: $resource('/apis/clusteranalysis/datacenterheatmap', {}, {
					post: {
						method: 'POST',
						isArray: false,
						headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
					}
				}),
				initClusterFac: $resource('/apis/clusteranalysis/initcluster/:clusterName', {}, {
					post: {
						method: 'POST',
						// isArray: false,
						headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
						params: {
							clusterName: '@clusterName'
						}
					}
				})
			}
		}
	]);


angular.module('factories')
	.factory('deleteClusterFactory', ['$resource',
		function($resource) {
			return $resource('/apis/cluster/:clusterName', {}, {
				submitDeleteForm: {
					method: 'DELETE',
					isArray: false,
					params: {
						clusterName: '@clusterName'
					}
				}
			});
		}
	]);

angular.module('factories')
	.factory('deleteJobFactory', ['$resource',
		function($resource) {
			return $resource('/apis/home/deletejob/:jobName', {}, {
				deleteJob: {
					method: 'POST',
					isArray: false,
					params: {
						clusterName: '@jobName'
					}
				}
			});
		}
	]);

angular.module('factories')
	.factory('getClusterDataFactory', ['$resource',
		function($resource) {
			return $resource('/apis/cluster/:clusterName', {}, {
				getClusterForm: {
					method: 'GET',
					isArray: false,
					params: {
						clusterName: '@clusterName'
					}

				}
			});
		}
	]);

angular.module('factories')
	.factory('updateClusterFactory', ['$resource',
		function($resource) {
			return $resource('/apis/cluster/:clusterName', {}, {
				updateCluster: {
					method: 'PUT',
					isArray: false,
					params: {
						clusterName: '@clusterName'
					}

				}
			});
		}
	]);

angular.module('factories')
	.factory('analyzeClusterFactory', ['$resource',
		function($resource) {
			return $resource('/clusteranalysis/', {}, {
				submitAnalyzeCluster: {
					method: 'POST',
					isArray: false

				}
			});
		}
	]);

angular.module('factories')
	.factory('analyzeJobFactory', ['$resource',
		function($resource) {
			return $resource('/apis/jobanalysis/save', {}, {
				submitAnalyzeJob: {
					method: 'POST',
					isArray: false

				}
			});
		}
	]);

angular.module('factories')
	.factory('analyzeOptimizedJob', ['$resource',
		function($resource) {
			return $resource('/apis/optimizejobservice', {}, {
				submitOptimizedJob: {
					method: 'POST',
					isArray: false

				}
			});
		}
	]);

angular.module('factories')
	.factory('getOtimizedYarn', ['$resource',
		function($resource) {
			return $resource('/apis/cluster/hadooptype', {}, {
				getYarn: {
					method: 'GET'
				}
			});
		}
	]);

angular.module('factories')
	.factory('analyzeDataProfiling', ['$resource',
		function($resource) {
			return $resource('/apis/jobanalysis/save', {}, {
				submitanalyzeData: {
					method: 'POST',
					isArray: false

				}
			});
		}
	]);

angular.module('factories')
	.factory('analyzeValidationFactory', ['$resource',
		function($resource) {
			return $resource('/apis/jobanalysis/save/', {}, {
				submitValidateJob: {
					method: 'POST',
					isArray: false

				}
			});
		}
	]);

angular.module('factories')
	.factory('getRecurringFactory', ['$resource',
		function($resource) {
			return $resource('apis/jobanalysis/jobhdfsdetails', {}, {
				getRecurring: {
					method: 'GET',
					isArray: true
				}
			});
		}
	]);

angular.module('factories')
	.factory('deleteRecurringFactory', ['$resource',
		function($resource) {
			return $resource('/apis/jobanalysis/dqt/:jobName', {}, {
				deleteRecurringForm: {
					method: 'DELETE',
					isArray: false,
					params: {
						jobName: '@jobName'
					}
				}
			});
		}
	]);

angular.module('factories')
	.factory('getTableDataFactory', ['$resource',
		function($resource) {
			return $resource('/apis/dvreport', {}, {
				getTableData: {
					method: 'POST',
					isArray: false,
					params: {
						fileName: '@fileName',
						jobName: '@jobName',
						dvType: '@dvType',
						page: '@page',
						rows: '@rows'

					}

				}
			});
		}
	]);
	
angular.module('factories')
    .factory('getDataComparisonTableFactory', ['$resource',
        function($resource) {
            return $resource('/apis/dvreport/data-source-table', {}, {
                getTableDataComp: {
                    method: 'POST',
                    isArray: false,
                    params: {
                        fileName: '@fileName',
                        jobName: '@jobName',
                        transformationNumber: '@transformationNumber',
                        page: '@page',
                        rows: '@rows'

                    }

                }
            });
        }
    ]);


angular.module('factories')
	.factory('getJsonTableDataFactory', ['$resource',
		function($resource) {
			return $resource('/apis/dvreport/jsondvtable', {}, {
				getJsonTableData: {
					method: 'POST',
					isArray: false,
					params: {
						fileName: '@fileName',
						jobName: '@jobName',
						dvType: '@dvType',
						page: '@page',
						rows: '@rows'

					}

				}
			});
		}
	]);

angular.module('factories')
	.factory('getXmlTableDataFactory', ['$resource',
		function($resource) {
			return $resource('/apis/xmldvreport/generateReport', {}, {
				getXmlTableData: {
					method: 'POST',
					isArray: false,
					params: {
						fileName: '@fileName',
						jobName: '@jobName',
						dvType: '@dvType',
						page: '@page',
						rows: '@rows'
					}
				}
			});
		}
	]);

angular.module('factories')
	.factory('getSchemaDataFactory', ['$resource',
		function($resource) {
			return $resource('/apis/xmldvreport/saveSchema', {}, {
				getSchemaData: {
					method: 'POST',
					isArray: false,
					params: {
						schemaInput: '@schemaInput',
						jobName: '@jobName'
					}
				}
			});
		}
	]);

angular.module('factories')
	.factory('getRecentJobFactory', ['$resource',
		function($resource) {
			return $resource('/apis/home/jobs', {}, {
				getJob: {
					method: 'GET',
					isArray: true
				}
			});
		}
	]);

angular.module('factories')
	.factory('GetScheduledTuningJobsListFactory', ['$resource',
		function($resource) {
			return $resource('/apis/home/scheduledtuningjobs', {}, {
				getScheduledJobsList: {
					method: 'GET',
					isArray: true
				}
			});
		}
	]);

angular.module('factories')
	.factory('GetScheduledDQTJobsListFactory', ['$resource',
		function($resource) {
			return $resource('/apis/home/scheduleddqtjobs', {}, {
				getScheduledJobsList: {
					method: 'GET',
					isArray: true
				}
			});
		}
	]);

angular.module('factories')
	.factory('getExampleListFactory', ['$resource',
		function($resource) {
			return $resource('/apis/home/examples', {}, {
				getList: {
					method: 'GET',
					isArray: false
				}
			});
		}
	]);
angular.module('factories')
	.factory('getSupportedFeaturesFactory', ['$resource',
		function($resource) {
			return $resource('/apis/home/supportedFeatures', {}, {
				getSupportedFeaturesList: {
					method: 'GET',
					isArray: true
				}
			});
		}
	]);

angular.module('factories')
	.factory('licenseValidateFactory', ['$resource',
		function($resource) {
			return $resource('/apis/home/licence', {}, {
				submitLicense: {
					method: 'POST',
					isArray: false

				}
			});
		}
	]);

angular.module('factories')
	.factory('getJobDataFactory', ['$resource',
		function($resource) {
			return $resource('/apis/home/jobs/:jobName', {}, {
				getJobForm: {
					method: 'GET',
					isArray: false,
					params: {
						jobName: '@jobName'
					}

				}
			});
		}
	]);

angular.module('factories')
	.factory('getExampleDataFactory', ['$resource',
		function($resource) {
			return $resource('/apis/home/examples/:jobName', {}, {
				getExampleList: {
					method: 'GET',
					isArray: false,
					params: {
						jobName: '@jobName'
					}

				}
			});
		}
	]);

angular.module('factories')
	.factory('getIsMaprDistributionFactory', ['$resource',
		function($resource) {
			return $resource('/apis/home/is-mapr', {}, {
				isMaprDistribution: {
					method: 'GET',
					isArray: false
				}
			});
		}
	]);
angular.module('factories')
	.factory('getJenkinsBuildNumberFactory', ['$resource',
		function($resource) {
			return $resource('/apis/home/jenkins-build-no', {}, {
				jenkinsBuilNumber: {
					method: 'GET',
					isArray: false
				}
			});
		}
	]);

angular.module('factories')
	.factory('validateLicenseFactory', ['$resource',
		function($resource) {
			return $resource('/apis/clusteranalysis/licence/:clusterName', {}, {
				validateLicense: {
					method: 'POST',
					isArray: false,
					params: {
						clusterName: '@clusterName'
					}

				}
			});
		}
	]);

angular.module('factories')
	.factory('deleteThreadFactory', ['$resource',
		function($resource) {
			return $resource('/apis/clusteranalysis/licence/:clusterName', {}, {
				validateLicense: {
					method: 'POST',
					isArray: false,
					params: {
						clusterName: '@clusterName'
					}

				}
			});
		}
	]);

angular.module('factories')
	.factory('getJobValidateFactory', ['$resource',
		function($resource) {
			return $resource('/apis/validateservice/jobname/:jobName', {}, {
				getJobValidate: {
					method: 'GET',
					isArray: false,
					params: {
						jobName: '@jobName'
					}

				}
			});
		}
	]);

angular.module('factories')
    .factory('getTotalClusterNodeFactory', ['$resource',
        function($resource) {
            return $resource('/apis/cluster/total-cluster-nodes-added', {}, {
                getTotalNodesList: {
                    method: 'GET',
                    isArray: false
                }
            });
        }
    ]);
 angular.module('factories')
	.factory('getDefaultRootFactory', ['$resource',
		function($resource) {
			return $resource('/apis/adminconfig/dlc-root/:clusterName', {}, {
				getDefaultRoot: {
					method: 'GET',
					isArray: false,
					params: {
						clusterName: '@clusterName'
					}

				}
			});
		}
	]);

angular.module('factories')
	.factory('ClusterResultFactoryNew', ['$resource', '$rootScope',
		function($resource, $rootScope) {
			return {
				dataCenter: $resource('/apis/clusteranalysis/datacenterheatmap', {}, {
					post: {
						method: 'POST',
						isArray: false,
						headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
						timeout: 300000
					}
				}),
				isInfluxdbLive: $resource('/apis/clusteranalysis/is-influxdb-live/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 15000
					}
				}),
				dataCenterPreview: $resource('/apis/clusteranalysis/datacenterheatmap/preview/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				resourceOverUsage: $resource('/apis/clusteranalysis/resourceoverusage/:clusterName?memoryThresholdMB=:memoryThresholdMB&vcoresThreshold=:vcoresThreshold', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName',
							memoryThresholdMB: '@memoryThresholdMB',
							vcoresThreshold: '@vcoresThreshold'
						},
						timeout: 300000
					}
				}),
				longRunningApps: $resource('/apis/clusteranalysis/longrunningapps/:clusterName?thresholdMillis=:thresholdMillis', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName',
							thresholdMillis: '@thresholdMillis'
						},
						timeout: 300000
					}
				}),
				slaApps: $resource('/apis/clusteranalysis/sla-apps/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: true,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				
				dataLoad: $resource('/apis/clusteranalysis/dataloadanddistribution/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: true,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				dataLoadDetails: $resource('/apis/clusteranalysis/dataloadanddistributiondetails/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: true,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				dataFetch: $resource('/apis/clusteranalysis/clusterprofiling/:clusterName', {}, {
					get: {
						method: 'POST',
						isArray: false,

						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),

				//resource object to fetch queue stats required on cluster analysis result page         
				queueStats: $resource('/apis/clusteranalysis/clusterprofiling/queuestats/:clusterName', {}, {
					get: {
						method: 'POST',
						isArray: true,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				schedulerType: $resource('/apis/clusteranalysis/is-fair-scheduler/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				meteredQueueUsage: $resource('/apis/clusteranalysis/clusterprofiling/metered-queue-usage/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: true,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				queueUtilizationSummary: $resource('/apis/clusteranalysis/clusterprofiling/queue-utilization-summary/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: true,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				backgroundProcesses: $resource('/apis/clusteranalysis/background-processes/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				//resource object to fetch rack aware stats required on cluster analysis result page
				rackAwareStats: $resource('/apis/clusteranalysis/clusterprofiling/rackAwareStats/:clusterName', {}, {
					get: {
						method: 'POST',
						isArray: false,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				//resource object to fetch live container stats required on cluster analysis result page
				liveContainerStats: $resource('/apis/clusteranalysis/clusterprofiling/liveContainerStats/:clusterName', {}, {
					get: {
						method: 'POST',
						isArray: false,

						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				//resource object to fetch effective capability utilization stats required on cluster analysis result page
				effCapUtilizationStats: $resource('/apis/clusteranalysis/clusterprofiling/effCapUtilizationStats/:clusterName', {}, {
					get: {
						method: 'POST',
						isArray: true,

						params: {
							clusterName: '@clusterName'
						},
						timeout: 900000
					}
				}),

				//Inititate history file copy to jumbune home
				copyHistoryFile: $resource('/apis/clusteranalysis/clusterprofiling/copyHistoryFile/:clusterName', {}, {
					get: {
						method: 'POST',
						isArray: false,

						params: {
							clusterName: '@clusterName'
						},
						timeout: 900000
					}
				}),

				filteredCategoryList: $resource('/apis/clusteranalysis/filteredcategories/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 120000
					}
				}),
				categoryList: $resource('/apis/clusteranalysis/filteredcategories/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 120000
					}
				}),
				clusterNodes: $resource('/apis/clusteranalysis/clusternodes/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: true,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 120000
					}
				}),
				profilerGraphData: $resource('/apis/clusteranalysis/profilejob?clusterName=:clusterName&jobID=:jobName', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName',
							jobName: '@jobName'
						},
						timeout: 300000
					}
				}),
				jobhistoryGraphData: $resource('/apis/clusteranalysis/job-history?clusterName=:clusterName&jobName=:jobName&duration=:duration', {}, {
					get: {
						method: 'GET',
						isArray: true,
						params: {
							clusterName: '@clusterName',
							jobName: '@jobName',
							duration:'@duration'
						},
						timeout: 300000
					}
				}),
				queueUserGraph: $resource('/apis/clusteranalysis/user-queue-utilization/:clusterName', {}, {
					post: {
						method: 'POST',
						isArray: false,

						params: {
							clusterName: '@clusterName'
						},
						timeout: 900000
					}
				}),
				getCategoryData: $resource('/apis/clusteranalysis/categories/nodespecific?clusterName=:clusterName&nodeIP=:nodeIP', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName',
							nodeIP: '@nodeIP'
						},
						timeout: 210000
					}                
				}),
				alertsUpdateInterval: $resource('/apis/clusteranalysis/alertsupdateinterval/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 120000
					}
				}),
				getCategoryChartData: $resource('/apis/clusteranalysis/getclusterchartdata', {}, {
					post: {
						method: 'POST',
						isArray: false,
						async: false,
						headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
						timeout: 210000
					}
				}),
				// Girish
				getNodeCategoryChartData: $resource('/apis/clusteranalysis/nodespecific', {}, {
					post: {
						method: 'POST',
						isArray: false,
						async: false,
						headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
						timeout: 210000
					}
				}),
				getAlerts: $resource('/apis/clusteranalysis/alerts/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: true,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				getRecommendations: $resource('/apis/clusteranalysis/recommendations/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: true,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				readWriteServiceData: $resource('/apis/clusteranalysis/writeclusterchartdata', {}, {
					post: {
						method: 'POST',
						isArray: false,
						headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
						timeout: 120000
					}
				}),
				clusterwideMajorCounters: $resource('/apis/clusteranalysis/clusterwide-majorcounters/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				maprCldbMetrics: $resource('/apis/clusteranalysis/mapr-cldb-metrics/:clusterName', {}, {
					get: {
						method: 'GET',
						isArray: false,
						params: {
							clusterName: '@clusterName'
						},
						timeout: 300000
					}
				}),
				getQuickTuningResult: $resource('/apis/clusteranalysis/quicktuning', {}, {
					post: {
						method: 'POST',
						isArray: true,
						headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
						timeout: 240000
					}
				}),
				initClusterFac: $resource('/apis/clusteranalysis/initcluster/:clusterName', {}, {
					post: {
						method: 'POST',
						// isArray: false,
						headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
						params: {
							clusterName: '@clusterName'
						}
					}
				})



			}
		}
	]);
