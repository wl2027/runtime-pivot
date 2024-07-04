package com.runtime.pivot.plugin.config;

import cn.hutool.core.util.StrUtil;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CompositeParameterTargetedValue;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.Sdk;
import com.runtime.pivot.plugin.utils.PluginUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RuntimeJavaAgentConfig extends JavaProgramPatcher {

    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {

        if (!(configuration instanceof RunConfiguration)) {
            return;
        }

        Sdk jdk = javaParameters.getJdk();

        if (Objects.isNull(jdk)) {
            return;
        }

        JavaSdkVersion version = JavaSdk.getInstance().getVersion(jdk);

        if (Objects.isNull(version)) {
            return;
        }

        if (version.compareTo(JavaSdkVersion.JDK_1_8) < 0) {
            return;
        }

        String agentCoreJarPath = PluginUtil.getAgentCoreJarPath();

        if (StrUtil.isBlank(agentCoreJarPath)) {
            return;
        }

        if (!RuntimePivotSettings.getInstance(((RunConfiguration) configuration).getProject()).isAttachAgent()){
            return;
        }

        RunConfiguration runConfiguration = (RunConfiguration) configuration;
        ParametersList vmParametersList = javaParameters.getVMParametersList();
        //放在所有参数最前
        //vmParametersList.addParametersString("-javaagent:" + agentCoreJarPath);
        List<String> parameters = vmParametersList.getParameters();
        List<String> resultParameters = new ArrayList<>();
        resultParameters.add("-javaagent:" + agentCoreJarPath);
        resultParameters.addAll(parameters);
        vmParametersList.clearAll();
        vmParametersList.addAll(resultParameters);
        vmParametersList.addNotEmptyProperty(RuntimePivotConstants.STARTUP_PARAMETERS_ID, runConfiguration.getProject().getLocationHash());
        vmParametersList.addNotEmptyProperty(RuntimePivotConstants.STARTUP_PARAMETERS_PATH, agentCoreJarPath);

    }

}
