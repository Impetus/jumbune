/* Cluster Controller controller */
'use strict';
angular.module('addCluster.ctrl', [])

.controller('AddClusterController', ['$scope', '$rootScope', '$routeParams', '$location', 'common', '$timeout', 'AddClusterFactory', 'updateClusterFactory', 'editClusterFactory', 'getClusterDataFactory', 'getTotalClusterNodeFactory', 'getIsMaprDistributionFactory', 


    function($scope, $rootScope, $routeParams, $location, common, $timeout, AddClusterFactory, updateClusterFactory, editClusterFactory, getClusterDataFactory,getTotalClusterNodeFactory, getIsMaprDistributionFactory) {

        $scope.setProfilingObj = {};
        $scope.toBeDeleted = false;
        $scope.currCluster;
        $scope.select = true;
        $scope.submitForm = false;
        $scope.disableClusterName = false;
        $scope.dataQualityGroup = common.dataQualityGroup;

        angular.element('#clusterNameId').trigger('focus');
        $("div.my-tool-tip").tooltip();
        $("td.my-tool-tip").tooltip();

        $scope.workerHostArr = [];
        $scope.workerNodeHostArrCount = [1];
        $scope.selectedDN = "false";
        $scope.agentPortArr = [];
        $scope.nameNodeHostArr = [];
        $scope.nodeHostArrCopy = [];
        $scope.nameNodeHostArrCount = [1];
        $scope.agentHostPortCount = [1];
        $scope.isMapr = common.getIsMapr();

        getIsMaprDistributionFactory.isMaprDistribution({},
                function(data) {
                    var isMapr = data.isMapr;
                    common.setIsMapr(isMapr);
                    $scope.isMapr = isMapr;
		    		$scope.isEmr = data.isEmr;
                },
                function(e) {
                    console.log(e);
        });

        /** create hosts fields in worker node */
        $scope.addWorkerNodeHost = function() {
            $scope.workerNodeHostArrCount.push($scope.workerNodeHostArrCount.length + 1);
        };

        /** remove hosts fields in worker node */
        $scope.removeWorkerNodeHost = function(index) {
            $scope.workerNodeHostArrCount.splice(index, 1);

            if ($scope.workerHostArr[index]){                
                $scope.workerHostArr.splice(index, 1);
            }
        };
       /** create name node hosts when HA is enable */
        $scope.addNameNodeHost = function() {
            $scope.nameNodeHostArrCount.push($scope.nameNodeHostArrCount.length + 1);
            $scope.agentHostPortCount.push($scope.nameNodeHostArrCount.length + 1);
        };
        /** remove  name node hosts when HA is enable */
        $scope.removeNameNodeHost = function(index) {
            $scope.nameNodeHostArrCount.splice(index, 1);
            $scope.agentHostPortCount.splice(index, 1);
            $scope.setProfilingObj.hostMNArr.splice(index, 1);
            $scope.nodeHostArrCopy.splice(index, 1);
            if ($scope.nameNodeHostArr[index]) {
                //$scope.nameNodeHostArr.splice(index,1);
            }
        };
        /** Function to generate port fields in jumbune agent  */
        $scope.addAgentHostPort = function() {
            $scope.agentHostPortCount.push($scope.agentHostPortCount.length + 1);
            for (var i = 0; i < $scope.agentHostPortCount.length; i++) {
                //$scope.nodeHostArrCopy[i] = $scope.setProfilingObj.hostMNArr[0];
            }
        };
        /** Function to remove port fields in jumbune agent  */
        $scope.removeAgentHostPort = function(index) {
            $scope.agentHostPortCount.splice(index, 1);
            $scope.nodeHostArrCopy.splice(index, 1);
            $scope.agentPortArr.splice(index, 1);
        };

        /*   $scope.$watch('nameNodeHostArr',function(newVal,oldVal){
               if($scope.currClusterDetail.clusterMode != 'edit') {
                   if ((newVal !== undefined) && (newVal !== oldVal)) {
                       angular.forEach(newVal, function(valueReal,keyReal) {
                           if(valueReal != '') {
                               $scope.nodeHostArrCopy[keyReal] = valueReal;
                               $scope.typedFromReal = true;
                           }
                       }); 
                   }
               }
           },true);*/

        $scope.$watch('setProfilingObj.enableHA', function(newVal, oldVal) {
            if (!newVal && oldVal) {
                $scope.typedFromReal = false;
                $scope.nameNodeHostArrCount = [1];
                $scope.agentHostPortCount = [1];
                $scope.nameNodeHostArr = [];
                //$scope.nodeHostArrCopy = [];
                $scope.agentPortArr = [];
                var baseVal = angular.copy($scope.setProfilingObj.hostMNArr[0]);
                $scope.setProfilingObj.hostMNArr = angular.copy([]);
                $scope.setProfilingObj.hostMNArr[0] = baseVal;
                $scope.nodeHostArrCopy[0] = baseVal;
                $scope.zkHostPortArr = [{ "host": "", "port": "2181" }];
            }
        });

        $scope.$watch('setProfilingObj.hostMNArr', function(newVal, oldVal) {
            angular.forEach($scope.setProfilingObj.hostMNArr, function(valueReal, keyReal) {
                if (valueReal != '') {
                    $scope.nodeHostArrCopy[keyReal] = valueReal;
                    $scope.typedFromReal = true;
                }
            });
        }, true);

        $scope.$watch('setProfilingObj.enableAgentHA', function(newVal, oldVal) {
            if (!newVal && oldVal) {
                $scope.agentHostPortCount = angular.copy($scope.nameNodeHostArrCount);
                //$scope.nodeHostArrCopy = angular.copy($scope.nameNodeHostArr);
                $scope.agentPortArr = [];

            }
        });

        $scope.$watch('selClusterObj.userMN', function(newVal, oldVal) {
            var user = 'user';
            if (newVal !== oldVal)
                user = newVal;
            $scope.selClusterObj.fileTypeMN = '/home/' + user + '/.ssh/id_rsa';

        });

        $scope.$watch('selClusterObj.userNameTaskMngr', function(newVal, oldVal) {
            var user = 'user';
            if (newVal !== oldVal)
                user = newVal;
            $scope.selClusterObj.fileTaskTypeMN = '/home/' + user + '/.ssh/id_rsa';
        });

        /*$scope.$watch('setProfilingObj.agentUserMN', function(newVal, oldVal) {
            var user = 'user';
            if (newVal !== oldVal)
                user = newVal;
            $scope.selClusterObj.fileAgentTypeMN = '/home/' + user + '/.ssh/id_rsa';
        });*/
        
        /** Add and remove zk host and port */
        $scope.zkHostPortObj = {
            "host": "",
            "port": "2181"
        };
        $scope.zkHostPortArr = [{
            "host": "",
            "port": "2181"
        }];

        $scope.addZkHostPort = function() {
            $scope.zkHostPortArr.push(angular.copy($scope.zkHostPortObj));
            var basePort = $scope.zkHostPortArr[0].port;

            angular.forEach($scope.zkHostPortArr, function(value, key) {
                if (key > 0) {
                    if (value.port.length < 1)
                        value.port = basePort;
                }
            });
        };
        $scope.removeZkHostPort = function(index) {
            $scope.zkHostPortArr.splice(index, 1);
        };
        /** END */

        /** Goes to index page */
        $scope.cancel = function() {
            $location.path('/index');
        };
        /** Shows error message */
        $scope.hasError = function(fieldName) {           
            var error = ($scope.clusterForm[fieldName].$invalid && !$scope.clusterForm[fieldName].$pristine) || ($scope.clusterForm[fieldName].$invalid && $scope.submitForm)
            return error;
        };
        /** Function validate node info */
        $scope.validateNodeInfo = function() {
            var nodeArr = $scope.selClusterObj.nodeArr;
            var errorFound = false,
                tempNode = null,
                user = "",
                host = [],
                breakInnerLoop = false;

            for (var i = 0; i < nodeArr.length; i++) {
                tempNode = nodeArr[i];
                breakInnerLoop = false;

                /* user = tempNode.userDN;
                 if(!user){
                     errorFound = true;
                     break;
                 }*/

                //host = tempNode.hostDN;
                if (tempNode.selectedDN && (!tempNode.hostRangeFrom || !tempNode.hostRangeTo)) {
                    /* errorFound = true;
                     break;*/
                }

                /*if(!tempNode.selectedDN && !host.length){
                    errorFound =  true;
                    break;
                }*/

                /*if(!tempNode.selectedDN && host.length){
                    for(var j=0; j<host.length; j++){
                        if(!(host[j].host)) {
                           errorFound = true;
                           breakInnerLoop = true;
                            break;
                        }
                    }

                    if(breakInnerLoop) {
                        break;
                    }
                }*/
            }
            return !errorFound;
        };

        /** Function saves cluster information and redirected to cluster configuration wizard  */
        $scope.saveClusConf = function() {
            var obj = $scope.getObjectForJSON();
            var convertJson = $scope.convertJson(angular.copy(obj));
            var isNodeInfoValid = $scope.validateNodeInfo();
            if ($scope.clusterName && $scope.selClusterObj.workDirectoryDN && isNodeInfoValid) {
                if (common.setClusterName($scope.clusterName)) {
                    common.setFieldCaption($scope.clusterName, convertJson);
                    var object = common.getFieldCaption($scope.clusterName);

                    var object1 = angular.extend({}, convertJson)

                    var t = angular.copy(object1);

                    if ($scope.currClusterDetail.clusterMode == 'add') {
                        //Factory to add cluster
                        AddClusterFactory.submitClusterForm({}, object1,
                            function(data) {
                                common.setConfigurationData($scope.clusterName)
                                $location.path('/manage-cluster');
                            },
                            function(e) {
                                console.log(e);
                            });
                    } else {
                        //Factory to edit cluster
                        updateClusterFactory.updateCluster({}, object1,
                            function(data) {
                                common.setConfigurationData($scope.clusterName)
                                $location.path('/manage-cluster');
                            },
                            function(e) {
                                console.log(e);
                            });
                    }

                } else {
                    $scope.displayMsgBox('Failure', "Cluster name already exists!");
                }
            }
            common.setClusterConfig($scope.selClusterObj);
        }

        /** Function to save cluster information */
        $scope.save = function() {
            var obj = $scope.getObjectForJSON();
            var convertJson = $scope.convertJson(angular.copy(obj));
            var isNodeInfoValid = $scope.validateNodeInfo();
            if ($scope.clusterName && $scope.selClusterObj.workDirectoryDN && isNodeInfoValid) {
                if (common.setClusterName($scope.clusterName)) {
                    common.setFieldCaption($scope.clusterName, convertJson);
                    var object = common.getFieldCaption($scope.clusterName);

                    var object1 = angular.extend({}, convertJson)

                    var t = angular.copy(object1);

                    if ($scope.currClusterDetail.clusterMode == 'add') {
                        //Factory to add cluster
                        AddClusterFactory.submitClusterForm({}, object1,
                            function(data) {
                                $scope.displaySaveMsgBox('Success', "Cluster saved successfully!!!");
                            },
                            function(e) {
                                console.log(e);
                            });
                    } else {
                        //Factory to edit cluster
                        updateClusterFactory.updateCluster({}, object1,
                            function(data) {
                                $scope.displaySaveMsgBox('Success', "Cluster updated successfully!!!");
                            },
                            function(e) {
                                console.log(e);
                            });
                    }


                } else {
                    $scope.displayMsgBox('Failure', "Cluster name already exists!");
                }
            }
            common.setClusterConfig($scope.selClusterObj);

        };

        /** Function to use convert json in the form of matching key with expected data */
        $scope.convertJson = function(obj) {
            var nameNodeHostArr = [];
            var hasPasswordNN = null;
            var hasFileTyeNN = null;

            var hasPasswordTM = null;
            var hasFileTyeTM = null;

            var hasPasswordAgent = null;
            var hasFileTyeAent = null;

            var jmxPluginEnabledRadio = null;
            var jmxPortMNOBJ = null;
            var jmxPortTaskMngrOBJ = null;
            var jmxPortDNOBJ = null;
            var jmxPortTT_DNOBJ = null;


            if ($scope.setProfilingObj.jmxPluginEnabled) {
                jmxPluginEnabledRadio = obj.jmxPluginEnabled;

            } else {
                jmxPortMNOBJ = obj.jmxPortMN;
                jmxPortTaskMngrOBJ = obj.jmxPortTaskMngr;
                jmxPortDNOBJ = obj.jmxPortDN;
                jmxPortTT_DNOBJ = obj.jmxPortTT_DN;
            }

            if ($scope.setProfilingObj.enableHA) {
                nameNodeHostArr = angular.copy(obj.nameNodeHostMN);

            } else {
                nameNodeHostArr = angular.copy(obj.hostMNArr);

            }
            //aunthenication
            if (!$scope.setProfilingObj.enableDataProfiling) {
                hasFileTyeNN = obj.fileTypeMN;

            } else {
                hasPasswordNN = obj.masterNodePassword;

            }
            if (!$scope.setProfilingObj.taskManagerPaswd) {
                hasFileTyeTM = obj.fileTaskTypeMN;
            } else {
                hasPasswordTM = obj.taskManagerPswd;
            }
            if ($scope.setProfilingObj.agentInfoPaswd) {
                hasFileTyeAent = obj.fileAgentTypeMN;
            } else {
                hasPasswordAgent = obj.agentPassword;
            }
            var jsonForServer = {
                "zks": obj.hostZKHostPort,
                "clusterName": obj.clusterName,
                "jmxPluginEnabled": jmxPluginEnabledRadio,
                "nameNodes": {
                    "nameNodeJmxPort": jmxPortMNOBJ,
                    "hosts": nameNodeHostArr,
                    "haEnabled": obj.enableHA
                },
                "hadoopUsers": {
                    "hasSingleUser": obj.enableHadoopUser
                },
                "agents": {
                    "user": obj.agentUserMN,
                    "agents": obj.agentHostPortName,
                    "hasPasswordlessAccess": obj.agentInfoPaswd,
                    "password": hasPasswordAgent,
                    "sshAuthKeysFile": hasFileTyeAent,
                    "haEnabled": obj.enableAgentHA
                },
                "hostRangeFromValue": obj.hostRangeFrom,
                "hostRangeToValue": obj.hostRangeTo,
                "enableHostRange": obj.selectedDN,
                "taskManagers": {
                    "taskManagerJmxPort": jmxPortTaskMngrOBJ,
                    "user": obj.userNameTaskMngr,
                    "hosts": obj.taskHostArr
                },
                "workers": {
                    "hosts": obj.hostDN,
                    "workDirectory": obj.workDirectoryDN,
		            "spotInstances": obj.spotInstances,
                    "user": obj.userWN,
                    "dataNodeJmxPort": jmxPortDNOBJ,
                    "taskExecutorJmxPort": jmxPortTT_DNOBJ
                }
            }
            return jsonForServer;
        }

       /* function totalNodesFactory() {
             
        }*/


         /** Function to check duplicates ips in workernode host array */
        function checkDuplicateIps(arr) {
            var map = {};
/*            for (var value of arr) {
                map[value] = 1;
            }*/
            for (var i = 0; i < arr.length; i++) {
                map[arr[i]] = 1;
            }
            if (Object.keys(map).length < arr.length) {
                return true;
            } else {
                return false;
            }
        }
        $scope.IPDuplicateFlag = false;
        $scope.removeBlankFlag = false;
        $scope.totalNodeFlag = false;
        var getNodeSize = common.getNodeSize();

        /** Function to get object (modals) for json */
        $scope.getObjectForJSON = function() {

            var agentHostPort = [];
            var port;

            angular.forEach($scope.nodeHostArrCopy, function(value, key) {
                port = $scope.agentPortArr[key];
                if (value) {
                    agentHostPort.push({ "host": value, "port": port })
                }
            });
            /*for (var indexVal =0 ; indexVal < $scope.workerHostArr.length ; indexVal++) {
                if ( $scope.workerHostArr[indexVal] == null || $scope.workerHostArr[indexVal] == undefined) {
                    $scope.removeBlankFlag = true;
                    return;
                }

            }*/
            var duplicateIP = checkDuplicateIps($scope.workerHostArr);
            if (duplicateIP == true) {
                $scope.IPDuplicateFlag = true;
                return;
            } else {
                $scope.IPDuplicateFlag = false;
            }

            getTotalClusterNodeFactory.getTotalNodesList(
                     {},
                    function(data) {
                        var totalclustersaddedtill = data[0]
                        var str = getNodeSize['Cluster Size'];
                        var totalNodesAllowed = str.substring(0, str.lastIndexOf(" "));
                        var totalCluster = Number(totalNodesAllowed) - Number(totalclustersaddedtill);
                        //var totalNodesToBEallow = Number(totalclustersaddedtill) + Number(totalCluster+1);
                        //if ( totalNodesToBEallow > totalNodesAllowed) {
                        if ( totalNodesAllowed < $scope.workerNodeHostArrCount.length) {
                            $scope.totalNodeFlag = true;
                        } else {
                            $scope.totalNodeFlag = false;
                        }
                    },
                    function(e) {});
            
            var getObject = {
                "clusterName": $scope.clusterName,
                "userMN": $scope.selClusterObj.userMN,
                "hostMNArr": $scope.setProfilingObj.hostMNArr,
                "fileTypeMN": $scope.selClusterObj.fileTypeMN,
                "fileAgentTypeMN": $scope.selClusterObj.fileAgentTypeMN,
                "userNameTaskMngr": $scope.selClusterObj.userNameTaskMngr,
                "jmxPortTaskMngr": $scope.selClusterObj.jmxPortTaskMngr,
                "fileTaskTypeMN": $scope.selClusterObj.fileTaskTypeMN,
                "taskManagerPswd": $scope.selClusterObj.taskManagerPswd,
                "taskHostArr": $scope.setProfilingObj.taskHostArr,
                "userWN": $scope.selClusterObj.userWN,
                "enableHA": $scope.setProfilingObj.enableHA,
                "jmxPluginEnabled": $scope.setProfilingObj.jmxPluginEnabled,
                "enableAgentHA": $scope.setProfilingObj.enableAgentHA,
                "enableDataProfiling": $scope.setProfilingObj.enableDataProfiling,
                "enableHadoopUser": $scope.setProfilingObj.enableHadoopUser,
                "agentInfoPaswd": $scope.setProfilingObj.agentInfoPaswd,
                "taskManagerPaswd": $scope.setProfilingObj.taskManagerPaswd,
                "hostZKHostPort": $scope.zkHostPortArr,
                "nameNodeHostMN": $scope.nameNodeHostArr,
                "agentHostPortName": agentHostPort,
                "masterNodePassword": $scope.selClusterObj.masterNodePassword,
                "agentPassword": $scope.selClusterObj.agentPassword,
                "agentUserMN": $scope.setProfilingObj.agentUserMN,
                "agentHostMN": $scope.selClusterObj.agentHostMN,
                "jmxPortMN": $scope.selClusterObj.jmxPortMN,
                "jobTrackerPortMN": $scope.selClusterObj.jobTrackerPortMN,
                "workDirectoryDN": $scope.selClusterObj.workDirectoryDN,
 		        "spotInstances": $scope.spotInstances,
                "jmxPortDN": $scope.selClusterObj.jmxPortDN,
                "jmxPortTT_DN": $scope.selClusterObj.jmxPortTT_DN,
                "uniqueUsersDN": $scope.selClusterObj.uniqueUsersDN,
                "userDN": $scope.selClusterObj.userDN,
                "hostDN": $scope.workerHostArr,
                "selectedDN": $scope.selectedDN,
                "hostRangeFrom": $scope.hostRangeFrom,
                "hostRangeTo": $scope.hostRangeTo
            };
            return getObject;
        };
        /** init function */
        $scope.init = function() {
            $scope.setProfilingObj = {
                "enableDataProfiling": false,
                "agentInfoPaswd": true,
                "jmxPluginEnabled": true,
                "taskManagerPaswd": false,
                "enableHA": false,
                "enableAgentHA": false,
                "user": '',
                "taskHostArr": [],
                "hostMNArr": []
            }

            $scope.defaultNodeObj = {
                "userDN": "",
                "hostDN": [{
                    "host": ""
                }]
            };
            $scope.selectedDN = 'FALSE';

            $scope.selClusterObj = {};
            $scope.selClusterObj.nodeArr = [];
            $scope.selClusterObj.nodeArr.push(angular.copy($scope.defaultNodeObj));
            $scope.selClusterObj.uniqueUsersDN = 1;
            $scope.selClusterObj.copyFromMaster = false;
            $scope.currClusterDetail = common.getClusterMode();
            if ($scope.currClusterDetail.clusterMode === 'edit') {
                $scope.disableClusterName = true;
                getClusterDataFactory.getClusterForm(
                    //{requestType : 'DELETE'clusterName : 'key'}, 
                    { clusterName: $scope.currClusterDetail.currCluster }, {},
                    function(data) {
                        setEditData(angular.copy(data));
                    },
                    function(e) {});
            }
        };
        /** Edit cluster function  */
        function setEditData(clusterObject) {
            if (clusterObject.nameNodes.hasPasswordlessAccess !== undefined) {
                $scope.setProfilingObj.enableDataProfiling = clusterObject.nameNodes.hasPasswordlessAccess;
            }
            if (clusterObject.agents.hasPasswordlessAccess !== undefined) {
                $scope.setProfilingObj.agentInfoPaswd = clusterObject.agents.hasPasswordlessAccess;
            }
            if (clusterObject.taskManagers.hasPasswordlessAccess !== undefined) {
                $scope.setProfilingObj.taskManagerPaswd = clusterObject.taskManagers.hasPasswordlessAccess;
            }
            if (clusterObject.hadoopUsers.hasSingleUser !== undefined) {
                $scope.setProfilingObj.enableHadoopUser = clusterObject.hadoopUsers.hasSingleUser;
            }
            if (clusterObject.jmxPluginEnabled !== undefined) {
                $scope.setProfilingObj.jmxPluginEnabled = clusterObject.jmxPluginEnabled;
            }
            if (clusterObject != undefined) {
                if (angular.isDefined(clusterObject.clusterName)) {
                    $scope.clusterName = clusterObject.clusterName;
                }
                if (angular.isDefined(clusterObject.zks)) {
                    $scope.zkHostPortArr = clusterObject.zks;
                }
                //NN
                if (angular.isDefined(clusterObject.nameNodes.hosts)) {

                    $scope.nameNodeHostArr = clusterObject.nameNodes.hosts;
                    for (var i = 2; i <= $scope.nameNodeHostArr.length; i++) {
                        $scope.nameNodeHostArrCount.push(i);
                    }
                }
                if (angular.isDefined(clusterObject.nameNodes.user)) {
                    $scope.selClusterObj.userMN = clusterObject.nameNodes.user;
                }
                if (angular.isDefined(clusterObject.nameNodes.hosts)) {
                    $scope.setProfilingObj.hostMNArr = clusterObject.nameNodes.hosts;
                }
                if (angular.isDefined(clusterObject.nameNodes.sshAuthKeysFile)) {
                    $scope.selClusterObj.fileTypeMN = clusterObject.nameNodes.sshAuthKeysFile;
                }
                if (angular.isDefined(clusterObject.nameNodes.password)) {
                    $scope.selClusterObj.masterNodePassword = clusterObject.nameNodes.password;
                }
                if (angular.isDefined(clusterObject.nameNodes.nameNodeJmxPort)) {
                    $scope.selClusterObj.jmxPortMN = clusterObject.nameNodes.nameNodeJmxPort;
                }
                if (angular.isDefined(clusterObject.nameNodes.haEnabled)) {
                    $scope.setProfilingObj.enableHA = clusterObject.nameNodes.haEnabled;
                }
                //Task Manager
                if (angular.isDefined(clusterObject.taskManagers.hosts)) {
                    $scope.setProfilingObj.taskHostArr = clusterObject.taskManagers.hosts;
                }
                if (angular.isDefined(clusterObject.taskManagers.user)) {
                    $scope.selClusterObj.userNameTaskMngr = clusterObject.taskManagers.user;
                }
                if (angular.isDefined(clusterObject.taskManagers.sshAuthKeysFile)) {
                    $scope.selClusterObj.fileTaskTypeMN = clusterObject.taskManagers.sshAuthKeysFile;
                }
                if (angular.isDefined(clusterObject.taskManagers.password)) {
                    $scope.selClusterObj.taskManagerPswd = clusterObject.taskManagers.password;
                }
                if (angular.isDefined(clusterObject.taskManagers.taskManagerJmxPort)) {
                    $scope.selClusterObj.jmxPortTaskMngr = clusterObject.taskManagers.taskManagerJmxPort;
                }
                //worker Node
                if (angular.isDefined(clusterObject.workers.workDirectory)) {
                    $scope.selClusterObj.workDirectoryDN = clusterObject.workers.workDirectory;
                }
		if (angular.isDefined(clusterObject.workers.spotInstances)) {
                    $scope.spotInstances = clusterObject.workers.spotInstances;
                }
                if (angular.isDefined(clusterObject.workers.dataNodeJmxPort)) {
                    $scope.selClusterObj.jmxPortDN = clusterObject.workers.dataNodeJmxPort;
                }
                if (angular.isDefined(clusterObject.workers.taskExecutorJmxPort)) {
                    $scope.selClusterObj.jmxPortTT_DN = clusterObject.workers.taskExecutorJmxPort;
                }
                if (angular.isDefined(clusterObject.workers.user)) {
                    $scope.selClusterObj.userWN = clusterObject.workers.user;
                }

                if (angular.isDefined(clusterObject.workers.hosts)) {

                    $scope.workerHostArr = clusterObject.workers.hosts;
                    for (var i = 2; i <= $scope.workerHostArr.length; i++) {
                        $scope.workerNodeHostArrCount.push(i);
                    }

                }
                //for range from - to
                if (angular.isDefined(clusterObject.hostRangeFromValue)) {
                    $scope.hostRangeFrom = clusterObject.hostRangeFromValue;
                }

                if (angular.isDefined(clusterObject.hostRangeToValue)) {
                    $scope.hostRangeTo = clusterObject.hostRangeToValue;
                }

                if (angular.isDefined(clusterObject.enableHostRange)) {
                    $scope.selectedDN = clusterObject.enableHostRange;
                    //$scope.selectedDN = new Boolean(clusterObject.enableHostRange);

                }
                if (angular.isDefined(clusterObject.agents.agents)) {
                    angular.forEach(clusterObject.agents.agents, function(value, key) {
                        if (value.host) {
                            $scope.nodeHostArrCopy[key] = value.host;
                            $scope.agentPortArr[key] = value.port;
                        }
                    });
                    for (var i = 2; i <= $scope.nodeHostArrCopy.length; i++) {
                        $scope.agentHostPortCount.push(i);
                    }

                }

                if (angular.isDefined(clusterObject.agents.user)) {
                    $scope.setProfilingObj.agentUserMN = clusterObject.agents.user;
                }
                if (angular.isDefined(clusterObject.agents.sshAuthKeysFile)) {
                    $scope.selClusterObj.fileAgentTypeMN = clusterObject.agents.sshAuthKeysFile;
                }
                if (angular.isDefined(clusterObject.agents.password)) {
                    $scope.selClusterObj.agentPassword = clusterObject.agents.password;
                }
                if (angular.isDefined(clusterObject.agents.haEnabled)) {
                    $scope.setProfilingObj.enableAgentHA = clusterObject.agents.haEnabled;
                }
            }
        }

        $scope.selectCluster = function(cluster) {
            $scope.currCluster = cluster;
            $scope.select = true;

            var clusterObj = common.getFieldCaption(cluster);
            $scope.userMN_D = clusterObj.userMN;
            //$scope.hostMN_D = clusterObj.hostMN;
            $scope.fileTypeMN_D = clusterObj.fileTypeMN;
            $scope.agentPortMN_D = clusterObj.agentPortMN;
            $scope.jmxPortMN_D = clusterObj.jmxPortMN;
            $scope.jobTrackerPortMN_D = clusterObj.jobTrackerPortMN;
        };

        /** Generate fields in cluster */
        $scope.generateFields = function(fields) {
            for (var i = 1; i <= fields; i++) {
                if (i > $scope.selClusterObj.nodeArr.length) {
                    $scope.selClusterObj.nodeArr.push(angular.copy($scope.defaultNodeObj))
                }
            }
        };

        /*  $scope.criteriaBased = function(param){
                $scope.startCriteria = false;
                if($scope.selProfilingObj.enableDataProfiling === 'withCriteria'){
                        $scope.startCriteria = true;
                }            
            };*/
        /*  $scope.resetDataNodes = function() {
              angular.forEach($scope.selClusterObj.nodeArr, function(value,key) {
                  value.userDN = "";
                  angular.forEach(value.hostDN, function(valueChild,keyChild) {
                      valueChild.host = "";
                  });
              });
          };*/

        /*  $scope.setNodeFromMaster = function() {
              if($scope.selClusterObj.copyFromMaster) {
                  angular.forEach($scope.selClusterObj.nodeArr, function(value,key) {
                          value.userDN = $scope.selClusterObj.userMN;
                      angular.forEach(value.hostDN, function(valueChild,keyChild) {
                          //valueChild.host = $scope.selClusterObj.hostMN;
                          valueChild.host = $scope.setProfilingObj.hostMNArr;
                      });
                  });
              } else {
                  $scope.resetDataNodes();
              }
          };*/

        /*$scope.generateHostFields = function(index){
             $scope.selClusterObj.nodeArr[index].hostDN.push({"host" : ""});
        };

         $scope.removeHostFields = function(nodeKey,hostKey) {
             $scope.selClusterObj.nodeArr[nodeKey].hostDN.splice(hostKey,1);
         };*/

         /** Display messages on save or update the cluster */
        $scope.saveSuccess = false;
        $scope.displaySaveBlock = false;
        $scope.displayMsgBox = function(type, messageString) {
            if (type == 'Success') {
                $scope.saveSuccess = true;
            }
            $scope.displayBlock = true;
            $scope.blockMessage = messageString;
            $timeout(function() {
                $scope.displayBlock = false;
                $scope.blockMessage = "";
            }, 3000);
        }
        $scope.displaySaveMsgBox = function(type, messageString) {
            if (type == 'Success') {
                $scope.saveSuccess = true;
            }
            $scope.displaySaveBlock = true;
            $scope.blockMessage = messageString;
            $timeout(function() {
                $scope.displaySaveBlock = false;
                $scope.blockMessage = "";
                $location.path('/index');
            }, 3000);
        }
        /** End */
    }
]);
