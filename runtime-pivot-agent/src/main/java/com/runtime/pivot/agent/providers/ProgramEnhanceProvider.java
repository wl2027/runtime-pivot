package com.runtime.pivot.agent.providers;

import cn.hutool.core.util.ReflectUtil;
import com.runtime.pivot.agent.AgentContext;
import com.runtime.pivot.agent.config.ActionType;
import com.runtime.pivot.agent.tools.ConsoleTool;
import sun.instrument.TransformerManager;

import java.lang.instrument.ClassFileTransformer;

public class ProgramEnhanceProvider extends EnhanceProvider {
    public static void printClassLoadTree() {
        ConsoleTool.print(ActionType.Program.printClassLoadTree,()->{

        });
    }

    public static void printTransformers() throws Exception{
        ConsoleTool.print(ActionType.Program.printTransformers,()->{
            TransformerManager mTransformerManager = (TransformerManager) ReflectUtil.getFieldValue(AgentContext.INSTRUMENTATION, "mTransformerManager");
            TransformerManager mRetransfomableTransformerManager = (TransformerManager) ReflectUtil.getFieldValue(AgentContext.INSTRUMENTATION, "mRetransfomableTransformerManager");

            Object[] mTransformerList = (Object[]) ReflectUtil.getFieldValue(mTransformerManager, "mTransformerList");
            Object[] mTransformerList2 = (Object[]) ReflectUtil.getFieldValue(mRetransfomableTransformerManager, "mTransformerList");
            for (Object o : mTransformerList) {
                ClassFileTransformer mTransformer = (ClassFileTransformer) ReflectUtil.getFieldValue(o, "mTransformer");
                System.out.println(mTransformer.toString()+mTransformer.getClass());
            }
            for (Object o : mTransformerList2) {
                ClassFileTransformer mTransformer = (ClassFileTransformer) ReflectUtil.getFieldValue(o, "mTransformer");
                System.out.println(mTransformer.toString()+mTransformer.getClass());
            }
        });
    }


}
