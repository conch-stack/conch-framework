## Netty

#### Netty架构

todo

### 水平触发 VS 边缘触发

在非阻塞IO中，通过Selector选出准备好的fd进行操作。有两种模式，一是水平触发（LT），二是边缘触发（ET）

在LT模式下，只要某个fd还有数据没读完，那么下次轮询还会被选出。而在ET模式下，只有fd状态发生改变后，该fd才会被再次选出。ET模式的特殊性，使在ET模式下的一次轮询必须处理完本次轮询出的fd的所有数据，否则该fd将不会在下次轮询中被选出。

### 封帧

<img src="assets/image-20210925221358755.png" alt="image-20210925221358755" style="zoom:80%;" />

### Keepalive + Idle

Netty开启TCP Keepalive 和 Idle 检测

- 开启Keepalive
  
  - Server端开启Keepalive 
    
    ```java
    // 方式一
    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
    // 方式二
    bootstrap.childOption(NioChannelOption.of(StandardSocketOptions.SO_KEEPALIVE), true);
    ```

- 开启Idle

```java
ch.pipline().addList("idleCheckHandler", new IdleStateHandler(0, 20, 0, TimeUnit.SECONDS));
// 0 - readerIdleTime
// 20 - writerIdleTime
// 0 - allIdleTime
// 0表示用 
```

目标：

- 服务器加上 read idle check - 服务器10s接收不到channel的请求，就断开连接
  - 保护自己，及时清理空闲连接
- 客户端加上 write idle check + keepalive - 客户端5s不发送数据，就发送一个keepalive
  - 避免连接被断开
  - 启用不频繁keepalive
