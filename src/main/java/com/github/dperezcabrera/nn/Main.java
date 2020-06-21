package com.github.dperezcabrera.nn;

import com.github.dperezcabrera.nn.Utils.Function;
import com.github.dperezcabrera.nn.Utils.Function2;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        // DataSet
        int n = 500;
        int p = 2;
        Data data = Data.makeCircles(n, 0.5, 0.05);
        ScatterPlot.show(data);

        int[] topology = {p, 4, 1};

        // Sigmoid Function & Sigmoid' Function
        Function[] sigm = {
            x -> 1 / (1 + Math.pow(Math.E, -x)),
            x -> x * (1 - x)
        };

        // Quadratic error Function & Quadratic error' Function
        Function2[] l2Cost = {
            (yP, yR) -> (yP - yR) * (yP - yR),
            (yP, yR) -> yP - yR
        };

        NeuralNet nn = NeuralNet.create(topology, sigm);

        double learningRate = 0.1;

        // Training
        IntStream.range(0, 1200).forEach(i -> nn.train(data, l2Cost, learningRate, true));

        double[][] result = nn.train(data, l2Cost, learningRate, true);

        showResults(data, result);
    }

    private static void showResults(Data data, double[][] result) {
        double[][] x = data.getX();
        double[] y = data.getY();
        System.out.println("[  coordenadas   ] | Grupo | Predicción | Resultado ");
        int ok = 0;
        String resultado;
        for (int i = 0; i < result.length; i++) {
            if (Math.round(result[i][0]) == y[i]) {
                ok++;
                resultado = "   V   ";
            } else {
                resultado = "   X   ";
            }
            System.out.println(String.join(" | ", "[" + Utils.format(x[i][0], "%.4f") + ", " + Utils.format(x[i][1], "%.4f") + "]", String.format("  %.0f  ", y[i]), String.format(" %.6f ", result[i][0]), resultado));
        }
        System.out.println("[  coordenadas   ] | Grupo | Predicción | Resultado \n");
        
        System.out.println("Aciertos:    " + String.format("%.2f", (100.0 * ok)/result.length)+"%");
    }

    static class NeuralLayer {

        private Function[] actFunc;
        private double[][] weights;
        private double[] bias;

        public NeuralLayer(int numConnections, int numNeurons, Utils.Function[] actFunc) {
            this.actFunc = actFunc;
            bias = Utils.rand(numNeurons, d -> 2 * d - 1);
            weights = Utils.rand(numConnections, numNeurons, d -> 2 * d - 1);
        }
    }

    static class NeuralNet {

        private NeuralLayer[] layers;

        public NeuralNet(NeuralLayer[] layers) {
            this.layers = layers;
        }

        public static NeuralNet create(int[] topology, Function[] actFunc) {
            NeuralLayer[] layers = IntStream.range(0, topology.length - 1)
                    .mapToObj(i -> new NeuralLayer(topology[i], topology[i + 1], actFunc))
                    .toArray(NeuralLayer[]::new);

            return new NeuralNet(layers);
        }

        public double[][] train(Data data, Function2[] l2Cost, double learningRate, boolean training) {
            List<double[][]> out = new ArrayList<>();
            out.add(data.getX());
            for (NeuralLayer layer : layers) {
                // z = out[-1] @ layer.weights + layer.bias
                double[][] z = Utils.sum(Utils.matrixMult(out.get(out.size() - 1), layer.weights), layer.bias);

                // a = layer.actFunc[0](z)
                double[][] a = Utils.apply(z, layer.actFunc[0]);
                out.add(a);
            }
            if (training) {
                double[][] y = data.getYExpanded();
                double[][] delta = null;
                double[][] wTransposed = null;
                for (int i = layers.length - 1; i >= 0; i--) {
                    NeuralLayer layer = layers[i];
                    double[][] a = out.get(i + 1);
                    if (i == layers.length - 1) {
                        // delta = l2Cost[1](a, y) * layer.actFunc[1](a) 
                        delta = Utils.mult(
                                Utils.apply(a, y, l2Cost[1]),
                                Utils.apply(a, layer.actFunc[1]));
                    } else {
                        // delta = delta @ _w.T * layer.actFunc[1](a) 
                        delta = Utils.mult(
                                Utils.matrixMult(delta, wTransposed),
                                Utils.apply(a, layer.actFunc[1]));
                    }
                    wTransposed = Utils.transpose(layer.weights);

                    // layer.bias = layer.bias - mean(delta) * learningRate
                    layer.bias = Utils.diff(
                            layer.bias,
                            Utils.mult(Utils.mean(delta), learningRate));

                    // layer.weights = layer.weights - out[i].T @ delta * learningRate
                    layer.weights = Utils.diff(
                            layer.weights,
                            Utils.mult(
                                    Utils.matrixMult(Utils.transpose(out.get(i)), delta),
                                    learningRate));
                }
            }
            return out.get(out.size() - 1);
        }
    }
}
