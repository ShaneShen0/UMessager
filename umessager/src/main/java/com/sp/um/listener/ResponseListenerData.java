package com.sp.um.listener;



public class ResponseListenerData {
	private Long timeout;
	private IMessageListener listener;
	private Long startTime;
	
	public ResponseListenerData(IMessageListener listener, Long startTime,Long timeout) {
		this.timeout = timeout;
		this.listener = listener;
		this.startTime = startTime;
	}
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	public IMessageListener getListener() {
		return listener;
	}
	public void setListener(IMessageListener listener) {
		this.listener = listener;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	
}
