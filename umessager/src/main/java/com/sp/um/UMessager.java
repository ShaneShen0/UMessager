package com.sp.um;

import java.io.Serializable;

import com.sp.um.listener.IMessageListener;
import com.sp.um.message.UMMessage;
/**
 *   
 * @author shanesheng
 *
 */
public interface UMessager extends IUMessageManager{


	/**
	 * Inform a destination of a message.
	 * @param message
	 * @param timout publisher confirmation timeout
	 * @return 
	 */
	public Boolean inform (UMMessage message,Long timout);
	public Boolean inform(UMMessage message);
	public Boolean inform(String destId,String messageName,Serializable content);
	public Boolean inform(String destId,String messageName,Long timeout,Serializable content);
	public Boolean inform(String destId,String messageName,Long timeout,Boolean persistent,Serializable content);
	

	/**
	 * Send a synchronous request message. A Response message is returned in timeout. 
	 * @param destId the destination of message
	 * @param message the message to be send
	 * @param timeout the timeOut
	 * @return the response message. If the response times out, return null.
	 */
	public UMMessage synRequest(UMMessage message,Long timeout);
	public UMMessage synRequest(UMMessage message);
	public UMMessage synRequest(String destId,String messageName,Serializable content);
	public UMMessage synRequest(String destId,String messageName,Long timeout,Serializable content);
	public UMMessage synRequest(String destId,String messageName,Long timeout,Boolean persistent,Serializable content);
	
	/**
	 * Send a asynchronous request message. A response message is returned to MessageListener in timeout
	 * @param destId
	 * @param message
	 * @param timeout
	 * @param listener the MessageListener to handle the response message.
	 */
	public void asynRequest(UMMessage message, IMessageListener listener, Long timeout);
	public void asynRequest(UMMessage message, IMessageListener listener);
	public void asynRequest(String destId,String messageName,IMessageListener listener,Serializable content);
	public void asynRequest(String destId,String messageName,IMessageListener listener,Serializable content,Long timeout);
	public void asynRequest(String destId,String messageName,IMessageListener listener,Serializable content,Long timeout,Boolean persistent);
	/**
	 * Send a response message for corresponding request message.
	 * @param requestMessage the received request message
	 * @param responseMessage the response message to reply 
	 */
	public void response(UMMessage requestMessage,UMMessage responseMessage);
	public void response(UMMessage requestMessage,Serializable content);

	/**
	 * Publish a event
	 * @param eventName the name of the event
	 * @param eventContent the event content  
	 */
	public void publishEvent(String eventName, Serializable eventContent);


	
}
