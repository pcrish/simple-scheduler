package com.krish.etl.scheduler;

import java.lang.reflect.InvocationTargetException;

public class MainDemo {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Runnable obj = (Runnable)Class.forName("com.krish.etl.scheduler.jobs.DelimitedFileLoader")
                .getDeclaredConstructor().newInstance();
        Thread thread = new Thread(obj);
        thread.start();
        System.out.println("Main thread completed");
    }
}
