package com.github.dperezcabrera.nn;

import java.awt.Color;
import java.util.stream.IntStream;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterPlot extends JFrame {

    private ScatterPlot(Data data) {
        XYDataset dataset = createDataset(data);
        JFreeChart chart = ChartFactory.createScatterPlot(
                null,
                "X-Axis",
                "Y-Axis",
                dataset,
                PlotOrientation.HORIZONTAL,
                false, true, false);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(255, 255, 255));

        setContentPane(new ChartPanel(chart));
    }

    private XYDataset createDataset(Data data) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        double[][] x = data.getX();
        double[] y = data.getY();
        XYSeries[] series = {new XYSeries("Y == 0"), new XYSeries("Y == 1")};
        IntStream.range(0, y.length).forEach(i -> series[(int) y[i]].add(x[i][0], x[i][1]));
        dataset.addSeries(series[0]);
        dataset.addSeries(series[1]);
        return dataset;
    }

    public static ScatterPlot show(Data data) {
        ScatterPlot plot = new ScatterPlot(data);
        plot.setSize(600, 600);
        plot.setLocationRelativeTo(null);
        plot.setVisible(true);
        return plot;
    }
}
