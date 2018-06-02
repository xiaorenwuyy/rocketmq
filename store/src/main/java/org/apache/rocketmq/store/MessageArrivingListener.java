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

package org.apache.rocketmq.store;

import java.util.Map;

/**
 * 消息到达监听器
 * @author yuyang
 * @date 2018年5月30日
 */
public interface MessageArrivingListener {
	/**
	 * 到达
	 * @param topic  主题
	 * @param queueId  队列id
	 * @param logicOffset  逻辑偏移
	 * @param tagsCode  标签编码
	 * @param msgStoreTime  消息存储时间
	 * @param filterBitMap  过滤bit map
	 * @param properties   配置文件
	 * @return void      
	 * @throws
	 */
    void arriving(String topic, int queueId, long logicOffset, long tagsCode,
        long msgStoreTime, byte[] filterBitMap, Map<String, String> properties);
}
