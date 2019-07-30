package com.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 类似synchronized中的wait notify,wait都进入一个等待队列但是condition可以new多个，就有多个等待集合
 * 与lock匹配使用，要先加锁在用
 * @author Administrator
 *
 */
public class ConditionTest {
	private static Lock lock = new ReentrantLock();
	private static Condition condition = lock.newCondition();
	public static void main(String[] args) throws InterruptedException {
		testConditionDead();
	}
	
	public static void testCondition() throws InterruptedException {
		Thread th = new Thread() {
			@Override
			public void run() {
				lock.lock();
				System.out.println("th拿到锁要执行await挂起th线程了,挂起后是WAITING状态");
				try {
					condition.await();
					System.out.println("th线程await后被唤醒重新拿到锁了");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
				
			}
		};
		th.start();
		Thread.sleep(2000);
		System.out.println("th执行await后状态" + th.getState());
		System.out.println("主线程开始抢锁");
		lock.lock();
		System.out.println("主线程拿到锁并唤醒th线程");
		condition.signal();
		System.out.println("主线程signal后唤醒其他线程但是要解锁执行完其他线程才执行");
		lock.unlock();
	}
	/**
	 * 死锁方式，一个线程先加锁并执行signal解锁后，另一个线程在await就会死锁。
	 * @throws InterruptedException
	 */
	public static void testConditionDead() throws InterruptedException {
		Thread th = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("th开始抢锁");
				lock.lock();
				System.out.println("th拿到锁要执行await挂起th线程了,但是没人再唤醒th了死锁啦一直等呀等·····");
				try {
					condition.await();
					System.out.println("th线程await后被唤醒重新拿到锁了");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
				
			}
		};
		th.start();
		System.out.println("主线程开始抢锁");
		lock.lock();
		System.out.println("主线程拿到锁并唤醒th线程");
		condition.signal();
		System.out.println("主线程signal后唤醒其他线程但是要解锁执行完其他线程才执行");
		lock.unlock();
	}
}
