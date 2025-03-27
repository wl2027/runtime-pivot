package com.runtime.pivot.plugin.actions.object;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.MessageTreeNode;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class VariableSearchActionBAK extends XDebuggerTreeActionBase {

    private void traverseAndFind(XValueNodeImpl rootNode, String searchQuery) {
        if (rootNode == null || searchQuery == null) {
            return;
        }

        Queue<XValueNodeImpl> queue = new LinkedList<>();
        queue.add(rootNode);

        while (!queue.isEmpty()) {
            XValueNodeImpl node = queue.poll();

            if (node == null) {
                continue;
            }

            try {
                String valueString = node.getValueContainer().toString();
                String rawValueString = node.getRawValue() != null ? node.getRawValue().toString() : "";

                if (valueString.contains(searchQuery) || rawValueString.contains(searchQuery)) {
                    // 展开并定位节点
                    expandAndLocateNode(node);
                }
            } catch (Exception ex) {
                System.err.println("Error processing node: " + ex.getMessage());
                ex.printStackTrace();
            }

            // 将子节点加入队列中
            for (int i = 0; i < node.getChildCount(); i++) {
                try {
                    if (node.getChildAt(i) instanceof MessageTreeNode) {
                        System.out.println(node.getClass());
                    }
                    if (node.getChildAt(i) instanceof XValueNodeImpl && node.getChildAt(i)!=null) {
                        queue.add((XValueNodeImpl) node.getChildAt(i));
                    } else {
                        System.out.println("Unexpected node type: " + node.getChildAt(i).getClass());
                    }
                }catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        }
    }

    private void expandAndLocateNode(XValueNodeImpl node) {
        if (node == null) {
            return;
        }

        try {
            XDebuggerTree tree = node.getTree();
            TreePath path = new TreePath(node.getPath());

            if (tree != null && path != null) {
                // 展开节点路径
                tree.expandPath(path);

                // 高亮显示节点
                tree.setSelectionPath(path);
                tree.scrollPathToVisible(path);
            }
        } catch (Exception ex) {
            System.err.println("Error expanding or locating node: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    protected void perform(XValueNodeImpl node, @NotNull String nodeName, AnActionEvent e) {
        // 提示用户输入搜索查询
//        String searchQuery = Messages.showInputDialog("请输入要搜索的值：", "搜索调试变量", Messages.getQuestionIcon());
        String searchQuery = "metricsRestTemplateCustomizer";
        if (searchQuery == null || searchQuery.isEmpty()) {
            return;
        }
        // 遍历变量值并找到匹配项
        //traverseAndFind(node, searchQuery);
        List<? extends TreeNode> allNode = new ArrayList<>();
        addList(node,allNode);
        for (TreeNode treeNode : allNode) {
            System.out.println("");
        }

    }

    private void addList(XValueNodeImpl node, List allNode) {
        List<? extends TreeNode> children = node.getChildren();
        allNode.addAll(children);
        for (TreeNode child : node.getChildren()) {
            addList((XValueNodeImpl) child,allNode);
        }
    }
}
