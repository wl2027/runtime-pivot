```banner https://patorjk.com/software/taag/
__________ ____ _____________________.___   _____  ___________        __________._______   _______________________
\______   \    |   \      \__    ___/|   | /     \ \_   _____/        \______   \   \   \ /   /\_____  \__    ___/
 |       _/    |   /   |   \|    |   |   |/  \ /  \ |    __)_   ______ |     ___/   |\   Y   /  /   |   \|    |   
 |    |   \    |  /    |    \    |   |   /    Y    \|        \ /_____/ |    |   |   | \     /  /    |    \    |   
 |____|_  /______/\____|__  /____|   |___\____|__  /_______  /         |____|   |___|  \___/   \_______  /____|   
        \/                \/                     \/        \/                                          \/         
```
# runtime-pivot-plugin

[![Version](https://img.shields.io/jetbrains/plugin/v/com.runtime.pivot.plugin.svg)](https://plugins.jetbrains.com/plugin/23828-runtime-pivot)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.runtime.pivot.plugin.svg)](https://plugins.jetbrains.com/plugin/23828-com.runtime.pivot.plugin)
![Downloads](https://img.shields.io/github/release/wl2027/runtime-pivot.svg)
![Downloads](https://img.shields.io/github/stars/wl2027/runtime-pivot)
![Downloads](https://img.shields.io/badge/license-GPLv3-blue.svg)
![Downloads](https://img.shields.io/badge/Java-8-brightgreen.svg?style=flat)
![Downloads](https://img.shields.io/badge/Java-17-brightgreen.svg?style=flat)
[![GitHub](https://img.shields.io/static/v1?label=&message=GitHub&logo=github&color=black&labelColor=555)](https://github.com/wl2027/runtime-pivot) 
[![Gitee](https://img.shields.io/static/v1?label=&message=Gitee&logo=gitee&color=orange&labelColor=555)](https://gitee.com/wl2027/runtime-pivot)

## Introduction
<!-- Plugin description -->
### English:
runtime-pivot is a runtime enhancement toolkit that provides convenient features for developers when debugging code.

The current features are divided into four dimensions:
- **program**: Analyzes instrument data during program runtime.
- **class**: Analyzes bytecode information of classes in memory during program runtime.
- **session**: Analyzes code invocation information during debugging sessions in program runtime.
- **object**: Analyzes and manipulates object memory during program runtime.

Comparison with similar tools:

|     | runtime-pivot         | arthas          |
|:--|:--|:--|
| Focus  | Debugging during development | Diagnosing issues online |
| Features  | Analysis at specific breakpoints | Analysis of JVM and methods |
| ... |                     |                   |

### 中文:
runtime-pivot 是一个运行时增强工具集,为开发人员在调试代码时提供便捷的功能.

当前功能分为四个维度:
- program 分析程序运行时instrument数据
- class 分析程序运行时内存类字节码信息
- session 分析程序运行中调试会话的代码调用信息
- object 分析和操作程序运行时对象内存信息

类似工具差异说明:

|     | runtime-pivot | arthas      |
|:--|:--|:--|
| 定位  | 开发阶段的调试工具     | 线上问题诊断工具    |
| 特点  | 针对特定断点的分析     | 针对JVM和方法的分析 |
| ... |               |             |

<!-- Plugin description end -->

## Features
- **program**
  - [x] View the runtime classLoader tree structure information. 查看运行时的classLoader树结构信息.
  - [x] View the runtime classLoader loaded classes tree structure information. 查看运行时的classLoader加载类的树结构信息.
  - [x] View the runtime transformers list information. 查看运行时的transformers列表信息.
- **class**
  - [x] View the runtime class loading chain information. 查看运行时class加载链路信息.
  - [x] Dump the runtime class bytecode information. 转储运行时class字节码信息.
- **session**
  - [x] Monitor the runtime code invocations. 监控运行时代码调用.
  - [x] View the runtime breakpoints list. 查看运行时断点列表.
- **object**
  - [x] View the runtime object memory layout. 查看运行时对象内存布局.
  - [x] Dump the runtime object JSON data. 转储运行时对象json数据.
  - [x] Load JSON data to update the runtime object. 加载json数据更新运行时对象.


## Installation

- **Using the IDE built-in plugin system:**

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "runtime-pivot"</kbd> >
  <kbd>Install</kbd>

- **Manually:**

  Download the [latest release](https://github.com/wl2027/runtime-pivot/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

Restart the **IDE** after installation.

## Using The Plugin

Using the open-source project [xxl-job](https://github.com/xuxueli/xxl-job) as an example, run the program and enter the breakpoint. 以开源项目[xxl-job](https://github.com/xuxueli/xxl-job)为例,运行程序并进入断点。

1.1 View the runtime classLoader tree structure information, the operation result is printed to the console. 查看运行时的 classLoader 树结构信息，操作结果打印到控制台。
![1.1 CLT.gif](doc%2Foperation%2F1.1%20CLT.gif)

1.2 View the runtime classLoader loaded classes tree structure information, the operation result is printed to the console. 查看运行时 classLoader 加载类的树结构信息，操作结果打印到控制台。
![1.2 CLTCT.gif](doc%2Foperation%2F1.2%20CLTCT.gif)

1.3 View the runtime transformers list information, the operation result is printed to the console. 查看运行时 transformers 列表信息，操作结果打印到控制台。
![1.3 TRS.gif](doc%2Foperation%2F1.3%20TRS.gif)

2.1 View the runtime class loading chain information, applicable to class files, search boxes, and runtime objects. The operation result is printed to the console. 查看运行时 class 加载链路信息，可作用于类文件、搜索框、运行时对象，操作结果打印到控制台。
![2.1 CPS.gif](doc%2Foperation%2F2.1%20CPS.gif)

2.2 Dump the runtime class bytecode information, applicable to class files, search boxes, and runtime objects. The dump path is the ```.runtime``` directory of the current project and is printed to the console. 转储运行时 class 字节码信息，可作用于类文件、搜索框、运行时对象。转储路径为当前项目的 ```.runtime``` 目录，并打印到控制台。
![2.2 CFD.gif](doc%2Foperation%2F2.2%20CFD.gif)

3.1 Monitor runtime code invocations, outputting overall time and time distribution between breakpoints. 监控运行时代码调用，输出总体时间和断点间时间分布。
![3.1 MT.gif](doc%2Foperation%2F3.1%20MT.gif)

3.2 View the runtime breakpoints list, outputting the breakpoint list information of the currently selected stack frame, and can navigate to the code location with a single click. 查看运行时断点列表，输出当前选择栈帧的断点列表信息，单击可导航至代码位置。
![3.2 SL.gif](doc%2Foperation%2F3.2%20SL.gif)

4.1 View the runtime object memory layout, including object size, occupied size, and object header information. 查看运行时对象内存布局，包括对象大小、占用大小、对象头信息。
![4.1 OI.gif](doc%2Foperation%2F4.1%20OI.gif)

4.2 Dump the runtime object's JSON data. The dump path is the ```.runtime``` directory of the current project and is printed to the console. 转储运行时对象的 JSON 数据，转储路径为当前项目的 ```.runtime``` 目录，并打印到控制台。
![4.2 OS.gif](doc%2Foperation%2F4.2%20OS.gif)

4.3 Load JSON data to update the runtime object. The default path is the ```.runtime``` directory of the current project. When loading collection data, empty collections will lose their generics. 加载 JSON 数据更新运行时对象，默认路径为当前项目的 ```.runtime``` 目录，加载集合数据时空集合会擦除泛型。
![4.3 OL.gif](doc%2Foperation%2F4.3%20OL.gif)

## Compatibility

- [ ] Android Studio
- [ ] AppCode
- [ ] CLion
- [ ] DataGrip
- [ ] GoLand
- [ ] HUAWEI DevEco Studio
- [x] **IntelliJ IDEA Ultimate**
- [x] IntelliJ IDEA Community
- [x] IntelliJ IDEA Educational
- [ ] MPS
- [ ] PhpStorm
- [ ] PyCharm Professional
- [ ] PyCharm Community
- [ ] PyCharm Educational
- [ ] Rider
- [ ] RubyMine
- [ ] WebStorm


## Contributing

Welcome to contribute to the project! You can fix bugs by submitting a Pull Request (PR) or discuss new features or changes by creating an [Issue](https://github.com/wl2027/data-pivot-plugin/issues/). Look forward to your valuable contributions!

欢迎参与项目贡献！如您可以通过提交Pull Request（PR）来修复bug，或者新建 [Issue](https://github.com/wl2027/data-pivot-plugin/issues/) 来讨论新特性或变更，期待您的宝贵贡献！

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
