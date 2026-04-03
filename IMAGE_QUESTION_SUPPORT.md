# 图片题支持说明

当前题库已融合第三方题库中的图片题数据。

## 数据来源
- https://raw.githubusercontent.com/doupoa/DrivingTestSubjectOne/refs/heads/main/q.json

## 当前状态
- 融合后题库总量：4458 题
- 含图片题：326 题
- 图片字段：`imageUrl`

## 展示方式
- 在 `PracticeActivity` 中增加了 `ImageView`
- 使用 Glide 加载第三方网站图片

## 注意事项
- 第三方图片链接如果失效，需要后续做本地缓存或镜像
- 建议后续增加图片预加载与离线缓存
