package com.runtime.pivot.agent.config;

public interface ActionType {
    enum Program implements ActionType{
        printClassLoadTree,
        printTransformers
    }
    enum Class implements ActionType{
        dumpClass,
        dumpObjectClass,
        dumpClassList,
    }
    enum Method implements ActionType{
        trackTime,
        breakpointRestore,
        exceptionInterrupt,
    }
    enum Object implements ActionType{
        printInfo,
        store,
        load,

    }
}
