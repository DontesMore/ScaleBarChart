package com.example.chenxinyi2.scalebarchart.ScaleBarChart;

/**
 * 数据模型. <br>
 *
 * @创建人 chenxinyi2
 * @修改人 <br>
 * @版本 1.0.0 2016-06-10 16:00 <br>
 */
public class BarData {
    private String topText;
    private String bottomText;
    private int value;  // 用于确定百分比
    private int color;

    public String getTopText() {
        return topText;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}


