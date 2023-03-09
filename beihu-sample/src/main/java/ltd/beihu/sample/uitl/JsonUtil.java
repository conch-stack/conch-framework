package ltd.beihu.sample.uitl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper.registerModule(javaTimeModule);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setTimeZone(TimeZone.getDefault());
    }

    /**
     * 序列化
     *
     * @param o
     * @return
     */
    public static String object2Json(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * 反序列化
     *
     * @param jsonStr
     * @param targetClz
     * @param <T>
     * @return
     */
    public static <T> T json2Object(String jsonStr, Class<T> targetClz) {
        try {
            return objectMapper.readValue(jsonStr,targetClz);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 反序列化泛型实体
     *
     * @param value
     * @param valueTypeRef
     * @param <T>
     * @return
     */
    public static <T> T json2Object(String value, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(value, valueTypeRef);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 序列化为 list<T> 对象
     *
     * @param value
     * @param valueType
     * @param <T>
     * @return
     */
    public static <T> List<T> json2List(String value, Class<T> valueType) {
        try {

            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, valueType);

            return objectMapper.readValue(value, javaType);
        }
        catch (Exception ex) {
            return null;
        }
    }
}
