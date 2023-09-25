package com.nabob.conch.hystrix;

import com.nabob.conch.hystrix.core.Bucket;
import com.nabob.conch.hystrix.core.BucketCircularArray;
import com.nabob.conch.hystrix.metric.MetricEventType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 控制器
 *
 * @author Adam
 * @since 2023/9/14
 */
public class MetricTimeWindow {

    private String metricName;

    private String serviceName;

    private String method;

    private BucketCircularArray bucketArray;

    private CircuitBreaker circuitBreaker;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // 每个Bucket时间跨度
    private long bucketTimeSpan = 3;

    public MetricTimeWindow(String serviceName, String method, int windowLength) {
        this.metricName = serviceName + "@" + method;
        this.serviceName = serviceName;
        this.method = method;

        this.bucketArray = new BucketCircularArray(windowLength);
        this.circuitBreaker = new CircuitBreaker(serviceName, method);
    }

    public void addEvent(MetricEventType type) {
        Bucket bucket = getCurrentBucket();
        switch (type) {
            case success:
                bucket.getSuccessNum().incrementAndGet();
                this.circuitBreaker.makeSuccess();
                break;
            case fail:
                bucket.getFailNum().incrementAndGet();
                this.circuitBreaker.makeNonSuccess();
                break;
            case timeout:
                bucket.getTimeoutNum().incrementAndGet();
                this.circuitBreaker.makeNonSuccess();
                break;
        }
    }

    // private

    /**
     * 获取最新Bucket
     * > 如果已过BucketTimeSpan，则新创建Bucket
     * > 创建新的Bucket后，触发判断是否熔断逻辑
     */
    private Bucket getCurrentBucket() {
        long currentTime = System.currentTimeMillis();
        lock.readLock().lock();
        Bucket bucket = null;
        try {
            bucket = bucketArray.tail();
            if (bucket != null && currentTime <= (bucket.getWindowStart() + bucketTimeSpan)) {
                return bucket;
            }
        } finally {
            lock.readLock().unlock();
        }

        boolean createNewBucket = false;
        lock.writeLock().lock();
        try {
            Bucket check = bucketArray.tail();
            if (check != null && currentTime <= (check.getWindowStart() + bucketTimeSpan)) {
                bucket = check;
            } else {
                // 新建Bucket
                bucket = new Bucket(currentTime);
                bucketArray.addBucket(bucket);
                createNewBucket = true;
            }
        } finally {
            lock.writeLock().unlock();
        }

        if (createNewBucket) {
            dealCreateBucketEvent();
        }

        return bucket;
    }

    /**
     * 处理Bucket创建事件
     * <p>
     * 尝试开启熔断
     */
    private void dealCreateBucketEvent() {
        lock.readLock().lock();
        List<Bucket> data = bucketArray.toList();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }

        long successNum = 0L;
        long failNum = 0L;

        for (Bucket dataItem : data) {
            successNum += dataItem.getSuccessNum().get();
            failNum += (dataItem.getFailNum().get() + dataItem.getTimeoutNum().get());
        }

        long reqNum = successNum + failNum;
        int errorPercentage = (int) ((double) failNum / reqNum * 100);
        circuitBreaker.tryMarkOpen(errorPercentage, reqNum);
    }
}
