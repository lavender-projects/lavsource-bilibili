# LavSource bilibili
![Java](./docs/image/Java-8-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-brightgreen?logo=Kotlin)
![Android](https://img.shields.io/badge/Android-8.0%2B-brightgreen?logo=Android)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.5-brightgreen?logo=Spring)<br />
[![License](https://img.shields.io/github/license/lavender-projects/lavsource-bilibili?label=License&color=blue&logo=GitHub)](./LICENSE)
![GitHub Stars](https://img.shields.io/github/stars/lavender-projects/lavsource-bilibili?label=Stars&logo=GitHub&style=flat)

## 简介
LavSource bilibili是一款为[Lavender](https://github.com/lavender-projects/lavender)应用提供来源于bilibili平台的媒体与文章信息数据的LavSource应用。

所有LavSource应用均有两种形式：插件式App形式、后端服务应用形式，这两种形式分别对应了Lavender应用所支持的两种信源：本地信源、网络信源。

本项目采用AGPL-3.0 License，其要求：
- 本项目的衍生项目需采用AGPL-3.0 License。
- 需在修改的文件中附有明确的说明，并包含所修改的部分及具体的修改日期。
- 通过任何形式发布衍生项目的可执行程序时，或对衍生项目进行部署，并通过网络提供服务时，**必须**同时附带或公布衍生项目的源代码。

**请严格遵守开源许可证中规定的相关要求，任何开发者在利用本项目或本项目中的任何一部分代码进行开发时，产生的任何形式和性质的影响，均由具体的开发者承担，与本项目的原作者无关。**

**本项目仅供学习、研究等非商业用途，切勿将本项目或本项目中的任何一部分代码用于商业用途。**

## 下载
- 正式版：可在[Releases](../../releases)栏目中下载各个正式版，或查看更新日志。
- 开发版：可在[Actions](../../actions)栏目中下载各个开发版，或查看构建工具的输出内容。

## 使用
### 插件式App
在[Releases](../../releases)或[Actions](../../actions)栏目中找到安装包，下载并安装，下载后不论是否启动均可。

进入手机设置的应用管理栏目中，找到LavSource bilibili，然后将其设为允许自启动。

需要将其设置为允许自启动的原因是，Lavender需要通过它将信源提供的数据，转化为符合Lavender所定义的API的数据，然后再从这些应用中获取转化后的数据。系统默认情况下不允许任何应用将未被设置为允许自启动的另一个应用唤醒，因此若不允许LavSource bilibili自动启动，Lavender将无法与之通信。

<!--suppress CheckImageSize -->
<img src="./docs/image/usage/1.jpg" width="350" alt="" />

然后返回Lavender应用的设置页，在信源设置栏目中将LavSource bilibili添加到本地信源列表中，具体步骤请参见Lavender项目的[文档](https://github.com/lavender-projects/lavender?tab=readme-ov-file#%E4%BD%BF%E7%94%A8)。

***
需要注意的是，LavSource bilibili中的账号登录是**可选**的，不登录仍然可以获取推荐视频列表、视频源和评论列表等数据。但由于bilibili平台的限制，不登录只能获得数量十分有限的视频推荐、低画质视频以及评论列表的第一页的内容。

如果您在安装和使用过程中遇到任何问题，请在[Issues](../../issues)栏目中描述您的问题。

## 鸣谢
### 开源项目
- bilibili-API-collect: [https://github.com/SocialSisterYi/bilibili-API-collect](https://github.com/SocialSisterYi/bilibili-API-collect)
- vant: [https://vant-ui.github.io/vant/#/zh-CN](https://vant-ui.github.io/vant/#/zh-CN)

### JetBrains
[IntelliJ IDEA](https://zh.wikipedia.org/zh-hans/IntelliJ_IDEA)是一个在各个方面都最大程度地提高开发人员的生产力的IDE，适用于JVM平台语言。

特别感谢[JetBrains](https://www.jetbrains.com)为开源项目提供免费的[IntelliJ IDEA](https://www.jetbrains.com/idea)等IDE的授权。

[<img src="./docs/image/jetbrains.png" width="200" />](https://www.jetbrains.com)
