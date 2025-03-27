package com.runtime.pivot.plugin.actions.object;

import cn.hutool.core.util.ReflectUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.MessageTreeNode;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import com.runtime.pivot.plugin.utils.platfrom.XDebuggerTestUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class VariableSearchAction extends XDebuggerTreeActionBase {

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
        Map<String,XValueNodeImpl> allNodeMap = new HashMap<>();
        //((XValueNodeImpl)node.getChildren().get(0)).getChildren()
        addList(node,allNodeMap);
        if (!node.isComputed()) {
            // 请求展开节点并加载其子节点
            node.setValueModificationStarted();
        }
        XValue valueContainer = node.getValueContainer();
        List<XValue> xValues = XDebuggerTestUtil.collectChildren(valueContainer);//可以触发到下一层的子节点获取
        node.getChildren();
        System.out.println(allNodeMap);
    }

    private void addList(TreeNode node, Map<String,XValueNodeImpl> allNodeMap) {
        try {
            String name = ReflectUtil.invoke(node, "getName");
            allNodeMap.put(name, (XValueNodeImpl) node);
            List<TreeNode> treeNodes = ReflectUtil.invoke(node, "getChildren");
            for (TreeNode treeNode : treeNodes) {
                addList(treeNode,allNodeMap);
            }
        }catch (Exception e){
            System.out.println(node);
//            System.out.println(allNode);
        }
    }
}
