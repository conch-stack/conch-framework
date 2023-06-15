package ltd.beihu.akka.demos.event;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * Event1 事件 处理行为定义
 *
 * @author Adam
 * @date 2021/2/23
 */
public class Event1Behavior extends AbstractBehavior<Event1> {

    // 子Actor
    private final ActorRef<Event2> event2ActorRef;

    private Event1Behavior(ActorContext<Event1> context) {
        super(context);

        // todo
        // context.scheduleOnce()

        // Create a child Actor from the given Behavior and with the given name.
        // 创建子Actor
        event2ActorRef = context.spawn(Event2Behavior.create(), "Event2Behavior");
    }

    /**
     * 构建 Event1 行为处理 - 由ActorSystem调用
     *
     * @return Behavior Event1
     */
    public static Behavior<Event1> create() {
        return Behaviors.setup(Event1Behavior::new);
    }

    @Override
    public Receive<Event1> createReceive() {
        return newReceiveBuilder().onMessage(Event1.class, this::handleEvent1)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    /**
     * 处理停止信号 - 生命周期
     *
     * @return Behavior Event1
     */
    private Behavior<Event1> onPostStop() {
        getContext().getLog().info("Event1Behavior Stopped");
        return this;
    }

    /**
     * 处理 event1
     *
     * 延迟重启:
     *      Behaviors.supervise(CreditCardProcessor.create())
     *               .onFailure(StorageFailedException.class, SupervisorStrategy.restartWithBackoff(Duration.ofSeconds(2), Duration.ofSeconds(10), 0.2));
     *
     * 重启策略：
     *      SupervisorStrategy.restart().withStopChildren(false)  - 保留子Actor - 消耗大
     *
     * 一个actor可以监控其他actor的生命周期，当actor停止的时候就能收到一个通知：
     *      // 监控configuration actor
     *      context.watch(configuration);
     *      // todo
     *      context.watchWith(actor, protocol)
     *
     * 信号包括两类：生命周期信号（PreRestart、PostStop）和监控信号（Terminated、ChildFailed）
     *
     * Behaviors.same()
     * ChildFailed.class
     * Terminated.class
     *
     * .onSignal(ChildFailed.class, childFailed -> {
     *   context.getLog().warn("子actor {} 失败了，异常原因：{}", childFailed.getRef(), childFailed.cause().getMessage());
     *   return Behaviors.same();
     * }).onSignal(Terminated.class, terminated -> {
     *   context.getLog().error("Configuration actor {} 不可用, 系统出问题了", terminated.getRef());
     *   return Behaviors.stopped();
     * })
     *
     * @param event1 event1
     * @return Behavior Event1
     */
    public Behavior<Event1> handleEvent1(Event1 event1) {
        // 构建Event3的ActorRef给Event2用 - 失败需重启
        ActorRef<Event3> event3ActorRef = getContext().spawn(Behaviors.supervise(Event3Behavior.create(3))
                .onFailure(SupervisorStrategy.restart()), "Event3Behavior");
        // 发消息给Event2
        event2ActorRef.tell(new Event2(event1.name, event3ActorRef));
        return this;
    }
}
