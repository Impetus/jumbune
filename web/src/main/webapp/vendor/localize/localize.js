'use strict';

/*
 * An AngularJS Localization Service
 */

angular.module('localization', [])
// localization service responsible for retrieving resource files from the server and
// managing the translation dictionary
.factory('localize', ['$http', '$rootScope', '$window', '$filter', 'common',
    function($http, $rootScope, $window, $filter, common) {
        var localize = {
            // use the $window service to get the language of the user's browser
            language: $window.navigator.userLanguage || $window.navigator.language,
            // array to hold the localized resource string entries
            dictionary: [],
            fallBackDictionary: [],
            // flag to indicate if the service hs loaded the resource file
            resourceFileLoaded: false,

            // success handler for all server communication
            successCallback: function(data, fallbackData) {
                // store the returned array in the dictionary
                localize.dictionary = data;
                localize.fallBackDictionary = fallbackData;

                common.setDictionary(data, fallbackData);

                // set the flag that the resource are loaded
                localize.resourceFileLoaded = true;
                // broadcast that the file has been loaded
                $rootScope.$broadcast('localizeResourcesUpdates');
            },

            promise: function() {
                var deferred = $.Deferred();
                var timer = $window.setInterval(function() {
                    if (localize.dictionary.length !== 0 && localize.fallBackDictionary !== 0) {
                        $window.clearInterval(timer);
                        deferred.resolve();
                    }
                }, 50);
                return deferred.promise();
            },

            loadFallbackLanguage: function() {
                var d1 = $.Deferred();
                $http({
                    method: "GET",
                    url: 'app/i18n/resources-locale_en.js',
                    cache: false
                }).success(function(data) {
                    d1.resolve(data);
                }).error(function(data) {
                    d1.rejet(data);
                });
                return d1.promise();
            },

            loadLanguage: function() {
                var d2 = $.Deferred();
                var langKey = localize.language.slice(0, 2);
                var url = 'app/i18n/resources-locale_' + langKey + '.js';
                $http({
                    method: "GET",
                    url: url,
                    cache: false
                }).success(function(data) {
                    d2.resolve(data);
                }).error(function(data) {
                    d2.rejet(data);
                });
                return d2.promise();
            },

            // allows setting of language on the fly
            setLanguage: function(value) {
                if (typeof value === "undefined" || $.trim(value) == "") {
                    localize.language = ($window.navigator.userLanguage || $window.navigator.language).slice(0, 2);
                } else {
                    localize.language = value;
                }
                $window.sessionStorage.setItem('langkey', localize.language);
                localize.initLocalizedResources();
            },

            // loads the language resource file from the server
            initLocalizedResources: function() {
                $.when(localize.loadLanguage(), localize.loadFallbackLanguage())
                    .done(localize.successCallback)
                    .fail(function() {
                        console.log("Error while loading locale file(s)");
                    });
            },

            // checks the dictionary for a localized resource string
            getLocalizedString: function(value) {
                // default the result to an empty string
                var result = '';
                var localDic = localize.dictionary;
                var fallBackDic = localize.fallBackDictionary;
                // make sure the dictionary has valid data
                if (localDic && fallBackDic) {
                    if (localDic[value]) {
                        return localDic[value];
                    } else if (fallBackDic[value]) {
                        return fallBackDic[value];
                    } else {
                        return value;
                    }
                }
            }
        };

        // force the load of the resource file
        //localize.initLocalizedResources(); 
        localize.setLanguage(common.langkey());

        // return the local instance when called
        return localize;
    }
])
// simple translation filter
// usage {{ TOKEN | i18n }}
.filter('i18n', ['localize',
    function(localize) {
        return function(input) {
            return localize.getLocalizedString(input);
        };
    }
])
// translation directive that can handle dynamic strings
// updates the text value of the attached element
// usage <span data-i18n="TOKEN" ></span>
// or
// <span data-i18n="TOKEN|VALUE1|VALUE2" ></span>
.directive('i18n', ['localize',
    function(localize) {
        var i18nDirective = {
            restrict: "EAC",
            updateText: function(elm, token) {
                var values = token.split('|');
                if (values.length >= 1) {
                    // construct the tag to insert into the element
                    var tag = localize.getLocalizedString(values[0]);
                    // update the element only if data was returned
                    if ((tag !== null) && (tag !== undefined) && (tag !== '')) {
                        if (values.length > 1) {
                            for (var index = 1; index < values.length; index++) {
                                var target = '{' + (index - 1) + '}';
                                tag = tag.replace(target, values[index]);
                            }
                        }
                        // insert the text into the element
                        elm.text(tag);
                    };
                }
            },

            link: function(scope, elm, attrs) {
                scope.$on('localizeResourcesUpdates', function() {
                    i18nDirective.updateText(elm, attrs.i18n);
                });

                attrs.$observe('i18n', function(value) {
                    i18nDirective.updateText(elm, attrs.i18n);
                });
            }
        };

        return i18nDirective;
    }
])
// translation directive that can handle dynamic strings
// updates the attribute value of the attached element
// usage <span data-i18n-attr="TOKEN|ATTRIBUTE" ></span>
// or
// <span data-i18n-attr="TOKEN|ATTRIBUTE|VALUE1|VALUE2" ></span>
.directive('i18nAttr', ['localize',
    function(localize) {
        var i18NAttrDirective = {
            restrict: "EAC",
            updateText: function(elm, token) {
                var values = token.split('|');
                // construct the tag to insert into the element
                var tag = localize.getLocalizedString(values[0]);
                // update the element only if data was returned
                if ((tag !== null) && (tag !== undefined) && (tag !== '')) {
                    if (values.length > 2) {
                        for (var index = 2; index < values.length; index++) {
                            var target = '{' + (index - 2) + '}';
                            tag = tag.replace(target, values[index]);
                        }
                    }
                    // insert the text into the element
                    elm.attr(values[1], tag);
                }
            },
            link: function(scope, elm, attrs) {
                scope.$on('localizeResourcesUpdated', function() {
                    i18NAttrDirective.updateText(elm, attrs.i18nAttr);
                });

                attrs.$observe('i18nAttr', function(value) {
                    i18NAttrDirective.updateText(elm, value);
                });
            }
        };

        return i18NAttrDirective;
    }
]);