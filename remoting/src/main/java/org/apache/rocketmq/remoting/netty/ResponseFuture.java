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
package org.apache.rocketmq.remoting.netty;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.rocketmq.remoting.InvokeCallback;
import org.apache.rocketmq.remoting.common.SemaphoreReleaseOnlyOnce;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
/**
 * 未来响应封装类
 * @author yuyang
 * @date 2018年5月27日
 */
public class ResponseFuture {
    private final int opaque;
    //超时时间
    private final long timeoutMillis;
    //callback 回调 
    private final InvokeCallback invokeCallback;
    //开始时间戳
    private final long beginTimestamp = System.currentTimeMillis();
    //计数器同步类
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    
    //限流类
    private final SemaphoreReleaseOnlyOnce once;

    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);
    //响应类
    private volatile RemotingCommand responseCommand;
    //发送请求成功标志
    private volatile boolean sendRequestOK = true;
    //异常
    private volatile Throwable cause;

    /**
     * 构建未来响应
     * @param opaque 请求识别码
     * @param timeoutMillis  超时时间
     * @param invokeCallback 回调
     * @param once  todo??
     */
    public ResponseFuture(int opaque, long timeoutMillis, InvokeCallback invokeCallback,
        SemaphoreReleaseOnlyOnce once) {
        this.opaque = opaque;
        this.timeoutMillis = timeoutMillis;
        this.invokeCallback = invokeCallback;
        this.once = once;
    }
    
    //callback 封装执行 在一个callback 线程中
    public void executeInvokeCallback() {
        if (invokeCallback != null) {
        	//只执行一次
            if (this.executeCallbackOnlyOnce.compareAndSet(false, true)) {
                invokeCallback.operationComplete(this);
            }
        }
    }
    
    //释放资源  释放一次
    public void release() {
        if (this.once != null) {
            this.once.release();
        }
    }

    public boolean isTimeout() {
        long diff = System.currentTimeMillis() - this.beginTimestamp;
        return diff > this.timeoutMillis;
    }

    //等待响应
    public RemotingCommand waitResponse(final long timeoutMillis) throws InterruptedException {
        this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return this.responseCommand;
    }

    //设置响应
    public void putResponse(final RemotingCommand responseCommand) {
        this.responseCommand = responseCommand;
        this.countDownLatch.countDown();
    }

    public long getBeginTimestamp() {
        return beginTimestamp;
    }

    public boolean isSendRequestOK() {
        return sendRequestOK;
    }

    public void setSendRequestOK(boolean sendRequestOK) {
        this.sendRequestOK = sendRequestOK;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public InvokeCallback getInvokeCallback() {
        return invokeCallback;
    }

    //获取异常
    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public RemotingCommand getResponseCommand() {
        return responseCommand;
    }

    public void setResponseCommand(RemotingCommand responseCommand) {
        this.responseCommand = responseCommand;
    }

    public int getOpaque() {
        return opaque;
    }

    @Override
    public String toString() {
        return "ResponseFuture [responseCommand=" + responseCommand + ", sendRequestOK=" + sendRequestOK
            + ", cause=" + cause + ", opaque=" + opaque + ", timeoutMillis=" + timeoutMillis
            + ", invokeCallback=" + invokeCallback + ", beginTimestamp=" + beginTimestamp
            + ", countDownLatch=" + countDownLatch + "]";
    }
}
