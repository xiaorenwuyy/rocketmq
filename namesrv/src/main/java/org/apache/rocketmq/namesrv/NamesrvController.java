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
package org.apache.rocketmq.namesrv;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.rocketmq.common.Configuration;
import org.apache.rocketmq.common.ThreadFactoryImpl;
import org.apache.rocketmq.common.constant.LoggerName;
import org.apache.rocketmq.common.namesrv.NamesrvConfig;
import org.apache.rocketmq.namesrv.kvconfig.KVConfigManager;
import org.apache.rocketmq.namesrv.processor.ClusterTestRequestProcessor;
import org.apache.rocketmq.namesrv.processor.DefaultRequestProcessor;
import org.apache.rocketmq.namesrv.routeinfo.BrokerHousekeepingService;
import org.apache.rocketmq.namesrv.routeinfo.RouteInfoManager;
import org.apache.rocketmq.remoting.RemotingServer;
import org.apache.rocketmq.remoting.netty.NettyRemotingServer;
import org.apache.rocketmq.remoting.netty.NettyServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * nameserver controller 
 * @author yuyang
 * @date 2018年5月25日
 */
public class NamesrvController {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.NAMESRV_LOGGER_NAME);
    //nameserver 配置类
    private final NamesrvConfig namesrvConfig;
    //netty 通信服务器配置类
    private final NettyServerConfig nettyServerConfig;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
        "NSScheduledThread"));
    private final KVConfigManager kvConfigManager;
    
    //路由器信息管理器
    private final RouteInfoManager routeInfoManager;

    private RemotingServer remotingServer;

    //传给netty 的事件监听器
    private BrokerHousekeepingService brokerHousekeepingService;
    
    //默认的工作执行线程
    private ExecutorService remotingExecutor;
    //总的配置类
    private Configuration configuration;

    public NamesrvController(NamesrvConfig namesrvConfig, NettyServerConfig nettyServerConfig) {
        this.namesrvConfig = namesrvConfig;
        this.nettyServerConfig = nettyServerConfig;
        this.kvConfigManager = new KVConfigManager(this);
        this.routeInfoManager = new RouteInfoManager();//路由信息管理器
        this.brokerHousekeepingService = new BrokerHousekeepingService(this);//放入nameserver controller 的引用备后用
        this.configuration = new Configuration(
            log,
            this.namesrvConfig, this.nettyServerConfig
        );
        this.configuration.setStorePathFromConfig(this.namesrvConfig, "configStorePath");
    }
    /**
     * controller 初始化
     * @return boolean      
     * @throws
     */
    public boolean initialize() {
    	//key value 配置管理器 把配置项放入也配置表中
        this.kvConfigManager.load();
        //netty 通信服务器
        this.remotingServer = new NettyRemotingServer(this.nettyServerConfig, this.brokerHousekeepingService);
        
        //netty 执行线程  netty 处理请求时用
        this.remotingExecutor =
            Executors.newFixedThreadPool(nettyServerConfig.getServerWorkerThreads(), new ThreadFactoryImpl("RemotingExecutorThread_"));
        
        //注册请求处理器
        this.registerProcessor();

        //扫描过期的broker,如果查到就销毁  10秒钟扫描一次
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                NamesrvController.this.routeInfoManager.scanNotActiveBroker();
            }
        }, 5, 10, TimeUnit.SECONDS);
        
        //打印nameserver 所有的配置  10分钟扫描一次
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                NamesrvController.this.kvConfigManager.printAllPeriodically();
            }
        }, 1, 10, TimeUnit.MINUTES);

        return true;
    }
    
    /**
     * 注册执行处理器  用netty 通信服务器注册
     *      
     * @return void      
     * @throws
     */
    private void registerProcessor() {
        if (namesrvConfig.isClusterTest()) {

            this.remotingServer.registerDefaultProcessor(new ClusterTestRequestProcessor(this, namesrvConfig.getProductEnvName()),
                this.remotingExecutor);
        } else {
        	//netty 服务器注册请求处理器，用的是默认的请求处理器	和执行线程
            this.remotingServer.registerDefaultProcessor(new DefaultRequestProcessor(this), this.remotingExecutor);
        }
    }
    /**
     * 服务启动，直接调用的netty 服务器启动方法
     * @throws Exception     
     * @return void      
     * @throws
     */
    public void start() throws Exception {
        this.remotingServer.start();
    }
    
    /**
     * 异常或者是正常controller 关闭
     *      
     * @return void      
     * @throws
     */
    public void shutdown() {
    	//netty 服务器关闭
        this.remotingServer.shutdown();
        //netty 工作线程池关闭
        this.remotingExecutor.shutdown();
        //规划表实现关闭
        this.scheduledExecutorService.shutdown();
    }

    public NamesrvConfig getNamesrvConfig() {
        return namesrvConfig;
    }

    public NettyServerConfig getNettyServerConfig() {
        return nettyServerConfig;
    }

    public KVConfigManager getKvConfigManager() {
        return kvConfigManager;
    }

    public RouteInfoManager getRouteInfoManager() {
        return routeInfoManager;
    }

    public RemotingServer getRemotingServer() {
        return remotingServer;
    }

    public void setRemotingServer(RemotingServer remotingServer) {
        this.remotingServer = remotingServer;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
