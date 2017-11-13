package com.zk.zju.hitplanes.GameView;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/11/12.
 */

public class YellowBullet extends Bullet {
    public YellowBullet(Bitmap bitmap) {
        super(bitmap);
        this.speed = -30;
        this.power = 4;
    }
}
