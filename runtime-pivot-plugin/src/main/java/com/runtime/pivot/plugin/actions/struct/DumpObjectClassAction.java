//package com.runtime.pivot.plugin.actions.struct;
//
//import com.intellij.debugger.JavaDebuggerBundle;
//import com.intellij.debugger.engine.JVMNameUtil;
//import com.intellij.ide.util.TreeClassChooser;
//import com.intellij.ide.util.TreeClassChooserFactory;
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.project.Project;
//import com.intellij.psi.CommonClassNames;
//import com.intellij.psi.JavaPsiFacade;
//import com.intellij.psi.PsiClass;
//import com.intellij.psi.search.GlobalSearchScope;
//import org.jetbrains.annotations.NotNull;
//
//public class DumpObjectClassAction extends AnAction {
//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
////        XExpressionImpl xExpression = XExpressionImpl.fromText("initialValue");
//        Project project = e.getProject();
//        final PsiClass throwableClass =
//                JavaPsiFacade.getInstance(project).findClass(CommonClassNames.JAVA_LANG_THROWABLE, GlobalSearchScope.allScope(project));
//        TreeClassChooser chooser = TreeClassChooserFactory.getInstance(project)
//                .createInheritanceClassChooser(JavaDebuggerBundle.message("add.exception.breakpoint.classchooser.title"),
//                        GlobalSearchScope.allScope(project), throwableClass, true, true, null);
//        chooser.showDialog();
//        final PsiClass selectedClass = chooser.getSelected();
//        final String qName = selectedClass == null ? null : JVMNameUtil.getNonAnonymousClassName(selectedClass);
//    }
//}
