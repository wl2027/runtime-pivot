package com.runtime.pivot.agent.config;

import java.io.File;

public class AgentConstants {
    public static final String BANNER =
            "__________ ____ _____________________.___   _____  ___________        __________._______   _______________________\n" +
            "\\______   \\    |   \\      \\__    ___/|   | /     \\ \\_   _____/        \\______   \\   \\   \\ /   /\\_____  \\__    ___/\n" +
            " |       _/    |   /   |   \\|    |   |   |/  \\ /  \\ |    __)_   ______ |     ___/   |\\   Y   /  /   |   \\|    |   \n" +
            " |    |   \\    |  /    |    \\    |   |   /    Y    \\|        \\ /_____/ |    |   |   | \\     /  /    |    \\    |   \n" +
            " |____|_  /______/\\____|__  /____|   |___\\____|__  /_______  /         |____|   |___|  \\___/   \\_______  /____|   \n" +
            "        \\/                \\/                     \\/        \\/                                          \\/         \n";
    public static final Boolean DEBUG = true;
    public static final String AGENT_PATH = "runtime.pivot.agent.path";
    public static final String PATH = File.separator+".runtime";
    public static final String VERSION = "1.0.0.RELEASE";
    public static final String IDENTIFICATION = " :: Runtime Pivot Agent::       (v"+VERSION+")";

    public static final String RESET = "\033[0m";  // 重置
    public static final String ANSI_BOLD = "\u001B[1m"; //加粗
    public static final String BLACK = "\033[0;30m";   // 黑色
    public static final String RED = "\033[0;31m";     // 红色
    public static final String GREEN = "\033[0;32m";   // 绿色
    public static final String YELLOW = "\033[0;33m";  // 黄色
    public static final String BLUE = "\033[0;34m";    // 蓝色
    public static final String DARK_BLUE = "\u001B[34m";
    public static final String PURPLE = "\033[0;35m";  // 紫色
    public static final String CYAN = "\033[0;36m";    // 青色
    public static final String WHITE = "\033[0;37m";   // 白色
    public static final String PRINT_START_STRING = "--------------------------------------------------------------------- Runtime Pivot START {} ---------------------------------------------------------------------";
    public static final String PRINT_END_STRING = "--------------------------------------------------------------------- Runtime Pivot END {} -----------------------------------------------------------------------";

}
