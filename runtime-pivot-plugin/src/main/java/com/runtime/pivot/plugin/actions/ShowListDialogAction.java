package com.runtime.pivot.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.view.method.BreakpointListDialog;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ShowListDialogAction extends AnAction {
    private ListDialog dialog;
    private BreakpointListDialog breakpointListDialog;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
//        Project project = e.getProject();
//        if (dialog == null || !dialog.isVisible()) {
//            dialog = new ListDialog(project);
//            dialog.setVisible(true);
//        }
//        Project project = e.getProject();
//        if (breakpointListDialog == null || !breakpointListDialog.isVisible()) {
//            breakpointListDialog = new BreakpointListDialog(project, DebuggerUIUtil.getSession(e));
//            breakpointListDialog.setVisible(true);
//        }
    }

    private static class ListDialog extends JDialog {
        private JList<ListItem> dataList;

        public ListDialog(Project project) {
            super(WindowManager.getInstance().getFrame(project), "UI Debugger", false);
            setLayout(new BorderLayout());
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setSize(400, 300);

            // 添加窗口关闭事件
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    onClose();
                }
            });

            DefaultListModel<ListItem> listModel = new DefaultListModel<>();
            listModel.addElement(new ListItem("Item 1", new ImageIcon("path/to/icon1.png")));
            listModel.addElement(new ListItem("Item 2", new ImageIcon("path/to/icon2.png")));
            listModel.addElement(new ListItem("Item 3", new ImageIcon("path/to/icon3.png")));

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
        public void updateListData(List<ListItem> newData) {
            DefaultListModel<ListItem> listModel = new DefaultListModel<>();
            for (ListItem item : newData) {
                listModel.addElement(item);
            }
            dataList.setModel(listModel);
        }

        // 关闭窗口和点击关闭按钮时执行的操作
        private void onClose() {
            System.out.println("Dialog is closing");
            // 在这里添加你需要执行的操作
        }
    }

    private static class ListItem {
        private final String text;
        private final Icon icon;

        public ListItem(String text, Icon icon) {
            this.text = text;
            this.icon = icon;
        }

        public String getText() {
            return text;
        }

        public Icon getIcon() {
            return icon;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private static class ListItemRenderer extends JLabel implements ListCellRenderer<ListItem> {
        @Override
        public Component getListCellRendererComponent(JList<? extends ListItem> list, ListItem value, int index, boolean isSelected, boolean cellHasFocus) {
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
