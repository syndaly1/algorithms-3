package cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.util.*;

public class GraphGenerator {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static final Random R_SMALL  = new Random(101);
    private static final Random R_MED    = new Random(202);
    private static final Random R_LARGE  = new Random(303);
    private static final Random R_EXTRA  = new Random(404);

    public static void main(String[] args) throws Exception {
        File resources = new File("src/main/resources");
        resources.mkdirs();

        InputData small  = generateSmallGraphs();
        InputData medium = generateMediumGraphs();
        InputData large  = generateLargeGraphs();
        InputData extra  = generateExtraLargeGraphs();

        MAPPER.writeValue(new File(resources, "small_graphs.json"),  small);
        MAPPER.writeValue(new File(resources, "medium_graphs.json"), medium);
        MAPPER.writeValue(new File(resources, "large_graphs.json"),  large);
        MAPPER.writeValue(new File(resources, "extra_graphs.json"),  extra);


        List<GraphInput> all = new ArrayList<>();
        all.addAll(small.graphs);
        all.addAll(medium.graphs);
        all.addAll(large.graphs);
        all.addAll(extra.graphs);
        for (int i = 0; i < all.size(); i++) all.get(i).id = i + 1;

        MAPPER.writeValue(new File(resources, "ass_3_input.json"), new InputData(all));
        System.out.println("Generated 30 graphs into src/main/resources/ (and merged ass_3_input.json)");
    }


    private static InputData generateSmallGraphs() {
        // <30 : (10,15,20,25,30)
        int[] sizes = {10, 15, 20, 25, 30};
        return generateGroup(sizes, 0.40, 0.70, R_SMALL); // 40–70% density
    }
    private static InputData generateMediumGraphs() {
        // <300 : (50,75,100,125,150,175,200,225,250,300)
        int[] sizes = {50, 75, 100, 125, 150, 175, 200, 225, 250, 300};
        return generateGroup(sizes, 0.20, 0.50, R_MED);   // 20–50%
    }
    private static InputData generateLargeGraphs() {
        // <1000 : (400,450,500,550,600,650,700,800,900,1000)
        int[] sizes = {400, 450, 500, 550, 600, 650, 700, 800, 900, 1000};
        return generateGroup(sizes, 0.05, 0.15, R_LARGE); // 5–15% (sparser)
    }
    private static InputData generateExtraLargeGraphs() {
        // extra : (1300,1600,2000,2300,2600)
        int[] sizes = {1300, 1600, 2000, 2300, 2600};
        return generateGroup(sizes, 0.02, 0.08, R_EXTRA); // 2–8%
    }

    private static InputData generateGroup(int[] sizes, double densLow, double densHigh, Random r) {
        List<GraphInput> list = new ArrayList<>();
        int localId = 1;
        for (int n : sizes) {
            double d = densLow + r.nextDouble() * (densHigh - densLow);
            list.add(generateGraph(localId++, n, d, r));
        }
        return new InputData(list);
    }

    private static GraphInput generateGraph(int id, int n, double density, Random r) {
        GraphInput g = new GraphInput();
        g.id = id;

        g.nodes = new ArrayList<>(n);
        for (int i = 0; i < n; i++) g.nodes.add(toAlphabet(i));

        g.edges = new ArrayList<>();
        List<Integer> perm = new ArrayList<>(n);
        for (int i = 0; i < n; i++) perm.add(i);
        Collections.shuffle(perm, r);

        Set<Long> used = new HashSet<>();
        for (int i = 1; i < n; i++) {
            int u = perm.get(i - 1), v = perm.get(i);
            addEdge(g, used, u, v, 1 + r.nextInt(100));
        }

        int maxEdges = n * (n - 1) / 2;
        int target = Math.min(maxEdges, Math.max(n - 1, (int) Math.round(density * maxEdges)));

        while (g.edges.size() < target) {
            int u = r.nextInt(n), v = r.nextInt(n);
            if (u == v) continue;
            if (markIfNew(used, u, v)) {
                addEdge(g, used, u, v, 1 + r.nextInt(100));
            }
        }
        return g;
    }

    private static void addEdge(GraphInput g, Set<Long> used, int u, int v, int w) {
        InputEdge e = new InputEdge();
        e.from = g.nodes.get(u);
        e.to = g.nodes.get(v);
        e.weight = w;
        g.edges.add(e);
        long a = Math.min(u, v), b = Math.max(u, v);
        used.add((a << 32) ^ b);
    }

    private static boolean markIfNew(Set<Long> used, int u, int v) {
        long a = Math.min(u, v), b = Math.max(u, v);
        long k = (a << 32) ^ b;
        if (used.contains(k)) return false;
        used.add(k);
        return true;
    }

    private static final String[] ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    private static String toAlphabet(int idx) {
        StringBuilder sb = new StringBuilder();
        while (idx >= 0) {
            sb.insert(0, ABC[idx % 26]);
            idx = idx / 26 - 1;
        }
        return sb.toString();
    }

    public static class InputEdge {
        public String from;
        public String to;
        public int weight;
    }
    public static class GraphInput {
        public int id;
        public List<String> nodes;
        public List<InputEdge> edges;
    }
    public static class InputData {
        public List<GraphInput> graphs;
        public InputData() {}
        public InputData(List<GraphInput> graphs) { this.graphs = graphs; }
    }
}
