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

public class RPCServer implements Closeable{
	private  static final  String EXCHANGE="com.sp";
	private static final String QUEUE="server";

	private Connection connection;
	private Channel channel;
	public RPCServer() {
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
		RPCServer server = new RPCServer();
		try {
				server.execute();		
		} catch (Exception e) {		
			e.printStackTrace();
		}
	}
	public void execute() throws IOException, InterruptedException {
		
		String tag = channel.basicConsume(QUEUE,true,(consumerTag,delivery)->{
			String correlationId = delivery.getProperties().getCorrelationId();
			String input = new String(delivery.getBody(),"utf-8");
			System.out.println("[x] server: reciev input "+input);
			String result = String.valueOf(fib(new Integer(input)));
			channel.basicPublish(EXCHANGE, delivery.getProperties().getReplyTo(), new BasicProperties.Builder().correlationId(correlationId).build(), result.getBytes());
			System.out.println("[.] server: response  "+result);
		},consumerTag->{});

	}
	public int fib(int input) {
		if(input == 0) return 0;
		if(input == 1) return 1;
		return fib(input-1)+fib(input-2);
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
