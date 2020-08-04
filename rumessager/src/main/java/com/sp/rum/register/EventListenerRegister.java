package com.sp.rum.register;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;

import com.sp.rum.utils.RUMApplicationContextAware;
import com.sp.rum.utils.RUMessagerUtils;
import com.sp.um.listener.IEventListener;
import com.sp.um.register.IEventListenerRegister;

public class EventListenerRegister implements IEventListenerRegister{


	private static final Logger logger = LoggerFactory.getLogger(EventListenerRegister.class);
	
	private Map<String, EventListenerData> eventAndListenerMap = new ConcurrentHashMap<String, EventListenerData>();
	private RUMApplicationContextAware contextAware;
	

	public void addEventListener(String eventName, IEventListener listener) {
		if(RUMessagerUtils.hasNullParams(eventName,listener)) 
		{
			logger.error("Failed to add event listener, because of there are null params. eventName="+eventName);
			return;
		}
		
		if (eventAndListenerMap.containsKey(eventName)) 
		{
			eventAndListenerMap.get(eventName).addListener(listener);
			return;
		}
		
		Binding binding = BindingBuilder.bind(contextAware.getLocalQueue()).to((DirectExchange)contextAware.getLocalExchange()).with(eventName);
		//Using the event name to bind the local queue.  
		contextAware.getAdmin().declareBinding(binding);
		
		eventAndListenerMap.put(eventName, new EventListenerData(binding, listener));
	}


	public void removeEventListener(String eventName, IEventListener listener) {
		
		if(RUMessagerUtils.hasNullParams(eventName,listener)) 
		{
			logger.error("Failed to remove event listener, because of there are null params. eventName="+eventName);
			return;
		}
		
		EventListenerData eventListenerData = eventAndListenerMap.get(eventName);
		
		if(eventListenerData == null || eventListenerData.getMessageListeners().isEmpty()) {
			logger.error("Failed to remove event listener. This event has not been listened. eventName="+eventName);
			return;
		}
		
		List<IEventListener> listeners = eventListenerData.getMessageListeners();
		listeners.remove(listener);
		
		// Listener list for event with name eventName is empty. Delete binding for this event.
		if(listeners.isEmpty()) {
			contextAware.getAdmin().declareBinding(eventListenerData.getBinding());
			
		}
	}


	public List<IEventListener> getListneresOf(String eventName) {
		if(RUMessagerUtils.hasNullParams(eventName)) 
		{
			logger.error("Failed to get event listeneres, because eventName="+eventName);
			return null;
		}
		EventListenerData data = eventAndListenerMap.get(eventName);
		return data == null ? null : data.getMessageListeners();
	}
	 
	public RUMApplicationContextAware getContextAware() {
		return contextAware;
	}

	public void setContextAware(RUMApplicationContextAware contextAware) {
		this.contextAware = contextAware;
	}



}
