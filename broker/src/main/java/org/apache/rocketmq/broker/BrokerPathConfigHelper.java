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

package org.apache.rocketmq.broker;

import java.io.File;
/**
 * broker 配置帮助类
 * @author yuyang
 * @date 2018年5月29日
 */
public class BrokerPathConfigHelper {
	//broker 配置路径
    private static String brokerConfigPath = System.getProperty("user.home") + File.separator + "store"
        + File.separator + "config" + File.separator + "broker.properties";

    public static String getBrokerConfigPath() {
        return brokerConfigPath;
    }
    //设置配置路径
    public static void setBrokerConfigPath(String path) {
        brokerConfigPath = path;
    }

    /**
     * 获取主题配置路径文件
     * @param rootDir  消息中心配置类中的根路径
     * @return     
     * @return String      
     * @throws
     */
    public static String getTopicConfigPath(final String rootDir) {
        return rootDir + File.separator + "config" + File.separator + "topics.json";
    }

    /**
     * 获取消费主偏移配置文件路径
     * @param rootDir 消息中心配置类中的根路径
     * @return     
     * @return String      
     * @throws
     */
    public static String getConsumerOffsetPath(final String rootDir) {
        return rootDir + File.separator + "config" + File.separator + "consumerOffset.json";
    }

    /**
     * 获取订阅组配置文件路径
     * @param rootDir 消息中心配置类中的根路径
     * @return     
     * @return String      
     * @throws
     */
    public static String getSubscriptionGroupPath(final String rootDir) {
        return rootDir + File.separator + "config" + File.separator + "subscriptionGroup.json";
    }

    /**
     * 获取消费者过滤器配置文件路径
     * @param rootDir 消息中心配置类中的根路径
     * @return     
     * @return String      
     * @throws
     */
    public static String getConsumerFilterPath(final String rootDir) {
        return rootDir + File.separator + "config" + File.separator + "consumerFilter.json";
    }
}
