package com.ge.sos.rmd.datasource.data;



public class SOSLight {
	
	private long light_id;
	private String light_name;
	private SOSSwitch switchs;
	private double wattage;
	public long getLight_id() {
		return light_id;
	}
	public void setLight_id(long light_id) {
		this.light_id = light_id;
	}
	public String getLight_name() {
		return light_name;
	}
	public void setLight_name(String light_name) {
		this.light_name = light_name;
	}
	public SOSSwitch getSwitchs() {
		return switchs;
	}
	public void setSwitchs(SOSSwitch switchs) {
		this.switchs = switchs;
	}
	public double getWattage() {
		return wattage;
	}
	public void setWattage(double wattage) {
		this.wattage = wattage;
	}

}
