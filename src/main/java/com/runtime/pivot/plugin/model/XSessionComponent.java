package com.runtime.pivot.plugin.model;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;

import javax.swing.*;
import java.awt.*;

public abstract class XSessionComponent<T extends Component> extends JDialog implements Disposable{

    protected Project myProject;
    protected XDebugSession myXDebugSession;
    protected XDebugSessionListener myXDebugSessionListener;

    protected XSessionComponent(XDebugSession xDebugSession,String title) {
        super(WindowManager.getInstance().getFrame(xDebugSession.getProject()), title, false);
        this.myProject = xDebugSession.getProject();
        this.myXDebugSession = xDebugSession;
        myXDebugSessionListener = getXDebugSessionListener();
        xDebugSession.addSessionListener(myXDebugSessionListener);
    }

    //获取实例
    //public abstract T getInstance(XDebugSession xDebugSession);

    //获取会话监听器
    public abstract XDebugSessionListener getXDebugSessionListener();

    //初始化数据
    public abstract void initData(XStackContext xStackContext);

    //更新数据
    public abstract void updateData(XStackContext xStackContext);
    public abstract void removeXSessionComponent();

    //关闭窗口和点击关闭按钮时执行的操作
    public void closeComponent(){
        removeXSessionComponent();
        myXDebugSession.removeSessionListener(myXDebugSessionListener);
    }
}
