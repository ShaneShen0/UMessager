package com.sp.umessager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sender {
	private final static String QUEUE_NAME="hello";
	private final static String QUEUE_NAME2="hello2";
	private final static String EXCHANGE_NAME="com.sp";
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		for(int i=1;i<10;i++) {			
			channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME, null, ("[hello]This is "+i+"th"+" message..").getBytes());
			channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME2, null, ("[hello2]This is "+i+"th"+" message..").getBytes());
		}
	}
}
