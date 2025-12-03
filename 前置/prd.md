# 产品需求文档 (PRD): 头条首页列表 Demo (Android)

| 项目名称 | ToutiaoLite (头条精简版) |
| :--- | :--- |
| **版本** | v1.0.0 |
| **状态** | 规划中 |
| **技术栈** | Android (Kotlin), Jetpack Compose, Room, MVI/MVVM |

---

## 1. 项目概述 (Project Overview)

### 1.1 目标
开发一个 Android 客户端 Demo，高保真还原“今日头条”App 首页的新闻列表交互体验。重点展示现代 Android 开发技术栈（Compose + MVI）在复杂列表、多布局类型及本地持久化存储中的应用。

### 1.2 核心场景
用户打开 App，查看顶部栏（天气/搜索），通过标签切换频道，浏览不同样式的新闻卡片（纯文、图文、视频），支持下拉刷新获取新数据，上拉加载更多历史数据，且在无网环境下能加载缓存数据。

---

## 2. 界面设计需求 (UI Requirements)
*参考依据：用户上传的截图*

### 2.1 整体布局结构
采用标准的 **Scaffold** 结构：
1.  **TopBar (顶部功能区)**
2.  **TabBar (频道导航栏)**
3.  **Content (新闻列表流)**
4.  **BottomBar (底部导航栏)**

### 2.2 详细 UI 拆解

#### A. 顶部区域 (Header)
* **搜索框**: 红色背景，中间包含“搜索”图标及占位文字（如：“习近平主席重要论述...”），右侧有“搜索”按钮。
* **状态信息**: 左上角显示圆形 Logo/天气图标，温度（如 “14°”），地点（如 “北京”），天气状况（如 “多云”）。
* **功能入口**: 右上角显示“发布”(+) 图标及 AI 助手入口。

#### B. 频道导航 (TabRow)
* **样式**: 单行可横向滚动列表。
* **内容**: 关注、**推荐** (默认选中)、热榜、新时代、小说、视频等。
* **选中态**: 字体加粗变大，底部有红色指示器。

#### C. 新闻列表卡片 (Feed List)
列表需支持多种 `ItemViewType`（多条目布局）：

1.  **Type 1: 纯文本/置顶新闻**
    * **布局**: 标题在两行以内。
    * **元数据**: 底部显示 “置顶”、“海外网”、“30评论”。
    * *参考截图前三条数据。*

2.  **Type 2: 右侧单图模式**
    * **布局**: 左侧为标题（多行），右侧为矩形缩略图（约 3:2 比例）。
    * **元数据**: 底部显示来源、评论数、发布时间、关闭(x)按钮。
    * *参考截图：“这档社交观察类综艺火了...”*

3.  **Type 3: 视频/大图模式**
    * **布局**: 标题在顶部，中间为 16:9 大图/视频封面。
    * **交互**: 图片中心显示“播放”三角形图标，右下角显示时长（如 "02:54"）。
    * *参考截图：“我国已有近320公里高铁...”*

4.  **Type 4: 三图模式**
    * **布局**: 标题在顶部，下方横向排列三张图片。
    * *参考截图底部：“全网笑出鹅叫声...”*

#### D. 底部导航 (Bottom Navigation)
* **Tab**: 首页 (选中)、视频、搜索 (圆圈图标)、任务、我的。

---

## 3. 功能需求 (Functional Requirements)

### 3.1 基础功能

#### F1. 数据加载与模拟
* **Mock Data**: 创建一个本地数据源生成器，随机生成上述 4 种类型的新闻数据。
* **ViewModel**: 负责从 Repository 获取数据并暴露 StateFlow 给 UI。

#### F2. 卡片展示
* 使用 `LazyColumn` 实现列表的高效渲染。
* 根据数据模型中的 `type` 字段，动态渲染对应的 Composable 组件。

### 3.2 进阶功能

#### F3. 加载状态控制 (Loading State)
* **Loading**: 首次进入页面时，显示骨架屏 (Skeleton) 或圆形进度条。
* **Content**: 数据加载成功，显示列表。
* **Error**: 加载失败（如网络错误），显示重试按钮。
* **Empty**: 无数据时显示占位图。

#### F4. 下拉刷新与加载更多
* **下拉刷新 (Pull-to-refresh)**:
    * 使用 Material 3 的 `PullToRefreshBox` 或 Accompanist SwipeRefresh。
    * 触发时模拟 1.5秒 网络延迟，在列表头部插入新数据。
* **加载更多 (Infinite Scrolling)**:
    * 当列表滑动到底部（倒数第 3 项）时，自动触发加载下一页。
    * 列表底部显示 "正在加载..." 或 "没有更多了"。

#### F5. 数据库存储 (Offline Cache)
* **技术**: Room Database。
* **逻辑**: "单一信源" (Single Source of Truth) 原则。
    1.  UI 观察数据库 (Flow)。
    2.  网络请求回来后 -> 写入数据库。
    3.  数据库更新 -> 自动触发 UI 刷新。
    4.  App 杀进程重启后，优先展示数据库中的缓存内容。

---

## 4. 技术实现要求 (Technical Requirements)

### 4.1 开发环境
* **Language**: Kotlin (必须，因为使用 Compose)。
    * *注：虽然需求提及 Java，但 Jetpack Compose 仅支持 Kotlin。本项目 UI 层及 ViewModel 将使用 Kotlin 编写。*
* **Min SDK**: 24 (Android 7.0)。
* **Build System**: Gradle (Kotlin DSL)。

### 4.2 架构模式: MVI (Model-View-Intent)
采用单向数据流架构，确保状态可预测。

* **Model (State)**: 定义 UI 状态的数据类。
    ```kotlin
    data class HomeState(
        val isLoading: Boolean = false,
        val newsList: List<NewsItem> = emptyList(),
        val error: String? = null,
        val isRefreshing: Boolean = false
    )
    ```
* **View (Compose)**: 仅负责根据 State 渲染 UI，并将用户操作 (Action/Intent) 发送给 ViewModel。
* **Intent (Action)**: 用户意图的密封类。
    ```kotlin
    sealed class HomeIntent {
        object LoadInitialData : HomeIntent()
        object RefreshData : HomeIntent()
        object LoadMore : HomeIntent()
    }
    ```

### 4.3 关键技术库
* **UI**: Jetpack Compose (Material3), Coil (图片加载)。
* **DI**: Hilt (依赖注入)。
* **Async**: Coroutines (协程), Flow。
* **Storage**: Room (SQLite ORM)。
* **Architecture**: Jetpack ViewModel, Lifecycle.

---

## 5. 数据模型定义 (Data Models)

### NewsEntity (Room Table)
```kotlin
@Entity(tableName = "news_table")
data class NewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val source: String,
    val commentCount: Int,
    val publishTime: Long,
    val type: Int, // 0: Text, 1: RightImg, 2: Video, 3: ThreeImg
    val imageUrls: List<String>, // 使用 TypeConverter 存储 JSON
    val videoDuration: String? = null,
    val isTop: Boolean = false
)