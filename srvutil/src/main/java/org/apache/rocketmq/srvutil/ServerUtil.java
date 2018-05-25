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
package org.apache.rocketmq.srvutil;

import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
/**
 * 服务工具类 主要给启动命令用的
 * @author yuyang
 * @date 2018年5月24日
 */
public class ServerUtil {
	/**
	 * 构建命令行属性 有 h-help 和 n name server 帮助和nameserver
	 * @param  options    
	 * @return Options      
	 * @throws
	 */
    public static Options buildCommandlineOptions(final Options options) {
        Option opt = new Option("h", "help", false, "Print help");//帮助
        opt.setRequired(false);//false 可选
        options.addOption(opt);
        
        //nameserver 列表
        opt =
            new Option("n", "namesrvAddr", true,
                "Name server address list, eg: 192.168.0.1:9876;192.168.0.2:9876");
        opt.setRequired(false);
        options.addOption(opt);

        return options;
    }
    /**
     * 解析命令行，分别传入
     * @param  appName  在命令行帮助栏中显示 usage 后面
     * @param  args  命令行传入的参数
     * @param  options  服务已经构建的属性项目
     * @param  parser   命令行参数解析器
     * @return CommandLine   解析成功会返回命令行，如果解析成功则打印帮助信息然后返回初始化为null的命令行 commandline   
     * @throws
     */
    public static CommandLine parseCmdLine(final String appName, String[] args, Options options,
        CommandLineParser parser) {
        HelpFormatter hf = new HelpFormatter();//打印格式化 器
        hf.setWidth(110);
        CommandLine commandLine = null;
        try {
        	//用命令行解析器去解析传入的参数
            commandLine = parser.parse(options, args);
            //判断是否传入h 属性,如果有传入则打印出帮助信息
            if (commandLine.hasOption('h')) {
                hf.printHelp(appName, options, true);
                return null;
            }
        } catch (ParseException e) {//解析报错，也要打印出帮助信息
            hf.printHelp(appName, options, true);
        }
        
        return commandLine;
    }

    public static void printCommandLineHelp(final String appName, final Options options) {
        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        hf.printHelp(appName, options, true);
    }

    public static Properties commandLine2Properties(final CommandLine commandLine) {
        Properties properties = new Properties();
        Option[] opts = commandLine.getOptions();

        if (opts != null) {
            for (Option opt : opts) {
                String name = opt.getLongOpt();
                String value = commandLine.getOptionValue(name);
                if (value != null) {
                    properties.setProperty(name, value);
                }
            }
        }

        return properties;
    }

}
