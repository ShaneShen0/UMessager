package com.sp.rum.receiver;

import java.security.interfaces.RSAKey;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

import com.sp.rum.register.EventListenerRegister;
import com.sp.rum.register.InformAndRequestMessageListenerRegister;
import com.sp.rum.register.ResponseMessageListenerRegister;
import com.sp.rum.utils.RUMessagerUtils;
import com.sp.um.listener.IEventListener;
import com.sp.um.listener.IMessageListener;
import com.sp.um.listener.ResponseListenerData;
import com.sp.um.message.UMMessage;
import com.sp.um.message.UMMessageType;
import com.sp.um.receiver.IMessageProcessor;
/**
 * Process all incoming message.
 * @author shanesheng
 *
 */
public class RUMMessageProcessor implements IMessageProcessor{
		private InformAndRequestMessageListenerRegister iAndRMessageListenerRegister;
		private EventListenerRegister eventListenerRegister;
		
		private ResponseMessageListenerRegister responseMessageListenerRegister;
		private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);
		
		public void onMessage(Message message)
		{

			if(message == null) {
				LOGGER.error("Message received is null");
				return;
			}
			MessageProperties properties = message.getMessageProperties();
			if(properties == null) {
				LOGGER.error("The MessageProperties of received Message is null");
				return;
			}
			Map<String, Object> header = properties.getHeaders();
			
			// Filter message
			if (header==null || !header.containsKey(RUMessagerUtils.getSrcId()) 
					|| !header.containsKey(RUMessagerUtils.getDestId()) 
					|| !header.containsKey(RUMessagerUtils.getMessageName())) {
				LOGGER.error("Message received does not correspond with UMessager's message speficaiton.");
				
			}
			UMMessage umMessage = RUMessagerUtils.convertToUMMessage(message);
			if(umMessage == null) 
			{
				LOGGER.error("Failed to convert incoming amqp message");
			}
			
			//Dispatch message to corresponding handler according to message type
			String messageType = properties.getType();
			LOGGER.info("Receive a "+umMessage.getType().toString()+ " message={}",umMessage.toString());
			if(messageType.equals(UMMessageType.INFORM.toString()) || messageType.equals(UMMessageType.REQUEST.toString())) 
			{
				
				onInformAndRequestMessage(umMessage);
			}
			else if (messageType.equals(UMMessageType.RESPONSE.toString())) 
			{
				onResponseMessage(umMessage);
			}
			else if (messageType.equals(UMMessageType.EVENT.toString())) {
				onEventMessage(umMessage);
			}
		}
		private void onEventMessage(UMMessage umMessage) {
			String eventName = umMessage.getName();
			List<IEventListener> listeners = eventListenerRegister.getListneresOf(eventName);
			if(listeners == null || listeners.isEmpty())
			{
				LOGGER.error("There is no listener for event={}", eventName);
				return;
			}
			for (IEventListener listener : listeners) {
				listener.onMessage(umMessage);
			}
		}
		private void onResponseMessage(UMMessage responseUMMessage)
		{
			String correlationId = responseUMMessage.getCorrelationId();
			ResponseListenerData responseListenerData = responseMessageListenerRegister.getMessageListener(correlationId);
			if (responseListenerData == null) 
			{
				LOGGER.error("The listener of the response message has not been reigistered. response message={} ",responseUMMessage.toString());
				return;
			}
			if(System.currentTimeMillis() >= responseListenerData.getStartTime()+responseListenerData.getTimeout())
			{
				LOGGER.error("The response message has timed out. response message={}",responseUMMessage.toString());
				return;
			}
			responseListenerData.getListener().onMessage(responseUMMessage);
		}
		/**
		 * Handle incoming message with type INFORM or REQUEST
		 */
		private void onInformAndRequestMessage(UMMessage umMessage)
		{
			String name = umMessage.getName();
			IMessageListener messageListener = iAndRMessageListenerRegister.getMessageListener(name);
			//The MessageListener not exists
			if (messageListener == null) {
				LOGGER.error("Message Listener corresponding to the "+umMessage.getType().toString()+" message is not in the register. message={}",umMessage.toString());
				return;
			}
			messageListener.onMessage(umMessage);
		}
		
		public InformAndRequestMessageListenerRegister getiAndRMessageListenerRegister()
		{
			return iAndRMessageListenerRegister;
		}
		public void setiAndRMessageListenerRegister(InformAndRequestMessageListenerRegister iAndRMessageListenerRegister) 
		{
			this.iAndRMessageListenerRegister = iAndRMessageListenerRegister;
		}
		public EventListenerRegister getEventListenerRegister() 
		{
			return eventListenerRegister;
		}
		public void setEventListenerRegister(EventListenerRegister eventListenerRegister)
		{
			this.eventListenerRegister = eventListenerRegister;
		}
		public ResponseMessageListenerRegister getResponseMessageListenerRegister()
		{
			return responseMessageListenerRegister;
		}
		public void setResponseMessageListenerRegister(
				ResponseMessageListenerRegister responseMessageListenerRegister)
		{
			this.responseMessageListenerRegister = responseMessageListenerRegister;
		}
		
		
		
		
		
		
		
}
