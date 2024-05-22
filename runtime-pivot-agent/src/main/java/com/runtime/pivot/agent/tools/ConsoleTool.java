package com.runtime.pivot.agent.tools;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.runtime.pivot.agent.AgentConstants;
import com.runtime.pivot.agent.config.ActionType;

import java.util.Date;

public class ConsoleTool {
    public static synchronized void print(ActionType actionType,Runnable runnable){
        String dateString = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
        String uuidString = "ID:"+IdUtil.nanoId(12);
        System.out.println(AgentConstants.ANSI_BOLD);
        System.out.println(StrUtil.format(AgentConstants.PRINT_START_STRING,uuidString));
        System.out.println("Action: "+actionType.toString());
        System.out.println("Time: "+dateString);
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
