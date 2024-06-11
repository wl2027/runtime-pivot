package com.runtime.pivot.plugin.view.method;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.xdebugger.XDebugSession;
import com.runtime.pivot.plugin.enums.BreakpointType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
public class BreakpointListDialog extends JDialog{
    private final Project project;
    private final XDebugSession xDebugSession;

    private JList<BreakpointListItem> dataList;

    public BreakpointListDialog(Project project,XDebugSession xDebugSession) {
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

        DefaultListModel<BreakpointListItem> listModel = new DefaultListModel<>();
        listModel.addElement(new BreakpointListItem("Item 1", BreakpointType.AVAILABLE));
        listModel.addElement(new BreakpointListItem("Item 2", BreakpointType.AVAILABLE));
        listModel.addElement(new BreakpointListItem("Item 3", BreakpointType.NOT_AVAILABLE));

        dataList = new JList<>(listModel);
        dataList.setCellRenderer(new ListItemRenderer());
        dataList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    System.out.println("Single click on: " + dataList.getSelectedValue().getText());
                } else if (e.getClickCount() == 2) {
                    System.out.println("Double click on: " + dataList.getSelectedValue().getText());
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
    public void updateListData(List<BreakpointListItem> newData) {
        DefaultListModel<BreakpointListItem> listModel = new DefaultListModel<>();
        for (BreakpointListItem item : newData) {
            listModel.addElement(item);
        }
        dataList.setModel(listModel);
    }

    // 关闭窗口和点击关闭按钮时执行的操作
    private void onClose() {
        System.out.println("Dialog is closing");
        // 在这里添加你需要执行的操作
    }

    private static class ListItemRenderer extends JLabel implements ListCellRenderer<BreakpointListItem> {
        @Override
        public Component getListCellRendererComponent(JList<? extends BreakpointListItem> list, BreakpointListItem value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.getText());
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
