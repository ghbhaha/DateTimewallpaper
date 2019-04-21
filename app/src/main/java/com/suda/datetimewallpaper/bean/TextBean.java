package com.suda.datetimewallpaper.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guhaibo
 * @date 2019/4/14
 */
public class TextBean {

    private int useCusFont = 0;
    private int bold = 0;
    private float dis;
    private String type;
    private int clockwise = 0;
    private List<String> array = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getArray() {
        return array;
    }

    public void setArray(List<String> array) {
        this.array = array;
    }

    public float getDis() {
        return dis;
    }

    public void setDis(float dis) {
        this.dis = dis;
    }

    public int getBold() {
        return bold;
    }

    public void setBold(int bold) {
        this.bold = bold;
    }

    public int getUseCusFont() {
        return useCusFont;
    }

    public void setUseCusFont(int useCusFont) {
        this.useCusFont = useCusFont;
    }

    public int getClockwise() {
        return clockwise;
    }

    public void setClockwise(int clockwise) {
        this.clockwise = clockwise;
    }
}
