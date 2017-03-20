package com.ge.predix.solsvc.dataingestion.service.type;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BodyVO {
	@JsonProperty("name")
	private String name;
	@JsonProperty("datapoints")
	private ArrayList<ArrayList<Long>> datapoints = new ArrayList<ArrayList<Long>>();
	@JsonProperty("attributes")
	private AttributesVO attributesVO;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<ArrayList<Long>> getDatapoints() {
		return datapoints;
	}
	public void setDatapoints(ArrayList<ArrayList<Long>> datapoints) {
		this.datapoints = datapoints;
	}
	public AttributesVO getAttributesVO() {
		return attributesVO;
	}
	public void setAttributesVO(AttributesVO attributesVO) {
		this.attributesVO = attributesVO;
	}
	@Override
	public String toString() {
		return "Body [name=" + name + ", datapoints=" + datapoints
				+ ", attributesVO=" + attributesVO + "]";
	}
	
	
}
