# 基于Spring Cloud的学生管理平台

[![GitHub stars](https://img.shields.io/github/stars/itning/smp-server.svg?style=social&label=Stars)](https://github.com/itning/smp-server/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/itning/smp-server.svg?style=social&label=Fork)](https://github.com/itning/smp-server/network/members)
[![GitHub watchers](https://img.shields.io/github/watchers/itning/smp-server.svg?style=social&label=Watch)](https://github.com/itning/smp-server/watchers)
[![GitHub followers](https://img.shields.io/github/followers/itning.svg?style=social&label=Follow)](https://github.com/itning?tab=followers)

[![GitHub issues](https://img.shields.io/github/issues/itning/smp-server.svg)](https://github.com/itning/smp-server/issues)
[![GitHub license](https://img.shields.io/github/license/itning/smp-server.svg)](https://github.com/itning/smp-server/blob/master/LICENSE)
[![GitHub last commit](https://img.shields.io/github/last-commit/itning/smp-server.svg)](https://github.com/itning/smp-server/commits)
[![GitHub release](https://img.shields.io/github/release/itning/smp-server.svg)](https://github.com/itning/smp-server/releases)
[![GitHub repo size in bytes](https://img.shields.io/github/repo-size/itning/smp-server.svg)](https://github.com/itning/smp-server)
[![HitCount](http://hits.dwyl.io/itning/smp-server.svg)](http://hits.dwyl.io/itning/smp-server)
[![language](https://img.shields.io/badge/language-JAVA-green.svg)](https://github.com/itning/smp-server)

## 图

### 功能结构图

![功能结构图](https://raw.githubusercontent.com/itning/smp-server/master/pic/image/functional-structure-diagram.png)

### 系统运行原理图

![系统运行原理图](https://raw.githubusercontent.com/itning/smp-server/master/pic/image/schematic-diagram-of-system-operation.png)

## 工程

1. 前端项目
   - [Vue.JS 实现](https://github.com/itning/smp-client)
   - [Angular 实现](https://github.com/itning/smp-client-angular)
2. Android移动端项目
   - [教师端](https://github.com/itning/smp-android-teacher)
   - [学生端](https://github.com/itning/smp-android)
3. 后端
   - [Spring Cloud](https://github.com/itning/smp-server)
4. 人脸识别模型库
   - [smp-ext-lib](https://gitee.com/itning/smp-ext-lib)
5. 统一配置中心数据存放仓库
   - [smp-server-config](https://gitee.com/itning/smp-server-config)

## 依赖

### 编译器

| 编译器            | 版本      |
| ----------------- | --------- |
| android studio    | 3.5.3+    |
| intellij idea     | 2019.3.1+ |
| intellij webstorm | 2019.3.1+ |

### 编译与运行

| 依赖                    | 版本                |
| ----------------------- | ------------------- |
| Java SE Development Kit | 8u231 (JDK 8<JDK11) |
| maven                   | 3.6.3+              |
| node.js                 | 12.14.0+            |
| yarn                    | 1.21.1+             |
| npm                     | 6.13.4+             |
| mysql                   | 8.0.18+             |

## 设置代理

### maven

[IDEA配置maven(配置阿里云中央仓库)](https://www.cnblogs.com/sword-successful/p/6408281.html)

```xml
<mirror>  
	<id>nexus-aliyun</id>  
	<mirrorOf>central</mirrorOf>    
	<name>Nexus aliyun</name>  
	<url>http://maven.aliyun.com/nexus/content/groups/public</url>  
</mirror> 
```

### npm

```shell
npm config set registry https://registry.npm.taobao.org
```

### yarn

```shell
yarn config set registry https://registry.npm.taobao.org
```

## 项目配置

### 人脸识别模型配置（seetaface.properties）

模型下载地址：[码云仓库](https://gitee.com/itning/smp-ext-lib)

目前只有两个微服务需要人脸识别模型

[smp-class 微服务模型配置文件](https://github.com/itning/smp-server/blob/master/smp-class/src/main/resources/seetaface.properties)

[smp-room 微服务模型配置文件](https://github.com/itning/smp-server/blob/master/smp-room/src/main/resources/seetaface.properties)

该配置用于人脸识别与JNI DLL加载

| KEY         | VALUE                                                        |
| ----------- | ------------------------------------------------------------ |
| libs.path   | 包含`lib-linux-x64.tar.bz2`或`lib-win-x64.zip`解压后的文件夹路径 |
| bindata.dir | 包含`SeetaFaceDetector2.0.ats`和`SeetaFaceRecognizer2.0.ats`和`SeetaPointDetector2.0.pts5.ats`三个文件的文件夹路径 |

### 微服务统一配置中心（smp-config->application.properties）

```properties
spring.cloud.config.server.git.uri=https://gitee.com/itning/smp-server-config
spring.cloud.config.server.git.username=${env.GITEE_USERNAME}
spring.cloud.config.server.git.password=${env.GITEE_PASSWORD}
spring.cloud.config.server.git.basedir=G:\\ProjectData\\IdeaProjects\\smp-server\\config-dir
```

项目中的配置可以看[https://gitee.com/itning/smp-server-config](https://gitee.com/itning/smp-server-config)中的配置文件

### SQL文件

项目使用JPA自动建表，无需SQL导入。

**注意MySQL版本为8.0以上**

### Excel模板文件

[学生信息导入模板](https://github.com/itning/smp-server/blob/master/pic/%E5%AD%A6%E7%94%9F%E4%BF%A1%E6%81%AF%E5%AF%BC%E5%85%A5%E6%A8%A1%E6%9D%BF.xlsx)

[班级信息导入模板](https://github.com/itning/smp-server/blob/master/pic/%E7%8F%AD%E7%BA%A7%E4%BF%A1%E6%81%AF%E5%AF%BC%E5%85%A5%E6%A8%A1%E6%9D%BF.xlsx)

## 项目打包

windows直接运行项目根目录[package.bat](https://github.com/itning/smp-server/blob/master/package.bat)脚本

## 项目编译

### 前端

配置目录：[src/api/index.js](https://github.com/itning/smp-client/blob/master/src/api/index.js#L1)

[高德地图开发者地址](https://lbs.amap.com/)

```js
SERVER_HOST=>后端地址
API.key=>用于高德地图KEY
```

安装依赖

```bash
yarn install
```

打包发布

```bash
yarn build
```

调试运行

```bash
yarn serve
```

### 后端

项目打包：

```bash
package.bat
```

### Android移动端

**minSdkVersion 28**
**手机必须是android p (9 sdk28) 以上**

```bash
# 查看构建版本
./gradlew -v
# 清除build文件夹
./gradlew clean
# 检查依赖并编译打包
./gradlew build
# 编译并安装debug包
./gradlew installDebug
# 编译并打印日志
./gradlew build --info
# 译并输出性能报告，性能报告一般在 构建工程根目录 build/reports/profile
./gradlew build --profile
# 调试模式构建并打印堆栈日志
./gradlew build --info --debug --stacktrace
# 强制更新最新依赖，清除构建并构建
./gradlew clean build --refresh-dependencies
```