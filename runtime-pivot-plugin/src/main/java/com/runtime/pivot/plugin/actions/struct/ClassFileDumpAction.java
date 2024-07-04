package com.runtime.pivot.plugin.actions.struct;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.actions.StructAction;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;


public class ClassFileDumpAction extends StructAction {

    @Override
    protected void executeAttachCode(String object, String className, Project project) {
        String code = ActionExecutorUtil.buildCode(ActionType.Class.classFileDump,null,object,ActionExecutorUtil.buildStringObject(className),ActionExecutorUtil.buildStringObject(project.getBasePath()));
        getRuntimeContext().executeAttachCode(code, (javaValue)->{
            VirtualFile baseDir = ProjectUtil.guessProjectDir(project);
            VirtualFile child = baseDir.findChild(".runtime");
            if (child == null) {
                baseDir.getFileSystem().refresh(false);
            }else {
                child.getFileSystem().refresh(false);
            }
        });
    }
}
