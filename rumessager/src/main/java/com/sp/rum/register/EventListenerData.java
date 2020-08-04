package com.sp.rum.register;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.amqp.core.Binding;

import com.sp.um.listener.IEventListener;




public final class EventListenerData {
	
	private Binding binding;
	
	private List<IEventListener> messageListeners = new LinkedList<IEventListener>();
	
	public EventListenerData(Binding binding,IEventListener listener) {
		 this.binding = binding;
		 this.messageListeners.add(listener);
	}
	
	public void addListener(IEventListener listener) {
		if(listener!=null)
			messageListeners.add(listener);
	}
	
	public Binding getBinding() {
		return binding;
	}
	
	public void setBinding(Binding binding) {
		this.binding = binding;
	}
	
	public List<IEventListener> getMessageListeners() {
		return Collections.unmodifiableList(messageListeners);
	}
	
	public void removeListener (IEventListener listener) {
		if(!messageListeners.isEmpty()) {
			Iterator<IEventListener> iterator = messageListeners.iterator();
			while (iterator.hasNext()) {
				if(iterator.next().equals(listener)) {
					iterator.remove();
				}
				
			}
			
		}
	}
	
}
