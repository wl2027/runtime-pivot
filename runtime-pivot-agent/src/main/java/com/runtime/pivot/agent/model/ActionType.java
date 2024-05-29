package com.runtime.pivot.agent.model;

public interface ActionType {
    interface Program extends ActionType{
        String classLoaderTree = "classLoaderTree";
        String classLoaderClassTree = "classLoaderClassTree";
        String transformers = "transformers";
    }
    interface Class extends ActionType{

        //加载顺序
        String classLoadingProcess = "classLoadingProcess";
        String classFileDump = "classFileDump";
//        String dumpClass = "dumpClass";
//        String dumpObjectClass = "dumpObjectClass";
//        String dumpClassList = "dumpClassList";

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

        String internals = "internals";
        String store = "store";
        String load = "load";
    }
}
