package com.nabob.conch.sample.enhanceconsumer;

import java.util.List;

// 使用示例
class OrderService {
    @EventConsumer
    private void processPayment(MyMessage msg) {
        System.out.println("处理支付消息: " + msg.getEventId());
    }

    @EventConsumer
    public void handleShipping(MyMessage1 msg) {
        System.out.println("处理物流消息: " + msg.getSms());
    }

    public static void main(String[] args) {
        // 注册监听器
        OrderService service = new OrderService();
        List<MessageListener> listeners = MultiEventListenerFactory.createListeners(service);

        for (MessageListener listener : listeners) {
            listener.onMessage(new EventMessage("2", "{\"eventId\":\"email来了\",\"sms\":\"sms来了\"}"));
        }
    }
}