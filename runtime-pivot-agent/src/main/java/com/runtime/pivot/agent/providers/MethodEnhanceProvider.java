package com.runtime.pivot.agent.providers;

import com.runtime.pivot.agent.config.ActionType;
import com.runtime.pivot.agent.tools.ConsoleTool;

public class MethodEnhanceProvider extends EnhanceProvider {
    public static void trackTime(String info){
        ConsoleTool.print(ActionType.Method.trackTime,()->System.out.println(info));
    }
}
