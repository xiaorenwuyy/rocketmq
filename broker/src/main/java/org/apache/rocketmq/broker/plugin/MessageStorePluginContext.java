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

package org.apache.rocketmq.broker.plugin;

import org.apache.rocketmq.common.BrokerConfig;
import org.apache.rocketmq.store.MessageArrivingListener;
import org.apache.rocketmq.store.config.MessageStoreConfig;
import org.apache.rocketmq.store.stats.BrokerStatsManager;
/**
 * 消息中心插件上下文
 * @author yuyang
 * @date 2018年5月30日
 */
public class MessageStorePluginContext {
	//消息中心配置
    private MessageStoreConfig messageStoreConfig;
    //broker 状态管理器
    private BrokerStatsManager brokerStatsManager;
    //消息到达监听器
    private MessageArrivingListener messageArrivingListener;
    //broker 配置类
    private BrokerConfig brokerConfig;

    public MessageStorePluginContext(MessageStoreConfig messageStoreConfig,
        BrokerStatsManager brokerStatsManager, MessageArrivingListener messageArrivingListener,
        BrokerConfig brokerConfig) {
        super();
        this.messageStoreConfig = messageStoreConfig;
        this.brokerStatsManager = brokerStatsManager;
        this.messageArrivingListener = messageArrivingListener;
        this.brokerConfig = brokerConfig;
    }

    public MessageStoreConfig getMessageStoreConfig() {
        return messageStoreConfig;
    }

    public BrokerStatsManager getBrokerStatsManager() {
        return brokerStatsManager;
    }

    public MessageArrivingListener getMessageArrivingListener() {
        return messageArrivingListener;
    }

    public BrokerConfig getBrokerConfig() {
        return brokerConfig;
    }

}
