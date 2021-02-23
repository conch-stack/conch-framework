## Akka核心思想



- ActorSystem：全局
- ActorRef：定位Actor
- 邮箱：存放消息（事件）
- 分发器：分发消息到Actor
- Actor



Actor模型：



![image-20210223172931292](assets/image-20210223172931292.png)



Actor生命周期：

- start
  - preStart：申请资源
- restart
  - preRestart：停止该Actor的所有子Actor，并调用postStop钩子，释放资源
  - postRestart
- stop
  - postStop：释放资源
  - Actor停止后，ActorRef重定向到系统中的deadLettersActorRef，它是一个特殊的ActorRef，接收发送到已死亡的Actor的消息



特性：

- 任何Actor都可以作为监督器，但只能管理自己创建的Actor（监控状态、重启、停止）
- 大约270万哥Actor可以存放在1G内存中，这与4096个线程占用1G内存有很大区别，意味着可以比直接使用线程更加自由的创建各种类型的Actor
- 单向消息：即发即丢
- 双向消息
- 出错恢复策略