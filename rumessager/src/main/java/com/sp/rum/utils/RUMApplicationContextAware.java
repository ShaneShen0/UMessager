package com.sp.rum.utils;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import com.sp.um.common.UMApplicationContextAware;

public class RUMApplicationContextAware extends UMApplicationContextAware {
	private Exchange localExchange;
	private Queue localQueue;
	private RabbitAdmin admin;

	@Override
	public void setLocalResource() {
		localExchange = context.getBean("directExchange",DirectExchange.class);
		localQueue = context.getBean("localQueue",Queue.class);
		admin = context.getBean("admin",RabbitAdmin.class);
	}
	
	public Exchange getLocalExchange() {
		return localExchange;
	}
	public void setLocalExchange(Exchange localExchange) {
		this.localExchange = localExchange;
	}
	public Queue getLocalQueue() {
		return localQueue;
	}
	public void setLocalQueue(Queue localQueue) {
		this.localQueue = localQueue;
	}
	public RabbitAdmin getAdmin() {
		return admin;
	}
	public void setAdmin(RabbitAdmin admin) {
		this.admin = admin;
	}
	

}
