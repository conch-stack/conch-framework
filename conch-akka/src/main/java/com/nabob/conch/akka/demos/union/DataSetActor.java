//package com.nabob.conch.akka.demos.union;
//
//import akka.actor.typed.ActorRef;
//import akka.actor.typed.Behavior;
//import akka.actor.typed.javadsl.AbstractBehavior;
//import akka.actor.typed.javadsl.ActorContext;
//import akka.actor.typed.javadsl.Behaviors;
//import akka.actor.typed.javadsl.Receive;
//import org.apache.commons.collections4.CollectionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class DataSetActor extends AbstractBehavior<Message> {
//
//    private final static Logger logger = LoggerFactory.getLogger(DataSetActor.class.getName());
//
//    private ActorRef<Message> parent;
//
//    private Integer count = 0;
//
//    private Integer childrenCount = 0;
//
//    private MessageFinished finished;
//
//    private String data;
//
//    public static Behavior<Message> create(String data, MessageFinished finished) {
//        return Behaviors.setup(context -> new DataSetActor(context, data, finished));
//    }
//
//    private DataSetActor(ActorContext<Message> context, String data, MessageFinished finished) {
//        super(context);
//        this.data = data;
//        this.finished = finished;
//    }
//
//    @Override
//    public Receive<Message> createReceive() {
//        return newReceiveBuilder().onMessage(Message.class, this::onStart).build();
//    }
//
//    private Behavior<Message> onStart(Message message) {
//        switch (message.getMessage()) {
//            case START:
//                onStartMessage(message);
//                break;
//            case DATA_SOURCE_FINISHED:
//                onDsFinishedMessage();
//                break;
//            case FINISHED:
//                onFinishedMessage();
//                break;
//            default:
//                onFinishedMessage();
//                break;
//        }
//        return this;
//    }
//
//
//    private void onFinishedMessage() {
//        childrenCount++;
//        logger.info("========= {}.childrenCount={}", data, childrenCount);
//
//        if (childrenCount >= dataSet.getChildren().size()) {
//
//            try {
//
//                dataSet.postProcessChildren();
//
//            } catch (Exception e) {
//
//                logger.error("failed to call postProcessChildren, error as ", e);
//
//            }
//
//            if (parent != null) {
//
//                parent.tell(new Message(Message.EMessage.FINISHED, null));
//
//            }
//
//            if (finished != null) {
//
//                finished.setFinished(true);
//
//            }
//
//        }
//
//    }
//
//
//    private void onDsFinishedMessage() {
//
//        count++;
//
//        logger.info("========= {}.count={}", dataSet.getName(), count);
//
//        if (count >= dataSet.getDataSources().size()) {
//
//            try {
//
//                dataSet.postProcessDataSource();
//
//            } catch (Exception e) {
//
//                logger.error("failed to call postProcessDataSource, error as ", e);
//
//            }
//
//            try {
//
//                dataSet.preProcessChildren();
//
//            } catch (Exception e) {
//
//                logger.error("failed to call preProcessChildren, error as ", e);
//
//            }
//
//            if (CollectionUtils.isEmpty(dataSet.getChildren())) {
//
//                logger.info("======= children is empty");
//
//                if (parent != null) {
//
//                    logger.info("======== paren is not empty");
//
//                    parent.tell(new Message(Message.EMessage.FINISHED, null));
//
//                }
//
//                if (finished != null) {
//
//                    logger.info("======== finished is not empty");
//
//                    finished.setFinished(true);
//
//                }
//
//                return;
//
//            }
//
//            dataSet.getChildren().stream().forEach(node -> {
//
//                ActorRef<Message> nodeActor = getContext().spawn(DataSetActor.create(node, null), node.getName());
//
//                nodeActor.tell(new Message(Message.EMessage.START, this.getContext().getSelf()));
//
//            });
//
//        }
//
//    }
//
//
//    private void onStartMessage(Message message) {
//
//        try {
//
//            dataSet.preProcessDataSource();
//
//        } catch (Exception e) {
//
//            logger.error("failed to call preProcessDataSource, error as ", e);
//
//        }
//
//        if (CollectionUtils.isEmpty(dataSet.getDataSources())) {
//
//            this.dsContext.getData().put(dataSet.getName(), "");
//
//            if (message.getParent() != null) {
//
//                message.getParent().tell(new Message(Message.EMessage.FINISHED, null));
//
//            } else {
//
//                if (finished != null) {
//
//                    finished.setFinished(true);
//
//                }
//
//            }
//
//            return;
//
//        }
//
//        parent = message.getParent();
//
//        dataSet.getDataSources().stream().forEach(data -> {
//
//            ActorRef<Message> dataActor = getContext().spawn(DataSourceActor.create(data), data.getName());
//
//            dataActor.tell(new Message(Message.EMessage.START, this.getContext().getSelf()));
//
//        });
//
//    }
//
//}