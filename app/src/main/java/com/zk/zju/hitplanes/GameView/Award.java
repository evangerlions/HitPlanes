package com.zk.zju.hitplanes.GameView;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/11/12.
 */

public class Award extends MoveItem {
    int health; //加入血量，方便移除
    public Award(Bitmap bitmap) {
        super(bitmap);
        this.type = 2;
        this.speed = 16;
        this.health = 1;
    }
}
