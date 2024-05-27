package com.runtime.pivot.agent;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.runtime.pivot.agent.config.AgentConstants;

import java.lang.reflect.Method;
import java.util.Date;

public class AgentConsole {
    public static synchronized void print(String actionType,Runnable runnable){
        String dateString = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
        System.out.println("IdUtil.class.getClassLoader():"+IdUtil.class.getClassLoader());
        for (Method declaredMethod : IdUtil.class.getDeclaredMethods()) {
            System.out.println("IdUtil.class.getDeclaredMethods():"+declaredMethod);
        }
        System.out.println("AgentConsole.class.getClassLoader():"+AgentConsole.class.getClassLoader());
        System.out.println("ContextClassLoader:"+Thread.currentThread().getContextClassLoader());
        String uuidString = "ID:"+IdUtil.nanoId(12);
        System.out.println(AgentConstants.ANSI_BOLD);
        System.out.println(StrUtil.format(AgentConstants.PRINT_START_STRING,uuidString));
        System.out.println("Action: "+actionType);
        System.out.println("Time: "+dateString+"\n");
        try {
            runnable.run();
        }catch (Exception e){
            System.out.println("RESULT : Error!");
            System.out.println("Error Message : "+e.getMessage());
            //IDEA捕获进行处理
            //throw e;
        } finally {
            System.out.println(StrUtil.format(AgentConstants.PRINT_END_STRING,uuidString));
            System.out.println(AgentConstants.RESET);
        }
    }
}
