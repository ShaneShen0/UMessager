package com.sp.rum.receiver;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageAsynchronousExecutor {
	private ExecutorService threadPoolExecutor;
	private String threadNamePrefix;
	private int concurrentProcessorCount;
	private static final Logger LOGGER=LoggerFactory.getLogger(MessageAsynchronousExecutor.class);
	
	public void init() {
		ThreadFactory factory = new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				AtomicInteger num = new AtomicInteger(0); // just used to set thread names
				Thread thread = new Thread(r,threadNamePrefix+"-"+num.incrementAndGet());
				thread.setDaemon(false);
				thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						LOGGER.error("Thread "+t.getName()+e.getLocalizedMessage(),e);
						
					}
				} );
				
				return thread;
			}
		};
		threadPoolExecutor =  Executors.newFixedThreadPool(concurrentProcessorCount, factory);	
	}
	public void executer(Runnable runnable) {
		threadPoolExecutor.execute(runnable);
	}
	public void destroy() {
		if(threadPoolExecutor !=null && !threadPoolExecutor.isTerminated() && !threadPoolExecutor.isShutdown()) {
			threadPoolExecutor.shutdown();
			LOGGER.info("Shutdown UMessager receiver...");
		}
		else {
			LOGGER.info("UMessager receiver has been shut down.");
		}
	}

	public ExecutorService getThreadPoolExecutor() {
		return threadPoolExecutor;
	}

	public void setThreadPoolExecutor(ExecutorService threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}
	public String getThreadNamePrefix() {
		return threadNamePrefix;
	}
	public void setThreadNamePrefix(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix;
	}
	public int getConcurrentProcessorCount() {
		return concurrentProcessorCount;
	}
	public void setConcurrentProcessorCount(int concurrentProcessorCount) {
		this.concurrentProcessorCount = concurrentProcessorCount;
	}


	
}
