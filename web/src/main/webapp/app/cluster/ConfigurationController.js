/* Configuration controller */
'use strict';
angular.module('configuration.ctrl', [])

.controller('ConfigurationController', ['$scope', 'common', '$timeout', '$location', 'clusterConfigurationFactory',
	function($scope, common, $timeout, $location, clusterConfigurationFactory) {
		var clusterName;
		var globalSettings;
		$scope.alertAction = {};
		$scope.slaAction = {};
		$scope.queueConf = {};
		$scope.queueAction = {};
		$scope.alertAction.snmpTraps = {};
		$scope.updateAlert = false;
		$scope.clusterQueuesList = [];
		$scope.toEditAlertActionData = false;
		$scope.toEditSLAActionData = false;
		$scope.toEditQueueConfData = false;
		$scope.isExecutionEngineAll = false;
		$scope.toAddAlertActionData = true;
		$scope.toAddSLAActionData = true;
		$scope.toAddQueueConfData = true;
		$scope.displaySnmpErrorMessage = false;
		$scope.showForm = true;
		$scope.showWarningCriticalErrorMessage = false;
		$scope.ObjectQueue = {"selectedQueueName":"","warningLevel":60,"criticalLevel":80};
		$scope.RUMQueue = {"queueName":"","executionEngine":"","vCore":"","memory":""};
		if ( common.getChargeBackConf() ) {
		   clusterName = common.getChargeBackConf().clusterName;
		   $scope.selectedTab = common.getChargeBackConf().tabName;
		   common.setChargeBackConf(undefined);
		} else {
		 clusterName = common.getConfigurationData();
		 $scope.selectedTab = 'emailTab';
		}
		$scope.clusterName = clusterName;

		$("div.my-tool-tip").tooltip();
		$("td.my-tool-tip").tooltip();

		/** Getting global/template/default configurations */
		clusterConfigurationFactory.getDefaultData.get({}, {},
			function(data) {
				globalSettings = data;
			},
			function(e) {
				console.log("Unable to fetch default data", e);
			});

		/** Getting cluster configurations  */
		clusterConfigurationFactory.getData.get({
				clusterName: clusterName
			}, {},
			function(data) {
				fillData(data);
			},
			function(e) {
				console.log("Unable to fetch data", e);
			});

		/** Getting cluster queues list  */
		clusterConfigurationFactory.getClusterQueuesList.get({
				clusterName: clusterName
			}, {},
			function(data) {
				if (data && data.length != 0) {
					$scope.clusterQueuesList = data;
				}
			},
			function(e) {
				console.log("Unable to fetch cluster queues list", e);
			});

		/** Getting Resource metering usage cluster queues list  */
		clusterConfigurationFactory.getRUMQueuesList.get({
				clusterName: clusterName
			}, {},
			function(data) {
				if (data && data.length != 0) {
					$scope.RUMQueuesList = data;
				}
			},
			function(e) {
				console.log("Unable to fetch cluster queues list", e);
			});

		/** Check if String null or empty */
		function isStringNullOrEmpty(x) {
			if (x == null || x == undefined || x.length == 0) {
				return true;
			}
		}
		/** set email authentication */
		function setEmailAuth(conf) {
			if (conf.authentication == true) {
				conf.authentication = "true";
			}
			if (conf.authentication == false) {
				conf.authentication = "false";
			}
			return conf;
		}
		/***/
		function setEmailAuthReverse(conf) {
			if (conf.authentication == "true") {
				conf.authentication = true;
			}
			if (conf.authentication == "false") {
				conf.authentication = false;
			}
			return conf;
		}

		/** Check if all snmp values (trapOID, ipAddress, port ) are filled or not */
		function checkSNMP(alertAction) {
			var snmpTraps = alertAction.snmpTraps;
			if (snmpTraps == null) {
				return alertAction;
			}
			if (isStringNullOrEmpty(snmpTraps.trapOID) || isStringNullOrEmpty(snmpTraps.ipAddress) || isStringNullOrEmpty(snmpTraps.port)) {
				alertAction.snmpTraps = null;
			}
			return alertAction;
		}
		/** check SNMP traps null or empty in escalation module */
		function isSNMPCorrect(alertAction) {
			var snmpTraps = alertAction.snmpTraps;
			if (snmpTraps.trapOID || snmpTraps.ipAddress || snmpTraps.port) {
				if (isStringNullOrEmpty(snmpTraps.trapOID)) {
					return false;
				}
				if (isStringNullOrEmpty(snmpTraps.ipAddress)) {
					return false;
				}
				if (isStringNullOrEmpty(snmpTraps.port)) {
					return false;
				}
			}
			return true;
		}

		$scope.showQueue = false;
		$scope.addQueue = function () {
			$scope.showQueue = true;
			$scope.toEditQueueUtilizationData = false;
			$scope.QueueUtilization = false;
			$scope.toEditGenericQueueUtilizationData = false;
			$scope.ObjectQueue.selectedQueueName = '';
			$scope.showWarningCriticalErrorMessage = false;
			$scope.ObjectQueue ={"selectedQueueName":"","warningLevel":60,"criticalLevel":80};
		}

		$scope.CancelQueue = function () {
			$scope.QueueUtilizationIndex = '';
			$scope.showQueue = false;
			$scope.toEditQueueUtilizationData = false;
			$scope.QueueUtilization = false;
			$scope.toEditGenericQueueUtilizationData = false;
		}

		/** Function disable Add and update button in escalation module */
		$scope.isDisabled = function() {
			if (!$scope.alertAction.alertLevel || !$scope.alertAction.occuringSinceHours) {
				return true;
			}

			if ($scope.alertAction.enableTicket == true) {
				if (!($scope.ticketConfiguration.host && $scope.ticketConfiguration.port && $scope.ticketConfiguration.username && $scope.ticketConfiguration.password && $scope.ticketConfiguration.formName)) {
					return true;
				}
			}
			if (!( $scope.alertAction.emailTo ||
					($scope.alertAction.snmpTraps
							&& $scope.alertAction.snmpTraps.trapOID
							&& $scope.alertAction.snmpTraps.ipAddress
							&& $scope.alertAction.snmpTraps.port) )) {
				return true;
			}
			return false;
		}
		/** Function disable Add and update button in SLA module */
		$scope.isDisabledSLA = function() {

			if ( !$scope.slaAction.maximumDuration || !$scope.slaAction.user ) {
				return true;
			}
			return false;
		}
		/** Function disable Add and update button in RUM module */
		$scope.isDisabledQueue = function () {
			if ( ( !$scope.RUMQueue.queueName || !$scope.RUMQueue.executionEngine ) || (!$scope.RUMQueue.vCore && !$scope.RUMQueue.memory) ) {
				return true;
			}
			return false;
		}
		/** Add new alert action in escalation module */
		$scope.addAlertAction = function() {
			if (!isSNMPCorrect($scope.alertAction)) {
				$scope.displaySnmpErrorMessage = true;
				return;
			}
			$scope.displaySnmpErrorMessage = false;
			$scope.alertActionConfiguration.alertActions.push(angular.copy(checkSNMP($scope.alertAction)));
			$scope.toAddAlertActionData = false;
		}
		$scope.cancelAlertAction = function() {
			$scope.toAddAlertActionData = false;
			$scope.toEditAlertActionData = false;
		}
		/** It updates alert action values */
		$scope.updateAlertAction = function() {
			if (!isSNMPCorrect($scope.alertAction)) {
				$scope.displaySnmpErrorMessage = true;
				return;
			}
			$scope.displaySnmpErrorMessage = false;
			$scope.alertActionConfiguration.alertActions[$scope.alertActionIndex] = angular.copy(checkSNMP($scope.alertAction));
			$scope.toEditAlertActionData = false;
		}
		/** Display alert action form */
		$scope.showAddAlertActionForm = function() {
			$scope.alertAction = {};
			$scope.alertAction.snmpTraps = {};
			$scope.toAddAlertActionData = true;
			$scope.toEditAlertActionData = false;
		}

		/** Display the form and fill it will that alertlevel object values */
		$scope.showAlertActionData = function(index) {
			$scope.alertActionIndex = index;
			$scope.alertAction = angular.copy($scope.alertActionConfiguration.alertActions[index]);
			$scope.toAddAlertActionData = false;
			$scope.toEditAlertActionData = true;
		}
		$scope.showQueueAction = function(index) {
			$scope.alertActionIndex = index;
			$scope.alertAction = angular.copy($scope.alertActionConfiguration.alertActions[index]);
			$scope.toAddAlertActionData = false;
			$scope.toEditAlertActionData = true;
		}
		/** delete alert action configuration */
		$scope.deleteAlertAction = function(index) {
			$scope.alertActionConfiguration.alertActions.splice(index, 1);
			if ($scope.alertActionIndex == index) {
				$scope.toEditAlertActionData = false;
				$scope.toAddAlertActionData = false;
			}
		}
		/** Add new SLA action in*/
		$scope.addSLAAction = function() {
			$scope.slaConfigurations.slaConfList.push($scope.slaAction);
			$scope.toAddSLAActionData = false;
		}
		$scope.cancelSLAAction = function() {
			$scope.toAddSLAActionData = false;
			$scope.toEditSLAActionData = false;
		}
		$scope.cancelRumAction = function() {
			$scope.toAddQueueConfData = false;
			$scope.toEditQueueConfData = false;
		}
		/** It updates alert action values */
		$scope.updateSLAAction = function() {
			$scope.slaConfigurations.slaConfList[$scope.slaActionIndex] = angular.copy(checkSNMP($scope.slaAction));
			$scope.toEditSLAActionData = false;
		}

		/** Display SLA action form */
		$scope.showAddSLAActionForm = function() {
			$scope.slaAction = {};
			$scope.toAddSLAActionData = true;
			$scope.toEditSLAActionData = false;
		}
		$scope.showAddQueueForm = function() {
			$scope.toAddQueueConfData = true;
			$scope.toEditQueueConfData = false;
			$scope.RUMQueue.selectedQueueName = '';
			$scope.RUMQueue = {"queueName":"","executionEngine":"","vCore":"","memory":""};
		}
		/** Display the form and fill it will that sla object values */
		$scope.showSLAActionData = function(index) {
			$scope.slaActionIndex = index;
			$scope.slaAction = angular.copy($scope.slaConfigurations.slaConfList[index]);
			$scope.toAddSLAActionData = false;
			$scope.toEditSLAActionData = true;
		}

		/** delete sla action configuration */
		$scope.deleteSLAAction = function(index) {
			$scope.slaConfigurations.slaConfList.splice(index, 1);
			if ($scope.slaActionIndex == index) {
				$scope.toEditSLAActionData = false;
				$scope.toAddSLAActionData = false;
			}
		}

		$scope.toEditQueueUtilizationData = false;
		$scope.QueueUtilization = true;
		$scope.queueFilter = function(queueName) {
			if ($scope.alertConfiguration == null) {
				return;
			}
			if ($scope.alertConfiguration.individualQueueAlerts == undefined || $scope.alertConfiguration.individualQueueAlerts == null) {
				$scope.alertConfiguration.individualQueueAlerts = {};
			}
			for ( var queue in $scope.alertConfiguration.individualQueueAlerts ) {
				if ( queueName == queue) {
					return;
				}

			}
			return queueName;

		}
		$scope.addQueueUtilization = function() {
			if ($scope.alertConfiguration.individualQueueAlerts == null || $scope.alertConfiguration.individualQueueAlerts == undefined) {
				$scope.alertConfiguration.individualQueueAlerts = {};
			}

			if (Number($scope.ObjectQueue.warningLevel) >= Number($scope.ObjectQueue.criticalLevel)) {
				$scope.showWarningCriticalErrorMessage = true;
				return;
			} else {
				$scope.showWarningCriticalErrorMessage = false;
			}
			var value = {"queueName":$scope.ObjectQueue.selectedQueueName,"warningLevel" :$scope.ObjectQueue.warningLevel , "criticalLevel" :$scope.ObjectQueue.criticalLevel }
			$scope.alertConfiguration.individualQueueAlerts[value.queueName] = {"warningLevel" :$scope.ObjectQueue.warningLevel , "criticalLevel" :$scope.ObjectQueue.criticalLevel};
			$scope.toEditQueueUtilizationData = false;
			$scope.toEditGenericQueueUtilizationData = false;
			$scope.QueueUtilization = false;
			$scope.showQueue = false;

		}
		$scope.queueTypeList = [
		{label:'All', value:'ALL'},
		{label:'MapReduce', value:'MAPREDUCE'},
		{label:'Spark', value:'SPARK'},
		{label:'Tez', value:'TEZ'}];

		$scope.showWarningCriticalError = function () {
			if (Number($scope.ObjectQueue.warningLevel) >= Number($scope.ObjectQueue.criticalLevel)) {
				$scope.showWarningCriticalErrorMessage = true;
			} else {
				$scope.showWarningCriticalErrorMessage = false;
			}
		}
		$scope.hDFS_UTILIZATIONError = function () {
			if ((Number($scope.alertConfiguration.configurableAlerts.HDFS_UTILIZATION.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.HDFS_UTILIZATION.criticalLevel))) {
				$scope.hDFS_UTILIZATIONFalse = true;
			} else {
				$scope.hDFS_UTILIZATIONFalse = false;
			}
		}
		$scope.uNDER_REPLICATED_BLOCKSError = function () {
			if (Number($scope.alertConfiguration.configurableAlerts.UNDER_REPLICATED_BLOCKS.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.UNDER_REPLICATED_BLOCKS.criticalLevel)) {
				$scope.uNDER_REPLICATED_BLOCKSFalse = true;
			} else {
				$scope.uNDER_REPLICATED_BLOCKSFalse = false;
			}
		}
		$scope.dISK_SPACE_UTILIZATIONError = function () {
			if (Number($scope.alertConfiguration.configurableAlerts.DISK_SPACE_UTILIZATION.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.DISK_SPACE_UTILIZATION.criticalLevel)) {
				$scope.dISK_SPACE_UTILIZATIONFalse = true;
			} else {
				$scope.dISK_SPACE_UTILIZATIONFalse = false;
			}
		}

		$scope.editQueueUtilizationData = function(key) {
			$scope.QueueUtilizationIndex = key;
			$scope.ObjectQueue.warningLevel = $scope.alertConfiguration.individualQueueAlerts[key].warningLevel;
			$scope.ObjectQueue.selectedQueueName = key;
			$scope.ObjectQueue.criticalLevel = $scope.alertConfiguration.individualQueueAlerts[key].criticalLevel;
			$scope.toEditQueueUtilizationData = true;
			$scope.editingIndividualQueue = true;
			$scope.toEditGenericQueueUtilizationData = false;
			$scope.QueueUtilization = false;
			$scope.showQueue = false;
			$scope.showWarningCriticalErrorMessage = false;
		}

		$scope.toEditGenericQueueUtilizationData = false;
		$scope.editGenericQueueUtilizationData = function(key) {
			$scope.GenericQueueUtilizationIndex = key;
			$scope.ObjectQueue.selectedQueueName = key;
			$scope.ObjectQueue.warningLevel = $scope.alertConfiguration.configurableAlerts["QUEUE_UTILIZATION"].warningLevel;
			$scope.ObjectQueue.criticalLevel = $scope.alertConfiguration.configurableAlerts["QUEUE_UTILIZATION"].criticalLevel;
			$scope.toEditQueueUtilizationData = true;
			$scope.editingIndividualQueue = false;
			$scope.toEditGenericQueueUtilizationData = true;
			$scope.QueueUtilization = false;
			$scope.showQueue = false;
			$scope.showWarningCriticalErrorMessage = false;
		}
		$scope.deleteQueueUtilizationData = function(key) {
			delete $scope.alertConfiguration.individualQueueAlerts[key];
			$scope.showQueue = false;
		}

		$scope.isNullOrEmpty = function (obj) {
			if (obj == null || obj == undefined || Object.keys(obj).length == 0) {
				return true;
			} else {
				return false;
			}
		};
		$scope.updateQueueUtilizationData = function() {

			if (Number($scope.ObjectQueue.warningLevel) >= Number($scope.ObjectQueue.criticalLevel)) {
				$scope.showWarningCriticalErrorMessage = true;
				return;
			} else {
				$scope.showWarningCriticalErrorMessage = false;
			}
			if ( $scope.toEditGenericQueueUtilizationData) {
				$scope.alertConfiguration.configurableAlerts["QUEUE_UTILIZATION"].warningLevel = $scope.ObjectQueue.warningLevel;
			$scope.alertConfiguration.configurableAlerts["QUEUE_UTILIZATION"].criticalLevel = $scope.ObjectQueue.criticalLevel;
			} else {
				$scope.alertConfiguration.individualQueueAlerts[$scope.QueueUtilizationIndex].warningLevel =$scope.ObjectQueue.warningLevel;
			$scope.alertConfiguration.individualQueueAlerts[$scope.QueueUtilizationIndex].criticalLevel = $scope.ObjectQueue.criticalLevel;
			}
			$scope.toEditQueueUtilizationData = false;
			$scope.showQueue = false;
			$scope.toEditGenericQueueUtilizationData = false;
			$scope.QueueUtilization = false;

		}
		/** Start : HA,Influx,alert reset settings */
		$scope.resetHASettings = function() {
			$scope.haConfiguration = globalSettings.haConfiguration;
		}

		$scope.resetInfluxDBSettings = function() {
			$scope.influxDBConfiguration = globalSettings.influxDBConfiguration;
		}

		$scope.resetAlertSettings = function() {
			$scope.alertConfiguration = globalSettings.alertConfiguration;
		}
		$scope.resetBackgroundProcessSettings = function() {
			$scope.backgroundProcessConfiguration = globalSettings.backgroundProcessConfiguration;
		}
		/** End : HA,Influx, alert reset settings */

		/** checks obj is null or not if null than set */
		function setNotNull(obj) {
			if (obj == null || obj == undefined) {
				return {};
			}
		}
		/** set data in modals */
		function fillData(data) {
			$scope.emailConfiguration = data.emailConfiguration;
			if (data.emailConfiguration == null) {
				$scope.emailConfiguration = {};
			}
			$scope.emailConfiguration = setEmailAuth($scope.emailConfiguration);
			$scope.haConfiguration = data.haConfiguration;
			if (data.haConfiguration == null) {
				if (globalSettings.haConfiguration != null) {
					$scope.haConfiguration = globalSettings.haConfiguration;
				} else {
					$scope.haConfiguration = {};
				}
			}
			$scope.influxDBConfiguration = data.influxDBConfiguration;
			if (data.influxDBConfiguration == null) {
				if (globalSettings.influxDBConfiguration != null) {
					$scope.influxDBConfiguration = globalSettings.influxDBConfiguration;
				} else {
					$scope.influxDBConfiguration = "";
				}
			}

			$scope.alertConfiguration = data.alertConfiguration;
			if (data.alertConfiguration == null) {
				if (globalSettings.alertConfiguration != null) {
					$scope.alertConfiguration = globalSettings.alertConfiguration;
				} else {
					$location.path("/dashboard");
				}
			}
			if ($scope.alertConfiguration['individualQueueAlerts'] == null || $scope.alertConfiguration['individualQueueAlerts'] == undefined) {
				$scope.alertConfiguration['individualQueueAlerts'] = {};
			}
			if ($scope.alertConfiguration['hdfsDirPaths'] == null || $scope.alertConfiguration['hdfsDirPaths'] == undefined) {
			 	$scope.alertConfiguration['hdfsDirPaths'] = [];
			}
			$scope.ticketConfiguration = data.ticketConfiguration;
			if (data.ticketConfiguration == null) {
				$scope.ticketConfiguration = {};
			}

			$scope.workerNodeHostArrCount = [1];

			$scope.alertActionConfiguration = data.alertActionConfiguration;
			if ($scope.alertActionConfiguration == null) {
				$scope.alertActionConfiguration = { "alertActions": [] };
			}
			$scope.slaConfigurations = data.slaConfigurations;
			if ($scope.slaConfigurations == null) {
				$scope.slaConfigurations = { "slaConfList": [] };
			}

			$scope.backgroundProcessConfiguration = data.backgroundProcessConfiguration;
			if (data.backgroundProcessConfiguration == null) {
				$scope.backgroundProcessConfiguration = {
					"processMap": {
						"SYSTEM_METRICS"   : false,
						"QUEUE_UTILIZATION": false,
						"WORKER_NODES_UPDATER" : false
					}
				};
			}

		}
		/** Goes to index page */
		$scope.backConfigData = function() {
			if ( common.getAcTabDetail() == true ) {
				$location.path('/analyze-cluster');
				common.setAcTabDetail(null);
			} else {
				$location.path('/index');
			}
		};
		/** Function to save cluster configuration data */
		$scope.saveConfigData = function() {
			var adminConfiguration = {};
			adminConfiguration['clusterName'] = $scope.clusterName;
			$scope.dISK_SPACE_UTILIZATIONFalse = false;
			$scope.uNDER_REPLICATED_BLOCKSFalse = false;
			$scope.hDFS_UTILIZATIONFalse = false;
			$scope.qUEUE_UTILIZATIONFalse = false;

			if ( ( Number($scope.alertConfiguration.configurableAlerts.DISK_SPACE_UTILIZATION.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.DISK_SPACE_UTILIZATION.criticalLevel) ) && ( Number($scope.alertConfiguration.configurableAlerts.UNDER_REPLICATED_BLOCKS.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.UNDER_REPLICATED_BLOCKS.criticalLevel)) && (Number($scope.alertConfiguration.configurableAlerts.HDFS_UTILIZATION.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.HDFS_UTILIZATION.criticalLevel))) {
				$scope.displayMsgBox('Failure', "Please check Cluster Configurations!");
				$scope.selectedTab = 'alertTab';
				$scope.dISK_SPACE_UTILIZATIONFalse = true;
				$scope.uNDER_REPLICATED_BLOCKSFalse = true;
				$scope.hDFS_UTILIZATIONFalse = true;
				return;
			}
			if ( ( Number($scope.alertConfiguration.configurableAlerts.DISK_SPACE_UTILIZATION.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.DISK_SPACE_UTILIZATION.criticalLevel) ) && ( Number($scope.alertConfiguration.configurableAlerts.UNDER_REPLICATED_BLOCKS.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.UNDER_REPLICATED_BLOCKS.criticalLevel))) {
				$scope.displayMsgBox('Failure', "Please check Cluster Configurations!");
				$scope.selectedTab = 'alertTab';
				$scope.dISK_SPACE_UTILIZATIONFalse = true;
				$scope.uNDER_REPLICATED_BLOCKSFalse = true;
				return;
			}
			if ( ( Number($scope.alertConfiguration.configurableAlerts.DISK_SPACE_UTILIZATION.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.DISK_SPACE_UTILIZATION.criticalLevel) ) && (Number($scope.alertConfiguration.configurableAlerts.HDFS_UTILIZATION.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.HDFS_UTILIZATION.criticalLevel))) {
				$scope.displayMsgBox('Failure', "Please check Cluster Configurations!");
				$scope.selectedTab = 'alertTab';
				$scope.dISK_SPACE_UTILIZATIONFalse = true;
				$scope.hDFS_UTILIZATIONFalse = true;
				return;
			}
			if ( ( Number($scope.alertConfiguration.configurableAlerts.UNDER_REPLICATED_BLOCKS.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.UNDER_REPLICATED_BLOCKS.criticalLevel)) && (Number($scope.alertConfiguration.configurableAlerts.HDFS_UTILIZATION.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.HDFS_UTILIZATION.criticalLevel))) {
				$scope.displayMsgBox('Failure', "Please check Cluster Configurations!");
				$scope.selectedTab = 'alertTab';
				$scope.uNDER_REPLICATED_BLOCKSFalse = true;
				$scope.hDFS_UTILIZATIONFalse = true;
				return;
			}
			if ( Number($scope.alertConfiguration.configurableAlerts.DISK_SPACE_UTILIZATION.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.DISK_SPACE_UTILIZATION.criticalLevel) ) {
				$scope.displayMsgBox('Failure', "Please check Cluster Configurations!");
				$scope.selectedTab = 'alertTab';
				$scope.dISK_SPACE_UTILIZATIONFalse = true;
				return;
			}
			if ( Number($scope.alertConfiguration.configurableAlerts.UNDER_REPLICATED_BLOCKS.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.UNDER_REPLICATED_BLOCKS.criticalLevel)) {
				$scope.displayMsgBox('Failure', "Please check Cluster Configurations!");
				$scope.selectedTab = 'alertTab';
				$scope.uNDER_REPLICATED_BLOCKSFalse = true;
				return;
			}
			if (Number($scope.alertConfiguration.configurableAlerts.HDFS_UTILIZATION.warningLevel) >= Number($scope.alertConfiguration.configurableAlerts.HDFS_UTILIZATION.criticalLevel)) {
				$scope.displayMsgBox('Failure', "Please check Cluster Configurations!");
				$scope.selectedTab = 'alertTab';
				$scope.hDFS_UTILIZATIONFalse = true;
				return;
			}

			$scope.emailConfiguration = setEmailAuthReverse($scope.emailConfiguration);

			adminConfiguration['emailConfiguration'] = $scope.emailConfiguration;
			adminConfiguration['haConfiguration'] = $scope.haConfiguration;
			adminConfiguration['influxDBConfiguration'] = $scope.influxDBConfiguration;

			if (isStringNullOrEmpty($scope.ticketConfiguration.host) || isStringNullOrEmpty($scope.ticketConfiguration.port) || isStringNullOrEmpty($scope.ticketConfiguration.username) || isStringNullOrEmpty($scope.ticketConfiguration.password) || isStringNullOrEmpty($scope.ticketConfiguration.formName)) {
				adminConfiguration['ticketConfiguration'] = { 'enable': false }
			} else {
				adminConfiguration['ticketConfiguration'] = $scope.ticketConfiguration;
				adminConfiguration['ticketConfiguration'].enable = true;
			}
			var str = $scope.alertConfiguration['hdfsDirPaths'];
			if($.isArray(str)) {
				$scope.alertConfiguration['hdfsDirPaths'] = str;
			} else {
				if ( str != undefined ) {
					var res = str.split(",");
					$scope.alertConfiguration['hdfsDirPaths'] = res;
				} else {
					$scope.alertConfiguration['hdfsDirPaths'] = [];
				}
			}

			adminConfiguration['alertConfiguration'] = $scope.alertConfiguration;
			adminConfiguration['alertActionConfiguration'] = $scope.alertActionConfiguration;
			adminConfiguration['slaConfigurations'] = $scope.slaConfigurations;
			adminConfiguration['backgroundProcessConfiguration'] = $scope.backgroundProcessConfiguration;

			clusterConfigurationFactory.saveData.post({}, adminConfiguration,
				function(data) {
					displaySaveMsgBox('Success', "Cluster Configurations saved successfully!!!");
				},
				function(e) {
					$scope.displayMsgBox('Failure', e);
					console.log(e);
				});

		};

		/** Start : Function to display message for cluster configuration */
		$scope.saveSuccess = false;
		$scope.displaySaveBlock = false;
		$scope.displayBlock = false;
		$scope.displayMsgBox = function(type, messageString) {
			if (type == 'Success') {
				$scope.saveSuccess = true;
			}
			$scope.displayBlock = true;
			$scope.blockMessage = "Problem while saving the configurations.";
			$timeout(function() {
				$scope.displayBlock = false;
				$scope.blockMessage = "";
			}, 3000);
		}

		function displaySaveMsgBox(type, messageString) {
			if (type == 'Success') {
				$scope.saveSuccess = true;
			}
			$scope.displaySaveBlock = true;
			$scope.blockMessage = messageString;
			$timeout(function() {
				$scope.displaySaveBlock = false;
				$scope.blockMessage = "";
				if ( common.getAcTabDetail() == true ) {
					$location.path('/analyze-cluster');
					common.setAcTabDetail(null);
				} else {
					$location.path('/index');
				}

			}, 3000);
		}
		/** End : Function to display message for cluster configuration */

	}
]);