package com.runtime.pivot.plugin.view.method;

import com.intellij.openapi.wm.WindowManager;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.runtime.pivot.plugin.model.XSessionComponent;
import com.runtime.pivot.plugin.model.XStackBreakpoint;
import com.runtime.pivot.plugin.model.XStackContext;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;


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
    private JBList<XStackBreakpoint> dataList = new JBList<>();
    private List<XStackBreakpoint> XStackBreakpointList = new ArrayList<>();

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

        dataList.setCellRenderer(new ListItemRenderer());
        dataList.addMouseListener(getMouseListener(dataList::getSelectedValue));
        add(new JBScrollPane(dataList), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> closeComponent());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(WindowManager.getInstance().getFrame(myProject));
        XStackContext xStackContext = XStackContext.getInstance(xDebugSession);
        initData(xStackContext);
    }

    public MouseAdapter getMouseListener(Supplier<XStackBreakpoint> selectedValueSupplier) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                XStackBreakpoint selectedValue = selectedValueSupplier.get();
                if (e.getClickCount() == 1 && selectedValue != null) {
                    selectedValue.getxBreakpoint().getNavigatable().navigate(true);
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
        return new XDebugSessionListener() {
            @Override
            public void sessionPaused() {
                updateData(XStackContext.getInstance(myXDebugSession));
            }

            @Override
            public void stackFrameChanged() {
                updateData(XStackContext.getInstance(myXDebugSession));
            }
        };
    }

    @Override
    public void initData(XStackContext xStackContext) {
        updateData(xStackContext);
    }

    @Override
    public void updateData(XStackContext xStackContext) {
        List<XStackBreakpoint> newData = xStackContext.getCurrentXStackBreakpointList();
        this.XStackBreakpointList.clear();
        this.XStackBreakpointList.addAll(newData);
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
}
