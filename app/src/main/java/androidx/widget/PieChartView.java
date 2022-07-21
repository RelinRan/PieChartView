package androidx.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.androidx.widget.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 饼图
 */
public class PieChartView extends View {

    //宽高
    private int width, height;
    //数据
    private List<Pie> data;
    //半径
    private int radius;
    //半径缩放比例
    private float radiusScale = 0.60F;
    //文字拐角间距
    private float textCornerMargin = dip(5);
    //饼图线高度
    private float pieLineWidth = dip(20);
    //标注文字大小
    private float pieTextSize = dip(12);
    //饼图区域
    private List<PieRegion> pieRegions;
    //点击位置
    private int checkedPosition = 0;
    //点击偏移量
    private float checkedOffset = dip(4);

    public PieChartView(Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    /**
     * 初始化属性参数
     *
     * @param context 上下文
     * @param attrs   属性
     */
    protected void initAttributeSet(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PieChartView);
            radiusScale = array.getFloat(R.styleable.PieChartView_radiusScale, radiusScale);
            textCornerMargin = array.getDimension(R.styleable.PieChartView_textCornerMargin, textCornerMargin);
            pieLineWidth = array.getDimension(R.styleable.PieChartView_pieLineWidth, pieLineWidth);
            pieTextSize = array.getDimension(R.styleable.PieChartView_pieTextSize, pieTextSize);
            checkedOffset = array.getDimension(R.styleable.PieChartView_checkedOffset, checkedOffset);
            array.recycle();
        }
    }

    /**
     * @param value 值
     * @return 尺寸
     */
    protected float dip(int value) {
        return Resources.getSystem().getDisplayMetrics().density * value;
    }

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setDatasource(List<Pie> data) {
        this.data = data;
        invalidate();
    }

    /**
     * @return 饼图个数
     */
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * @param position 位置
     * @return 饼图数据item
     */
    public Pie getItem(int position) {
        if (position < 0 || position > getItemCount() - 1) {
            return null;
        }
        return data.get(position);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        int requiredWidth = getMeasuredWidth();
        int diameter = width <= height ? width : height;
        if (width <= height) {
            diameter -= getPaddingLeft() + getPaddingRight();
        } else {
            diameter -= getPaddingTop() + getPaddingBottom();
        }
        int requiredHeight = diameter;
        int measureSpecWidth = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureSpecHeight = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = measureSpecWidth;
        int measureHeight = measureSpecHeight;
        if ((widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED)
                && heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            measureWidth = requiredWidth;
            measureHeight = requiredHeight;
        } else if (widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED) {
            measureWidth = requiredWidth;
            measureHeight = measureSpecHeight;
        } else if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            measureWidth = measureSpecWidth;
            measureHeight = requiredHeight;
        }
        setMeasuredDimension(measureWidth, measureHeight);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        diameter = width <= height ? width : height;
        if (width <= height) {
            diameter -= getPaddingLeft() + getPaddingRight();
        } else {
            diameter -= getPaddingTop() + getPaddingBottom();
        }
        radius = (int) (diameter / 2.0F * radiusScale);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                checkedPosition = findPiePosition((int) x, (int) y);
                invalidate();
                if (onPieChartItemClickListener != null) {
                    onPieChartItemClickListener.onPieChartItemClick(this, checkedPosition);
                }
                break;
        }
        return true;
    }

    /**
     * 查找饼图位置
     *
     * @param x 横向坐标
     * @param y 纵向坐标
     * @return
     */
    protected int findPiePosition(int x, int y) {
        int size = pieRegions == null ? 0 : pieRegions.size();
        for (int i = 0; i < size; i++) {
            if (pieRegions.get(i).getRegion().contains(x, y)) {
                return i;
            }
        }
        return 0;
    }

    private OnPieChartItemClickListener onPieChartItemClickListener;

    /**
     * 设置饼图点击事件
     *
     * @param onPieChartItemClickListener
     */
    public void setOnPieChartItemClickListener(OnPieChartItemClickListener onPieChartItemClickListener) {
        this.onPieChartItemClickListener = onPieChartItemClickListener;
    }

    public interface OnPieChartItemClickListener {

        /**
         * @param v        饼图
         * @param position 位置
         */
        void onPieChartItemClick(PieChartView v, int position);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
    }

    /**
     * @return 总值
     */
    private double getTotal() {
        double total = 0;
        double size = data == null ? 0 : data.size();
        for (int i = 0; i < size; i++) {
            total += data.get(i).getValue();
        }
        return total;
    }

    /**
     * @param position 位置
     * @return 计算item比重
     */
    public float calculateProportion(int position) {
        Pie pie = getItem(position);
        return (float) (pie.getValue() * 100D / getTotal());
    }

    /**
     * 绘制扇形
     *
     * @param canvas 画布
     */
    private void drawArc(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        int size = data == null ? 0 : data.size();
        double total = getTotal();
        float startAngle = -80F;
        pieRegions = new ArrayList<>();
        float cx;
        float cy;
        for (int i = 0; i < size; i++) {
            Pie item = data.get(i);
            float swipeAngle = (float) (360.0F * item.getValue() / total);
            //便宜逻辑处理
            double radians = Math.toRadians(startAngle + swipeAngle / 2f - 2 * Math.PI + 95F);
            float offsetX = (float) (Math.sin(radians) * checkedOffset);
            float offsetY = -(float) (Math.cos(radians) * checkedOffset);
            paint.setColor(item.getColor());
            if (i == checkedPosition) {
                cx = width / 2 + offsetX;
                cy = height / 2 + offsetY;
            } else {
                cx = width / 2;
                cy = height / 2;
            }
            float left = cx - radius;
            float top = cy - radius;
            float right = cx + radius;
            float bottom = cy + radius;
            RectF oval = new RectF(left, top, right, bottom);
            //绘制扇形
            Region region = new Region();
            Path path = new Path();
            path.moveTo(cx, cy);
            path.addArc(oval, startAngle, swipeAngle);
            path.lineTo(cx, cy);
            Region clip = new Region((int) left, (int) top, (int) right, (int) bottom);
            region.setPath(path, clip);
            PieRegion pieRegion = new PieRegion();
            pieRegion.setPosition(i);
            pieRegion.setPie(item);
            pieRegion.setRegion(region);
            pieRegions.add(pieRegion);
            canvas.drawPath(path, paint);
            //记录角度
            float angel = startAngle + swipeAngle / 2f;
            //绘制文字
            drawPieText(canvas, cx, cy, angel, radius, item.getColor(), item.getName());
            startAngle += swipeAngle;
        }
    }

    /**
     * 绘制饼图文字
     *
     * @param canvas 画布
     * @param angle  角度
     * @param radius 半径
     * @param color  颜色
     * @param text   文字
     */
    private void drawPieText(Canvas canvas, float cx, float cy, float angle, float radius, int color, String text) {
        double radians = Math.toRadians(angle - 2 * Math.PI + 95F);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(2);
        paint.setTextSize(pieTextSize);
        paint.setStyle(Paint.Style.STROKE);
        float x = (float) (Math.sin(radians) * radius / 2);
        float y = -(float) (Math.cos(radians) * radius / 2);
        float endX = (float) (Math.sin(radians) * (radius + pieLineWidth));
        float endY = -(float) (Math.cos(radians) * (radius + pieLineWidth));
        float startX = cx + x;
        float startY = cy + y;
        float stopX = cx + endX;
        float stopY = cy + endY;
        //绘制指示线
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        float direction = x > 0 ? 1 : -1;
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        float lineStopX = stopX + (textBounds.width() + textCornerMargin) * direction;
        float lineStopY = stopY;
        canvas.drawLine(stopX, stopY, lineStopX, lineStopY, paint);
        //绘制文字
        Rect bounds = new Rect();
        paint.setStyle(Paint.Style.FILL);
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, lineStopX + (direction < 0 ? 0 : -bounds.width()), lineStopY - bounds.height() / 2, paint);
    }

    /**
     * @return 圆形半径
     */
    public int getRadius() {
        return radius;
    }

    /**
     * 设置圆形半径
     *
     * @param radius
     */
    public void setRadius(int radius) {
        this.radius = radius;
        invalidate();
    }

    /**
     * @return 半径缩放值
     */
    public float getRadiusScale() {
        return radiusScale;
    }

    /**
     * 设置半径缩放值
     *
     * @param radiusScale
     */
    public void setRadiusScale(float radiusScale) {
        this.radiusScale = radiusScale;
        invalidate();
    }

    /**
     * @return 文字拐角间距
     */
    public float getTextCornerMargin() {
        return textCornerMargin;
    }

    /**
     * 设置文字拐角间距
     *
     * @param textCornerMargin
     */
    public void setTextCornerMargin(float textCornerMargin) {
        this.textCornerMargin = textCornerMargin;
        invalidate();
    }

    /**
     * @return 饼图指示器线长度
     */
    public float getPieLineWidth() {
        return pieLineWidth;
    }

    /**
     * 设置饼图指示器线长度
     *
     * @param pieLineWidth
     */
    public void setPieLineWidth(float pieLineWidth) {
        this.pieLineWidth = pieLineWidth;
        invalidate();
    }

    /**
     * @return 饼图文字大小
     */
    public float getPieTextSize() {
        return pieTextSize;
    }

    /**
     * 饼图文字大小
     *
     * @param pieTextSize
     */
    public void setPieTextSize(float pieTextSize) {
        this.pieTextSize = pieTextSize;
        invalidate();
    }

    /**
     * @return 点击位置
     */
    public int getCheckedPosition() {
        return checkedPosition;
    }

    /**
     * 设置点击位置
     *
     * @param position
     */
    public void setCheckedPosition(int position) {
        this.checkedPosition = position;
        invalidate();
    }

    /**
     * @return 选中偏移量
     */
    public float getCheckedOffset() {
        return checkedOffset;
    }

    /**
     * 设置选中偏移量
     *
     * @param checkedOffset 偏移量
     */
    public void setCheckedOffset(float checkedOffset) {
        this.checkedOffset = checkedOffset;
        invalidate();
    }

}


