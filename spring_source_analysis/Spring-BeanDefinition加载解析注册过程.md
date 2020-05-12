## Spring-BeanDefinition加载解析注册过程

- 创建BeanFactory -> obtainFreshBeanFactory()
  - 得到DefaultListableBeanFactory
  - 执行loadBeanDefinitions方法
    - ApplicationXmlApplication中的实现
      - XmlBeanDefinitionReader （组合 DefaultListableBeanFactory用于注册）
        - Resource资源
        - loadBeanDefinitions
          - DocumentLoader -> 普通的DocumentLoader用于读取XML的Document
          - BeanDefinitionDocumentReader -> SpringBean的DoucmentReader用于去取Document中的XML元素
            - registerBeanDefinitions
              - BeanDefinitionParserDelegate -> BeanDefinition解析的委托，全部由这个类来完成
                - processBeanDefinition
                  - 先处理import、alias、bean这些root级别的文档
                  - 再每个进去处理对应的逻辑：如bean元素
                    - parseBeanDefinitionElement -> 最终返回BeanDefinitionHolder 
                      - parseBeanDefinitionElement -> 重载方法返回AbstractBeanDefinition：内部对bean的子元素进行解析
                        - 如 property 元素
                        - 先获取所有property集合
                        - 解析每个property的属性值 -> PropertyValue包装
                          - value ： TypedStringValue
                          - **ref**：**RuntimeBeanReference**
                      - 注册：BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());



- XmlBeanDefinitionReader#loadBeanDefinitions#doLoadBeanDefinitions#registerBeanDefinitions

- DefaultBeanDefinitionDocumentReader#doRegisterBeanDefinitions#parseBeanDefinitions

  ```java
  protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
      // 1.默认命名空间的处理
      if (delegate.isDefaultNamespace(root)) {
          NodeList nl = root.getChildNodes();
          // 遍历root的子节点列表
          for (int i = 0; i < nl.getLength(); i++) {
              Node node = nl.item(i);
              if (node instanceof Element) {
                  Element ele = (Element) node;
                  if (delegate.isDefaultNamespace(ele)) {
                      // 1.1 默认命名空间节点的处理，例如： <bean id="test" class="" />
                      parseDefaultElement(ele, delegate);
                  }
                  else {
                      // 1.2 自定义命名空间节点的处理，例如：<context:component-scan/>、<aop:aspectj-autoproxy/>
                      delegate.parseCustomElement(ele);
                  }
              }
          }
      } else {
          // 2.自定义命名空间的处理
          delegate.parseCustomElement(root);
      }
  }
  ```

- BeanDefinitionParserDelegate#parseBeanDefinitionElement

  ```java
  public AbstractBeanDefinition parseBeanDefinitionElement(
        Element ele, String beanName, @Nullable BeanDefinition containingBean) {
  
     this.parseState.push(new BeanEntry(beanName));
  
     // 1.解析class、parent属性
     String className = null;
     if (ele.hasAttribute(CLASS_ATTRIBUTE)) {
        className = ele.getAttribute(CLASS_ATTRIBUTE).trim();
     }
     String parent = null;
     if (ele.hasAttribute(PARENT_ATTRIBUTE)) {
        parent = ele.getAttribute(PARENT_ATTRIBUTE);
     }
  
     try {
        // 2.创建用于承载属性的AbstractBeanDefinition类型的GenericBeanDefinition
        AbstractBeanDefinition bd = createBeanDefinition(className, parent);
  
        // 3.解析bean的各种属性 scope、primary、lazy-init、autowre、init-method、destroy-method、factory-method
        parseBeanDefinitionAttributes(ele, beanName, containingBean, bd);
        // 提取description
        bd.setDescription(DomUtils.getChildElementValueByTagName(ele, DESCRIPTION_ELEMENT));
  
        // 解析元数据子节点(基本不用, 不深入介绍)
        parseMetaElements(ele, bd);
        // 解析lookup-method子节点(基本不用, 不深入介绍)
        parseLookupOverrideSubElements(ele, bd.getMethodOverrides());
        // 解析replaced-method子节点(基本不用, 不深入介绍)
        parseReplacedMethodSubElements(ele, bd.getMethodOverrides());
  
        // 4.解析constructor-arg子节点
        parseConstructorArgElements(ele, bd);
        // 5.解析property子节点
        parsePropertyElements(ele, bd);
        // 解析qualifier子节点(基本不用, 不深入介绍)
        parseQualifierElements(ele, bd);
  
        bd.setResource(this.readerContext.getResource());
        bd.setSource(extractSource(ele));
  
        return bd;
     }
     catch (ClassNotFoundException ex) {
        error("Bean class [" + className + "] not found", ele, ex);
     }
     catch (NoClassDefFoundError err) {
        error("Class that bean class [" + className + "] depends on not found", ele, err);
     }
     catch (Throwable ex) {
        error("Unexpected failure during bean definition parsing", ele, ex);
     }
     finally {
        this.parseState.pop();
     }
  
     return null;
  }
  ```