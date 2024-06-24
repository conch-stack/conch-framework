package com.nabob.conch.akka.dag;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import scala.collection.immutable.List;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class DagDemo {

    static class Node {
        final String id;
        final Set<String> dependencies;

        public Node(String id, Set<String> dependencies) {
            this.id = id;
            this.dependencies = dependencies;
        }
    }

    static class StartExecution {
        final ActorRef node;

        public StartExecution(ActorRef node) {
            this.node = node;
        }
    }

    static class Executed {
        final ActorRef node;

        public Executed(ActorRef node) {
            this.node = node;
        }
    }

    static class NodeActor extends AbstractActor {
        private final Node node;

        public NodeActor(Node node) {
            this.node = node;
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(StartExecution.class, this::handleStartExecution)
                    .build();
        }

        private void handleStartExecution(StartExecution message) {
            // Check if all dependencies have been executed
            Set<String> executedDependencies = new HashSet<>();
            for (String dependencyId : node.dependencies) {
                if (context().children().forall(a -> a.path().name().equals(dependencyId))) {
                    executedDependencies.add(dependencyId);
                }
            }

            if (executedDependencies.size() == node.dependencies.size()) {
                // All dependencies have been executed, execute this node
                System.out.println("Executing: " + node.id);
                // Notify others that this node has been executed
                context().children().foreach(child -> {
                    if (!child.equals(getSelf())) {
                        child.tell(new Executed(getSelf()), getSelf());
                    }
                    return null;
                });
                // Stop this actor
                context().stop(getSelf());
            } else {
                System.out.println(node.id + " waiting for dependencies to be executed.");
            }
        }
    }

    public static void main(String[] args) {
        akka.actor.ActorSystem system = akka.actor.ActorSystem.create("DAGDemo");

        // Define the DAG nodes and their dependencies
        Node nodeA = new Node("A", new HashSet<>());
        Node nodeB = new Node("B", Sets.newHashSet("A"));
        Node nodeC = new Node("C", Sets.newHashSet("A"));
        Node nodeD = new Node("D", Sets.newHashSet("B", "C"));

        // Create the actors for the nodes
        ActorRef actorA = system.actorOf(Props.create(NodeActor.class, nodeA));
        ActorRef actorB = system.actorOf(Props.create(NodeActor.class, nodeB), "B");
        ActorRef actorC = system.actorOf(Props.create(NodeActor.class, nodeC), "C");
        ActorRef actorD = system.actorOf(Props.create(NodeActor.class, nodeD), "D");

        // Start the execution
        actorA.tell(new StartExecution(actorA), ActorRef.noSender());

        // Wait for the system to shut down
        system.whenTerminated();

    }
}