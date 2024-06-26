package com.runtime.pivot.plugin.view.method;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.runtime.pivot.plugin.model.XSessionComponent;
import com.runtime.pivot.plugin.model.XStackBreakpoint;
import com.runtime.pivot.plugin.model.XStackContext;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class XSessionBreakpointDialog extends XSessionComponent<XSessionBreakpointDialog> {

    private JBLabel descriptionLabel = new JBLabel();
    private JBList<XStackBreakpoint> dataList = new JBList<>();
    private List<XStackBreakpoint> myXStackBreakpointList = new ArrayList<>();

    protected XSessionBreakpointDialog(XDebugSession xDebugSession) {
        super(xDebugSession, "XStack Breakpoint List");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(300, 500);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeComponent();
            }
        });

        // 添加顶部说明文本
        JPanel topPanel = new JPanel(new BorderLayout());

        descriptionLabel.setBorder(JBUI.Borders.empty(5));
        topPanel.add(descriptionLabel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);

        dataList.setCellRenderer(new ListItemRenderer());
        dataList.addMouseListener(getMouseListener(dataList::getSelectedValue));
        add(new JBScrollPane(dataList), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> closeComponent());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(WindowManager.getInstance().getFrame(myProject));
    }

    public MouseAdapter getMouseListener(Supplier<XStackBreakpoint> selectedValueSupplier) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                XStackBreakpoint selectedValue = selectedValueSupplier.get();
                if (e.getClickCount() == 1 && selectedValue != null) {
                    selectedValue.getXBreakpoint().getNavigatable().navigate(true);
                }
            }
        };
    }

    public static XSessionBreakpointDialog getInstance(XDebugSession xDebugSession) {
        XSessionBreakpointDialog xSessionBreakpointDialog = new XSessionBreakpointDialog(xDebugSession);
        xSessionBreakpointDialog.initData(XStackContext.getInstance(xDebugSession));
        return xSessionBreakpointDialog;
    }

    @Override
    public XDebugSessionListener getXDebugSessionListener() {
        //异步编排invokeAndWait而不是invokeLater,避免同时在更新Dialog的视图导致并发问题
        return new XDebugSessionListener() {
            //多线程中进入断点都会调用
            @Override
            public void sessionPaused() {
                ApplicationManager.getApplication().invokeLater(() -> {
                    updateData(XStackContext.getInstance(myXDebugSession));
                });
            }
            //手动改变栈 or 改变栈帧 时调用
            @Override
            public void stackFrameChanged() {
                ApplicationManager.getApplication().invokeLater(() -> {
                    updateData(XStackContext.getInstance(myXDebugSession));
                });
            }
        };
    }

    @Override
    public void initData(XStackContext xStackContext) {
        updateData(xStackContext);
    }

    @Override
    synchronized public void updateData(XStackContext xStackContext) {
        if (xStackContext!=null) {
            updateLabelData(xStackContext.getXDebugSession().getSuspendContext().getActiveExecutionStack().getDisplayName());
            updateListData(xStackContext.getCurrentXStackBreakpointList());
        }
    }

    private void updateLabelData(String text) {
        descriptionLabel.setText(text);
    }

    synchronized public void updateListData(List<XStackBreakpoint> newData) {
        this.myXStackBreakpointList.clear();
        this.myXStackBreakpointList.addAll(newData);
        DefaultListModel<XStackBreakpoint> listModel = new DefaultListModel<>();
        for (XStackBreakpoint item : newData) {
            listModel.addElement(item);
        }
        dataList.setModel(listModel);
    }

    @Override
    public void removeXSessionComponent() {
        RuntimePivotMethodService.getInstance(myProject).removeXSessionBreakpointDialog(myXDebugSession);
    }

    @Override
    public void closeComponent() {
        super.closeComponent();
        dispose();
    }

    private static class ListItemRenderer extends JLabel implements ListCellRenderer<XStackBreakpoint> {
        @Override
        public Component getListCellRendererComponent(JList<? extends XStackBreakpoint> list, XStackBreakpoint value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.toString());
            setIcon(value.getIcon());
            if (isSelected) {
                setBackground(JBColor.background().darker());
                setForeground(JBColor.foreground().brighter());
            } else {
                setBackground(JBColor.background());
                setForeground(JBColor.foreground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

    public List<XStackBreakpoint> getXStackBreakpointList() {
        return myXStackBreakpointList;
    }
}
