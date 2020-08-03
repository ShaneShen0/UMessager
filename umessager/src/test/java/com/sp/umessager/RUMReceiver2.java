package com.sp.umessager;

import java.util.UUID;

import com.sp.um.UMessager;
import com.sp.um.UMFactory;
import com.sp.um.listener.IEventListener;
import com.sp.um.message.UMMessage;
import com.sp.um.message.UMMessageType;

public class RUMReceiver2 {
	public final static String QUEUE_NAME="hello";
	public final static String EXCHANGE_NAME="com.sp";
	public final static String TOPIC_EXCHANGE_NAME="topicEx";
	public static void main(String[] args) throws Exception{

			UMessager uMessager = UMFactory.getUMessgerInstance();
//			uMessager.addMessageListener("test", new IMessageListener() {
//
//				@Override
//				public void onMessage(UMMessage message) {
//					System.out.println("RUMReveiver :"+message.toString());
//					UMMessage responseMessage = new UMMessage();
//					responseMessage.setMid(UUID.randomUUID().toString());
//					responseMessage.setName(message.getName());
//					responseMessage.setSrcId("dest");
//					responseMessage.setDestId(message.getReplyTo());
//					responseMessage.setCorrelationId(message.getCorrelationId());
//					responseMessage.setType(UMMessageType.RESPONSE);
//					responseMessage.setContent("This is a resposne");
//					uMessager.response(message, responseMessage);
//				}
//				
//			
//			});
//			uMessager.addMessageListener("test", new IMessageListener() {
//
//				@Override
//				public void onMessage(UMMessage message) {
//					System.out.println("RUMReveiver :"+message.toString()+"\n");
//					UMMessage responseMessage = new UMMessage();
//					responseMessage.setMid(UUID.randomUUID().toString());
//					responseMessage.setName(message.getName());
//					responseMessage.setCorrelationId(message.getCorrelationId());
//					responseMessage.setSrcId("dest");
//					responseMessage.setDestId(message.getReplyTo());
//					responseMessage.setCorrelationId(message.getCorrelationId());
//					responseMessage.setType(UMMessageType.RESPONSE);
//					responseMessage.setContent("This is a resposne");
//					uMessager.response(message, responseMessage);
//				}
//				
//			
//			});
			
			//Event receiver
			uMessager.addEventListener("eventtest", new IEventListener() {
				
				@Override
				public void onMessage(UMMessage message) {
					System.out.println("Receiver 2 Receive a event "+message.getName());
					
				}
			});
		}

}
