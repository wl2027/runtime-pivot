package com.runtime.pivot.plugin.view.method;

import cn.hutool.core.collection.ListUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageUtil;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.domain.BacktrackingXBreakpoint;
import com.runtime.pivot.plugin.domain.BreakpointListItem;
import com.runtime.pivot.plugin.domain.MethodBacktrackingContext;
import com.runtime.pivot.plugin.enums.BreakpointType;
import com.runtime.pivot.plugin.listeners.XStackFrameListener;
import com.runtime.pivot.plugin.test.XDebuggerTestUtil;
import com.runtime.pivot.plugin.utils.RuntimePivotUtil;
import com.runtime.pivot.plugin.utils.StackFrameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BreakpointListDialog extends JDialog {
    private final Project project;
    private final XDebugSession xDebugSession;
    private XDebugSessionListener xDebugSessionListener;

    //private JList<BreakpointListItem> dataList;
    private JList<BacktrackingXBreakpoint> dataList;
    private List<BacktrackingXBreakpoint> backtrackingXBreakpointList = new ArrayList<>();

    public BreakpointListDialog(Project project, XDebugSession xDebugSession, List<BacktrackingXBreakpoint> backtrackingXBreakpointList) {
        super(WindowManager.getInstance().getFrame(project), "Debugger Breakpoint List", false);
        this.project = project;
        this.xDebugSession = xDebugSession;
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(300, 500);

        // 添加窗口关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        DefaultListModel<BacktrackingXBreakpoint> listModel = new DefaultListModel<>();
        this.backtrackingXBreakpointList.clear();
        this.backtrackingXBreakpointList.addAll(backtrackingXBreakpointList);
        // TODO 从栈底到栈顶
        for (BacktrackingXBreakpoint backtrackingXBreakpoint : backtrackingXBreakpointList) {
            listModel.addElement(backtrackingXBreakpoint);
        }
//        listModel.addElement(new BreakpointListItem("Item 1", BreakpointType.AVAILABLE));
//        listModel.addElement(new BreakpointListItem("Item 2", BreakpointType.AVAILABLE));
//        listModel.addElement(new BreakpointListItem("Item 3", BreakpointType.NOT_AVAILABLE));

        dataList = new JList<>(listModel);
        dataList.setCellRenderer(new ListItemRenderer());
        dataList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BacktrackingXBreakpoint selectedValue = dataList.getSelectedValue();
                if (e.getClickCount() == 1) {
                    //单击-导航
                    //System.out.println("Single click on: " + dataList.getSelectedValue().getText());
                    selectedValue.getxBreakpoint().getNavigatable().navigate(true);
                } else if (e.getClickCount() == 2) {
                    //双击-回溯
                    //System.out.println("Double click on: " + dataList.getSelectedValue().getText());
                    if (selectedValue.getBreakpointType() != BreakpointType.NOT_AVAILABLE) {
                        if (MessageUtil.showYesNoDialog(
                                "回溯到断点",
                                RuntimePivotUtil.getNextPositionName(selectedValue.getSourcePosition()),
                                project,
                                "确认",
                                "取消",
                                null
                        )) {
                            StackFrameUtils.invokeBacktracking(selectedValue);
                            dispose();
                        }
                    }
                }
            }
        });

        add(new JScrollPane(dataList), BorderLayout.CENTER);

        // 创建关闭按钮并添加到右下角
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            onClose();
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(WindowManager.getInstance().getFrame(project));

//        initListeners(xDebugSession);

    }

    public void close(){
        onClose();
        dispose();
    }

    private void initListeners(XDebugSession xDebugSession) {
        xDebugSessionListener = new XDebugSessionListener() {
            @Override
            public void sessionPaused() {
                //执行任何操作停下来+回溯成功时
                XDebugSessionListener.super.sessionPaused();
                updateData();
            }

            private void updateData() {
                //java.lang.Throwable: Read access is allowed from inside read-action (or EDT) only (see com.intellij.openapi.application.Application.runReadAction())
                XDebuggerManager debuggerManager = XDebuggerManager.getInstance(project);
                XBreakpointManager breakpointManager = debuggerManager.getBreakpointManager();
                XBreakpoint<?>[] allBreakpoints = breakpointManager.getAllBreakpoints();
                List<XBreakpoint<?>> xBreakpointList = ListUtil.of(allBreakpoints).stream()
                        .filter(bean -> bean.isEnabled())
                        .collect(Collectors.toList());
                List<XStackFrame> xStackFrames = XDebuggerTestUtil.collectFrames(xDebugSession);
                MethodBacktrackingContext methodBacktrackingContext = new MethodBacktrackingContext(
                        xBreakpointList,
                        xStackFrames,
                        xDebugSession
                );
                updateListData(methodBacktrackingContext.getBacktrackingXBreakpointList());
            }

            @Override
            public void stackFrameChanged() {
                //栈帧改变&线程切换时
                XDebugSessionListener.super.stackFrameChanged();
                updateData();
            }
        };
        xDebugSession.addSessionListener(xDebugSessionListener);
    }

    // 更新列表数据的方法
    public void updateListData(List<BacktrackingXBreakpoint> newData) {
        this.backtrackingXBreakpointList.clear();
        this.backtrackingXBreakpointList.addAll(newData);
        DefaultListModel<BacktrackingXBreakpoint> listModel = new DefaultListModel<>();
        for (BacktrackingXBreakpoint item : newData) {
            listModel.addElement(item);
        }
        dataList.setModel(listModel);
    }

    // 关闭窗口和点击关闭按钮时执行的操作
    private void onClose() {
//        xDebugSession.removeSessionListener(xDebugSessionListener);
        System.out.println("Dialog is closing");
        //TODO 关闭断点列表
        // 在这里添加你需要执行的操作
    }

    private static class ListItemRenderer extends JLabel implements ListCellRenderer<BacktrackingXBreakpoint> {
        @Override
        public Component getListCellRendererComponent(JList<? extends BacktrackingXBreakpoint> list, BacktrackingXBreakpoint value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.toString());
            setIcon(value.getIcon());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

    public List<BacktrackingXBreakpoint> getBacktrackingXBreakpointList() {
        return backtrackingXBreakpointList;
    }
}
