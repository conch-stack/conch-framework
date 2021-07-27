## Java 类型



![JavaTypes](assets/JavaTypes.png)



##### Type

- ParameterizedType：参数化类型，例如：java.util.List<java.lang.String>、java.util.Map<java.lang.Integer, java.lang.String>

  - actualTypeArguments：泛型实际类型列表，例如：java.lang.String、java.lang.Integer & java.lang.String
  - rawType：原始类型，例如：java.util.List、java.util.Map
  - ownerType：所有者类型（内部类会有值）：null、null

- WildcardType：通配符泛型

  - ```java
    public void wildcardType(TestType<? super String> param) {
    	// do nothing
    }
    
    {
      WildcardType wildcardType = (WildcardType) actualTypeArgument;
    	Type[] lowerBounds = wildcardType.getLowerBounds();
    	Type[] upperBounds = wildcardType.getUpperBounds();
    }
    
    // 表达式上边界：[class java.lang.Object]
    // 表达式下边界：[class java.lang.String]
    ```

- GenericArrayType：泛型数组

  - 例如：List\<String>[] testGenericArrayType
    - 泛型数组类型：java.util.List<java.lang.String>[]
    - 泛型数组成员类型：java.util.List<java.lang.String>







##### TypeVariable：类型变量（反映JVM在编译该泛型前的信息）









##### AnnotatedElement

- AnnotatedType
- GenericDeclaration