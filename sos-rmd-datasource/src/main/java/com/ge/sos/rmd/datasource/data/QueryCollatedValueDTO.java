package com.ge.sos.rmd.datasource.data;

import java.util.ArrayList;
import java.util.List;

public class QueryCollatedValueDTO {
	private String floor_id;
	private String interval;
	private List<String> zoneNames=new ArrayList<String>();;
	public String getFloor_id() {
		return floor_id;
	}
	public void setFloor_id(String floor_id) {
		this.floor_id = floor_id;
	}
	public String getInterval() {
		return interval;
	}
	public void setInterval(String interval) {
		this.interval = interval;
	}
	public List<String> getZoneNames() {
		return zoneNames;
	}
	public void setZoneNames(List<String> zoneNames) {
		this.zoneNames = zoneNames;
	}
}
