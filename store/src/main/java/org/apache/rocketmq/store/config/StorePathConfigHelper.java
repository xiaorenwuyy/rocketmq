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
package org.apache.rocketmq.store.config;

import java.io.File;
/**
 * 存储路径帮助类
 * @author yuyang
 * @date 2018年5月30日
 */
public class StorePathConfigHelper {

    public static String getStorePathConsumeQueue(final String rootDir) {
        return rootDir + File.separator + "consumequeue";
    }

    //获取消费者扩展文件
    public static String getStorePathConsumeQueueExt(final String rootDir) {
        return rootDir + File.separator + "consumequeue_ext";
    }
    
    //获取存储索引
    public static String getStorePathIndex(final String rootDir) {
        return rootDir + File.separator + "index";
    }

    //获取存储检查点文件
    public static String getStoreCheckpoint(final String rootDir) {
        return rootDir + File.separator + "checkpoint";
    }

    //获取废弃文件
    public static String getAbortFile(final String rootDir) {
        return rootDir + File.separator + "abort";
    }

    /**
     * 获取锁文件
     * @param rootDir  根目录
     * @return     
     * @return String      
     * @throws
     */
    public static String getLockFile(final String rootDir) {
        return rootDir + File.separator + "lock";
    }
    
    //获取延迟偏移路径
    public static String getDelayOffsetStorePath(final String rootDir) {
        return rootDir + File.separator + "config" + File.separator + "delayOffset.json";
    }

    public static String getTranStateTableStorePath(final String rootDir) {
        return rootDir + File.separator + "transaction" + File.separator + "statetable";
    }

    public static String getTranRedoLogStorePath(final String rootDir) {
        return rootDir + File.separator + "transaction" + File.separator + "redolog";
    }

}
