(function($) {
	// make sure undefined is undefined
	var undefined;
	var _axisNames = ['yMidAxis', 'xaxis', 'yaxis', 'x2axis', 'y2axis', 'y3axis', 'y4axis', 'y5axis', 'y6axis', 'y7axis', 'y8axis', 'y9axis'];
	
	/**
	 *
	 * Hooks: jqPlot Pugin Hooks
	 *
	 * $.jqplot.postRedrawSeriesHooks - called after series redrawn.					// customizing	(2012-08-30, Roy Choi)
	 * $.jqplot.postRemoveSeriesPointHooks - called after series point removed.			// customizing	(2012-08-30, Roy Choi)
	 * $.jqplot.postAppendSeriesPointHooks - called after series point append.			// customizing	(2012-08-30, Roy Choi)
	 * $.jqplot.postUpdateSeriesHooks - called after series data update.			// customizing	(2013-02-14, Roy Choi)
	 * $.jqplot.postSeriesHighlightHooks - called after series highlight update.			// customizing	(2013-02-28, Roy Choi)
	 * $.jqplot.postSeriesUnhighlightHooks - called after series unhighlight update.			// customizing	(2013-02-28, Roy Choi)
	 *
	 */

	$.jqplot.preRedrawSeriesHooks = [];
	// customizing	(2013-03-14, Roy Choi)
	$.jqplot.postRedrawSeriesHooks = [];
	// customizing	(2012-08-30, Roy Choi)
	$.jqplot.postRemoveSeriesPointHooks = [];
	// customizing	(2012-08-30, Roy Choi)
	$.jqplot.postAppendSeriesPointHooks = [];
	// customizing	(2012-08-30, Roy Choi)
	$.jqplot.postUpdateSeriesHooks = [];
	// customizing	(2013-02-14, Roy Choi)

	$.jqplot.postSeriesHighlightHooks = [];
	// customizing	(2013-02-28, Roy Choi)
	$.jqplot.postSeriesUnhighlightHooks = [];
	// customizing	(2013-02-28, Roy Choi)
	
	$.jqplot.preReinitHooks = [];
	
	function Axis() {};
	
  Axis.prototype.resetDataBounds = function() {
      // Go through all the series attached to this axis and find
      // the min/max bounds for this axis.
      var db = this._dataBounds;
      var nd = [];
    	db.min = null;
    	db.max = null;
      var l, s, d;
      // check for when to force min 0 on bar series plots.
      var doforce = (this.show) ? true : false;
      for (var i=0; i<this._series.length; i++) {
          s = this._series[i];
          if (s.show || this.scaleToHiddenSeries) {
              d = s._plotData;
              if (s._type === 'line' && s.renderer.bands.show && this.name.charAt(0) !== 'x') {
                  d = [[0, s.renderer.bands._min], [1, s.renderer.bands._max]];
              }

              var minyidx = 1, maxyidx = 1;

              if (s._type != null && s._type == 'ohlc') {
                  minyidx = 3;
                  maxyidx = 2;
              }
              
	            if (s.renderer.constructor == $.jqplot.BigDataScatterRenderer) {
	            	if (this.name == s.xaxis || this.name == s.yaxis) {
	            		var dp;
	                	if (this.name == 'xaxis' || this.name == 'x2axis') {
	                		for (var j=0, l=d.length; j<l; j++) { 
	                			dp = d[j][0];
	                			if (dp != null) {
	                				if (dp < db.min) {
	                                    db.min = d[j][0];
	                                }
	                                if (dp > db.max) {
	                                    db.max = d[j][0];
	                                }
	                			} else {
	                				nd.push(j);
	                			}
	                        }
	                    }              
	                    else {
	                    	for (var j=0, l=d.length; j<l; j++) { 
	                    		dp = d[j][minyidx];
	                    		if (dp != null) {
	                    			if (dp < db.min) {
	                                    db.min = dp;
	                                }
	                                if (dp > db.max) {
	                                    db.max = dp;
	                                }
	                    		} else {
	                				nd.push(j);
	                			}
	                        }
	                    } 
	                	dp = null;
	                }
	            } else {
	            	//if (this.name == s.xaxis || this.name == s.yaxis) {
	                	if (this.name == 'xaxis' || this.name == 'x2axis') {
	                		
	                		for (var j=0, l=d.length; j<l; j++) { 
	                			if (d[j][0] != null && !isNaN(d[j][0])) {
	                				if (d[j][0] < db.min || db.min == null) {
	                                    db.min = d[j][0];
	                                }
	                                if (d[j][0] > db.max || db.max == null) {
	                                    db.max = d[j][0];
	                                }
	                			} else {
	                				nd.push(j);
	                			}
	                        }
	                    }              
	                    else {
	                    	for (var j=0, l=d.length; j<l; j++) { 
	                    		if (d[j][minyidx] != null && !isNaN(d[j][minyidx])) {
	                    			if (d[j][minyidx] < db.min || db.min == null) {
	                                    db.min = d[j][minyidx];
	                                }
	                                if (d[j][maxyidx] > db.max || db.max == null) {
	                                    db.max = d[j][maxyidx];
	                                }
	                    		} else {
	                				nd.push(j);
	                			}
	                        }
	                    } 
	                //}
	            }

              // Hack to not pad out bottom of bar plots unless user has specified a padding.
              // every series will have a chance to set doforce to false.  once it is set to 
              // false, it cannot be reset to true.
              // If any series attached to axis is not a bar, wont force 0.
              if (doforce && s.renderer.constructor !== $.jqplot.BarRenderer) {
                  doforce = false;
              }

              else if (doforce && this._options.hasOwnProperty('forceTickAt0') && this._options.forceTickAt0 == false) {
                  doforce = false;
              }

              else if (doforce && s.renderer.constructor === $.jqplot.BarRenderer) {
                  if (s.barDirection == 'vertical' && this.name != 'xaxis' && this.name != 'x2axis') { 
                      if (this._options.pad != null || this._options.padMin != null) {
                          doforce = false;
                      }
                  }

                  else if (s.barDirection == 'horizontal' && (this.name == 'xaxis' || this.name == 'x2axis')) {
                      if (this._options.pad != null || this._options.padMin != null) {
                          doforce = false;
                      }
                  }

              }
          }
      }

      if (doforce && this.renderer.constructor === $.jqplot.LinearAxisRenderer && db.min >= 0) {
          this.padMin = 1.0;
          this.forceTickAt0 = true;
      }
      
      db = s = l = d = null;
  };
	
	/**
	 * Class: Series
	 * An individual data series object.  Cannot be instantiated directly, but created
	 * by the Plot oject.  Series properties can be set or overriden by the
	 * options passed in from the user.
	 * - custom options
	 * 	markerOptions: {
	 * 		stroke: Boolean
	 * 	}
	 */
	function Series() {
		this.hide = false;
		this.highlight = true;
		this.highlighted = null;
		this.hasNullPoint = false;	// to check series has empty point
		this.breakOnDiff = null;
	}
	
	

	Series.prototype.init = function(index, gridbw, plot) {
		

		// weed out any null values in the data.
		this.index = index;
		this.gridBorderWidth = gridbw;
		var d = this.data;
		var temp = [], i, l;
		
		if (this.renderer !== $.jqplot.BigDataScatterRenderer) {
			/*if (!this.breakOnNull) {
				for ( i = 0, l = d.length; i < l; i++) {
					if (d[i] == null || d[i][0] == null || isNaN(d[i][0]) || d[i][1] == null || isNaN(d[i][1])) {
						if (!this.hasNullPoint) this.hasNullPoint = true;
						continue;
					} else {
						temp.push(d[i]);
					}
				}
			} else {
				for ( i = 0, l = d.length; i < l; i++) {
					if ((d[i] == null || d[i][0] == null || isNaN(d[i][0]) || d[i][1] == null || isNaN(d[i][1])) && !this.hasNullPoint) {
						this.hasNullPoint = true;
					}
					// TODO: figure out what to do with null values
					// probably involve keeping nulls in data array
					// and then updating renderers to break line
					// when it hits null value.
					// For now, just keep value.
					temp.push(d[i]);
				}
			}*/
            for ( i = 0, l = d.length; i < l; i++) {
                if ((d[i] == null || d[i][0] == null || isNaN(d[i][0]) || d[i][1] == null || isNaN(d[i][1])) && !this.hasNullPoint) {
                    this.hasNullPoint = true;
                }
                // TODO: figure out what to do with null values
                // probably involve keeping nulls in data array
                // and then updating renderers to break line
                // when it hits null value.
                // For now, just keep value.
                temp.push(d[i]);
            }
			
			this.data = temp;
		}
		d = null;

		// parse the renderer options and apply default colors if not provided
		// Set color even if not shown, so series don't change colors when other
		// series on plot shown/hidden.
		if (!this.color) {
			this.color = plot.colorGenerator.get(this.index);
		}
		// customizing
		if (this.showMarker && this.markerOptions && !this.markerOptions.strokeStyle) {
			this.markerOptions.strokeStyle = this.getStrokeStyleByColor(this.color);
		}
		if (!this.negativeColor) {
			this.negativeColor = plot.negativeColorGenerator.get(this.index);
		}

		if (!this.fillColor) {
			this.fillColor = this.color;
		}
		if (this.fillAlpha) {
			var comp = $.jqplot.normalize2rgb(this.fillColor);
			var comp = $.jqplot.getColorComponents(comp);
			this.fillColor = 'rgba(' + comp[0] + ',' + comp[1] + ',' + comp[2] + ',' + this.fillAlpha + ')';
		}
		if ($.isFunction(this.renderer)) {
			this.renderer = new this.renderer();
		}
		this.renderer.init.call(this, this.rendererOptions, plot);
		if (this.renderer.constructor !== $.jqplot.BigdataScatterRenderer) {
			this.markerRenderer = new this.markerRenderer();
			
			if (!this.markerOptions.color) {
				this.markerOptions.color = this.color;
			}
			if (this.markerOptions.show == null) {
				this.markerOptions.show = this.showMarker;
			}
			this.showMarker = this.markerOptions.show;
			
			// the markerRenderer is called within its own scope, don't want to overwrite series options!!
			this.markerRenderer.init(this.markerOptions);
		} else {
			if (!this.markerOptions.color) {
				this.markerOptions.color = this.color;
			}
			if (this.markerOptions.show == null) {
				this.markerOptions.show = this.showMarker;
			}
			this.showMarker = this.markerOptions.show;
		}
		
	};

	// customizing (2011-11-02, Roy Choi)
	Series.prototype.getStrokeStyleByColor = function(style) {
		if (style) {
			if (style.indexOf('#') != -1) {
				var r, g, b, h = style;
				h = h.replace('#', '');
				if (h.length == 3) {
					h = h.charAt(0) + h.charAt(0) + h.charAt(1) + h.charAt(1) + h.charAt(2) + h.charAt(2);
				}
				r = parseInt(h.slice(0, 2), 16) - parseInt('40', 16);
				if (r < 0)
					r = 0;
				r = r.toString(16);
				g = parseInt(h.slice(2, 4), 16) - parseInt('40', 16);
				if (g < 0)
					g = 0;
				g = g.toString(16);
				b = parseInt(h.slice(4, 6), 16) - parseInt('40', 16);
				if (b < 0)
					b = 0;
				b = b.toString(16);
				var hex;
				hex = '#' + (r.length == 1 ? '0' + r : r) + (g.length == 1 ? '0' + g : g) + (b.length == 1 ? '0' + b : b);

				r = g = b = h = null;

				try {
					return hex;
				} finally {
					hex = null;
				}
			} else if (style.indexOf('rgb') != -1) {
				var r, g, b, pat = /rgba?\( *([0-9]{1,3}\.?[0-9]*%?) *, *([0-9]{1,3}\.?[0-9]*%?) *, *([0-9]{1,3}\.?[0-9]*%?) *(?:, *[0-9.]*)?\)/;
				var m = style.match(pat);
				var h = '#';
				for (var i = 1; i < 4; i++) {
					var temp;
					if (m[i].search(/%/) != -1) {
						temp = parseInt(255 * m[i] / 100 - 64, 10);
					} else {
						temp = parseInt(m[i] - 64, 10);
					}
					if (temp < 0)
						temp = 0;
					temp.toString(16);
					if (temp.length == 1)
						temp = '0' + temp;
					h += temp;
				}

				r = g = b = pat = m = temp = null;

				try {
					return h;
				} finally {
					h = null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	};

	// customizing add new method
	/** removePoint
	 *
	 */
	Series.prototype.removePoint = function(plot, pointIndex, draw) {
		this.data.splice(pointIndex, 1);

		if (draw) {
			if (plot.options.multiCanvas) {
				this.draw(this.canvas._ctx, {}, plot);
			} else {
				plot.redrawSeries();
			}

			for (var j = 0; j < $.jqplot.postRemoveSeriesPointHooks.length; j++) {
				$.jqplot.postRemoveSeriesPointHooks[j].call(this, pointIndex, plot);
			}
			j = null;
		}
	};

	// customizing add new method
	/** appendPoint
	 *
	 */
	Series.prototype.appendPoint = function(plot, pointData, draw) {
		this.data.push(pointData);

		if (draw) {
			if (plot.options.multiCanvas) {
				this.draw(this.canvas._ctx, {}, plot);
			} else {
				plot.redrawSeries();
			}
			for (var j = 0; j < $.jqplot.postAppendSeriesPointHooks.length; j++) {
				$.jqplot.postAppendSeriesPointHooks[j].call(this, pointData, plot);
			}
			j = null;
		}
	};

	// customizing add new method
	/** updateData
	 *
	 */
	Series.prototype.updateData = function(plot, data, draw) {
		this.data = data;

		if (draw) {
			if (plot.options.multiCanvas) {
				this.draw(this.canvas._ctx, {}, plot);
			} else {
				plot.redrawSeries();
			}
			for (var j = 0; j < $.jqplot.postUpdateSeriesHooks.length; j++) {
				$.jqplot.postUpdateSeriesHooks[j].call(this, data, plot);
			}
			j = null;
			//plot.replot({resetAxes:true});
		}
	};

	// toggles series display on plot, e.g. show/hide series
	Series.prototype.toggleDisplay = function(ev, callback) {
		var plot = ev.data.plot;
		var s, speed;

		if (plot.options.multiCanvas) {
			if (ev.data.series) {
				s = ev.data.series;
			} else {
				s = this;
			}

			if (ev.data.speed) {
				speed = ev.data.speed;
			}
			if (speed) {
				// this can be tricky because series may not have a canvas element if replotting.
				if (s.canvas._elem.is(':hidden') || !s.show) {
					s.show = true;

					s.canvas._elem.removeClass('jqplot-series-hidden');
					if (s.shadowCanvas._elem) {
						s.shadowCanvas._elem.fadeIn(speed);
					}
					s.canvas._elem.fadeIn(speed, callback);
					s.canvas._elem.nextAll('.jqplot-point-label.jqplot-series-' + s.index).fadeIn(speed);
				} else {
					s.show = false;

					s.canvas._elem.addClass('jqplot-series-hidden');
					if (s.shadowCanvas._elem) {
						s.shadowCanvas._elem.fadeOut(speed);
					}
					s.canvas._elem.fadeOut(speed, callback);
					s.canvas._elem.nextAll('.jqplot-point-label.jqplot-series-' + s.index).fadeOut(speed);
				}
			} else {
				// this can be tricky because series may not have a canvas element if replotting.
				if (s.canvas._elem.is(':hidden') || !s.show) {
					s.show = true;

					s.canvas._elem.removeClass('jqplot-series-hidden');
					if (s.shadowCanvas._elem) {
						s.shadowCanvas._elem.show();
					}
					s.canvas._elem.show(0, callback);
					s.canvas._elem.nextAll('.jqplot-point-label.jqplot-series-' + s.index).show();
				} else {
					s.show = false;

					s.canvas._elem.addClass('jqplot-series-hidden');
					if (s.shadowCanvas._elem) {
						s.shadowCanvas._elem.hide();
					}
					s.canvas._elem.hide(0, callback);
					s.canvas._elem.nextAll('.jqplot-point-label.jqplot-series-' + s.index).hide();
				}
			}
		} else {
			if (ev.data.series) {
				s = ev.data.series;
			} else {
				s = this;
			}

			if (ev.data.series && !$.isArray(s) && s.index == plot.highlightSeriesIndex && s.hide) {
				plot.seriesCanvas._elem.fadeTo(0, 1.0);
				var ctx = plot.seriesHighlightCanvas._ctx;
				ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
				ctx = null;
			}

			var ctx = this.canvas._ctx;
			ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
			var series = plot.series;
			var j;
			for (var i = 0, len = series.length; i < len; i++) {
				j = plot.seriesStack[i];
				if (!series[j].hide) {
					series[j].draw(ctx, {}, plot);
				}
			}

			i = j = len = ctx = series = null;

		}

		plot.target.trigger('jqPlot.seriesVisibleChange', [plot, s]);

		s = null;
	};

  // data - optional data point array to draw using this series renderer
  // gridData - optional grid data point array to draw using this series renderer
  // stackData - array of cumulative data for stacked plots.
  Series.prototype.draw = function(sctx, opts, plot) {
  	
      var options = (opts == undefined) ? {} : opts;
      sctx = (sctx == undefined) ? this.canvas._ctx : sctx;
      
      var j, data, gridData;
      
      // hooks get called even if series not shown
      // we don't clear canvas here, it would wipe out all other series as well.
      for (j=0; j<$.jqplot.preDrawSeriesHooks.length; j++) {
          $.jqplot.preDrawSeriesHooks[j].call(this, sctx, options);
      }
      if (this.show) {
      		if (this._type === 'line' && !this.renderer.smooth) {
      			this.renderer.setNormalGridData.call(this, plot);
      		} else {
      			this.renderer.setGridData.call(this, plot);
      		}
          if (!options.preventJqPlotSeriesDrawTrigger) {
              $(sctx.canvas).trigger('jqplotSeriesDraw', [this.data, this.gridData]);
          }
          data = [];
          if (options.data) {
              data = options.data;
          }
          else if (!this._stack) {
              data = this.data;
          }
          else {
              data = this._plotData;
          }
          
          
          if (this._type === 'line' && !this.renderer.smooth) {
              if (!options.data && !this._stack) {
                gridData = options.gridData || this.gridData;
              } else {
                gridData = options.gridData || this.renderer.makeNormalGridData.call(this, data, plot);
              }
          } else {
              gridData = options.gridData || this.renderer.makeGridData.call(this, data, plot);
              
              if (this._type === 'line' && this.renderer.smooth && this.renderer._smoothedData.length) {
                	gridData = this.renderer._smoothedData;
              }
          }
          
          if (!options.data && !this._stack && this.breakOnDiff !== null && data.length > 0) {
              var startIndex = 0;
              var diffColumnIndex = this.breakOnDiff;
              var currentDiff = data[0][diffColumnIndex];
              
              for (var i = 0, l = data.length; i < l; i++) {
                  if (data[i][diffColumnIndex] !== currentDiff) {
                      currentDiff = data[i][diffColumnIndex];
                      
                      this.renderer.draw.call(this, sctx, gridData.slice(startIndex, i), options, plot);
                      
                      startIndex = i;
                  }
              }
              this.renderer.draw.call(this, sctx, gridData.slice(startIndex), options, plot);
              
          } else {
              this.renderer.draw.call(this, sctx, gridData, options, plot);
          }
      }
      
      for (j=0; j<$.jqplot.postDrawSeriesHooks.length; j++) {
          $.jqplot.postDrawSeriesHooks[j].call(this, sctx, options, plot);
      }
      
      sctx = opts = plot = j = data = gridData = null;

  };

	/**
	 * Class: jqPlot
	 * Plot object returned by call to $.jqplot.  Handles parsing user options,
	 * creating sub objects (Axes, legend, title, series) and rendering the plot.
	 */
	function jqPlot() {
		this.seriesCanvas = null;
		this.seriesHighlightCanvas = null;
		this.highlightSeriesIndex = null;
		
		this.multiCanvasLimit = 50;

		// Group: methods
		//
		// method: init
		// sets the plot target, checks data and applies user
		// options to plot.
		this.init = function(target, data, options) {
			options = options || {};
			for (var i = 0; i < $.jqplot.preInitHooks.length; i++) {
				$.jqplot.preInitHooks[i].call(this, target, data, options);
			}

			for (var i = 0; i < this.preInitHooks.hooks.length; i++) {
				this.preInitHooks.hooks[i].call(this, target, data, options);
			}

			this.targetId = '#' + target;
			this.target = $('#' + target);

			//////
			// Add a reference to plot
			//////
			if (this._addDomReference) {
				this.target.data('jqplot', this);
			}
			// remove any error class that may be stuck on target.
			this.target.removeClass('jqplot-error');
			if (!this.target.get(0)) {
				throw new Error("No plot target specified");
			}

			// make sure the target is positioned by some means and set css
			if (this.target.css('position') == 'static') {
				this.target.css('position', 'relative');
			}
			if (!this.target.hasClass('jqplot-target')) {
				this.target.addClass('jqplot-target');
			}

			// if no height or width specified, use a default.
			if (!this.target.height()) {
				var h;
				if (options && options.height) {
					h = parseInt(options.height, 10);
				} else if (this.target.attr('data-height')) {
					h = parseInt(this.target.attr('data-height'), 10);
				} else if (this.target.css('height')) {
				    ;
                } else {
					h = parseInt($.jqplot.config.defaultHeight, 10);
				}
				this._height = h;
				this.target.css('height', h + 'px');
			} else {
				this._height = h = this.target.height();
			}
			if (!this.target.width()) {
				var w;
				if (options && options.width) {
					w = parseInt(options.width, 10);
				} else if (this.target.attr('data-width')) {
					w = parseInt(this.target.attr('data-width'), 10);
				} else if (this.target.css('width')) {
				    ;
				} else {
					w = parseInt($.jqplot.config.defaultWidth, 10);
				}
				this._width = w;
				this.target.css('width', w + 'px');
			} else {
				this._width = w = this.target.width();
			}

			for (var i = 0, l = _axisNames.length; i < l; i++) {
				this.axes[_axisNames[i]] = new Axis(_axisNames[i]);
			}

			this._plotDimensions.height = this._height;
			this._plotDimensions.width = this._width;
			this.grid._plotDimensions = this._plotDimensions;
			this.title._plotDimensions = this._plotDimensions;
			this.baseCanvas._plotDimensions = this._plotDimensions;
			this.eventCanvas._plotDimensions = this._plotDimensions;
			this.legend._plotDimensions = this._plotDimensions;
			if (this._height <= 0 || this._width <= 0 || !this._height || !this._width) {
				throw new Error("Canvas dimension not set");
			}

			if (options.dataRenderer && $.isFunction(options.dataRenderer)) {
				if (options.dataRendererOptions) {
					this.dataRendererOptions = options.dataRendererOptions;
				}
				this.dataRenderer = options.dataRenderer;
				data = this.dataRenderer(data, this, this.dataRendererOptions);
			}

			if (options.noDataIndicator && $.isPlainObject(options.noDataIndicator)) {
				$.extend(true, this.noDataIndicator, options.noDataIndicator);
			}

			if (data == null || $.isArray(data) == false || data.length == 0 || $.isArray(data[0]) == false || data[0].length == 0) {

				if (this.noDataIndicator.show == false) {
					throw new Error("No data specified");
				} else {
					// have to be descructive here in order for plot to not try and render series.
					// This means that $.jqplot() will have to be called again when there is data.
					//delete options.series;

					for (var ax in this.noDataIndicator.axes) {
						for (var prop in this.noDataIndicator.axes[ax]) {
							this.axes[ax][prop] = this.noDataIndicator.axes[ax][prop];
						}
					}

					this.postDrawHooks.add(function() {
						var eh = this.eventCanvas.getHeight();
						var ew = this.eventCanvas.getWidth();
						var temp = $('<div class="jqplot-noData-container" style="position:absolute;"></div>');
						this.target.append(temp);
						temp.height(eh);
						temp.width(ew);
						temp.css('top', this.eventCanvas._offsets.top);
						temp.css('left', this.eventCanvas._offsets.left);

						var temp2 = $('<div class="jqplot-noData-contents" style="text-align:center; position:relative; margin-left:auto; margin-right:auto;"></div>');
						temp.append(temp2);
						temp2.html(this.noDataIndicator.indicator);
						var th = temp2.height();
						var tw = temp2.width();
						temp2.height(th);
						temp2.width(tw);
						temp2.css('top', (eh - th) / 2 + 'px');
					});

				}
			}

			// customizing
			if (options.copyData) {
				// make a copy of the data
				this.data = $.extend(true, [], data);
			} else {
				// use orginal data
				this.data = data;
			}
			
			if (options.defaultGridPadding) {
				$.extend(this._defaultGridPadding, options.defaultGridPadding);
			}

			this.parseOptions(options);

			if (this.textColor) {
				this.target.css('color', this.textColor);
			}
			if (this.fontFamily) {
				this.target.css('font-family', this.fontFamily);
			}
			if (this.fontSize) {
				this.target.css('font-size', this.fontSize);
			}

			this.title.init();
			this.legend.init();
			this._sumy = 0;
			this._sumx = 0;
			this.computePlotData();
			for (var i = 0; i < this.series.length; i++) {
				// set default stacking order for series canvases
				this.seriesStack.push(i);
				this.previousSeriesStack.push(i);
				if(this.options.multiCanvas && this.series[i].shadowCanvas) {
					this.series[i].shadowCanvas._plotDimensions = this._plotDimensions;
				}
				this.series[i].canvas._plotDimensions = this._plotDimensions;
				for (var j = 0; j < $.jqplot.preSeriesInitHooks.length; j++) {
					$.jqplot.preSeriesInitHooks[j].call(this.series[i], target, this.data, this.options.seriesDefaults, this.options.series[i], this);
				}
				for (var j = 0; j < this.preSeriesInitHooks.hooks.length; j++) {
					this.preSeriesInitHooks.hooks[j].call(this.series[i], target, this.data, this.options.seriesDefaults, this.options.series[i], this);
				}
				// this.computePlotData(this.series[i], i);
				this.series[i]._plotDimensions = this._plotDimensions;
				this.series[i].init(i, this.grid.borderWidth, this);
				for (var j = 0; j < $.jqplot.postSeriesInitHooks.length; j++) {
					$.jqplot.postSeriesInitHooks[j].call(this.series[i], target, this.data, this.options.seriesDefaults, this.options.series[i], this);
				}
				for (var j = 0; j < this.postSeriesInitHooks.hooks.length; j++) {
					this.postSeriesInitHooks.hooks[j].call(this.series[i], target, this.data, this.options.seriesDefaults, this.options.series[i], this);
				}
				this._sumy += this.series[i]._sumy;
				this._sumx += this.series[i]._sumx;
			}

			var name, axis;
			for (var i = 0, l = _axisNames.length; i < l; i++) {
				name = _axisNames[i];
				axis = this.axes[name];
				axis._plotDimensions = this._plotDimensions;
				axis.init();
				if (this.axes[name].borderColor == null) {
					if (name.charAt(0) !== 'x' && axis.useSeriesColor === true && axis.show) {
						axis.borderColor = axis._series[0].color;
					} else {
						axis.borderColor = this.grid.borderColor;
					}
				}
			}

			if (this.sortData) {
				sortData(this.series);
			}
			this.grid.init();
			this.grid._axes = this.axes;

			this.legend._series = this.series;

			for (var i = 0; i < $.jqplot.postInitHooks.length; i++) {
				$.jqplot.postInitHooks[i].call(this, target, this.data, options);
			}

			for (var i = 0; i < this.postInitHooks.hooks.length; i++) {
				this.postInitHooks.hooks[i].call(this, target, this.data, options);
			}
		};

    // method: reInitialize
    // reinitialize plot for replotting.
    // not called directly.
    this.reInitialize = function (data, opts) {
        // Plot should be visible and have a height and width.
        // If plot doesn't have height and width for some
        // reason, set it by other means.  Plot must not have
        // a display:none attribute, however.

        for (var i=0; i<$.jqplot.preReinitHooks.length; i++) {
            $.jqplot.preReinitHooks[i].call(this, this.targetId.substr(1), data, opts);
        }

        var options = $.extend(true, {}, this.options, opts);

        var target = this.targetId.substr(1);
        var tdata = (data == null) ? this.data : data;

        for (var i=0; i<$.jqplot.preInitHooks.length; i++) {
            $.jqplot.preInitHooks[i].call(this, target, tdata, options);
        }

        for (var i=0; i<this.preInitHooks.hooks.length; i++) {
            this.preInitHooks.hooks[i].call(this, target, tdata, options);
        }
        
        this._height = this.target.height();
        this._width = this.target.width();
        
        if (this._height <=0 || this._width <=0 || !this._height || !this._width) {
            throw new Error("Target dimension not set");
        }
        
        this._plotDimensions.height = this._height;
        this._plotDimensions.width = this._width;
        this.grid._plotDimensions = this._plotDimensions;
        this.title._plotDimensions = this._plotDimensions;
        this.baseCanvas._plotDimensions = this._plotDimensions;
        this.eventCanvas._plotDimensions = this._plotDimensions;
        this.legend._plotDimensions = this._plotDimensions;

        var name,
            t, 
            j, 
            axis;

        for (var i=0, l=_axisNames.length; i<l; i++) {
            name = _axisNames[i];
            axis = this.axes[name];

            // Memory Leaks patch : clear ticks elements
            t = axis._ticks;
            for (var j = 0, tlen = t.length; j < tlen; j++) {
              var el = t[j]._elem;
              if (el) {
                // if canvas renderer
                if ($.jqplot.use_excanvas && window.G_vmlCanvasManager.uninitElement !== undefined) {
                  window.G_vmlCanvasManager.uninitElement(el.get(0));
                }
                el.emptyForce();
                el = null;
                t._elem = null;
              }
            }
            t = null;

            delete axis.ticks;
            delete axis._ticks;
            this.axes[name] = new Axis(name);
            this.axes[name]._plotWidth = this._width;
            this.axes[name]._plotHeight = this._height;
        }
        
        if (data) {
            if (options.dataRenderer && $.isFunction(options.dataRenderer)) {
                if (options.dataRendererOptions) {
                    this.dataRendererOptions = options.dataRendererOptions;
                }
                this.dataRenderer = options.dataRenderer;
                data = this.dataRenderer(data, this, this.dataRendererOptions);
            }
            
            // make a copy of the data
            this.data = $.extend(true, [], data);
        }

        if (opts) {
            this.parseOptions(options);
        }
        
        this.title._plotWidth = this._width;
        
        if (this.textColor) {
            this.target.css('color', this.textColor);
        }
        if (this.fontFamily) {
            this.target.css('font-family', this.fontFamily);
        }
        if (this.fontSize) {
            this.target.css('font-size', this.fontSize);
        }

        this.title.init();
        this.legend.init();
        this._sumy = 0;
        this._sumx = 0;

        this.seriesStack = [];
        this.previousSeriesStack = [];

        this.computePlotData();
        for (var i=0, l=this.series.length; i<l; i++) {
            // set default stacking order for series canvases
            this.seriesStack.push(i);
            this.previousSeriesStack.push(i);
            this.series[i].shadowCanvas._plotDimensions = this._plotDimensions;
            this.series[i].canvas._plotDimensions = this._plotDimensions;
            for (var j=0; j<$.jqplot.preSeriesInitHooks.length; j++) {
                $.jqplot.preSeriesInitHooks[j].call(this.series[i], target, this.data, this.options.seriesDefaults, this.options.series[i], this);
            }
            for (var j=0; j<this.preSeriesInitHooks.hooks.length; j++) {
                this.preSeriesInitHooks.hooks[j].call(this.series[i], target, this.data, this.options.seriesDefaults, this.options.series[i], this);
            }
            // this.populatePlotData(this.series[i], i);
            this.series[i]._plotDimensions = this._plotDimensions;
            this.series[i].init(i, this.grid.borderWidth, this);
            for (var j=0; j<$.jqplot.postSeriesInitHooks.length; j++) {
                $.jqplot.postSeriesInitHooks[j].call(this.series[i], target, this.data, this.options.seriesDefaults, this.options.series[i], this);
            }
            for (var j=0; j<this.postSeriesInitHooks.hooks.length; j++) {
                this.postSeriesInitHooks.hooks[j].call(this.series[i], target, this.data, this.options.seriesDefaults, this.options.series[i], this);
            }
            this._sumy += this.series[i]._sumy;
            this._sumx += this.series[i]._sumx;
        }

        for (var i=0, l=_axisNames.length; i<l; i++) {
            name = _axisNames[i];
            axis = this.axes[name];

            axis._plotDimensions = this._plotDimensions;
            axis.init();
            if (axis.borderColor == null) {
                if (name.charAt(0) !== 'x' && axis.useSeriesColor === true && axis.show) {
                    axis.borderColor = axis._series[0].color;
                }
                else {
                    axis.borderColor = this.grid.borderColor;
                }
            }
        }
        
        if (this.sortData) {
            sortData(this.series);
        }
        this.grid.init();
        this.grid._axes = this.axes;
        
        this.legend._series = this.series;

        for (var i=0, l=$.jqplot.postInitHooks.length; i<l; i++) {
            $.jqplot.postInitHooks[i].call(this, target, this.data, options);
        }

        for (var i=0, l=this.postInitHooks.hooks.length; i<l; i++) {
            this.postInitHooks.hooks[i].call(this, target, this.data, options);
        }
    };

    // method: replot
    // Does a reinitialization of the plot followed by
    // a redraw.  Method could be used to interactively
    // change plot characteristics and then replot.
    //
    // Parameters:
    // options - Options used for replotting.
    //
    // Properties:
    // clear - false to not clear (empty) the plot container before replotting (default: true).
    // resetAxes - true to reset all axes min, max, numberTicks and tickInterval setting so axes will rescale themselves.
    //             optionally pass in list of axes to reset (e.g. ['xaxis', 'y2axis']) (default: false).
    this.replot = function(options) {
        var opts =  options || {};
        var data = opts.data || null;
        var clear = (opts.clear === false) ? false : true;
        var resetAxes = opts.resetAxes || false;
        delete opts.data;
        delete opts.clear;
        delete opts.resetAxes;

        this.target.trigger('jqplotPreReplot');
        
        if (clear) {
            this.destroy();
        }
        // if have data or other options, full reinit.
        // otherwise, quickinit.
        if (data || !$.isEmptyObject(opts)) {
            this.reInitialize(data, opts);
        }
        else {
            this.quickInit();
        }

        if (resetAxes) {
            this.resetAxesScale(resetAxes, typeof resetAxes === 'object' ? resetAxes : {});
        }
        this.draw();
        this.target.trigger('jqplotPostReplot');
    };

    // sort the series data in increasing order.
    function sortData(series) {
        var d, sd, pd, ppd, ret;
        for (var i=0; i<series.length; i++) {
            var check;
            var bat = [series[i].data, series[i]._stackData, series[i]._plotData, series[i]._prevPlotData];
            for (var n=0; n<4; n++) {
                check = true;
                d = bat[n];
                if (series[i]._stackAxis == 'x') {
                    for (var j = 0; j < d.length; j++) {
                        if (typeof(d[j][1]) != "number") {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        d.sort(function(a,b) { return a[1] - b[1]; });
                    }
                }
                else {
                    for (var j = 0; j < d.length; j++) {
                        if (typeof(d[j][0]) != "number") {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        d.sort(function(a,b) { return a[0] - b[0]; });
                    }
                }
            }
           
        }
    }
    
    // populate the _stackData and _plotData arrays for the plot and the series.
    this.populatePlotData = function(series, index) {
        // if a stacked chart, compute the stacked data
        this._plotData = [];
        this._stackData = [];
        series._stackData = [];
        series._plotData = [];
        series._sumy = 0;
        series._sumx = 0;
        //var plotValues = {x:[], y:[]};
        if (this.stackSeries && !series.disableStack) {
            series._stack = true;
            var sidx = series._stackAxis == 'x' ? 0 : 1;
            var idx = sidx ? 0 : 1;
            // push the current data into stackData
            //this._stackData.push(this.series[i].data);
            var temp = $.extend(true, [], series.data);
            // create the data that will be plotted for this series
            var plotdata = $.extend(true, [], series.data);
            // for first series, nothing to add to stackData.
            for (var j=0; j<index; j++) {
                var cd = this.series[j].data;
                for (var k=0; k<cd.length; k++) {
                    temp[k][0] += cd[k][0];
                    temp[k][1] += cd[k][1];
                    // only need to sum up the stack axis column of data
                    plotdata[k][sidx] += cd[k][sidx];
                }
            }
            /*for (var i=0; i<plotdata.length; i++) {
                plotValues.x.push(plotdata[i][0]);
                plotValues.y.push(plotdata[i][1]);
            }*/
            this._plotData.push(plotdata);
            this._stackData.push(temp);
            series._stackData = temp;
            series._plotData = plotdata;
            //series._plotValues = plotValues;
            
            cd = j = sidx = idx = temp = plotdata = null;
        }
        else {
           /* for (var i=0; i<series.data.length; i++) {
                plotValues.x.push(series.data[i][0]);
                plotValues.y.push(series.data[i][1]);
                
                series._sumy += series.data[i][1];
                series._sumx += series.data[i][0];
            }*/
        	if (series.renderer.constructor == $.jqplot.MekkoRenderer) {
        		series._sumy += series.data[i][1];
            series._sumx += series.data[i][0];
        	}
            this._stackData.push(series.data);
            this.series[index]._stackData = series.data;
            this._plotData.push(series.data);
            series._plotData = series.data;
            //series._plotValues = plotValues;
        }
        if (index>0) {
            series._prevPlotData = this.series[index-1]._plotData;
        }
        /*series._sumy = 0;
        series._sumx = 0;
        for (i=series.data.length-1; i>-1; i--) {
            series._sumy += series.data[i][1];
            series._sumx += series.data[i][0];
        }*/
        i = null;
        plotValues = null;
    };
    
	  this.computePlotData = function() {
	      this._plotData = [];
	      this._stackData = [];
	      var series,
	          index,
	          l;
	
	
	      for (index=0, l=this.series.length; index<l; index++) {
	          series = this.series[index];
	          this._plotData.push([]);
	          this._stackData.push([]);
	          var cd = series.data;
	          var plotValues = {x:[], y:[]};
	
	          if (this.stackSeries && !series.disableStack) {
			          this._plotData[index] = $.extend(true, [], cd);
			          this._stackData[index] = $.extend(true, [], cd);
			          series._plotData = this._plotData[index];
			          series._stackData = this._stackData[index];
			          
	              series._stack = true;
	              ///////////////////////////
	              // have to check for nulls
	              ///////////////////////////
	              var sidx = (series._stackAxis === 'x') ? 0 : 1;
	
	              for (var k=0, cdl=cd.length; k<cdl; k++) {
	                  var temp = cd[k][sidx];
	                  if (temp == null) {
	                      temp = 0;
	                  }
	                  this._plotData[index][k][sidx] = temp;
	                  this._stackData[index][k][sidx] = temp;
	
	                  if (index > 0) {
	                      for (var j=index; j--;) {
	                          var prevval = this._plotData[j][k][sidx];
	                          // only need to sum up the stack axis column of data
	                          // and only sum if it is of same sign.
	                          // if previous series isn't same sign, keep looking
	                          // at earlier series untill we find one of same sign.
	                          if (temp * prevval >= 0) {
	                              this._plotData[index][k][sidx] += prevval;
	                              this._stackData[index][k][sidx] += prevval;
	                              break;
	                          } 
	                      }
	                  }
	              }
	
	          }
	          else {
			          this._plotData[index] = cd;
			          this._stackData[index] = cd;
			          series._plotData = this._plotData[index];
			          series._stackData = this._stackData[index];
			          
	              /*for (var i=0; i<series.data.length; i++) {
	                  plotValues.x.push(series.data[i][0]);
	                  plotValues.y.push(series.data[i][1]);
	              }*/
	              this._stackData.push(series.data);
	              this.series[index]._stackData = series.data;
	              this._plotData.push(series.data);
	              series._plotData = series.data;
	              //series._plotValues = plotValues;
	          }
	          if (index>0) {
	              series._prevPlotData = this.series[index-1]._plotData;
	          }
	          series._sumy = 0;
	          series._sumx = 0;
	          /*for (i=series.data.length-1; i>-1; i--) {
	              series._sumy += series.data[i][1];
	              series._sumx += series.data[i][0];
	          }*/
	      }
	  };

		this.parseOptions = function(options) {
			for (var i = 0; i < this.preParseOptionsHooks.hooks.length; i++) {
				this.preParseOptionsHooks.hooks[i].call(this, options);
			}
			for (var i = 0; i < $.jqplot.preParseOptionsHooks.length; i++) {
				$.jqplot.preParseOptionsHooks[i].call(this, options);
			}
			this.options = $.extend(true, {}, this.defaults, options);
			
			// customizing
			if (!this.options.multiCanvas || this.data.length > this.multiCanvasLimit) {
				this.seriesCanvas = new $.jqplot.GenericCanvas();
				this.seriesHighlightCanvas = new $.jqplot.GenericCanvas();
				
				this.options.multiCanvas = false;
			}
			
			var opts = this.options;
			this.animate = opts.animate;
			this.animateReplot = opts.animateReplot;
			this.stackSeries = opts.stackSeries;
			if ($.isPlainObject(opts.fillBetween)) {

				var temp = ['series1', 'series2', 'color', 'baseSeries', 'fill'], tempi;

				for (var i = 0, l = temp.length; i < l; i++) {
					tempi = temp[i];
					if (opts.fillBetween[tempi] != null) {
						this.fillBetween[tempi] = opts.fillBetween[tempi];
					}
				}
			}

			if (opts.seriesColors) {
				this.seriesColors = opts.seriesColors;
			}
			if (opts.negativeSeriesColors) {
				this.negativeSeriesColors = opts.negativeSeriesColors;
			}
			if (opts.captureRightClick) {
				this.captureRightClick = opts.captureRightClick;
			}
			this.defaultAxisStart = (options && options.defaultAxisStart != null) ? options.defaultAxisStart : this.defaultAxisStart;
			this.colorGenerator.setColors(this.seriesColors);
			this.negativeColorGenerator.setColors(this.negativeSeriesColors);
			// var cg = new this.colorGenerator(this.seriesColors);
			// var ncg = new this.colorGenerator(this.negativeSeriesColors);
			// this._gridPadding = this.options.gridPadding;
			$.extend(true, this._gridPadding, opts.gridPadding);
			this.sortData = (opts.sortData != null) ? opts.sortData : this.sortData;
			for (var i = 0; i < 12; i++) {
				var n = _axisNames[i];
				var axis = this.axes[n];
				axis._options = $.extend(true, {}, opts.axesDefaults, opts.axes[n]);
				$.extend(true, axis, opts.axesDefaults, opts.axes[n]);
				axis._plotWidth = this._width;
				axis._plotHeight = this._height;
			}
			// if (this.data.length == 0) {
			//     this.data = [];
			//     for (var i=0; i<this.options.series.length; i++) {
			//         this.data.push(this.options.series.data);
			//     }
			// }

			var normalizeData = function(data, dir, start) {
				// return data as an array of point arrays,
				// in form [[x1,y1...], [x2,y2...], ...]
				var temp = [];
				var i, l;
				dir = dir || 'vertical';
				if (!$.isArray(data[0])) {
					// we have a series of scalars.  One line with just y values.
					// turn the scalar list of data into a data array of form:
					// [[1, data[0]], [2, data[1]], ...]
					for ( i = 0, l = data.length; i < l; i++) {
						if (dir == 'vertical') {
							temp.push([start + i, data[i]]);
						} else {
							temp.push([data[i], start + i]);
						}
					}
				} else {
					// we have a properly formatted data series, copy it.
					$.extend(true, temp, data);
				}
				return temp;
			};

			var colorIndex = 0;
			this.series = [];
			for (var i = 0; i < this.data.length; i++) {
				var sopts = $.extend(true, {
					index : i
				}, {
					seriesColors : this.seriesColors,
					negativeSeriesColors : this.negativeSeriesColors
				}, this.options.seriesDefaults, this.options.series[i], {
					rendererOptions : {
						animation : {
							show : this.animate
						}
					}
				});
				// customizing (2013-02-12, Roy Choi)
				var tempGenericCanvas;
				if (!this.options.multiCanvas) {
					tempGenericCanvas = $.jqplot.GenericCanvas;
					$.jqplot.GenericCanvas = function() {
						return null;
					};
				}
				// pass in options in case something needs set prior to initialization.
				var temp = new Series(sopts);
				// customizing (2013-02-12, Roy Choi)
				if (!this.options.multiCanvas) {
					$.jqplot.GenericCanvas = tempGenericCanvas;
					temp.canvas = this.seriesCanvas;
				}
				for (var j = 0; j < $.jqplot.preParseSeriesOptionsHooks.length; j++) {
					$.jqplot.preParseSeriesOptionsHooks[j].call(temp, this.options.seriesDefaults, this.options.series[i]);
				}
				for (var j = 0; j < this.preParseSeriesOptionsHooks.hooks.length; j++) {
					this.preParseSeriesOptionsHooks.hooks[j].call(temp, this.options.seriesDefaults, this.options.series[i]);
				}
				// Now go back and apply the options to the series.  Really should just do this during initializaiton, but don't want to
				// mess up preParseSeriesOptionsHooks at this point.
				$.extend(true, temp, sopts);
				var dir = 'vertical';
				if (temp.renderer === $.jqplot.BarRenderer && temp.rendererOptions && temp.rendererOptions.barDirection == 'horizontal') {
					dir = 'horizontal';
					temp._stackAxis = 'x';
					temp._primaryAxis = '_yaxis';
				}
				// enhancement (2011-10-24, Roy Choi)
				temp.data = ($.isArray(this.data[i][0])) ? this.data[i] : normalizeData(this.data[i], dir, this.defaultAxisStart);
				//temp.data = normalizeData(this.data[i], dir, this.defaultAxisStart);
				switch (temp.xaxis) {
					case 'xaxis':
						temp._xaxis = this.axes.xaxis;
						break;
					case 'x2axis':
						temp._xaxis = this.axes.x2axis;
						break;
					default:
						break;
				}
				temp._yaxis = this.axes[temp.yaxis];
				temp._xaxis._series.push(temp);
				temp._yaxis._series.push(temp);
				if (temp.show) {
					temp._xaxis.show = true;
					temp._yaxis.show = true;
				} else {
					if (temp._xaxis.scaleToHiddenSeries) {
						temp._xaxis.show = true;
					}
					if (temp._yaxis.scaleToHiddenSeries) {
						temp._yaxis.show = true;
					}
				}

				// // parse the renderer options and apply default colors if not provided
				// if (!temp.color && temp.show != false) {
				//     temp.color = cg.next();
				//     colorIndex = cg.getIndex() - 1;;
				// }
				// if (!temp.negativeColor && temp.show != false) {
				//     temp.negativeColor = ncg.get(colorIndex);
				//     ncg.setIndex(colorIndex);
				// }
				if (!temp.label) {
					temp.label = 'Series ' + (i + 1).toString();
				}
				// temp.rendererOptions.show = temp.show;
				// $.extend(true, temp.renderer, {color:this.seriesColors[i]}, this.rendererOptions);
				this.series.push(temp);
				for (var j = 0; j < $.jqplot.postParseSeriesOptionsHooks.length; j++) {
					$.jqplot.postParseSeriesOptionsHooks[j].call(this.series[i], this.options.seriesDefaults, this.options.series[i]);
				}
				for (var j = 0; j < this.postParseSeriesOptionsHooks.hooks.length; j++) {
					this.postParseSeriesOptionsHooks.hooks[j].call(this.series[i], this.options.seriesDefaults, this.options.series[i]);
				}
			}

			// copy the grid and title options into this object.
			$.extend(true, this.grid, this.options.grid);
			// if axis border properties aren't set, set default.
			for (var i = 0, l = _axisNames.length; i < l; i++) {
				var n = _axisNames[i];
				var axis = this.axes[n];
				if (axis.borderWidth == null) {
					axis.borderWidth = this.grid.borderWidth;
				}
			}

			if ( typeof this.options.title == 'string') {
				this.title.text = this.options.title;
			} else if ( typeof this.options.title == 'object') {
				$.extend(true, this.title, this.options.title);
			}
			this.title._plotWidth = this._width;
			this.legend.setOptions(this.options.legend);

			for (var i = 0; i < $.jqplot.postParseOptionsHooks.length; i++) {
				$.jqplot.postParseOptionsHooks[i].call(this, options);
			}
			for (var i = 0; i < this.postParseOptionsHooks.hooks.length; i++) {
				this.postParseOptionsHooks.hooks[i].call(this, options);
			}
			
			
		};

		// method: draw
		// Draws all elements of the plot into the container.
		// Does not clear the container before drawing.
		this.draw = function() {
			
			if (this.drawIfHidden || this.target.is(':visible')) {
				this.target.trigger('jqplotPreDraw');
				var i, j, l, tempseries;
				for ( i = 0, l = $.jqplot.preDrawHooks.length; i < l; i++) {
					$.jqplot.preDrawHooks[i].call(this);
				}
				for ( i = 0, l = this.preDrawHooks.hooks.length; i < l; i++) {
					this.preDrawHooks.hooks[i].apply(this, this.preDrawSeriesHooks.args[i]);
				}
				// create an underlying canvas to be used for special features.
				this.target.append(this.baseCanvas.createElement({
					left : 0,
					right : 0,
					top : 0,
					bottom : 0
				}, 'jqplot-base-canvas', null, this));
				this.baseCanvas.setContext();
				this.target.append(this.title.draw());
				this.title.pack({
					top : 0,
					left : 0
				});

				// make room  for the legend between the grid and the edge.
				// pass a dummy offsets object and a reference to the plot.
				var legendElem = this.legend.draw({}, this);

				var gridPadding = {
					top : 0,
					left : 0,
					bottom : 0,
					right : 0
				};

				if (this.legend.placement == "outsideGrid" && this.legend.show) {
					// temporarily append the legend to get dimensions
					this.target.append(legendElem);
					switch (this.legend.location) {
						case 'n':
							gridPadding.top += this.legend.getHeight() + this._defaultGridPadding.top;
							break;
						case 's':
							gridPadding.bottom += this.legend.getHeight();
							break;
						case 'ne':
						case 'e':
						case 'se':
							gridPadding.right += this.legend.getWidth();
							break;
						case 'nw':
						case 'w':
						case 'sw':
							gridPadding.left += this.legend.getWidth();
							break;
						default:
							// same as 'ne'
							gridPadding.right += this.legend.getWidth();
							break;
					}
					legendElem = legendElem.detach();
				}

				var ax = this.axes;
				var name;
				// draw the yMidAxis first, so xaxis of pyramid chart can adjust itself if needed.
				for ( i = 0; i < 12; i++) {
					name = _axisNames[i];
					this.target.append(ax[name].draw(this.baseCanvas._ctx, this));
					ax[name].set();
				}
				if (ax.yaxis.show) {
					gridPadding.left += ax.yaxis.getWidth();
				}
				var ra = ['y2axis', 'y3axis', 'y4axis', 'y5axis', 'y6axis', 'y7axis', 'y8axis', 'y9axis'];
				var rapad = [0, 0, 0, 0, 0, 0, 0, 0];
				var gpr = 0;
				var n;
				for ( n = 0; n < 8; n++) {
					if (ax[ra[n]].show) {
						gpr += ax[ra[n]].getWidth();
						rapad[n] = gpr;
					}
				}
				gridPadding.right += gpr;
				if (ax.x2axis.show) {
					gridPadding.top += ax.x2axis.getHeight();
				}
				if (this.title.show) {
					gridPadding.top += this.title.getHeight();
				}
				if (ax.xaxis.show) {
					gridPadding.bottom += ax.xaxis.getHeight();
				}
				
				// custom
				if (this.options.customAxes && $.isPlainObject(this.options.customAxes)) {
					var customAxesPaddingTop = 0;
					var customAxesPaddingBottom = 0;
					$.each(this.options.customAxes, function(key, ca) {
						if (ca.location === 'top' && customAxesPaddingTop < $.isNumeric(ca.padding) ? ca.padding : 0) {
							customAxesPaddingTop = ca.padding;
						} else if (ca.location === 'bottom' && customAxesPaddingBottom < $.isNumeric(ca.padding) ? ca.padding : 0) {
							customAxesPaddingBottom = ca.padding;
						}
					});
					
					if (customAxesPaddingTop) {
						gridPadding.top += customAxesPaddingTop;
					}
					if (customAxesPaddingBottom) {
						gridPadding.bottom += customAxesPaddingBottom;
					}
				}

				// end of gridPadding adjustments.

				// if user passed in gridDimensions option, check against calculated gridPadding
				if (this.options.gridDimensions && $.isPlainObject(this.options.gridDimensions)) {
					var gdw = parseInt(this.options.gridDimensions.width, 10) || 0;
					var gdh = parseInt(this.options.gridDimensions.height, 10) || 0;
					var widthAdj = (this._width - gridPadding.left - gridPadding.right - gdw) / 2;
					var heightAdj = (this._height - gridPadding.top - gridPadding.bottom - gdh) / 2;

					if (heightAdj >= 0 && widthAdj >= 0) {
						gridPadding.top += heightAdj;
						gridPadding.bottom += heightAdj;
						gridPadding.left += widthAdj;
						gridPadding.right += widthAdj;
					}
				}
				var arr = ['top', 'bottom', 'left', 'right'];
				for (var n in arr) {
					//if (/*this._gridPadding[arr[n]] == null &&*/ gridPadding[arr[n]] > 0) {
						this._gridPadding[arr[n]] = gridPadding[arr[n]];
					if (this._gridPadding[arr[n]] == null || this._gridPadding[arr[n]] === 0) {
						this._gridPadding[arr[n]] = this._defaultGridPadding[arr[n]];
					}
				}

				var legendPadding = this._gridPadding;

				if (this.legend.placement === 'outsideGrid') {
					legendPadding = {
						top : this.title.getHeight(),
						left : 0,
						right : 0,
						bottom : 0
					};
					if (this.legend.location === 's') {
						legendPadding.left = this._gridPadding.left;
						legendPadding.right = this._gridPadding.right;
					}
				}

				ax.xaxis.pack({
					position : 'absolute',
					bottom : this._gridPadding.bottom - ax.xaxis.getHeight(),
					left : 0,
					width : this._width
				}, {
					min : this._gridPadding.left,
					max : this._width - this._gridPadding.right
				});
				ax.yaxis.pack({
					position : 'absolute',
					top : 0,
					left : this._gridPadding.left - ax.yaxis.getWidth(),
					height : this._height
				}, {
					min : this._height - this._gridPadding.bottom,
					max : this._gridPadding.top
				});
				ax.x2axis.pack({
					position : 'absolute',
					top : this._gridPadding.top - ax.x2axis.getHeight(),
					left : 0,
					width : this._width
				}, {
					min : this._gridPadding.left,
					max : this._width - this._gridPadding.right
				});
				for ( i = 8; i > 0; i--) {
					ax[ra[i - 1]].pack({
						position : 'absolute',
						top : 0,
						right : this._gridPadding.right - rapad[i - 1]
					}, {
						min : this._height - this._gridPadding.bottom,
						max : this._gridPadding.top
					});
				}
				var ltemp = (this._width - this._gridPadding.left - this._gridPadding.right) / 2.0 + this._gridPadding.left - ax.yMidAxis.getWidth() / 2.0;
				ax.yMidAxis.pack({
					position : 'absolute',
					top : 0,
					left : ltemp,
					zIndex : 9,
					textAlign : 'center'
				}, {
					min : this._height - this._gridPadding.bottom,
					max : this._gridPadding.top
				});

				this.target.append(this.grid.createElement(this._gridPadding, this));
				this.grid.draw();

				var series = this.series;
				var seriesLength = series.length;
				if (this.options.multiCanvas) {
					// put the shadow canvases behind the series canvases so shadows don't overlap on stacked bars.
					for ( i = 0, l = seriesLength; i < l; i++) {
						// draw series in order of stacking.  This affects only
						// order in which canvases are added to dom.
						j = this.seriesStack[i];
						this.target.append(series[j].shadowCanvas.createElement(this._gridPadding, 'jqplot-series-shadowCanvas', null, this));
						series[j].shadowCanvas.setContext();
						series[j].shadowCanvas._elem.data('seriesIndex', j);
					}

					for ( i = 0, l = seriesLength; i < l; i++) {
						// draw series in order of stacking.  This affects only
						// order in which canvases are added to dom.
						j = this.seriesStack[i];
						this.target.append(series[j].canvas.createElement(this._gridPadding, 'jqplot-series-canvas', null, this));
						series[j].canvas.setContext();
						series[j].canvas._elem.data('seriesIndex', j);
					}
				} else {
					this.target.append(this.seriesCanvas.createElement(this._gridPadding, 'jqplot-series-canvas', null, this));
					this.seriesCanvas.setContext();
					this.target.append(this.seriesHighlightCanvas.createElement(this._gridPadding, 'jqplot-series-canvas', this._plotDimensions, this));
					this.seriesHighlightCanvas.setContext();
				}

				// Need to use filled canvas to capture events in IE.
				// Also, canvas seems to block selection of other elements in document on FF.
				this.target.append(this.eventCanvas.createElement(this._gridPadding, 'jqplot-event-canvas', null, this));
				this.eventCanvas.setContext();
				this.eventCanvas._ctx.fillStyle = 'rgba(0,0,0,0)';
				this.eventCanvas._ctx.fillRect(0, 0, this.eventCanvas._ctx.canvas.width, this.eventCanvas._ctx.canvas.height);

				// bind custom event handlers to regular events.
				this.bindCustomEvents();

				// draw legend before series if the series needs to know the legend dimensions.
				if (this.legend.preDraw) {
					if(this.legend.show) {
						this.eventCanvas._elem.before(legendElem);
					}
					this.legend.pack(legendPadding);
					if (this.legend._elem) {
						this.drawSeries({
							legendInfo : {
								location : this.legend.location,
								placement : this.legend.placement,
								width : this.legend.getWidth(),
								height : this.legend.getHeight(),
								xoffset : this.legend.xoffset,
								yoffset : this.legend.yoffset
							}
						});
					} else {
						this.drawSeries();
					}
				} else {// draw series before legend
					this.drawSeries();
					if (seriesLength && this.legend.show) {
						$(series[seriesLength - 1].canvas._elem).after(legendElem);
					}
					this.legend.pack(legendPadding);
				}

				// register event listeners on the overlay canvas
				for (var i = 0, l = $.jqplot.eventListenerHooks.length; i < l; i++) {
					// in the handler, this will refer to the eventCanvas dom element.
					// make sure there are references back into plot objects.
					this.eventCanvas._elem.bind($.jqplot.eventListenerHooks[i][0], {
						plot : this
					}, $.jqplot.eventListenerHooks[i][1]);
				}

				// register event listeners on the overlay canvas
				for (var i = 0, l = this.eventListenerHooks.hooks.length; i < l; i++) {
					// in the handler, this will refer to the eventCanvas dom element.
					// make sure there are references back into plot objects.
					this.eventCanvas._elem.bind(this.eventListenerHooks.hooks[i][0], {
						plot : this
					}, this.eventListenerHooks.hooks[i][1]);
				}

				var fb = this.fillBetween;
				if (fb.fill && fb.series1 !== fb.series2 && fb.series1 < seriesLength && fb.series2 < seriesLength && series[fb.series1]._type === 'line' && series[fb.series2]._type === 'line') {
					this.doFillBetweenLines();
				}

				for (var i = 0, l = $.jqplot.postDrawHooks.length; i < l; i++) {
					$.jqplot.postDrawHooks[i].call(this);
				}

				for (var i = 0, l = this.postDrawHooks.hooks.length; i < l; i++) {
					this.postDrawHooks.hooks[i].apply(this, this.postDrawHooks.args[i]);
				}

				if (this.target.is(':visible')) {
					this._drawCount += 1;
				}

				var temps, tempr, sel, _els, multiCanvas = this.options.multiCanvas;
				// customizing
				// ughh.  ideally would hide all series then show them.
				for ( i = 0, l = seriesLength; i < l; i++) {
					temps = series[i];
					tempr = temps.renderer;
					sel = '.jqplot-point-label.jqplot-series-' + i;
					// customizing
					if (multiCanvas && tempr.animation && tempr.animation._supported && tempr.animation.show && (this._drawCount < 2 || this.animateReplot)) {
						_els = this.target.find(sel);
						_els.stop(true, true).hide();
						temps.canvas._elem.stop(true, true).hide();
						//temps.shadowCanvas._elem.stop(true, true).hide();
						temps.canvas._elem.jqplotEffect('blind', {
							mode : 'show',
							direction : tempr.animation.direction
						}, tempr.animation.speed);
						//temps.shadowCanvas._elem.jqplotEffect('blind', {mode: 'show', direction: tempr.animation.direction}, tempr.animation.speed);
						_els.fadeIn(tempr.animation.speed * 0.8);
					}
				}
				_els = null;

				this.target.trigger('jqplotPostDraw', [this]);
			}
			
		};

		// method: redrawSeries
		// customizing (2012-08-21, Roy Choi)
		// only useable on single canvase mode
		this.redrawSeries = function(axes) {
			for ( i = 0; i < $.jqplot.preRedrawSeriesHooks.length; i++) {
				$.jqplot.preRedrawSeriesHooks[i].call(this);
			}

			this._redrawInset = true;

			axes = axes || {};

			this.resetAxesScale(true);

			var i, j;

			for (var ax in axes) {
				$.extend(true, this.axes[ax], axes[ax]);
				this.axes[ax]._ticks = [];
			}

			if ( typeof axes !== 'object' || Object.keys(axes).length === 0) {
				for (var ax in this.axes) {
					this.axes[ax]._ticks = [];
				}
			}

			ax = null;

			this._sumy = 0;
			this._sumx = 0;
			for ( i = 0; i < this.series.length; i++) {
				this._sumy += this.series[i]._sumy;
				this._sumx += this.series[i]._sumx;
			}

			if (this.drawIfHidden || this.target.is(':visible')) {
				this.target.children('.jqplot-axis').each(function() {
					$(this).remove();
				});

				var gridPadding = {
					top : 0,
					left : 0,
					bottom : 0,
					right : 0
				};

				if (this.legend.placement == "outsideGrid" && this.legend.show) {// customizing (2012-05-03, Roy Choi)
					// temporarily append the legend to get dimensions
					//this.target.append(legendElem);		// customizing (2012-05-03, Roy Choi)
					switch (this.legend.location) {
						case 'n':
							gridPadding.top += this.legend.getHeight() + 10;
							// customizing (2012-05-03, Roy Choi)
							break;
						case 's':
							gridPadding.bottom += this.legend.getHeight() + 10;
							break;
						case 'ne':
						case 'e':
						case 'se':
							gridPadding.right += this.legend.getWidth() + 10;
							break;
						case 'nw':
						case 'w':
						case 'sw':
							gridPadding.left += this.legend.getWidth() + 10;
							break;
						default:
							// same as 'ne'
							gridPadding.right += this.legend.getWidth() + 10;
							break;
					}
					//legendElem = legendElem.detach();		// customizing (2012-05-03, Roy Choi)
				}

				var ax = this.axes;
				var name;

				// draw the yMidAxis first, so xaxis of pyramid chart can adjust itself if needed.
				for ( i = 0; i < 12; i++) {
					name = _axisNames[i];
					this.target.append(ax[name].draw(this.baseCanvas._ctx, this));
					ax[name].set();
				}
				if (ax.yaxis.show) {
					gridPadding.left += ax.yaxis.getWidth();
				}
				var ra = ['y2axis', 'y3axis', 'y4axis', 'y5axis', 'y6axis', 'y7axis', 'y8axis', 'y9axis'];
				var rapad = [0, 0, 0, 0, 0, 0, 0, 0];
				var gpr = 0;
				var n;

				for ( n = 0; n < 8; n++) {
					if (ax[ra[n]].show) {
						gpr += ax[ra[n]].getWidth();
						rapad[n] = gpr;
					}
				}
				gridPadding.right += gpr;
				if (ax.x2axis.show) {
					gridPadding.top += ax.x2axis.getHeight();
				}
				if (this.title.show) {
					gridPadding.top += this.title.getHeight();
				}
				if (ax.xaxis.show) {
					gridPadding.bottom += ax.xaxis.getHeight();
				}

				// end of gridPadding adjustments.

				// if user passed in gridDimensions option, check against calculated gridPadding
				if (this.options.gridDimensions && $.isPlainObject(this.options.gridDimensions)) {
					var gdw = parseInt(this.options.gridDimensions.width, 10) || 0;
					var gdh = parseInt(this.options.gridDimensions.height, 10) || 0;
					var widthAdj = (this._width - gridPadding.left - gridPadding.right - gdw) / 2;
					var heightAdj = (this._height - gridPadding.top - gridPadding.bottom - gdh) / 2;

					if (heightAdj >= 0 && widthAdj >= 0) {
						gridPadding.top += heightAdj;
						gridPadding.bottom += heightAdj;
						gridPadding.left += widthAdj;
						gridPadding.right += widthAdj;
					}
					gdw = gdh = widthAdj = heightAdj = null;
				}
				var arr = ['top', 'bottom', 'left', 'right'];
				for (var n in arr) {
					// customizing (2012-05-03, Roy Choi) - legend resize
					this._gridPadding[arr[n]] = gridPadding[arr[n]];
					if (this._defaultGridPadding[arr[n]] && this._gridPadding[arr[n]] <= 0) {
						this._gridPadding[arr[n]] = this._defaultGridPadding[arr[n]];
					}
				}
				n = null;

				ax.xaxis.pack({
					position : 'absolute',
					bottom : this._gridPadding.bottom - ax.xaxis.getHeight(),
					left : 0,
					width : this._width
				}, {
					min : this._gridPadding.left,
					max : this._width - this._gridPadding.right
				});
				ax.yaxis.pack({
					position : 'absolute',
					top : 0,
					left : this._gridPadding.left - ax.yaxis.getWidth(),
					height : this._height
				}, {
					min : this._height - this._gridPadding.bottom,
					max : this._gridPadding.top
				});
				ax.x2axis.pack({
					position : 'absolute',
					top : this._gridPadding.top - ax.x2axis.getHeight(),
					left : 0,
					width : this._width
				}, {
					min : this._gridPadding.left,
					max : this._width - this._gridPadding.right
				});
				for ( i = 8; i > 0; i--) {
					ax[ra[i - 1]].pack({
						position : 'absolute',
						top : 0,
						right : this._gridPadding.right - rapad[i - 1]
					}, {
						min : this._height - this._gridPadding.bottom,
						max : this._gridPadding.top
					});
				}
				var ltemp = (this._width - this._gridPadding.left - this._gridPadding.right) / 2.0 + this._gridPadding.left - ax.yMidAxis.getWidth() / 2.0;
				ax.yMidAxis.pack({
					position : 'absolute',
					top : 0,
					left : ltemp,
					zIndex : 9,
					textAlign : 'center'
				}, {
					min : this._height - this._gridPadding.bottom,
					max : this._gridPadding.top
				});

				this.grid.draw();

				for ( i = 0; i < this.series.length; i++) {
					// draw series in order of stacking.  This affects only
					// order in which canvases are added to dom.
					j = this.seriesStack[i];
					if (this.series[j].shadowCanvas) {
						this.target.append(this.series[j].shadowCanvas.createElement(this._gridPadding, 'jqplot-series-shadowCanvas', null, this));
						this.series[j].shadowCanvas.setContext();
						this.series[j].shadowCanvas._elem.data('seriesIndex', j);
					}
				}

				if (this.options.multiCanvas) {
					/*this.seriesCanvas.setContext();
					 this.seriesHighlightCanvas.setContext();*/
					for ( i = 0; i < this.series.length; i++) {
						// draw series in order of stacking.  This affects only
						// order in which canvases are added to dom.
						j = this.seriesStack[i];
						this.target.append(this.series[j].canvas.createElement(this._gridPadding, 'jqplot-series-canvas', null, this));
						this.series[j].canvas.setContext();
						this.series[j].canvas._elem.data('seriesIndex', j);
					}
				} else {
					this.seriesCanvas.setContext();
					//this.seriesHighlightCanvas.setContext();
					for ( i = 0; i < this.series.length; i++) {
						// draw series in order of stacking.  This affects only
						// order in which canvases are added to dom.
						j = this.seriesStack[i];
						this.series[j].canvas = this.seriesCanvas;
					}

					// draw legend before series if the series needs to know the legend dimensions.
					var ctx = this.seriesCanvas._ctx;
					ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
					ctx = null;
				}

				this.drawSeries();
				if ( typeof this.highlightSeriesIndex == 'number') {
					this.series[this.highlightSeriesIndex].highlighted = false;
					this.moveSeriesToFront(this.highlightSeriesIndex);
				}

				if (this.target.is(':visible')) {
					this._drawCount += 1;
				}

				i = j = gridPadding = ax = name = ra = rapad = gpr = n = arr = ltemp = null;
			}

			for ( i = 0; i < $.jqplot.postRedrawSeriesHooks.length; i++) {
				$.jqplot.postRedrawSeriesHooks[i].call(this);
			}

			i = j = null;

			this._redrawInset = false;
		};

		this.bindCustomEvents = function() {
			this.eventCanvas._elem.bind('click', {
				plot : this
			}, this.onClick);
			this.eventCanvas._elem.bind('dblclick', {
				plot : this
			}, this.onDblClick);
			this.eventCanvas._elem.bind('mousedown', {
				plot : this
			}, this.onMouseDown);
			this.eventCanvas._elem.bind('mousemove', {
				plot : this
			}, this.onMouseMove);
			this.eventCanvas._elem.bind('mouseenter', {
				plot : this
			}, this.onMouseEnter);
			this.eventCanvas._elem.bind('mouseleave', {
				plot : this
			}, this.onMouseLeave);
			this.eventCanvas._elem.bind('mouseup', {
				plot : this
			}, this.onMouseUp);
			this.eventCanvas._elem.bind('keydown', {
				plot : this
			}, this.onKeyDown);
			if (this.captureRightClick)
				this.eventCanvas._elem.bind('contextmenu', {
					plot : this
				}, this.onRightClick);
		};

		// customizing for key down (2013-02-20, Roy Choi)
		this.onKeyDown = function(ev) {
			var evt = jQuery.Event('jqplotKeyDown');
			$(this).trigger(evt, [ev.data.plot]);

			evt = null;
		};


    this.checkIntersection = function (gridpos, plot) {
        var series = plot.series;
        var i, j, k, s, r, x, y, theta, sm, sa, minang, maxang;
        var d0, d, p, pp, points, bw, hp;
        var threshold, t;
        for (k=plot.seriesStack.length-1; k>=0; k--) {
            i = plot.seriesStack[k];
            s = series[i];
            hp = s._highlightThreshold;
            switch (s.renderer.constructor) {
                case $.jqplot.BarRenderer:
                    x = gridpos.x;
                    y = gridpos.y;
                    for (j=0; j<s._barPoints.length; j++) {
                        points = s._barPoints[j];
                        p = s.gridData[j];
                        if (x>points[0][0] && x<points[2][0] && y>points[2][1] && y<points[0][1]) {
                            return {seriesIndex:s.index, pointIndex:j, gridData:p, data:s.data[j], points:s._barPoints[j]};
                        }
                    }
                    break;
                case $.jqplot.PyramidRenderer:
                    x = gridpos.x;
                    y = gridpos.y;
                    for (j=0; j<s._barPoints.length; j++) {
                        points = s._barPoints[j];
                        p = s.gridData[j];
                        if (x > points[0][0] + hp[0][0] && x < points[2][0] + hp[2][0] && y > points[2][1] && y < points[0][1]) {
                            return {seriesIndex:s.index, pointIndex:j, gridData:p, data:s.data[j], points:s._barPoints[j]};
                        }
                    }
                    break;
                
                case $.jqplot.DonutRenderer:
                    sa = s.startAngle/180*Math.PI;
                    x = gridpos.x - s._center[0];
                    y = gridpos.y - s._center[1];
                    r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
                    if (x > 0 && -y >= 0) {
                        theta = 2*Math.PI - Math.atan(-y/x);
                    }
                    else if (x > 0 && -y < 0) {
                        theta = -Math.atan(-y/x);
                    }
                    else if (x < 0) {
                        theta = Math.PI - Math.atan(-y/x);
                    }
                    else if (x == 0 && -y > 0) {
                        theta = 3*Math.PI/2;
                    }
                    else if (x == 0 && -y < 0) {
                        theta = Math.PI/2;
                    }
                    else if (x == 0 && y == 0) {
                        theta = 0;
                    }
                    if (sa) {
                        theta -= sa;
                        if (theta < 0) {
                            theta += 2*Math.PI;
                        }
                        else if (theta > 2*Math.PI) {
                            theta -= 2*Math.PI;
                        }
                    }
        
                    sm = s.sliceMargin/180*Math.PI;
                    if (r < s._radius && r > s._innerRadius) {
                        for (j=0; j<s.gridData.length; j++) {
                            minang = (j>0) ? s.gridData[j-1][1]+sm : sm;
                            maxang = s.gridData[j][1];
                            if (theta > minang && theta < maxang) {
                                return {seriesIndex:s.index, pointIndex:j, gridData:[gridpos.x,gridpos.y], data:s.data[j]};
                            }
                        }
                    }
                    break;
                    
                case $.jqplot.PieRenderer:
                    sa = s.startAngle/180*Math.PI;
                    x = gridpos.x - s._center[0];
                    y = gridpos.y - s._center[1];
                    r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
                    if (x > 0 && -y >= 0) {
                        theta = 2*Math.PI - Math.atan(-y/x);
                    }
                    else if (x > 0 && -y < 0) {
                        theta = -Math.atan(-y/x);
                    }
                    else if (x < 0) {
                        theta = Math.PI - Math.atan(-y/x);
                    }
                    else if (x == 0 && -y > 0) {
                        theta = 3*Math.PI/2;
                    }
                    else if (x == 0 && -y < 0) {
                        theta = Math.PI/2;
                    }
                    else if (x == 0 && y == 0) {
                        theta = 0;
                    }
                    if (sa) {
                        theta -= sa;
                        if (theta < 0) {
                            theta += 2*Math.PI;
                        }
                        else if (theta > 2*Math.PI) {
                            theta -= 2*Math.PI;
                        }
                    }
        
                    sm = s.sliceMargin/180*Math.PI;
                    if (r < s._radius) {
                        for (j=0; j<s.gridData.length; j++) {
                            minang = (j>0) ? s.gridData[j-1][1]+sm : sm;
                            maxang = s.gridData[j][1];
                            if (theta > minang && theta < maxang) {
                                return {seriesIndex:s.index, pointIndex:j, gridData:[gridpos.x,gridpos.y], data:s.data[j]};
                            }
                        }
                    }
                    break;
                    
                case $.jqplot.BubbleRenderer:
                    x = gridpos.x;
                    y = gridpos.y;
                    var ret = null;
                    
                    if (s.show) {
                        for (var j=0; j<s.gridData.length; j++) {
                            p = s.gridData[j];
                            d = Math.sqrt( (x-p[0]) * (x-p[0]) + (y-p[1]) * (y-p[1]) );
                            if (d <= p[2] && (d <= d0 || d0 == null)) {
                               d0 = d;
                               ret = {seriesIndex: i, pointIndex:j, gridData:p, data:s.data[j]};
                            }
                        }
                        if (ret != null) {
                            return ret;
                        }
                    }
                    break;
                    
                case $.jqplot.FunnelRenderer:
                    x = gridpos.x;
                    y = gridpos.y;
                    var v = s._vertices,
                        vfirst = v[0],
                        vlast = v[v.length-1],
                        lex,
                        rex,
                        cv;

                    // equations of right and left sides, returns x, y values given height of section (y value and 2 points)

                    function findedge (l, p1 , p2) {
                        var m = (p1[1] - p2[1])/(p1[0] - p2[0]);
                        var b = p1[1] - m*p1[0];
                        var y = l + p1[1];
    
                        return [(y - b)/m, y];
                    }

                    // check each section
                    lex = findedge(y, vfirst[0], vlast[3]);
                    rex = findedge(y, vfirst[1], vlast[2]);
                    for (j=0; j<v.length; j++) {
                        cv = v[j];
                        if (y >= cv[0][1] && y <= cv[3][1] && x >= lex[0] && x <= rex[0]) {
                            return {seriesIndex:s.index, pointIndex:j, gridData:null, data:s.data[j]};
                        }
                    }         
                    break;           
                
                case $.jqplot.LineRenderer:
                    x = gridpos.x;
                    y = gridpos.y;
                    r = s.renderer;
                    if (s.show) {
                        if ((s.fill || (s.renderer.bands.show && s.renderer.bands.fill)) && (!plot.plugins.highlighter || !plot.plugins.highlighter.show)) {
                            // first check if it is in bounding box
                            var inside = false;
                            if (x>s._boundingBox[0][0] && x<s._boundingBox[1][0] && y>s._boundingBox[1][1] && y<s._boundingBox[0][1]) { 
                                // now check the crossing number   
                                
                                var numPoints = s._areaPoints.length;
                                var ii;
                                var j = numPoints-1;

                                for(var ii=0; ii < numPoints; ii++) { 
                                    var vertex1 = [s._areaPoints[ii][0], s._areaPoints[ii][1]];
                                    var vertex2 = [s._areaPoints[j][0], s._areaPoints[j][1]];

                                    if (vertex1[1] < y && vertex2[1] >= y || vertex2[1] < y && vertex1[1] >= y)     {
                                        if (vertex1[0] + (y - vertex1[1]) / (vertex2[1] - vertex1[1]) * (vertex2[0] - vertex1[0]) < x) {
                                            inside = !inside;
                                        }
                                    }

                                    j = ii;
                                }        
                            }
                            if (inside) {
                                return {seriesIndex:i, pointIndex:null, gridData:s.gridData, data:s.data, points:s._areaPoints};
                            }
                            break;
                            
                        }

                        else {
                            t = s.markerRenderer.size/2+s.neighborThreshold;
                            threshold = (t > 0) ? t : 0;
                            for (var j=0; j<s.gridData.length; j++) {
                                p = s.gridData[j];
                                // neighbor looks different to OHLC chart.
                                if (r.constructor == $.jqplot.OHLCRenderer) {
                                    if (r.candleStick) {
                                        var yp = s._yaxis.series_u2p;
                                        if (x >= p[0]-r._bodyWidth/2 && x <= p[0]+r._bodyWidth/2 && y >= yp(s.data[j][2]) && y <= yp(s.data[j][3])) {
                                            return {seriesIndex: i, pointIndex:j, gridData:p, data:s.data[j]};
                                        }
                                    }
                                    // if an open hi low close chart
                                    else if (!r.hlc){
                                        var yp = s._yaxis.series_u2p;
                                        if (x >= p[0]-r._tickLength && x <= p[0]+r._tickLength && y >= yp(s.data[j][2]) && y <= yp(s.data[j][3])) {
                                            return {seriesIndex: i, pointIndex:j, gridData:p, data:s.data[j]};
                                        }
                                    }
                                    // a hi low close chart
                                    else {
                                        var yp = s._yaxis.series_u2p;
                                        if (x >= p[0]-r._tickLength && x <= p[0]+r._tickLength && y >= yp(s.data[j][1]) && y <= yp(s.data[j][2])) {
                                            return {seriesIndex: i, pointIndex:j, gridData:p, data:s.data[j]};
                                        }
                                    }
                        
                                }
                                else if (p[0] != null && p[1] != null){
                                    d = Math.sqrt( (x-p[0]) * (x-p[0]) + (y-p[1]) * (y-p[1]) );
                                    if (d <= threshold && (d <= d0 || d0 == null)) {
                                       d0 = d;
                                       return {seriesIndex: i, pointIndex:j, gridData:p, data:s.data[j]};
                                    }
                                }
                            } 
                        }
                    }
                    break;
                    
                default:
                    x = gridpos.x;
                    y = gridpos.y;
                    r = s.renderer;
                    if (s.show) {
                        t = s.markerRenderer.size/2+s.neighborThreshold;
                        threshold = (t > 0) ? t : 0;
                        for (var j=0; j<s.gridData.length; j++) {
                            p = s.gridData[j];
                            // neighbor looks different to OHLC chart.
                            if (r.constructor == $.jqplot.OHLCRenderer) {
                                if (r.candleStick) {
                                    var yp = s._yaxis.series_u2p;
                                    if (x >= p[0]-r._bodyWidth/2 && x <= p[0]+r._bodyWidth/2 && y >= yp(s.data[j][2]) && y <= yp(s.data[j][3])) {
                                        return {seriesIndex: i, pointIndex:j, gridData:p, data:s.data[j]};
                                    }
                                }
                                // if an open hi low close chart
                                else if (!r.hlc){
                                    var yp = s._yaxis.series_u2p;
                                    if (x >= p[0]-r._tickLength && x <= p[0]+r._tickLength && y >= yp(s.data[j][2]) && y <= yp(s.data[j][3])) {
                                        return {seriesIndex: i, pointIndex:j, gridData:p, data:s.data[j]};
                                    }
                                }
                                // a hi low close chart
                                else {
                                    var yp = s._yaxis.series_u2p;
                                    if (x >= p[0]-r._tickLength && x <= p[0]+r._tickLength && y >= yp(s.data[j][1]) && y <= yp(s.data[j][2])) {
                                        return {seriesIndex: i, pointIndex:j, gridData:p, data:s.data[j]};
                                    }
                                }
                        
                            }
                            else {
                                d = Math.sqrt( (x-p[0]) * (x-p[0]) + (y-p[1]) * (y-p[1]) );
                                if (d <= threshold && (d <= d0 || d0 == null)) {
                                   d0 = d;
                                   return {seriesIndex: i, pointIndex:j, gridData:p, data:s.data[j]};
                                }
                            }
                        } 
                    }
                    break;
            }
        }
        
        return null;
    };
    
    this.getEventPosition = function (ev) {
      var plot = ev.data.plot;
      var go = plot.eventCanvas._elem.offset();
      var gridPos = {x:ev.pageX - go.left, y:ev.pageY - go.top};
      var dataPos = {xaxis:null, yaxis:null, x2axis:null, y2axis:null, y3axis:null, y4axis:null, y5axis:null, y6axis:null, y7axis:null, y8axis:null, y9axis:null, yMidAxis:null};
      var an = ['xaxis', 'yaxis', 'x2axis', 'y2axis', 'y3axis', 'y4axis', 'y5axis', 'y6axis', 'y7axis', 'y8axis', 'y9axis', 'yMidAxis'];
      var ax = plot.axes;
      var n, axis;
      for (n=11; n>0; n--) {
          axis = an[n-1];
          if (ax[axis].show) {
              dataPos[axis] = ax[axis].series_p2u(gridPos[axis.charAt(0)]);
          }
      }

      return {offsets:go, gridPos:gridPos, dataPos:dataPos};
    };
    
    this.onClick = function(ev) {
        // Event passed in is normalized and will have data attribute.
        // Event passed out is unnormalized.
        var positions = ev.data.plot.getEventPosition(ev);
        var p = ev.data.plot;
        var neighbor = p.checkIntersection(positions.gridPos, p);
        var evt = $.Event('jqplotClick');
        evt.pageX = ev.pageX;
        evt.pageY = ev.pageY;
        $(this).trigger(evt, [positions.gridPos, positions.dataPos, neighbor, p]);
    };
    
    this.onDblClick = function(ev) {
        // Event passed in is normalized and will have data attribute.
        // Event passed out is unnormalized.
        var positions = ev.data.plot.getEventPosition(ev);
        var p = ev.data.plot;
        var neighbor = p.checkIntersection(positions.gridPos, p);
        var evt = $.Event('jqplotDblClick');
        evt.pageX = ev.pageX;
        evt.pageY = ev.pageY;
        $(this).trigger(evt, [positions.gridPos, positions.dataPos, neighbor, p]);
    };
    
    this.onMouseDown = function(ev) {
        var positions = ev.data.plot.getEventPosition(ev);
        var p = ev.data.plot;
        var neighbor = p.checkIntersection(positions.gridPos, p);
        var evt = $.Event('jqplotMouseDown');
        evt.pageX = ev.pageX;
        evt.pageY = ev.pageY;
        $(this).trigger(evt, [positions.gridPos, positions.dataPos, neighbor, p]);
    };
    
    this.onMouseUp = function(ev) {
        var positions = ev.data.plot.getEventPosition(ev);
        var evt = $.Event('jqplotMouseUp');
        evt.pageX = ev.pageX;
        evt.pageY = ev.pageY;
        $(this).trigger(evt, [positions.gridPos, positions.dataPos, null, ev.data.plot]);
    };
    
    this.onRightClick = function(ev) {
        var positions = ev.data.plot.getEventPosition(ev);
        var p = ev.data.plot;
        var neighbor = p.checkIntersection(positions.gridPos, p);
        if (p.captureRightClick) {
            if (ev.which == 3) {
            var evt = $.Event('jqplotRightClick');
            evt.pageX = ev.pageX;
            evt.pageY = ev.pageY;
                $(this).trigger(evt, [positions.gridPos, positions.dataPos, neighbor, p]);
            }
            else {
            var evt = $.Event('jqplotMouseUp');
            evt.pageX = ev.pageX;
            evt.pageY = ev.pageY;
                $(this).trigger(evt, [positions.gridPos, positions.dataPos, neighbor, p]);
            }
        }
    };
    
    this.onMouseMove = function(ev) {
        var positions = ev.data.plot.getEventPosition(ev);
        var p = ev.data.plot;
        var neighbor = p.checkIntersection(positions.gridPos, p);
        var evt = $.Event('jqplotMouseMove');
        evt.pageX = ev.pageX;
        evt.pageY = ev.pageY;
        $(this).trigger(evt, [positions.gridPos, positions.dataPos, neighbor, p]);
    };
    
    this.onMouseEnter = function(ev) {
        var positions = ev.data.plot.getEventPosition(ev);
        var p = ev.data.plot;
        var evt = $.Event('jqplotMouseEnter');
        evt.pageX = ev.pageX;
        evt.pageY = ev.pageY;
        evt.relatedTarget = ev.relatedTarget;
        $(this).trigger(evt, [positions.gridPos, positions.dataPos, null, p]);
    };
    
    this.onMouseLeave = function(ev) {
        var positions = ev.data.plot.getEventPosition(ev);
        var p = ev.data.plot;
        var evt = $.Event('jqplotMouseLeave');
        evt.pageX = ev.pageX;
        evt.pageY = ev.pageY;
        evt.relatedTarget = ev.relatedTarget;
        $(this).trigger(evt, [positions.gridPos, positions.dataPos, null, p]);
    };

		// customizing new method (2012-05-30, Roy Choi)
		// method: addSeries
		// Add Series one series on the plot.
		// data is array
		// options is extended to the SeriesDefault option
		this.addSeries = function(data, options, draw) {
			//var colorIndex = 0;
			var slen = this.series.length;
			var k = 0;

			var self = this;

			for (var i = slen, d_len = data.length; i < slen + d_len; i++) {
				this.data.push(data[k]);
				//$.merge(this._plotData,data[k]);

				this.options.series.push(options[k]);
				var temp = new Series();
				// customizing (2013-02-12, Roy Choi)
				if (this.options.multiCanvas) {
					temp.canvas = new $.jqplot.GenericCanvas();
					if ( typeof this.options.seriesDefaults == 'object' && this.options.seriesDefaults.shadow) {
						temp.shadowCanvas = new $.jqplot.GenericCanvas();
					}
				} else {
					temp.canvas = this.seriesCanvas;
				}

				$.extend(true, temp, {
					seriesColors : this.seriesColors,
					negativeSeriesColors : this.negativeSeriesColors
				}, this.options.seriesDefaults, options[k]);
				var dir = 'vertical';
				if (temp.renderer === $.jqplot.BarRenderer && temp.rendererOptions && temp.rendererOptions.barDirection == 'horizontal' && temp.transposeData === true) {
					dir = 'horizontal';
				}
				// enhancement (2011-10-24, Roy Choi)
				//temp.data = data[k]; //(jQuery.isArray(data[k][0])) ? data[k] : normalizeData(data[k], dir, this.defaultAxisStart);
				temp.data = (jQuery.isArray(data[k][0])) ? data[k] : normalizeData(data[k], dir, this.defaultAxisStart);
				//temp.data = normalizeData(this.data[i], dir, this.defaultAxisStart);
				switch (temp.xaxis) {
					case 'xaxis':
						temp._xaxis = this.axes.xaxis;
						break;
					case 'x2axis':
						temp._xaxis = this.axes.x2axis;
						break;
					default:
						break;
				}
				temp._yaxis = this.axes[temp.yaxis];
				temp._xaxis._series.push(temp);
				temp._yaxis._series.push(temp);
				if (temp.show) {
					temp._xaxis.show = true;
					temp._yaxis.show = true;
				}

				if (!temp.label) {
					temp.label = 'Series ' + (i + 1).toString();
				}
				// temp.rendererOptions.show = temp.show;
				// $.extend(true, temp.renderer, {color:this.seriesColors[i]}, this.rendererOptions);
				this.series.push(temp);

				temp = null;

				// set default stacking order for series canvases
				this.seriesStack.push(i);
				this.previousSeriesStack.push(i);
				// customizing (2013-02-12, Roy Choi)
				if (this.options.multiCanvas && this.series[i].shadowCanvas) {
					this.series[i].shadowCanvas._plotDimensions = this._plotDimensions;
				}
				this.series[i].canvas._plotDimensions = this._plotDimensions;
				this.populatePlotData(this.series[i], i);
				this.series[i]._plotDimensions = this._plotDimensions;
				this.series[i].init(i, this.grid.borderWidth, this);

				this._sumy += this.series[i]._sumy;
				this._sumx += this.series[i]._sumx;

				// customizing (2013-02-12, Roy Choi)
				if (this.options.multiCanvas) {
					this.target.find('.jqplot-series-canvas:last').after(this.series[i].canvas.createElement(this._gridPadding, 'jqplot-series-canvas', null, this));
					this.series[i].canvas.setContext();
					this.series[i].canvas._elem.data('seriesIndex', i);
				}

				if (draw) {
					var sr = this.series[i];
					sr.draw(sr.canvas._ctx, {}, self);
					if (options.showLabel) this.legend.renderer.addSeries.call(this.legend, this, [sr]);
					sr = null;
					/*setTimeout(function(){
					 sr.draw(sr.canvas._ctx, {},self);
					 self.legend.renderer.addSeries.call(self.legend,self,[sr]);
					 self = sr = null;
					 },0);*/
				} else {
					if (options.showLabel) this.legend.renderer.addSeries.call(this.legend, this, [this.series[i]]);
				}

				k++;
			}
		};

		// customizing new method (2013-02-15, Roy Choi)
		// method: removeSeries
		// remove a series on the plot.
		// seriesIndex is number
		// draw is boolean whether redraw series
		this.removeSeries = function(seriesIndex, draw) {
			draw = draw === false ? false : true;
			this.options.series[seriesIndex] = null;
			this.options.series.splice(seriesIndex, 1);
			this.series[seriesIndex].data = null;
			this.series.splice(seriesIndex, 1);
			this.data.splice(seriesIndex, 1);

			var stackIndex = $.inArray(seriesIndex, this.seriesStack);
			if (stackIndex > -1) {
				this.seriesStack.splice(stackIndex, 1);
			}

			if (draw) {
				this.redrawSeries();
			}
		};

		// method: drawSeries
		// Redraws all or just one series on the plot.  No axis scaling
		// is performed and no other elements on the plot are redrawn.
		// options is an options object to pass on to the series renderers.
		// It can be an empty object {}.  idx is the series index
		// to redraw if only one series is to be redrawn.
		this.drawSeries = function(options, idx) {
			
			if (!this.options.multiCanvas) {
				this.seriesHighlightCanvas._plotDimensions = this._plotDimensions;
				var shctx = this.seriesHighlightCanvas._ctx;
				shctx.clearRect(0, 0, shctx.canvas.width, shctx.canvas.height);
				shctx = null;
			}

			var i, series, ctx;
			// if only one argument passed in and it is a number, use it ad idx.
			idx = ( typeof (options) === "number" && idx == null) ? options : idx;
			options = ( typeof (options) === "object") ? options : {};
			// draw specified series
			if (idx != undefined) {
				series = this.series[idx];
				
				if (this.options.multiCanvas) {
					if (series.shadowCanvas) {
						ctx = series.shadowCanvas._ctx;
						ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
						series.drawShadow(ctx, options, this);
					}
					ctx = series.canvas._ctx;
					ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
					series.draw(ctx, options, this);
				} else {
					ctx = series.canvas._ctx;
					if (series.hide)
						series.draw(ctx, options, this);
					// customizing (2012-08-30, Roy Choi)
				}

				if (series.renderer.constructor == $.jqplot.BezierCurveRenderer) {
					if (idx < this.series.length - 1) {
						this.drawSeries(idx + 1);
					}
				}
			}  else if (this.series.length && this.series[0].renderer.constructor === $.jqplot.BigDataScatterRenderer) {
				this.options.multiCanvas = false;
      	ctx = this.series[0].canvas._ctx;
      	this.series[0].renderer.draw.call(this, ctx, null, options, this);
      } else {
				// if call series drawShadow method first, in case all series shadows
				// should be drawn before any series.  This will ensure, like for
				// stacked bar plots, that shadows don't overlap series.
				for ( i = 0; i < this.series.length; i++) {
					// first clear the canvas
					series = this.series[i];
					
					if (series.hide)
						continue;
					// customizing (2012-08-30, Roy Choi)
					if (this.options.multiCanvas) {
						if (series.shadow) {
							ctx = series.shadowCanvas._ctx;
							ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
							series.drawShadow(ctx, options, this);
						}
						ctx = series.canvas._ctx;
						ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
						series.draw(ctx, options, this);
					} else {
						ctx = series.canvas._ctx;
						series.draw(ctx, options, this);
					}
				}
			}
			options = idx = i = series = ctx = null;
			
			
		};

		// method: moveSeriesToFront
		// This method requires jQuery 1.4+
		// Moves the specified series canvas in front of all other series canvases.
		// This effectively "draws" the specified series on top of all other series,
		// although it is performed through DOM manipulation, no redrawing is performed.
		//
		// Parameters:
		// idx - 0 based index of the series to move.  This will be the index of the series
		// as it was first passed into the jqplot function.
		this.moveSeriesToFront = function(idx) {
			idx = parseInt(idx, 10);
			var stackIndex = $.inArray(idx, this.seriesStack);
			// if already in front, return
			if (stackIndex == -1) {
				return;
			}

			if (this.options.multiCanvas) {
				if (stackIndex == this.seriesStack.length - 1) {// customizing (2012-04-25, Roy Choi)
					this.previousSeriesStack = this.seriesStack.slice(0);
					//return;
				} else {
					var opidx = this.seriesStack[this.seriesStack.length - 1];
					var serelem = this.series[idx].canvas._elem.detach();
					if (this.series[idx].shadowCanvas) {
						var shadelem = this.series[idx].shadowCanvas._elem.detach();
						this.series[opidx].shadowCanvas._elem.after(shadelem);
					}
					this.series[opidx].canvas._elem.after(serelem);
					this.previousSeriesStack = this.seriesStack.slice(0);
					this.seriesStack.splice(stackIndex, 1);
					this.seriesStack.push(idx);
				}
				// customizing Start (2012-04-20, Roy Choi)
				$.each(this.series, function(ind, series) {
					if (!series.hide) {
						if (ind == idx) {
							$(series.canvas._elem).fadeTo('fast', 1.0);
							series.highlighted = true;
						} else {
							$(series.canvas._elem).fadeTo('fast', .2);
							series.highlighted = false;
						}
					}
				});

				// customizing End (2012-04-20, Roy Choi)
			} else {
				if (this.series[idx].highlighted) {
					return;
				}

				if (stackIndex == this.seriesStack.length - 1) {// customizing (2012-04-25, Roy Choi)
					this.previousSeriesStack = this.seriesStack.slice(0);
					//return;
				} else {
					var opidx = this.seriesStack[this.seriesStack.length - 1];
					//var serelem = this.series[idx].canvas._elem.detach();
					//var shadelem = this.series[idx].shadowCanvas._elem.detach();
					//this.series[opidx].shadowCanvas._elem.after(shadelem);
					//this.series[opidx].canvas._elem.after(serelem);
					this.previousSeriesStack = this.seriesStack.slice(0);
					this.seriesStack.splice(stackIndex, 1);
					this.seriesStack.push(idx);
				}
				// customizing Start (2012-04-20, Roy Choi)
				this.seriesCanvas._elem.fadeTo(0, .2);
				var ctx = this.seriesHighlightCanvas._ctx;
				ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
				this.series[idx].draw(ctx, {}, this);
				ctx = null;

				// customizing Start (2013-02-19, Roy Choi)
				$.each(this.series, function(ind, series) {
					if (!series.hide) {
						if (ind == idx) {
							series.highlighted = true;
						} else {
							series.highlighted = false;
						}
					}
				});
				// customizing End (2012-04-20, Roy Choi)
			}
			this.highlightSeriesIndex = idx;
			this.target.trigger('jqPlot.SeriesToFront', [idx, this]);

			for (var i = 0; i < $.jqplot.postSeriesHighlightHooks.length; i++) {
				$.jqplot.postSeriesHighlightHooks[i].call(this, idx);
			}
		};

		// method: moveSeriesToBack
		// This method requires jQuery 1.4+
		// Moves the specified series canvas behind all other series canvases.
		//
		// Parameters:
		// idx - 0 based index of the series to move.  This will be the index of the series
		// as it was first passed into the jqplot function.
		this.moveSeriesToBack = function(idx) {
			idx = parseInt(idx, 10);
			var stackIndex = $.inArray(idx, this.seriesStack);
			// if already in back, return
			if (stackIndex == 0 || stackIndex == -1) {
				return;
			}

			if (this.options.multiCanvas) {
				var opidx = this.seriesStack[0];
				// customizing Start (2012-04-20, Roy Choi)
				$.each(this.series, function(ind, series) {
					if (!series.hide) {
						series.highlighted = false;
						$(series.canvas._elem).fadeTo(0, 1.0);
					}
				});
				// customizing End (2012-04-20, Roy Choi)
				var serelem = this.series[idx].canvas._elem.detach();
				if (this.series[idx].shadowCanvas) {
					var shadelem = this.series[idx].shadowCanvas._elem.detach();
					this.series[opidx].shadowCanvas._elem.before(shadelem);
				}
				this.series[opidx].canvas._elem.before(serelem);
				this.previousSeriesStack = this.seriesStack.slice(0);
				this.seriesStack.splice(stackIndex, 1);
				this.seriesStack.unshift(idx);
			} else {
				//var opidx = this.seriesStack[0];
				// customizing Start (2012-04-20, Roy Choi)
				$.each(this.series, function(ind, series) {
					if (!series.hide) {
						series.highlighted = false;
						//$(series.canvas._elem).fadeTo(0,1.0);
					}
				});
				// customizing End (2012-04-20, Roy Choi)
				//var serelem = this.series[idx].canvas._elem.detach();
				//var shadelem = this.series[idx].shadowCanvas._elem.detach();
				//this.series[opidx].shadowCanvas._elem.before(shadelem);
				//this.series[opidx].canvas._elem.before(serelem);
				this.previousSeriesStack = this.seriesStack.slice(0);
				this.seriesStack.splice(stackIndex, 1);
				this.seriesStack.unshift(idx);
				this.seriesCanvas._elem.fadeTo(0, 1.0);
				var ctx = this.seriesHighlightCanvas._ctx;
				ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
				ctx = null;
				//this.redraw();
			}

			this.highlightSeriesIndex = null;
			this.target.trigger('jqPlot.SeriesToBack', [idx, this]);

			for (var i = 0; i < $.jqplot.postSeriesUnhighlightHooks.length; i++) {
				$.jqplot.postSeriesUnhighlightHooks[i].call(this);
			}
		};

		// method: restorePreviousSeriesOrder
		// This method requires jQuery 1.4+
		// Restore the series canvas order to its previous state.
		// Useful to put a series back where it belongs after moving
		// it to the front.
		this.restorePreviousSeriesOrder = function() {
			var i, j, serelem, shadelem, temp, move, keep;
			// if no change, return.
			if (this.seriesStack == this.previousSeriesStack) {
				return;
			}

			if (this.options.multiCanvas) {
				// customizing Start (2012-04-20, Roy Choi)
				$.each(this.series, function(ind, series) {
					if (!series.hide) {
						series.highlighted = false;
						$(series.canvas._elem).fadeTo(0, 1.0);
					}
				});
				// customizing End (2012-04-20, Roy Choi)
				for ( i = 1; i < this.previousSeriesStack.length; i++) {
					move = this.previousSeriesStack[i];
					keep = this.previousSeriesStack[i - 1];
					serelem = this.series[move].canvas._elem.detach();
					if (this.series[move].shadowCanvas) {
						shadelem = this.series[move].shadowCanvas._elem.detach();
						this.series[keep].shadowCanvas._elem.after(shadelem);
					}
					this.series[keep].canvas._elem.after(serelem);
				}
				temp = this.seriesStack.slice(0);
				this.seriesStack = this.previousSeriesStack.slice(0);
				this.previousSeriesStack = temp;
			} else {
				// customizing Start (2012-04-20, Roy Choi)
				$.each(this.series, function(ind, series) {
					if (!series.hide) {
						series.highlighted = false;
						//$(series.canvas._elem).fadeTo(0,1.0);
					}
				});
				// customizing End (2012-04-20, Roy Choi)
				for ( i = 1; i < this.previousSeriesStack.length; i++) {
					move = this.previousSeriesStack[i];
					keep = this.previousSeriesStack[i - 1];
					//serelem = this.series[move].canvas._elem.detach();
					//shadelem = this.series[move].shadowCanvas._elem.detach();
					//this.series[keep].shadowCanvas._elem.after(shadelem);
					//this.series[keep].canvas._elem.after(serelem);
				}
				temp = this.seriesStack.slice(0);
				this.seriesStack = this.previousSeriesStack.slice(0);
				this.previousSeriesStack = temp;
				temp = null;
				this.seriesCanvas._elem.fadeTo(0, 1.0);
				var ctx = this.seriesHighlightCanvas._ctx;
				ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
				ctx = null;
				//this.redraw();
			}
			this.highlightSeriesIndex = null;
			this.target.trigger('jqPlot.PreviousSeriesOrder', [this]);

			for (var i = 0; i < $.jqplot.postSeriesUnhighlightHooks.length; i++) {
				$.jqplot.postSeriesUnhighlightHooks[i].call(this);
			}
		};

		// method: restoreOriginalSeriesOrder
		// This method requires jQuery 1.4+
		// Restore the series canvas order to its original order
		// when the plot was created.
		this.restoreOriginalSeriesOrder = function() {
			var i, j, arr = [], serelem, shadelem;
			//var i, arr=[];

			if (this.options.multiCanvas) {
				var i, j, arr = [], serelem, shadelem;
				for ( i = 0; i < this.series.length; i++) {
					arr.push(i);
				}
				if (this.seriesStack == arr) {
					return;
				}
				// customizing Start (2012-04-20, Roy Choi)
				$.each(this.series, function(ind, series) {
					if (!series.hide) {
						series.highlighted = false;
						$(series.canvas._elem).fadeTo(0, 1.0);
					}
				});
				// customizing End (2012-04-20, Roy Choi)
				this.previousSeriesStack = this.seriesStack.slice(0);
				this.seriesStack = arr;
				for ( i = 1; i < this.seriesStack.length; i++) {
					serelem = this.series[i].canvas._elem.detach();
					if ( shadelem = this.series[i].shadowCanvas) {
						shadelem = this.series[i].shadowCanvas._elem.detach();
						this.series[i - 1].shadowCanvas._elem.after(shadelem);
					}
					this.series[i - 1].canvas._elem.after(serelem);
				}
			} else {
				for ( i = 0; i < this.series.length; i++) {
					arr.push(i);
				}
				if (this.seriesStack == arr) {
					return;
				}
				// customizing Start (2012-04-20, Roy Choi)
				$.each(this.series, function(ind, series) {
					if (!series.hide) {
						series.highlighted = false;
						//$(series.canvas._elem).fadeTo(0,1.0);
					}
				});
				// customizing End (2012-04-20, Roy Choi)
				this.previousSeriesStack = this.seriesStack.slice(0);
				this.seriesStack = arr;
				arr = null;
				//for (i=1; i<this.seriesStack.length; i++) {
				//serelem = this.series[i].canvas._elem.detach();
				//shadelem = this.series[i].shadowCanvas._elem.detach();
				//this.series[i-1].shadowCanvas._elem.after(shadelem);
				//this.series[i-1].canvas._elem.after(serelem);
				//}
				this.seriesCanvas._elem.fadeTo(0, 1.0);
				var ctx = this.seriesHighlightCanvas._ctx;
				ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
				ctx = null;
				//this.redraw();
			}

			this.highlightSeriesIndex = null;
			this.target.trigger('jqPlot.OriginalSeriesOrder', [this]);

			for (var i = 0; i < $.jqplot.postSeriesUnhighlightHooks.length; i++) {
				$.jqplot.postSeriesUnhighlightHooks[i].call(this);
			}
		};
	}

	// setGridData
	// converts the user data values to grid coordinates and stores them
	// in the gridData array.
	// Called with scope of a series.
	$.jqplot.LineRenderer.prototype.setNormalGridData = function(plot) {
		// recalculate the grid data
		var xp = this._xaxis.series_u2p;
		var yp = this._yaxis.series_u2p;
		var data = this._plotData;
		var pdata = this._prevPlotData;
		this.gridData = [];
		this._prevGridData = [];
		this.renderer._smoothedData = [];
		this.renderer._smoothedPlotData = [];
		this.renderer._hiBandGridData = [];
		this.renderer._lowBandGridData = [];
		this.renderer._hiBandSmoothedData = [];
		this.renderer._lowBandSmoothedData = [];
		var bands = this.renderer.bands;
		var hasNull = this.hasNullPoint;
		var pointData, prevPointData;
		
		var gd = this.gridData;
		var pd = this._prevGridData;

		if (!xp) {
			return;
		}
		
		if (!hasNull) {
			for (var i = 0, l = data.length; i < l; i++) {
				// if not a line series or if no nulls in data, push the converted point onto the array.
				pointData = data[i];
	      gd.push([xp.call(this._xaxis, pointData[0]), yp.call(this._yaxis, pointData[1])]);
	      
	      prevPointData = pdata[i];
	      if (prevPointData != null) {
	      	pd.push([xp.call(this._xaxis, prevPointData[0]), yp.call(this._yaxis, prevPointData[1])]);
	      }
			}
		} else {
			for (var i = 0, l = data.length; i < l; i++) {
				pointData = data[i];

				// if not a line series or if no nulls in data, push the converted point onto the array.
        if (pointData[0] != null && !isNaN(pointData[0]) && pointData[1] != null && !isNaN(pointData[1])) {
            gd.push([xp.call(this._xaxis, pointData[0]), yp.call(this._yaxis, pointData[1])]);
        }
        // else if there is a null, preserve it.
        else if (pointData[0] == null || isNaN(pointData[0])) {
					gd.push([null, yp.call(this._yaxis, pointData[1])]);
				} else if (pointData[1] == null || isNaN(pointData[1])) {
					gd.push([xp.call(this._xaxis, pointData[0]), null]);
				}
				
				prevPointData = pdata[i];
				if (prevPointData != null) {
					// if not a line series or if no nulls in data, push the converted point onto the array.
					if (prevPointData[0] != null && !isNaN(prevPointData[0]) && prevPointData[1] != null && !isNaN(prevPointData[1])) {
						pd.push([xp.call(this._xaxis, prevPointData[0]), yp.call(this._yaxis, prevPointData[1])]);
					}
					// else if there is a null, preserve it.
					else if (prevPointData[0] == null || isNaN(prevPointData[0])) {
						pd.push([null, yp.call(this._yaxis, prevPointData[1])]);
					} else if (prevPointData[1] == null || isNaN(prevPointData[1])) {
						pd.push([xp.call(this._xaxis, prevPointData[0]), null]);
					}
				}
			}
		}
		
		gd = pd = null;

		// don't do smoothing or bands on broken lines.
		if (hasNull) {
			this.renderer.smooth = false;
			if (this._type === 'line') {
				bands.show = false;
			}
		}

		if (this._type === 'line' && bands.show) {
			var hiData, lowData;
			for (var i = 0, l = bands.hiData.length; i < l; i++) {
				hiData = bands.hiData[i];
				this.renderer._hiBandGridData.push([xp.call(this._xaxis, hiData[0]), yp.call(this._yaxis, hiData[1])]);
			}
			for (var i = 0, l = bands.lowData.length; i < l; i++) {
				lowData = bands.lowData[i];
				this.renderer._lowBandGridData.push([xp.call(this._xaxis, lowData[0]), yp.call(this._yaxis, lowData[1])]);
			}
		}
	};
	
    // makeNormalGridData	- customizing (2014-05-21, Roy)
    // converts any arbitrary data values to grid coordinates and
    // returns them.  This method exists so that plugins can use a series'
    // linerenderer to generate grid data points without overwriting the
    // grid data associated with that series.
    // Called with scope of a series.
    $.jqplot.LineRenderer.prototype.makeNormalGridData = function(data, plot) {
    	
        // recalculate the grid data
        var xp = this._xaxis.series_u2p;
        var yp = this._yaxis.series_u2p;
        var gd = [];
        var pgd = [];
        this.renderer._smoothedData = [];
        this.renderer._smoothedPlotData = [];
        this.renderer._hiBandGridData = [];
        this.renderer._lowBandGridData = [];
        this.renderer._hiBandSmoothedData = [];
        this.renderer._lowBandSmoothedData = [];
        var bands = this.renderer.bands;
        var i, l, pointData;

        if (!this.hasNullPoint) {
        	for (i=0, l=data.length; i<l; i++) {
        		pointData = data[i];
        		gd.push([xp.call(this._xaxis, pointData[0]), yp.call(this._yaxis, pointData[1])]);
        	}
        } else {
        	for (i=0, l=data.length; i<l; i++) {
        		pointData = data[i];
        		// if not a line series or if no nulls in data, push the converted point onto the array.
            if (pointData[0] != null && !isNaN(pointData[0]) && pointData[1] != null && !isNaN(pointData[1])) {
                gd.push([xp.call(this._xaxis, pointData[0]), yp.call(this._yaxis, pointData[1])]);
            }
            // else if there is a null, preserve it.
            else if (pointData[0] == null || isNaN(pointData[0])) {
	              gd.push([null, yp.call(this._yaxis, pointData[1])]);
	          }
	          else if (pointData[1] == null || isNaN(pointData[1])) {
	              gd.push([xp.call(this._xaxis, pointData[0]), null]);
	          }
        	}
        }

        // don't do smoothing or bands on broken lines.
        if (this.hasNullPoint) {
            this.renderer.smooth = false;
            if (this._type === 'line') {
                bands.show = false;
            }
        }

        if (this._type === 'line' && bands.show) {
            for (var i=0, l=bands.hiData.length; i<l; i++) {
                this.renderer._hiBandGridData.push([xp.call(this._xaxis, bands.hiData[i][0]), yp.call(this._yaxis, bands.hiData[i][1])]);
            }
            for (var i=0, l=bands.lowData.length; i<l; i++) {
                this.renderer._lowBandGridData.push([xp.call(this._xaxis, bands.lowData[i][0]), yp.call(this._yaxis, bands.lowData[i][1])]);
            }
        }


        return gd;
    };

    // called within scope of series.
    $.jqplot.LineRenderer.prototype.draw = function(ctx, gd, options, plot) {
    	
        var i;
        // get a copy of the options, so we don't modify the original object.
        var opts = $.extend(true, {}, options);
        var shadow = (opts.shadow != undefined) ? opts.shadow : this.shadow;
        var showLine = (opts.showLine != undefined) ? opts.showLine : this.showLine;
        var fill = (opts.fill != undefined) ? opts.fill : this.fill;
        var fillAndStroke = (opts.fillAndStroke != undefined) ? opts.fillAndStroke : this.fillAndStroke;
        var xmin, ymin, xmax, ymax;
        
        var cursor = plot.plugins.cursor;
        var isZoomed = cursor && (cursor._zoom.isZoomed || cursor._zoom.zooming) && !cursor._zoom.resetzooming;
        cursor = null;
        
        //ctx.save();
        if (gd.length) {
            if (showLine) {
                opts.breakOnNull = this.breakOnNull;
                opts.hasNullPoint = this.hasNullPoint;
                
                // if we fill, we'll have to add points to close the curve.
                if (fill) {
                    if (this.fillToZero) { 
                        // have to break line up into shapes at axis crossings
                        var negativeColor = this.negativeColor;
                        if (! this.useNegativeColors) {
                            negativeColor = opts.fillStyle;
                        }
                        var isnegative = false;
                        var posfs = opts.fillStyle;
                    
                        // if stoking line as well as filling, get a copy of line data.
                        if (fillAndStroke) {
                            var fasgd = gd.slice(0);
                        }
                        // if not stacked, fill down to axis
                        if (this.index == 0 || !this._stack) {
                        
                            var tempgd = [];
                            var pd = (this.renderer.smooth) ? this.renderer._smoothedPlotData : this._plotData;
                            this._areaPoints = [];
                            var pyzero = this._yaxis.series_u2p(this.fillToValue);
                            var pxzero = this._xaxis.series_u2p(this.fillToValue);

                            opts.closePath = true;
                            
                            if (this.fillAxis == 'y') {
                                tempgd.push([gd[0][0], pyzero]);
                                this._areaPoints.push([gd[0][0], pyzero]);
                                
                                for (var i=0; i<gd.length-1; i++) {
                                    tempgd.push(gd[i]);
                                    this._areaPoints.push(gd[i]);
                                    // do we have an axis crossing?
                                    if (pd[i][1] * pd[i+1][1] <= 0) {
                                        if (pd[i][1] < 0) {
                                            isnegative = true;
                                            opts.fillStyle = negativeColor;
                                        }
                                        else {
                                            isnegative = false;
                                            opts.fillStyle = posfs;
                                        }
                                        
                                        var xintercept = gd[i][0] + (gd[i+1][0] - gd[i][0]) * (pyzero-gd[i][1])/(gd[i+1][1] - gd[i][1]);
                                        tempgd.push([xintercept, pyzero]);
                                        this._areaPoints.push([xintercept, pyzero]);
                                        // now draw this shape and shadow.
                                        if (shadow) {
                                            this.renderer.shadowRenderer.draw(ctx, tempgd, opts);
                                        }
                                        this.renderer.shapeRenderer.draw(ctx, tempgd, opts);
                                        // now empty temp array and continue
                                        tempgd = [[xintercept, pyzero]];
                                        // this._areaPoints = [[xintercept, pyzero]];
                                    }   
                                }
                                if (pd[gd.length-1][1] < 0) {
                                    isnegative = true;
                                    opts.fillStyle = negativeColor;
                                }
                                else {
                                    isnegative = false;
                                    opts.fillStyle = posfs;
                                }
                                tempgd.push(gd[gd.length-1]);
                                this._areaPoints.push(gd[gd.length-1]);
                                tempgd.push([gd[gd.length-1][0], pyzero]); 
                                this._areaPoints.push([gd[gd.length-1][0], pyzero]); 
                            }
                            // now draw the last area.
                            if (shadow) {
                                this.renderer.shadowRenderer.draw(ctx, tempgd, opts);
                            }
                            this.renderer.shapeRenderer.draw(ctx, tempgd, opts);
                            
                            
                            // var gridymin = this._yaxis.series_u2p(0);
                            // // IE doesn't return new length on unshift
                            // gd.unshift([gd[0][0], gridymin]);
                            // len = gd.length;
                            // gd.push([gd[len - 1][0], gridymin]);                   
                        }
                        // if stacked, fill to line below 
                        else {
                            var prev = this._prevGridData;
                            for (var i=prev.length; i>0; i--) {
                                gd.push(prev[i-1]);
                                // this._areaPoints.push(prev[i-1]);
                            }
                            if (shadow) {
                                this.renderer.shadowRenderer.draw(ctx, gd, opts);
                            }
                            this._areaPoints = gd;
                            this.renderer.shapeRenderer.draw(ctx, gd, opts);
                        }
                    }
                    /////////////////////////
                    // Not filled to zero
                    ////////////////////////
                    else {                    
                        // if stoking line as well as filling, get a copy of line data.
                        if (fillAndStroke) {
                            var fasgd = gd.slice(0);
                        }
                        // if not stacked, fill down to axis
                        if (this.index == 0 || !this._stack) {
                            // var gridymin = this._yaxis.series_u2p(this._yaxis.min) - this.gridBorderWidth / 2;
                            var gridymin = ctx.canvas.height;
                            // IE doesn't return new length on unshift
                            gd.unshift([gd[0][0], gridymin]);
                            var len = gd.length;
                            gd.push([gd[len - 1][0], gridymin]);                   
                        }
                        // if stacked, fill to line below 
                        else {
                            var prev = this._prevGridData;
                            for (var i=prev.length; i>0; i--) {
                                gd.push(prev[i-1]);
                            }
                        }
                        this._areaPoints = gd;
                        
                        if (shadow) {
                            this.renderer.shadowRenderer.draw(ctx, gd, opts);
                        }

                        this.renderer.shapeRenderer.draw(ctx, gd, opts, isZoomed);
                    }
                    if (fillAndStroke) {
                        var fasopts = $.extend(true, {}, opts, {fill:false, closePath:false});
                        this.renderer.shapeRenderer.draw(ctx, fasgd, fasopts);
                        //////////
                        // TODO: figure out some way to do shadows nicely
                        // if (shadow) {
                        //     this.renderer.shadowRenderer.draw(ctx, fasgd, fasopts);
                        // }
                        // now draw the markers
                        if (this.markerRenderer.show) {
                            if (this.renderer.smooth) {
                                fasgd = this.gridData;
                            }
                            for (i=0; i<fasgd.length; i++) {
                                this.markerRenderer.draw(fasgd[i][0], fasgd[i][1], ctx, opts.markerOptions);
                            }
                            //this.markerRenderer.drawAll(fasgd, ctx, opts.markerOptions);
                        }
                    }
                }
                else {

                    if (this.renderer.bands.show) {
                        var bdat;
                        var bopts = $.extend(true, {}, opts);
                        if (this.renderer.bands.showLines) {
                            bdat = (this.renderer.smooth) ? this.renderer._hiBandSmoothedData : this.renderer._hiBandGridData;
                            this.renderer.shapeRenderer.draw(ctx, bdat, opts);
                            bdat = (this.renderer.smooth) ? this.renderer._lowBandSmoothedData : this.renderer._lowBandGridData;
                            this.renderer.shapeRenderer.draw(ctx, bdat, bopts);
                        }

                        if (this.renderer.bands.fill) {
                            if (this.renderer.smooth) {
                                bdat = this.renderer._hiBandSmoothedData.concat(this.renderer._lowBandSmoothedData.reverse());
                            }
                            else {
                                bdat = this.renderer._hiBandGridData.concat(this.renderer._lowBandGridData.reverse());
                            }
                            this._areaPoints = bdat;
                            bopts.closePath = true;
                            bopts.fill = true;
                            bopts.fillStyle = this.renderer.bands.fillColor;
                            this.renderer.shapeRenderer.draw(ctx, bdat, bopts);
                        }
                    }

                    if (shadow) {
                        this.renderer.shadowRenderer.draw(ctx, gd, opts);
                    }
    
                    this.renderer.shapeRenderer.draw(ctx, gd, opts, isZoomed);
                }
            }
            // calculate the bounding box
            var xmin = xmax = ymin = ymax = null;
            for (i=0; i<this._areaPoints.length; i++) {
                var p = this._areaPoints[i];
                if (xmin > p[0] || xmin == null) {
                    xmin = p[0];
                }
                if (ymax < p[1] || ymax == null) {
                    ymax = p[1];
                }
                if (xmax < p[0] || xmax == null) {
                    xmax = p[0];
                }
                if (ymin > p[1] || ymin == null) {
                    ymin = p[1];
                }
            }

            if (this.type === 'line' && this.renderer.bands.show) {
                ymax = this._yaxis.series_u2p(this.renderer.bands._min);
                ymin = this._yaxis.series_u2p(this.renderer.bands._max);
            }

            this._boundingBox = [[xmin, ymax], [xmax, ymin]];
        
            // now draw the markers
            if (this.markerRenderer.show && !fill) {
            	
          	  if (this.renderer.smooth) {
                  gd = this.gridData;
              }
            	if ($.isFunction(this.markerRenderer.fillStyleFilter)) {	// customizing (2014-04-24 Roy Choi)
            		var sd = this.data;
            		if (!$.isPlainObject(opts.markerOptions)) {
            			opts.markerOptions = {};
            		}
            		if (this.hasNullPoint) {
            			for (i=0; i<gd.length; i++) {
	                    if (gd[i][0] != null && !isNaN(gd[i][0]) && gd[i][1] != null && !isNaN(gd[i][1])) {
	                    	opts.markerOptions.fillStyle = this.markerRenderer.fillStyleFilter.call(this, sd[i]);
	                      this.markerRenderer.draw(gd[i][0], gd[i][1], ctx, opts.markerOptions);
	                    }
	                }
            		} else {
            			for (i=0; i<gd.length; i++) {
	                    opts.markerOptions.fillStyle = this.markerRenderer.fillStyleFilter.call(this, sd[i]);
	                    this.markerRenderer.draw(gd[i][0], gd[i][1], ctx, opts.markerOptions);
	                }
            		}
                
            	} else {
            		if (this.hasNullPoint) {
            			for (i=0; i<gd.length; i++) {
	                    if (gd[i][0] != null && !isNaN(gd[i][0]) && gd[i][1] != null && !isNaN(gd[i][1])) {
	                        this.markerRenderer.draw(gd[i][0], gd[i][1], ctx, opts.markerOptions);
	                    }
	                }
            		} else {
            			for (i=0; i<gd.length; i++) {
	                	this.markerRenderer.draw(gd[i][0], gd[i][1], ctx, opts.markerOptions);
	                }
            			/*var cursor = plot.plugins.cursor;
            			this.markerRenderer.drawAll(gd, ctx, opts.markerOptions, cursor && cursor._zoom.isZoomed);
            			cursor = null;*/
            		}
            	}
            	
            	

            }
        }
        
        //ctx.restore();
        

    }; 

	// called within context of plot
	// create a canvas which we can draw on.
	// insert it before the eventCanvas, so eventCanvas will still capture events.

	$.jqplot.postDrawHooks.push(function() {
		// customizing (2012-05-23, Roy Choi) bug fixed
		this.target.find('.jqplot-lineRenderer-highlight-canvas').remove();
	});

	// class: $.jqplot.MarkerRenderer
	// The default jqPlot marker renderer, rendering the points on the line.
	var orgMarkerRenderer = $.jqplot.MarkerRenderer;
	$.jqplot.MarkerRenderer = function(options) {
		orgMarkerRenderer.call(this, options);
		
		// customizing (2011-10-27, Roy Choi)
		// prop: stroke
		// stroke or not. when marker style is filled...
		this.stroke = false;
		// prop: strokeStyle
		// stroke style of marker.
		this.strokeStyle = null;
		
		// customizing (2014-04-14, Roy Choi)
		this.fillRect = false;
		this.strokeRect = false;
	};
	$.jqplot.MarkerRenderer.prototype = new orgMarkerRenderer();

	$.jqplot.MarkerRenderer.prototype.init = function(options) {
		$.extend(true, this, options);
		var sdopt = {
			angle : this.shadowAngle,
			offset : this.shadowOffset,
			alpha : this.shadowAlpha,
			lineWidth : this.lineWidth,
			depth : this.shadowDepth,
			closePath : true
		};
		if (this.style.indexOf('filled') != -1) {
			sdopt.fill = true;
		}
		if (this.style.indexOf('ircle') != -1) {
			sdopt.isarc = true;
			sdopt.closePath = false;
		}
		this.shadowRenderer.init(sdopt);

		var shopt = {
			fill : false,
			isarc : false,
			stroke : false,
			strokeStyle : this.strokeStyle,
			fillStyle : this.color,
			lineWidth : this.lineWidth,
			closePath : true,
			// customizing (2014-04-14, Roy Choi)
			fillRect : this.fillRect,
			strokeRect : this.strokeRect,
			size : this.size
		};
		if (this.style.indexOf('filled') != -1) {
			shopt.fill = true;
		}
		if (this.style.indexOf('ircle') != -1) {
			shopt.isarc = true;
			shopt.closePath = false;
		}
		// customizing (2011-10-27, Roy Choi)
		if (this.stroke) {
			shopt.stroke = true;
			shopt.strokeStyle = this.strokeStyle;
		}
		this.shapeRenderer.init(shopt);
	};

  $.jqplot.MarkerRenderer.prototype.drawAll = function(gd, ctx, options, isZoomed) {
	    options = options || {};
	    // hack here b/c shape renderer uses canvas based color style options
	    // and marker uses css style names.
	    if (options.show == null || options.show != false) {
	        if (options.color && !options.fillStyle) {
	            options.fillStyle = options.color;
	        }
	        if (options.color && !options.strokeStyle) {
	            options.strokeStyle = options.color;
	        }

		    	this.shapeRenderer.drawShapes(ctx, gd, options, this.style, isZoomed);
	    }
	};

	// class: $.jqplot.shapeRenderer
	// The default jqPlot shape renderer.  Given a set of points will
	// plot them and either stroke a line (fill = false) or fill them (fill = true).
	// If a filled shape is desired, closePath = true must also be set to close
	// the shape.
	var orgShapeRenderer = $.jqplot.ShapeRenderer;
	$.jqplot.ShapeRenderer = function(options) {
		orgShapeRenderer.call(this, options);
		
		// customizing
		// prop: stroke
		// true to draw shape as a stroked.
		this.stroke = false;
		// prop: strokeStyle
		// css color spec for the stoke style
		this.strokeStyle = null;
		
		// customizing (2014-04-14, Roy Choi)
		this.size = 5;
	};
	$.jqplot.ShapeRenderer.prototype = new orgShapeRenderer();
	
  $.jqplot.ShapeRenderer.prototype.getDiamondPoints = function(x, y) {
      var stretch = 1.2;
      var dx = this.size/2/stretch;
      var dy = this.size/2*stretch;
      var points = [[x-dx, y], [x, y+dy], [x+dx, y], [x, y-dy]];
      return points;
      //this.shapeRenderer.draw(ctx, points, options);
  };
  
  $.jqplot.ShapeRenderer.prototype.getPlusPoints = function(x, y) {
      var stretch = 1.0;
      var dx = this.size/2*stretch;
      var dy = this.size/2*stretch;
      var points1 = [[x, y-dy], [x, y+dy]];
      var points2 = [[x+dx, y], [x-dx, y]];
      return [points1, points2];
      //var opts = $.extend(true, {}, this.options, {closePath:false});
      //this.shapeRenderer.draw(ctx, points1, opts);
      //this.shapeRenderer.draw(ctx, points2, opts);
  };
  
  $.jqplot.ShapeRenderer.prototype.getXPoints = function(x, y) {
      var stretch = 1.0;
      var dx = this.size/2*stretch;
      var dy = this.size/2*stretch;
      //var opts = $.extend(true, {}, this.options, {closePath:false});
      var points1 = [[x-dx, y-dy], [x+dx, y+dy]];
      var points2 = [[x-dx, y+dy], [x+dx, y-dy]];
      return [points1, points2];
      //this.shapeRenderer.draw(ctx, points1, opts);
      //this.shapeRenderer.draw(ctx, points2, opts);
  };
  
  $.jqplot.ShapeRenderer.prototype.getDashPoints = function(x, y) {
      var stretch = 1.0;
      var dx = this.size/2*stretch;
      var dy = this.size/2*stretch;
      var points = [[x-dx, y], [x+dx, y]];
      return points;
      //this.shapeRenderer.draw(ctx, points, options);
  };
  
  $.jqplot.ShapeRenderer.prototype.getLinePoints = function(p1, p2) {
      var points = [p1, p2];
      return points;
      //this.shapeRenderer.draw(ctx, points, options);
  };
  
  $.jqplot.ShapeRenderer.prototype.getSquarePoints = function(x, y) {
      var stretch = 1.0;
      var dx = this.size/2/stretch;
      var dy = this.size/2*stretch;
      var points = [[x-dx, y-dy], [x-dx, y+dy], [x+dx, y+dy], [x+dx, y-dy]];
      return points;
      //this.shapeRenderer.draw(ctx, points, options);
  };
  
  $.jqplot.ShapeRenderer.prototype.getCirclePoints = function(x, y) {
      var radius = this.size/2;
      var end = 2*Math.PI;
      var points = [x, y, radius, 0, end, true];
      return points;
      //this.shapeRenderer.draw(ctx, points, options);
  };
	
	// function: draw
	// draws the shape.
	//
	// ctx - canvas drawing context
	// points - array of points for shapes or
	// [x, y, width, height] for rectangles or
	// [x, y, radius, start angle (rad), end angle (rad)] for circles and arcs.
	$.jqplot.ShapeRenderer.prototype.draw = function(ctx, points, options, isZoomed) {
		ctx.save();
		var opts = (options != null) ? options : {};
		var fill = (opts.fill != null) ? opts.fill : this.fill;
		var stroke = (opts.stroke != null) ? opts.stroke : this.stroke;
		// customizing
		var closePath = (opts.closePath != null) ? opts.closePath : this.closePath;
		var fillRect = (opts.fillRect != null) ? opts.fillRect : this.fillRect;
		var strokeRect = (opts.strokeRect != null) ? opts.strokeRect : this.strokeRect;
		var clearRect = (opts.clearRect != null) ? opts.clearRect : this.clearRect;
		var isarc = (opts.isarc != null) ? opts.isarc : this.isarc;
		var linePattern = (opts.linePattern != null) ? opts.linePattern : this.linePattern;
		var ctxPattern = $.jqplot.LinePattern(ctx, linePattern);
		
		var breakOnNull = opts.breakOnNull;
		var hasNullPoint = opts.hasNullPoint;
		
		var width = ctx.canvas.width;
		var height = ctx.canvas.height;
		
		ctx.lineWidth = opts.lineWidth || this.lineWidth;
		ctx.lineJoin = opts.lineJoin || this.lineJoin;
		ctx.lineCap = opts.lineCap || this.lineCap;
		ctx.strokeStyle = (opts.strokeStyle || opts.color) || this.strokeStyle || this.color;
		ctx.fillStyle = opts.fillStyle || this.fillStyle;

		ctx.beginPath();
		if (isarc) {
			ctx.arc(points[0], points[1], points[2], points[3], points[4], true);
			if (closePath) {
				ctx.closePath();
			}
			if (fill) {
				ctx.fill();
				// customizing
				if (stroke) {
					ctx.stroke();
				}
			} else {
				ctx.stroke();
			}
		} else if (clearRect) {
			ctx.clearRect(points[0], points[1], points[2], points[3]);
		} else if (fillRect || strokeRect) {
			if ($.isArray(points[0])) {	// customizing (2014-04-14, Roy Choi)
				if (fillRect) {
					ctx.fillRect(points[0][0], points[0][1], this.size, this.size);
				}
				if (strokeRect) {
					ctx.strokeRect(points[0][0], points[0][1], this.size, this.size);
				}
			} else {
				if (fillRect) {
					ctx.fillRect(points[0], points[1], points[2], points[3]);
				}
				if (strokeRect) {
					ctx.strokeRect(points[0], points[1], points[2], points[3]);
				}
			}
		} else if (points && points.length) {
			if (hasNullPoint) {
				var move = true;
				for (var i = 0, points_len = points.length; i < points_len; i++) {
					// skip to the first non-null point and move to it.
					if (points[i][0] != null && !isNaN(points[i][0]) && points[i][1] != null && !isNaN(points[i][1])) {
						if (move && breakOnNull) {
							ctxPattern.moveTo(points[i][0], points[i][1]);
						} else {
							ctxPattern.lineTo(points[i][0], points[i][1]);
						}
						move = false;
					} else {
						move = true;
					}
				}
			} else {
				ctxPattern.moveTo(points[0][0], points[0][1]);
				for (var i = 1, points_len = points.length; i < points_len; i++) {
					ctxPattern.lineTo(points[i][0], points[i][1]);
				}
			}
			
			if (closePath) {
				ctxPattern.closePath();
			}
			if (fill) {
				ctx.fill();
				// customizing
				if (stroke) {
					ctx.stroke();
				}
			} else {
				ctx.stroke();
			}
		}
		ctx.restore();
	};
	
	// function: drawShapes
	// draw all shapes.
	//
	// ctx - canvas drawing context
	// points - array of points for shapes or
	// [x, y, width, height] for rectangles or
	// [x, y, radius, start angle (rad), end angle (rad)] for circles and arcs.
	$.jqplot.ShapeRenderer.prototype.drawShapes = function(ctx, gd, options, style, isZoomed) {
		ctx.save();
		var opts = (options != null) ? options : {};
		var getPoints, closeAngle = false;
		
		switch (style) {
      case 'diamond':
      case 'filledDiamond':
          getPoints = this.getDiamondPoints;
          closeAngle = true;
          break;
      case 'circle':
      case 'filledCircle':
          getPoints = this.getCirclePoints;
          break;
      case 'x':
          getPoints = this.getXPoints;
          opts = $.extend(true, {}, opts, {closePath:false});
          break;
      case 'plus':
          getPoints = this.getPlusPoints;
          opts = $.extend(true, {}, opts, {closePath:false});
          break;
      case 'dash':
          getPoints = this.getDashPoints;
          break;
      /*case 'line':
          this.getLine(x, y, ctx, false, options);
          break;*/
      case 'square':
      case 'filledSquare':
      default:
          getPoints = this.getSquarePoints;
          closeAngle = true;
          break;
  	}
		
		var fill = (opts.fill != null) ? opts.fill : this.fill;
		var stroke = (opts.stroke != null) ? opts.stroke : this.stroke;
		// customizing
		var closePath = (opts.closePath != null) ? opts.closePath : this.closePath;
		var fillRect = (opts.fillRect != null) ? opts.fillRect : this.fillRect;
		var strokeRect = (opts.strokeRect != null) ? opts.strokeRect : this.strokeRect;
		var clearRect = (opts.clearRect != null) ? opts.clearRect : this.clearRect;
		var isarc = (opts.isarc != null) ? opts.isarc : this.isarc;
		var linePattern = (opts.linePattern != null) ? opts.linePattern : this.linePattern;
		var ctxPattern = $.jqplot.LinePattern(ctx, linePattern);
		var points;
		var batchMaxCount = 1000;
		
		var width = ctx.canvas.width;
		var height = ctx.canvas.height;
		
		ctx.lineWidth = opts.lineWidth || this.lineWidth;
		ctx.lineJoin = opts.lineJoin || this.lineJoin;
		ctx.lineCap = opts.lineCap || this.lineCap;
		ctx.strokeStyle = opts.strokeStyle || opts.color || this.strokeStyle || this.color;
		ctx.fillStyle = opts.fillStyle || this.fillStyle;

		ctx.beginPath();
		
		var ctxOptions = {
			lineWidth: ctx.lineWidth,
			lineJoin: ctx.lineJoin,
			lineCap: ctx.lineCap,
			strokeStyle: ctx.strokeStyle,
			fillStyle: ctx.fillStyle
		};
		
		if (isarc) {
			var done = false;
			
			if (isZoomed) {
				for (var i=0, l=gd.length; i<l; i++) {
					points = getPoints.call(this, gd[i][0], gd[i][1]);
					
					if (points[0]+points[2] < 0 || points[0]-points[2] > width || points[1]+points[2] < 0 || points[1]-points[2] > height) {
						continue;
					}
					
					ctx.moveTo(points[0], points[1]);
					ctx.arc(points[0], points[1], points[2], points[3], points[4], true);
					
					if (i > 0 && i % batchMaxCount === 0) {
						if (fill) {
							ctx.fill();
							if (stroke) {
								ctx.stroke();
							}
						} else {
							ctx.stroke();
						}
						ctx.restore();
						
						ctx.lineWidth = ctxOptions.lineWidth;
						ctx.lineJoin = ctxOptions.lineJoin;
						ctx.lineCap = ctxOptions.lineCap;
						ctx.strokeStyle = ctxOptions.strokeStyle;
						ctx.fillStyle = ctxOptions.fillStyle;
						
						ctx.beginPath();
						
						done = true;
					} else {
						done = false;
					}
				}
			} else {
				for (var i=0, l=gd.length; i<l; i++) {
					points = getPoints.call(this, gd[i][0], gd[i][1]);

					ctx.moveTo(points[0], points[1]);
					ctx.arc(points[0], points[1], points[2], points[3], points[4], true);
					
					if (i > 0 && i % batchMaxCount === 0) {
						if (fill) {
							ctx.fill();
							if (stroke) {
								ctx.stroke();
							}
						} else {
							ctx.stroke();
						}
						ctx.restore();
						
						ctx.lineWidth = ctxOptions.lineWidth;
						ctx.lineJoin = ctxOptions.lineJoin;
						ctx.lineCap = ctxOptions.lineCap;
						ctx.strokeStyle = ctxOptions.strokeStyle;
						ctx.fillStyle = ctxOptions.fillStyle;
						
						ctx.beginPath();
						
						done = true;
					} else {
						done = false;
					}
				}
			}
			
			
			
			if (!done) {
				if (fill) {
					ctx.fill();
					if (stroke) {
						ctx.stroke();
					}
				} else {
					ctx.stroke();
				}
			}

		} else if (clearRect) {
			for (var i=0, l=gd.length; i<l; i++) {
				points = getPoints.call(this, gd[i][0], gd[i][1]);
				ctx.clearRect(points[0], points[1], points[2], points[3]);
			}
		} else if (fillRect && strokeRect) {
			for (var i=0, l=gd.length; i<l; i++) {
				points = getPoints.call(this, gd[i][0], gd[i][1]);
				
				ctx.fillRect(points[0][0], points[0][1], this.size, this.size);
				ctxPattern.moveTo(points[0][0], points[0][1]);
				for (var j = 1, points_len = points.length; j < points_len; j++) {
					ctxPattern.lineTo(points[j][0], points[j][1]);
				}
				ctxPattern.lineTo(points[0][0], points[0][1]);
			}
			ctx.stroke();
		} else if (fillRect) {
			for (var i=0, l=gd.length; i<l; i++) {
				points = getPoints.call(this, gd[i][0], gd[i][1]);
				
				ctx.fillRect(points[0][0], points[0][1], this.size, this.size);
			}
		} else if (strokeRect) {
			for (var i=0, l=gd.length; i<l; i++) {
				points = getPoints.call(this, gd[i][0], gd[i][1]);

				ctxPattern.moveTo(points[0][0], points[0][1]);
				for (var j = 1, points_len = points.length; j < points_len; j++) {
					ctxPattern.lineTo(points[j][0], points[j][1]);
				}
				ctxPattern.lineTo(points[0][0], points[0][1]);
			}
			ctx.stroke();
		} else {
			var done = false;
			if (closePath) {
				if (isZoomed) {
					for (var i=0, l=gd.length; i<l; i++) {
						points = getPoints.call(this, gd[i][0], gd[i][1]);
						
						if (points.filter(function(point) {
							return point[0] >= 0 && point[0] <= width && point[1] >= 0 && point[1] <= height;
						}).length === 0) {
							continue;
						}
		
						ctxPattern.moveTo(points[0][0], points[0][1]);
						for (var j = 1, points_len = points.length; j < points_len; j++) {
							ctxPattern.lineTo(points[j][0], points[j][1]);
						}
						ctxPattern.lineTo(points[0][0], points[0][1]);
		
						if (i > 0 && i % batchMaxCount === 0) {
		
							if (fill) {
								ctx.fill();
								if (stroke) {
									ctx.stroke();
								}
							} else {
								ctx.stroke();
							}
							
							ctx.restore();
							
							ctx.lineWidth = ctxOptions.lineWidth;
							ctx.lineJoin = ctxOptions.lineJoin;
							ctx.lineCap = ctxOptions.lineCap;
							ctx.strokeStyle = ctxOptions.strokeStyle;
							ctx.fillStyle = ctxOptions.fillStyle;
	
							ctx.beginPath();
							
							done = true;
						} else {
							done = false;
						}
					}
				} else {
					for (var i=0, l=gd.length; i<l; i++) {
						points = getPoints.call(this, gd[i][0], gd[i][1]);
		
						ctxPattern.moveTo(points[0][0], points[0][1]);
						for (var j = 1, points_len = points.length; j < points_len; j++) {
							ctxPattern.lineTo(points[j][0], points[j][1]);
						}
						ctxPattern.lineTo(points[0][0], points[0][1]);
		
						if ((i > 0 && i % batchMaxCount === 0) || (fill && stroke)) {
							if (fill) {
								ctx.fill();
								if (stroke) {
									ctx.stroke();
								}
							} else {
								ctx.stroke();
							}
							
							ctx.restore();
							
							ctx.lineWidth = ctxOptions.lineWidth;
							ctx.lineJoin = ctxOptions.lineJoin;
							ctx.lineCap = ctxOptions.lineCap;
							ctx.strokeStyle = ctxOptions.strokeStyle;
							ctx.fillStyle = ctxOptions.fillStyle;
	
							ctx.beginPath();
							
							done = true;
						} else {
							done = false;
						}
					}
				}
				
			} else {
				for (var i=0, l=gd.length; i<l; i++) {
					points = getPoints.call(this, gd[i][0], gd[i][1]);

					for (var j = 0, points_len = points.length; j < points_len; j++) {
						ctxPattern.moveTo(points[j][0][0], points[j][0][1]);
						for (var k=1; k<points[j].length; k++) {
							ctxPattern.lineTo(points[j][k][0], points[j][k][1]);
						}
					}

					if (i > 0 && i % batchMaxCount === 0) {
	
						if (fill) {
							ctx.fill();
							if (stroke) {
								ctx.stroke();
							}
						} else {
							ctx.stroke();
						}
						
						ctx.restore();
						
						$.extend(true, ctx, ctxOptions);
						
						ctx.beginPath();
						
						done = true;
					} else {
						done = false;
					}
				}
			}

			if (!done) {
				if (fill) {
					ctx.fill();
					if (stroke) {
						ctx.stroke();
					}
				} else {
					ctx.stroke();
				}
			}
		}
		ctx.restore();
	};
    
    // called with scope of axis
    $.jqplot.LinearAxisRenderer.prototype.pack = function(pos, offsets) {
        // Add defaults for repacking from resetTickValues function.
        pos = pos || {};
        offsets = offsets || this._offsets;
        
        var ticks = this._ticks;
        var max = this.max;
        var min = this.min;
        var offmax = offsets.max;
        var offmin = offsets.min;
        var lshow = (this._label == null) ? false : this._label.show;
        
        for (var p in pos) {
            this._elem.css(p, pos[p]);
        }
        
        this._offsets = offsets;
        // pixellength will be + for x axes and - for y axes becasue pixels always measured from top left.
        var pixellength = offmax - offmin;
        var unitlength = max - min;
        this.tempCalcMin = min;
        this.tempCalcMax = max;
        var tempCalcResult_p2u = unitlength / pixellength;
        var tempCalcResult_u2p = pixellength / unitlength;
        this.tempCalcResult_p2u = tempCalcResult_p2u;
        this.tempCalcResult_u2p = tempCalcResult_u2p;
        
        // point to unit and unit to point conversions references to Plot DOM element top left corner.
        if (this.breakPoints) {
            unitlength = unitlength - this.breakPoints[1] + this.breakPoints[0];
            
            this.p2u = function(p){
                return (p - offmin) * unitlength / pixellength + min;
            };
        
            this.u2p = function(u){
                if (u > this.breakPoints[0] && u < this.breakPoints[1]){
                    u = this.breakPoints[0];
                }
                if (u <= this.breakPoints[0]) {
                    return (u - min) * pixellength / unitlength + offmin;
                }
                else {
                    return (u - this.breakPoints[1] + this.breakPoints[0] - min) * pixellength / unitlength + offmin;
                }
            };
                
            if (this.name.charAt(0) == 'x'){
                this.series_u2p = function(u){
                    if (u > this.breakPoints[0] && u < this.breakPoints[1]){
                        u = this.breakPoints[0];
                    }
                    if (u <= this.breakPoints[0]) {
                        return (u - min) * pixellength / unitlength;
                    }
                    else {
                        return (u - this.breakPoints[1] + this.breakPoints[0] - min) * pixellength / unitlength;
                    }
                };
                this.series_p2u = function(p){
                    return p * unitlength / pixellength + min;
                };
            }
        
            else {
                this.series_u2p = function(u){
                    if (u > this.breakPoints[0] && u < this.breakPoints[1]){
                        u = this.breakPoints[0];
                    }
                    if (u >= this.breakPoints[1]) {
                        return (u - max) * pixellength / unitlength;
                    }
                    else {
                        return (u + this.breakPoints[1] - this.breakPoints[0] - max) * pixellength / unitlength;
                    }
                };
                this.series_p2u = function(p){
                    return p * unitlength / pixellength + max;
                };
            }
        }
        else {
            this.p2u = function(p){
                return (p - offmin) * unitlength / pixellength + min;
            };
        
            this.u2p = function(u){
                return (u - min) * pixellength / unitlength + offmin;
            };
                
            if (this.name == 'xaxis' || this.name == 'x2axis'){
                this.series_u2p = function(u){
                    return (u - min) * pixellength / unitlength;
                };
                this.series_p2u = function(p){
                    return p * unitlength / pixellength + min;
                };
            }
        
            else {
                this.series_u2p = function(u){
                    return (u - max) * pixellength / unitlength;
                };
                this.series_p2u = function(p){
                    return p * unitlength / pixellength + max;
                };
            }
        }
        
        if (this.show) {
            if (this.name == 'xaxis' || this.name == 'x2axis') {
                for (var i=0; i<ticks.length; i++) {
                    var t = ticks[i];
                    if (t.show && t.showLabel) {
                        var shim;
                        
                        if (t.constructor == $.jqplot.CanvasAxisTickRenderer && t.angle) {
                            // will need to adjust auto positioning based on which axis this is.
                            var temp = (this.name == 'xaxis') ? 1 : -1;
                            switch (t.labelPosition) {
                                case 'auto':
                                    // position at end
                                    if (temp * t.angle < 0) {
                                        shim = -t.getWidth() + t._textRenderer.height * Math.sin(-t._textRenderer.angle) / 2;
                                    }
                                    // position at start
                                    else {
                                        shim = -t._textRenderer.height * Math.sin(t._textRenderer.angle) / 2;
                                    }
                                    break;
                                case 'end':
                                    shim = -t.getWidth() + t._textRenderer.height * Math.sin(-t._textRenderer.angle) / 2;
                                    break;
                                case 'start':
                                    shim = -t._textRenderer.height * Math.sin(t._textRenderer.angle) / 2;
                                    break;
                                case 'middle':
                                    shim = -t.getWidth()/2 + t._textRenderer.height * Math.sin(-t._textRenderer.angle) / 2;
                                    break;
                                default:
                                    shim = -t.getWidth()/2 + t._textRenderer.height * Math.sin(-t._textRenderer.angle) / 2;
                                    break;
                            }
                        }
                        else {
                            shim = -t.getWidth()/2;
                        }
                        var val = this.u2p(t.value) + shim + 'px';
                        t._elem.css('left', val);
                        t.pack();
                    }
                }
                if (lshow) {
                    var w = this._label._elem.outerWidth(true);
                    this._label._elem.css('left', offmin + pixellength/2 - w/2 + 'px');
                    if (this.name == 'xaxis') {
                        this._label._elem.css('bottom', '0px');
                    }
                    else {
                        this._label._elem.css('top', '0px');
                    }
                    this._label.pack();
                }
            }
            else {
                for (var i=0; i<ticks.length; i++) {
                    var t = ticks[i];
                    if (t.show && t.showLabel) {                        
                        var shim;
                        if (t.constructor == $.jqplot.CanvasAxisTickRenderer && t.angle) {
                            // will need to adjust auto positioning based on which axis this is.
                            var temp = (this.name == 'yaxis') ? 1 : -1;
                            switch (t.labelPosition) {
                                case 'auto':
                                    // position at end
                                case 'end':
                                    if (temp * t.angle < 0) {
                                        shim = -t._textRenderer.height * Math.cos(-t._textRenderer.angle) / 2;
                                    }
                                    else {
                                        shim = -t.getHeight() + t._textRenderer.height * Math.cos(t._textRenderer.angle) / 2;
                                    }
                                    break;
                                case 'start':
                                    if (t.angle > 0) {
                                        shim = -t._textRenderer.height * Math.cos(-t._textRenderer.angle) / 2;
                                    }
                                    else {
                                        shim = -t.getHeight() + t._textRenderer.height * Math.cos(t._textRenderer.angle) / 2;
                                    }
                                    break;
                                case 'middle':
                                    // if (t.angle > 0) {
                                    //     shim = -t.getHeight()/2 + t._textRenderer.height * Math.sin(-t._textRenderer.angle) / 2;
                                    // }
                                    // else {
                                    //     shim = -t.getHeight()/2 - t._textRenderer.height * Math.sin(t._textRenderer.angle) / 2;
                                    // }
                                    shim = -t.getHeight()/2;
                                    break;
                                default:
                                    shim = -t.getHeight()/2;
                                    break;
                            }
                        }
                        else {
                            shim = -t.getHeight()/2;
                        }
                        
                        var val = this.u2p(t.value) + shim + 'px';
                        t._elem.css('top', val);
                        t.pack();
                    }
                }
                if (lshow) {
                    var h = this._label._elem.outerHeight(true);
                    this._label._elem.css('top', offmax - pixellength/2 - h/2 + 'px');
                    if (this.name == 'yaxis') {
                        this._label._elem.css('left', '0px');
                    }
                    else {
                        this._label._elem.css('right', '0px');
                    }   
                    this._label.pack();
                }
            }
        }

        ticks = null;
    };

  // called with scope of axis
  $.jqplot.LinearAxisRenderer.prototype.createTicks = function(plot) {
      // we're are operating on an axis here
      var ticks = this._ticks;
      var userTicks = this.ticks;
      var name = this.name;
      // databounds were set on axis initialization.
      var db = this._dataBounds;
      var dim = (this.name.charAt(0) === 'x') ? this._plotDimensions.width : this._plotDimensions.height;
      var interval;
      var min, max;
      var pos1, pos2;
      var tt, i;
      // get a copy of user's settings for min/max.
      var userMin = this.min;
      var userMax = this.max;
      var userNT = this.numberTicks;
      var userTI = this.tickInterval;

      var threshold = 30;
      this._scalefact =  (Math.max(dim, threshold+1) - threshold)/300.0;
      
      // if we already have ticks, use them.
      // ticks must be in order of increasing value.
      
      if (userTicks.length) {
          // ticks could be 1D or 2D array of [val, val, ,,,] or [[val, label], [val, label], ...] or mixed
          for (i=0; i<userTicks.length; i++){
              var ut = userTicks[i];
              var t = new this.tickRenderer(this.tickOptions);
              if ($.isArray(ut)) {
                  t.value = ut[0];
                  if (this.breakPoints) {
                      if (ut[0] == this.breakPoints[0]) {
                          t.label = this.breakTickLabel;
                          t._breakTick = true;
                          t.showGridline = false;
                          t.showMark = false;
                      }
                      else if (ut[0] > this.breakPoints[0] && ut[0] <= this.breakPoints[1]) {
                          t.show = false;
                          t.showGridline = false;
                          t.label = ut[1];
                      }
                      else {
                          t.label = ut[1];
                      }
                  }
                  else {
                      t.label = ut[1];
                  }
                  t.setTick(ut[0], this.name);
                  this._ticks.push(t);
              }

              else if ($.isPlainObject(ut)) {
                  $.extend(true, t, ut);
                  t.axis = this.name;
                  this._ticks.push(t);
              }
              
              else {
                  t.value = ut;
                  if (this.breakPoints) {
                      if (ut == this.breakPoints[0]) {
                          t.label = this.breakTickLabel;
                          t._breakTick = true;
                          t.showGridline = false;
                          t.showMark = false;
                      }
                      else if (ut > this.breakPoints[0] && ut <= this.breakPoints[1]) {
                          t.show = false;
                          t.showGridline = false;
                      }
                  }
                  t.setTick(ut, this.name);
                  this._ticks.push(t);
              }
          }
          this.numberTicks = userTicks.length;
          this.min = this._ticks[0].value;
          this.max = this._ticks[this.numberTicks-1].value;
          this.tickInterval = (this.max - this.min) / (this.numberTicks - 1);
      }
      
      // we don't have any ticks yet, let's make some!
      else {
          if (name == 'xaxis' || name == 'x2axis') {
              dim = this._plotDimensions.width;
          }
          else {
              dim = this._plotDimensions.height;
          }

          var _numberTicks = this.numberTicks;

          // if aligning this axis, use number of ticks from previous axis.
          // Do I need to reset somehow if alignTicks is changed and then graph is replotted??
          if (this.alignTicks) {
              if (this.name === 'x2axis' && plot.axes.xaxis.show) {
                  _numberTicks = plot.axes.xaxis.numberTicks;
              }
              else if (this.name.charAt(0) === 'y' && this.name !== 'yaxis' && this.name !== 'yMidAxis' && plot.axes.yaxis.show) {
                  _numberTicks = plot.axes.yaxis.numberTicks;
              }
          }
      
          min = ((this.min != null) ? this.min : db.min);
          max = ((this.max != null) ? this.max : db.max);
          
          var range;
          if (max === min && max != null) {
              range = 1;
              max += 0.5;
              min -= 0.5;
          } else {
              range = max - min;
          }
          var rmin, rmax;
          var temp;

          if (this.tickOptions == null || !this.tickOptions.formatString) {
              this._overrideFormatString = true;
          }

          // Doing complete autoscaling
          if ((this.min == null || this.max == null) && this.tickInterval == null && this.autoscale) {
              // Check if user must have tick at 0 or 100 and ensure they are in range.
              // The autoscaling algorithm will always place ticks at 0 and 100 if they are in range.
              if (this.forceTickAt0) {
                  if (min > 0) {
                      min = 0;
                  }
                  if (max < 0) {
                      max = 0;
                  }
              }

              if (this.forceTickAt100) {
                  if (min > 100) {
                      min = 100;
                  }
                  if (max < 100) {
                      max = 100;
                  }
              }

              var keepMin = false,
                  keepMax = false;

              if (this.min != null) {
                  keepMin = true;
              }

              else if (this.max != null) {
                  keepMax = true;
              }

              // var threshold = 30;
              // var tdim = Math.max(dim, threshold+1);
              // this._scalefact =  (tdim-threshold)/300.0;
              var ret = $.jqplot.LinearTickGenerator(min, max, this._scalefact, _numberTicks, keepMin, keepMax); 
              // calculate a padded max and min, points should be less than these
              // so that they aren't too close to the edges of the plot.
              // User can adjust how much padding is allowed with pad, padMin and PadMax options. 
              // If min or max is set, don't pad that end of axis.
              var tumin = (this.min != null) ? min : min + range*(this.padMin - 1);
              var tumax = (this.max != null) ? max : max - range*(this.padMax - 1);

              // if they're equal, we shouldn't have to do anything, right?
              // if (min <=tumin || max >= tumax) {
              if (min <tumin || max > tumax) {
                  tumin = (this.min != null) ? min : min - range*(this.padMin - 1);
                  tumax = (this.max != null) ? max : max + range*(this.padMax - 1);
                  ret = $.jqplot.LinearTickGenerator(tumin, tumax, this._scalefact, _numberTicks, keepMin, keepMax);
              }

              this.min = ret[0];
              this.max = ret[1];
              // if numberTicks specified, it should return the same.
              this.numberTicks = ret[2];
              this._autoFormatString = ret[3];
              this.tickInterval = ret[4];
          } 

          // User has specified some axis scale related option, can use auto algorithm
          else {
              
              // if min and max are same, space them out a bit
              if (min == max) {
                  var adj = 0.05;
                  if (min > 0) {
                      adj = Math.max(Math.log(min)/Math.LN10, 0.05);
                  }
                  min -= adj;
                  max += adj;
              }
              
              // autoscale.  Can't autoscale if min or max is supplied.
              // Will use numberTicks and tickInterval if supplied.  Ticks
              // across multiple axes may not line up depending on how
              // bars are to be plotted.
              if (!this.autoscale && (this.min == null || this.max == null) && plot.options.axes[this.name].tickSpacing) {
                  var rrange, ti, margin;
                  var forceMinZero = false;
                  var forceZeroLine = false;
                  var intervals = {min:null, max:null, average:null, stddev:null};
                  // if any series are bars, or if any are fill to zero, and if this
                  // is the axis to fill toward, check to see if we can start axis at zero.
                  for (var i=0; i<this._series.length; i++) {
                      var s = this._series[i];
                      
                      if (s.renderer.constructor == $.jqplot.BigDataScatterRenderer) {
                      	continue;
                      }
                      
                      var faname = (s.fillAxis == 'x') ? s._xaxis.name : s._yaxis.name;
                      // check to see if this is the fill axis
                      if (this.name == faname) {
                          var vals = s._plotValues[s.fillAxis];
                          var vmin = vals[0];
                          var vmax = vals[0];
                          for (var j=1; j<vals.length; j++) {
                              if (vals[j] < vmin) {
                                  vmin = vals[j];
                              }
                              else if (vals[j] > vmax) {
                                  vmax = vals[j];
                              }
                          }
                          var dp = (vmax - vmin) / vmax;
                          // is this sries a bar?
                          if (s.renderer.constructor == $.jqplot.BarRenderer) {
                              // if no negative values and could also check range.
                              if (vmin >= 0 && (s.fillToZero || dp > 0.1)) {
                                  forceMinZero = true;
                              }
                              else {
                                  forceMinZero = false;
                                  if (s.fill && s.fillToZero && vmin < 0 && vmax > 0) {
                                      forceZeroLine = true;
                                  }
                                  else {
                                      forceZeroLine = false;
                                  }
                              }
                          }
                          
                          // if not a bar and filling, use appropriate method.
                          else if (s.fill) {
                              if (vmin >= 0 && (s.fillToZero || dp > 0.1)) {
                                  forceMinZero = true;
                              }
                              else if (vmin < 0 && vmax > 0 && s.fillToZero) {
                                  forceMinZero = false;
                                  forceZeroLine = true;
                              }
                              else {
                                  forceMinZero = false;
                                  forceZeroLine = false;
                              }
                          }
                          
                          // if not a bar and not filling, only change existing state
                          // if it doesn't make sense
                          else if (vmin < 0) {
                              forceMinZero = false;
                          }
                      }
                  }
                  
                  // check if we need make axis min at 0.
                  if (forceMinZero) {
                      // compute number of ticks
                      this.numberTicks = 2 + Math.ceil((dim-(this.tickSpacing-1))/this.tickSpacing);
                      this.min = 0;
                      userMin = 0;
                      // what order is this range?
                      // what tick interval does that give us?
                      ti = max/(this.numberTicks-1);
                      temp = Math.pow(10, Math.abs(Math.floor(Math.log(ti)/Math.LN10)));
                      if (ti/temp == parseInt(ti/temp, 10)) {
                          ti += temp;
                      }
                      this.tickInterval = Math.ceil(ti/temp) * temp;
                      this.max = this.tickInterval * (this.numberTicks - 1);
                  }
                  
                  // check if we need to make sure there is a tick at 0.
                  else if (forceZeroLine) {
                      // compute number of ticks
                      this.numberTicks = 2 + Math.ceil((dim-(this.tickSpacing-1))/this.tickSpacing);
                      var ntmin = Math.ceil(Math.abs(min)/range*(this.numberTicks-1));
                      var ntmax = this.numberTicks - 1  - ntmin;
                      ti = Math.max(Math.abs(min/ntmin), Math.abs(max/ntmax));
                      temp = Math.pow(10, Math.abs(Math.floor(Math.log(ti)/Math.LN10)));
                      this.tickInterval = Math.ceil(ti/temp) * temp;
                      this.max = this.tickInterval * ntmax;
                      this.min = -this.tickInterval * ntmin;
                  }
                  
                  // if nothing else, do autoscaling which will try to line up ticks across axes.
                  else {  
                      if (this.numberTicks == null){
                          if (this.tickInterval) {
                              this.numberTicks = 3 + Math.ceil(range / this.tickInterval);
                          }
                          else {
                              this.numberTicks = 2 + Math.ceil((dim-(this.tickSpacing-1))/this.tickSpacing);
                          }
                      }
              
                      if (this.tickInterval == null) {
                          // get a tick interval
                          ti = range/(this.numberTicks - 1);

                          if (ti < 1) {
                              temp = Math.pow(10, Math.abs(Math.floor(Math.log(ti)/Math.LN10)));
                          }
                          else {
                              temp = 1;
                          }
                          this.tickInterval = Math.ceil(ti*temp*this.pad)/temp;
                      }
                      else {
                          temp = 1 / this.tickInterval;
                      }
                      
                      // try to compute a nicer, more even tick interval
                      // temp = Math.pow(10, Math.floor(Math.log(ti)/Math.LN10));
                      // this.tickInterval = Math.ceil(ti/temp) * temp;
                      rrange = this.tickInterval * (this.numberTicks - 1);
                      margin = (rrange - range)/2;
         
                      if (this.min == null) {
                          this.min = Math.floor(temp*(min-margin))/temp;
                      }
                      if (this.max == null) {
                          this.max = this.min + rrange;
                      }
                  }

                  // Compute a somewhat decent format string if it is needed.
                  // get precision of interval and determine a format string.
                  var sf = $.jqplot.getSignificantFigures(this.tickInterval);

                  var fstr;

                  // if we have only a whole number, use integer formatting
                  if (sf.digitsLeft >= sf.significantDigits) {
                      fstr = '%d';
                  }

                  else {
                      var temp = Math.max(0, 5 - sf.digitsLeft);
                      temp = Math.min(temp, sf.digitsRight);
                      fstr = '%.'+ temp + 'f';
                  }

                  this._autoFormatString = fstr;
              }
              
              // Use the default algorithm which pads each axis to make the chart
              // centered nicely on the grid.
              else {

                  rmin = (this.min != null) ? this.min : min - range*(this.padMin - 1);
                  rmax = (this.max != null) ? this.max : max + range*(this.padMax - 1);
                  range = rmax - rmin;
      
                  if (this.numberTicks == null){
                      // if tickInterval is specified by user, we will ignore computed maximum.
                      // max will be equal or greater to fit even # of ticks.
                      if (this.tickInterval != null) {
                          this.numberTicks = Math.ceil((rmax - rmin)/this.tickInterval)+1;
                      }
                      else if (dim > 100) {
                          this.numberTicks = parseInt(3+(dim-100)/75, 10);
                      }
                      else {
                          this.numberTicks = 2;
                      }
                  }
              
                  if (this.tickInterval == null) {
                      this.tickInterval = range / (this.numberTicks-1);
                  }
                  
                  if (this.max == null) {
                      rmax = rmin + this.tickInterval*(this.numberTicks - 1);
                  }        
                  if (this.min == null) {
                      rmin = rmax - this.tickInterval*(this.numberTicks - 1);
                  }

                  // get precision of interval and determine a format string.
                  var sf = $.jqplot.getSignificantFigures(this.tickInterval);

                  var fstr;

                  // if we have only a whole number, use integer formatting
                  if (sf.digitsLeft >= sf.significantDigits) {
                      fstr = '%d';
                  }

                  else {
                      var temp = Math.max(0, 5 - sf.digitsLeft);
                      temp = Math.min(temp, sf.digitsRight);
                      fstr = '%.'+ temp + 'f';
                  }


                  this._autoFormatString = fstr;

                  this.min = rmin;
                  this.max = rmax;
              }
              
              if (this.renderer.constructor == $.jqplot.LinearAxisRenderer && this._autoFormatString == '') {
                  // fix for misleading tick display with small range and low precision.
                  range = this.max - this.min;
                  // figure out precision
                  var temptick = new this.tickRenderer(this.tickOptions);
                  // use the tick formatString or, the default.
                  var fs = temptick.formatString || $.jqplot.config.defaultTickFormatString; 
                  var fs = fs.match($.jqplot.sprintf.regex)[0];
                  var precision = 0;
                  if (fs) {
                      if (fs.search(/[fFeEgGpP]/) > -1) {
                          var m = fs.match(/\%\.(\d{0,})?[eEfFgGpP]/);
                          if (m) {
                              precision = parseInt(m[1], 10);
                          }
                          else {
                              precision = 6;
                          }
                      }
                      else if (fs.search(/[di]/) > -1) {
                          precision = 0;
                      }
                      // fact will be <= 1;
                      var fact = Math.pow(10, -precision);
                      if (this.tickInterval < fact) {
                          // need to correct underrange
                          if (userNT == null && userTI == null) {
                              this.tickInterval = fact;
                              if (userMax == null && userMin == null) {
                                  // this.min = Math.floor((this._dataBounds.min - this.tickInterval)/fact) * fact;
                                  this.min = Math.floor(this._dataBounds.min/fact) * fact;
                                  if (this.min == this._dataBounds.min) {
                                      this.min = this._dataBounds.min - this.tickInterval;
                                  }
                                  // this.max = Math.ceil((this._dataBounds.max + this.tickInterval)/fact) * fact;
                                  this.max = Math.ceil(this._dataBounds.max/fact) * fact;
                                  if (this.max == this._dataBounds.max) {
                                      this.max = this._dataBounds.max + this.tickInterval;
                                  }
                                  var n = (this.max - this.min)/this.tickInterval;
                                  n = n.toFixed(11);
                                  n = Math.ceil(n);
                                  this.numberTicks = n + 1;
                              }
                              else if (userMax == null) {
                                  // add one tick for top of range.
                                  var n = (this._dataBounds.max - this.min) / this.tickInterval;
                                  n = n.toFixed(11);
                                  this.numberTicks = Math.ceil(n) + 2;
                                  this.max = this.min + this.tickInterval * (this.numberTicks-1);
                              }
                              else if (userMin == null) {
                                  // add one tick for bottom of range.
                                  var n = (this.max - this._dataBounds.min) / this.tickInterval;
                                  n = n.toFixed(11);
                                  this.numberTicks = Math.ceil(n) + 2;
                                  this.min = this.max - this.tickInterval * (this.numberTicks-1);
                              }
                              else {
                                  // calculate a number of ticks so max is within axis scale
                                  this.numberTicks = Math.ceil((userMax - userMin)/this.tickInterval) + 1;
                                  // if user's min and max don't fit evenly in ticks, adjust.
                                  // This takes care of cases such as user min set to 0, max set to 3.5 but tick
                                  // format string set to %d (integer ticks)
                                  this.min =  Math.floor(userMin*Math.pow(10, precision))/Math.pow(10, precision);
                                  this.max =  Math.ceil(userMax*Math.pow(10, precision))/Math.pow(10, precision);
                                  // this.max = this.min + this.tickInterval*(this.numberTicks-1);
                                  this.numberTicks = Math.ceil((this.max - this.min)/this.tickInterval) + 1;
                              }
                          }
                      }
                  }
              }
              
          }
          
          if (this._overrideFormatString && this._autoFormatString != '') {
              this.tickOptions = this.tickOptions || {};
              this.tickOptions.formatString = this._autoFormatString;
          }

          var t, to;
          for (var i=0; i<this.numberTicks; i++){
              tt = this.min + i * this.tickInterval;
              t = new this.tickRenderer(this.tickOptions);
              // var t = new $.jqplot.AxisTickRenderer(this.tickOptions);

              t.setTick(tt, this.name);
              this._ticks.push(t);

              if (i < this.numberTicks - 1) {
                  for (var j=0; j<this.minorTicks; j++) {
                      tt += this.tickInterval/(this.minorTicks+1);
                      to = $.extend(true, {}, this.tickOptions, {name:this.name, value:tt, label:'', isMinorTick:true});
                      t = new this.tickRenderer(to);
                      this._ticks.push(t);
                  }
              }
              t = null;
          }
      }

      if (this.tickInset) {
          this.min = this.min - this.tickInset * this.tickInterval;
          this.max = this.max + this.tickInset * this.tickInterval;
      }

      ticks = null;
  };

	// Override jqPlot's inner functions
	$.each($.jqplot.applyEnhance({'Axis': Axis, 'Series': Series, 'jqPlot': jqPlot}), function(name, func) {
		eval(name + '=func');
	});
	
	$.fn.jqplotToImageStr = function(options) {
	  var defer = new $.Deferred();
	  try {
      var imgCanvas = html2canvas(this, {
        onrendered: function(canvas) {
            defer.resolve(canvas.toDataURL("image/png"));
            canvas = null;
        },
        letterRendering: true
      });
	  } catch(e) {
	    defer.reject('');
	  }

      return defer.promise();
  };
  
  // return a DOM <img> element and return it.
  // Should work on canvas supporting browsers.
  $.fn.jqplotToImageElem = function(options) {
    var defer = new $.Deferred();
    var elem = document.createElement("img");
    $(this).jqplotToImageStr(options).then(function(str) {
      
      elem.src = str;
      
      defer.resolve(elem);
    }, function() {
      defer.reject();
    });
    return defer.promise();
  };

  // return a string for an <img> element and return it.
  // Should work on canvas supporting browsers.
  $.fn.jqplotToImageElemStr = function(options) {
    var defer = new $.Deferred();
    
    $(this).jqplotToImageStr(options).then(function(str) {
      
      str = '<img src='+str+' />';
      
      defer.resolve(str);
    }, function() {
      defer.reject();
    });
    
    return defer.promise();
  };

  // Not guaranteed to work, even on canvas supporting browsers due to 
  // limitations with location.href and browser support.
  $.fn.jqplotSaveImage = function() {
    var defer = new $.Deferred();
    
    $(this).jqplotToImageStr({}).then(function(str) {
      
      if (str) {
          window.location.href = str.replace("image/png", "image/octet-stream");
      }
      
      defer.resolve();
    }, function() {
      defer.reject();
    });
    
    return defer.promise();
  };

  // Not guaranteed to work, even on canvas supporting browsers due to
  // limitations with window.open and arbitrary data.
  $.fn.jqplotViewImage = function() {
    var defer = new $.Deferred();
    
    $(this).jqplotToImageElemStr({}).then(function(imgStr) {

      if (imgStr) {
          var w = window.open('');
          w.document.open("image/png");
          w.document.write(imgStr);
          w.document.close();
          w = null;
      }
      
      defer.resolve();
    }, function() {
      defer.reject();
    });
    
    return defer.promise();
  };
	
    /**
     * Namespace: $.fn
     * jQuery namespace to attach functions to jQuery elements.
     * modified by Roy because of infinite loop in writeWrappedText function
     * ref: https://bitbucket.org/cleonello/jqplot/issue/833/font-scaling-in-firefox-22-causes-infinite
     */

    $.fn.jqplotToImageCanvas = function(options) {

        options = options || {};
        var x_offset = (options.x_offset == null) ? 0 : options.x_offset;
        var y_offset = (options.y_offset == null) ? 0 : options.y_offset;
        var backgroundColor = (options.backgroundColor == null) ? 'rgb(255,255,255)' : options.backgroundColor;

        if ($(this).width() == 0 || $(this).height() == 0) {
            return null;
        }

        // excanvas and hence IE < 9 do not support toDataURL and cannot export images.
        if ($.jqplot.use_excanvas) {
            return null;
        }
        
        var newCanvas = document.createElement("canvas");
        var h = $(this).outerHeight(true);
        var w = $(this).outerWidth(true);
        var offs = $(this).offset();
        var plotleft = offs.left;
        var plottop = offs.top;
        var transx = 0, transy = 0;

        // have to check if any elements are hanging outside of plot area before rendering,
        // since changing width of canvas will erase canvas.

        var clses = ['jqplot-table-legend', 'jqplot-xaxis-tick', 'jqplot-x2axis-tick', 'jqplot-yaxis-tick', 'jqplot-y2axis-tick', 'jqplot-y3axis-tick', 
        'jqplot-y4axis-tick', 'jqplot-y5axis-tick', 'jqplot-y6axis-tick', 'jqplot-y7axis-tick', 'jqplot-y8axis-tick', 'jqplot-y9axis-tick',
        'jqplot-xaxis-label', 'jqplot-x2axis-label', 'jqplot-yaxis-label', 'jqplot-y2axis-label', 'jqplot-y3axis-label', 'jqplot-y4axis-label', 
        'jqplot-y5axis-label', 'jqplot-y6axis-label', 'jqplot-y7axis-label', 'jqplot-y8axis-label', 'jqplot-y9axis-label', 'jqplot-highlighter-tooltip-wrapper', 'jqplot-highlighter-tooltip' ];

        var temptop, templeft, tempbottom, tempright;

        for (var i = 0; i < clses.length; i++) {
            $(this).find('.'+clses[i]).each(function() {
                temptop = $(this).offset().top - plottop;
                templeft = $(this).offset().left - plotleft;
                tempright = templeft + $(this).outerWidth(true) + transx;
                tempbottom = temptop + $(this).outerHeight(true) + transy;
                if (templeft < -transx) {
                    w = w - transx - templeft;
                    transx = -templeft;
                }
                if (temptop < -transy) {
                    h = h - transy - temptop;
                    transy = - temptop;
                }
                if (tempright > w) {
                    w = tempright;
                }
                if (tempbottom > h) {
                    h =  tempbottom;
                }
            });
        }

        newCanvas.width = w + Number(x_offset);
        newCanvas.height = h + Number(y_offset);

        var newContext = newCanvas.getContext("2d"); 

        newContext.save();
        newContext.fillStyle = backgroundColor;
        newContext.fillRect(0,0, newCanvas.width, newCanvas.height);
        newContext.restore();

        newContext.translate(transx, transy);
        newContext.textAlign = 'left';
        newContext.textBaseline = 'top';

        function getLineheight(el) {
            var lineheight = parseInt($(el).css('line-height'), 10);

            if (isNaN(lineheight)) {
                lineheight = parseInt($(el).css('font-size'), 10) * 1.2;
            }
            return lineheight;
        }

        function writeWrappedText (el, context, text, left, top, canvasWidth) {
            var lineheight = getLineheight(el);
            var tagwidth = $(el).innerWidth();
            var tagheight = $(el).innerHeight();
            var words = text.split(/\s+/);
            var wl = words.length;
            var w = '';
            var breaks = [];
            var temptop = top;
            var templeft = left;

            for (var i=0; i<wl; i++) {
                w += words[i];
                //only add breaks if there is more than one word
                if (context.measureText(w).width > tagwidth && i > 0) { // cusomized ref. : https://bitbucket.org/cleonello/jqplot/issue/833/font-scaling-in-firefox-22-causes-infinite
                    breaks.push(i);
                    w = '';
                    i--;
                }   
            }
            if (breaks.length === 0) {
                // center text if necessary
                if ($(el).css('textAlign') === 'center') {
                    templeft = left + (canvasWidth - context.measureText(w).width)/2  - transx;
                }
                context.fillText(text, templeft, top);
            }
            else {
                w = words.slice(0, breaks[0]).join(' ');
                // center text if necessary
                if ($(el).css('textAlign') === 'center') {
                    templeft = left + (canvasWidth - context.measureText(w).width)/2  - transx;
                }
                context.fillText(w, templeft, temptop);
                temptop += lineheight;
                for (var i=1, l=breaks.length; i<l; i++) {
                    w = words.slice(breaks[i-1], breaks[i]).join(' ');
                    // center text if necessary
                    if ($(el).css('textAlign') === 'center') {
                        templeft = left + (canvasWidth - context.measureText(w).width)/2  - transx;
                    }
                    context.fillText(w, templeft, temptop);
                    temptop += lineheight;
                }
                w = words.slice(breaks[i-1], words.length).join(' ');
                // center text if necessary
                if ($(el).css('textAlign') === 'center') {
                    templeft = left + (canvasWidth - context.measureText(w).width)/2  - transx;
                }
                context.fillText(w, templeft, temptop);
            }

        }

        function _jqpToImage(el, x_offset, y_offset) {
            var tagname = el.tagName.toLowerCase();
            var p = $(el).position();
            var css = window.getComputedStyle ?  window.getComputedStyle(el, "") : el.currentStyle; // for IE < 9
            var left = x_offset + p.left + parseInt(css.marginLeft, 10) + parseInt(css.borderLeftWidth, 10) + parseInt(css.paddingLeft, 10);
            var top = y_offset + p.top + parseInt(css.marginTop, 10) + parseInt(css.borderTopWidth, 10)+ parseInt(css.paddingTop, 10);
            var w = newCanvas.width;
            // var left = x_offset + p.left + $(el).css('marginLeft') + $(el).css('borderLeftWidth') 

            // somehow in here, for divs within divs, the width of the inner div should be used instead of the canvas.

            if ((tagname == 'div' || tagname == 'span' || tagname == 'ul' || tagname == 'li') /*&& !$(el).hasClass('jqplot-highlighter-tooltip')*/) {
                $(el).children().each(function() {
                    _jqpToImage(this, left, top);
                });
                var text = $(el).jqplotChildText();

                if (text) {
                    newContext.font = $(el).jqplotGetComputedFontStyle();
                    newContext.fillStyle = $(el).css('color');

                    writeWrappedText(el, newContext, text, left, top, w);
                }
            }

            // handle the standard table legend

            else if (tagname === 'table' && $(el).hasClass('jqplot-table-legend')) {
                newContext.strokeStyle = $(el).css('border-top-color');
                newContext.fillStyle = $(el).css('background-color');
                newContext.fillRect(left, top, $(el).innerWidth(), $(el).innerHeight());
                if (parseInt($(el).css('border-top-width'), 10) > 0) {
                    newContext.strokeRect(left, top, $(el).innerWidth(), $(el).innerHeight());
                }

                // find all the swatches
                $(el).find('div.jqplot-table-legend-swatch-outline').each(function() {
                    // get the first div and stroke it
                    var elem = $(this);
                    newContext.strokeStyle = elem.css('border-top-color');
                    var l = left + elem.position().left;
                    var t = top + elem.position().top;
                    newContext.strokeRect(l, t, elem.innerWidth(), elem.innerHeight());

                    // now fill the swatch
                    
                    l += parseInt(elem.css('padding-left'), 10);
                    t += parseInt(elem.css('padding-top'), 10);
                    var h = elem.innerHeight() - 2 * parseInt(elem.css('padding-top'), 10);
                    var w = elem.innerWidth() - 2 * parseInt(elem.css('padding-left'), 10);

                    var swatch = elem.children('div.jqplot-table-legend-swatch');
                    newContext.fillStyle = swatch.css('background-color');
                    newContext.fillRect(l, t, w, h);
                });

                // now add text

                $(el).find('td.jqplot-table-legend-label').each(function(){
                    var elem = $(this);
                    var l = left + elem.position().left;
                    var t = top + elem.position().top + parseInt(elem.css('padding-top'), 10);
                    newContext.font = elem.jqplotGetComputedFontStyle();
                    newContext.fillStyle = elem.css('color');
                    writeWrappedText(elem, newContext, elem.text(), l, t, w);
                });

                var elem = null;
            }

            else if (tagname == 'canvas') {
                newContext.drawImage(el, left, top);
            }
        }
        $(this).children().each(function() {
            _jqpToImage(this, x_offset, y_offset);
        });
        return newCanvas;
    };
	
	// externel Series
	$.jqplot.Series = Series;
})(jQuery);;/**
 * jqPlot
 * Pure JavaScript plotting plugin using jQuery
 *
 * Version: 1.0.8
 * Revision: 1250
 *
 * Copyright (c) 2009-2013 Chris Leonello
 * jqPlot is currently available for use in all personal or commercial projects
 * under both the MIT (http://www.opensource.org/licenses/mit-license.php) and GPL
 * version 2.0 (http://www.gnu.org/licenses/gpl-2.0.html) licenses. This means that you can
 * choose the license that best suits your project and use it accordingly.
 *
 * Although not required, the author would appreciate an email letting him
 * know of any substantial use of jqPlot.  You can reach the author at:
 * chris at jqplot dot com or see http://www.jqplot.com/info.php .
 *
 * If you are feeling kind and generous, consider supporting the project by
 * making a donation at: http://www.jqplot.com/donate.php .
 *
 * sprintf functions contained in jqplot.sprintf.js by Ash Searle:
 *
 *     version 2007.04.27
 *     author Ash Searle
 *     http://hexmen.com/blog/2007/03/printf-sprintf/
 *     http://hexmen.com/js/sprintf.js
 *     The author (Ash Searle) has placed this code in the public domain:
 *     "This code is unrestricted: you are free to use it however you like."
 *
 */
(function($) {
	if ($.jqplot.Cursor) {
		$.jqplot.preInitHooks.forEach(function(callback, ind) {
			if (callback === $.jqplot.Cursor.init) {
				$.jqplot.preInitHooks.splice(ind, 1);
				return;
			}
		});
		$.jqplot.postDrawHooks.forEach(function(callback, ind) {
			if (callback === $.jqplot.Cursor.postDraw) {
				$.jqplot.postDrawHooks.splice(ind, 1);
				return;
			}
		});
	}

	/**
	 * Class: $.jqplot.Cursor
	 * Plugin class representing the cursor as displayed on the plot.
	 */
	$.jqplot.Cursor = function(options) {
		// Group: Properties
		//
		// prop: style
		// CSS spec for cursor style
		this.style = 'crosshair';
		this.previousCursor = 'auto';
		// prop: show
		// wether to show the cursor or not.
		this.show = $.jqplot.config.enablePlugins;
		// prop: showTooltip
		// show a cursor position tooltip.  Location of the tooltip
		// will be controlled by followMouse and tooltipLocation.
		this.showTooltip = true;
		// prop: followMouse
		// Tooltip follows the mouse, it is not at a fixed location.
		// Tooltip will show on the grid at the location given by
		// tooltipLocation, offset from the grid edge by tooltipOffset.
		this.followMouse = false;
		// prop: tooltipLocation
		// Where to position tooltip.  If followMouse is true, this is
		// relative to the cursor, otherwise, it is relative to the grid.
		// One of 'n', 'ne', 'e', 'se', 's', 'sw', 'w', 'nw'
		this.tooltipLocation = 'se';
		// prop: tooltipOffset
		// Pixel offset of tooltip from the grid boudaries or cursor center.
		this.tooltipOffset = 6;
		// prop: showTooltipGridPosition
		// show the grid pixel coordinates of the mouse.
		this.showTooltipGridPosition = false;
		// prop: showTooltipUnitPosition
		// show the unit (data) coordinates of the mouse.
		this.showTooltipUnitPosition = true;
		// prop: showTooltipDataPosition
		// Used with showVerticalLine to show intersecting data points in the tooltip.
		this.showTooltipDataPosition = false;
		// prop: tooltipFormatString
		// sprintf format string for the tooltip.
		// Uses Ash Searle's javascript sprintf implementation
		// found here: http://hexmen.com/blog/2007/03/printf-sprintf/
		// See http://perldoc.perl.org/functions/sprintf.html for reference
		// Note, if showTooltipDataPosition is true, the default tooltipFormatString
		// will be set to the cursorLegendFormatString, not the default given here.
		this.tooltipFormatString = '%.4P, %.4P';
		// prop: useAxesFormatters
		// Use the x and y axes formatters to format the text in the tooltip.
		this.useAxesFormatters = true;
		// prop: tooltipAxisGroups
		// Show position for the specified axes.
		// This is an array like [['xaxis', 'yaxis'], ['xaxis', 'y2axis']]
		// Default is to compute automatically for all visible axes.
		this.tooltipAxisGroups = [];
		// prop: zoom
		// Enable plot zooming.
		this.zoom = false;
		// zoomProxy and zoomTarget properties are not directly set by user.
		// They Will be set through call to zoomProxy method.
		this.zoomProxy = false;
		this.zoomTarget = false;
		// prop: looseZoom
		// Will expand zoom range to provide more rounded tick values.
		// Works only with linear axes and date axes.
		this.looseZoom = false;
		// prop: clickReset
		// Will reset plot zoom if single click on plot without drag.
		this.clickReset = false;
		// prop: dblClickReset
		// Will reset plot zoom if double click on plot without drag.
		this.dblClickReset = true;
		// prop: showVerticalLine
		// draw a vertical line across the plot which follows the cursor.
		// When the line is near a data point, a special legend and/or tooltip can
		// be updated with the data values.
		this.showVerticalLine = false;
		// prop: showHorizontalLine
		// draw a horizontal line across the plot which follows the cursor.
		this.showHorizontalLine = false;
		// prop: constrainZoomTo
		// 'none', 'x' or 'y'
		this.constrainZoomTo = 'none';
		// // prop: autoscaleConstraint
		// // when a constrained axis is specified, true will
		// // auatoscale the adjacent axis.
		// this.autoscaleConstraint = true;
		this.shapeRenderer = new $.jqplot.ShapeRenderer();
		this._zoom = {
			start : [],
			end : [],
			started : false,
			zooming : false,
			isZoomed : false,
			axes : {
				start : {},
				end : {}
			},
			gridpos : {},
			datapos : {}
		};
		this._tooltipElem;
		this.zoomCanvas;
		this.cursorCanvas;
		// prop: intersectionThreshold
		// pixel distance from data point or marker to consider cursor lines intersecting with point.
		// If data point markers are not shown, this should be >= 1 or will often miss point intersections.
		this.intersectionThreshold = 2;
		// prop: showCursorLegend
		// Replace the plot legend with an enhanced legend displaying intersection information.
		this.showCursorLegend = false;
		// prop: cursorLegendFormatString
		// Format string used in the cursor legend.  If showTooltipDataPosition is true,
		// this will also be the default format string used by tooltipFormatString.
		this.cursorLegendFormatString = $.jqplot.Cursor.cursorLegendFormatString;
		// whether the cursor is over the grid or not.
		this._oldHandlers = {
			onselectstart : null,
			ondrag : null,
			onmousedown : null
		};
		// prop: constrainOutsideZoom
		// True to limit actual zoom area to edges of grid, even when zooming
		// outside of plot area.  That is, can't zoom out by mousing outside plot.
		this.constrainOutsideZoom = true;
		// prop: showTooltipOutsideZoom
		// True will keep updating the tooltip when zooming of the grid.
		this.showTooltipOutsideZoom = false;
		// true if mouse is over grid, false if not.
		this.onGrid = false;

		// customizing - snapshot (2012-04-09, Roy Choi)
		this.snapshotOn = false;
		this.snapshotWin = null;
		var jqplot_cursor_localpath;
		$('script').each(function(ind, val) {
			if (val.src.search(/jqplot\.cursor/) > -1) {
				jqplot_cursor_localpath = val.src.replace(/^(.*)plugins\/jqplot\.cursor.*$/i, "$1");
			}
		});
		this.localpath = jqplot_cursor_localpath;
		this.zoomHistory = [];
		this.right2leftUndo = true;
		this.right2leftUndoWidth = 6;
		this.right2leftResetWidth = 40;
		this.right2leftUndoDirection = 20;
		this.startGridPoint = [];
		this.endGridPoint = [];
		//this._prevzoom = {axes:{start:{}, end:{}}, gridpos:{}, datapos:{}};

		// customizing - drag (2012-04-23, Roy Choi)
		this.draggable = false;
		this._drag = {
			start : [],
			end : [],
			started : false,
			dragging : false,
			isDragged : false,
			axes : {
				start : {},
				end : {}
			},
			gridpos : {},
			datapos : {}
		};
		this.constrainDragTo = 'none';
		this.dragStartAxes = {};

		// series shift customizing (2012-05-11, Roy Choi)
		this.seriesShift = false;
		this.seriesShiftIndex = null;
		this.seriesShiftStyle = 'horizontal';
		this.seriesShiftOrgData = null;
		this.shiftedSeries = [];
		this.shiftedSeriesBase = null;
		this._shift = {
			start : [],
			end : [],
			started : false,
			shifting : false,
			isShifted : false,
			data : []
		};

		// move zoombox customizing (2013-02-21, Roy Choi)
		this.zoomboxThreshold = 5;
		this.endDataPos = {};
		this.drawOnZoomBoxMove = false;
		//this.overZoomBoxLineType = null;		// Type Case : sv, sh, svh, ev, eh, evh
		this.prevZoomInfo = {
			start : [],
			end : [],
			started : false,
			zooming : false,
			isZoomed : false,
			axes : {
				start : {},
				end : {}
			},
			gridpos : {},
			datapos : {}
		};
		this.prevCursorStyle = null;
		this.zoomboxMoving = false;
		this.zoomboxDraging = false;
		//this.zoomboxMoveStart = false;

		// ---------------------- CUSTOMIZING END --------------------------------- //

		$.extend(true, this, options);
	};

	$.jqplot.Cursor.cursorLegendFormatString = '%s x:%s, y:%s';

	// called with scope of plot
	$.jqplot.Cursor.init = function(target, data, opts) {
	  // If highlighter.selectable is on then zoom is not available 
	  if (opts.highlighter && opts.highlighter.selectable && opts.highlighter.selectable.show) {
	    opts.cursor.zoom = false;
	  }
	  
		// add a cursor attribute to the plot
		var options = opts || {};
		this.plugins.cursor = new $.jqplot.Cursor(options.cursor);
		var c = this.plugins.cursor;

		if (c.show) {
			$.jqplot.eventListenerHooks.push(['jqplotMouseEnter', handleMouseEnter]);
			$.jqplot.eventListenerHooks.push(['jqplotMouseLeave', handleMouseLeave]);
			$.jqplot.eventListenerHooks.push(['jqplotMouseMove', handleMouseMove]);

			if (c.showCursorLegend) {
				opts.legend = opts.legend || {};
				opts.legend.renderer = $.jqplot.CursorLegendRenderer;
				opts.legend.formatString = this.plugins.cursor.cursorLegendFormatString;
				opts.legend.show = true;
			}

			if (c.zoom) {
				$.jqplot.eventListenerHooks.push(['jqplotMouseDown', handleMouseDown]);

				if (c.draggable) {
					$.jqplot.eventListenerHooks.push(['jqplotRightDown', handleRightDown]);
				}

				if (c.clickReset) {
					$.jqplot.eventListenerHooks.push(['jqplotClick', handleClick]);
				}

				if (c.dblClickReset) {
					$.jqplot.eventListenerHooks.push(['jqplotDblClick', handleDblClick]);
				}
			}

			this.resetZoom = function() {
				if (this.plugins.groupplot && this.plugins.groupplot.ischild && this.plugins.groupplot.parent.plugins.groupplot.groupZoom) {// customizing (2012-05-10, Roy Choi)
					var gplots = this.plugins.groupplot.parent.plugins.groupplot.plots;
					$.each(gplots, function(gid, gplot) {
						var axes = gplot.axes;
						var gc = gplot.plugins.cursor;
						gc.resetzooming = true;
						for (var ax in axes) {
							axes[ax].reset();
							axes[ax]._ticks = [];
							// fake out tick creation algorithm to make sure original auto
							// computed format string is used if _overrideFormatString is true
							if (gc._zoom.axes[ax] !== undefined) {
								axes[ax]._autoFormatString = gc._zoom.axes[ax].tickFormatString;
							}
						}

						gplot.redraw();
						//if(gplot.plugins.highlighter) gplot.plugins.highlighter.moveToFrontMultiTooltip(gplot);		// customizing (2012-04-23, Roy Choi)

						gc.resetzooming = false;
						gc = axes = null;
						gplot.plugins.cursor.zoomHistory = [];
						gplot.plugins.cursor._zoom.isZoomed = false;
						gplot.target.trigger('jqplotResetZoom', [gplot, gplot.plugins.cursor]);
					});
					gplots = null;
					return;
				} else if (!c.zoomProxy) {
					if (this.plugins.cursor._zoom.isZoomed) {
						var axes = this.axes;
						c.resetzooming = true;
						for (var ax in axes) {
							axes[ax].reset();
							axes[ax]._ticks = [];
							// fake out tick creation algorithm to make sure original auto
							// computed format string is used if _overrideFormatString is true
							if (c._zoom.axes[ax] !== undefined) {
								axes[ax]._autoFormatString = c._zoom.axes[ax].tickFormatString;
							}
						}

						this.replot();
						//if(this.plugins.highlighter) this.plugins.highlighter.moveToFrontMultiTooltip(this);		// customizing (2012-04-23, Roy Choi)
						axes = null;
					} else {
						var ctx = this.plugins.cursor.zoomCanvas._ctx;
						ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
						ctx = null;
					}
				} else {
					c.resetzooming = true;
					var ctx = this.plugins.cursor.zoomCanvas._ctx;
					ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
					ctx = null;
				}
				c.resetzooming = false;
				this.plugins.cursor.zoomHistory = [];
				this.plugins.cursor._zoom.isZoomed = false;
				this.target.trigger('jqplotResetZoom', [this, this.plugins.cursor]);
			};

			this.undoZoom = function() {
				if (this.plugins.cursor.zoomHistory.length === 0) {
					var ctx = this.plugins.cursor.zoomCanvas._ctx;
					ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
					ctx = null;
					return;
				} else if (this.plugins.cursor.zoomHistory.length === 1) {
					this.resetZoom();
				} else if (this.plugins.groupplot && this.plugins.groupplot.ischild && this.plugins.groupplot.parent.plugins.groupplot.groupZoom) {// customizing (2012-05-10, Roy Choi)
					var gplots = this.plugins.groupplot.parent.plugins.groupplot.plots;
					$.each(gplots, function(gid, gplot) {
						var _zoomH = gplot.plugins.cursor.zoomHistory.pop();
						if (!gplot.plugins.cursor.zoomProxy) {
							var axes = gplot.axes;
							var caxes = gplot.plugins.cursor._zoom.axes;
							var zoomaxes = _zoomH.zoomaxes;
							$.each(_zoomH.axes, function(ax, val) {
								axes[ax].min = val.min;
								axes[ax].max = val.max;
								axes[ax].tickInterval = val.tickInterval;
								axes[ax].daTickInterval = val.daTickInterval;
								axes[ax].ticks = val.ticks;
							});
						}

						gplot.redraw();
						gplot.target.trigger('jqplotUndoZoom', [this, this.plugins.cursor]);
					});
					gplots = null;
					return;
				} else {
					var _zoomH = this.plugins.cursor.zoomHistory.pop();
					if (_zoomH && !this.plugins.cursor.zoomProxy) {
						var axes = this.axes;
						var caxes = this.plugins.cursor._zoom.axes;
						var zoomaxes = _zoomH.zoomaxes;
						$.each(_zoomH.axes, function(ax, val) {
							axes[ax].min = val.min;
							axes[ax].max = val.max;
							axes[ax].tickInterval = val.tickInterval;
							axes[ax].daTickInterval = val.daTickInterval;
							axes[ax].ticks = val.ticks;
						});
						this.redraw();
					}
				}
				this.target.trigger('jqplotUndoZoom', [this, this.plugins.cursor]);
				//if(this.plugins.highlighter) this.plugins.highlighter.moveToFrontMultiTooltip(this); // customizing (2012-04-23, Roy Choi)
			};

			if (c.showTooltipDataPosition) {
				c.showTooltipUnitPosition = false;
				c.showTooltipGridPosition = false;
				if (options.cursor.tooltipFormatString == undefined) {
					c.tooltipFormatString = $.jqplot.Cursor.cursorLegendFormatString;
				}
			}
		}
	};

	// called with context of plot
	$.jqplot.Cursor.postDraw = function() {
		var c = this.plugins.cursor;
		
		if (!c.show) {
		  return;
		}

		// Memory Leaks patch
		if (c.zoomCanvas) {
			c.zoomCanvas.resetCanvas();
			c.zoomCanvas = null;
		}

		if (c.cursorCanvas) {
			c.cursorCanvas.resetCanvas();
			c.cursorCanvas = null;
		}

		if (c._tooltipElem) {
			c._tooltipElem.emptyForce();
			c._tooltipElem = null;
		}

		if (c.zoom) {
			c.zoomCanvas = new $.jqplot.GenericCanvas();
			this.eventCanvas._elem.before(c.zoomCanvas.createElement(this._gridPadding, 'jqplot-zoom-canvas', this._plotDimensions, this));
			c.zoomCanvas.setContext();
		}

		var elem = document.createElement('div');
		c._tooltipElem = $(elem);
		elem = null;
		c._tooltipElem.addClass('jqplot-cursor-tooltip');
		c._tooltipElem.css({
			position : 'absolute',
			display : 'none'
		});

		if (c.zoomCanvas) {
			c.zoomCanvas._elem.before(c._tooltipElem);
		} else {
			this.eventCanvas._elem.before(c._tooltipElem);
		}

		if (c.showVerticalLine || c.showHorizontalLine) {
			c.cursorCanvas = new $.jqplot.GenericCanvas();
			this.eventCanvas._elem.before(c.cursorCanvas.createElement(this._gridPadding, 'jqplot-cursor-canvas', this._plotDimensions, this));
			c.cursorCanvas.setContext();
		}

		// if we are showing the positions in unit coordinates, and no axes groups
		// were specified, create a default set.
		if (c.showTooltipUnitPosition) {
			if (c.tooltipAxisGroups.length === 0) {
				var series = this.series;
				var s;
				var temp = [];
				for (var i = 0; i < series.length; i++) {
					s = series[i];
					var ax = s.xaxis + ',' + s.yaxis;
					if ($.inArray(ax, temp) == -1) {
						temp.push(ax);
					}
				}
				for (var i = 0; i < temp.length; i++) {
					c.tooltipAxisGroups.push(temp[i].split(','));
				}
			}
		}

		if (c._zoom.isZoomed && c.zoomProxy) {
			c.resetZoom(this, c);
		}
	};

	// Group: methods
	//
	// method: $.jqplot.Cursor.zoomProxy
	// links targetPlot to controllerPlot so that plot zooming of
	// targetPlot will be controlled by zooming on the controllerPlot.
	// controllerPlot will not actually zoom, but acts as an
	// overview plot.  Note, the zoom options must be set to true for
	// zoomProxy to work.
	$.jqplot.Cursor.zoomProxy = function(targetPlot, controllerPlot, drawOnZoomBoxMove) {
		var tc = targetPlot.plugins.cursor;
		var cc = controllerPlot.plugins.cursor;
		tc.zoomTarget = true;
		tc.zoom = true;
		tc.style = 'auto';
		tc.dblClickReset = false;
		cc.zoom = true;
		cc.zoomProxy = true;

		if (controllerPlot.legend.show) {
			controllerPlot.legend._elem.css({
				visibility : 'hidden'
			});
		}
		tc = cc = null;

		var targetPlotId = targetPlot.target.attr('id');
		var controlPlotId = controllerPlot.target.attr('id');

		controllerPlot.target.bind('jqplotZoom', plotZoom);
		controllerPlot.target.bind('jqplotResetZoom', plotReset);
		controllerPlot.target.bind('jqplotUndoZoom', plotUndoZoom);
		// customizing (2012-04-13, Roy Choi)

		function plotZoom(ev, gridpos, datapos, plot, cursor) {
			var targetPlot = $.jqplot.plotList[targetPlotId];
			// customizing (2013-02-19, Roy Choi)
			var tc = targetPlot.plugins.cursor;
			tc.zoomTarget = true;
			tc.zoom = true;
			tc.style = 'auto';

			targetPlot.plugins.cursor.doZoom(gridpos, datapos, targetPlot, cursor);
			// customizing (2013-02-19, Roy Choi)

			targetPlot = tc = null;
		}

		function plotReset(ev, plot, cursor) {
			var targetPlot = $.jqplot.plotList[targetPlotId];
			// customizing (2013-02-19, Roy Choi)
			var controlPlot = $.jqplot.plotList[controlPlotId];
			var tc = targetPlot.plugins.cursor;
			tc.zoomTarget = true;
			tc.zoom = true;
			tc.style = 'auto';
			tc._zoom.isZoomed = true;
			var axes = targetPlot.axes;
			for (var ax in axes) {
				axes[ax].max = controlPlot.axes[ax].max;
				axes[ax].min = controlPlot.axes[ax].min;
				axes[ax].ticks = [];
				axes[ax]._ticks = [];
			}
			//targetPlot.resetZoom();
			//console.log(controlPlot, [new Date(controlPlot.axes.xaxis.min), new Date(controlPlot.axes.xaxis.max)]);
			targetPlot.plugins.cursor.resetZoom(targetPlot, targetPlot.plugins.cursor, controlPlot.axes);

			targetPlot = controlPlot = tc = axes = caxes = null;
		}

		function plotUndoZoom(ev, plot, cursor) {// customizing (2012-04-13, Roy Choi)
			var targetPlot = $.jqplot.plotList[targetPlotId];
			// customizing (2013-02-19, Roy Choi)
			var tc = targetPlot.plugins.cursor;
			tc.zoomTarget = true;
			tc.zoom = true;
			tc.style = 'auto';
			tc._zoom.isZoomed = true;
			tc.undoZoom();

			targetPlot = tc = null;
		}

		targetPlot = controllerPlot = null;
	};

	$.jqplot.Cursor.prototype.resetZoom = function(plot, cursor, controllerAxes) {
		var axes = plot.axes;

		var cax = cursor._zoom.axes;
		if (!plot.plugins.cursor.zoomProxy && cursor._zoom.isZoomed) {
			for (var ax in axes) {
				// axes[ax]._ticks = [];
				// axes[ax].min = cax[ax].min;
				// axes[ax].max = cax[ax].max;
				// axes[ax].numberTicks = cax[ax].numberTicks;
				// axes[ax].tickInterval = cax[ax].tickInterval;
				// // for date axes
				// axes[ax].daTickInterval = cax[ax].daTickInterval;

				axes[ax].reset();
				// customizing (2013-03-11, Roy Choi)
				if (plot.plugins.cursor.zoomTarget) {
					if (controllerAxes[ax].tickInset) {
						axes[ax].min = controllerAxes[ax].noInsetMin;
						axes[ax].max = controllerAxes[ax].noInsetMax;
					} else {
						axes[ax].min = controllerAxes[ax].min;
						axes[ax].max = controllerAxes[ax].max;
					}
				}
				axes[ax]._ticks = [];
				// fake out tick creation algorithm to make sure original auto
				// computed format string is used if _overrideFormatString is true
				if (cax[ax] !== undefined) {
					axes[ax]._autoFormatString = cax[ax].tickFormatString;
				}
			}
			// customizing (2012-04-23, Roy Choi)
			plot.replot();
			//cursor._zoom.isZoomed = false;
		} else {
			var ctx = cursor.zoomCanvas._ctx;
			cursor.prevZoomInfo = {
				start : [],
				end : [],
				started : false,
				zooming : false,
				isZoomed : false,
				axes : {
					start : {},
					end : {}
				},
				gridpos : {},
				datapos : {}
			};
			ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
			ctx = null;
		}
		cursor._zoom.isZoomed = false;
		plot.plugins.cursor.zoomHistory = [];
		plot.target.trigger('jqplotResetZoom', [plot, cursor]);
	};
	// cusomizing for zoom proxy (2013-03-04, Roy Choi) start
	$.jqplot.Cursor.prototype.resetZoomProxy = function(plot, cursor) {
		var ctx = cursor.zoomCanvas._ctx;
		ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		ctx = null;
		cursor._zoom.isZoomed = false;
		plot.plugins.cursor.zoomHistory = [];
	};
	// cusomizing for zoom proxy (2013-03-04, Roy Choi) end
	$.jqplot.Cursor.resetZoom = function(plot) {
		plot.resetZoom();
	};

	// customizing Start (2012-04-12, Roy Choi)
	$.jqplot.Cursor.prototype.undoZoom = function(plot, cursor) {
		plot.undoZoom();
	};
	$.jqplot.Cursor.undoZoom = function(plot) {
		plot.undoZoom();
	};
	// customizing End (2012-04-12, Roy Choi)

	$.jqplot.Cursor.prototype.setSnapshot = function(plot) {
		var c = plot.plugins.cursor;
		c.style = 'crosshair';
		c.zoom = true;
		c.constrainZoomTo = 'none';
		c.showVerticalLine = true;
		c.showHorizontalLine = true;
		c.snapshotOn = true;
		//$.jqplot.eventListenerHooks.push(['jqplotMouseMove', handleMouseMove]);
		plot.redraw();
		//plot.eventCanvas._elem.bind('jqplotMouseMove', {plot:plot}, handleMouseMove);
		c = null;
	};

	$.jqplot.Cursor.prototype.unsetSnapshot = function(plot) {
		var c = plot.plugins.cursor;
		c.showVerticalLine = false;
		c.showHorizontalLine = false;
		c.snapshotOn = false;
		c.style = 'auto';
		//plot.redraw();
		//var idx = $.inArray(['jqplotMouseMove', handleMouseMove],$.jqplot.eventListenerHooks);
		//if(idx >= 0) delete $.jqplot.eventListenerHooks[idx];
		//plot.eventCanvas._elem.unbind('jqplotMouseMove', handleMouseMove);
		plot.redraw();
		c = null;
	};

	// customizing Start (2012-04-12, Roy Choi)
	$.jqplot.Cursor.prototype.doZoom = function(gridpos, datapos, plot, cursor) {
		var c = cursor.snapshotOn ? $.extend(false, {}, cursor) : cursor;
		var axes = cursor.snapshotOn ? $.extend(false, {}, plot.axes) : plot.axes;
		// customizing (2012-04-09, Roy Choi)
		var zaxes = c._zoom.axes;
		var start = zaxes.start;
		var end = zaxes.end;
		var min, max, dp, span;
		var ctx = plot.plugins.cursor.zoomCanvas._ctx;
		var min_x, min_y, max_x, max_y, snapAxes = {};
		// customizing (2012-04-10, Roy Choi)
		var axisLimit = 5000000000;
		// don't zoom if zoom area is too small (in pixels)

		if (c.zoomProxy || (c.constrainZoomTo == 'none' && Math.abs(gridpos.x - c._zoom.start[0]) > 6 && Math.abs(gridpos.y - c._zoom.start[1]) > 6) || (c.constrainZoomTo == 'x' && Math.abs(gridpos.x - c._zoom.start[0]) > 6) || (c.constrainZoomTo == 'y' && Math.abs(gridpos.y - c._zoom.start[1]) > 6)) {
			if (!plot.plugins.cursor.zoomProxy) {
				if (!plot.plugins.cursor.zoomTarget) {
					var historyaxes = {};
					var zoomaxes = {};
					$.each(axes, function(ax, val) {
						if (historyaxes[ax] == undefined)
							historyaxes[ax] = {};
						historyaxes[ax].min = val.min;
						historyaxes[ax].max = val.max;
						historyaxes[ax].tickInterval = null;
						historyaxes[ax].daTickInterval = null;
						historyaxes[ax].ticks = val.ticks;
					});
					c.zoomHistory.push({
						axes : $.extend(true, {}, historyaxes),
						zoomaxes : $.extend(true, {}, zoomaxes)
					});
					zoomaxes = null;
				}
				for (var ax in datapos) {
					if (!cursor.snapshotOn) {
						// make a copy of the original axes to revert back.
						if (c._zoom.axes[ax] == undefined) {
							c._zoom.axes[ax] = {};
							c._zoom.axes[ax].numberTicks = axes[ax].numberTicks;
							c._zoom.axes[ax].tickInterval = axes[ax].tickInterval;
							// for date axes...
							c._zoom.axes[ax].daTickInterval = axes[ax].daTickInterval;
							c._zoom.axes[ax].min = axes[ax].min;
							c._zoom.axes[ax].max = axes[ax].max;
							c._zoom.axes[ax].tickFormatString = (axes[ax].tickOptions != null) ? axes[ax].tickOptions.formatString : '';
						}

						if ((c.constrainZoomTo == 'none') || (c.constrainZoomTo == 'x' && ax.charAt(0) == 'x') || (c.constrainZoomTo == 'y' && ax.charAt(0) == 'y')) {
							dp = datapos[ax];
							if (dp != null) {
								var newmin, newmax;
								if (dp > start[ax]) {
									newmin = start[ax];
									newmax = dp;
								} else {
									span = start[ax] - dp;
									newmin = dp;
									newmax = start[ax];
								}

								var curax = axes[ax];

								var _numberTicks = null;

								// if aligning this axis, use number of ticks from previous axis.
								// Do I need to reset somehow if alignTicks is changed and then graph is replotted??
								if (curax.alignTicks) {
									if (curax.name === 'x2axis' && plot.axes.xaxis.show) {
										_numberTicks = plot.axes.xaxis.numberTicks;
									} else if (curax.name.charAt(0) === 'y' && curax.name !== 'yaxis' && curax.name !== 'yMidAxis' && plot.axes.yaxis.show) {
										_numberTicks = plot.axes.yaxis.numberTicks;
									}
								}

								if (this.looseZoom && (axes[ax].renderer.constructor === $.jqplot.LinearAxisRenderer || axes[ax].renderer.constructor === $.jqplot.DateAxisRenderer)) {
									var ret = $.jqplot.LinearTickGenerator(newmin, newmax, curax._scalefact, _numberTicks);

									// if new minimum is less than "true" minimum of axis display, adjust it
									if (axes[ax].tickInset && ret[0] < axes[ax].min + axes[ax].tickInset * axes[ax].tickInterval) {
										ret[0] += ret[4];
										ret[2] -= 1;
									}

									// if new maximum is greater than "true" max of axis display, adjust it
									if (axes[ax].tickInset && ret[1] > axes[ax].max - axes[ax].tickInset * axes[ax].tickInterval) {
										ret[1] -= ret[4];
										ret[2] -= 1;
									}
									axes[ax].min = ret[0];
									axes[ax].max = ret[1];
									axes[ax]._autoFormatString = ret[3];
									axes[ax].numberTicks = ret[2];
									axes[ax].tickInterval = ret[4];
									// for date axes...
									axes[ax].daTickInterval = [ret[4] / 1000, 'seconds'];
								} else {
									axes[ax].min = newmin;
									axes[ax].max = newmax;
									axes[ax].tickInterval = null;
									// for date axes...
									axes[ax].daTickInterval = null;
								}

								axes[ax]._ticks = [];

								// customizing for zoomproxy on the CategoryAxisRenderer (2013-02-19, Roy Choi)
								if (plot.plugins.cursor.zoomTarget === true && axes[ax].renderer.constructor === $.jqplot.CategoryAxisRenderer) {
									axes[ax].ticks = [];
									axes[ax].numberTicks = null;
								}

								if ((Math.abs(axes[ax].series_u2p(axes[ax]._min)) > axisLimit || Math.abs(axes[ax].series_u2p(axes[ax].max)) > axisLimit)) {
									if (c.zoomHistory.length) {
										var historyAxes = c.zoomHistory.pop().axes;
										$.each(historyAxes, function(ax, axis) {
											axes[ax].min = axis.min;
											axes[ax].max = axis.max;
											axes[ax].tickInterval = axis.tickInterval;
											axes[ax].daTickInterval = axis.daTickInterval;
											axes[ax].ticks = axis.ticks;
										});
									}
									ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
									return;
								}
							}
						}

					} else {
						if (snapAxes[ax] == undefined)
							snapAxes[ax] = $.extend(true, {}, axes[ax]);
						if ((c.constrainZoomTo == 'none') || (c.constrainZoomTo == 'x' && ax.charAt(0) == 'x') || (c.constrainZoomTo == 'y' && ax.charAt(0) == 'y')) {
							dp = datapos[ax];
							if (dp != null) {
								var newmin, newmax;
								if (dp > start[ax]) {
									newmin = start[ax];
									newmax = dp;
								} else {
									span = start[ax] - dp;
									newmin = dp;
									newmax = start[ax];
								}

								var curax = snapAxes[ax];

								var _numberTicks = null;

								// if aligning this axis, use number of ticks from previous axis.
								// Do I need to reset somehow if alignTicks is changed and then graph is replotted??
								if (curax.alignTicks) {
									if (curax.name === 'x2axis' && plot.axes.xaxis.show) {
										_numberTicks = plot.axes.xaxis.numberTicks;
									} else if (curax.name.charAt(0) === 'y' && curax.name !== 'yaxis' && curax.name !== 'yMidAxis' && plot.axes.yaxis.show) {
										_numberTicks = plot.axes.yaxis.numberTicks;
									}
								}

								if (this.looseZoom && (snapAxes[ax].renderer.constructor === $.jqplot.LinearAxisRenderer || snapAxes[ax].renderer.constructor === $.jqplot.DateAxisRenderer)) {
									var ret = $.jqplot.LinearTickGenerator(newmin, newmax, curax._scalefact, _numberTicks);

									// if new minimum is less than "true" minimum of axis display, adjust it
									if (snapAxes[ax].tickInset && ret[0] < snapAxes[ax].min + snapAxes[ax].tickInset * snapAxes[ax].tickInterval) {
										ret[0] += ret[4];
										ret[2] -= 1;
									}

									// if new maximum is greater than "true" max of axis display, adjust it
									if (snapAxes[ax].tickInset && ret[1] > snapAxes[ax].max - snapAxes[ax].tickInset * snapAxes[ax].tickInterval) {
										ret[1] -= ret[4];
										ret[2] -= 1;
									}
									if (curax.name.charAt(0) === 'x') {// customizing (2012-04-10, Roy Choi)
										min_x = ret[0];
										max_x = ret[1];
									} else {
										min_y = ret[0];
										max_y = ret[1];
									}
								} else {
									if (curax.name.charAt(0) === 'x') {// customizing (2012-04-10, Roy Choi)
										min_x = newmin;
										max_x = newmax;
									} else {
										min_y = newmin;
										max_y = newmax;
									}
								}
							}

							snapAxes[ax]._ticks = [];
						}
					}

					// if ((c.constrainZoomTo == 'x' && ax.charAt(0) == 'y' && c.autoscaleConstraint) || (c.constrainZoomTo == 'y' && ax.charAt(0) == 'x' && c.autoscaleConstraint)) {
					//     dp = datapos[ax];
					//     if (dp != null) {
					//         axes[ax].max == null;
					//         axes[ax].min = null;
					//     }
					// }
				}

				ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
				if (c.snapshotOn) {
					var snapshotData = {
						data : [],
						series : []
					};
					var series = plot.series;
					var pushed_count = 0;

					for (var i = 0, len = series.length; i < len; i++) {
						var snapdata = [];
						for (var j = 0, slen = series[i].data.length; j < slen; j++) {
							if (series[i].data[j][0] >= min_x && series[i].data[j][0] <= max_x && series[i].data[j][1] >= min_y && series[i].data[j][1] <= max_y) {
								snapdata.push(series[i].data[j]);
								pushed_count++;
							}
						}
						if (snapdata.length > 0) {
							snapshotData.data.push(snapdata);
							snapshotData.series.push({
								color : series[i].color,
								label : series[i].label
							});
						}
					}

					if (pushed_count > 0) {
						if (cursor.snapshotWin && cursor.snapshotWin.closed === false) {
							cursor.snapshotWin.focus();
							cursor.snapshotWin.addSnapShot.call(this, plot, snapshotData);
						} else {
							//if(cursor.snapshotWin) cursor.snapshotWin.unload();
							$(plot.targetId).unbind('getsnapshotdata');
							$(plot.targetId).unbind('closesnapshotwin');
							plot.target.bind('getsnapshotdata', {
								plot : plot,
								snapshotData : snapshotData
							}, function(ev) {
								cursor.snapshotWin.addSnapShotFirst.call(this, ev.data.plot, ev.data.snapshotData);
							});
							//cursor.snapshotWin.location.href = '';
							plot.target.bind('closesnapshotwin', {
								cursor : cursor
							}, function(ev) {
								ev.data.cursor.snapshotWin.snapshot_list = null;
								delete ev.data.cursor.snapshotWin;
							});
							cursor.snapshotWin = window.open(c.localpath + 'snapshot.html?targetId=' + escape(plot.targetId) + '&' + ((new Date()).getTime() / 1000), 'JqplotSnapShotWin', 'top=10,left=10,width=800,height=' + (window.screen.height - 150) + ',location=no,menubar=no,status=no,resizable=yes,scrollbars=no');
						}
					}

					snapshotData = null;
					pushed_count = null;
					snapAxes = null;
					//c._zoom.isZoomed = false;

				} else {
					if (plot.plugins.groupplot && plot.plugins.groupplot.ischild && plot.plugins.groupplot.parent.plugins.groupplot.groupZoom) {
						var gplots = plot.plugins.groupplot.parent.plugins.groupplot.plots;
						var zHistory = c._zoom.isZoomed ? c.zoomHistory[c.zoomHistory.length - 1] : null;
						var groupid = plot.plugins.groupplot.groupid;
						$.each(gplots, function(gid, gplot) {
							var gc = gplot.plugins.cursor;
							if (zHistory && gplot !== plot) {
								gc.zoomHistory.push(zHistory);
							}
							for (var ax in datapos) {
								gplot.axes[ax].min = axes[ax].min;
								gplot.axes[ax].max = axes[ax].max;
								gplot.axes[ax].tickInterval = null;
								gplot.axes[ax].daTickInterval = null;
								//gplot.axes[ax].ticks = axes[ax].ticks;
							}
							gplot.redraw();
							gc._zoom.isZoomed = true;
							if (!gc.snapshotOn && gid != groupid)
								gplot.target.trigger('jqplotZoom');
							//if(gplot.plugins.highlighter) gplot.plugins.highlighter.moveToFrontMultiTooltip(gplot);
						});
						gplots = zHistory = null;
					} else {
						plot.redraw();
						c._zoom.isZoomed = true;
						//if(plot.plugins.highlighter) plot.plugins.highlighter.moveToFrontMultiTooltip(plot);		// customizing (2012-04-23, Roy Choi)
					}

					if (!c.zoomboxMoving && !c.zoomboxDraging) {
						c.prevZoomInfo = $.extend(true, {}, c._zoom);
					}
				}
			}
			if (!c.snapshotOn)
				plot.target.trigger('jqplotZoom', [gridpos, datapos, plot, cursor]);
		} else {
			ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		}

		c = axes = zaxes = start = end = min = max = dp = span = ctx = null;
		min_x = min_y = max_x = max_y = snapAxes = null;
	};
	// customizing End (2012-04-12, Roy Choi)

	// customizing Start (2012-04-24, Roy Choi)
	$.jqplot.Cursor.prototype.doDrag = function(x_move_pos, y_move_pos, datapos, plot, cursor) {
		var c = cursor;
		var axes = plot.axes;
		// customizing (2012-04-09, Roy Choi)
		var daxes = c._drag.axes;
		var start = daxes.start;
		var min, max, dp, span;
		var startAxes = c.dragStartAxes;

		for (var ax in datapos) {
			dp = datapos[ax];
			if (dp != null) {
				var newmin, newmax;
				var oldmin, oldmax;

				var curax = startAxes[ax];

				oldmin = curax.min;
				oldmax = curax.max;

				if (curax.name.charAt(0) == 'x') {
					newmin = curax.p2u(curax.u2p(oldmin) - x_move_pos);
					newmax = curax.p2u(curax.u2p(oldmax) - x_move_pos);
				} else if (curax.name.charAt(0) == 'y') {
					newmin = curax.p2u(curax.u2p(oldmin) - y_move_pos);
					newmax = curax.p2u(curax.u2p(oldmax) - y_move_pos);
				}

				var _numberTicks = null;

				if (curax.alignTicks) {
					if (curax.name === 'x2axis' && plot.axes.xaxis.show) {
						_numberTicks = plot.axes.xaxis.numberTicks;
					} else if (curax.name.charAt(0) === 'y' && curax.name !== 'yaxis' && curax.name !== 'yMidAxis' && plot.axes.yaxis.show) {
						_numberTicks = plot.axes.yaxis.numberTicks;
					}
				}

				if (this.looseZoom && (axes[ax].renderer.constructor === $.jqplot.LinearAxisRenderer || axes[ax].renderer.constructor === $.jqplot.DateAxisRenderer)) {
					var ret = $.jqplot.LinearTickGenerator(newmin, newmax, curax._scalefact, _numberTicks);

					// if new minimum is less than "true" minimum of axis display, adjust it
					/*if (axes[ax].tickInset && ret[0] < axes[ax].min + axes[ax].tickInset * axes[ax].tickInterval) {
					 ret[0] += ret[4];
					 ret[2] -= 1;
					 }

					 // if new maximum is greater than "true" max of axis display, adjust it
					 if (axes[ax].tickInset && ret[1] > axes[ax].max - axes[ax].tickInset * axes[ax].tickInterval) {
					 ret[1] -= ret[4];
					 ret[2] -= 1;
					 }*/
					axes[ax].min = ret[0];
					axes[ax].max = ret[1];
					axes[ax]._autoFormatString = ret[3];
					axes[ax].numberTicks = ret[2];
					axes[ax].tickInterval = ret[4];
					// for date axes...
					axes[ax].daTickInterval = [ret[4] / 1000, 'seconds'];
				} else {
					axes[ax].min = newmin;
					axes[ax].max = newmax;
					axes[ax].tickInterval = null;
					// for date axes...
					axes[ax].daTickInterval = null;
				}

				axes[ax]._ticks = [];
			}
		}

		plot.redraw();
	};
	// customizing End (2012-04-24, Roy Choi)

	// customizing Start (2012-05-13, Roy Choi) - Series Shift
	$.jqplot.Cursor.prototype.doShift = function(x_move_pos, y_move_pos, datapos, plot, cursor) {
		var c = cursor;
		var ctx = plot.seriesHighlightCanvas._ctx;
		ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		if (c.seriesShift && c.seriesShiftIndex != null && c._shift.started) {
			plot.seriesCanvas._elem.fadeTo(0, 0.2);
			var s = plot.series[c.seriesShiftIndex];
			var orgData = c._shift.data;
			for (var i = 0, sdlen = s.data.length; i < sdlen; i++) {
				var tmp = orgData[i];
				s.data[i][0] = tmp[0] + x_move_pos;
				s.data[i][1] = tmp[1] + y_move_pos;
				tmp = null;
				//console.log(s.data[i][1], y_move_pos);
			}

			s.draw(ctx, {}, plot);
			orgData = null;
		}
		ctx = null;
	};

	$.jqplot.Cursor.prototype.seriesSameStartPoint = function(plot) {
		var c = plot.plugins.cursor;
		var series = plot.series;
		var startPoint = series[0].data[0];
		var ctx = plot.seriesCanvas._ctx;
		ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		for (var i = 1, slen = series.length; i < slen; i++) {
			c.shiftedSeries.push(i);
			var sd = series[i].data;
			var x_move = startPoint[0] - sd[0][0];
			var y_move = startPoint[1] - sd[0][1];
			$.each(sd, function(ind, point) {
				point[0] += x_move;
				point[1] += y_move;
			});
			series[i].draw();
		}
		c.seriesShift = false;
		ctx = null;

	};

	$.jqplot.Cursor.prototype.setSeriesShift = function(shiftStyle, plot) {
		var c = plot.plugins.cursor;
		c.seriesShiftStyle = shiftStyle;
		if (c.seriesShiftOrgData == null)
			c.seriesShiftOrgData = $.extend(true, [], plot.data);

		if (shiftStyle == 'samestart') {
			c.seriesSameStartPoint(plot);
		} else {
			c.seriesShift = true;
		}

	};

	$.jqplot.Cursor.prototype.clearShift = function(plot) {
		var c = plot.plugins.cursor;

		var orgData = c.seriesShiftOrgData;
		var series = plot.series;
		/*for(var i=0, slen=series.length; i<slen; i++ ) {
		 series[i].data = $.extend(true,[],orgData[i]);
		 }*/
		$.each(c.shiftedSeries, function(ind, i) {
			series[i].data = $.extend(true, [], orgData[i]);
		});
		c._shift = {
			start : [],
			end : [],
			started : false,
			shifting : false,
			isShifted : false,
			data : []
		};
		c.seriesShiftOrgData = null;
		c.shiftedSeries = [];
		c.seriesShiftStyle = 'horizontal';
		c.seriesShiftIndex = null;
		c.seriesShift = false;
		plot.redraw();
		orgData = null;
		series = null;
		c = null;
	};
	// customizing End (2012-04-24, Roy Choi)

	$.jqplot.preInitHooks.push($.jqplot.Cursor.init);
	$.jqplot.postDrawHooks.push($.jqplot.Cursor.postDraw);

	function updateTooltip(gridpos, datapos, plot) {
		var c = plot.plugins.cursor;
		var s = '';
		var addbr = false;
		if (c.showTooltipGridPosition) {
			s = gridpos.x + ', ' + gridpos.y;
			addbr = true;
		}
		if (c.showTooltipUnitPosition) {
			var g;
			for (var i = 0; i < c.tooltipAxisGroups.length; i++) {
				g = c.tooltipAxisGroups[i];
				if (addbr) {
					s += '<br />';
				}
				if (c.useAxesFormatters) {
					var xf = plot.axes[g[0]]._ticks[0].formatter;
					var yf = plot.axes[g[1]]._ticks[0].formatter;
					var xfstr = plot.axes[g[0]]._ticks[0].formatString;
					var yfstr = plot.axes[g[1]]._ticks[0].formatString;
					s += xf(xfstr, datapos[g[0]]) + ', ' + yf(yfstr, datapos[g[1]]);
				} else {
					s += $.jqplot.sprintf(c.tooltipFormatString, datapos[g[0]], datapos[g[1]]);
				}
				addbr = true;
			}
		}

		if (c.showTooltipDataPosition) {
			var series = plot.series;
			var ret = getIntersectingPoints(plot, gridpos.x, gridpos.y);
			var addbr = false;

			for (var i = 0; i < series.length; i++) {
				if (series[i].show) {
					var idx = series[i].index;
					var label = series[i].label.toString();
					var cellid = $.inArray(idx, ret.indices);
					var sx = undefined;
					var sy = undefined;
					if (cellid != -1) {
						var data = ret.data[cellid].data;
						if (c.useAxesFormatters) {
							var xf = series[i]._xaxis._ticks[0].formatter;
							var yf = series[i]._yaxis._ticks[0].formatter;
							var xfstr = series[i]._xaxis._ticks[0].formatString;
							var yfstr = series[i]._yaxis._ticks[0].formatString;
							sx = xf(xfstr, data[0]);
							sy = yf(yfstr, data[1]);
						} else {
							sx = data[0];
							sy = data[1];
						}
						if (addbr) {
							s += '<br />';
						}
						s += $.jqplot.sprintf(c.tooltipFormatString, label, sx, sy);
						addbr = true;
					}
				}
			}

		}
		c._tooltipElem.html(s);
	}

	function moveLine(gridpos, plot) {
		var c = plot.plugins.cursor;
		var ctx = c.cursorCanvas._ctx;
		ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		if (c.showVerticalLine) {
			c.shapeRenderer.draw(ctx, [[gridpos.x, 0], [gridpos.x, ctx.canvas.height]]);
		}
		if (c.showHorizontalLine) {
			c.shapeRenderer.draw(ctx, [[0, gridpos.y], [ctx.canvas.width, gridpos.y]]);
		}
		var ret = getIntersectingPoints(plot, gridpos.x, gridpos.y);
		if (c.showCursorLegend) {
			var cells = $(plot.targetId + ' td.jqplot-cursor-legend-label');
			for (var i = 0; i < cells.length; i++) {
				var idx = $(cells[i]).data('seriesIndex');
				var series = plot.series[idx];
				var label = series.label.toString();
				var cellid = $.inArray(idx, ret.indices);
				var sx = undefined;
				var sy = undefined;
				if (cellid != -1) {
					var data = ret.data[cellid].data;
					if (c.useAxesFormatters) {
						var xf = series._xaxis._ticks[0].formatter;
						var yf = series._yaxis._ticks[0].formatter;
						var xfstr = series._xaxis._ticks[0].formatString;
						var yfstr = series._yaxis._ticks[0].formatString;
						sx = xf(xfstr, data[0]);
						sy = yf(yfstr, data[1]);
					} else {
						sx = data[0];
						sy = data[1];
					}
				}
				if (plot.legend.escapeHtml) {
					$(cells[i]).text($.jqplot.sprintf(c.cursorLegendFormatString, label, sx, sy));
				} else {
					$(cells[i]).html($.jqplot.sprintf(c.cursorLegendFormatString, label, sx, sy));
				}
			}
		}
		ctx = null;
	}

	function getIntersectingPoints(plot, x, y) {
		var ret = {
			indices : [],
			data : []
		};
		var s, i, d0, d, j, r, p;
		var threshold;
		var c = plot.plugins.cursor;
		for (var i = 0; i < plot.series.length; i++) {
			s = plot.series[i];
			r = s.renderer;
			if (s.show) {
				threshold = c.intersectionThreshold;
				if (s.showMarker) {
					threshold += s.markerRenderer.size / 2;
				}
				for (var j = 0; j < s.gridData.length; j++) {
					p = s.gridData[j];
					// check vertical line
					if (c.showVerticalLine) {
						if (Math.abs(x - p[0]) <= threshold) {
							ret.indices.push(i);
							ret.data.push({
								seriesIndex : i,
								pointIndex : j,
								gridData : p,
								data : s.data[j]
							});
						}
					}
				}
			}
		}
		return ret;
	}

	function moveTooltip(gridpos, plot) {
		var c = plot.plugins.cursor;
		var elem = c._tooltipElem;
		switch (c.tooltipLocation) {
			case 'nw':
				var x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) - c.tooltipOffset;
				var y = gridpos.y + plot._gridPadding.top - c.tooltipOffset - elem.outerHeight(true);
				break;
			case 'n':
				var x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) / 2;
				var y = gridpos.y + plot._gridPadding.top - c.tooltipOffset - elem.outerHeight(true);
				break;
			case 'ne':
				var x = gridpos.x + plot._gridPadding.left + c.tooltipOffset;
				var y = gridpos.y + plot._gridPadding.top - c.tooltipOffset - elem.outerHeight(true);
				break;
			case 'e':
				var x = gridpos.x + plot._gridPadding.left + c.tooltipOffset;
				var y = gridpos.y + plot._gridPadding.top - elem.outerHeight(true) / 2;
				break;
			case 'se':
				var x = gridpos.x + plot._gridPadding.left + c.tooltipOffset;
				var y = gridpos.y + plot._gridPadding.top + c.tooltipOffset;
				break;
			case 's':
				var x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) / 2;
				var y = gridpos.y + plot._gridPadding.top + c.tooltipOffset;
				break;
			case 'sw':
				var x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) - c.tooltipOffset;
				var y = gridpos.y + plot._gridPadding.top + c.tooltipOffset;
				break;
			case 'w':
				var x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) - c.tooltipOffset;
				var y = gridpos.y + plot._gridPadding.top - elem.outerHeight(true) / 2;
				break;
			default:
				var x = gridpos.x + plot._gridPadding.left + c.tooltipOffset;
				var y = gridpos.y + plot._gridPadding.top + c.tooltipOffset;
				break;
		}

		elem.css('left', x);
		elem.css('top', y);
		elem = null;
	}

	function positionTooltip(plot) {
		// fake a grid for positioning
		var grid = plot._gridPadding;
		var c = plot.plugins.cursor;
		var elem = c._tooltipElem;
		switch (c.tooltipLocation) {
			case 'nw':
				var a = grid.left + c.tooltipOffset;
				var b = grid.top + c.tooltipOffset;
				elem.css('left', a);
				elem.css('top', b);
				break;
			case 'n':
				var a = (grid.left + (plot._plotDimensions.width - grid.right)) / 2 - elem.outerWidth(true) / 2;
				var b = grid.top + c.tooltipOffset;
				elem.css('left', a);
				elem.css('top', b);
				break;
			case 'ne':
				var a = grid.right + c.tooltipOffset;
				var b = grid.top + c.tooltipOffset;
				elem.css({
					right : a,
					top : b
				});
				break;
			case 'e':
				var a = grid.right + c.tooltipOffset;
				var b = (grid.top + (plot._plotDimensions.height - grid.bottom)) / 2 - elem.outerHeight(true) / 2;
				elem.css({
					right : a,
					top : b
				});
				break;
			case 'se':
				var a = grid.right + c.tooltipOffset;
				var b = grid.bottom + c.tooltipOffset;
				elem.css({
					right : a,
					bottom : b
				});
				break;
			case 's':
				var a = (grid.left + (plot._plotDimensions.width - grid.right)) / 2 - elem.outerWidth(true) / 2;
				var b = grid.bottom + c.tooltipOffset;
				elem.css({
					left : a,
					bottom : b
				});
				break;
			case 'sw':
				var a = grid.left + c.tooltipOffset;
				var b = grid.bottom + c.tooltipOffset;
				elem.css({
					left : a,
					bottom : b
				});
				break;
			case 'w':
				var a = grid.left + c.tooltipOffset;
				var b = (grid.top + (plot._plotDimensions.height - grid.bottom)) / 2 - elem.outerHeight(true) / 2;
				elem.css({
					left : a,
					top : b
				});
				break;
			default:
				// same as 'se'
				var a = grid.right - c.tooltipOffset;
				var b = grid.bottom + c.tooltipOffset;
				elem.css({
					right : a,
					bottom : b
				});
				break;
		}
		elem = null;
	}

	function handleClick(ev, gridpos, datapos, neighbor, plot) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var c = plot.plugins.cursor;
		if (c.clickReset) {
			c.resetZoom(plot, c);
		}
		var sel = window.getSelection;
		if (document.selection && document.selection.empty) {
			document.selection.empty();
		} else if (sel && !sel().isCollapsed) {
			sel().collapse();
		}
		return false;
	}

	function handleDblClick(ev, gridpos, datapos, neighbor, plot) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var c = plot.plugins.cursor;
		if (c.dblClickReset) {
			c.resetZoom(plot, c);
		}
		var sel = window.getSelection;
		if (document.selection && document.selection.empty) {
			document.selection.empty();
		} else if (sel && !sel().isCollapsed) {
			sel().collapse();
		}
		return false;
	}

	function handleMouseLeave(ev, gridpos, datapos, neighbor, plot) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var c = plot.plugins.cursor;
		c.onGrid = false;
		if (c.show) {
			$(ev.target).css('cursor', c.previousCursor);
			if (c.showTooltip && !(c._zoom.zooming && c.showTooltipOutsideZoom && !c.constrainOutsideZoom)) {
				c._tooltipElem.hide();
			}
			if (c.zoom) {
				c._zoom.gridpos = gridpos;
				c._zoom.datapos = datapos;
			}
			if (c.showVerticalLine || c.showHorizontalLine) {
				var ctx = c.cursorCanvas._ctx;
				ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
				ctx = null;
			}
			if (c.showCursorLegend) {
				var cells = $(plot.targetId + ' td.jqplot-cursor-legend-label');
				for (var i = 0; i < cells.length; i++) {
					var idx = $(cells[i]).data('seriesIndex');
					var series = plot.series[idx];
					var label = series.label.toString();
					if (plot.legend.escapeHtml) {
						$(cells[i]).text($.jqplot.sprintf(c.cursorLegendFormatString, label, undefined, undefined));
					} else {
						$(cells[i]).html($.jqplot.sprintf(c.cursorLegendFormatString, label, undefined, undefined));
					}

				}
			}
		}
	}

	function handleMouseEnter(ev, gridpos, datapos, neighbor, plot) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var c = plot.plugins.cursor;
		c.onGrid = true;
		if (c.show) {
			c.previousCursor = ev.target.style.cursor;
			ev.target.style.cursor = c.style;
			if (c.showTooltip) {
				updateTooltip(gridpos, datapos, plot);
				if (c.followMouse) {
					moveTooltip(gridpos, plot);
				} else {
					positionTooltip(plot);
				}
				c._tooltipElem.show();
			}
			if (c.showVerticalLine || c.showHorizontalLine) {
				moveLine(gridpos, plot);
			}
		}

	}

	function handleMouseMove(ev, gridpos, datapos, neighbor, plot) {
		var c = plot.plugins.cursor;
		if (c.zoomProxy && c._zoom.isZoomed) {// cusomizing for zoom box (2013-02-22, Roy Choi)
			var overType = checkOverZoomBoxLine(ev, gridpos, c);
			if (!c._zoom.zooming && overType !== null) {
				if (c.prevCursorStyle === null) {
					c.prevCursorStyle = ev.target.style.cursor;
					if (c.constrainZoomTo == 'x' || c.constrainZoomTo == '')
						switch(overType) {//s, sv, sh, svh, e, ev, eh, evh
							case 'sv':
							case 'ev':
								ev.target.style.cursor = 'col-resize';
								break;
							case 'sh':
							case 'eh':
								ev.target.style.cursor = 'row-resize';
								break;
							case 'svh':
							case 'evh':
								//TODO
								break;
						}
				}
				/*} else if(!c._zoom.zooming && isInnerZoomBox(ev, gridpos, c)) {
				 if(c.prevCursorStyle === null) {
				 c.prevCursorStyle = ev.target.style.cursor;
				 ev.target.style.cursor = 'move';
				 }*/
			} else if (!c._zoom.zooming) {
				ev.target.style.cursor = c.style;
				c.prevCursorStyle = null;
			}
		} else if (c._zoom.zooming) {
			ev.target.style.cursor = c.style;
		}
		if (c.show) {
			if (c.showTooltip) {
				updateTooltip(gridpos, datapos, plot);
				if (c.followMouse) {
				    ev.preventDefault();
                    ev.stopImmediatePropagation();
					moveTooltip(gridpos, plot);
				}
			}
			if (c.showVerticalLine || c.showHorizontalLine) {
			    ev.preventDefault();
                ev.stopImmediatePropagation();
				moveLine(gridpos, plot);
			}
		}
	}

	function getEventPosition(ev) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var plot = ev.data.plot;
		var go = plot.eventCanvas._elem.offset();
		var gridPos = {
			x : ev.pageX - go.left,
			y : ev.pageY - go.top
		};
		//////
		// TO DO: handle yMidAxis
		//////
		var dataPos = {
			xaxis : null,
			yaxis : null,
			x2axis : null,
			y2axis : null,
			y3axis : null,
			y4axis : null,
			y5axis : null,
			y6axis : null,
			y7axis : null,
			y8axis : null,
			y9axis : null,
			yMidAxis : null
		};
		var an = ['xaxis', 'yaxis', 'x2axis', 'y2axis', 'y3axis', 'y4axis', 'y5axis', 'y6axis', 'y7axis', 'y8axis', 'y9axis', 'yMidAxis'];
		var ax = plot.axes;
		var n, axis;
		for ( n = 11; n > 0; n--) {
			axis = an[n - 1];
			if (ax[axis].show) {
				dataPos[axis] = ax[axis].series_p2u(gridPos[axis.charAt(0)]);
			}
		}

		return {
			offsets : go,
			gridPos : gridPos,
			dataPos : dataPos
		};
	}

	function handleZoomMove(ev) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var plot = ev.data.plot;
		var start = ev.data.start;
		var c = plot.plugins.cursor;
		// don't do anything if not on grid.
		if (c.show && c.zoom && c._zoom.started && !c.zoomTarget) {
			var ctx = c.zoomCanvas._ctx;
			var positions = getEventPosition(ev);
			var gridpos = positions.gridPos;
			if (Math.abs(gridpos.x - start.x) * Math.abs(gridpos.y - start.y) < 10)
				return;
			var datapos = positions.dataPos;
			c._zoom.gridpos = gridpos;
			c._zoom.datapos = datapos;
			c._zoom.zooming = true;
			var xpos = gridpos.x;
			var ypos = gridpos.y;
			var height = ctx.canvas.height;
			var width = ctx.canvas.width;
			if (c.showTooltip && !c.onGrid && c.showTooltipOutsideZoom) {
				updateTooltip(gridpos, datapos, plot);
				if (c.followMouse) {
					moveTooltip(gridpos, plot);
				}
			}
			if (c.constrainZoomTo == 'x') {
				c._zoom.end = [xpos, height];
			} else if (c.constrainZoomTo == 'y') {
				c._zoom.end = [width, ypos];
			} else {
				c._zoom.end = [xpos, ypos];
			}
			var sel = window.getSelection;
			if (document.selection && document.selection.empty) {
				document.selection.empty();
			} else if (sel && !sel().isCollapsed) {
				sel().collapse();
			}
			drawZoomBox.call(c);
			ctx = null;
		}
	}

	function forceMin(val, min) {
		return val < min ? min : val;
	}

	function forceMax(val, max) {
		return val > max ? max : val;
	}

	function forceValue(val, min, max) {
		return forceMax(forceMin(val, min), max);
	}

	function forceCompareValue(val1, val2, add, min, max) {
		if (val1 > val2) {
			if (val1 > max) {
				val1 = forceMax(val1, max);
				val2 = val1 - add;
			} else if (val2 < min) {
				val2 = forceMin(val2, min);
				val1 = val2 + add;
			}
		} else {
			if (val2 > max) {
				val2 = forceMax(val2, max);
				val1 = val2 - add;
			} else if (val1 < min) {
				val1 = forceMin(val1, min);
				val2 = val1 + add;
			}
		}

		return [val1, val2];
	}

	// customizing for move zoom box (2013-02-21, Roy Choi)
	function handleMoveZoomBox(ev) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var plot = ev.data.plot;
		var start = ev.data.start;
		// mouse down point
		var overType = ev.data.overType;
		var c = plot.plugins.cursor;
		c._zoom = $.extend(true, {}, c.prevZoomInfo);
		// don't do anything if not on grid.
		if (c.show && c.zoom && c._zoom && c.zoomProxy) {
			var ctx = c.zoomCanvas._ctx;
			var positions = getEventPosition(ev);
			var gridpos = positions.gridPos;

			var movingX = gridpos.x - start.x;
			var movingY = gridpos.y - start.y;

			var boxWidth = Math.abs(c.prevZoomInfo.end[0] - c.prevZoomInfo.start[0]);
			var boxHeight = Math.abs(c.prevZoomInfo.end[1] - c.prevZoomInfo.start[1]);

			var height = ctx.canvas.height;
			var width = ctx.canvas.width;

			var go = plot.eventCanvas._elem.offset();
			var offsetLeft = go.left;
			var offsetTop = go.top;

			var x, y, xs, xe, ys, ye;

			if ( typeof overType == 'undefined') {
				if (c.constrainZoomTo == 'x') {
					xs = c._zoom.start[0] + movingX;
					xe = c._zoom.end[0] + movingX;

					x = forceCompareValue(xs, xe, boxWidth, 0, width);

					c._zoom.start = [x[0], 0];
					c._zoom.end = [x[1], 0];
				} else if (c.constrainZoomTo == 'y') {
					ys = c._zoom.start[1] + movingY;
					ye = c._zoom.end[1] + movingY;

					y = forceCompareValue(ys, ye, boxHeight, 0, height);

					c._zoom.start = [0, y[0]];
					c._zoom.end = [0, y[1]];
				} else {
					xs = c._zoom.start[0] + movingX;
					xe = c._zoom.end[0] + movingX;

					x = forceCompareValue(xs, xe, boxWidth, 0, width);

					ys = c._zoom.start[1] + movingY;
					ye = c._zoom.end[1] + movingY;

					y = forceCompareValue(ys, ye, boxHeight, 0, height);

					c._zoom.start = [x[0], y[0]];
					c._zoom.end = [x[1], y[1]];
				}
			} else {
				if (c.constrainZoomTo == 'x') {
					//console.log('box width', boxWidth);
					if (overType == 'sv') {
						x = forceValue(c._zoom.start[0] + movingX, 0, width);
						c._zoom.start = [x, 0];
					} else if (overType == 'ev') {
						x = forceValue(c._zoom.end[0] + movingX, 0, width);
						c._zoom.end = [x, 0];
					}
				} else if (c.constrainZoomTo == 'y') {
					if (overType == 'sh') {
						y = forceValue(c._zoom.start[1] + movingY, 0, height);
						c._zoom.start = [0, y];
					} else if (overType == 'eh') {
						y = forceValue(c._zoom.end[1] + movingY, 0, height);
						c._zoom.end = [0, y];
					}
				} else {
					/// TODO:
				}
			}

			x = y = xs = xe = ys = ye = null;

			var startEv = $.extend(true, {}, ev);
			startEv.pageX = c._zoom.start[0] + offsetLeft;
			startEv.pageY = c._zoom.start[1] + offsetTop;

			var startPosition = getEventPosition(startEv);
			var startDatapos = startPosition.dataPos;

			c._zoom.started = true;
			for (var ax in startDatapos) {
				// get zoom starting position.
				c._zoom.axes.start[ax] = startDatapos[ax];
			}
			startEv = startDatapos = startPosition = ax = null;

			var endEv = $.extend(true, {}, ev);
			endEv.pageX = c._zoom.end[0] + offsetLeft;
			endEv.pageY = c._zoom.end[0] + offsetTop;

			var endPosition = getEventPosition(endEv);
			var datapos = endPosition.dataPos;
			gridpos = endPosition.gridPos;

			endEv = null;

			c._zoom.gridpos = gridpos;
			c._zoom.datapos = datapos;
			c._zoom.zooming = true;

			var xpos = gridpos.x;
			var ypos = gridpos.y;

			if (c.constrainZoomTo == 'x') {
				c._zoom.end = [xpos, height];
				ypos = height;
			} else if (c.constrainZoomTo == 'y') {
				c._zoom.end = [width, ypos];
				xpos = width;
			} else {
				c._zoom.end = [xpos, ypos];
			}

			var sel = window.getSelection;
			if (document.selection && document.selection.empty) {
				document.selection.empty();
			} else if (sel && !sel().isCollapsed) {
				sel().collapse();
			}

			if (Math.abs(c._zoom.start[0] - c._zoom.end[0]) * Math.abs(c._zoom.start[1] - c._zoom.end[1]) < 10) {
				return;
			}

			drawZoomBox.call(c);
			ctx = null;

			/* doZoom prepare start */
			var axes = plot.axes;

			c.endGridPoint = [xpos, ypos];
			//customizing (2012-04-13, Roy Choi)

			if (c.constrainOutsideZoom && !c.onGrid) {
				if (xpos < 0) {
					xpos = 0;
				} else if (xpos > width) {
					xpos = width;
				}
				if (ypos < 0) {
					ypos = 0;
				} else if (ypos > height) {
					ypos = height;
				}

				for (var axis in datapos) {
					if (datapos[axis]) {
						if (axis.charAt(0) == 'x') {
							datapos[axis] = axes[axis].series_p2u(xpos);
						} else {
							datapos[axis] = axes[axis].series_p2u(ypos);
						}
					}
				}
			}

			c.endDataPos = datapos;

			if (c.drawOnZoomBoxMove) {
				c.doZoom(c._zoom.gridpos, datapos, plot, c);
			}
		}

	}

	// customizing for move zoom box (2013-02-21, Roy Choi)
	function handleZoomBoxStop(ev) {
		$(document).unbind('mousemove.jqplotCursor', handleMoveZoomBox);
		//$(document).unbind('mousemove.jqplotCursor', handleDragZoomBoxLine);

		var plot = ev.data.plot;
		var c = plot.plugins.cursor;

		if (!c.drawOnZoomBoxMove) {
			c.doZoom(c.endGridPoint, c.endDataPos, plot, c);
		}

		c._zoom.started = false;
		c._zoom.zooming = false;

		c.zoomboxMoving = false;
		c.zoomboxDraging = false;

		ev.target.style.cursor = c.prevCursorStyle;
		c.prevCursorStyle = null;

		c.prevZoomInfo = $.extend(true, {}, c._zoom);

		//if (document.onselectstart != undefined && c._oldHandlers.onselectstart != null){
		document.onselectstart = c._oldHandlers.onselectstart;
		c._oldHandlers.onselectstart = null;
		//}
		if (document.ondrag != undefined && c._oldHandlers.ondrag != null) {
			document.ondrag = c._oldHandlers.ondrag;
			c._oldHandlers.ondrag = null;
		}
		if (document.onmousedown != undefined && c._oldHandlers.onmousedown != null) {
			document.onmousedown = c._oldHandlers.onmousedown;
			c._oldHandlers.onmousedown = null;
		}
		plot = c = null;
	}

	/* customizing start (2012-04-23, Roy Choi) */
	function handleDragMove(ev) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var plot = ev.data.plot;
		var c = plot.plugins.cursor;
		// don't do anything if not on grid.
		if (c.show && c.zoom && c._zoom.isZoomed && c.draggable && c._drag.started && !c.zoomTarget) {
			var positions = getEventPosition(ev);
			var gridpos = positions.gridPos;
			var datapos = positions.dataPos;

			c._drag.dragging = true;
			var xpos = gridpos.x;
			var ypos = gridpos.y;

			if (c.constrainDragTo == 'x') {
				ypos = 0;
			} else if (c.constrainDragTo == 'y') {
				xpos = 0;
			}

			c.endGridPoint = [xpos, ypos];

			c._drag.end = [xpos, ypos];
			c._drag.gridpos = {
				x : xpos,
				y : ypos
			};

			var x_move_pos = c.endGridPoint[0] - c.startGridPoint[0];
			var y_move_pos = c.endGridPoint[1] - c.startGridPoint[1];

			c.doDrag.call(c, x_move_pos, y_move_pos, datapos, plot, c);
		}
	}

	/* customizing end (2012-04-23, Roy Choi) */

	/* customizing start (2012-04-23, Roy Choi) */
	function handleShiftMove(ev) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var plot = ev.data.plot;
		var c = plot.plugins.cursor;
		// don't do anything if not on grid.
		if (c.show && c.seriesShift && c.seriesShiftIndex != null && c._shift.started) {
			var positions = getEventPosition(ev);
			var gridpos = positions.gridPos;
			var datapos = positions.dataPos;

			c._shift.shifting = true;
			var xpos = gridpos.x;
			var ypos = gridpos.y;

			if (c.seriesShiftStyle == 'horizontal') {
				ypos = 0;
			} else if (c.seriesShiftStyle == 'vertical') {
				xpos = 0;
			}

			c.endGridPoint = [xpos, ypos];

			c._shift.end = [xpos, ypos];
			c._shift.gridpos = {
				x : xpos,
				y : ypos
			};

			var p2u_series_index = 0;

			for (var i = 0, slen = plot.series.length; i < slen; i++) {
				if ($.inArray(i, c.shiftedSeries) < 0 && c.seriesShiftIndex) {
					var p2u_series_index = i;
					break;
				}
			}

			var xaxis = plot.axes[plot.series[p2u_series_index].xaxis];
			var yaxis = plot.axes[plot.series[p2u_series_index].yaxis];

			var x_move_pos = xaxis.series_p2u(c.endGridPoint[0]) - xaxis.series_p2u(c.startGridPoint[0]);
			var y_move_pos = yaxis.series_p2u(c.endGridPoint[1]) - yaxis.series_p2u(c.startGridPoint[1]);

			//console.log(x_move_pos, c.endGridPoint[0]-c.startGridPoint[0]);

			//console.log(c,x_move_pos, y_move_pos, datapos, plot, c);

			c.doShift(x_move_pos, y_move_pos, datapos, plot, c);
		}
	}

	/* customizing end (2012-04-23, Roy Choi) */

	/* customizing start (2013-02-22, Roy Choi) */
	function isInnerZoomBox(ev, gridpos, cursor) {
		var c = cursor;
		var prevZoomInfo = c.prevZoomInfo;
		var prevStart = prevZoomInfo.start;
		var prevEnd = prevZoomInfo.end;

		var innerOfPrevZoomBox = false;

		switch(c.constrainZoomTo) {
			case 'none':
				if ((gridpos.x > prevStart[0] + c.zoomboxThreshold && gridpos.x < prevEnd[0] + c.zoomboxThreshold && gridpos.y > prevStart[1] + c.zoomboxThreshold && gridpos.y < prevEnd[1] - c.zoomboxThreshold) || (gridpos.x > prevStart[0] + c.zoomboxThreshold && gridpos.x < prevEnd[0] + c.zoomboxThreshold && gridpos.y < prevStart[1] - c.zoomboxThreshold && gridpos.y > prevEnd[1] + c.zoomboxThreshold) || (gridpos.x < prevStart[0] + c.zoomboxThreshold && gridpos.x > prevEnd[0] + c.zoomboxThreshold && gridpos.y > prevStart[1] + c.zoomboxThreshold && gridpos.y < prevEnd[1] - c.zoomboxThreshold) || (gridpos.x < prevStart[0] + c.zoomboxThreshold && gridpos.x > prevEnd[0] + c.zoomboxThreshold && gridpos.y < prevStart[1] - c.zoomboxThreshold && gridpos.y > prevEnd[1] + c.zoomboxThreshold)) {
					innerOfPrevZoomBox = true;
				}
				break;
			case 'y':
				if (gridpos.y > prevStart[1] + c.zoomboxThreshold && gridpos.y < prevEnd[1] - c.zoomboxThreshold || gridpos.y < prevStart[1] - c.zoomboxThreshold && gridpos.y > prevEnd[1] + c.zoomboxThreshold) {
					innerOfPrevZoomBox = true;
				}
				break;
			case 'x':
			default:
				if (gridpos.x > prevStart[0] + c.zoomboxThreshold && gridpos.x < prevEnd[0] + c.zoomboxThreshold || gridpos.x < prevStart[0] + c.zoomboxThreshold && gridpos.x > prevEnd[0] + c.zoomboxThreshold) {
					innerOfPrevZoomBox = true;
				}
				break;
		}
		c = prevZoomInfo = prevStart = prevEnd = null;

		return innerOfPrevZoomBox;
	}

	function checkOverZoomBoxLine(ev, gridpos, cursor) {
		var c = cursor;
		var prevZoomInfo = c.prevZoomInfo;
		var prevStart = prevZoomInfo.start;
		var prevEnd = prevZoomInfo.end;

		var overType = null;

		switch(c.constrainZoomTo) {
			case 'none':
				//if (false) {
				/// TODO: both zoom box resize
				//}
				break;
			case 'y':
				if (gridpos.y <= prevStart[1] + c.zoomboxThreshold && gridpos.y >= prevStart[1] - c.zoomboxThreshold) {
					overType = 'sh';
				} else if (gridpos.y <= prevEnd[1] + c.zoomboxThreshold && gridpos.y >= prevEnd[1] - c.zoomboxThreshold) {
					overType = 'eh';
				}
				break;
			case 'x':
			default:
				if (gridpos.x <= prevStart[0] + c.zoomboxThreshold && gridpos.x >= prevStart[0] - c.zoomboxThreshold) {
					overType = 'sv';
				} else if (gridpos.x <= prevEnd[0] + c.zoomboxThreshold && gridpos.x >= prevEnd[0] - c.zoomboxThreshold) {
					overType = 'ev';
				}
				break;
		}
		c = prevZoomInfo = prevStart = prevEnd = null;

		return overType;
	}

	/* customizing end (2013-02-22, Roy Choi) */

	function handleMouseDown(ev, gridpos, datapos, neighbor, plot) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var c = plot.plugins.cursor;

		// customizing for move zoombox on the zoomProxy plot (2013-02-21, Roy Choi) start
		if (c.zoomProxy/* && c._zoom.isZoomed*/) {
			//c.prevZoomInfo = $.extend(true, {}, c._zoom);

			var overType = checkOverZoomBoxLine(ev, gridpos, c);

			if (overType !== null) {
				c.zoomboxDraging = true;
				$(document).bind('mousemove.jqplotCursor', {
					plot : plot,
					start : gridpos,
					overType : overType
				}, handleMoveZoomBox);
				$(document).one('mouseup.jqplot_cursor', {
					plot : plot
				}, handleZoomBoxStop);

				c._oldHandlers.onselectstart = document.onselectstart;
				document.onselectstart = function() {
					return false;
				};

				if (document.ondrag != undefined) {
					c._oldHandlers.ondrag = document.ondrag;
					document.ondrag = function() {
						return false;
					};
				}
				if (document.onmousedown != undefined) {
					c._oldHandlers.onmousedown = document.onmousedown;
					document.onmousedown = function() {
						return false;
					};
				}

				return;
			} else if (isInnerZoomBox(ev, gridpos, c)) {
				c.zoomboxMoving = true;
				$(document).bind('mousemove.jqplotCursor', {
					plot : plot,
					start : gridpos
				}, handleMoveZoomBox);
				$(document).one('mouseup.jqplot_cursor', {
					plot : plot
				}, handleZoomBoxStop);

				c._oldHandlers.onselectstart = document.onselectstart;
				document.onselectstart = function() {
					return false;
				};

				if (document.ondrag != undefined) {
					c._oldHandlers.ondrag = document.ondrag;
					document.ondrag = function() {
						return false;
					};
				}
				if (document.onmousedown != undefined) {
					c._oldHandlers.onmousedown = document.onmousedown;
					document.onmousedown = function() {
						return false;
					};
				}

				return;
			}

			$(document).unbind('mousemove.jqplotCursor', handleMoveZoomBox);
		}
		// customizing for move zoombox on the zoomProxy plot (2013-02-21, Roy Choi) end

		$(document).one('mouseup.jqplot_cursor', {
			plot : plot
		}, handleMouseUp);
		//if (document.onselectstart != undefined) {
		c._oldHandlers.onselectstart = document.onselectstart;
		document.onselectstart = function() {
			return false;
		};
		//}
		if (document.ondrag != undefined) {
			c._oldHandlers.ondrag = document.ondrag;
			document.ondrag = function() {
				return false;
			};
		}
		if (document.onmousedown != undefined) {
			c._oldHandlers.onmousedown = document.onmousedown;
			document.onmousedown = function() {
				return false;
			};
		}
		if (c.seriesShift) {//customizing (2012-05-13, Roy Choi)
			if (neighbor == null) {
				neighbor = plot.plugins.highlighter.isLineOver(gridpos, plot);
			}
			if (!c._shift.started && neighbor && c.seriesShiftIndex != neighbor.seriesIndex) {
				if (plot.series.length > 1) {
					if (plot.series.length > c.shiftedSeries.length) {
						if (plot.series.length - c.shiftedSeries.length == 1 && $.inArray(neighbor.seriesIndex, c.shiftedSeries) < 0) {
							alert('You can not shift the last series because it is base data series to compare the other one');
						} else {
							if ($.inArray(neighbor.seriesIndex, c.shiftedSeries) < 0)
								c.shiftedSeries.push(neighbor.seriesIndex);
							c.seriesShiftIndex = neighbor.seriesIndex;
						}
					}
				} else {
					alert('You can not shift the last series because it is base data series to compare the other one');
				}

			} else if (c.seriesShiftIndex != null) {
				c.startGridPoint = [gridpos.x, gridpos.y];
				if (c.seriesShiftStyle == 'vertical') {
					c._shift.start = [gridpos.x, 0];
					c.startGridPoint[0] = 0;
				} else if (c.seriesShiftStyle == 'horizontal') {
					c._shift.start = [0, gridpos.y];
					c.startGridPoint[1] = 0;
				} else {
					c._shift.start = [gridpos.x, gridpos.y];
				}
				c._shift.data = $.extend(true, [], plot.series[c.seriesShiftIndex].data);
				c._shift.started = true;
				$(document).bind('mousemove.jqplotCursor', {
					plot : plot,
					start : gridpos
				}, handleShiftMove);
			}
		} else if (c.zoom) {
			c.startGridPoint = [gridpos.x, gridpos.y];
			//customizing (2012-04-13, Roy Choi)
			if (!c.zoomProxy) {
				var ctx = c.zoomCanvas._ctx;
				ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
				ctx = null;
			}
			if (c.constrainZoomTo == 'x') {
				c._zoom.start = [gridpos.x, 0];
			} else if (c.constrainZoomTo == 'y') {
				c._zoom.start = [0, gridpos.y];
			} else {
				c._zoom.start = [gridpos.x, gridpos.y];
			}
			c._zoom.started = true;
			for (var ax in datapos) {
				// get zoom starting position.
				c._zoom.axes.start[ax] = datapos[ax];
			}
			$(document).bind('mousemove.jqplotCursor', {
				plot : plot,
				start : gridpos
			}, handleZoomMove);
		}
	}

	/* customizing start (2012-04-23, Roy Choi) */
	function handleRightDown(ev, gridpos, datapos, neighbor, plot) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var c = plot.plugins.cursor;
		var startAxes = c.dragStartAxes;
		$(document).one('mouseup.jqplot_cursor', {
			plot : plot
		}, handleRightUp);
		var axes = plot.axes;
		if (document.onselectstart != undefined) {
			c._oldHandlers.onselectstart = document.onselectstart;
			document.onselectstart = function() {
				return false;
			};
		}
		if (document.ondrag != undefined) {
			c._oldHandlers.ondrag = document.ondrag;
			document.ondrag = function() {
				return false;
			};
		}
		if (document.onmousedown != undefined) {
			c._oldHandlers.onmousedown = document.onmousedown;
			document.onmousedown = function() {
				return false;
			};
		}
		if (c.zoom && c._zoom.isZoomed && c.draggable) {
			for (var ax in datapos) {
				startAxes[ax] = $.extend(true, {}, axes[ax]);
			}

			c.startGridPoint = [gridpos.x, gridpos.y];
			//customizing (2012-04-13, Roy Choi)
			if (c.constrainDragTo == 'x') {
				c._drag.start = [gridpos.x, 0];
			} else if (c.constrainDragTo == 'y') {
				c._drag.start = [0, gridpos.y];
			} else {
				c._drag.start = [gridpos.x, gridpos.y];
			}
			c._drag.started = true;
			for (var ax in datapos) {
				// get zoom starting position.
				c._drag.axes.start[ax] = datapos[ax];
			}
			$(document).bind('mousemove.jqplotCursor', {
				plot : plot
			}, handleDragMove);
		}
	}

	/* customizing end (2012-04-23, Roy Choi) */

	function handleMouseUp(ev, neighbor) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var plot = ev.data.plot;
		var c = plot.plugins.cursor;
		if (c.seriesShift && c._shift.shifting) {
			var ctx = plot.seriesHighlightCanvas._ctx;
			ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
			ctx = null;
			plot.redraw();
			//plot.replot({resetAxes:true});
			c._shift.shifting = false;
		} else if (c.zoom && c._zoom.zooming && !c.zoomTarget) {
			var xpos = c._zoom.gridpos.x;
			var ypos = c._zoom.gridpos.y;
			var datapos = c._zoom.datapos;
			var height = c.zoomCanvas._ctx.canvas.height;
			var width = c.zoomCanvas._ctx.canvas.width;
			var axes = plot.axes;

			c.endGridPoint = [xpos, ypos];
			//customizing (2012-04-13, Roy Choi)

			if (c.constrainOutsideZoom && !c.onGrid) {
				if (xpos < 0) {
					xpos = 0;
				} else if (xpos > width) {
					xpos = width;
				}
				if (ypos < 0) {
					ypos = 0;
				} else if (ypos > height) {
					ypos = height;
				}

				for (var axis in datapos) {
					if (datapos[axis]) {
						if (axis.charAt(0) == 'x') {
							datapos[axis] = axes[axis].series_p2u(xpos);
						} else {
							datapos[axis] = axes[axis].series_p2u(ypos);
						}
					}
				}
			}

			if (c.constrainZoomTo == 'x') {
				ypos = height;
			} else if (c.constrainZoomTo == 'y') {
				xpos = width;
			}
			c._zoom.end = [xpos, ypos];
			c._zoom.gridpos = {
				x : xpos,
				y : ypos
			};

			// customizing Start (2012-04-12, Roy Choi)
			var right2left = 0;
			var direction = 0;
			if (c.right2leftUndo && !c.zoomProxy) {
				if (c.constrainZoomTo == 'x') {
					right2left = Math.abs(c.startGridPoint[1] - c.endGridPoint[1]);
					direction = Math.abs(c.startGridPoint[0] - c.endGridPoint[0]);
				} else if (c.constrainZoomTo == 'y') {
					right2left = Math.abs(c.startGridPoint[0] - c.endGridPoint[0]);
					direction = Math.abs(c.startGridPoint[1] - c.endGridPoint[1]);
				} else {
					right2left = c.startGridPoint[0] - c.endGridPoint[0];
				}
			}
			if (direction < c.right2leftUndoDirection && right2left >= c.right2leftUndoWidth && right2left < c.right2leftResetWidth) {
				plot.undoZoom();
			} else if (direction < c.right2leftUndoDirection && right2left > c.right2leftResetWidth) {
				plot.resetZoom();
			} else {
				c.doZoom(c._zoom.gridpos, datapos, plot, c);
			}
			// customizing End (2012-04-12, Roy Choi)

		}

		/*if(c.zoomProxy) {
		 c._zoom.isZoomed = true;
		 }*/

		c._zoom.started = false;
		c._zoom.zooming = false;

		c._shift.started = false;
		c._shift.shifting = false;
		c._shift.data = null;

		$(document).unbind('mousemove.jqplotCursor', handleZoomMove);
		$(document).unbind('mousemove.jqplotCursor', handleShiftMove);

		//if (document.onselectstart != undefined && c._oldHandlers.onselectstart != null){
		document.onselectstart = c._oldHandlers.onselectstart;
		c._oldHandlers.onselectstart = null;
		//}
		if (document.ondrag != undefined && c._oldHandlers.ondrag != null) {
			document.ondrag = c._oldHandlers.ondrag;
			c._oldHandlers.ondrag = null;
		}
		if (document.onmousedown != undefined && c._oldHandlers.onmousedown != null) {
			document.onmousedown = c._oldHandlers.onmousedown;
			c._oldHandlers.onmousedown = null;
		}
		plot = c = null;
	}

	/* customizing start (2012-04-23, Roy Choi) */
	function handleRightUp(ev) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		var plot = ev.data.plot;
		var c = plot.plugins.cursor;

		c._drag.started = false;
		c._drag.dragging = false;
		c.dragStartAxes = {};

		$(document).unbind('mousemove.jqplotCursor', handleDragMove);

		if (document.onselectstart != undefined && c._oldHandlers.onselectstart != null) {
			document.onselectstart = c._oldHandlers.onselectstart;
			c._oldHandlers.onselectstart = null;
		}
		if (document.ondrag != undefined && c._oldHandlers.ondrag != null) {
			document.ondrag = c._oldHandlers.ondrag;
			c._oldHandlers.ondrag = null;
		}
		if (document.onmousedown != undefined && c._oldHandlers.onmousedown != null) {
			document.onmousedown = c._oldHandlers.onmousedown;
			c._oldHandlers.onmousedown = null;
		}
	}

	/* customizing end (2012-04-23, Roy Choi) */

	function drawZoomBox() {
		var start = this._zoom.start;
		var end = this._zoom.end;

		var ctx = this.zoomCanvas._ctx;
		var l, t, h, w;
		if (end[0] > start[0]) {
			l = start[0];
			w = end[0] - start[0];
		} else {
			l = end[0];
			w = start[0] - end[0];
		}
		if (end[1] > start[1]) {
			t = start[1];
			h = end[1] - start[1];
		} else {
			t = end[1];
			h = start[1] - end[1];
		}
		ctx.fillStyle = 'rgba(0,0,0,0.2)';
		ctx.strokeStyle = '#999999';
		ctx.lineWidth = 1.0;
		ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		ctx.clearRect(l, t, w, h);
		// IE won't show transparent fill rect, so stroke a rect also.
		//ctx.strokeRect(l,t,w,h);
		ctx = null;
	}

})(jQuery); ;/**
 * jqPlot
 * Pure JavaScript plotting plugin using jQuery
 *
 * Version: 1.0.8
 * Revision: 1250
 *
 * Copyright (c) 2009-2013 Chris Leonello
 * jqPlot is currently available for use in all personal or commercial projects
 * under both the MIT (http://www.opensource.org/licenses/mit-license.php) and GPL
 * version 2.0 (http://www.gnu.org/licenses/gpl-2.0.html) licenses. This means that you can
 * choose the license that best suits your project and use it accordingly.
 *
 * Although not required, the author would appreciate an email letting him
 * know of any substantial use of jqPlot.  You can reach the author at:
 * chris at jqplot dot com or see http://www.jqplot.com/info.php .
 *
 * If you are feeling kind and generous, consider supporting the project by
 * making a donation at: http://www.jqplot.com/donate.php .
 *
 * sprintf functions contained in jqplot.sprintf.js by Ash Searle:
 *
 *     version 2007.04.27
 *     author Ash Searle
 *     http://hexmen.com/blog/2007/03/printf-sprintf/
 *     http://hexmen.com/js/sprintf.js
 *     The author (Ash Searle) has placed this code in the public domain:
 *     "This code is unrestricted: you are free to use it however you like."
 *
 */
(function($) {
	$.jqplot.eventListenerHooks.push(['jqplotMouseMove', handleMove]);
	$.jqplot.eventListenerHooks.push(['jqplotClick', handleClick]);
	$.jqplot.eventListenerHooks.push(['jqplotMouseDown', handleMouseDown]);
	// customizing (2012-04-23, Roy Choi)
	$.jqplot.eventListenerHooks.push(['jqplotRightClick', handleRightClick]);
	// customizing (2012-05-02, Roy Choi)
	$.jqplot.eventListenerHooks.push(['jqplotRightDown', handleRightDown]);
	// customizing (2012-05-02, Roy Choi)

	/**
	 * Class: $.jqplot.Highlighter
	 * Plugin which will highlight data points when they are moused over.
	 *
	 * To use this plugin, include the js
	 * file in your source:
	 *
	 * > <script type="text/javascript" src="plugins/jqplot.highlighter.js"></script>
	 *
	 * A tooltip providing information about the data point is enabled by default.
	 * To disable the tooltip, set "showTooltip" to false.
	 *
	 * You can control what data is displayed in the tooltip with various
	 * options.  The "tooltipAxes" option controls wether the x, y or both
	 * data values are displayed.
	 *
	 * Some chart types (e.g. hi-low-close) have more than one y value per
	 * data point. To display the additional values in the tooltip, set the
	 * "yvalues" option to the desired number of y values present (3 for a hlc chart).
	 *
	 * By default, data values will be formatted with the same formatting
	 * specifiers as used to format the axis ticks.  A custom format code
	 * can be supplied with the tooltipFormatString option.  This will apply
	 * to all values in the tooltip.
	 *
	 * For more complete control, the "formatString" option can be set.  This
	 * Allows conplete control over tooltip formatting.  Values are passed to
	 * the format string in an order determined by the "tooltipAxes" and "yvalues"
	 * options.  So, if you have a hi-low-close chart and you just want to display
	 * the hi-low-close values in the tooltip, you could set a formatString like:
	 *
	 * > highlighter: {
	 * >     tooltipAxes: 'y',
	 * >     yvalues: 3,
	 * >     formatString:'<table class="jqplot-highlighter">
	 * >         <tr><td>hi:</td><td>%s</td></tr>
	 * >         <tr><td>low:</td><td>%s</td></tr>
	 * >         <tr><td>close:</td><td>%s</td></tr></table>'
	 * > }
	 *
	 */
	$.jqplot.Highlighter = function(options) {
		// Group: Properties
		//
		//prop: show
		// true to show the highlight.
		this.show = $.jqplot.config.enablePlugins;
		// prop: markerRenderer
		// Renderer used to draw the marker of the highlighted point.
		// Renderer will assimilate attributes from the data point being highlighted,
		// so no attributes need set on the renderer directly.
		// Default is to turn off shadow drawing on the highlighted point.
		this.markerRenderer = new $.jqplot.MarkerRenderer({
			shadow : false
		});
		// prop: showMarker
		// true to show the marker
		this.showMarker = true;
		// prop: lineWidthAdjust
		// Pixels to add to the lineWidth of the highlight.
		this.lineWidthAdjust = 2.5;
		// prop: sizeAdjust
		// Pixels to add to the overall size of the highlight.
		this.sizeAdjust = 5;
		// prop: showTooltip
		// Show a tooltip with data point values.
		this.showTooltip = true;
		// prop: tooltipLocation
		// Where to position tooltip, 'n', 'ne', 'e', 'se', 's', 'sw', 'w', 'nw'
		this.tooltipLocation = 'nw';
		// prop: fadeTooltip
		// true = fade in/out tooltip, flase = show/hide tooltip
		this.fadeTooltip = true;
		// prop: tooltipFadeSpeed
		// 'slow', 'def', 'fast', or number of milliseconds.
		this.tooltipFadeSpeed = "fast";
		// prop: tooltipOffset
		// Pixel offset of tooltip from the highlight.
		this.tooltipOffset = 2;
		// prop: tooltipAxes
		// Which axes to display in tooltip, 'x', 'y' or 'both', 'xy' or 'yx'
		// 'both' and 'xy' are equivalent, 'yx' reverses order of labels.
		this.tooltipAxes = 'both';
		// prop; tooltipSeparator
		// String to use to separate x and y axes in tooltip.
		this.tooltipSeparator = ', ';
		// prop; tooltipContentEditor
		// Function used to edit/augment/replace the formatted tooltip contents.
		// Called as str = tooltipContentEditor(str, seriesIndex, pointIndex)
		// where str is the generated tooltip html and seriesIndex and pointIndex identify
		// the data point being highlighted. Should return the html for the tooltip contents.
		this.tooltipContentEditor = null;
		// prop: useAxesFormatters
		// Use the x and y axes formatters to format the text in the tooltip.
		this.useAxesFormatters = true;
		// prop: tooltipFormatString
		// sprintf format string for the tooltip.
		// Uses Ash Searle's javascript sprintf implementation
		// found here: http://hexmen.com/blog/2007/03/printf-sprintf/
		// See http://perldoc.perl.org/functions/sprintf.html for reference.
		// Additional "p" and "P" format specifiers added by Chris Leonello.
		this.tooltipFormatString = '%.5P';
		// prop: formatString
		// alternative to tooltipFormatString
		// will format the whole tooltip text, populating with x, y values as
		// indicated by tooltipAxes option.  So, you could have a tooltip like:
		// 'Date: %s, number of cats: %d' to format the whole tooltip at one go.
		// If useAxesFormatters is true, values will be formatted according to
		// Axes formatters and you can populate your tooltip string with
		// %s placeholders.
		this.formatString = null;
		// prop: yvalues
		// Number of y values to expect in the data point array.
		// Typically this is 1.  Certain plots, like OHLC, will
		// have more y values in each data point array.
		this.yvalues = 1;
		// prop: bringSeriesToFront
		// This option requires jQuery 1.4+
		// True to bring the series of the highlighted point to the front
		// of other series.
		this.bringSeriesToFront = false;
		this._tooltipElem = null;
		this.isHighlighting = false;
		this.currentNeighbor = null;

		this.isClickHighlighting = false;
		// customizing (2012-04-19, Roy Choi)
		this.isMultiTooltip = false;
		// customizing (2012-04-19, Roy Choi)
		this.isClickTooltip = false;
		// customizing (2012-04-19, Roy Choi)
		this.multiTooltipPoint = [];
		// customizing (2012-05-23, Roy Choi)
		this.multiTooltipElem = [];
		// customizing (2012-04-19, Roy Choi)
		this.multiTooltipLineNames = [];
		// customizing (2012-04-20, Roy Choi)
		this.tooltipLineName = 'highlighter_tooltip_line';
		// customizing (2012-04-20, Roy Choi)
		this.clickStart = {
			x : null,
			y : null
		};
		// customizing (2012-04-20, Roy Choi)

		// point variation customizing (2012-04-27, Roy Choi)
		this.pointVariation = false;
		this.pointVariationStart = false;
		this.pointVariationTooltipElem = null;
		this.pointVariationLineName = 'highlighter_pointvariation_line';
		this.pointVariationLineColor = '#000000';
		this.pointVariationLineWidth = 0.8;
		this.pointVariationStartPos = null;
		this.pointVariationEndPos = {};
		this.pointVariationThreshold = 6;

		// context menu customizing modified (2013-01-22, Roy Choi)
		this.contextMenu = false;
		/*this.contextMenuElem = null;*/
		this.contextMenuSeriesOptions = {};
		this.contextMenuBackgroundOptions = {};

		// point cross line customizing (2012-05-03, Roy Choi)
		this.pointCrossLine = false;
		this.pointCrossLineStart = false;
		this.pointCrossLineSeriesIndex = null;
		this.pointCrossLineCanvas = null;
		
		// customizing to hide tooltip on click outside (2014-06-02, Roy Choi)
		this.clearTooltipOnClickOutside = false;
		
		// customizing to show tooltip on mouse over a point (2014-10-16, Roy Choi)
		this.overTooltip = false;
		this.overTooltipOptions = {
  			showMarker: false,
  			showTooltip: true,
  			lineOver: false
		};
		this.clickTooltip = true;
		this.currentOverNeighbor = null;
		this.highlightOnlyMarker = false;
		this.isTooltipDragging = false;
		this.stroke = false;
		this.strokeStyle = null;
		this.tooltipDraggable = true;
		
		this.selectable = {
			show: false,
			multiSelect: true,
			seriesHighlight: false,
			selected: [],
			tooltipContentEditor: null,
			selectChanged: null,
			tooltipOpend: false,
			seriesFadeOut: 1,
			selectColumnIndex: null,
			unselect: false,
			ctrlRemove: true,
			_selected: [],
			_selectedInfo: [],
			_oldSelected: []
		};
		
		$.extend(true, this, options);
	};

	var locations = ['nw', 'n', 'ne', 'e', 'se', 's', 'sw', 'w'];
	var locationIndicies = {
		'nw' : 0,
		'n' : 1,
		'ne' : 2,
		'e' : 3,
		'se' : 4,
		's' : 5,
		'sw' : 6,
		'w' : 7
	};
	var oppositeLocations = ['se', 's', 'sw', 'w', 'nw', 'n', 'ne', 'e'];

	// axis.renderer.tickrenderer.formatter

	// called with scope of plot
	$.jqplot.Highlighter.init = function(target, data, opts) {
		var options = opts || {};
		// add a highlighter attribute to the plot
		this.plugins.highlighter = new $.jqplot.Highlighter(options.highlighter);
	};

	// customizing (2012-04-20, Roy Choi)
	$.jqplot.Highlighter.prototype.clearMultiTooltip = function(plot) {
		var hl = plot.plugins.highlighter;
		$.each(hl.multiTooltipElem, function(ind, elem) {
			elem.hide();
			elem.remove();
		});
		hl.multiTooltipElem = [];
		hl.multiTooltipPoint = [];
		// customizing (2012-05-23, Roy Choi)
		hideTooltip(plot);
		plot.restoreOriginalSeriesOrder();
		hl.isClickHighlighting = false;
		hl.currentNeighbor = null;
		/*$.each(plot.series, function(ind, series){
		 $(series.canvas._elem).fadeTo('fast',1.0);
		 });*/

		var co = plot.plugins.canvasOverlay;
		$.each(hl.multiTooltipLineNames, function(ind, lineName) {
			co.removeObject(lineName);
		});
		hl.multiTooltipLineNames = [];
		co.draw(plot);
		hideTooltipLine(plot);
	};

	// customizing (2012-04-23, Roy Choi)
	$.jqplot.Highlighter.prototype.moveToFrontMultiTooltip = function(plot) {
		var hl = plot.plugins.highlighter;
		$.each(hl.multiTooltipElem, function(ind, elem) {
			
			var elemInfo = hl.multiTooltipPoint[ind];
			
			plot.eventCanvas._elem.after(elem);
			elem.css({
				cursor : 'default',
				'border-color' : elemInfo.color
			});
			
			if (hl.tooltipDraggable) {
				elem.draggable({
					cursor : 'default',
					containment : plot.target.parent()
				}).bind('drag', {
					plot : plot,
					series : elemInfo.series,
					name : elemInfo.name,
					color : elemInfo.color,
					start : elemInfo.start,
					stop : false
				}, handleDragToolTip).bind('dragstop', {
					plot : plot,
					series : elemInfo.series,
					name : elemInfo.name,
					color : elemInfo.color,
					start : elemInfo.start,
					stop : true
				}, handleDragToolTip);
	
				elem.trigger('dragstop');
			}
		});
	};

	// customizing (2012-04-23, Roy Choi)
	$.jqplot.Highlighter.prototype.offVariation = function(plot) {
		var hl = plot.plugins.highlighter;
		hl.pointVariation = false;
		clearVariation(plot);
	};

	// customizing (2012-05-31, Roy Choi) for point cross line
	$.jqplot.Highlighter.prototype.setCrossLine = function(plot) {
		this.pointCrossLine = true;
	};

	// customizing (2012-05-31, Roy Choi) for point cross line
	$.jqplot.Highlighter.prototype.unsetCrossLine = function(plot) {
		this.pointCrossLine = false;
	};

	// customizing (2012-05-03, Roy Choi)
	$.jqplot.Highlighter.prototype.clearCrossLine = function(plot) {
		var hl = plot.plugins.highlighter;
		var ctx = hl.pointCrossLineCanvas._ctx;
		ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		ctx = null;
	};

	// customizing (2012-06-15, Roy Choi)
	$.jqplot.Highlighter.prototype.clearHighlight = function(plot) {
		var hl = plot.plugins.highlighter;
		var ctx = hl.highlightCanvas._ctx;
		ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		if (hl.fadeTooltip) {
			if (hl.isMultiTooltip && hl.multiTooltipElem.length > 0) {
				//hl.clearMultiTooltip(plot);
			} else {
				hideTooltipLine(plot);
				fadeoutTooltip(plot);;
			}
		} else {
			if (hl.isMultiTooltip && hl.multiTooltipElem.length > 0) {
				//hl.clearMultiTooltip(plot);
			} else {
				hideTooltipLine(plot);
				hideTooltip(plot);
			}
		}
		if (hl.bringSeriesToFront) {
			plot.restoreOriginalSeriesOrder();
		}
		hl.isClickTooltip = false;
		hl.isClickHighlighting = false;
		hl.currentNeighbor = null;
		ctx = null;
	};

	// called within scope of series
	$.jqplot.Highlighter.parseOptions = function(defaults, options) {
		// Add a showHighlight option to the series
		// and set it to true by default.
		this.showHighlight = true;
	};
	
	$.jqplot.Highlighter.preParseOptions = function(options) {
	  if (this.plugins.highlighter.selectable &&  this.plugins.highlighter.selectable.show && options.highlighter.selectable && $.isArray(options.highlighter.selectable.selected)) {
      this.plugins.highlighter.selectable._selected = options.highlighter.selectable.selected;
    }
	};

	// called within context of plot
	// create a canvas which we can draw on.
	// insert it before the eventCanvas, so eventCanvas will still capture events.
	$.jqplot.Highlighter.postPlotDraw = function() {
		// Memory Leaks patch
		if (this.plugins.highlighter && this.plugins.highlighter.highlightCanvas) {
			this.plugins.highlighter.highlightCanvas.resetCanvas();
			this.plugins.highlighter.highlightCanvas = null;
		}

		if (this.plugins.highlighter && this.plugins.highlighter._tooltipElem) {
			this.plugins.highlighter._tooltipElem.emptyForce();
			// customizing start (2012-05-03, Roy Choi)
			try {
				this.plugins.highlighter._tooltipElem.draggable();
				this.plugins.highlighter._tooltipElem.draggable('destroy');
			} catch (e) {
			}

			this.plugins.highlighter._tooltipElem.remove();
			// customizing end (2012-05-03, Roy Choi)
			this.plugins.highlighter._tooltipElem = null;
			hideTooltipLine(this);
		}

		this.plugins.highlighter.highlightCanvas = new $.jqplot.GenericCanvas();

		this.eventCanvas._elem.before(this.plugins.highlighter.highlightCanvas.createElement(this._gridPadding, 'jqplot-highlight-canvas', this._plotDimensions, this));
		this.plugins.highlighter.highlightCanvas.setContext();

		var elem = document.createElement('div');
		this.plugins.highlighter._tooltipElem = $(elem);
		this.plugins.highlighter._tooltipElem.addClass('jqplot-highlighter-tooltip-wrapper');
		elem = null;

		//this.plugins.highlighter._tooltipElem.addClass('jqplot-highlighter-tooltip');
		this.plugins.highlighter._tooltipElem.css({
			position : 'absolute',
			display : 'none'
		});

		this.eventCanvas._elem.after(this.plugins.highlighter._tooltipElem);
		
		if (this.plugins.highlighter.selectable.show) {
			var selectable = this.plugins.highlighter.selectable;
			
			this.plugins.highlighter.canvas = new $.jqplot.GenericCanvas();
			this.eventCanvas._elem.before(this.plugins.highlighter.canvas.createElement(this._gridPadding, 'jqplot-cursor-canvas', this._plotDimensions, this));
			this.plugins.highlighter.canvas.setContext();
			
			if ($.isArray(selectable._selected) && selectable._selected.length > 0) {
				highlightByData(this, selectable._selected);
			}
		}
		


		// customizing start (2012-04-25, Roy Choi)
		/*this.target.unbind('jqplotPostReplot', moveToFrontMultiTooltipPostReplot);
		this.target.bind('jqplotPostReplot', {
			plot : this
		}, moveToFrontMultiTooltipPostReplot);*/
		// customizing end (2012-04-25, Roy Choi)

		// customizing start (2012-04-27, Roy Choi)
		if (this.plugins.highlighter.pointVariationTooltipElem) {
			this.plugins.highlighter.pointVariationTooltipElem.remove();
		}
		var elem = document.createElement('div');
		this.plugins.highlighter.pointVariationTooltipElem = $(elem);
		this.plugins.highlighter.pointVariationTooltipElem.addClass('jqplot-highlighter-variation-tooltip');
		elem = null;
		this.eventCanvas._elem.before(this.plugins.highlighter.pointVariationTooltipElem);
		if ( typeof (this.plugins.canvasOverlay.get(this.plugins.highlighter.pointVariationLineName)) != 'object')
			addTooltipLine(this, this.plugins.highlighter.pointVariationLineName);
		// customizing end (2012-04-27, Roy Choi)

		// customizing start (2012-05-02, Roy Choi)
		/*if(this.plugins.highlighter.contextMenu) {

		if(this.plugins.highlighter.contextMenuElem) {
		this.plugins.highlighter.contextMenuElem.empty();
		this.plugins.highlighter.contextMenuElem.remove();
		}

		var elem = document.createElement('ul');

		this.plugins.highlighter.contextMenuElem = $(elem);
		this.plugins.highlighter.contextMenuElem.addClass('jqplot-highlighter-contextmenu');
		elem = null;

		this.plugins.highlighter.contextMenuElem.appendTo('body');
		this.plugins.highlighter.contextMenuElem.css({zIndex:'20000'});

		$(window).on('blur',{plot:this},rightClickBlur);
		$(document).on('click',{plot:this},rightClickBlur);
		$(document).on('mousedown',{plot:this},function(ev){if(ev.which == 3) rightClickBlur(ev)});
		}*/
		// customizing end (2012-05-02, Roy Choi)

		// customizing start (2012-05-03, Roy Choi)	- canvas create for point cross line
		this.plugins.highlighter.pointCrossLineCanvas = new $.jqplot.GenericCanvas();
		this.eventCanvas._elem.before(this.plugins.highlighter.pointCrossLineCanvas.createElement(this._gridPadding, 'jqplot-crossline-canvas', this._plotDimensions, this));
		this.plugins.highlighter.pointCrossLineCanvas.setContext();
		// customizing end (2012-05-03, Roy Choi)
		/*this.target.off('jqplotResetZoom',  function(ev) {
			ev.data.plot.plugins.highlighter.moveToFrontMultiTooltip(ev.data.plot);
		});
		this.target.on('jqplotResetZoom', {
			plot : this
		}, function(ev) {
			ev.data.plot.plugins.highlighter.moveToFrontMultiTooltip(ev.data.plot);
		});
		this.target.off('jqplotUndoZoom', function(ev) {
			ev.data.plot.plugins.highlighter.moveToFrontMultiTooltip(ev.data.plot);
		});
		this.target.on('jqplotUndoZoom', {
			plot : this
		}, function(ev) {
			ev.data.plot.plugins.highlighter.moveToFrontMultiTooltip(ev.data.plot);
		});
		this.target.off('jqplotZoom', function(ev) {
			ev.data.plot.plugins.highlighter.moveToFrontMultiTooltip(ev.data.plot);
		});
		this.target.on('jqplotZoom', {
			plot : this
		}, function(ev) {
			ev.data.plot.plugins.highlighter.moveToFrontMultiTooltip(ev.data.plot);
		});*/

		this.target.off('jqPlot.PreviousSeriesOrder', handleRemoveHighlight);
		this.target.off('jqPlot.OriginalSeriesOrder', handleRemoveHighlight);
		this.target.off('jqPlot.seriesVisibleChange', handleVisibleChange);

		this.target.on('jqPlot.PreviousSeriesOrder', handleRemoveHighlight);
		this.target.on('jqPlot.OriginalSeriesOrder', handleRemoveHighlight);
		this.target.on('jqPlot.seriesVisibleChange', handleVisibleChange);

		this.plugins.highlighter.moveToFrontMultiTooltip(this);

		$(document).unbind('mousedown.jqplot_highlighter.'+this.targetId);
		if (this.plugins.highlighter.clearTooltipOnClickOutside) {
			$(document).on('mousedown.jqplot_highlighter.'+this.targetId, {plot: this}, this.plugins.highlighter.clearHighlightByEvent);
		}
	};
	
  $.jqplot.Highlighter.preReinitOptions = function(target, data, options) {
    if (this.options.highlighter && this.options.highlighter.selectable && options && options.highlighter && options.highlighter.selectable && $.isArray(options.highlighter.selectable.selected)) {
      this.options.highlighter.selectable.selected = [];
    }
  };

	$.jqplot.preInitHooks.push($.jqplot.Highlighter.init);
	$.jqplot.preParseOptionsHooks.push($.jqplot.Highlighter.preParseOptions);
	$.jqplot.preParseSeriesOptionsHooks.push($.jqplot.Highlighter.parseOptions);
	$.jqplot.postDrawHooks.push($.jqplot.Highlighter.postPlotDraw);
	
	$.jqplot.preReinitHooks.push($.jqplot.Highlighter.preReinitOptions);

	// customizing (2014-06-02, Roy Choi)
	$.jqplot.Highlighter.prototype.clearHighlightByEvent = function(ev) {
		var plot = ev.data.plot;
		if (plot.target.is(':visible') && plot.target.length && plot.target.find(ev.target).length === 0) {
			plot.plugins.highlighter.clearHighlight(plot);
		}
	};

	// customizing (2012-04-25, Roy Choi)
	function moveToFrontMultiTooltipPostReplot(ev) {
		var plot = ev.data.plot;
		var hl = plot.plugins.highlighter;
		$.each(hl.multiTooltipElem, function(ind, elem) {
			plot.eventCanvas._elem.after(elem);
			elem.trigger('postReplot');
		});
	}

	// customizing (2012-04-25, Roy Choi)
	function moveToFrontMultiTooltipEnd(ev) {
		var plot = ev.data.plot;
		var hl = plot.plugins.highlighter;
		$.each(hl.multiTooltipElem, function(ind, elem) {
			plot.eventCanvas._elem.after(elem);
			elem.trigger('drag');
		});
	}

	// customizing (2012-04-25, Roy Choi)
	$.jqplot.Highlighter.prototype.clearVariation = function(plot) {
		clearVariation(plot);
	};

	// customizing (2012-04-25, Roy Choi)
	function clearVariation(plot) {
		var co = plot.plugins.canvasOverlay;
		var hl = plot.plugins.highlighter;
		hl.pointVariationStart = false;
		hl.pointVariationTooltipElem.hide();
		var obj = co.get(hl.pointVariationLineName);
		obj.options.show = false;
		co.draw(plot);
		hl.pointVariationStartPos = null;
		hl.pointVariationEndPos = null;
	}

	// customizing (2012-04-23, Roy Choi)
	function addTooltipLine(plot, name) {
		var co = plot.plugins.canvasOverlay;
		var opts = {
			show : true,
			name : name,
			lineWidth : 0,
			lineBehind : false,
			showTooltip : false,
			shadow : false,
			isDragable : false,
			start : [0, 0],
			stop : [0, 0]
		};
		co.addLine(opts);
	}

	// customizing (2012-04-23, Roy Choi)
	function hideTooltipLine(plot) {
		var co = plot.plugins.canvasOverlay;
		var hl = plot.plugins.highlighter;
		var obj = co.get(hl.tooltipLineName);
		if (obj) {
			co.removeObject(hl.tooltipLineName);
			co.draw(plot);
		}
	}
	
	function hideTooltip(plot) {
		var hl = plot.plugins.highlighter;
		
		hl._tooltipElem.hide().empty();
		if (hl.selectable) {
			hl.selectable.tooltipOpend = false;
		}
	}
	
	function fadeoutTooltip(plot) {
		var hl = plot.plugins.highlighter;
		
		hl._tooltipElem.fadeOut(hl.tooltipFadeSpeed).empty();
		if (hl.selectable) {
			hl.selectable.tooltipOpend = false;
		}
	}

	function draw(plot, neighbor) {
		var hl = plot.plugins.highlighter;
		var s = plot.series[neighbor.seriesIndex];
		if (s.renderer.constructor === $.jqplot.BoxplotRenderer && neighbor.points) {
			return;
		}
		var smr = s.markerRenderer;
		var mr = hl.markerRenderer;
		mr.style = smr.style;
		mr.lineWidth = smr.lineWidth + hl.lineWidthAdjust;
		mr.size = smr.size + hl.sizeAdjust;
		var rgba = $.jqplot.getColorComponents(smr.color);
		var newrgb = [rgba[0], rgba[1], rgba[2]];
		var alpha = (rgba[3] >= 0.6) ? rgba[3] * 0.6 : rgba[3] * (2 - rgba[3]);
		mr.color = 'rgba(' + newrgb[0] + ',' + newrgb[1] + ',' + newrgb[2] + ',' + alpha + ')';
		mr.shapeRenderer.color = mr.color;
		mr.stroke = hl.stroke;
		mr.strokeStyle = hl.strokeStyle;
		mr.init();
		var y;
		if (s.renderer.constructor === $.jqplot.BoxplotRenderer) {
			y = neighbor.gridData[1];
		} else {
			y = s.gridData[neighbor.pointIndex][1];
		}
		mr.draw(s.gridData[neighbor.pointIndex][0], y, hl.highlightCanvas._ctx);
	}

	// customizing (2012-04-23, Roy Choi)
	function handleDragToolTip(ev) {
		var plot = ev.data.plot;
		var series = ev.data.series;
		var co = plot.plugins.canvasOverlay;
		var hl = plot.plugins.highlighter;
		var name = ev.data.name;
		var color = ev.data.color;
		var stop = ev.data.stop;
		var obj = co.get(name);
		
		if (stop) {
			hl.isTooltipDragging = false;
		} else {
			hl.isTooltipDragging = true;
		}
		
		if (obj) {
			var start = ev.data.start;
			start = [plot.axes[obj.options.xaxis].series_p2u(plot.axes[series.xaxis].series_u2p(start[0])), plot.axes[obj.options.yaxis].series_p2u(plot.axes[series.yaxis].series_u2p(start[1]))];
			var go = plot.eventCanvas._elem.offset();
			var offset = $('div.jqplot-highlighter-tooltip', this).offset();
			var gridPos = {
				x : offset.left - go.left,
				y : offset.top - go.top
			};
			var stop = [plot.axes[obj.options.xaxis].series_p2u(gridPos.x), plot.axes[obj.options.yaxis].series_p2u(gridPos.y)];
			var opts = {
				show: true,
				lineWidth : 1.5,
				color : color,
				start : start,
				stop : stop
			};
			$.extend(true, obj.options, opts);
			co.draw(plot);
		}
	}

	function showTooltip(ev, plot, series, neighbor, over) {
		// neighbor looks like: {seriesIndex: i, pointIndex:j, gridData:p, data:s.data[j]}
		// gridData should be x,y pixel coords on the grid.
		// add the plot._gridPadding to that to get x,y in the target.
		if (series.hide || !series.show) {
			return;
		}
		var hl = plot.plugins.highlighter;
		// customizing Start (2012-04-23, Roy Choi)
		if (hl.isMultiTooltip) {
			var isAleady = false;
			$.each(hl.multiTooltipPoint, function(ind, savedTooltip) {
				if (savedTooltip.neighbor.seriesIndex == neighbor.seriesIndex && savedTooltip.neighbor.data[0] == neighbor.data[0] && savedTooltip.neighbor.data[1] == neighbor.data[1]) {
					isAleady = true;
					return false;
				}
			});
			if (isAleady)
				return;
			var elem = hl._tooltipElem.clone(false);
			elem.css({
				position : 'absolute',
				display : 'none'
			});
			//$(elem).live('mouseup',{plot:plot},function(ev){ev.data.plot.eventCanvas._elem.trigger('mouseup');}).live('mousemove',{plot:plot},function(ev){ev.data.plot.eventCanvas._elem.trigger('mousemove');}).live('mouseenter',{plot:plot},function(ev){ev.data.plot.eventCanvas._elem.trigger('mouseenter');}).live('mouseleave',{plot:plot},function(ev){ev.data.plot.eventCanvas._elem.trigger('mouseleave');});	// customizing (2012-04-23, Roy Choi)
			//hl._tooltipElem.addClass('jqplot-highlighter-tooltip');

			plot.eventCanvas._elem.after(elem);
		} else {
			var elem = hl._tooltipElem;
		}
		// customizing End (2012-04-23, Roy Choi)

		var serieshl = series.highlighter || {};

		var opts = $.extend(true, {}, hl, serieshl);

		if (opts.useAxesFormatters) {
			var xf = series._xaxis._ticks[0].formatter;
			var yf = series._yaxis._ticks[0].formatter;
			var xfstr = series._xaxis._ticks[0].formatString;
			var yfstr = series._yaxis._ticks[0].formatString;
			var str;
			var xstr = xf(xfstr, neighbor.data[0]);
			var ystrs = [];
			for (var i = 1; i < opts.yvalues + 1; i++) {
				ystrs.push(yf(yfstr, neighbor.data[i]));
			}
			if ( typeof opts.formatString === 'string') {
				switch (opts.tooltipAxes) {
					case 'both':
					case 'xy':
						ystrs.unshift(xstr);
						ystrs.unshift(opts.formatString);
						str = $.jqplot.sprintf.apply($.jqplot.sprintf, ystrs);
						break;
					case 'yx':
						ystrs.push(xstr);
						ystrs.unshift(opts.formatString);
						str = $.jqplot.sprintf.apply($.jqplot.sprintf, ystrs);
						break;
					case 'x':
						str = $.jqplot.sprintf.apply($.jqplot.sprintf, [opts.formatString, xstr]);
						break;
					case 'y':
						ystrs.unshift(opts.formatString);
						str = $.jqplot.sprintf.apply($.jqplot.sprintf, ystrs);
						break;
					default:
						// same as xy
						ystrs.unshift(xstr);
						ystrs.unshift(opts.formatString);
						str = $.jqplot.sprintf.apply($.jqplot.sprintf, ystrs);
						break;
				}
			} else {
				switch (opts.tooltipAxes) {
					case 'both':
					case 'xy':
						str = xstr;
						for (var i = 0; i < ystrs.length; i++) {
							str += opts.tooltipSeparator + ystrs[i];
						}
						break;
					case 'yx':
						str = '';
						for (var i = 0; i < ystrs.length; i++) {
							str += ystrs[i] + opts.tooltipSeparator;
						}
						str += xstr;
						break;
					case 'x':
						str = xstr;
						break;
					case 'y':
						str = ystrs.join(opts.tooltipSeparator);
						break;
					default:
						// same as 'xy'
						str = xstr;
						for (var i = 0; i < ystrs.length; i++) {
							str += opts.tooltipSeparator + ystrs[i];
						}
						break;

				}
			}
		} else {
			var str;
			if ( typeof opts.formatString === 'string') {
				str = $.jqplot.sprintf.apply($.jqplot.sprintf, [opts.formatString].concat(neighbor.data));
			} else {
				if (opts.tooltipAxes == 'both' || opts.tooltipAxes == 'xy') {
					str = $.jqplot.sprintf(opts.tooltipFormatString, neighbor.data[0]) + opts.tooltipSeparator + $.jqplot.sprintf(opts.tooltipFormatString, neighbor.data[1]);
				} else if (opts.tooltipAxes == 'yx') {
					str = $.jqplot.sprintf(opts.tooltipFormatString, neighbor.data[1]) + opts.tooltipSeparator + $.jqplot.sprintf(opts.tooltipFormatString, neighbor.data[0]);
				} else if (opts.tooltipAxes == 'x') {
					str = $.jqplot.sprintf(opts.tooltipFormatString, neighbor.data[0]);
				} else if (opts.tooltipAxes == 'y') {
					str = $.jqplot.sprintf(opts.tooltipFormatString, neighbor.data[1]);
				}
			}
		}

		var tooltipContentProc = function(str) {
			var x, y;
			var innerElem = null;
			elem.emptyForce();
			if (typeof str !== 'object') {
				innerElem = $('<div></div>');
				innerElem.addClass('jqplot-highlighter-tooltip');
				elem.append(innerElem);
				innerElem.html(str);
			} else if (typeof str === 'object') {
				innerElem = str;
				elem.empty().append(str);
			}
			
			
			var gridpos = {
				x : neighbor.gridData[0],
				y : neighbor.gridData[1]
			};
			var ms = 0;
			var fact = 0.707;
			var tooltipPaddingSize = 0;
			var tooltipPadding = {
				marginLeft: 0,
				marginRight: 0,
				marginTop: 0,
				marginBottom: 0
			};
			if (series.markerRenderer.show == true) {
				ms = (series.markerRenderer.size + opts.sizeAdjust) / 2;
				tooltipPaddingSize = (series.markerRenderer.size + opts.sizeAdjust);
			} else {
				tooltipPaddingSize = opts.tooltipOffset;
			}

			var loc = locations;
			if (series.fillToZero && series.fill && neighbor.data[1] < 0) {
				loc = oppositeLocations;
			}
			
			var locStr = loc[locationIndicies[opts.tooltipLocation]];
			
			switch (locStr) {
				case 'nw':
					x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset - fact * ms;
					y = gridpos.y + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true) - fact * ms;
					tooltipPadding.marginBottom = tooltipPaddingSize + 'px';
					break;
				case 'n':
					x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) / 2;
					y = gridpos.y + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true) - ms;
					break;
				case 'ne':
					x = gridpos.x + plot._gridPadding.left + opts.tooltipOffset + fact * ms;
					y = gridpos.y + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true) - fact * ms;
					tooltipPadding.marginBottom = tooltipPaddingSize + 'px';
					break;
				case 'e':
					x = gridpos.x + plot._gridPadding.left + opts.tooltipOffset + ms;
					y = gridpos.y + plot._gridPadding.top - elem.outerHeight(true) / 2;
					break;
				case 'se':
					x = gridpos.x + plot._gridPadding.left + opts.tooltipOffset - tooltipPaddingSize + fact * ms;
					y = gridpos.y + plot._gridPadding.top + opts.tooltipOffset + fact * ms;
					tooltipPadding.marginTop = tooltipPaddingSize + 'px';
					break;
				case 's':
					x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) / 2;
					y = gridpos.y + plot._gridPadding.top + opts.tooltipOffset + ms;
					break;
				case 'sw':
					x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset - fact * ms;
					y = gridpos.y + plot._gridPadding.top + opts.tooltipOffset - tooltipPaddingSize + fact * ms;
					tooltipPadding.marginTop = tooltipPaddingSize + 'px';
					break;
				case 'w':
					x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset - ms;
					y = gridpos.y + plot._gridPadding.top - elem.outerHeight(true) / 2;
					break;
				default:
					// same as 'nw'
					x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset - fact * ms;
					y = gridpos.y + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true) - fact * ms;
					tooltipPadding.marginBottom = tooltipPaddingSize + 'px';
					break;
			}
			
			innerElem.css(tooltipPadding);
			
			var highlightCanvasElem = hl.highlightCanvas._elem;
			var highlightCanvasOffset = highlightCanvasElem.offset();

			/*if (gridpos.x < 0 || gridpos.x > hl.highlightCanvas._ctx.canvas.width || gridpos.y < 0 || gridpos.y > hl.highlightCanvas._ctx.canvas.height) {
				x += ev.pageX - gridpos.x - highlightCanvasOffset.left;
				y += ev.pageY - gridpos.y - highlightCanvasOffset.top;
			}*/
			
      if (x < 0 && locStr.indexOf('w') > -1) {
        x = gridpos.x + plot._gridPadding.left + opts.tooltipOffset + ms;
      } else if (x + elem.width() > plot.target.width()) {
        x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset - ms;
      }
      
      if (y < 0) {
        y = gridpos.y + plot._gridPadding.top + opts.tooltipOffset + ms;
        if (tooltipPadding.marginBottom) {
          tooltipPadding.marginTop = tooltipPadding.marginBottom;
          delete tooltipPadding.marginBottom;
        }
      } else if (gridpos.y + elem.height() > plot.target.height()) {
        y = gridpos.y + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true) - ms;
        if (tooltipPadding.marginTop) {
          tooltipPadding.marginBottom = tooltipPadding.marginTop;
          delete tooltipPadding.marginTop;
        }
      }
      
      if (y < 0 || gridpos.y + elem.height() > plot.target.height()) {
        y = Math.max(plot._gridPadding.top, gridpos.y + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true) - ms);
        if (tooltipPadding.marginTop) {
          tooltipPadding.marginBottom = tooltipPadding.marginTop;
          delete tooltipPadding.marginTop;
        }
      }
      
      innerElem.css(tooltipPadding);
			
      elem.css('left', x);
      elem.css('top', y);
			
			var elem_line_name = '';
			var color = series.color;

			/* customizing for stacked series (2013-02-20, Roy Choi) start */
			var start = [];
			if (plot.options.stackSeries) {
				var seriesList = plot.series;
				var starty = 0;
				for (var i = 0; i <= neighbor.seriesIndex; i++) {
					starty += seriesList[i].data[neighbor.pointIndex][1];
				}
				start = [neighbor.data[0], starty];
				seriesList = startx = starty = null;
			} else if (plot.series[neighbor.seriesIndex].renderer.constructor === $.jqplot.BoxplotRenderer && neighbor.points) {
				var yu = plot.series[neighbor.seriesIndex]._yaxis.series_p2u;
				var points = neighbor.points;
				start = [neighbor.data[0], yu(points[0][1]+(points[2][1]-points[0][1])/2)];
			} else if (plot.series[neighbor.seriesIndex].renderer.constructor === $.jqplot.BoxplotRenderer && neighbor.start) {
				start = neighbor.start;
			} else {
				start = [neighbor.data[0], neighbor.data[1]];
			}
			/* customizing for stacked series (2013-02-20, Roy Choi) end */

			var gridstop = [x, y];

			/* customizing start (2012-04-20, Roy Choi) */
			if (hl.isMultiTooltip) {
				elem_line_name = 'highlighter_multitooltip_line' + hl.multiTooltipLineNames.length;
				hl.multiTooltipLineNames.push(elem_line_name);
				addTooltipLine(plot, elem_line_name);
			} else {
				elem_line_name = hl.tooltipLineName;
				elem.unbind('drag');
				addTooltipLine(plot, elem_line_name);
				/*var co = plot.plugins.canvasOverlay;
				 var obj = co.get(elem_line_name);
				 var stop = [plot.axes[obj.options.xaxis].series_p2u(gridstop[0]), plot.axes[obj.options.yaxis].series_p2u(gridstop[1])];

				 var opts = {
				 color: color,
				 start:start,
				 stop:stop
				 }
				 $.extend(obj.options,opts);
				 plot.redraw(false);

				 */
			}
			//elem.parent().css('overflow','hidden');
			elem.css({
				cursor : 'default',
				'border-color' : color
			});
			
			if (hl.tooltipDraggable) {
				elem.draggable({
					cursor : 'default',
					containment : plot.target.parent()
				}).bind('drag', {
					plot : plot,
					series : series,
					name : elem_line_name,
					color : color,
					start : start,
					stop : false
				}, handleDragToolTip).bind('dragstop', {
					plot : plot,
					series : series,
					name : elem_line_name,
					color : color,
					start : start,
					stop : true
				}, handleDragToolTip);
			}
			
			if (hl.isMultiTooltip) {
				hl.multiTooltipElem.push(elem);
				hl.multiTooltipPoint.push({
					neighbor: neighbor,
					series: series,
					name: elem_line_name,
					color: color,
					start: start
				});
			}
			
			/* customizing end (2012-04-20, Roy Choi) */

			if (opts.fadeTooltip) {
				// Fix for stacked up animations.  Thnanks Trevor!
				elem.stop(true, true).fadeIn(opts.tooltipFadeSpeed);
			} else {
				elem.show();
			}
			
			elem = null;
		};
		if ($.isFunction(opts.tooltipContentEditor)) {
			// args str, seriesIndex, pointIndex are essential so the hook can look up
			// extra data for the point.
			str = opts.tooltipContentEditor.call(hl, str, neighbor.seriesIndex, neighbor.pointIndex, plot, tooltipContentProc);
		} else {
			tooltipContentProc(str);
		}

	}

	// customizing Start (2014-10-21, Roy Choi), To show selected tooltip
	function showSelectTooltip(ev, plot, selected, oldSelected, selectedData, neighbor) {
		// neighbor looks like: {seriesIndex: i, pointIndex:j, gridData:p, data:s.data[j]}
		// gridData should be x,y pixel coords on the grid.
		// add the plot._gridPadding to that to get x,y in the target.
		var hl = plot.plugins.highlighter;
		
		var elem = hl._tooltipElem;

		//var hl = $.extend(true, {}, hl);
		
		var str = 'Total count : ' + selected.length + ' item(s) selected';

		var tooltipContentProc = function(str) {
			var x, y;
			var innerElem = null;
			elem.emptyForce();
			if (typeof str !== 'object') {
				innerElem = $('<div></div>');
				innerElem.addClass('jqplot-highlighter-tooltip');
				elem.append(innerElem);
				innerElem.html(str);
			} else if (typeof str === 'object') {
				innerElem = str;
				elem.empty().append(str);
			}
			
			var gridpos = {
				x : neighbor.gridPos.x,
				y : neighbor.gridPos.y
			};
			var ms = 0;
			var fact = 0.707;

			var loc = locations;

			switch (loc[locationIndicies[hl.tooltipLocation]]) {
				case 'nw':
					x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) - hl.tooltipOffset - fact * ms;
					y = gridpos.y + plot._gridPadding.top - hl.tooltipOffset - elem.outerHeight(true) - fact * ms;
					break;
				case 'n':
					x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) / 2;
					y = gridpos.y + plot._gridPadding.top - hl.tooltipOffset - elem.outerHeight(true) - ms;
					break;
				case 'ne':
					x = gridpos.x + plot._gridPadding.left + hl.tooltipOffset + fact * ms;
					y = gridpos.y + plot._gridPadding.top - hl.tooltipOffset - elem.outerHeight(true) - fact * ms;
					break;
				case 'e':
					x = gridpos.x + plot._gridPadding.left + hl.tooltipOffset + ms;
					y = gridpos.y + plot._gridPadding.top - elem.outerHeight(true) / 2;
					break;
				case 'se':
					x = gridpos.x + plot._gridPadding.left + hl.tooltipOffset - tooltipPaddingSize + fact * ms;
					y = gridpos.y + plot._gridPadding.top + hl.tooltipOffset + fact * ms;
					break;
				case 's':
					x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) / 2;
					y = gridpos.y + plot._gridPadding.top + hl.tooltipOffset + ms;
					break;
				case 'sw':
					x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) - hl.tooltipOffset - fact * ms;
					y = gridpos.y + plot._gridPadding.top + hl.tooltipOffset - tooltipPaddingSize + fact * ms;
					break;
				case 'w':
					x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) - hl.tooltipOffset - ms;
					y = gridpos.y + plot._gridPadding.top - elem.outerHeight(true) / 2;
					break;
				default:
					// same as 'nw'
					x = gridpos.x + plot._gridPadding.left - elem.outerWidth(true) - hl.tooltipOffset - fact * ms;
					y = gridpos.y + plot._gridPadding.top - hl.tooltipOffset - elem.outerHeight(true) - fact * ms;
					break;
			}
			
			var highlightCanvasElem = hl.highlightCanvas._elem;
			var highlightCanvasOffset = highlightCanvasElem.offset();

			if (gridpos.x < 0 || gridpos.x > hl.highlightCanvas._ctx.canvas.width || gridpos.y < 0 || gridpos.y > hl.highlightCanvas._ctx.canvas.height) {
				x += ev.pageX - gridpos.x - highlightCanvasOffset.left;
				y += ev.pageY - gridpos.y - highlightCanvasOffset.top;
			}
			
			elem.css('left', x < 0 ? 0 : x + elem.width() > plot.target.width() ? plot.target.width() - elem.width() : x);
			elem.css('top', y < 0 ? 0 : y + elem.height() > plot.target.height() ? plot.target.height() - elem.height() : y);

			var gridstop = [x, y];

			elem.unbind('drag');
			
			elem.draggable();
			elem.draggable('destroy');

			elem.css({
				cursor : 'default'
			});

			if (hl.fadeTooltip) {
				// Fix for stacked up animations.  Thnanks Trevor!
				elem.stop(true, true).fadeIn(hl.tooltipFadeSpeed);
			} else {
				elem.show();
			}
			elem = null;
		};
		if ($.isFunction(hl.selectable.tooltipContentEditor)) {
			// args str, seriesIndex, pointIndex are essential so the hook can look up
			// extra data for the point.
			str = hl.selectable.tooltipContentEditor.call(hl, hl.selectable._selected, oldSelected, neighbor, plot, tooltipContentProc);
		} else {
			tooltipContentProc(str);
		}

	}

	function handleMove(ev, gridpos, datapos, neighbor, plot) {// customizing (2012-04-19, Roy Choi)
		var hl = plot.plugins.highlighter;
		var c = plot.plugins.cursor;
		if (hl.pointCrossLine) {
			moveCrossLine(ev, gridpos, datapos, plot);
		} else if (hl.show) {
			if (hl.pointVariation && hl.pointVariationStart) {
				handleVariationMove(ev, gridpos, datapos, plot);
			} else if (hl.selectable && hl.selectable.show && hl.selectable.selecting && hl.selectable.tooltipOpend) {
				setTimeout(function() {
					hl.selectable.selecting = false;
				}, 500);
			} else if (hl.overTooltip && !hl.isTooltipDragging && !hl.selectable.selecting) {
				// customizing (2012-04-30, Roy Choi)
				if (!hl.selectable.show && neighbor === null && hl.overTooltipOptions.lineOver) {
					neighbor = isLineOver(gridpos, plot);
				}
				
				if (neighbor && !plot.series[neighbor.seriesIndex].highlight) {
						neighbor = null;
				}
				
				if (neighbor == null) {
					if (!hl.isClickTooltip && !hl.selectable.show && hl.overTooltipOptions.showMarker) {
						var ctx = hl.highlightCanvas._ctx;
						ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
					}
					if (hl.fadeTooltip) {
						if (!hl.isMultiTooltip && !hl.isClickTooltip) {
							hideTooltipLine(plot);
							fadeoutTooltip(plot);
						}
					} else if (!hl.isMultiTooltip && !hl.isClickTooltip) {
						hideTooltipLine(plot);
						hideTooltip(plot);
					}
					hl.currentOverNeighbor = null;
					ctx = null;
				} else if (neighbor != null && !hl.currentOverNeighbor) {
					if (hl.showTooltip && (!c || !c._zoom.started)) {
						if (!hl.isMultiTooltip && (hl.overTooltipOptions.showMarker || hl.overTooltipOptions.showTooltip)) {
              hideTooltipLine(plot);
              hl.isClickTooltip = false;
              hl.currentOverNeighbor = neighbor;
              
							if (!hl.selectable.show && hl.overTooltipOptions.showMarker) {
                var ctx = hl.highlightCanvas._ctx;
                ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
                draw(plot, neighbor);
							}

							if (hl.overTooltipOptions.showTooltip) {
								showTooltip(ev, plot, plot.series[neighbor.seriesIndex], neighbor, true);
							}
						}
					}
				}
			}
		}
	}

	function harfSearchArray(x, startValue, endValue, start, end) {
		if (x > (startValue - endValue) / 2 + startValue) {
			start += Math.floor(end / 2);
		} else {
			end -= Math.floor(end / 2);
		}
	}

	function searchGridData(series, gridpos) {
		var grid_data = series.gridData;
		var sr_data = series.data;
		var x = gridpos.x;

		var start = 0;
		var end = grid_data.length - 1;

		if (x <= grid_data[start][0])
			return {
				pointIndex : start,
				gridData : grid_data[start],
				data : sr_data[start]
			};
		if (x >= grid_data[end][0])
			return {
				pointIndex : end,
				gridData : grid_data[end],
				data : sr_data[end]
			};

		// harf search占쏙옙 占쌍댐옙 10占쏙옙占쏙옙占쏙옙占�占쏙옙占쏙옙占싼댐옙.
		for (var i = 0; i < 10; i++) {
			if (end - start <= 4)
				break;
			harfSearchArray(x, grid_data[start][0], grid_data[end][0], start, end);
		}

		for (var k = start; k <= end; k++) {
			if (end > k) {
				var p = grid_data[k];
				var p1 = grid_data[k + 1];
				if (x > p[0] && x < p1[0]) {
					var ret_data;
					if (Math.abs(p[0] - x) > Math.abs(p1[0] - x)) {
						k++;
						p = p1;
					}
					var ret_data = sr_data[k];
					grid_data = null;
					sr_data = null;
					return {
						pointIndex : k,
						gridData : p,
						data : ret_data
					};
				}
			} else {
				var p = grid_data[k];
				var ret_data = sr_data[k];
				grid_data = null;
				sr_data = null;
				return {
					pointIndex : k,
					gridData : p,
					data : ret_data
				};
			}
		}

	}

	function moveCrossLine(ev, gridpos, datapos, plot) {// customizing (2012-05-03, Roy Choi) - Point Cross Line draw on mouse move
		var hl = plot.plugins.highlighter;

		if (hl.pointCrossLine && hl.pointCrossLineStart && !isNaN(hl.pointCrossLineSeriesIndex)) {
			var sr = plot.series[hl.pointCrossLineSeriesIndex];
			var sr_index = hl.pointCrossLineSeriesIndex;
			var x = gridpos.x;

			var neighbor = searchGridData(sr, gridpos);
			$.extend(neighbor, {
				seriesIndex : sr_index
			});

			var ctx = hl.highlightCanvas._ctx;
			ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
			ctx = null;
			hideTooltipLine(plot);
			draw(plot, neighbor);
			showTooltip(ev, plot, sr, neighbor);

			var c = plot.plugins.cursor;
			var ctx = hl.pointCrossLineCanvas._ctx;
			ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
			var options = {
				lineWidth : 1,
				color : '#0000ff'
			};
			c.shapeRenderer.draw(ctx, [[neighbor.gridData[0], 0], [neighbor.gridData[0], ctx.canvas.height]], options);
			c.shapeRenderer.draw(ctx, [[0, neighbor.gridData[1]], [ctx.canvas.width, neighbor.gridData[1]]], options);
			ctx = null;
			options = null;
		}
	}


	$.jqplot.Highlighter.prototype.isLineOver = function(gridpos, plot) {// customizing (2012-05-14, Roy Choi)
		return isLineOver(gridpos, plot);
	};

	function isLineOver(gridpos, plot) {// customizing (2012-06-01, Roy Choi)
		var hl = plot.plugins.highlighter;
		var seriesStack = plot.seriesStack;
		var point_color;
		
		var dMin0, dMax0;

		if (plot.options.multiCanvas) {
			for (var si = 0, sl = seriesStack.length; si < sl; si++) {
				var i = seriesStack[si];
				var sr = plot.series[i];
				
				if (sr.hide || !sr.show || !sr.highlight)
					continue;
				
				if (sr.renderer.constructor === $.jqplot.LineRenderer && sr.showLine) {
					var lineWidth = sr.lineWidth < hl.pointVariationThreshold ? hl.pointVariationThreshold : Math.ceil(sr.lineWidth);
					var fix = Math.floor(lineWidth / 2);
					var left = gridpos.x - fix;
					var top = gridpos.y - fix;
					var imgd = sr.canvas._ctx.getImageData(left, top, lineWidth, lineWidth);
					var data = imgd.data;
					for (var j = 0; j < data.length; j += 4) {
						point_color = $.jqplot.rgb2hex($.jqplot.normalize2rgb("rgb(" + data[j] + "," + data[j + 1] + "," + data[j + 2] + ")"));
						//if(point_color == sr_color) {
						if (point_color != '#000000') {
							var sr_index = sr.index;
							var x = gridpos.x;
							for (var k = 0, grid_len = sr.gridData.length; k < grid_len; k++) {
								if (grid_len > k) {
									var p = sr.gridData[k];
									var p1 = sr.gridData[k + 1];
									if (x > p[0] && x < p1[0]) {
										/*if(Math.abs(p[0]-x) > Math.abs(p1[0]-x)) {
										 k++;
										 p = p1;
										 }*/
										var ret_data = sr.data[k];
										data = null;
										sr = null;
										return {
											seriesIndex : sr_index,
											pointIndex : k,
											gridData : p,
											data : ret_data
										};
									}
								} else {
									var p = sr.gridData[k];
									var ret_data = sr.data[k];
									data = null;
									sr = null;
									return {
										seriesIndex : sr_index,
										pointIndex : k,
										gridData : p,
										data : ret_data
									};
								}
							}
						}
					}
					
					data = null;
				}  else if (sr.renderer.constructor === $.jqplot.BoxplotRenderer) {
					var x = gridpos.x;
					var y = gridpos.y;
					var sdata = sr.data, data;
					var boxPoints = sr._boxPoints;
			
			    var dMin, dMax, gridData, maxY, points;
			    var t = sr.markerRenderer.size/2+sr.neighborThreshold;
			    var threshold = (t > 0) ? t : 0;
			    var yp = sr._yaxis.series_u2p;
			    
			    for (var j=0, l=sdata.length; j<l; j++) {
			        gridData = sr.gridData[j];
			        data = sdata[j];
			
			        dMin = Math.sqrt( (x-gridData[0]) * (x-gridData[0]) + (y-gridData[1]) * (y-gridData[1]) );
			        if (dMin <= threshold && (dMin <= dMin0 || dMin0 == null)) {
			           dMin0 = dMin;
			           return {seriesIndex: i, pointIndex:j, gridData:gridData, data:data};
			        }
			        
			        maxY = yp(data[5]);
			        dMax = Math.sqrt( (x-gridData[0]) * (x-gridData[0]) + (y-maxY) * (y-maxY) );
			        if (dMax <= threshold && (dMax <= dMax0 || dMax0 == null)) {
			           dMax0 = dMax;
			           return {seriesIndex: i, pointIndex:j, gridData:[gridData[0], maxY], data:data, start:[data[0], data[5]]};
			        }
			        
			        points = sr._boxPoints[j];
			        if (x>points[0][0] && x<points[2][0] && y<points[2][1] && y>points[0][1]) {
                 return {seriesIndex:i, pointIndex:j, gridData:[x, y], data:data, points:points};
              }

			        data = gridData = null;
			    }
					sdata = boxPoints = null;
				}

				sr = null;
				
			}
			return null;
		} else {
			var x = gridpos.x;
			var y = gridpos.y;
			var sr, sd, pd, nd, ret_data;
			var tanD, tanYP;

			try {
				for (var si = seriesStack.length - 1; si >= 0; si--) {
					var i = seriesStack[si];
					sr = plot.series[i];
					
					if (sr.hide || !sr.show)
						continue;
						
					if (sr.renderer.constructor === $.jqplot.LineRenderer && sr.showLine && !sr.renderer.smooth) {
						var lineWidth = sr.lineWidth < hl.pointVariationThreshold ? hl.pointVariationThreshold : Math.ceil(sr.lineWidth);
						var fix = lineWidth / 2;
						var top = y + fix;
						var bottom = y - fix;
						var left = x - fix;
						var right = x + fix;
						
						var equation, leftEq, rightEq, topEq, bottomEq;
						var prevX, prevY, nextX, nextY, tempAdd;
						
	
						sd = sr.gridData;
	
						for (var j = 1, sdlen = sd.length; j < sdlen; j++) {
							pd = sd[j - 1];
							nd = sd[j];
							
							if (pd[0] > nd[0]) {
								prevX = nd[0];
								prevY = nd[1];
								nextX = pd[0];
								nextY = pd[1];
							} else {
								prevX = pd[0];
								prevY = pd[1];
								nextX = nd[0];
								nextY = nd[1];
							}
							
							if (x >= prevX && x <= nextX && ((y >= prevY && y <= nextY) || (y <= prevY && y >= nextY))) {
								if (!$.isNumeric(prevX) || !$.isNumeric(nextX) || !$.isNumeric(prevY) || !$.isNumeric(nextY) ||
								  (sr.breakOnDiff !== null && sr.data[j-1][sr.breakOnDiff] !== sr.data[j][sr.breakOnDiff])) {
									return null;
								}
								/*if (nextX - prevX <= hl.pointVariationThreshold && ((nextY >= prevY && y <= nextY && y >= prevY) || (nextY <= prevY && y >= nextY && y <= prevY))) {
									ret_data = sr.data[j - 1];
									return {
										seriesIndex : i,
										pointIndex : j - 1,
										gridData : sd[j - 1],
										data : ret_data
									};
								}*/
								
								equation = (nextY - prevY) / (nextX - prevX);
								
								if (left < prevX) {
									tempAdd = prevX - left + 0.1;
									prevX -= tempAdd;
									nextX -= tempAdd;
								} else if (left == prevX) {
									tempAdd = 0.1;
									prevX -= tempAdd;
									nextX -= tempAdd;
								}
								
								leftEq = (y - prevY) / (left - prevX);
								rightEq = (y - prevY) / (right - prevX);
								topEq = (top - prevY) / (x - prevX);
								bottomEq = (bottom - prevY) / (x - prevX);
								
								if ((((leftEq < rightEq && leftEq <= equation && rightEq >= equation) || (leftEq >= rightEq && leftEq >= equation && rightEq <= equation))) ||
										(((bottomEq < topEq && bottomEq <= equation && topEq >= equation) || (bottomEq > topEq && bottomEq >= equation && topEq <= equation)))) {
									return {
										seriesIndex : i,
										pointIndex : j - 1,
										gridData : sd[j - 1],
										data : sr.data[j - 1]
									};
								}
							}
	
						}
					} else if (sr.renderer.constructor === $.jqplot.BoxplotRenderer) {
						var x = gridpos.x;
						var y = gridpos.y;
						var sdata = sr.data, data;
						var boxPoints = sr._boxPoints;
				
				    var dMin, dMax, gridData, maxY, points;
				    var t = sr.markerRenderer.size/2+sr.neighborThreshold;
				    var threshold = (t > 0) ? t : 0;
				    var yp = sr._yaxis.series_u2p;
				    
				    for (var j=0, l=sdata.length; j<l; j++) {
				        gridData = sr.gridData[j];
				        data = sdata[j];
				
				        dMin = Math.sqrt( (x-gridData[0]) * (x-gridData[0]) + (y-gridData[1]) * (y-gridData[1]) );
				        if (dMin <= threshold && (dMin <= dMin0 || dMin0 == null)) {
				           dMin0 = dMin;
				           return {seriesIndex: i, pointIndex:j, gridData:gridData, data:data};
				        }
				        
				        maxY = yp(data[5]);
				        dMax = Math.sqrt( (x-gridData[0]) * (x-gridData[0]) + (y-maxY) * (y-maxY) );
				        if (dMax <= threshold && (dMax <= dMax0 || dMax0 == null)) {
				           dMax0 = dMax;
				           return {seriesIndex: i, pointIndex:j, gridData:[gridData[0], maxY], data:data, start:[data[0], data[5]]};
				        }
				        
				        points = sr._boxPoints[j];
				        if (x>points[0][0] && x<points[2][0] && y<points[2][1] && y>points[0][1]) {
	                 return {seriesIndex:i, pointIndex:j, gridData:[x, y], data:data, points:points};
	              }
	
				        data = gridData = null;
				    }
						sdata = boxPoints = null;
					}

				}
				return null;
			} finally {
				x = y = hl = seriesStack = ret = point_color = null;
				sr = sd = pd = nd = ret_data = null;
			}
		}
	}

	function handleVariationMove(ev, gridpos, datapos, plot) {
		var hl = plot.plugins.highlighter;
		var co = plot.plugins.canvasOverlay;

		if (hl.pointVariationStart || hl.pointVariationEndPos != null) {
			var x_interval = $.jqplot.DateTickFormatter('%H:%M:%S:%#N', datapos.xaxis - hl.pointVariationStartPos.x);
			var y_interval = Math.floor((datapos.yaxis - hl.pointVariationStartPos.y) * 100) / 100;
			hl.pointVariationTooltipElem.offset({
				top : ev.pageY,
				left : ev.pageX
			}).html("x:" + x_interval + "<br />y:" + y_interval).show();
			obj = co.get(hl.pointVariationLineName);
			var opts = {
				show : true,
				lineWidth : hl.pointVariationLineWidth,
				color : hl.pointVariationLineColor,
				start : [hl.pointVariationStartPos.x, hl.pointVariationStartPos.y],
				stop : [datapos.xaxis, datapos.yaxis]
			};
			$.extend(true, obj.options, opts);
			co.draw(plot);
		}
	}
	
	function seriesFadeOut(plot, opacity) {
		$('.jqplot-series-canvas',plot.target).each(function() {
			$(this).fadeTo(0, $.isNumeric(opacity) ? opacity : .2);
		});
	}
	
	function seriesFadeIn(plot) {
		$('.jqplot-series-canvas',plot.target).each(function() {
			$(this).fadeTo(0, 1.0);
		});
	}

	function handleMouseDown(ev, gridpos, datapos, neighbor, plot) {// customizing (2012-04-19, Roy Choi)
		var hl = plot.plugins.highlighter;
		var c = plot.plugins.cursor;
		var co = plot.plugins.canvasOverlay;
		if (hl.show) {
			if (neighbor == null) {
				neighbor = isLineOver(gridpos, plot);
			} else if (plot.series[neighbor.seriesIndex].hide || !plot.series[neighbor.seriesIndex].show) {
				neighbor = null;
			}
			if (hl.pointVariation) {
				if (neighbor) {
					if (hl.pointVariationStart) {
						hl.pointVariationStart = false;
						hl.pointVariationEndPos = {
							x : neighbor.data[0],
							y : neighbor.data[1]
						};
						handleVariationMove(ev, gridpos, {
							xaxis : neighbor.data[0],
							yaxis : neighbor.data[1]
						}, plot);
					} else {
						clearVariation(plot);
						hl.pointVariationStartPos = {
							x : neighbor.data[0],
							y : neighbor.data[1]
						};
						hl.pointVariationStart = true;
						handleVariationMove(ev, gridpos, {
							xaxis : neighbor.data[0],
							yaxis : neighbor.data[1]
						}, plot);
					}
				}
			} else if (hl.pointCrossLine) {
				hl.isClickTooltip = false;
				if (neighbor) {
					hl.pointCrossLineStart = true;
					hl.pointCrossLineSeriesIndex = neighbor.seriesIndex;
					if (hl.bringSeriesToFront && !plot.series[neighbor.seriesIndex].highlighted) {
						plot.moveSeriesToFront(neighbor.seriesIndex);
					}
					moveCrossLine.call(this, ev, gridpos, datapos, plot);
				} else {
					hl.pointCrossLineStart = false;
					hl.pointCrossLineSeriesIndex = null;
					//var c = plot.plugins.cursor;
					hl.clearCrossLine(plot);
				}
			} else if (hl.selectable.show && hl.selectable.multiSelect) {
				hl.selectable.start = gridpos;
				hl.selectable.selecting = true;
				hl.selectable.dragging = false;
				
				// TODO: modify window object
				if (window && window.event && hl.selectable.ctrlRemove) {
					hl.selectable.removing = window.event.ctrlKey;
				}
				
				$(document).unbind('mouseup', {plot: plot}, handleSelectStop);
				
				$(document).bind('mousemove', {plot: plot}, handleSelectDrag);
				$(document).bind('mouseup', {plot: plot}, handleSelectStop);
			}
		}
	}
	
	function isSelected(plot, conditionData) {
	  var hl = plot.plugins.highlighter;
	  
	  return $.inArray(conditionData, hl.selectable._selected) > -1;
	}
	
	function drawSelectBox(start, end) {
		var ctx = this.canvas._ctx;
		
		ctx.save();
		
		ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		
		var l, t, h, w;
		if (end[0] > start[0]) {
			l = start[0];
			w = end[0] - start[0];
		} else {
			l = end[0];
			w = start[0] - end[0];
		}
		if (end[1] > start[1]) {
			t = start[1];
			h = end[1] - start[1];
		} else {
			t = end[1];
			h = start[1] - end[1];
		}
		ctx.fillStyle = 'rgba(42,127,213,0.2)';
		ctx.strokeStyle = 'rgb(42,127,213)';
		ctx.lineWidth = 1.0;
		
		//ctx.fillRect(l,t,w,h);
		ctx.strokeRect(l,t,w,h);
		ctx.fillRect(l,t,w,h);
		// IE won't show transparent fill rect, so stroke a rect also.
		//ctx.strokeRect(l,t,w,h);
		
		ctx.restore();
		ctx = null;
	}
	
	function handleSelectDrag(ev) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		
		var plot = ev.data.plot;
		var hl = plot.plugins.highlighter;
		
		var positions = plot.getEventPosition(ev);
		var gridpos = positions.gridPos;
		
		if (gridpos.x < 0) {
			gridpos.x = 0;
		} else if (gridpos.x >= hl.canvas._ctx.canvas.width) {
			gridpos.x = hl.canvas._ctx.canvas.width - 1;
		}
		if (gridpos.y < 0) {
			gridpos.y = 0;
		} else if (gridpos.y >= hl.canvas._ctx.canvas.height) {
			gridpos.y = hl.canvas._ctx.canvas.height - 1;
		}

		if (Math.abs(gridpos.x - hl.selectable.start.x) * Math.abs(gridpos.y - hl.selectable.start.y) < 5) {
		  return;
		}
		
		hl.selectable.dragging = true;
		
		var start = [hl.selectable.start.x, hl.selectable.start.y];
		var end = [gridpos.x, gridpos.y];
		
		drawSelectBox.call(hl, start, end);
	}
	
	function handleSelectStop(ev) {
		var plot = ev.data.plot;
		var hl = plot.plugins.highlighter;
		
    $(document).unbind('mousemove', handleSelectDrag);
		
    var ctx = hl.canvas._ctx;
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		
		if (hl.selectable.start && hl.selectable.selecting) {
      var position = plot.getEventPosition(ev);
      hl.selectable.stop = position.gridPos;
      
      if (hl.selectable.removing) {
        removeSelected(ev, plot, hl.selectable.start, hl.selectable.stop);
      } else {
        var neighbor = plot.checkIntersection(position.gridPos, plot);
        if (!hl.selectable.dragging && neighbor !== null && isSelected(plot, plot.series[neighbor.seriesIndex].data[neighbor.pointIndex][hl.selectable.selectColumnIndex])) {
          removeSelectedPoint(plot, plot.series[neighbor.seriesIndex].data[neighbor.pointIndex][hl.selectable.selectColumnIndex]);
        } else {
          highlightSelected(ev, plot, hl.selectable.start, hl.selectable.stop, position);
        }
      }
      
      hl.selectable.start = null;
      hl.selectable.stop = null;
      hl.selectable.dragging = false;
		}
	}
	
	function removeIntersect(source, target) {
		return $.grep(source, function(i)
    {
        return $.inArray(i, target) === -1;
    });
	}
	
	function removeSelectedPoint(plot, conditionData) {
    var hl = plot.plugins.highlighter;
    
    hl.selectable._selectedInfo = [];
    var oldSelected = hl.selectable._selected.concat([]);
    hl.selectable._selected.splice(hl.selectable._selected.indexOf(conditionData), 1);
    highlightByData(plot, hl.selectable._selected);
    
    if ($.isFunction(hl.selectable.selectChanged)) {
      hl.selectable.selectChanged(hl.selectable._selected, oldSelected);
    }
    
    hl.selectable.selecting = false;
	}
	
	function removeSelected(ev, plot, start, stop) {
		var hl = plot.plugins.highlighter;
		var series = plot.series;
		var seriesStack = plot.seriesStack;
		var removeCount = 0;
		var s, i, j;
		
		var minX, minY, maxX, maxY;
		var dataX, dataY;
		var series_xu2p, series_yu2p;
		var markerSize = 0;
		var removed = [];
		var condition;
		var columnIndex = hl.selectable.selectColumnIndex;
		var gridPos = {
			x: null,
			y: null
		};
		
		if (hl.selectable.unselect) {
			hl.selectable._selected = [];
			hl.selectable._selectedInfo = [];
		}
		
		if (!$.isNumeric(columnIndex)) {
			console.warn('selectColumnIndex is not numeric');
			return;
		}
		 
		search: {
			for (i = seriesStack.length - 1; i >= 0; i--) {
				s = series[seriesStack[i]];
				
				if (s.hide || !s.show) {
					continue;
				}
				
				if (s.showMarker) {
					markerSize = s.markerOptions.size / 2;
				}
	
				if (start.x < stop.x) {
					gridPos.x = stop.x;
					minX = start.x - markerSize;
					maxX = stop.x + markerSize;
				} else {
					gridPos.x = start.x;
					minX = stop.x - markerSize;
					maxX = start.x + markerSize;
				}
				if (start.y < stop.y) {
					gridPos.y = stop.y;
					minY = start.y - markerSize;
					maxY = stop.y + markerSize;
				} else {
					gridPos.y = start.y;
					minY = stop.y - markerSize;
					maxY = start.y + markerSize;
				}
				
				series_xu2p = s._xaxis.series_u2p;
				series_yu2p = s._yaxis.series_u2p;
	
				for (j = 0, slen = s.data.length; j < slen; j++) {
					dataX = series_xu2p(s.data[j][0]);
					dataY = series_yu2p(s.data[j][1]);
					condition = s.data[j][columnIndex];
					
					if (dataX >= minX && dataX <= maxX && dataY >= minY && dataY <= maxY) {
						if (!hl.selectable.unselect && $.inArray(condition, hl.selectable._selected) > -1) {
							removed.push(condition);
							
							if (!hl.selectable.dragging) {
								break search;
							}
						}
					}
				}
			}
		}
		
		if (removed.length > 0) {
			var oldSelected = hl.selectable._selected.concat([]);
			plot.options.highlighter.selectable._selected = hl.selectable._selected = removeIntersect(hl.selectable._selected, removed);
			
			hl.selectable._selectedInfo = [];
			highlightByData(plot, hl.selectable._selected);
			
			if ($.isFunction(hl.selectable.selectChanged)) {
				hl.selectable.selectChanged(hl.selectable._selected, oldSelected);
			}
		} else {
			if (hl.selectable.unselect) {
				if (hl.fadeTooltip) {
					hideTooltipLine(plot);
					fadeoutTooltip(plot);
				} else {
					hideTooltipLine(plot);
					hideTooltip(plot);
				}
				if (hl.bringSeriesToFront) {
					plot.restoreOriginalSeriesOrder();
				}
			}
			
			hl.selectable.selecting = false;
		}

		snapshotData = null;
		pushed_count = null;
	}
	
	function highlightByData(plot, selected) {
		var hl = plot.plugins.highlighter;
		
		hl.selectable._selectedInfo = [];

    var ctx = hl.highlightCanvas._ctx;
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);

		if (selected.length === 0) {
			plot.restoreOriginalSeriesOrder();
			return;
		}
		
		var snapshotData = [];
		var series = plot.series;
		var pushed_count = 0;
		var seriesData = [], pointData = [], gridData = [];
		var s, i, j;
		
		var series_xu2p, series_yu2p;
		var dataX, dataY;
		var markerSize = 0;
		var condition;
		var columnIndex = hl.selectable.selectColumnIndex;
		
		if (!$.isNumeric(columnIndex)) {
			console.warn('selectColumnIndex is not numeric');
			return;
		}
		 
		for (i = 0, len = series.length; i < len; i++) {
			seriesData = [];
			pointData = [];
			gridData = [];
			s = series[i];
			
			if (s.hide || !s.show) {
				continue;
			}
			
			if (s.showMarker) {
				markerSize = s.markerOptions.size / 2;
			}
			
			series_xu2p = s._xaxis.series_u2p;
			series_yu2p = s._yaxis.series_u2p;

			for (j = 0, slen = s.data.length; j < slen; j++) {
				condition = s.data[j][columnIndex];
				
				if ($.inArray(condition, selected) > -1) {
					dataX = series_xu2p(s.data[j][0]);
					dataY = series_yu2p(s.data[j][1]);
					seriesData.push(s.data[j]);
					pointData.push(j);
					gridData.push([dataX, dataY]);
				}
			}

			if (seriesData.length > 0) {
				snapshotData.push({
					data : seriesData,
					gridData: gridData,
					pointIndexes : pointData,
					seriesIndex : i,
					label : series[i].label
				});
			}
		}
		
		hl.selectable._selectedInfo = snapshotData;
		
		seriesFadeOut(plot, hl.selectable.seriesFadeOut);
		drawMarkers(plot, hl.selectable._selectedInfo);

		snapshotData = null;
		pushed_count = null;
	}
	
	function highlightSelected(ev, plot, start, stop, neighbor) {
		var hl = plot.plugins.highlighter;
		var snapshotData = [];
		var series = plot.series;
		var seriesStack = plot.seriesStack;
		var pushed_count = 0;
		var seriesData = [], pointData = [], gridData = [];
		var s, i, j;
		
		var minX, minY, maxX, maxY;
		var dataX, dataY;
		var series_xu2p, series_yu2p;
		var markerSize = 0;
		var selected = [];
		var condition;
		var columnIndex = hl.selectable.selectColumnIndex;
		var gridPos = {
			x: null,
			y: null
		};
		
		if (hl.selectable.unselect) {
			hl.selectable._selected = [];
			hl.selectable._selectedInfo = [];
		}
		
		if (!$.isNumeric(columnIndex)) {
			console.warn('selectColumnIndex is not numeric');
			return;
		}
		 
		search: {
			for (i = 0, l = seriesStack.length; i < l; i++) {
				seriesData = [];
				pointData = [];
				gridData = [];
				s = series[seriesStack[i]];
				
				if (s.hide || !s.show) {
					continue;
				}
				
				if (s.showMarker) {
					markerSize = s.markerOptions.size / 2;
				}
	
				if (start.x < stop.x) {
					gridPos.x = stop.x;
					minX = start.x - markerSize;
					maxX = stop.x + markerSize;
				} else {
					gridPos.x = start.x;
					minX = stop.x - markerSize;
					maxX = start.x + markerSize;
				}
				if (start.y < stop.y) {
					gridPos.y = stop.y;
					minY = start.y - markerSize;
					maxY = stop.y + markerSize;
				} else {
					gridPos.y = start.y;
					minY = stop.y - markerSize;
					maxY = start.y + markerSize;
				}
				
				series_xu2p = s._xaxis.series_u2p;
				series_yu2p = s._yaxis.series_u2p;
	
				for (j = 0, slen = s.data.length; j < slen; j++) {
					dataX = series_xu2p(s.data[j][0]);
					dataY = series_yu2p(s.data[j][1]);
					condition = s.data[j][columnIndex];
					
					if (dataX >= minX && dataX <= maxX && dataY >= minY && dataY <= maxY) {
						if (!hl.selectable.unselect && $.inArray(condition, hl.selectable._selected) > -1) {
							continue;
						}
						
						seriesData.push(s.data[j]);
						pointData.push(j);
						gridData.push([dataX, dataY]);
						selected.push(s.data[j][columnIndex]);
						if (!hl.selectable.dragging) {
							pushed_count += seriesData.length;
							
							snapshotData.push({
								data : seriesData,
								gridData: gridData,
								pointIndexes : pointData,
								seriesIndex : seriesStack[i],
								label : series[seriesStack[i]].label
							});
							
							break search;
						}
					}
				}
				
				pushed_count += seriesData.length;
				
				if (seriesData.length > 0) {
					snapshotData.push({
						data : seriesData,
						gridData: gridData,
						pointIndexes : pointData,
						seriesIndex : seriesStack[i],
						label : series[seriesStack[i]].label
					});
				}
			}
		}
		 

		
		if (pushed_count > 0) {
			var oldSelected = hl.selectable._selected.concat([]);
			plot.options.highlighter.selectable._selected = hl.selectable._selected = hl.selectable._selected.concat(selected);
			hl.selectable._selectedInfo = hl.selectable._selectedInfo.concat(snapshotData);
			
			seriesFadeOut(plot, hl.selectable.seriesFadeOut);
			drawMarkers(plot, hl.selectable._selectedInfo);
			if (hl.selectable.showTooltip && ev) {
				showSelectTooltip(ev, plot, hl.selectable._selected, oldSelected, snapshotData, $.extend(true, {}, neighbor, {gridPos: {x: stop.x, y: stop.y}}));
				hl.selectable.tooltipOpend = true;
			}
			
			if ($.isFunction(hl.selectable.selectChanged)) {
				hl.selectable.selectChanged(hl.selectable._selected, oldSelected);
			}
		} else {
			if (hl.selectable.unselect) {
				if (hl.fadeTooltip) {
					hideTooltipLine(plot);
					fadeoutTooltip(plot);
				} else {
					hideTooltipLine(plot);
					hideTooltip(plot);
				}
				if (hl.bringSeriesToFront) {
					plot.restoreOriginalSeriesOrder();
				}
			}
			
			hl.selectable.selecting = false;
		}

		snapshotData = null;
		pushed_count = null;
	}
	
	function drawMarkers(plot, selectedData) {
		var hl = plot.plugins.highlighter;
		
		var mr = hl.markerRenderer;
		var ctx = hl.highlightCanvas._ctx;
		
		ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		
		selectedData.forEach(function(seriesInfo) {
			var s = plot.series[seriesInfo.seriesIndex];
			
			if (s.hide || !s.show) {
				return;
			}
			
			var smr = s.markerRenderer;
			
      mr.style = smr.style;
      mr.lineWidth = smr.lineWidth + hl.lineWidthAdjust;
      mr.size = smr.size + hl.sizeAdjust;
      var rgba = $.jqplot.getColorComponents(smr.color);
      var newrgb = [rgba[0], rgba[1], rgba[2]];
      var alpha = (rgba[3] >= 0.6) ? rgba[3] * 0.6 : rgba[3] * (2 - rgba[3]);
      mr.color = 'rgba(' + newrgb[0] + ',' + newrgb[1] + ',' + newrgb[2] + ',' + alpha + ')';
      mr.shapeRenderer.color = mr.color;
      mr.stroke = hl.stroke;
      mr.strokeStyle = hl.strokeStyle;
      mr.init();
			
      seriesInfo.gridData.forEach(function(point, index) {
        if ($.isFunction(smr.fillStyleFilter)) {
          mr.shapeRenderer.fillStyle = smr.fillStyleFilter.call(s, s.data[index]);
        }
        
        mr.draw(point[0], point[1], ctx);
      });
		});
	}
	
	function handleClick(ev, gridpos, datapos, neighbor, plot) {// customizing (2014-10-20, Roy Choi)
		var hl = plot.plugins.highlighter;
		var c = plot.plugins.cursor;
		var co = plot.plugins.canvasOverlay;
		if (hl.show) {
			if (neighbor == null) {
				neighbor = isLineOver(gridpos, plot);
			} else if (plot.series[neighbor.seriesIndex].hide || !plot.series[neighbor.seriesIndex].show) {
				neighbor = null;
			}
			
			if (neighbor && !plot.series[neighbor.seriesIndex].highlight) {
				neighbor = null;
			}
			
			if (hl.clickTooltip && (!hl.selectable || !hl.selectable.show || !hl.selectable.selecting)) {
				if (neighbor == null && (hl.isClickHighlighting || $.isNumeric(plot.highlightSeriesIndex))) {
					var ctx = hl.highlightCanvas._ctx;
					ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
					if (hl.fadeTooltip) {
						if (hl.isMultiTooltip && hl.multiTooltipElem.length > 0) {
							//hl.clearMultiTooltip(plot);
						} else {
							hideTooltipLine(plot);
							fadeoutTooltip(plot);;
						}
					} else {
						if (hl.isMultiTooltip && hl.multiTooltipElem.length > 0) {
							//hl.clearMultiTooltip(plot);
						} else {
							hideTooltipLine(plot);
							hideTooltip(plot);
						}
					}
					if (hl.bringSeriesToFront) {
						plot.restoreOriginalSeriesOrder();
					}
					hl.isClickTooltip = false;
					hl.isClickHighlighting = false;
					hl.currentNeighbor = null;
					ctx = null;
				}
				else if (neighbor != null && plot.series[neighbor.seriesIndex].showHighlight && !hl.isClickHighlighting) {
					hl.isClickHighlighting = true;
					hl.currentNeighbor = neighbor;
					if (hl.showMarker) {
						draw(plot, neighbor);
					}
					if (hl.showTooltip && (!c || !c._zoom.started)) {
						hideTooltipLine(plot);
						showTooltip(ev, plot, plot.series[neighbor.seriesIndex], neighbor);
						hl.isClickTooltip = true;
					}
					if (hl.bringSeriesToFront && !plot.series[neighbor.seriesIndex].highlighted) {
						if (hl.highlightOnlyMarker) {
							seriesFadeOut(plot);
						} else {
							plot.moveSeriesToFront(neighbor.seriesIndex);
						}
					}
				}
				// check to see if we're highlighting the wrong point.
				else if (neighbor != null && hl.isClickHighlighting && hl.currentNeighbor != neighbor) {
					// highlighting the wrong point.
	
					// if new series allows highlighting, highlight new point.
					if (plot.series[neighbor.seriesIndex].showHighlight) {
						var ctx = hl.highlightCanvas._ctx;
						ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
						hl.isClickHighlighting = true;
						hl.currentNeighbor = neighbor;
						if (hl.showMarker) {
							draw(plot, neighbor);
						}
						if (hl.showTooltip && (!c || !c._zoom.started)) {
							hideTooltipLine(plot);
							showTooltip(ev, plot, plot.series[neighbor.seriesIndex], neighbor);
							hl.isClickTooltip = true;
						}
						if (hl.bringSeriesToFront && !plot.series[neighbor.seriesIndex].highlighted) {
							if (hl.highlightOnlyMarker) {
								seriesFadeOut(plot);
							} else {
								plot.moveSeriesToFront(neighbor.seriesIndex);
							}
						}
					}
				}
			}
		}
	}

	function handleRightClick(ev, gridpos, datapos, neighbor, plot) {// customizing (2012-05-02, Roy Choi)
		var hl = plot.plugins.highlighter;
		if (hl.contextMenu) {
			if (neighbor == null) {
				neighbor = isLineOver(gridpos, plot);
			} else if (plot.series[neighbor.seriesIndex].hide || !plot.series[neighbor.seriesIndex].show) {
				neighbor = null;
				neighbor = isLineOver(gridpos, plot);
			}

			/*hl.contextMenuElem.empty();
			 hl.contextMenuElem.html('');*/

			if (neighbor && typeof hl.contextMenuSeriesOptions == 'object' && Object.keys(hl.contextMenuSeriesOptions).length > 0) {
				if ( typeof hl.contextMenuSeriesOptions.items == 'function') {
					var items = hl.contextMenuSeriesOptions.items(ev, gridpos, datapos, neighbor, plot);

					hl.contextMenuSeriesOptions.seriesInfo = {
						plot : plot,
						seriesIndex : neighbor.seriesIndex
					};
					$.contextMenu('destroy', plot.targetId);
					$.contextMenu({
						selector : plot.targetId,
						zIndex : 1000,
						build : function($trigger, e) {
							return $.extend(true, {}, hl.contextMenuSeriesOptions, {
								items : items
							});
						}
					});

					$(plot.target).contextMenu({
						x : ev.pageX,
						y : ev.pageY
					});
				} else if ( typeof hl.contextMenuSeriesOptions.items == 'object' && Object.keys(hl.contextMenuSeriesOptions.items).length > 0) {
					hl.contextMenuSeriesOptions.seriesInfo = {
						plot : plot,
						seriesIndex : neighbor.seriesIndex
					};
					$.contextMenu('destroy', plot.targetId);
					$.contextMenu({
						selector : plot.targetId,
						zIndex : 1000,
						build : function($trigger, e) {
							return hl.contextMenuSeriesOptions;
						}
					});

					$(plot.target).contextMenu({
						x : ev.pageX,
						y : ev.pageY
					});

					/*ev.stopImmediatePropagation();
					 $(plot.target).contextmenu({});

					 var evt = jQuery.Event('contextmenu');
					 evt.pageX = ev.pageX;
					 evt.pageY = ev.pageY;
					 evt.data = {plot: plot, seriesIndex: ev.data.seriesIndex};
					 $(plot.target).contextmenu(hl.contextMenuSeriesOptions).trigger(evt);*/

				}
				/*var li = document.createElement('li');
				 $(li).addClass('jqplot-highlighter-contextmenu');
				 $(li).html('<span class="jqplot-highlighter-contextmenu-text">Hide Series</span>');
				 $(li).prepend('<span class="jqplot-highlighter-contextmenu-icon"></span>');
				 $(li).bind('click', {plot:plot,seriesIndex:neighbor.seriesIndex}, function(ev) {
				 var plot = ev.data.plot;
				 var hl = plot.plugins.highlighter;
				 plot.legend.renderer.toggleLegend(plot, ev.data.seriesIndex);
				 //$(plot.legend._elem.find('.jqplot-table-legend-label').get(ev.data.seriesIndex)).trigger('click');

				 hl.contextMenuElem.hide();
				 });
				 $(li).appendTo(hl.contextMenuElem);

				 li = null;*/
			} else {
				if (hl.contextMenuBackgroundOptions.items && Object.keys(hl.contextMenuBackgroundOptions.items).length > 0) {
					$.contextMenu('destroy', plot.targetId);
					$.contextMenu({
						selector : plot.targetId,
						zIndex : 1000,
						build : function($trigger, e) {
							return hl.contextMenuBackgroundOptions;
						}
					});

					$(plot.target).contextMenu({
						x : ev.pageX,
						y : ev.pageY
					});
				}

				/*ev.stopImmediatePropagation();
				$(plot.target).contextmenu({});

				var evt = jQuery.Event('contextmenu');
				evt.pageX = ev.pageX;
				evt.pageY = ev.pageY;
				$(plot.target).contextmenu(hl.contextMenuBackgroundOptions).trigger(evt);*/
				//$(plot.target).unbind('contextmenu');
				/*var menuList = hl.contextMenuBackgroundOptions;
				 $.each(menuList, function(ind, menu) {
				 var li = document.createElement('li');
				 $(li).addClass('jqplot-highlighter-contextmenu');
				 $(li).html('<span class="jqplot-highlighter-contextmenu-text">'+menu.text+'</span>');
				 if(menu.icon) {
				 $(li).append('<img src="'+menu.icon+'" class="jqplot-highlighter-contextmenu-icon" />');
				 }
				 //$(li).attr({id:menu.id});
				 if($.isArray(menu.events)) {
				 $.each(menu.events, function(eind, e){
				 $(li).bind(e.name, {plot:plot}, e.callback);
				 });
				 }
				 $(li).appendTo(hl.contextMenuElem);

				 li = null;
				 });*/
			}
			/*var height = hl.contextMenuElem.outerHeight();
			 var width = hl.contextMenuElem.outerWidth();

			 var wHeight = $(window).height();
			 var wWidth = $(window).width();

			 var sTop = $(document).scrollTop();
			 var sLeft = $(document).scrollLeft();

			 var x = (ev.pageX > (sLeft+wWidth-width)) ? sLeft+wWidth-width: ev.pageX;
			 var y = (ev.pageY > (sTop+wHeight-height)) ? sTop+wHeight-height: ev.pageY;

			 hl.contextMenuElem.show();
			 hl.contextMenuElem.offset({top:y, left:x});*/
		}
	}

	/*var rightClickBlur = function(ev) {				// customizing (2012-05-02, Roy Choi)
	 var hl = ev.data.plot.plugins.highlighter;
	 hl.contextMenuElem.hide();
	 }*/

	/*function handleMouseDown(ev, gridpos, datapos, neighbor, plot) {	// customizing (2012-04-19, Roy Choi)
	 var hl = plot.plugins.highlighter;
	 hl.clickStart = gridpos;
	 }*/

	function handleRightDown(ev, gridpos, datapos, neighbor, plot) {// customizing (2012-04-19, Roy Choi)
		var hl = ev.data.plot.plugins.highlighter;

		var ctx = hl.highlightCanvas._ctx;
		ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		if (hl.fadeTooltip) {
			hideTooltipLine(plot);
			fadeoutTooltip(plot);;
		} else {
			hideTooltipLine(plot);
			hideTooltip(plot);
		}
	}

	// customizing (2012-08-30, Roy Choi)
	function postSeriesRedraw() {
		var plot = this;
		var hl = plot.plugins.highlighter;
		var c = plot.plugins.cursor;
		var co = plot.plugins.canvasOverlay;
		var neighbor = hl.currentNeighbor;
		if (hl.show) {
			if (neighbor == null && typeof plot.highlightSeriesIndex == 'number') {
				var ctx = hl.highlightCanvas._ctx;
				ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
				if (hl.fadeTooltip) {
					if (hl.isMultiTooltip && hl.multiTooltipElem.length > 0) {
						//hl.clearMultiTooltip(plot);
					} else {
						hideTooltipLine(plot);
						fadeoutTooltip(plot);;
					}
				} else {
					if (hl.isMultiTooltip && hl.multiTooltipElem.length > 0) {
						//hl.clearMultiTooltip(plot);
					} else {
						hideTooltipLine(plot);
						hideTooltip(plot);
					}
				}
				if (hl.bringSeriesToFront && !$.isNumeric(plot.highlightSeriesIndex)) {
					plot.restoreOriginalSeriesOrder();
				}
				hl.isClickTooltip = false;
				hl.isClickHighlighting = false;
				hl.currentNeighbor = null;
				ctx = null;
			} else if (neighbor != null) {
				// highlighting the wrong point.

				// if new series allows highlighting, highlight new point.
				if (plot.series[neighbor.seriesIndex].showHighlight) {
					var ctx = hl.highlightCanvas._ctx;
					ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
					ctx = null;

					if (plot.highlightSeriesIndex === neighbor.seriesIndex) {
						if (hl.showMarker) {
							draw(plot, neighbor);
						}
						if (hl.showTooltip && (!c || !c._zoom.started)) {
							hl._tooltipElem.trigger('drag');
						}
					} else {
						if (hl.fadeTooltip) {
							if (hl.isMultiTooltip && hl.multiTooltipElem.length > 0) {
								//hl.clearMultiTooltip(plot);
							} else {
								hideTooltipLine(plot);
								fadeoutTooltip(plot);;
							}
						} else {
							if (hl.isMultiTooltip && hl.multiTooltipElem.length > 0) {
								//hl.clearMultiTooltip(plot);
							} else {
								hideTooltipLine(plot);
								hideTooltip(plot);
							}
						}
					}

				}
			}
			
			if (hl.selectable.show && hl.selectable._selected.length > 0) {
				highlightByData(plot, hl.selectable._selected);
			} 
		}
		plot = hl = co = c = null;
	}

	function postSeriesPointRemove(pointIndex, plot) {
		var series = this;
		var hl = plot.plugins.highlighter;

		if (hl.currentNeighbor != null && series.index == hl.currentNeighbor.seriesIndex) {
			if (pointIndex == hl.currentNeighbor.pointIndex) {
				hl.currentNeighbor = null;
			} else if (pointIndex < hl.currentNeighbor.pointIndex) {
				hl.currentNeighbor.pointIndex--;
				hl.currentNeighbor.gridData = series.gridData[hl.currentNeighbor.pointIndex];
			}
		}
		hl = series = null;
	}

	function handleRemoveHighlight(ev, plot) {
		if (!plot.options.multiCanvas) {
			var ctx = plot.seriesHighlightCanvas._ctx;
			ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
			ctx = null;
		}
		var hl = plot.plugins.highlighter;
		var ctx = hl.highlightCanvas._ctx;
		ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		if (hl.fadeTooltip) {
			if (hl.isMultiTooltip && hl.multiTooltipElem.length > 0) {
				//hl.clearMultiTooltip(plot);
			} else {
				hideTooltipLine(plot);
				fadeoutTooltip(plot);;
			}
		} else {
			if (hl.isMultiTooltip && hl.multiTooltipElem.length > 0) {
				//hl.clearMultiTooltip(plot);
			} else {
				hideTooltipLine(plot);
				hideTooltip(plot);
			}
		}
		hl.isClickTooltip = false;
		hl.isClickHighlighting = false;
		hl.currentNeighbor = null;
		ctx = null;
	}

	function handleVisibleChange(ev, plot, series) {
		var hl = plot.plugins.highlighter;
		if (hl.currentNeighbor && plot.series[hl.currentNeighbor.seriesIndex].hide) {
			hl.clearHighlight(plot);
			//handleRemoveHighlight(ev, plot);
		}
		if ($.isArray(hl.selectable._selected) && hl.selectable._selected.length > 0) {
			highlightByData(plot, hl.selectable._selected);
		}
	}


	$.jqplot.postRedrawSeriesHooks.push(postSeriesRedraw);
	$.jqplot.postRemoveSeriesPointHooks.push(postSeriesPointRemove);
})(jQuery);;/**
 * jqPlot
 * Pure JavaScript plotting plugin using jQuery
 *
 * Version: 1.0.8
 * Revision: 1250
 *
 * Copyright (c) 2009-2013 Chris Leonello
 * jqPlot is currently available for use in all personal or commercial projects 
 * under both the MIT (http://www.opensource.org/licenses/mit-license.php) and GPL 
 * version 2.0 (http://www.gnu.org/licenses/gpl-2.0.html) licenses. This means that you can 
 * choose the license that best suits your project and use it accordingly. 
 *
 * Although not required, the author would appreciate an email letting him 
 * know of any substantial use of jqPlot.  You can reach the author at: 
 * chris at jqplot dot com or see http://www.jqplot.com/info.php .
 *
 * If you are feeling kind and generous, consider supporting the project by
 * making a donation at: http://www.jqplot.com/donate.php .
 *
 * sprintf functions contained in jqplot.sprintf.js by Ash Searle:
 *
 *     version 2007.04.27
 *     author Ash Searle
 *     http://hexmen.com/blog/2007/03/printf-sprintf/
 *     http://hexmen.com/js/sprintf.js
 *     The author (Ash Searle) has placed this code in the public domain:
 *     "This code is unrestricted: you are free to use it however you like."
 * 
 */
(function($) {   
    $.jqplot.CategoryAxisRenderer.prototype.createTicks = function() {
        // we're are operating on an axis here
        var ticks = this._ticks;
        var userTicks = this.ticks;
        var name = this.name;
        // databounds were set on axis initialization.
        var db = this._dataBounds;
        var dim, interval;
        var min, max;
        var pos1, pos2;
        var tt, i;

        // if we already have ticks, use them.
        if (userTicks.length) {
            // adjust with blanks if we have groups
            if (this.groups > 1 && !this._grouped) {
                var l = userTicks.length;
                var skip = parseInt(l/this.groups, 10);
                var count = 0;
                for (var i=skip; i<l; i+=skip) {
                    userTicks.splice(i+count, 0, ' ');
                    count++;
                }
                this._grouped = true;
            }
            this.min = 0.5;
            this.max = userTicks.length + 0.5;
            var range = this.max - this.min;
            this.numberTicks = 2*userTicks.length + 1;
            for (i=0; i<userTicks.length; i++){
                tt = this.min + 2 * i * range / (this.numberTicks-1);
                // need a marker before and after the tick
                var t = new this.tickRenderer(this.tickOptions);
                t.showLabel = false;
                // t.showMark = true;
                t.setTick(tt, this.name);
                this._ticks.push(t);
                var t = new this.tickRenderer(this.tickOptions);
                t.label = userTicks[i];
                // t.showLabel = true;
                t.showMark = false;
                t.showGridline = false;
                t.setTick(tt+0.5, this.name);
                this._ticks.push(t);
            }
            // now add the last tick at the end
            var t = new this.tickRenderer(this.tickOptions);
            t.showLabel = false;
            // t.showMark = true;
            t.setTick(tt+1, this.name);
            this._ticks.push(t);
        }

        // we don't have any ticks yet, let's make some!
        else {
            if (name == 'xaxis' || name == 'x2axis') {
                dim = this._plotDimensions.width;
            }
            else {
                dim = this._plotDimensions.height;
            }
            
            // if min, max and number of ticks specified, user can't specify interval.
            if (this.min != null && this.max != null && this.numberTicks != null) {
                this.tickInterval = null;
            }
            
            // if max, min, and interval specified and interval won't fit, ignore interval.
            if (this.min != null && this.max != null && this.tickInterval != null) {
                if (parseInt((this.max-this.min)/this.tickInterval, 10) != (this.max-this.min)/this.tickInterval) {
                    this.tickInterval = null;
                }
            }
        
            // find out how many categories are in the lines and collect labels
            var labels = [];
            var numcats = 0;
            var min = (this.min != null) ? Math.ceil(this.min)-0.5 : 0.5;	// customizing
            var max, val;
            var isMerged = false;
            
            // customizing for zoom (2013-02-20, Roy Choi)
            max = (this.max != null) ? Math.floor(this.max, 10)+0.5 : null;
            
            for (var i=0; i<this._series.length; i++) {
                var s = this._series[i];
                for (var j=0; j<s.data.length; j++) {
                    if (this.name == 'xaxis' || this.name == 'x2axis') {
                        val = s.data[j][0];
                    }
                    else {
                        val = s.data[j][1];
                    }
                    if ($.inArray(val, labels) == -1) {
                        isMerged = true;
                        numcats += 1;      
                        labels.push(val);
                    }
                }
            }
            
            if (isMerged && this.sortMergedLabels) {
                if (typeof labels[0] == "string") {
                    labels.sort();
                } else {
                    labels.sort(function(a,b) { return a - b; });
                }
            }
            

            
            // keep a reference to these tick labels to use for redrawing plot (see bug #57)
            this.ticks = labels;
            
            // now bin the data values to the right lables.
            for (var i=0; i<this._series.length; i++) {
                var s = this._series[i];
                for (var j=0; j<s.data.length; j++) {
                    if (this.name == 'xaxis' || this.name == 'x2axis') {
                        val = s.data[j][0];
                    }
                    else {
                        val = s.data[j][1];
                    }
                    // for category axis, force the values into category bins.
                    // we should have the value in the label array now.
                    var idx = $.inArray(val, labels)+1;
                    if (this.name == 'xaxis' || this.name == 'x2axis') {
                        s.data[j][0] = idx;
                    }
                    else {
                        s.data[j][1] = idx;
                    }
                }
            }
            
            // customizing for zoom (2013-02-20, Roy Choi) start
            if(min != null && max != null && (parseInt(min, 10) > 0 || parseInt(max, 10) < labels.length)) {
            	var newLabels = [];
            	for(i=parseInt(min, 10); i<=parseInt(max, 10); i++) {
            		newLabels.push(labels[i]);
            	}
            	this.ticks = labels = newLabels;
            	numcats = labels.length-1;
            }
            
            // adjust with blanks if we have groups
            if (this.groups > 1 && !this._grouped) {
                var l = labels.length;
                var skip = parseInt(l/this.groups, 10);
                var count = 0;
                for (var i=skip; i<l; i+=skip+1) {
                    labels[i] = ' ';
                }
                this._grouped = true;
            }
        		
        		// customizing
            if(max == null) {
            	max = numcats + 0.5;
            }
            if (this.numberTicks == null) {
                this.numberTicks = 2*numcats + 1;
            }

            var range = max - min;
            this.min = min;
            this.max = max;
            var track = 0;
            
            // todo: adjust this so more ticks displayed.
            var maxVisibleTicks = parseInt(3+dim/10, 10);
            var skip = parseInt(numcats/maxVisibleTicks, 10);

            if (this.tickInterval == null) {

                this.tickInterval = range / (this.numberTicks-1);

            }
            // if tickInterval is specified, we will ignore any computed maximum.
            for (var i=0; i<this.numberTicks; i++){
                tt = this.min + i * this.tickInterval;
                var t = new this.tickRenderer(this.tickOptions);
                // if even tick, it isn't a category, it's a divider
                if (i/2 == parseInt(i/2, 10)) {
                    t.showLabel = false;
                    t.showMark = true;
                }
                else {
                    if (skip>0 && track<skip) {
                        t.showLabel = false;
                        track += 1;
                    }
                    else {
                        t.showLabel = true;
                        track = 0;
                    } 
                    t.label = t.formatter(t.formatString, labels[(i-1)/2]);
                    t.showMark = false;
                    t.showGridline = false;
                }
                t.setTick(tt, this.name);
                this._ticks.push(t);
            }
        }
        
    };
})(jQuery);;/**
 * jqPlot
 * Pure JavaScript plotting plugin using jQuery
 *
 * Version: 1.0.8
 * Revision: 1250
 *
 * Copyright (c) 2009-2013 Chris Leonello
 * jqPlot is currently available for use in all personal or commercial projects 
 * under both the MIT (http://www.opensource.org/licenses/mit-license.php) and GPL 
 * version 2.0 (http://www.gnu.org/licenses/gpl-2.0.html) licenses. This means that you can 
 * choose the license that best suits your project and use it accordingly. 
 *
 * Although not required, the author would appreciate an email letting him 
 * know of any substantial use of jqPlot.  You can reach the author at: 
 * chris at jqplot dot com or see http://www.jqplot.com/info.php .
 *
 * If you are feeling kind and generous, consider supporting the project by
 * making a donation at: http://www.jqplot.com/donate.php .
 *
 * sprintf functions contained in jqplot.sprintf.js by Ash Searle:
 *
 *     version 2007.04.27
 *     author Ash Searle
 *     http://hexmen.com/blog/2007/03/printf-sprintf/
 *     http://hexmen.com/js/sprintf.js
 *     The author (Ash Searle) has placed this code in the public domain:
 *     "This code is unrestricted: you are free to use it however you like."
 * 
 */
(function($) {
    // called with scope of legend.
    $.jqplot.EnhancedLegendRenderer.prototype.init = function(options) {
        // prop: numberRows
        // Maximum number of rows in the legend.  0 or null for unlimited.
        this.numberRows = null;
        // prop: numberColumns
        // Maximum number of columns in the legend.  0 or null for unlimited.
        this.numberColumns = null;
        // prop: seriesToggle
        // false to not enable series on/off toggling on the legend.
        // true or a fadein/fadeout speed (number of milliseconds or 'fast', 'normal', 'slow') 
        // to enable show/hide of series on click of legend item.
        this.seriesToggle = 'normal';
        // prop: disableIEFading
        // true to toggle series with a show/hide method only and not allow fading in/out.  
        // This is to overcome poor performance of fade in some versions of IE.
        this.disableIEFading = true;
        $.extend(true, this, options);

        /*if (this.seriesToggle) {
            $.jqplot.postDrawHooks.push(postDraw);
        }*/
    };

    // customizing Start (2012-05-03, Roy Choi) legend visible
 $.jqplot.EnhancedLegendRenderer.prototype.toggleLegend = function(plot, index) {
		var s = plot.series;
		if(index == undefined) {
			if(s.length > 0) {
				var show = false;
				for(var i=0, len=s.length; i<len; i++) {
					if(!s[i].hide) {
						show = true;
						break;
					}
				}
				
				for(var i=0, len=s.length; i<len; i++) {
					if(show && !s[i].hide) {
						s[i].hide = true;
					} else if(!show && s[i].hide) {
						s[i].hide = false;
					}
				}
				
				plot.series[0].toggleDisplay({data:{plot:plot, series:s}});
				
				/*if(plot.options.multiCanvas) {
					var show = false;
					for(var i=0, len=s.length; i<len; i++) {
						if(!s[i].hide) {
							show = true;
							break;
						}
					}
					
					if(plot.legend.show) {
						plot.legend._elem.find('.jqplot-table-legend-label').each(function(ind){
							if((show && !s[ind].hide) || (!show && s[ind].hide)) {
								$(this).trigger('click');
							}
						});
					} else {
						for(var i=0, len=s.length; i<len; i++) {
							if(show && !s[i].hide) {
								s[i].hide = true;
								s[i].toggleDisplay({data:{series:s[i]}});
							} else if(!show && s[i].hide) {
								s[i].hide = false;
								s[i].toggleDisplay({data:{series:s[i]}});
							}
						}
					}
				} else {
					var show = true;
					var xor = true;
					for(var i=0, len=s.length; i<len; i++) {
						if(i>0 && s[i-1].hide != s[i].hide) xor = false;
						if(!s[i].hide) {
							show = false;
						}
					}
					if(plot.legend.show) {
				        plot.legend._elem.find('.jqplot-table-legend-label').each(function(ind){
							if(show && s[ind].hide) {
								$(this).removeClass('jqplot-series-hidden');
					            $(this).next('.jqplot-table-legend-label').removeClass('jqplot-series-hidden');
					            $(this).prev('.jqplot-table-legend-swatch').removeClass('jqplot-series-hidden');
							} else if(!show && !s[ind].hide) {
								$(this).addClass('jqplot-series-hidden');
								$(this).next('.jqplot-table-legend-label').addClass('jqplot-series-hidden');
					            $(this).prev('.jqplot-table-legend-swatch').addClass('jqplot-series-hidden');
							}
						});
					}

					if(show) {
						for(var i=0, len=s.length; i<len; i++) {
							s[i].hide = false;
						}
						plot.drawSeries();
					} else {
						for(var i=0, len=s.length; i<len; i++) {
							s[i].hide = true;
						}
						var ctx = plot.seriesCanvas._ctx;
						ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
						ctx = null;
					}
				}*/
			}
		} else {
			plot.series[index].hide = true;
			plot.series[index].toggleDisplay({data:{plot:plot, series:plot.series[index]}});
			/*if(plot.legend.show) {
				$(plot.legend._elem.find('.jqplot-table-legend-label').get(index)).trigger('click');
			} else {
				s[index].hide = !s[index].hide;
				s[index].toggleDisplay({data:{plot:plot,index:index,series:s[index]}});			// customizing (2012-05-03, Roy Choi) for one canvas series
			}*/
		}
		s = null;
	};
    
    // called with scope of legend
    $.jqplot.EnhancedLegendRenderer.prototype.draw = function(offsets, plot) {
        var legend = this;
        if (this.show) {
            var series = this._series;
			var s;
            var ss = 'position:absolute;';
            ss += (this.background) ? 'background:'+this.background+';' : '';
            ss += (this.border) ? 'border:'+this.border+';' : '';
            ss += (this.fontSize) ? 'font-size:'+this.fontSize+';' : '';
            ss += (this.fontFamily) ? 'font-family:'+this.fontFamily+';' : '';
            ss += (this.textColor) ? 'color:'+this.textColor+';' : '';
            ss += (this.marginTop != null) ? 'margin-top:'+this.marginTop+';' : '';
            ss += (this.marginBottom != null) ? 'margin-bottom:'+this.marginBottom+';' : '';
            ss += (this.marginLeft != null) ? 'margin-left:'+this.marginLeft+';' : '';
            ss += (this.marginRight != null) ? 'margin-right:'+this.marginRight+';' : '';
						if(this._elem) {
							//this._elem.resizable('destroy');
							this._elem.unbind();
							this._elem.remove();
						}
            this._elem = $('<table class="jqplot-table-legend" style="'+ss+'"></table>');
            /*
            if (this.seriesToggle) {
                            this._elem.css('z-index', '3');
                        }*/
            
        
            var pad = false, 
                reverse = false,
                nr, nc;
            if (this.numberRows) {
                nr = this.numberRows;
                if (!this.numberColumns){
                    nc = Math.ceil(series.length/nr);
                }
                else{
                    nc = this.numberColumns;
                }
            }
            else if (this.numberColumns) {
                nc = this.numberColumns;
                nr = Math.ceil(series.length/this.numberColumns);
            }
            else {
                nr = series.length;
                nc = 1;
            }
                
            var i, j, tr, td1, td2, lt, rs, div, div0, div1, chk;
            var idx = 0;
            // check to see if we need to reverse
            for (i=series.length-1; i>=0; i--) {
                if (nc == 1 && series[i]._stack || series[i].renderer.constructor == $.jqplot.BezierCurveRenderer){
                    reverse = true;
                }
            }    
                
            for (i=0; i<nr; i++) {
                tr = $(document.createElement('tr'));
                tr.addClass('jqplot-table-legend');
                if (reverse){
                    tr.prependTo(this._elem);
                }
                else{
                    tr.appendTo(this._elem);
                }
                for (j=0; j<nc; j++) {
                    if (idx < series.length && series[idx].show && series[idx].showLabel){
                        s = series[idx];
                        tr.attr('id', 'jqplot_'+plot.target.attr('id')+'_enhanced_legend_'+s.label); 	//customizing for series visible (2013-03-13, Roy Choi)
                        lt = this.labels[idx] || s.label.toString();
                        if (lt) {
                            var color = s.color;
                            if (!reverse){
                                if (i>0){
                                    pad = true;
                                }
                                else{
                                    pad = false;
                                }
                            }
                            else{
                                if (i == nr -1){
                                    pad = false;
                                }
                                else{
                                    pad = true;
                                }
                            }
                            rs = (pad) ? this.rowSpacing : '0';

                            td1 = $(document.createElement('td'));
                            td1.attr('id', 'jqplot_'+plot.target.attr('id')+'_table_legend_td1_'+s.label);	//customizing for series visible (2013-03-13, Roy Choi)
                            td1.addClass('jqplot-table-legend jqplot-table-legend-swatch');
                            td1.css({textAlign: 'center', paddingTop: rs});
                            if(s.hide) td1.addClass('jqplot-series-hidden');
                            
                            /* modified by Boram - add checkbox, delete colorbox */
                            chk = $(document.createElement('input')).attr({type: 'checkbox', checked: true}).addClass('jqplot-table-legend-cbox');
                            td1.append(chk);                            

                            /*div0 = $(document.createElement('div'));
                            div0.addClass('jqplot-table-legend-swatch-outline');
                            div1 = $(document.createElement('div'));
                            div1.addClass('jqplot-table-legend-swatch');
							if(s.hide) div1.addClass('jqplot-series-hidden');

                            div1.css({backgroundColor: color, borderColor: color});

                            td1.append(div0.append(div1));*/

                            td2 = $(document.createElement('td'));
                            td2.attr('id', 'jqplot_'+plot.target.attr('id')+'_table_legend_td2_'+s.label);	//customizing for series visible (2013-03-13, Roy Choi)
                            td2.addClass('jqplot-table-legend jqplot-table-legend-label');
                            td2.css('paddingTop', rs);
                            td2.css('color', color);
							if(s.hide) td2.addClass('jqplot-series-hidden');
                    
                            // td1 = $('<td class="jqplot-table-legend" style="text-align:center;padding-top:'+rs+';">'+
                            //     '<div><div class="jqplot-table-legend-swatch" style="background-color:'+color+';border-color:'+color+';"></div>'+
                            //     '</div></td>');
                            // td2 = $('<td class="jqplot-table-legend" style="padding-top:'+rs+';"></td>');
                            if (this.escapeHtml){
                                td2.text(lt);
                                td2.attr('title', lt);
                            }
                            else {
                                td2.html(lt);
                                td2.attr('title', lt.replace(/(<([^>]+)>)/ig,""));
                            }
                            if (reverse) {
                                if (this.showLabels) {td2.prependTo(tr);}
                                if (this.showSwatches) {td1.prependTo(tr);}
                            }
                            else {
                                if (this.showSwatches) {td1.appendTo(tr);}
                                if (this.showLabels) {td2.appendTo(tr);}
                            }
                            
                            if (this.seriesToggle) {

                                // add an overlay for clicking series on/off
                                // div0 = $(document.createElement('div'));
                                // div0.addClass('jqplot-table-legend-overlay');
                                // div0.css({position:'relative', left:0, top:0, height:'100%', width:'100%'});
                                // tr.append(div0);

                                var speed;
                                if (typeof(this.seriesToggle) == 'string' || typeof(this.seriesToggle) == 'number') {
                                    if (!$.jqplot.use_excanvas || !this.disableIEFading) {
                                        speed = this.seriesToggle;
                                    }
                                } 
                                if (this.showSwatches) {
    	                        	chk.bind('change', {plot:plot, index:idx, series:s, speed:speed}, handleToggle);	// customizing (2012-05-03, Roy Choi) for one canvas series
    	                        	chk.addClass('jqplot-seriesToggle');
    	                        }
                                /*if (this.showSwatches) {
                                    td1.bind('click', {plot:plot, index:idx, series:s, speed:speed}, handleToggle);	// customizing (2012-05-03, Roy Choi) for one canvas series
                                    td1.addClass('jqplot-seriesToggle');
                                }*/
                                if (this.showLabels)  {
                                    td2.bind('click', {plot:plot, index:idx, series:s, speed:speed}, handleToggle);	// customizing (2012-05-03, Roy Choi) for one canvas series
                                    td2.addClass('jqplot-seriesToggle');
                                }
                            }
                            
                            pad = true;
                        }
                    }
                    idx++;
                }
                
                //td1 = td2 = div0 = div1 = null;   
                td1 = td2 = chk = null;   
            }
			// customizing Start (2012-05-03, Roy Choi) legend location
			var wrap = $('<div></div>');
			wrap.addClass('jqplot-table-legend-wrap');
			var elem = this._elem.detach();
			wrap.append(elem);
			elem = null;

			switch(this.location.charAt(this.location.length-1)) {
				case 'n':
					wrap.addClass('jqplot-table-legend-wrap-top');
					wrap.height(50);
					break;
				case 's':
					wrap.addClass('jqplot-table-legend-wrap-bottom');
					wrap.height(50);
					break;
				case 'w':
					wrap.addClass('jqplot-table-legend-wrap-left');
					wrap.height(plot.baseCanvas._elem.height()-plot._defaultGridPadding.top-plot._defaultGridPadding.bottom-2).width(98);
					wrap.offset({top: plot._defaultGridPadding.top});
					break;
				case 'e':
					wrap.addClass('jqplot-table-legend-wrap-right');
					wrap.height(plot.baseCanvas._elem.height()-plot._defaultGridPadding.top-plot._defaultGridPadding.bottom-2).width(98);
					wrap.offset({top: plot._defaultGridPadding.top});
					break;
			}
			
			//elem = resize_elem = null;

			if(this.width > 0) {
				wrap.width(this.width);
				this.width = 0;
			}
			if(this.height > 0) {
				wrap.height(this.height);
				this.height = 0;
			}

			this._elem = wrap;
			plot.baseCanvas._elem.after(this._elem);
			
			var offset = this._elem.offset();
			offset.left = Math.floor(offset.left);
			this._elem.offset(offset);
			offset = null;
			
			this._elem.bind('click', function(ev) {
				if(ev.target.type != 'checkbox') {
					plot.restorePreviousSeriesOrder();
		    	}
			});

			// customizing End (2012-05-03, Roy Choi) legend location
        }


        return this._elem;
    };
    
    $.jqplot.EnhancedLegendRenderer.prototype.addSeries = function(plot, series) {
        var pad = false, 
        reverse = false,
        nr, nc;
        
        if (this.show) {
        	if (typeof this._elem === 'undefined') {
        		var series = this._series;
    			var s;
                var ss = 'position:absolute;';
                ss += (this.background) ? 'background:'+this.background+';' : '';
                ss += (this.border) ? 'border:'+this.border+';' : '';
                ss += (this.fontSize) ? 'font-size:'+this.fontSize+';' : '';
                ss += (this.fontFamily) ? 'font-family:'+this.fontFamily+';' : '';
                ss += (this.textColor) ? 'color:'+this.textColor+';' : '';
                ss += (this.marginTop != null) ? 'margin-top:'+this.marginTop+';' : '';
                ss += (this.marginBottom != null) ? 'margin-bottom:'+this.marginBottom+';' : '';
                ss += (this.marginLeft != null) ? 'margin-left:'+this.marginLeft+';' : '';
                ss += (this.marginRight != null) ? 'margin-right:'+this.marginRight+';' : '';
    			if(this._elem) {
    				//this._elem.resizable('destroy');
    				this._elem.unbind();
    				this._elem.remove();
    			}
                this._elem = $('<table class="jqplot-table-legend" style="'+ss+'"></table>');
                /*
                if (this.seriesToggle) {
                                    this._elem.css('z-index', '3');
                                }*/
                
    	    }
        	
        	if (this.numberRows) {
    	        nr = this.numberRows;
    	        if (!this.numberColumns){
    	            nc = Math.ceil(series.length/nr);
    	        }
    	        else{
    	            nc = this.numberColumns;
    	        }
    	    }
    	    else if (this.numberColumns) {
    	        nc = this.numberColumns;
    	        nr = Math.ceil(series.length/this.numberColumns);
    	    }
    	    else {
    	        nr = series.length;
    	        nc = 1;
    	    }
    	        
    	    var i, j, tr, td1, td2, lt, rs, div, div0, div1;
    	    var idx = 0;
    	    // check to see if we need to reverse
    /*	    for (i=series.length-1; i>=0; i--) {
    	        if (nc == 1 && series[i]._stack || series[i].renderer.constructor == $.jqplot.BezierCurveRenderer){
    	            reverse = true;
    	        }
    	    }*/
    	    
    	    
    	    
    	    var elem = $('table',this._elem);
    	    for (i=0; i<nr; i++) {
    	        tr = $(document.createElement('tr'));
    	        tr.addClass('jqplot-table-legend');
    	        if (reverse){
    	            tr.prependTo(elem);
    	        }
    	        else{
    	            tr.appendTo(elem);
    	        }
    	        for (j=0; j<nc; j++) {
    	            if (idx < series.length && series[idx].show && series[idx].showLabel){
    	                s = series[idx];
    	                lt = this.labels[idx] || s.label.toString();
    	                if (lt) {
    	                    var color = s.color;
    	                    if (!reverse){
    	                        if (i>0){
    	                            pad = true;
    	                        }
    	                        else{
    	                            pad = false;
    	                        }
    	                    }
    	                    else{
    	                        if (i == nr -1){
    	                            pad = false;
    	                        }
    	                        else{
    	                            pad = true;
    	                        }
    	                    }
    	                    rs = (pad) ? this.rowSpacing : '0';
    	
                            td1 = $(document.createElement('td'));
                            td1.attr('id', 'jqplot_'+plot.target.attr('id')+'_table_legend_td1_'+s.label);	//customizing for series visible (2013-03-13, Roy Choi)
                            td1.addClass('jqplot-table-legend jqplot-table-legend-swatch');
                            td1.css({textAlign: 'center', paddingTop: rs});
                            if(s.hide) td1.addClass('jqplot-series-hidden');
                            
                            /* modified by Boram - add checkbox, delete colorbox */
                            chk = $(document.createElement('input')).attr({type: 'checkbox', checked: true}).addClass('jqplot-table-legend-cbox');
                            td1.append(chk);                            

                            /*div0 = $(document.createElement('div'));
                            div0.addClass('jqplot-table-legend-swatch-outline');
                            div1 = $(document.createElement('div'));
                            div1.addClass('jqplot-table-legend-swatch');
    						if(s.hide) div1.addClass('jqplot-series-hidden');

                            div1.css({backgroundColor: color, borderColor: color});

                            td1.append(div0.append(div1));*/

                            td2 = $(document.createElement('td'));
                            td2.attr('id', 'jqplot_'+plot.target.attr('id')+'_table_legend_td2_'+s.label);	//customizing for series visible (2013-03-13, Roy Choi)
                            td2.addClass('jqplot-table-legend jqplot-table-legend-label');
                            td2.css('paddingTop', rs);
                            td2.css('color', color);
    						if(s.hide) td2.addClass('jqplot-series-hidden');
                    
                            // td1 = $('<td class="jqplot-table-legend" style="text-align:center;padding-top:'+rs+';">'+
                            //     '<div><div class="jqplot-table-legend-swatch" style="background-color:'+color+';border-color:'+color+';"></div>'+
                            //     '</div></td>');
                            // td2 = $('<td class="jqplot-table-legend" style="padding-top:'+rs+';"></td>');
    						if (this.escapeHtml){
                                td2.text(lt);
                                td2.attr('title', lt);
                            }
                            else {
                                td2.html(lt);
                                td2.attr('title', lt.replace(/(<([^>]+)>)/ig,""));
                            }
    	                    if (reverse) {
    	                        if (this.showLabels) {td2.prependTo(tr);}
    	                        if (this.showSwatches) {td1.prependTo(tr);}
    	                    }
    	                    else {
    	                        if (this.showSwatches) {td1.appendTo(tr);}
    	                        if (this.showLabels) {td2.appendTo(tr);}
    	                    }
    	                    
    	                    if (this.seriesToggle) {
    	
    	                        // add an overlay for clicking series on/off
    	                        // div0 = $(document.createElement('div'));
    	                        // div0.addClass('jqplot-table-legend-overlay');
    	                        // div0.css({position:'relative', left:0, top:0, height:'100%', width:'100%'});
    	                        // tr.append(div0);
    	
    	                        var speed;
    	                        if (typeof(this.seriesToggle) == 'string' || typeof(this.seriesToggle) == 'number') {
    	                            if (!$.jqplot.use_excanvas || !this.disableIEFading) {
    	                                speed = this.seriesToggle;
    	                            }
    	                        } 
    	                        if (this.showSwatches) {
    	                        	chk.bind('change', {plot:plot, index:idx, series:s, speed:speed}, handleToggle);	// customizing (2012-05-03, Roy Choi) for one canvas series
    	                        	chk.addClass('jqplot-seriesToggle');
    	                        }
    	                        /*if (this.showSwatches) {
    	                            td1.bind('click', {plot:plot, index:idx, series:s, speed:speed}, handleToggle);	// customizing (2012-05-03, Roy Choi) for one canvas series
    	                            td1.addClass('jqplot-seriesToggle');
    	                        }*/
    	                        if (this.showLabels)  {
    	                            td2.bind('click', {plot:plot, index:idx, series:s, speed:speed}, handleToggle);	// customizing (2012-05-03, Roy Choi) for one canvas series
    	                            td2.addClass('jqplot-seriesToggle');
    	                        }
    	                    }
    	                    
    	                    pad = true;
    	                }
    	            }
    	            idx++;
    	        }
    	        
    	        td1 = td2 = div0 = div1 = null;   
    	    }
        }
    };
    
    // customizing (2014-06-03, Roy)
    $.jqplot.EnhancedLegendRenderer.prototype.pack = function(offsets) {
    	return;
    };

    /* modified by Boram - checkbox click:series show/hide, label click:series highlighted/unhighlighted */
    var handleToggle = function (ev) {
    	/* init legend label style */
    	ev.preventDefault();
        ev.stopImmediatePropagation();
        
        var plot = ev.data.plot;
        var series = ev.data.series;
        
        if(ev.target.type == "checkbox"){
        	if (!ev.target.checked) {
        		if(series.highlighted) {
        			plot.restorePreviousSeriesOrder();
        			if(plot.plugins.highlighter && plot.plugins.highlighter.show) {
        					plot.plugins.highlighter.isClickHighlighting = false;
        			}
        		}
    			series.hide = true;
    			$('td[id="jqplot_'+plot.target.attr('id')+'_table_legend_td2_'+series.label+'"]', plot.legend._elem).addClass('jqplot-series-hidden');
                //$(this).parent().parent().addClass('jqplot-series-hidden');                
                //$(this).prev('.jqplot-table-legend-swatch').addClass('jqplot-series-hidden');
            }
            else {
                series.hide = false;
                $('td[id="jqplot_'+plot.target.attr('id')+'_table_legend_td2_'+series.label+'"]', plot.legend._elem).removeClass('jqplot-series-hidden');
    			//$(this).parent().parent().removeClass('jqplot-series-hidden');                
                //$(this).parent().prev('.jqplot-table-legend-swatch').removeClass('jqplot-series-hidden');
            }
        	plot.target.off('jqPlot.seriesVisibleChange', handleVisible);
        	series.toggleDisplay(ev);
        	plot.target.on('jqPlot.seriesVisibleChange', handleVisible);
        	/*plot.target.trigger('jqPlot.seriesVisibleChange', [plot]);*/
        }
        else{
        	$('.jqplot-table-legend-highlighted', plot).each(function(ind){
    			$(this).removeClass('jqplot-table-legend-highlighted');
    		});
        	if (!series.hide){
        		if (plot.options.multicanvas) {
        			plot.restorePreviousSeriesOrder();
        		}
        		if(!series.highlighted) {  
        			/*if(plot.plugins.highlighter && plot.plugins.highlighter.show) {
    					plot.plugins.highlighter.isClickHighlighting = true;
        			}*/
        			plot.moveSeriesToFront(series.index);
        		}
            }
        }
        
        plot = series = null;
    };
    
    // called with scope of plot.
    var postDraw = function () {
        if (this.legend.renderer.constructor == $.jqplot.EnhancedLegendRenderer && this.legend.seriesToggle && this.legend.show){
        	this.legend._elem.show();
        } else if(this.legend._elem) {		// customizing End (2012-05-03, Roy Choi) legend visible
					this.legend._elem.hide();
				}
		
				for(var i=0; i < this.series.length; i++) {
					var s = this.series[i];
					var r = this.legend.renderer;
					if(s.hide) {
						s.toggleDisplay({data:{plot:this,series:s}});
					}
				}
				
				this.target.off('jqPlot.SeriesToFront', handleFocus);
				this.target.off('jqPlot.PreviousSeriesOrder', handleRemoveFocus);
				this.target.off('jqPlot.OriginalSeriesOrder', handleRemoveFocus);
				this.target.off('jqPlot.seriesVisibleChange', handleVisible);
				
				this.target.on('jqPlot.SeriesToFront', handleFocus);
				this.target.on('jqPlot.PreviousSeriesOrder', handleRemoveFocus);
				this.target.on('jqPlot.OriginalSeriesOrder', handleRemoveFocus);
				this.target.on('jqPlot.seriesVisibleChange', handleVisible);
    };
    
    var handleFocus = function(ev, idx, plot) {
    	$('.jqplot-table-legend-highlighted', plot.target).each(function(ind){
			$(this).removeClass('jqplot-table-legend-highlighted');
		});
    	if(plot.options.stackSeries) {
    		$($('tr', $('.jqplot-table-legend', plot.target)).get(plot.series.length -1 - idx)).addClass('jqplot-table-legend-highlighted');
		} else {
			$($('tr', $('.jqplot-table-legend', plot.target)).get(idx)).addClass('jqplot-table-legend-highlighted');
		}
    	
    };
    
    var handleRemoveFocus = function(ev, plot) {
    	$('.jqplot-table-legend-highlighted', plot.target).each(function(ind){
			$(this).removeClass('jqplot-table-legend-highlighted');
		});
    };
    
    var handleVisible = function(ev, plot, series) {
    	if ($.isArray(series)) {
    		var s = plot.series;
    		for(var i=0, len=s.length; i<len; i++) {
    			var cbox = $('.jqplot-table-legend-cbox', 'td[id="jqplot_'+plot.target.attr('id')+'_table_legend_td1_'+s[i].label+'"]', plot.legend._elem);
    			var td2 = $('td[id="jqplot_'+plot.target.attr('id')+'_table_legend_td2_'+s[i].label+'"]', plot.legend._elem);
    			
    			if (s[i].hide) {
    				if (cbox.length > 0) {
    					cbox.get(0).checked = false;
    				}
    				td2.addClass('jqplot-series-hidden');
    			} else {
    				if (cbox.length > 0) {
    					cbox.get(0).checked = true;
    				}
    				td2.removeClass('jqplot-series-hidden');
    			}
    			
    			cbox = td2 = null;
    		}
    	} else {
    		var cbox = $('.jqplot-table-legend-cbox', 'td[id="jqplot_'+plot.target.attr('id')+'_table_legend_td1_'+series.label+'"]', plot.legend._elem);
			var td2 = $('td[id="jqplot_'+plot.target.attr('id')+'_table_legend_td2_'+series.label+'"]', plot.legend._elem);
			
			if (series.hide) {
				if (cbox.length > 0) {
					cbox.get(0).checked = false;
				}
				td2.addClass('jqplot-series-hidden');
			} else {
				if (cbox.length > 0) {
					cbox.get(0).checked = true;
				}
				td2.removeClass('jqplot-series-hidden');
			}
			
			cbox = td2 = null;
    	}
    };
    
	$.jqplot.postDrawHooks.push(postDraw);
})(jQuery);;/**
 jqPlot Group View Plugin
 2012-05-10, Roy Choi
 */
(function($) {
	/**
	 * Class: $.jqplot.GroupPlot
	 * Plugin which will show by group plot.
	 */
	$.jqplot.GroupPlot = function(options) {
		// Group: Properties
		//
		//prop: enable
		// true to enable the Show By Group.
		this.enable = $.jqplot.config.enablePlugins;
		// prop: showByGroup
		// true to show by group plot
		this.show = false;
		// prop: parent
		// parent plot.
		this.parent = null;
		// prop: groupid
		// if this plot is child then plot groupid.
		this.groupid = null;
		// prop: listStyle
		// horizontal, vertical, matrix.
		this.listStyle = 'horizontal';
		// prop: matrixOption
		// If listStyle is 'matrix'.
		this.matrixOption = [1, 1];
		// prop: groupZoom
		// sync zoom all plot.
		this.groupZoom = false;
		// prop: syncXAxis
		// sync x axis all plot
		this.syncXAxis = false;
		this.syncAxis = false;
		
		this.showAllAxis = false;
		
		this.sizeRate = [];

		//this.plotOptions = {};

		this.ischild = false;
		this.plots = [];
		this.container = null;
		this.groupContainer = null;

		$.extend(true, this, options);
	};

	// axis.renderer.tickrenderer.formatter

	// called with scope of plot
	$.jqplot.GroupPlot.init = function(target, data, opts) {
		var options = opts || {};

		// add a highlighter attribute to the plot
		this.plugins.groupplot = new $.jqplot.GroupPlot(options.groupplot);

		this.plugins.groupplot.container = $('#' + target).parent();

		//this.plugins.groupplot.plotOptions = options;

	};

	// called within context of plot
	// create a canvas which we can draw on.
	// insert it before the eventCanvas, so eventCanvas will still capture events.
	$.jqplot.GroupPlot.postPlotDraw = function() {
		// Memory Leaks patch
		if (this.plugins.groupplot.show)
			this.plugins.groupplot.showByGroup(this);
	};

	$.jqplot.preInitHooks.push($.jqplot.GroupPlot.init);
	$.jqplot.postDrawHooks.push($.jqplot.GroupPlot.postPlotDraw);

	$.jqplot.GroupPlot.prototype.showByGroup = function(plot, opts) {
		var options = opts || {};
		var gp = plot.plugins.groupplot;

		if (gp.enable && gp.show && !gp.ischild) {

			/*if(gp.groupContainer) {
			 gp.clearGroupPlot(plot);
			 gp.groupContainer.remove();
			 }*/
			gp.container.children('.jqplot-group-container').remove();

			var elem = document.createElement('ul');
			$(elem).css({
				width : '100%',
				height : '100%',
				overflowY : 'auto',
				overflowX : 'hidden'
			}).addClass('jqplot-group-container');

			$(elem).appendTo(gp.container);

			gp.groupContainer = $(elem);
			elem = null;

			//gp.groupContainer.show();

			// �׷� �� series�� copy�Ѵ�.
			var groupSeriesList = [];
			var groupDataList = [];
			var groupCount = 0;
			var gid;
			var series = plot.series;
			var so;
			for (var i = 0, slen = series.length; i < slen; i++) {
				var si = series[i];
				gid = si.group;
				if (!$.isArray(groupSeriesList[gid])) {
					groupCount++;
					groupSeriesList[gid] = [];
					groupDataList[gid] = [];
				}
				if ($.isArray(plot.options.series) &&  plot.options.series[i]) {
					so = plot.options.series[i];
				} else {
					so = {
						label : si.label,
						group : si.group,
						color : si.color
					};
				}

				groupDataList[gid].push(si.data);
				groupSeriesList[gid].push(so);
				si = so = null;
			}
			//grouCount = groupSeriesList.length;
			series = null;

			// Group �����̳� ��
			plot.target.empty();
			plot.target.append(gp.groupContainer);

			// Group Style ������ �� �� �׷� ��Ʈ�� ũ�⸦ ���Ѵ�.
			var plotWidth = plot.target.innerWidth()-10;
			var plotHeight = plot.target.innerHeight()-10;
			var gwidth, gheight;
			switch(gp.listStyle) {
				case 'vertical':
					gwidth = plotWidth / groupCount;
					gheight = plotHeight;
					break;
				case 'horizontal':
					gwidth = plotWidth;
					gheight = plotHeight / groupCount;
					break;
				case 'matrix':
					gwidth = plotWidth / gp.matrixOption[0];
					gheight = plotHeight / gp.matrixOption[1] - 1;
					break;
			}

			var xaxis = plot.axes.xaxis;
			for (var i = 0; i < groupCount; i++) {
				// Group �����̳ʿ� �� ��Ʈ������ �����Ѵ�.
				var gdata = groupDataList.shift();
				var gseries = groupSeriesList.shift();
				var gid = gseries[0].group;
				var opts = $.extend(true, {}, plot.options, options);
				if (gp.syncAxis) {
					for (var axis in plot.axes) {
						opts.axes[axis].min = plot.axes[axis].min;
						opts.axes[axis].max = plot.axes[axis].max;
					}
				} else {
					for (var axis in opts.axes) {
						delete opts.axes[axis].min;
						delete opts.axes[axis].max;
					}
				}

				if (!gp.showAllAxis) {
					switch(gp.listStyle) {
						case 'vertical':
							if (i > 0) {
								opts.axes.yaxis.show = true;
								opts.axes.yaxis.showLabel = false;
								opts.axes.yaxis.showTicks = false;
							}
							break;
						case 'horizontal':
							if (groupCount - 1 > i) {
								opts.axes.xaxis.show = true;
								opts.axes.xaxis.showLabel = false;
								opts.axes.xaxis.showTicks = false;
							}
							break;
					}
				}
				
				opts.series = gseries;
				opts.groupplot = {
					groupid : gid,
					show : false,
					parent : plot,
					ischild : true
				};
				var gli = document.createElement('li');
				var gchart_id = plot.target.attr('id') + '-group-' + gid;

				$(gli).css({
					display : 'inline-block',
					listStyle : 'none',
					margin : 0,
					padding : 0,
					width : (gp.listStyle === 'vertical' && !isNaN(gp.sizeRate[i]) ? plotWidth * gp.sizeRate[i] : gwidth) + 'px',
					height : (gp.listStyle === 'horizontal' && !isNaN(gp.sizeRate[i]) ? plotWidth * gp.sizeRate[i] : gheight) + 'px'
				}).appendTo(gp.groupContainer);

				var gchart = document.createElement('div');

				$(gchart).attr('id', gchart_id).css({
					width : '100%',
					height : '100%',
					margin : 0,
					padding : 0
				}).appendTo(gli);
				// Group ��Ʈ�� ���Ѵ�.
				
				var isDefinedXaxis = false;
				opts.series.forEach(function(s, ind) {
					if (s.xaxis === 'xaxis') {
						isDefinedXaxis = true;
						return false;
					}
				});
				
				if (!isDefinedXaxis) {
					opts.series.push({
						xaxis: 'xaxis',
						show: false
					});
					
					if (gdata.length > 0 && gdata[0].length > 0) {
						gdata = $.merge(gdata, gdata[0][0]);
					}
				}
				
				
				var gplot = $.jqplot(gchart_id, gdata, opts);
				gp.plots[gid] = gplot;

			}

		}
	};

	$.jqplot.GroupPlot.prototype.clearGroupPlot = function(plot) {
		var gp = plot.plugins.groupplot;

		$.each(gp.plots, function(gid, gplot) {
			gplot.destroy(true);
			delete gplot;
		});
		gp.plots = [];

		gp.groupContainer.empty();
		gp.groupContainer.html('');
	};

	$.jqplot.GroupPlot.prototype.restorePlot = function(plot) {
		var gp = plot.plugins.groupplot;
		gp.show = false;
		gp.clearGroupPlot(plot);
		plot.target.show();
		plot.replot({
			resetAxes : true
		});
	};

	$.jqplot.GroupPlot.prototype.setGroupZoom = function(plot, flag) {
		var gp = plot.plugins.groupplot;

		if (flag) {
			resetGroupChildZoom(plot);
		}

		gp.groupZoom = flag;
	};

	$.jqplot.GroupPlot.prototype.setSyncXAxis = function(plot, flag) {
		var gp = plot.plugins.groupplot;
		var min, max;
		var xaxis = null;

		if (flag) {
			//resetGroupChildZoom(plot);

			xaxis = plot.axes.xaxis;
			$.each(gp.plots, function(gid, gplot) {
				var xa = gplot.options.axes.xaxis;
				xa.min = xaxis.min;
				xa.max = xaxis.max;
				//gplot.replot({resetAxes:true,axes:{xaxis:xa}});
			});
		} else {
			$.each(gp.plots, function(gid, gplot) {
				var xa = gplot.options.axes.xaxis;
				delete xa.min;
				delete xa.max;
				//gplot.replot({resetAxes:true,axes:{xaxis:xa}});
			});
		}

		gp.syncXAxis = flag;
	};

	function resetGroupChildZoom(plot) {
		var gp = plot.plugins.groupplot;
		var tmp = gp.groupZoom;
		gp.groupZoom = false;

		var gplots = plot.plugins.groupplot.plots;
		$.each(gplots, function(gid, gplot) {
			gplot.resetZoom();
		});
		gp.groupZoom = tmp;
	}

	function clearGroupPlot(plot) {
		$.jqplot.GroupPlot.clearGroupPlot(plot);
	}

	function restorePlot(plot) {
		$.jqplot.GroupPlot.restorePlot(plot);
	}

	function showByGroup(plot) {
		$.jqplot.GroupPlot.showByGroup(plot);
	}

})(jQuery); ;(function($) { 
  /** 
   * Class: $.jqplot.BoxplotRenderer 
   * jqPlot Plugin to draw box plots <http://en.wikipedia.org/wiki/Box_plot>. 
   *  
   * To use this plugin, include the renderer js file in  
   * your source: 
   *  
   * > <script type="text/javascript" src="plugins/jqplot.boxplotRenderer.js"></script> 
    *  
    * Then you set the renderer in the series options on your plot: 
    *  
    * > series: [{renderer:$.jqplot.BoxplotRenderer}] 
    *  
    * Data should be specified like so: 
    *  
    * > dat = [[sample_id, min, q1, median, q3, max], ...] 
    *  
    */ 
   $.jqplot.BoxplotRenderer = function(){ 
       // subclass line renderer to make use of some of its methods. 
       $.jqplot.LineRenderer.call(this); 
       // prop: boxWidth 
       // Default will auto calculate based on plot width and number 
       // of boxes displayed. 
       this.boxWidth = 'auto'; 
       this._boxMaxWidth = 100; // if 'auto', cap at this max 
       // prop: lineWidth 
       // The thickness of all lines drawn. Default is 1.5 pixels. 
       this.lineWidth = 1.5;
       
       this.colorFilter = null;
   }; 
    
   $.jqplot.BoxplotRenderer.prototype = new $.jqplot.LineRenderer(); 
   $.jqplot.BoxplotRenderer.prototype.constructor = $.jqplot.BoxplotRenderer; 
    
   // called with scope of series. 
   $.jqplot.BoxplotRenderer.prototype.init = function(options) { 
       this.lineWidth = options.lineWidth || this.renderer.lineWidth;
       this.colorFilter = options.colorFilter; 
       $.jqplot.LineRenderer.prototype.init.call(this, options); 
       // set the yaxis data bounds here to account for high and low values 
       var db = this._yaxis._dataBounds; 
       var d = this._plotData; 
       for (var j=0, dj=d[j]; j<d.length; dj=d[++j]) { 
           if (dj[1] < db.min || db.min == null) 
               db.min = dj[1]; 
           if (dj[5] > db.max || db.max == null) 
               db.max = dj[5]; 
       } 
   }; 
    
   // called within scope of series. 
   $.jqplot.BoxplotRenderer.prototype.draw = function(ctx, gd, options) { 
       var d = this.data; 
       var r = this.renderer; 
       var xp = this._xaxis.series_u2p; 
       var yp = this._yaxis.series_u2p; 
       var strokeStyle = options.strokeStyle || '#888888';
       var seriesColorArr = $.jqplot.getColorComponents(this.color);
       var boxColor = $.jqplot.sprintf('rgba(%s,%s,%s,.3)', seriesColorArr[0], seriesColorArr[1], seriesColorArr[2]);
       var medianColor = $.jqplot.sprintf('rgba(%s,%s,%s,.5)', seriesColorArr[0], seriesColorArr[1], seriesColorArr[2]);
       if (!options) 
           options = {}; 
       if (!('lineWidth' in options)) 
           $.extend(true, options, {lineWidth: this.lineWidth}); 
       
       var opts = $.extend(true, {}, options, {strokeStyle: strokeStyle});
       var fillBoxopts = $.extend(true, {}, opts, {fillRect: true, fillStyle: boxColor}); 
       var strokeBoxopts = $.extend(true, {}, opts, {strokeRect: true, strokeStyle: strokeStyle});
       var boxW = opts.boxWidth || r.boxWidth; 
       if (boxW == 'auto') 
           boxW = Math.min(r._boxMaxWidth, 0.6 * ctx.canvas.width/d.length); 
       var endW = boxW / 2; // min and max ticks are half the box width 
       
       var newColor, newSeriesColorArr, newMedianColor, newFillBoxopts;
       
       boxW -= this.lineWidth*2; 
       ctx.save(); 
       if (this.show) {
       	this._boxPoints = [];
           for (var i=0, di=d[i]; i<d.length; di=d[++i]) { 
              var  x = xp(di[0]), 
                 min = yp(di[1]), 
                  q1 = yp(di[2]), 
                 med = yp(di[3]), 
                  q3 = yp(di[4]), 
                 max = yp(di[5]); 

              var endL = x - endW/2; // start (left) x coord of min/max ticks 
              var endR = x + endW/2; // end (right) x coord of min/max ticks 
              var medL = x - boxW/2; // start (left) x coord of median tick 
              var medR = x + boxW/2; // end (right) x coord of median tick 
              
              // draw box 
              boxH = q1 - q3; 
              boxpoints = [medL, q3, boxW, boxH]; 
              this._boxPoints.push([[medL, q3], [medR, q3], [medR, q1], [medL, q1]]);
              
              if ($.isFunction(this.colorFilter)) {
              	newColor = this.colorFilter.call(this, di, i);
              	if (newColor) {
              		newSeriesColorArr = $.jqplot.getColorComponents(newColor);
              		newFillBoxopts = $.extend(true, {}, fillBoxopts, {fillStyle: $.jqplot.sprintf('rgba(%s,%s,%s,.3)', newSeriesColorArr[0], newSeriesColorArr[1], newSeriesColorArr[2])});
              		newMedianColor = $.jqplot.sprintf('rgba(%s,%s,%s,.5)', newSeriesColorArr[0], newSeriesColorArr[1], newSeriesColorArr[2]);
              	} else {
              		newFillBoxopts = fillBoxopts;
              		newMedianColor = medianColor;
              	}
              } else {
            		newFillBoxopts = fillBoxopts;
            		newMedianColor = medianColor;
              }
              
              r.shapeRenderer.draw(ctx, boxpoints, newFillBoxopts); 
              r.shapeRenderer.draw(ctx, boxpoints, strokeBoxopts); 

              // draw whiskers 
              r.shapeRenderer.draw(ctx, [[x, min], [x, q1]], opts);  
              r.shapeRenderer.draw(ctx, [[x, q3], [x, max]], opts);  

              // draw min and max ticks 
              r.shapeRenderer.draw(ctx, [[endL, min], [endR, min]], opts); 
              r.shapeRenderer.draw(ctx, [[endL, max], [endR, max]], opts); 
              // median tick is full box width 
              r.shapeRenderer.draw(ctx, [[medL, med], [medR, med]], $.extend(true, {}, opts, {strokeStyle: newMedianColor})); 
           } 
       } 
       ctx.restore(); 
   };   
     
    $.jqplot.BoxplotRenderer.prototype.drawShadow = function(ctx, gd, options) { 
        // This is a no-op, shadows drawn with lines. 
    }; 
     
    // called with scope of plot. 
    $.jqplot.BoxplotRenderer.checkOptions = function(target, data, options, plot) { 
        // provide some sensible highlighter options by default 
        hldefaults = { 
            showMarker: false, 
            tooltipAxes: 'y', 
            yvalues: 5, 
            formatString: '<table class="jqplot-highlighter">' + 
            							'<tr><td>index:</td><td>%s</td></tr>' + 
                          '<tr><td>min:</td><td>%s</td></tr>' + 
                          '<tr><td>q1:</td><td>%s</td></tr>' + 
                          '<tr><td>med:</td><td>%s</td></tr>' + 
                          '<tr><td>q3:</td><td>%s</td></tr>' + 
                          '<tr><td>max:</td><td>%s</td></tr>' + 
                          '</table>' 
            }; 
        if (!options.highlighter) 
            options.highlighter = {show: true}; 
        if (options.highlighter.show) {
        	if ($.isArray(options.series)) {
        		var s;
        		for (var i=0; i<this.series.length; i++) {
        			s = this.series[i];
        			if (s.renderer.constructor === $.jqplot.BoxplotRenderer)
        				s.highlighter = $.extend(true, {}, s.highlighter || {}, hldefaults);
        		}
        	}
        } 
    }; 
     
    $.jqplot.postInitHooks.push($.jqplot.BoxplotRenderer.checkOptions); 
     
})(jQuery); ;/**
 jqPlot Big Data Scatter Renderer Plugin
 2013-04-17, Roy Choi
 */
(function($) {
	'use strict';
	
	function isIE() {
		if (navigator && navigator.appName.indexOf("Internet Explorer") != -1) {
			return true;
		}
		return false;
	}

	// Class: $.jqplot.BigDataScatterRenderer
	// The default line renderer for jqPlot, this class has no options beyond the <Series> class.
	// Draws series as a line.
	$.jqplot.BigDataScatterRenderer = function() {
	};

	// called with scope of series.
	$.jqplot.BigDataScatterRenderer.prototype.init = function(options, plot) {
		// Group: Properties
		//
		options = options || {};
		this._type = 'scatter';
		this.renderer.size = options.size || 3;
		this.renderer.lineWidth = 1;
		this.renderer.style = options.style;
		this.renderer.fastMode = options.fastMode || false;

		options.stroke = options.stroke || false;
		delete (options.highlightMouseOver);
		delete (options.highlightMouseDown);
		delete (options.highlightColor);

		$.extend(true, this.renderer, options);

		this.renderer.options = options;

		//this._imgPlotData = null;

		if (!this.isTrendline && plot) {
			plot.plugins.lineRenderer = {};
		}

	};

	$.jqplot.BigDataScatterRenderer.prototype.initBands = function(options, plot) {
	};

	// setGridData
	// converts the user data values to grid coordinates and stores them
	// in the gridData array.
	// Called with scope of a series.
	$.jqplot.BigDataScatterRenderer.prototype.setGridData = function(plot) {
		return;
		// recalculate the grid data
		/*var xp = this._xaxis.series_u2p;
		 var yp = this._yaxis.series_u2p;
		 var data = this._plotData;
		 var pdata = this._prevPlotData;
		 this.gridData = [];

		 if (!xp) {
		 return;
		 }

		 for (var i=0, l=this.data.length; i < l; i++) {
		 // if not a line series or if no nulls in data, push the converted point onto the array.
		 this.gridData.push([Math.round(xp.call(this._xaxis, data[i][0])), Math.round(yp.call(this._yaxis, data[i][1]))]);
		 }

		 data = pdata = null;*/
	};

	// makeGridData
	// converts any arbitrary data values to grid coordinates and
	// returns them.  This method exists so that plugins can use a series'
	// linerenderer to generate grid data points without overwriting the
	// grid data associated with that series.
	// Called with scope of a series.
	$.jqplot.BigDataScatterRenderer.prototype.makeGridData = function(data, plot) {
		// recalculate the grid data
		/*var xp = this._xaxis.series_u2p;
		var yp = this._yaxis.series_u2p;
		var gd = [];
		this.renderer._smoothedData = [];
		this.renderer._smoothedPlotData = [];
		this.renderer._hiBandGridData = [];
		this.renderer._lowBandGridData = [];
		this.renderer._hiBandSmoothedData = [];
		this.renderer._lowBandSmoothedData = [];

		for (var i = 0; i < data.length; i++) {
			gd.push([Math.round(xp.call(this._xaxis, data[i][0])), Math.round(yp.call(this._yaxis, data[i][1]))]);
		}

		return gd;*/
	};

	/*function makeGridData(plot) {
	 var series = plot.series;
	 var i, j, sl, dl, data;

	 var max, min, offsets, offmax,offmin, pixellength, unitlength;

	 sl = series.length;

	 $.jqplot.config.pluginLocation = '';
	 var worker = new Worker($.jqplot.config.pluginLocation+'jqplot.bigDataGridDataWorker.js');
	 worker.addEventListener('message', function(event) {

	 }, false);

	 for (i=0; i<sl; i++) {
	 s = series[i];
	 max = s.max;
	 min = s.min;
	 offsets = s._offsets;
	 offmax = offsets.max;
	 offmin = offsets.min;

	 pixellength = offmax - offmin;
	 unitlength = max - min;

	 data = s.data;

	 }

	 }*/

	function dataStrokeZoomProcIE(plotData, series, width, height, dataWidth, color) {
		var dataIndex, j, point, newIndex, data, l;
		var dataWidthLower = dataWidth - 4, dataWidthUpper = dataWidth + 4;
		var dataWidthLower2 = dataWidth - 8, dataWidthUpper2 = dataWidth + 8;
		var dataWidthDouble = dataWidth * 2;
		var dataWidthDoubleLower = dataWidthDouble - 4, dataWidthDoubleUpper = dataWidthDouble + 4;
		var dataWidthDoubleLower2 = dataWidthDouble - 8, dataWidthDoubleUpper2 = dataWidthDouble + 8;
		var xLimit = width - 2;
		var xLimit2 = width - 3;
		var yLimit = height - 2;
		var yLimit2 = height - 3;
		var x, y;
		var r = color[0], g = color[1], b = color[2], a = color[3];
		var sr = parseInt(r * 0.8, 10);
		var sg = parseInt(g * 0.8, 10);
		var sb = parseInt(b * 0.8, 10);
		var sa = 255;

		var xMin, xMax, yMin, yMax;

		var tempCalcResult_u2p_x = series._xaxis.tempCalcResult_u2p;
		var tempCalcResult_u2p_y = series._yaxis.tempCalcResult_u2p;
		var tempCalcMin = series._xaxis.tempCalcMin;
		var tempCalcMax = series._yaxis.tempCalcMax;

		data = series.data;
		
		l = data.length;
		if (l) {
			xMin = series._xaxis.min;
			xMax = series._xaxis.max;
			yMin = series._yaxis.min;
			yMax = series._yaxis.max;

			for ( j = 0; j < l; j++) {
				point = data[j];
				x = point[0];
				y = point[1];
				if (x >= xMin && x <= xMax && y >= yMin && y <= yMax) {
					x = Math.round((x - tempCalcMin) * tempCalcResult_u2p_x);
					y = Math.round((y - tempCalcMax) * tempCalcResult_u2p_y);

					if (x >= xLimit || x <= 2 || y <= 2 || y >= yLimit) {
						continue;
					}

					dataIndex = dataWidth * y + x * 4;

					if (y > 1) {
						if (y > 2) {
							if (x > 1) {
								if (x > 2) {
									newIndex = dataIndex - dataWidthDoubleUpper2;
									plotData[newIndex] = sr;
									plotData[newIndex + 1] = sg;
									plotData[newIndex + 2] = sb;
									plotData[newIndex + 3] = sa;
								}
								newIndex = dataIndex - dataWidthDoubleUpper;
								plotData[newIndex] = sr;
								plotData[newIndex + 1] = sg;
								plotData[newIndex + 2] = sb;
								plotData[newIndex + 3] = sa;
							}
							newIndex = dataIndex - dataWidthDouble;
							plotData[newIndex] = sr;
							plotData[newIndex + 1] = sg;
							plotData[newIndex + 2] = sb;
							plotData[newIndex + 3] = sa;
							if (x < xLimit) {
								newIndex = dataIndex - dataWidthDoubleLower;
								plotData[newIndex] = sr;
								plotData[newIndex + 1] = sg;
								plotData[newIndex + 2] = sb;
								plotData[newIndex + 3] = sa;

								if (x < xLimit2) {
									newIndex = dataIndex - dataWidthDoubleLower2;
									plotData[newIndex] = sr;
									plotData[newIndex + 1] = sg;
									plotData[newIndex + 2] = sb;
									plotData[newIndex + 3] = sa;
								}
							}
						}

						if (x > 1) {
							if (x > 2) {
								newIndex = dataIndex - dataWidthUpper2;
								plotData[newIndex] = sr;
								plotData[newIndex + 1] = sg;
								plotData[newIndex + 2] = sb;
								plotData[newIndex + 3] = sa;
							}

							newIndex = dataIndex - dataWidthUpper;
							plotData[newIndex] = r;
							plotData[newIndex + 1] = g;
							plotData[newIndex + 2] = b;
							plotData[newIndex + 3] = a;
						}

						newIndex = dataIndex - dataWidth;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;

						if (x < xLimit) {
							newIndex = dataIndex - dataWidthLower;
							plotData[newIndex] = r;
							plotData[newIndex + 1] = g;
							plotData[newIndex + 2] = b;
							plotData[newIndex + 3] = a;

							if (x < xLimit2) {
								newIndex = dataIndex - dataWidthLower2;
								plotData[newIndex] = sr;
								plotData[newIndex + 1] = sg;
								plotData[newIndex + 2] = sb;
								plotData[newIndex + 3] = sa;
							}
						}
					}

					if (x > 1) {
						if (x > 2) {
							newIndex = dataIndex - 8;
							plotData[newIndex] = sr;
							plotData[newIndex + 1] = sg;
							plotData[newIndex + 2] = sb;
							plotData[newIndex + 3] = sa;
						}
						newIndex = dataIndex - 4;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;
					}

					newIndex = dataIndex;
					plotData[newIndex] = r;
					plotData[newIndex + 1] = g;
					plotData[newIndex + 2] = b;
					plotData[newIndex + 3] = a;

					if (x < xLimit) {
						newIndex = dataIndex + 4;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;
						if (x < xLimit2) {
							newIndex = dataIndex + 8;
							plotData[newIndex] = sr;
							plotData[newIndex + 1] = sg;
							plotData[newIndex + 2] = sb;
							plotData[newIndex + 3] = sa;
						}
					}

					if (y < yLimit) {
						if (x > 1) {
							if (x > 2) {
								newIndex = dataIndex + dataWidthLower2;
								plotData[newIndex] = sr;
								plotData[newIndex + 1] = sg;
								plotData[newIndex + 2] = sb;
								plotData[newIndex + 3] = sa;
							}
							newIndex = dataIndex + dataWidthLower;
							plotData[newIndex] = r;
							plotData[newIndex + 1] = g;
							plotData[newIndex + 2] = b;
							plotData[newIndex + 3] = a;
						}

						newIndex = dataIndex + dataWidth;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;

						if (x < xLimit) {
							newIndex = dataIndex + dataWidthUpper;
							plotData[newIndex] = r;
							plotData[newIndex + 1] = g;
							plotData[newIndex + 2] = b;
							plotData[newIndex + 3] = a;
							if (x < xLimit2) {
								newIndex = dataIndex + dataWidthUpper2;
								plotData[newIndex] = sr;
								plotData[newIndex + 1] = sg;
								plotData[newIndex + 2] = sb;
								plotData[newIndex + 3] = sa;
							}
						}

						if (y < yLimit2) {
							if (x > 1) {
								if (x > 2) {
									newIndex = dataIndex + dataWidthDoubleLower2;
									plotData[newIndex] = sr;
									plotData[newIndex + 1] = sg;
									plotData[newIndex + 2] = sb;
									plotData[newIndex + 3] = sa;
								}
								newIndex = dataIndex + dataWidthDoubleLower;
								plotData[newIndex] = sr;
								plotData[newIndex + 1] = sg;
								plotData[newIndex + 2] = sb;
								plotData[newIndex + 3] = sa;
							}

							newIndex = dataIndex + dataWidthDouble;
							plotData[newIndex] = sr;
							plotData[newIndex + 1] = sg;
							plotData[newIndex + 2] = sb;
							plotData[newIndex + 3] = sa;

							if (x < xLimit) {
								newIndex = dataIndex + dataWidthDoubleUpper;
								plotData[newIndex] = sr;
								plotData[newIndex + 1] = sg;
								plotData[newIndex + 2] = sb;
								plotData[newIndex + 3] = sa;
								if (x < xLimit2) {
									newIndex = dataIndex + dataWidthDoubleUpper2;
									plotData[newIndex] = sr;
									plotData[newIndex + 1] = sg;
									plotData[newIndex + 2] = sb;
									plotData[newIndex + 3] = sa;
								}
							}
						}
					}
				}
			}
		}

		tempCalcResult_u2p_x = tempCalcResult_u2p_y = tempCalcMin = tempCalcMax = null;
		xLimit = xLimit2 = yLimit = yLimit2 = null;
		r = g = b = a = sr = sg = sb = sa = null;
		dataWidthLower2 = dataWidthUpper2 = dataWidthDoubleLower = null;
		dataWidthDoubleUpper = dataWidthDoubleLower2 = dataWidthDoubleUpper2 = null;
		dataIndex = j = newIndex = point = data = l = null;
		xa = ya = x = y = null;
		dataWidthLower = dataWidthUpper = null;
		xp = yp = strokeColor = null;
		xMin = xMax = yMin = yMax = null;
	}

	function dataStrokeProcIE(plotData, series, width, height, dataWidth, color) {
		var dataIndex, j, newIndex, point, data, l;
		var dataWidthLower = dataWidth - 4, dataWidthUpper = dataWidth + 4;
		var dataWidthLower2 = dataWidth - 8, dataWidthUpper2 = dataWidth + 8;
		var dataWidthDouble = dataWidth * 2;
		var dataWidthDoubleLower = dataWidthDouble - 4, dataWidthDoubleUpper = dataWidthDouble + 4;
		var dataWidthDoubleLower2 = dataWidthDouble - 8, dataWidthDoubleUpper2 = dataWidthDouble + 8;
		var xLimit = width - 2;
		var xLimit2 = width - 3;
		var yLimit = height - 2;
		var yLimit2 = height - 3;
		var x, y;
		var r = color[0], g = color[1], b = color[2], a = color[3];
		var sr = parseInt(r * 0.8, 10);
		var sg = parseInt(g * 0.8, 10);
		var sb = parseInt(b * 0.8, 10);
		var sa = 255;

		var tempCalcResult_u2p_x = series._xaxis.tempCalcResult_u2p;
		var tempCalcResult_u2p_y = series._yaxis.tempCalcResult_u2p;
		var tempCalcMin = series._xaxis.tempCalcMin;
		var tempCalcMax = series._yaxis.tempCalcMax;

		data = series.data;

		l = data.length;
		if (l) {
			for ( j = 0; j < l; j++) {
				point = data[j];
				x = Math.round((point[0] - tempCalcMin) * tempCalcResult_u2p_x);
				y = Math.round((point[1] - tempCalcMax) * tempCalcResult_u2p_y);

				if (x >= xLimit || x <= 2 || y <= 2 || y >= yLimit) {
					continue;
				}

				dataIndex = dataWidth * y + x * 4;

				if (y > 1) {
					if (y > 2) {
						if (x > 1) {
							if (x > 2) {
								newIndex = dataIndex - dataWidthDoubleUpper2;
								plotData[newIndex] = sr;
								plotData[newIndex + 1] = sg;
								plotData[newIndex + 2] = sb;
								plotData[newIndex + 3] = sa;
							}
							newIndex = dataIndex - dataWidthDoubleUpper;
							plotData[newIndex] = sr;
							plotData[newIndex + 1] = sg;
							plotData[newIndex + 2] = sb;
							plotData[newIndex + 3] = sa;
						}
						newIndex = dataIndex - dataWidthDouble;
						plotData[newIndex] = sr;
						plotData[newIndex + 1] = sg;
						plotData[newIndex + 2] = sb;
						plotData[newIndex + 3] = sa;
						if (x < xLimit) {
							newIndex = dataIndex - dataWidthDoubleLower;
							plotData[newIndex] = sr;
							plotData[newIndex + 1] = sg;
							plotData[newIndex + 2] = sb;
							plotData[newIndex + 3] = sa;

							if (x < xLimit2) {
								newIndex = dataIndex - dataWidthDoubleLower2;
								plotData[newIndex] = sr;
								plotData[newIndex + 1] = sg;
								plotData[newIndex + 2] = sb;
								plotData[newIndex + 3] = sa;
							}
						}
					}

					if (x > 1) {
						if (x > 2) {
							newIndex = dataIndex - dataWidthUpper2;
							plotData[newIndex] = sr;
							plotData[newIndex + 1] = sg;
							plotData[newIndex + 2] = sb;
							plotData[newIndex + 3] = sa;
						}

						newIndex = dataIndex - dataWidthUpper;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;
					}

					newIndex = dataIndex - dataWidth;
					plotData[newIndex] = r;
					plotData[newIndex + 1] = g;
					plotData[newIndex + 2] = b;
					plotData[newIndex + 3] = a;

					if (x < xLimit) {
						newIndex = dataIndex - dataWidthLower;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;

						if (x < xLimit2) {
							newIndex = dataIndex - dataWidthLower2;
							plotData[newIndex] = sr;
							plotData[newIndex + 1] = sg;
							plotData[newIndex + 2] = sb;
							plotData[newIndex + 3] = sa;
						}
					}
				}

				if (x > 1) {
					if (x > 2) {
						newIndex = dataIndex - 8;
						plotData[newIndex] = sr;
						plotData[newIndex + 1] = sg;
						plotData[newIndex + 2] = sb;
						plotData[newIndex + 3] = sa;
					}
					newIndex = dataIndex - 4;
					plotData[newIndex] = r;
					plotData[newIndex + 1] = g;
					plotData[newIndex + 2] = b;
					plotData[newIndex + 3] = a;
				}

				newIndex = dataIndex;
				plotData[newIndex] = r;
				plotData[newIndex + 1] = g;
				plotData[newIndex + 2] = b;
				plotData[newIndex + 3] = a;

				if (x < xLimit) {
					newIndex = dataIndex + 4;
					plotData[newIndex] = r;
					plotData[newIndex + 1] = g;
					plotData[newIndex + 2] = b;
					plotData[newIndex + 3] = a;
					if (x < xLimit2) {
						newIndex = dataIndex + 8;
						plotData[newIndex] = sr;
						plotData[newIndex + 1] = sg;
						plotData[newIndex + 2] = sb;
						plotData[newIndex + 3] = sa;
					}
				}

				if (y < yLimit) {
					if (x > 1) {
						if (x > 2) {
							newIndex = dataIndex + dataWidthLower2;
							plotData[newIndex] = sr;
							plotData[newIndex + 1] = sg;
							plotData[newIndex + 2] = sb;
							plotData[newIndex + 3] = sa;
						}
						newIndex = dataIndex + dataWidthLower;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;
					}

					newIndex = dataIndex + dataWidth;
					plotData[newIndex] = r;
					plotData[newIndex + 1] = g;
					plotData[newIndex + 2] = b;
					plotData[newIndex + 3] = a;

					if (x < xLimit) {
						newIndex = dataIndex + dataWidthUpper;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;
						if (x < xLimit2) {
							newIndex = dataIndex + dataWidthUpper2;
							plotData[newIndex] = sr;
							plotData[newIndex + 1] = sg;
							plotData[newIndex + 2] = sb;
							plotData[newIndex + 3] = sa;
						}
					}

					if (y < yLimit2) {
						if (x > 1) {
							if (x > 2) {
								newIndex = dataIndex + dataWidthDoubleLower2;
								plotData[newIndex] = sr;
								plotData[newIndex + 1] = sg;
								plotData[newIndex + 2] = sb;
								plotData[newIndex + 3] = sa;
							}
							newIndex = dataIndex + dataWidthDoubleLower;
							plotData[newIndex] = sr;
							plotData[newIndex + 1] = sg;
							plotData[newIndex + 2] = sb;
							plotData[newIndex + 3] = sa;
						}

						newIndex = dataIndex + dataWidthDouble;
						plotData[newIndex] = sr;
						plotData[newIndex + 1] = sg;
						plotData[newIndex + 2] = sb;
						plotData[newIndex + 3] = sa;

						if (x < xLimit) {
							newIndex = dataIndex + dataWidthDoubleUpper;
							plotData[newIndex] = sr;
							plotData[newIndex + 1] = sg;
							plotData[newIndex + 2] = sb;
							plotData[newIndex + 3] = sa;
							if (x < xLimit2) {
								newIndex = dataIndex + dataWidthDoubleUpper2;
								plotData[newIndex] = sr;
								plotData[newIndex + 1] = sg;
								plotData[newIndex + 2] = sb;
								plotData[newIndex + 3] = sa;
							}
						}
					}
				}
			}
		}

		tempCalcResult_u2p_x = tempCalcResult_u2p_y = tempCalcMin = tempCalcMax = null;
		xLimit = xLimit2 = yLimit = yLimit2 = null;
		r = g = b = a = sr = sg = sb = sa = null;
		dataWidthLower2 = dataWidthUpper2 = dataWidthDoubleLower = null;
		dataWidthDoubleUpper = dataWidthDoubleLower2 = dataWidthDoubleUpper2 = null;
		dataIndex = j = newIndex = point = data = l = null;
		xa = ya = x = y = null;
		dataWidthLower = dataWidthUpper = null;
		xp = yp = strokeColor = null;
	}

	function dataZoomProcIE(plotData, series, width, height, dataWidth, color) {
		var dataIndex, j, newIndex, point, data, l;
		var dataWidthLower = dataWidth - 4, dataWidthUpper = dataWidth + 4;
		var xLimit = width - 2;
		var yLimit = height - 2;
		var x, y;
		var left, right, top, bottom;
		var r = color[0], g = color[1], b = color[2], a = color[3];

		var xMin, xMax, yMin, yMax;

		var tempCalcResult_u2p_x = series._xaxis.tempCalcResult_u2p;
		var tempCalcResult_u2p_y = series._yaxis.tempCalcResult_u2p;
		var tempCalcMin = series._xaxis.tempCalcMin;
		var tempCalcMax = series._yaxis.tempCalcMax;

		data = series.data;
		
		l = data.length;
		if (l) {
			xMin = series._xaxis.min;
			xMax = series._xaxis.max;
			yMin = series._yaxis.min;
			yMax = series._yaxis.max;

			for ( j = 0; j < l; j++) {
				point = data[j];
				x = point[0];
				y = point[1];
				if (x >= xMin && x <= xMax && y >= yMin && y <= yMax) {
					x = Math.round((x - tempCalcMin) * tempCalcResult_u2p_x);
					y = Math.round((y - tempCalcMax) * tempCalcResult_u2p_y);

					if (x >= xLimit || x <= 1 || y <= 1 || y >= yLimit) {
						continue;
					}

					dataIndex = dataWidth * y + x * 4;

					left = x >= 1;
					right = x <= xLimit;
					top = y >= 1;
					bottom = y <= yLimit;

					if (top) {
						if (left) {
							newIndex = dataIndex - dataWidthUpper;
							plotData[newIndex] = r;
							plotData[newIndex + 1] = g;
							plotData[newIndex + 2] = b;
							plotData[newIndex + 3] = a;
						}

						newIndex = dataIndex - dataWidth;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;

						if (right) {
							newIndex = dataIndex - dataWidthLower;
							plotData[newIndex] = r;
							plotData[newIndex + 1] = g;
							plotData[newIndex + 2] = b;
							plotData[newIndex + 3] = a;
						}
					}

					if (left) {
						newIndex = dataIndex - 4;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;
					}

					plotData[dataIndex] = r;
					plotData[dataIndex + 1] = g;
					plotData[dataIndex + 2] = b;
					plotData[dataIndex + 3] = a;

					if (right) {
						newIndex = dataIndex + 4;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;
					}

					if (bottom) {
						if (left) {
							newIndex = dataIndex + dataWidthLower;
							plotData[newIndex] = r;
							plotData[newIndex + 1] = g;
							plotData[newIndex + 2] = b;
							plotData[newIndex + 3] = a;
						}

						newIndex = dataIndex + dataWidth;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;

						if (right) {
							newIndex = dataIndex + dataWidthUpper;
							plotData[newIndex] = r;
							plotData[newIndex + 1] = g;
							plotData[newIndex + 2] = b;
							plotData[newIndex + 3] = a;
						}
					}
				}
			}
		}
		tempCalcResult_u2p_x = tempCalcResult_u2p_y = tempCalcMin = tempCalcMax = null;
		left = right = top = bottom = null;
		dataIndex = j = newIndex = point = data = l = null;
		x = y = null;
		dataWidthLower = dataWidthUpper = null;
		xMin = xMax = yMin = yMax = null;
	}

	function dataProcIE(plotData, series, width, height, dataWidth, color) {
		var dataIndex, j, newIndex, point, data, l;
		var dataWidthLower = dataWidth - 4, dataWidthUpper = dataWidth + 4;
		var xLimit = width - 2;
		var yLimit = height - 2;
		var x, y;
		var left, right, top, bottom;
		var r = color[0], g = color[1], b = color[2], a = color[3];

		var tempCalcResult_u2p_x = series._xaxis.tempCalcResult_u2p;
		var tempCalcResult_u2p_y = series._yaxis.tempCalcResult_u2p;
		var tempCalcMin = series._xaxis.tempCalcMin;
		var tempCalcMax = series._yaxis.tempCalcMax;

		data = series.data;

		l = data.length;
		if (l) {
			for ( j = 0; j < l; j++) {
				point = data[j];
				x = (0.5 + (point[0] - tempCalcMin) * tempCalcResult_u2p_x) | 0;
				y = (0.5 + (point[1] - tempCalcMax) * tempCalcResult_u2p_y) | 0;
				/*x = Math.round((point[0] - tempCalcMin) * tempCalcResult_u2p_x);
				 y = Math.round((point[1] - tempCalcMax) * tempCalcResult_u2p_y);*/

				if (x >= xLimit || x <= 1 || y <= 1 || y >= yLimit) {
					continue;
				}

				dataIndex = dataWidth * y + x * 4;

				left = x >= 1;
				right = x <= xLimit;
				top = y >= 1;
				bottom = y <= yLimit;

				if (top) {
					if (left) {
						newIndex = dataIndex - dataWidthUpper;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;
					}

					newIndex = dataIndex - dataWidth;
					plotData[newIndex] = r;
					plotData[newIndex + 1] = g;
					plotData[newIndex + 2] = b;
					plotData[newIndex + 3] = a;

					if (right) {
						newIndex = dataIndex - dataWidthLower;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;
					}
				}

				if (left) {
					newIndex = dataIndex - 4;
					plotData[newIndex] = r;
					plotData[newIndex + 1] = g;
					plotData[newIndex + 2] = b;
					plotData[newIndex + 3] = a;
				}

				plotData[dataIndex] = r;
				plotData[dataIndex + 1] = g;
				plotData[dataIndex + 2] = b;
				plotData[dataIndex + 3] = a;

				if (right) {
					newIndex = dataIndex + 4;
					plotData[newIndex] = r;
					plotData[newIndex + 1] = g;
					plotData[newIndex + 2] = b;
					plotData[newIndex + 3] = a;
				}

				if (bottom) {
					if (left) {
						newIndex = dataIndex + dataWidthLower;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;
					}

					newIndex = dataIndex + dataWidth;
					plotData[newIndex] = r;
					plotData[newIndex + 1] = g;
					plotData[newIndex + 2] = b;
					plotData[newIndex + 3] = a;

					if (right) {
						newIndex = dataIndex + dataWidthUpper;
						plotData[newIndex] = r;
						plotData[newIndex + 1] = g;
						plotData[newIndex + 2] = b;
						plotData[newIndex + 3] = a;
					}
				}
			}
		}

		tempCalcResult_u2p_x = tempCalcResult_u2p_y = tempCalcMin = tempCalcMax = null;
		left = right = top = bottom = null;
		dataIndex = j = newIndex = point = data = l = null;
		x = y = null;
		dataWidthLower = dataWidthUpper = null;
	}

	function drawSquareIE(ctx, opts, plot, series) {
		

		var color, i, j;
		//var data;
		var width = ctx.canvas.width;
		var height = ctx.canvas.height;

		var imgData = ctx.getImageData(0, 0, ctx.canvas.width, ctx.canvas.height);
		var plotData = imgData.data;

		

		var dataWidth = width * 4;

		var zooming = (plot.plugins.cursor && (plot.plugins.cursor._zoom.zooming || plot.plugins.cursor._zoom.isZoomed));
		var resetzooming = (plot.plugins.cursor && plot.plugins.cursor.resetzooming);

		//series.gridData = [];

		color = $.jqplot.getColorComponents(series.color);
		color[3] = 255;

		if (series.show && !series.hide) {
			// hooks get called even if series not shown
			// we don't clear canvas here, it would wipe out all other series as well.
			for ( j = 0; j < $.jqplot.preDrawSeriesHooks.length; j++) {
				$.jqplot.preDrawSeriesHooks[j].call(series, ctx, options);
			}

			if (opts.stroke) {
				if (zooming && !resetzooming) {
					dataStrokeZoomProcIE(plotData, series, width, height, dataWidth, color);
				} else {
					dataStrokeProcIE(plotData, series, width, height, dataWidth, color);
				}
			} else {
				if (zooming && !resetzooming) {
					dataZoomProcIE(plotData, series, width, height, dataWidth, color);
				} else {
					dataProcIE(plotData, series, width, height, dataWidth, color);
				}
			}

			//dataProc(plotData, data, series, width, height, dataWidth, zooming && !resetzooming, color, strokeColor);

			for ( j = 0; j < $.jqplot.postDrawSeriesHooks.length; j++) {// customizing(2012-05-31, Roy Choi)
				$.jqplot.postDrawSeriesHooks[j].call(series, ctx, options);
			}
		}

		

		//
		//

		ctx.putImageData(imgData, 0, 0);
		ctx.save();

		ctx = null;

		width = height = null;
		color = i = j = null;
		imgData = plotData = series = null;
		zooming = resetzooming = null;
		dataWidth = null;

		
	}


	$.jqplot.BigDataScatterRenderer.prototype.draw = function(ctx, sdata, opts, plot) {
		if (this === plot) {
			if (plot.series.length > 0) {
  			draw(ctx, sdata, opts, plot);
  		} 
		} else {
			opts.stroke = this.markerOptions.stroke;

  		draw(ctx, sdata, opts, plot, this.index);
		}
	};

	$.jqplot.BigDataScatterRenderer.prototype.drawShadow = function(ctx, gd, options) {
		// This is a no-op, shadows drawn with lines.
	};
	
	// with series object
	$.jqplot.BigDataScatterRenderer.prototype.getBigGridData = function() {
		
		var tempCalcResult_u2p_x = this._xaxis.tempCalcResult_u2p;
		var tempCalcResult_u2p_y = this._yaxis.tempCalcResult_u2p;
		var tempCalcMin = this._xaxis.tempCalcMin;
		var tempCalcMax = this._yaxis.tempCalcMax;
		var d = this.data;
		var point;
		
		var gd = [];
		
		/*if (isZoomed) {
			var xMin, xMax, yMin, yMax, x, y;
			xMin = this._xaxis.min;
			xMax = this._xaxis.max;
			yMin = this._yaxis.min;
			yMax = this._yaxis.max;
			
			for (var i=0, l=d.length; i<l; i++) {
				point = d[i];
				x = point[0];
				y = point[1];
				
				if (x >= xMin && x <= xMax && y >= yMin && y <= yMax) {
					gd.push([(0.5 + (x - tempCalcMin) * tempCalcResult_u2p_x) | 0, (0.5 + (y - tempCalcMax) * tempCalcResult_u2p_y) | 0]);
				}
			}
		} else {*/
			for (var i=0, l=d.length; i<l; i++) {
				point = d[i];
				gd.push([(0.5 + (point[0] - tempCalcMin) * tempCalcResult_u2p_x) | 0, (0.5 + (point[1] - tempCalcMax) * tempCalcResult_u2p_y) | 0]);
			}
		//}
		
		

		return gd;
	};
	
	function draw(ctx, sdata, options, plot, seriesIndex) {
		options = options || {};
		
		

    var seriesList, series, color, i, j;
		//var data;
		var width = ctx.canvas.width;
		var height = ctx.canvas.height;
		
		var dataWidth = width * 4;
		
		var zooming = (plot.plugins.cursor && (plot.plugins.cursor._zoom.zooming || plot.plugins.cursor._zoom.isZoomed));
		var resetzooming = (plot.plugins.cursor && plot.plugins.cursor.resetzooming);
		var isZoomed = zooming && !resetzooming;

		var imgData = ctx.getImageData(0,0,width,height);
		var plotData = imgData.data;
		
		var borderWidth = plot.grid.borderWidth || 0;
		var opts = {};
		
		if (typeof seriesIndex == 'number') {
			seriesList = [seriesIndex];
		} else {
			seriesList = plot.seriesStack;
		}

		for(i=0; i<seriesList.length; i++) {
			series = plot.series[seriesList[i]];
			//series.gridData = [];
			
			color = $.jqplot.getColorComponents(series.color);
			color[3] *= 255;

			$.extend(true, opts, {
				fill: series.fill,
				borderWidth: borderWidth,
				color: color,
				fastMode: series.renderer.fastMode,
				markerOptions: series.markerOptions
			}, options);

			if (series.show && !series.hide) {
				
				//gridData = gd[i] || series.renderer.setBigGridData.call(series);
				
				// hooks get called even if series not shown
	      // we don't clear canvas here, it would wipe out all other series as well.
	      for (j=0; j<$.jqplot.preDrawSeriesHooks.length; j++) {
	          $.jqplot.preDrawSeriesHooks[j].call(series, ctx, opts);
	      }
	      
	      if (series.showLine && series.showMarker) {
	      	drawLines(plotData, sdata, series, width, height, dataWidth, opts, isZoomed);
	      	if (opts.markerOptions.stroke) {
	      		drawStrokeMarkers(plotData, sdata, series, width, height, dataWidth, opts, isZoomed);
		      } else {
		      	drawMarkers(plotData, sdata, series, width, height, dataWidth, opts, isZoomed);
		      }
	      	//drawLineWithMarker(plotData, sdata, series, width, height, dataWidth, opts, isZoomed);
	      } else if (series.showLine && !series.showMarker) {
					drawLines(plotData, sdata, series, width, height, dataWidth, opts, isZoomed);
	      } else if (opts.markerOptions.stroke) {
	      	drawStrokeMarkers(plotData, sdata, series, width, height, dataWidth, opts, isZoomed);
	      } else {
	      	drawMarkers(plotData, sdata, series, width, height, dataWidth, opts, isZoomed);
	      }

	      //dataProc(plotData, data, series, width, height, dataWidth, zooming && !resetzooming, color, strokeColor);
	
	      for (j=0; j<$.jqplot.postDrawSeriesHooks.length; j++) {		// customizing(2012-05-31, Roy Choi)
	          $.jqplot.postDrawSeriesHooks[j].call(series, ctx, opts);
	      }
			}
			
		}
		//
		//
		ctx.putImageData(imgData, 0, 0);
		//ctx.save();
		ctx = null;

		width = height = null;
		color = i = j = null;
		imgData = plotData = series = null;
		zooming = resetzooming = null;
		dataWidth = null;

		
	}
	
	function getGraphFunction(x0, y0, x1, y1) {
    var equation, additional;
    
  	equation = (y1 - y0) / (x1 - x0);
  	additional = y1 - x1 * equation;
    
    return {
    	y: function(x) {
	      return equation * x + additional;
	    },
	    x: function(y) {
	    	return (y - additional) / equation;
	    }
    };
	}
	
	function drawLineWithMarker(plotData, x0, y0, x1, y1, dataWidth, color) {
		var dataIndex, i, j, point, l;
		var x, y;

		var tempCalcResult_u2p_x = series._xaxis.tempCalcResult_u2p;
		var tempCalcResult_u2p_y = series._yaxis.tempCalcResult_u2p;
		var tempCalcMin = series._xaxis.tempCalcMin;
		var tempCalcMax = series._yaxis.tempCalcMax;
		
		var color = opts.color;
		var r = color[0], g = color[1], b = color[2], a = color[3];
		
		var dx, dy, steep;
		var t;

		var gradient, xend, yend, xgap, xpxl1, ypxl1;
		var intery, xpxl2, ypxl2, fpart, rfpart, opacity, ropacity;
		
		var x0, x1, y0, y1;
		var ox0, ox1, oy0, oy1;
		
		var startPoint, move;
		var xMin, xMax;
		//var absStartX, absStartY, absPointX, absPointY;
		var graphFunc;
		
		var alpha0, alpha1, rAlpha0, rAlpha1;
		
		var startPoint, move;
		var xMin, xMax;
		//var absStartX, absStartY, absPointX, absPointY;
		var graphFunc, condition;
		var xdiff, ydiff; 

		if (!data) data = series.data;

		l = data.length;

		if (isZoomed) {
			move = true;
			
			point = data[0];
			ox0 = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
			oy0 = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;

			for (i = 1; i < l; i++) {
				point = data[i];
				ox1 = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
				oy1 = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;
				
				x0 = ox0;
				y0 = oy0;
				x1 = ox1;
				y1 = oy1;
				
				if (((x0 < 0 || x0 > width || y0 < 0 || y0 > height) 
							|| (x1 < 0 || x1 > width || y1 < 0 || y1 > height))
						&& ((x0 < 0 && x1 < 0) || (x0 > width && x1 > width)
							|| (y0 < 0 && y1 < 0) || (y0 > height && y1 > height))) {
					move = true;
					
					//ctx.moveTo(x1, y1);
				} else {
					condition = x0 == x1 && x0 > 0 && x0 < width;
					if (!condition) {
						graphFunc = getGraphFunction(x0, y0, x1, y1);
						
						xMin = graphFunc.y(0);
						xMax = graphFunc.y(width);
						
						condition = (xMin > xMax && xMin > 0 && xMax < height) || (xMin <= xMax && xMax > 0 && xMin < height);
					}
					
					if (condition) {
						if (x0 > width) {
							x0 = width;
							y0 = graphFunc.y(width);
						} else if (x0 < 0) {
							x0 = 0;
							y0 = graphFunc.y(0);
						}
						if (y0 > height) {
							x0 = graphFunc.x(height);
							y0 = height;
						} else if (y0 < 0) {
							x0 = graphFunc.x(0);
							y0 = 0;
						}

						if (x1 > width) {
							x1 = width;
							y1 = graphFunc.y(width);
						} else if (x1 < 0) {
							x1 = 0;
							y1 = graphFunc.y(0);
						}
						if (y1 > height) {
							y1 = height;
							x1 = graphFunc.x(height);
						} else if (y1 < 0) {
							y1 = 0;
							x1 = graphFunc.x(0);
						}
						
						x0 = (x0 + 0.5) | 0;
						y0 = (y0 + 0.5) | 0;
						x1 = (x1 + 0.5) | 0;
						y1 = (y1 + 0.5) | 0;
						
						xdiff = x1 - x0;
						ydiff = y1 - y0;
						steep = (ydiff < 0 ? -ydiff : ydiff) > (xdiff < 0 ? -xdiff : xdiff);
								
						if (steep) {
						  t = x0; x0 = y0; y0 = t;
						  t = x1; x1 = y1; y1 = t;
						}
						if (x0 > x1) {
							t = x0; x0 = x1; x1 = t;
							t = y0; y0 = y1; y1 = t;
						}
						
						
						dx = x1 - x0;
						dy = y1 - y0;
						gradient = dy / dx;
						
						// handle first endpoint
						xend = (0.5 + x0) | 0;
						yend = y0 + gradient * (xend - x0);
						xpxl1 = xend;   //this will be used in the main loop
				
					
						intery = yend + gradient; // first y-intersection for the main loop
						
						// handle second endpoint
						
						xend = (0.5 + x1) | 0;
						yend = y1 + gradient * (xend - x1);
						xpxl2 = xend; //this will be used in the main loop
				
						// main loop
						
						for (x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
							y = intery | 0;
							fpart = intery - (intery | 0);
						
							if (steep) {
								dataIndex = dataWidth * x + y * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								dataIndex = dataWidth * x + (y+1) * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							} else {
								dataIndex = dataWidth * y + x * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								dataIndex = dataWidth * (y+1) + x * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
				
						  intery = intery + gradient;
						}
					} else {
						if (x0 > 0 && x0 < width &&
								y0 > 0 && y0 < height &&
								x1 > 0 && x1 < width &&
								y1 > 0 && y1 < height) {
									//console.log(x0, y0, x1, y1);
									//console.log(ox0, oy0, ox1, oy1);
								}
					}
				}
				
		    ox0 = ox1;
		    oy0 = oy1;
			}
		} else {
			point = data[0];
			ox0 = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
			oy0 = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;
			
			for (i=1; i<l; i++) {
				point = data[i];
				ox1 = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
				oy1 = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;
				
				x0 = (ox0 + 0.5) | 0;
				y0 = (oy0 + 0.5) | 0;
				x1 = (ox1 + 0.5) | 0;
				y1 = (oy1 + 0.5) | 0;
				
				xdiff = x1 - x0;
				ydiff = y1 - y0;
				steep = (ydiff < 0 ? -ydiff : ydiff) > (xdiff < 0 ? -xdiff : xdiff);
						
				if (steep) {
				  t = x0; x0 = y0; y0 = t;
				  t = x1; x1 = y1; y1 = t;
				}
				if (x0 > x1) {
					t = x0; x0 = x1; x1 = t;
					t = y0; y0 = y1; y1 = t;
				}
				
				
				dx = x1 - x0;
				dy = y1 - y0;
				gradient = dy / dx;
				
				// handle first endpoint
				xend = (0.5 + x0) | 0;
				yend = y0 + gradient * (xend - x0);
				xpxl1 = xend;   //this will be used in the main loop
		
			
				intery = yend + gradient; // first y-intersection for the main loop
				
				// handle second endpoint
				
				xend = (0.5 + x1) | 0;
				yend = y1 + gradient * (xend - x1);
				xpxl2 = xend; //this will be used in the main loop
		
				// main loop
				
				for (x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
					y = intery | 0;
					fpart = intery - (intery | 0);
				
					if (steep) {
						dataIndex = dataWidth * x + y * 4;
		
						alpha0 = plotData[dataIndex + 3] / 255;
						alpha1 = 1 - fpart;
						rAlpha0 = (1 - alpha0);
						rAlpha1 = (1 - alpha1);
		
						plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
						plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
						plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
						plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						
						dataIndex = dataWidth * x + (y+1) * 4;
						
						alpha0 = plotData[dataIndex + 3] / 255;
						alpha1 = fpart;
						rAlpha0 = (1 - alpha0);
						rAlpha1 = (1 - alpha1);
		
						plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
						plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
						plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
						plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
					} else {
						dataIndex = dataWidth * y + x * 4;
						
						alpha0 = plotData[dataIndex + 3] / 255;
						alpha1 = 1 - fpart;
						rAlpha0 = (1 - alpha0);
						rAlpha1 = (1 - alpha1);
		
						plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
						plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
						plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
						plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						
						dataIndex = dataWidth * (y+1) + x * 4;
						
						alpha0 = plotData[dataIndex + 3] / 255;
						alpha1 = fpart;
						rAlpha0 = (1 - alpha0);
						rAlpha1 = (1 - alpha1);
		
						plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
						plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
						plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
						plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
					}
		
				  intery = intery + gradient;
				}
		    
		    ox0 = ox1;
		    oy0 = oy1;
			}
		}

		data = null;
		tempCalcResult_u2p_x = tempCalcResult_u2p_y = tempCalcMin = tempCalcMax = null;
		dataIndex = i = j = l = null;
		x = y = null;
	}

	function drawLines(plotData, data, series, width, height, dataWidth, opts, isZoomed) {
		var dataIndex, i, j, point, l;
		var x, y;

		var xLowerLimit = opts.borderWidth;
		var yLowerLimit = opts.borderWidth;
		var xUpperLimit = width - opts.borderWidth;
		var yUpperLimit = height - opts.borderWidth;

		var tempCalcResult_u2p_x = series._xaxis.tempCalcResult_u2p;
		var tempCalcResult_u2p_y = series._yaxis.tempCalcResult_u2p;
		var tempCalcMin = series._xaxis.tempCalcMin;
		var tempCalcMax = series._yaxis.tempCalcMax;
		
		var color = opts.color;
		var r = color[0], g = color[1], b = color[2], a = color[3];
		var fastMode = opts.fastMode || false;
		
		var dx, dy, steep;
		var t;

		var gradient, xend, yend, xgap, xpxl1, ypxl1;
		var intery, xpxl2, ypxl2, fpart;
		
		var x0, x1, y0, y1;
		var ox0, ox1, oy0, oy1;
		
		var startPoint, move;
		var xMinY, xMaxY;
		var graphFunc, condition;
		var alpha0, alpha1, rAlpha0, rAlpha1;
		var xdiff, ydiff; 

		if (!data) data = series.data;

		l = data.length;
		
		if (fastMode) {
			var fastAlpha = 0.05;
			
			if (isZoomed) {
				move = true;
				
				point = data[0];
				ox0 = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
				oy0 = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;
	
				for (i = 1; i < l; i++) {
					point = data[i];
					ox1 = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
					oy1 = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;
					
					x0 = ox0 - 0.5;
					y0 = oy0 - 0.5;
					x1 = ox1 - 0.5;
					y1 = oy1 - 0.5;
					
					if (((x0 < xLowerLimit || x0 > xUpperLimit || y0 < yLowerLimit || y0 > yUpperLimit) 
								&& (x1 < xLowerLimit || x1 > xUpperLimit || y1 < yLowerLimit || y1 > yUpperLimit))
							&& ((x0 < xLowerLimit && x1 < xLowerLimit) || (x0 > xUpperLimit && x1 > xUpperLimit)
								|| (y0 < yLowerLimit && y1 < yLowerLimit) || (y0 > yUpperLimit && y1 > yUpperLimit))) {
						move = true;
						
						//ctx.moveTo(x1, y1);
					} else {
						condition = x0 == x1 && x0 > 0 && x0 < width;
						if (!condition) {
							graphFunc = getGraphFunction(x0, y0, x1, y1);
							
							xMinY = graphFunc.y(0);
							xMaxY = graphFunc.y(width);
							
							condition = (xMinY > xMaxY && xMinY > yLowerLimit && xMaxY < yUpperLimit) || (xMinY <= xMaxY && xMaxY > yLowerLimit && xMinY < yUpperLimit);
						}
						
						if (condition) {
							x0 = (x0 + 0.5) | 0;
							y0 = (y0 + 0.5) | 0;
							x1 = (x1 + 0.5) | 0;
							y1 = (y1 + 0.5) | 0;
							
							if (x0 > xUpperLimit) {
								x0 = xUpperLimit;
								y0 = (graphFunc.y(xUpperLimit) + 0.5) | 0;
							} else if (x0 < xLowerLimit) {
								x0 = xLowerLimit;
								y0 = (graphFunc.y(xLowerLimit) + 0.5) | 0;
							}
							if (y0 > yUpperLimit) {
								x0 = (graphFunc.x(yUpperLimit) + 0.5) | 0;
								y0 = yUpperLimit;
							} else if (y0 < yLowerLimit) {
								x0 = (graphFunc.x(yLowerLimit) + 0.5) | 0;
								y0 = yLowerLimit;
							}
	
							if (x1 > xUpperLimit) {
								x1 = xUpperLimit;
								y1 = (graphFunc.y(xUpperLimit) + 0.5) | 0;
							} else if (x1 < xLowerLimit) {
								x1 = xLowerLimit;
								y1 = (graphFunc.y(xLowerLimit) + 0.5) | 0;
							}
							if (y1 > yUpperLimit) {
								y1 = yUpperLimit;
								x1 = (graphFunc.x(yUpperLimit) + 0.5) | 0;
							} else if (y1 < yLowerLimit) {
								y1 = yLowerLimit;
								x1 = (graphFunc.x(yLowerLimit) + 0.5) | 0;
							}

					    dy = y1 - y0;
					    dx = x1 - x0;
		
					    if (dy < 0) {
					    	dy = -dy;
					    	stepy = -width;
					    } else {
					    	stepy = width;
					    }
					    if (dx < 0) {
					    	dx = -dx;
					    	stepx = -1;
					    } else {
					    	stepx = 1;
					    }
					    
					    dy <<= 1;                                     
					    dx <<= 1;                                     
					    y0 *= width;
					    y1 *= width;
					    
					    dataIndex = (y0 + x0) * 4;
					    plotData[dataIndex] = r;
					    plotData[dataIndex+1] = g;
					    plotData[dataIndex+2] = b;
					    plotData[dataIndex+3] = a;
		
					    if (dx > dy) {
				        fraction = dy - (dx >> 1);    
				        while (x0 != x1) {
			            if (fraction >= 0) {
		                y0 += stepy;
		                fraction -= dx;                
			            }
			            x0 += stepx;
			            fraction += dy;
			            
							    dataIndex = (y0 + x0) * 4;
							    plotData[dataIndex] = r;
							    plotData[dataIndex+1] = g;
							    plotData[dataIndex+2] = b;
							    plotData[dataIndex+3] = a;
				        }
					    } else {
				        fraction = dx - (dy >> 1);
		            while (y0 != y1) {
		              if (fraction >= 0) {
		                x0 += stepx;
		                fraction -= dy;
		              }
		              y0 += stepy;
		              fraction += dx;
		              
		            	dataIndex = (y0 + x0) * 4;
							    plotData[dataIndex] = r;
							    plotData[dataIndex+1] = g;
							    plotData[dataIndex+2] = b;
							    plotData[dataIndex+3] = a;
		            }
					    }
						}
					}
					
			    ox0 = ox1;
			    oy0 = oy1;
				}
			} else {
				var fraction, stepx, stepy;
				
				point = data[0];
				ox0 = (point[0] - tempCalcMin) * tempCalcResult_u2p_x - 0.5;
				oy0 = (point[1] - tempCalcMax) * tempCalcResult_u2p_y - 0.5;
				
				for (i=1; i<l; i++) {
					point = data[i];
					ox1 = (point[0] - tempCalcMin) * tempCalcResult_u2p_x - 0.5;
					oy1 = (point[1] - tempCalcMax) * tempCalcResult_u2p_y - 0.5;
					
					x0 = (ox0 + 0.5) | 0;
					y0 = (oy0 + 0.5) | 0;
					x1 = (ox1 + 0.5) | 0;
					y1 = (oy1 + 0.5) | 0;

			    dy = y1 - y0;
			    dx = x1 - x0;

			    if (dy < 0) {
			    	dy = -dy;
			    	stepy = -width;
			    } else {
			    	stepy = width;
			    }
			    if (dx < 0) {
			    	dx = -dx;
			    	stepx = -1;
			    } else {
			    	stepx = 1;
			    }
			    
			    dy <<= 1;                                     
			    dx <<= 1;                                     
			    y0 *= width;
			    y1 *= width;
			    
			    dataIndex = (y0 + x0) * 4;
			    plotData[dataIndex] = r;
			    plotData[dataIndex+1] = g;
			    plotData[dataIndex+2] = b;
			    plotData[dataIndex+3] = a;

			    if (dx > dy) {
		        fraction = dy - (dx >> 1);    
		        while (x0 != x1) {
	            if (fraction >= 0) {
                y0 += stepy;
                fraction -= dx;                
	            }
	            x0 += stepx;
	            fraction += dy;
	            
					    dataIndex = (y0 + x0) * 4;
					    plotData[dataIndex] = r;
					    plotData[dataIndex+1] = g;
					    plotData[dataIndex+2] = b;
					    plotData[dataIndex+3] = a;
		        }
			    } else {
		        fraction = dx - (dy >> 1);
            while (y0 != y1) {
              if (fraction >= 0) {
                x0 += stepx;
                fraction -= dy;
              }
              y0 += stepy;
              fraction += dx;
              
            	dataIndex = (y0 + x0) * 4;
					    plotData[dataIndex] = r;
					    plotData[dataIndex+1] = g;
					    plotData[dataIndex+2] = b;
					    plotData[dataIndex+3] = a;
            }
			    }

			    ox0 = ox1;
			    oy0 = oy1;
				}
			}
		} else {
			if (isZoomed) {
				move = true;
				
				point = data[0];
				ox0 = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
				oy0 = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;
	
				for (i = 1; i < l; i++) {
					point = data[i];
					ox1 = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
					oy1 = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;
					
					x0 = ox0 - 0.5;
					y0 = oy0 - 0.5;
					x1 = ox1 - 0.5;
					y1 = oy1 - 0.5;
					
					if (((x0 < xLowerLimit || x0 > xUpperLimit || y0 < yLowerLimit || y0 > yUpperLimit) 
								&& (x1 < xLowerLimit || x1 > xUpperLimit || y1 < yLowerLimit || y1 > yUpperLimit))
							&& ((x0 < xLowerLimit && x1 < xLowerLimit) || (x0 > xUpperLimit && x1 > xUpperLimit)
								|| (y0 < yLowerLimit && y1 < yLowerLimit) || (y0 > yUpperLimit && y1 > yUpperLimit))) {
						move = true;
						
						//ctx.moveTo(x1, y1);
					} else {
						condition = x0 == x1 && x0 > 0 && x0 < width;
						if (!condition) {
							graphFunc = getGraphFunction(x0, y0, x1, y1);
							
							xMinY = graphFunc.y(0);
							xMaxY = graphFunc.y(width);
							
							condition = (xMinY > xMaxY && xMinY > yLowerLimit && xMaxY < yUpperLimit) || (xMinY <= xMaxY && xMaxY > yLowerLimit && xMinY < yUpperLimit);
						}
						
						if (condition) {
							if (x0 > xUpperLimit) {
								x0 = xUpperLimit;
								y0 = graphFunc.y(xUpperLimit);
							} else if (x0 < xLowerLimit) {
								x0 = xLowerLimit;
								y0 = graphFunc.y(xLowerLimit);
							}
							if (y0 > yUpperLimit) {
								x0 = graphFunc.x(yUpperLimit);
								y0 = yUpperLimit;
							} else if (y0 < yLowerLimit) {
								x0 = graphFunc.x(yLowerLimit);
								y0 = yLowerLimit;
							}
	
							if (x1 > xUpperLimit) {
								x1 = xUpperLimit;
								y1 = graphFunc.y(xUpperLimit);
							} else if (x1 < xLowerLimit) {
								x1 = xLowerLimit;
								y1 = graphFunc.y(xLowerLimit);
							}
							if (y1 > yUpperLimit) {
								y1 = yUpperLimit;
								x1 = graphFunc.x(yUpperLimit);
							} else if (y1 < yLowerLimit) {
								y1 = yLowerLimit;
								x1 = graphFunc.x(yLowerLimit);
							}
							
							xdiff = x1 - x0;
							ydiff = y1 - y0;
							steep = (ydiff < 0 ? -ydiff : ydiff) > (xdiff < 0 ? -xdiff : xdiff);
									
							if (steep) {
							  t = x0; x0 = y0; y0 = t;
							  t = x1; x1 = y1; y1 = t;
							}
							if (x0 > x1) {
								t = x0; x0 = x1; x1 = t;
								t = y0; y0 = y1; y1 = t;
							}
							
							
							dx = x1 - x0;
							dy = y1 - y0;
							gradient = dy / dx;
							
							// handle first endpoint
							xend = (0.5 + x0) | 0;
							yend = y0 + gradient * (xend - x0);
							xpxl1 = xend;   //this will be used in the main loop
							ypxl1 = yend | 0;
							
							fpart = yend - (yend | 0);
			
							if (steep) {
								dataIndex = dataWidth * xpxl1 + ypxl1 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl1 + xpxl1 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
			
							intery = yend + gradient; // first y-intersection for the main loop
							
							// handle second endpoint
							xend = (0.5 + x1) | 0;
							yend = y1 + gradient * (xend - x1);
							xpxl2 = xend; //this will be used in the main loop
							ypxl2 = yend | 0;
							
							fpart = yend - (yend | 0);
							
							if (steep) {
								dataIndex = dataWidth * xpxl2 + ypxl2 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl2 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl2 + xpxl2 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (y + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
					
							// main loop
							
							for (x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
								y = intery | 0;
								fpart = intery - (intery | 0);
							
								if (steep) {
									dataIndex = dataWidth * x + y * 4;
					
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 < xUpperLimit) {
										dataIndex += 4;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								} else {
									dataIndex = dataWidth * y + x * 4;
									
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 <= yUpperLimit) {
										dataIndex += dataWidth;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								}
					
							  intery = intery + gradient;
							}
						}
					}
					
			    ox0 = ox1;
			    oy0 = oy1;
				}
			} else {
				point = data[0];
				ox0 = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
				oy0 = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;
				
				for (i=1; i<l; i++) {
					point = data[i];
					ox1 = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
					oy1 = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;
					
					x0 = ox0 - 0.5;
					y0 = oy0 - 0.5;
					x1 = ox1 - 0.5;
					y1 = oy1 - 0.5;
	
					/*x0 = (ox0 + 0.5) | 0;
					y0 = (oy0 + 0.5) | 0;
					x1 = (ox1 + 0.5) | 0;
					y1 = (oy1 + 0.5) | 0;*/
					
					xdiff = x1 - x0;
					ydiff = y1 - y0;
					steep = (ydiff < 0 ? -ydiff : ydiff) > (xdiff < 0 ? -xdiff : xdiff);
							
					if (steep) {
					  t = x0; x0 = y0; y0 = t;
					  t = x1; x1 = y1; y1 = t;
					}
					if (x0 > x1) {
						t = x0; x0 = x1; x1 = t;
						t = y0; y0 = y1; y1 = t;
					}
	
					dx = x1 - x0;
					dy = y1 - y0;
					gradient = dy / dx;
					
					// handle first endpoint
					xend = (0.5 + x0) | 0;
					yend = y0 + gradient * (xend - x0);
					xpxl1 = xend;   //this will be used in the main loop
					ypxl1 = yend | 0;
					
					fpart = yend - (yend | 0);
	
					if (steep) {
						dataIndex = dataWidth * xpxl1 + ypxl1 * 4;
		
						alpha0 = plotData[dataIndex + 3] / 255;
						alpha1 = 1 - fpart;
						rAlpha0 = (1 - alpha0);
						rAlpha1 = (1 - alpha1);
		
						plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
						plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
						plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
						plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						
						if (ypxl1 + 1 < xUpperLimit) {
							dataIndex += 4;
						
							alpha0 = plotData[dataIndex + 3] / 255;
							alpha1 = fpart;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						}
					} else {
						dataIndex = dataWidth * ypxl1 + xpxl1 * 4;
						
						alpha0 = plotData[dataIndex + 3] / 255;
						alpha1 = 1 - fpart;
						rAlpha0 = (1 - alpha0);
						rAlpha1 = (1 - alpha1);
		
						plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
						plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
						plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
						plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						
						if (ypxl1 + 1 <= yUpperLimit) {
							dataIndex += dataWidth;
						
							alpha0 = plotData[dataIndex + 3] / 255;
							alpha1 = fpart;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						}
					}
	
					intery = yend + gradient; // first y-intersection for the main loop
					
					// handle second endpoint
					xend = (0.5 + x1) | 0;
					yend = y1 + gradient * (xend - x1);
					xpxl2 = xend; //this will be used in the main loop
					ypxl2 = yend | 0;
					
					fpart = yend - (yend | 0);
					
					if (steep) {
						dataIndex = dataWidth * xpxl2 + ypxl2 * 4;
		
						alpha0 = plotData[dataIndex + 3] / 255;
						alpha1 = 1 - fpart;
						rAlpha0 = (1 - alpha0);
						rAlpha1 = (1 - alpha1);
		
						plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
						plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
						plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
						plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						
						if (ypxl2 + 1 < xUpperLimit) {
							dataIndex += 4;
						
							alpha0 = plotData[dataIndex + 3] / 255;
							alpha1 = fpart;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						}
					} else {
						dataIndex = dataWidth * ypxl2 + xpxl2 * 4;
						
						alpha0 = plotData[dataIndex + 3] / 255;
						alpha1 = 1 - fpart;
						rAlpha0 = (1 - alpha0);
						rAlpha1 = (1 - alpha1);
		
						plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
						plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
						plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
						plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						
						if (y + 1 <= yUpperLimit) {
							dataIndex += dataWidth;
						
							alpha0 = plotData[dataIndex + 3] / 255;
							alpha1 = fpart;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						}
					}
	
					// main loop
					
					for (x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
						y = intery | 0;
						fpart = intery - (intery | 0);
					
						if (steep) {
							dataIndex = dataWidth * x + y * 4;
			
							alpha0 = plotData[dataIndex + 3] / 255;
							alpha1 = 1 - fpart;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							
							if (y + 1 < xUpperLimit) {
								dataIndex += 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
						} else {
							dataIndex = dataWidth * y + x * 4;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							alpha1 = 1 - fpart;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							
							if (y + 1 <= yUpperLimit) {
								dataIndex += dataWidth;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
						}
			
					  intery = intery + gradient;
					}
			    
			    ox0 = ox1;
			    oy0 = oy1;
				}
			}
		}


		data = null;
		tempCalcResult_u2p_x = tempCalcResult_u2p_y = tempCalcMin = tempCalcMax = null;
		dataIndex = i = j = l = null;
		x = y = null;
	}
	
	function drawMarkers(plotData, data, series, width, height, dataWidth, opts, isZoomed) {
		var dataIndex, i, j, newIndex, point, l;
		var xLowerLimit = opts.borderWidth;
		var yLowerLimit = opts.borderWidth;
		var xUpperLimit = width - opts.borderWidth * 2;
		var yUpperLimit = height - opts.borderWidth * 2;
		var x, y;
		var color = opts.color;
		var r = color[0], g = color[1], b = color[2], a = color[3], af = a / 255;
		var markSize = opts.markerOptions.size || 3;
		var fastMode = opts.markerOptions.fastMode;
		
		var tempCalcResult_u2p_x = series._xaxis.tempCalcResult_u2p;
		var tempCalcResult_u2p_y = series._yaxis.tempCalcResult_u2p;
		var tempCalcMin = series._xaxis.tempCalcMin;
		var tempCalcMax = series._yaxis.tempCalcMax;
		
		var fpart;
		
		var x0, y0, x1, y1, xf0, yf0;
		var xf, yf, rxf, ryf;
		var distance = (markSize / 2) / 1.0;
		var alpha0, alpha1, rAlpha0, rAlpha1;
		

		if (!data) data = series.data;

		l = data.length;
		
		
		if (l) {
			if (isZoomed) {
				var xMin, xMax, yMin, yMax;
				xMin = series._xaxis.min;
				xMax = series._xaxis.max;
				yMin = series._yaxis.min;
				yMax = series._yaxis.max;
				
				if (fastMode) {
					markSize--;
					for ( j = 0; j < l; j++) {
						point = data[j];
						
						x = point[0];
						y = point[1];
						
						if (x < xMin || x > xMax || y < yMin || y > yMax) {
							continue;
						}
						
						x = (x - tempCalcMin) * tempCalcResult_u2p_x;
						y = (y - tempCalcMax) * tempCalcResult_u2p_y;
		
						if (x > xUpperLimit || x < xLowerLimit || y < yLowerLimit || y > yUpperLimit) {
							continue;
						}

						x0 = (x - distance + 0.5) | 0;
						y0 = (y - distance + 0.5) | 0;
	
						x1 = x0 + markSize;
						y1 = y0 + markSize;
						
						if (x0 < xLowerLimit) {
							x0 = xLowerLimit;
						}
						if (x1 > xUpperLimit) {
							x1 = xUpperLimit;
						}
						
						if (y0 < yLowerLimit) {
							y0 = xLowerLimit;
						}
						if (y1 > yUpperLimit) {
							y1 = yUpperLimit;
						}

						for (x = x0; x <= x1; x++) {
							dataIndex = dataWidth * y0 + x * 4;

							for (y = y0; y <= y1; y++) {
								plotData[dataIndex] = r;
								plotData[dataIndex + 1] = g;
								plotData[dataIndex + 2] = b;
								plotData[dataIndex + 3] = 255;
								
								dataIndex += dataWidth;
							}
						}
					}
				} else {
					for ( j = 0; j < l; j++) {
						point = data[j];
						
						x = point[0];
						y = point[1];
						
						if (x < xMin || x > xMax || y < yMin || y > yMax) {
							continue;
						}
						
						x = (x - tempCalcMin) * tempCalcResult_u2p_x;
						y = (y - tempCalcMax) * tempCalcResult_u2p_y;
		
						if (x > xUpperLimit || x < xLowerLimit || y < yLowerLimit || y > yUpperLimit) {
							continue;
						}
						
						xf0 = x - distance;
						yf0 = y - distance;
						
						x0 = xf0 | 0;
						y0 = yf0 | 0;
						
						xf = xf0 - x0;
						yf = yf0 - y0;
						rxf = 1 - xf;
						ryf = 1 - yf;

						x1 = x0 + markSize;
						y1 = y0 + markSize;
						
						if (x0 < xLowerLimit) {
							x0 = xLowerLimit;
							rxf = 1;
						}
						if (x1 > xUpperLimit) {
							x1 = xUpperLimit;
							xf = 1;
						}
						
						if (y0 < yLowerLimit) {
							y0 = yLowerLimit;
							ryf = 1;
						}
						if (y1 > yUpperLimit) {
							y1 = yUpperLimit;
							yf = 1;
						}
						
						dataIndex = dataWidth * y0 + x0 * 4;
	
						alpha1 = rxf * ryf * af;
						
						alpha0 = plotData[dataIndex + 3] / 255;
						rAlpha0 = (1 - alpha0);
						rAlpha1 = (1 - alpha1);
		
						plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
						plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
						plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
						plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
	
						alpha1 = rxf * af;
	
						for (y = y0+1; y < y1; y++) {
							dataIndex += dataWidth;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						}
						
						if (y0 < y1) {
							dataIndex += dataWidth;
							
							alpha1 = rxf * yf * af;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						}
	
						for (x = x0+1; x < x1; x++) {
							dataIndex = dataWidth * y0 + x * 4;
	
							alpha1 = ryf * af;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
	
							alpha1 = af;
	
							for (y = y0+1; y < y1; y++) {
								dataIndex += dataWidth;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
							
							if (y0 < y1) {
								dataIndex += dataWidth;
								
								alpha1 = yf * af;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
						}
						
						if (x0 < x1) {
							dataIndex = dataWidth * y0 + x1 * 4;
	
							alpha1 = xf * ryf * af;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
	
							alpha1 = xf * af;
	
							for (y = y0+1; y < y1; y++) {
								dataIndex += dataWidth;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
							
							if (y0 < y1) {
								dataIndex += dataWidth;
								
								alpha1 = xf * yf * af;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
						}
					}
				}
			} else {
				if (fastMode) {
					markSize--;
					for ( j = 0; j < l; j++) {
						point = data[j];
						x = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
						y = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;
		
						if (x > xUpperLimit || x < xLowerLimit || y < yLowerLimit || y > yUpperLimit) {
							continue;
						}

						x0 = (x - distance + 0.5) | 0;
						y0 = (y - distance + 0.5) | 0;
	
						x1 = x0 + markSize;
						y1 = y0 + markSize;
						
						if (x0 < xLowerLimit) {
							x0 = xLowerLimit;
						}
						if (x1 > xUpperLimit) {
							x1 = xUpperLimit;
						}
						
						if (y0 < yLowerLimit) {
							y0 = yLowerLimit;
						}
						if (y1 > yUpperLimit) {
							y1 = yUpperLimit;
						}

						for (x = x0; x <= x1; x++) {
							dataIndex = dataWidth * y0 + x * 4;

							for (y = y0; y <= y1; y++) {
								plotData[dataIndex] = r;
								plotData[dataIndex + 1] = g;
								plotData[dataIndex + 2] = b;
								plotData[dataIndex + 3] = 255;
								
								dataIndex += dataWidth;
							}
						}
					}
				} else {
					for ( j = 0; j < l; j++) {
						point = data[j];
						x = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
						y = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;
		
						if (x > xUpperLimit || x < xLowerLimit || y < yLowerLimit || y > yUpperLimit) {
							continue;
						}

						xf0 = x - distance;
						yf0 = y - distance;
						
						x0 = xf0 | 0;
						y0 = yf0 | 0;
						
						xf = xf0 - x0;
						yf = yf0 - y0;
						rxf = 1 - xf;
						ryf = 1 - yf;
	
						x1 = x0 + markSize;
						y1 = y0 + markSize;
						
						if (x0 < xLowerLimit) {
							x0 = xLowerLimit;
							rxf = 1;
						}
						if (x1 > xUpperLimit) {
							x1 = xUpperLimit;
							xf = 1;
						}
						
						if (y0 < yLowerLimit) {
							y0 = xLowerLimit;
							ryf = 1;
						}
						if (y1 > yUpperLimit) {
							y1 = yUpperLimit;
							yf = 1;
						}
						
						dataIndex = dataWidth * y0 + x0 * 4;
	
						alpha1 = rxf * ryf * af;
						
						alpha0 = plotData[dataIndex + 3] / 255;
						rAlpha0 = (1 - alpha0);
						rAlpha1 = (1 - alpha1);
		
						plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
						plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
						plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
						plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
	
						alpha1 = rxf * af;
	
						for (y = y0+1; y < y1; y++) {
							dataIndex += dataWidth;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						}
						
						if (y0 < y1) {
							dataIndex += dataWidth;
							
							alpha1 = rxf * yf * af;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						}
	
						for (x = x0+1; x < x1; x++) {
							dataIndex = dataWidth * y0 + x * 4;
	
							alpha1 = ryf * af;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
	
							alpha1 = af;
	
							for (y = y0+1; y < y1; y++) {
								dataIndex += dataWidth;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
							
							if (y0 < y1) {
								dataIndex += dataWidth;
								
								alpha1 = yf * af;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
						}
						
						if (x0 < x1) {
							dataIndex = dataWidth * y0 + x1 * 4;
	
							alpha1 = xf * ryf * af;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
	
							alpha1 = xf * af;
	
							for (y = y0+1; y < y1; y++) {
								dataIndex += dataWidth;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
							
							if (y0 < y1) {
								dataIndex += dataWidth;
								
								alpha1 = xf * yf * af;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
						}
					}
				}
			}

		}

		tempCalcResult_u2p_x = tempCalcResult_u2p_y = tempCalcMin = tempCalcMax = null;
		dataIndex = j = newIndex = l = null;
		x = y = null;
	}
	
	function drawStrokeMarkers(plotData, data, series, width, height, dataWidth, opts, isZoomed) {
		var dataIndex, i, j, newIndex, point, l;
		var xLowerLimit = opts.borderWidth;
		var yLowerLimit = opts.borderWidth;
		var xUpperLimit = width - opts.borderWidth;
		var yUpperLimit = height - opts.borderWidth;
		var x, y;
		var color = opts.color;
		var r = color[0], g = color[1], b = color[2], a = color[3], af = a / 255;
		var sr = r * 0.8, sg = g * 0.8, sb = b * 0.8, sa = a, saf = af;
		var markSize = opts.markerOptions.size || 3;
		var fastMode = opts.markerOptions.fastMode;
		
		var tempCalcResult_u2p_x = series._xaxis.tempCalcResult_u2p;
		var tempCalcResult_u2p_y = series._yaxis.tempCalcResult_u2p;
		var tempCalcMin = series._xaxis.tempCalcMin;
		var tempCalcMax = series._yaxis.tempCalcMax;
		
		var fpart;
		
		var x0, y0, x1, y1, xf0, yf0, xf1, yf1;
		var xf, yf, rxf, ryf;
		var distance = (markSize / 2) / 1.0;
		
		var dx, dy, steep;
		var t;

		var gradient, xend, yend, xgap, xpxl1, ypxl1;
		var intery, xpxl2, ypxl2, fpart;
		
		var x0, x1, y0, y1;

		var xMinY, xMaxY;
		var graphFunc, condition;
		var alpha0, alpha1, rAlpha0, rAlpha1;
		var xdiff, ydiff; 

		if (!data) data = series.data;

		l = data.length;

		if (l) {
			if (isZoomed) {
				var xMin, xMax, yMin, yMax;
				xMin = series._xaxis.min;
				xMax = series._xaxis.max;
				yMin = series._yaxis.min;
				yMax = series._yaxis.max;
				
				if (fastMode) {
					for ( j = 0; j < l; j++) {
						point = data[j];
						
						x = point[0];
						y = point[1];
						
						if (x < xMin || x > xMax || y < yMin || y > yMax) {
							continue;
						}
						
						x = (x - tempCalcMin) * tempCalcResult_u2p_x;
						y = (y - tempCalcMax) * tempCalcResult_u2p_y;
		
						if (x > xUpperLimit || x < xLowerLimit || y < yLowerLimit || y > yUpperLimit) {
							continue;
						}

						x0 = (x - distance) | 0;
						y0 = (y - distance) | 0;
	
						x1 = x0 + markSize;
						y1 = y0 + markSize;
						
						for (x = x0; x <= x1; x++) {
							if (x < xLowerLimit || x > xUpperLimit) {
								continue;
							}
							dataIndex = dataWidth * y0 + x * 4;

							for (y = y0; y <= y1; y++) {
								if (y >= yLowerLimit && y <= yUpperLimit) {
									if (x == x0 || y == y0 || x == x1 || y == y1) {
										plotData[dataIndex] = sr;
										plotData[dataIndex + 1] = sg;
										plotData[dataIndex + 2] = sb;
										plotData[dataIndex + 3] = 255;
									} else {
										plotData[dataIndex] = r;
										plotData[dataIndex + 1] = g;
										plotData[dataIndex + 2] = b;
										plotData[dataIndex + 3] = 255;
									}
								}

								dataIndex += dataWidth;
							}
						}
					}
				} else {
					for ( j = 0; j < l; j++) {
						point = data[j];
						
						x = point[0];
						y = point[1];
						
						if (x < xMin || x > xMax || y < yMin || y > yMax) {
							continue;
						}
						
						x = (x - tempCalcMin) * tempCalcResult_u2p_x;
						y = (y - tempCalcMax) * tempCalcResult_u2p_y;
		
						if (x > xUpperLimit || x < xLowerLimit || y < yLowerLimit || y > yUpperLimit) {
							continue;
						}

						xf0 = x - distance;
						yf0 = y - distance;
						xf1 = xf0 + markSize - 0.5;
						yf1 = yf0 + markSize - 0.5;
						
						x0 = xf0 | 0;
						y0 = yf0 | 0;
						
						xf = xf0 - x0;
						yf = yf0 - y0;
						rxf = 1 - xf;
						ryf = 1 - yf;
	
						x1 = x0 + markSize;
						y1 = y0 + markSize;
						
						if (x0 < xLowerLimit) {
							x0 = xLowerLimit;
							rxf = 1;
						}
						if (x1 > xUpperLimit) {
							x1 = xUpperLimit;
							xf = 1;
						}
						
						if (y0 < yLowerLimit) {
							y0 = xLowerLimit;
							ryf = 1;
						}
						if (y1 > yUpperLimit) {
							y1 = yUpperLimit;
							yf = 1;
						}
						
						dataIndex = dataWidth * y0 + x0 * 4;
	
						alpha1 = rxf * ryf * af;
						
						alpha0 = plotData[dataIndex + 3] / 255;
						rAlpha0 = (1 - alpha0);
						rAlpha1 = (1 - alpha1);
		
						plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
						plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
						plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
						plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
	
						alpha1 = rxf * af;
	
						for (y = y0+1; y < y1; y++) {
							dataIndex += dataWidth;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						}
						
						if (y0 < y1) {
							dataIndex += dataWidth;
							
							alpha1 = rxf * yf * af;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						}
	
						for (x = x0+1; x < x1; x++) {
							dataIndex = dataWidth * y0 + x * 4;
	
							alpha1 = ryf * af;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
	
							alpha1 = af;
	
							for (y = y0+1; y < y1; y++) {
								dataIndex += dataWidth;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
							
							if (y0 < y1) {
								dataIndex += dataWidth;
								
								alpha1 = yf * af;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
						}
						
						if (x0 < x1) {
							dataIndex = dataWidth * y0 + x1 * 4;
	
							alpha1 = xf * ryf * af;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
	
							alpha1 = xf * af;
	
							for (y = y0+1; y < y1; y++) {
								dataIndex += dataWidth;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
							
							if (y0 < y1) {
								dataIndex += dataWidth;
								
								alpha1 = xf * yf * af;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
						}
						
						xf0 -= 0.5;
						yf0 -= 0.5;
						
						x0 = xf0;
						y0 = yf0;
						x1 = xf1;
						y1 = yf0;
				
						if (((x0 >= xLowerLimit && x0 <= xUpperLimit) || (x1 >= xLowerLimit && x1 <= xUpperLimit)) && (y0 >= yLowerLimit && y0 <= yUpperLimit)) {
							if (x0 > xUpperLimit) {
								x0 = xUpperLimit;
							} else if (x0 < xLowerLimit) {
								x0 = xLowerLimit;
							}
				
							if (x1 > xUpperLimit) {
								x1 = xUpperLimit;
							} else if (x1 < xLowerLimit) {
								x1 = xLowerLimit;
							}
							
							xdiff = x1 - x0;
							ydiff = y1 - y0;
							steep = (ydiff < 0 ? -ydiff : ydiff) > (xdiff < 0 ? -xdiff : xdiff);
									
							if (steep) {
							  t = x0; x0 = y0; y0 = t;
							  t = x1; x1 = y1; y1 = t;
							}
							if (x0 > x1) {
								t = x0; x0 = x1; x1 = t;
								t = y0; y0 = y1; y1 = t;
							}
							
							
							dx = x1 - x0;
							dy = y1 - y0;
							gradient = dy / dx;
							
							// handle first endpoint
							xend = (0.5 + x0) | 0;
							yend = y0 + gradient * (xend - x0);
							xpxl1 = xend;   //this will be used in the main loop
							ypxl1 = yend | 0;
							
							fpart = yend - (yend | 0);
				
							if (steep) {
								dataIndex = dataWidth * xpxl1 + ypxl1 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl1 + xpxl1 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
				
							intery = yend + gradient; // first y-intersection for the main loop
							
							// handle second endpoint
							xend = (0.5 + x1) | 0;
							yend = y1 + gradient * (xend - x1);
							xpxl2 = xend; //this will be used in the main loop
							ypxl2 = yend | 0;
							
							fpart = yend - (yend | 0);
							
							if (steep) {
								dataIndex = dataWidth * xpxl2 + ypxl2 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl2 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl2 + xpxl2 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (y + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
					
							// main loop
							
							for (x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
								y = intery | 0;
								fpart = intery - (intery | 0);
							
								if (steep) {
									dataIndex = dataWidth * x + y * 4;
					
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 < xUpperLimit) {
										dataIndex += 4;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								} else {
									dataIndex = dataWidth * y + x * 4;
									
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 <= yUpperLimit) {
										dataIndex += dataWidth;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								}
					
							  intery = intery + gradient;
							}
						}
						
						x0 = xf1;
						y0 = yf0;
						x1 = xf1;
						y1 = yf1;
				
						if (((y0 >= yLowerLimit && y0 <= yUpperLimit) || (y1 >= yLowerLimit && y1 <= yUpperLimit)) && (x1 >= xLowerLimit && x1 <= xUpperLimit)) {
							if (y0 > yUpperLimit) {
								y0 = yUpperLimit;
							} else if (y0 < yLowerLimit) {
								y0 = yLowerLimit;
							}
							if (y1 > yUpperLimit) {
								y1 = yUpperLimit;
							} else if (y1 < yLowerLimit) {
								y1 = yLowerLimit;
							}
							
							xdiff = x1 - x0;
							ydiff = y1 - y0;
							steep = (ydiff < 0 ? -ydiff : ydiff) > (xdiff < 0 ? -xdiff : xdiff);
									
							if (steep) {
							  t = x0; x0 = y0; y0 = t;
							  t = x1; x1 = y1; y1 = t;
							}
							if (x0 > x1) {
								t = x0; x0 = x1; x1 = t;
								t = y0; y0 = y1; y1 = t;
							}
							
							
							dx = x1 - x0;
							dy = y1 - y0;
							gradient = dy / dx;
							
							// handle first endpoint
							xend = (0.5 + x0) | 0;
							yend = y0 + gradient * (xend - x0);
							xpxl1 = xend;   //this will be used in the main loop
							ypxl1 = yend | 0;
							
							fpart = yend - (yend | 0);
				
							if (steep) {
								dataIndex = dataWidth * xpxl1 + ypxl1 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl1 + xpxl1 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
				
							intery = yend + gradient; // first y-intersection for the main loop
							
							// handle second endpoint
							xend = (0.5 + x1) | 0;
							yend = y1 + gradient * (xend - x1);
							xpxl2 = xend; //this will be used in the main loop
							ypxl2 = yend | 0;
							
							fpart = yend - (yend | 0);
							
							if (steep) {
								dataIndex = dataWidth * xpxl2 + ypxl2 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl2 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl2 + xpxl2 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (y + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
					
							// main loop
							
							for (x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
								y = intery | 0;
								fpart = intery - (intery | 0);
							
								if (steep) {
									dataIndex = dataWidth * x + y * 4;
					
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 < xUpperLimit) {
										dataIndex += 4;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								} else {
									dataIndex = dataWidth * y + x * 4;
									
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 <= yUpperLimit) {
										dataIndex += dataWidth;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								}
					
							  intery = intery + gradient;
							}
						}
						
						x0 = xf1;
						y0 = yf1;
						x1 = xf0;
						y1 = yf1;
				
						if (((x0 >= xLowerLimit && x0 <= xUpperLimit) || (x1 >= xLowerLimit && x1 <= xUpperLimit)) && (y1 >= yLowerLimit && y1 <= yUpperLimit)) {
							if (x0 > xUpperLimit) {
								x0 = xUpperLimit;
							} else if (x0 < xLowerLimit) {
								x0 = xLowerLimit;
							}
				
							if (x1 > xUpperLimit) {
								x1 = xUpperLimit;
							} else if (x1 < xLowerLimit) {
								x1 = xLowerLimit;
							}
							
							xdiff = x1 - x0;
							ydiff = y1 - y0;
							steep = (ydiff < 0 ? -ydiff : ydiff) > (xdiff < 0 ? -xdiff : xdiff);
									
							if (steep) {
							  t = x0; x0 = y0; y0 = t;
							  t = x1; x1 = y1; y1 = t;
							}
							if (x0 > x1) {
								t = x0; x0 = x1; x1 = t;
								t = y0; y0 = y1; y1 = t;
							}
							
							
							dx = x1 - x0;
							dy = y1 - y0;
							gradient = dy / dx;
							
							// handle first endpoint
							xend = (0.5 + x0) | 0;
							yend = y0 + gradient * (xend - x0);
							xpxl1 = xend;   //this will be used in the main loop
							ypxl1 = yend | 0;
							
							fpart = yend - (yend | 0);
				
							if (steep) {
								dataIndex = dataWidth * xpxl1 + ypxl1 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl1 + xpxl1 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
				
							intery = yend + gradient; // first y-intersection for the main loop
							
							// handle second endpoint
							xend = (0.5 + x1) | 0;
							yend = y1 + gradient * (xend - x1);
							xpxl2 = xend; //this will be used in the main loop
							ypxl2 = yend | 0;
							
							fpart = yend - (yend | 0);
							
							if (steep) {
								dataIndex = dataWidth * xpxl2 + ypxl2 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl2 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl2 + xpxl2 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (y + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
					
							// main loop
							
							for (x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
								y = intery | 0;
								fpart = intery - (intery | 0);
							
								if (steep) {
									dataIndex = dataWidth * x + y * 4;
					
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 < xUpperLimit) {
										dataIndex += 4;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								} else {
									dataIndex = dataWidth * y + x * 4;
									
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 <= yUpperLimit) {
										dataIndex += dataWidth;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								}
					
							  intery = intery + gradient;
							}
						}
						
						x0 = xf0;
						y0 = yf1;
						x1 = xf0;
						y1 = yf0;
				
						if (((y0 >= yLowerLimit && y0 <= yUpperLimit) || (y1 >= yLowerLimit && y1 <= yUpperLimit)) && (x0 >= xLowerLimit && x0 <= xUpperLimit)) {
							if (y0 > yUpperLimit) {
								y0 = yUpperLimit;
							} else if (y0 < yLowerLimit) {
								y0 = yLowerLimit;
							}
							if (y1 > yUpperLimit) {
								y1 = yUpperLimit;
							} else if (y1 < yLowerLimit) {
								y1 = yLowerLimit;
							}
							
							xdiff = x1 - x0;
							ydiff = y1 - y0;
							steep = (ydiff < 0 ? -ydiff : ydiff) > (xdiff < 0 ? -xdiff : xdiff);
									
							if (steep) {
							  t = x0; x0 = y0; y0 = t;
							  t = x1; x1 = y1; y1 = t;
							}
							if (x0 > x1) {
								t = x0; x0 = x1; x1 = t;
								t = y0; y0 = y1; y1 = t;
							}
							
							
							dx = x1 - x0;
							dy = y1 - y0;
							gradient = dy / dx;
							
							// handle first endpoint
							xend = (0.5 + x0) | 0;
							yend = y0 + gradient * (xend - x0);
							xpxl1 = xend;   //this will be used in the main loop
							ypxl1 = yend | 0;
							
							fpart = yend - (yend | 0);
				
							if (steep) {
								dataIndex = dataWidth * xpxl1 + ypxl1 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl1 + xpxl1 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
				
							intery = yend + gradient; // first y-intersection for the main loop
							
							// handle second endpoint
							xend = (0.5 + x1) | 0;
							yend = y1 + gradient * (xend - x1);
							xpxl2 = xend; //this will be used in the main loop
							ypxl2 = yend | 0;
							
							fpart = yend - (yend | 0);
							
							if (steep) {
								dataIndex = dataWidth * xpxl2 + ypxl2 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl2 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl2 + xpxl2 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (y + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
					
							// main loop
							
							for (x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
								y = intery | 0;
								fpart = intery - (intery | 0);
							
								if (steep) {
									dataIndex = dataWidth * x + y * 4;
					
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 < xUpperLimit) {
										dataIndex += 4;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								} else {
									dataIndex = dataWidth * y + x * 4;
									
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 <= yUpperLimit) {
										dataIndex += dataWidth;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								}
					
							  intery = intery + gradient;
							}
						}
					}
				}
			} else {
				if (fastMode) {
					for ( j = 0; j < l; j++) {
						point = data[j];
						x = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
						y = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;
		
						if (x > xUpperLimit || x < xLowerLimit || y < yLowerLimit || y > yUpperLimit) {
							continue;
						}

						x0 = (x - distance) | 0;
						y0 = (y - distance) | 0;
	
						x1 = x0 + markSize;
						y1 = y0 + markSize;
						

						for (x = x0; x <= x1; x++) {
							if (x < xLowerLimit || x > xUpperLimit) {
								continue;
							}
							dataIndex = dataWidth * y0 + x * 4;

							for (y = y0; y <= y1; y++) {
								if (y >= yLowerLimit && y <= yUpperLimit) {
									if (x == x0 || y == y0 || x == x1 || y == y1) {
										plotData[dataIndex] = sr;
										plotData[dataIndex + 1] = sg;
										plotData[dataIndex + 2] = sb;
										plotData[dataIndex + 3] = 255;
									} else {
										plotData[dataIndex] = r;
										plotData[dataIndex + 1] = g;
										plotData[dataIndex + 2] = b;
										plotData[dataIndex + 3] = 255;
									}
								}

								dataIndex += dataWidth;
							}
						}
					}
				} else {
					for ( j = 0; j < l; j++) {
						point = data[j];
						x = (point[0] - tempCalcMin) * tempCalcResult_u2p_x;
						y = (point[1] - tempCalcMax) * tempCalcResult_u2p_y;

						xf0 = x - distance;
						yf0 = y - distance;
						xf1 = xf0 + markSize - 0.5;
						yf1 = yf0 + markSize - 0.5;
						
						x0 = xf0 | 0;
						y0 = yf0 | 0;
						
						xf = xf0 - x0;
						yf = yf0 - y0;
						rxf = 1 - xf;
						ryf = 1 - yf;
	
						x1 = x0 + markSize;
						y1 = y0 + markSize;
						
						if (x0 < xLowerLimit) {
							x0 = xLowerLimit;
							rxf = 1;
						}
						if (x1 > xUpperLimit) {
							x1 = xUpperLimit;
							xf = 1;
						}
						
						if (y0 < yLowerLimit) {
							y0 = xLowerLimit;
							ryf = 1;
						}
						if (y1 > yUpperLimit) {
							y1 = yUpperLimit;
							yf = 1;
						}
						
						dataIndex = dataWidth * y0 + x0 * 4;
	
						alpha1 = rxf * ryf * af;
						
						alpha0 = plotData[dataIndex + 3] / 255;
						rAlpha0 = (1 - alpha0);
						rAlpha1 = (1 - alpha1);
		
						plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
						plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
						plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
						plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
	
						alpha1 = rxf * af;
	
						for (y = y0+1; y < y1; y++) {
							dataIndex += dataWidth;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						}
						
						if (y0 < y1) {
							dataIndex += dataWidth;
							
							alpha1 = rxf * yf * af;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
						}
	
						for (x = x0+1; x < x1; x++) {
							dataIndex = dataWidth * y0 + x * 4;
	
							alpha1 = ryf * af;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
	
							alpha1 = af;
	
							for (y = y0+1; y < y1; y++) {
								dataIndex += dataWidth;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
							
							if (y0 < y1) {
								dataIndex += dataWidth;
								
								alpha1 = yf * af;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
						}
						
						if (x0 < x1) {
							dataIndex = dataWidth * y0 + x1 * 4;
	
							alpha1 = xf * ryf * af;
							
							alpha0 = plotData[dataIndex + 3] / 255;
							rAlpha0 = (1 - alpha0);
							rAlpha1 = (1 - alpha1);
			
							plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
							plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
							plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
							plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
	
							alpha1 = xf * af;
	
							for (y = y0+1; y < y1; y++) {
								dataIndex += dataWidth;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
							
							if (y0 < y1) {
								dataIndex += dataWidth;
								
								alpha1 = xf * yf * af;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = r * rAlpha0 + (r * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = g * rAlpha0 + (g * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = b * rAlpha0 + (b * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
							}
						}
						
						xf0 -= 0.5;
						yf0 -= 0.5;
						
						x0 = xf0;
						y0 = yf0;
						x1 = xf1;
						y1 = yf0;
				
						if (((x0 >= xLowerLimit && x0 <= xUpperLimit) || (x1 >= xLowerLimit && x1 <= xUpperLimit)) && (y0 >= yLowerLimit && y0 <= yUpperLimit)) {
							if (x0 > xUpperLimit) {
								x0 = xUpperLimit;
							} else if (x0 < xLowerLimit) {
								x0 = xLowerLimit;
							}
				
							if (x1 > xUpperLimit) {
								x1 = xUpperLimit;
							} else if (x1 < xLowerLimit) {
								x1 = xLowerLimit;
							}
							
							xdiff = x1 - x0;
							ydiff = y1 - y0;
							steep = (ydiff < 0 ? -ydiff : ydiff) > (xdiff < 0 ? -xdiff : xdiff);
									
							if (steep) {
							  t = x0; x0 = y0; y0 = t;
							  t = x1; x1 = y1; y1 = t;
							}
							if (x0 > x1) {
								t = x0; x0 = x1; x1 = t;
								t = y0; y0 = y1; y1 = t;
							}
							
							
							dx = x1 - x0;
							dy = y1 - y0;
							gradient = dy / dx;
							
							// handle first endpoint
							xend = (0.5 + x0) | 0;
							yend = y0 + gradient * (xend - x0);
							xpxl1 = xend;   //this will be used in the main loop
							ypxl1 = yend | 0;
							
							fpart = yend - (yend | 0);
				
							if (steep) {
								dataIndex = dataWidth * xpxl1 + ypxl1 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl1 + xpxl1 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
				
							intery = yend + gradient; // first y-intersection for the main loop
							
							// handle second endpoint
							xend = (0.5 + x1) | 0;
							yend = y1 + gradient * (xend - x1);
							xpxl2 = xend; //this will be used in the main loop
							ypxl2 = yend | 0;
							
							fpart = yend - (yend | 0);
							
							if (steep) {
								dataIndex = dataWidth * xpxl2 + ypxl2 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl2 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl2 + xpxl2 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (y + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
					
							// main loop
							
							for (x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
								y = intery | 0;
								fpart = intery - (intery | 0);
							
								if (steep) {
									dataIndex = dataWidth * x + y * 4;
					
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 < xUpperLimit) {
										dataIndex += 4;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								} else {
									dataIndex = dataWidth * y + x * 4;
									
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 <= yUpperLimit) {
										dataIndex += dataWidth;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								}
					
							  intery = intery + gradient;
							}
						}
						
						x0 = xf1;
						y0 = yf0;
						x1 = xf1;
						y1 = yf1;
				
						if (((y0 >= yLowerLimit && y0 <= yUpperLimit) || (y1 >= yLowerLimit && y1 <= yUpperLimit)) && (x1 >= xLowerLimit && x1 <= xUpperLimit)) {
							if (y0 > yUpperLimit) {
								y0 = yUpperLimit;
							} else if (y0 < yLowerLimit) {
								y0 = yLowerLimit;
							}
							if (y1 > yUpperLimit) {
								y1 = yUpperLimit;
							} else if (y1 < yLowerLimit) {
								y1 = yLowerLimit;
							}
							
							xdiff = x1 - x0;
							ydiff = y1 - y0;
							steep = (ydiff < 0 ? -ydiff : ydiff) > (xdiff < 0 ? -xdiff : xdiff);
									
							if (steep) {
							  t = x0; x0 = y0; y0 = t;
							  t = x1; x1 = y1; y1 = t;
							}
							if (x0 > x1) {
								t = x0; x0 = x1; x1 = t;
								t = y0; y0 = y1; y1 = t;
							}
							
							
							dx = x1 - x0;
							dy = y1 - y0;
							gradient = dy / dx;
							
							// handle first endpoint
							xend = (0.5 + x0) | 0;
							yend = y0 + gradient * (xend - x0);
							xpxl1 = xend;   //this will be used in the main loop
							ypxl1 = yend | 0;
							
							fpart = yend - (yend | 0);
				
							if (steep) {
								dataIndex = dataWidth * xpxl1 + ypxl1 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl1 + xpxl1 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
				
							intery = yend + gradient; // first y-intersection for the main loop
							
							// handle second endpoint
							xend = (0.5 + x1) | 0;
							yend = y1 + gradient * (xend - x1);
							xpxl2 = xend; //this will be used in the main loop
							ypxl2 = yend | 0;
							
							fpart = yend - (yend | 0);
							
							if (steep) {
								dataIndex = dataWidth * xpxl2 + ypxl2 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl2 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl2 + xpxl2 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (y + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
					
							// main loop
							
							for (x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
								y = intery | 0;
								fpart = intery - (intery | 0);
							
								if (steep) {
									dataIndex = dataWidth * x + y * 4;
					
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 < xUpperLimit) {
										dataIndex += 4;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								} else {
									dataIndex = dataWidth * y + x * 4;
									
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 <= yUpperLimit) {
										dataIndex += dataWidth;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								}
					
							  intery = intery + gradient;
							}
						}
						
						x0 = xf1;
						y0 = yf1;
						x1 = xf0;
						y1 = yf1;
				
						if (((x0 >= xLowerLimit && x0 <= xUpperLimit) || (x1 >= xLowerLimit && x1 <= xUpperLimit)) && (y1 >= yLowerLimit && y1 <= yUpperLimit)) {
							if (x0 > xUpperLimit) {
								x0 = xUpperLimit;
							} else if (x0 < xLowerLimit) {
								x0 = xLowerLimit;
							}
				
							if (x1 > xUpperLimit) {
								x1 = xUpperLimit;
							} else if (x1 < xLowerLimit) {
								x1 = xLowerLimit;
							}
							
							xdiff = x1 - x0;
							ydiff = y1 - y0;
							steep = (ydiff < 0 ? -ydiff : ydiff) > (xdiff < 0 ? -xdiff : xdiff);
									
							if (steep) {
							  t = x0; x0 = y0; y0 = t;
							  t = x1; x1 = y1; y1 = t;
							}
							if (x0 > x1) {
								t = x0; x0 = x1; x1 = t;
								t = y0; y0 = y1; y1 = t;
							}
							
							
							dx = x1 - x0;
							dy = y1 - y0;
							gradient = dy / dx;
							
							// handle first endpoint
							xend = (0.5 + x0) | 0;
							yend = y0 + gradient * (xend - x0);
							xpxl1 = xend;   //this will be used in the main loop
							ypxl1 = yend | 0;
							
							fpart = yend - (yend | 0);
				
							if (steep) {
								dataIndex = dataWidth * xpxl1 + ypxl1 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl1 + xpxl1 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
				
							intery = yend + gradient; // first y-intersection for the main loop
							
							// handle second endpoint
							xend = (0.5 + x1) | 0;
							yend = y1 + gradient * (xend - x1);
							xpxl2 = xend; //this will be used in the main loop
							ypxl2 = yend | 0;
							
							fpart = yend - (yend | 0);
							
							if (steep) {
								dataIndex = dataWidth * xpxl2 + ypxl2 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl2 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl2 + xpxl2 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (y + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
					
							// main loop
							
							for (x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
								y = intery | 0;
								fpart = intery - (intery | 0);
							
								if (steep) {
									dataIndex = dataWidth * x + y * 4;
					
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 < xUpperLimit) {
										dataIndex += 4;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								} else {
									dataIndex = dataWidth * y + x * 4;
									
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 <= yUpperLimit) {
										dataIndex += dataWidth;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								}
					
							  intery = intery + gradient;
							}
						}
						
						x0 = xf0;
						y0 = yf1;
						x1 = xf0;
						y1 = yf0;
				
						if (((y0 >= yLowerLimit && y0 <= yUpperLimit) || (y1 >= yLowerLimit && y1 <= yUpperLimit)) && (x0 >= xLowerLimit && x0 <= xUpperLimit)) {
							if (y0 > yUpperLimit) {
								y0 = yUpperLimit;
							} else if (y0 < yLowerLimit) {
								y0 = yLowerLimit;
							}
							if (y1 > yUpperLimit) {
								y1 = yUpperLimit;
							} else if (y1 < yLowerLimit) {
								y1 = yLowerLimit;
							}
							
							xdiff = x1 - x0;
							ydiff = y1 - y0;
							steep = (ydiff < 0 ? -ydiff : ydiff) > (xdiff < 0 ? -xdiff : xdiff);
									
							if (steep) {
							  t = x0; x0 = y0; y0 = t;
							  t = x1; x1 = y1; y1 = t;
							}
							if (x0 > x1) {
								t = x0; x0 = x1; x1 = t;
								t = y0; y0 = y1; y1 = t;
							}
							
							
							dx = x1 - x0;
							dy = y1 - y0;
							gradient = dy / dx;
							
							// handle first endpoint
							xend = (0.5 + x0) | 0;
							yend = y0 + gradient * (xend - x0);
							xpxl1 = xend;   //this will be used in the main loop
							ypxl1 = yend | 0;
							
							fpart = yend - (yend | 0);
				
							if (steep) {
								dataIndex = dataWidth * xpxl1 + ypxl1 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl1 + xpxl1 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl1 + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
				
							intery = yend + gradient; // first y-intersection for the main loop
							
							// handle second endpoint
							xend = (0.5 + x1) | 0;
							yend = y1 + gradient * (xend - x1);
							xpxl2 = xend; //this will be used in the main loop
							ypxl2 = yend | 0;
							
							fpart = yend - (yend | 0);
							
							if (steep) {
								dataIndex = dataWidth * xpxl2 + ypxl2 * 4;
				
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (ypxl2 + 1 < xUpperLimit) {
									dataIndex += 4;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							} else {
								dataIndex = dataWidth * ypxl2 + xpxl2 * 4;
								
								alpha0 = plotData[dataIndex + 3] / 255;
								alpha1 = 1 - fpart;
								rAlpha0 = (1 - alpha0);
								rAlpha1 = (1 - alpha1);
				
								plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
								plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
								plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
								plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								
								if (y + 1 <= yUpperLimit) {
									dataIndex += dataWidth;
								
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
								}
							}
					
							// main loop
							
							for (x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
								y = intery | 0;
								fpart = intery - (intery | 0);
							
								if (steep) {
									dataIndex = dataWidth * x + y * 4;
					
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 < xUpperLimit) {
										dataIndex += 4;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								} else {
									dataIndex = dataWidth * y + x * 4;
									
									alpha0 = plotData[dataIndex + 3] / 255;
									alpha1 = 1 - fpart;
									rAlpha0 = (1 - alpha0);
									rAlpha1 = (1 - alpha1);
					
									plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
									plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
									plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
									plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									
									if (y + 1 <= yUpperLimit) {
										dataIndex += dataWidth;
									
										alpha0 = plotData[dataIndex + 3] / 255;
										alpha1 = fpart;
										rAlpha0 = (1 - alpha0);
										rAlpha1 = (1 - alpha1);
						
										plotData[dataIndex] = sr * rAlpha0 + (sr * alpha1 + plotData[dataIndex] * rAlpha1) * alpha0;
										plotData[dataIndex + 1] = sg * rAlpha0 + (sg * alpha1 + plotData[dataIndex+1] * rAlpha1) * alpha0;
										plotData[dataIndex + 2] = sb * rAlpha0 + (sb * alpha1 + plotData[dataIndex+2] * rAlpha1) * alpha0;
										plotData[dataIndex + 3] = (alpha0 + (1-alpha0) * alpha1) * 255;
									}
								}
					
							  intery = intery + gradient;
							}
						}
					}
				}
			}

		}

		tempCalcResult_u2p_x = tempCalcResult_u2p_y = tempCalcMin = tempCalcMax = null;
		dataIndex = j = newIndex = l = null;
		x = y = null;
	}

})(jQuery); ;/**
	jqPlot Custom Axes Plugin
	2013-04-02, Roy Choi
*/
(function($) {
    /**
     * Class: $.jqplot.CustomAxes
     * Plugin which will show with custom axes.
     */
    $.jqplot.CustomAxes = function(options) {
        // Group: Properties
        //
        //prop: enable
        // true to enable show with custom axes.
        this.enable = $.jqplot.config.enablePlugins;
        // prop: show
        // true to show with custom axes
        this.show  = false;
        // prop: columnIndex
        // index of custom axis in the point data
        this.columnIndex = null;
        // prop: location
        // 'top' or 'bottom'
        this.location = 'bottom';
        // prop: padding
        // 
        this.padding = 0;
        // prop: showTicks
        //
        //this.showTicks = false;
        // prop: showGridLine
        //
        this.showGridLine = false;
        // prop: renderer
        //
        this.renderer = $.jqplot.CustomAxes.LabelRenderer;
     // prop: markerOptions
        //
        this.rendererOptions = {};
        
        this.showTicks = false;
        this._ticks = null;
        
        this.onlyFirstSeries = false;
        
        this.gridLineColor = '#ccc';
        this.gridLineWidth = 1;
        
        this.fromCurrentMin = false;
        
        this.axis = 'xaxis';
        
        this.data = null;
        this.xaxis = 'xaxis';
        this.yaxis = 'yaxis';

        $.extend(true, this, options);
    };
    
    // axis.renderer.tickrenderer.formatter
    
    // called with scope of plot
    $.jqplot.CustomAxes.init = function (target, data, opts){
        var options = opts || {};
        this.plugins.customAxes = {
        	_labelElem: null,
        	_markElem: null,
        	_canvas: null,
        	axes: {}
        };
        
        for (var ax in options.customAxes) {
        	this.plugins.customAxes.axes[ax] = new $.jqplot.CustomAxes(options.customAxes[ax]);
        }
    };

    // 
    $.jqplot.CustomAxes.postPlotDraw = function() {
    	var ca = this.plugins.customAxes;
    	
    	if (ca._labelElem) {
    		ca._labelElem.emptyForce();
    		ca._labelElem.remove();
    	}
    	if (ca._markElem) {
    		ca._markElem.emptyForce();
    		ca._markElem.remove();
    	}
    	
    	ca._labelElem = $('<div />');
    	ca._markElem = $('<div />');
    	
    	ca._labelElem.css({
    		width: this._plotDimensions.width - this._gridPadding.left - this._gridPadding.right,
    		height: this._plotDimensions.height - this._gridPadding.top - this._gridPadding.bottom,
    		position: 'absolute',
    		left: this._gridPadding.left+'px',
    		top: this._gridPadding.top+'px'
    	});
    	ca._markElem.css({
    		width: this._plotDimensions.width - this._gridPadding.left - this._gridPadding.right,
    		height: this._plotDimensions.height - this._gridPadding.top - this._gridPadding.bottom,
    		position: 'absolute',
    		overflow: 'hidden',
    		left: this._gridPadding.left+'px',
    		top: this._gridPadding.top+'px'
    	});
    	this.eventCanvas._elem.before(ca._labelElem);
    	this.eventCanvas._elem.before(ca._markElem);
    	
    	for (var ax in this.plugins.customAxes.axes) {
    		if(this.plugins.customAxes.axes[ax].show) {
    			this.plugins.customAxes.axes[ax].draw(this);
    		}
      }
    };
    
    $.jqplot.preInitHooks.push($.jqplot.CustomAxes.init);
    $.jqplot.postDrawHooks.push($.jqplot.CustomAxes.postPlotDraw);

	$.jqplot.CustomAxes.prototype.draw = function(plot) {
		if (/*this.enable && */this.show) {
			this.renderer.set.call(this, this.rendererOptions, plot);
			this.renderer.draw.call(this, plot);
		}
	};
	
	$.jqplot.CustomAxes.prototype.drawGridLine = function(points, plot) {
		var ctx;
		
		ctx = plot.grid._ctx;
		
		ctx.save();
		
		ctx.lineWidth = this.gridLineWidth;
		ctx.strokeStyle = this.gridLineColor;
		
		ctx.beginPath();
		ctx.moveTo(points[0][0], points[0][1]);
		ctx.lineTo(points[1][0], points[1][1]);
		
		ctx.stroke();
		ctx.restore();
		
		ctx = null;
	};
	
	$.jqplot.CustomAxes.MarkRenderer = function() {
	};
	
	$.jqplot.CustomAxes.MarkRenderer.set = function(opts) {
		this.size = opts.size || 5;
    	this.style = opts.style || 'filledSquare';
    	this.color = opts.color || 'rgba(0,0,0,0)';
    	this.lineWidth = opts.lineWidth || 3;
    	this.linePattern = opts.linePattern || 'solid';
    	this.lineJoin = opts.lineJoin || 'miter';
    	this.lineCap = opts.lineCap || 'round';
    	this.closePath = opts.closePath || false;
    	this.fill = opts.fill || false;
    	this.fillRect = opts.fillRect || false;
    	this.fillStyle = opts.fillStyle || null;
    	this.stroke = opts.stroke || false;
    	this.strokeRect = opts.strokeRect || false;
    	this.strokeStyle = opts.strokeStyle || null;
    	this.markFormatter = opts.markFormatter || null;
    	this.markCondition = opts.markCondition || function() {return false;};
    	this.fillToNext = opts.fillToNext === false ? false : true;
    	
    	this.shapeRenderer = new $.jqplot.ShapeRenderer();
    	
    	var shopt = {fill:false, isarc:false, stroke:false, strokeStyle:this.strokeStyle, fillStyle:this.color, lineWidth:this.lineWidth, closePath:true};
        if (this.style.indexOf('filled') != -1) {
            shopt.fill = true;
        }
        if (this.style.indexOf('ircle') != -1) {
            shopt.isarc = true;
            shopt.closePath = false;
        }

		if (this.stroke) {
			shopt.stroke = true;
			shopt.strokeStyle = this.strokeStyle;
		}
        this.shapeRenderer.init(shopt);
	};
	
    $.jqplot.CustomAxes.prototype.drawDiamond = function(mark, ctx, fill, options) {
        var stretch = 1.2;
        var x = mark.start + (mark.stop - mark.start)/2;
        var y = this.size / 2;
        var dx = this.size/2/stretch;
        var dy = this.size/2*stretch;
        var points = [[x-dx, y], [x, y+dy], [x+dx, y], [x, y-dy]];

        this.shapeRenderer.draw(ctx, points, options);
    };
    
    $.jqplot.CustomAxes.prototype.drawPlus = function(mark, ctx, fill, options) {
        var stretch = 1.0;
        var x = mark.start + (mark.stop - mark.start)/2;
        var y = this.size / 2;
        var dx = this.size/2*stretch;
        var dy = this.size/2*stretch;
        var points1 = [[x, y-dy], [x, y+dy]];
        var points2 = [[x+dx, y], [x-dx, y]];
        var opts = $.extend(true, {}, this.options, {closePath:false});

        this.shapeRenderer.draw(ctx, points1, opts);
        this.shapeRenderer.draw(ctx, points2, opts);
    };
    
    $.jqplot.CustomAxes.prototype.drawX = function(mark, ctx, fill, options) {
        var stretch = 1.0;
        var x = mark.start + (mark.stop - mark.start)/2;
        var y = this.size / 2;
        var dx = this.size/2*stretch;
        var dy = this.size/2*stretch;
        var opts = $.extend(true, {}, this.options, {closePath:false});
        var points1 = [[x-dx, y-dy], [x+dx, y+dy]];
        var points2 = [[x-dx, y+dy], [x+dx, y-dy]];

        this.shapeRenderer.draw(ctx, points1, opts);
        this.shapeRenderer.draw(ctx, points2, opts);
    };
    
    $.jqplot.CustomAxes.prototype.drawDash = function(mark, ctx, fill, options) {
        var stretch = 1.0;
        var x = mark.start + (mark.stop - mark.start)/2;
        var y = this.size / 2;
        var dx = this.size/2*stretch;
        var dy = this.size/2*stretch;
        var points = [[x-dx, y], [x+dx, y]];

        this.shapeRenderer.draw(ctx, points, options);
    };
    
    $.jqplot.CustomAxes.prototype.drawLine = function(mark, ctx, fill, options) {
    	var y = this.size / 2;
    	var p1 = [mark.start, y];
    	var p2 = [mark.stop, y];
        var points = [p1, p2];

        this.shapeRenderer.draw(ctx, points, options);
    };
    
    $.jqplot.CustomAxes.prototype.drawSquare = function(mark, ctx, fill, options) {
        var points = [[mark.start, 0], [mark.start, this.size], [mark.stop, this.size], [mark.stop, 0]];

        this.shapeRenderer.draw(ctx, points, options);
    };
    
    $.jqplot.CustomAxes.prototype.drawCircle = function(mark, ctx, fill, options) {
    	var y = this.size / 2;
        var radius = this.size/2;
        var end = 2*Math.PI;
        var points = [mark.x, y, radius, 0, end, true];

        this.shapeRenderer.draw(ctx, points, options);
    };
	
	$.jqplot.CustomAxes.MarkRenderer.draw = function(plot) {
		if (typeof this.markCondition != 'function' || plot.data.length <= 0) {
			return;
		}
		
		var series = plot.series;
		var i, j, sd, min, max;

		var start = false;
		var condition;
		var mark;
		
		this._canvas = new $.jqplot.GenericCanvas();
		
		var gridPadding = {
			left: plot._gridPadding.left,
			right: plot._gridPadding.right,
			top: plot._gridPadding.top+(this.location=='top'?-this.padding:0),
			bottom: plot._gridPadding.bottom+(this.location=='bottom'?this.padding:0)
		};

		plot.grid.canvas._elem.before(this._canvas.createElement(gridPadding, 'jqplot-customaxes-canvas',
				{width:plot._plotDimensions.width, height: this.size+gridPadding.top+gridPadding.bottom}, plot));
		this._canvas.setContext();
		
		this._canvas._elem.css({
			position: 'absolute'
		});
		
		var ctx = this._canvas._ctx;
		ctx.clearRect(0,0,ctx.canvas.width, ctx.canvas.height);
		
		if (this.color && !this.fillStyle) {
            this.fillStyle = this.color;
        }
        if (this.color && !this.strokeStyle) {
        	this.strokeStyle = this.color;
        }
        
        var drawFunction;
        var fill;
        
    	switch (this.style) {
	        case 'diamond':
	        	drawFunction = this.drawDiamond;
	        	fill = false;
	            break;
	        case 'filledDiamond':
	            drawFunction = this.drawDiamond;
	        	fill = true;
	            break;
	        case 'circle':
	            drawFunction = this.drawCircle;
	        	fill = false;
	            break;
	        case 'filledCircle':
	            drawFunction = this.drawCircle;
	        	fill = true;
	            break;
	        case 'square':
	            drawFunction = this.drawSquare;
	        	fill = false;
	            break;
	        case 'filledSquare':
	            drawFunction = this.drawSquare;
	        	fill = true;
	            break;
	        case 'x':
	            drawFunction = this.drawX;
	        	fill = true;
	            break;
	        case 'plus':
	            drawFunction = this.drawPlus;
	        	fill = true;
	            break;
	        case 'dash':
	            drawFunction = this.drawDash;
	        	fill = true;
	            break;
	        case 'line':
	            drawFunction = this.drawLine;
	        	fill = false;
	            break;
	        default:
		        drawFunction = this.drawSquare;
		    	fill = true;
	            break;
    	}
    	
    	var draw = function(mark) {
    		if (this.fillToNext && mark.stop - mark.start < 1) {
        		mark.stop = mark.start + 1;
        	}
        	drawFunction.call(this, mark, ctx, fill, this);
    	};
		
		for(i=0; i<series.length; i++) {
			sd = series[i].data;
			
			min = plot.axes[series[i].xaxis].min;
			max = plot.axes[series[i].xaxis].max;
			
			mark = {
				x: null,
				start: null,
				stop: null
			};
			
			for(j=0; j<sd.length; j++) {
				if (min !== null && sd[j][0] < min) {
					continue;
				} else if (max !== null && max < sd[j][0]) {
					break;
				}
				
				condition = this.markCondition.call(this, sd[j][this.columnIndex]);
				
				if (start  && !condition) {
					mark.stop = series_u2p(sd[j][0])-plot._gridPadding.left;
					draw.call(this, mark);
					mark = {
						start: null,
						stop: null
					};
					start = false;
				} else if (!start && condition) {
					mark.x = series_u2p(sd[j][0])-plot._gridPadding.left;
					mark.start = mark.x;
					if (!this.fillToNext) {
						mark.start -= this.size / 2;
						mark.stop = mark.start + this.size;
						draw.call(this, mark);
						mark = {
							start: null,
							stop: null
						};
						start = false;
					} else {
						start = true;
					}
				}
			}
			
			if (typeof mark.start === 'number' && !mark.stop) {
				mark.stop = mark.start+this.size;
				draw.call(this, mark);
			}
			
			mark = null;
		}
        
        ctx = drawFunction = fill = options = null;

	};
	
	$.jqplot.CustomAxes.LabelRenderer = function() {
	};
	
	$.jqplot.CustomAxes.LabelRenderer.set = function(opts, plot) {
      this.mergedLabel = opts.mergedLabel === false ? false : true;
      this.labelStyle = opts.labelStyle || null;
      this.labelAlign = opts.labelAlign || 'middle';
      this.formatter = opts.formatter || null;
      this.showLabel = opts.showLabel === false ? false : true;
      this.labelType = opts.labelType || 'single';
      this.labelOffset = $.extend(true, {top: 0, left: 0}, opts.labelOffset ? opts.labelOffset : {});
      this.selectArea = {
      	height: 0,
      	show: false,
      	color: '#ccc',
      	lineColor: '#fff',
      	selectedColor: {},
      	toggleBodyEvent: null,
      	toggleSelect: null,
      	selected: []
      };
      this.selectArea = $.extend(true, {}, this.selectArea, opts.selectArea);
      this.selectArea.selectBlocks = [];
    	if (this.selectArea.show && (this.onlyFirstSeries || $.isArray(this.data)) && this.mergedLabel && this.labelType === 'block') {
    		this._canvas = new $.jqplot.GenericCanvas();
    		plot.grid._elem.after(this._canvas.createElement({
    			top: plot._gridPadding.top - this.selectArea.height,
    			left: plot._gridPadding.left,
    			right: plot._gridPadding.right,
    			bottom: plot._plotDimensions.height - plot._gridPadding.top
    		}, 'jqplot-customaxes-canvas', plot._plotDimensions, plot));
    		
    		this._canvas.setContext();
    		this._canvas._elem.bind('selectstart', false);
    	}
	};
	
	$.jqplot.CustomAxes.LabelRenderer.fillBlock = function(x1, x2, color) {
		var ctx = this._canvas._ctx;
		
		ctx.save();
		
		ctx.fillStyle = color;
		
		ctx.fillRect(x1, 0, x2-x1, ctx.canvas.height, color);
		
		ctx.restore();
		ctx = null;
	};
	
	$.jqplot.CustomAxes.LabelRenderer.onSelectBlock = function(ev, xaxis, selectArea, plot) {
		var series_p2u = xaxis.series_p2u;
		var series_u2p = xaxis.series_u2p;
	
		var selectBlocks = selectArea.selectBlocks;
		var l = selectBlocks.length;
		var last = l - 1;
		var nextX, next;
		
		var go = this._canvas._elem.offset();
		
		var eventX = ev.pageX - go.left;

		for (var i = 0 ; i < l; i++) {
			if (i === 0 && eventX < selectBlocks[i].x) {
				return;
			}
			
			nextX = (i === last) ? this._canvas._ctx.canvas.width - 1 : selectBlocks[i+1].x;
			next = (i === last) ? plot.axes.xaxis.max : selectBlocks[i+1].data[0];
			
			if (eventX >= selectBlocks[i].x && eventX < nextX) {
				selectBlocks[i].selected = !selectBlocks[i].selected;
				if (selectBlocks[i].selected) {
					this.renderer.fillBlock.call(this, selectBlocks[i].x + 2, nextX - 2, selectArea.selectedColor);
				} else {
					this.renderer.fillBlock.call(this, selectBlocks[i].x + 1, nextX - 1, selectArea.color);
				}
				
				if ($.isFunction(selectArea.toggleSelect)) {
					selectArea.toggleSelect.call(this, ev, selectBlocks[i].selected, {
						start: selectBlocks[i].data[0],
						next: next
					}, selectBlocks[i].pointIndex, selectBlocks[i].seriesIndex, selectBlocks[i].data, plot);
				}
				return;
			}
		}
	};
	
	  /*
		function getEventPosition(ev) {
					var plot = ev.data.plot;
					var go = plot.eventCanvas._elem.offset();
					var gridPos = {x:ev.pageX - go.left, y:ev.pageY - go.top};
					var dataPos = {xaxis:null, yaxis:null, x2axis:null, y2axis:null, y3axis:null, y4axis:null, y5axis:null, y6axis:null, y7axis:null, y8axis:null, y9axis:null, yMidAxis:null};
					var an = ['xaxis', 'yaxis', 'x2axis', 'y2axis', 'y3axis', 'y4axis', 'y5axis', 'y6axis', 'y7axis', 'y8axis', 'y9axis', 'yMidAxis'];
					var ax = plot.axes;
					var n, axis;
					for (n=11; n>0; n--) {
							axis = an[n-1];
							if (ax[axis].show) {
									dataPos[axis] = ax[axis].series_p2u(gridPos[axis.charAt(0)]);
							}
					}
							 return {offsets:go, gridPos:gridPos, dataPos:dataPos};
			}*/
		
	
	$.jqplot.CustomAxes.LabelRenderer.drawSelectAreaLine = function(x, color, plot) {
		var ctx;
		
		ctx = this._canvas._ctx;
		
		ctx.save();

		ctx.lineWidth = 1;
		ctx.strokeStyle = color;
		
		ctx.beginPath();
		ctx.moveTo(x, 0);
		ctx.lineTo(x, ctx.canvas.height);
		
		ctx.stroke();
		ctx.restore();
		
		ctx = null;
	};
	
	$.jqplot.CustomAxes.LabelRenderer.draw = function(plot) {
		var series = plot.series;
		var i, j, sd, min, max;
		var lastValue = null;
		var lastData = null;

		var css = {
				position: 'absolute',
				width: 'auto'
			};
		
		var x, xaxis, yaxis, yMin, yMax;
		var series_u2p = null;
		var selectArea = this.selectArea;
		var str;
		var elem;
		var columnIndex = this.columnIndex;
		var renderer = this.renderer;
		var self = this;
		var align, axis;
		var gridPadding = plot._gridPadding;
		
		var axisIndex = this.axis === 'xaxis' ? 0 : 1;
		
		// TODO: support yaxis
		if ($.isArray(this.data)) {
			if (this.data.length === 0 || series.length === 0) {
				return;
			}
			
			sd = this.data;
			
			xaxis = plot.axes[this.xaxis];
			series_u2p = xaxis.u2p;
			yaxis = plot.axes[this.yaxis];
			
			min = plot.axes[this.axis].min;
			max = plot.axes[this.axis].max;
			
			if (selectArea.show && this.mergedLabel && this.labelType === 'block') {
				this._canvas._elem.bind('click', function(ev) {
					renderer.onSelectBlock.call(self, ev, xaxis, selectArea, plot);
				});
				
    		ctx = this._canvas._ctx;
				ctx.clearRect(0,0,ctx.canvas.width, ctx.canvas.height);

				ctx.fillStyle = this.selectArea.color;
				
				ctx.fillRect(0,0,ctx.canvas.width, ctx.canvas.height);
				ctx.restore();
			}
			
			for(j=0; j<sd.length; j++) {
				if ( sd[j][columnIndex] === null || isNaN(sd[j][columnIndex]) || (this.mergedLabel && lastValue === sd[j][columnIndex])) {
					continue;
				}
				
        if (selectArea.show && (this.onlyFirstSeries || $.isArray(this.data)) && this.mergedLabel && this.labelType === 'block') {
          x = series_u2p(sd[j][axisIndex]);
          
          selectArea.selectBlocks.push({
            selected: false,
            pointIndex: j,
            seriesIndex: i,
            data: sd[j],
            x: x - gridPadding.left
          });
          
          renderer.drawSelectAreaLine.call(this, x - gridPadding.left, selectArea.lineColor, plot);
        }
				
				if ( min !== null && sd[j][axisIndex] < min ) {
					lastValue = sd[j][columnIndex];
					continue;
				} else if (max !== null && max < sd[j][axisIndex]) {
					break;
				}
				
				str = '';
				if (typeof this.formatter === 'function') {
					str = this.formatter.call(this, sd[j][columnIndex], sd[j]);
					if (str === false) {
						continue;
					}
				} else {
					str = sd[j][columnIndex];
				}

				if (this.showLabel) {
					var elem = $('<div />');
					elem.html(str);
					
					elem.addClass('jqplot-axis');
					elem.css(css);
					if (this.labelStyle && typeof this.labelStyle === 'object') {
						elem.css(this.labelStyle);
					} else if (typeof this.labelStyle === 'string') {
						elem.addClass(this.labelStyle);
					}
					elem.appendTo(plot.plugins.customAxes._labelElem);
					switch(this.labelAlign) {
						case 'middle':
							align = plot.target.offset().left+series_u2p(sd[j][axisIndex]) - elem.outerWidth()/2;
							break;
						case 'start':
							align = plot.target.offset().left+series_u2p(sd[j][axisIndex]);
							break;
						case 'end':
							align = plot.target.offset().left+series_u2p(sd[j][axisIndex]) + elem.outerWidth();
							break;
					}

					elem.offset({
						top: this.location === 'bottom' ? plot.target.outerHeight() - this.padding + this.labelOffset.top : plot.grid._elem.offset().top + this.labelOffset.top,
						left: align + this.labelOffset.left
					});
				}

				if (this.showGridLine) {
					x = series_u2p(sd[j][axisIndex]);
					yMin = yaxis.u2p(yaxis.min);
					yMax = yaxis.u2p(yaxis.max);
					
					this.drawGridLine([[x, yMin], [x, yMax]], plot);
				}
				
				lastValue = sd[j][columnIndex];
			}

			lastValue = null;
			lastData = null;
		} else {
			var l = this.onlyFirstSeries ? Math.min(1, series.length) : series.length;
			for(i=0; i<l; i++) {
				sd = series[i].data;
				
				xaxis = plot.axes[series[i].xaxis];
				series_u2p = xaxis.u2p;
				yaxis = plot.axes[series[i].yaxis];
				
				min = plot.axes[series[i][this.axis]].min;
				max = plot.axes[series[i][this.axis]].max;
				
				if (i===0 && selectArea.show && this.onlyFirstSeries && this.mergedLabel && this.labelType === 'block') {
					series_xaxis = plot.axes[series[i].xaxis];
					
					this._canvas._elem.bind('click', function(ev) {
						renderer.onSelectBlock.call(self, ev, series_xaxis, selectArea, plot);
					});
					
	    		ctx = this._canvas._ctx;
					ctx.clearRect(0,0,ctx.canvas.width, ctx.canvas.height);
	
					ctx.fillStyle = this.selectArea.color;
					
					ctx.fillRect(0,0,ctx.canvas.width, ctx.canvas.height);
					ctx.restore();
				}
				
				for(j=0; j<sd.length; j++) {
					if ( sd[j][columnIndex] === null || isNaN(sd[j][columnIndex]) || (this.mergedLabel && lastValue === sd[j][columnIndex])) {
						continue;
					}
					
          if (selectArea.show && this.onlyFirstSeries && this.mergedLabel && this.labelType === 'block') {
            x = series_u2p(sd[j][axisIndex]);
            
            selectArea.selectBlocks.push({
              selected: false,
              pointIndex: j,
              seriesIndex: i,
              data: sd[j],
              x: x - gridPadding.left
            });
            
            renderer.drawSelectAreaLine.call(this, x - gridPadding.left, selectArea.lineColor, plot);
          }
					
					if ( min !== null && sd[j][axisIndex] < min ) {
						lastValue = sd[j][columnIndex];
						continue;
					} else if (max !== null && max < sd[j][axisIndex]) {
						break;
					}
					
					str = '';
					if (typeof this.formatter === 'function') {
						str = this.formatter.call(this, sd[j][columnIndex], sd[j]);
						if (str === false) {
							continue;
						}
					} else {
						str = sd[j][columnIndex];
					}
	
					gridPadding = plot._gridPadding;
	
					if (this.showLabel) {
						var elem = $('<div />');
						elem.html(str);
						
						elem.addClass('jqplot-axis');
						elem.css(css);
						if (this.labelStyle && typeof this.labelStyle === 'object') {
							elem.css(this.labelStyle);
						} else if (typeof this.labelStyle === 'string') {
							elem.addClass(this.labelStyle);
						}
						elem.appendTo(plot.plugins.customAxes._labelElem);
						switch(this.labelAlign) {
							case 'middle':
								align = plot.target.offset().left+series_u2p(sd[j][axisIndex]) - elem.outerWidth()/2;
								break;
							case 'start':
								align = plot.target.offset().left+series_u2p(sd[j][axisIndex]);
								break;
							case 'end':
								align = plot.target.offset().left+series_u2p(sd[j][axisIndex]) + elem.outerWidth();
								break;
						}
	
						elem.offset({
							top: this.location === 'bottom' ? plot.target.outerHeight() - this.padding + this.labelOffset.top : plot.grid._elem.offset().top + this.labelOffset.top,
							left: align + this.labelOffset.left
						});
					}

					if (this.showGridLine) {
						x = series_u2p(sd[j][axisIndex]);
						yMin = yaxis.u2p(yaxis.min);
						yMax = yaxis.u2p(yaxis.max);
						
						this.drawGridLine([[x, yMin], [x, yMax]], plot);
					}
					
					lastValue = sd[j][columnIndex];
				}
	
				lastValue = null;
				lastData = null;
			}
		}

	};

})(jQuery);;/**
 * jqPlot
 * Pure JavaScript plotting plugin using jQuery
 *
 * Version: 1.0.8
 * Revision: 1250
 *
 * Copyright (c) 2009-2013 Chris Leonello
 * jqPlot is currently available for use in all personal or commercial projects
 * under both the MIT (http://www.opensource.org/licenses/mit-license.php) and GPL
 * version 2.0 (http://www.gnu.org/licenses/gpl-2.0.html) licenses. This means that you can
 * choose the license that best suits your project and use it accordingly.
 *
 * Although not required, the author would appreciate an email letting him
 * know of any substantial use of jqPlot.  You can reach the author at:
 * chris at jqplot dot com or see http://www.jqplot.com/info.php .
 *
 * If you are feeling kind and generous, consider supporting the project by
 * making a donation at: http://www.jqplot.com/donate.php .
 *
 * sprintf functions contained in jqplot.sprintf.js by Ash Searle:
 *
 *     version 2007.04.27
 *     author Ash Searle
 *     http://hexmen.com/blog/2007/03/printf-sprintf/
 *     http://hexmen.com/js/sprintf.js
 *     The author (Ash Searle) has placed this code in the public domain:
 *     "This code is unrestricted: you are free to use it however you like."
 *
 */
(function($) {
	var objCounter = 0;
	// class: $.jqplot.CanvasOverlay
	$.jqplot.CanvasOverlay = function(opts) {
		var options = opts || {};
		this.options = {
			show : $.jqplot.config.enablePlugins,
			deferDraw : false
			//outsideDraw : false, // customizing for outside tooltip (2013-03-05, Roy Choi)
			//outsideDirection : null
		};
		// prop: objects
		this.objects = [];
		this.objectNames = [];
		this.canvas = null;
		this.markerRenderer = new $.jqplot.MarkerRenderer({
			style : 'line'
		});
		this.markerRenderer.init();
		this.highlightObjectIndex = null;

		this.bcanvas = null;
		// customizing
		//this.outsideCanvas = null;
		// customizing for outside tooltip (2013-03-09, Roy Choi)
		//this.outsideBCanvas = null;
		// customizing for outside tooltip (2013-03-09, Roy Choi)
		this._neighbor = null;
		this.isDragging = false;
		//this.preZoomStatus = [];
		//this._paddingDirection = null;
		// customizing for outside tooltip (2013-03-06, Roy Choi)
		//this._paddingWidth = 0;
		// customizing for outside tooltip (2013-03-06, Roy Choi)
		this._oldHandlers = {};
		
		this._prevCursorShow = false;

		if (options.objects) {
			var objs = options.objects, obj;
			for (var i = 0; i < objs.length; i++) {
				obj = objs[i];
				for (var n in obj) {
					switch (n) {
						case 'line':
							this.addLine(obj[n]);
							break;
						case 'horizontalLine':
							this.addHorizontalLine(obj[n]);
							break;
						case 'dashedHorizontalLine':
							this.addDashedHorizontalLine(obj[n]);
							break;
						case 'verticalLine':
							this.addVerticalLine(obj[n]);
							break;
						case 'dashedVerticalLine':
							this.addDashedVerticalLine(obj[n]);
							break;
						case 'rectangle':
							this.addRectangle(obj[n]);
							break;
						default:
							break;
					}
				}
			}
		}
		$.extend(true, this.options, options);
	};

	// called with scope of a plot object
	$.jqplot.CanvasOverlay.postPlotInit = function(target, data, opts) {
		var options = opts || {};
		// add a canvasOverlay attribute to the plot
		this.plugins.canvasOverlay = new $.jqplot.CanvasOverlay(options.canvasOverlay);
	};

	// customizing
	function ShapeBase() {
		this.uid = null;
		this.type = null;
		this.gridStart = null;
		this.gridStop = null;
		this.tooltipWidthFactor = 0;
		this.draggableArrows = [];
		this.options = {
			// prop: name
			// Optional name for the overlay object.
			// Can be later used to retrieve the object by name.
			name : null,
			// prop: show
			// true to show (draw), false to not draw.
			show : true,
			// prop: lineWidth
			// Width of the line.
			lineWidth : 2,
			// prop: lineCap
			// Type of ending placed on the line ['round', 'butt', 'square']
			lineCap : 'round',
			// prop: color
			// color of the line
			color : '#666666',
			// prop: shadow
			// wether or not to draw a shadow on the line
			shadow : true,
			// prop: shadowAngle
			// Shadow angle in degrees
			shadowAngle : 45,
			// prop: shadowOffset
			// Shadow offset from line in pixels
			shadowOffset : 1,
			// prop: shadowDepth
			// Number of times shadow is stroked, each stroke offset shadowOffset from the last.
			shadowDepth : 3,
			// prop: shadowAlpha
			// Alpha channel transparency of shadow.  0 = transparent.
			shadowAlpha : '0.07',
			// prop: xaxis
			// X axis to use for positioning/scaling the line.
			xaxis : 'xaxis',
			// prop: yaxis
			// Y axis to use for positioning/scaling the line.
			yaxis : 'yaxis',
			// prop: showTooltip
			// Show a tooltip with data point values.
			showTooltip : false,
			// prop: showTooltipPrecision
			// Controls how close to line cursor must be to show tooltip.
			// Higher number = closer to line, lower number = farther from line.
			// 1.0 = cursor must be over line.
			showTooltipPrecision : 0.6,
			// prop: tooltipLocation
			// Where to position tooltip, 'n', 'ne', 'e', 'se', 's', 'sw', 'w', 'nw'
			tooltipLocation : 'nw',
			// prop: fadeTooltip
			// true = fade in/out tooltip, flase = show/hide tooltip
			fadeTooltip : true,
			// prop: tooltipFadeSpeed
			// 'slow', 'def', 'fast', or number of milliseconds.
			tooltipFadeSpeed : "fast",
			// prop: tooltipOffset
			// Pixel offset of tooltip from the highlight.
			tooltipOffset : 4,
			// prop: tooltipFormatString
			// Format string passed the x and y values of the cursor on the line.
			// e.g., 'Dogs: %.2f, Cats: %d'.
			tooltipFormatString : '%d, %d',
			
			// overlay or underlay
			isBackground : false,
			isFullLine : false,
			fillBehind : false
		};
	}

	// customizing
	function LineBase() {
		ShapeBase.call(this);
		
		this.cursor = [];
		this.isOver = false;

		var opts = {
			// customizing (2011-11-03, Roy Choi)
			// dragable line
			isDraggable : false,
			dragable : {
				color : 'rgba(200,200,200,0.5)',
				constrainTo : 'y',
				min: null,
				max: null,
				afterRedraw : null,
				notifyOnDrag : false,		// customizing (2013-03-07, Roy Choi)
				showArrow: false,
				arrowOptions: {
				  gapWidth: 3,
				  width: 10,
				  height: 10,
          showHighArrow: false,
          showLowArrow: false,
          stroke: false,
          strokeWidth: 1,
          fill: true,
          color: null,
          fillStyle: null
				}
			},
			fillArea : false,
			fillStyle : null,
			fillToValue : 0,
			fillToMax : false,
			fillToMin : false,
			fillToBind : null,
			showLabel: false,
      labelOptions: {
        formatter: null,
        classes: '',
        location: '',
        offset: {
          top: 0,
          left: 0
        }
      }
			/*
			 observableKey : null, // customizing (2013-03-06, Roy Choi)
			 _observableKey : null,
			 _observableSubscription : null, // customizing (2013-03-06, Roy Choi)
			 showOutsideTooltip : false, // customizing (2013-03-06, Roy Choi)
			 outsideTooltipElem : null, // customizing (2013-03-06, Roy Choi)
			 outsideTooltipEditable : false, // customizing (2013-03-06, Roy Choi)
			 outsideTooltipStyle : {}, // customizing (2013-03-06, Roy Choi)
			 outsideTooltipFormatter : null	// customizing (2013-03-07, Roy Choi)*/

		};

		$.extend(true, this.options, opts);
	}

	function Rectangle(options) {
		ShapeBase.call(this);
		this.type = 'rectangle';
		var opts = {
			// prop: xmin
			// x value for the start of the line, null to scale to axis min.
			xmin : null,
			// prop: xmax
			// x value for the end of the line, null to scale to axis max.
			xmax : null,
			// prop xOffset
			// offset ends of the line inside the grid. Number
			xOffset : '6px', // number or string. Number interpreted as units, string as pixels.
			xminOffset : null,
			xmaxOffset : null,

			ymin : null,
			ymax : null,
			yOffset : '6px', // number or string. Number interpreted as units, string as pixels.
			yminOffset : null,
			ymaxOffset : null,
			
			// customizing (2014-04-16, Roy Choi)
			xminBind: null,
			xmaxBind: null,
			yminBind: null,
			ymaxBind: null
		};
		$.extend(true, this.options, opts, options);

		if (this.options.showTooltipPrecision < 0.01) {
			this.options.showTooltipPrecision = 0.01;
		}
	}

	/**
	 * Class: Line
	 * A straight line.
	 */
	function Line(options) {
		LineBase.call(this);
		this.type = 'line';
		var opts = {
			// prop: start
			// [x, y] coordinates for the start of the line.
			start : [],
			// prop: stop
			// [x, y] coordinates for the end of the line.
			stop : []
		};
		$.extend(true, this.options, opts, options);

		if (this.options.showTooltipPrecision < 0.01) {
			this.options.showTooltipPrecision = 0.01;
		}
	}


	Line.prototype = new LineBase();
	Line.prototype.constructor = Line;

	/**
	 * Class: HorizontalLine
	 * A straight horizontal line.
	 */
	function HorizontalLine(options) {
		LineBase.call(this);
		this.type = 'horizontalLine';
		var opts = {
			// prop: y
			// y value to position the line
			y : null,
			// prop: xmin
			// x value for the start of the line, null to scale to axis min.
			xmin : null,
			// prop: xmax
			// x value for the end of the line, null to scale to axis max.
			xmax : null,
			// prop xOffset
			// offset ends of the line inside the grid.  Number
			xminBind : null,
			xmaxBind : null,
			xOffset : '6px', // number or string.  Number interpreted as units, string as pixels.
			xminOffset : null,
			xmaxOffset : null,
      labelOptions: {
        location: 'right'   // right | left | center
      }
		};
		$.extend(true, this.options, opts, options);

		if (this.options.showTooltipPrecision < 0.01) {
			this.options.showTooltipPrecision = 0.01;
		}
	}


	HorizontalLine.prototype = new LineBase();
	HorizontalLine.prototype.constructor = HorizontalLine;

	/**
	 * Class: DashedHorizontalLine
	 * A straight dashed horizontal line.
	 */
	function DashedHorizontalLine(options) {
		LineBase.call(this);
		this.type = 'dashedHorizontalLine';
		var opts = {
			y : null,
			xmin : null,
			xmax : null,
			xminBind : null,
			xmaxBind : null,
			xOffset : '6px', // number or string.  Number interpreted as units, string as pixels.
			xminOffset : null,
			xmaxOffset : null,
			// prop: dashPattern
			// Array of line, space settings in pixels.
			// Default is 8 pixel of line, 8 pixel of space.
			// Note, limit to a 2 element array b/c of bug with higher order arrays.
			dashPattern : [8, 8],
      labelOptions: {
        location: 'right'   // right | left | center
      }
		};
		$.extend(true, this.options, opts, options);

		if (this.options.showTooltipPrecision < 0.01) {
			this.options.showTooltipPrecision = 0.01;
		}
	}


	DashedHorizontalLine.prototype = new LineBase();
	DashedHorizontalLine.prototype.constructor = DashedHorizontalLine;

	/**
	 * Class: VerticalLine
	 * A straight vertical line.
	 */
	function VerticalLine(options) {
		LineBase.call(this);
		this.type = 'verticalLine';
		var opts = {
			x : null,
			ymin : null,
			ymax : null,
			yOffset : '6px', // number or string.  Number interpreted as units, string as pixels.
			yminOffset : null,
			ymaxOffset : null,
      labelOptions: {
        location: 'top'   // top | bottom | middle
      }
		};
		$.extend(true, this.options, opts, options);

		if (this.options.showTooltipPrecision < 0.01) {
			this.options.showTooltipPrecision = 0.01;
		}
	}


	VerticalLine.prototype = new LineBase();
	VerticalLine.prototype.constructor = VerticalLine;

	/**
	 * Class: DashedVerticalLine
	 * A straight dashed vertical line.
	 */
	function DashedVerticalLine(options) {
		LineBase.call(this);
		this.type = 'dashedVerticalLine';
		this.start = null;
		this.stop = null;
		var opts = {
			x : null,
			ymin : null,
			ymax : null,
			yOffset : '6px', // number or string.  Number interpreted as units, string as pixels.
			yminOffset : null,
			ymaxOffset : null,
			// prop: dashPattern
			// Array of line, space settings in pixels.
			// Default is 8 pixel of line, 8 pixel of space.
			// Note, limit to a 2 element array b/c of bug with higher order arrays.
			dashPattern : [8, 8],
      labelOptions: {
        location: 'top'   // top | bottom | middle
      }
		};
		$.extend(true, this.options, opts, options);

		if (this.options.showTooltipPrecision < 0.01) {
			this.options.showTooltipPrecision = 0.01;
		}
	}


	DashedVerticalLine.prototype = new LineBase();
	DashedVerticalLine.prototype.constructor = DashedVerticalLine;

	$.jqplot.CanvasOverlay.prototype.addLine = function(opts) {
		var line = new Line(opts);
		line.uid = objCounter++;
		this.objects.push(line);
		this.objectNames.push(line.options.name);
	};

	$.jqplot.CanvasOverlay.prototype.addHorizontalLine = function(opts) {
		var line = new HorizontalLine(opts);
		line.uid = objCounter++;
		this.objects.push(line);
		this.objectNames.push(line.options.name);
	};

	$.jqplot.CanvasOverlay.prototype.addDashedHorizontalLine = function(opts) {
		var line = new DashedHorizontalLine(opts);
		line.uid = objCounter++;
		this.objects.push(line);
		this.objectNames.push(line.options.name);
	};

	$.jqplot.CanvasOverlay.prototype.addVerticalLine = function(opts) {
		var line = new VerticalLine(opts);
		line.uid = objCounter++;
		this.objects.push(line);
		this.objectNames.push(line.options.name);
	};

	$.jqplot.CanvasOverlay.prototype.addDashedVerticalLine = function(opts) {
		var line = new DashedVerticalLine(opts);
		line.uid = objCounter++;
		this.objects.push(line);
		this.objectNames.push(line.options.name);
	};

	$.jqplot.CanvasOverlay.prototype.addRectangle = function(opts) {
		var line = new Rectangle(opts);
		line.uid = objCounter++;
		this.objects.push(line);
		this.objectNames.push(line.options.name);
	};

	// customizing (2013-01-24, Roy Choi)
	$.jqplot.CanvasOverlay.prototype.modifyObject = function(plot, name, opts) {
		var obj = this.get(name);
		try {
			if (obj) {
				obj.options = $.extend(true, obj.options, opts);
				this.draw(plot, obj);
				return true;
			} else {
				return false;
			}
		} catch(e) {
			return false;
		} finally {
			obj = null;
		}
	};
	
	$.jqplot.CanvasOverlay.prototype.setObjects = function(plot, objects) {
		if ($.isArray(objects) && objects.length) {
			var co = this;
			co.removeObject();
			objects.forEach(function(object) {
				var type = Object.keys(object).shift();
				var obj = object[type];
				switch (type) {
					case 'line':
						co.addLine(obj);
						break;
					case 'horizontalLine':
						co.addHorizontalLine(obj);
						break;
					case 'dashedHorizontalLine':
						co.addDashedHorizontalLine(obj);
						break;
					case 'verticalLine':
						co.addVerticalLine(obj);
						break;
					case 'dashedVerticalLine':
						co.addDashedVerticalLine(obj);
						break;
					case 'rectangle':
						co.addRectangle(obj);
						break;
					default:
						break;
				}
			});
			co.draw(plot);
		} 
	};
	
	$.jqplot.CanvasOverlay.prototype.addObjects = function(plot, objects) {
		if ($.isArray(objects) && objects.length) {
			var co = this;
			objects.forEach(function(object) {
				var type = Object.keys(object).shift();
				var obj = object[type];
				switch (type) {
					case 'line':
						co.addLine(obj);
						break;
					case 'horizontalLine':
						co.addHorizontalLine(obj);
						break;
					case 'dashedHorizontalLine':
						co.addDashedHorizontalLine(obj);
						break;
					case 'verticalLine':
						co.addVerticalLine(obj);
						break;
					case 'dashedVerticalLine':
						co.addDashedVerticalLine(obj);
						break;
					case 'rectangle':
						co.addRectangle(obj);
						break;
					default:
						break;
				}
			});
			co.draw(plot);
		} 
	};

	$.jqplot.CanvasOverlay.prototype.removeObject = function(idx) {
		// check if integer, remove by index
		if ($.type(idx) == 'number') {
			this.objects.splice(idx, 1);
			this.objectNames.splice(idx, 1);
		}
		// if string, remove by name
		else if ($.type(idx) === 'string') {
			var id = $.inArray(idx, this.objectNames);
			if (id != -1) {
				this.objects.splice(id, 1);
				this.objectNames.splice(id, 1);
			}
			// all objects
		} else {
			this.objectNames.splice(0);
			this.objects.splice(0);
		}
	};

	$.jqplot.CanvasOverlay.prototype.getObject = function(idx) {
		// check if integer, remove by index
		if ($.type(idx) == 'number') {
			return this.objects[idx];
		}
		// if string, remove by name
		else {
			var id = $.inArray(idx, this.objectNames);
			if (id != -1) {
				return this.objects[id];
			}
		}
	};

	// Set get as alias for getObject.
	$.jqplot.CanvasOverlay.prototype.get = $.jqplot.CanvasOverlay.prototype.getObject;

	$.jqplot.CanvasOverlay.prototype.clear = function(plot) {
		if (!this.canvas || !this.bcanvas) {
			return;
		}
		this.canvas._ctx.clearRect(0, 0, this.canvas.getWidth() + 1, this.canvas.getHeight() + 1);
		this.bcanvas._ctx.clearRect(0, 0, this.bcanvas.getWidth() + 1, this.bcanvas.getHeight() + 1);

		/*
		 if (this.options.outsideDraw) {
		 this.outsideCanvas._ctx.clearRect(0, 0, this.outsideCanvas.getWidth() + 1, this.outsideCanvas.getHeight() + 1);
		 this.outsideBCanvas._ctx.clearRect(0, 0, this.outsideBCanvas.getWidth() + 1, this.outsideBCanvas.getHeight() + 1);

		 $('.jqplot-canvasoverlay-outsidetooltip', this.target).each(function() {
		 $(this).children().each(function() {
		 $(this).unbind();
		 $(this).empty();
		 $(this).remove();
		 });
		 $(this).unbind();
		 $(this).empty();
		 $(this).remove();
		 });
		 }*/

	};

	$.jqplot.CanvasOverlay.prototype.draw = function(plot, dragobj, ev) {
		var obj, objs = this.objects, mr = this.markerRenderer, start, stop;
		if (this.options.show) {
			this.clear();
			clearLabel(plot);
			/*this.canvas._ctx.clearRect(0,0,this.canvas.getWidth(), this.canvas.getHeight());
			 this.bcanvas._ctx.clearRect(0,0,this.bcanvas.getWidth(), this.bcanvas.getHeight());*/

			for (var k = 0; k < objs.length; k++) {
				obj = objs[k];
				var opts = $.extend(true, {}, obj.options);
				if (obj.options.show) {
					// style and shadow properties should be set before
					// every draw of marker renderer.
					mr.shadow = obj.options.shadow;
					obj.tooltipWidthFactor = obj.options.lineWidth / obj.options.showTooltipPrecision;
					
					// TODO: bug fix plot.axes[obj.options.xaxis].series_u2p
					// Temporary
					if (obj.options.xaxis === 'xaxis' && typeof plot.axes[obj.options.xaxis].series_u2p === 'undefined') {
						var xaxis_series_u2p = plot.axes['x2axis'].series_u2p;
					} else {
						var xaxis_series_u2p = plot.axes[obj.options.xaxis].series_u2p;
					}
					
					if (obj.options.yaxis === 'yaxis' && typeof plot.axes[obj.options.yaxis].series_u2p === 'undefined') {
						var yaxis_series_u2p = plot.axes['y2axis'].series_u2p;
					} else {
						var yaxis_series_u2p = plot.axes[obj.options.yaxis].series_u2p;
					}
					
					
					switch (obj.type) {
						case 'line':
							// style and shadow properties should be set before
							// every draw of marker renderer.
							mr.style = 'line';
							opts.closePath = false;
							start = [xaxis_series_u2p(obj.options.start[0]), yaxis_series_u2p(obj.options.start[1])];
							stop = [xaxis_series_u2p(obj.options.stop[0]), yaxis_series_u2p(obj.options.stop[1])];
							obj.gridStart = start;
							obj.gridStop = stop;
							mr.draw(start, stop, (opts.isBackground ? this.bcanvas._ctx : this.canvas._ctx), opts);
							break;
						case 'horizontalLine':
							// style and shadow properties should be set before
							// every draw of marker renderer.
							if (obj.options.y != null) {
								mr.style = 'line';
								opts.closePath = false;
								var xaxis = plot.axes[obj.options.xaxis], yaxis = plot.axes[obj.options.yaxis], xstart = xaxis.series_u2p(xaxis.min), xstop = xaxis.series_u2p(xaxis.max), y = yaxis_series_u2p(obj.options.y), xminoff = obj.options.xminOffset || obj.options.xOffset, xmaxoff = obj.options.xmaxOffset || obj.options.xOffset, fillStart = xaxis.series_u2p(xaxis.min), fillStop = xaxis.series_u2p(xaxis.max);
								if (obj.options.xmin != null) {
									fillStart = xaxis.series_u2p(obj.options.xmin);
									if (!obj.options.isFullLine)
										xstart = fillStart;
								} else if (obj.options.xminBind != null) {
									fillStart = xaxis.series_u2p(this.get(opts.xminBind).options.x);
									if (!obj.options.isFullLine)
										xstart = fillStart;
								} else if (xminoff != null) {
									if ($.type(xminoff) == "number") {
										fillStart = xaxis.series_u2p(xaxis.min + xminoff);
										if (!obj.options.isFullLine)
											xstart = fillStart;
									} else if ($.type(xminoff) == "string") {
										fillStart = xaxis.series_u2p(xaxis.min) + parseFloat(xminoff);
										if (!obj.options.isFullLine)
											xstart = fillStart;
									}
								}
								if (obj.options.xmax != null) {
									fillStop = xaxis.series_u2p(obj.options.xmax);
									if (!obj.options.isFullLine)
										xstop = fillStop;
								} else if (obj.options.xmaxBind != null && this.get(opts.xmaxBind).options.show) {
									fillStop = xaxis.series_u2p(this.get(opts.xmaxBind).options.x);
									if (!obj.options.isFullLine)
										xstop = fillStop;
								} else if (xmaxoff != null) {
									if ($.type(xmaxoff) == "number") {
										fillStop = xaxis.series_u2p(xaxis.max - xmaxoff);
										if (!obj.options.isFullLine)
											xstop = fillStop;
									} else if ($.type(xmaxoff) == "string") {
										fillStop = xaxis.series_u2p(xaxis.max) - parseFloat(xmaxoff);
										if (!obj.options.isFullLine)
											xstop = fillStop;
									}
								}
								if (xstop != null && xstart != null) {
									obj.gridStart = [xstart, y];
									obj.gridStop = [xstop, y];

									var ctx = opts.isBackground ? this.bcanvas._ctx : this.canvas._ctx;

									// customizing for outsideTooltip (2013-03-04, Roy choi)
									if (this.options.outsideDraw) {
										xstop += this._paddingWidth;

										drawHorizontalOutsideTooltip.call(this, plot, obj, opts, y);

										ctx = opts.isBackground ? this.outsideBCanvas._ctx : this.outsideCanvas._ctx;
									}

									mr.draw([xstart, y], [xstop, y], ctx, opts);
									
									if (opts.fillArea) {
										if (fillStart < 0) {
											fillStart = 0;
											fillStop += parseFloat(xminoff);
										}

										// customizing for outsideTooltip (2013-03-04, Roy choi)
										if (this.options.outsideDraw) {
											if (this._paddingDirection == 'left') {
												fillStart += this._paddingWidth;
												fillStop += this._paddingWidth;
											}
										}

										ctx.fillStyle = opts.fillStyle;
										var fillToValue = null;
										if (opts.fillToBind && this.get(opts.fillToBind)) {
											fillToValue = this.get(opts.fillToBind).options.y;
										} else if (opts.fillToMin) {
											fillToValue = yaxis.min;
										} else if (opts.fillToMax) {
											fillToValue = yaxis.max;
										} else if (opts.fillToValue != null) {
											fillToValue = opts.fillToValue;
										}
										//var fillToValue = opts.fillToBind && this.get(opts.fillToBind) ? this.get(opts.fillToBind).options.y : opts.fillToValue;
										var ystop = yaxis.series_u2p(opts.y) - yaxis.series_u2p(fillToValue);
										if (opts.fillBehind)
											ctx.fillRect(fillStart, yaxis.series_u2p(fillToValue), fillStop - fillStart, ystop);
										else
											ctx.fillRect(fillStart, yaxis.series_u2p(fillToValue), fillStop - fillStart, ystop);
									}
									
									if (opts.isDraggable && opts.dragable.showArrow) {
									  obj.draggableArrows = [];
                    var start = Math.min(xstart, xstop);
                    var stop = Math.max(xstart, xstop);
                    var max = opts.dragable.max != null ? yaxis.series_u2p(opts.dragable.max) : opts.dragable.max;
                    var min = opts.dragable.min != null ? yaxis.series_u2p(opts.dragable.min) : opts.dragable.min;
                    if (opts.dragable.arrowOptions && opts.dragable.arrowOptions.showHighArrow && (max == null || max > y + opts.dragable.arrowOptions.gapWidth + opts.dragable.arrowOptions.width)) {
                      drawArrow.call(opts, ctx, 'top', [start + (stop - start) / 2, y], opts.dragable.arrowOptions, obj);
                    }
                    if (opts.dragable.arrowOptions && opts.dragable.arrowOptions.showLowArrow && (min == null || min < y - opts.dragable.arrowOptions.gapWidth - opts.dragable.arrowOptions.width)) {
                      drawArrow.call(opts, ctx, 'bottom', [start + (stop - start) / 2, y], opts.dragable.arrowOptions, obj);
                    }
                  }

									ctx = null;
									
									if (opts.showLabel) {
									  showLabel(plot, obj);
									}

								}
								if (opts.isDraggable && opts.dragable && opts.dragable.notifyOnDrag && $.isFunction(obj.options.dragable.afterRedraw) && this.isDragging && dragobj && dragobj.options.name == obj.options.name) {
									obj.options.dragable.afterRedraw.call(obj, ev, false, obj.options.y, plot, this);
								}
							}

							break;

						case 'dashedHorizontalLine':

							var dashPat = obj.options.dashPattern;
							var dashPatLen = 0;
							for (var i = 0; i < dashPat.length; i++) {
								dashPatLen += dashPat[i];
							}

							// style and shadow properties should be set before
							// every draw of marker renderer.
							if (obj.options.y != null) {
								mr.style = 'line';
								opts.closePath = false;
								var xaxis = plot.axes[obj.options.xaxis], xstart = xaxis.series_u2p(xaxis.min), xstop = xaxis.series_u2p(xaxis.max), y = yaxis_series_u2p(obj.options.y), xminoff = obj.options.xminOffset || obj.options.xOffset, xmaxoff = obj.options.xmaxOffset || obj.options.xOffset, fillStart = xaxis.series_u2p(xaxis.min), fillStop = xaxis.series_u2p(xaxis.max);
								if (obj.options.xmin != null) {
									fillStart = xaxis.series_u2p(obj.options.xmin);
									if (!obj.options.isFullLine)
										xstart = fillStart;
								} else if (obj.options.xminBind != null) {
									fillStart = xaxis.series_u2p(this.get(opts.xminBind).options.x);
									if (!obj.options.isFullLine)
										xstart = fillStart;
								} else if (xminoff != null) {
									if ($.type(xminoff) == "number") {
										fillStart = xaxis.series_u2p(xaxis.min + xminoff);
										if (!obj.options.isFullLine)
											xstart = fillStart;
									} else if ($.type(xminoff) == "string") {
										fillStart = xaxis.series_u2p(xaxis.min) + parseFloat(xminoff);
										if (!obj.options.isFullLine)
											xstart = fillStart;
									}
								}
								if (obj.options.xmax != null) {
									fillStop = xaxis.series_u2p(obj.options.xmax);
									if (!obj.options.isFullLine)
										xstop = fillStop;
								} else if (obj.options.xmaxBind != null) {
									fillStop = xaxis.series_u2p(this.get(opts.xmaxBind).options.x);
									if (!obj.options.isFullLine)
										xstop = fillStop;
								} else if (xmaxoff != null) {
									if ($.type(xmaxoff) == "number") {
										fillStop = xaxis.series_u2p(xaxis.max - xmaxoff);
										if (!obj.options.isFullLine)
											xstop = fillStop;
									} else if ($.type(xmaxoff) == "string") {
										fillStop = xaxis.series_u2p(xaxis.max) - parseFloat(xmaxoff);
										if (!obj.options.isFullLine)
											xstop = fillStop;
									}
								}
								if (xstop != null && xstart != null) {
									obj.gridStart = [xstart, y];
									obj.gridStop = [xstop, y];

									var ctx = opts.isBackground ? this.bcanvas._ctx : this.canvas._ctx;

									// customizing for outsideTooltip (2013-03-04, Roy choi)
									if (this.options.outsideDraw) {
										xstop += this._paddingWidth;

										drawHorizontalOutsideTooltip.call(this, plot, obj, opts, y);

										ctx = opts.isBackground ? this.outsideBCanvas._ctx : this.outsideCanvas._ctx;
									}

									var numDash = Math.ceil((xstop - xstart) / dashPatLen);
									var b = xstart, e;
									for (var i = 0; i < numDash; i++) {
										for (var j = 0; j < dashPat.length; j += 2) {
											e = b + dashPat[j];
											mr.draw([b, y], [e, y], ctx, opts);
											b += dashPat[j];
											if (j < dashPat.length - 1) {
												b += dashPat[j + 1];
											}
										}
									}
									if (opts.fillArea) {
										if (fillStart < 0) {
											fillStart = 0;
											fillStop += parseFloat(xminoff);
										}

										// customizing for outsideTooltip (2013-03-04, Roy choi)
										if (this.options.outsideDraw) {
											if (this._paddingDirection == 'left') {
												fillStart += this._paddingWidth;
												fillStop += this._paddingWidth;
											}
										}

										ctx.fillStyle = opts.fillStyle;
										var fillToValue = null;
										if (opts.fillToBind && this.get(opts.fillToBind)) {
											fillToValue = this.get(opts.fillToBind).options.y;
										} else if (opts.fillToMin) {
											fillToValue = yaxis.min;
										} else if (opts.fillToMax) {
											fillToValue = yaxis.max;
										} else if (opts.fillToValue != null) {
											fillToValue = opts.fillToValue;
										}
										//var fillToValue = opts.fillToBind && this.get(opts.fillToBind) ? this.get(opts.fillToBind).options.y : opts.fillToValue;
										var ystop = yaxis.series_u2p(opts.y) - yaxis.series_u2p(fillToValue);
										if (opts.fillBehind)
											ctx.fillRect(fillStart, yaxis.series_u2p(fillToValue), fillStop - fillStart, ystop);
										else
											ctx.fillRect(fillStart, yaxis.series_u2p(fillToValue), fillStop - fillStart, ystop);
									}
									
									if (opts.isDraggable && opts.dragable.showArrow) {
                    var start = Math.min(xstart, xstop);
                    var stop = Math.max(xstart, xstop);
                    var max = opts.dragable.max != null ? yaxis.series_u2p(opts.dragable.max) : opts.dragable.max;
                    var min = opts.dragable.min != null ? yaxis.series_u2p(opts.dragable.min) : opts.dragable.min;
                    if (opts.dragable.arrowOptions && opts.dragable.arrowOptions.showHighArrow && (max == null || max > y + opts.dragable.arrowOptions.gapWidth + opts.dragable.arrowOptions.width)) {
                      drawArrow.call(opts, ctx, 'top', [start + (stop - start) / 2, y], opts.dragable.arrowOptions, obj);
                    }
                    if (opts.dragable.arrowOptions && opts.dragable.arrowOptions.showLowArrow && (min == null || min < y - opts.dragable.arrowOptions.gapWidth - opts.dragable.arrowOptions.width)) {
                      drawArrow.call(opts, ctx, 'bottom', [start + (stop - start) / 2, y], opts.dragable.arrowOptions, obj);
                    }
                  }

									ctx = null;
									
									if (opts.showLabel) {
                    showLabel(plot, obj);
                  }
								}

								if (opts.isDraggable && opts.dragable && opts.dragable.notifyOnDrag && $.isFunction(obj.options.dragable.afterRedraw) && this.isDragging && dragobj && dragobj.options.name == obj.options.name) {
									obj.options.dragable.afterRedraw.call(obj, ev, false, obj.options.y, plot, this);
								}
							}
							break;

						case 'verticalLine':

							// style and shadow properties should be set before
							// every draw of marker renderer.
							if (obj.options.x != null) {
								mr.style = 'line';
								opts.closePath = false;
								var yaxis = plot.axes[obj.options.yaxis], xaxis = plot.axes[obj.options.xaxis], ystart, ystop, x = xaxis_series_u2p(obj.options.x), yminoff = obj.options.yminOffset || obj.options.yOffset, ymaxoff = obj.options.ymaxOffset || obj.options.yOffset;
								if (obj.options.ymin != null) {
									ystart = yaxis.series_u2p(obj.options.ymin);
								} else if (yminoff != null) {
									if ($.type(yminoff) == "number") {
										ystart = yaxis.series_u2p(yaxis.min - yminoff);
									} else if ($.type(yminoff) == "string") {
										ystart = yaxis.series_u2p(yaxis.min) - parseFloat(yminoff);
									}
								}
								if (obj.options.ymax != null) {
									ystop = yaxis.series_u2p(obj.options.ymax);
								} else if (ymaxoff != null) {
									if ($.type(ymaxoff) == "number") {
										ystop = yaxis.series_u2p(yaxis.max + ymaxoff);
									} else if ($.type(ymaxoff) == "string") {
										ystop = yaxis.series_u2p(yaxis.max) + parseFloat(ymaxoff);
									}
								}
								if (ystop != null && ystart != null) {
									obj.gridStart = [x, ystart];
									obj.gridStop = [x, ystop];
									
									var ctx = opts.isBackground ? this.bcanvas._ctx : this.canvas._ctx;

									// customizing for outsideTooltip (2013-03-07, Roy choi)
									if (this.options.outsideDraw) {
										/// TODO : Draw vertical outside tooltip
										/*ystop += this._paddingWidth;

										drawVerticalOutsideTooltip.call(this, plot, obj, opts, x);*/
									}

									mr.draw([x, ystart], [x, ystop], ctx, opts);

									if (opts.fillArea) {

										// customizing for outsideTooltip (2013-03-07, Roy choi)
										if (this.options.outsideDraw) {
											/// TODO : Draw vertical outside tooltip
											/*if (this._paddingDirection == 'top') {
											ystart += this._paddingWidth;
											ystop += this._paddingWidth;
											}*/
										}

										ctx.fillStyle = opts.fillStyle;

										var fillToValue = null;
										if (opts.fillToBind && this.get(opts.fillToBind)) {
											fillToValue = this.get(opts.fillToBind).options.x;
										} else if (opts.fillToMin) {
											fillToValue = xaxis.min;
										} else if (opts.fillToMax) {
											fillToValue = xaxis.max;
										} else if (opts.fillToValue != null) {
											fillToValue = opts.fillToValue;
										}
										//var fillToValue = opts.fillToBind && this.get(opts.fillToBind) ? this.get(opts.fillToBind).options.x : opts.fillToValue;
										var xstop = xaxis.series_u2p(opts.x) - xaxis.series_u2p(fillToValue);
										ctx.fillRect(xaxis.series_u2p(fillToValue), ystart, xstop, ystop - ystart);
									}
									
									if (opts.isDraggable && opts.dragable.showArrow) {
                    var start = Math.min(ystart, ystop);
                    var stop = Math.max(ystart, ystop);
                    var max = opts.dragable.max != null ? xaxis.series_u2p(opts.dragable.max) : opts.dragable.max;
                    var min = opts.dragable.min != null ? xaxis.series_u2p(opts.dragable.min) : opts.dragable.min;
                    if (opts.dragable.arrowOptions && opts.dragable.arrowOptions.showHighArrow && (max == null || max > x + opts.dragable.arrowOptions.gapWidth + opts.dragable.arrowOptions.width)) {
                      drawArrow.call(opts, ctx, 'right', [x, start + (stop - start) / 2], opts.dragable.arrowOptions, obj);
                    }
                    if (opts.dragable.arrowOptions && opts.dragable.arrowOptions.showLowArrow && (min == null || min < x - opts.dragable.arrowOptions.gapWidth - opts.dragable.arrowOptions.width)) {
                      drawArrow.call(opts, ctx, 'left', [x, start + (stop - start) / 2], opts.dragable.arrowOptions, obj);
                    }
                  }
									
									ctx = null;
									
									if (opts.showLabel) {
                    showLabel(plot, obj);
                  }
								}

								if (opts.isDraggable && opts.dragable && opts.dragable.notifyOnDrag && $.isFunction(obj.options.dragable.afterRedraw) && this.isDragging && dragobj && dragobj.options.name == obj.options.name) {
									obj.options.dragable.afterRedraw.call(obj, ev, false, obj.options.x, plot, this);
								}
							}
							break;

						case 'dashedVerticalLine':

							var dashPat = obj.options.dashPattern;
							var dashPatLen = 0;
							for (var i = 0; i < dashPat.length; i++) {
								dashPatLen += dashPat[i];
							}

							// style and shadow properties should be set before
							// every draw of marker renderer.
							if (obj.options.x != null) {
								mr.style = 'line';
								opts.closePath = false;
								var yaxis = plot.axes[obj.options.yaxis], xaxis = plot.axes[obj.options.xaxis], ystart, ystop, x = xaxis_series_u2p(obj.options.x), yminoff = obj.options.yminOffset || obj.options.yOffset, ymaxoff = obj.options.ymaxOffset || obj.options.yOffset;
								if (obj.options.ymin != null) {
									ystart = yaxis.series_u2p(obj.options.ymin);
								} else if (yminoff != null) {
									if ($.type(yminoff) == "number") {
										ystart = yaxis.series_u2p(yaxis.min - yminoff);
									} else if ($.type(yminoff) == "string") {
										ystart = yaxis.series_u2p(yaxis.min) - parseFloat(yminoff);
									}
								}
								if (obj.options.ymax != null) {
									ystop = yaxis.series_u2p(obj.options.ymax);
								} else if (ymaxoff != null) {
									if ($.type(ymaxoff) == "number") {
										ystop = yaxis.series_u2p(yaxis.max + ymaxoff);
									} else if ($.type(ymaxoff) == "string") {
										ystop = yaxis.series_u2p(yaxis.max) + parseFloat(ymaxoff);
									}
								}

								if (ystop != null && ystart != null) {
									obj.gridStart = [x, ystart];
									obj.gridStop = [x, ystop];
									
									var ctx = opts.isBackground ? this.bcanvas._ctx : this.canvas._ctx;

									// customizing for outsideTooltip (2013-03-07, Roy choi)
									if (this.options.outsideDraw) {
										/// TODO : Draw vertical outside tooltip
										/*ystop += this._paddingWidth;

										drawVerticalOutsideTooltip.call(this, plot, obj, opts, x);*/
									}

									var numDash = Math.ceil((ystart - ystop) / dashPatLen);
									var firstDashAdjust = ((numDash * dashPatLen) - (ystart - ystop)) / 2.0;
									var b = ystart, e, bs, es;
									for (var i = 0; i < numDash; i++) {
										for (var j = 0; j < dashPat.length; j += 2) {
											e = b - dashPat[j];
											if (e < ystop) {
												e = ystop;
											}
											if (b < ystop) {
												b = ystop;
											}
											// es = e;
											// if (i == 0) {
											//  es += firstDashAdjust;
											// }
											mr.draw([x, b], [x, e], ctx, opts);
											b -= dashPat[j];
											if (j < dashPat.length - 1) {
												b -= dashPat[j + 1];
											}
										}
									}
									if (opts.fillArea) {
										// customizing for outsideTooltip (2013-03-07, Roy choi)
										if (this.options.outsideDraw) {
											/// TODO : Draw vertical outside tooltip
											/*if (this._paddingDirection == 'top') {
											ystart += this._paddingWidth;
											ystop += this._paddingWidth;
											}*/
										}

										ctx.fillStyle = opts.fillStyle;

										var fillToValue = null;
										if (opts.fillToBind && this.get(opts.fillToBind)) {
											fillToValue = this.get(opts.fillToBind).options.x;
										} else if (opts.fillToMin) {
											fillToValue = xaxis.min;
										} else if (opts.fillToMax) {
											fillToValue = xaxis.max;
										} else if (opts.fillToValue != null) {
											fillToValue = opts.fillToValue;
										}
										//var fillToValue = opts.fillToBind && this.get(opts.fillToBind) ? this.get(opts.fillToBind).options.x : opts.fillToValue;
										var xstop = xaxis.series_u2p(opts.x) - xaxis.series_u2p(fillToValue);
										ctx.fillRect(xaxis.series_u2p(fillToValue), ystart, xstop, ystop - ystart);
									}
									
									if (opts.isDraggable && opts.dragable.showArrow) {
                    var start = Math.min(ystart, ystop);
                    var stop = Math.max(ystart, ystop);
                    var max = opts.dragable.max != null ? xaxis.series_u2p(opts.dragable.max) : opts.dragable.max;
                    var min = opts.dragable.min != null ? xaxis.series_u2p(opts.dragable.min) : opts.dragable.min;
                    console.log(x + opts.dragable.arrowOptions.gapWidth + opts.dragable.arrowOptions.width, opts.dragable.max);
                    if (opts.dragable.arrowOptions && opts.dragable.arrowOptions.showHighArrow && (max == null || max > x + opts.dragable.arrowOptions.gapWidth + opts.dragable.arrowOptions.width)) {
                      drawArrow.call(opts, ctx, 'right', [x, start + (stop - start) / 2], opts.dragable.arrowOptions, obj);
                    }
                    if (opts.dragable.arrowOptions && opts.dragable.arrowOptions.showLowArrow && (min == null || min < x - opts.dragable.arrowOptions.gapWidth - opts.dragable.arrowOptions.width)) {
                      drawArrow.call(opts, ctx, 'left', [x, start + (stop - start) / 2], opts.dragable.arrowOptions, obj);
                    }
                  }
									
									ctx = null;
									
									if (opts.showLabel) {
                    showLabel(plot, obj);
                  }
								}

								if (opts.isDraggable && opts.dragable && opts.dragable.notifyOnDrag && $.isFunction(obj.options.dragable.afterRedraw) && this.isDragging && dragobj && dragobj.options.name == obj.options.name) {
									obj.options.dragable.afterRedraw.call(obj, ev, false, obj.options.x, plot, this);
								}
							}
							break;

						case 'rectangle':
							// style and shadow properties should be set before
							// every draw of marker renderer.
							mr.style = 'line';
							opts.closePath = true;

							var xaxis = plot.axes[obj.options.xaxis], xstart, xstop, y = yaxis_series_u2p(obj.options.y), xminoff = obj.options.xminOffset || obj.options.xOffset, xmaxoff = obj.options.xmaxOffset || obj.options.xOffset;
							// customizing start (2014-04-16, Roy Choi)
							if (typeof obj.options.xminBind === 'string' && this.get(obj.options.xminBind)) {
								xstart = xaxis.series_u2p(this.get(obj.options.xminBind).options.x);
							// customizing end
							} else if (obj.options.xmin != null) {
								xstart = xaxis.series_u2p(obj.options.xmin);
							} else if (xminoff != null) {
								if ($.type(xminoff) == "number") {
									xstart = xaxis.series_u2p(xaxis.min + xminoff);
								} else if ($.type(xminoff) == "string") {
									xstart = xaxis.series_u2p(xaxis.min) + parseFloat(xminoff);
								}
							}
							// customizing start (2014-04-16, Roy Choi)
							if (typeof obj.options.xmaxBind === 'string' && this.get(obj.options.xmaxBind)) {
								xstop = xaxis.series_u2p(this.get(obj.options.xmaxBind).options.x);
							// customizing end
							} else if (obj.options.xmax != null) {
								xstop = xaxis.series_u2p(obj.options.xmax);
							} else if (xmaxoff != null) {
								if ($.type(xmaxoff) == "number") {
									xstop = xaxis.series_u2p(xaxis.max - xmaxoff);
								} else if ($.type(xmaxoff) == "string") {
									xstop = xaxis.series_u2p(xaxis.max) - parseFloat(xmaxoff);
								}
							}

							var yaxis = plot.axes[obj.options.yaxis], ystart, ystop, x = xaxis_series_u2p(obj.options.x), yminoff = obj.options.yminOffset || obj.options.yOffset, ymaxoff = obj.options.ymaxOffset || obj.options.yOffset;
							// customizing start (2014-04-16, Roy Choi)
							if (typeof obj.options.yminBind === 'string' && this.get(obj.options.yminBind)) {
								ystart = yaxis.series_u2p(this.get(obj.options.yminBind).options.y);
							// customizing end
							} else if (obj.options.ymin != null) {
								ystart = yaxis.series_u2p(obj.options.ymin);
							} else if (yminoff != null) {
								if ($.type(yminoff) == "number") {
									ystart = yaxis.series_u2p(yaxis.min - yminoff);
								} else if ($.type(yminoff) == "string") {
									ystart = yaxis.series_u2p(yaxis.min) - parseFloat(yminoff);
								}
							}
							// customizing start (2014-04-16, Roy Choi)
							if (typeof obj.options.ymaxBind === 'string' && this.get(obj.options.ymaxBind)) {
								ystop = yaxis.series_u2p(this.get(obj.options.ymaxBind).options.y);
							// customizing end
							} else if (obj.options.ymax != null) {
								ystop = yaxis.series_u2p(obj.options.ymax);
							} else if (ymaxoff != null) {
								if ($.type(ymaxoff) == "number") {
									ystop = yaxis.series_u2p(yaxis.max + ymaxoff);
								} else if ($.type(ymaxoff) == "string") {
									ystop = yaxis.series_u2p(yaxis.max) + parseFloat(ymaxoff);
								}
							}

							if (xstop != null && xstart != null && ystop != null && ystart != null) {
								obj.gridStart = [xstart, ystart];
								obj.gridStop = [xstop, ystop];
								
								var yend;
								if (ystop - ystart < -ystart) {	// temporary bug fix
									yend = -ystart;
								} else {
									yend = ystop - ystart;
								}
								
								// TODO: bug if do zoom of many counts
								if (obj.options.isBackground) {
									this.bcanvas._ctx.fillStyle = obj.options.color;
									this.bcanvas._ctx.fillRect(xstart, ystart, xstop - xstart, yend);
								} else {
									this.canvas._ctx.fillStyle = obj.options.color;
									this.canvas._ctx.fillRect(xstart, ystart, xstop - xstart, yend);
								}
							}
							break;

						default:
							break;
					}
				}
			}
			/*debugger;
			 var coelem = this.canvas._elem.detach();*/
			var cobelem = this.bcanvas._elem.detach();
			if (plot.series.length > 0)
				plot.series[plot.seriesStack[0]].canvas._elem.before(cobelem);
			cobelem = null;

			if (this.options.outsideDraw) {
				var cobelem = this.outsideBCanvas._elem.detach();
				if (plot.series.length > 0)
					plot.series[plot.seriesStack[0]].canvas._elem.before(cobelem);
				cobelem = null;
			}
		}
	};

	// called within context of plot
	// create a canvas which we can draw on.
	// insert it before the eventCanvas, so eventCanvas will still capture events.
	$.jqplot.CanvasOverlay.postPlotDraw = function() {
		var co = this.plugins.canvasOverlay;
		// Memory Leaks patch
		if (co && co.highlightCanvas) {
			co.highlightCanvas.resetCanvas();
			co.highlightCanvas = null;
		}
		co.canvas = new $.jqplot.GenericCanvas();
		co.bcanvas = new $.jqplot.GenericCanvas();

		/*
		 // customizing for outside tooltip (2013-03-04, Roy Choi)
		 if (co.options.outsideDraw) {
		 co.outsideCanvas = new $.jqplot.GenericCanvas();
		 co.outsideBCanvas = new $.jqplot.GenericCanvas();

		 $('.jqplot-canvasoverlay-outsidetooltip', this.target).each(function() {
		 $(this).children().each(function() {
		 $(this).unbind();
		 $(this).empty();
		 $(this).remove();
		 });
		 $(this).unbind();
		 $(this).empty();
		 $(this).remove();
		 });

		 co._paddingDirection = co.options.outsideDirection;
		 var dimentions = $.extend({}, this._plotDimensions);
		 var gridPadding = $.extend({}, this._gridPadding);
		 switch (co.options.outsideDirection) {
		 case 'left':
		 co._paddingWidth = parseInt(this.target.parent().css('padding-' + co.options.outsideDirection).replace(/[^\d]/, ''), 10);
		 //dimentions.width += padding;
		 gridPadding.left -= co._paddingWidth;
		 break;
		 case 'right':
		 co._paddingWidth = parseInt(this.target.parent().css('padding-' + co.options.outsideDirection).replace(/[^\d]/, ''), 10);
		 dimentions.width += co._paddingWidth;
		 break;
		 case 'top':
		 co._paddingWidth = parseInt(this.target.parent().css('padding-' + co.options.outsideDirection).replace(/[^\d]/, ''), 10);
		 //dimentions.height += this._paddingWidth;
		 gridPadding.top -= co._paddingWidth;
		 break;
		 case 'bottom':
		 co._paddingWidth = parseInt(this.target.parent().css('padding-' + co.options.outsideDirection).replace(/[^\d]/, ''), 10);
		 dimentions.height += co._paddingWidth;
		 break;
		 }
		 this.eventCanvas._elem.before(co.outsideCanvas.createElement(gridPadding, 'jqplot-overlayCanvas-canvas', dimentions, this));
		 this.eventCanvas._elem.after(co.outsideBCanvas.createElement(gridPadding, 'jqplot-overlayCanvas-canvas', dimentions, this));

		 co.outsideCanvas.setContext();
		 co.outsideBCanvas.setContext();

		 dimentions = gridPadding = null;
		 }*/

		this.eventCanvas._elem.before(co.canvas.createElement(this._gridPadding, 'jqplot-overlayCanvas-canvas', this._plotDimensions, this));
		this.eventCanvas._elem.after(co.bcanvas.createElement(this._gridPadding, 'jqplot-overlayCanvas-canvas', this._plotDimensions, this));

		co.canvas.setContext();
		co.bcanvas.setContext();

		if (!co.deferDraw) {
			co.draw(this);
		}

		var elem = document.createElement('div');
		co._tooltipElem = $(elem);
		elem = null;
		co._tooltipElem.addClass('jqplot-canvasOverlay-tooltip');
		co._tooltipElem.css({
			position : 'absolute',
			display : 'none'
		});

		this.eventCanvas._elem.before(co._tooltipElem);
		this.eventCanvas._elem.bind('mouseleave', {
			elem : co._tooltipElem
		}, function(ev) {
			ev.data.elem.hide().empty();
		});

		var co = null;
	};

	/*
	 // customizing for outside tooltip (2013-03-07)
	 function drawHorizontalOutsideTooltip(plot, obj, opts, y) {
	 var direction = this._paddingDirection;

	 if (opts.showOutsideTooltip) {
	 if (!obj.options.outsideTooltipElem || !obj.options.outsideTooltipElem.parent().offset()) {
	 if (obj.options.outsideTooltipElem) {
	 obj.options.outsideTooltipElem.each(function() {
	 $(this).children().each(function() {
	 $(this).unbind();
	 $(this).empty();
	 $(this).remove();
	 });
	 $(this).unbind();
	 $(this).empty();
	 $(this).remove();
	 });
	 obj.options.outsideTooltipElem.unbind();
	 }
	 obj.options.outsideTooltipElem = obj.options.outsideTooltipElem = $('<div></div>');
	 obj.options.outsideTooltipElem.addClass('jqplot-canvasoverlay-outsidetooltip');
	 obj.options.outsideTooltipElem.css({
	 width : (this._paddingWidth - plot._gridPadding[direction]) + 'px',
	 maxWidth : (this._paddingWidth - plot._gridPadding[direction]) + 'px'
	 });

	 plot.eventCanvas._elem.after(obj.options.outsideTooltipElem);

	 if ( typeof opts.outsideTooltipStyle == 'string') {
	 obj.options.outsideTooltipElem.addClass(opts.outsideTooltipStyle);
	 } else if ( typeof opts.outsideTooltipStyle == 'object') {
	 obj.options.outsideTooltipElem.css(opts.outsideTooltipStyle);
	 }

	 if (opts.outsideTooltipEditable && ko && opts._observableKey && ko.isObservable(opts._observableKey)) {
	 var tmpViewModel = new function() {
	 this.value = opts.observableKey;
	 this.textValue = opts._observableKey;
	 this.text = typeof opts.outsideTooltipFormatter == 'function' ? ko.computed(function() {
	 return opts.outsideTooltipFormatter(this.textValue());
	 }, this) : opts._observableKey;
	 };
	 var inputElem = $('<input type="text" data-bind="value: value" />');
	 inputElem.css({
	 overflow : 'hidden',
	 width : (this._paddingWidth - plot._gridPadding[direction]) + 'px',
	 maxWidth : (this._paddingWidth - plot._gridPadding[direction]) + 'px',
	 textAlign : direction,
	 display : 'none'
	 });
	 obj.options.outsideTooltipElem.append(inputElem);

	 var textElem = $('<div data-bind="text: text"></div>');
	 textElem.css({
	 width : (this._paddingWidth - plot._gridPadding[direction]) + 'px',
	 maxWidth : (this._paddingWidth - plot._gridPadding[direction]) + 'px',
	 textAlign : direction
	 });
	 obj.options.outsideTooltipElem.append(textElem);

	 inputElem.bind('blur', {
	 textElem : textElem
	 }, function(ev) {
	 $(this).hide();
	 ev.data.textElem.show();
	 }).bind('keydown', {
	 textElem : textElem
	 }, function(ev) {
	 if (ev.keyCode == 13) {
	 $(this).hide();
	 ev.data.textElem.show();
	 }
	 });

	 ko.applyBindings(tmpViewModel, obj.options.outsideTooltipElem.get(0));
	 tmpViewModel = null;

	 obj.options.outsideTooltipElem.css({
	 textAlign : direction
	 });

	 obj.options.outsideTooltipElem.bind('click', {
	 inputElem : inputElem,
	 textElem : textElem
	 }, function(ev) {
	 ev.data.textElem.hide();
	 ev.data.inputElem.show().focus().select();
	 });

	 inputElem = textElem = null;
	 } else if (opts.outsideTooltipEditable && (!ko || !opts._observableKey || !ko.isObservable(opts._observableKey))) {
	 ///TODO : Use DOM node
	 }
	 else {
	 obj.options.outsideTooltipElem.text( typeof opts.outsideTooltipFormatter == 'function' ? opts.outsideTooltipFormatter(plot.axes[obj.options.yaxis].series_p2u(y)) : plot.axes[obj.options.yaxis].series_p2u(y));
	 }
	 } else if (!(opts.outsideTooltipEditable && ko && opts._observableKey && ko.isObservable(opts._observableKey))) {
	 obj.options.outsideTooltipElem.text( typeof opts.outsideTooltipFormatter == 'function' ? opts.outsideTooltipFormatter(plot.axes[obj.options.yaxis].series_p2u(y)) : plot.axes[obj.options.yaxis].series_p2u(y));
	 }

	 if (opts.outsideTooltipEditable && ko && opts._observableKey && ko.isObservable(opts._observableKey)) {
	 opts._observableKey(plot.axes[obj.options.yaxis].series_p2u(y));
	 }

	 var po = obj.options.outsideTooltipElem.parent().offset();
	 var pm = {
	 left : parseInt(obj.options.outsideTooltipElem.parent().css('margin-left').replace(/[^\d]/g, ''), 10),
	 right : parseInt(obj.options.outsideTooltipElem.parent().css('margin-right').replace(/[^\d]/g, ''), 10),
	 top : parseInt(obj.options.outsideTooltipElem.parent().css('margin-top').replace(/[^\d]/g, ''), 10),
	 bottom : parseInt(obj.options.outsideTooltipElem.parent().css('margin-bottom').replace(/[^\d]/g, ''), 10)
	 };
	 var top;
	 if (direction == 'right') {
	 var pw = obj.options.outsideTooltipElem.parent().innerWidth();
	 top = (y + pm.top + plot._gridPadding.bottom + obj.options.outsideTooltipElem.outerHeight() + 10 > plot._plotDimensions.height) ? y - 10 : y + 15;
	 top += po.top;
	 obj.options.outsideTooltipElem.offset({
	 left : po.left + pm.left + pw + this._paddingWidth - plot._gridPadding[direction] - obj.options.outsideTooltipElem.outerWidth(),
	 top : top
	 });
	 pw = null;
	 } else {
	 top = (y + pm.top + plot._gridPadding.bottom + obj.options.outsideTooltipElem.outerHeight() + 10 > plot._plotDimensions.height) ? y - 10 : y + 15;
	 top += po.top;
	 obj.options.outsideTooltipElem.offset({
	 left : po.left + pm.left - this._paddingWidth + plot._gridPadding.left,
	 top : top
	 });
	 }
	 po = pm = top = null;
	 }
	 }*/

	/*
	 function drawVerticalOutsideTooltip(plot, obj, opts, x) {
	 /// TODO : Draw vertical outside tooltip
	 }*/

	function showTooltip(plot, obj, gridpos, datapos) {
		var co = plot.plugins.canvasOverlay;
		var elem = co._tooltipElem;

		var opts = obj.options, x, y;

		elem.html($.jqplot.sprintf(opts.tooltipFormatString, datapos[0], datapos[1]));

		switch (opts.tooltipLocation) {
			case 'nw':
				x = gridpos[0] + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true);
				break;
			case 'n':
				x = gridpos[0] + plot._gridPadding.left - elem.outerWidth(true) / 2;
				y = gridpos[1] + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true);
				break;
			case 'ne':
				x = gridpos[0] + plot._gridPadding.left + opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true);
				break;
			case 'e':
				x = gridpos[0] + plot._gridPadding.left + opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top - elem.outerHeight(true) / 2;
				break;
			case 'se':
				x = gridpos[0] + plot._gridPadding.left + opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top + opts.tooltipOffset;
				break;
			case 's':
				x = gridpos[0] + plot._gridPadding.left - elem.outerWidth(true) / 2;
				y = gridpos[1] + plot._gridPadding.top + opts.tooltipOffset;
				break;
			case 'sw':
				x = gridpos[0] + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top + opts.tooltipOffset;
				break;
			case 'w':
				x = gridpos[0] + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top - elem.outerHeight(true) / 2;
				break;
			default:
				// same as 'nw'
				x = gridpos[0] + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true);
				break;
		}

		elem.css('left', x);
		elem.css('top', y);
		if (opts.fadeTooltip) {
			// Fix for stacked up animations.  Thnanks Trevor!
			elem.stop(true, true).fadeIn(opts.tooltipFadeSpeed);
		} else {
			elem.show();
		}
		elem = null;
	}
	
	function clearLabel(plot) {
	  $('.jqplot-canvasoverlay-label', plot.target).each(function(index, elem) {
	    $(elem).remove();
	  });
	}
	
  function showLabel(plot, obj) {
    var xaxis = plot.axes[obj.options.xaxis], yaxis = plot.axes[obj.options.yaxis];
    var co = plot.plugins.canvasOverlay;
    var elem = co._tooltipElem.clone();
    var offset = {
      top: plot._gridPadding.top,
      left: plot._gridPadding.left
    };
    
    elem.addClass('jqplot-canvasoverlay-label');
    
    var opts = obj.options, x, y, labelOptions = opts.labelOptions;
    
    if (typeof labelOptions.classes === 'string') {
      elem.addClass(labelOptions.classes);
    }
    
    plot.target.append(elem);
    
    switch(obj.constructor) {
      case HorizontalLine:
      case DashedHorizontalLine:
        elem.html($.isFunction(labelOptions.formatter) ? labelOptions.formatter.call(obj, opts.y, plot) : opts.y);
        
        var gridStart = Math.max(obj.gridStart[0], obj.gridStop[0]), gridStop = Math.min(obj.gridStart[0], obj.gridStop[0]);
        
        switch(labelOptions.location) {
          case 'right':
            offset.left += gridStop;
            break;
          case 'center':
            offset.left += gridStart + ((gridStop - gridStart) / 2) - (elem.width() / 2);
            break;
          case 'left':
          default:
            offset.left -= elem.width() / 2;
            break;
        }
        
        offset.left += ($.isNumeric(labelOptions.offset.left) ? labelOptions.offset.left : 0);
        offset.top += yaxis.series_u2p(opts.y) + ($.isNumeric(labelOptions.offset.top) ? labelOptions.offset.top : 0) - (elem.height() / 2);
        break;
      case VerticalLine:
      case DashedVerticalLine:
        elem.html($.isFunction(labelOptions.formatter) ? labelOptions.formatter.call(obj, opts.x, plot) : opts.x);
        
        var gridStart = Math.max(obj.gridStart[1], obj.gridStop[1]), gridStop = Math.min(obj.gridStart[1], obj.gridStop[1]);
        
        switch(labelOptions.location) {
          case 'bottom':
            offset.top += gridStop;
            break;
          case 'middle':
            offset.top += gridStart + ((gridStop - gridStart) / 2) - (elem.height() / 2);
            break;
          case 'top':
          default:
            offset.top -= elem.height() / 2;
            break;
        }
        
        offset.top += ($.isNumeric(labelOptions.offset.top) ? labelOptions.offset.top : 0);
        offset.left += xaxis.series_u2p(opts.x) + ($.isNumeric(labelOptions.offset.left) ? labelOptions.offset.left : 0) - (elem.width() / 2);
        break;
    }
    
    elem.offset(offset);
    
    elem.show();
  }

	function isNearLine(point, lstart, lstop, width) {
		// r is point to test, p and q are end points.
		var rx = point[0];
		var ry = point[1];
		var px = Math.round(lstop[0]);
		var py = Math.round(lstop[1]);
		var qx = Math.round(lstart[0]);
		var qy = Math.round(lstart[1]);

		var l = Math.sqrt(Math.pow(px - qx, 2) + Math.pow(py - qy, 2));

		// scale error term by length of line.
		var eps = width * l;
		var res = Math.abs((qx - px) * (ry - py) - (qy - py) * (rx - px));
		var ret = (res < eps) ? true : false;
		return ret;
	}
	
	function isNearArrow(point, obj) {
      if (obj.options.isDraggable && obj.options.dragable.showArrow && obj.draggableArrows.length) {
        for (var j=0; j<obj.draggableArrows.length; j++) {
          var arrowPoints = obj.draggableArrows[j];
          if (checkNearArrow(point[0], point[1], arrowPoints[0], arrowPoints[1], arrowPoints[2], arrowPoints[3], arrowPoints[4], arrowPoints[5])) {
            return true; 
          }
        }
      }
      
      return false;
	}

	function isNearRectangle(point, lstart, lstop, width) {
		// r is point to test, p and q are end points.
		var rx = point[0];
		var ry = point[1];
		var px = Math.round(lstop[0]);
		var py = Math.round(lstop[1]);
		var qx = Math.round(lstart[0]);
		var qy = Math.round(lstart[1]);

		var temp;
		if (px > qx) {
			temp = px;
			px = qx;
			qx = temp;
		}
		if (py > qy) {
			temp = py;
			py = qy;
			qy = temp;
		}

		var ret = (rx >= px && rx <= qx && ry >= py && ry <= qy);

		return ret;
	}

	function checkNearLine(objs, point, plot) {
		var obj;
		var len = objs.length;
		var lstart, lstop, width;
		for (var i = 0; i < len; i++) {
			obj = objs[i];
			if (obj.type === 'rectangle') {
				continue;
			}
			lstart = obj.gridStart;
			lstop = obj.gridStop;
			width = obj.tooltipWidthFactor;

			// r is point to test, p and q are end points.
			var rx = point[0];
			var ry = point[1];
			if (lstop && lstart) {
				var px = Math.round(lstop[0]);
				var py = Math.round(lstop[1]);
				var qx = Math.round(lstart[0]);
				var qy = Math.round(lstart[1]);
			} else {
				return null;
			}

			var l = Math.sqrt(Math.pow(px - qx, 2) + Math.pow(py - qy, 2));

			// scale error term by length of line.
			var eps = width * l;
			var res = Math.abs((qx - px) * (ry - py) - (qy - py) * (rx - px));
			if (res < eps) {
				return {
          lineIndex : i
        };
			} else if (obj.options.isDraggable && obj.options.dragable.showArrow && obj.draggableArrows.length) {
			  for (var j=0; j<obj.draggableArrows.length; j++) {
			    var arrowPoints = obj.draggableArrows[j];
			    if (checkNearArrow(rx, ry, arrowPoints[0], arrowPoints[1], arrowPoints[2], arrowPoints[3], arrowPoints[4], arrowPoints[5])) {
			      return {
              lineIndex : i
            }; 
			    }
			  }
			}
		}

		return null;
	}
	
  function checkNearArrow (pointX, pointY, v1X, v1Y, v2X, v2Y, v3X, v3Y) {
    var A =  (-v2Y * v3X + v1Y * (-v2X + v3X) + v1X * (v2Y - v3Y) + v2X * v3Y) / 2;
    var sign = A < 0 ? -1 : 1;
    var s = (v1Y * v3X - v1X * v3Y + (v3Y - v1Y) * pointX + (v1X - v3X) * pointY) * sign;
    var t = (v1X * v2Y - v1Y * v2X + (v1Y - v2Y) * pointX + (v2X - v1X) * pointY) * sign;
    return s > 0 && t > 0 && s + t < 2 * A * sign;
  }

	function handleMove(ev, gridpos, datapos, neighbor, plot) {
		var co = plot.plugins.canvasOverlay;
		var objs = co.objects;
		var l = objs.length;
		var obj, haveHighlight = false;
		var elem;

		// customizing (2011-11-11, Roy Choi)
		if (co.isDragging && co._neighbor !== null) {
			/*var n = co._neighbor;
			obj = objs[n.lineIndex];
			//var drag = obj.options.dragable;

			switch (obj.type) {
				case 'line':
					return;
					break;

				case 'horizontalLine':
				case 'dashedHorizontalLine':
				    var y = datapos.yaxis;
				    if (obj.options.dragable.min != null && obj.options.dragable.min > y) {
				        y = obj.options.dragable.min;
				    } else if (obj.options.dragable.max != null && obj.options.dragable.max < y) {
				        y = obj.options.dragable.max;
				    }
					obj.options.y = y;
					co.draw(plot, obj, ev);
					break;

				case 'verticalLine':
				case 'dashedVerticalLine':
				    var x = datapos.xaxis;
                    if (obj.options.dragable.min != null && obj.options.dragable.min > x) {
                        x = obj.options.dragable.min;
                    } else if (obj.options.dragable.max != null && obj.options.dragable.max < x) {
                        x = obj.options.dragable.max;
                    }
					obj.options.x = x;
					co.draw(plot, obj, ev);
					break;
				default:
					break;
			}*/
			return;
			//haveHighlight = true;
		}

		for (var i = 0; i < l; i++) {
			obj = objs[i];
			if (obj.options.showTooltip || obj.options.isDraggable) {
				var n;
				if (obj.type === 'rectangle') {
					n = isNearRectangle([gridpos.x, gridpos.y], obj.gridStart, obj.gridStop, obj.tooltipWidthFactor);
				} else {
					n = isNearLine([gridpos.x, gridpos.y], obj.gridStart, obj.gridStop, obj.tooltipWidthFactor) || isNearArrow([gridpos.x, gridpos.y], obj);
				}
			}
			
			if(obj.options.isDraggable && !obj.options.isOver && n) {
				//obj.options._cursor.push(ev.target.style.cursor);
				switch (obj.type) {
					case 'line':
						break;
					case 'horizontalLine':
					case 'dashedHorizontalLine':
						ev.target.style.cursor = "row-resize";
						break;

					case 'verticalLine':
					case 'dashedVerticalLine':
						ev.target.style.cursor = "col-resize";
						break;
					default:
						break;
				} 
				obj.options.isOver = true;
			} else if (obj.options.isDraggable && obj.options.isOver && !n) {
				ev.target.style.cursor = /*obj.cursor.length > 0 ? obj.cursor.pop() :*/ 'auto';
				obj.options.isOver = false;
			}
			
			if (obj.options.showTooltip) {
				datapos = [plot.axes[obj.options.xaxis].series_p2u(gridpos.x), plot.axes[obj.options.yaxis].series_p2u(gridpos.y)];

				// cases:
				//    near line, no highlighting
				//    near line, highliting on this line
				//    near line, highlighting another line
				//    not near any line, highlighting
				//    not near any line, no highlighting

				// near line, not currently highlighting
				if (n && co.highlightObjectIndex == null) {
					switch (obj.type) {
						case 'line':
							showTooltip(plot, obj, [gridpos.x, gridpos.y], datapos);
							break;

						case 'horizontalLine':
						case 'dashedHorizontalLine':
							showTooltip(plot, obj, [gridpos.x, obj.gridStart[1]], [datapos[0], obj.options.y]);
							break;

						case 'verticalLine':
						case 'dashedVerticalLine':
							showTooltip(plot, obj, [obj.gridStart[0], gridpos.y], [obj.options.x, datapos[1]]);
							break;

						case 'rectangle':
							showTooltip(plot, obj, [obj.gridStart[0], gridpos.y], [obj.options.x, datapos[1]]);
							break;

						default:
							break;
					}
					co.highlightObjectIndex = i;
					haveHighlight = true;
					break;
				}

				// near line, highlighting another line.
				else if (n && co.highlightObjectIndex !== i) {
					// turn off tooltip.
					elem = co._tooltipElem;
					if (obj.fadeTooltip) {
						elem.fadeOut(obj.tooltipFadeSpeed).empty();
					} else {
						elem.hide().empty();
					}

					// turn on right tooltip.
					switch (obj.type) {
						case 'line':
							showTooltip(plot, obj, [gridpos.x, gridpos.y], datapos);
							break;

						case 'horizontalLine':
						case 'dashedHorizontalLine':
							showTooltip(plot, obj, [gridpos.x, obj.gridStart[1]], [datapos[0], obj.options.y]);
							break;

						case 'verticalLine':
						case 'dashedVerticalLine':
							showTooltip(plot, obj, [obj.gridStart[0], gridpos.y], [obj.options.x, datapos[1]]);
							break;

						case 'rectangle':
							showTooltip(plot, obj, [obj.gridStart[0], gridpos.y], [obj.options.x, datapos[1]]);
							break;

						default:
							break;
					}

					co.highlightObjectIndex = i;
					haveHighlight = true;
					break;
				}

				// near line, already highlighting this line, update
				else if (n) {
					switch (obj.type) {
						case 'line':
							showTooltip(plot, obj, [gridpos.x, gridpos.y], datapos);
							break;

						case 'horizontalLine':
						case 'dashedHorizontalLine':
							showTooltip(plot, obj, [gridpos.x, obj.gridStart[1]], [datapos[0], obj.options.y]);
							break;

						case 'verticalLine':
						case 'dashedVerticalLine':
							showTooltip(plot, obj, [obj.gridStart[0], gridpos.y], [obj.options.x, datapos[1]]);
							break;

						case 'rectangle':
							showTooltip(plot, obj, [obj.gridStart[0], gridpos.y], [obj.options.x, datapos[1]]);
							break;

						default:
							break;
					}

					haveHighlight = true;
					break;
				}
			}
		}

		// check if we are highlighting and not near a line, turn it off.
		if (!haveHighlight && co.highlightObjectIndex !== null) {
			elem = co._tooltipElem;
			obj = co.getObject(co.highlightObjectIndex);
			if (obj.fadeTooltip) {
				elem.fadeOut(obj.tooltipFadeSpeed).empty();
			} else {
				elem.hide().empty();
			}
			co.highlightObjectIndex = null;
		}
	}

	// customizing (2011-11-11, Roy Choi)
	function handleDown(ev, gridpos, datapos, neighbor, plot) {
		var co = plot.plugins.canvasOverlay;
		var objs = co.objects;

		neighbor = checkNearLine(objs, [gridpos.x, gridpos.y], plot);
		if (neighbor != null) {
			var obj = objs[neighbor.lineIndex];
			obj.cursor.push(ev.target.style.cursor);
			var dragable = obj.options.isDraggable;
			if (dragable && !co.isDragging) {
				ev.preventDefault();
				ev.stopImmediatePropagation();

				co._neighbor = neighbor;
				co.isDragging = true;
				//co.preZoomStatus.push(plot.plugins.cursor.zoom);
				co._prevCursorShow = plot.plugins.cursor.show;
				plot.plugins.cursor.show = false;

				//if (document.onselectstart != undefined) {
				co._oldHandlers.onselectstart = document.onselectstart;
				document.onselectstart = function() {
					return false;
				};
				//}
				if (document.ondrag != undefined) {
					co._oldHandlers.ondrag = document.ondrag;
					document.ondrag = function() {
						return false;
					};
				}
				if (document.onmousedown != undefined) {
					co._oldHandlers.onmousedown = document.onmousedown;
					document.onmousedown = function() {
						return false;
					};
				}
				
        $(document).on('mousemove', {plot: plot}, handleDrag);
        $(document).on('mouseup', {plot: plot}, handleUp);

				//initDragPoint(obj);
				//ev.target.style.cursor = "move";
				//plot.target.trigger('jqplotDragStart', [neighbor.lineIndex, null, gridpos, null]);

				//$(document).one('mouseup.jqplot_canvasoverlay', {plot:plot}, handleMouseUp);
			}
		} else {
			co.isDragging = false;
		}
	}
  
  function handleDrag(ev) {
    var plot = ev.data.plot;
    var co = plot.plugins.canvasOverlay;
    
    if (co.isDragging && co._neighbor !== null) {
      var position = getEventPosition(ev);
      var gridPos = position.gridPos;
      var dataPos = position.dataPos;
      var an = ['xaxis', 'yaxis', 'x2axis', 'y2axis', 'y3axis', 'y4axis', 'y5axis', 'y6axis', 'y7axis', 'y8axis', 'y9axis', 'yMidAxis'];
      var ax = plot.axes;
      var n, axis;
      for (n=11; n>0; n--) {
          axis = an[n-1];
          if (ax[axis].show) {
              dataPos[axis] = ax[axis].series_p2u(gridPos[axis.charAt(0)]);
          }
      }
  
      var n = co._neighbor;
      n.gridPos = gridPos;
      n.dataPos = dataPos;
      obj = co.get(n.lineIndex);
      //var drag = obj.options.dragable;
  
      switch (obj.type) {
        case 'line':
          return;
          break;
  
        case 'horizontalLine':
        case 'dashedHorizontalLine':
          if (dataPos[obj.options.yaxis] <= plot.axes[obj.options.yaxis].max && dataPos[obj.options.yaxis] >= plot.axes[obj.options.yaxis].min) {
            var y = dataPos[obj.options.yaxis];
            if (obj.options.dragable.min != null && obj.options.dragable.min > y) {
                y = obj.options.dragable.min;
            } else if (obj.options.dragable.max != null && obj.options.dragable.max < y) {
                y = obj.options.dragable.max;
            }
            
            if (obj.options.y === y) {
                return;
            }
            
            obj.options.y = y;
            co.draw(plot, obj, ev);
          }
          break;
  
        case 'verticalLine':
        case 'dashedVerticalLine':
          if (dataPos[obj.options.xaxis] <= plot.axes[obj.options.xaxis].max && dataPos[obj.options.xaxis] >= plot.axes[obj.options.xaxis].min) {
            var x = dataPos[obj.options.xaxis];
            if (obj.options.dragable.min != null && obj.options.dragable.min > x) {
                x = obj.options.dragable.min;
            } else if (obj.options.dragable.max != null && obj.options.dragable.max < x) {
                x = obj.options.dragable.max;
            }
            
            if (obj.options.x === x) {
                return;
            }
            
            obj.options.x = x;
            co.draw(plot, obj, ev);
          }
          break;
        default:
          break;
      }
    }
  }

	// customizing (2011-11-11, Roy Choi)
	function handleUp(ev) {
    ev.preventDefault();
    ev.stopImmediatePropagation();
    
    var plot = ev.data.plot;
    var co = plot.plugins.canvasOverlay;
		var objs = co.objects;

		if (co._neighbor !== null && co.isDragging) {

      var position = getEventPosition(ev);
      
      var obj = co.get(co._neighbor.lineIndex);
      
      var drag = obj.options.dragable;
      co.isDragging = false;
      co._neighbor = null;
      $('body').css('cursor', 'auto');
      $(plot.target).css('cursor', 'auto');
      plot.eventCanvas._elem.css('cursor', 'auto');
      plot.plugins.cursor.show = co._prevCursorShow;

			if (drag && typeof drag.afterRedraw == 'function') {
				switch(obj.constructor) {
					case HorizontalLine:
					case DashedHorizontalLine:
						drag.afterRedraw.call(obj, ev, true, obj.options.y, plot, this);
						break;
					case VerticalLine:
					case DashedVerticalLine:
						drag.afterRedraw.call(obj, ev, true, obj.options.x, plot, this);
						break;
				}
			}

			//if (document.onselectstart != undefined && c._oldHandlers.onselectstart != null){
			document.onselectstart = co._oldHandlers.onselectstart;
			co._oldHandlers.onselectstart = null;
			//}
			if (document.ondrag != undefined && co._oldHandlers.ondrag != null) {
				document.ondrag = co._oldHandlers.ondrag;
				co._oldHandlers.ondrag = null;
			}
			if (document.onmousedown != undefined && co._oldHandlers.onmousedown != null) {
				document.onmousedown = co._oldHandlers.onmousedown;
				co._oldHandlers.onmousedown = null;
			}

      $(document).off('mousemove', handleDrag);
      $(document).off('mouseup', handleUp);
		}
	}
	
  function getEventPosition(ev) {
      var plot = ev.data.plot;
      var go = plot.eventCanvas._elem.offset();
      var gridPos = {x:ev.pageX - go.left, y:ev.pageY - go.top};
      var dataPos = {xaxis:null, yaxis:null, x2axis:null, y2axis:null, y3axis:null, y4axis:null, y5axis:null, y6axis:null, y7axis:null, y8axis:null, y9axis:null, yMidAxis:null};
      var an = ['xaxis', 'yaxis', 'x2axis', 'y2axis', 'y3axis', 'y4axis', 'y5axis', 'y6axis', 'y7axis', 'y8axis', 'y9axis', 'yMidAxis'];
      var ax = plot.axes;
      var n, axis;
      for (n=11; n>0; n--) {
          axis = an[n-1];
          if (ax[axis].show) {
              dataPos[axis] = ax[axis].series_p2u(gridPos[axis.charAt(0)]);
          }
      }

      return {offsets:go, gridPos:gridPos, dataPos:dataPos};
  }

	/// TODO : outside drag
	// customizing (2013-03-11, Roy Choi)
	/*function handleMouseUp(ev, neighbor) {
	var plot = ev.data.plot;
	var co = plot.plugins.canvasOverlay;
	var objs = co.objects;

	if (co._neighbor !== null && co.isDragging) {
	var obj = objs[co._neighbor.lineIndex];
	co.isDragging = false;
	var drag = obj.options.dragable;
	// compute the new grid position with any constraints.
	//var x = (drag.constrainTo == 'y') ? datapos[0] : datapos[s.xaxis];
	//var y = (drag.constrainTo == 'x') ? dp.data[1] : datapos[s.yaxis];
	// var x = datapos[s.xaxis];
	// var y = datapos[s.yaxis];
	resetArea(obj, neighbor.gridpos, neighbor.datapos, plot);
	co._neighbor = null;
	ev.target.style.cursor = obj.cursor.pop();
	plot.plugins.cursor.zoom = co.preZoomStatus.pop();

	if (drag && typeof drag.afterRedraw == 'function') {
	switch(obj.constructor) {
	case HorizontalLine:
	case DashedHorizontalLine:
	drag.afterRedraw.call(obj, obj.options.y, plot, this);
	break;
	case VerticalLine:
	case DashedVerticalLine:
	drag.afterRedraw.call(obj, obj.options.x, plot, this);
	break;
	}

	}

	//plot.target.trigger('jqplotDragStop', [gridpos, datapos]);
	}
	}*/

	// customizing (2011-11-11, Roy Choi)
	function resetArea(obj, gridpos, datapos, plot) {
		/// TODO : outside drag
	}
	
	function drawArrow(ctx, type, point, opts, obj) {
	  var width = opts.width;
	  var height = opts.height;
	  
	  var x1, y1, x2, y2, x3, y3;
	  
    ctx.lineWidth = opts.strokeWidth;
    ctx.strokeStyle = opts.color || this.color;
    ctx.fillStyle = opts.fillStyle || this.color;
    
    var gapWidth = opts.gapWidth != null ? Math.abs(opts.gapWidth) : 0;
	  
	  switch(type) {
	     case 'top':
	       x1 = point[0] - (width / 2);
	       y1 = point[1] - gapWidth;
	       x2 = point[0] + (width / 2);
	       y2 = point[1] - gapWidth;
	       x3 = point[0];
	       y3 = point[1] - gapWidth - height;
	       break;
	     case 'bottom':
	       x1 = point[0] - (width / 2);
         y1 = point[1] + gapWidth;
         x2 = point[0] + (width / 2);
         y2 = point[1] + gapWidth;
         x3 = point[0];
         y3 = point[1] + gapWidth + height;
	       break;
	     case 'left':
	       x1 = point[0] - gapWidth;
	       y1 = point[1] - (height / 2);
	       x2 = point[0] - gapWidth;
	       y2 = point[1] + (height / 2);
	       x3 = point[0] - gapWidth - width;
	       y3 = point[1];
	       break;
	     case 'right':
	       x1 = point[0] + gapWidth;
	       y1 = point[1] - (height / 2);
	       x2 = point[0] + gapWidth;
	       y2 = point[1] + (height / 2);
	       x3 = point[0] + gapWidth + width;
	       y3 = point[1];
	       break;
	     default:
	       return;
	  }
	  
     ctx.moveTo(x1, y1);
     ctx.lineTo(x2, y2);
     ctx.lineTo(x3, y3);
     ctx.closePath();
     
     if (opts.stroke) {
       ctx.stroke();
     }
     if (opts.fill) {
       ctx.fill();
     }
     
     obj.draggableArrows.push([x1, y1, x2, y2, x3, y3]);
	}
	
	$.jqplot.postInitHooks.push($.jqplot.CanvasOverlay.postPlotInit);
	$.jqplot.postDrawHooks.push($.jqplot.CanvasOverlay.postPlotDraw);
	$.jqplot.eventListenerHooks.push(['jqplotMouseMove', handleMove]);
	$.jqplot.eventListenerHooks.push(['jqplotMouseDown', handleDown]);
	//$.jqplot.eventListenerHooks.push(['jqplotMouseUp', handleUp]);

})(jQuery);
;/**
 * jqPlot
 * Pure JavaScript plotting plugin using jQuery
 *
 * Version: 1.0.8
 *
 *
 */
(function($) {
	var objCounter = 0;
	// class: $.jqplot.CanvasWindow
	$.jqplot.CanvasWindow = function(opts) {
		var options = opts || {};
		this.options = {
			show : $.jqplot.config.enablePlugins,
			deferDraw : false,
			
			canvasExtension: null,
			canvasExtendDirection: 'top',
			canvasExtensionEvents: {},
			snapToGrid: false,
			snapToGridInterval: null,
			defaultConfigIcon: ''
		};
		// prop: objects
		this.objects = [];
		this.objectNames = [];
		this.canvas = null;
		this.markerRenderer = new $.jqplot.MarkerRenderer({
			style : 'line'
		});
		this.markerRenderer.init();
		this.highlightObjectIndex = null;

		this.bcanvas = null;
		this._neighbor = null;
		this.isDragging = false;
		this.preZoomStatus = [];
		this._oldHandlers = {};
		
		this._prevCursorShow = false;

		if (options.objects) {
			var objs = options.objects, obj;
			for (var i = 0; i < objs.length; i++) {
				obj = objs[i];
				for (var n in obj) {
					switch (n) {
						case 'line':
							this.addLine(obj[n]);
							break;
						case 'horizontalLine':
							this.addHorizontalLine(obj[n]);
							break;
						case 'dashedHorizontalLine':
							this.addDashedHorizontalLine(obj[n]);
							break;
						case 'verticalLine':
							this.addVerticalLine(obj[n]);
							break;
						case 'dashedVerticalLine':
							this.addDashedVerticalLine(obj[n]);
							break;
						case 'rectangle':
							this.addRectangle(obj[n]);
							break;
						default:
							break;
					}
				}
			}
		}
		$.extend(true, this.options, options);
	};

	// called with scope of a plot object
	$.jqplot.CanvasWindow.postPlotInit = function(target, data, opts) {
		var options = opts || {};
		// add a canvasWindow attribute to the plot
		this.plugins.canvasWindow = new $.jqplot.CanvasWindow(options.canvasWindow);
		
		this.target.unbind('mouseleave', handleLeave);
		this.target.bind('mouseleave', {plot: this}, handleLeave);
	};

	// customizing
	function ShapeBase() {
		this.uid = null;
		this.type = null;
		this.gridStart = null;
		this.gridStop = null;
		this.tooltipWidthFactor = 0;
		this.options = {
			// prop: name
			// Optional name for the overlay object.
			// Can be later used to retrieve the object by name.
			name : null,
			// prop: show
			// true to show (draw), false to not draw.
			show : true,
			// prop: lineWidth
			// Width of the line.
			lineWidth : 2,
			// prop: lineCap
			// Type of ending placed on the line ['round', 'butt', 'square']
			lineCap : 'round',
			// prop: color
			// color of the line
			color : '#666666',
			// prop: shadow
			// wether or not to draw a shadow on the line
			shadow : true,
			// prop: shadowAngle
			// Shadow angle in degrees
			shadowAngle : 45,
			// prop: shadowOffset
			// Shadow offset from line in pixels
			shadowOffset : 1,
			// prop: shadowDepth
			// Number of times shadow is stroked, each stroke offset shadowOffset from the last.
			shadowDepth : 3,
			// prop: shadowAlpha
			// Alpha channel transparency of shadow.  0 = transparent.
			shadowAlpha : '0.07',
			// prop: xaxis
			// X axis to use for positioning/scaling the line.
			xaxis : 'xaxis',
			// prop: yaxis
			// Y axis to use for positioning/scaling the line.
			yaxis : 'yaxis',
			// prop: showTooltip
			// Show a tooltip with data point values.
			showTooltip : false,
			// prop: showTooltipPrecision
			// Controls how close to line cursor must be to show tooltip.
			// Higher number = closer to line, lower number = farther from line.
			// 1.0 = cursor must be over line.
			showTooltipPrecision : 0.6,
			// prop: tooltipLocation
			// Where to position tooltip, 'n', 'ne', 'e', 'se', 's', 'sw', 'w', 'nw'
			tooltipLocation : 'nw',
			// prop: fadeTooltip
			// true = fade in/out tooltip, flase = show/hide tooltip
			fadeTooltip : true,
			// prop: tooltipFadeSpeed
			// 'slow', 'def', 'fast', or number of milliseconds.
			tooltipFadeSpeed : "fast",
			// prop: tooltipOffset
			// Pixel offset of tooltip from the highlight.
			tooltipOffset : 4,
			// prop: tooltipFormatString
			// Format string passed the x and y values of the cursor on the line.
			// e.g., 'Dogs: %.2f, Cats: %d'.
			tooltipFormatString : '%d, %d',
			
			// overlay or underlay
			isBackground : false,
			isFullLine : false,
			fillBehind : false,
			
			configIcon: {
			    show: false,
			    image: null,
			    width: 0,
			    height: 0,
			    style: {},   // css style
			    classes: [], // css classes
			    location: 'ne', // same with tooltip
			    events: {}
			}
		};
	}

	// customizing
	function LineBase() {
		ShapeBase.call(this);
		
		this.cursor = [];
		this.isOver = false;

		var opts = {
			// customizing (2011-11-03, Roy Choi)
			// dragable line
			isDraggable : false,
			dragable : {
				color : 'rgba(200,200,200,0.5)',
				constrainTo : 'y',
				afterRedraw : null,
				showTooltip: false,
				notifyOnDrag : false		// customizing (2013-03-07, Roy Choi)
			},
			fillArea : false,
			fillStyle : null,
			fillToValue : 0,
			fillToMax : false,
			fillToMin : false,
			fillToBind : null

		};

		$.extend(true, this.options, opts);
	}

	function Rectangle(options) {
		ShapeBase.call(this);
		this.type = 'rectangle';
		var opts = {
			// prop: xmin
			// x value for the start of the line, null to scale to axis min.
			xmin : null,
			// prop: xmax
			// x value for the end of the line, null to scale to axis max.
			xmax : null,
			// prop xOffset
			// offset ends of the line inside the grid. Number
			xOffset : '6px', // number or string. Number interpreted as units, string as pixels.
			xminOffset : null,
			xmaxOffset : null,

			ymin : null,
			ymax : null,
			yOffset : '6px', // number or string. Number interpreted as units, string as pixels.
			yminOffset : null,
			ymaxOffset : null,
			
			// customizing (2014-04-16, Roy Choi)
			xminBind: null,
			xmaxBind: null,
			yminBind: null,
			ymaxBind: null
		};
		$.extend(true, this.options, opts, options);

		if (this.options.showTooltipPrecision < 0.01) {
			this.options.showTooltipPrecision = 0.01;
		}
	}

	/**
	 * Class: Line
	 * A straight line.
	 */
	function Line(options) {
		LineBase.call(this);
		this.type = 'line';
		var opts = {
			// prop: start
			// [x, y] coordinates for the start of the line.
			start : [],
			// prop: stop
			// [x, y] coordinates for the end of the line.
			stop : []
		};
		$.extend(true, this.options, opts, options);

		if (this.options.showTooltipPrecision < 0.01) {
			this.options.showTooltipPrecision = 0.01;
		}
	}


	Line.prototype = new LineBase();
	Line.prototype.constructor = Line;

	/**
	 * Class: HorizontalLine
	 * A straight horizontal line.
	 */
	function HorizontalLine(options) {
		LineBase.call(this);
		this.type = 'horizontalLine';
		var opts = {
			// prop: y
			// y value to position the line
			y : null,
			// prop: xmin
			// x value for the start of the line, null to scale to axis min.
			xmin : null,
			// prop: xmax
			// x value for the end of the line, null to scale to axis max.
			xmax : null,
			// prop xOffset
			// offset ends of the line inside the grid.  Number
			xminBind : null,
			xmaxBind : null,
			xOffset : '6px', // number or string.  Number interpreted as units, string as pixels.
			xminOffset : null,
			xmaxOffset : null
		};
		$.extend(true, this.options, opts, options);

		if (this.options.showTooltipPrecision < 0.01) {
			this.options.showTooltipPrecision = 0.01;
		}
	}


	HorizontalLine.prototype = new LineBase();
	HorizontalLine.prototype.constructor = HorizontalLine;

	/**
	 * Class: DashedHorizontalLine
	 * A straight dashed horizontal line.
	 */
	function DashedHorizontalLine(options) {
		LineBase.call(this);
		this.type = 'dashedHorizontalLine';
		var opts = {
			y : null,
			xmin : null,
			xmax : null,
			xminBind : null,
			xmaxBind : null,
			xOffset : '6px', // number or string.  Number interpreted as units, string as pixels.
			xminOffset : null,
			xmaxOffset : null,
			// prop: dashPattern
			// Array of line, space settings in pixels.
			// Default is 8 pixel of line, 8 pixel of space.
			// Note, limit to a 2 element array b/c of bug with higher order arrays.
			dashPattern : [8, 8]
		};
		$.extend(true, this.options, opts, options);

		if (this.options.showTooltipPrecision < 0.01) {
			this.options.showTooltipPrecision = 0.01;
		}
	}


	DashedHorizontalLine.prototype = new LineBase();
	DashedHorizontalLine.prototype.constructor = DashedHorizontalLine;

	/**
	 * Class: VerticalLine
	 * A straight vertical line.
	 */
	function VerticalLine(options) {
		LineBase.call(this);
		this.type = 'verticalLine';
		var opts = {
			x : null,
			ymin : null,
			ymax : null,
			yOffset : '6px', // number or string.  Number interpreted as units, string as pixels.
			yminOffset : null,
			ymaxOffset : null
		};
		$.extend(true, this.options, opts, options);

		if (this.options.showTooltipPrecision < 0.01) {
			this.options.showTooltipPrecision = 0.01;
		}
	}


	VerticalLine.prototype = new LineBase();
	VerticalLine.prototype.constructor = VerticalLine;

	/**
	 * Class: DashedVerticalLine
	 * A straight dashed vertical line.
	 */
	function DashedVerticalLine(options) {
		LineBase.call(this);
		this.type = 'dashedVerticalLine';
		this.start = null;
		this.stop = null;
		var opts = {
			x : null,
			ymin : null,
			ymax : null,
			yOffset : '6px', // number or string.  Number interpreted as units, string as pixels.
			yminOffset : null,
			ymaxOffset : null,
			// prop: dashPattern
			// Array of line, space settings in pixels.
			// Default is 8 pixel of line, 8 pixel of space.
			// Note, limit to a 2 element array b/c of bug with higher order arrays.
			dashPattern : [8, 8]
		};
		$.extend(true, this.options, opts, options);

		if (this.options.showTooltipPrecision < 0.01) {
			this.options.showTooltipPrecision = 0.01;
		}
	}


	DashedVerticalLine.prototype = new LineBase();
	DashedVerticalLine.prototype.constructor = DashedVerticalLine;

	$.jqplot.CanvasWindow.prototype.addLine = function(opts) {
		var line = new Line(opts);
		line.canvas = opts.isBackground ? this.bcanvas : this.canvas;
		line.uid = objCounter++;
		this.objects.push(line);
		this.objectNames.push(line.options.name);
	};

	$.jqplot.CanvasWindow.prototype.addHorizontalLine = function(opts) {
		var line = new HorizontalLine(opts);
		line.canvas = opts.isBackground ? this.bcanvas : this.canvas;
		line.uid = objCounter++;
		this.objects.push(line);
		this.objectNames.push(line.options.name);
	};

	$.jqplot.CanvasWindow.prototype.addDashedHorizontalLine = function(opts) {
		var line = new DashedHorizontalLine(opts);
		line.canvas = opts.isBackground ? this.bcanvas : this.canvas;
		line.uid = objCounter++;
		this.objects.push(line);
		this.objectNames.push(line.options.name);
	};

	$.jqplot.CanvasWindow.prototype.addVerticalLine = function(opts) {
		var line = new VerticalLine(opts);
		line.canvas = opts.isBackground ? this.bcanvas : this.canvas;
		line.uid = objCounter++;
		this.objects.push(line);
		this.objectNames.push(line.options.name);
	};

	$.jqplot.CanvasWindow.prototype.addDashedVerticalLine = function(opts) {
		var line = new DashedVerticalLine(opts);
		line.canvas = opts.isBackground ? this.bcanvas : this.canvas;
		line.uid = objCounter++;
		this.objects.push(line);
		this.objectNames.push(line.options.name);
	};

	$.jqplot.CanvasWindow.prototype.addRectangle = function(opts) {
		var line = new Rectangle(opts);
		line.canvas = opts.isBackground ? this.bcanvas : this.canvas;
		line.uid = objCounter++;
		this.objects.push(line);
		this.objectNames.push(line.options.name);
	};

	// customizing (2013-01-24, Roy Choi)
	$.jqplot.CanvasWindow.prototype.modifyObject = function(plot, name, opts) {
		var obj = this.get(name);
		try {
			if (obj) {
				obj.options = $.extend(true, obj.options, opts);
				this.draw(plot, obj);
				return true;
			} else {
				return false;
			}
		} catch(e) {
			return false;
		} finally {
			obj = null;
		}
	};
	
	$.jqplot.CanvasWindow.prototype.setObjects = function(plot, objects) {
		if ($.isArray(objects)) {
			var cw = this;
			cw.removeObject();
			objects.forEach(function(object) {
				var type = Object.keys(object).shift();
				var obj = object[type];
				switch (type) {
					case 'line':
						cw.addLine(obj);
						break;
					case 'horizontalLine':
						cw.addHorizontalLine(obj);
						break;
					case 'dashedHorizontalLine':
						cw.addDashedHorizontalLine(obj);
						break;
					case 'verticalLine':
						cw.addVerticalLine(obj);
						break;
					case 'dashedVerticalLine':
						cw.addDashedVerticalLine(obj);
						break;
					case 'rectangle':
						cw.addRectangle(obj);
						break;
					default:
						break;
				}
			});
			cw.draw(plot);
		} 
	};
	
	$.jqplot.CanvasWindow.prototype.addObjects = function(plot, objects) {
		if ($.isArray(objects) && objects.length) {
			var cw = this;
			objects.forEach(function(object) {
				var type = Object.keys(object).shift();
				var obj = object[type];
				switch (type) {
					case 'line':
						cw.addLine(obj);
						break;
					case 'horizontalLine':
						cw.addHorizontalLine(obj);
						break;
					case 'dashedHorizontalLine':
						cw.addDashedHorizontalLine(obj);
						break;
					case 'verticalLine':
						cw.addVerticalLine(obj);
						break;
					case 'dashedVerticalLine':
						cw.addDashedVerticalLine(obj);
						break;
					case 'rectangle':
						cw.addRectangle(obj);
						break;
					default:
						break;
				}
			});
			cw.draw(plot);
		} 
	};

	$.jqplot.CanvasWindow.prototype.removeObject = function(idx) {
		// check if integer, remove by index
		if ($.type(idx) == 'number') {
			this.objects.splice(idx, 1);
			this.objectNames.splice(idx, 1);
		}
		// if string, remove by name
		else if ($.type(idx) === 'string') {
			var id = $.inArray(idx, this.objectNames);
			if (id != -1) {
				this.objects.splice(id, 1);
				this.objectNames.splice(id, 1);
			}
			// all objects
		} else {
			this.objectNames.splice(0);
			this.objects.splice(0);
		}
	};

	$.jqplot.CanvasWindow.prototype.getObject = function(idx) {
		// check if integer, remove by index
		if ($.type(idx) == 'number') {
			return this.objects[idx];
		}
		// if string, remove by name
		else {
			var id = $.inArray(idx, this.objectNames);
			if (id != -1) {
				return this.objects[id];
			}
		}
	};

	// Set get as alias for getObject.
	$.jqplot.CanvasWindow.prototype.get = $.jqplot.CanvasWindow.prototype.getObject;

	$.jqplot.CanvasWindow.prototype.clear = function(plot) {
		if (!this.canvas || !this.bcanvas) {
			return;
		}
		this.canvas._ctx.clearRect(0, 0, this.canvas.getWidth() + 1, this.canvas.getHeight() + 1);
		this.bcanvas._ctx.clearRect(0, 0, this.bcanvas.getWidth() + 1, this.bcanvas.getHeight() + 1);
		$('.jqplot-canvaswindow-configicon', plot.target).each(function() {
		  $(this).remove();
		});
	};

	$.jqplot.CanvasWindow.prototype.draw = function(plot, dragobj, ev) {
		var obj, objs = this.objects, mr = this.markerRenderer, start, stop;
		if (this.options.show) {
			this.clear(plot);
			/*this.canvas._ctx.clearRect(0,0,this.canvas.getWidth(), this.canvas.getHeight());
			 this.bcanvas._ctx.clearRect(0,0,this.bcanvas.getWidth(), this.bcanvas.getHeight());*/

			for (var k = 0; k < objs.length; k++) {
				obj = objs[k];
				var opts = $.extend(true, {}, obj.options);
				if (opts.show) {
					if (obj.canvas === null) {
						obj.canvas = opts.isBackground ? this.bcanvas : this.canvas;
					}
				
					// style and shadow properties should be set before
					// every draw of marker renderer.
					mr.shadow = opts.shadow;
					obj.tooltipWidthFactor = opts.lineWidth / opts.showTooltipPrecision;
					
					// TODO: bug fix plot.axes[opts.xaxis].series_u2p
					// Temporary
					if (opts.xaxis === 'xaxis' && typeof plot.axes[opts.xaxis].series_u2p === 'undefined') {
						var xaxis_series_u2p = plot.axes['x2axis'].series_u2p;
					} else {
						var xaxis_series_u2p = plot.axes[opts.xaxis].series_u2p;
					}
					
					if (opts.yaxis === 'yaxis' && typeof plot.axes[opts.yaxis].series_u2p === 'undefined') {
						var yaxis_series_u2p = plot.axes['y2axis'].series_u2p;
					} else {
						var yaxis_series_u2p = plot.axes[opts.yaxis].series_u2p;
					}
					
					
					switch (obj.type) {
						case 'line':
							// style and shadow properties should be set before
							// every draw of marker renderer.
							mr.style = 'line';
							opts.closePath = false;
							start = [xaxis_series_u2p(opts.start[0]), yaxis_series_u2p(opts.start[1])];
							stop = [xaxis_series_u2p(opts.stop[0]), yaxis_series_u2p(opts.stop[1])];
							obj.gridStart = start;
							obj.gridStop = stop;
							mr.draw(start, stop, obj.canvas._ctx, opts);
							break;
						case 'horizontalLine':
							// style and shadow properties should be set before
							// every draw of marker renderer.
							if (opts.y != null) {
								mr.style = 'line';
								opts.closePath = false;
								var xaxis = plot.axes[opts.xaxis], yaxis = plot.axes[opts.yaxis], xstart = xaxis.series_u2p(xaxis.min), xstop = xaxis.series_u2p(xaxis.max), y = yaxis_series_u2p(opts.y), xminoff = opts.xminOffset || opts.xOffset, xmaxoff = opts.xmaxOffset || opts.xOffset, fillStart = xaxis.series_u2p(xaxis.min), fillStop = xaxis.series_u2p(xaxis.max);
								if (opts.xmin != null) {
									fillStart = xaxis.series_u2p(opts.xmin);
									if (!opts.isFullLine)
										xstart = fillStart;
								} else if (opts.xminBind != null) {
									fillStart = xaxis.series_u2p(this.get(opts.xminBind).options.x);
									if (!opts.isFullLine)
										xstart = fillStart;
								} else if (xminoff != null) {
									if ($.type(xminoff) == "number") {
										fillStart = xaxis.series_u2p(xaxis.min + xminoff);
										if (!opts.isFullLine)
											xstart = fillStart;
									} else if ($.type(xminoff) == "string") {
										fillStart = xaxis.series_u2p(xaxis.min) + parseFloat(xminoff);
										if (!opts.isFullLine)
											xstart = fillStart;
									}
								}
								if (opts.xmax != null) {
									fillStop = xaxis.series_u2p(opts.xmax);
									if (!opts.isFullLine)
										xstop = fillStop;
								} else if (opts.xmaxBind != null && this.get(opts.xmaxBind).options.show) {
									fillStop = xaxis.series_u2p(this.get(opts.xmaxBind).options.x);
									if (!opts.isFullLine)
										xstop = fillStop;
								} else if (xmaxoff != null) {
									if ($.type(xmaxoff) == "number") {
										fillStop = xaxis.series_u2p(xaxis.max - xmaxoff);
										if (!opts.isFullLine)
											xstop = fillStop;
									} else if ($.type(xmaxoff) == "string") {
										fillStop = xaxis.series_u2p(xaxis.max) - parseFloat(xmaxoff);
										if (!opts.isFullLine)
											xstop = fillStop;
									}
								}
								if (xstop != null && xstart != null) {
									obj.gridStart = [xstart, y];
									obj.gridStop = [xstop, y];

									mr.draw([xstart, y], [xstop, y], obj.canvas._ctx, opts);

									if (opts.fillArea) {
										var ctx = opts.fillBehind ? this.bcanvas._ctx : obj.canvas._ctx;
										
										if (fillStart < 0) {
											fillStart = 0;
											fillStop += parseFloat(xminoff);
										}

										// customizing for outsideTooltip (2013-03-04, Roy choi)
										if (this.options.outsideDraw) {
											if (this._paddingDirection == 'left') {
												fillStart += this._paddingWidth;
												fillStop += this._paddingWidth;
											}
										}

										ctx.fillStyle = opts.fillStyle;
										var fillToValue = null;
										if (opts.fillToBind && this.get(opts.fillToBind)) {
											fillToValue = this.get(opts.fillToBind).options.y;
										} else if (opts.fillToMin) {
											fillToValue = yaxis.min;
										} else if (opts.fillToMax) {
											fillToValue = yaxis.max;
										} else if (opts.fillToValue != null) {
											fillToValue = opts.fillToValue;
										}
										//var fillToValue = opts.fillToBind && this.get(opts.fillToBind) ? this.get(opts.fillToBind).options.y : opts.fillToValue;
										var ystart = yaxis.series_u2p(fillToValue);
										var ystop = yaxis.series_u2p(opts.y) - yaxis.series_u2p(fillToValue);
										ctx.fillRect(fillStart, ystart, fillStop - fillStart, ystop);
										
										if (opts.configIcon.show && (this.options.defaultConfigIcon.match(/\.(bmp|jpeg|jpg|gif|png)$/i) || opts.configIcon.image.match(/\.(bmp|jpeg|jpg|gif|png)$/i))) {
                        showConfigIcon(plot, obj, opts.configIcon, {
                          x: fillStart,
                          y: ystart,
                          width: fillStop - fillStart,
                          height: ystop
                        }, ctx.canvas);
                    }
										
										ctx.restore();
										
										ctx = null;
									}
								}
								
								if (opts.showTooltip && this.isDragging && opts.dragable && opts.dragable.showTooltip && dragobj && dragobj.options.name === opts.name) {
									var neighbor = this._neighbor;
									showTooltip(plot, obj, [neighbor.gridPos.x, neighbor.gridPos.y], neighbor.dataPos);
								}
								
								if (opts.dragable && opts.dragable.notifyOnDrag && $.isFunction(opts.dragable.afterRedraw) && this.isDragging && dragobj && dragobj.options.name === opts.name) {
									opts.dragable.afterRedraw.call(obj, ev, false, opts.y, plot, this);
								}
							}

							break;

						case 'dashedHorizontalLine':

							var dashPat = opts.dashPattern;
							var dashPatLen = 0;
							for (var i = 0; i < dashPat.length; i++) {
								dashPatLen += dashPat[i];
							}

							// style and shadow properties should be set before
							// every draw of marker renderer.
							if (opts.y != null) {
								mr.style = 'line';
								opts.closePath = false;
								var xaxis = plot.axes[opts.xaxis], xstart = xaxis.series_u2p(xaxis.min), xstop = xaxis.series_u2p(xaxis.max), y = yaxis_series_u2p(opts.y), xminoff = opts.xminOffset || opts.xOffset, xmaxoff = opts.xmaxOffset || opts.xOffset, fillStart = xaxis.series_u2p(xaxis.min), fillStop = xaxis.series_u2p(xaxis.max);
								if (opts.xmin != null) {
									fillStart = xaxis.series_u2p(opts.xmin);
									if (!opts.isFullLine)
										xstart = fillStart;
								} else if (opts.xminBind != null) {
									fillStart = xaxis.series_u2p(this.get(opts.xminBind).options.x);
									if (!opts.isFullLine)
										xstart = fillStart;
								} else if (xminoff != null) {
									if ($.type(xminoff) == "number") {
										fillStart = xaxis.series_u2p(xaxis.min + xminoff);
										if (!opts.isFullLine)
											xstart = fillStart;
									} else if ($.type(xminoff) == "string") {
										fillStart = xaxis.series_u2p(xaxis.min) + parseFloat(xminoff);
										if (!opts.isFullLine)
											xstart = fillStart;
									}
								}
								if (opts.xmax != null) {
									fillStop = xaxis.series_u2p(opts.xmax);
									if (!opts.isFullLine)
										xstop = fillStop;
								} else if (opts.xmaxBind != null) {
									fillStop = xaxis.series_u2p(this.get(opts.xmaxBind).options.x);
									if (!opts.isFullLine)
										xstop = fillStop;
								} else if (xmaxoff != null) {
									if ($.type(xmaxoff) == "number") {
										fillStop = xaxis.series_u2p(xaxis.max - xmaxoff);
										if (!opts.isFullLine)
											xstop = fillStop;
									} else if ($.type(xmaxoff) == "string") {
										fillStop = xaxis.series_u2p(xaxis.max) - parseFloat(xmaxoff);
										if (!opts.isFullLine)
											xstop = fillStop;
									}
								}
								if (xstop != null && xstart != null) {
									obj.gridStart = [xstart, y];
									obj.gridStop = [xstop, y];

									var numDash = Math.ceil((xstop - xstart) / dashPatLen);
									var b = xstart, e;
									for (var i = 0; i < numDash; i++) {
										for (var j = 0; j < dashPat.length; j += 2) {
											e = b + dashPat[j];
											mr.draw([b, y], [e, y], obj.canvas._ctx, opts);
											b += dashPat[j];
											if (j < dashPat.length - 1) {
												b += dashPat[j + 1];
											}
										}
									}
									if (opts.fillArea) {
										var ctx = opts.fillBehind ? this.bcanvas._ctx : obj.canvas._ctx;
										
										if (fillStart < 0) {
											fillStart = 0;
											fillStop += parseFloat(xminoff);
										}

										// customizing for outsideTooltip (2013-03-04, Roy choi)
										if (this.options.outsideDraw) {
											if (this._paddingDirection == 'left') {
												fillStart += this._paddingWidth;
												fillStop += this._paddingWidth;
											}
										}

										ctx.fillStyle = opts.fillStyle;
										var fillToValue = null;
										if (opts.fillToBind && this.get(opts.fillToBind)) {
											fillToValue = this.get(opts.fillToBind).options.y;
										} else if (opts.fillToMin) {
											fillToValue = yaxis.min;
										} else if (opts.fillToMax) {
											fillToValue = yaxis.max;
										} else if (opts.fillToValue != null) {
											fillToValue = opts.fillToValue;
										}
										//var fillToValue = opts.fillToBind && this.get(opts.fillToBind) ? this.get(opts.fillToBind).options.y : opts.fillToValue;
										var ystart = yaxis.series_u2p(fillToValue);
										var ystop = yaxis.series_u2p(opts.y) - yaxis.series_u2p(fillToValue);
										ctx.fillRect(fillStart, ystart, fillStop - fillStart, ystop);
										
										if (opts.configIcon.show && (this.options.defaultConfigIcon.match(/\.(bmp|jpeg|jpg|gif|png)$/i) || opts.configIcon.image.match(/\.(bmp|jpeg|jpg|gif|png)$/i))) {
                        showConfigIcon(plot, obj, opts.configIcon, {
                          x: fillStart,
                          y: ystart,
                          width: fillStop - fillStart,
                          height: ystop
                        }, ctx.canvas);
                    }
										
										ctx.restore();
											
										ctx = null;
									}
								}
								
								if (opts.showTooltip && this.isDragging && opts.dragable && opts.dragable.showTooltip && dragobj && dragobj.options.name === opts.name) {
									var neighbor = this._neighbor;
									showTooltip(plot, obj, [neighbor.gridPos.x, neighbor.gridPos.y], neighbor.dataPos);
								}

								if (opts.dragable && opts.dragable.notifyOnDrag && $.isFunction(opts.dragable.afterRedraw) && this.isDragging && dragobj && dragobj.options.name === opts.name) {
									opts.dragable.afterRedraw.call(obj, ev, false, opts.y, plot, this);
								}
							}
							break;

						case 'verticalLine':

							// style and shadow properties should be set before
							// every draw of marker renderer.
							if (opts.x != null) {
								mr.style = 'line';
								opts.closePath = false;
								var yaxis = plot.axes[opts.yaxis], xaxis = plot.axes[opts.xaxis], ystart, ystop, x = xaxis_series_u2p(opts.x), yminoff = opts.yminOffset || opts.yOffset, ymaxoff = opts.ymaxOffset || opts.yOffset;
								
								if (opts.isFullLine) {
									if (this.options.canvasExtension) {
										ystart = obj.canvas._ctx.canvas.height;
										ystop = 0;
									}
								} else {
									ystart = yaxis.min;
									ystop = yaxis.max;
									
									if (opts.ymin != null) {
										ystart = yaxis.series_u2p(opts.ymin);
									} else if (yminoff != null) {
										if ($.type(yminoff) == "number") {
											ystart = yaxis.series_u2p(yaxis.min - yminoff);
										} else if ($.type(yminoff) == "string") {
											ystart = yaxis.series_u2p(yaxis.min) - parseFloat(yminoff);
										}
									}
									if (opts.ymax != null) {
										ystop = yaxis.series_u2p(opts.ymax);
									} else if (ymaxoff != null) {
										if ($.type(ymaxoff) == "number") {
											ystop = yaxis.series_u2p(yaxis.max + ymaxoff);
										} else if ($.type(ymaxoff) == "string") {
											ystop = yaxis.series_u2p(yaxis.max) + parseFloat(ymaxoff);
										}
									}
								}

								if (ystop != null && ystart != null) {
									obj.gridStart = [x, ystart];
									obj.gridStop = [x, ystop];

									mr.draw([x, ystart], [x, ystop], obj.canvas._ctx, opts);

									if (opts.fillArea) {
										var ctx = opts.fillBehind ? this.bcanvas._ctx : obj.canvas._ctx;
										
										ctx.fillStyle = opts.fillStyle;

										var fillToValue = null;
										if (opts.fillToBind && this.get(opts.fillToBind)) {
											fillToValue = this.get(opts.fillToBind).options.x;
										} else if (opts.fillToMin) {
											fillToValue = xaxis.min;
										} else if (opts.fillToMax) {
											fillToValue = xaxis.max;
										} else if (opts.fillToValue != null) {
											fillToValue = opts.fillToValue;
										}
										//var fillToValue = opts.fillToBind && this.get(opts.fillToBind) ? this.get(opts.fillToBind).options.x : opts.fillToValue;
										var xstop = xaxis.series_u2p(opts.x) - xaxis.series_u2p(fillToValue);
										var xstart = xaxis.series_u2p(fillToValue);
										ctx.fillRect(xstart, ystart, xstop, ystop - ystart);
										
										if (opts.configIcon.show && (this.options.defaultConfigIcon.match(/\.(bmp|jpeg|jpg|gif|png)$/i) || opts.configIcon.image.match(/\.(bmp|jpeg|jpg|gif|png)$/i))) {
										    showConfigIcon(plot, obj, opts.configIcon, {
										      x: xstart,
										      y: ystart,
										      width: xstop,
										      height: ystop - ystart
										    }, ctx.canvas);
										}
										
										ctx.restore();
									}
								}
								
								if (opts.showTooltip && this.isDragging && opts.dragable && opts.dragable.showTooltip && dragobj && dragobj.options.name === opts.name) {
									var neighbor = this._neighbor;
									var ypos = this.options.canvasExtension && this.options.canvasExtendDirection === 'top' ? neighbor.gridPos.y - this.options.canvasExtension : neighbor.gridPos.y;
									showTooltip(plot, obj, [neighbor.gridPos.x, ypos], neighbor.dataPos);
								}

								if (opts.dragable && opts.dragable.notifyOnDrag && $.isFunction(opts.dragable.afterRedraw) && this.isDragging && dragobj && dragobj.options.name === opts.name) {
									opts.dragable.afterRedraw.call(obj, ev, false, opts.x, plot, this);
								}
							}
							break;

						case 'dashedVerticalLine':

							var dashPat = opts.dashPattern;
							var dashPatLen = 0;
							for (var i = 0; i < dashPat.length; i++) {
								dashPatLen += dashPat[i];
							}

							// style and shadow properties should be set before
							// every draw of marker renderer.
							if (opts.x != null) {
								mr.style = 'line';
								opts.closePath = false;
								var yaxis = plot.axes[opts.yaxis], xaxis = plot.axes[opts.xaxis], ystart, ystop, x = xaxis_series_u2p(opts.x), yminoff = opts.yminOffset || opts.yOffset, ymaxoff = opts.ymaxOffset || opts.yOffset;
								
								if (opts.isFullLine) {
									if (this.options.canvasExtension) {
										ystart = obj.canvas._ctx.canvas.height;
										ystop = 0;
									}
								} else {
									ystart = yaxis.min;
									ystop = yaxis.max;
									
									if (opts.ymin != null) {
										ystart = yaxis.series_u2p(opts.ymin);
									} else if (yminoff != null) {
										if ($.type(yminoff) == "number") {
											ystart = yaxis.series_u2p(yaxis.min - yminoff);
										} else if ($.type(yminoff) == "string") {
											ystart = yaxis.series_u2p(yaxis.min) - parseFloat(yminoff);
										}
									}
									if (opts.ymax != null) {
										ystop = yaxis.series_u2p(opts.ymax);
									} else if (ymaxoff != null) {
										if ($.type(ymaxoff) == "number") {
											ystop = yaxis.series_u2p(yaxis.max + ymaxoff);
										} else if ($.type(ymaxoff) == "string") {
											ystop = yaxis.series_u2p(yaxis.max) + parseFloat(ymaxoff);
										}
									}
								}

								if (ystop != null && ystart != null) {
									obj.gridStart = [x, ystart];
									obj.gridStop = [x, ystop];


									var numDash = Math.ceil((ystart - ystop) / dashPatLen);
									var firstDashAdjust = ((numDash * dashPatLen) - (ystart - ystop)) / 2.0;
									var b = ystart, e, bs, es;
									for (var i = 0; i < numDash; i++) {
										for (var j = 0; j < dashPat.length; j += 2) {
											e = b - dashPat[j];
											if (e < ystop) {
												e = ystop;
											}
											if (b < ystop) {
												b = ystop;
											}
											// es = e;
											// if (i == 0) {
											//  es += firstDashAdjust;
											// }
											mr.draw([x, b], [x, e], obj.canvas._ctx, opts);
											b -= dashPat[j];
											if (j < dashPat.length - 1) {
												b -= dashPat[j + 1];
											}
										}
									}
									if (opts.fillArea) {
										
										var ctx = opts.fillBehind ? this.bcanvas._ctx : obj.canvas._ctx;

										ctx.fillStyle = opts.fillStyle;

										var fillToValue = null;
										if (opts.fillToBind && this.get(opts.fillToBind)) {
											fillToValue = this.get(opts.fillToBind).options.x;
										} else if (opts.fillToMin) {
											fillToValue = xaxis.min;
										} else if (opts.fillToMax) {
											fillToValue = xaxis.max;
										} else if (opts.fillToValue != null) {
											fillToValue = opts.fillToValue;
										}
										//var fillToValue = opts.fillToBind && this.get(opts.fillToBind) ? this.get(opts.fillToBind).options.x : opts.fillToValue;
										var xstop = xaxis.series_u2p(opts.x) - xaxis.series_u2p(fillToValue);
                    var xstart = xaxis.series_u2p(fillToValue);
										ctx.fillRect(xstart, ystart, xstop, ystop - ystart);
										
										if (opts.configIcon.show && (this.options.defaultConfigIcon.match(/\.(bmp|jpeg|jpg|gif|png)$/i) || opts.configIcon.image.match(/\.(bmp|jpeg|jpg|gif|png)$/i))) {
                        showConfigIcon(plot, obj, opts.configIcon, {
                          x: xstart,
                          y: ystart,
                          width: xstop,
                          height: ystop - ystart
                        }, ctx.canvas);
                    }
										
										ctx.restore();
									}
								}
								
								if (opts.showTooltip && this.isDragging && opts.dragable && opts.dragable.showTooltip && dragobj && dragobj.options.name === opts.name) {
									var neighbor = this._neighbor;
									var ypos = this.options.canvasExtension && this.options.canvasExtendDirection === 'top' ? neighbor.gridPos.y + this.options.canvasExtension : neighbor.gridPos.y;
									showTooltip(plot, obj, [neighbor.gridPos.x, ypos], neighbor.dataPos);
								}

								if (opts.dragable && opts.dragable.notifyOnDrag && $.isFunction(opts.dragable.afterRedraw) && this.isDragging && dragobj && dragobj.options.name === opts.name) {
									opts.dragable.afterRedraw.call(obj, ev, false, opts.x, plot, this);
								}
							}
							break;

						case 'rectangle':
							// style and shadow properties should be set before
							// every draw of marker renderer.
							mr.style = 'line';
							opts.closePath = true;

							var xaxis = plot.axes[opts.xaxis], xstart, xstop, y = yaxis_series_u2p(opts.y), xminoff = opts.xminOffset || opts.xOffset, xmaxoff = opts.xmaxOffset || opts.xOffset;
							// customizing start (2014-04-16, Roy Choi)
							if (typeof opts.xminBind === 'string' && this.get(opts.xminBind)) {
								xstart = xaxis.series_u2p(this.get(opts.xminBind).options.x);
							// customizing end
							} else if (opts.xmin != null) {
								xstart = xaxis.series_u2p(opts.xmin);
							} else if (xminoff != null) {
								if ($.type(xminoff) == "number") {
									xstart = xaxis.series_u2p(xaxis.min + xminoff);
								} else if ($.type(xminoff) == "string") {
									xstart = xaxis.series_u2p(xaxis.min) + parseFloat(xminoff);
								}
							}
							// customizing start (2014-04-16, Roy Choi)
							if (typeof opts.xmaxBind === 'string' && this.get(opts.xmaxBind)) {
								xstop = xaxis.series_u2p(this.get(opts.xmaxBind).options.x);
							// customizing end
							} else if (opts.xmax != null) {
								xstop = xaxis.series_u2p(opts.xmax);
							} else if (xmaxoff != null) {
								if ($.type(xmaxoff) == "number") {
									xstop = xaxis.series_u2p(xaxis.max - xmaxoff);
								} else if ($.type(xmaxoff) == "string") {
									xstop = xaxis.series_u2p(xaxis.max) - parseFloat(xmaxoff);
								}
							}

							var yaxis = plot.axes[opts.yaxis], ystart, ystop, x = xaxis_series_u2p(opts.x), yminoff = opts.yminOffset || opts.yOffset, ymaxoff = opts.ymaxOffset || opts.yOffset;
							// customizing start (2014-04-16, Roy Choi)
							if (typeof opts.yminBind === 'string' && this.get(opts.yminBind)) {
								ystart = yaxis.series_u2p(this.get(opts.yminBind).options.y);
							// customizing end
							} else if (opts.ymin != null) {
								ystart = yaxis.series_u2p(opts.ymin);
							} else if (yminoff != null) {
								if ($.type(yminoff) == "number") {
									ystart = yaxis.series_u2p(yaxis.min - yminoff);
								} else if ($.type(yminoff) == "string") {
									ystart = yaxis.series_u2p(yaxis.min) - parseFloat(yminoff);
								}
							}
							// customizing start (2014-04-16, Roy Choi)
							if (typeof opts.ymaxBind === 'string' && this.get(opts.ymaxBind)) {
								ystop = yaxis.series_u2p(this.get(opts.ymaxBind).options.y);
							// customizing end
							} else if (opts.ymax != null) {
								ystop = yaxis.series_u2p(opts.ymax);
							} else if (ymaxoff != null) {
								if ($.type(ymaxoff) == "number") {
									ystop = yaxis.series_u2p(yaxis.max + ymaxoff);
								} else if ($.type(ymaxoff) == "string") {
									ystop = yaxis.series_u2p(yaxis.max) + parseFloat(ymaxoff);
								}
							}

							if (xstop != null && xstart != null && ystop != null && ystart != null) {
								obj.gridStart = [xstart, ystart];
								obj.gridStop = [xstop, ystop];
								
								var yend;
								if (ystop - ystart < -ystart) {	// temporary bug fix
									yend = -ystart;
								} else {
									yend = ystop - ystart;
								}
								
								var ctx = opts.isBackground ? this.bcanvas._ctx : this.canvas;
								
                ctx.fillStyle = opts.color;
                ctx.fillRect(xstart, ystart, xstop - xstart, yend);
								
								if (opts.configIcon.show && (this.options.defaultConfigIcon.match(/\.(bmp|jpeg|jpg|gif|png)$/i) || opts.configIcon.image.match(/\.(bmp|jpeg|jpg|gif|png)$/i))) {
                    showConfigIcon(plot, obj, opts.configIcon, {
                      x: xstart,
                      y: ystart,
                      width: xstop - xstart,
                      height: yend
                    }, ctx.canvas);
                }
                
                ctx.restore();
                ctx = null;
							}
							break;

						default:
							break;
					}
				}
			}
			
			var coelem = this.canvas._elem.detach();
			var cobelem = this.bcanvas._elem.detach();
      if (plot.plugins.specWindow && plot.plugins.specWindow.specCanvas._elem && plot.plugins.specWindow.specCanvas._elem.width() > 0) {
        plot.plugins.specWindow.specCanvas._elem.before(cobelem);
        plot.plugins.specWindow.specCanvas._elem.before(coelem);
      } else if (plot.series.length > 0) {
        plot.series[plot.seriesStack[0]].canvas._elem.before(cobelem);
        plot.eventCanvas._elem.before(coelem);
      }

			coelem = cobelem = null;
		}
	};

	// called within context of plot
	// create a canvas which we can draw on.
	// insert it before the eventCanvas, so eventCanvas will still capture events.
	$.jqplot.CanvasWindow.postPlotDraw = function() {
		var cw = this.plugins.canvasWindow;
		// Memory Leaks patch
		if (cw && cw.highlightCanvas) {
			cw.highlightCanvas.resetCanvas();
			cw.highlightCanvas = null;
		}
		cw.canvas = new $.jqplot.GenericCanvas();
		cw.bcanvas = new $.jqplot.GenericCanvas();

		var canvasGridPadding = $.extend(true, {}, this._gridPadding);
		if (cw.options.canvasExtension) {
			canvasGridPadding[cw.options.canvasExtendDirection] -= cw.options.canvasExtension;
		}
		
		this.eventCanvas._elem.before(cw.canvas.createElement(canvasGridPadding, 'jqplot-canvaswindow-canvas', this._plotDimensions, this));
		this.eventCanvas._elem.before(cw.bcanvas.createElement(canvasGridPadding, 'jqplot-canvaswindow-canvas', this._plotDimensions, this));

		cw.canvas.setContext();
		cw.bcanvas.setContext();
		
		cw.canvas._elem.on('mousemove', {plot: this, canvas: cw.canvas}, function(ev) {
      var positions = getEventPosition(ev);
      var p = ev.data.plot;
      var evt = $.Event('jqplotMouseMove');
      evt.pageX = ev.pageX;
      evt.pageY = ev.pageY;
      evt.target = ev.data.canvas._elem;
      handleMove(evt, positions.gridPos, positions.dataPos, null, p);
		});
		cw.canvas._elem.on('mousedown', {plot: this, canvas: cw.canvas}, function(ev) {
      var positions = getEventPosition(ev);
      var plot = ev.data.plot;
      var evt = $.Event('jqplotMouseDown');
      evt.pageX = ev.pageX;
      evt.pageY = ev.pageY;
      handleDown(ev, positions.gridPos, positions.dataPos, null, plot);
		});
		cw.canvas._elem.on('mouseenter', {canvasWindow: cw}, function(ev) {
		  if (!ev.data.canvasWindow.isDragging) {
		    ev.data.canvasWindow.canvas._elem.css('cursor', 'default');
		  }
		});

		if ($.isPlainObject(cw.options.canvasExtensionEvents)) {
			var plot = this;
			$.each(cw.options.canvasExtensionEvents, function(eventKey, handler) {
				cw.canvas._elem.on(eventKey, {plot: plot, canvas: cw.canvas}, function(evt) {
					var plot = evt.data.plot;
					var cw = plot.plugins.canvasWindow;
					var position = getEventPosition(evt);
					var neighbor = checkNearLine(cw.objects, position.gridPos, position.dataPos);
					if (neighbor === null && !cw.isDragging) {
						var objName = checkFillArea(evt, plot);
						if (objName) {
							var obj = cw.get(objName);
							handler.call(this, evt, objName, obj, plot);
						} else {
							var ca = plot.plugins.customAxes;
							if (ca && $.isPlainObject(ca.axes) && Object.keys(ca.axes).length) {
								$.each(ca.axes, function(key, axis) {
							    var pEvent = $.Event(evt);
							    var target = axis._canvas._elem;
							    pEvent.target = target;
							    pEvent.type = evt.type,
							    pEvent.data = evt.data;
							    pEvent.currentTarget = target;
							    pEvent.pageX = evt.pageX;
							    pEvent.pageY = evt.pageY;
							    pEvent.result = evt.result;
							    pEvent.timeStamp = evt.timeStamp;
							    pEvent.which = evt.which;
							    pEvent.button = evt.button;
							    $(target).trigger(evt, pEvent);
									ca = null;
								});
							}
						}
					}
					cw = null;
				});
			});
		}

		if (!cw.deferDraw) {
			cw.draw(this);
		}

		var elem = document.createElement('div');
		cw._tooltipElem = $(elem);
		elem = null;
		cw._tooltipElem.addClass('jqplot-canvasWindow-tooltip');
		cw._tooltipElem.css({
			position : 'absolute',
			display : 'none'
		});

		this.eventCanvas._elem.before(cw._tooltipElem);
		this.eventCanvas._elem.bind('mouseleave', {
			elem : cw._tooltipElem
		}, function(ev) {
			ev.data.elem.hide().empty();
		});

		var cw = null;
	};
	
	function showConfigIcon(plot, obj, configIcon, offset, canvas) {
	  var cw = plot.plugins.canvasWindow;
	  
	  var elem = $('<div></div>');
	  elem.addClass('jqplot-canvaswindow-configicon').css({
	    position: 'absolute',
	    width: configIcon.width,
	    height: configIcon.height,
	    backgroundRepeat: 'no-repeat',
	    backgroundPosition: 'center center'
	  });
	  if (configIcon.style) {
	    elem.css(configIcon.style);
	  }
	  if ($.isArray(configIcon.classes.length) && configIcon.classes.length > 0) {
      configIcon.classes.forEach(function(className) {
        elem.addClass(className);
      });
    }
    
    if (configIcon.image !== null) {
      elem.css({
        backgroundImage: 'url('+configIcon.image+')'
      });
    }

	  plot.eventCanvas._elem.after(elem);
	  
	  if (Math.abs(offset.width) < elem.outerWidth(true)) {
	    elem.css({
        width: Math.max(0, Math.abs(offset.width) - (elem.outerWidth(true) - elem.width()))
      });
	  }
	  
    if (Math.abs(offset.height) < elem.outerHeight(true)) {
      elem.css({
        width: Math.max(0, Math.abs(offset.height) - (elem.outerHeight(true) - elem.height()))
      });
    }
	  
	  var top = 0, left = 0;
	  var x1, y1, x2, y2;
	  
	  var canvasOffset = $(canvas).offset();
	  
	  var plotOffset = plot.target.offset();
	  var gridPadding = plot._gridPadding;
	  
	  if (offset.width > 0) {
	    x1 = canvasOffset.left + offset.x;
	    x2 = canvasOffset.left + offset.x + offset.width;
	  } else {
      x1 = canvasOffset.left + offset.x + offset.width;
      x2 = canvasOffset.left + offset.x;
	  }
	  
	  if (offset.height > 0) {
      y1 = canvasOffset.top + offset.y;
      y2 = canvasOffset.top + offset.y + offset.height;
    } else {
      y1 = canvasOffset.top + offset.y + offset.height;
      y2 = canvasOffset.top + offset.y;
    }
    
    switch (configIcon.location) {
      case 'n':
        top = y1;
        left = x1 + offset.width / 2 - elem.outerWidth(true) / 2;
        break;
      case 's':
        top = y2 - elem.outerHeight(true);
        left = x1 + offset.width / 2 - elem.outerWidth(true) / 2;
        break;
      case 'e':
        top = y1 + offset.height / 2 - elem.outerHeight(true) / 2;
        left = x2 - elem.outerWidth(true);
        break;
      case 'se':
        top = y2 - elem.outerHeight(true);
        left = x2 - elem.outerWidth(true);
        break;
      case 'nw':
        top = y1;
        left = x1;
        break;
      case 'w':
        top = y1 + offset.height / 2 - elem.outerHeight(true) / 2;
        left = x1;
        break;
      case 'sw':
        top = y2 - elem.outerHeight(true);
        left = x1;
        break;
      case 'ne':
      default:
        top = y1;
        left = x2 - elem.outerWidth(true);
        break;
    }
    
    elem.offset({
      top: top,
      left: left
    });
    
    if (configIcon.events) {
      $.each(configIcon.events, function(eventName, eventCallback) {
        elem.bind(eventName, {obj: obj.options, plot: plot}, function(ev) {
          eventCallback(ev, ev.data.obj.name, ev.data.obj, ev.data.plot);
        });
      });
    }
    
    elem.bind();
	}

	function showTooltip(plot, obj, gridpos, datapos) {
		var cw = plot.plugins.canvasWindow;
		var elem = cw._tooltipElem;

		var opts = obj.options, x, y;

		elem.html($.isFunction(opts.tooltipFormatString) ? opts.tooltipFormatString(plot, obj, gridpos, datapos) : $.jqplot.sprintf(opts.tooltipFormatString, datapos[0], datapos[1]));

		switch (opts.tooltipLocation) {
			case 'nw':
				x = gridpos[0] + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true);
				break;
			case 'n':
				x = gridpos[0] + plot._gridPadding.left - elem.outerWidth(true) / 2;
				y = gridpos[1] + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true);
				break;
			case 'ne':
				x = gridpos[0] + plot._gridPadding.left + opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true);
				break;
			case 'e':
				x = gridpos[0] + plot._gridPadding.left + opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top - elem.outerHeight(true) / 2;
				break;
			case 'se':
				x = gridpos[0] + plot._gridPadding.left + opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top + opts.tooltipOffset;
				break;
			case 's':
				x = gridpos[0] + plot._gridPadding.left - elem.outerWidth(true) / 2;
				y = gridpos[1] + plot._gridPadding.top + opts.tooltipOffset;
				break;
			case 'sw':
				x = gridpos[0] + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top + opts.tooltipOffset;
				break;
			case 'w':
				x = gridpos[0] + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top - elem.outerHeight(true) / 2;
				break;
			default:
				// same as 'nw'
				x = gridpos[0] + plot._gridPadding.left - elem.outerWidth(true) - opts.tooltipOffset;
				y = gridpos[1] + plot._gridPadding.top - opts.tooltipOffset - elem.outerHeight(true);
				break;
		}

		elem.css('left', x);
		elem.css('top', y);
		if (opts.fadeTooltip) {
			// Fix for stacked up animations.  Thnanks Trevor!
			elem.stop(true, true).fadeIn(opts.tooltipFadeSpeed);
		} else {
			elem.show();
		}
		elem = null;
	}

	function isNearLine(point, lstart, lstop, width) {
		// r is point to test, p and q are end points.
		var rx = point[0];
		var ry = point[1];
		var px = Math.round(lstop[0]);
		var py = Math.round(lstop[1]);
		var qx = Math.round(lstart[0]);
		var qy = Math.round(lstart[1]);

		var l = Math.sqrt(Math.pow(px - qx, 2) + Math.pow(py - qy, 2));

		// scale error term by length of line.
		var eps = width * l;
		var res = Math.abs((qx - px) * (ry - py) - (qy - py) * (rx - px));
		var ret = (res < eps) ? true : false;
		return ret;
	}

	function isNearRectangle(point, lstart, lstop, width) {
		// r is point to test, p and q are end points.
		var rx = point[0];
		var ry = point[1];
		var px = Math.round(lstop[0]);
		var py = Math.round(lstop[1]);
		var qx = Math.round(lstart[0]);
		var qy = Math.round(lstart[1]);

		var temp;
		if (px > qx) {
			temp = px;
			px = qx;
			qx = temp;
		}
		if (py > qy) {
			temp = py;
			py = qy;
			qy = temp;
		}

		var ret = (rx >= px && rx <= qx && ry >= py && ry <= qy);

		return ret;
	}

	function checkNearLine(objs, gridPos, dataPos) {
		var obj;
		var len = objs.length;
		var lstart, lstop, width;
		for (var i = 0; i < len; i++) {
			obj = objs[i];
			if (obj.type === 'rectangle') {
				continue;
			}
			lstart = obj.gridStart;
			lstop = obj.gridStop;
			width = obj.tooltipWidthFactor;

			// r is point to test, p and q are end points.
			var rx = gridPos.x;
			var ry = gridPos.y;
			if (lstop && lstart) {
				var px = Math.round(lstop[0]);
				var py = Math.round(lstop[1]);
				var qx = Math.round(lstart[0]);
				var qy = Math.round(lstart[1]);
			} else {
				return null;
			}

			var l = Math.sqrt(Math.pow(px - qx, 2) + Math.pow(py - qy, 2));

			// scale error term by length of line.
			var eps = width * l;
			var res = Math.abs((qx - px) * (ry - py) - (qy - py) * (rx - px));
			if (res < eps) {
				ret = {
					lineIndex : i
				};
				return ret;
			}
		}

		return null;
	}
	
	function checkFillArea(ev, plot) {
		var obj, robj;
		var cw = plot.plugins.canvasWindow;
		var objs = cw.objects;
		var len = objs.length;
		var opts;
		var start, stop;
		var max, min;
		var axis, direction;
		var position = getEventPosition(ev);
		
		for (var i = 0; i < len; i++) {
			obj = objs[i];
			opts = obj.options;
			if (obj.type === 'rectangle' || !opts.fillArea) {
				continue;
			}
			
			switch(obj.constructor) {
				case HorizontalLine:
				case DashedHorizontalLine:
					direction = 'y';
					axis = opts['yaxis'];
					break;
				case VerticalLine:
				case DashedVerticalLine:
					direction = 'x';
					axis = opts['xaxis'];
					break;
			}
			
			start = opts[direction];

			if (opts.fillToBind) {
				robj = cw.get(opts.fillToBind);
				stop = robj.options[direction];
			} else if (opts.fillToValue) {
				stop = opts.fillToValue;
			} else if (opts.fillToMax) {
				stop = plot.axes[axis].max;
			} else if (opts.fillToMin) {
				stop = plot.axes[axis].min;
			}
			
			if ((start < stop && start <= position.dataPos[axis] && stop >= position.dataPos[axis])
				|| (start >= stop && start >= position.dataPos[axis] && stop <= position.dataPos[axis])){
				return obj.options.name;
			}
		}

		return null;
	}

	function handleMove(ev, gridpos, datapos, neighbor, plot) {
		var cw = plot.plugins.canvasWindow;
		var objs = cw.objects;
		var l = objs.length;
		var obj, haveHighlight = false;
		var elem;

		// customizing (2011-11-11, Roy Choi)
		if (cw.isDragging && cw._neighbor !== null) {
			/*var n = cw._neighbor;
			obj = objs[n.lineIndex];
			//var drag = obj.options.dragable;

			switch (obj.type) {
				case 'line':
					return;
					break;

				case 'horizontalLine':
				case 'dashedHorizontalLine':
					obj.options.y = datapos.yaxis;
					cw.draw(plot, obj);
					break;

				case 'verticalLine':
				case 'dashedVerticalLine':
					obj.options.x = datapos.xaxis;
					cw.draw(plot, obj);
					break;
				default:
					break;
			}*/
			return;
			//haveHighlight = true;
		}

		for (var i = 0; i < l; i++) {
			obj = objs[i];
			if (obj.options.showTooltip || obj.options.isDraggable) {
				var n;
				if (obj.type === 'rectangle') {
					n = isNearRectangle([gridpos.x, gridpos.y], obj.gridStart, obj.gridStop, obj.tooltipWidthFactor);
				} else {
					n = isNearLine([gridpos.x, gridpos.y], obj.gridStart, obj.gridStop, obj.tooltipWidthFactor);
				}
			}

			if(obj.options.isDraggable && !obj.options.isOver && n) {
				//obj.options._cursor.push(ev.target.style.cursor);
				switch (obj.type) {
					case 'line':
						break;
					case 'horizontalLine':
					case 'dashedHorizontalLine':
						$(ev.target).css('cursor', 'row-resize');
						break;

					case 'verticalLine':
					case 'dashedVerticalLine':
						$(ev.target).css('cursor', 'col-resize');
						break;
					default:
						break;
				} 
				obj.options.isOver = true;
			} else if (obj.options.isDraggable && obj.options.isOver && !n) {
				$(ev.target).css('cursor', 'default');
				obj.options.isOver = false;
			}
			
			if (obj.options.showTooltip) {
				datapos = [plot.axes[obj.options.xaxis].series_p2u(gridpos.x), plot.axes[obj.options.yaxis].series_p2u(gridpos.y)];
				var addYpos = 0;
				if (cw.options.canvasExtension && cw.options.canvasExtendDirection === 'top' && ev.target === cw.canvas._elem) {
					addYpos = -cw.options.canvasExtension;
				}

				// cases:
				//    near line, no highlighting
				//    near line, highliting on this line
				//    near line, highlighting another line
				//    not near any line, highlighting
				//    not near any line, no highlighting

				// near line, not currently highlighting
				if (n && cw.highlightObjectIndex == null) {
					switch (obj.type) {
						case 'line':
							showTooltip(plot, obj, [gridpos.x, gridpos.y], datapos);
							break;

						case 'horizontalLine':
						case 'dashedHorizontalLine':
							showTooltip(plot, obj, [gridpos.x, obj.gridStart[1]], [datapos[0], obj.options.y]);
							break;

						case 'verticalLine':
						case 'dashedVerticalLine':
							showTooltip(plot, obj, [obj.gridStart[0], gridpos.y + addYpos], [obj.options.x, datapos[1]]);
							break;

						case 'rectangle':
							showTooltip(plot, obj, [obj.gridStart[0], gridpos.y], [obj.options.x, datapos[1]]);
							break;

						default:
							break;
					}
					cw.highlightObjectIndex = i;
					haveHighlight = true;
					break;
				}

				// near line, highlighting another line.
				else if (n && cw.highlightObjectIndex !== i) {
					// turn off tooltip.
					elem = cw._tooltipElem;
					if (obj.fadeTooltip) {
						elem.fadeOut(obj.tooltipFadeSpeed).empty();
					} else {
						elem.hide().empty();
					}

					// turn on right tooltip.
					switch (obj.type) {
						case 'line':
							showTooltip(plot, obj, [gridpos.x, gridpos.y], datapos);
							break;

						case 'horizontalLine':
						case 'dashedHorizontalLine':
							showTooltip(plot, obj, [gridpos.x, obj.gridStart[1]], [datapos[0], obj.options.y]);
							break;

						case 'verticalLine':
						case 'dashedVerticalLine':
							showTooltip(plot, obj, [obj.gridStart[0], gridpos.y + addYpos], [obj.options.x, datapos[1]]);
							break;

						case 'rectangle':
							showTooltip(plot, obj, [obj.gridStart[0], gridpos.y], [obj.options.x, datapos[1]]);
							break;

						default:
							break;
					}

					cw.highlightObjectIndex = i;
					haveHighlight = true;
					break;
				}

				// near line, already highlighting this line, update
				else if (n) {
					switch (obj.type) {
						case 'line':
							showTooltip(plot, obj, [gridpos.x, gridpos.y], datapos);
							break;

						case 'horizontalLine':
						case 'dashedHorizontalLine':
							showTooltip(plot, obj, [gridpos.x, obj.gridStart[1]], [datapos[0], obj.options.y]);
							break;

						case 'verticalLine':
						case 'dashedVerticalLine':
							showTooltip(plot, obj, [obj.gridStart[0], gridpos.y + addYpos], [obj.options.x, datapos[1]]);
							break;

						case 'rectangle':
							showTooltip(plot, obj, [obj.gridStart[0], gridpos.y], [obj.options.x, datapos[1]]);
							break;

						default:
							break;
					}

					haveHighlight = true;
					break;
				}
			}
		}

		// check if we are highlighting and not near a line, turn it off.
		if (!haveHighlight && cw.highlightObjectIndex !== null) {
			elem = cw._tooltipElem;
			obj = cw.getObject(cw.highlightObjectIndex);
			if (obj && obj.fadeTooltip) {
				elem.fadeOut(obj.tooltipFadeSpeed).empty();
			} else {
				elem.hide().empty();
			}
			cw.highlightObjectIndex = null;
		}
	}

	// customizing (2011-11-11, Roy Choi)
	function handleDown(ev, gridpos, datapos, neighbor, plot) {
		var cw = plot.plugins.canvasWindow;
		var objs = cw.objects;

		neighbor = checkNearLine(objs, gridpos, datapos);
		if (neighbor != null) {
			ev.preventDefault();
			ev.stopImmediatePropagation();
			var obj = objs[neighbor.lineIndex];
			//obj.cursor.push($(ev.target).css('cursor'));
			var dragable = obj.options.isDraggable;
			if (dragable && !cw.isDragging) {
				cw._neighbor = neighbor;
				cw.isDragging = true;
				//cw.preZoomStatus.push(plot.plugins.cursor.show);
				cw._prevCursorShow = plot.plugins.cursor.show;
				plot.plugins.cursor.show = false;

				//if (document.onselectstart != undefined) {
				cw._oldHandlers.onselectstart = document.onselectstart;
				document.onselectstart = function() {
					return false;
				};
				//}
				if (document.ondrag != undefined) {
					cw._oldHandlers.ondrag = document.ondrag;
					document.ondrag = function() {
						return false;
					};
				}
				if (document.onmousedown != undefined) {
					cw._oldHandlers.onmousedown = document.onmousedown;
					document.onmousedown = function() {
						return false;
					};
				}

				$(document).on('mousemove', {plot: plot, canvas:cw.canvas}, handleDrag);
				$(document).on('mouseup', {plot: plot, canvas:cw.canvas}, handleUp);

				//initDragPoint(obj);
				//ev.target.style.cursor = "move";
				//plot.target.trigger('jqplotDragStart', [neighbor.lineIndex, null, gridpos, null]);

				//$(document).one('mouseup.jqplot_canvasoverlay', {plot:plot}, handleMouseUp);
			}
		} else {
			cw.isDragging = false;
		}
	}

	// customizing (2011-11-11, Roy Choi)
	function handleUp(ev) {
		ev.preventDefault();
		ev.stopImmediatePropagation();
		
    var plot = ev.data.plot;
    var cw = plot.plugins.canvasWindow;

		if (cw._neighbor !== null && cw.isDragging) {
			
			var position = getEventPosition(ev);

	    var canvas = cw.canvas;

			var obj = cw.get(cw._neighbor.lineIndex);
			var drag = obj.options.dragable;
			cw.isDragging = false;
			cw._neighbor = null;
			$('body').css('cursor', 'auto');
			$(plot.target).css('cursor', 'auto');
			$(cw.canvas._elem).css('cursor', 'auto');
			plot.eventCanvas._elem.css('cursor', 'auto');
			plot.plugins.cursor.show = cw._prevCursorShow;
      
      if (cw.options.snapToGrid && $.isNumeric(cw.options.snapToGridInterval)) {
        switch(obj.constructor) {
          case HorizontalLine:
          case DashedHorizontalLine:
            obj.options.y = Math.round(obj.options.y / cw.options.snapToGridInterval) * cw.options.snapToGridInterval;
            cw.draw(plot);
            break;
          case VerticalLine:
          case DashedVerticalLine:
            obj.options.x = Math.round(obj.options.x / cw.options.snapToGridInterval) * cw.options.snapToGridInterval;
            cw.draw(plot);
            break;
        }
      }

			if ($.isFunction(drag.afterRedraw)) {
				switch(obj.constructor) {
					case HorizontalLine:
					case DashedHorizontalLine:
						drag.afterRedraw.call(obj, ev, true, obj.options.y, plot, cw);
						break;
					case VerticalLine:
					case DashedVerticalLine:
						drag.afterRedraw.call(obj, ev, true, obj.options.x, plot, cw);
						break;
				}

			}

			//if (document.onselectstart != undefined && c._oldHandlers.onselectstart != null){
			document.onselectstart = cw._oldHandlers.onselectstart;
			cw._oldHandlers.onselectstart = null;
			//}
			if (document.ondrag != undefined && cw._oldHandlers.ondrag != null) {
				document.ondrag = cw._oldHandlers.ondrag;
				cw._oldHandlers.ondrag = null;
			}
			if (document.onmousedown != undefined && cw._oldHandlers.onmousedown != null) {
				document.onmousedown = cw._oldHandlers.onmousedown;
				cw._oldHandlers.onmousedown = null;
			}
			
			$(document).off('mousemove', handleDrag);
			$(document).off('mouseup', handleUp);

			//plot.target.trigger('jqplotDragStop', [gridpos, datapos]);
		}
	}
	
	function handleDrag(ev) {
		var plot = ev.data.plot;
		var cw = plot.plugins.canvasWindow;
		
		if (cw.isDragging && cw._neighbor !== null) {
	    var canvas = cw.canvas;
	    var position = getEventPosition(ev);
	    var gridPos = position.gridPos;
	    var dataPos = position.dataPos;
	    var an = ['xaxis', 'yaxis', 'x2axis', 'y2axis', 'y3axis', 'y4axis', 'y5axis', 'y6axis', 'y7axis', 'y8axis', 'y9axis', 'yMidAxis'];
	    var ax = plot.axes;
	    var n, axis;
	    for (n=11; n>0; n--) {
	        axis = an[n-1];
	        if (ax[axis].show) {
	            dataPos[axis] = ax[axis].series_p2u(gridPos[axis.charAt(0)]);
	        }
	    }
	
			var n = cw._neighbor;
			n.gridPos = gridPos;
			n.dataPos = dataPos;
			obj = cw.get(n.lineIndex);
			//var drag = obj.options.dragable;
	
			switch (obj.type) {
				case 'line':
					return;
					break;
	
				case 'horizontalLine':
				case 'dashedHorizontalLine':
					if (dataPos[obj.options.yaxis] <= plot.axes[obj.options.yaxis].max && dataPos[obj.options.yaxis] >= plot.axes[obj.options.yaxis].min) {
						obj.options.y = dataPos[obj.options.yaxis];
						cw.draw(plot, obj, ev);
					}
					break;
	
				case 'verticalLine':
				case 'dashedVerticalLine':
					if (dataPos[obj.options.xaxis] <= plot.axes[obj.options.xaxis].max && dataPos[obj.options.xaxis] >= plot.axes[obj.options.xaxis].min) {
						obj.options.x = dataPos[obj.options.xaxis];
						cw.draw(plot, obj, ev);
					}
					break;
				default:
					break;
			}
		}
	}

  function getEventPosition(ev) {
      var plot = ev.data.plot;
      var canvas = ev.data.canvas;
      var go = canvas._elem.offset();
      var gridPos = {x:ev.pageX - go.left, y:ev.pageY - go.top};
      var dataPos = {xaxis:null, yaxis:null, x2axis:null, y2axis:null, y3axis:null, y4axis:null, y5axis:null, y6axis:null, y7axis:null, y8axis:null, y9axis:null, yMidAxis:null};
      var an = ['xaxis', 'yaxis', 'x2axis', 'y2axis', 'y3axis', 'y4axis', 'y5axis', 'y6axis', 'y7axis', 'y8axis', 'y9axis', 'yMidAxis'];
      var ax = plot.axes;
      var n, axis;
      for (n=11; n>0; n--) {
          axis = an[n-1];
          if (ax[axis].show) {
              dataPos[axis] = ax[axis].series_p2u(gridPos[axis.charAt(0)]);
          }
      }

      return {offsets:go, gridPos:gridPos, dataPos:dataPos};
  }
  
  function handleLeave(ev) {
      var plot = ev.data.plot;
      var cw = plot.plugins.canvasWindow;
      if (cw._tooltipElem) {
          cw._tooltipElem.hide().empty();
      }
  }



	$.jqplot.postInitHooks.push($.jqplot.CanvasWindow.postPlotInit);
	$.jqplot.postDrawHooks.push($.jqplot.CanvasWindow.postPlotDraw);
	$.jqplot.eventListenerHooks.push(['jqplotMouseMove', handleMove]);
	$.jqplot.eventListenerHooks.push(['jqplotMouseDown', handleDown]);
    //$.jqplot.eventListenerHooks.push(['jqplotMouseUp', handleUp]);

})(jQuery);
;/**
 jqPlot Spec Window Plugin
 2014-10-27, Roy Choi
 */
(function($) {
	/**
	 * Class: $.jqplot.SpecWindow
	 * Plugin which will show by spec window.
	 */
	$.jqplot.SpecWindow = function(options) {
		// Group: Properties
		//
		//prop: enable
		// true to enable the Show By Group.
		this.enable = $.jqplot.config.enablePlugins;
		// prop: showByGroup
		// true to show by group plot
		this.show = false;
		this.shadow = false;
        this.showMarker = false;
        this.showLine = true;
        this.highlight = false;
        this.showLabel = false;
        
        this.fitStep = false;
        this.stepInfo = null;
        this.stepColumn = null;
        this.stepSequenceColumn = null;
        
        this.useSeriesData = false;
        this.baseSeries = 0;

		this.xaxis = 'xaxis';
		this.yaxis = 'yaxis';
		this.xaxisColumn = 0;
		this.data = [];

		this.specLimit = {
		    show: true,
			lowerLimit: null,
			upperLimit: null,
			color: '#ffff00', 
			fillColor: 'rgba(255, 255, 0, .9)'
		};
		
		this.controlLimit = {
		    show: true,
            lowerLimit: null,
            upperLimit: null,
			color: '#00ff00', 
			fillColor: 'rgba(0, 255, 0, .9)'
		};
		
		$.extend(true, this, options);
		
		this.defaultSeriesOptions = {
			show: this.show,
			lowerLimit: [],
			upperLimit: [],
			shadow: this.shadow,
			showMarker: this.showMarker,
			showLine: this.showLine,
			highlight: this.highlight,
			showLabel: this.showLabel,
			hasNullPoint: false
		};
		
        this._data = [];
		
		this.specs = [];
		
		this.stepIndex = {};
		
		this.dataBounds = {
            xaxis: {
                min: null,
                max: null
            },
            yaxis: {
                min: null,
                max: null
            }
        };
	};
	
	function SpecSeries(options, data) {
	    this.show = false;
	    this.shadow = false;
	    this.highlight = false;
	    this.color = null;
	    
	    $.extend(true, this, options);
	    
	    this.data = data;
	    
	    this.gridData = [];
	}
	
	function SpecWindow(options) {
	    
        this.show = false;
        this.shadow = false;
        this.showMarker = false;
        this.showLine = false;
        this.highlight = false;
        this.color = null;
        this.fill = false;
        this.fillColor = null;
        this.hasNullPoint = false;
	    
	    $.extend(true, this, options);
	    
	    var sopts = {
            show: this.show,
            shadow: this.shadow,
            showMarker: this.showMarker,
            showLine: this.showLine,
            highlight: this.highlight,
            color: this.color
        };
	    
        this.lowerLimit = new SpecSeries(sopts, options.lowerLimit);
        this.upperLimit = new SpecSeries(sopts, options.upperLimit);
	}


	// axis.renderer.tickrenderer.formatter

	// called with scope of plot
	$.jqplot.SpecWindow.init = function(target, data, opts) {
		var options = opts || {};

		// add a highlighter attribute to the plot
		this.plugins.specWindow = new $.jqplot.SpecWindow(options.specWindow);
		
		if (this.plugins.specWindow.fitStep) {
		    this.plugins.specWindow.createStepIndex();
		}

		this.plugins.specWindow.specCanvas = new $.jqplot.GenericCanvas();
	};

	$.jqplot.SpecWindow.prePlotDraw = function(options) {
        var sw = this.plugins.specWindow;
        
        if (!sw.show || this.data.length === 0) {
            return;
        }
        
        var data;
        var x, lowerLimit, upperLimit;
        var ymin, ymax;
        var step, dataSequnece;
        var stepSequence;
        
        if (sw.useSeriesData) {
            data = this.data;
        } else {
            data = sw._data;
        }
        
        sw.dataBounds = {
            xaxis: {
                min: null,
                max: null
            },
            yaxis: {
                min: null,
                max: null
            }
        };
        
        if (sw.fitStep) {
            if ($.isArray(sw.stepInfo) && sw.stepInfo.length > 0 && $.isNumeric(sw.stepSequenceColumn) && $.isNumeric(sw.stepColumn)) {
                for (var i=0; i<data.length; i++) {
                    var spec = {
                        specLimit: null,
                        controlLimit: null
                    };
                    
                    var seriesOptions = $.extend(true, {}, sw.defaultSeriesOptions);
                    
                    if (sw.specLimit.show) {
                        var specLimitSeriesOptions = $.extend(true, {}, seriesOptions, {
                            show: true,
                            color: sw.specLimit.color,
                            fillColor: sw.specLimit.fillColor
                        });
                    }
                    
                    if (sw.controlLimit.show) {
                        var controlLimitSeriesOptions = $.extend(true, {}, seriesOptions, {
                            show: true,
                            color: sw.controlLimit.color,
                            fillColor: sw.controlLimit.fillColor
                        });
                    }
                    
                    data[i].forEach(function(pointData, index) {
                        step = pointData[sw.stepColumn];
                        stepSequence = pointData[sw.stepSequenceColumn];
                        
                        x = sw.getDataSequence(step, stepSequence);
                        
                        if (x === null || isNaN(x)) return;
                        
                        if (sw.specLimit.show) {
                            upperLimit = pointData[sw.specLimit.upperLimit];
                            lowerLimit = pointData[sw.specLimit.lowerLimit];
                            
                            sw.pack(specLimitSeriesOptions, lowerLimit, upperLimit, x);
                        }
                        if (sw.controlLimit.show) {
                            upperLimit = pointData[sw.controlLimit.upperLimit];
                            lowerLimit = pointData[sw.controlLimit.lowerLimit];
                            
                            sw.pack(controlLimitSeriesOptions, lowerLimit, upperLimit, x);
                        }
                    });
                    
                    currentStep = null;
                    
                    spec.specLimit = new SpecWindow(specLimitSeriesOptions);
                    spec.controlLimit = new SpecWindow(controlLimitSeriesOptions);
                    sw.specs.push(spec);
                }
            } else {
                console.warn('fitStep is true but some settings are not set');
            }
        } else {
            for (var i=0; i<data.length; i++) {
                var spec = {
                    specLimit: null,
                    controlLimit: null
                };
                
                var seriesOptions = $.extend(true, {}, sw.defaultSeriesOptions);
                
                if (sw.specLimit.show) {
                    var specLimitSeriesOptions = $.extend(true, {}, seriesOptions, {
                        show: true,
                        color: sw.specLimit.color,
                        fillColor: sw.specLimit.fillColor
                    });
                }
                
                if (sw.controlLimit.show) {
                    var controlLimitSeriesOptions = $.extend(true, {}, seriesOptions, {
                        show: true,
                        color: sw.controlLimit.color,
                        fillColor: sw.controlLimit.fillColor
                    });
                }
                
                data[i].forEach(function(pointData, index) {
                    x = pointData[sw.xaxisColumn];
                    if (x === null || isNaN(x)) return;
                    
                    /*if (sw.dataBounds.xaxis.min === null || sw.dataBounds.xaxis.min > x) {
                        sw.dataBounds.xaxis.min = x;
                    }
                    if (sw.dataBounds.xaxis.max === null || sw.dataBounds.xaxis.max < x) {
                        sw.dataBounds.xaxis.max = x;
                    }*/
                    
                    if (sw.specLimit.show) {
                        upperLimit = pointData[sw.specLimit.upperLimit];
                        lowerLimit = pointData[sw.specLimit.lowerLimit];
                        
                        sw.pack(specLimitSeriesOptions, lowerLimit, upperLimit, x);
                    }
                    if (sw.controlLimit.show) {
                        upperLimit = pointData[sw.controlLimit.upperLimit];
                        lowerLimit = pointData[sw.controlLimit.lowerLimit];
                        
                        sw.pack(controlLimitSeriesOptions, lowerLimit, upperLimit, x);
                    }
                });
                
                spec.specLimit = new SpecWindow(specLimitSeriesOptions);
                spec.controlLimit = new SpecWindow(controlLimitSeriesOptions);
                sw.specs.push(spec);
            }
        }
        

        
        sw.resetAxes(this);
	};
	
	$.jqplot.SpecWindow.postPlotDraw = function() {
		var sw = this.plugins.specWindow;
		
		if (!sw.show || this.series.length === 0) {
			return;
		}
		
		if (sw && sw.specCanvas) {
            sw.specCanvas.resetCanvas();
            sw.specCanvas = null;
        }
        
        sw.specCanvas = new $.jqplot.GenericCanvas();
        
        var canvasGridPadding = $.extend(true, {}, this._gridPadding);
        this.series[this.seriesStack[0]].canvas._elem.before(sw.specCanvas.createElement(canvasGridPadding, 'jqplot-spec-canvas', this._plotDimensions, this));
        sw.specCanvas.setContext();
		
		sw.draw(this);
	};
	
	$.jqplot.SpecWindow.preParseOptions = function(options) {
	  var sw = this.plugins.specWindow;
	  if (sw.show && options.specWindow && $.isArray(options.specWindow.data)) {
	    sw._data = options.specWindow.data;
	  }
	};
	
  $.jqplot.SpecWindow.preReinitOptions = function(target, data, options) {
    if (this.options.specWindow && options && options.specWindow && $.isArray(options.specWindow.data)) {
      this.options.specWindow.data = [];
    }
  };

	$.jqplot.preInitHooks.push($.jqplot.SpecWindow.init);
	$.jqplot.preParseOptionsHooks.push($.jqplot.SpecWindow.preParseOptions);
    $.jqplot.preDrawHooks.push($.jqplot.SpecWindow.prePlotDraw);
	$.jqplot.postDrawHooks.push($.jqplot.SpecWindow.postPlotDraw);
	$.jqplot.preReinitHooks.push($.jqplot.SpecWindow.preReinitOptions);
	
	function getGridData(data, xU2p, yU2p) {
	    var gd = [];
        data.forEach(function(pointData) {
            if (pointData[1] === null || isNaN(pointData[1])) {
                gd.push([xU2p(pointData[0]), null]);
            } else {
                gd.push([xU2p(pointData[0]), yU2p(pointData[1])]);
            }
        });
        
        return gd;
	}
	
	$.jqplot.SpecWindow.prototype.createStepIndex = function() {
        this.stepInfo.forEach(function(stepObj) {
            this.stepIndex[stepObj.step] = stepObj;
        }, this);
    };
	
    $.jqplot.SpecWindow.prototype.getDataSequence = function(stepName, stepSequence) {
        var step = this.stepIndex[stepName];
        var sequnce = null;
        
        if (step && $.isNumeric(stepSequence) && stepSequence < step.maxCount) {
            sequnce = step.startCountSlot + stepSequence;
        }
        
        return sequnce;
    };
	
	$.jqplot.SpecWindow.prototype.pack = function(seriesOptions, lowerLimit, upperLimit, x) {
        var ymin, ymax;
        seriesOptions.upperLimit.push([x, upperLimit]);
        seriesOptions.lowerLimit.push([x, lowerLimit]);
     
        if (upperLimit !== null && !isNaN(upperLimit) && lowerLimit !== null && !isNaN(lowerLimit)) {
            ymin = Math.min(lowerLimit, upperLimit);
            if (this.dataBounds.yaxis.min === null || this.dataBounds.yaxis.min > ymin) {
                this.dataBounds.yaxis.min = ymin;
            }
            ymax = Math.max(lowerLimit, upperLimit);
            if (this.dataBounds.yaxis.max === null || this.dataBounds.yaxis.max < ymax) {
                this.dataBounds.yaxis.max = ymax;
            }
        } else {
            seriesOptions.hasNullPoint = true;
        }
	};
	
    $.jqplot.SpecWindow.prototype.resetAxes = function(plot) {
        if (!this.show) {
            return;
        }
        
        var xaxis, yaxis;
        
        var axes = plot.axes;
        
        xaxis = axes[this.xaxis];
        yaxis = axes[this.yaxis];
        
        /*if (xaxis._dataBounds.min > this.dataBounds.xaxis.min) {
            xaxis._dataBounds.min = this.dataBounds.xaxis.min;
        }
        if (xaxis._dataBounds.max < this.dataBounds.xaxis.max) {
            xaxis._dataBounds.max = this.dataBounds.xaxis.max;
        }*/
        if (yaxis._dataBounds.min !== null && this.dataBounds.yaxis.min !== null && yaxis._dataBounds.min > this.dataBounds.yaxis.min) {
            yaxis._dataBounds.min = this.dataBounds.yaxis.min;
        }
        if (yaxis._dataBounds.max !== null && this.dataBounds.yaxis.max !== null && yaxis._dataBounds.max < this.dataBounds.yaxis.max) {
            yaxis._dataBounds.max = this.dataBounds.yaxis.max;
        }
    };
    
    $.jqplot.SpecWindow.prototype.draw = function(plot) {
        var specs = this.specs;
        
        if (!this.show || plot.series.length === 0) {
            return;
        }
        
        var axes = plot.axes;
        
        xaxis = axes[this.xaxis];
        yaxis = axes[this.yaxis];
        
        var ctx = this.specCanvas._ctx;
        ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);

        var xU2p = xaxis.series_u2p, yU2p = yaxis.series_u2p;
        specs.forEach(function(spec) {
            if (spec.specLimit.show) {
                spec.specLimit.lowerLimit.gridData = getGridData(spec.specLimit.lowerLimit.data, xU2p, yU2p);
                spec.specLimit.upperLimit.gridData = getGridData(spec.specLimit.upperLimit.data, xU2p, yU2p);
                this.doFillBetweenLines.call(plot, spec.specLimit, this.baseSeries);
            }
            
            if (spec.specLimit.show) {
                spec.controlLimit.lowerLimit.gridData = getGridData(spec.controlLimit.lowerLimit.data, xU2p, yU2p);
                spec.controlLimit.upperLimit.gridData = getGridData(spec.controlLimit.upperLimit.data, xU2p, yU2p);
                this.doFillBetweenLines.call(plot, spec.controlLimit, this.baseSeries);
            }
        }, this);
    };
  
    $.jqplot.SpecWindow.prototype.doFillBetweenLines = function (specLimits, baseSeries) {
        var sw = this.plugins.specWindow;
        var series1 = specLimits.lowerLimit;
        var series2 = specLimits.upperLimit;
        var gd = [], tempgd = [];
        var sr = this.series[baseSeries].renderer.shapeRenderer;
        var opts = {fillStyle: specLimits.color, fill: true, closePath: true};
        
        if (specLimits.hasNullPoint) {
        	series1.gridData.forEach(function(s1, index) {
        		var s2 = series2.gridData[index];
        		
        		if (s1[0] === null || isNaN(s1[0]) || s1[1] === null || isNaN(s1[1]) || s2[1] === null || isNaN(s2[1])) {
        			if (gd.length) {
        				sr.draw(sw.specCanvas._ctx, gd.concat(tempgd.reverse()), opts);
        				gd = [];
        				tempgd = [];
        			}
        		} else {
        			gd.push(s1);
        			tempgd.push(s2);
        		}
        	});
        	
			if (gd.length) {
				sr.draw(sw.specCanvas._ctx, gd.concat(tempgd.reverse()), opts);
			}
        } else {
        	tempgd = series2.gridData.slice(0).reverse();
        	gd = series1.gridData.concat(tempgd);
        	
            sr.draw(sw.specCanvas._ctx, gd, opts);
        }
    };

})(jQuery); 