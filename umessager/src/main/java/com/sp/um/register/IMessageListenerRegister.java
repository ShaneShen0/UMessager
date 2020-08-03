package com.sp.um.register;

import com.sp.um.listener.IMessageListener;

public interface IMessageListenerRegister {
	/**
	 * Bind message with name messageName and listener together. 
	 * @param messageName the messageName of a message.
	 * @param listener a message listener.
	 */
	void addMessageListener(String messageName,IMessageListener listener);
	
	void removeMessageListener(String messageName);
	
	IMessageListener getMessageListener(String messageName);
}
