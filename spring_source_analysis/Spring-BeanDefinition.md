## Spring-BeanDefinition加载解析注册过程

### XML方式：

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



- parseConstructorArgElements#parseConstructorArgElement#parsePropertyValue：解析constructor-arg

  - RuntimeBeanReference：运行时引用

  ```java
  if (hasRefAttribute) {
  // 5.ref属性的处理，使用RuntimeBeanReference封装对应的ref值（该ref值指向另一个bean的beanName），
  // RuntimeBeanReference起到占位符的作用，ref指向的beanName将在运行时被解析成真正的bean实例引用
    String refName = ele.getAttribute(REF_ATTRIBUTE);
    if (!StringUtils.hasText(refName)) {
      error(elementName + " contains empty 'ref' attribute", ele);
    }
    RuntimeBeanReference ref = new RuntimeBeanReference(refName);
    ref.setSource(extractSource(ele));
    return ref;
  } else if (hasValueAttribute) {
    // 6.value属性的处理，使用TypedStringValue封装
    TypedStringValue valueHolder = new TypedStringValue(ele.getAttribute(VALUE_ATTRIBUTE));
    valueHolder.setSource(extractSource(ele));
    return valueHolder;
  } else if (subElement != null) {
    // 7.解析子节点
    return parsePropertySubElement(subElement, bd);
  } else {
    // 8.既没有ref属性，也没有value属性，也没有子节点，没法获取ele节点的值，直接抛异常
    // Neither child element nor "ref" or "value" attribute found.
    error(elementName + " must specify a ref or value", ele);
    return null;
  }
  ```

  - 将构造器值写入BeanDefinition：

  ```java
  // 解析 constructor-arg 的 value 值
  Object value = parsePropertyValue(ele, bd, null);
  ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
  if (StringUtils.hasLength(typeAttr)) {
    valueHolder.setType(typeAttr);
  }
  if (StringUtils.hasLength(nameAttr)) {
    valueHolder.setName(nameAttr);
  }
  valueHolder.setSource(extractSource(ele));
  // 放入BeanDefinition
  bd.getConstructorArgumentValues().addGenericArgumentValue(valueHolder);
  ```

  
  - parseValueElement：解析XML的 value 值

  ```java
  public Object parseValueElement(Element ele, String defaultTypeName) {
      // It's a literal value.
      // 拿到ele节点值
      String value = DomUtils.getTextValue(ele);
      // 拿到ele节点的type属性
      String specifiedTypeName = ele.getAttribute(TYPE_ATTRIBUTE);
      String typeName = specifiedTypeName;
      if (!StringUtils.hasText(typeName)) {
          // ele节点没有type属性则则使用入参defaultTypeName
          typeName = defaultTypeName;
      }
      try {
          // 1.使用value和type构建TypedStringValue
          TypedStringValue typedValue = buildTypedStringValue(value, typeName);
          typedValue.setSource(extractSource(ele));
          typedValue.setSpecifiedTypeName(specifiedTypeName);
          return typedValue;
      }
      catch (ClassNotFoundException ex) {
          error("Type class [" + typeName + "] not found for <value> element", ele, ex);
          return value;
      }
  }
  ```

  

  - buildTypedStringValue：构建TypeStringValue

  ```java
  protected TypedStringValue buildTypedStringValue(String value, String targetTypeName)
          throws ClassNotFoundException {
   
      ClassLoader classLoader = this.readerContext.getBeanClassLoader();
      TypedStringValue typedValue;
      // 1.targetTypeName为空，则只使用value来构建TypedStringValue
      if (!StringUtils.hasText(targetTypeName)) {
          typedValue = new TypedStringValue(value);
      }
      // 2.targetTypeName不为空，并且classLoader不为null
      else if (classLoader != null) {
          // 2.1 利用反射，构建出type的Class，如果type是基本类型，或者 java.lang 包下的常用类，
          // 可以直接从缓存（primitiveTypeNameMap、commonClassCache）中获取
          Class<?> targetType = ClassUtils.forName(targetTypeName, classLoader);
          typedValue = new TypedStringValue(value, targetType);
      } else {
          typedValue = new TypedStringValue(value, targetTypeName);
      }
      return typedValue;
  }    
  ```

- parsePropertyElements#parsePropertyElement#parsePropertyValue：解析property

  - parsePropertyValue是复用的，获取Object

  ```java
  // 解析 property 的 value 值
  Object val = parsePropertyValue(ele, bd, propertyName);
  PropertyValue pv = new PropertyValue(propertyName, val);
  parseMetaElements(ele, pv);
  pv.setSource(extractSource(ele));
  // 放入BeanDefinition
  bd.getPropertyValues().addPropertyValue(pv);
  ```



- 解析完后：注册BeanDefinition

  DefaultBeanDefinitionDocumentReader#processBeanDefinition

```java
// Register the final decorated instance.
BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());

// 具体实现
public static void registerBeanDefinition(
			BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {

		// Register bean definition under primary name.
  	// 1.拿到beanName
		String beanName = definitionHolder.getBeanName();
  
    // 2.注册beanName、BeanDefinition到缓存中（核心逻辑）,实现类为; DefaultListableBeanFactory
		registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

		// Register aliases for bean name, if any.
  	// 注册bean名称的别名（如果有的话）
		String[] aliases = definitionHolder.getAliases();
		if (aliases != null) {
			for (String alias : aliases) {
        // 3.注册bean的beanName和对应的别名映射到缓存中（缓存：aliasMap）
				registry.registerAlias(beanName, alias);
			}
		}
	}
```



- DefaultListableBeanFactory#registerBeanDefinition(String beanName, BeanDefinition beanDefinition)

```java
// 验证
((AbstractBeanDefinition) beanDefinition).validate();

// 检测缓存
BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
		if (existingDefinition != null) {
			if (!isAllowBeanDefinitionOverriding()) {
        // 不支持重写报错
				throw new BeanDefinitionOverrideException(beanName, beanDefinition, existingDefinition);
			}
			// 注册 - 重写
			this.beanDefinitionMap.put(beanName, beanDefinition);
		}
		else {
      // Bean创建是否已经开始了
			if (hasBeanCreationStarted()) {
				// Cannot modify startup-time collection elements anymore (for stable iteration)
				synchronized (this.beanDefinitionMap) {
          // 注册
					this.beanDefinitionMap.put(beanName, beanDefinition);
          // 更新 beanDefinitionNames 加上新的beanName
					List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
					updatedDefinitions.addAll(this.beanDefinitionNames);
					updatedDefinitions.add(beanName);
					this.beanDefinitionNames = updatedDefinitions;
					// 将beanName从ManualSingletonName中移除
					removeManualSingletonName(beanName);
				}
			}
			else {
				// Still in startup registration phase
        // bean创建阶段还未开始
				this.beanDefinitionMap.put(beanName, beanDefinition);
				this.beanDefinitionNames.add(beanName);
				removeManualSingletonName(beanName);
			}
			this.frozenBeanDefinitionNames = null;
		}

		// 重置
		// 如果存在相同beanName的BeanDefinition，并且beanName已经存在单例对象，则将该beanName对应的缓存信息、单例对象清除，
    // 因为这些对象都是通过existingDefinition创建出来的，需要被覆盖掉的，
    // 我们需要用新的BeanDefinition（也就是本次传进来的beanDefinition）来创建这些缓存和单例对象
		if (existingDefinition != null || containsSingleton(beanName)) {
			resetBeanDefinition(beanName);
		}


```

- 清除旧的BeanDefinition

```java
protected void resetBeanDefinition(String beanName) {
		// Remove the merged bean definition for the given bean, if already created.
		clearMergedBeanDefinition(beanName);

		// Remove corresponding bean from singleton cache, if any. Shouldn't usually
		// be necessary, rather just meant for overriding a context's default beans
		// (e.g. the default StaticMessageSource in a StaticApplicationContext).
		destroySingleton(beanName);

		// Notify all post-processors that the specified bean definition has been reset.
		for (BeanPostProcessor processor : getBeanPostProcessors()) {
			if (processor instanceof MergedBeanDefinitionPostProcessor) {
				((MergedBeanDefinitionPostProcessor) processor).resetBeanDefinition(beanName);
			}
		}

  	// 递归清除重置所有引用beanName的子BeanDefinition
		// Reset all bean definitions that have the given bean as parent (recursively).
		for (String bdName : this.beanDefinitionNames) {
			if (!beanName.equals(bdName)) {
				BeanDefinition bd = this.beanDefinitionMap.get(bdName);
				// Ensure bd is non-null due to potential concurrent modification
				// of the beanDefinitionMap.
				if (bd != null && beanName.equals(bd.getParentName())) {
					resetBeanDefinition(bdName);
				}
			}
		}
	}
```

