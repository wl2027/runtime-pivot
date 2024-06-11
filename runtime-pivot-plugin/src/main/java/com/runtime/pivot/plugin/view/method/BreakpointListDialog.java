package com.runtime.pivot.plugin.view.method;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageUtil;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.xdebugger.XDebugSession;
import com.runtime.pivot.plugin.domain.BacktrackingXBreakpoint;
import com.runtime.pivot.plugin.domain.BreakpointListItem;
import com.runtime.pivot.plugin.enums.BreakpointType;
import com.runtime.pivot.plugin.listeners.XStackFrameListener;
import com.runtime.pivot.plugin.utils.RuntimePivotUtil;
import com.runtime.pivot.plugin.utils.StackFrameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class BreakpointListDialog extends JDialog {
    private final Project project;
    private final XDebugSession xDebugSession;

    //private JList<BreakpointListItem> dataList;
    private JList<BacktrackingXBreakpoint> dataList;

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

    // 更新列表数据的方法
    public void updateListData(List<BacktrackingXBreakpoint> newData) {
        DefaultListModel<BacktrackingXBreakpoint> listModel = new DefaultListModel<>();
        for (BacktrackingXBreakpoint item : newData) {
            listModel.addElement(item);
        }
        dataList.setModel(listModel);
    }

    // 关闭窗口和点击关闭按钮时执行的操作
    private void onClose() {
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
}
