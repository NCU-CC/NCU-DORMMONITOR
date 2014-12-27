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
        String[] titles = new String[] { "流量" }; // �w�qbar���W��
        List<double[]> x = new ArrayList<double[]>(); // �I��x����
        List<double[]> y = new ArrayList<double[]>(); // �I��y����
        double flowMax = 0;
        for(int i=0;i<144;i++) {
        	mHour[i] = 1/6.0*i-24;
        	mFlow[144-i-1] = Double.parseDouble(dataStrings.get(i))/1024/1024/1024;
        	if(mFlow[144-i-1]>flowMax)
        		flowMax = mFlow[144-i-1];
        	
        }
        if(flowMax>1.5)
        	flowMax = 1.5;
        
        // �ƭ�X,Y���Эȿ�J
        x.add(mHour);   
        y.add(mFlow);
        XYMultipleSeriesDataset dataset = buildDatset(titles, x, y); // �x�s�y�Э�

        int[] colors = new int[] { Color.rgb(61, 126, 255) };// bar���C��
        //PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE }; // �I���Ϊ�
        XYMultipleSeriesRenderer renderer = buildRenderer(colors);
        
        
        setChartSettings(renderer, "最近 24 小時流量 (單位: Byte)", "", "流量(G)", -24, 0, 0, flowMax*2, Color.BLACK);// �w�q����
 
        View chart = ChartFactory.getBarChartView(this, dataset, renderer, Type.DEFAULT);
        //ChartFactory.getBubbleChartView(context, dataset, renderer)
        

        setContentView(chart);
    }

    // �w�q�ϦW��
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
            String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor) {
        renderer.setChartTitle(title); //�ϦW��
        renderer.setChartTitleTextSize(26); // �ϦW�٦r�Τj�p
        renderer.setXTitle(xTitle); // X�b�W��
        renderer.setYTitle(yTitle); // Y�b�W��
        renderer.setXAxisMin(xMin); // X�b��̤ܳp��
        renderer.setXAxisMax(xMax); // X�b��̤ܳj��
        renderer.setXLabelsColor(Color.rgb(158, 61, 0)); // X�b�u�C��
        renderer.setYAxisMin(yMin); // Y�b��̤ܳp��
        renderer.setYAxisMax(yMax); // Y�b��̤ܳj��
        renderer.setAxesColor(axesColor); // �]�w���жb�C��
        renderer.setYLabelsColor(0, Color.rgb(158, 61, 0)); // Y�b�u�C��
        renderer.setLabelsColor(Color.rgb(158, 61, 0));// �]�w�����C��
        renderer.setMarginsColor(Color.rgb(255, 255, 240)); // �]�w�I���C��
        //renderer.setShowGrid(true); // �]�w��u
        renderer.setBarSpacing(0.05f);
        renderer.setZoomEnabled(false, false);
        renderer.setPanEnabled(false, false);    
    }

    // �w�q�Ϫ��榡
    private XYMultipleSeriesRenderer buildRenderer(int[] colors) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            //r.setPointStyle(styles[i]);
           // r.setFillPoints(fill);
            renderer.addSeriesRenderer(r); //�N�y���ܦ��u�[�J�Ϥ����
        }
        return renderer;
    }

    // ��ƳB�z
    private XYMultipleSeriesDataset buildDatset(String[] titles, List<double[]> xValues,
            List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        int length = titles.length; // bar�ƶq
        for (int i = 0; i < length; i++) {
            // XYseries��H,�Ω󴣨�ø�s���I���X�����
            XYSeries series = new XYSeries(titles[i]); // �̾ڨC��u���W�ٷs�W
            double[] xV = xValues.get(i); // ����i��bar�����
            double[] yV = yValues.get(i);
            int seriesLength = xV.length; // ���X���I

            for (int k = 0; k < seriesLength; k++) // �C��u�̦��X���I
            {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }
        return dataset;
    }
}