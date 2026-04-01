# Spring Boot Hello World Demo

这是一个使用 Maven 和 Spring Boot 的入门示例，输出 "Hello World"。

## 项目结构

```
.
├── pom.xml
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── example
│       │           └── demo
│       │               ├── SpringBootHelloWorldApplication.java
│       │               └── HelloWorldController.java
│       └── resources
│           └── application.yml
└── README.md
```

## 主要文件说明

1. **pom.xml**: Maven 配置文件，定义了 Spring Boot 依赖和项目配置
2. **SpringBootHelloWorldApplication.java**: Spring Boot 主应用程序类
3. **HelloWorldController.java**: REST 控制器，处理 `/hello` 请求
4. **application.yml**: YAML格式配置文件，设置服务器端口为 8080

## 运行方法

### 1. 编译项目
```bash
mvn clean compile
```

### 2. 运行应用程序
```bash
mvn spring-boot:run
```

### 3. 测试接口
应用启动后，在浏览器或使用 curl 访问：
```
http://localhost:8080/hello
```

预期输出：
```
Hello World!
```

## 技术栈

- Java 17
- Spring Boot 4.0.5
- Maven 3.6.3
- Spring Web (用于创建 REST API)

## 创建过程

1. 创建 Maven 项目结构
2. 配置 pom.xml 添加 Spring Boot 依赖
3. 编写 Spring Boot 主类
4. 创建 REST 控制器
5. 配置应用属性
6. 使用 Maven 编译和运行