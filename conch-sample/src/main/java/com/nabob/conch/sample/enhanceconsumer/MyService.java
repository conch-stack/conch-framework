package com.nabob.conch.sample.enhanceconsumer;

// 3. 使用示例
public class MyService {
    @EventConsumer
    public void handleMessage1(MyMessage msg) {
        System.out.println("Processing1: " + msg.getEventId());
    }
//
//    @EventConsumer
//    public void handleMessage2(Message msg) {
//        System.out.println("Processing2: " + msg.getFormat());
//    }

    public static void main(String[] args) {
        // 4. 注册使用
        MyService service = new MyService();
        MessageListener listener = EventListenerEnhancer.createListener(service);

        listener.onMessage(new EventMessage("1", "{\"eventId\":\"限时3折·直升VIP速*3次\"}"));
    }
}