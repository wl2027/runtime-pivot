package com.runtime.pivot.plugin.view;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.FormBuilder;
import com.runtime.pivot.plugin.config.RuntimePivotSettings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RuntimePivotConfigurable implements Configurable {
    private JPanel mainPanel;
    private JBCheckBox attachAgentCheckBox;
    private final Project project;

    public RuntimePivotConfigurable(Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Runtime-Pivot Configuration";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        attachAgentCheckBox = new JBCheckBox("Attach Agent");
        attachAgentCheckBox.setToolTipText("是否为项目程序开启runtime-pivot 的program, class, object功能增强");

        JBLabel restartLabel = new JBLabel("是否为项目程序开启runtime-pivot 的program, class, object功能增强");
        restartLabel.setForeground(JBColor.GRAY);

        JPanel panel = new JBPanel<>();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(attachAgentCheckBox);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(restartLabel);

        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(panel)
                .addComponentFillVertically(new JBPanel<>(), 0)
                .getPanel();
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return attachAgentCheckBox.isSelected() != RuntimePivotSettings.getInstance(project).isAttachAgent();
    }

    @Override
    public void apply() {
        RuntimePivotSettings.getInstance(project).setAttachAgent(attachAgentCheckBox.isSelected());
    }

    @Override
    public void reset() {
        attachAgentCheckBox.setSelected(RuntimePivotSettings.getInstance(project).isAttachAgent());
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
        attachAgentCheckBox = null;
    }
}
