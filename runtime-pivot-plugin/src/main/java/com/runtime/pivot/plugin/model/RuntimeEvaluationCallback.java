/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.runtime.pivot.plugin.model;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.ide.impl.ProjectNewWindowDoNotAskOption;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.MessageUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.ui.tree.nodes.XEvaluationCallbackBase;
import com.runtime.pivot.plugin.config.RuntimePivotConstants;
import com.sun.jdi.Value;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RuntimeEvaluationCallback extends XEvaluationCallbackBase {
  private JavaValue myJavaResult;
  private String myResultString;
  private String myErrorMessage;
  private Consumer<JavaValue> myEvaluated;
  private Consumer<String> myErrorOccurred;
  public RuntimeEvaluationCallback() {
  }
  public RuntimeEvaluationCallback(Consumer<JavaValue> evaluated) {
    this.myEvaluated = evaluated;
  }

  public RuntimeEvaluationCallback(Consumer<JavaValue> evaluated, Consumer<String> errorOccurred) {
    this.myEvaluated = evaluated;
    this.myErrorOccurred = errorOccurred;
  }

  @Override
  public void evaluated(@NotNull XValue result) {
    myJavaResult = ((JavaValue) result);
    Value value = myJavaResult.getDescriptor().getValue();
    myResultString = String.valueOf(value);
    if (myEvaluated != null) {
      myEvaluated.accept(myJavaResult);
    }
    ApplicationManager.getApplication().invokeLater(()->Messages.showMessageDialog("执行结果: \n"+myResultString,RuntimePivotConstants.RESULT_MSG_TITLE,null));
  }

  @Override
  public void errorOccurred(@NotNull String errorMessage) {
    myErrorMessage = errorMessage;
    if (myErrorOccurred != null) {
      myErrorOccurred.accept(errorMessage);
    }
    ApplicationManager.getApplication().invokeLater(()->Messages.showErrorDialog("异常信息: \n"+myErrorOccurred,RuntimePivotConstants.ERROR_MSG_TITLE));
  }

  public JavaValue getJavaResult() {
    return myJavaResult;
  }

  public String getResultString() {
    return myResultString;
  }

  public String getErrorMessage() {
    return myErrorMessage;
  }
}
