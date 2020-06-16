package com.github.dperezcabrera.nn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Data{ 

    private static final Random RAND = new Random();

    private double[][] x;
    private double[] y;

    public Data(double[][] x, double[] y) {
        this.x = x;
        this.y = y;
    }

    public static Data makeCircles(int size, double factor, double noise) {
        double[] factors = {1, factor};
        List<Double> yList = new ArrayList<>(size);
        double[][] x = IntStream.range(0, size)
                .map(i -> i % 2)
                .filter(i -> yList.add((double) i))
                .mapToDouble(i -> factors[i])
                .mapToObj(f -> circle(f, noise)).toArray(double[][]::new);

        double[] y = yList.stream().mapToDouble(Double::doubleValue).toArray();
        return new Data(x, y);
    }

    private static double[] circle(double r, double noise) {
        double delta = randomAngle();
        return new double[]{noise(r * Math.cos(delta), noise), noise(r * Math.sin(delta), noise)};
    }

    private static double randomAngle() {
        return RAND.nextDouble() * 2 * Math.PI;
    }

    private static double noise(double value, double noise) {
        return value + (RAND.nextDouble() * 2 * noise - noise);
    }

    public double[][] getX() {
        return x;
    }

    public double[] getY() {
        return y;
    }
    
    public double[][] getYExpanded(){
        return Arrays.stream(y).mapToObj(v -> new double[]{v}).toArray(double[][]::new);
    }
}
