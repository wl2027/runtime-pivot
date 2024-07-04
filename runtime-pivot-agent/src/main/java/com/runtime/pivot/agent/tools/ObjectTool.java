package com.runtime.pivot.agent.tools;

public class ObjectTool {
    public static String getHexId(Object object){
        //System.identityHashCode(object)
        return Integer.toHexString(object.hashCode());
    }
    public static String toString(Object object){
        return object.getClass().getName() + "@"+getHexId(object);
    }
}
