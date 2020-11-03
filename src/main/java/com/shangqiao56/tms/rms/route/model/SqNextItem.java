package com.shangqiao56.tms.rms.route.model;

import com.shangqiao56.tms.rms.route.core.Path;
import com.shangqiao56.tms.rms.route.core.Station;

import java.math.BigDecimal;

public class SqNextItem<T> extends Path.NextItem {

    private int onDay;      //表示一个星期第几天上班

    private boolean isUsing;         // 状态
    private BigDecimal mileage;  //里程
    private BigDecimal payRatio;
    private BigDecimal  trunkRatio; //TRUNK_RATIO

    private Path path;

  //  private T extra;

    public SqNextItem(Station station) {
        super(station);
    }

    public int getOnDay() {
        return onDay;
    }

    public void setOnDay(int onDay) {
        this.onDay = onDay;
    }


    public boolean getIsUsing() {
        return isUsing;
    }

    public void setUsing(boolean using) {
        isUsing = using;
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public BigDecimal getPayRatio() {
        return payRatio;
    }

    public void setPayRatio(BigDecimal payRatio) {
        this.payRatio = payRatio;
    }

    public BigDecimal getTrunkRatio() {
        return trunkRatio;
    }

    public void setTrunkRatio(BigDecimal trunkRatio) {
        this.trunkRatio = trunkRatio;
    }

    public boolean isUsing() {
        return isUsing;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
