/*global define */
define(['angular', './sample-module'], function (angular, module) {
	'use strict';
	/**
	 * PredixAssetService is a sample angular service that integrates with Predix Asset Server API
	 */
	module.factory('sosService', ['$q', '$http', function ($q, $http) {

		/**
		 * predix asset server base url
		 */
		
		var timeseriesURL = 'https://time-series-store-predix.run.aws-usw02-pr.ice.predix.io';
		var predix_zone_id = 'e517eeef-02bc-4548-afea-2d16ecc25a3f';
		var authorization = 'Basic YXBwX2NsaWVudF8xOnNlY3JldA==';
		var tokenURL = 'https://5df08919-1ad4-49bd-975a-dd83f706f78e.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token?client_id=app_client_1&grant_type=client_credentials';
		var getSosAppData = function(parent) {
				var url = parent.url;
				var _headers = {};
				var deferred = $q.defer();
				$http({
					method: 'GET',
					headers: _headers,
					url:url
				}).success(function(results) {
					deferred.resolve(results);
				}).error(function () {
					deferred.reject('Error fetching data with url ' + url);
				});
				return deferred.promise;
		};
		var postSosAppData = function(parent) {
				var url = parent.url;
				var data = parent.data;
				var deferred = $q.defer();
				$http({
					method: 'POST',
					url: url,
					headers: {'Content-Type': 'application/json'},
					data:JSON.stringify(data),
					dataType: "json",
					contentType: "application/json"
				}).success(function(results) {
					deferred.resolve(results);
				}).error(function (error, status) {
					deferred.reject({error:error,status:status});
				});
				return deferred.promise;
		};
		var getClientToken = function() {
				var deferred = $q.defer();
				$http({
					method: 'POST',
					headers: {'authorization':authorization,'Content-Type':'application/x-www-form-urlencoded'},
					url: tokenURL,
				}).success(function(results) {
					deferred.resolve(results);
				}).error(function () {
					deferred.reject('Error fetching data with url ' + url);
				});
				return deferred.promise;
		};
		var getTimeseriesData = function(parent) {
				var url = parent.url;
				var data = parent.data;
				var token = parent.token;
				var deferred = $q.defer();
				$http({
					method: 'POST',
					url: url,
					headers: {'predix-zone-id': predix_zone_id, 'authorization': 'bearer '+token},
					data:JSON.stringify(data),
					dataType: "json",
					contentType: "application/json"
				}).success(function(results) {
					deferred.resolve(results);
				}).error(function (error, status) {
					deferred.reject({error:error,status:status});
				});
				return deferred.promise;
		};
		var getTotalWattage = function(parent){
			var zones=parent.zones;
			
			var floor_id=parent.floor_id;
			var token = parent.token;
			var interval=parent.interval;
			//console.log(zones);
			var deferred = $q.defer();
			$http({
					method: 'POST',
					url: "https://sos-rmd-datasource-app.run.aws-usw02-pr.ice.predix.io/sos/getSOSCollatedValue",
					data:{'floor_id':floor_id, 'interval':interval,'zoneNames':zones},
					headers: {'authorization': 'bearer '+token},
					dataType: "json",
					contentType: "application/json"
				}).success(function(results) {
					deferred.resolve(results);
				}).error(function (error, status) {
					deferred.reject({error:error,status:status});
				});
				return deferred.promise;
		};
		
		var getZonesNames=function(parent){
			var floor_id=parent.floor_id;
			var deferred = $q.defer();
			$http({
				method: 'GET',
				url: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/findOneFloor?id="+floor_id,
			}).success(function(results) {
				deferred.resolve(results);
			}).error(function (error, status) {
				deferred.reject({error:error,status:status});
			});
			return deferred.promise;
		};
		var uploadImage = function(parent) {
			var url = parent.url;
			var data = parent.data;
			var deferred = $q.defer();
			/* $http({
				method: 'POST',
				url: "http://3.209.212.233:8080/upload",
				data:data,
				dataType: "json",
				contentType: false,
				processData: false,
				mimeType: "multipart/form-data",
			}).success(function(results) {
				deferred.resolve(results);
			}).error(function (error, status) {
				deferred.reject({error:error,status:status});
			}); */
			var settings = {
				"async": true,
				"crossDomain": true,
				"url": url,//"http://3.209.212.233:8080/upload",
				"method": "POST",
				"processData": false,
				"contentType": false,
				"mimeType": "multipart/form-data",
				"data": data,
				xhr: function(){
					//upload Progress
					var xhr = $.ajaxSettings.xhr();
					if (xhr.upload) {
						xhr.upload.addEventListener('progress', function(event) {
							var percent = 0;
							var position = event.loaded || event.position;
							var total = event.total;
							if (event.lengthComputable) {
								percent = Math.ceil(position / total * 100);
							}
							$("#percentageuploaded").text(percent+"% uploaded");
						}, true);
					}
					return xhr;
				},
				success: function (data) {
					deferred.resolve(data);
				},
				error: function (textStatus, errorThrown) {
					//console.log(textStatus)
					//console.log(errorThrown)
					deferred.reject({error:textStatus});
				}
			}
			$.ajax(settings).done(function (data) {
				deferred.resolve(data);
			});
			return deferred.promise;
		};
		return {
			getClientToken: getClientToken,
			getSosAppData: getSosAppData,
			postSosAppData: postSosAppData,
			uploadImage: uploadImage,
			getTimeseriesData:getTimeseriesData,
			getTotalWattage:getTotalWattage,
			getZonesNames:getZonesNames,
			timeseriesdatapointurl: timeseriesURL+'/v1/datapoints',
			getsosdata:"https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/wrapperForQuery",
			updatebuilding: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/updateBuilding",
			updatefloor: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/updateFloor",
			updatezone: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/updateZone",
			updateswitch: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/updateSwitch",
			updatelight: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/updateLight",
			savebuilding: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/saveBuilding",
			savefloor: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/saveFloor",
			savezone: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/saveZone",
			saveswitch: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/saveSwitch",
			savelight: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/saveLight",
			uploadfloorplan: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/uploadFloorPlan",
			getfloorplan: "https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/getFloorPlan?id="
		};
	}]);
});