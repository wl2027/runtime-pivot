package com.runtime.pivot.plugin.actions.object;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.intellij.xdebugger.impl.ui.tree.nodes.XEvaluationCallbackBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.runtime.pivot.agent.config.AgentConstants;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.actions.ObjectAction;
import com.runtime.pivot.plugin.utils.platfrom.XTestEvaluationCallback;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @see com.intellij.openapi.vcs.changes.patch.ApplyPatchAction
 * @see com.intellij.ide.actions.OpenFileAction
 */
public class ObjectLoadAction extends ObjectAction {

    @Override
    public void action(@NotNull AnActionEvent e) {
        XValueNodeImpl node = getSelectedNode(e.getDataContext());
        String name = node.getName();
        String script = name;
        if (node.getRawValue().equals("null")) {
            //空值不允许json转换,只能用返回对象接收
//            script = script+" = "+ActionExecutorUtil.RETURN_OBJECT;
        }
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true,false,false,false,false,false);
        @Nullable VirtualFile toSelect = null;
        try {
            toSelect = VfsUtil.createDirectories(e.getProject().getBasePath()+ AgentConstants.PATH);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        VirtualFile virtualFile = FileChooser.chooseFile(fileChooserDescriptor, e.getProject(), toSelect);
        String path = virtualFile.getPath();
        String code = ActionExecutorUtil.buildCode(ActionType.Object.objectLoad,null,name,ActionExecutorUtil.buildStringObject(path));
        //刷新当前文件
        virtualFile.refresh(false,false);
        //加载当前文件到对象
        getRuntimeContext().executeAttachCode(code, new XEvaluationCallbackBase() {
            @Override
            public void evaluated(@NotNull XValue result) {
                getRuntimeContext().getXDebugSession().rebuildViews();
            }

            @Override
            public void errorOccurred(@NotNull @NlsContexts.DialogMessage String errorMessage) {
                //TODO 弹窗提示
            }
        });

    }
}
