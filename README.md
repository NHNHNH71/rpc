# RPC Framework

一个基于Netty+ZooKeeper实现的高性能、可扩展的RPC框架。

## 🌟 特性

### 1. 网络通信
- 基于Netty实现NIO网络通信
- 自定义Channel池化技术，实现连接复用
- 支持心跳机制，保持长连接
- 自定义协议，解决TCP粘包问题

### 2. 注册中心
- 使用ZooKeeper作为注册中心
- 实现服务注册与发现功能
- 客户端本地缓存+Watcher机制实现动态更新
- 支持服务分组和版本管理

### 3. 序列化机制
- 支持多种序列化方式：
  - Kryo序列化
  - Hessian序列化
  - Protostuff序列化
- 可扩展的序列化接口设计

### 4. 负载均衡
- 实现多种负载均衡策略：
  - 随机负载均衡
  - 轮询负载均衡
  - 一致性哈希负载均衡
- 可扩展的负载均衡接口

### 5. 服务治理
- 服务限流：基于Guava RateLimiter实现方法级别的限流
- 服务重试：支持失败重试，可配置重试次数和重试间隔
- 服务熔断：基于滑动窗口的熔断器实现，支持熔断恢复
- 注解驱动：
  - @Limit：限流注解
  - @Retry：重试注解
  - @Breaker：熔断注解

### 6. 可扩展性设计
- 自定义SPI机制，支持功能扩展
- 插件化架构，核心组件可自由替换
- 配置文件驱动，支持动态加载

## 🛠 核心模块

### 1. rpc-core
- 框架核心实现
- 包含网络传输、序列化、负载均衡等核心功能
- 提供服务注册、发现、调用等基础API

### 2. test-api
- 接口定义模块
- 服务契约

### 3. test-server
- 服务提供者示例
- 演示服务注册和实现

### 4. test-client
- 服务消费者示例
- 演示服务发现和调用

## 🔧 快速开始

### 1. 环境准备
- JDK 1.8+
- Maven 3.0+
- ZooKeeper 3.4.x+

### 2. 定义服务接口
```java
public interface HelloService {
    String hello(String name);
}
```

### 3. 实现服务接口
```java
@RpcService(group = "default", version = "1.0")
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello, " + name;
    }
}
```

### 4. 启动服务提供者
```java
public class ServerApplication {
    public static void main(String[] args) {
        RpcServer server = new NettyRpcServer();
        HelloService helloService = new HelloServiceImpl();
        server.publishService(new RpcServiceConfig(helloService, "1.0", "default"));
        server.start();
    }
}
```

### 5. 启动服务消费者
```java
public class ClientApplication {
    public static void main(String[] args) {
        RpcClient client = new NettyRpcClient();
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        String result = helloService.hello("RPC");
        System.out.println(result);
    }
}
```

## 📚 技术栈

- 网络通信：Netty 4.x
- 注册中心：ZooKeeper
- 序列化：Kryo、Hessian、Protostuff
- 负载均衡：自定义实现
- 服务治理：Guava RateLimiter
- 项目管理：Maven
- 日志框架：Logback
- 工具库：Hutool

## 🔨 核心设计

### 1. 通信协议
```
+---------------------------------------------------------------+
| 魔数(4B) | 版本号(1B) | 消息长度(4B) | 消息类型(1B) | 序列化类型(1B) |
+---------------------------------------------------------------+
| 压缩类型(1B) | 请求ID(4B) | 消息体 |
+---------------------------------------------------------------+
```

### 2. 服务注册与发现
- 服务注册：服务端启动时注册服务到ZooKeeper
- 服务发现：客户端从ZooKeeper获取服务地址
- 本地缓存：客户端维护服务地址本地缓存
- 动态更新：通过ZooKeeper的Watcher机制实现缓存更新

### 3. 负载均衡
- 随机策略：完全随机选择服务节点
- 轮询策略：按顺序轮流选择服务节点
- 一致性哈希：相同参数的请求总是发到相同节点

### 4. 熔断器设计
- 状态转换：CLOSED -> OPEN -> HALF_OPEN
- 失败计数：统计失败次数，超过阈值触发熔断
- 自动恢复：熔断后等待一定时间进入半开状态
- 半开保护：半开状态下限制请求量，观察成功率

## 📈 性能优化

1. 网络优化
   - 使用Netty NIO提高并发性能
   - Channel复用减少连接建立开销
   - 心跳保活，避免频繁重连

2. 序列化优化
   - 支持多种高性能序列化方案
   - ThreadLocal缓存序列化对象
   - 压缩传输数据减少网络开销

3. 资源管理
   - 统一的线程池管理
   - 请求超时机制
   - 优雅停机处理

## 🔐 可靠性保证

1. 服务治理
   - 限流保护：避免服务过载
   - 熔断保护：防止服务雪崩
   - 超时重试：提高调用成功率

2. 异常处理
   - 完善的异常体系
   - 详细的错误日志
   - 优雅的失败处理

3. 监控告警
   - 服务调用统计
   - 性能指标监控
   - 异常情况告警

## 📝 待优化项

1. 增加更多序列化方式支持
2. 完善监控和统计功能
3. 支持异步调用方式
4. 引入Spring集成支持
5. 补充单元测试用例
6. 完善项目文档
