package com.runtime.pivot.agent.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeepCopyUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static void deepCopy(Object source, Object target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target must not be null");
        }
        if (!source.getClass().equals(target.getClass())) {
            throw new IllegalArgumentException("Source and target must be of the same type");
        }

        try {
//            String json = objectMapper.writeValueAsString(source);
//            if (json != null && !json.isEmpty()) {
//                if (target instanceof List) {
//                    List sourceList = objectMapper.readValue(json, List.class);
//                    List<?> targetList = (List<?>) target;
//                    targetList.clear();
//                    targetList.addAll(sourceList);
//                } else if (target.getClass().isArray()) {
//                    Object[] sourceArray = objectMapper.readValue(json, Object[].class);
//                    Object[] targetArray = (Object[]) target;
//                    System.arraycopy(sourceArray, 0, targetArray, 0, Math.min(sourceArray.length, targetArray.length));
//                } else {
//                    objectMapper.readerForUpdating(target).readValue(json);
//                }
//            }
            String json = objectMapper.writeValueAsString(source);
            if (json != null && !json.isEmpty()) {
                if (target instanceof List) {
                    objectMapper.readerForUpdating((List)target).readValue(json);
                } else if (target.getClass().isArray()) {
                    objectMapper.readerForUpdating((Object[]) target).readValue(json);
                } else {
                    objectMapper.readerForUpdating(target).readValue(json);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy properties", e);
        }
    }

    public static void main(String[] args) {
        // Test for ordinary object
        MyObject originalObject = new MyObject();
        originalObject.setName("Original");
        originalObject.setValue(123);

//        MyObject copyObject = null;
        MyObject copyObject = new MyObject();
        copyObject.setName("Copy");
        copyObject.setValue(456);
        //MY null应该直接赋值
        deepCopy(originalObject, copyObject);
        System.out.println("Original Object: " + originalObject);
        System.out.println("Copy Object after copy: " + copyObject);

        // Test for array
        MyObject[] originalArray = { new MyObject("Object1", 1), new MyObject("Object2", 2),new MyObject("Object2", 2)  };
//        MyObject[] copyArray = new MyObject[originalArray.length];
        MyObject[] copyArray = new MyObject[]{new MyObject("Objectx", 0), new MyObject("Objectx", 0)};
        deepCopy(originalArray, copyArray);//MY ab两个length必须相等,只做等量替换
        System.out.println("Original Array: " + Arrays.toString(originalArray));
        System.out.println("Copy Array after copy: " + Arrays.toString(copyArray));

        // Test for List
        List<MyObject> originalList = Arrays.asList(new MyObject("List1", 1), new MyObject("List2", 2));
        List<MyObject> copyList = Arrays.asList(new MyObject[originalList.size()]);
        deepCopy(originalList, copyList);
        System.out.println("Original List: " + originalList);
        System.out.println("Copy List after copy: " + copyList);

        // Test for Map
        Map<String, MyObject> originalMap = new HashMap<>();
        originalMap.put("key1", new MyObject("Map1", 1));
        originalMap.put("key2", new MyObject("Map2", 2));

        Map<String, MyObject> copyMap = new HashMap<>();
        deepCopy(originalMap, copyMap);
        System.out.println("Original Map: " + originalMap);
        System.out.println("Copy Map after copy: " + copyMap);
    }
}

class MyObject {
    private String name;
    private int value;

    public MyObject() {
    }

    public MyObject(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "MyObject{name='" + name + "', value=" + value + '}';
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
