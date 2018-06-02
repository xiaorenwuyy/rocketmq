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

/**
 * $Id: TopAddressing.java 1831 2013-05-16 01:39:51Z vintagewang@apache.org $
 */
package org.apache.rocketmq.common.namesrv;

import java.io.IOException;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.constant.LoggerName;
import org.apache.rocketmq.common.help.FAQUrl;
import org.apache.rocketmq.common.utils.HttpTinyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 地址类
 * @author yuyang
 * @date 2018年5月31日
 */
public class TopAddressing {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.COMMON_LOGGER_NAME);

    private String nsAddr;
    //ws 地址
    private String wsAddr;
    //单元名称
    private String unitName;

    public TopAddressing(final String wsAddr) {
        this(wsAddr, null);
    }

    public TopAddressing(final String wsAddr, final String unitName) {
        this.wsAddr = wsAddr;
        this.unitName = unitName;
    }

    //清除反应结果的字符串中的新行
    private static String clearNewLine(final String str) {
        String newString = str.trim();
        int index = newString.indexOf("\r");
        if (index != -1) {
            return newString.substring(0, index);
        }

        index = newString.indexOf("\n");
        if (index != -1) {
            return newString.substring(0, index);
        }

        return newString;
    }

    //获取ns 地址
    public final String fetchNSAddr() {
        return fetchNSAddr(true, 3000);
    }

    /**
     * 获取ns 地址
     * @param verbose  冗长的
     * @param timeoutMills
     * @return     
     * @return String      
     * @throws
     */
    public final String fetchNSAddr(boolean verbose, long timeoutMills) {
        String url = this.wsAddr;
        try {  
            if (!UtilAll.isBlank(this.unitName)) {
                url = url + "-" + this.unitName + "?nofix=1";
            }
            HttpTinyClient.HttpResult result = HttpTinyClient.httpGet(url, null, null, "UTF-8", timeoutMills);
            //成功通信
            if (200 == result.code) {
                String responseStr = result.content;
                if (responseStr != null) {
                    return clearNewLine(responseStr);
                } else {
                    log.error("fetch nameserver address is null");
                }
            } else {
                log.error("fetch nameserver address failed. statusCode={}", result.code);
            }
        } catch (IOException e) {
            if (verbose) {
                log.error("fetch name server address exception", e);
            }
        }
        //显示日志
        if (verbose) {
            String errorMsg =
                "connect to " + url + " failed, maybe the domain name " + MixAll.getWSAddr() + " not bind in /etc/hosts";
            errorMsg += FAQUrl.suggestTodo(FAQUrl.NAME_SERVER_ADDR_NOT_EXIST_URL);

            log.warn(errorMsg);
        }
        return null;
    }

    public String getNsAddr() {
        return nsAddr;
    }

    public void setNsAddr(String nsAddr) {
        this.nsAddr = nsAddr;
    }
}
