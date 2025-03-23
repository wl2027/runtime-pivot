package com.runtime.pivot.plugin.actions.struct;

import com.intellij.openapi.project.Project;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.actions.StructAction;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;

public class ClassLoadingProcessAction extends StructAction {

    @Override
    protected void executeAttachCode(String object, String className, Project project) {
        String code = ActionExecutorUtil.buildCode(ActionType.Class.classLoadingProcess,null,object,ActionExecutorUtil.buildStringObject(className));
        getRuntimeContext().executeAttachCode(code);
    }
}
