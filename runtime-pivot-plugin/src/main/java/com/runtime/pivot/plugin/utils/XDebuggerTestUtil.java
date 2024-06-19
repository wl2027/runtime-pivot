// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.runtime.pivot.plugin.utils;

import cn.hutool.core.util.StrUtil;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ExceptionUtil;
import com.intellij.util.ThrowableConvertor;
import com.intellij.util.ui.TextTransferable;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XBreakpointType;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XNavigatable;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueContainer;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.XDebuggerUtilImpl;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointUtil;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.frame.XStackFrameContainerEx;
import com.runtime.pivot.plugin.utils.platfrom.XTestCompositeNode;
import com.runtime.pivot.plugin.utils.platfrom.XTestContainer;
import com.runtime.pivot.plugin.utils.platfrom.XTestEvaluationCallback;
import com.runtime.pivot.plugin.utils.platfrom.XTestValueNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.concurrency.Promise;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;

@TestOnly
public class XDebuggerTestUtil {
//  public static final int TIMEOUT_MS = 25_000;
  public static final int TIMEOUT_MS = 5_000;

  XDebuggerTestUtil() {
  }

//  public static List<? extends XLineBreakpointType.XLineBreakpointVariant>
//  computeLineBreakpointVariants(Project project, VirtualFile file, int line) {
//    return computeLineBreakpointVariants(project, file, line, 0);
//  }

  //MY 计算行断点变量
//  public static List<? extends XLineBreakpointType.XLineBreakpointVariant>
//  computeLineBreakpointVariants(Project project, VirtualFile file, int line, int column) {
//    return ReadAction.compute(() -> {
//      List<XLineBreakpointType> types = StreamEx.of(XDebuggerUtil.getInstance().getLineBreakpointTypes())
//                                                .filter(type -> type.canPutAt(file, line, project))
//                                                .collect(Collectors.toCollection(SmartList::new));
//      return XDebuggerUtilImpl.getLineBreakpointVariantsSync(project, types, XSourcePositionImpl.create(file, line, column));
//    });
//  }

  //MY 切换断点
  @Nullable
  public static XLineBreakpoint toggleBreakpoint(Project project, VirtualFile file, int line) {
    final XDebuggerUtilImpl debuggerUtil = (XDebuggerUtilImpl)XDebuggerUtil.getInstance();
    final Promise<XLineBreakpoint> breakpointPromise = WriteAction.computeAndWait(
      () -> debuggerUtil.toggleAndReturnLineBreakpoint(project, file, line, false));
    try {
      return breakpointPromise.blockingGet(TIMEOUT_MS);
    }
    catch (TimeoutException e) {
      throw new RuntimeException(e);
    }
    catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  //MY 插入断点
  public static <P extends XBreakpointProperties> XBreakpoint<P> insertBreakpoint(final Project project,
                                                                                  final P properties,
                                                                                  final Class<? extends XBreakpointType<XBreakpoint<P>, P>> typeClass) {
    return XDebuggerManager.getInstance(project).getBreakpointManager()
      .addBreakpoint(XBreakpointType.EXTENSION_POINT_NAME.findExtension(typeClass), properties);
  }

  //MY 移除断点
//  public static void removeBreakpoint(@NotNull final Project project,
//                                      @NotNull final VirtualFile file,
//                                      final int line) {
//    XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();
//    WriteAction.runAndWait(() -> {
//      XLineBreakpoint<?> breakpoint = Arrays.stream(XDebuggerUtil.getInstance().getLineBreakpointTypes())
//        .map(t -> breakpointManager.findBreakpointAtLine(t, file, line))
//        .filter(nonNull())
//        .findFirst().orElse(null);
//      assertNotNull(breakpoint);
//      breakpointManager.removeBreakpoint(breakpoint);
//    });
//  }

  //获取活动线程
  public static @Nullable XExecutionStack getActiveThread(@NotNull XDebugSession session) {
    return session.getSuspendContext().getActiveExecutionStack();
  }

  //收集线程
  public static List<XExecutionStack> collectThreads(@NotNull XDebugSession session) {
    return collectThreads(session, TIMEOUT_MS);
  }

  public static List<XExecutionStack> collectThreads(@NotNull XDebugSession session, int timeoutMs) {
    return collectThreadsWithErrors(session, timeoutMs).first;
  }

  //MY 收集有错误线程
  public static Pair<List<XExecutionStack>, String> collectThreadsWithErrors(@NotNull XDebugSession session) {
    return collectThreadsWithErrors(session, TIMEOUT_MS);
  }

  public static Pair<List<XExecutionStack>, String> collectThreadsWithErrors(@NotNull XDebugSession session, int timeoutMs) {
    XTestExecutionStackContainer container = new XTestExecutionStackContainer();
    session.getSuspendContext().computeExecutionStacks(container);
    return container.waitFor(timeoutMs);
  }

  //收集栈帧
  public static List<XStackFrame> collectFrames(@NotNull XDebugSession session) {
    return collectFrames(null, session);
  }

  public static List<XStackFrame> collectFrames(@Nullable XExecutionStack thread, @NotNull XDebugSession session) {
    return collectFrames(thread == null ? Objects.requireNonNull(getActiveThread(session)) : thread);
  }

  //获取框架演示
  public static String getFramePresentation(XStackFrame frame) {
    TextTransferable.ColoredStringBuilder builder = new TextTransferable.ColoredStringBuilder();
    frame.customizePresentation(builder);
    return builder.getBuilder().toString();
  }

  public static List<XStackFrame> collectFrames(@NotNull XExecutionStack thread) {
    return collectFrames(thread, TIMEOUT_MS * 2);
  }

  public static List<XStackFrame> collectFrames(XExecutionStack thread, long timeout) {
    return collectFramesWithError(thread, timeout).first;
  }

  public static Pair<List<XStackFrame>, String> collectFramesWithError(XExecutionStack thread, long timeout) {
    XTestStackFrameContainer container = new XTestStackFrameContainer();
    thread.computeStackFrames(0, container);
    return container.waitFor(timeout);
  }

  //MY 获取选中的栈帧
  public static Pair<List<XStackFrame>, XStackFrame> collectFramesWithSelected(@NotNull XDebugSession session, long timeout) {
    return collectFramesWithSelected(Objects.requireNonNull(getActiveThread(session)), timeout);
  }

  public static Pair<List<XStackFrame>, XStackFrame> collectFramesWithSelected(XExecutionStack thread, long timeout) {
    XTestStackFrameContainer container = new XTestStackFrameContainer();
    thread.computeStackFrames(0, container);
    List<XStackFrame> all = container.waitFor(timeout).first;
    return Pair.create(all, container.frameToSelect);
  }

  public static XStackFrame getFrameAt(@NotNull XDebugSession session, int frameIndex) {
    return getFrameAt(Objects.requireNonNull(getActiveThread(session)), frameIndex);
  }

  public static XStackFrame getFrameAt(@NotNull XExecutionStack thread, int frameIndex) {
    return frameIndex == 0 ? thread.getTopFrame() : collectFrames(thread).get(frameIndex);
  }

  @NotNull
  public static List<XValue> collectChildren(XValueContainer value) {
    return new XTestCompositeNode(value).collectChildren();
  }

  @NotNull
  public static Pair<List<XValue>, String> collectChildrenWithError(XValueContainer value) {
    return new XTestCompositeNode(value).collectChildrenWithError();
  }

//  public static void waitForSwing() throws InterruptedException {
//    final com.intellij.util.concurrency.Semaphore s = new com.intellij.util.concurrency.Semaphore();
//    s.down();
//    ApplicationManager.getApplication().invokeLater(() -> s.up());
//    s.waitForUnsafe();
//    UIUtil.invokeAndWaitIfNeeded(() -> {});
//  }

  @NotNull
  public static XValue findVar(Collection<? extends XValue> vars, String name) {
    StringBuilder names = new StringBuilder();
    for (XValue each : vars) {
      if (each instanceof XNamedValue) {
        String eachName = ((XNamedValue)each).getName();
        if (eachName.equals(name)) return each;

        //if (!names.isEmpty()) names.append(", ");
        if (StrUtil.isNotEmpty(names)) {
          names.append(", ");
        }
        names.append(eachName);
      }
    }
    throw new AssertionError("var '" + name + "' not found among " + names);
  }

  public static XTestValueNode computePresentation(@NotNull XValue value) {
    return computePresentation(value, TIMEOUT_MS);
  }

  public static XTestValueNode computePresentation(XValue value, long timeout) {
    XTestValueNode node = new XTestValueNode();
    if (value instanceof XNamedValue) {
      node.myName = ((XNamedValue)value).getName();
    }
    value.computePresentation(node, XValuePlace.TREE);
    node.waitFor(timeout);
    return node;
  }

  public static <T> @Nullable T waitFor(@NotNull Future<T> future, long timeoutInMillis) {
    return waitFor(remaining -> {
      try {
        return future.get(remaining, TimeUnit.MILLISECONDS);
      }
      catch (TimeoutException e) {
        throw new InterruptedException();
      }
      catch (ExecutionException e) {
        Throwable cause = e.getCause();
        if (cause != null) {
          ExceptionUtil.rethrow(cause);
        }
        throw new RuntimeException(e);
      }
    }, timeoutInMillis);
  }

  public static boolean waitFor(@NotNull Semaphore semaphore, long timeoutInMillis) {
    return waitFor(remaining -> {
      if (semaphore.tryAcquire(remaining, TimeUnit.MILLISECONDS)) {
        return true;
      }
      throw new InterruptedException();
    }, timeoutInMillis) == Boolean.TRUE;
  }

  private static <T> @Nullable T waitFor(@NotNull ThrowableConvertor<? super Long, T, ? extends InterruptedException> waitFunction,
                                         long timeoutInMillis) {
    long end = System.currentTimeMillis() + timeoutInMillis;
    flushEventQueue();
    for (long remaining = timeoutInMillis; remaining > 0; remaining = end - System.currentTimeMillis()) {
      try {
        // 10ms is the sleep interval used by ProgressIndicatorUtils for busy-waiting.
        return waitFunction.convert(Math.min(10, remaining));
      }
      catch (InterruptedException ignored) {
      }
      finally {
        flushEventQueue();
      }
    }
    return null;
  }

//  public static void markValue(XValueMarkers<?, ?> markers, @NotNull XValue value, @NotNull ValueMarkup markup) {
//    try {
//      markers.markValue(value, markup).blockingGet(TIMEOUT_MS);
//    }
//    catch (TimeoutException | ExecutionException e) {
//      throw new RuntimeException(e);
//    }
//  }

  //MY Rider 需要这个，以便在等待 EDT 时能够接收来自后端的消息。 flush事件队列

  // Rider needs this in order to be able to receive messages from the backend when waiting on the EDT.
  private static void flushEventQueue() {
    if (ApplicationManager.getApplication().isDispatchThread()) {
      UIUtil.dispatchAllInvocationEvents();
    }
    else {
      UIUtil.pump();
    }
  }

  //MY 获取控制台文本
  @NotNull
  public static String getConsoleText(final @NotNull ConsoleViewImpl consoleView) {
    WriteAction.runAndWait(() -> consoleView.flushDeferredText());

    return consoleView.getEditor().getDocument().getText();
  }

  public static <T extends XBreakpointType> XBreakpoint addBreakpoint(@NotNull final Project project,
                                                                      @NotNull final Class<T> exceptionType,
                                                                      @NotNull final XBreakpointProperties properties) {
    XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();
    Ref<XBreakpoint> breakpoint = Ref.create(null);
    XBreakpointUtil.breakpointTypes()
                   .select(exceptionType)
                   .findFirst()
                   .ifPresent(type -> breakpoint.set(breakpointManager.addBreakpoint(type, properties)));
    return breakpoint.get();
  }

  //MY 删除所有断点
//  public static void removeAllBreakpoints(@NotNull Project project) {
//    XDebuggerUtilImpl.removeAllBreakpoints(project);
//  }

  //MY 获取断点
  public static XBreakpoint<?>[] getBreakpoints(final XBreakpointManager breakpointManager) {
    return breakpointManager.getAllBreakpoints();
  }

  //MY 将默认断点设置为已启用
  public static <B extends XBreakpoint<?>>
  void setDefaultBreakpointEnabled(@NotNull final Project project, Class<? extends XBreakpointType<B, ?>> bpTypeClass, boolean enabled) {
    final XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();
    XBreakpointType<B, ?> bpType = XDebuggerUtil.getInstance().findBreakpointType(bpTypeClass);
    Set<B> defaultBreakpoints = breakpointManager.getDefaultBreakpoints(bpType);
    for (B defaultBreakpoint : defaultBreakpoints) {
      defaultBreakpoint.setEnabled(enabled);
    }
  }

  //MY 设置断点条件
  public static void setBreakpointCondition(Project project, int line, final String condition) {
    XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();
    for (XBreakpoint breakpoint : getBreakpoints(breakpointManager)) {
      if (breakpoint instanceof XLineBreakpoint) {
        XLineBreakpoint lineBreakpoint = (XLineBreakpoint) breakpoint;
        if (lineBreakpoint.getLine() == line) {
          WriteAction.runAndWait(() -> lineBreakpoint.setCondition(condition));
        }
      }
    }
  }

  //MY 设置断点日志表达式
  public static void setBreakpointLogExpression(Project project, int line, final String logExpression) {
    XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();
    for (XBreakpoint breakpoint : getBreakpoints(breakpointManager)) {
      if (breakpoint instanceof XLineBreakpoint) {
        XLineBreakpoint lineBreakpoint = (XLineBreakpoint) breakpoint;
        if (lineBreakpoint.getLine() == line) {
          WriteAction.runAndWait(() -> {
            lineBreakpoint.setLogExpression(logExpression);
            lineBreakpoint.setLogMessage(true);
          });
        }
      }
    }
  }

  //MY 释放调试会话
  public static void disposeDebugSession(final XDebugSession debugSession) {
    WriteAction.runAndWait(() -> {
      XDebugSessionImpl session = (XDebugSessionImpl)debugSession;
      Disposer.dispose(session.getSessionTab());
      Disposer.dispose(session.getConsoleView());
    });
  }

  public static class XTestExecutionStackContainer extends XTestContainer<XExecutionStack> implements XSuspendContext.XExecutionStackContainer {
    @Override
    public void errorOccurred(@NotNull String errorMessage) {
      setErrorMessage(errorMessage);
    }

    @Override
    public void addExecutionStack(@NotNull List<? extends XExecutionStack> executionStacks, boolean last) {
      addChildren(executionStacks, last);
    }
  }

  public static class XTestStackFrameContainer extends XTestContainer<XStackFrame> implements XStackFrameContainerEx {
    public volatile XStackFrame frameToSelect;

    @Override
    public void addStackFrames(@NotNull List<? extends XStackFrame> stackFrames, boolean last) {
      addChildren(stackFrames, last);
    }

    @Override
    public void addStackFrames(@NotNull List<? extends XStackFrame> stackFrames, @Nullable XStackFrame toSelect, boolean last) {
      if (toSelect != null) frameToSelect = toSelect;
      addChildren(stackFrames, last);
    }

    @Override
    public void errorOccurred(@NotNull String errorMessage) {
      setErrorMessage(errorMessage);
    }
  }

  public static class XTestNavigatable implements XNavigatable {
    private XSourcePosition myPosition;

    @Override
    public void setSourcePosition(@Nullable XSourcePosition sourcePosition) {
      myPosition = sourcePosition;
    }

    public XSourcePosition getPosition() {
      return myPosition;
    }
  }
}
