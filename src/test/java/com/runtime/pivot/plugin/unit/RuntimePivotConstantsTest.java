package com.runtime.pivot.plugin.unit;

import com.runtime.pivot.plugin.config.RuntimePivotConstants;
import com.runtime.pivot.plugin.enums.XStackBreakpointType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Fast unit tests that exercise pure plugin logic without spinning up an IntelliJ Platform fixture.
 */
public class RuntimePivotConstantsTest {

    @Test
    public void pluginIdMatchesDeclaredId() {
        // Must stay in sync with the <id> declared in META-INF/plugin.xml.
        assertEquals("com.runtime.pivot.plugin", RuntimePivotConstants.PLUGIN_ID);
    }

    @Test
    public void agentJarNameIsStable() {
        assertEquals("runtime-pivot-agent", RuntimePivotConstants.AGENT_JAR_NAME);
    }

    @Test
    public void breakpointTypesExposeNonEmptyDescriptions() {
        XStackBreakpointType[] values = XStackBreakpointType.values();
        assertEquals(5, values.length);
        for (XStackBreakpointType type : values) {
            assertNotNull("description for " + type.name() + " should not be null", type.getDescription());
            assertFalse("description for " + type.name() + " should not be empty", type.getDescription().isEmpty());
        }
    }
}
