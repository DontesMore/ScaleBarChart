package com.example.chenxinyi2.scalebarchart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.chenxinyi2.scalebarchart.ScaleBarChart.BarData;
import com.example.chenxinyi2.scalebarchart.ScaleBarChart.ScaleBarChart;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // 颜色表
    private int[] mColors = {0xFFE6B800, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000};
    private ArrayList<BarData> mBarDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBarData();
        ScaleBarChart scaleBarChart = (ScaleBarChart) findViewById(R.id.scaleBarChart);
        scaleBarChart.setData(mBarDatas);
    }

    private void initBarData() {
        for (int i = 0; i < 4; i++) {
            BarData data = new BarData();
            data.setColor(mColors[i]);
            if (i == 0) {
                data.setBottomText("广西");
                data.setValue(5282);
                data.setTopText("5282万");
            } else if (i == 1) {
                data.setBottomText("安徽");
                data.setValue(6928);
                data.setTopText("6928万");
            } else if (i == 2) {
                data.setBottomText("湖南");
                data.setValue(6690);
                data.setTopText("6690万");
            } else if (i == 3) {
                data.setBottomText("广东");
                data.setValue(10644);
                data.setTopText("10644万");
            }

            mBarDatas.add(data);
        }
    }
}
