angular.module('formButtonDir.ctrl', [])
.directive("formButton", [function(){
	return {
		template: "<div class='txt-rgt'><button type='button' class='btn btn-default' ng-click='cancel()'>Cancel</button> <button type='button' class='btn btn-primary' ng-click='preview()'>Next</button></div>"
	};	
}])