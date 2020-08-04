package com.sp.um;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sp.um.UMessager;
import com.sp.um.common.UMPropertyPlaceHolderConfigurer;
/**
 * 
 * @author shanesheng
 *
 */
public class UMFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(UMFactory.class);
	private static String UMESSAGER_INSTANCE_BEAN_ID = "umessagerInstance";
	private static  List<AbstractApplicationContext> contexts = new ArrayList<AbstractApplicationContext>();
	/**
	 * Create IUMessager instance using default ApplicationContext
	 * @return IUMessager
	 */
	public synchronized static UMessager getUMessgerInstance() {
		UMessager uMessagerInstance = null;
		try {
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:umessager.xml");
			UMPropertyPlaceHolderConfigurer.clearParams();
			uMessagerInstance=(UMessager)context.getBean(UMESSAGER_INSTANCE_BEAN_ID);		
			contexts.add(context);
		} catch (BeansException e) {
			LOGGER.error(e.getLocalizedMessage(),e);
			throw new RuntimeException("Failed to create umessger instance from ApplicationContext of Spring");
		}
		return uMessagerInstance;
	}
	//TODO Create IUMessager instance using params instead of ApplicationContext
//	public synchronized static IUMessager getUMessager(UMessagerParams params) {
//		
//	}
	public synchronized static void destory() {
		for (AbstractApplicationContext context : contexts) {
			if(context!=null) context.close();
		}
	}

}