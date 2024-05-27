package com.runtime.pivot.agent.providers;

import cn.hutool.json.JSONUtil;
import com.runtime.pivot.agent.model.Action;
import com.runtime.pivot.agent.model.ActionProvider;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.agent.tools.JSONFileTool;
import org.apache.lucene.util.RamUsageEstimator;
import org.openjdk.jol.info.ClassLayout;

import java.util.List;
import java.util.Map;

public class ObjectEnhanceProvider extends ActionProvider {

    @Action(ActionType.Object.internals)
    public static void information(Object object){
        // 打印对象头信息=>引用大小
        System.out.println("Object Size Layout:");
        //计算指定对象本身在堆空间的大小，单位字节
        System.out.println("size of heap space: " + RamUsageEstimator.shallowSizeOf(object)+" B");
        //计算指定对象及其引用树上的所有对象的综合大小，单位字节
        long size = RamUsageEstimator.sizeOfObject(object);
        System.out.println("RAM resource usage: " + size+" B");//字节
        //计算指定对象及其引用树上的所有对象的综合大小，返回可读的结果，如：2KB
        System.out.println("RAM resource usage: " + RamUsageEstimator.humanReadableUnits(size));//K字节
        System.out.println("Object Header Layout:");
        System.out.println(ClassLayout.parseInstance(object).toPrintable());
//      //打印对象类信息=>引用大小
//      System.out.println("Object graph layout:");
//      System.out.println(ClassLayout.parseClass(object.getClass()).toPrintable());
//      //打印实际对象信息
//      System.out.println("Object total size:");
//      System.out.println(GraphLayout.parseInstance(object).toPrintable());
    }

    @Action(ActionType.Object.store)
    public static String store(Object object){
        //转成JSON
        String writePath = JSONFileTool.write(object, null);
        System.out.println("object store path: "+writePath);
        return writePath;
    }


    @Action(ActionType.Object.load)
    public static <E> E load(Object object,String path){
        //读取文件 参考IDEA setValue
        Class<?> aClass = object.getClass();
        if (object instanceof List){
            object = JSONFileTool.readList(path, Map.class);
        }else {
            object = JSONFileTool.readObject(path,aClass);
        }
        System.out.println("object load result : "+ JSONUtil.toJsonStr(object));
        return (E) object;
    }

}
