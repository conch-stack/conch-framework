##### Tips：

日志+链路追踪

缓存+二级缓存

aspectj源码分析

Linux流量监控工具iftop

Freecodecamp

调优之 Btrace

[JVM Attach机制实现](https://www.jianshu.com/p/4fa6a66fc8d9)



支持业务接口重试 + 支持事件重试

jdbc savepoint设计



数据同步：

- porter
  - https://github.com/sxfad/porter

- datalink
  - https://github.com/ucarGroup/DataLink



秒杀设计

- https://github.com/colg-cloud/seckill



抽象：可访问，可执行，可配置，等等



特殊工具：https://github.com/vavr-io/vavr

JBoss测试工具：https://github.com/arquillian/arquillian-core

面向云的链路追踪：https://github.com/jaegertracing/jaeger

metrics：https://github.com/micrometer-metrics/micrometer

checker：https://github.com/typetools/checker-framework/

antora：https://gitlab.com/antora/antora

vavr：https://github.com/vavr-io/vavr

MQTT:工业消息协议

Sharding-Sphere：https://mp.weixin.qq.com/s/Dw9e33Xk9DO7PbYPIfo_kg



DCache火车票接入

BUG：

- BUG1：DCache切DAL的SQL的粒度太粗，导致未配置的库表也被组件解析；

- BUG2：异常SQL用法识别后，上报对应SQL到Hickwall，由于异常SQL使用了唯一ID等信息拼接，导致触发Hickwall限流；

- BUG3：不支持SQL原因 Message Metric 上报丢失；



新增功能：

- DCache.skip()  功能支持：业务使用方可在特定业务场景中通过该功能跳过缓存；

- Update IN 缓存删除支持
  - UPDATE `tbl_zhixing_grab_order` SET `partner`=? WHERE `order_number` in (?,?)
- 新增全局维度与库表维度的 Exclude SQL 与 Exclude AppID 支持，进一步加强业务接入时的可控能力

修复功能：

- BUG1：
  - 新增预解析功能，识别当前调用无对应配置承接的场景下，跳过DCache

- BUG2：
  - 新增CK Metric上报支持：避免业务量大的场景触发Hickwall限流
  - 缩小SQL上报功能的粒度：取消全局SQL上报能力，新增表维度的SQL上报，业务方可梯度使用上报功能对业务SQL进行观测
- BUG3：已修复



发现问题：同一个库的同一个表，存在多个Entity Class定义问题：

- 不同应用中Entitiy Class包名不一致
- **相同应用中的同一个库的同一个表，Entity Class包名不一致**

解决：新增建议约定（http://conf.ctripcorp.com/pages/viewpage.action?pageId=3044242587#a.%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3-5.%E6%B3%A8%E6%84%8F%E7%82%B9）



新增**最佳实践路径**文档（编写中 http://conf.ctripcorp.com/pages/viewpage.action?pageId=3044242587#a.%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3-4.%E6%9C%80%E4%BD%B3%E5%AE%9E%E8%B7%B5%E8%B7%AF%E5%BE%84）

