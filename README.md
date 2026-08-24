# 生活助手 (LifeApp)

一个仅供个人使用的安卓原生生活管理 App，数据完全本地存储，不联网、不登录、不需要云服务。

## 功能模块

1. **首页总览** - 今日待办、快速备忘、各模块摘要
2. **今日计划** - 按日期管理待办，支持优先级和提醒
3. **开发工作** - 项目管理、任务/BUG、代码片段（支持文件导入）、技术笔记
4. **自媒体制作** - 灵感库、脚本草稿（自动保存）、发布计划、素材清单
5. **娱乐板块** - 想看清单、进行中/已看完、随机推荐"今天看什么"
6. **数据与设置** - 数据路径查看、备份导出/恢复、清空数据、背景设置、主题切换

## 技术栈

- Kotlin
- Room (SQLite) 本地数据库
- Material Design Components
- ViewBinding
- 最低支持 Android 8.0 (API 26)

## 构建

```bash
./gradlew assembleDebug
```

APK 输出路径：`app/build/outputs/apk/debug/app-debug.apk`

## 测试

```bash
./gradlew testDebugUnitTest
```

## 数据存储

所有数据存储在 App 内部数据库：`/data/data/com.lifeapp/databases/life_app.db`

支持备份导出和恢复，数据在关闭/重启后不丢失。
