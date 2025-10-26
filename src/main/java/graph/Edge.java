package graph;

public class Edge implements Comparable<Edge> {
    public final int u;
    public final int v;
    public final double w;

    public Edge(int u, int v, double w) {
        if (u == v) throw new IllegalArgumentException("Self-loop not allowed");
        this.u = u; this.v = v; this.w = w;
    }
    @Override public int compareTo(Edge o) { return Double.compare(this.w, o.w); }
}
