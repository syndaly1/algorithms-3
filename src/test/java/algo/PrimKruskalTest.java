package algo;

import graph.Graph;
import io.JsonIO;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PrimKruskalTest {

    private Graph tinyGraph() {
        List<String> nodes = List.of("A","B","C","D","E");
        Graph g = new Graph(nodes);
        g.addEdge(0,1,4);
        g.addEdge(0,2,3);
        g.addEdge(1,2,2);
        g.addEdge(1,3,5);
        g.addEdge(2,3,7);
        g.addEdge(2,4,8);
        g.addEdge(3,4,6);
        return g;
    }

    private void assertTreeProperties(int V, List<graph.Edge> edges) {
        assertEquals(V - 1, edges.size(), "MST must have V-1 edges");

        graph.UnionFind uf = new graph.UnionFind(V);
        for (graph.Edge e : edges) {
            int u = e.u, v = e.v;
            assertNotEquals(uf.find(u), uf.find(v), "Cycle detected in MST edges");
            uf.union(u, v);
        }
        int root = uf.find(0);
        for (int i = 1; i < V; i++) {
            assertEquals(root, uf.find(i), "MST is not a single connected component");
        }
    }

    @Test
    void tinyGraph_Correctness() {
        Graph g = tinyGraph();

        var p = Prim.mst(g);
        var k = Kruskal.mst(g);

        assertTrue(p.success, "Prim must succeed on tiny graph");
        assertTrue(k.success, "Kruskal must succeed on tiny graph");

        assertEquals(p.totalCost, k.totalCost, 1e-9);

        assertTreeProperties(g.size(), p.edges);
        assertTreeProperties(g.size(), k.edges);

        assertTrue(p.timeMs >= 0.0);   assertTrue(k.timeMs >= 0.0);
        assertTrue(p.operations >= 0); assertTrue(k.operations >= 0);
    }

    @Test
    void disconnectedGraph_Handled() {
        List<String> nodes = List.of("A","B","C","D");
        Graph g = new Graph(nodes);
        g.addEdge(0,1,1);
        g.addEdge(2,3,1);

        var p = Prim.mst(g);
        var k = Kruskal.mst(g);

        assertFalse(p.success, "Prim must report disconnected graph");
        assertFalse(k.success, "Kruskal must report disconnected graph");
    }

    @Test
    void dataset_AllGraphs_CheckEverything() throws Exception {
        File input = new File("src/main/resources/ass_3_input.json");
        assertTrue(input.exists(), "ass_3_input.json must exist (30 graphs)");

        JsonIO.InputData data = JsonIO.readInput(input);
        assertNotNull(data.graphs);
        assertEquals(30, data.graphs.size(), "Expected 30 graphs in combined input");

        for (var ig : data.graphs) {
            Graph g = JsonIO.toGraph(ig);

            var p1 = Prim.mst(g);
            var k1 = Kruskal.mst(g);

            var p2 = Prim.mst(g);
            var k2 = Kruskal.mst(g);

            if (p1.success && k1.success) {
                assertEquals(p1.totalCost, k1.totalCost, 1e-6, "MST costs must match (Prim vs Kruskal), graph " + ig.id);

                assertTreeProperties(g.size(), p1.edges);
                assertTreeProperties(g.size(), k1.edges);
            }

            assertTrue(p1.timeMs >= 0 && k1.timeMs >= 0);
            assertTrue(p1.operations >= 0 && k1.operations >= 0);

            assertEquals(p1.totalCost, p2.totalCost, 1e-9);
            assertEquals(k1.totalCost, k2.totalCost, 1e-9);

            Set<String> pSet1 = asUndirectedSet(p1.edges);
            Set<String> pSet2 = asUndirectedSet(p2.edges);
            Set<String> kSet1 = asUndirectedSet(k1.edges);
            Set<String> kSet2 = asUndirectedSet(k2.edges);

            assertEquals(pSet1, pSet2, "Prim MST edges must be reproducible, graph " + ig.id);
            assertEquals(kSet1, kSet2, "Kruskal MST edges must be reproducible, graph " + ig.id);
        }
    }

    private Set<String> asUndirectedSet(List<graph.Edge> edges) {
        Set<String> s = new HashSet<>();
        for (graph.Edge e : edges) {
            int a = Math.min(e.u, e.v), b = Math.max(e.u, e.v);
            s.add(a + "-" + b + ":" + Math.round(e.w));
        }
        return s;
    }
}
