package graph;

public class UnionFind {
    private final int[] p, r;

    public long finds = 0;
    public long unions = 0;

    public UnionFind(int n) {
        p = new int[n]; r = new int[n];
        for (int i = 0; i < n; i++) { p[i] = i; r[i] = 0; }
    }

    public int find(int x) {
        finds++;
        if (p[x] != x) p[x] = find(p[x]);
        return p[x];
    }

    public boolean union(int a, int b) {
        unions++;
        int ra = find(a), rb = find(b);
        if (ra == rb) return false;
        if (r[ra] < r[rb]) p[ra] = rb;
        else if (r[rb] < r[ra]) p[rb] = ra;
        else { p[rb] = ra; r[ra]++; }
        return true;
    }
}
