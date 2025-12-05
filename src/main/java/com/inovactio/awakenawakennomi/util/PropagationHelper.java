package com.inovactio.awakenawakennomi.util;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;

/**
 * Helper générique pour calculer un ordre de propagation (BFS) et assigner un tick cible par bloc via une courbe d'easing.
 */
public final class PropagationHelper {

    private PropagationHelper() {}

    public static final int[][] NEI_6 = {{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1}};

    public static class PropagationEntry {
        public final BlockPos pos;
        public final int tickToApply;
        public PropagationEntry(BlockPos pos, int tickToApply) {
            this.pos = pos;
            this.tickToApply = tickToApply;
        }
    }

    /**
     * Calcul principal.
     *
     * @param center centre de la zone
     * @param radius rayon en blocs
     * @param totalTicks durée totale (ticks) utilisée pour mapper la courbe d'easing
     * @param easing fonction d'easing : entrée fraction [0..1], sortie [0..1]
     * @param useSphere si true collecte les positions dans une sphère, sinon dans un cube
     * @param neighbors tableau de offsets de voisinage (ex : NEI_6). si null, NEI_6 est utilisé
     * @return liste ordonnée d'entrées (ordre de propagation)
     */
    public static List<PropagationEntry> computePropagationEntries(BlockPos center, int radius, int totalTicks,
                                                                   DoubleUnaryOperator easing, boolean useSphere, int[][] neighbors) {
        if (easing == null) easing = frac -> frac;
        if (neighbors == null) neighbors = NEI_6;

        List<BlockPos> all = useSphere ? collectSphere(center, radius) : collectCube(center, radius);
        List<BlockPos> ordered = bfsOrder(all, center, neighbors);

        int total = ordered.size();
        List<PropagationEntry> entries = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            double frac = total > 1 ? (double) i / (double) (total - 1) : 0.0;
            double eased = clamp01(easing.applyAsDouble(frac));
            int tick = Math.min(totalTicks, (int) Math.round(eased * totalTicks));
            entries.add(new PropagationEntry(ordered.get(i), tick));
        }
        return entries;
    }

    /**
     * Surcharge pratique : easing power (power > 1 = slow start / accelerate later).
     */
    public static List<PropagationEntry> computeWithPowerEase(BlockPos center, int radius, int totalTicks, double power) {
        DoubleUnaryOperator ease = frac -> Math.pow(frac, power);
        return computePropagationEntries(center, radius, totalTicks, ease, true, NEI_6);
    }

    private static double clamp01(double v) {
        if (Double.isNaN(v)) return 0.0;
        if (v < 0.0) return 0.0;
        if (v > 1.0) return 1.0;
        return v;
    }

    private static List<BlockPos> collectSphere(BlockPos center, int radius) {
        int r = radius;
        int r2 = r * r;
        List<BlockPos> temp = new ArrayList<>();
        for (int dy = -r; dy <= r; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx * dx + dy * dy + dz * dz <= r2) {
                        temp.add(new BlockPos(center.getX() + dx, center.getY() + dy, center.getZ() + dz));
                    }
                }
            }
        }
        return temp;
    }

    private static List<BlockPos> collectCube(BlockPos center, int radius) {
        int r = radius;
        List<BlockPos> temp = new ArrayList<>();
        for (int dy = -r; dy <= r; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    temp.add(new BlockPos(center.getX() + dx, center.getY() + dy, center.getZ() + dz));
                }
            }
        }
        return temp;
    }

    private static List<BlockPos> bfsOrder(List<BlockPos> allBlocks, BlockPos center, int[][] neighbors) {
        Set<Long> available = new HashSet<>(allBlocks.size());
        for (BlockPos p : allBlocks) available.add(p.asLong());

        List<BlockPos> ordered = new ArrayList<>(allBlocks.size());
        Deque<BlockPos> q = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();

        if (available.contains(center.asLong())) {
            q.add(center);
            visited.add(center.asLong());
        } else {
            allBlocks.sort(Comparator.comparingDouble(p -> p.distSqr(center)));
            BlockPos start = allBlocks.isEmpty() ? center : allBlocks.get(0);
            q.add(start);
            visited.add(start.asLong());
        }

        while (!q.isEmpty()) {
            BlockPos cur = q.poll();
            ordered.add(cur);
            for (int[] d : neighbors) {
                BlockPos nb = cur.offset(d[0], d[1], d[2]);
                long key = nb.asLong();
                if (available.contains(key) && !visited.contains(key)) {
                    visited.add(key);
                    q.add(nb);
                }
            }
        }

        if (ordered.size() < allBlocks.size()) {
            for (BlockPos p : allBlocks) {
                if (!visited.contains(p.asLong())) ordered.add(p);
            }
            ordered.sort(Comparator.comparingDouble(p -> p.distSqr(center)));
        }

        return ordered;
    }
}
