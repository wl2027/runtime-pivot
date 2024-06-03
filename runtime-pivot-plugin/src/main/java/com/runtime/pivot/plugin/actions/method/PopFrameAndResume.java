//import com.intellij.debugger.DebuggerManagerEx;
//import com.intellij.debugger.engine.DebugProcessImpl;
//import com.intellij.debugger.engine.SuspendContextImpl;
//import com.intellij.debugger.engine.events.DebuggerCommandImpl;
//import com.intellij.debugger.impl.DebuggerContextImpl;
//import com.intellij.openapi.project.Project;
//import com.sun.jdi.ThreadReference;
//import com.sun.jdi.VirtualMachine;
//import com.sun.jdi.request.StepRequest;
//
//public class PopFrameAndResume {
//
//    public static void popFrameAndResume(Project project) {
//        DebuggerManagerEx debuggerManager = DebuggerManagerEx.getInstanceEx(project);
//        DebugProcessImpl debugProcess = (DebugProcessImpl) debuggerManager.getContext().getDebugProcess();
//
//        SuspendContextImpl suspendContext = debugProcess.getSuspendManager().getPausedContext();
//        DebuggerContextImpl debuggerContext = debuggerManager.getContext();
//
//        if (suspendContext != null) {
//            try {
//                ThreadReference thread = suspendContext.getThread().getThreadReference();
//                VirtualMachine vm = thread.virtualMachine();
//                vm.popFrames(thread.frame(0)); // 弹出当前栈帧
//
//                debugProcess.getManagerThread().schedule(new DebuggerCommandImpl() {
//                    @Override
//                    protected void action() {
//                        // 创建一个步进请求，以确保恢复执行到下一个断点
//                        StepRequest stepRequest = vm.eventRequestManager().createStepRequest(thread, StepRequest.STEP_LINE, StepRequest.STEP_OVER);
//                        stepRequest.addCountFilter(1); // 执行一步
//                        stepRequest.enable();
//
//                        // 恢复执行
//                        debugProcess.resume(suspendContext);
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
