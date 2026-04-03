# 驾考宝典 Android 应用程序

## 项目概述

基于 `android-native-dev` skill 设计的驾考宝典应用程序，包含以下功能：

- ✅ 顺序练习
- ✅ 随机练习
- ✅ 错题练习
- ✅ 收藏功能
- ✅ 模拟考试
- ✅ 分类练习
- ✅ 题库更新

## 项目结构

```
DrivingTestApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/drivingtest/app/
│   │   │   ├── data/
│   │   │   │   ├── AppDatabase.java       # Room 数据库
│   │   │   │   ├── QuestionDao.java       # 数据访问对象
│   │   │   │   └── QuestionDataManager.java # 数据管理
│   │   │   ├── model/
│   │   │   │   └── Question.java          # 题目实体类
│   │   │   ├── ui/
│   │   │   │   ├── MainActivity.java      # 主界面
│   │   │   │   ├── PracticeActivity.java  # 练习界面
│   │   │   │   ├── ExamActivity.java      # 考试界面
│   │   │   │   └── ResultActivity.java    # 结果界面
│   │   │   └── util/
│   │   │       └── JsonUtil.java          # JSON 工具
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   └── drawable/
│   │   └── assets/
│   │       └── questions.json             # 题目数据
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## 核心功能

### 1. 数据库设计

使用 Room 数据库存储：
- 题目内容（问题、选项、答案、解析）
- 错题标记和错误次数
- 收藏标记
- 练习次数

### 2. 题目类型支持

- 单选题
- 多选题
- 判断题

### 3. 练习模式

| 模式 | 说明 |
|------|------|
| 顺序练习 | 按题库顺序逐一练习 |
| 随机练习 | 随机抽取题目 |
| 错题练习 | 针对错题反复练习 |
| 收藏练习 | 练习收藏的题目 |
| 模拟考试 | 100题模拟真实考试 |
| 分类练习 | 按类别（法规/信号/安全等）练习 |

### 4. 题库更新

支持从服务器下载最新题库，保留用户的错题和收藏记录。

## 技术栈

- **语言**: Kotlin (推荐) 或 Java
- **UI**: Jetpack Compose 或 XML Layout
- **数据库**: Room
- **网络**: Retrofit + OkHttp
- **架构**: MVVM
- **依赖注入**: Hilt (可选)

## 构建说明

### 1. 创建新项目

在 Android Studio 中创建新项目：
- Name: DrivingTestApp
- Package: com.drivingtest.app
- Language: Kotlin
- Minimum SDK: API 24 (Android 7.0)

### 2. 添加依赖

在 `app/build.gradle` 中添加：

```gradle
dependencies {
    // Room
    implementation "androidx.room:room-runtime:2.6.1"
    kapt "androidx.room:room-compiler:2.6.1"
    
    // Material Design
    implementation "com.google.android.material:material:1.11.0"
    
    // ViewModel & LiveData
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0"
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.7.0"
    
    // Coroutines
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
    
    // Retrofit
    implementation "com.squareup.retrofit2:retrofit:2.9.0"
    implementation "com.squareup.retrofit2:converter-gson:2.9.0"
}
```

### 3. 复制文件

将本项目中的文件复制到对应目录。

### 4. 构建运行

点击 Android Studio 的 Run 按钮运行应用。

## 题目数据格式

```json
{
  "question": "题目内容",
  "optionA": "选项A",
  "optionB": "选项B",
  "optionC": "选项C",
  "optionD": "选项D",
  "correctAnswer": "A",
  "explanation": "答案解析",
  "category": "交通法规",
  "type": "单选题",
  "difficulty": 1
}
```

## 扩展功能建议

1. **用户系统**: 登录、注册、学习进度同步
2. **学习统计**: 正确率、学习时长、进度图表
3. **视频讲解**: 难题视频解析
4. **社区功能**: 讨论区、经验分享
5. **离线模式**: 无网络也能练习
6. **夜间模式**: 护眼深色主题

## 许可证

MIT License
