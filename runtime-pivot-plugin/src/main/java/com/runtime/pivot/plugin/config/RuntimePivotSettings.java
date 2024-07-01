package com.runtime.pivot.plugin.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "RuntimePivotSettings",
        storages = @Storage("RuntimePivotSettings.xml")
)
public class RuntimePivotSettings implements PersistentStateComponent<RuntimePivotSettings.State> {

    public static class State {
        public boolean attachAgent = true;
    }

    private State state = new State();

    private final Project project;

    public RuntimePivotSettings(Project project) {
        this.project = project;
    }

    public static RuntimePivotSettings getInstance(Project project) {
        return project.getService(RuntimePivotSettings.class);
    }

    @Nullable
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public boolean isAttachAgent() {
        return state.attachAgent;
    }

    public void setAttachAgent(boolean attachAgent) {
        state.attachAgent = attachAgent;
    }
}
