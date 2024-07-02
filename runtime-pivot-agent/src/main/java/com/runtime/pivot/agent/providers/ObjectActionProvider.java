package com.runtime.pivot.agent.providers;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.runtime.pivot.agent.ActionContext;
import com.runtime.pivot.agent.ActionExecutor;
import com.runtime.pivot.agent.config.AgentConstants;
import com.runtime.pivot.agent.model.Action;
import com.runtime.pivot.agent.model.ActionProvider;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.agent.tools.ObjectTool;
import org.apache.lucene.util.RamUsageEstimator;
import org.openjdk.jol.info.ClassLayout;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public class ObjectActionProvider extends ActionProvider {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()); // Support for Java 8 date/time types
    }

    @Action(ActionType.Object.objectInternals)
    public static String internals(Object object){
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
        String printable = ClassLayout.parseInstance(object).toPrintable();
        System.out.println(printable);
        return ObjectTool.toString(object)+" layout has been printed on the console";
    }

    @Action(ActionType.Object.objectStore)
    public static String store(Object object,String path) throws Exception{
        ActionContext actionContext = ActionExecutor.getActionContext();
        String dateFileString = actionContext.getDateFileString();
        //转成JSON
        path = path+ AgentConstants.PATH+File.separator+ActionType.Object.objectStore +File.separator+dateFileString;
        // 创建一个格式化的 JSON 写入器
        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        // 将 JSON 格式字符串写入到文件
        String className = object.getClass().getName();
        String filePath = path + File.separatorChar + className.replace('.', File.separatorChar) +"@"+ObjectTool.getHexId(object)+".json";
        File touch = FileUtil.touch(filePath);
        writer.writeValue(touch, object);
        System.out.println("object: "+ObjectTool.toString(object) +"\nObject store path: "+touch.getPath());
        return ObjectTool.toString(object)+" Object store path has been printed on the console";
    }


    @Action(ActionType.Object.objectLoad)
    public static <E> String load(E object,String path) throws Exception {
        if (object == null) {
            //值赋上去没有任何用处,因为null本身没有开辟任何地址空间 : 直接赋值~对象,数组,集合,Map...
            //object = (E) objectMapper.readValue(jsonString,Object.class);
            return "Object cannot be empty";
        }
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        if (object.getClass().isArray()){
            //暂时不处理数组
            if (true) return ObjectTool.toString(object)+" processing array data is not supported";
            Object[] sourceArray = objectMapper.readValue(new File(path), Object[].class);
            Object[] targetArray = (Object[]) object;
            int length = targetArray.length;
            System.arraycopy(sourceArray, 0, targetArray, 0, length);
        }else if (object instanceof Collection && !((Collection<?>) object).isEmpty()) {
            Collection<?> collection = (Collection<?>) object;
            CollectionType collectionType = typeFactory.constructCollectionType(collection.getClass(), typeFactory.constructType(collection.iterator().next().getClass()));
            collection.clear();
            collection.addAll(objectMapper.readValue(new File(path), collectionType));
        } else if (object instanceof Map && !((Map<?, ?>) object).isEmpty()) {
            Map<?, ?> map = (Map<?, ?>) object;
            MapType mapType = typeFactory.constructMapType(map.getClass(), typeFactory.constructType(map.keySet().iterator().next().getClass()), typeFactory.constructType(map.values().iterator().next().getClass()));
            map.clear();
            map.putAll(objectMapper.readValue(new File(path), mapType));
        }else {
            objectMapper.readerForUpdating(object).readValue(new File(path));
        }
        System.out.println(ObjectTool.toString(object) + " Object has reloaded data");
        return ObjectTool.toString(object)+ " Object has reloaded data";
    }

}
