package com.runtime.pivot.plugin.listeners;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import com.runtime.pivot.plugin.model.XStackBreakpoint;
import com.runtime.pivot.plugin.model.XStackContext;
import com.runtime.pivot.plugin.service.RuntimePivotXSessionService;
import com.runtime.pivot.plugin.view.method.XSessionBreakpointDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

//关闭UI+更新UI
public class XSessionBreakpointListener implements XBreakpointListener {

    void updateData(BiConsumer<XDebugSession, XSessionBreakpointDialog> biConsumer){
        Map<XDebugSession, XSessionBreakpointDialog> sessionBreakpointDialogMap = RuntimePivotXSessionService.getInstance().getSessionBreakpointDialogMap();
        sessionBreakpointDialogMap.forEach((session,dialog)->{
            if (dialog.isVisible()) {
                biConsumer.accept(session,dialog);
            }
        });
    }

    @Override
    public void breakpointRemoved(@NotNull XBreakpoint breakpoint) {
        updateData((session,dialog)->{
            List<XStackBreakpoint> xStackBreakpointList = dialog.getXStackBreakpointList();
            List<XStackBreakpoint> collect = xStackBreakpointList.stream().filter(
                    bean -> !bean.getXBreakpoint().equals(breakpoint)
                    //bean-> !RuntimePivotUtil.compareBreakpoints(bean.getXBreakpoint(),breakpoint)
            ).collect(Collectors.toList());
            dialog.updateTreeData(collect);
        });
    }

    @Override
    public void breakpointChanged(@NotNull XBreakpoint breakpoint) {
        updateData((session,dialog)->{
            List<XStackBreakpoint> xStackBreakpointList = dialog.getXStackBreakpointList();
            xStackBreakpointList.forEach(XStackBreakpoint::updateType);
            dialog.updateTreeData(new ArrayList<>(xStackBreakpointList));
        });
    }

    //当前线程相关则调用
    @Override
    public void breakpointPresentationUpdated(@NotNull XBreakpoint breakpoint, @Nullable XDebugSession session) {
        //断点视图已更新-调试会话启动也会调用,因为也属于断点视图更新
        //breakpointChanged,breakpointRemoved,breakpointAdded 都会调用
        XSessionBreakpointDialog xSessionBreakpointDialog = RuntimePivotXSessionService.getInstance().getXSessionBreakpointDialog(session);
        if (xSessionBreakpointDialog != null) {
            if (xSessionBreakpointDialog.isVisible()) {
                XStackContext xStackContext = XStackContext.getInstance(session);
                if (xStackContext != null) {
                    xSessionBreakpointDialog.updateData(xStackContext);
                }
            }
        }
    }
}
