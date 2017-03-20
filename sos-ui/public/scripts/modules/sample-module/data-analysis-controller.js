/* jshint unused: false */
define(['angular',
 './sample-module'
], function(angular, controllers) {
 'use strict';

 // Controller definition
 controllers.controller('DataAnalysisCtrl', ['$window', '$scope', '$rootScope', '$http', '$log', '$interval', 'sosService', '$timeout',
  function($window, $scope, $rootScope, $http, $log, $interval, sosService, $timeout) {
		$rootScope.hideloader = false;
		$scope.isexist = false;
		var colBrowser;
		sosService.getSosAppData({url: sosService.getsosdata}).then(function (data) {
			$rootScope.hideloader = true;
			$scope.initialBrowserContext = data;
			console.log(data);
			/* $scope.initialBrowserContext = {
				"data": [{
					"id": "1",
					"name": "Building 01",
					"isOpenable": true,
					"category": "building",
					"children":[{
						"name": "Floor 1",
						"id": "1",
						"isOpenable": true,
						"category": "floor",
						"children":[{
							"name": "Zone 1",
							"id": "1",
							"isOpenable": true,
							"category": "zone",
							"children":[{
								"name": "Switch 1",
								"id": "1",
								"isOpenable": true,
								"category": "switch",
								"children":[{
									"name": "Light 1",
									"id": "1",
									"isOpenable": true,
									"hasChildren": false,
									"category": "light"
								}]
							}]
						}]
					}]
				}],
				"meta": {
					"parentId": null
				}
			} */
			$scope.changeToUniqId($scope.initialBrowserContext.data);
			//console.log($scope.initialBrowserContext);
			/* if(!$window.localStorage.initialBrowserContext1){
				$window.localStorage.initialBrowserContext1 = JSON.stringify($scope.initialBrowserContext);
			}else{
				$scope.initialBrowserContext = JSON.parse($window.localStorage.initialBrowserContext1);
			} */
			$scope.errors = [];
			for(var i = 0; i < Polymer.telemetry.registrations.length; i++) {
        if(Polymer.telemetry.registrations[i].is == "px-modal") {
          Polymer.telemetry.registrations[i].modalActionButtonClicked = function (evt) {
						if(evt.target.id === 'btnModalPositive') {
              this.fire(this.btnModalPositiveClickedEventName);
              if($scope.errors.length == 0) {
                this.modalButtonClicked(evt);
              }
            } else {
              $scope.errors = [];
              this.fire(this.btnModalNegativeClickedEventName);
              this.modalButtonClicked(evt);
            }
          }
        }
      }
			document.getElementById('adminaddmodal').addEventListener('btnModalPositiveClicked', function() {
				$scope.errors = [1];
				/* var v = angular.element("#uploadfilebox").val();
				var allowedExtensions = new Array("jpg","JPG","jpeg","JPEG","PNG","png");
				for(var ct=0;ct<allowedExtensions.length;ct++){
					if(v.lastIndexOf(allowedExtensions[ct]) == -1){$scope.errors=[1]}
				} */
				/* if($scope.errors.length > 0){
					alert("Please select image only");
				}else{ */
					$scope.uploadForm.append("file", angular.element("#uploadfilebox")[0].files[0]);
					$scope.percentage = 0;
					sosService.uploadImage({url:sosService.uploadfloorplan,data:$scope.uploadForm}).then(function (data){
						alert("image uploaded successfully");
						document.getElementById('adminaddmodal').modalButtonClicked();
					},function(message){
						$log.error(message);
					});
				//}
			});
			document.getElementById('additemmodal').addEventListener('btnModalPositiveClicked', function() {
				var _item = angular.element("#itemname").val();
				if (_item != null) {
					var _pid = $scope.pid;
					$scope.isexist = false;
					$scope.findChild($scope.initialBrowserContext.data,_item,_pid);
					if($scope.isexist){
						alert("name already exist");
						return;
					}
					var _url="";
					var _data={};
					if(!_pid){
						_url = sosService.savebuilding;
						_data = {building_name:_item}
					}else if(_pid.indexOf("building") > -1){
						_url = sosService.savefloor;
						_data = {floor_name:_item,building_id:_pid.replace("building_","")}
					}else if(_pid.indexOf("floor") > -1){
						_url = sosService.savezone;
						_data = {zone_name:_item,floor_id:_pid.replace("floor_","")}
					}else if(_pid.indexOf("zone") > -1){
						_url = sosService.saveswitch;
						_data = {switch_name:_item,zone_id:_pid.replace("zone_","")}
					}else if(_pid.indexOf("switch") > -1){
						_url = sosService.savelight;
						_data = {light_name:_item,switch_id:_pid.replace("switch_","")}
					}
					
					sosService.postSosAppData({url: _url,data:_data}).then(function (data) {
						if(data.error){
							alert(data.error);
							return;
						}
						if(_pid == undefined){
							$scope.initialBrowserContext.data.push({"category": "building", "name": _item,"id": "building_"+data.id,"isOpenable": true,"hasChildren": true})
						}else{
							var _id = data.id;
							var _cat = "";
							if(_pid.indexOf("building") > -1){
								_id = "floor_"+data.id;
								_cat = "floor";
							}else if(_pid.indexOf("floor") > -1){
								_id = "zone_"+data.id;
								_cat = "zone";
							}else if(_pid.indexOf("zone") > -1){
								_id = "switch_"+data.id;
								_cat = "switch";
							}else if(_pid.indexOf("switch") > -1){
								_id = "light_"+data.id;
								_cat = "light";
							}
							$scope.addChild($scope.initialBrowserContext.data,_pid,{"category": _cat,"name": _item,"id": _id,"isOpenable": true,"hasChildren": true})
						}
						colBrowser.initialContexts = JSON.stringify($scope.initialBrowserContext);
					},function(message){
						$log.error(message);
					});
					
					//$window.localStorage.initialBrowserContext1 = JSON.stringify($scope.initialBrowserContext);
				}
			});
			document.getElementById('edititemmodal').addEventListener('btnModalPositiveClicked', function() {
				var _item = angular.element("#itemeditname").val();
				var context = $scope.context;
				var parentId = $scope.parentId;
				if (_item != null) {
					$scope.isexist=false;
					$scope.findChild($scope.initialBrowserContext.data,_item,parentId);
					if($scope.isexist){
						alert("name already exist");
						return;
					}
					var _url="";
					var _data={name:_item,id_child:context.id.replace(context.category+"_",""),id_parent:parentId.split("_")[1]};
					if(context.category == "building"){
						_url = sosService.updatebuilding;
						_data={name:_item,id:context.id.replace(context.category+"_","")};
					}else if(context.category == "floor"){
						_url = sosService.updatefloor;
					}else if(context.category == "zone"){
						_url = sosService.updatezone;
					}else if(context.category == "switch"){
						_url = sosService.updateswitch;
					}else if(context.category == "light"){
						_url = sosService.updatelight;
					}
					sosService.postSosAppData({url: _url,data:_data}).then(function (data) {
						if(data.error){
							alert(data.error);
							return;
						}
						$scope.editChild($scope.initialBrowserContext.data,context.id,_item);
						colBrowser.initialContexts = JSON.stringify($scope.initialBrowserContext);
					},function(message){
						$log.error(message);
					});
					//$window.localStorage.initialBrowserContext1 = JSON.stringify($scope.initialBrowserContext);
				}
			});
			colBrowser = document.querySelector('px-context-browser');
			colBrowser.toggleColumnBrowser();
			colBrowser.handlers = {
				getChildren: function(parent, newIndex) {
					return new Promise(function(resolve, reject) {
						var _item = parent.children;
						if(!_item || (_item && _item.length == 0)){
							_item = [{}];
						}
						if(parent.category == "light"){
							_item = undefined;
						}
						resolve({
							data: _item,
							meta: {
								parentId: parent.id
							}
						});
					});
				},
				itemAddHandler: function(evt) {
					var context = evt.currentTarget;
					$scope.pid = angular.element(context).attr("_pid");
					if(!$scope.pid){
						angular.element("#additemmodal").attr("modal-heading","Add Building");
					}else if($scope.pid.indexOf("building") > -1){
						angular.element("#additemmodal").attr("modal-heading","Add Floor");
					}else if($scope.pid.indexOf("floor") > -1){
						angular.element("#additemmodal").attr("modal-heading","Add Zone");
					}else if($scope.pid.indexOf("zone") > -1){
						angular.element("#additemmodal").attr("modal-heading","Add Switch");
					}else if($scope.pid.indexOf("switch") > -1){
						angular.element("#additemmodal").attr("modal-heading","Add Light");
					}
					angular.element("#itemname").val("");
					document.getElementById('additemmodal').modalButtonClicked(evt);
					// var _item = prompt("Add new", "");
				},
				itemUploadHandler: function(evt) {
					$("#floorplan").attr("src","");
					$("#floorplan").css("display","none");
					$("#noimagetxt").css("display","none");
					var _floorid = angular.element(evt.currentTarget).attr("key").split("floor_")[1];
					sosService.getSosAppData({url: sosService.getfloorplan+_floorid}).then(function (data) {
						$("#floorplan").attr("src",sosService.getfloorplan+_floorid);
						$("#floorplan").css("display","block");
						$("#noimagetxt").css("display","none");
					},function(message){
						$("#floorplan").css("display","none");
						$("#noimagetxt").css("display","block");
					});
					$scope.uploadForm = new FormData();
					$scope.uploadForm.append("id", _floorid);
					angular.element("#uploadfilebox").val("");
					document.getElementById('adminaddmodal').modalButtonClicked(evt);
				},
				itemEditHandler: function(context,evt) {
					var parentId=angular.element(evt.currentTarget).parent().parent().siblings("button").attr("_pid");
					$scope.context = context;
					$scope.parentId = parentId;
					//var _item = prompt("Edit name", context.name);
					angular.element("#itemeditname").val(context.name);
					document.getElementById('edititemmodal').modalButtonClicked(evt);
				}
			};
		},function(message){
			$log.error(message);
		});
		$scope.editChild = function(finder,finderId,_name){
			for(var j=0;j<finder.length;j++){
				if(finder[j].id == finderId){
					finder[j].name = _name;
				}else if(finder[j].children && finder[j].children.length>0){
					$scope.editChild(finder[j].children,finderId,_name);
				}
			}
		}
		$scope.addChild = function(finder,finderId,obj){
			for(var j=0;j<finder.length;j++){
				if(finder[j].id == finderId){
					if(!finder[j].children)finder[j].children=[];
					finder[j].children.push(obj);
				}else if(finder[j].children && finder[j].children.length>0){
					$scope.addChild(finder[j].children,finderId,obj);
				}
			}
		}
		$scope.findChild = function(finder,finderName,parentId){
			for(var j=0;j<finder.length;j++){
				if(finder[j].id == parentId){
					if(finder[j].children && finder[j].children.length>0){
						for(var k=0;k<finder[j].children.length;k++){
							if(finder[j].children[k].name.toLowerCase() == finderName.toLowerCase()){
								$scope.isexist = true;
							}
						}
					}
				}else if(finder[j].children && finder[j].children.length>0){
					$scope.findChild(finder[j].children,finderName,parentId);
				}
			}
		}
		$scope.changeToUniqId = function(finder){
			for(var j=0;j<finder.length;j++){
				finder[j].id = finder[j].category+"_"+finder[j].id;
				if(finder[j].children && finder[j].children.length>0){
					$scope.changeToUniqId(finder[j].children);
				}
			}
		}
	}
 ]);
});