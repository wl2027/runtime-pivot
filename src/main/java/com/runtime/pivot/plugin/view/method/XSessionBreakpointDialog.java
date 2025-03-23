package com.runtime.pivot.plugin.view.method;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.MessageUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.IconManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XDropFrameHandler;
import com.intellij.xdebugger.frame.XStackFrame;
import com.runtime.pivot.plugin.config.RuntimePivotConstants;
import com.runtime.pivot.plugin.enums.XStackBreakpointType;
import com.runtime.pivot.plugin.i18n.RuntimePivotBundle;
import com.runtime.pivot.plugin.model.XSessionComponent;
import com.runtime.pivot.plugin.model.XStackBreakpoint;
import com.runtime.pivot.plugin.model.XStackContext;
import com.runtime.pivot.plugin.service.RuntimePivotXSessionService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

public class XSessionBreakpointDialog extends XSessionComponent<XSessionBreakpointDialog> {

    private JBLabel descriptionLabel = new JBLabel();
    private Tree breakpointTree;
    private DefaultTreeModel treeModel;
    private List<XStackBreakpoint> myXStackBreakpointList = new ArrayList<>();

    protected XSessionBreakpointDialog(XDebugSession xDebugSession) {
        super(xDebugSession, RuntimePivotConstants.X_SESSION_BREAKPOINT);
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

        // 分层视图
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        treeModel = new DefaultTreeModel(root);
        breakpointTree = new Tree(treeModel);
        breakpointTree.setRootVisible(false); // 隐藏根节点
        breakpointTree.setShowsRootHandles(true); // 显示根节点的句柄
        breakpointTree.setCellRenderer(new BreakpointTreeCellRenderer());
        breakpointTree.addMouseListener(getMouseListener(() -> (XStackBreakpoint) ((DefaultMutableTreeNode) breakpointTree.getLastSelectedPathComponent()).getUserObject()));
        add(new JBScrollPane(breakpointTree), BorderLayout.CENTER);

        // 展开所有节点
        expandAllNodes(breakpointTree, 0, breakpointTree.getRowCount());

        // 添加关闭按钮
        JButton closeButton = new JButton(RuntimePivotConstants.CLOSE_BUTTON);
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
                if (selectedValue == null) return;

                if (e.getClickCount() == 1) {
                    selectedValue.getXBreakpoint().getNavigatable().navigate(true);
                }
                if (e.getClickCount() == 2) {
                    if (!selectedValue.getXStackBreakpointType().equals(XStackBreakpointType.UNAVAILABLE)){
                        if (MessageUtil.showOkNoDialog(RuntimePivotConstants.MSG_TITLE,
                                RuntimePivotBundle.message("runtime.pivot.plugin.action.session.pop.msg",selectedValue.getXStackFrame()),
                                myProject,
                                RuntimePivotBundle.message("runtime.pivot.plugin.action.session.pop.ok"),
                                RuntimePivotBundle.message("runtime.pivot.plugin.action.session.pop.no"),
                                null)) {
                            XStackFrame xStackFrame = selectedValue.getXStackFrame();
                            XDropFrameHandler dropFrameHandler = getDropFrameHandler(myXDebugSession);
                            dropFrameHandler.drop(xStackFrame);
                        }
                    } else {
                        Messages.showMessageDialog(RuntimePivotBundle.message("runtime.pivot.plugin.action.session.pop.error",selectedValue.getXStackFrame()),RuntimePivotConstants.MSG_TITLE,null);
                    }
                }
            }
        };
    }

    @Nullable
    private static XDropFrameHandler getDropFrameHandler(@NotNull XDebugSession xDebugSession) {
        return Optional.ofNullable(xDebugSession)
                .map(XDebugSession::getDebugProcess)
                .map(XDebugProcess::getDropFrameHandler)
                .orElse(null);
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
                ApplicationManager.getApplication().invokeLater(() -> {
                    updateData(XStackContext.getInstance(myXDebugSession));
                });
            }

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
        if (xStackContext != null) {
            updateLabelData(xStackContext.getXDebugSession().getSuspendContext().getActiveExecutionStack().getDisplayName());
            updateTreeData(xStackContext.getCurrentXStackBreakpointList());
        }
    }

    private void updateLabelData(String text) {
        descriptionLabel.setText(text);
    }

    public synchronized void updateTreeData(List<XStackBreakpoint> newData) {
        this.myXStackBreakpointList.clear();
        this.myXStackBreakpointList.addAll(newData);

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.removeAllChildren();

        Map<String, List<DefaultMutableTreeNode>> frameNodes = new LinkedHashMap<>();
        for (XStackBreakpoint breakpoint : newData) {
            String frameName = breakpoint.getXStackFrame().toString();
            DefaultMutableTreeNode frameNode = new DefaultMutableTreeNode(breakpoint);
            frameNodes.computeIfAbsent(frameName, k -> new ArrayList<>()).add(frameNode);
        }

        List<String> frameNames = new ArrayList<>(frameNodes.keySet());
//        Collections.reverse(frameNames); // 反转分组顺序

        for (String frameName : frameNames) {
            List<DefaultMutableTreeNode> nodes = frameNodes.get(frameName);
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(frameName);
            for (DefaultMutableTreeNode node : nodes) {
                groupNode.add(node);
            }
            root.add(groupNode);
        }

        treeModel.reload();
        expandAllNodes(breakpointTree, 0, breakpointTree.getRowCount());
    }


    @Override
    public void removeXSessionComponent() {
        RuntimePivotXSessionService.getInstance(myProject).removeXSessionBreakpointDialog(myXDebugSession);
    }

    @Override
    public void closeComponent() {
        super.closeComponent();
        dispose();
    }

    private static class BreakpointTreeCellRenderer extends DefaultTreeCellRenderer {

        private final Icon stackFrameIcon = IconManager.getInstance().getIcon("/icons/stack.svg", BreakpointTreeCellRenderer.class);

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();
                if (userObject instanceof XStackBreakpoint) {
                    XStackBreakpoint breakpoint = (XStackBreakpoint) userObject;
                    setText(breakpoint.toString());
                    setIcon(breakpoint.getIcon());
                } else {
                    setIcon(stackFrameIcon);
                }
            }
            return component;
        }
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    public List<XStackBreakpoint> getXStackBreakpointList() {
        return myXStackBreakpointList;
    }
}
