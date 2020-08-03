package com.sp.um.register;

import java.util.List;

import com.sp.um.listener.IEventListener;




public interface IEventListenerRegister {
	void addEventListener(String eventName, IEventListener listener);
	void removeEventListener(String eventName,IEventListener listener);
	List<IEventListener> getListneresOf(String eventName);
}
