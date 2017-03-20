package com.ge.predix.solsvc.machinedata.simulator.vo;

import java.util.ArrayList;
import java.util.List;

public class ObjectVO {
	private Long messageId;
	private List<BodyVO> body = new ArrayList<BodyVO>();
	public Long getMessageId() {
		return messageId;
	}
	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}
	public List<BodyVO> getBody() {
		return body;
	}
	public void setBody(List<BodyVO> body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "EnergyObjectVO [messageId=" + messageId + ", body=" + body
				+ "]";
	}
	
}
