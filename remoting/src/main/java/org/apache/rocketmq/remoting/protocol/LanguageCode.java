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

package org.apache.rocketmq.remoting.protocol;
/**
 * 语言代码
 * @author yuyang
 * @date 2018年5月23日
 */
public enum LanguageCode {
    JAVA((byte) 0),//java 
    CPP((byte) 1),
    DOTNET((byte) 2),//.NET
    PYTHON((byte) 3),
    DELPHI((byte) 4),
    ERLANG((byte) 5),//一种面向并发的编程语言
    RUBY((byte) 6),
    OTHER((byte) 7),
    HTTP((byte) 8),
    GO((byte) 9),
    PHP((byte) 10);

    private byte code;

    LanguageCode(byte code) {
        this.code = code;
    }

    /**
     * 根据传入的code 获取对应的枚举类
     * @param code
     * @return
     */
    public static LanguageCode valueOf(byte code) {
    	//LanguageCode.values() 是获取所拥有的枚举类型
        for (LanguageCode languageCode : LanguageCode.values()) {
            if (languageCode.getCode() == code) {
                return languageCode;
            }
        }
        return null;
    }
    /**
     * 根据枚举类获取它自己封装的code
     * @return
     */
    public byte getCode() {
        return code;
    }
}
