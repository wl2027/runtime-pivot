package com.runtime.pivot.agent.providers;

import com.runtime.pivot.agent.model.Action;
import com.runtime.pivot.agent.model.ActionProvider;
import com.runtime.pivot.agent.model.ActionType;

public class ClassActionProvider extends ActionProvider<ActionType.Class> {

    @Action(ActionType.Class.classLoadingProcess)
    public static void classLoadingProcess(Object object) {
        //class每次加载的时间和classLoad
    }
    @Action(ActionType.Class.dumpClass)
    public static void dumpClass(String className) {
        //精准查询
    }

    @Action(ActionType.Class.dumpClassList)
    public static void dumpClassList(String className) {
        //模糊查询

    }

    @Action(ActionType.Class.dumpObjectClass)
    public static void dumpObjectClass(Object object) {
        //精准查询
    }



}
