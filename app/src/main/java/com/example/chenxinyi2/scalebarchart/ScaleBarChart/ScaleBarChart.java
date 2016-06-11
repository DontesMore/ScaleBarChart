package com.example.chenxinyi2.scalebarchart.ScaleBarChart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.chenxinyi2.scalebarchart.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 比例柱形图. <br>
 *
 * @创建人 chenxinyi2
 * @修改人 <br>
 * @版本 1.0.0 2016-06-10 16:03 <br>
 */
public class ScaleBarChart extends View {

    private static final int MIN_BAR_HEIGHT = 150;  // 最小的柱形高度

    private String chartName;
    private int chartNameTextSize, chartNameTextColor;

    private Paint mPaint = new Paint();
    private int defaultBarPadding = 20;    // 默认两柱之间的padding
    private int defaultBarTextMargin = 8;  // 默认柱形与标识文字之间的距离
    private int defaultBarTextSize = 13;    // 默认柱形文字大小
    private int defaultChartNamePadding = 10; // 默认表格名字间距

    private List<BarData> barDatas = new ArrayList<BarData>();
    private int perBarWidth;    // 每柱宽度

    private int maxValue = 0;   // 最大的值
    private int maxBarHeight = 0;   // 最大的柱形高度

    public ScaleBarChart(Context context) {
        super(context);
    }

    public ScaleBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ScaleBarChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public ScaleBarChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScaleBarChart, defStyleAttr, defStyleRes);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            switch (i) {
                case R.styleable.ScaleBarChart_chartName:
                    chartName = array.getString(i);
                    break;
                case R.styleable.ScaleBarChart_titleSize:
                    chartNameTextSize = array.getInt(i, chartNameTextSize);
                    break;
                case R.styleable.ScaleBarChart_titleColor:
                    chartNameTextColor = array.getInt(i, chartNameTextColor);
                    break;
            }
        }
        array.recycle();

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);//抗锯齿
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        // 最高柱子的高度=View高度-（柱子顶部文字+间距+柱子底部文字+间距）-表格文字大小
        maxBarHeight = getHeight() - (defaultBarTextSize + defaultBarPadding) * 2  - chartNameTextSize;
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int res = 0;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.EXACTLY:
                res = specSize;
                perBarWidth = (specSize - getPaddingLeft() - getPaddingRight() - barDatas.size() * defaultBarPadding)
                        / barDatas.size(); // 每柱子宽度=总宽度-(柱子间的padding值)
                break;
            case MeasureSpec.AT_MOST:
                res = Math.min(specSize, adaptedWidth(specSize));
                break;
            case MeasureSpec.UNSPECIFIED:
                res = adaptedWidth(specSize);
                break;
        }
        return res;
    }

    private int adaptedWidth(int specSize) {
        int resWidth = 0;

        if (barDatas != null && barDatas.size() > 0) {
            resWidth += getPaddingLeft();
            resWidth += getPaddingRight();

            int paddingNum = barDatas.size() - 1;   // 计算两柱之间的padding数量
            int barAreaWidth = specSize - resWidth - paddingNum * defaultBarPadding;
            perBarWidth = (specSize - resWidth - paddingNum * defaultBarPadding) / barDatas.size(); // 每柱子宽度
            resWidth += barAreaWidth;
        }

        return resWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (barDatas == null)
            return;

        // 1.绘制柱形
        for (int i = 0; i < barDatas.size(); i++) {
            BarData data = barDatas.get(i);

            mPaint.setColor(data.getColor());
            // 1.1绘制柱形
            float left = perBarWidth * i + (i * defaultBarPadding);
            float top = getHeight() - getRate(data.getValue(), maxValue) * maxBarHeight / 100
                    - (defaultBarTextSize + defaultBarPadding + chartNameTextSize);
            float right = perBarWidth * (i + 1) + (i * defaultBarPadding);
            float bottom = getHeight() - (defaultBarTextSize + defaultBarPadding + chartNameTextSize);
            Log.d("david", "left:" + left + ", top:" + top + ", right:" + right + ", bottom:" + bottom);

            RectF targetRect = new RectF(left, top, right, bottom);
            canvas.drawRect(targetRect, mPaint);

            // 1.2.绘制柱形头部文本
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(defaultBarTextSize);
            canvas.drawText(data.getTopText(), (right + left) / 2, top - defaultBarTextMargin, mPaint);

            // 1.3.绘制柱形底部文字文本
            mPaint.setColor(getResources().getColor(R.color.colorTitle));
            canvas.drawText(data.getBottomText(), (right + left) / 2, bottom + defaultBarTextSize, mPaint);
        }

        // 2.绘制表格名
        mPaint.setColor(chartNameTextColor);
        mPaint.setTextSize(chartNameTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(chartName, getWidth() / 2, getHeight() - chartNameTextSize + defaultChartNamePadding, mPaint);
    }

    public void setData(ArrayList<BarData> datas) {
        this.barDatas = datas;
        initData();
    }

    private void initData() {
        if (barDatas == null)
            return;

        // 取出最大个高度
        for (int i = 0; i < barDatas.size(); i++) {
            BarData data = barDatas.get(i);
            if (maxValue < data.getValue())
                maxValue = data.getValue();
        }
    }

    /**
     * 获取百分比
     *
     * @param numerator
     * @param denominator
     * @return
     */
    public float getRate(int numerator, int denominator) {
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);

        String result = numberFormat.format((float) numerator / (float) denominator * 100);
        return Float.parseFloat(result);
    }
}
