## Netty实战

#### 数据结构设计

<img src="assets/image-20220120195745360.png" alt="image-20220120195745360" style="zoom:50%;" />

- Frame：整个数据报文
  - length：报文消息长度，用于TCP封帧，处理底层粘包、半包问题
  - Message：消息
    - Message Header：消息头
      - version：协议版本
      - opCode：操作类型 - 对应后续Message Body的二次编解码
      - streamId：用于请求分发，IdMapping跟踪
    - Message Body（Json/Protobuf）
      - operation
      - operation result
