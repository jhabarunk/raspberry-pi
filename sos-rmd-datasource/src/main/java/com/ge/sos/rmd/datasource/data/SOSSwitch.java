package com.ge.sos.rmd.datasource.data;
import java.util.ArrayList;
import java.util.List;


public class SOSSwitch {
	
	private long switch_id;
	private String switch_name;
	private List<SOSLight> light=new ArrayList<SOSLight>();
	
	public List<SOSLight> getLights() {
		return light;
	}
	public void setLights(List<SOSLight> light) {
		this.light = light;
	}
	@Override
	public String toString() {
		return "SOSSwitch [switch_id=" + switch_id + ", switch_name="
				+ switch_name + ", light=" + light + "]";
	}
	public long getSwitch_id() {
		return switch_id;
	}
	public void setSwitch_id(long switch_id) {
		this.switch_id = switch_id;
	}
	public String getSwitch_name() {
		return switch_name;
	}
	public void setSwitch_name(String switch_name) {
		this.switch_name = switch_name;
	}
}
