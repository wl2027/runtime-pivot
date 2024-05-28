package com.runtime.pivot.agent.providers;

import cn.hutool.core.util.ReflectUtil;
import com.runtime.pivot.agent.ActionExecutor;
import com.runtime.pivot.agent.model.Action;
import com.runtime.pivot.agent.model.ActionProvider;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.agent.model.ClassLoaderInfo;
import com.runtime.pivot.agent.tools.ClassLoaderUtil;
import sun.instrument.TransformerManager;

import java.lang.instrument.ClassFileTransformer;
import java.util.List;

public class ProgramEnhanceProvider extends ActionProvider {

    @Action(ActionType.Program.classLoaderTree)
    public static void classLoadTree() throws Exception{
        List<ClassLoaderInfo> classLoaderTree = ClassLoaderUtil.getClassLoaderTree(ActionExecutor.getAgentContext().getInstrumentation());
        ClassLoaderUtil.printClassLoaderTree(classLoaderTree);
    }

    @Action(ActionType.Program.classLoaderClassTree)
    public static void classLoaderClassTree() throws Exception{
        System.out.println("sout");
        List<ClassLoaderInfo> classLoaderTree = ClassLoaderUtil.getClassLoaderTree(ActionExecutor.getAgentContext().getInstrumentation());
        System.out.println("sout2");
        ClassLoaderUtil.printClassLoaderClassTree(classLoaderTree);
    }

    @Action(ActionType.Program.transformers)
    public static void transformers() throws Exception{
        TransformerManager mTransformerManager = (TransformerManager) ReflectUtil.getFieldValue(ActionExecutor.getAgentContext().getInstrumentation(), "mTransformerManager");
        TransformerManager mRetransfomableTransformerManager = (TransformerManager) ReflectUtil.getFieldValue(ActionExecutor.getAgentContext().getInstrumentation(), "mRetransfomableTransformerManager");
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
    }

}
