**已完成事项**
- 下滑刷新：使用 `PullToRefreshBox`，刷新指示器在“关注/推荐”栏目下方，新闻卡片与指示器同步下移。
- 触底加载：每次到底部追加 20 条；新增列表底部“加载更多…”页脚，数据直接插入尾部不跳屏。
- 列表稳定性：为 `LazyColumn` 项设置稳定 `key`，减少加载变化导致的跳动。
- 数据完整展示：文本与图片解析加载，视频卡片展示封面与时长。
- 构建修复：移除多余的 `material3-pullrefresh` 单独依赖；使用 Material3 主库中的 Pull-To-Refresh API。

**运行环境**
- 操作系统：Windows
- Android 配置：`compileSdk 36`、`minSdk 24`、`targetSdk 36`、`jvmTarget 17`
- Kotlin/Compose：`kotlin 1.9.24`、`compose compiler extension 1.5.14`、Compose BOM `2024.09.00`
- 核心依赖：Material3、Coil、Room、Hilt、Lifecycle Compose、Gson

**构建配置**
- `d:\Android\Work\toutiao\app\build.gradle.kts`
  - Compose：`buildFeatures.compose = true`，`composeOptions.kotlinCompilerExtensionVersion = "1.5.14"`
  - 依赖：`platform(libs.compose.bom)`、`libs.material3`、`libs.coil`、`libs.room.*`、`libs.hilt.*`、`libs.gson`
  - 移除：单独的 `material3-pullrefresh` 依赖（API由 `material3` 主库提供）
- `d:\Android\Work\toutiao\gradle\libs.versions.toml`
  - 版本：`composeBom = "2024.09.00"`、`kotlin = "1.9.24"`、`agp = "8.7.2"` 等
- 构建命令
  - 构建：`./gradlew clean assembleDebug -x test`
  - 安装：`./gradlew installDebug`

**核心文件与职责**
- `app/src/main/java/com/example/toutiao/ui/screen/HomeScreen.kt`
  - 刷新容器：`PullToRefreshBox` 与内容位移 `graphicsLayer`，位置 `HomeScreen.kt:67-96`
  - 列表与页脚：`FeedList` 渲染卡片与“加载更多”页脚，位置 `HomeScreen.kt:126-170`
  - 触底检测：滚动中、末尾接近时触发加载，位置 `HomeScreen.kt:145-153`
- `app/src/main/java/com/example/toutiao/ui/vm/HomeViewModel.kt`
  - 状态与意图派发：`dispatch(HomeIntent)` 管理首次加载、刷新、加载更多，位置 `HomeViewModel.kt:29-35`
  - 数据映射：解析 `NewsEntity.imageUrls` JSON 为 `List<String>` 并映射到 `FeedItem`，位置 `HomeViewModel.kt:45-61`
  - 加载更多防重：`isLoadingMore` 防止重复触发，位置 `HomeViewModel.kt:76-83`
- `app/src/main/java/com/example/toutiao/ui/state/HomeState.kt`
  - 状态模型：`isLoading/isRefreshing/isLoadingMore/feedList`，位置 `HomeState.kt:5-11`
- `app/src/main/java/com/example/toutiao/ui/component/FeedCard.kt`
  - 多卡片类型：纯文本、右图、视频、三图；完整图片显示，示例 `FeedCard.kt:60-87`、`FeedCard.kt:104-132`、`FeedCard.kt:144-166`
- `app/src/main/java/com/example/toutiao/data/repository/NewsRepository.kt`
  - 数据源：初始刷新、加载更多（每次 20 条），位置 `NewsRepository.kt:16-27`
- `app/src/main/java/com/example/toutiao/data/MockDataSource.kt`
  - Mock 生成：标题、来源、图片列表与类型随机，位置 `MockDataSource.kt:18-44`
- `app/src/main/java/com/example/toutiao/data/model/NewsEntity.kt`
  - 数据结构：`imageUrls` 为 JSON 字符串，位置 `NewsEntity.kt:6-17`
- `app/src/main/java/com/example/toutiao/MainActivity.kt`
  - 入口：设置主题并加载 `HomeScreen`，位置 `MainActivity.kt:14-20`
- `app/src/main/java/com/example/toutiao/ui/component/TabBar.kt`
  - 栏目导航：稳定在刷新容器外层，避免遮挡，位置 `TabBar.kt:1-60`

**交互与数据流**
- 刷新流
  - 手势 → `PullToRefreshBox` → `HomeIntent.Refresh` → Repository `refresh()` → Flow 推送 → `feedList` 更新
- 分页流
  - 触底检测 → `HomeIntent.LoadMore` → Repository `loadMore()` → 直接在数据库尾部插入 → UI 末尾展示新数据
- 视觉反馈
  - 下拉位移：刷新指示与内容联动位移，释放后回弹
  - 加载更多：页脚文案“正在加载更多…”或“继续上拉加载更多”，无跳屏与复位
