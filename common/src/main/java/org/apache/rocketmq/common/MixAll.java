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
package org.apache.rocketmq.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.rocketmq.common.annotation.ImportantField;
import org.apache.rocketmq.common.constant.LoggerName;
import org.apache.rocketmq.common.help.FAQUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 配置信息转化类和常量命名类
 * @author yuyang
 * @date 2018年5月24日
 */
public class MixAll {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.COMMON_LOGGER_NAME);
    //mq 主地址
    public static final String ROCKETMQ_HOME_ENV = "ROCKETMQ_HOME";
    //mq 主地址
    public static final String ROCKETMQ_HOME_PROPERTY = "rocketmq.home.dir";
    //nameserver 地址
    public static final String NAMESRV_ADDR_ENV = "NAMESRV_ADDR";
    //nameserver 地址
    public static final String NAMESRV_ADDR_PROPERTY = "rocketmq.namesrv.addr";
    public static final String MESSAGE_COMPRESS_LEVEL = "rocketmq.message.compressLevel";
    //默认的nameserver 地址
    public static final String DEFAULT_NAMESRV_ADDR_LOOKUP = "jmenv.tbsite.net";
    //nameserver domain 名称
    public static final String WS_DOMAIN_NAME = System.getProperty("rocketmq.namesrv.domain", DEFAULT_NAMESRV_ADDR_LOOKUP);
    //nameserver domain 群组
    public static final String WS_DOMAIN_SUBGROUP = System.getProperty("rocketmq.namesrv.domain.subgroup", "nsaddr");
    //http://jmenv.tbsite.net:8080/rocketmq/nsaddr
    //public static final String WS_ADDR = "http://" + WS_DOMAIN_NAME + ":8080/rocketmq/" + WS_DOMAIN_SUBGROUP;
    //系统默认主题名
    public static final String DEFAULT_TOPIC = "TBW102";
    //benchmark测试主题名
    public static final String BENCHMARK_TOPIC = "BenchmarkTest";
    public static final String DEFAULT_PRODUCER_GROUP = "DEFAULT_PRODUCER";
    public static final String DEFAULT_CONSUMER_GROUP = "DEFAULT_CONSUMER";
    //工具类消费者组名称
    public static final String TOOLS_CONSUMER_GROUP = "TOOLS_CONSUMER";
    //过滤服务消费者组名称
    public static final String FILTERSRV_CONSUMER_GROUP = "FILTERSRV_CONSUMER";
    public static final String MONITOR_CONSUMER_GROUP = "__MONITOR_CONSUMER";
    public static final String CLIENT_INNER_PRODUCER_GROUP = "CLIENT_INNER_PRODUCER";
    public static final String SELF_TEST_PRODUCER_GROUP = "SELF_TEST_P_GROUP";
    //自测消费者组
    public static final String SELF_TEST_CONSUMER_GROUP = "SELF_TEST_C_GROUP";
    //私有测试主题
    public static final String SELF_TEST_TOPIC = "SELF_TEST_TOPIC";
    //偏移量移动事件
    public static final String OFFSET_MOVED_EVENT = "OFFSET_MOVED_EVENT";
    //http 代理组名称
    public static final String ONS_HTTP_PROXY_GROUP = "CID_ONS-HTTP-PROXY";
    //api禁止 组名称
    public static final String CID_ONSAPI_PERMISSION_GROUP = "CID_ONSAPI_PERMISSION";
    //api自有 组名称
    public static final String CID_ONSAPI_OWNER_GROUP = "CID_ONSAPI_OWNER";
    //api pull 组名称
    public static final String CID_ONSAPI_PULL_GROUP = "CID_ONSAPI_PULL";
    public static final String CID_RMQ_SYS_PREFIX = "CID_RMQ_SYS_";

    public static final List<String> LOCAL_INET_ADDRESS = getLocalInetAddress();
    public static final String LOCALHOST = localhost();
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final long MASTER_ID = 0L;
    public static final long CURRENT_JVM_PID = getPID();

    public static final String RETRY_GROUP_TOPIC_PREFIX = "%RETRY%";

    public static final String DLQ_GROUP_TOPIC_PREFIX = "%DLQ%";
    public static final String SYSTEM_TOPIC_PREFIX = "rmq_sys_";
    public static final String UNIQUE_MSG_QUERY_FLAG = "_UNIQUE_KEY_QUERY";
    public static final String DEFAULT_TRACE_REGION_ID = "DefaultRegion";
    public static final String CONSUME_CONTEXT_TYPE = "ConsumeContextType";

    //获取ws 地址
    public static String getWSAddr() {
        String wsDomainName = System.getProperty("rocketmq.namesrv.domain", DEFAULT_NAMESRV_ADDR_LOOKUP);
        String wsDomainSubgroup = System.getProperty("rocketmq.namesrv.domain.subgroup", "nsaddr");
        String wsAddr = "http://" + wsDomainName + ":8080/rocketmq/" + wsDomainSubgroup;
        //如果有冒号则说明有端口了，所以拼接路径上就不再加端口了
        if (wsDomainName.indexOf(":") > 0) {
            wsAddr = "http://" + wsDomainName + "/rocketmq/" + wsDomainSubgroup;
        }
        return wsAddr;
    }

    public static String getRetryTopic(final String consumerGroup) {
        return RETRY_GROUP_TOPIC_PREFIX + consumerGroup;
    }

    public static boolean isSysConsumerGroup(final String consumerGroup) {
        return consumerGroup.startsWith(CID_RMQ_SYS_PREFIX);
    }

    public static boolean isSystemTopic(final String topic) {
        return topic.startsWith(SYSTEM_TOPIC_PREFIX);
    }

    public static String getDLQTopic(final String consumerGroup) {
        return DLQ_GROUP_TOPIC_PREFIX + consumerGroup;
    }
    
    /**
     * 获取渠道地址，就是地址
     * @param isChange  todo
     * @param brokerAddr  broker 地址
     * @return     
     * @return String      
     * @throws
     */
    public static String brokerVIPChannel(final boolean isChange, final String brokerAddr) {
        if (isChange) {
            String[] ipAndPort = brokerAddr.split(":");
            //设置新端口
            String brokerAddrNew = ipAndPort[0] + ":" + (Integer.parseInt(ipAndPort[1]) - 2);
            return brokerAddrNew;
        } else {
            return brokerAddr;
        }
    }

    public static long getPID() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        if (processName != null && processName.length() > 0) {
            try {
                return Long.parseLong(processName.split("@")[0]);
            } catch (Exception e) {
                return 0;
            }
        }

        return 0;
    }

    public static void string2File(final String str, final String fileName) throws IOException {

        String tmpFile = fileName + ".tmp";
        string2FileNotSafe(str, tmpFile);

        String bakFile = fileName + ".bak";
        String prevContent = file2String(fileName);
        if (prevContent != null) {
            string2FileNotSafe(prevContent, bakFile);
        }

        File file = new File(fileName);
        file.delete();

        file = new File(tmpFile);
        file.renameTo(new File(fileName));
    }

    /**
     * 把字符串写入文件
     * @param str  需要写入的字符串
     * @param fileName  文件名
     * @throws IOException     
     * @return void      
     * @throws
     */
    public static void string2FileNotSafe(final String str, final String fileName) throws IOException {
        File file = new File(fileName);
        File fileParent = file.getParentFile();
        if (fileParent != null) {
            fileParent.mkdirs();
        }
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(str);
        } catch (IOException e) {
            throw e;
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    /**
     * 文件转字符串
     * @param fileName  文件名  比如主题配置文件   topic.json 等
     * @return
     * @throws IOException     
     * @return String      
     * @throws
     */
    public static String file2String(final String fileName) throws IOException {
        File file = new File(fileName);
        return file2String(file);
    }

    /**
     * 文件转字符串
     * @param file  文件
     * @return
     * @throws IOException     
     * @return String      
     * @throws
     */
    public static String file2String(final File file) throws IOException {
        if (file.exists()) {
        	//用的是缓冲流
            byte[] data = new byte[(int) file.length()];
            boolean result;

            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                int len = inputStream.read(data);
                //判断文件的长度和读取的长度是否相等
                result = len == data.length;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            if (result) {
                return new String(data);
            }
        }
        return null;
    }

    public static String file2String(final URL url) {
        InputStream in = null;
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setUseCaches(false);
            in = urlConnection.getInputStream();
            int len = in.available();
            byte[] data = new byte[len];
            in.read(data, 0, len);
            return new String(data, "UTF-8");
        } catch (Exception ignored) {
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }

        return null;
    }
    /**
     * 打印配置的属性
     * @param  logger  日志工具类
     * @param  object  要打印的配置类   
     * @return void      
     * @throws
     */
    public static void printObjectProperties(final Logger logger, final Object object) {
        printObjectProperties(logger, object, false);
    }
    /**
     * 打印配置项属性
     * @param  logger  日志工具类
     * @param  object  要打印的配置类
     * @param  onlyImportantField     是否重要的属性的的选择  可以和注解搭配
     * @return void      
     * @throws
     */
    public static void printObjectProperties(final Logger logger, final Object object,
        final boolean onlyImportantField) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
        	//判断属性是否是静态的，不是静态的有打印的可能
            if (!Modifier.isStatic(field.getModifiers())) {
                String name = field.getName();
                if (!name.startsWith("this")) {
                    Object value = null;
                    try {
                        field.setAccessible(true);
                        value = field.get(object);
                        if (null == value) {//value 如果为null 则重新赋值空 
                            value = "";
                        }
                    } catch (IllegalAccessException e) {
                        log.error("Failed to obtain object properties", e);
                    }

                    if (onlyImportantField) {
                        Annotation annotation = field.getAnnotation(ImportantField.class);
                        if (null == annotation) {
                            continue;
                        }
                    }
                    //logger 不为null 才打印，为null 啥也不做	
                    if (logger != null) {
                        logger.info(name + "=" + value);
                    } else {
                    }
                }
            }
        }
    }

    public static String properties2String(final Properties properties) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getValue() != null) {
                sb.append(entry.getKey().toString() + "=" + entry.getValue().toString() + "\n");
            }
        }
        return sb.toString();
    }

    public static Properties string2Properties(final String str) {
        Properties properties = new Properties();
        try {
            InputStream in = new ByteArrayInputStream(str.getBytes(DEFAULT_CHARSET));
            properties.load(in);
        } catch (Exception e) {
            log.error("Failed to handle properties", e);
            return null;
        }

        return properties;
    }

    /**
     * 把配置类merge 到配置项中去
     * @param object  配置类
     * @return     
     * @return Properties      
     * @throws
     */
    public static Properties object2Properties(final Object object) {
        Properties properties = new Properties();

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                String name = field.getName();
                if (!name.startsWith("this")) {
                    Object value = null;
                    try {
                        field.setAccessible(true);
                        value = field.get(object);
                    } catch (IllegalAccessException e) {
                        log.error("Failed to handle properties", e);
                    }

                    if (value != null) {
                        properties.setProperty(name, value.toString());
                    }
                }
            }
        }

        return properties;
    }
    /**
     * 把配置文件的配置项信息传入配置类中
     * @param  p  配置项信息
     * @param  object 配置 类    
     * @return void      
     * @throws
     */
    public static void properties2Object(final Properties p, final Object object) {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            String mn = method.getName();//获取方法名
            if (mn.startsWith("set")) {
                try {
                    String tmp = mn.substring(4);
                    String first = mn.substring(3, 4);

                    String key = first.toLowerCase() + tmp;//获取方法的属性名
                    String property = p.getProperty(key);//获取对应的配置项的配置信息
                    if (property != null) {
                    	//获取方法的参数类型
                        Class<?>[] pt = method.getParameterTypes();
                        if (pt != null && pt.length > 0) {
                            String cn = pt[0].getSimpleName();
                            Object arg = null;
                            //根据参数类型转义获取实际类型的配置信息   其实可以省略的这个主意都是简单的类型
                            if (cn.equals("int") || cn.equals("Integer")) {
                                arg = Integer.parseInt(property);
                            } else if (cn.equals("long") || cn.equals("Long")) {
                                arg = Long.parseLong(property);
                            } else if (cn.equals("double") || cn.equals("Double")) {
                                arg = Double.parseDouble(property);
                            } else if (cn.equals("boolean") || cn.equals("Boolean")) {
                                arg = Boolean.parseBoolean(property);
                            } else if (cn.equals("float") || cn.equals("Float")) {
                                arg = Float.parseFloat(property);
                            } else if (cn.equals("String")) {
                                arg = property;
                            } else {
                                continue;
                            }
                            //反射方法设置变量
                            method.invoke(object, arg);
                        }
                    }
                } catch (Throwable ignored) {//报错 了不做处理
                }
            }
        }
    }

    public static boolean isPropertiesEqual(final Properties p1, final Properties p2) {
        return p1.equals(p2);
    }

    public static List<String> getLocalInetAddress() {
        List<String> inetAddressList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = enumeration.nextElement();
                Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    inetAddressList.add(addrs.nextElement().getHostAddress());
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("get local inet address fail", e);
        }

        return inetAddressList;
    }

    private static String localhost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Throwable e) {
            try {
                String candidatesHost = getLocalhostByNetworkInterface();
                if (candidatesHost != null)
                    return candidatesHost;

            } catch (Exception ignored) {
            }

            throw new RuntimeException("InetAddress java.net.InetAddress.getLocalHost() throws UnknownHostException" + FAQUrl.suggestTodo(FAQUrl.UNKNOWN_HOST_EXCEPTION), e);
        }
    }

    //Reverse logic comparing to RemotingUtil method, consider refactor in RocketMQ 5.0
    public static String getLocalhostByNetworkInterface() throws SocketException {
        List<String> candidatesHost = new ArrayList<String>();
        Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();

        while (enumeration.hasMoreElements()) {
            NetworkInterface networkInterface = enumeration.nextElement();
            // Workaround for docker0 bridge
            if ("docker0".equals(networkInterface.getName()) || !networkInterface.isUp()) {
                continue;
            }
            Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress address = addrs.nextElement();
                if (address.isLoopbackAddress()) {
                    continue;
                }
                //ip4 highter priority
                if (address instanceof Inet6Address) {
                    candidatesHost.add(address.getHostAddress());
                    continue;
                }
                return address.getHostAddress();
            }
        }

        if (!candidatesHost.isEmpty()) {
            return candidatesHost.get(0);
        }
        return null;
    }

    public static boolean compareAndIncreaseOnly(final AtomicLong target, final long value) {
        long prev = target.get();
        while (value > prev) {
            boolean updated = target.compareAndSet(prev, value);
            if (updated)
                return true;

            prev = target.get();
        }

        return false;
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
