(function($) {
    $.jqplot.plotList = {};
    
    $.jqplot.config = {
      enablePlugins:true,
      defaultHeight:300,
      defaultWidth:400,
      UTCAdjust:false,
      timezoneOffset: new Date(new Date().getTimezoneOffset() * 60000),
      errorMessage: '',
      errorBackground: '',
      errorBorder: '',
      errorFontFamily: '',
      errorFontSize: '',
      errorFontStyle: '',
      errorFontWeight: '',
      catchErrors: false,
      defaultTickFormatString: "%.1f",
      defaultColors: [ '#00ff00','#000080','#cccc00','#800080','#ffa500','#00ffff','#dc143c','#191970','#4682b4','#9400d3','#f0e68c','#1e90ff','#00ff7f','#ff1493','#708090','#fffacd','#ee82ee','#ffc0cb','#48d1cc','#adff2f','#f08080','#808080','#ff69b4','#cd5c5c','#ffa07a','#0000ff'],
      defaultNegativeColors: [ '#ff00ff','#ffff7f','#3434ff','#7fff7f','#005aff','#ff0000','#23ebc3','#e6e68f','#b97d4b','#6bff2c','#0f1973','#e16f00','#ff0080','#00eb6c','#8f7f6f','#000532','#117d11','#003f34','#b72e33','#5200d0','#0f7f7f','#7f7f7f','#00964b','#32a3a3','#005f85','#ffff00'],
      dashLength: 4,
      gapLength: 4,
      dotGapLength: 2.5
  };
        /**
     * Axes Style
     * @ignore
     */
    var AxesStyles = {
        normal: 'normal',
        multi: 'multi',
        series: 'series',
        split: 'split'
    };
    
    /**
     * Chart Instances
     * @ignore
     */
    var __chartList = {};
    
    /**
     * @ignore
     */
    var dispose = function() {
        for(var key in __chartList) {
            __chartList[key].destroy();
            __chartList[key] = null;
            delete __chartList[key];
        }
    };
    
    BistelChart = {};
    /**
     * jqPlot Default Options (Some custom options added.)
     * @static
     */
    BistelChart.getDefaultPlotOptions = function() {
        return {
                defaultGridPadding: {top:10, right:10, bottom:23, left:10},
                title: '',
                data: [],
                captureRightClick: true,
                multiCanvas: false,
                copyData: false,
                stackSeries: false,
                legend: {
                    renderer: $.jqplot.EnhancedLegendRenderer,
                    show: true,
                    showLabels: true,
                    showSwatch: true,
                    border: 0,
                    rendererOptions: {
                        numberColumns: 1,
                        seriesToggle: 'fast',
                        disableIEFading: false
                    },
                    placement: 'outsideGrid',
                    shrinkGrid: true,
                    location: 'e'
                },
                seriesDefaults: {
                    renderer: $.jqplot.LineRenderer,
                    rendererOptions: {
                        highlightMouseOver: false,
                        highlightMouseDown: false
                    },
                    shadow: false,
                    showLine: true,
                    showMarker: true,
                    lineWidth: 1,
                    isDragable: false,
                    stackSeries: false,
                    showHighlight: true,
                    xaxis: 'xaxis',
                    yaxis: 'yaxis',
                    strokeStyle: 'rgba(100,100,100,1)',
                    breakOnNull: false,
                    markerOptions: {
                        shadow: false,
                        style: 'filledCircle',
                        fillRect: false,
                        strokeRect: false,
                        lineWidth: 1,
                        stroke: true,
                        size: 7,
                        allowZero: true,
                        printSize: false
                    },
                    dragable: {
                        constrainTo: 'x'
                    },
                    trendline: {
                        show: false
                    },
                    pointLabels: {
                      show: false
                    }
                },
                series: [],
                sortData: false,
                canvasOverlay: {
                    show: true,
                    objects: []
                },
                grid: {
                    shadow: false,
                    marginLeft: '0',
                    borderWidth: 1,
                    gridLineWidth: 0,
                    background: '#fff'
                },
                axes: {
        
                    xaxis: {
                        renderer: $.jqplot.LinearAxisRenderer,
                        rendererOptions: {
                            tickInset: 0.1,
                            minorTicks: 3
                        },
                        drawMinorTickMarks: true,
                        showMinorTicks: true,
                        autoscale: true,
                        tickOptions: {
                            markSize: 6,
                            //formatter: function(format, val) {return val;},
                            fontSize: '8px'
                        }
                    },
                    yaxis: {
                        showMinorTicks: true,
                        renderer: $.jqplot.LinearAxisRenderer,
                        autoscale: true,
                        rendererOptions: {
                            //forceTickAt0: true,
                            minorTicks: 3
                        },
                        padMin: 1,
                        padMax: 1,
                        tickOptions: {
                            markSize: 4,
                            renderer: $.jqplot.CanvasAxisTickRenderer,
                            fontSize: '8px',
                            formatString: "%d"
                        },
                        useSeriesColor: false
                    }
                },
                noDataIndicator: {
                    show: true,
                    indicator: '',
                    axes: {
                        xaxis: {
                            showTicks: false
                        },
                        yaxis: {
                            showTicks: false
                        }
                    }
                },
                cursor: {
                    zoom: true,
                    style: 'auto',
                    showTooltip: false,
                    draggable: false,
                    dblClickReset: false
                },
                highlighter: {
                    show: true,
                    tooltipLocation: 'ne',
                    fadeTooltip: false,
                    tooltipContentEditor: null,
                    tooltipFormatString: '%s %s',
                    useAxesFormatters: false,
                    bringSeriesToFront: true,
                    contextMenu: true,
                    contextMenuSeriesOptions: {},
                    contextMenuBackgroundOptions: {},
                    clearTooltipOnClickOutside: false,
                    overTooltip: false,
                overTooltipOptions: {
                    showMarker: true,
                    showTooltip: false
                }
                },
                groupplot: {
                    show: false
                }
            };
    };
    
    angular.module('bistel.chart', [])
  .directive('bistelChart', ['$rootScope', '$timeout', '$window', '$q', function ($rootScope, $timeout, $window, $q) {
    return {
      restrict: 'A',
      template: '<div></div>',
      replace: true,
      link: function (scope, elem, attrs) {
        if (!elem.attr('id')) {
            elem.attr('id', 'bistelchart'+(Math.floor(Math.random()*100000)).toString()+_.uniqueId());
        }
        
        var timer;
        var timeout = 200;
        
        var renderChart = function () {
          var defer = $q.defer();
          
          var data = scope.$eval(attrs.bistelChart);
          var plot = elem.data('jqplot');
          if (plot && $.isFunction(plot.destroy)) {
            plot.destroy();
            plot = null;
          }
          elem.html('');
          if (!angular.isArray(data)) {
            defer.reject();
            
            return defer.promise;
          }
          
          var chartOptions = scope.$eval(attrs.chartOptions);
          
          var chartEvents = scope.$eval(attrs.chartEvents);
          
          if ($.isPlainObject(chartEvents)) {
            $.each(chartEvents, function(key, eventCallback) {
              elem.unbind(key);
              elem.bind(key, eventCallback);
            });
          }

          var opts = chartOptions ? chartOptions : {};
          opts = $.extend(true, {}, BistelChart.getDefaultPlotOptions(), opts);

          /*$.jqplot.plotList[$(elem).attr('id')] = */
         try {
           elem.jqplot(data, opts);
           if (chartOptions) {
             chartOptions.plot = elem.data('jqplot');
           }
           defer.resolve(elem.data('jqplot'));
         } catch(e) {
           if (timer) {
             $timeout.cancel(timer);
           }
           timer = $timeout(function() {
             try {
               elem.jqplot(data, opts);
               if (chartOptions) {
                 chartOptions.plot = elem.data('jqplot');
               }
               defer.resolve(elem.data('jqplot'));
             } catch(e) {
               if ($rootScope.debug) {
                console.log(e.stack);  // TODO: how to handle removed element
              }
              defer.reject();
             }
           }, timeout);
           
           timer.then(function() {}, function() {
             defer.reject();
           });
         }
         
         return defer.promise;
        };
        
        renderChart();
        
        var optionChange = function(options) {
            var plot = elem.data('jqplot');
            
            if (plot && plot.target.height() <=0 || plot.target.width() <=0 || !plot.target.height() || !plot.target.width()) {
                renderChart();
            } else if (options && plot && plot.series.length > 0) {
                plot.replot(options);
            }
            
            plot = null;
        };
        
        var drawCanvasOverlay = function(objects) {
            var plot = elem.data('jqplot');
            
            if (plot && plot.options.canvasOverlay && $.isArray(objects)) {
                plot.options.canvasOverlay.objects = objects;
                
                var co = plot.plugins.canvasOverlay;
                
                co.setObjects(plot, objects);

                co = null;
            };
            
            plot = null;
        };
        
        var drawCanvasWindow = function(objects) {
            var plot = elem.data('jqplot');
            
            if (plot && plot.options.canvasWindow && $.isArray(objects)) {
                plot.options.canvasWindow.objects = objects;
                
                var cw = plot.plugins.canvasWindow;
                
                cw.setObjects(plot, objects);

                cw = null;
            };
            
            plot = null;
        };
        
        var drawSpecWindow = function(data) {
            var plot = elem.data('jqplot');
            
            if (plot && plot.data.length && plot.options.specWindow && $.isArray(data)) {
                plot.options.specWindow.data = data;
                var axes = scope.$eval(attrs.chartOptions).axes;
                plot.replot({
                  resetAxes: axes,
                  specWindow: {
                    data: data
                  }
                });
            };
            
            plot = null;
        };

        scope.$watch(attrs.bistelChart, function () {
          try {
            renderChart();
          } catch(e) {
            if ($rootScope.debug) {
              console.log(e.stack);
            }
          }
        });
        
        scope.$watch(attrs.chartOptions, function (newValue) {
          try {
            optionChange(newValue);
          } catch(e) {
            if ($rootScope.debug) {
              renderChart();
              console.log(e.stack);
            }
          }
        });
        
        if (attrs.changeOptions != null) {
            scope.$watch(attrs.changeOptions, function (newValue) {
              try {
                optionChange(newValue);
              } catch(e) {
                renderChart();
                if ($rootScope.debug) {
                  console.log(e.stack);
                }
              }
            });
        }
        
        if (attrs.overlayObjects != null) {
            scope.$watch(attrs.overlayObjects, function (newValue) {
              var plot = elem.data('jqplot');
              if (plot && plot.target.height() <=0 || plot.target.width() <=0 || !plot.target.height() || !plot.target.width()) {
                renderChart().then(function() {
                  drawCanvasOverlay(newValue);
                });
              } else {
                drawCanvasOverlay(newValue);
              }
            });
            
            scope.$watchCollection(attrs.overlayObjects, function (newValue) {
              var plot = elem.data('jqplot');
              if (plot && plot.target.height() <=0 || plot.target.width() <=0 || !plot.target.height() || !plot.target.width()) {
                renderChart().then(function() {
                  drawCanvasOverlay(newValue);
                });
              } else {
                drawCanvasOverlay(newValue);
              }
            });
        }
        
        if (attrs.windowObjects != null) {
            scope.$watch(attrs.windowObjects, function (newValue) {
              var plot = elem.data('jqplot');
              if (plot && plot.target.height() <=0 || plot.target.width() <=0 || !plot.target.height() || !plot.target.width()) {
                renderChart().then(function() {
                  drawCanvasWindow(newValue);
                });
              } else {
                drawCanvasWindow(newValue);
              }
            });
            scope.$watchCollection(attrs.windowObjects, function (newValue) {
              var plot = elem.data('jqplot');
              if (plot && plot.target.height() <=0 || plot.target.width() <=0 || !plot.target.height() || !plot.target.width()) {
                renderChart().then(function() {
                  drawCanvasWindow(newValue);
                });
              } else {
                drawCanvasWindow(newValue);
              }
            });
        }
        
        if (attrs.specWindows != null) {
            scope.$watch(attrs.specWindows, function (newValue) {
              var plot = elem.data('jqplot');
              if (plot && plot.target.height() <=0 || plot.target.width() <=0 || !plot.target.height() || !plot.target.width()) {
                renderChart().then(function() {
                  drawSpecWindow(newValue);
                });
              } else {
                drawSpecWindow(newValue);
              }
            });
            scope.$watchCollection(attrs.specWindows, function (newValue) {
              var plot = elem.data('jqplot');
              if (plot && plot.target.height() <=0 || plot.target.width() <=0 || !plot.target.height() || !plot.target.width()) {
                renderChart().then(function() {
                  drawSpecWindow(newValue);
                });
              } else {
                drawSpecWindow(newValue);
              }
            });
        }
        
        function handleResize(ev) {
            if (timer) $timeout.cancel(timer);
            timer = $timeout(function() {
                var plot = elem.data('jqplot');
                try {
                  if (plot && plot.target.width() && plot.target.height()) {
                    if (plot.target.width() !== plot.baseCanvas._elem.width() || plot.target.height() !== plot.baseCanvas._elem.height()) {
                      var chartOptions = scope.$eval(attrs.chartOptions);
                      var axes = $.extend(true, {}, chartOptions.axes);
                      
                      var hasCategoryAxis = false;
                      $.each(axes, function(axisName, ax) {
                        if (ax.renderer &&  ax.renderer !== $.jqplot.LinearAxisRenderer) {
                          delete axes[axisName];
                        }
                      });
                      
                      plot.replot({resetAxes: axes});
                    }
                  } else if (elem.length === 0) {
                    if ($(elem).parent()) {
                      $(elem).parent().unbind('resize', handleResize);
                    }
                     angular.element($window).unbind('resize', handleResize);
                  }
                } catch(e) {
                  console.log('target removed');
                }
            }, timeout);
        }

        if ($window) {
            $(elem).parent().on('resize', handleResize).trigger('resize');
            angular.element($window).bind('resize', handleResize);
        }
        
        
      }
    };
  }]);
}).call(this, jQuery);
