/**
 * Renders all the widgets on the tab and triggers the datasources that are used by the widgets.
 * Customize your widgets by:
 *  - Overriding or extending widget API methods
 *  - Changing widget settings or options
 */
/* jshint unused: false */
define(['angular',
    './sample-module'
], function (angular, controllers) {
    'use strict';
    // Controller definition
    controllers.controller('DashboardCtrl', ['$timeout', '$http', '$scope', '$rootScope', '$log', 'sosService',
			function ($timeout, $http, $scope, $rootScope, $log, sosService) {
				$rootScope.hideloader = false;
				$scope.getzonestatus = function(floor_id){
					var _zonedata = [];
					
					var names=[];
					sosService.getZonesNames({
						floor_id: $scope.selectedFloor.id
					}).then(function(data) {
						$scope.zones=data.zones;
						if($scope.zones){
							for(var i=0;i<$scope.zones.length;i++){
								names.push($scope.zones[i].zone_name);
							}
							$scope.zoneNames=names;
						}
						
						sosService.getClientToken().then(function(data) {
							$rootScope.access_token = data.access_token;
								var zoneNames=$scope.zoneNames;
								sosService.getTimeseriesData({
								url: sosService.timeseriesdatapointurl,
								data: {
									"cache_time": 0,
									"start":"1mi-ago","tags":[{"name":zoneNames,"filters":{"attributes":{"floor_id":floor_id}}}]
									},
								token: $rootScope.access_token
							 }).then(function(data) {
								 $scope.queryData=data.tags;
								 if($scope.queryData){
								for(var i=0;i<$scope.queryData.length;i++){
									if($scope.queryData[i].results[0].values[0]){
										var zone_name=$scope.queryData[i].name;
										var value=$scope.queryData[i].results[0].values[0][1];
										if(value>0)
										_zonedata.push({zone:zone_name,status:"ON"});
										else
										_zonedata.push({zone:zone_name,status:"OFF"});
									}
									else{
										_zonedata.push({zone:$scope.queryData[i].name,status:"OFF"});
									}
								}
							}
							 }, function(message) {
								$log.error(message);
							 });
							}, function(message) {
								$log.error(message);
							});
					});
					
					
					return _zonedata;
				}
				
				$scope.getTotalWattage = function(menuItem){
					$scope.totalWattage=0;
					$scope.savings=0;
					$scope.activeMenu = menuItem
					var intervals='';
					if(menuItem=="Days"){
						intervals='d';
					}
					else if(menuItem=="Week"){
						intervals='w';
					}
					else if(menuItem=="Month"){
						intervals='m';
					}
					else if(menuItem=="Quarter"){
						intervals='q';
					}
					else if(menuItem=="Year"){
						intervals='y';
					}
						sosService.getTotalWattage({
							floor_id:$scope.selectedFloor.id,
							zones:$scope.zoneNames,
							interval:intervals,
							token: $rootScope.access_token
						}).then(function(data) {
						 if(data){
							console.log(data);
							$scope.totalWattage=data.enrgyUsed;
							$scope.savings=data.savings;
						 }
						else{
							$scope.totalWattage="0";
							$scope.savings="0";
						}
						})
				}
				$scope.chartData = [];
				$scope.zonedata=[];
				$scope.zoneNames=[];
				$scope.chartZoneNames=[];
				sosService.getSosAppData({url: sosService.getsosdata}).then(function (data) {
					$rootScope.hideloader = true;
					$rootScope.initialBrowserContext = data;
					$scope.selectedBuilding = $rootScope.initialBrowserContext.data[0];
					$scope.selectedFloor = $scope.selectedBuilding.children[0];
					$scope.setActive("Days");
					$scope.zonedata = $scope.getzonestatus($scope.selectedBuilding.children[0].id);
					sosService.getSosAppData({url: sosService.getfloorplan+$scope.selectedFloor.id}).then(function (data) {
						$("#floorplan").attr("src",sosService.getfloorplan+$scope.selectedFloor.id);
						$("#floorplan").css("display","block");
						$("#noimagetxt").css("display","none");
					},function(message){
						$("#floorplan").css("display","none");
						$("#noimagetxt").css("display","block");
					});
				})
				$scope.updateFloor = function(){
					console.log($scope.selectedFloor);
					sosService.getSosAppData({url: sosService.getfloorplan+$scope.selectedFloor.id}).then(function (data) {
						$("#floorplan").attr("src",sosService.getfloorplan+$scope.selectedFloor.id);
						$("#floorplan").css("display","block");
						$("#noimagetxt").css("display","none");
					},function(message){
						$("#floorplan").css("display","none");
						$("#noimagetxt").css("display","block");
					});
					$scope.setActive("Days");
					$scope.zonedata = $scope.getzonestatus($scope.selectedFloor.id);
					
				}
				$scope.updateBuilding = function(){
					$scope.selectedFloor = $scope.selectedBuilding.children[0];
					sosService.getSosAppData({url: sosService.getfloorplan+$scope.selectedFloor.id}).then(function (data) {
						$("#floorplan").attr("src",sosService.getfloorplan+$scope.selectedFloor.id);
						$("#floorplan").css("display","block");
						$("#noimagetxt").css("display","none");
					},function(message){
						$("#floorplan").css("display","none");
						$("#noimagetxt").css("display","block");
					});
				}
				$scope.showchart = function(){
					$scope.displaychartsection = true;
				}
				$scope.hidechart = function(){
					$scope.displaychartsection = false;
				}
				$scope.menuItems = ['Days', 'Week', 'Month', 'Quarter', 'Year'];
				$scope.activeMenu = $scope.menuItems[0];
			
				$scope.setActive = function(menuItem) {
					$scope.activeMenu = menuItem
					var names=[];
					sosService.getZonesNames({
						floor_id: $scope.selectedFloor.id
					}).then(function(data) {
						$scope.zones=data.zones;
						if($scope.zones){
							for(var i=0;i<$scope.zones.length;i++){
								names.push($scope.zones[i].zone_name);
							}
							$scope.zoneNames=names;
						}
						$scope.getTotalWattage($scope.activeMenu);
						$scope.chartZoneNames=names;
						angular.element("#chartZone").detach();
						angular.element("#chartFloor").detach();
						$scope.chart(menuItem);
					});
				}
				$scope.chartMenuItems = ['Days', 'Week', 'Month', 'Quarter', 'Year'];
				$scope.chartActiveMenu = $scope.chartMenuItems[0];
				$scope.chart=function(menuItem){
					$scope.chartActiveMenu=menuItem;
					sosService.getClientToken().then(function(data) {
					$rootScope.access_token = data.access_token;
					var start="1d-ago";
					var interval="3600s";
					if(menuItem=="Days"){
						interval="3600s";
						start="1d-ago";
					}
					else if(menuItem=="Week"){
						interval="86400s";
						start="1w-ago";
					}
					
					else if(menuItem=="Month"){
						interval="604800s";
						start="1mm-ago";
					}
					
					else if(menuItem=="Quarter"){
						interval="604800s";
						start="3mm-ago";
					}
					
					else if(menuItem=="Year"){
						interval="2678400s";
						start="1y-ago";
					}
					sosService.getTimeseriesData({
						url: sosService.timeseriesdatapointurl,
						data: {
						 "cache_time": 0,
						 "tags":[{"name":$scope.chartZoneNames,"aggregations":[{"type":"sum","interval":interval}],"filters":{"attributes":{"floor_id":$scope.selectedFloor.id},"measurements":{"condition":"ge","values":"1"}}}],
						 "start": start
						},
						token: $rootScope.access_token
					 }).then(function(data) {
						var _floor="Floor-";
						_floor+=$scope.selectedFloor.id; 
						var _floorData=[];	
						var _chartFloorHtml = "<px-chart id='chartFloor' zoom-controls='hidecontrols' tooltip-kind='hc'  tooltip-type='condensed' legend='{\"enabled\":true}' tooltip-date-format='MMM DD, YYYY' tooltip-time-format='HH:mm:ss ZZ' tooltip-timestamp='show'>";
							for(var j=0;j<data.tags[0].results[0].values.length;j++){
								var total=0;
								for(var k=0;k<data.tags.length;k++){
									if(data.tags[k].results[0].values[j]!=null){
									 total+=data.tags[k].results[0].values[j][1];
									}
								}
								
								_floorData[j]=new Array(data.tags[0].results[0].values[j][0],total);
							}
							_chartFloorHtml += "<px-chart-series-line id="+_floor+" line-width='5'  upper-threshold='300' data="+JSON.stringify(_floorData)+"></px-chart-series-line>";
							_chartFloorHtml += "</px-chart>";
							angular.element("#chartFloor").detach();
							angular.element("#chartbox").append(_chartFloorHtml);
							
							var _chartZoneHtml = "<px-chart id='chartZone' zoom-controls='hidecontrols' tooltip-kind='hc' tooltip-type='condensed' legend='{\"enabled\":true}' tooltip-date-format='MMM DD, YYYY' tooltip-time-format='HH:mm:ss ZZ' tooltip-timestamp='show'>";
							var threshold=300;
							for(var i=0;i<data.tags.length;i++){
								var _data = data.tags[i].results[0].values;
								for(var j=0;j<_data.length;j++){
									_data[j].splice(2,1);
								}
								_chartZoneHtml += "<px-chart-series-line line-width='2'id="+(data.tags[i].name).replace(/ /g,"-")+" data="+JSON.stringify(_data)+"></px-chart-series-line>";
							}
							_chartZoneHtml += "</px-chart>";
							angular.element("#chartZone").detach();
							angular.element("#chartbox").append(_chartZoneHtml);
							
							
					 }, function(message) {
						$log.error(message);
					 });
				}, function(message) {
					$log.error(message);
				});	
				}
			}
		]
	);

});
