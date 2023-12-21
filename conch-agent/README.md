# Conch-Agent

### Jar Static Load
进入 conch-agent-test/target 目录下，执行
Window: Git Shell
```shell
java -javaagent:D:\\conch\\conch-framework\\conch-agent\\conch-agent-jar\\target\\conch-agent-jar-0.0.1.jar -jar conch-agent-test-0.0.1.jar
```
Window: cmd
```shell
java -javaagent:D:\conch\conch-framework\conch-agent\conch-agent-jar\target\conch-agent-jar-0.0.1.jar -jar conch-agent-test-0.0.1.jar
```

### Attach Dynamic Load
> agentmain 在 main 函数开始运行后才启动（依赖于Attach机制）

编译打包
启动目标程序 conch-agent-test（为基于spring的web程序）
使用conch-agent-attach-launcher 利用 Idea启动AttachLauncher，添加Run参数启动，传入目标程序的pid，即可（备注：conch-agent-attach-agent agent被硬编码在AttachLauncher的main方法中了）

### 扩展：
BootStrap class扩展方案
Java 命令行提供了如何扩展 bootStrap 级别 class 的简单方法。
-Xbootclasspath: 完全取代基本核心的Java class 搜索路径。不常用，否则要重新写所有Java核心class
-Xbootclasspath/a: 后缀在核心class搜索路径后面。常用!!
-Xbootclasspath/p: 前缀在核心class搜索路径前面。不常用，避免引起不必要的冲突。

多个path：分隔符与classpath参数类似，unix使用:号，windows使用;号，这里以unix为例

热加载：
- https://tech.meituan.com/2022/03/17/java-hotswap-sonic.html
- https://github.com/dcevm/dcevm
- https://github.com/HotswapProjects/HotswapAgent/tree/d57efeaf40109edf1877fbaef1d1173e7e1bd0e7
- http://hotswapagent.org/
- https://maven.apache.org/plugins/maven-toolchains-plugin/

架构：
- https://github.com/alibaba/tengine
- https://github.com/zfoo-project/zfoo


