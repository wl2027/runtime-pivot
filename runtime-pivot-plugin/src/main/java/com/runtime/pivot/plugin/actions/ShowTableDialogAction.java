package com.runtime.pivot.plugin.actions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class ShowTableDialogAction extends AnAction {
    private TableDialog dialog;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (dialog == null || !dialog.isVisible()) {
            dialog = new TableDialog(project);
            dialog.setVisible(true);
        }
    }

    private static class TableDialog extends JDialog {
        private JTable table;
        private DefaultTableModel tableModel;
        private JTextArea textArea;
        private JButton clearButton;
        private Map<Integer, Object> rowDataMap;

        public TableDialog(Project project) {
            super(WindowManager.getInstance().getFrame(project), "Table Dialog", false);
            setLayout(new BorderLayout());
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setSize(600, 400);

            // 添加窗口关闭事件
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    onClose();
                }
            });

            // 创建表格模型和表格
            String[] columnNames = {"Column 1", "Column 2", "Column 3"};
            Object[][] data = {
                    {"Data 1", "Data 2", "Data 3"},
                    {"Data 4", "Data 5", "Data 6"},
                    {"Data 7", "Data 8", "Data 9"}
            };
            rowDataMap = new HashMap<>();
            for (int i = 0; i < data.length; i++) {
                rowDataMap.put(i, "Hidden Data " + (i + 1));  // 初始化隐藏数据
            }
            tableModel = new DefaultTableModel(data, columnNames);
            table = new JTable(tableModel);
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        Object hiddenData = rowDataMap.get(selectedRow);
                        onRowSelected(selectedRow, hiddenData);
                    }
                }
            });
            add(new JScrollPane(table), BorderLayout.CENTER);

            // 创建顶部按钮面板
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            clearButton = new JButton(new ImageIcon("path/to/clearIcon.png"));
            clearButton.addActionListener(e -> onClearButtonClicked());
            topPanel.add(clearButton);
            add(topPanel, BorderLayout.NORTH);

            // 创建底部文本区域和关闭按钮
            JPanel bottomPanel = new JPanel(new BorderLayout());

            textArea = new JTextArea(3, 20);
            textArea.setText("Initial text");
            bottomPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> {
                onClose();
                dispose();
            });

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(closeButton);
            bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(bottomPanel, BorderLayout.SOUTH);

            setLocationRelativeTo(WindowManager.getInstance().getFrame(project));
        }

        private void onClearButtonClicked() {
            System.out.println("Clear button clicked");
            tableModel.setRowCount(0);
            rowDataMap.clear();
            // 在这里添加清除表格内容时需要触发的其他事件
        }

        private void onRowSelected(int rowIndex, Object hiddenData) {
            System.out.println("Row " + rowIndex + " selected with hidden data: " + hiddenData);
            // 在这里处理行选择事件，并使用隐藏数据对象
        }

        // 更新表头和表格数据的方法
        public void updateTableData(String[] newColumnNames, Object[][] newData, Object[] hiddenDataArray) {
            tableModel.setDataVector(newData, newColumnNames);
            rowDataMap.clear();
            for (int i = 0; i < newData.length; i++) {
                rowDataMap.put(i, hiddenDataArray[i]);
            }
        }

        // 更新文本区域的方法
        public void updateTextArea(String newText) {
            textArea.setText(newText);
        }

        // 关闭窗口和点击关闭按钮时执行的操作
        private void onClose() {
            System.out.println("Dialog is closing");
            // 在这里添加你需要执行的操作
        }
    }
}
