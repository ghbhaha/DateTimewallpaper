package com.suda.datetimewallpaper.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guhaibo
 * @date 2019/4/14
 */
public class DrawBean {

    List<TextBean> centerText = new ArrayList<>();
    List<TextBean> circleText = new ArrayList<>();

    int centerTextSize = 90;
    int circleTextSize = 50;
    String cusFont = "";

    public String getCusFont() {
        return cusFont;
    }

    public void setCusFont(String cusFont) {
        this.cusFont = cusFont;
    }

    public int getCenterTextSize() {
        return centerTextSize;
    }

    public void setCenterTextSize(int centerTextSize) {
        this.centerTextSize = centerTextSize;
    }

    public int getCircleTextSize() {
        return circleTextSize;
    }

    public void setCircleTextSize(int circleTextSize) {
        this.circleTextSize = circleTextSize;
    }

    public List<TextBean> getCenterText() {
        return centerText;
    }

    public void setCenterText(List<TextBean> centerText) {
        this.centerText = centerText;
    }

    public List<TextBean> getCircleText() {
        return circleText;
    }

    public void setCircleText(List<TextBean> circleText) {
        this.circleText = circleText;
    }
}
