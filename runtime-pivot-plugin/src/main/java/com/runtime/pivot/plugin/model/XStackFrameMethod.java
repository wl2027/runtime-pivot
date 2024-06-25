package com.runtime.pivot.plugin.model;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiMethod;

public class XStackFrameMethod {
    private final int start;
    private final int end;
    private final int line;

    private final VirtualFile virtualFile;
    private final PsiMethod psiMethod;

    public XStackFrameMethod(VirtualFile virtualFile, PsiMethod psiMethod, int start, int end, int line) {
        this.start = start;
        this.end = end;
        this.line = line;
        this.virtualFile = virtualFile;
        this.psiMethod = psiMethod;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getLine() {
        return line;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public PsiMethod getPsiMethod() {
        return psiMethod;
    }
}

