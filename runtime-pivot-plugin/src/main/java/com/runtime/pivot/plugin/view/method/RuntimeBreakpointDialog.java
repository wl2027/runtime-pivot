package com.runtime.pivot.plugin.view.method;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageUtil;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.runtime.pivot.plugin.model.BacktrackingBreakpoint;
import com.runtime.pivot.plugin.model.RuntimeContext;
import com.runtime.pivot.plugin.enums.RuntimeBreakpointType;
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

public class RuntimeBreakpointDialog extends JDialog {
    private final Project project;
    private final XDebugSession xDebugSession;
    private XDebugSessionListener xDebugSessionListener;
    private JList<BacktrackingBreakpoint> dataList;
    private List<BacktrackingBreakpoint> backtrackingBreakpointList = new ArrayList<>();

    public RuntimeBreakpointDialog(Project project, XDebugSession xDebugSession, List<BacktrackingBreakpoint> backtrackingBreakpointList) {
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

        DefaultListModel<BacktrackingBreakpoint> listModel = new DefaultListModel<>();
        this.backtrackingBreakpointList.clear();
        this.backtrackingBreakpointList.addAll(backtrackingBreakpointList);
        // TODO 从栈底到栈顶
        for (BacktrackingBreakpoint backtrackingBreakpoint : backtrackingBreakpointList) {
            listModel.addElement(backtrackingBreakpoint);
        }

        dataList = new JList<>(listModel);
        dataList.setCellRenderer(new ListItemRenderer());
        dataList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BacktrackingBreakpoint selectedValue = dataList.getSelectedValue();
                if (e.getClickCount() == 1) {
                    //单击-导航
                    //System.out.println("Single click on: " + dataList.getSelectedValue().getText());
                    selectedValue.getxBreakpoint().getNavigatable().navigate(true);
                } else if (e.getClickCount() == 2) {
                    //双击-回溯
                    //System.out.println("Double click on: " + dataList.getSelectedValue().getText());
                    if (selectedValue.getBreakpointType() != RuntimeBreakpointType.NOT_AVAILABLE) {
                        if (MessageUtil.showYesNoDialog(
                                "回溯到断点",
                                RuntimePivotUtil.getNextPositionName(selectedValue.getSourcePosition()),
                                project,
                                "确认",
                                "取消",
                                null
                        )) {
                            StackFrameUtils.invokeBacktracking(selectedValue);
//                            StackFrameUtils.invokeBacktrackingTest(selectedValue);
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

    }

    public static RuntimeBreakpointDialog getInstance(Project project, XDebugSession xDebugSession, List<BacktrackingBreakpoint> backtrackingBreakpointList) {
        return new RuntimeBreakpointDialog(project,xDebugSession, backtrackingBreakpointList);
    }

    public void close(){
        onClose();
        dispose();
    }

    //一次性回溯,所以没必要做这种监听
    private void updateListeners(XDebugSession xDebugSession) {
        xDebugSessionListener = new XDebugSessionListener() {
            @Override
            public void sessionPaused() {
                //执行任何操作停下来+回溯成功时
                updateData();
            }

            private void updateData() {
                //java.lang.Throwable: Read access is allowed from inside read-action (or EDT) only (see com.intellij.openapi.application.Application.runReadAction())
                RuntimeContext runtimeContext = RuntimeContext.getInstance(xDebugSession);
                updateListData(runtimeContext.getBacktrackingXBreakpointList());
            }

            @Override
            public void stackFrameChanged() {
                //栈帧改变&线程切换时
                updateData();
            }
        };
        xDebugSession.addSessionListener(xDebugSessionListener);
    }

    // 更新列表数据的方法
    public void updateListData(List<BacktrackingBreakpoint> newData) {
        this.backtrackingBreakpointList.clear();
        this.backtrackingBreakpointList.addAll(newData);
        DefaultListModel<BacktrackingBreakpoint> listModel = new DefaultListModel<>();
        for (BacktrackingBreakpoint item : newData) {
            listModel.addElement(item);
        }
        dataList.setModel(listModel);
    }

    // 关闭窗口和点击关闭按钮时执行的操作
    private void onClose() {
//        xDebugSession.removeSessionListener(xDebugSessionListener);
        System.out.println("Dialog is closing");
        //TODO 关闭断点列表

    }

    private static class ListItemRenderer extends JLabel implements ListCellRenderer<BacktrackingBreakpoint> {
        @Override
        public Component getListCellRendererComponent(JList<? extends BacktrackingBreakpoint> list, BacktrackingBreakpoint value, int index, boolean isSelected, boolean cellHasFocus) {
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

    public List<BacktrackingBreakpoint> getBacktrackingXBreakpointList() {
        return backtrackingBreakpointList;
    }
}
