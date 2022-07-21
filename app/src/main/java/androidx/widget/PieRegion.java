package androidx.widget;

import android.graphics.Region;

/**
 * 饼图区域
 */
public class PieRegion {

    /**
     * 饼图
     */
    private Pie pie;
    /**
     * 位置
     */
    private int position;
    /**
     * 区域
     */
    private Region region;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Pie getPie() {
        return pie;
    }

    public void setPie(Pie pie) {
        this.pie = pie;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

}
