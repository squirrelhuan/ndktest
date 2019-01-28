package cn.demomaster.huan.ndktest.model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable{

	private String id;
	private String messageId;//用于返回消息发送结果
	private String sendUserId;
	private String reciveGroupId;
	private Date sendDateTime = new Date();
	private String content;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
	}

	public String getReciveGroupId() {
		return reciveGroupId;
	}

	public void setReciveGroupId(String reciveGroupId) {
		this.reciveGroupId = reciveGroupId;
	}

	public Date getSendDateTime() {
		return sendDateTime;
	}

	public void setSendDateTime(Date sendDateTime) {
		this.sendDateTime = sendDateTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
