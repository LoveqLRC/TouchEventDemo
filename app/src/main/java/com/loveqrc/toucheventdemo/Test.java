package com.loveqrc.toucheventdemo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Test {

    private Integer index;

    public void plusIndex() {
        index++;
        System.out.println(index);
    }

    public static void main(String[] args) {
        Test test = new Test();
        for (int i = 0; i < 100; i++) {
            new Thread(test::plusIndex).start();
        }

    }
}
