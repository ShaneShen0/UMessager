package com.sp.rum.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sp.rum.utils.RUMessagerUtils;
import com.sp.um.listener.ResponseListenerData;
import com.sp.um.register.IResponseListenerRigister;


public class ResponseMessageListenerRegister implements IResponseListenerRigister{
	private final Map<String, ResponseListenerData> correlationIdAndListenerDataMap = new ConcurrentHashMap<String, ResponseListenerData>();
	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseMessageListenerRegister.class);

	@Override
	public void addResponseListener(String correlationId, ResponseListenerData listenerData) {
		if (RUMessagerUtils.hasNullParams(correlationId,listenerData)) 
		{
			LOGGER.error("Failed to add response messasge listener. There are null params.");
			return;
		
		}
		if(getMessageListener(correlationId)!=null)
		{
			LOGGER.error("The correlationId={} has already been registered.");
			return;
		}
		correlationIdAndListenerDataMap.put(correlationId, listenerData);
	
	}

	@Override
	public void removeReponseListener(String correlationId) {
			correlationIdAndListenerDataMap.remove(correlationId);
	}

	@Override
	public ResponseListenerData getMessageListener(String correlationId) {
		if(correlationId == null || correlationId.isEmpty()) return null;
		return correlationIdAndListenerDataMap.get(correlationId);
	}



}
