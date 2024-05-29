package com.runtime.pivot.agent.providers;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.runtime.pivot.agent.model.Action;
import com.runtime.pivot.agent.model.ActionProvider;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.agent.model.RuntimePivotException;
import com.runtime.pivot.agent.tools.JSONFileTool;
import org.apache.lucene.util.RamUsageEstimator;
import org.openjdk.jol.info.ClassLayout;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ObjectActionProvider extends ActionProvider {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()); // Support for Java 8 date/time types
    }

    @Action(ActionType.Object.internals)
    public static void internals(Object object){
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
    public static <E> E load(E object,String path) throws Exception {
        if (object == null) {
            throw new IllegalArgumentException("object must not be null");
        }
        System.out.println("object load result before: "+ System.identityHashCode(object)+": "+JSONUtil.toJsonStr(object));
        //读取文件 参考IDEA setValue
        JSON json = JSONUtil.readJSON(new File(path), StandardCharsets.UTF_8);
        String jsonString = json.toJSONString(0);
        if (StrUtil.isBlank(jsonString)) {
            //没有json数据
        }
        if (object == null) {
            //MY 直接赋值~对象,数组,集合,Map...
//            object = objectMapper.readValue(jsonString,eClass);
            object = (E) objectMapper.readValue(jsonString,Object.class);
        }else {
            if (object instanceof Collection){
                ((Collection)object).clear();
            }
            if (object instanceof Map){
                ((Map)object).clear();
            }
            if (object.getClass().isArray()){
                //先不处理数组
                if (true) throw new RuntimePivotException();
                Object[] sourceArray = objectMapper.readValue(jsonString, Object[].class);
                Object[] targetArray = (Object[]) object;
                int length = targetArray.length;
                System.arraycopy(sourceArray, 0, targetArray, 0, length);
            }else {
                objectMapper.readerForUpdating(object).readValue(jsonString);
            }
        }
        System.out.println("object load result after: "+ System.identityHashCode(object)+": "+JSONUtil.toJsonStr(object));
        return object;
    }

    public static void main(String[] args) throws Exception {
//        AgentClassLoader agentClassLoader =null;
//        System.out.println(agentClassLoader.getClass());
        String path = "E:\\002_Code\\000_github\\APM\\apm-demo\\target\\classes\\com\\wl\\apm\\APMApplicationMain$120240528155747@1377301456.json";
        JSON json = JSONUtil.readJSON(new File(path), StandardCharsets.UTF_8);
        String jsonString = json.toJSONString(0);
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("ccc");
        objects.add("bbb");
        objects.clear();
        objectMapper.readerForUpdating(objects).readValue(jsonString);
        System.out.println("");
    }

//    @Action(ActionType.Object.load)
//    public static <E> E load(Object object,String path){
//        System.out.println("object load result 0: "+ System.identityHashCode(object));
//        //读取文件 参考IDEA setValue
//        Class<?> aClass = object.getClass();
//        Object rs = new Object();
//        if (object instanceof List){
//            //泛型被擦除了,所以用Object
//            rs = JSONFileTool.readList(path, Object.class);
//        }else {
//            rs = JSONFileTool.readObject(path, aClass);
//        }
//
//        BeanUtil.copyProperties(object,rs);
//        try {
//
////            Main.deepCopy(object,rs);
//        } catch (Exception e) {
//            System.out.println("deepCopy Exception");
//        }
////        object=rs;
//        System.out.println("object load result 1: "+ System.identityHashCode(object)+": "+JSONUtil.toJsonStr(object));
//        System.out.println("object load result 2: "+ System.identityHashCode(rs)+": "+JSONUtil.toJsonStr(rs));
//
//        return (E) rs;
//    }

}
