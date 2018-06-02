/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.rocketmq.common.constant.LoggerName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 服务线程抽象类
 * @author yuyang
 * @date 2018年5月30日
 */
public abstract class ServiceThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.COMMON_LOGGER_NAME);

    private static final long JOIN_TIME = 90 * 1000;

    protected final Thread thread;
    //等待计数器
    protected final CountDownLatch2 waitPoint = new CountDownLatch2(1);
    //原子类，是否已经通知 默认为false
    protected volatile AtomicBoolean hasNotified = new AtomicBoolean(false);
    //线程停止标志 用的  volatile 关键字
    protected volatile boolean stopped = false;

    public ServiceThread() {
        this.thread = new Thread(this, this.getServiceName());
    }

    public abstract String getServiceName();

    //启动内部的线程
    public void start() {
        this.thread.start();
    }

    public void shutdown() {
        this.shutdown(false);
    }

    public void shutdown(final boolean interrupt) {
        this.stopped = true;
        log.info("shutdown thread " + this.getServiceName() + " interrupt " + interrupt);

        if (hasNotified.compareAndSet(false, true)) {
            waitPoint.countDown(); // notify
        }

        try {
            if (interrupt) {
                this.thread.interrupt();
            }

            long beginTime = System.currentTimeMillis();
            if (!this.thread.isDaemon()) {
                this.thread.join(this.getJointime());
            }
            long eclipseTime = System.currentTimeMillis() - beginTime;
            log.info("join thread " + this.getServiceName() + " eclipse time(ms) " + eclipseTime + " "
                + this.getJointime());
        } catch (InterruptedException e) {
            log.error("Interrupted", e);
        }
    }

    public long getJointime() {
        return JOIN_TIME;
    }

    public void stop() {
        this.stop(false);
    }

    public void stop(final boolean interrupt) {
        this.stopped = true;
        log.info("stop thread " + this.getServiceName() + " interrupt " + interrupt);

        if (hasNotified.compareAndSet(false, true)) {
            waitPoint.countDown(); // notify
        }

        if (interrupt) {
            this.thread.interrupt();
        }
    }

    public void makeStop() {
        this.stopped = true;
        log.info("makestop thread " + this.getServiceName());
    }

    //唤醒
    public void wakeup() {
        if (hasNotified.compareAndSet(false, true)) {
            waitPoint.countDown(); // notify
        }
    }
    
    /**
     * 等待执行  ？？？
     * @param interval  时间间隔
     * @return void      
     * @throws
     */
    protected void waitForRunning(long interval) {
        if (hasNotified.compareAndSet(true, false)) {
            this.onWaitEnd();
            return;
        }

        //entry to wait
        waitPoint.reset();

        try {
            waitPoint.await(interval, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("Interrupted", e);
        } finally {
            hasNotified.set(false);
            this.onWaitEnd();
        }
    }

    protected void onWaitEnd() {
    }

    public boolean isStopped() {
        return stopped;
    }
}
