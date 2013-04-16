package com.coolroy.photoscanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Filename: ThreadPoolUtil.java
 * @Author: coolroy
 * @Email: lianghwen@live.cn
 * @CreateDate: 2013-4-13
 * @Description: description of the new class
 * @Others: comments
 * @ModifyHistory:
 */
public class ThreadPoolUtil {
	
	private static ExecutorService executorService;
	
	private static int maxThreadNum = 40;
	
	private static void initService(){
		if(executorService == null){
			executorService = Executors.newFixedThreadPool(maxThreadNum);
		}
	}
	
	/**
	 * @Description:ִ执行
	 * @Author: coolroy
	 * @Email: lianghwen@live.cn
	 * @param runnable
	 * @Others:
	 */
	public static void addRunnable(final Runnable runnable) {
		initService();
		executorService.execute(new Runnable() {
			public void run() {
				try {
					runnable.run();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

}
