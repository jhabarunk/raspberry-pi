package com.ge.sos.rmd.datasource.data;

import java.util.ArrayList;
import java.util.List;

public class SOSZone {

	private long zone_id;
	private String zone_name;
	private String zone_dimension;
	private String hardware_id;
	
	public String getHardware_id() {
		return hardware_id;
	}
	public void setHardware_id(String hardware_id) {
		this.hardware_id = hardware_id;
	}
	
	private List<SOSSwitch> switchs=new ArrayList<SOSSwitch>();
	
	public List<SOSSwitch> getSwitchs() {
		return switchs;
	}
	
	public void setSwitchs(List<SOSSwitch> switchs) {
		this.switchs = switchs;
	}
	public long getZone_id() {
		return zone_id;
	}
	public void setZone_id(long zone_id) {
		this.zone_id = zone_id;
	}
	public String getZone_name() {
		return zone_name;
	}
	public void setZone_name(String zone_name) {
		this.zone_name = zone_name;
	}
	public String getZone_dimension() {
		return zone_dimension;
	}
	public void setZone_dimension(String zone_dimension) {
		this.zone_dimension = zone_dimension;
	}
}
