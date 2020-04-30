package ltd.beihu.core.icodeless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Adam
 * @date 2020/4/29
 */
public class TestStream {

    public static void testStream() {
        List<String> testList = new ArrayList<>();

        testList.add("111");
        testList.add("222");
        testList.add("eee");
        testList.add("eeer");
        testList.add("rrt");
        testList.add("yyyy");

        Stream<String> stream = testList.stream();

        stream.forEach(System.out::println);

        System.out.println("-----------测试流重复读取---------");

        stream.forEach(System.out::println);
    }

    public static void testMap1() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        Integer integer = map.computeIfAbsent("3", key -> new Integer(4));//key存在返回value
        Integer integer1 = map.computeIfAbsent("4", key -> new Integer(4));//key不存在执行函数存入
        System.out.println(integer);
        System.out.println(integer1);
        System.out.println(map.toString());
    }

    public static void testMap2() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        //只对map中存在的key对应的value进行操作
        Integer integer = map.computeIfPresent("3", (k, v) -> v + 1);
        Integer integer1 = map.computeIfPresent("4", (k, v) -> {
            if (v == null) {
                return 0;
            }
            return v + 1;
        });
        System.out.println(integer);
        System.out.println(integer1);
        System.out.println(map.toString());
    }

    public static void main(String[] args) {
//        testStream();
//        testMap1();
//        testMap2();

        // 测试 fastjson bean 转 map ： 忽略字段
//        SingleOrderMiniRefundDTO singleOrderMiniRefundDTO = new SingleOrderMiniRefundDTO();
//        singleOrderMiniRefundDTO.setPlatform(1);
//        singleOrderMiniRefundDTO.setOrderNo("sss");
//        singleOrderMiniRefundDTO.setAmount(BigDecimal.valueOf(10));
//        singleOrderMiniRefundDTO.setCourseClosed(true);
//        singleOrderMiniRefundDTO.setSerialNo(null);
//        singleOrderMiniRefundDTO.setRemark("dddd");
//
//        PropertyPreFilters filters = new PropertyPreFilters();
//        PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
//        excludefilter.addExcludes("remark");
//        Map<String, Object> rs = JSON.parseObject(JSON.toJSONString(singleOrderMiniRefundDTO, excludefilter));
//        Map<String, Object> rs1 = JSON.parseObject(JSON.toJSONString(singleOrderMiniRefundDTO));
//
//        System.out.println(rs);
//        System.out.println(rs1);
    }
}
