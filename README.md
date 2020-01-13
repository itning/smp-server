# 依赖

## 编译器

| 编译器                 | 版本     |
| ---------------------- | -------- |
| android studio         | 3.5.3    |
| intellij idea ultimate | 2019.3.1 |
| webstorm               | 2019.3.1 |

## 编译与运行

| 依赖                    | 版本          |
| ----------------------- | ------------- |
| Java SE Development Kit | 8u231 (JDK 8) |
| maven                   | 3.6.3         |
| node.js                 | 12.14.0       |
| yarn                    | 1.21.1        |
| npm                     | 6.13.4        |
| mysql                   | 8.0.18        |

# 设置代理

## maven

[IDEA配置maven(配置阿里云中央仓库)](https://www.cnblogs.com/sword-successful/p/6408281.html)

```xml
<mirror>  
	<id>nexus-aliyun</id>  
	<mirrorOf>central</mirrorOf>    
	<name>Nexus aliyun</name>  
	<url>http://maven.aliyun.com/nexus/content/groups/public</url>  
</mirror> 
```

## npm

```shell
npm config set registry https://registry.npm.taobao.org
```

## yarn

```shell
yarn config set registry https://registry.npm.taobao.org
```

# 项目配置

## seetaface.properties

该配置用于人脸识别与JNI DLL加载

**seetaface model目录应包含以下文件:**

SeetaFaceDetector2.0.ats

SeetaFaceRecognizer2.0.ats

SeetaPointDetector2.0.pts5.ats

## smp-config->application.properties

```properties
spring.cloud.config.server.git.uri=https://gitee.com/itning/smp-server-config
spring.cloud.config.server.git.username=${env.GITEE_USERNAME}
spring.cloud.config.server.git.password=${env.GITEE_PASSWORD}
spring.cloud.config.server.git.basedir=G:\\ProjectData\\IdeaProjects\\smp-server\\config-dir
```

项目中的配置可以看[https://gitee.com/itning/smp-server-config](https://gitee.com/itning/smp-server-config)中的配置文件

# 项目打包

windows直接运行项目根目录``package.bat``脚本

# 项目编译

## 前端

配置目录：``src/api/index.js``

[高德地图开发者地址](https://lbs.amap.com/)

```js
SERVER_HOST=>后端地址
API.key=>用于高德地图KEY
```

构建步骤

```bash
yarn
yarn build
```

直接运行

```bash
yarn serve
```

## 后端

```bash
package.bat
```

## Android移动端

```
minSdkVersion 28
手机必须是android p(9 sdk29)以上
```

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