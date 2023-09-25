package com.nabob.conch.hystrix.core;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 桶 环形数组
 *
 * @author Adam
 * @since 2023/9/14
 */
@Data
public class BucketCircularArray {

    /**
     * 默认 数组大小
     */
    private volatile int size = 10;

    /**
     * 最大长度，可调整
     */
    private static int maxSize = 60;

    /**
     * 真实数据长度
     */
    private volatile int dataLength;

    /**
     * 数据
     */
    private Bucket[] data;

    /**
     * 头
     */
    private int head;

    /**
     * 尾
     * <p>
     * tail指针指向的是 尾元素的下一个可用位置
     */
    private int tail;

    public BucketCircularArray(int size) {
        this.size = size;
    }

    /**
     * 添加桶
     */
    public void addBucket(Bucket bucket) {
        data[tail] = bucket;
        incrementTail();
    }

    /**
     * 获取末尾元素
     */
    public Bucket tail() {
        if (dataLength == 0) {
            return null;
        }

        // 计算在环形数组上的 tail位置；因为tail指针指向的是 尾元素的下一个可用位置
        int index = (head + dataLength - 1) % dataLength;
        return data[index];
    }

    /**
     * 获取所有元素 按实际顺序
     */
    public List<Bucket> toList() {
        List<Bucket> rs = Lists.newArrayList();
        for (int i = 0; i < dataLength; i++) {
            int index = (head + i) % dataLength;
            Bucket tmp = data[index];
            if (tmp != null) {
                rs.add(tmp);
            }
        }
        return rs;
    }

    // private

    /**
     * 环形数组
     */
    private void incrementTail() {
        if (dataLength == size) {
            // 满了，则移到tail指针到数组头；head指针同时往后挪
            head = (head + 1) % size;
            tail = (tail + 1) % size;
        } else {
            tail = (tail + 1) % size;
        }

        if (dataLength < size) {
            dataLength++;
        }
    }

}
