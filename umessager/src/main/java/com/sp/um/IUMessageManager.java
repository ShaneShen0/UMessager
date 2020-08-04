package com.sp.um;

import com.sp.um.listener.IEventListener;
import com.sp.um.listener.IMessageListener;


public interface IUMessageManager {
	public void addMessageListener(String messageName,IMessageListener listener );
	public void removeMessageListener(String messageName);
	public void addEventListener(String eventName,IEventListener listener);
	public void removeEventListener(String eventName,IEventListener listener);



}
