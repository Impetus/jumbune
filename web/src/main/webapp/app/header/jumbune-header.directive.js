/*
Usage: <div jumbune-header></div>
 */

angular.module('directives')
.directive("jumbuneHeader", [function(){
	return {
		templateUrl: "app/header/jumbune-header.tmpl.html",
		link: function (scope, elem, attr) {
		}
	};	
}]);