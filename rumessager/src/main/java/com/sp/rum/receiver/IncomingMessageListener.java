package com.sp.rum.receiver;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;


public class IncomingMessageListener implements MessageListener{
	private RUMMessageProcessor messageProcessor; 
	private MessageAsynchronousExecutor messageExecutor;
	
	@Override
	public void onMessage(Message message) {
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				messageProcessor.onMessage(message);			
			}
		};
		messageExecutor.executer(runnable);
	}

	public RUMMessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	public void setMessageProcessor(RUMMessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

	public MessageAsynchronousExecutor getMessageExecutor() {
		return messageExecutor;
	}

	public void setMessageExecutor(MessageAsynchronousExecutor messageExecutor) {
		this.messageExecutor = messageExecutor;
	}


}
