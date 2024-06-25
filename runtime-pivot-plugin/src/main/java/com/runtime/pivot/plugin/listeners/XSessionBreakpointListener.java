package com.runtime.pivot.plugin.listeners;

import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerManagerListener;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import com.runtime.pivot.plugin.enums.XStackBreakpointType;
import com.runtime.pivot.plugin.model.XStackBreakpoint;
import com.runtime.pivot.plugin.model.XStackContext;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;
import com.runtime.pivot.plugin.utils.ProjectUtils;
import com.runtime.pivot.plugin.view.method.XSessionBreakpointDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

//关闭UI+更新UI
public class XSessionBreakpointListener implements XBreakpointListener {

    void updateData(BiConsumer<XDebugSession, XSessionBreakpointDialog> biConsumer){
        Map<XDebugSession, XSessionBreakpointDialog> sessionBreakpointDialogMap = RuntimePivotMethodService.getInstance().getSessionBreakpointDialogMap();
        sessionBreakpointDialogMap.forEach((session,dialog)->{
            if (dialog.isVisible()) {
                biConsumer.accept(session,dialog);
            }
        });
    }

    //添加断点不一定要加入到当前断点列表中,因为当前断点列表指的是已经过的断点,属于历史记录
    //不过还是需要-因为回溯时不将其记录进去会被影响而卡住
//    @Override
//    public void breakpointAdded(@NotNull XBreakpoint breakpoint) {
//        updateData((session,dialog)->{
//            dialog.updateData(XStackContext.getInstance(session));
//        });
//    }
//
//    @Override
//    public void breakpointRemoved(@NotNull XBreakpoint breakpoint) {
//        updateData((session,dialog)->{
//            List<XStackBreakpoint> xStackBreakpointList = dialog.getXStackBreakpointList();
//            List<XStackBreakpoint> collect = xStackBreakpointList.stream().filter(
//                    bean -> !bean.getXBreakpoint().equals(breakpoint)
//                    //bean-> !RuntimePivotUtil.compareBreakpoints(bean.getXBreakpoint(),breakpoint)
//            ).collect(Collectors.toList());
//            dialog.updateData(collect);
//        });
//    }
//
//    @Override
//    public void breakpointChanged(@NotNull XBreakpoint breakpoint) {
//        updateData((session,dialog)->{
//            List<XStackBreakpoint> xStackBreakpointList = dialog.getXStackBreakpointList();
//            xStackBreakpointList.forEach(XStackBreakpoint::updateType);
//            dialog.updateData(xStackBreakpointList);
//        });
//    }

    //当前线程相关则调用
    @Override
    public void breakpointPresentationUpdated(@NotNull XBreakpoint breakpoint, @Nullable XDebugSession session) {
        //断点视图已更新-调试会话启动也会调用,因为也属于断点视图更新
        //breakpointChanged,breakpointRemoved,breakpointAdded 都会调用
        XSessionBreakpointDialog xSessionBreakpointDialog = RuntimePivotMethodService.getInstance().getXSessionBreakpointDialog(session);
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
