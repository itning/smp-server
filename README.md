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

项目中的配置可以看``https://gitee.com/itning/smp-server-config``中的配置文件  

# 项目打包

windows直接运行项目根目录``package.bat``脚本                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      