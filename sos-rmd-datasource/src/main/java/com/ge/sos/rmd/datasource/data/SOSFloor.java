package com.ge.sos.rmd.datasource.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SOSFloor {

	
	private long floor_id;
	private String floor_name;
	private String floor_map;
	private byte[] imagefile;
	
	public byte[] getImagefile() {
		return imagefile;
	}
	public void setImagefile(byte[] imagefile) {
		this.imagefile = imagefile;
	}
	public String getFloor_map() {
		return floor_map;
	}
	public void setFloor_map(String floor_map) {
		this.floor_map = floor_map;
	}
	private List<SOSZone> zones=new ArrayList<SOSZone>();

	public List<SOSZone> getZones() {
		return zones;
	}
	public void setZones(List<SOSZone> zones) {
		this.zones = zones;
	}
	
	public long getFloor_id() {
		return floor_id;
	}
	public void setFloor_id(long floor_id) {
		this.floor_id = floor_id;
	}
	public String getFloor_name() {
		return floor_name;
	}
	public void setFloor_name(String floor_name) {
		this.floor_name = floor_name;
	}
	
	@Override
	public String toString() {
		return "SOSFloor [floor_id=" + floor_id + ", floor_name=" + floor_name
				+ ", floor_map=" + floor_map + ", imagefile="
				+ Arrays.toString(imagefile) + ", zones=" + zones + "]";
	}
}
