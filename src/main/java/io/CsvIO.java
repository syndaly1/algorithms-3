package io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class CsvIO {
    public static PrintWriter open(File file) throws IOException {
        return new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
    }
    public static void header(PrintWriter pw) {
        pw.println("graph_id,V,E,prim_cost,kruskal_cost,prim_ms,kruskal_ms,prim_ops,kruskal_ops");
    }
    public static void line(PrintWriter pw, int id, int V, int E,
                            String pc, String kc,
                            double pms, double kms,
                            String ps, String ks,
                            long pops, long kops) {
        pw.printf(Locale.US, "%d,%d,%d,%s,%s,%.3f,%.3f,%d,%d%n",
                id, V, E, pc, kc, pms, kms, pops, kops);
    }
}
