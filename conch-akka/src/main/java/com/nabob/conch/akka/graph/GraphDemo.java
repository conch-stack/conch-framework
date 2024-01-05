package com.nabob.conch.akka.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.Deque;
import java.util.Iterator;
import java.util.Set;

/**
 * Graph Demo
 *
 * @author Adam
 * @since 2024/1/5
 */
public class GraphDemo {

    public static void main(String[] args) {

        Graph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
        directedGraph.addVertex("v1");
        directedGraph.addVertex("v2");
        directedGraph.addVertex("v3");
        directedGraph.addVertex("v4");
        directedGraph.addEdge("v1", "v2");
        directedGraph.addEdge("v1", "v3");
        directedGraph.addEdge("v2", "v4");
        directedGraph.addEdge("v3", "v4");

//        iterator(directedGraph);


        Iterator<String> bter = new BreadthFirstIterator<>(directedGraph);
        while (bter.hasNext()) {
            String vertex = bter.next();
            Set<DefaultEdge> defaultEdges = directedGraph.edgesOf(vertex);
        }

    }

    private static void iterator(Graph<String, DefaultEdge> directedGraph) {
        // BreadthFirstIterator（广度优先），DepthFirstIterator（深度优先）
        DepthFirstIterator<String, DefaultEdge> stringDefaultEdgeDepthFirstIterator = new DepthFirstIterator<>(directedGraph);
//        BreadthFirstIterator breadthFirstIterator = new BreadthFirstIterator<>(directedGraph);
        while (stringDefaultEdgeDepthFirstIterator.hasNext()) {
            String next = stringDefaultEdgeDepthFirstIterator.next();
            System.out.println(next);
        }

        System.out.println();

        Iterator<String> iter = new DepthFirstIterator<>(directedGraph);
        while (iter.hasNext()) {
            String vertex = iter.next();
            System.out.println(
                    "Vertex " + vertex + " is connected to: "
                            + directedGraph.edgesOf(vertex).toString());
        }

        System.out.println();

        Iterator<String> bter = new BreadthFirstIterator<>(directedGraph);
        while (bter.hasNext()) {
            String vertex = bter.next();
            System.out.println(
                    "Vertex " + vertex + " is connected to: "
                            + directedGraph.edgesOf(vertex).toString());
        }
    }
}
