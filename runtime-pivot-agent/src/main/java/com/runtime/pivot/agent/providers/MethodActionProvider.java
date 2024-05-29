package com.runtime.pivot.agent.providers;

import com.runtime.pivot.agent.model.Action;
import com.runtime.pivot.agent.model.ActionProvider;
import com.runtime.pivot.agent.model.ActionType;

public class MethodActionProvider extends ActionProvider {

    @Action(ActionType.Method.trackTime)
    public static void trackTime(String info){
        System.out.println(info);
    }

    @Action(ActionType.Method.breakpointRestore)
    public static void breakpointRestore(String info){
        System.out.println(info);
    }

    @Action(ActionType.Method.exceptionInterrupt)
    public static void exceptionInterrupt(String info){
        System.out.println(info);
    }
}
