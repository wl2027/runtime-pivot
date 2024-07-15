package com.runtime.pivot.agent.providers;

import com.runtime.pivot.agent.config.AgentConstants;
import com.runtime.pivot.agent.model.Action;
import com.runtime.pivot.agent.model.ActionProvider;
import com.runtime.pivot.agent.model.ActionType;

public class MethodActionProvider extends ActionProvider {

    @Action(ActionType.Method.trackTime)
    public static void trackTime(String info){
        if (AgentConstants.DEBUG){
            throw new RuntimeException(new ClassNotFoundException());
        }
        System.out.println(info);
    }

    @Action(ActionType.Method.breakpointRestore)
    public static void breakpointRestore(String info){
        System.out.println("breakpoint restore success ...");
    }

    @Action(ActionType.Method.exceptionRestore)
    public static void exceptionRestore(String info){
        System.out.println("exception interrupted listener success ...");
    }
}
