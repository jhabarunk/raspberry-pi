package com.ge.sos.rmd.datasource.data;

import java.util.ArrayList;
import java.util.List;

public class SOSBuilding {
	
	
	private long building_id;
	private String building_name;
	private String building_address;
	private String building_city;
	private String building_country;
	private long building_zipcode;
	private List<SOSFloor> floors=new ArrayList<SOSFloor>();
	public List<SOSFloor> getFloors() {
		return floors;
	}

	public void setFloors(List<SOSFloor> floors) {
		this.floors = floors;
	}

	public long getBuilding_id() {
		return building_id;
	}

	public void setBuilding_id(long building_id) {
		this.building_id = building_id;
	}

	public String getBuilding_name() {
		return building_name;
	}

	public void setBuilding_name(String building_name) {
		this.building_name = building_name;
	}

	public String getBuilding_address() {
		return building_address;
	}

	public void setBuilding_address(String building_address) {
		this.building_address = building_address;
	}

	public String getBuilding_city() {
		return building_city;
	}

	public void setBuilding_city(String building_city) {
		this.building_city = building_city;
	}

	public String getBuilding_country() {
		return building_country;
	}

	public void setBuilding_country(String building_country) {
		this.building_country = building_country;
	}

	public long getBuilding_zipcode() {
		return building_zipcode;
	}

	public void setBuilding_zipcode(long building_zipcode) {
		this.building_zipcode = building_zipcode;
	}

	@Override
	public String toString() {
		return "SOSBuilding [building_id=" + building_id + ", building_name="
				+ building_name + ", building_address=" + building_address
				+ ", building_city=" + building_city + ", building_country="
				+ building_country + ", building_zipcode=" + building_zipcode
				+ ", floors=" + floors + "]";
	}
	
	

}
