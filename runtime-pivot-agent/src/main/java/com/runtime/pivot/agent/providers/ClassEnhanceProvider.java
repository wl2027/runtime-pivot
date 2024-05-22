package com.runtime.pivot.agent.providers;

import com.runtime.pivot.agent.config.ActionType;
import com.runtime.pivot.agent.tools.ConsoleTool;
import com.runtime.pivot.agent.tools.ExpressionTool;

public class ClassEnhanceProvider extends EnhanceProvider {
    public static void dumpClass(String classPath) {

        ConsoleTool.print(ActionType.Class.dumpClass,()->{

        });
    }

    public static void dumpClassList(String packagePath) {
        ConsoleTool.print(ActionType.Class.dumpClassList,()->{

        });
    }
    public static void dumpObjectClass(Object object) {
        ConsoleTool.print(ActionType.Class.dumpObjectClass,()->{

        });
    }

}
