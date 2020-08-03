package com.sp.umessager;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.RpcClient;

public class RPCClient implements Closeable{
	private  static final  String EXCHANGE="com.sp";
	private static final String QUEUE="client";
	private static final String RK="client";
	private static final String SERVER_RK = "server";
	private Connection connection;
	private Channel channel;
	public RPCClient() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE, "direct");
			channel.queueDeclare(QUEUE, false, true, true, null);
			channel.queueBind(QUEUE, EXCHANGE, QUEUE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		RPCClient client = new RPCClient();
		try {
			System.out.println("[x] request fib(13)");
			String reponse = client.remoteCall("13");
			System.out.println("[.] response: "+reponse);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String remoteCall(String message) throws IOException, InterruptedException {
		String correlationId = UUID.randomUUID().toString();
		BasicProperties properties = new BasicProperties().builder().correlationId(correlationId).replyTo(RK).build();
		BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(1);
		channel.basicPublish(EXCHANGE, SERVER_RK, properties, message.getBytes());
		String tag = channel.basicConsume(QUEUE,true,(consumerTag,delivery)->{
			if(delivery.getProperties().getCorrelationId().equals(correlationId)) {
				blockingQueue.offer(new String(delivery.getBody(), "utf-8"));
			}
		},consumerTag->{});
		String result = blockingQueue.take();
		channel.basicCancel(tag);
		return result;
	}
	
	@Override
	public void close() throws IOException {
		if(channel!=null) {
			try {
				connection.close();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		
	}
}
