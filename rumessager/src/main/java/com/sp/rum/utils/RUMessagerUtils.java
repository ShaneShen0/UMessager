package com.sp.rum.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;

import com.sp.um.common.UMPropertyPlaceHolderConfigurer;
import com.sp.um.message.UMMessage;
import com.sp.um.message.UMMessageType;


public class RUMessagerUtils {
	private  static final String  SRC_ID="srcId";
	private static final String MESSAGE_NAME="messageName";
	private static final String DEST_ID="destId";
	private static final Logger LOGGER = LoggerFactory.getLogger(RUMessagerUtils.class);

	
	private static Map<String, UMMessageType> nameAndMessageTypeMap = new HashMap<String, UMMessageType>();
	static {
		nameAndMessageTypeMap.put(UMMessageType.INFORM.toString(), UMMessageType.INFORM);
		nameAndMessageTypeMap.put(UMMessageType.REQUEST.toString(), UMMessageType.REQUEST);
		nameAndMessageTypeMap.put(UMMessageType.RESPONSE.toString(), UMMessageType.RESPONSE);
		nameAndMessageTypeMap.put(UMMessageType.EVENT.toString(), UMMessageType.EVENT);
	}

	/**
	 * Judge if there are null parameters in parameter list.
	 * @param objects 
	 * @return
	 */
	public static boolean hasNullParams(Object... objects) {
		if(objects == null) return true;
		for (Object object : objects) {
			if (object == null) {
				return true;
			}
		}
		return false;
	}
	public static String printAMQPMessage(Message amqpMessage) {
		if (amqpMessage == null) {
			return null;
		}
		MessageProperties properties = amqpMessage.getMessageProperties();
		if (properties==null) {
			LOGGER.error("The AMQPMessage to print has no MessageProperties");
			return null;
		}
		Map<String, Object> headers = properties.getHeaders();
		if (headers==null) {
			LOGGER.error("The  MessageProperties of the AMQPMessage have no Headers");
			return null;
		}
		StringBuilder sb = new StringBuilder("Message[type=");
		sb.append(properties.getType());
		sb.append(", mid=");
		sb.append(properties.getMessageId());
		sb.append(", name=");
		sb.append(headers.get(MESSAGE_NAME));
		sb.append(", srcId=");
		sb.append(headers.get(SRC_ID));
		sb.append(", destId=");
		sb.append(headers.get(DEST_ID));
		sb.append(", correlationId=");
		sb.append(properties.getCorrelationId());
		sb.append(", replyTo=");
		sb.append(properties.getReplyTo());
		sb.append(", content=");
		try 
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(amqpMessage.getBody());
			ObjectInputStream ois = new ObjectInputStream(bis);
			sb.append((Serializable)ois.readObject());
		} 
		catch (Exception e)
		{
			LOGGER.error("Failed to get AMQPMessage content");
			LOGGER.error(e.getLocalizedMessage(),e);
			return null;
		}	
		return sb.append("]").toString();
	}
	public static Message convertToAMQPMessage(UMMessage message) {
		if(message == null) return null;
		MessagePropertiesBuilder mPropertiesBuilder = MessagePropertiesBuilder.newInstance();
		mPropertiesBuilder.setMessageId(message.getMid());
		if (message.getType()!=null) {
			mPropertiesBuilder.setType(message.getType().name());
		}
		String correlationId = message.getCorrelationId();
		mPropertiesBuilder.setCorrelationId((correlationId==null||correlationId.isEmpty())?UUID.randomUUID().toString():correlationId);
		mPropertiesBuilder.setExpiration(message.getExpiration()==null?null:message.getExpiration().toString());
		mPropertiesBuilder.setDeliveryMode((message.getPersistent()==null||message.getPersistent())?MessageDeliveryMode.PERSISTENT:MessageDeliveryMode.NON_PERSISTENT);
		mPropertiesBuilder.setHeader(SRC_ID, message.getSrcId());
		mPropertiesBuilder.setHeader(DEST_ID, message.getDestId());
		mPropertiesBuilder.setHeader(MESSAGE_NAME, message.getName());
		mPropertiesBuilder.setContentEncoding(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
		mPropertiesBuilder.setReplyTo(message.getReplyTo()); 
		MessageProperties messagePros = mPropertiesBuilder.build();
		Serializable content = message.getContent();
		try 
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(content);
			oos.close();
			baos.close();
			Message amqpMessage = new Message(baos.toByteArray(), messagePros);
			return amqpMessage;
		} 
		catch (Exception e)
		{
			LOGGER.error("Failed to convert UMMessage to AMQPMessage. message content={}",content);
			LOGGER.error(e.getLocalizedMessage(),e);
			return null;
		}		
	}

	public static UMMessage convertToUMMessage(Message amqpMessage) {
		
		if (amqpMessage==null) {			
			return null;
		}
		try {
			MessageProperties properties = amqpMessage.getMessageProperties();
			UMMessage message = new UMMessage();
			
			UMMessageType umMessageType = nameAndMessageTypeMap.get(properties.getType());
			if(umMessageType == null) {
				throw new Exception("The amqp message name to be converted does not exists.");
			}
			message.setType(umMessageType);
			message.setReplyTo(properties.getReplyTo());
			message.setMid(properties.getMessageId());
			message.setCorrelationId(properties.getCorrelationId());	
			String expiration = properties.getExpiration();
			message.setExpiration((expiration==null||expiration.isEmpty()) ? null : new Long(properties.getExpiration()));
			message.setPersistent(properties.getDeliveryMode()==MessageDeliveryMode.PERSISTENT ? true : false);
			// Header setting
			Map<String, Object> headers = properties.getHeaders();
			message.setSrcId((String)headers.get(SRC_ID));
			message.setDestId((String)headers.get(DEST_ID));
			message.setName((String)headers.get(MESSAGE_NAME));
			ByteArrayInputStream bis = new ByteArrayInputStream(amqpMessage.getBody());
			ObjectInputStream ois = new ObjectInputStream(bis);
			message.setContent((Serializable)ois.readObject());
			return message;
		} catch (Exception e) {
			LOGGER.error("Failed to convert AMQPMessage to UMMessage.");
			LOGGER.error(e.getLocalizedMessage(),e);
			return null;
		}
		
	}

	public static String getSrcId() {
		return SRC_ID;
	}

	public static String getMessageName() {
		return MESSAGE_NAME;
	}

	public static String getDestId() {
		return DEST_ID;
	}
	public static UMMessage generateEventMessage(String eventName, Serializable eventContent) {
		return generateUMMessage(UMMessageType.EVENT, eventName, null, null, eventName, null, null, null, null, eventContent);
	}
	
	public static UMMessage generateUMMessage(UMMessageType messageType,String messageName,String mid
			,String srcId,String destId,String correlationId,String replyTo,Long expiration, Boolean persistent, Serializable content)
	{
		if(RUMessagerUtils.hasNullParams(messageType,messageName,destId,content))
		{
			LOGGER.error("Failed to create UMMessage. There are null params");
			return null;
		}
		// check parameters
		if(mid == null || mid.isEmpty()) mid = UUID.randomUUID().toString();
		if (srcId == null || srcId.isEmpty()) srcId = UMPropertyPlaceHolderConfigurer.getLocalId();
		if (messageType == UMMessageType.REQUEST && (correlationId == null || correlationId.isEmpty())) correlationId = UUID.randomUUID().toString();
		if (persistent == null) persistent = UMPropertyPlaceHolderConfigurer.getDefaultMessagePersistent();
		if (expiration == null) expiration = UMPropertyPlaceHolderConfigurer.getDefaultMessageExpiration();
		return new UMMessage(mid,messageName,messageType,srcId,destId,content,replyTo,expiration,persistent,correlationId);
	}
	
	public static UMMessage generateInformUMMessage(String destId, String messageName, Long timeout, Boolean persistent,
			Serializable content)
	{
		return generateUMMessage(UMMessageType.INFORM, messageName, null, null, destId, null, null, timeout, persistent, content);
		
	}

	public static UMMessage generateSynRequestUMMessage(String destId, String messageName, Long timeout, Boolean persistent,
			Serializable content)
	{
		return generateUMMessage(UMMessageType.REQUEST, messageName, null, null, destId, null, UMPropertyPlaceHolderConfigurer.DIRECT_REPLY_TO, timeout, persistent, content);
		
	}
	public static UMMessage generateAsynRequestUMMessage(String destId, String messageName, Serializable content, Long timeout,
			Boolean persistent) 
	{
		
		return generateUMMessage(UMMessageType.REQUEST, messageName, null, null, destId, null, UMPropertyPlaceHolderConfigurer.getLocalId(), timeout, persistent, content);
	}
	public static UMMessage generateResponseUMMessage(UMMessage requestMessage, Serializable responseContent) 
	{
	
		return generateUMMessage(UMMessageType.RESPONSE, requestMessage.getName(), null, requestMessage.getDestId()
				, requestMessage.getSrcId(), requestMessage.getCorrelationId(), null, null, null, responseContent);
	}

}
