

angular.module('directives')
    .directive("jumbuneMandatoryLabel", [function(){
        return {
            transclude: true,
            template: "<span ng-transclude></span><span style='color:red'>*</span>"
        };
    }]);

/** Date Time input field*/	
angular.module('directives')
	.directive("jumbuneDatepicker", [function(){
		return {
			link: function(scope, ele, attr) {
				ele.datetimepicker();
			}
		};
	}]);


/** To allow only digits in input field*/
angular.module('directives')
    .directive('jumbuneOnlyDigits', function (){
        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, element, attrs, ngModel) {
                if (!ngModel) {return undefined;}
                ngModel.$parsers.unshift(function (inputValue) {
                    var digits = inputValue.split('').filter(function (s) {
                        return ( !isNaN(s) && s !== ' ');
                    }).join('');

                    var trimDigits = $.trim(digits);
                    ngModel.$viewValue = trimDigits;
                    ngModel.$render();
                    return trimDigits;
                });
            }
        };
    });

/** To allow digits and decimal values in input field*/
angular.module('directives').directive('validNumber', function() {
      return {
        require: '?ngModel',
        link: function(scope, element, attrs, ngModelCtrl) {
          if(!ngModelCtrl) {
            return; 
          }

          ngModelCtrl.$parsers.push(function(val) {
            if (angular.isUndefined(val)) {
                var val = '';
            }
            
            var clean = val.replace(/[^-0-9\.]/g, '');
            var negativeCheck = clean.split('-');
			var decimalCheck = clean.split('.');
            if(!angular.isUndefined(negativeCheck[1])) {
                negativeCheck[1] = negativeCheck[1].slice(0, negativeCheck[1].length);
                clean =negativeCheck[0] + '-' + negativeCheck[1];
                if(negativeCheck[0].length > 0) {
                	clean =negativeCheck[0];
                }
                
            }
              
            if(!angular.isUndefined(decimalCheck[1])) {
                decimalCheck[1] = decimalCheck[1].slice(0,2);
                clean =decimalCheck[0] + '.' + decimalCheck[1];
            }

            if (val !== clean) {
              ngModelCtrl.$setViewValue(clean);
              ngModelCtrl.$render();
            }
            return clean;
          });

          element.bind('keypress', function(event) {
            if(event.keyCode === 32) {
              event.preventDefault();
            }
          });
        }
      };
    });

/** To allow numbers in min max range
 * Usage: jumbune-num-range-validate="{min: 10, max: 1000}" */
angular.module('directives')
    .directive('jumbuneNumRangeValidate', function (){
        return {
            require: 'ngModel',
            link: function(scope, element, attribute, ngModel) {
                element.on("keypress", function(event) {
                    var key = event.keyCode || event.which;
                    if(key === 32) { //if space, return
                        return false;
                    }
                });
                var minMax = scope.$eval(attribute.jumbuneNumRangeValidate);
                ngModel.$parsers.unshift(function (value) {
                    if (minMax.min && parseInt(value) < minMax.min) {
                        ngModel.$setValidity('jumbuneNumRangeValidate', false);
                    } else if (minMax.max && parseInt(value) > minMax.max) {
                        ngModel.$setValidity('jumbuneNumRangeValidate', false);
                    } else {
                        ngModel.$setValidity('jumbuneNumRangeValidate', true);
                    }
                    return value;
                });
            }
        };
    });

/** To set value of selected file into an input tag */
angular.module('directives')
.directive('fileChange', function () {

    var linker = function ($scope, element, attributes) {
        // onChange, push the files to $scope.files.
        element.bind('change', function (event) {
            var files = event.target.files;
            $scope.$apply(function () {
                if(angular.element('#filePathLocal').val() !== ''){
					angular.element('#fullFilePathLocal').val(angular.element('#filePathLocal').val());
				}
            });
        });
    };
    return {
        restrict: 'A',
        link: linker
    };

}).directive('jarSelected', function () {

    var linker = function ($scope, element, attributes) {
        // onChange, push the files to $scope.files.
        element.bind('change', function (event) {
            $scope.$apply(function () {
                if(angular.element('#addjar').val() !== ''){
					angular.element('#addjarText').val(angular.element('#addjar').val());
				}
            });
        });
    };
    return {
        restrict: 'A',
        link: linker
    };

});
angular.module('directives')
    .directive('onReadFile', function ($parse) {
    return {
        restrict: 'A',
        scope: false,
        link: function(scope, element, attrs) {
            var fn = $parse(attrs.onReadFile);

            element.on('change', function(onChangeEvent) {
                var reader = new FileReader();

                reader.onload = function(onLoadEvent) {
                    scope.$apply(function() {
                        fn(scope, {$fileContent:onLoadEvent.target.result});
                    });
                };

                var extension = onChangeEvent.target.files[0].name.split('.').pop().toLowerCase(); //file extension from input file
                if (extension === 'json') {
                    reader.readAsText((onChangeEvent.srcElement || onChangeEvent.target).files[0]);
                } else {
                    alert('Please choose a JSON file.');
                }
            });
        }
    };
});








angular.module('directives').directive('circularChartF1', function(){
    return {
        restrict: 'A',
        template: '<div data-dimension="300" data-text="Field-1 [>=120049950.000]" data-info="" data-width="25" data-fontsize="25" data-percent="100" data-fgcolor="green" data-bgcolor="#eee" data-type="half" data-icon="fa-task"><div class="grp-val"><span>0</span><span>100102</span></div><div class="grp-data-val"><span><h1><b>100102</b></h1></span></div></div>',
        replace: true,
        scope:{
            dtext: '@',
            fillColor: '@',
            dpercent: '@'
        },
        link : function (scope, element, attrs) {
            scope.dPercentage = attrs.dPercent;
            //scope.abc = attrs.dtext;
            element.circliful();
        }
    }
});

angular.module('directives').directive('circularChartF2', function(){
    return {
        restrict: 'A',
        template: '<div data-dimension="300" data-text="Field-5 [<=1981260901.000]" data-info="" data-width="25" data-fontsize="25" data-percent="20" data-fgcolor="red" data-bgcolor="#eee" data-type="half" data-icon="fa-task"><div class="grp-val"><span>0</span><span>100102</span></div><div class="grp-data-val"><span><h1><b>12216</b></h1></span></div></div>',
        replace: true,
        scope:{
            dtext: '@',
            fillColor: '@',
            dpercent: '@'
        },
        link : function (scope, element, attrs) {
            scope.dPercentage = attrs.dPercent;
            element.circliful();
        }
    }
});

/*angular.module('directives').directive('fileUpload', ['common', function(common){
    return {
        scope: true,
        link: function(scope,el,attrs) {
            el.bind("change", function(event){
                var file = event.target.files[0];
                console.log("FileUpload Directive:::::", file);
                common.setJobJarFile(file, attrs.fileUpload);
            });
        }
    }
}]);*/
/*angular.module('directives').directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;
            
            element.bind('change', function(){
            	alert("on fileMOdel")
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);*/

angular.module('directives').directive('fileModel', ['common' ,'$parse' , function(common,$parse){
    return {
        restrict: 'A',
        link: function(scope,el,attrs) {
        	var model = $parse(attrs.fileModel);
        	var modelSetter = model.assign;
        	
        	el.bind("change", function(event){
            
            	scope.$apply(function(){
                    modelSetter(scope, el[0].files[0]);
                });
            	
                //var file = event.target.files[0];
               	var file = el[0].files[0];
                common.setJobJarFile(el[0].files[0], attrs.fileModel);
                

			          })
			      }
    			//		emnd
            }
}]);

angular.module('directives').directive('optimizeGraphData', function($compile){
    return {
        restrict: 'E',
        replace: true,
        scope: {
            graphInfo: '=info',
            quickInfo: '=quickInfo',
            tunningFlag:'=tunningFlag'

        },
        link : function (scope, element, attrs) {
            
            if (scope.tunningFlag == 'TRUE'){
                scope.$watch('quickInfo', function(newVal,oldVal) {
                    if(Object.keys(newVal).length>0) {
                      // var htmlData = '<div class="col-md-6">'+scope.quickInfo+'<p>'+scope.quickInfo['dfs.blocksize']+'</p></div>';
                        /*var htmlData = '<div class="col-md-6" style="display: table;"><dl style="display: table-row;"><dt style="display: inline-block;text-align: right; width: 15%;font-weight: bold;">dfs.blocksize</dt>'+'<dd style="display: inline-block;margin: 0;text-align: left;width: 80%;">'+scope.quickInfo['dfs.blocksize']+'</dd>'+'<dt style="display: inline-block;text-align: right; width: 15%;font-weight: bold;">mapreduce.tasktracker.http.threads</dt>'+'<dd style="display: inline-block;margin: 0;text-align: left;width: 80%;">'+scope.quickInfo['mapreduce.tasktracker.http.threads']+'</dd>'+*/
                        var htmlData = '<div class="col-md-6"><dl>';
                        var htmlData1 = '<div class="col-md-6"><dl>';
                        var alternate = false;
                    	for (var key in scope.quickInfo) {
                    		if (alternate) {
	                    		htmlData1 = htmlData1 + '<dt>' + key + '</dt>'+'<dd>'+scope.quickInfo[key]+'</dd>';
	                    		alternate = false;
                    		} else {
	                    		htmlData = htmlData + '<dt>' + key + '</dt>'+'<dd>'+scope.quickInfo[key]+'</dd>';
	                    		alternate = true;
	                    	}
                        }
                        htmlData = htmlData + '</dl></div>';
                        htmlData1 = htmlData1 + '</dl></div>';
                        htmlData = htmlData + htmlData1;
                        $('#quickGraphData').html(htmlData);
                    }
                    
                });
            }
            else if (scope.tunningFlag == 'FALSE') {
                scope.$watch('graphInfo', function(newVal,oldVal) {
                    if(Object.keys(newVal).length>0) {
                    	var htmlData = '<div class="col-md-6"><dl>';
                    	var htmlData1 = '<div class="col-md-6"><dl>';
                    	var alternate = false;
                    	for (var key in scope.graphInfo.coefficientNameValuePair) {
                    		if (alternate) {
                    			htmlData1 = htmlData1 + '<dt>' + key + '</dt>'+'<dd>'+scope.graphInfo.coefficientNameValuePair[key]+'</dd>';
                    			alternate = false;
                    		} else {
	                    		htmlData = htmlData + '<dt>' + key + '</dt>'+'<dd>'+scope.graphInfo.coefficientNameValuePair[key]+'</dd>';
	                    		alternate = true;
	                    	}
                        }
                        htmlData = htmlData + '</dl></div>';
                        htmlData1 = htmlData1 + '</dl></div>';
                        htmlData = '<div class="row">' + htmlData + htmlData1 + '</div>';
                        $('#graphData').html(htmlData);
                    }
                 });
            }
            /* scope.$watch('graphInfo', function(newVal,oldVal) {
                    if(Object.keys(newVal).length>0) {
                        var htmlData = '<div class="col-md-6"><dl><dt>Execution Time</dt>'+
                            '<dd>'+scope.graphInfo.executionTimeInMsecs+'</dd>'+
                            '<dt>Execution Time</dt>'+
                            '<dd>'+scope.graphInfo.executionTimeInMsecs+'</dd>'+
                            '<dt>dfs.blocksize</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['dfs.blocksize']+'</dd>'+
                            '<dt>dfs.datanode.handler.count</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['dfs.datanode.handler.count']+'</dd>'+
                            '<dt>dfs.namenode.handler.count</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['dfs.namenode.handler.count']+'</dd>'+
                            '<dt>io.file.buffer.size</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['io.file.buffer.size']+'</dd>'+
                            '<dt>mapreduce.input.fileinputformat.split.maxsize</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.input.fileinputformat.split.maxsize']+'</dd>'+
                            '<dt>mapreduce.input.fileinputformat.split.minsize</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.input.fileinputformat.split.minsize']+'</dd>'+
                            '<dt>mapreduce.job.jvm.numtasks</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.job.jvm.numtasks']+'</dd>'+
                            '<dt>mapreduce.job.reduces</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.job.reduces']+'</dd>'+
                            '<dt>mapreduce.map.java.opts</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.map.java.opts']+'</dd>'+
                            '</dl></div>'+
                            '<div class="col-md-6">'+
                            '<dl>'+
                            '<dt>mapreduce.map.memory.mb</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.map.memory.mb']+'</dd>'+
                            '<dt>mapreduce.map.sort.spill.percent</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.map.sort.spill.percent']+'</dd>'+
                            '<dt>mapreduce.reduce.input.buffer.percent</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.reduce.input.buffer.percent']+'</dd>'+
                            '<dt>mapreduce.reduce.java.opts</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.reduce.java.opts']+'</dd>'+
                            '<dt>mapreduce.reduce.memory.mb</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.reduce.memory.mb']+'</dd>'+
                            '<dt>mapreduce.reduce.shuffle.input.buffer.percent</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.reduce.shuffle.input.buffer.percent']+'</dd>'+
                            '<dt>mapreduce.reduce.shuffle.merge.percent</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.reduce.shuffle.merge.percent']+'</dd>'+
                            '<dt>mapreduce.reduce.shuffle.parallelcopies</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.reduce.shuffle.parallelcopies']+'</dd>'+
                            '<dt>mapreduce.task.io.sort.factor</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.task.io.sort.factor']+'</dd>'+
                            '<dt>mapreduce.task.io.sort.mb</dt>'+
                            '<dd>'+scope.graphInfo.coefficientNameValuePair['mapreduce.task.io.sort.mb']+'</dd>'+
                            '</dl></div>';
                        
                        $('#graphData').html(htmlData);
                    }
                 });*/
               
            
        }
    }
});