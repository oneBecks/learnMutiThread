package com.unsafe;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeTest {
	public String str;
	public static int i=1;
	public static void main(String[] args) {
		try {
			UnsafeTest unsafeTest = new UnsafeTest();
			unsafeTest.str = "new";

			//1.Unsafe单例 通过getUnsafe获取要求是由系统类加载器BootstrapClassLoader加载的类才能用这个方法获取，否则报错java.lang.SecurityException: Unsafe
			//Unsafe unsafe = Unsafe.getUnsafe();
			//普通类可以通过反射的方式直接暴力获取unsafe私有变量

			Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
		    Unsafe unsafe = (Unsafe) field.get(null);//如果为静态属性 则get 参数为null

		    //3.通过unsafe找到对象属性的偏移量

		    Field field1 = UnsafeTest.class.getDeclaredField("str");
		    // 这就是一旦这个类实例化后，该属性在内存中的偏移位置,objectFieldOffset获取非静态成员变量
		    long offset1 = unsafe.objectFieldOffset(field1);
		    System.out.println("str offset = " + offset1);


		    //staticFieldOffset获取非静态成员变量,用objectFieldOffset会报错
		    Field field2 = UnsafeTest.class.getDeclaredField("i");
		    long offset2 = unsafe.staticFieldOffset(field2);
		    System.out.println("i offset = " + offset2);


		    //CAS(compare And Swap 比较并替换)
		    /**
		    * 比较obj的offset处内存位置中的值和期望的值，如果相同则更新。此更新是不可中断的。
		    * 
		    * @param obj 需要更新的对象
		    * @param offset obj中整型field的偏移量
		    * @param expect 希望field中存在的值
		    * @param update 如果期望值expect与field的当前值相同，设置filed的值为这个新值
		    * @return 如果field的值被更改返回true
		    */
		    if(unsafe.compareAndSwapInt(UnsafeTest.class, offset2, 0, 1)) {
		    	System.out.println("成功" + UnsafeTest.i);
		    } else {
		    	System.out.println("失败" + UnsafeTest.i);
		    }

		    if(unsafe.compareAndSwapObject(unsafeTest, offset1,"new", "new1")) {
		    	System.out.println("成功" + unsafe.getObject(unsafeTest, offset1));
		    } else {
		    	System.out.println("失败" + unsafeTest.str);
		    }

		    
		   // 利用Unsafe的原子操作性，向调用者返回某个属性当前的值，并且紧接着将这个属性增加一个新的值
		    //******注意：如果要改变静态变量，第一个参数传类.class对象要不然传对象结果不对
		    System.out.println("获取当前i值" + unsafe.getAndAddLong(UnsafeTest.class, offset2, 4) + ";修改后i值" + i);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
