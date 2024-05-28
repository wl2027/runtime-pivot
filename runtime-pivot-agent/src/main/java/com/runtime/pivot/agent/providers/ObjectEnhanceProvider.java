package com.runtime.pivot.agent.providers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.runtime.pivot.agent.model.Action;
import com.runtime.pivot.agent.model.ActionProvider;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.agent.tools.JSONFileTool;
import com.runtime.pivot.agent.tools.Main;
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
        System.out.println("\nObject Header Layout:");
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

    /**
     * java.lang.Class<?> actionExecutorClass = java.lang.ClassLoader.getSystemClassLoader().loadClass("com.runtime.pivot.agent.ActionExecutor");
     * java.lang.reflect.Method method = actionExecutorClass.getMethod("execute",String.class,Object[].class);
     * method.invoke(null,"load",new Object[]{aaa,new String("E:/002_Code/000_github/APM/apm-demo/target/classes/com/wl/apm/APMApplicationMain$120240528160128@1377301456.json")});
     * aaa=new ArrayList<>();
     *
     * @param object
     * @param path
     * @return
     * @param <E>
     */

    @Action(ActionType.Object.load)
    public static <E> E load(Object object,String path){
        //读取文件 参考IDEA setValue
        Class<?> aClass = object.getClass();
        Object rs = new Object();
        if (object instanceof List){
            //泛型被擦除了,所以用Object
            rs = JSONFileTool.readList(path, Object.class);
        }else {
            rs = JSONFileTool.readObject(path, aClass);
        }
        BeanUtil.copyProperties(object,rs);
        try {
            Main.deepCopy(object,rs);
        } catch (Exception e) {
            System.out.println("deepCopy Exception");
        }
//        object=rs;
        System.out.println("object load result 1: "+ System.identityHashCode(object)+": "+JSONUtil.toJsonStr(object));
        System.out.println("object load result 2: "+ System.identityHashCode(rs)+": "+JSONUtil.toJsonStr(rs));

        return (E) rs;
    }

}
