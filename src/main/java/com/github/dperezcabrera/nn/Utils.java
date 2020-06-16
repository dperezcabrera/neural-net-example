package com.github.dperezcabrera.nn;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Utils {

    private static final Random RAND = new Random();

    private Utils() {
    }

    public static double[] rand(int size, Function fn) {
        return RAND.doubles(size).map(fn::apply).toArray();
    }

    public static double[][] rand(int sizeX, int sizeY, Function fn) {
        return IntStream.range(0, sizeX)
                .mapToObj(i -> rand(sizeY, fn))
                .toArray(double[][]::new);
    }

    public static double[] apply(double[] v, Function fn) {
        return IntStream.range(0, v.length)
                .mapToDouble(i -> fn.apply(v[i]))
                .toArray();
    }

    public static double[][] apply(double[][] m, Function fn) {
        return IntStream.range(0, m.length)
                .mapToObj(i -> apply(m[i], fn))
                .toArray(double[][]::new);
    }

    public static double[] apply(double[] v1, double[] v2, Function2 fn) {
        return IntStream.range(0, v1.length)
                .mapToDouble(i -> fn.apply(v1[i], v2[i]))
                .toArray();
    }

    public static double[][] apply(double[][] m1, double[][] m2, Function2 fn) {
        return IntStream.range(0, m1.length)
                .mapToObj(i -> apply(m1[i], m2[i], fn))
                .toArray(double[][]::new);
    }

    public static double[] mean(double[][] m) {
        return IntStream.range(0, m[0].length).mapToDouble(i -> IntStream.range(0, m.length)
                .mapToDouble(j -> m[j][i])
                .average()
                .orElse(0))
                .toArray();
    }

    public static double[][] transpose(double[][] m) {
        return IntStream.range(0, m[0].length)
                .mapToObj(i -> IntStream.range(0, m.length).mapToDouble(j -> m[j][i]).toArray())
                .toArray(double[][]::new);
    }

    public static double[] diff(double[] v1, double[] v2) {
        return apply(v1, v2, (e1, e2) -> e1 - e2);
    }

    public static double[][] sum(double[][] m, double[] v) {
        return IntStream.range(0, m.length)
                .mapToObj(i -> apply(m[i], v, (a1, a2) -> a1 + a2))
                .toArray(double[][]::new);
    }

    public static double[][] diff(double[][] m1, double[][] m2) {
        return apply(m1, m2, (v1, v2) -> v1 - v2);
    }

    public static double[] mult(double[] v, double a) {
        return apply(v, e -> e * a);
    }

    public static double[][] mult(double[][] m, double v) {
        return apply(m, a -> v * a);
    }

    public static double[][] mult(double[][] m1, double[][] m2) {
        return apply(m1, m2, (v1, v2) -> v1 * v2);
    }

    public static double[][] matrixMult(double[][] m1, double[][] m2) {
        return Stream.of(m1).map(r -> IntStream.range(0, m2[0].length)
                .mapToDouble(i -> IntStream.range(0, m2.length).mapToDouble(j -> r[j] * m2[j][i]).sum())
                .toArray())
                .toArray(double[][]::new);
    }

    public static String format(double value, String format){
        if (value >= 0) {
            format = " ".concat(format);
        }
        return String.format(format, value);
    }

    public interface Function {

        double apply(double d);
    }

    public interface Function2 {

        double apply(double d, double d2);
    }
}
