/*
Usage: <div jumbune-header></div>
 */

angular.module('directives')
.directive("jumbuneHeader", ['common',function(common){
	return {
		templateUrl: "app/header/jumbune-header.tmpl.html"
	};	
}]);