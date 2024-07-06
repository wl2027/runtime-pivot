package com.runtime.pivot.plugin.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.runtime.pivot.agent.config.AgentConstants;
import com.runtime.pivot.plugin.config.RuntimePivotConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PluginUtil {

    private static final String PLUGIN_HOME_PATH = System.getProperty("user.home")+ AgentConstants.PATH;

    private static final IdeaPluginDescriptor IDEA_PLUGIN_DESCRIPTOR;

    static {
        PluginId pluginId = PluginId.getId(RuntimePivotConstants.PLUGIN_ID);
        IDEA_PLUGIN_DESCRIPTOR = PluginManagerCore.getPlugin(pluginId);
    }

    public static String overrideToUserHome(String jarFilePath) {
        // 定义源文件和目标文件路径
        Path sourcePath = Paths.get(jarFilePath);
        Path destDir = Paths.get(PLUGIN_HOME_PATH);
        String jarFileName = sourcePath.getFileName().toString();
        Path destPath = destDir.resolve(jarFileName);
        try {
            // 如果目标目录不存在，则创建
            if (!Files.exists(destDir)) {
                Files.createDirectories(destDir);
            }
            // 复制
            Files.copy(sourcePath, destPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 返回目标文件路径
        return destPath.toString();
    }

    /**
     * 获取核心Jar路径
     *
     * @return String
     */
    public static String getAgentCoreJarPath() {
        return getJarPathByStartWith(RuntimePivotConstants.AGENT_JAR_NAME);
    }

    /**
     * 根据jar包的前缀名称获路径
     *
     * @param startWith 前缀名称
     * @return String
     */
    private static String getJarPathByStartWith(String startWith) {
        final String quotes = "\"";
        List<File> files = FileUtil.loopFiles(IDEA_PLUGIN_DESCRIPTOR.getPath());
        for (File file : files) {
            String name = file.getName();
            if (name.startsWith(startWith)) {
                String pathStr = FileUtil.getCanonicalPath(file);
                if (StrUtil.contains(pathStr, StrUtil.SPACE)) {
                    return StrUtil.builder().append(quotes).append(pathStr).append(quotes).toString();
                }
                return pathStr;
            }
        }
        return StrUtil.EMPTY;
    }

}
