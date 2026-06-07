package com.runtime.pivot.plugin.ui;

import com.runtime.pivot.plugin.config.RuntimePivotConstants;
import com.runtime.pivot.plugin.enums.XStackBreakpointType;
import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.awt.BorderLayout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Headless Swing/UI component tests. These build Swing components on the Event Dispatch Thread and
 * assert on their structure. They run fully headless and do NOT start a RemoteRobot server.
 */
public class RuntimePivotSwingComponentTest {

    @Test
    public void buildsBreakpointTreeModelOnEventDispatchThread() throws Exception {
        final JTree[] holder = new JTree[1];
        SwingUtilities.invokeAndWait(() -> {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Breakpoints");
            for (XStackBreakpointType type : XStackBreakpointType.values()) {
                root.add(new DefaultMutableTreeNode(type.getDescription()));
            }
            holder[0] = new JTree(root);
        });

        JTree tree = holder[0];
        assertNotNull(tree);
        TreeModel model = tree.getModel();
        assertEquals(XStackBreakpointType.values().length, model.getChildCount(model.getRoot()));
    }

    @Test
    public void buildsLabeledPanelOnEventDispatchThread() throws Exception {
        final JPanel[] holder = new JPanel[1];
        SwingUtilities.invokeAndWait(() -> {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel(RuntimePivotConstants.MSG_TITLE), BorderLayout.NORTH);
            holder[0] = panel;
        });

        JPanel panel = holder[0];
        assertNotNull(panel);
        assertEquals(1, panel.getComponentCount());
        assertTrue(panel.getComponent(0) instanceof JLabel);
        assertEquals(RuntimePivotConstants.MSG_TITLE, ((JLabel) panel.getComponent(0)).getText());
    }
}
