package com.sp.umessager;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.AMQP.Basic.Deliver;

public class Reciever2 {
	private final static String QUEUE_NAME="hello";
	public final static String TOPIC_EXCHANGE_NAME="topicEx";
	private final static String EXCHANGE_NAME="com.sp";
	public static void main(String[] args)throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(QUEUE_NAME,false,false,false,null);
		
		//declare exchange
		channel.exchangeDeclare(TOPIC_EXCHANGE_NAME, "topic");
		String randBLKQ = channel.queueDeclare().getQueue();
		channel.queueBind(randBLKQ, TOPIC_EXCHANGE_NAME, "com.sp.master.*");
		System.out.println("[*] waiting for message, To exit press CTRL+C");
		DeliverCallback diDeliver = (consumerTag,delivery)->{
			String message = new String(delivery.getBody(),"utf-8");
			System.out.println("[x] received '"+message+"'");
			try {
				doWork(message);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				System.out.println("[x] done");
//				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}
			
		};
		boolean autoAck=true;
		channel.basicConsume(randBLKQ,autoAck,diDeliver,consumerTag->{});
	}
	private static void doWork(String message) throws InterruptedException {
		for (char ch : message.toCharArray()) {
			if(ch=='.') Thread.sleep(1000);
		}
		
	}
			
}
