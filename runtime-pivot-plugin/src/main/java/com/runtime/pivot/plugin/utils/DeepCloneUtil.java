package com.runtime.pivot.plugin.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 序列化=>二进制byte=>impl Ser =>反射拿构造+=>Unsafe=>处理数组类型=>处理接口和抽象类=>静态变量=>循环依赖
 */
public class DeepCloneUtil {

    private static final Unsafe UNSAFE;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to access Unsafe", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T deepClone(T object) {
        return (T) deepClone(object, new IdentityHashMap<>());
    }

    private static Object deepClone(Object object, Map<Object, Object> clonedObjects) {
        if (object == null) {
            return null;
        }

        if (clonedObjects.containsKey(object)) {
            return clonedObjects.get(object);
        }

        try {
            Class<?> clazz = object.getClass();

            // 处理数组类型
            if (clazz.isArray()) {
                int length = Array.getLength(object);
                Object clone = Array.newInstance(clazz.getComponentType(), length);
                clonedObjects.put(object, clone);
                for (int i = 0; i < length; i++) {
                    Array.set(clone, i, deepClone(Array.get(object, i), clonedObjects));
                }
                return clone;
            }

            // 处理接口和抽象类
            if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) {
                throw new UnsupportedOperationException("Cannot clone abstract class or interface: " + clazz.getName());
            }

            Object clone = UNSAFE.allocateInstance(clazz);
            clonedObjects.put(object, clone);

            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);
                Object fieldValue = field.get(object);

                if (fieldValue != null && !field.getType().isPrimitive() && !(fieldValue instanceof String)) {
                    Object clonedFieldValue = deepClone(fieldValue, clonedObjects);
                    field.set(clone, clonedFieldValue);

                    // 调试信息
                    if (clonedFieldValue == null) {
                        System.err.println("Field " + field.getName() + " in class " + clazz.getName() + " was not cloned properly.");
                    } else {
                        System.out.println("Field " + field.getName() + " in class " + clazz.getName() + " cloned successfully.");
                    }
                } else {
                    field.set(clone, fieldValue);
                }
            }
            return clone;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during cloning", e);
        }
    }
}
