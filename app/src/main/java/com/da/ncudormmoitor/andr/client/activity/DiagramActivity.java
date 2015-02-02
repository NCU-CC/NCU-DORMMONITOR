package com.da.ncudormmoitor.andr.client.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;



import java.util.ArrayList;
import java.util.List;

public class DiagramActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        ArrayList<String> dataStrings = getIntent().getStringArrayListExtra("diagram_data");
        double[] mHour = new double[144];
        double[] mFlow = new double[144];
        String[] titles = new String[] { "流量" }; // 定義bar的名稱
        List<double[]> x = new ArrayList<double[]>(); // 點的x坐標
        List<double[]> y = new ArrayList<double[]>(); // 點的y坐標
        double flowMax = 0;
        for(int i=0;i<144;i++) {
        	mHour[i] = 1/6.0*i-24;
        	mFlow[144-i-1] = Double.parseDouble(dataStrings.get(i))/1024/1024/1024;
        	if(mFlow[144-i-1]>flowMax)
        		flowMax = mFlow[144-i-1];
        	
        }
        if(flowMax>1.5)
        	flowMax = 1.5;

        // 數值X,Y坐標值輸入
        x.add(mHour);   
        y.add(mFlow);
        XYMultipleSeriesDataset dataset = buildDatset(titles, x, y); // 儲存座標值

        int[] colors = new int[] { Color.rgb(61, 126, 255) };// bar的顏色
        //PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE }; // 點的形狀
        XYMultipleSeriesRenderer renderer = buildRenderer(colors);
        
        
        setChartSettings(renderer, "最近 24 小時流量 (單位: Byte)", "", "流量(G)", -24, 0, 0, flowMax*2, Color.BLACK);// 定義長條圖
 
        View chart = ChartFactory.getBarChartView(this, dataset, renderer, Type.DEFAULT);
        //ChartFactory.getBubbleChartView(context, dataset, renderer)
        

        setContentView(chart);
    }

    // 定義圖名稱
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
            String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor) {
        renderer.setChartTitle(title); //圖名稱
        renderer.setChartTitleTextSize(26); // 圖名稱字形大小
        renderer.setXTitle(xTitle); // X軸名稱
        renderer.setYTitle(yTitle); // Y軸名稱
        renderer.setXAxisMin(xMin); // X軸顯示最小值
        renderer.setXAxisMax(xMax); // X軸顯示最大值
        renderer.setXLabelsColor(Color.rgb(158, 61, 0)); // X軸線顏色
        renderer.setYAxisMin(yMin); // Y軸顯示最小值
        renderer.setYAxisMax(yMax); // Y軸顯示最大值
        renderer.setAxesColor(axesColor); // 設定坐標軸顏色
        renderer.setYLabelsColor(0, Color.rgb(158, 61, 0)); // Y軸線顏色
        renderer.setLabelsColor(Color.rgb(158, 61, 0));// 設定標籤顏色
        renderer.setMarginsColor(Color.rgb(255, 255, 240)); // 設定背景顏色
        //renderer.setShowGrid(true); // 設定格線
        renderer.setBarSpacing(0.05f);
        renderer.setZoomEnabled(false, false);
        renderer.setPanEnabled(false, false);    
    }

    // 定義圖的格式
    private XYMultipleSeriesRenderer buildRenderer(int[] colors) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            //r.setPointStyle(styles[i]);
           // r.setFillPoints(fill);
            renderer.addSeriesRenderer(r); //將座標變成線加入圖中顯示
        }
        return renderer;
    }

    // 資料處理
    private XYMultipleSeriesDataset buildDatset(String[] titles, List<double[]> xValues,
            List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        int length = titles.length; // bar數量
        for (int i = 0; i < length; i++) {
            // XYseries對象,用於提供繪製的點集合的資料
            XYSeries series = new XYSeries(titles[i]); // 依據每條線的名稱新增
            double[] xV = xValues.get(i); // 獲取第i條bar的資料
            double[] yV = yValues.get(i);
            int seriesLength = xV.length; // // 有幾個點

            for (int k = 0; k < seriesLength; k++) // 每條線裡有幾個點
            {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }
        return dataset;
    }
}