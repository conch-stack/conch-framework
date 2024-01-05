//package com.nabob.conch.akka.demos.union;
//
//import akka.actor.typed.Behavior;
//
//public class DataSourceActor extends AbstractBehavior<Message> {
//
// private final static Logger logger = LoggerFactory.getLogger(DataSourceActor.class.getName());
//
// private DataSourceContext dsContext;
//
// private IDataSource dataSource;
//
// public static Behavior<Message> create(IDataSource dataSource) {
//
//  return Behaviors.setup(context -> new DataSourceActor(context, dataSource));
//
// }
//
// public String getName() {
//
//  return this.dataSource.getName();
//
// }
//
// public DataSourceActor(ActorContext<Message> context, IDataSource dataSource) {
//
//  super(context);
//
//  this.dataSource = dataSource;
//
//  this.dsContext = dataSource.getContext();
//
// }
//
// @Override
//
// public Receive<Message> createReceive() {
//
//  return newReceiveBuilder().onMessage(Message.class, this::onStart).build();
//
// }
//
// private Behavior<Message> onStart(Message message) {
//
//  try {
//
//   dataSource.process();
//
//  } catch (Exception e) {
//
//   logger.error("failed to call datasource.process, datasource as {}, error as ", dataSource, e);
//
//  }
//
//  logger.info("========= before tell {}.onStart", dataSource.getName());
//
//  message.getParent().tell(new Message(Message.EMessage.DATA_SOURCE_FINISHED, null));
//
//  logger.info("========= finished {}.onStart", dataSource.getName());
//
//  return this;
//
// }
//
//}
