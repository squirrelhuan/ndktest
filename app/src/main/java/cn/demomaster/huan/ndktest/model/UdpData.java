package cn.demomaster.huan.ndktest.model;

import java.io.Serializable;

public class UdpData implements Serializable{

	private String id;//消息id
	private enums.requestType requestType;
	private Message message;//消息内容

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	public enums.requestType getRequestType() {
		return requestType;
	}
	public void setRequestType(enums.requestType requestType) {
		this.requestType = requestType;
	}

}
