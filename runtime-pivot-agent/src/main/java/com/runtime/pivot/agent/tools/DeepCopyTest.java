//package com.runtime.pivot.agent.tools;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class DeepCopyTest {
//
//    public static void main(String[] args) {
//        // Test for ordinary object
//        MyObject originalObject = new MyObject();
//        originalObject.setName("Original");
//        originalObject.setValue(123);
//
//        MyObject copyObject = new MyObject();
////        originalObject.setName("copy");
////        originalObject.setValue(321);
//        deepCopy(originalObject, copyObject);
//        System.out.println("Original Object after copy: " + originalObject);
//        System.out.println("Copy Object: " + copyObject);
//
//        // Test for array
//        MyObject[] originalArray = { new MyObject("Object1", 1), new MyObject("Object2", 2) };
//        MyObject[] copyArray = new MyObject[originalArray.length];
//        deepCopy(originalArray, copyArray);
//        System.out.println("Original Array after copy: " + Arrays.toString(originalArray));
//        System.out.println("Copy Array: " + Arrays.toString(copyArray));
//
//        // Test for List
//        List<MyObject> originalList = Arrays.asList(new MyObject("List1", 1), new MyObject("List2", 2));
//        List<MyObject> copyList = Arrays.asList(new MyObject[originalList.size()]);
//        deepCopy(originalList, copyList);
//        System.out.println("Original List after copy: " + originalList);
//        System.out.println("Copy List: " + copyList);
//
//        // Test for Map
//        Map<String, MyObject> originalMap = new HashMap<>();
//        originalMap.put("key1", new MyObject("Map1", 1));
//        originalMap.put("key2", new MyObject("Map2", 2));
//
//        Map<String, MyObject> copyMap = new HashMap<>();
//        deepCopy(originalMap, copyMap);
//        System.out.println("Original Map after copy: " + originalMap);
//        System.out.println("Copy Map: " + copyMap);
//    }
//
//    public static void deepCopy(Object source, Object target) {
//        if (source == null || target == null) {
//            throw new IllegalArgumentException("Source and target must not be null");
//        }
//        if (!source.getClass().equals(target.getClass())) {
//            throw new IllegalArgumentException("Source and target must be of the same type");
//        }
//
//        // Convert source object to JSON string
//        String jsonString = JSONUtil.toJsonStr(source);
//        // Parse JSON string to JSONObject
//        JSONObject jsonObject = JSONUtil.parseObj(jsonString);
//        // Copy properties from JSONObject to target object
//        BeanUtil.copyProperties(jsonObject, target);
//    }
//}
//
//class MyObject {
//    private String name;
//    private int value;
//
//    public MyObject() {
//    }
//
//    public MyObject(String name, int value) {
//        this.name = name;
//        this.value = value;
//    }
//
//    @Override
//    public String toString() {
//        return "MyObject{name='" + name + "', value=" + value + '}';
//    }
//
//    // Getters and setters
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getValue() {
//        return value;
//    }
//
//    public void setValue(int value) {
//        this.value = value;
//    }
//}
