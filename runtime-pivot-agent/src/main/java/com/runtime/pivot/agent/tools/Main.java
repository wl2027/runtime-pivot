package com.runtime.pivot.agent.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            // 创建Person对象
            Person person1 = new Person("Alice", 30);
            Person person2 = new Person("Bob", 25);
            System.out.println("Before: " + person1);

            // 将person2的值复制给person1
            deepCopy(person1, person2);

            System.out.println("After: " + person1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deepCopy(Object dest, Object src) throws Exception {
        if (dest == null || src == null) {
            throw new IllegalArgumentException("Source and destination objects cannot be null");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode srcNode = mapper.valueToTree(src);

        Map<Object, Object> visited = new IdentityHashMap<>();
        deepCopyRecursive(dest, srcNode, mapper, visited);
    }

    private static void deepCopyRecursive(Object dest, JsonNode srcNode, ObjectMapper mapper, Map<Object, Object> visited) throws Exception {
        if (visited.containsKey(dest)) {
            return; // Avoid cyclic references
        }

        visited.put(dest, srcNode);

        Iterator<String> fieldNames = srcNode.fieldNames();

        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode fieldValue = srcNode.get(fieldName);

            Field field = getField(dest.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);

                Class<?> fieldType = field.getType();

                if (fieldValue.isObject() && !fieldType.isPrimitive() && !fieldType.equals(String.class)) {
                    if (Map.class.isAssignableFrom(fieldType)) {
                        Map<Object, Object> mapDest = (Map<Object, Object>) field.get(dest);
                        if (mapDest == null) {
                            mapDest = new HashMap<>();
                            field.set(dest, mapDest);
                        }
                        deepCopyMap(mapDest, fieldValue, mapper, visited);
                    } else if (Collection.class.isAssignableFrom(fieldType)) {
                        Collection<Object> collectionDest = (Collection<Object>) field.get(dest);
                        if (collectionDest == null) {
                            collectionDest = new ArrayList<>();
                            field.set(dest, collectionDest);
                        }
                        deepCopyCollection(collectionDest, fieldValue, mapper, visited);
                    } else if (fieldType.isArray()) {
                        Object arrayDest = field.get(dest);
                        if (arrayDest == null) {
                            arrayDest = Array.newInstance(fieldType.getComponentType(), fieldValue.size());
                            field.set(dest, arrayDest);
                        }
                        deepCopyArray(arrayDest, fieldValue, mapper, visited);
                    } else {
                        Object childDest = field.get(dest);
                        if (childDest == null) {
                            childDest = fieldType.newInstance();
                            field.set(dest, childDest);
                        }
                        deepCopyRecursive(childDest, fieldValue, mapper, visited);
                    }
                } else {
                    Object value = mapper.treeToValue(fieldValue, fieldType);
                    field.set(dest, value);
                }
            }
        }
    }

    private static void deepCopyMap(Map<Object, Object> dest, JsonNode srcNode, ObjectMapper mapper, Map<Object, Object> visited) throws Exception {
        Iterator<Map.Entry<String, JsonNode>> fields = srcNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            JsonNode valueNode = entry.getValue();

            Object keyObject = mapper.treeToValue(mapper.valueToTree(key), Object.class);
            Object valueObject = mapper.treeToValue(valueNode, Object.class);

            if (valueNode.isObject()) {
                if (!visited.containsKey(valueObject)) {
                    deepCopyRecursive(valueObject, valueNode, mapper, visited);
                }
            }

            dest.put(keyObject, valueObject);
        }
    }

    private static void deepCopyCollection(Collection<Object> dest, JsonNode srcNode, ObjectMapper mapper, Map<Object, Object> visited) throws Exception {
        for (JsonNode elementNode : srcNode) {
            Object element = mapper.treeToValue(elementNode, Object.class);
            if (elementNode.isObject() && !visited.containsKey(element)) {
                deepCopyRecursive(element, elementNode, mapper, visited);
            }
            dest.add(element);
        }
    }

    private static void deepCopyArray(Object dest, JsonNode srcNode, ObjectMapper mapper, Map<Object, Object> visited) throws Exception {
        int length = Array.getLength(dest);
        for (int i = 0; i < length; i++) {
            JsonNode elementNode = srcNode.get(i);
            Object element = mapper.treeToValue(elementNode, dest.getClass().getComponentType());
            if (elementNode.isObject() && !visited.containsKey(element)) {
                deepCopyRecursive(element, elementNode, mapper, visited);
            }
            Array.set(dest, i, element);
        }
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        Field field = null;
        while (clazz != null) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return field;
    }
}

class Person {
    private String name;
    private int age;
    private Address address;
    private List<String> hobbies;
    private Map<String, String> attributes;

    public Person() {
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        this.address = new Address("Street 1", "City A");
        this.hobbies = Arrays.asList("Reading", "Traveling");
        this.attributes = new HashMap<>();
        this.attributes.put("HairColor", "Brown");
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + ", address=" + address +
                ", hobbies=" + hobbies + ", attributes=" + attributes + "}";
    }
}

class Address {
    private String street;
    private String city;

    public Address() {
    }

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }

    @Override
    public String toString() {
        return "Address{street='" + street + "', city='" + city + "'}";
    }
}

