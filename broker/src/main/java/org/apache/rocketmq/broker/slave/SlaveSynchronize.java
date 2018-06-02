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
package org.apache.rocketmq.broker.slave;

import java.io.IOException;
import org.apache.rocketmq.broker.BrokerController;
import org.apache.rocketmq.broker.subscription.SubscriptionGroupManager;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.constant.LoggerName;
import org.apache.rocketmq.common.protocol.body.ConsumerOffsetSerializeWrapper;
import org.apache.rocketmq.common.protocol.body.SubscriptionGroupWrapper;
import org.apache.rocketmq.common.protocol.body.TopicConfigSerializeWrapper;
import org.apache.rocketmq.store.config.StorePathConfigHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 从服务同步
 * @author yuyang
 * @date 2018年6月2日
 */
public class SlaveSynchronize {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.BROKER_LOGGER_NAME);
    private final BrokerController brokerController;
    private volatile String masterAddr = null;

    public SlaveSynchronize(BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    public String getMasterAddr() {
        return masterAddr;
    }

    public void setMasterAddr(String masterAddr) {
        this.masterAddr = masterAddr;
    }
    
    /**
     * 同步数据
     *      
     * @return void      
     * @throws
     */
    public void syncAll() {
    	//同步主题配置
        this.syncTopicConfig();
        //同步消费者偏移量
        this.syncConsumerOffset();
        //同步延迟偏移量
        this.syncDelayOffset();
        //同步订阅组配置
        this.syncSubscriptionGroupConfig();
    }

    /**
     * 同步主题配置
     *      
     * @return void      
     * @throws
     */
    private void syncTopicConfig() {
    	//获取主地址，启动后才有值
        String masterAddrBak = this.masterAddr;
        if (masterAddrBak != null) {
            try {
            	//获取topic 数据
                TopicConfigSerializeWrapper topicWrapper =
                    this.brokerController.getBrokerOuterAPI().getAllTopicConfig(masterAddrBak);
                if (!this.brokerController.getTopicConfigManager().getDataVersion()
                    .equals(topicWrapper.getDataVersion())) {
                	//设置新版本	
                    this.brokerController.getTopicConfigManager().getDataVersion()
                        .assignNewOne(topicWrapper.getDataVersion());
                    //清空设置新数据
                    this.brokerController.getTopicConfigManager().getTopicConfigTable().clear();
                    this.brokerController.getTopicConfigManager().getTopicConfigTable()
                        .putAll(topicWrapper.getTopicConfigTable());
                    //保存数据到文件
                    this.brokerController.getTopicConfigManager().persist();

                    log.info("Update slave topic config from master, {}", masterAddrBak);
                }
            } catch (Exception e) {
                log.error("SyncTopicConfig Exception, {}", masterAddrBak, e);
            }
        }
    }

    /**
     * 同步消费者偏移量
     *      
     * @return void      
     * @throws
     */
    private void syncConsumerOffset() {
        String masterAddrBak = this.masterAddr;
        if (masterAddrBak != null) {
            try {
                ConsumerOffsetSerializeWrapper offsetWrapper =
                    this.brokerController.getBrokerOuterAPI().getAllConsumerOffset(masterAddrBak);
                //设置新的偏移表
                this.brokerController.getConsumerOffsetManager().getOffsetTable()
                    .putAll(offsetWrapper.getOffsetTable());
                //保存数据到配置文件
                this.brokerController.getConsumerOffsetManager().persist();
                log.info("Update slave consumer offset from master, {}", masterAddrBak);
            } catch (Exception e) {
                log.error("SyncConsumerOffset Exception, {}", masterAddrBak, e);
            }
        }
    }

    /**
     * 同步延迟偏移量
     *      
     * @return void      
     * @throws
     */
    private void syncDelayOffset() {
        String masterAddrBak = this.masterAddr;
        if (masterAddrBak != null) {
            try {
            	//获取偏移量
                String delayOffset =
                    this.brokerController.getBrokerOuterAPI().getAllDelayOffset(masterAddrBak);
                if (delayOffset != null) {
                	
                	//获取配置文件
                    String fileName =
                        StorePathConfigHelper.getDelayOffsetStorePath(this.brokerController
                            .getMessageStoreConfig().getStorePathRootDir());
                    try {
                    	//把同步偏移数据同步到配置文件中
                        MixAll.string2File(delayOffset, fileName);
                    } catch (IOException e) {
                        log.error("Persist file Exception, {}", fileName, e);
                    }
                }
                log.info("Update slave delay offset from master, {}", masterAddrBak);
            } catch (Exception e) {
                log.error("SyncDelayOffset Exception, {}", masterAddrBak, e);
            }
        }
    }
    

    /**
     * 同步订阅组配置
     *      
     * @return void      
     * @throws
     */
    private void syncSubscriptionGroupConfig() {
        String masterAddrBak = this.masterAddr;
        if (masterAddrBak != null) {
            try {
                SubscriptionGroupWrapper subscriptionWrapper =
                    this.brokerController.getBrokerOuterAPI()
                        .getAllSubscriptionGroupConfig(masterAddrBak);

                if (!this.brokerController.getSubscriptionGroupManager().getDataVersion()
                    .equals(subscriptionWrapper.getDataVersion())) {
                    SubscriptionGroupManager subscriptionGroupManager =
                        this.brokerController.getSubscriptionGroupManager();
                    //设置新版本
                    subscriptionGroupManager.getDataVersion().assignNewOne(
                        subscriptionWrapper.getDataVersion());
                    //清空数据，并保存新数据
                    subscriptionGroupManager.getSubscriptionGroupTable().clear();
                    subscriptionGroupManager.getSubscriptionGroupTable().putAll(
                        subscriptionWrapper.getSubscriptionGroupTable());
                    //保存数据到配置文件
                    subscriptionGroupManager.persist();
                    log.info("Update slave Subscription Group from master, {}", masterAddrBak);
                }
            } catch (Exception e) {
                log.error("SyncSubscriptionGroup Exception, {}", masterAddrBak, e);
            }
        }
    }
}
