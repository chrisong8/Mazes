package graphs.shortestpaths;

import graphs.BaseEdge;
import graphs.Graph;
import priorityqueues.DoubleMapMinPQ;
import priorityqueues.ExtrinsicMinPQ;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 * @see SPTShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new DoubleMapMinPQ<>();
        /*
        If you have confidence in your heap implementation, you can disable the line above
        and enable the one below.
         */
        // return new ArrayHeapMinPQ<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        Map<V, E> edgeTo = new HashMap<>();
        Map<V, Double> distTo = new HashMap<>();
        if (Objects.equals(start, end)) {
            return edgeTo;
        }
        ExtrinsicMinPQ<V> perimeter = createMinPQ();
        distTo.put(start, 0.0);
        perimeter.add(start, 0.0);
        while (!perimeter.isEmpty() && !Objects.equals(perimeter.peekMin(), end)) {
            V u = perimeter.removeMin();
            for (E edge : graph.outgoingEdgesFrom(u)) {
                V v = edge.to();
                if (!distTo.containsKey(v)) {
                    distTo.put(v, Double.POSITIVE_INFINITY);
                    edgeTo.put(v, null);
                }
                double oldDistance = distTo.get(v);
                double newDistance = distTo.get(u) + edge.weight();
                if (newDistance < oldDistance) {
                    distTo.put(v, newDistance);
                    edgeTo.put(v, edge);
                    if (perimeter.contains(v)) {
                        perimeter.changePriority(v, newDistance);
                    } else {
                        perimeter.add(v, newDistance);
                    }
                }
            }
        }
        // if the spt is just 2 edges that point to each other
        if (edgeTo.size() == 2) {
            edgeTo.remove(start);
        }
        return edgeTo;
    }

    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {
        if (Objects.equals(start, end)) {
            return new ShortestPath.SingleVertex<>(start);
        }
        E back = spt.get(end);
        if (Objects.equals(back, null)) {
            return new ShortestPath.Failure<>();
        }
        List<E> edgeList = new ArrayList<>();
        // begin from end, backtrack through map to find path
        V node = end;
        while (!Objects.equals(node, start)) {
            E component = spt.get(node);
            edgeList.add(spt.get(node));
            node = component.from();
        }
        Collections.reverse(edgeList);
        return new ShortestPath.Success<>(edgeList);
    }
}
