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

package org.apache.rocketmq.common.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import org.apache.rocketmq.common.MQVersion;
import org.apache.rocketmq.common.MixAll;
/**
 * http 通信
 * @author yuyang
 * @date 2018年6月2日
 */
public class HttpTinyClient {

	/**
	 * Get 方式获取结果
	 * @param url  路径
	 * @param headers  请求头
	 * @param paramValues 请求参数
	 * @param encoding 编码
	 * @param readTimeoutMs  超时时间
	 * @return
	 * @throws IOException     
	 * @return HttpResult      
	 * @throws
	 */
    static public HttpResult httpGet(String url, List<String> headers, List<String> paramValues,
        String encoding, long readTimeoutMs) throws IOException {
        String encodedContent = encodingParams(paramValues, encoding);
        url += (null == encodedContent) ? "" : ("?" + encodedContent);

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout((int) readTimeoutMs);
            conn.setReadTimeout((int) readTimeoutMs);
            //请求头
            setHeaders(conn, headers, encoding);

            conn.connect();
            //返回码
            int respCode = conn.getResponseCode();
            String resp = null;

            if (HttpURLConnection.HTTP_OK == respCode) {
            	//从输入流中获取字符串
                resp = IOTinyUtils.toString(conn.getInputStream(), encoding);
            } else {
            	//从输入错误流中获取字符串
                resp = IOTinyUtils.toString(conn.getErrorStream(), encoding);
            }
            return new HttpResult(respCode, resp);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    /**
     * 获取拼接的字符串Get 方式的
     * @param paramValues 参数列表
     * @param encoding  编码
     * @return
     * @throws UnsupportedEncodingException     
     * @return String      
     * @throws
     */
    static private String encodingParams(List<String> paramValues, String encoding)
        throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (null == paramValues) {
            return null;
        }

        for (Iterator<String> iter = paramValues.iterator(); iter.hasNext(); ) {
            sb.append(iter.next()).append("=");
            sb.append(URLEncoder.encode(iter.next(), encoding));
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    /**
     * 设置请求头  添加请求属性
     * @param conn  url 连接
     * @param headers  请求头
     * @param encoding     编码
     * @return void      
     * @throws
     */
    static private void setHeaders(HttpURLConnection conn, List<String> headers, String encoding) {
        //添加自定义的请求头
    	if (null != headers) {
            for (Iterator<String> iter = headers.iterator(); iter.hasNext(); ) {
                conn.addRequestProperty(iter.next(), iter.next());
            }
        }
        conn.addRequestProperty("Client-Version", MQVersion.getVersionDesc(MQVersion.CURRENT_VERSION));
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + encoding);

        String ts = String.valueOf(System.currentTimeMillis());
        conn.addRequestProperty("Metaq-Client-RequestTS", ts);
    }

    /**
     * @return the http response of given http post request
     */
    static public HttpResult httpPost(String url, List<String> headers, List<String> paramValues,
        String encoding, long readTimeoutMs) throws IOException {
        String encodedContent = encodingParams(paramValues, encoding);

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout((int) readTimeoutMs);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            setHeaders(conn, headers, encoding);

            conn.getOutputStream().write(encodedContent.getBytes(MixAll.DEFAULT_CHARSET));

            int respCode = conn.getResponseCode();
            String resp = null;

            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = IOTinyUtils.toString(conn.getInputStream(), encoding);
            } else {
                resp = IOTinyUtils.toString(conn.getErrorStream(), encoding);
            }
            return new HttpResult(respCode, resp);
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
    }
    //http 通信结果
    static public class HttpResult {
        final public int code;
        final public String content;

        public HttpResult(int code, String content) {
            this.code = code;
            this.content = content;
        }
    }
}
