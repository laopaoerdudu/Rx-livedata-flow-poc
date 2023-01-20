package com.wwe

import java.lang.Thread.sleep

val lock = Object()

fun main() {
    startThreadA()
}

private fun startThreadA() {
    Thread {
        synchronized(lock) {
            println("thread-A get lock")

            // 线程 B 也需要对象锁
            // 只有等线程 A 先执行完 wait() 释放了对象锁
            // 线程 B 才可以拿到锁
            startThreadB()

            try {
                println("thread-A release lock before")
                lock.wait()
                println("thread-A release lock after")
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
        }
    }.apply {
        name = "thread-A"
    }.start()
}

private fun startThreadB() {
    Thread {
        synchronized(lock) {
            println("thread-B get lock")

            // 启动线程 C 也需要对象锁，这就需要线程 B 先释放锁
            startThreadC()

            // 线程 C 要等，B 先睡一会儿
            sleep(1000)

            // 执行 notify 唤醒线程A
            // 线程A 在线程B 执行notify()之后就一直在等待锁，这时候线程B 还没有退出 synchronize 代码块，锁还在线程B 手里；
            // 线程B退出 synchronize 代码块，释放锁之后，线程 A 和线程 C 竞争锁；
            // B释放锁之后 A 总会先得到锁，为什么？
            lock.notify()
        }
        println("thread-B release lock")
    }.apply {
        name = "thread-B"
    }.start()
}

private fun startThreadC() {
    Thread {
        synchronized(lock) {
            println("thread-C get lock")
        }
    }.apply {
        name = "thread-C"
    }.start()
}