package androidx.widget;

import android.graphics.Color;

/**
 * 饼图Item数据
 */
public class Pie {

    /**
     * 名称
     */
    private String name;
    /**
     * 值
     */
    private double value;
    /**
     * 颜色
     */
    private int color;

    public Pie(String name, double value, int color) {
        this.name = name;
        this.value = value;
        this.color = color;
    }

    public Pie(double value, int color) {
        this.value = value;
        this.color = color;
    }

    public Pie(double value, String color) {
        this.value = value;
        this.color = Color.parseColor(color);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
