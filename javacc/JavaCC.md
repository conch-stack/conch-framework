## JavaCC入门

### JJ语法

##### options

主要功能是javacc在读取*.jj语法文件生成java程序时，配置生成参数

```java
//可选配置参数
options{
     STATIC = false; //关闭生成java方法是静态的，默认是true
     DEBUG_PARSER = true;//开启调试解析打印,默认是false
     JDK_VERSION = "1.8";//生产java时使用jdk版本,默认1.5
     UNICODE_INPUT=true;//接收unicode编码的输入，默认是false
}
```

