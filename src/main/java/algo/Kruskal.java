package algo;

import graph.Edge;
import graph.Graph;
import graph.UnionFind;
import metrics.Metrics;

import java.util.*;

public class Kruskal {

    public static Result mst(Graph g) {
        int n = g.size();
        List<Edge> edges = new ArrayList<>(g.edges());
        Metrics M = new Metrics();

        Comparator<Edge> cmp = (a, b) -> { M.comparisons++; return Double.compare(a.w, b.w); };

        long t0 = System.nanoTime();
        edges.sort(cmp);
        UnionFind uf = new UnionFind(n);
        List<Edge> mst = new ArrayList<>(n - 1);
        for (Edge e : edges) {
            int ru = uf.find(e.u); int rv = uf.find(e.v); M.ufFinds += 2;
            if (ru != rv) {
                if (uf.union(ru, rv)) { M.ufUnions++; mst.add(e); }
            }
            if (mst.size() == n - 1) break;
        }
        long t1 = System.nanoTime();

        if (mst.size() != n - 1) return Result.err("Graph is disconnected (Kruskal)");
        double cost = mst.stream().mapToDouble(ed -> ed.w).sum();
        M.ufFinds += uf.finds; M.ufUnions += uf.unions;
        return Result.ok(mst, cost, M.total(), (t1 - t0) / 1e6);
    }

    public static class Result {
        public final List<Edge> edges;
        public final double totalCost;
        public final long operations;
        public final double timeMs;
        public final boolean success;
        public final String error;

        private Result(List<Edge> edges, double totalCost, long operations, double timeMs, boolean success, String error) {
            this.edges = edges; this.totalCost = totalCost; this.operations = operations; this.timeMs = timeMs; this.success = success; this.error = error;
        }
        public static Result ok(List<Edge> e, double c, long op, double ms) { return new Result(e, c, op, ms, true, null); }
        public static Result err(String msg) { return new Result(List.of(), 0, 0, 0, false, msg); }
    }
}
