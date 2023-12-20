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

进入 conch-agent-attach/target 目录下，执行
Window：Git Shell
```shell
# 启动业务程序
java -jar D:\\conch\\conch-framework\\conch-agent\\conch-agent-test\\target\\conch-agent-test-0.0.1.jar
```
获取业务程序PID ${PID}，替换下方Shell中的变量
```shell
java -classpath D:\\Users\\jz.zheng\\.jdks\\corretto-1.8.0_392\\lib\\tools.jar -jar conch-agent-attach-launcher-0.0.1.jar 35696 D:\\conch\\conch-framework\\conch-agent\\conch-agent-jar\\target\\conch-agent-jar-0.0.1.jar
java -Xbootclasspath/a:D:\\Users\\jz.zheng\\.jdks\\corretto-1.8.0_392\\lib\\tools.jar -jar conch-agent-attach-launcher-0.0.1.jar 35696 D:\\conch\\conch-framework\\conch-agent\\conch-agent-jar\\target\\conch-agent-jar-0.0.1.jar
java -Xbootclasspath/a:D:\\Users\\jz.zheng\\.jdks\\corretto-1.8.0_392\\lib\\tools.jar -jar conch-agent-attach-launcher-0.0.1.jar 35696 D:\conch\conch-framework\conch-agent\conch-agent-jar\target\conch-agent-jar-0.0.1.jar

java -Xbootclasspath/a:D:\\Users\\jz.zheng\\.jdks\\corretto-1.8.0_392\\jre\\lib\\rt.jar;D:\\Users\\jz.zheng\\.jdks\\corretto-1.8.0_392\\jre\\lib\\jce.jar;D:\\Users\\jz.zheng\\.jdks\\corretto-1.8.0_392\\lib\\tools.jar -jar conch-agent-attach-0.0.1.jar 35696 D:\conch\conch-framework\conch-agent\conch-agent-jar\target\conch-agent-jar-0.0.1.jar

D:\Users\jz.zheng\.jdks\corretto-1.8.0_392\bin\java.exe  -Djava.library.path=D:\Users\jz.zheng\.jdks\corretto-1.8.0_392\jre\bin -Xbootclasspath/a:D:\\Users\\jz.zheng\\.jdks\\corretto-1.8.0_392\\lib\\tools.jar  -jar conch-agent-attach-0.0.1.jar 35696
```

### 扩展：
BootStrap class扩展方案
Java 命令行提供了如何扩展 bootStrap 级别 class 的简单方法。
-Xbootclasspath: 完全取代基本核心的Java class 搜索路径。不常用，否则要重新写所有Java核心class
-Xbootclasspath/a: 后缀在核心class搜索路径后面。常用!!
-Xbootclasspath/p: 前缀在核心class搜索路径前面。不常用，避免引起不必要的冲突。

多个path：分隔符与classpath参数类似，unix使用:号，windows使用;号，这里以unix为例

