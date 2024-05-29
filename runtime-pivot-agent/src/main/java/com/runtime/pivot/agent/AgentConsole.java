package com.runtime.pivot.agent;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.runtime.pivot.agent.config.AgentConstants;

import java.util.Date;

public class AgentConsole {
    public static synchronized void print(String actionType,Runnable runnable){
        String dateString = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
        String uuidString = "ID:"+IdUtil.nanoId(12);
        System.out.println(AgentConstants.ANSI_BOLD);
        System.out.println(StrUtil.format(AgentConstants.PRINT_START_STRING,uuidString));
        System.out.println("Action: "+actionType);
        System.out.println("Time: "+dateString);
        System.out.println(AgentConstants.RESET);
        try {
            runnable.run();
        }catch (Exception e){
            //TODO 异常打印无法嵌套进去
            System.out.print(AgentConstants.ANSI_BOLD);
            System.err.println("RESULT : Error!");
            System.err.println("Error Message : "+e.getMessage());
            if (AgentConstants.DEBUG) {
                e.printStackTrace();
            }
            //IDEA捕获进行处理
            //throw e;
        } finally {
            System.out.print(AgentConstants.RESET);
            System.out.print(AgentConstants.ANSI_BOLD);
            System.out.println(StrUtil.format(AgentConstants.PRINT_END_STRING,uuidString));
            System.out.println(AgentConstants.RESET);
        }
    }
}
