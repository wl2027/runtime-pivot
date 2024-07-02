package com.runtime.pivot.agent;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.runtime.pivot.agent.config.AgentConstants;

import java.util.Date;

public class AgentConsole {
    public static void print(String actionType,Runnable runnable){
        synchronized (System.out){
            Date date = new Date();
            String datePrintString = DateUtil.format(date, DatePattern.NORM_DATETIME_MS_PATTERN);
            String dateFileString = DateUtil.format(date, DatePattern.PURE_DATETIME_PATTERN);
            String uid = "ID:"+IdUtil.nanoId(12);
            ActionContext actionContext = new ActionContext();
            actionContext.setAction(actionType);
            actionContext.setUid(uid);
            actionContext.setDatePrintString(datePrintString);
            actionContext.setDateFileString(dateFileString);
            actionContext.setDate(date);
            ActionExecutor.initActionContext(actionContext);
            System.out.println(AgentConstants.ANSI_BOLD);
            System.out.println(StrUtil.format(AgentConstants.PRINT_START_STRING,uid));
            System.out.println("Action: "+actionType);
            System.out.println("Time: "+datePrintString);
            System.out.println(AgentConstants.RESET);
            try {
                runnable.run();
            }catch (Exception e){
                //TODO 异常打印无法嵌套进去
                if (AgentConstants.DEBUG) {
                    e.printStackTrace();
                }
                //IDEA捕获进行处理
                //throw e;
            } finally {
                ActionExecutor.removeActionContext();
                System.out.print(AgentConstants.RESET);
                System.out.print(AgentConstants.ANSI_BOLD);
                System.out.println(StrUtil.format(AgentConstants.PRINT_END_STRING,uid));
                System.out.println(AgentConstants.RESET);
            }
        }
    }
}
