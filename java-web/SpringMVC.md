### Spring MVC

![SpringMVC](./assets/SpringMVC.png)





##### DispatcherServlet

执行流程 **todo 画图**



##### HandlerMapping：

- 获取HttpServletRequest与Handler之间的映射

- ```java
  HandlerExecutionChain getHandler(HttpServletRequest request);
  ```



##### HandlerExecutionChain：

- Handler执行链
- 组合了 适配的 HandlerInterceptor 列表



##### HandlerInterceptor：

- HandlerMethod拦截器
- 前置判断 - preHandle
  - 当且仅当方法返回 true 时，执行 HandlerMethod
- 后置处理 - postHandle
  - HandlerMethod 已经被执行，其执行结果为ModelAndView，当 ModelAndView 参数为空，说明是非视图渲染， 即 REST 场景(@since 2.5) 否则，就是视图渲染

- 完成回调 - afterCompletion
  - 正常 preHandle -> handle -> postHandle -> afterCompletion
  - preHandle 失败 -> afterCompletion



##### HandlerAdapter：

- Handler适配器，适配不同Handler，返回ModelAndView

- ```java
  ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler);
  ```

- HandlerMethod 与 Servlet API 做适配，利用 HandlerMethod 中的执行结果，去控制适配 ServletRequest 和 ServletResponse。



##### HandlerMethod：



##### Validation



##### Converter



##### LocaleResolver：

全球化支持





##### WebMvcConfigurer

- Spring 5 之前通常使用 WebMvcConfigurerAdapter 类 Spring 5 开始直接使用 WebMvcConfigurer 接口

- Spring Framework 留给应用程序扩展 Web MVC 特性的， WebMvcConfigurer Bean 不是必须

- **WebMvcConfigurer** **引导类** **- DelegatingWebMvcConfiguration**
  - DelegatingWebMvcConfiguration (@Configuration Class)会被 @EnableWebMvc 引导
  - DelegatingWebMvcConfiguration 继承了 WebMvcConfigurationSupport，其中 WebMvcConfigurationSupport 定义了
    - HandlerAdapter Bean - RequestMappingHandlerAdapter
    - HandlerMapping Bean 
      - RequestMappingHandlerMapping
      - BeanNameUrlHandlerMapping



##### Servlet 引擎静态资源 Servlet 处理器

- org.springframework.web.servlet.r esource.DefaultServletHttpRequest Handler



##### HandlerMethodArgumentResolver

- 提供方法参数获取元信息，并且将 Servlet API 中的信息作为 方法参数填充
- **HandlerMethod** 方法上参数注解如何处理：
  - @RequestParam - ServletRequest#getParameter(String)
  - 利用 Java 反射 API Method
    - 获取参数注解 - Annotation\[][]getParameterAnnotations()，方法允许多个参数，一个参数允许标注不同的注解
    - 获取参数名称 - Parameter[] getParameters()
- 实现 - org.springframework.web.method.annotation.**RequestParamMethodArgumentResolver**



##### HttpMessageConverter



##### HandlerExceptionResolver

实现：

- org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
- org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver
- org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver



##### SpringServletContainerInitializer

Spring Web SPI注入