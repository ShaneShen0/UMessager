package com.sp.rum.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;

import com.sp.rum.RUMessagerImpl;

public class RUMConfirmCallback implements ConfirmCallback {
	private RUMessagerImpl rumImpl;
	private static final Logger LOGGER = LoggerFactory.getLogger(RUMConfirmCallback.class);
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		if (rumImpl == null) {
			LOGGER.error("RUMMessafeImpl is null");
		}
		if (correlationData != null) {
			    
			Object lock = rumImpl.getCorrelationDataLocks().get(correlationData);
			if (lock == null) return;
			synchronized (lock) {
				if(ack) {
					lock.notifyAll();
					rumImpl.getCorrelationDataLocks().remove(correlationData);
				}
				else {
					LOGGER.error("Failed to send message. CorrelationId={},cause={}",correlationData.getId(),cause);
				}
			}
			
		}
	}
	public RUMessagerImpl getRum() {
		return rumImpl;
	}
	public void setRum(RUMessagerImpl rum) {
		this.rumImpl = rum;
	}
	public RUMessagerImpl getRumImpl() {
		return rumImpl;
	}
	public void setRumImpl(RUMessagerImpl rumImpl) {
		this.rumImpl = rumImpl;
	}
	
}
