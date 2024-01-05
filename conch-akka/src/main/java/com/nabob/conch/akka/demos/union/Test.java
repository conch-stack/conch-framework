//package com.nabob.conch.akka.demos.union;
//
//import akka.actor.typed.ActorSystem;
//
///**
// * @author Adam
// * @since 2024/1/5
// */
//public class Test {
//
//    public void processDataSetSync(IDataSourceSet root, DataSourceContext dsContext) {
//
// MessageFinished finished = new MessageFinished();
//
// final ActorSystem<Message> rootActor = ActorSystem.create(DataSetActor.create(root, finished), root.getName());
//
// rootActor.tell(new Message(Message.EMessage.START, null));
//
// while(!finished.getFinished()) {
//
// try {
//
//  TimeUnit.SECONDS.sleep(5);
//
//  logger.info("=======sleep");
//
// } catch (Exception e) {
//
// }
//
// }
//
// rootActor.terminate();
//
//}
//
//
//}
