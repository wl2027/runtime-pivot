package com.runtime.pivot.agent.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

class TreeNode {
    String value;
    List<TreeNode> children;

    TreeNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    void addChild(TreeNode child) {
        children.add(child);
    }
}

public class TreePrinter {

    public static void main(String[] args) {
        // 创建树的根节点
        TreeNode root = new TreeNode("Root");

        // 创建子节点并添加到根节点
        TreeNode child1 = new TreeNode("Child1");
        TreeNode child2 = new TreeNode("Child2");
        root.addChild(child1);
        root.addChild(child2);

        // 创建子节点的子节点并添加
        TreeNode child1_1 = new TreeNode("Child1.1");
        TreeNode child1_2 = new TreeNode("Child1.2");
        child1.addChild(child1_1);
        child1.addChild(child1_2);

        TreeNode child2_1 = new TreeNode("Child2.1");
        child2.addChild(child2_1);

        // 打印树结构
        printTree(root, "", true);
    }

    // 递归地打印树节点
    static void printTree(TreeNode node, String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "`-- " : "|-- ") + node.value);
        for (int i = 0; i < node.children.size() - 1; i++) {
            printTree(node.children.get(i), prefix + (isTail ? "    " : "|   "), false);
        }
        if (node.children.size() > 0) {
            printTree(node.children.get(node.children.size() - 1), prefix + (isTail ? "    " : "|   "), true);
        }
    }
}
