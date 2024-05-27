package com.runtime.pivot.agent.providers;

import com.runtime.pivot.agent.model.Action;
import com.runtime.pivot.agent.model.ActionProvider;
import com.runtime.pivot.agent.model.ActionType;

public class ClassActionProvider extends ActionProvider<ActionType.Class> {
    @Action(ActionType.Class.dumpClass)
    public static void dumpClass(String classPath) {

    }

    @Action(ActionType.Class.dumpClassList)
    public static void dumpClassList(String packagePath) {

    }

    @Action(ActionType.Class.dumpObjectClass)
    public static void dumpObjectClass(Object object) {

    }

    @Action(ActionType.Class.classLoadingProcess)
    public static void classLoadingProcess(Object object) {

    }

}
