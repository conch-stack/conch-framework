package ltd.beihu.spring.bean.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 1. 定义 ThreadLocal Scope
 *
 * @author Adam
 * @date 2020/4/27
 */
public class ThreadLocalScope implements Scope {

    public static final String SCOPE_NAME = "thread-local";
    private final NamedThreadLocal<Map<String, Object>> threadLocal = new NamedThreadLocal<Map<String, Object>>("thread-local-scope") {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    };

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Map<String, Object> context = getContext();
        Object scopeObject = context.get(name);
        if (null == scopeObject) {
            scopeObject = objectFactory.getObject();
            context.put(name, scopeObject);
        }
        return scopeObject;
    }

    @NonNull
    private Map<String, Object> getContext() {
        return threadLocal.get();
    }

    @Override
    public Object remove(String name) {
        return getContext().remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // todo 注册回调
    }

    @Override
    public Object resolveContextualObject(String key) {
        return getContext().get(key);
    }

    @Override
    public String getConversationId() {
        // 会话ID  - 线程ID
        return String.valueOf(Thread.currentThread().getId());
    }
}
