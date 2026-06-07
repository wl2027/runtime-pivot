package com.runtime.pivot.plugin.integration;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.runtime.pivot.plugin.config.RuntimePivotConstants;
import com.runtime.pivot.plugin.config.RuntimePivotSettings;

/**
 * IntelliJ Platform integration tests. These run inside a light platform fixture so that the
 * plugin under development is loaded and its services / actions are registered.
 */
public class RuntimePivotPluginIntegrationTest extends BasePlatformTestCase {

    public void testPluginIsLoaded() {
        PluginId pluginId = PluginId.getId(RuntimePivotConstants.PLUGIN_ID);
        assertNotNull("Plugin descriptor for " + RuntimePivotConstants.PLUGIN_ID + " should be available",
                PluginManagerCore.getPlugin(pluginId));
    }

    public void testProjectServiceIsRegistered() {
        RuntimePivotSettings settings = RuntimePivotSettings.getInstance(getProject());
        assertNotNull("RuntimePivotSettings project service should be available", settings);
        assertTrue("attachAgent should default to true", settings.isAttachAgent());
    }

    public void testActionsAreRegistered() {
        ActionManager actionManager = ActionManager.getInstance();
        assertNotNull("RuntimePivot.ClassLoaderTree action should be registered",
                actionManager.getAction("RuntimePivot.ClassLoaderTree"));
        assertNotNull("RuntimePivot.XSessionMonitoring action should be registered",
                actionManager.getAction("RuntimePivot.XSessionMonitoring"));
    }
}
