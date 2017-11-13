package com.zk.zju.hitplanes.GameView;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/11/11.
 */

public class Bullet extends MoveItem{
    int power;
    public Bullet(Bitmap bitmap) {
        super(bitmap);
        this.type = 3;
        this.speed = -10;
        this.power = 2;
    }
}
