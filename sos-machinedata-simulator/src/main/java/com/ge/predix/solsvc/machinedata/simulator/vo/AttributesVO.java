package com.ge.predix.solsvc.machinedata.simulator.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AttributesVO {
	@JsonProperty("floor_id")
	private String floor_id;

	public String getFloor_id() {
		return floor_id;
	}

	public void setFloor_id(String floor_id) {
		this.floor_id = floor_id;
	}

	@Override
	public String toString() {
		return "AttributesVO [floor_id=" + floor_id + "]";
	}

	
}
