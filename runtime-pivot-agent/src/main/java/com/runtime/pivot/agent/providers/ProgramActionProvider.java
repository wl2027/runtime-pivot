package com.runtime.pivot.agent.providers;

import cn.hutool.core.util.ReflectUtil;
import com.runtime.pivot.agent.ActionExecutor;
import com.runtime.pivot.agent.model.Action;
import com.runtime.pivot.agent.model.ActionProvider;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.agent.model.ClassLoaderInfo;
import com.runtime.pivot.agent.tools.ClassLoaderUtil;
import com.runtime.pivot.agent.tools.ObjectTool;
import sun.instrument.TransformerManager;

import java.lang.instrument.ClassFileTransformer;
import java.util.List;

public class ProgramActionProvider extends ActionProvider {

    @Action(ActionType.Program.classLoaderTree)
    public static String classLoadTree() throws Exception{
        List<ClassLoaderInfo> classLoaderTree = ClassLoaderUtil.getClassLoaderTree(ActionExecutor.getAgentContext().getInstrumentation());
        ClassLoaderUtil.printClassLoaderTree(classLoaderTree);
        return  "classLoadTree has been printed on the console";
    }

    @Action(ActionType.Program.classLoaderClassTree)
    public static String classLoaderClassTree() throws Exception{
        List<ClassLoaderInfo> classLoaderTree = ClassLoaderUtil.getClassLoaderTree(ActionExecutor.getAgentContext().getInstrumentation());
        ClassLoaderUtil.printClassLoaderClassTree(classLoaderTree);
        return  "classLoaderClassTree has been printed on the console";
    }

    @Action(ActionType.Program.transformers)
    public static String transformers() throws Exception{
        TransformerManager mTransformerManager = (TransformerManager) ReflectUtil.getFieldValue(ActionExecutor.getAgentContext().getInstrumentation(), "mTransformerManager");
        TransformerManager mRetransfomableTransformerManager = (TransformerManager) ReflectUtil.getFieldValue(ActionExecutor.getAgentContext().getInstrumentation(), "mRetransfomableTransformerManager");
        Object[] mTransformerList = (Object[]) ReflectUtil.getFieldValue(mTransformerManager, "mTransformerList");
        Object[] mRetransfomableTransformerList = (Object[]) ReflectUtil.getFieldValue(mRetransfomableTransformerManager, "mTransformerList");
        System.out.println("Transformers:");
        printClassFileTransformers(mTransformerList);
        System.out.println("Retransfomable Transformers:");
        printClassFileTransformers(mRetransfomableTransformerList);
        return "transformers has been printed on the console";
    }
    private static void printClassFileTransformers(Object[] objects){
        for (Object object : objects) {
            ClassFileTransformer mTransformer = (ClassFileTransformer) ReflectUtil.getFieldValue(object, "mTransformer");
            System.out.println(ObjectTool.toString(mTransformer));
        }

    }

}
