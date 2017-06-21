/* jobwidget controller */
'use strict';
angular.module('jobwidget.ctrl').controller('DebuggerController', ['$scope', '$rootScope','common', '$location','$timeout', function($scope, $rootScope, common, $location,$timeout) {
	if(!$scope.debuggerTab){
		$scope.debuggerTab = {};
	}

	$scope.debuggerTab.useRegex = [];
	$scope.debuggerTab.udv = [];
	$scope.debuggerTab.indexUseRegex = null;
	$scope.debuggerTab.indexUdv = null;
	$scope.debuggerTab.enableDebChk = false;
	$scope.debuggerTab.useRegexChk = false;
	$scope.debuggerTab.usrDefChk = false;
	$scope.debuggerTab.disableRegex = false;
	$scope.debuggerTab.disableUdv = false;
	$scope.debuggerTab.updateRegex = false;
	$scope.debuggerTab.selectedTab = 'regex';
	$scope.debuggerTab.enableLoggingOfUnmatched = false;
    $scope.debuggerTab.isValidationChecked = false;
    $scope.debuggerTab.reducerNameText= "";
	$scope.debuggerTab.regexValText = "";
	$scope.debuggerTab.regexKeyText="";
	$scope.debuggerTab.regexType = 'add';
	$scope.debuggerTab.userType = 'add';

    $scope.debuggerTab.addUserRegexClick = function(){
		$scope.debuggerTab.indexUseRegex=null;
		$scope.debuggerTab.regexType = 'add';
		$scope.debuggerTab.reducerNameText= "";
		$scope.debuggerTab.regexValText = "";
		$scope.debuggerTab.regexKeyText="";
	    $(".use-regex-rows").removeClass("active");
	    //angular.element("#regexInputboxWrapper").css({"display":"block"}); 
	    $scope.debuggerTab.hideRegexData();
    };
    $scope.debuggerTab.addUdvClick = function(){
	    $scope.debuggerTab.indexUdv=null;
	    $scope.debuggerTab.userType = 'add';
		$scope.debuggerTab.udvReducerNameText = "";
		$scope.debuggerTab.udvValText = "";
		$scope.debuggerTab.udvKeyText = "";
    	$(".udv-rows").removeClass("active");
    	//angular.element("#udvInputboxWrapper").css({"display":"block"}); 
    	$scope.debuggerTab.hideUserData();
    };

    $scope.debuggerTab.checkValidateRecord = function(){
    	if($scope.debuggerTab.reducerNameText!="" && (($scope.debuggerTab.regexKeyText==undefined || $scope.debuggerTab.regexKeyText=="") && ($scope.debuggerTab.regexValText==undefined   || $scope.debuggerTab.regexValText=="")))
    		return true;    	
    	else if($scope.debuggerTab.reducerNameText=="")
    		return true;
    	else
    		return false;
    	
    }
	$scope.debuggerTab.showRegexInputs = function(){
    	angular.element("#regexInputboxWrapper").css({"display":"block"});    	
    	
    };
	$scope.debuggerTab.hideRegexInputboxWrapper = function(){
	 	angular.element("#regexInputboxWrapper").css({"display":"none"});	
	 	$scope.debuggerTab.indexUseRegex=null;
	 	$scope.debuggerTab.regexType = 'add';
	};

	$scope.debuggerTab.showUdvInputs = function(){
    	angular.element("#udvInputboxWrapper").css({"display":"block"});    	
    };
	$scope.debuggerTab.hideUdvInputboxWrapper = function(){
	 	angular.element("#udvInputboxWrapper").css({"display":"none"});	
	 	$scope.debuggerTab.indexUdv=null;
	    $scope.debuggerTab.userType = 'add';
	};

	$scope.debuggerTab.showRegexData = function(index){
		$scope.debuggerTab.indexUseRegex=index;
		$scope.debuggerTab.reducerNameText= $scope.debuggerTab.useRegex[index].classname;
		$scope.debuggerTab.regexValText = $scope.debuggerTab.useRegex[index].value;
		$scope.debuggerTab.regexKeyText=$scope.debuggerTab.useRegex[index].key;

    	angular.element("#regexInputboxWrapper").css({"display":"none"});    	
    	angular.element("#regexDataLabel").css({"display":"block"});    	
    };

    $scope.debuggerTab.showUserData = function(index){
		$scope.debuggerTab.indexUdv=index;
		$scope.debuggerTab.udvReducerNameText= $scope.debuggerTab.udv[index].classname;
		$scope.debuggerTab.udvValText = $scope.debuggerTab.udv[index].value;
		$scope.debuggerTab.udvKeyText=$scope.debuggerTab.udv[index].key;

    	angular.element("#udvInputboxWrapper").css({"display":"none"});    	
    	angular.element("#userDataLabel").css({"display":"block"});    	
    };

    $scope.debuggerTab.hideRegexData = function(){
    	angular.element("#regexInputboxWrapper").css({"display":"block"});    	
    	angular.element("#regexDataLabel").css({"display":"none"});    	
    };

    $scope.debuggerTab.hideUserData = function(){
    	angular.element("#udvInputboxWrapper").css({"display":"block"});    	
    	angular.element("#userDataLabel").css({"display":"none"});    	
    };



/*	$scope.debuggerTab.useRegexSetIndex = function(index){
		$scope.debuggerTab.indexUseRegex = index;	
		//$scope.debuggerTab.showRegexInputs();
		$scope.debuggerTab.showRegexData(index);
		$(".use-regex-rows").removeClass("active");
		//$("#"+$scope.debuggerTab.indexUseRegex).addClass("active");	
	};*/

/*	$scope.debuggerTab.udvSetIndex = function(index){
		$scope.debuggerTab.indexUdv = index;
		$scope.debuggerTab.showUserData(index);	
		$(".udv-rows").removeClass("active");
		$("#udv_"+$scope.debuggerTab.indexUdv).addClass("active");	
	};*/

	$scope.debuggerTab.addRegexInput = function() {
		if($scope.debuggerTab.regexType == 'add') {

			var useRow = {
				"classname":$scope.debuggerTab.reducerNameText || null,
				"value":$scope.debuggerTab.regexValText || null,
				"key":$scope.debuggerTab.regexKeyText || null
			};
			$scope.debuggerTab.useRegex.push(useRow);
		} else {
			var index = $scope.debuggerTab.indexUseRegex;
			$scope.debuggerTab.useRegex[index].classname = $scope.debuggerTab.reducerNameText;
			$scope.debuggerTab.useRegex[index].value = $scope.debuggerTab.regexValText || null;
			$scope.debuggerTab.useRegex[index].key = $scope.debuggerTab.regexKeyText || null;
		}
		$scope.debuggerTab.disableRegex = true;
		common.setRegexJson($scope.debuggerTab.useRegex);
		//if($scope.debuggerTab.indexUseRegex == null ){	
			
		//}
		$(".use-regex-rows").removeClass("active");
		$("#"+$scope.debuggerTab.index).removeClass("active");
		$scope.debuggerTab.index = null;		
		$scope.debuggerTab.hideRegexInputboxWrapper();
	};

	$scope.debuggerTab.addUdvInput = function(){
		if($scope.debuggerTab.userType == 'add') {

			var useRow =
				{
					"classname":$scope.debuggerTab.udvReducerNameText || null,
					"value":$scope.debuggerTab.udvValText || null,
					"key":$scope.debuggerTab.udvKeyText || null
				};
			
			$scope.debuggerTab.udv.push(useRow);
		} else {
			var index = $scope.debuggerTab.indexUdv;
			$scope.debuggerTab.udv[index].classname = $scope.debuggerTab.udvReducerNameText;
			$scope.debuggerTab.udv[index].value = $scope.debuggerTab.udvValText || null;
			$scope.debuggerTab.udv[index].key = $scope.debuggerTab.udvKeyText || null;
		}
		$scope.debuggerTab.disableUdv = true;
		common.setUdvRegexJson($scope.debuggerTab.udv);
		//if($scope.debuggerTab.indexUseRegex == null ){
		/*common.setUdvRegexJson(useRow);
		if($scope.debuggerTab.udvReducerNameText != ''){
			$scope.debuggerTab.udv.push(useRow);
		}*/
		
		$(".udv-rows").removeClass("active");
		$("#udv_"+$scope.debuggerTab.indexUdv).removeClass("active");
		$scope.debuggerTab.indexUdv = null;		
		$scope.debuggerTab.hideUdvInputboxWrapper();
	};


	$scope.debuggerTab.useRegexEdit = function(index){
		$scope.debuggerTab.showRegexData(index);
		var obj= $scope.debuggerTab.useRegex[index];
			$scope.debuggerTab.reducerNameText= obj.classname;
			$scope.debuggerTab.regexValText = obj.value;
			$scope.debuggerTab.regexKeyText=obj.key;
			$scope.debuggerTab.regexType = 'edit';
			$scope.debuggerTab.hideRegexData();
			$scope.debuggerTab.updateRegex = true;
			//$scope.debuggerTab.showRegexData(index);
		//$(".use-regex-rows").removeClass("active");

		
	/*	if($scope.debuggerTab.indexUseRegex!=null)
		{

			var obj= $scope.debuggerTab.useRegex[$scope.debuggerTab.indexUseRegex];
			$scope.debuggerTab.reducerNameText= obj.classname;
			$scope.debuggerTab.regexValText = obj.value;
			$scope.debuggerTab.regexKeyText=obj.key;
			$scope.debuggerTab.regexType = 'edit';
			$scope.debuggerTab.hideRegexData();
		}*/
		//$scope.debuggerTab.showRegexInputs();
	};
	
	$scope.debuggerTab.udvEdit = function(index){
		$scope.debuggerTab.showUserData(index);	
		var obj= $scope.debuggerTab.udv[index];
			$scope.debuggerTab.udvReducerNameText= obj.classname;
			$scope.debuggerTab.udvValText = obj.value;
			$scope.debuggerTab.udvKeyText=obj.key;
			//$scope.debuggerTab.showUdvInputs();
			$scope.debuggerTab.userType = 'edit';
			$scope.debuggerTab.hideUserData();
		/*if($scope.debuggerTab.indexUdv!=null) {
			var obj= $scope.debuggerTab.udv[$scope.debuggerTab.indexUdv];
			$scope.debuggerTab.udvReducerNameText= obj.classname;
			$scope.debuggerTab.udvValText = obj.value;
			$scope.debuggerTab.udvKeyText=obj.key;
			//$scope.debuggerTab.showUdvInputs();
			$scope.debuggerTab.userType = 'edit';
			$scope.debuggerTab.hideUserData();
		}*/
	};


	$scope.debuggerTab.useRegexDelete = function(index){
	/*	if($scope.debuggerTab.indexUseRegex!=null)
		{
			$scope.debuggerTab.useRegex.splice($scope.debuggerTab.indexUseRegex,1);
			$scope.debuggerTab.indexUseRegex = null;

			if($scope.debuggerTab.useRegex.length>0) {
				var obj= $scope.debuggerTab.useRegex[0];
				$scope.debuggerTab.reducerNameText= obj.classname;
				$scope.debuggerTab.regexValText = obj.value;
				$scope.debuggerTab.regexKeyText=obj.key;
			}
			common.setRegexJson($scope.debuggerTab.useRegex);
			$(".use-regex-rows").removeClass("active");
			$scope.debuggerTab.hideRegexInputboxWrapper();
			angular.element("#regexDataLabel").css({"display":"none"});
		}*/
		$scope.debuggerTab.useRegex.splice(index,1);
			index = null;

			if($scope.debuggerTab.useRegex.length>0) {
				var obj= $scope.debuggerTab.useRegex[0];
				$scope.debuggerTab.reducerNameText= obj.classname;
				$scope.debuggerTab.regexValText = obj.value;
				$scope.debuggerTab.regexKeyText=obj.key;
			}
			if($scope.debuggerTab.useRegex.length==0){
				$scope.debuggerTab.disableRegex = false;
				//$scope.debuggerTab.disableRegex = false;
			
			}
			common.setRegexJson($scope.debuggerTab.useRegex);
			$(".use-regex-rows").removeClass("active");
			$scope.debuggerTab.hideRegexInputboxWrapper();
			angular.element("#regexDataLabel").css({"display":"none"});
	};

	$scope.debuggerTab.udvDelete = function(index){
		/*if($scope.debuggerTab.indexUdv!=null) {
			$scope.debuggerTab.udv.splice($scope.debuggerTab.indexUdv,1);
			$scope.debuggerTab.indexUdv = null;
			$(".udv-rows").removeClass("active");
			$scope.debuggerTab.hideUdvInputboxWrapper();
			angular.element("#userDataLabel").css({"display":"none"}); 
		}*/
	/*	if($scope.debuggerTab.index!=null) {
			$scope.debuggerTab.udv.splice($scope.debuggerTab.index,1);
			$scope.debuggerTab.index = null;
			$(".udv-rows").removeClass("active");
			$scope.debuggerTab.hideUdvInputboxWrapper();
			angular.element("#userDataLabel").css({"display":"none"}); 
		}
		if($scope.debuggerTab.index==null){
				$scope.debuggerTab.disableUdv = false;
		}*/
		$scope.debuggerTab.udv.splice(index,1);
			index = null;

			if($scope.debuggerTab.udv.length>0) {
				var obj= $scope.debuggerTab.udv[0];
				$scope.debuggerTab.udvReducerNameText= obj.classname;
				$scope.debuggerTab.udvValText = obj.value;
				$scope.debuggerTab.udvKeyText=obj.key;
			}
			if($scope.debuggerTab.udv.length==0){
				$scope.debuggerTab.disableUdv = false;
				//$scope.debuggerTab.disableRegex = false;
			
			}
			//common.setUdvRegexJson($scope.debuggerTab.udv);
			$(".udv-rows").removeClass("active");
			$scope.debuggerTab.hideUdvInputboxWrapper();
			angular.element("#userDataLabel").css({"display":"none"});
	};   

	$scope.debuggerTab.setTabValue = function() {
		var userReg = $scope.debuggerTab.useRegexChk;
		var userDef = $scope.debuggerTab.usrDefChk;
		$scope.debuggerTab.selectedTab = 'regex';
		if((!userReg) || userDef){
			$scope.debuggerTab.selectedTab = 'udv';
		}
        //$scope.debuggerTab.handleErrorMessages();
	};

	$scope.debuggerTab.init = function () {
		$("td.my-tool-tip").tooltip();
		$scope.debuggerTab.addUserRegexClick();
		$scope.debuggerTab.addUdvClick();
		$scope.debuggerTab.regexType = 'add';

		if(common.jobMode === 'edit') {
			var jobwidget = common.getWidgetInfo();
			if(jobwidget && (jobwidget["debuggerDtl"] !== undefined)) {
				$scope.debuggerTab.enableDebChk = jobwidget["debuggerDtl"].enableDebChk;
				$scope.debuggerTab.useRegexChk = jobwidget["debuggerDtl"].useRegexChk;
				$scope.debuggerTab.usrDefChk = jobwidget["debuggerDtl"].usrDefChk ;
				$scope.debuggerTab.useRegex = jobwidget["debuggerDtl"].useRegex;
				$scope.debuggerTab.udv = jobwidget["debuggerDtl"].udv;
			}
		}
		//$scope.jobDebuggerAutoFill();
		console.log("loc param validatio" +$location.search().module);
            var searchModule = $location.search().module;
           if(searchModule) {
                 /*$scope.dataValidationTab.hdfsInputPath = null;
                $scope.dataValidationTab.tupleRecordSeparator = null;
                $scope.dataValidationTab.tupleFieldSeparator = null;
                $scope.dataValidationTab.fieldCount = null;
                $scope.dataValidationTab.fields = null;*/
                
            } else {
                 $scope.autoFillDebugger = { analyzeJob: $scope.debuggerTab.jobDebuggerAutoFill()} 
                //$scope.autoFillDebugger.searchModule;
            }

	};

	$scope.debuggerTab.jobDebuggerAutoFill = function () {
             $scope.recentJobResonse = common.getResonseData();
             $scope.debuggerTab.disableRegex = true;
			 $scope.debuggerTab.disableUdv = true;
			 $scope.debuggerTab.updateRegex = true;
           /*     $scope.debuggerTab.useRegexChk = $scope.recentJobResonse.runJobFromJumbune;
                $scope.debuggerTab.usrDefChk = $scope.recentJobResonse.inputFile;*/
                if ($scope.recentJobResonse.logKeyValues == 'TRUE') {
                    $scope.debuggerTab.enableLoggingOfUnmatched = true
                } else {
                	//nothing
                	//$scope.tuningTab.quickRecommendationsRadio = "TRUE"
                }

              /*  $scope.debuggerTab.reducerNameText =[];
                $scope.debuggerTab.regexKeyText = [];
                $scope.debuggerTab.regexValText =[];
				$scope.debuggerTab.useRegex=[];
                for(var i=0;i<$scope.recentJobResonse.regexValidations.length;i++){
                	$scope.debuggerTab.useRegex[i]=$scope.recentJobResonse.regexValidations[i].classname;
                	$scope.debuggerTab.reducerNameText[i] = $scope.recentJobResonse.regexValidations[i].classname;
	                $scope.debuggerTab.regexKeyText[i] = $scope.recentJobResonse.regexValidations[i].key;
	                $scope.debuggerTab.regexValText[i] = $scope.recentJobResonse.regexValidations[i].value;
                }
                
                //user
                $scope.debuggerTab.udvReducerNameText = [];
                $scope.debuggerTab.udvKeyText = [];
                $scope.debuggerTab.udvValText = [];
                $scope.debuggerTab.udv = [];

                for(var j=0;j<$scope.recentJobResonse.userValidations.length;j++){
                	$scope.debuggerTab.udv[j] = $scope.recentJobResonse.userValidations[j].classname;
                	$scope.debuggerTab.udvReducerNameText[j] = $scope.recentJobResonse.userValidations[j].classname;
	                $scope.debuggerTab.udvKeyText[j]= $scope.recentJobResonse.userValidations[j].key;
	                $scope.debuggerTab.udvValText[j] = $scope.recentJobResonse.userValidations[j].value;
                }

                $scope.debuggerTab.showRegexInputs();
                $scope.debuggerTab.showUdvInputs();*/

               /* for(var i=0;i< $scope.recentJobResonse.regexValidations.length;i++){
                	$scope.debuggerTab.reducerNameText = $scope.recentJobResonse.regexValidations[i].classname;
	                $scope.debuggerTab.regexKeyText = $scope.recentJobResonse.regexValidations[i].key;
	                $scope.debuggerTab.regexValText = $scope.recentJobResonse.regexValidations[i].value;
                }
                
                for(var j=0;j< $scope.recentJobResonse.userValidations.length;j++){
                	$scope.debuggerTab.udvReducerNameText = $scope.recentJobResonse.userValidations[j].classname;
	                $scope.debuggerTab.udvKeyText = $scope.recentJobResonse.userValidations[j].key;
	                $scope.debuggerTab.udvValText = $scope.recentJobResonse.userValidations[j].value;
                }*/
                if ( $scope.recentJobResonse.regexValidations == undefined || $scope.recentJobResonse.regexValidations == null ) {
                		$scope.debuggerTab.useRegex = [];
                } else {
                	$scope.debuggerTab.useRegex = $scope.recentJobResonse.regexValidations;
                }
                if ( $scope.recentJobResonse.userValidations == undefined || $scope.recentJobResonse.userValidations == null ) {
                		$scope.debuggerTab.udv = [];
                } else {
                	$scope.debuggerTab.udv = $scope.recentJobResonse.userValidations;

                }
                $scope.debuggerTab.showRegexInputs();
                $scope.debuggerTab.showUdvInputs();
                common.setRegexJson($scope.debuggerTab.useRegex);
                common.setUdvRegexJson($scope.debuggerTab.udv);
                //$scope.DefineJobInfoController.allJobInfo = $scope.recentJobResonse.job;
                //$scope.DefineJobInfoController.DefineJobInfoController.allJobInfo[$index].parameters = $scope.recentJobResonse.jobs.parameters
           		//$scope.debuggerTab.enableLoggingOfUnmatched = $scope.recentJobResonse
    }

}]);