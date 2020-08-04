package com.sp.um.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class UMApplicationContextAware implements ApplicationContextAware{
	
	protected ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
		setLocalResource();
	}
	
	public abstract void setLocalResource();

}
