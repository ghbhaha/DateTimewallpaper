package com.suda.datetimewallpaper.bean;

/**
 * @author guhaibo
 * @date 2019/4/26
 */
public class WallPaperModel {

    String modelName;
    long paperId;
    boolean isCheck;
    int orderId;

    public WallPaperModel() {
    }

    public WallPaperModel(String modelName, long paperId, boolean isCheck, int orderId) {
        this.modelName = modelName;
        this.paperId = paperId;
        this.isCheck = isCheck;
        this.orderId = orderId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public long getPaperId() {
        return paperId;
    }

    public void setPaperId(long paperId) {
        this.paperId = paperId;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
