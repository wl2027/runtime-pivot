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
package com.runtime.pivot.plugin.utils.platfrom;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.ui.tree.nodes.XEvaluationCallbackBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValuePresentationUtil;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class XTestEvaluationCallback extends XEvaluationCallbackBase {
  private XValue myResult;
  private String myErrorMessage;
  private Consumer<XValue> myEvaluated;
  private Consumer<String> myErrorOccurred;
  public XTestEvaluationCallback() {
  }

  public XTestEvaluationCallback(Consumer<XValue> evaluated, Consumer<String> errorOccurred) {
    this.myEvaluated = evaluated;
    this.myErrorOccurred = errorOccurred;
  }

  @Override
  public void evaluated(@NotNull XValue result) {
    myResult = result;
    Value value = ((JavaValue) result).getDescriptor().getValue();
    Type type = value.type();
    String.valueOf(((JavaValue)(result)).getDescriptor().getValue());
    if (myEvaluated != null) {
      myEvaluated.accept(result);
    }

  }

  @Override
  public void errorOccurred(@NotNull String errorMessage) {

    myErrorMessage = errorMessage;
    if (myErrorOccurred != null) {
      myErrorOccurred.accept(errorMessage);
    }
  }

}
