package metrics;

public class Metrics {
    public long comparisons = 0;
    public long pushes = 0;
    public long pops = 0;
    public long ufFinds = 0;
    public long ufUnions = 0;

    public long total() {
        return comparisons + pushes + pops + ufFinds + ufUnions;
    }
}
