package com.runtime.pivot.agent;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.runtime.pivot.agent.config.ActionType;
import com.runtime.pivot.agent.providers.ClassEnhanceProvider;
import com.runtime.pivot.agent.providers.ObjectEnhanceProvider;
import com.runtime.pivot.agent.tools.ConsoleTool;
import com.runtime.pivot.agent.tools.ExpressionTool;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;

public class AgentMain {
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println(AgentConstants.ANSI_BOLD);
        System.out.println(AgentConstants.BANNER);
        System.out.println(AgentConstants.IDENTIFICATION);
        System.out.println(AgentConstants.RESET);
        AgentContext.INSTRUMENTATION = instrumentation;
    }

    public static void main(String[] args) {
        System.out.println(AgentConstants.ANSI_BOLD);
        System.out.println(AgentConstants.BANNER);
        System.out.println(AgentConstants.IDENTIFICATION);
        System.out.println(AgentConstants.RESET);
//        ConsoleTool.print(ActionType.Object.printInfo,()->{
//            String s = ExpressionTool.executeProvider(ClassEnhanceProvider.class, ActionType.Class.dumpClass, "path");
//            System.out.println(s);
//        });
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            map.put(RandomUtil.randomNumbers(10), RandomUtil.randomNumbers(10));
        }
//        ObjectEnhanceProvider.printInfo(map);
//        String store = ObjectEnhanceProvider.store(map);
//        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
//        System.out.println(System.identityHashCode(objectObjectHashMap));
//        objectObjectHashMap = ObjectEnhanceProvider.load(objectObjectHashMap,store);
//        System.out.println(System.identityHashCode(objectObjectHashMap));
//        System.out.println(objectObjectHashMap);
        A a = new A();
        String store = ObjectEnhanceProvider.store(a);
        A a1 = new A();
        System.out.println(System.identityHashCode(a1));
        a1 = ObjectEnhanceProvider.load(a1,store);
        System.out.println(System.identityHashCode(a1));
        System.out.println(a);
    }
}
class A{
    public String aaa="111";
    public String bbb="222";

}