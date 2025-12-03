# ToutiaoLite (头条精简版)

- 项目定位：使用 Jetpack Compose + MVI 架构，高保真还原“今日头条”首页新闻流的交互体验。
- 技术栈：Kotlin、Jetpack Compose(Material3)、Room、Hilt、Coroutines/Flow、Coil；Min SDK 24；Gradle（Kotlin DSL）。

## 功能概览
- 多布局新闻卡片：纯文本/置顶、右侧单图、视频/大图、三图。
- 列表能力：`LazyColumn` 渲染、下拉刷新、上拉加载更多（分页）。
- 状态管理：Loading / Content / Error / Empty。
- 离线缓存：Room 实现 Single Source of Truth，UI 通过 Flow 观察数据库。

## 架构说明
- 模式：MVI（单向数据流）。
- 视图仅根据 `State` 渲染；用户操作以 `Intent` 形式发送给 `ViewModel`；`Repository` 统一提供数据并写入数据库。

## 开发与运行
- 打开 `d:\Android\Work\toutiao` 于 Android Studio（推荐最新稳定版）。
- 同步 Gradle，选择目标设备运行 `app`（Mock 数据源，后续可接入网络）。

## 参考文档与素材
- 需求文档：`前置/prd.md`
- 原型与截图：`前置/原型图.html`、`前置/屏幕截图*.png`

## 路线图（简要）
- 完成多类型列表与加载状态。
- 接入 Room 缓存与分页加载。
- 引入 Hilt 进行依赖注入与模块化。
- 优化图片/视频加载与性能。
