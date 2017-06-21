/* Cluster Controller controller */
'use strict';
angular.module('cluster.ctrl', [])
    
.controller('ClusterController', ['$scope', '$rootScope','$location', 'common',

    function($scope, $rootScope, $location, common) {
		
		$scope.mode = 'add';
        $scope.toBeDeleted = false;
        $scope.currCluster;
		$scope.selClusterObj = {};
        $scope.select = true;
		
        $scope.switchTab = '';

        $scope.setCurrentTab = function(tab) {
            common.setCurrentTab(tab);
            $scope.switchTab = tab;            
        };
		
        $scope.cancel = function(){
			$location.path('/cluster');	
		};
        $scope.save = function(){
            var obj = $scope.setObjectForJSON();
            common.setFieldCaption($scope.clusterName, obj);
            var object = common.getFieldCaption($scope.clusterName);
            common.setClusterName($scope.clusterName);
            $location.path('/cluster');
        };

        $scope.setObjectForJSON = function() {
            var getObject = {
				"clusterName": $scope.clusterName,
				"userMN": $scope.selClusterObj.userMN,
				"hostMN": $scope.selClusterObj.hostMN,
				"fileTypeMN": $scope.selClusterObj.fileTypeMN,
				"agentPortMN": $scope.selClusterObj.agentPortMN,
				"jmxPortMN": $scope.selClusterObj.jmxPortMN, 
				"jobTrackerPortMN": $scope.selClusterObj.jobTrackerPortMN, 
				"workDirectoryDN": $scope.selClusterObj.workDirectoryDN, 
				"jmxPortDN": $scope.selClusterObj.jmxPortDN,
				"jmxPortTT_DN": $scope.selClusterObj.jmxPortTT_DN,
				"uniqueUsersDN": $scope.selClusterObj.uniqueUsersDN,
				"userDN1": $scope.selClusterObj.userDN1,
				"hostDN1":$scope.selClusterObj.hostDN1,
				"selectedDN1": $scope.selClusterObj.selectedDN1
            };
            return getObject;
        };

        $scope.init = function() {
            $scope.clusterNameArr  = common.getClusterName();
            //set data for edit cluster
            var clusterObject = common.getFieldCaption(common.getClusterName());
            $scope.switchTab = common.getCurrentTab();
            $scope.dataQualityNameArr =common.getDQFieldCaption();
        };

        $scope.selectCluster = function(cluster){
            
            $scope.currCluster = cluster;
            $scope.select = true;
            var clusterObj = common.getFieldCaption(cluster);
            $scope.userMN_D = clusterObj.userMN;
            $scope.hostMN_D = clusterObj.hostMN;
            $scope.fileTypeMN_D = clusterObj.fileTypeMN;
            $scope.agentPortMN_D = clusterObj.agentPortMN;
            $scope.jmxPortMN_D = clusterObj.jmxPortMN;
            $scope.jobTrackerPortMN_D = clusterObj.jobTrackerPortMN;
        };

        $scope.deleteCluster = function(cluster){
            $scope.toBeDeleted = true;
            var clusterObject = common.getFieldCaption(cluster);
            if(clusterObject.length===0) {
                return;
            }else{
                for(var i=0; i< $scope.clusterNameArr.length; i++){
                    if($scope.clusterNameArr[i]===cluster){
                        $scope.clusterNameArr.splice(i,1);
                    }
                }
            }
        };
		
		$scope.setClusterModeType = function(type,name) {
            common.setClusterMode(type,name);
		};

        $scope.editCluster = function(cluster){

			$scope.setClusterModeType('edit',cluster);
			
            var clusterObject = common.getFieldCaption(cluster);
            if(clusterObject.length===0) {
                return;
            }else {
				$location.path('/add-cluster');
				var clusterObject = common.getFieldCaption(cluster);
				$scope.clusterName = clusterObject.clusterName;
            }
        };

        $scope.gotoIndex = function(){
            $location.path('/index')
        };
    }]);