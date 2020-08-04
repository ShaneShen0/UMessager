package com.sp.umessager;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.aspectj.weaver.NewConstructorTypeMunger;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.sp.um.UMessager;
import com.sp.um.UMFactory;
import com.sp.um.listener.IMessageListener;
import com.sp.um.message.UMMessage;
import com.sp.um.message.UMMessageType;

public class RUMSender {
	public final static String QUEUE_NAME="hello";
	public final static String EXCHANGE_NAME="com.sp";
	public final static String TOPIC_EXCHANGE_NAME="topicEx";
	public static void main(String[] args) throws Exception{
			// 1.Inform test
			UMessager uMessager = UMFactory.getUMessgerInstance();
//			UMMessage message = new UMMessage(UUID.randomUUID().toString(), "test", UMMessageType.INFORM, "src", "dest", "hhhhhThis is a inform");
//			uMessager.inform(message);
			uMessager.inform("dest", "testInform", "This is a information");
			
			// 2.sync Request test
//			UMMessage message = new UMMessage(UUID.randomUUID().toString(), "test", UMMessageType.REQUEST, "src", "dest", "222hhhhhThis is a request");
//			message.setReplyTo("amq.rabbitmq.reply-to"); 
//			uMessager.synRequest(message);
			UMMessage responseMessage = uMessager.synRequest("dest", "testSyn", "This is a synRequest");
			System.out.println(responseMessage.toString());
			// 3.async Request
//			UMMessage umMessage = new UMMessage(UUID.randomUUID().toString(),"test",UMMessageType.REQUEST,"src","dest","This a async request");
//			umMessage.setCorrelationId(UUID.randomUUID().toString());
//			umMessage.setReplyTo(umMessage.getSrcId());
//			IMessageListener listener = new IMessageListener() {
//				
//				@Override
//				public void onMessage(UMMessage message) {
//					System.out.println("handle repsonse message"+message.toString());
//				}
//			};
//			uMessager.asynRequest(umMessage, listener);
			uMessager.asynRequest("dest", "testAsyn", new IMessageListener() {
				
				@Override
				public void onMessage(UMMessage message) {
					System.out.println("Receive a asyn request "+ message.toString());
					
				}
			}, "This is a asynRequest");
			// 4. Publish event

			uMessager.publishEvent("eventtest", "This is a event test");
			

		}

}
