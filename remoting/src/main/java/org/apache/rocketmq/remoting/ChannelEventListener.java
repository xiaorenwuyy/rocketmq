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
package org.apache.rocketmq.remoting;

import io.netty.channel.Channel;
/**
 * 渠道事件监听器  
 * 注意：方法参数是个final 类型的
 * @author yuyang
 * @date 2018年5月23日
 */
public interface ChannelEventListener {
	/**
	 * 渠道连接事件
	 * @param  remoteAddr  远程地址，是个字符串
	 * @param  channel    netty 的 渠道类channel 
	 * @return void      
	 * @throws
	 */
    void onChannelConnect(final String remoteAddr, final Channel channel);
	/**
	 * 渠道关闭事件
	 * @param  remoteAddr  远程地址，是个字符串
	 * @param  channel    netty 的 渠道类channel 
	 * @return void      
	 * @throws
	 */
    void onChannelClose(final String remoteAddr, final Channel channel);
	/**
	 * 渠道抛错事件
	 * @param  remoteAddr  远程地址，是个字符串
	 * @param  channel    netty 的 渠道类channel 
	 * @return void      
	 * @throws
	 */
    void onChannelException(final String remoteAddr, final Channel channel);
	/**
	 * 渠道空闲事件
	 * @param  remoteAddr  远程地址，是个字符串
	 * @param  channel    netty 的 渠道类channel 
	 * @return void      
	 * @throws
	 */
    void onChannelIdle(final String remoteAddr, final Channel channel);
}
