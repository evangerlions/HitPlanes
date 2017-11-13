package com.zk.zju.hitplanes.GameView;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/11/9.
 */

public abstract class  EnemyPlane extends MoveItem {
    //敌机血量
    int health;
    //击落敌机所得分数
    int score;
    //敌机种类 0-小型敌机，1-中型敌机，2大型敌机
    short enemyPlaneType;

    public EnemyPlane(Bitmap bitmap) {
       super(bitmap);
       type = 1;
    }
}
