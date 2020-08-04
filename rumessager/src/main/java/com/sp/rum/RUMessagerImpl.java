package com.sp.rum;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.relation.Relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.sp.rum.register.EventListenerRegister;
import com.sp.rum.register.InformAndRequestMessageListenerRegister;
import com.sp.rum.register.ResponseMessageListenerRegister;
import com.sp.rum.utils.RUMApplicationContextAware;
import com.sp.rum.utils.RUMessagerUtils;
import com.sp.um.UMessager;
import com.sp.um.common.UMPropertyPlaceHolderConfigurer;
import com.sp.um.listener.IEventListener;
import com.sp.um.listener.IMessageListener;
import com.sp.um.listener.ResponseListenerData;
import com.sp.um.message.UMMessage;



public class RUMessagerImpl implements UMessager{
	private InformAndRequestMessageListenerRegister iAndRMessageListenerRegister;
	private ResponseMessageListenerRegister asyncResponseMessageListenerRegister;
	private EventListenerRegister eventListenerRegister;
	private static final Logger LOGGER = LoggerFactory.getLogger(RUMessagerImpl.class);
	private RUMApplicationContextAware contextAware;
	private RabbitTemplate template;
	private final ConcurrentHashMap<CorrelationData, Object> correlationDataLocks = new ConcurrentHashMap<CorrelationData, Object>();


	@Override
	public void addMessageListener(String messageName, IMessageListener listener) {
		if (RUMessagerUtils.hasNullParams(messageName,listener)) {
			LOGGER.error("The message listener addition failed, because of the null parameter. messageName={}",messageName);
		}
		iAndRMessageListenerRegister.addMessageListener(messageName, listener);
	}

	@Override
	public void removeMessageListener(String messageName) {
		iAndRMessageListenerRegister.removeMessageListener(messageName);
	}

	@Override
	public synchronized void addEventListener(String eventName, IEventListener listener) {
		eventListenerRegister.addEventListener(eventName, listener);

	}

	@Override
	public void removeEventListener(String eventName, IEventListener listener) {
		eventListenerRegister.removeEventListener(eventName, listener);
	}
	
	public Boolean inform (UMMessage message) {
		return inform(message,UMPropertyPlaceHolderConfigurer.getDefaultPublisherConfirmTimout());
	}
	
	@Override
	public Boolean inform (UMMessage message,Long publisherConfirmationTimout) {
		if(RUMessagerUtils.hasNullParams(message)) {
			LOGGER.error("Failed to send notification message.Message is null");
			return false;
		}
		Message amqpMessage = RUMessagerUtils.convertToAMQPMessage(message);
		if (amqpMessage == null) {
			LOGGER.error("Failed to send notification message. Convert UMMessage to AMQPMessage unsuccessfully. message content={}",message.getContent());
			return false;
		}
		if (publisherConfirmationTimout == null) {
			publisherConfirmationTimout = UMPropertyPlaceHolderConfigurer.getDefaultPublisherConfirmTimout();
		}
		Object lock = new Object();
		CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
		correlationDataLocks.put(correlationData, lock);
		synchronized (lock) {
			try {
				template.send(contextAware.getLocalExchange().getName(), message.getDestId(),amqpMessage,correlationData);
				Long startTime = System.currentTimeMillis();
				lock.wait(publisherConfirmationTimout);
				Long endTime = System.currentTimeMillis();
				if(endTime - startTime >= publisherConfirmationTimout)
				{
					// This thread is not notified by confirmation callback in time.
					LOGGER.error("The publisher confirmation of notification message times out. message content={}",message.getContent());
					return false;
				}
				else 
				{
					LOGGER.info("Succeeded to send notification message {}",message.toString());
					return true;
				}
			}
			catch (Exception e)
			{
				LOGGER.error(e.getLocalizedMessage(),e);
				return false;
			}
		}
		
	}

	@Override
	public UMMessage synRequest(UMMessage message) {
		return synRequest(message,UMPropertyPlaceHolderConfigurer.getDefaultSyncResponseTimeout());
	}

	@Override
	public UMMessage synRequest(UMMessage message, Long timeout) {
		if(RUMessagerUtils.hasNullParams(message)) 
		{
			LOGGER.error("Failed to send synchronous request message.Message is null");
			return null;
		}
		
		//1.Convert request message
		Message amqpMessage = RUMessagerUtils.convertToAMQPMessage(message);
		if (amqpMessage == null) 
		{
			LOGGER.error("Failed to send synchronous request message. Convert UMMessage to AMQPMessage unsuccessfully.UMMessage={}",message.toString());
			return null;
		}
		//2.set reply timeout
		if (timeout == null) {
			timeout = UMPropertyPlaceHolderConfigurer.getDefaultSyncResponseTimeout();
		}
		template.setReplyTimeout(timeout);
		//3. use user-defined correlation id
		template.setUserCorrelationId(true);
		LOGGER.info("Send a synchronous request messsage={}",RUMessagerUtils.printAMQPMessage(amqpMessage));
		//4.Send request message
		Message amqpResponseMessage = template.sendAndReceive(contextAware.getLocalExchange().getName(), message.getDestId(), amqpMessage);
		if (amqpResponseMessage==null)
		{
			LOGGER.error("The response message times out for the request message with mid={}",message.getMid());
			return null;
		}
		//5.Convert response message
		UMMessage umResposeMessage = RUMessagerUtils.convertToUMMessage(amqpResponseMessage);
		if(umResposeMessage==null)
		{
			
			LOGGER.error("Failed to convert response message");
		}
		else 
		{
			
			LOGGER.info("Receive the response message={}",RUMessagerUtils.printAMQPMessage(amqpResponseMessage));
		}
		return umResposeMessage;
	}
	
	public void asynRequest(UMMessage message, IMessageListener listener) {
		 asynRequest(message, listener,UMPropertyPlaceHolderConfigurer.getDefaultAsyncResponseTimeout());
		
	}
	
	@Override
	public void asynRequest(UMMessage message, IMessageListener listener, Long timeout) {
		 if(RUMessagerUtils.hasNullParams(message,listener)) 
		 {
			 LOGGER.error("Failed to send a aysnchronous request. There are null params");
			 return;
		 }
		 if (timeout == null) {
			timeout = UMPropertyPlaceHolderConfigurer.getDefaultAsyncResponseTimeout();
		}
		 // 1.convert to AMQPMessage
		 Message amqpMessage = RUMessagerUtils.convertToAMQPMessage(message);
		 if(amqpMessage == null)
		 {
			 LOGGER.error("Convert UMMessage to AMQPMessage unsuccessfully. UMMessage={}",message.toString());
			 return;
		 }
		 // 2.send message
		 template.send(contextAware.getLocalExchange().getName(),message.getDestId(),amqpMessage);
		 LOGGER.info("Send a asynchronous request message "+RUMessagerUtils.printAMQPMessage(amqpMessage));
		 
		 // 3.add listener to register 
		 ResponseListenerData listenerData = new ResponseListenerData(listener,System.currentTimeMillis(),timeout);
		 asyncResponseMessageListenerRegister.addResponseListener(message.getCorrelationId(), listenerData);
		 
	}
	
	@Override
	public void response(UMMessage requestMessage,UMMessage responseMessage) {
		
		if(RUMessagerUtils.hasNullParams(requestMessage,responseMessage))
		{
			LOGGER.error("There are null params");
			return;
		}
		Message amqpResponseMessage = RUMessagerUtils.convertToAMQPMessage(responseMessage);
		if(amqpResponseMessage == null) {
			LOGGER.error("Failed to send a response message. Convert response UMMessage to AMQPMessage one unsuccessfully. Request messge={}",
					requestMessage.toString());
			return;
		}
		
		String replyTo = requestMessage.getReplyTo();
		if (replyTo.startsWith("amq.rabbitmq.reply-to"))
		{
			// use direct reply-to
			template.send(replyTo, amqpResponseMessage);
		}
		else 
		{
			template.send(contextAware.getLocalExchange().getName(),replyTo,amqpResponseMessage);
		}
		LOGGER.info("Send a response message={}",responseMessage.toString());
	}
	
	@Override
	public void publishEvent(String eventName, Serializable eventContent) {
		if(RUMessagerUtils.hasNullParams(eventName,eventContent))
		{
			LOGGER.error("Failed to publish event. There are null params.");
			return;
		}
		UMMessage eventMessage = RUMessagerUtils.generateEventMessage(eventName,eventContent);
		Message amqpEventMessage = RUMessagerUtils.convertToAMQPMessage(eventMessage);
		if (amqpEventMessage == null) {
			LOGGER.error("Failed to publish event={}. The event message generation failed",eventName);
			return;
		}
		template.send(UMPropertyPlaceHolderConfigurer.getExchangeName(),eventName,amqpEventMessage);
		LOGGER.info("Publish event {}, event message={}",eventName,RUMessagerUtils.printAMQPMessage(amqpEventMessage));
	}
	
	@Override
	public Boolean inform(String destId, String messageName, Serializable content) {

		return inform(destId, messageName, null,null, content);
	}

	@Override
	public Boolean inform(String destId, String messageName, Long timeout, Serializable content) {

		return inform(destId, messageName, timeout, null, content);
	}

	@Override
	public Boolean inform(String destId, String messageName, Long timeout, Boolean persistent, Serializable content) {
		
		return inform(RUMessagerUtils.generateInformUMMessage(destId, messageName, timeout, persistent, content),timeout);
	}

	@Override
	public UMMessage synRequest(String destId, String messageName, Serializable content) {

		return synRequest(destId, messageName, null, null, content);
	}

	@Override
	public UMMessage synRequest(String destId, String messageName, Long timeout, Serializable content) {
		return synRequest(destId, messageName, timeout, null, content);
	}

	@Override
	public UMMessage synRequest(String destId, String messageName, Long timeout, Boolean persistent,
			Serializable content) {
		
		return synRequest(RUMessagerUtils.generateSynRequestUMMessage(destId, messageName,timeout, persistent, content),timeout);
	}

	@Override
	public void asynRequest(String destId, String messageName, IMessageListener listener, Serializable content) {
		
		asynRequest(destId, messageName, listener, content, null, null);
	}

	@Override
	public void asynRequest(String destId, String messageName, IMessageListener listener, Serializable content,
			Long timeout) {
		
		asynRequest(destId, messageName, listener, content, timeout, null);
	}

	@Override
	public void asynRequest(String destId, String messageName, IMessageListener listener, Serializable content,
			Long timeout, Boolean persistent) {
		
		asynRequest(RUMessagerUtils.generateAsynRequestUMMessage(destId, messageName, content,
			timeout, persistent), listener, timeout);
	}

	@Override
	public void response(UMMessage requestMessage, Serializable responseContent) {
		response(requestMessage, RUMessagerUtils.generateResponseUMMessage(requestMessage,responseContent));
	}

	public InformAndRequestMessageListenerRegister getiAndRMessageListenerRegister() {
		return iAndRMessageListenerRegister;
	}

	public void setiAndRMessageListenerRegister(InformAndRequestMessageListenerRegister iAndRMessageListenerRegister) {
		this.iAndRMessageListenerRegister = iAndRMessageListenerRegister;
	}

	public EventListenerRegister getEventListenerRegister() {
		return eventListenerRegister;
	}

	public void setEventListenerRegister(EventListenerRegister eventListenerRegister) {
		this.eventListenerRegister = eventListenerRegister;
	}

	public ResponseMessageListenerRegister getAsyncResponseMessageListenerRegister() {
		return asyncResponseMessageListenerRegister;
	}

	public void setAsyncResponseMessageListenerRegister(
			ResponseMessageListenerRegister asyncResponseMessageListenerRegister) {
		this.asyncResponseMessageListenerRegister = asyncResponseMessageListenerRegister;
	}

	public RUMApplicationContextAware getContextAware() {
		return contextAware;
	}

	public void setContextAware(RUMApplicationContextAware contextAware) {
		this.contextAware = contextAware;
	}

	public RabbitTemplate getTemplate() {
		return template;
	}

	public void setTemplate(RabbitTemplate template) {
		this.template = template;
	}

	public ConcurrentHashMap<CorrelationData, Object> getCorrelationDataLocks() {
		return correlationDataLocks;
	}
	
	
}

	






