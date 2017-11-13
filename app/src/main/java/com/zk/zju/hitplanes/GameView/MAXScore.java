package com.zk.zju.hitplanes.GameView;

import java.util.Date;

/**
 * Created by Administrator on 2017/11/12.
 */
//这个类用来记录用户最高分数
public class MAXScore {
    private int id;
    private long MAXScore;

    public MAXScore() {
    }

    public MAXScore(int id, long score) {
        this.id = id;
        this.MAXScore = score;
    }

    public long getMAXScore() {
        return MAXScore;
    }

    public void setMAXScore(long MAXscore) {
        this.MAXScore = MAXscore;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;

    }
}


