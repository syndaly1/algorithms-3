package io;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import graph.Graph;

import java.io.*;
import java.util.*;

public class JsonIO {
    private static final ObjectMapper M = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static class InputEdge { public String from; public String to; public double weight; }
    public static class InputGraph { public int id; public List<String> nodes; public List<InputEdge> edges; }
    public static class InputData { public List<InputGraph> graphs; }

    public static class AlgoOut {
        public List<Map<String,Object>> mst_edges;
        public double total_cost;
        public long operations_count;
        public double execution_time_ms;
        public String error;
    }

    public static class ResultItem {
        public int graph_id;
        public Map<String,Integer> input_stats;
        public AlgoOut prim;
        public AlgoOut kruskal;
    }
    public static class OutputData { public List<ResultItem> results = new ArrayList<>(); }

    public static InputData readInput(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return M.readValue(is, InputData.class);
        }
    }

    public static void writeOutput(File file, OutputData data) throws IOException {
        try (OutputStream os = new FileOutputStream(file)) {
            M.writeValue(os, data);
        }
    }

    public static Graph toGraph(InputGraph ig) {
        Graph g = new Graph(ig.nodes);
        for (InputEdge e : ig.edges) {
            int u = g.indexOfLabel(e.from);
            int v = g.indexOfLabel(e.to);
            g.addEdge(u, v, e.weight);
        }
        return g;
    }

    public static Map<String,Object> edgeObj(String from, String to, double w) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("from", from); m.put("to", to); m.put("weight", w);
        return m;
    }
}
