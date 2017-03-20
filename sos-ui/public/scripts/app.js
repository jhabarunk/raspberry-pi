/**
 * Load controllers, directives, filters, services before bootstrapping the application.
 * NOTE: These are named references that are defined inside of the config.js RequireJS configuration file.
 */
define([
    'jquery',
    'angular',
    'main',
    'routes',
    'lodash',
    'interceptors',
    'px-datasource'
], function ($, angular) {
    'use strict';

    /**
     * Application definition
     * This is where the AngularJS application is defined and all application dependencies declared.
     * @type {module}
     */
    var predixApp = angular.module('predixApp', [
        'app.routes',
        'app.interceptors',
        'sample.module',
        'predix.datasource'
    ]);

    /**
     * Main Controller
     * This controller is the top most level controller that allows for all
     * child controllers to access properties defined on the $rootScope.
     */
    predixApp.controller('MainCtrl', ['$scope', '$rootScope', 'PredixUserService', function ($scope, $rootScope, predixUserService) {
				//Global application object
        window.App = $rootScope.App = {
            version: '1.0',
            name: 'Predix Seed',
            session: {},
            tabs: [
                {icon: 'fa-tachometer', state: '#/dashboard', label: 'Dashboards'},
                {icon: 'fa-area-chart', state: '#/createupdateplan', label: 'Create/Update Plan'}
            ]
        };
        $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState, fromParams, error) {
            if (angular.isObject(error) && angular.isString(error.code)) {
                switch (error.code) {
                    case 'UNAUTHORIZED':
                        //redirect
                        predixUserService.login(toState);
                        break;
                    default:
                        //go to other error state
                }
            }
            else {
                // unexpected error
            }
        });
				/* $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams){ 
					window.localStorage.setItem('state', toState.name);
				}) */
    }]);
		predixApp.directive('resize',['$window', function($window) {
			return {
				restrict: 'EA',
				link: function(scope) {
					angular.element($window).bind('resize', function() {
						if(angular.element(".loader").parent()[0].clientWidth > 0)
							angular.element(".loader").css({width:angular.element(".loader").parent()[0].clientWidth+'px'});
					});
				}
			};
		}])

    //Set on window for debugging
    window.predixApp = predixApp;

    //Return the application  object
    return predixApp;
});
