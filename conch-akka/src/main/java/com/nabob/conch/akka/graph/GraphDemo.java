package com.nabob.conch.akka.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.DepthFirstIterator;

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
        directedGraph.addEdge("v1", "v2");


        // BreadthFirstIterator（广度优先），DepthFirstIterator（深度优先）
        DepthFirstIterator<String, DefaultEdge> stringDefaultEdgeDepthFirstIterator = new DepthFirstIterator<>(directedGraph);
//        BreadthFirstIterator breadthFirstIterator = new BreadthFirstIterator<>(directedGraph);




    }

}
