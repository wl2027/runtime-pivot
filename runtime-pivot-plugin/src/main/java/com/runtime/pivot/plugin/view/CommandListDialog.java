package com.runtime.pivot.plugin.view;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CommandListDialog extends DialogWrapper {

    private final JList<CommandItem> list;

    public CommandListDialog(List<CommandItem> commands) {
        super(true);
        setTitle("Command List");

        list = new JList<>(new DefaultListModel<>());
        DefaultListModel<CommandItem> listModel = (DefaultListModel<CommandItem>) list.getModel();
        for (CommandItem command : commands) {
            listModel.addElement(command);
        }

        list.setCellRenderer(new CommandListCellRenderer());

        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        // 单击和双击事件
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = list.locationToIndex(e.getPoint());
                    CommandItem item = list.getModel().getElementAt(index);
                    onSingleClick(item);
                } else if (e.getClickCount() == 2) {
                    int index = list.locationToIndex(e.getPoint());
                    CommandItem item = list.getModel().getElementAt(index);
                    onDoubleClick(item);
                }
            }
        });

        return panel;
    }

    private void onSingleClick(CommandItem item) {
        // 单击事件处理
        System.out.println("Single Clicked: " + item.getCommand());
    }

    private void onDoubleClick(CommandItem item) {
        // 双击事件处理
        System.out.println("Double Clicked: " + item.getCommand());
    }

    private static class CommandListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            CommandItem item = (CommandItem) value;
            label.setText(item.getCommand());
            label.setIcon(item.getIcon());
            return label;
        }
    }

    public static void main(String[] args) {
        List<CommandItem> commands = List.of(
                new CommandItem("Command 1", new ImageIcon("icon1.png")),
                new CommandItem("Command 2", new ImageIcon("icon2.png")),
                new CommandItem("Command 3", new ImageIcon("icon3.png"))
        );

        CommandListDialog dialog = new CommandListDialog(commands);
        dialog.showAndGet();
    }
}

class CommandItem {
    private final String command;
    private final Icon icon;

    public CommandItem(String command, Icon icon) {
        this.command = command;
        this.icon = icon;
    }

    public String getCommand() {
        return command;
    }

    public Icon getIcon() {
        return icon;
    }
}