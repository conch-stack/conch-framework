package ltd.beihu.core.mybatis;

import ltd.beihu.core.mybatis.reflection.ReflectionException;

import java.lang.reflect.*;
import java.util.*;

/**
 * TODO add to reactor-demo
 *
 * @author Adam
 * @since 2020/3/8
 */
public class TestJvmType {

    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw new ReflectionException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
        }

        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }

    public List list = new ArrayList();
    public List<String> list1 = new ArrayList<>();
    public List<TestType<String>> list2 = new ArrayList<>();

    public Map map = new HashMap();
    public Map<Integer, String> map1 = new HashMap<>();
    public Map<Integer, TestType<String>> map2 = new HashMap<>();
    public HashMap<TestType<String>, TestType<String>> map3 = new HashMap<>();
    public Map.Entry<String, String> map4;

    private TestType<String> testType;

    private List<String>[] testGenericArrayType;


    /**
     * 参数化类型
     */
    public static void testParameterizedType() {
        Field[] fields = TestJvmType.class.getDeclaredFields();
        System.out.println("===========================================================================================================================================");
        for (Field field : fields) {
            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) field.getGenericType();
                System.out.println("变量：" + pType.getTypeName() + "     ");
                /**
                 * 获取原始类型
                 */
                Type rawType = pType.getRawType();
                System.out.println("ownerType: " + rawType.getTypeName());

                /**
                 * 获取参数化类型的类型变量（实际类型列表）
                 */
                System.out.println("-------------------- getActualTypeArguments: ");
                Type[] types = pType.getActualTypeArguments();
                for (Type t : types) {
                    System.out.println("\t类型：" + t.getTypeName());
                }
                Type ownerType = pType.getOwnerType();
                System.out.println("类型所属类型：" + ((null != ownerType) ? ownerType.getTypeName() : "null"));

                System.out.println("----------------------------------------------------------------");
            }
        }
        System.out.println("===========================================================================================================================================");
    }

    /**
     * 类型变量：反映JVM在编译该泛型前的信息
     */
    public static void testTypeVariable() {
        Field[] fields = TestJvmType.class.getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            System.out.println(field.getName());
            TypeVariable<? extends Class<?>>[] typeParameters = type.getTypeParameters();
            for (TypeVariable<? extends Class<?>> typeParameter : typeParameters) {

                AnnotatedType[] annotatedBounds = typeParameter.getAnnotatedBounds();
                for (AnnotatedType annotatedBound : annotatedBounds) {
                    System.out.println("annotatedBound: " + annotatedBound.getType().getTypeName());
                }

                /**
                 * 类型变量的上边界，未明确，默认为Object
                 *      class Test<K extends Person>中 K 的上界就是 Person
                 */
                Type[] bounds = typeParameter.getBounds();
                for (Type bound : bounds) {
                    System.out.println("bound: " + bound.getTypeName());
                }

                /**
                 * 获取声明该变量的原始类型
                 */
                Class<?> genericDeclaration = typeParameter.getGenericDeclaration();
                System.out.println("getGenericDeclaration:" + genericDeclaration.getTypeName());

                String name = typeParameter.getName();
                System.out.println("name: " + name);
                String typeName = typeParameter.getTypeName();
                System.out.println("typeName: " + typeName);
                System.out.println("----------------------------------------------------------------");
            }
        }
    }

    /**
     * 泛型数组
     */
    public static void testGenericArrayType() throws NoSuchFieldException {
        Field testGenericArrayType = TestJvmType.class.getDeclaredField("testGenericArrayType");
        Type genericType = testGenericArrayType.getGenericType();
        if (genericType instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) genericType;
            Type genericComponentType = genericArrayType.getGenericComponentType();
            System.out.println("泛型数组：" + genericComponentType.getTypeName());
        }
    }

    /**
     * 通配符泛型
     */
    public static void testWildcardType() {
        Method[] declaredMethods = TestJvmType.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().equals("wildcardType")) {
                Parameter[] parameters = declaredMethod.getParameters();
                for (Parameter parameter : parameters) {
                    System.out.println(parameter.getType().getSimpleName());
                    System.out.println(parameter.getName());
                    System.out.println(parameter.getParameterizedType().getTypeName());

                    ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    for (Type actualTypeArgument : actualTypeArguments) {
                        if (actualTypeArgument instanceof WildcardType) {
                            WildcardType wildcardType = (WildcardType) actualTypeArgument;
                            Type[] lowerBounds = wildcardType.getLowerBounds();
                            Type[] upperBounds = wildcardType.getUpperBounds();
                            if (upperBounds.length != 0) {
                                System.out.println("表达式上边界：" + Arrays.asList(upperBounds));
                            }
                            if (lowerBounds.length != 0) {
                                System.out.println("表达式下边界：" + Arrays.asList(lowerBounds));
                            }
                        }
                    }

                }
                System.out.println("-------------------------------------");
                Type[] genericParameterTypes = declaredMethod.getGenericParameterTypes();
                Type genericReturnType = declaredMethod.getGenericReturnType();
                Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                Class<?> returnType = declaredMethod.getReturnType();
                TypeVariable<Method>[] typeParameters = declaredMethod.getTypeParameters();
            }
        }
    }

    public void wildcardType(TestType<? super String> param) {

    }

    public static void main(String[] args) throws NoSuchFieldException {
        //        System.out.println(methodToProperty("getAbc"));
        //        System.out.println(methodToProperty("getabc"));
        //        System.out.println(methodToProperty("getAAbc"));

        //                testParameterizedType();
        //        testTypeVariable();
        //        testGenericArrayType();
        testWildcardType();
    }

    public class TestType<T extends String> {
        private T t;

        public TestType(T t) {
            this.t = t;
        }

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }
    }
}
