package com.sp.rum.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sp.rum.utils.RUMessagerUtils;
import com.sp.um.listener.IMessageListener;
import com.sp.um.register.IMessageListenerRegister;


public class InformAndRequestMessageListenerRegister implements IMessageListenerRegister {
	private static final Logger logger = LoggerFactory.getLogger(InformAndRequestMessageListenerRegister.class);
	
	private final Map<String, IMessageListener> messageListenerMap = new ConcurrentHashMap<String, IMessageListener>();
	
	@Override
	public void addMessageListener(String messageName, IMessageListener listener) {
		if(RUMessagerUtils.hasNullParams(messageName,listener)) {
			logger.error("Failed to register inform message listener, because of null parameters. massageName="+messageName);
		}
		IMessageListener previousListener = messageListenerMap.putIfAbsent(messageName, listener);
		if (previousListener != null) {
			logger.error("There is already MessageListener for message with name {}",messageName);
		}
	}

	@Override
	public void removeMessageListener(String messageName) {
		messageListenerMap.remove(messageName);	
	}

	@Override
	public IMessageListener getMessageListener(String messageName) {
		return messageListenerMap.get(messageName);
	}

}
