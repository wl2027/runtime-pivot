package com.runtime.pivot.agent.providers;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.runtime.pivot.agent.tools.JSONFileTool;
import com.runtime.pivot.agent.tools.ObjectTool;
import org.apache.lucene.util.RamUsageEstimator;
import org.openjdk.jol.info.ClassLayout;

import java.io.File;
import java.nio.charset.StandardCharsets;
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
        System.out.println(ClassLayout.parseInstance(object).toPrintable());
        return object+" layout has been printed on the console";
    }

    @Action(ActionType.Object.objectStore)
    public static String store(Object object,String path){
        ActionContext actionContext = ActionExecutor.getActionContext();
        String dateFileString = actionContext.getDateFileString();
        //转成JSON
        path = path+ AgentConstants.PATH+File.separator+ActionType.Object.objectStore +File.separator+dateFileString;
        String writePath = JSONFileTool.write(object,path);
        System.out.println("object: "+ObjectTool.toString(object) +"\nObject store path: "+writePath);
        return ObjectTool.toString(object)+" Object store path has been printed on the console";
    }


    @Action(ActionType.Object.objectLoad)
    public static <E> String load(E object,String path) throws Exception {
        if (object == null) {
            //值赋上去没有任何用处,因为null本身没有开辟任何地址空间 : 直接赋值~对象,数组,集合,Map...
            //object = (E) objectMapper.readValue(jsonString,Object.class);
            return "Object cannot be empty";
        }
        System.out.println("object load result before: "+ ObjectTool.toString(object)+": \n"+JSONUtil.toJsonStr(object));
        //读取文件 参考IDEA setValue
        JSON json = JSONUtil.readJSON(new File(path), StandardCharsets.UTF_8);
        String jsonString = json.toJSONString(0);
        if (StrUtil.isBlank(jsonString)) {
            //没有json数据
            return path+" there is no JSON data";
        }
        if (object instanceof Collection){
            ((Collection)object).clear();
        }
        if (object instanceof Map){
            ((Map)object).clear();
        }

        TypeFactory typeFactory = objectMapper.getTypeFactory();

        if (object.getClass().isArray()){
            //暂时不处理数组
            if (true) return ObjectTool.toString(object)+" processing array data is not supported";
            Object[] sourceArray = objectMapper.readValue(jsonString, Object[].class);
            Object[] targetArray = (Object[]) object;
            int length = targetArray.length;
            System.arraycopy(sourceArray, 0, targetArray, 0, length);
        }else if (object instanceof Collection && !((Collection<?>) object).isEmpty()) {
            Collection<?> collection = (Collection<?>) object;
            CollectionType collectionType = typeFactory.constructCollectionType(collection.getClass(), typeFactory.constructType(collection.iterator().next().getClass()));
            collection.clear();
            System.out.println("object is not empty list");
            collection.addAll(objectMapper.readValue(jsonString, collectionType));
        } else if (object instanceof Map && !((Map<?, ?>) object).isEmpty()) {
            Map<?, ?> map = (Map<?, ?>) object;
            MapType mapType = typeFactory.constructMapType(map.getClass(), typeFactory.constructType(map.keySet().iterator().next().getClass()), typeFactory.constructType(map.values().iterator().next().getClass()));
            map.clear();
            map.putAll(objectMapper.readValue(jsonString, mapType));
        }else {
            objectMapper.readerForUpdating(object).readValue(jsonString);
        }
        System.out.println("object load result after: "+ ObjectTool.toString(object)+": \n"+JSONUtil.toJsonStr(object));
        return ObjectTool.toString(object)+ " Object has reloaded data";
    }

}
