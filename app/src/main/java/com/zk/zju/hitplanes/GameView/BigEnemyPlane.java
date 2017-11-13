package com.zk.zju.hitplanes.GameView;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/11/12.
 */

public class BigEnemyPlane extends EnemyPlane {
    public BigEnemyPlane(Bitmap bitmap) {
        super(bitmap);
        this.health = 50;
        this.score = 500;
        this.speed = 3;
        this.positionX = 0;
        this.positionY = 0;
        this.enemyPlaneType = 2;
    }
}
