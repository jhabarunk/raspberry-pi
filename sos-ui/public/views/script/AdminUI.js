for(var i=0;i<Polymer.telemetry.registrations.length;i++){
	if(Polymer.telemetry.registrations[i].is == "aha-table"){
		Polymer.telemetry.registrations[i]._selectRow = function(row,indexInDisplayedRows){this.fire('before-select',row);var _bool=false;for(var i=0;i<this.selectedRows.length;i++){if(JSON.stringify(this.selectedRows[i])==JSON.stringify(row)){_bool=true;break;}}if(_bool==true){row._selected=false;this.notifyPath('displayedRows.'+indexInDisplayedRows+'._selected',row._selected);this.splice('selectedRows',this.selectedRows.indexOf(row),1);}else{row._selected=true;this.notifyPath('displayedRows.'+indexInDisplayedRows+'._selected',row._selected);this.push('selectedRows',row);}this.set('meta.0.label',"Selected ("+this.selectedRows.length+")");this.fire('after-select',row);}
	}
}
document.getElementById('adminaddmodal').addEventListener('btnModalPositiveClicked', function() {
  angular.element(document.getElementById('adminMenuBar')).scope().addInfo();
});
document.getElementById('admineditmodal').addEventListener('btnModalPositiveClicked', function() {
  angular.element(document.getElementById('adminMenuBar')).scope().editInfo();
});
document.getElementById('admindeletemodal').addEventListener('btnModalPositiveClicked', function() {
  angular.element(document.getElementById('adminMenuBar')).scope().deleteInfo();
});