package com.sp.um.register;

import com.sp.um.listener.ResponseListenerData;

public interface IResponseListenerRigister {
	/**
	 * Bind message with name messageName and listener together. 
	 * @param messageName the messageName of a message.
	 * @param listener a message listener.
	 */
	void addResponseListener(String correlationId,ResponseListenerData listenerData);
	
	void removeReponseListener(String correlationId);
	
	ResponseListenerData getMessageListener(String correlationId);
}
