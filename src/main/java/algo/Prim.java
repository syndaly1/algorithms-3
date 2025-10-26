package algo;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;

import java.util.*;

public class Prim {

    private static class Item implements Comparable<Item> {
        final int from, to; final double w;
        Item(int from, int to, double w) { this.from = from; this.to = to; this.w = w; }
        @Override public int compareTo(Item o) { return Double.compare(this.w, o.w); }
    }

    public static Result mst(Graph g) {
        int n = g.size();
        if (n == 0) return Result.ok(List.of(), 0, 0, 0);

        boolean[] used = new boolean[n];
        List<Edge> mst = new ArrayList<>(n - 1);
        Metrics M = new Metrics();

        long t0 = System.nanoTime();

        PriorityQueue<Item> pq = new PriorityQueue<>();
        used[0] = true;
        for (Edge e : g.adj().get(0)) {
            int other = (e.u == 0 ? e.v : e.u);
            pq.offer(new Item(0, other, e.w)); M.pushes++;
        }

        while (!pq.isEmpty() && mst.size() < n - 1) {
            Item it = pq.poll(); M.pops++;
            if (used[it.to]) { M.comparisons++; continue; }
            used[it.to] = true;
            mst.add(new Edge(it.from, it.to, it.w));
            for (Edge e : g.adj().get(it.to)) {
                int other = (e.u == it.to ? e.v : e.u);
                if (!used[other]) { pq.offer(new Item(it.to, other, e.w)); M.pushes++; }
                M.comparisons++;
            }
        }

        long t1 = System.nanoTime();
        if (mst.size() != n - 1) return Result.err("Graph is disconnected (Prim)");
        double cost = mst.stream().mapToDouble(e -> e.w).sum();
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
