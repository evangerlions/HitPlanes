package com.zk.zju.hitplanes.GameView;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/11/12.
 */

public class SmallEnemyPlane extends EnemyPlane {
    public SmallEnemyPlane(Bitmap bitmap) {
        super(bitmap);
        this.health = 10;
        this.score = 300;
        this.speed = 13;
        this.positionX = 0;
        this.positionY = 0;
        this.enemyPlaneType = 0;
    }
}
