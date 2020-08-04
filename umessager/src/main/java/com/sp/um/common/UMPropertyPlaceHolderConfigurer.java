package com.sp.um.common;


import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class UMPropertyPlaceHolderConfigurer extends PropertyPlaceholderConfigurer {
	private static final Map<String,String> uMessagerParamMap = new ConcurrentHashMap<String, String>();
	private static Properties properties = null;
	public 	final static String DIRECT_REPLY_TO = "amq.rabbitmq.reply-to";
	//property name constant value in the properties file
	public final static String PROPNAME_LOCALID = "localId";
	public final static String PROPNAME_LOCAL_EXCHANGE="exchangeName";
	public final static String PROPNAME_DEFAULT_PUBLISHER_CONFIRM_TIMEOUT = "defaultPublisherConfirmTimout";
	public final static String PROPNAME_DEFAULT_MESSGAE_EXPIRATION = "defaultMessageExpiration";
	public final static String PROPNAME_DEFAULT_MESSAGE_PERSISTENT = "defaultMessagePersistent";
	public final static String PROPNAME_DEFAULT_SYNC_RESPONSE_TIMEOUT = "defaultSyncResponseTimeout";
	public final static String PROPNAME_DEFAULT_ASYNC_RESPONSE_TIMEOUT = "defaultAsyncResponseTimeout";
	
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {
		// TODO Auto-generated method stub
		super.processProperties(beanFactoryToProcess, props);
		properties = props;
	}
	@Override
	protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
		
		synchronized (UMPropertyPlaceHolderConfigurer.class) {
			String value = uMessagerParamMap.get(placeholder);
			if(value!=null) {
				props.put(placeholder, value);
				return value;
			}
		}
		return super.resolvePlaceholder(placeholder, props, systemPropertiesMode);
	}
	public static String getProperty(String key) {
		return properties==null ? null : properties.getProperty(key); 
	}
	public static String getLocalId() {
		return getProperty(PROPNAME_LOCALID);
	}
	public static String getExchangeName() {
		return getProperty(PROPNAME_LOCAL_EXCHANGE);
	}
	public static Long getDefaultPublisherConfirmTimout() {
		return Long.valueOf(getProperty(PROPNAME_DEFAULT_PUBLISHER_CONFIRM_TIMEOUT));
	}
	public static Long getDefaultMessageExpiration() {
		return Long.valueOf(getProperty(PROPNAME_DEFAULT_MESSGAE_EXPIRATION));
	}
	public static Boolean getDefaultMessagePersistent() {
		return Boolean.valueOf(getProperty(PROPNAME_DEFAULT_MESSAGE_PERSISTENT));
	}
	public static Long getDefaultSyncResponseTimeout() {
		return Long.valueOf(getProperty(PROPNAME_DEFAULT_SYNC_RESPONSE_TIMEOUT));
	}
	public static Long getDefaultAsyncResponseTimeout() {
		return Long.valueOf(getProperty(PROPNAME_DEFAULT_ASYNC_RESPONSE_TIMEOUT));
	}
	public synchronized static void clearParams() {
		if(uMessagerParamMap!=null && !uMessagerParamMap.isEmpty()) {
			uMessagerParamMap.clear();
		}
	}
}
