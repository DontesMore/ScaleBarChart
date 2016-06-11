# ScaleBarChart
Android上快速形成柱形图
有时一些业务场景，在移动端上也要显示统计数据以及图表，假期尝试写了一个ScaleBarChart。使用了自定义的方式来实现这一需求。

一、数据模型
从图中，我们需要向用户展示的数据有哪些呢？有哪些有意义的值？能看到的、要展示出来的数据有柱形顶部文字，柱形底部文字以及表格名称。然后柱子的高度是怎么确定的呢？这时候就需要一个值，来确定该柱子的长度了。
public class BarData {
    private String topText;
    private String bottomText;
    private int value;  // 用于确定百分比
    private int color;
二、自定义View的实现
还记得画自定义View的步骤吗？忘记的友友们可以点开这里：http://mp.weixin.qq.com/s?__biz=MzIxMTM3MDYzMg==&mid=2247483668&idx=1&sn=589ca1b3536d69a8e262b2efe2a7e59d&scene=4#wechat_redirect
1. attrs.xml
在这里我们确定要在界面布局中传入的属性有表格名字、大小、颜色。其余柱形图的相关属性我们在java文件中编写传入。
<resources>

    <declare-styleable name="ScaleBarChart">
        <attr name="chartName" format="string" />
        <attr name="titleSize" format="integer" />
        <attr name="titleColor" format="integer" />
    </declare-styleable>
</resources>
2. 界面布局文件传入定义的属性：
<com.example.chenxinyi2.scalebarchart.ScaleBarChart.ScaleBarChart
android:id="@+id/scaleBarChart"
android:layout_width="wrap_content"
android:layout_height="250dp"
android:layout_centerHorizontal="true"
bar:chartName="2015中国部分省份人口统计"
bar:titleSize="18"
bar:titleColor="@color/colorTitle"
/>
3. 自定义View-构造方法：
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
在构造方法中去捕获定义的属性值。

4. onMeasure
View默认的onMeasure方法中，并没有根据测量模式，对布局宽高进行调整，所以为了适应wrap_content的布局设置，需要对onMeasure方法进行重写。
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = measureWidth(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);
    // 最高柱子的高度=View高度-（柱子顶部文字+间距+柱子底部文字+间距）-表格文字大小
    maxBarHeight = getHeight() - (defaultBarTextSize + defaultBarPadding) * 2  - chartNameTextSize;
    setMeasuredDimension(width, height);
}
再通过setMeasuredDimension方法指定宽高，看看我们自定义的measureWidth方法：
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
在specMode中，
EXACTLY：对应与LayoutParams中的match_parent和具体的数值。
AT_MOST：对应于LayoutParams中的wrap_content属性。
在这里我们也把每个柱子宽度给测量出来了。那高度呢？其实在onMeasure中，我们已经把最大柱子的高度测量出来了。我们约定，最大的高度对应的是100%的柱子高度，其余的柱子均以最高的柱子为参照按比例算出高度。

5. onDraw
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
按大的方向走，我们先把柱形画出之后再把表格名字画上。
5.1 绘制柱形
    柱形->顶部文字->底部文字
5.2 绘制表格名

大功告成拉，现只需要拼接好数据，一个setData方法。即可实现柱形效果。是不是很方便？





