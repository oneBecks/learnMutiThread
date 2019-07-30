package com.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MutiCondition {

	public static void main(String[] args) throws InterruptedException {
		MyQueue myQueue = new MutiCondition().new MyQueue(3);
		    new Thread() {
	            @Override
	            public void run() {
	                for (int i = 0; i < 6; i++) {
	                	if(i == 5) {
                		  try {
							Thread.sleep(10000L);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                	}
	                	myQueue.put("x" + i);
	                }
	            }
	        }.start();

	        Thread.sleep(3000L);
	        for (int i = 0; i < 6; i++) {
	        	myQueue.take();
	            Thread.sleep(2000L);
	        }
	}

    class MyQueue {
    	List<Object> list = new ArrayList<Object>();
    	int maxSize;

		Lock lock = new ReentrantLock();
	    Condition putCondition = lock.newCondition();
	    Condition takeCondition = lock.newCondition();

		public MyQueue(int maxSize) {
			this.maxSize = maxSize;
		}

	    public void put(Object obj){
	    	lock.lock();
	    	try {
	    		if(list.size() >= maxSize) {
				   //加满了，不能再加了，要等待，取走之后在唤醒加操作
	    			System.out.println("加满啦等着");
	    			putCondition.await();
	    		}
	    	} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				System.out.println("加第" + (list.size()+1) + "个：" + obj);
				list.add(obj);
				System.out.println(list);
				//加进去之后就可以取了，如果之前是空的不能取，取操作阻塞，加完要唤醒
				takeCondition.signal();
				lock.unlock();
			}
		}
	    
	    public void take(){
	    	lock.lock();
	    	try {
	    		if(list.size() == 0) {
					//取没了，不能再取了，要等待，加完之后在唤醒取操作
	    			System.out.println("取没了等着");
					takeCondition.await();
					System.out.println("有新加进来的了" + list.size());
				}
	    	}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				System.out.println("开始取");
				list.remove(0);
				System.out.println("被取走空出来" + (maxSize - list.size()) + "个");
				System.out.println(list);
				//取之后就可以加了，如果之前是满的的不能加了，加操作阻塞，取完要唤醒
				putCondition.signal();
				lock.unlock();
			}
			
		}
		
	}
}

