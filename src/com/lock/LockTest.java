package com.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * ReentrantLock可重入加几次就要解几次才算释放，owner记录当前获取锁的线程，count记录当前线程加了几次锁
 * @author Administrator
 *
 */
public class LockTest {
	static Lock lock = new ReentrantLock();
	public static void main(String[] args) throws InterruptedException {
		LockTest lockTest = new LockTest();
		lockTest.testLockInterruptibly();
	}

	/**
	 * lock方法的使用:调用lock抢锁会一直阻塞直到其他线程释放了锁该线程获取到锁
	 * @throws InterruptedException
	 */
	public void testLock() throws InterruptedException {
		lock.lock();
		System.out.println("主线程获取锁" + System.currentTimeMillis());
		new Thread() {
			@Override
			public void run() {
				System.out.println("线程开始抢锁" + System.currentTimeMillis());
				lock.lock();
				System.out.println("线程抢到了锁" + System.currentTimeMillis());
			}
		}.start();
		Thread.sleep(4000);
		lock.unlock();
		System.out.println("主线程释放锁" + System.currentTimeMillis());
	}
	
	/**
	 * trylock方法的使用:调用trylock抢锁会尝试抢一次，当时没抢到就没抢到，就执行完不抢了
	 * @throws InterruptedException
	 */
	public void testTryLock() throws InterruptedException {
		lock.lock();
		System.out.println("主线程获取锁" + System.currentTimeMillis());
		new Thread() {
			@Override
			public void run() {
				System.out.println("线程开始抢锁" + System.currentTimeMillis());
				if(lock.tryLock()) {
					System.out.println("线程抢到了锁" + System.currentTimeMillis());
				}
			}
		}.start();
		Thread.sleep(4000);
		lock.unlock();
		System.out.println("主线程释放锁" + System.currentTimeMillis());
	}
	/**
	 * trylock方法的使用:调用trylocks设定时间内会阻塞一直抢锁，时间过了就放弃抢锁
	 * @throws InterruptedException
	 */
	public void testTryLockTime() throws InterruptedException {
		lock.lock();
		System.out.println("主线程获取锁" + System.currentTimeMillis());
		new Thread() {
			@Override
			public void run() {
				System.out.println("线程开始抢锁" + System.currentTimeMillis());
				try {
					if(lock.tryLock(5000,TimeUnit.MILLISECONDS)) {
						System.out.println("线程抢到了锁" + System.currentTimeMillis());
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		Thread.sleep(4000);
		lock.unlock();
		System.out.println("主线程释放锁" + System.currentTimeMillis());
	}
	
	/**
	 * lockInterruptibly方法的使用:调用lockInterruptibly保持阻塞一直抢锁，当别的线程对该线程调用interrupt方法，会执行该线程catch部分并退出抢锁。
	 * @throws InterruptedException
	 */
	public void testLockInterruptibly() throws InterruptedException {
		lock.lock();
		System.out.println("主线程获取锁" + System.currentTimeMillis());
		Thread t1 = new Thread() {
			@Override
			public void run() {
				System.out.println("线程开始抢锁" + System.currentTimeMillis());
				try {
					lock.lockInterruptibly();
					System.out.println("线程抢到了锁" + System.currentTimeMillis());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("不抢了结束" + System.currentTimeMillis());
				}
			}
		};
		t1.start();
		Thread.sleep(4000);
		System.out.println("线程你别抢了" + System.currentTimeMillis());
		t1.interrupt();
		Thread.sleep(1000);
    	System.out.println("主线程释放锁" + System.currentTimeMillis());
	}
}
