package com.runtime.pivot.agent.providers;

import cn.hutool.json.JSONUtil;
import com.runtime.pivot.agent.config.ActionType;
import com.runtime.pivot.agent.tools.ConsoleTool;
import com.runtime.pivot.agent.tools.JSONFileUtils;
import org.apache.lucene.util.RamUsageEstimator;
import org.openjdk.jol.info.ClassLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ObjectEnhanceProvider extends EnhanceProvider {
    public static void printInfo(Object object){
        ConsoleTool.print(ActionType.Object.printInfo,()->{
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
//            //打印对象类信息=>引用大小
//            System.out.println("Object graph layout:");
//            System.out.println(ClassLayout.parseClass(object.getClass()).toPrintable());
//            //打印实际对象信息
//            System.out.println("Object total size:");
//            System.out.println(GraphLayout.parseInstance(object).toPrintable());
        });
    }
    public static String store(Object object){
        //转成JSON
        AtomicReference<String> writePath = new AtomicReference<>("");
        ConsoleTool.print(ActionType.Object.store,()->{
            writePath.set(JSONFileUtils.write(object, null));
            System.out.println("object store path: "+writePath.get());
        });
        return writePath.get();
    }

    public static <E> E load(Object object,String path){
        //文件读~ 参考IDEA setValue
        Class<?> aClass = object.getClass();
        if (object instanceof List){
            object = JSONFileUtils.readList(path, Map.class);
        }else {
            object = JSONFileUtils.readObject(path,aClass);
        }
        Object finalObject = object;
        ConsoleTool.print(ActionType.Object.load,()->{
            System.out.println("object load result : "+ JSONUtil.toJsonStr(finalObject));
        });
        return (E) finalObject;
    }

}
