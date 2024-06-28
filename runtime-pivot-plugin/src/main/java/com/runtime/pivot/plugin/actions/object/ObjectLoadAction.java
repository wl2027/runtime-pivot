package com.runtime.pivot.plugin.actions.object;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.ui.tree.nodes.XEvaluationCallbackBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.runtime.pivot.agent.config.AgentConstants;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.actions.ObjectAction;
import com.runtime.pivot.plugin.config.RuntimePivotConstants;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @see com.intellij.openapi.vcs.changes.patch.ApplyPatchAction
 * @see com.intellij.ide.actions.OpenFileAction
 * @see com.intellij.ide.actions.SaveAllAction
 */
public class ObjectLoadAction extends ObjectAction {

    /**
     * TODO 解决空值问题
     * java.lang.Class<?> actionExecutorClass = java.lang.ClassLoader.getSystemClassLoader().loadClass("com.runtime.pivot.agent.ActionExecutor");
     * java.lang.reflect.Method method = actionExecutorClass.getMethod("execute",String.class,Object[].class);
     * Object returnObject = method.invoke(null,"objectLoad",new Object[]{aaa,new String("E:/002_Code/000_github/APM/apm-demo/.runtime/objectStore/20240628133808/com/wl/apm/APMApplicationMainMaster$1@979532928.json")});
     * ;
     * @param e
     * @throws Exception
     */
    @Override
    public void action(@NotNull AnActionEvent e) throws Exception{
        XValueNodeImpl node = getSelectedNode(e.getDataContext());
        String name = node.getName();
        if (node.getRawValue().equals("null")) {
            //空值不允许json转换,只能用返回对象接收
            Messages.showErrorDialog("对象不能为null", RuntimePivotConstants.ERROR_MSG_TITLE);
            return;
        }
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true,false,false,false,false,false);
        @Nullable VirtualFile toSelect = VfsUtil.createDirectories(e.getProject().getBasePath()+ AgentConstants.PATH);
        VirtualFile virtualFile = FileChooser.chooseFile(fileChooserDescriptor, e.getProject(), toSelect);
        //保存文件
        FileDocumentManager.getInstance().saveAllDocuments();
        String path = virtualFile.getPath();
        String code = ActionExecutorUtil.buildCode(ActionType.Object.objectLoad,null,name,ActionExecutorUtil.buildStringObject(path));
        //刷新当前文件
        virtualFile.refresh(false,false);
        //加载当前文件到对象
        getRuntimeContext().executeAttachCode(code, (javaValue)->getRuntimeContext().getXDebugSession().rebuildViews());
    }
}
