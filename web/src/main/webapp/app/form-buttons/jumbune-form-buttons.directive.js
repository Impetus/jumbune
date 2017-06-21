/*
Usage: <div jumbune-form-buttons></div>
 */

angular.module('directives')
    .directive("jumbuneFormButtons", [function(){
        return {
            scope: {
                cancel: '&',
                next: '&',
		tab: '@'
            },
            templateUrl: "app/form-buttons/jumbune-form-buttons.tmpl.html"
        };
    }]);

angular.module('directives')
    .directive("jumbuneWidgetFormButtons", [function(){
        return {
            scope: {
                cancel: '&',
                next: '&',
                tab: '@'
            },
            templateUrl: "app/form-buttons/jumbune-widget-form-buttons.tmpl.html"
        };
    }]);

angular.module('directives')
    .directive("jumbuneWidgetFormButtonsThree", [function(){
        return {
            scope: {
                cancel: '&',
                next: '&',
                back: '&',
                disable: '=',
                tab: '@'
            },
            link: function (scope, elem, attr) {               
                    $('#formDisableOneClick').one('click', function() {  
                        $(this).attr('disabled','disabled');
                    });
                
            },
            templateUrl: "app/form-buttons/jumbune-widget-form-buttons.three.html"
        };
    }]);