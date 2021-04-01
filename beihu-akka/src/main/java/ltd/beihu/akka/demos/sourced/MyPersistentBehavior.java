package ltd.beihu.akka.demos.sourced;

import akka.actor.typed.Behavior;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;

/**
 * EventSourced 事件溯源
 *    persistenceId：唯一id
 *    emptyState：初始化状态
 *    commandHandler：定义如何处理Command，如持久化Event或停止Persistent Actor
 *    eventHandler：Event持久化后，返回给定State的新State
 */
public class MyPersistentBehavior extends EventSourcedBehavior<MyPersistentBehavior.Command, MyPersistentBehavior.Event, MyPersistentBehavior.State> {

  interface Command {}

  interface Event {}

  public static class State {}

  public static Behavior<Command> create() {
    return new MyPersistentBehavior(PersistenceId.ofUniqueId("pid"));
  }

  private MyPersistentBehavior(PersistenceId persistenceId) {
    super(persistenceId);
  }

  @Override
  public State emptyState() {
    return new State();
  }

  /**
   * 命令处理器根据输入的状态和命令，返回造成的影响
   *    影响包含下面几种：
   *      Effect.persist 将持久地保留一个事件或多个事件，即如果发生错误，将存储所有事件或不存储任何事件
   *      Effect.none    不会保留任何事件，例如，只读命令
   *      Effect.unhandled 当前状态下未处理命令（不支持）
   *      Effect.stop 阻止这个演员
   *      Effect.stash 当前命令已隐藏
   *      Effect.unstashAll 处理存放在其中的命令 Effect().stash
   *      Effect.reply 向给定的人发送回复消息 ActorRef
   *
   * Effect().persist(..).thenRun
   *      thenRun成功持久后，除了以下操作之外，还可以执行以下操作：
   *          thenStop 演员将被阻止
   *          thenUnstashAll 处理存放在其中的命令 Effect().stash
   *          thenReply 向给定的人发送回复消息 ActorRef
   */
  @Override
  public CommandHandler<Command, Event, State> commandHandler() {

    /*

    return newCommandHandlerBuilder()
      .forAnyState()
      .onCommand(Add.class, this::onAdd)
      .onCommand(Clear.class, this::onClear)
      .build();

      private Effect<Event, State> onAdd(Add command) {
      return Effect()
          .persist(new Added(command.data))
          .thenRun(newState -> subscriber.tell(newState));
    }

    private Effect<Event, State> onClear(Clear command) {
      return Effect()
          .persist(Cleared.INSTANCE)
          .thenRun(newState -> subscriber.tell(newState))
          .thenStop();
    }

     */

    return (state, command) -> {
      throw new RuntimeException("TODO: process the command & return an Effect");
    };
  }

  @Override
  public EventHandler<State, Event> eventHandler() {
    return (state, event) -> {
      throw new RuntimeException("TODO: process the event return the next state");
    };
  }
}