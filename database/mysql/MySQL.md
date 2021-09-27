## MySQL

### 基础架构

![MySQL架构](assets/MySQL架构.png)



### Redo Log & Binlog

##### Redo Log：重做日志

- 是**WAL（Write-Ahead Loggin）方案**的载体：先写日志（这里的Redo Log），再写存储引擎；分层操作，可提升性能，防止操作丢失（**crash-safe**）

- **工作在存储引擎层**：因为MySQL期初是没有InnoDB的，而Server的Binlog是没有crash-safe功能的，所有InnoDB实现的WAL

- 配置为一组4个文件，每个文件1G，从头开始写，写到末尾就又回到开 头循环写。
  - 两个指针操作日志偏移量（write pos）与持久化偏移量（checkpoint）：

<img src="assets/image-20210928010032431.png" alt="image-20210928010032431" style="zoom:70%;" />



##### BinLog：归档日志

- 工作在Server层



##### 对比：

| Redo Log                                     | Binlog                                                       |
| -------------------------------------------- | ------------------------------------------------------------ |
| InnoDB引擎特有                               | Server层通用                                                 |
| 物理日志（记录了：某个数据页上做了什么修改） | 逻辑日志（记录语句的原始逻辑，比如：给ID=2这一行的c字段加1） |
| 循环写，固定大小                             | 追加写，写到一定大小后，会创建下一个文件                     |



**问题：为什么有两套日志？**

答：期初MySQL无InnoDB引擎，只能利用Binlog进行日志归档，后期InnoDB引擎为支持crash-safe，实现了WAL方案

**问题：先写redolog还是先写binlog**

答：先写binlog



### Update/Insert执行流程

