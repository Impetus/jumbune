'use strict';

var directiveModule = angular.module('directives');

directiveModule.directive('ngDropdownMultiselect', ['$filter', '$http', '$rootScope', '$document', '$compile', '$parse', 'common', 'ClusterResultFactoryNew', function($filter, $http, $rootScope, $document, $compile, $parse, common, ClusterResultFactoryNew) {

		return {
			restrict: 'AE',
			scope: {
				selectedModel       : '=',
				options             : '=',
				extraSettings       : '=',
				openedData          : '=',
				events              : '=',
				searchFilter        : '=?',
				translationTexts    : '=',
				readWriteServiceData: '=',
				groupBy             : '@',
				listype             : '@',
				valtype             : '@',
				domId               : '@',
				nodeServiceData     : '=',
				customFunctions     : '='
			},
			template: function(element, attrs) {
				var checkboxes = attrs.checkboxes ? true : false;
				var groups     = attrs.groupBy ? true : false;
				var type       = attrs.listype;
				var template   = '<div class="multiselect-parent btn-group dropdown-multiselect">';
				//template += '<ul ><li ng-repeat="(key, mainoption)  in options | filter: searchFilter">{{key}}<ul><li ng-repeat="(subkey, suboption) in mainoption | filter: searchFilter">{{subkey}}<ul><li ng-repeat="row in suboption"></li></ul></li></ul></li></ul>';
				//template += '<li ng-show="settings.enableSearch"><div class="dropdown-header"><input type="text" class="form-control" style="width: 100%;" ng-model="searchFilter" placeholder="{{texts.searchPlaceholder}}" /></li>';
				template += '<span ng-click="toggleDropdown()" class="filter-categories-span">{{getButtonText()}}</span>';
				if (type == "categories") {
					template += '<ul id="clusterMonitoringStats" class="dropdown-menu dropdown-menu-form pull-right" ng-style="{display: open ? \'block\' : \'none\', height : settings.scrollable ? settings.scrollableHeight : \'auto\', minWidth : settings.scrollable ? settings.scrollableWidth : \'auto\' }" style="overflow: auto" >' +
						'<li ng-show="settings.enableSearch"><div class="dropdown-header"><input type="text" class="form-control" style="width: 100%;" ng-model="searchFilter" placeholder="{{texts.searchPlaceholder}}" /></li>' +
						'<li ng-repeat="(key, mainoption)  in options | filter: searchFilter"><span class="list-group-item" ng-click="toggleHeader($event,key)" ><i id="i_{{key}}_{{domId}}_{{nodeKey1}}" class="fa fa-caret-down fa-caret-right" style="padding-right: 5px;"></i>{{key}}</span>' +
						'               <ul style="display:none" id="ul_{{key}}_{{domId}}_{{nodeKey1}}">' +
						'                   <li ng-repeat="(subkey, suboption) in mainoption | filter: searchFilter"   ><span class="list-group-item " ng-click="toggleHeader($event,subkey)"><i id="i_{{subkey}}_{{domId}}_{{nodeKey1}}" class="fa fa-caret-down fa-caret-right" style="padding-right: 5px;"></i>{{subkey}}</span>';

					template += '<ul id="ul_{{subkey}}_{{domId}}_{{nodeKey1}}" style="display:none">';

					//template += '<li>';
					if (groups) {
						template += '<li ng-repeat-start="option in orderedItems | filter: searchFilter" ng-show="getPropertyForObject(option, settings.groupBy) !== getPropertyForObject(orderedItems[$index - 1], settings.groupBy)" role="presentation" class="dropdown-header">{{ getGroupTitle(getPropertyForObject(option, settings.groupBy)) }}</li>';
						template += '<li ng-repeat-end role="presentation">';
					} else {
						template += '<li role="presentation" ng-repeat="option in suboption | filter: searchFilter" class="list-group-item node-treeview5" >';
					}


					//template += '<a role="menuitem" tabindex="-1" ng-click="setSelectedItem(getPropertyForObject(option,settings.idProp))">';

					if (checkboxes && (attrs.valtype == 'Allnode')) {
						template += '<div class="checkbox">' + '<input ' + 'class="checkboxInput" ' + 'type="checkbox" ' + 'ng-click="checkboxClick($event, getPropertyForObject(option,settings.idProp),key,subkey,false,clusterWide)"' + 'id = clusterWide.{{key}}.{{subkey}}.{{getPropertyForObject(option,settings.idProp)}} ' + 'ng-disabled="countFlag && (ifDisable(getPropertyForObject(option,settings.idProp),key,subkey))" ' + 'ng-checked="isChecked(getPropertyForObject(option,settings.idProp),key,subkey)" /> ' + '<label style="color:#333;cursor: auto;">' + '{{getPropertyForObject(option, settings.idProp)}} ' + '</label>' + '</div></a>';
					} else if (checkboxes && (attrs.valtype == 'node')) {
						template += '<div class="checkbox">' + '<input ' + 'class="checkboxInput" ' + 'type="checkbox" ' + 'ng-click="checkboxClick($event, getPropertyForObject(option,settings.idProp),key,subkey,false,tabId)"' + 'id = {{tabId}}.{{key}}.{{subkey}}.{{getPropertyForObject(option,settings.idProp)}} ' + 'ng-disabled="countFlag && (ifDisable(getPropertyForObject(option,settings.idProp),key,subkey))" ' + 'ng-checked="isCheckedNode(getPropertyForObject(option,settings.idProp),key,subkey)" /> ' + '<label style="color:#333;cursor: auto;">' + '{{getPropertyForObject(option, settings.idProp)}} ' + '</label>' + '</div></a>';
					} else {
						template += '<span data-ng-class="{\'glyphicon glyphicon-ok\': isChecked(getPropertyForObject(option,settings.idProp))}"></span> {{getPropertyForObject(option, settings.displayProp)}}</a>';
					}

					//template += '</li>';


					template += '<li class="divider" ng-show="settings.selectionLimit > 1"></li>';
					template += '<li role="presentation" ng-show="settings.selectionLimit > 1"><a role="menuitem">{{selectedModel.length}} {{texts.selectionOf}} {{settings.selectionLimit}} {{texts.selectionCount}}</a></li>';

					template += '</ul>';

					template += '</li></ul>';
					template += '</li></ul>';
				} else if (type == "nodes") {
					template += '<ul class="dropdown-menu dropdown-menu-form pull-right" ng-style="{display: open ? \'block\' : \'none\', height : settings.scrollable ? settings.scrollableHeight : \'auto\', minWidth : settings.scrollable ? settings.scrollableWidth : \'auto\' }" style="overflow: auto"  >' +
						'<li ng-show="settings.enableSearch"><div class="dropdown-header"><input type="text" class="form-control" style="width: 100%; margin-bottom:5px;" ng-model="searchFilter" placeholder="{{texts.searchPlaceholder}}" /></li>'





					//template += '<li>';
					if (groups) {
						template += '<li ng-repeat-start="option in orderedItems | filter: searchFilter" ng-show="getPropertyForObject(option, settings.groupBy) !== getPropertyForObject(orderedItems[$index - 1], settings.groupBy)" role="presentation" class="dropdown-header">{{ getGroupTitle(getPropertyForObject(option, settings.groupBy)) }}</li>';
						template += '<li ng-repeat-end role="presentation">';
					} else {
						template += '<li role="presentation" ng-repeat="option in options | filter: searchFilter" class="list-group-item node-treeview5" >';
					}

					template += '<a role="menuitem" tabindex="-1" ng-click="setSelectedItem(getPropertyForObject(option,settings.idProp))">';

					if (checkboxes) {
						template += '<div class="checkbox"><input class="checkboxInput" type="checkbox" ng-click="checkboxClick($event, getPropertyForObject(option,settings.idProp))" ng-checked="isChecked(getPropertyForObject(option,settings.idProp))" checked /> <label>{{getPropertyForObject(option, settings.displayProp)}} </label></div></a>';
					} else {
						template += '<span data-ng-class="{\'glyphicon glyphicon-ok\': isChecked(getPropertyForObject(option,settings.idProp))}"></span> {{getPropertyForObject(option, settings.displayProp)}}</a>';
					}

					template += '<li class="divider" ng-show="settings.selectionLimit > 1"></li>';
					template += '<li role="presentation" ng-show="settings.selectionLimit > 1"><a role="menuitem">{{selectedModel.length}} {{texts.selectionOf}} {{settings.selectionLimit}} {{texts.selectionCount}}</a></li>';



					template += '</li></ul>';
				}


				template += '</div>';

				element.html(template);
			},
			link: function($scope, $element, $attrs) {
				var type         = $attrs.listype;
				var id2          = $attrs.tabNodeId;
				var nodeKey      = '';
				$scope.options = $scope.options[$attrs.tabNodeId];
				$scope.tabId = $attrs.tabNodeId;
				$scope.countVar  = 0;
				$scope.countFlag = false;
				$scope.nodeKey1  = '';
				if ($attrs.tabNodeId) {
					var tabWithoutDot = $attrs.tabNodeId.split('.').join('_');
					$scope.customFunctions[tabWithoutDot] = {};

					$scope.customFunctions[tabWithoutDot]['addStat'] = function(id, key, subkey) {
						if ($attrs.tabNodeId == 'All Nodes') {
							var idclusterWide = document.getElementById("clusterWide" + "." + key + "." + subkey + "." + id);
							var checkedUnchekedAllNode = $(idclusterWide).prop('checked');
							if (checkedUnchekedAllNode == false) {								
								$(idclusterWide).prop('checked',true);
							} else {
								return;
							}
							$scope.setSelectedItem(id, key, subkey);
							if (key && subkey) {
								$scope.setReadWriteData(id, key, subkey);
							}
							return $scope.selectedModel.indexOf(id) === -1;
						} else {
							var idnodes = document.getElementById($attrs.tabNodeId + "." + key + "." + subkey + "." + id);
							var checkedUnchekedNode = $(idnodes).prop('checked');
								if (checkedUnchekedNode == false) {									
									$(idnodes).prop('checked',true);
								} else {
									return;
								}
								$scope.setSelectedItem(id, key, subkey);
								if (key && subkey) {
									$scope.setReadWriteNodeData(id, key, subkey)
								}								
								return $scope.selectedModel.indexOf(id) === -1;
						}
					};
					$scope.customFunctions[tabWithoutDot]['removeStat'] = function(id, key, subkey) {
						if ($attrs.tabNodeId == 'All Nodes') {
							var idclusterWide = document.getElementById("clusterWide" + "." + key + "." + subkey + "." + id);
							var checkedUnchekedAllNode = $(idclusterWide).prop('checked');
							if (checkedUnchekedAllNode == true) {
								$(idclusterWide).prop('checked',false);
							} else {
								return;
							}
							$scope.setSelectedItem(id, key, subkey);
							if (key && subkey) {
								$scope.setReadWriteData(id, key, subkey);
							}
							return $scope.selectedModel.indexOf(id) === -1;
						} else {
							var idnodes = document.getElementById($attrs.tabNodeId + "." + key + "." + subkey + "." + id);
							var checkedUnchekedNode = $(idnodes).prop('checked');
								if (checkedUnchekedNode == true) {
									$(idnodes).prop('checked',false);
								} else {
									return;
								}
								$scope.setSelectedItem(id, key, subkey);
								if (key && subkey) {
									$scope.setReadWriteNodeData(id, key, subkey)
								}								
								return $scope.selectedModel.indexOf(id) === -1;
						}
					};
				}
				if (id2 != 'All Nodes') {
					if (id2 != undefined) {
						id2.split('\.').forEach(function(index) { $scope.nodeKey1 += index });
					}

				} else {                    
					$scope.nodeKey1 = id2.replace(/\s/g, "")
				}

				var $dropdownTrigger = $element.children()[0];

				$scope.toggleDropdown = function() {
					$scope.open = !$scope.open;
				};
								   
				$scope.$on('scanner-started', function(event, args) {
					if (($attrs.listype == "categories") && ($scope.domId == "category_for_clusterwide") &&
						($attrs.tabNodeId == 'All Nodes')) { 
						var key = args.split(".")
						var finalId = ""
						if (key.length > 3) {
							for (var i = 2; i < key.length; i++) {
								finalId += key[i] + ".";
							}
							finalId = finalId.substr(0, finalId.length - 1)
						} else {
							finalId = key[2]
						}


						$scope.checkboxClick(false, finalId, key[0], key[1], true, "clusterWide")

					}
				})

				$scope.$on('scanner-single', function(event, args, val) {
					if (($attrs.listype == "categories") && ($scope.domId == "category_for_single_node") &&
						($attrs.tabNodeId == val)) {
						var key = args.split(".")
						var finalId = ""
						if (key.length > 3) {
							for (var i = 2; i < key.length; i++) {
								finalId += key[i] + ".";
							}
							finalId = finalId.substr(0, finalId.length - 1)
						} else {
							finalId = key[2]
						}

						$scope.checkboxClick(false, finalId, key[0], key[1], true, $attrs.tabNodeId)

					}
				})

				$scope.checkboxClick = function($event, id, key, subkey, calledOnClose, idVal) { 
					var addCategory = key + "." + subkey + "." + id;  
					if (calledOnClose) {
						var cpu_usage = document.getElementById(idVal + "." + key + "." + subkey + "." + id);            
						if ($attrs.valtype == "Allnode") {
							cpu_usage.checked = false;              
							$scope.setSelectedItem(id, key, subkey);              
							$scope.setReadWriteData(id, key, subkey);              
							$rootScope.$broadcast('add-remove-chart', key, subkey, id, "remove");
						} else {
							cpu_usage.checked = false;              
							$scope.setSelectedItem(id, key, subkey);              
							$scope.setReadWriteNodeData(id, key, subkey);              
							$rootScope.$broadcast('add-remove-chart', key, subkey, id, "remove");             }
					} else {
						$scope.setSelectedItem(id, key, subkey);
						if (key && subkey) {
							if ($attrs.valtype == "Allnode") {
								$scope.setReadWriteData(id, key, subkey);
							} else {
								$scope.setReadWriteNodeData(id, key, subkey);
							}

							if ($event.target.checked == true) {
								$rootScope.$broadcast('add-remove-chart', key, subkey, id, "add");
							} else {
								$rootScope.$broadcast('add-remove-chart', key, subkey, id, "remove");
							}
						}
						$event.stopImmediatePropagation();
					}
				};


				/* $scope.checkboxClick = function ($event, id,key,subkey) {
					$scope.setSelectedItem(id, key, subkey);
					if(key && subkey) {
						$scope.setReadWriteData(id, key, subkey);
						$scope.setReadWriteNodeData(id, key, subkey)
					}
					$event.stopImmediatePropagation();
				};*/

				$scope.ifDisable = function(id, key, subkey) {
					$scope.countVar  = common.getCountVar();
					$scope.countFlag = common.getCountFlag();
					var idWriteData  = key + "." + subkey + "." + id;
					if ($scope.selectedModel.indexOf(idWriteData) > -1) {
						return false;
					} else {
						return true;
					}
				}
				$scope.setReadWriteData = function(id, key, subkey) {
					var idWriteData     = key + "." + subkey + "." + id;
					var nodeKey         = $attrs.tabNodeId;
					if (nodeKey == 'All Nodes') {
						var findObj = (idWriteData);

						var exists = $scope.selectedModel.indexOf(findObj) !== -1;

						if (exists) {
							if (!$scope.readWriteServiceData[key])
								$scope.readWriteServiceData[key] = {};
							if (!$scope.readWriteServiceData[key][subkey])
								$scope.readWriteServiceData[key][subkey] = [];

							$scope.readWriteServiceData[key][subkey].push(id);

						} else   {
							var indexSOData = $scope.readWriteServiceData[key][subkey].indexOf(id);
							$scope.readWriteServiceData[key][subkey].splice(indexSOData, 1);
							  // $scope.readWriteServiceData[key][subkey].splice(_.findIndex($scope.readWriteServiceData[key][subkey], findObj), 1);
							
						}
						if ($scope.readWriteServiceData[key][subkey].length <= 0) {
							delete $scope.readWriteServiceData[key][subkey]
						}
						var count = 0;
						angular.forEach($scope.readWriteServiceData[key], function(row, i) {
							count++;
						})
						if (count == 0) {
							delete $scope.readWriteServiceData[key];
						}
					}
				}

				$scope.setReadWriteNodeData = function(id, key, subkey) {
					var idNodeData = key + "." + subkey + "." + id;
					var nodeKey = $attrs.tabNodeId;
					if (nodeKey != 'All Nodes') {
						var findNodeObj = (idNodeData);

						var exists = $scope.selectedModel.indexOf(findNodeObj) !== -1;
						// if(nodeKey != 'All Nodes'){
						if (exists) {
							if (!$scope.nodeServiceData[nodeKey])
								$scope.nodeServiceData[nodeKey] = {};
							if (!$scope.nodeServiceData[nodeKey][key])
								$scope.nodeServiceData[nodeKey][key] = {};
							if (!$scope.nodeServiceData[nodeKey][key][subkey])
								$scope.nodeServiceData[nodeKey][key][subkey] = [];


							$scope.nodeServiceData[nodeKey][key][subkey].push(id);
						} else {
							var indexSO1 = $scope.nodeServiceData[nodeKey][key][subkey].indexOf(id);
							//$scope.selectedModel.splice(indexSO1, 1);
							$scope.nodeServiceData[nodeKey][key][subkey].splice(indexSO1, 1);						
						}
						// }   
						if ($scope.nodeServiceData[nodeKey][key][subkey].length <= 0) {
							delete $scope.nodeServiceData[nodeKey][key][subkey]
						}

						var count = 0;
						angular.forEach($scope.nodeServiceData[nodeKey], function(row, i) {
							count++;
						})
						if (count == 0) {
							delete $scope.nodeServiceData[nodeKey];
						}
					}
				}

				$scope.externalEvents = {
					onItemSelect         : angular.noop,
					onItemDeselect       : angular.noop,
					onSelectAll          : angular.noop,
					onDeselectAll        : angular.noop,
					onInitDone           : angular.noop,
					onMaxSelectionReached: angular.noop
				};

				$scope.settings = {
					dynamicTitle            : true,
					scrollable              : true,
					scrollableHeight        : '300px',
					scrollableWidth         : '300px',
					closeOnBlur             : true,
					displayProp             : 'label',
					idProp                  : '$index',
					externalIdProp          : 'id',
					enableSearch            : true,
					selectionLimit          : 0,
					showCheckAll            : true,
					showUncheckAll          : true,
					closeOnSelect           : false,
					buttonClasses           : 'btn btn-default',
					closeOnDeselect         : false,
					groupBy                 : $attrs.groupBy || undefined,
					groupByTextProvider     : null,
					smartButtonMaxItems     : 0,
					smartButtonTextConverter: angular.noop
				};

				$scope.texts = {
					checkAll               : 'Check All',
					uncheckAll             : 'Uncheck All',
					selectionCount         : 'checked',
					selectionOf            : '/',
					searchPlaceholder      : 'Search...',
					buttonDefaultText      : 'Select',
					dynamicButtonTextSuffix: 'checked'
				};

				$scope.searchFilter = $scope.searchFilter || '';

				if (angular.isDefined($scope.settings.groupBy)) {
					$scope.$watch('options', function(newValue) {
						if (angular.isDefined(newValue)) {
							$scope.orderedItems = $filter('orderBy')(newValue, $scope.settings.groupBy);
						}
					});
				}

				angular.extend($scope.settings, $scope.extraSettings || []);
				angular.extend($scope.externalEvents, $scope.events || []);
				angular.extend($scope.texts, $scope.translationTexts);

				$scope.singleSelection = $scope.settings.selectionLimit === 1;

				function getFindObj(id) {
					var findObj = {};

					if ($scope.settings.externalIdProp === '') {
						findObj[$scope.settings.idProp] = id;
					} else {
						findObj[$scope.settings.externalIdProp] = id;
					}

					return findObj;
				}

				function clearObject(object) {
					for (var prop in object) {
						delete object[prop];
					}
				}

				if ($scope.singleSelection) {
					if (angular.isArray($scope.selectedModel) && $scope.selectedModel.length === 0) {
						clearObject($scope.selectedModel);
					}
				}

				if ($scope.settings.closeOnBlur) {
					$document.on('click', function(e) {
						var target = e.target.parentElement;
						var parentFound = false;

						while (angular.isDefined(target) && target !== null && !parentFound) {
							if (_.contains(target.className.split(' '), 'multiselect-parent') && !parentFound) {
								if (target === $dropdownTrigger) {
									parentFound = true;
								}
							}
							target = target.parentElement;
						}

						if (!parentFound) {
							$scope.$apply(function() {
								$scope.open = false;
							});
						}
					});
				}

				$scope.getGroupTitle = function(groupValue) {
					if ($scope.settings.groupByTextProvider !== null) {
						return $scope.settings.groupByTextProvider(groupValue);
					}

					return groupValue;
				};

				$scope.getButtonText = function() {
					if ($scope.settings.dynamicTitle && ($scope.selectedModel.length > 0 || (angular.isObject($scope.selectedModel) && _.keys($scope.selectedModel).length > 0))) {
						if ($scope.settings.smartButtonMaxItems > 0) {
							var itemsText = [];

							angular.forEach($scope.options, function(optionItem) {
								if ($scope.isChecked($scope.getPropertyForObject(optionItem, $scope.settings.idProp))) {
									var displayText = $scope.getPropertyForObject(optionItem, $scope.settings.displayProp);
									var converterResponse = $scope.settings.smartButtonTextConverter(displayText, optionItem);

									itemsText.push(converterResponse ? converterResponse : displayText);
								}
							});

							if ($scope.selectedModel.length > $scope.settings.smartButtonMaxItems) {
								itemsText = itemsText.slice(0, $scope.settings.smartButtonMaxItems);
								itemsText.push('...');
							}

							return itemsText.join(', ');
						} else {
							var totalSelected;

							if ($scope.singleSelection) {
								totalSelected = ($scope.selectedModel !== null && angular.isDefined($scope.selectedModel[$scope.settings.idProp])) ? 1 : 0;
							} else {
								totalSelected = angular.isDefined($scope.selectedModel) ? $scope.selectedModel.length : 0;
							}

							if (totalSelected === 0) {
								return $scope.texts.buttonDefaultText;
							} else {
								return totalSelected + ' ' + $scope.texts.dynamicButtonTextSuffix;
							}
						}
					} else {
						return $scope.texts.buttonDefaultText;
					}
				};

				$scope.getPropertyForObject = function(object, property) {
					if (angular.isDefined(object) && object.hasOwnProperty(property)) {

						return object[property];
					} else {
						return object;
					}

					//return '';
				};

				$scope.selectAll = function() {
					$scope.deselectAll(false);
					$scope.externalEvents.onSelectAll();

					angular.forEach($scope.options, function(value) {
						$scope.setSelectedItem(value[$scope.settings.idProp], true);
					});
				};

				$scope.deselectAll = function(sendEvent) {
					sendEvent = sendEvent || true;

					if (sendEvent) {
						$scope.externalEvents.onDeselectAll();
					}

					if ($scope.singleSelection) {
						clearObject($scope.selectedModel);
					} else {
						$scope.selectedModel.splice(0, $scope.selectedModel.length);
					}
				};

				$scope.setSelectedItem = function(id, key, subkey, dontRemove) {
					if (type == "nodes") {
						var idTemp = id;
					} else {
						var idTemp = key + "." + subkey + "." + id;
					}

					var findObj = (idTemp);
					var finalObj = null;

					if ($scope.settings.externalIdProp === '') {
						finalObj = _.find($scope.options, findObj);
					} else {
						finalObj = findObj;
					}

					if ($scope.singleSelection) {
						clearObject($scope.selectedModel);
						angular.extend($scope.selectedModel, finalObj);
						$scope.externalEvents.onItemSelect(finalObj);
						if ($scope.settings.closeOnSelect) $scope.open = false;

						return;
					}

					dontRemove = dontRemove || false;

					//var exists = _.findIndex($scope.selectedModel, findObj) !== -1;
					var exists = $scope.selectedModel.indexOf(findObj) !== -1;

					if (!dontRemove && exists) {
						//$scope.selectedModel.splice(_.findIndex($scope.selectedModel, findObj), 1);
						var indexSO = $scope.selectedModel.indexOf(findObj);
						$scope.selectedModel.splice(indexSO, 1);
						$scope.externalEvents.onItemDeselect(findObj);
						$scope.countVar = $scope.countVar - 1;
						common.setCountVar($scope.countVar)
						$scope.countFlag = false;
						common.setCountFlag($scope.countFlag)
					} else if (!exists && ($scope.settings.selectionLimit === 0 || $scope.selectedModel.length < $scope.settings.selectionLimit)) {
						$scope.countVar = $scope.countVar + 1;
						common.setCountVar($scope.countVar)
						if ($scope.countVar >= 25) {
							$scope.countFlag = true;
							common.setCountFlag($scope.countFlag)
								//common.setCountVar($scope.countVar)
							$('#myModal11').modal({ show: true });
						}
						$scope.selectedModel.push(finalObj);
						$scope.externalEvents.onItemSelect(finalObj);

					}
					if ($scope.settings.closeOnSelect) $scope.open = false;
				};
				var arrayVal = [];var arrayValNode = [];
				angular.forEach($scope.openedData, function(row, index) {
						var statsData = row['openedStats']?row['openedStats']:row['closedStats'];
						for ( var key in statsData) {							
								for (var key1 in statsData[key]) {
									var arr = statsData[key][key1]
									for ( var j=0; j< arr.length; j++) {
										var obj = {};
										var objNode = {};
										if ( row['tabName'] == 'All Nodes') {
											obj.tabName = row['tabName'];
											obj.value = key + "." +key1 + "." + arr[j];
											arrayVal.push(obj);
										} else {
											objNode.tabName = row['tabName'];
											objNode.value = key + "." +key1 + "." + arr[j];
											arrayValNode.push(objNode);
										}
									}
								}
						}
				});
				//var cpuUsageChecked = 'systemStats.cpu.CpuUsage';
				var flag = true;
				$scope.isChecked = function(id, key, subkey) {
					var listId = key + "." + subkey + "." + id;
					if ($scope.singleSelection) {
						return $scope.selectedModel !== null && angular.isDefined($scope.selectedModel[$scope.settings.idProp]) && $scope.selectedModel[$scope.settings.idProp] === getFindObj(id)[$scope.settings.idProp];
					} /*else if ((listId == cpuUsageChecked)) {
						if (flag) {
							flag = false;
							$scope.setSelectedItem(id, key, subkey);
							if (key && subkey) {
								$scope.setReadWriteData(id, key, subkey);
								$scope.setReadWriteNodeData(id, key, subkey)
							}
						}
						return $scope.selectedModel.indexOf(id) === -1;
					}*/ else {
							for ( var i=0; i< arrayVal.length; i++) {
								if (listId == arrayVal[i].value) {
									if ( flag) {
										if ( i+1 == arrayVal.length) {
											flag = false;
										}
										if ((arrayVal[i].tabName == $attrs.tabNodeId)) {
											$scope.setSelectedItem(id, key, subkey);
											if (key && subkey) {
												$scope.setReadWriteData(id, key, subkey);
											}
										}	
									}
									if (arrayVal[i].tabName == $attrs.tabNodeId) {
										return $scope.selectedModel.indexOf(id) === -1;
									}
								}
							}
						}
					//return _.findIndex($scope.selectedModel, getFindObj(id)) !== -1;
					//return $scope.selectedModel.indexOf(id) !== -1;
				};
				var flag1 = true;
				$scope.isCheckedNode = function(id, key, subkey) {
					var listId = key + "." + subkey + "." + id;
					if ($scope.singleSelection) {
						return $scope.selectedModel !== null && angular.isDefined($scope.selectedModel[$scope.settings.idProp]) && $scope.selectedModel[$scope.settings.idProp] === getFindObj(id)[$scope.settings.idProp];
					} else {
							for ( var i=0; i< arrayValNode.length; i++) {
								if (listId == arrayValNode[i].value) {
									if ( flag1) {
										if ( i+1 == arrayValNode.length) {
											flag1 = false;
										} 										
										if ((arrayValNode[i].tabName == $attrs.tabNodeId)) {
											$scope.setSelectedItem(id, key, subkey);
											if (key && subkey) {
												$scope.setReadWriteNodeData(id, key, subkey);
											}
										}
										
									}
									if (arrayValNode[i].tabName == $attrs.tabNodeId) {
										return $scope.selectedModel.indexOf(id) === -1;
									}
								}
							}
						}
					//return $scope.selectedModel.indexOf(id) !== -1;
				};
				$scope.toggleHeader = function(event, id) {
					var id2 = $attrs.tabNodeId;
					var nodeKey = '';
					if (id2 != 'All Nodes') {
						id2.split('\.').forEach(function(index) { nodeKey += index })
						$("#ul_" + id + "_" + $scope.domId + "_" + nodeKey).toggle();
						$("#i_" + id + "_" + $scope.domId + "_" + nodeKey).toggleClass("fa-caret-right");
					} else {
						//nothing
						//id2.split('\).forEach(function(index){nodeKey+=index})
						nodeKey = id2.replace(/\s/g, "");
						$("#ul_" + id + "_" + $scope.domId + "_" + nodeKey).toggle();
						$("#i_" + id + "_" + $scope.domId + "_" + nodeKey).toggleClass("fa-caret-right");
					}
				}

				$scope.externalEvents.onInitDone();
			}
		};
	}
]);
