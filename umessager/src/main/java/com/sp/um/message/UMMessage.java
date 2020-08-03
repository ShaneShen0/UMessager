package com.sp.um.message;

import java.io.Serializable;

public final class UMMessage {
	private String mid;
	private String name;
	private UMMessageType type;
	//The source end id, essentially is the routing key of source's queue.
	private String srcId;
	//The destination end id, essentially is the routing key of destination's queue.
	private String destId;
	private Serializable content;
	//The receiver id for response message of the asynchronous request.
	private String replyTo;
	private Long expiration;
	private Boolean persistent;
	private String correlationId;
	
	


	@Override
	public String toString() {
		return "Message [type="+type.name()+", mid=" + mid + ", name=" + name + ", srcId=" + srcId + ", destId=" + destId
				+", correlationId="+correlationId+", replyTo="+ replyTo +", content=" + content + "]";
	}



	public UMMessage(){}

	public UMMessage(String mid, String name, UMMessageType type, String srcId, String destId, Serializable content,
			Long timeout) {
		super();
		this.mid = mid;
		this.name = name;
		this.type = type;
		this.srcId = srcId;
		this.destId = destId;
		this.content = content;
		this.expiration = timeout;
	}





	public UMMessage(String mid, String name, UMMessageType type, String srcId, String destId, Serializable content,
			String replyTo, Long expiration, Boolean persistent, String correlationId) {
		this.mid = mid;
		this.name = name;
		this.type = type;
		this.srcId = srcId;
		this.destId = destId;
		this.content = content;
		this.replyTo = replyTo;
		this.expiration = expiration;
		this.persistent = persistent;
		this.correlationId = correlationId;
	}



	public UMMessage(String mid, String name, UMMessageType type, String srcId, String destId, Serializable content,
			String responseReceiverId, String correlationMid) {
		super();
		this.mid = mid;
		this.name = name;
		this.type = type;
		this.srcId = srcId;
		this.destId = destId;
		this.content = content;
		this.replyTo = responseReceiverId;
		this.correlationId = correlationMid;
	}





	public UMMessage(String mid, String name, UMMessageType type, String srcId, String destId, Serializable content) {
		super();
		this.mid = mid;
		this.name = name;
		this.type = type;
		this.srcId = srcId;
		this.destId = destId;
		this.content = content;
	}





	public String getMid() {
		return mid;
	}





	public void setMid(String mid) {
		this.mid = mid;
	}





	public String getName() {
		return name;
	}





	public void setName(String name) {
		this.name = name;
	}





	public UMMessageType getType() {
		return type;
	}





	public void setType(UMMessageType type) {
		this.type = type;
	}





	public String getSrcId() {
		return srcId;
	}





	public void setSrcId(String srcId) {
		this.srcId = srcId;
	}





	public String getDestId() {
		return destId;
	}





	public void setDestId(String destId) {
		this.destId = destId;
	}





	public Serializable getContent() {
		return content;
	}





	public void setContent(Serializable content) {
		this.content = content;
	}





	public String getResponseReceiverId() {
		return replyTo;
	}





	public void setResponseReceiverId(String responseReceiverId) {
		this.replyTo = responseReceiverId;
	}









	public String getReplyTo() {
		return replyTo;
	}





	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}





	public Long getExpiration() {
		return expiration;
	}





	public void setExpiration(Long expiration) {
		this.expiration = expiration;
	}





	public String getCorrelationId() {
		return correlationId;
	}





	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}





	public Boolean getPersistent() {
		return persistent;
	}





	public void setPersistent(Boolean persistent) {
		this.persistent = persistent;
	}









}
