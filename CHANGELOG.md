<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# runtime-pivot-plugin Changelog

## [Unreleased]

## [2.0.0] - 2025-03-23
### Refactor
- 重构项目模块

## [1.1.2] - 2024-07-15
### Changed
- agent升级到1.1.0
- 修改报错信息返回展示
- 更新issue模板

## [1.1.1] - 2024-07-12
### Changed
- 修改一些国际化文本
- 修改操作说明
- MacOS对Agent Jar的操作支持
- 默认开启探针

### Changed
- 修改操作说明

## [1.1.0] - 2024-07-09
### Added
- 添加一些提示文本的国际化
- 添加双击断点列表回溯栈帧
- 修改attach探针默认配置为false
- 添加相应说明文档

## [1.0.1] - 2024-07-06
### Fixed
- 解决安装目录有空格导致探针失效问题,将探针复制到用户目录再执行

### Changed
- 更新readme的pluginId和对比说明
- 更新issue模板的idea错误报告

### Removed
- remove internal API 

## [1.0.0] - 2024-07-05
### Added 
project initialization
- program 分析程序运行时instrument数据
- class 分析程序运行时内存类字节码信息
- session 分析程序运行中调试会话的代码调用信息
- object 分析和操作程序运行时对象内存信息


[2.0.0]: https://github.com/wl2027/runtime-pivot/compare/1.1.2...2.0.0
[1.1.2]: https://github.com/wl2027/runtime-pivot/compare/1.1.1...1.1.2
[1.1.1]: https://github.com/wl2027/runtime-pivot/compare/1.1.0...1.1.1
[1.1.0]: https://github.com/wl2027/runtime-pivot/compare/1.0.1...1.1.0
[1.0.1]: https://github.com/wl2027/runtime-pivot/compare/1.0.0...1.0.1
[1.0.0]: https://github.com/wl2027/runtime-pivot/commits/1.0.0
