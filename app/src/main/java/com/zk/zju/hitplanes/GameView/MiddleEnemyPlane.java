package com.zk.zju.hitplanes.GameView;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/11/9.
 */

public class MiddleEnemyPlane extends EnemyPlane{
    public MiddleEnemyPlane(Bitmap bitmap) {
        super(bitmap);
        this.health = 20;
        this.score = 200;
        this.speed = 7;
        this.positionX = 0;
        this.positionY = 0;
        this.enemyPlaneType = 1;
    }
}
