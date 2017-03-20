/**
 * Router Config
 * This is the router definition that defines all application routes.
 */
/*global define */
define(['angular', 'angular-ui-router'], function(angular) {
	'use strict';
	return angular.module('app.routes', ['ui.router']).config(['$stateProvider', '$urlRouterProvider', '$locationProvider', function($stateProvider, $urlRouterProvider, $locationProvider) {

		//Turn on or off HTML5 mode which uses the # hash
		//$locationProvider.html5Mode(true).hashPrefix('!');

		/**
		 * Router paths
		 * This is where the name of the route is matched to the controller and view template.
		 */
		$stateProvider
			.state('secure', {
				template: '<ui-view/>',
				abstract: true,
				resolve: {
					authenticated: ['$q', 'PredixUserService', '$rootScope', 'sosService', function ($q, predixUserService, $rootScope, sosService) {
						var deferred = $q.defer();
						deferred.resolve({});
						return deferred.promise;
					}]
				}
			})
			.state('#/dashboard', {
				url: '/dashboard',
				templateUrl: 'views/dashboards.html',
				controller: 'DashboardCtrl'
			})
			.state('#/createupdateplan', {
				url: '/createupdateplan',
				templateUrl: 'views/data_analysis.html',
				controller: 'DataAnalysisCtrl'
			});

		$urlRouterProvider.otherwise(function ($injector) {
			var $state = $injector.get('$state');
			document.querySelector('px-app-nav').markSelected('#/dashboard');
			$state.go('#/dashboard');
		});

	}]);
});
