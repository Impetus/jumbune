/*
Usage: <div jumbune-footer></div>
 */

angular.module('directives')
.directive("jumbuneFooter", [function(){
	return {
        replace: true,
		templateUrl: "app/footer/jumbune-footer.tmpl.html"
	};	
}]);