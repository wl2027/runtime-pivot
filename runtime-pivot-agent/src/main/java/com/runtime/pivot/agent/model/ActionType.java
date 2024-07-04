package com.runtime.pivot.agent.model;

public interface ActionType {
    interface Program extends ActionType{
        //CL 树
        String classLoaderTree = "classLoaderTree";
        //CL Class树
        String classLoaderClassTree = "classLoaderClassTree";
        String transformers = "transformers";
    }
    interface Class extends ActionType{

        //类加载过程
        String classLoadingProcess = "classLoadingProcess";
        //类文件转储
        String classFileDump = "classFileDump";
    }
    interface Thread extends ActionType{

    }
    interface Frame extends ActionType{

    }
    interface Method extends ActionType{
        String trackTime = "trackTime";
        String breakpointRestore = "breakpointRestore";
        String exceptionRestore = "exceptionRestore";
    }
    interface Object extends ActionType{
        //对象内部信息
        String objectInternals = "objectInternals";
        //对象存储
        String objectStore = "objectStore";
        //对象加载
        String objectLoad = "objectLoad";
    }
}
