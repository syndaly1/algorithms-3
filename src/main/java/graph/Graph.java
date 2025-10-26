package graph;

import java.util.*;

public class Graph {
    private final int n;
    private final List<String> labels;
    private final List<List<Edge>> adj;

    public Graph(List<String> labels) {
        this.n = labels.size();
        this.labels = new ArrayList<>(labels);
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }

    public int size() { return n; }
    public String labelOf(int i) { return labels.get(i); }
    public int indexOfLabel(String label) { return labels.indexOf(label); }
    public List<List<Edge>> adj() { return adj; }

    public void addEdge(int u, int v, double w) {
        if (u < 0 || u >= n || v < 0 || v >= n) throw new IndexOutOfBoundsException();
        Edge e = new Edge(u, v, w);
        adj.get(u).add(e);
        adj.get(v).add(e);
    }

    public List<Edge> edges() {
        List<Edge> list = new ArrayList<>();
        boolean[][] seen = new boolean[n][n];
        for (int u = 0; u < n; u++) {
            for (Edge e : adj.get(u)) {
                if (!seen[e.u][e.v] && !seen[e.v][e.u]) {
                    list.add(e);
                    seen[e.u][e.v] = seen[e.v][e.u] = true;
                }
            }
        }
        return list;
    }
}
