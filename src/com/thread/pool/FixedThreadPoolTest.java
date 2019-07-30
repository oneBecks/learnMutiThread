package com.thread.pool;

import java.sql.Time;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FixedThreadPoolTest {
	public static void main(String[] args) {
		  ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
		  for (int i = 0; i < 10; i++) {
		   final int index = i;
		   fixedThreadPool.execute(new Runnable() {
		    public void run() {
		     try {
		      System.out.println(Thread.currentThread() + ":任务" + index + " 开始 :" + System.currentTimeMillis());
		      Thread.sleep(2000);
		      System.out.println(Thread.currentThread() + ":任务" + index + " 结束 :" + System.currentTimeMillis());
		     } catch (InterruptedException e) {
		      e.printStackTrace();
		     }
		    }
		   });
		  }
		 }
}
