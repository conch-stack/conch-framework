package ltd.beihu.sample.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.logging.log4j.spi.ThreadContextMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基本逻辑跟 ${@DefaultThreadContextMap} 保持一致，使用TransmittableThreadLocal替换ThreadLocal
 * 使用ConcurrentMap替换HashMap+copyOnWrite机制保证线程安全
 */
public class TtlThreadContextMap implements ThreadContextMap {
    private final ThreadLocal<Map<String, String>> localMap;

    public TtlThreadContextMap() {
        this.localMap = new TransmittableThreadLocal<Map<String, String>>() {
            @Override
            protected ConcurrentMap<String, String> initialValue() {
                return new ConcurrentHashMap<>();
            }
        };
    }

    @Override
    public void put(final String key, final String value) {
        Map<String, String> map = localMap.get();
        map = map == null ? new ConcurrentHashMap<String, String>() : map;
        map.put(key, value);
    }

    @Override
    public String get(final String key) {
        final Map<String, String> map = localMap.get();
        return map == null ? null : map.get(key);
    }

    @Override
    public void remove(final String key) {
        final Map<String, String> map = localMap.get();
        if (map != null) {
            map.remove(key);
        }
    }

    @Override
    public void clear() {
        localMap.remove();
    }

    @Override
    public boolean containsKey(final String key) {
        final Map<String, String> map = localMap.get();
        return map != null && map.containsKey(key);
    }

    @Override
    public Map<String, String> getCopy() {
        final Map<String, String> map = localMap.get();
        return map == null ? new HashMap<String, String>() : new HashMap<>(map);
    }

    @Override
    public Map<String, String> getImmutableMapOrNull() {
        return localMap.get();
    }

    @Override
    public boolean isEmpty() {
        final Map<String, String> map = localMap.get();
        return map == null || map.size() == 0;
    }

    @Override
    public String toString() {
        final Map<String, String> map = localMap.get();
        return map == null ? "{}" : map.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        final Map<String, String> map = this.localMap.get();
        result = prime * result + ((map == null) ? 0 : map.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TtlThreadContextMap)) {
            return false;
        }
        final TtlThreadContextMap other = (TtlThreadContextMap) obj;
        final Map<String, String> map = this.localMap.get();
        final Map<String, String> otherMap = other.getImmutableMapOrNull();
        if (map == null) {
            return otherMap == null;
        } else {
            return map.equals(otherMap);
        }
    }
}
