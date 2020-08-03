package com.sp.umessager;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.AMQP.Basic.Deliver;

public class Reciver {
	private final static String QUEUE_NAME="hello";
	private final static String QUEUE_NAME2="hello2";
	private final static String EXCHANGE_NAME="com.sp";
	public final static String TOPIC_EXCHANGE_NAME="topicEx";
	public static void main(String[] args)throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(QUEUE_NAME,false,false,false,null);
		channel.queueDeclare(QUEUE_NAME2,false,false,false,null);
		//declare direct exchange
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "hello");
		channel.queueBind(QUEUE_NAME2, EXCHANGE_NAME, "hello2");
		//declare topic exchange
		channel.exchangeDeclare(TOPIC_EXCHANGE_NAME, "topic");
		String randRedQ = channel.queueDeclare().getQueue();
		channel.queueBind(randRedQ, TOPIC_EXCHANGE_NAME, "com.sp.server.*");
		System.out.println("[*] waiting for message, To exit press CTRL+C");
		DeliverCallback diDeliver1 = (consumerTag,delivery)->{
			String message = new String(delivery.getBody(),"utf-8");
			System.out.println("[x] received '"+message+"'");
			try {
				doWork(message);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				System.out.println("[x] done.[Deliver 1]");
//				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}
			
		};
		
		DeliverCallback diDeliver2 = (consumerTag,delivery)->{
			String message = new String(delivery.getBody(),"utf-8");
			System.out.println("[x] received '"+message+"'");
			try {
				doWork2(message);

			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				System.out.println("[x] done.[Deliver 2]");
//				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}
			
		};
		boolean autoAck=false;
		channel.basicQos(1);
		channel.basicConsume(QUEUE_NAME,autoAck,diDeliver1,consumerTag->{});
		
		channel.basicConsume(QUEUE_NAME2,autoAck,diDeliver2,consumerTag->{});
		System.out.println("end");
		return;
	}
	private static void doWork(String message) throws InterruptedException {
		for (char ch : message.toCharArray()) {
			if(ch=='.') Thread.sleep(1000);
		}
		
	}
	private static void doWork2(String message) throws InterruptedException {
		for (char ch : message.toCharArray()) {
//			if(ch=='.') Thread.sleep(10);
		}
		
	}	
}
